package com.example.russiancalendar.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiancalendar.R;
import com.example.russiancalendar.adapters.EventAdapter;
import com.example.russiancalendar.models.Event;
import com.example.russiancalendar.utils.EventStorage;
import com.example.russiancalendar.utils.NotificationHelper;
import com.example.russiancalendar.utils.ThemeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    private static final String[] RU_MONTHS = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    private EventAdapter eventAdapter;
    private String selectedDate;
    private Calendar displayedMonth;
    private GridLayout calendarGrid;
    private TextView tvMonthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyBackground();
        requestNotificationPermission();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        displayedMonth = Calendar.getInstance();
        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvMonthYear);

        setupCalendar();
        setupRecyclerView();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEvents();
        buildCalendarGrid();
    }

    // ─── Фон ──────────────────────────────────────────────
    private void applyBackground() {
        androidx.constraintlayout.widget.ConstraintLayout root =
                findViewById(R.id.rootLayout);
        if (root == null) return;
        if (ThemeUtils.isDarkMode(this)) {
            root.setBackgroundResource(R.drawable.gradient_main_dark);
        } else {
            root.setBackgroundResource(R.drawable.gradient_main);
        }
    }

    // ─── Календарь ────────────────────────────────────────
    private void setupCalendar() {
        updateMonthLabel();
        buildCalendarGrid();

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            updateMonthLabel();
            buildCalendarGrid();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            updateMonthLabel();
            buildCalendarGrid();
        });
    }

    private void updateMonthLabel() {
        int month = displayedMonth.get(Calendar.MONTH);
        int year = displayedMonth.get(Calendar.YEAR);
        tvMonthYear.setText(RU_MONTHS[month] + " " + year);
    }

    private void buildCalendarGrid() {
        calendarGrid.removeAllViews();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDow = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDow == Calendar.SUNDAY) ? 6 : firstDow - Calendar.MONDAY;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = (screenWidth - dpToPx(16)) / 7;

        int totalCells = offset + daysInMonth;
        int rows = (int) Math.ceil(totalCells / 7.0);
        calendarGrid.setRowCount(rows);

        for (int i = 0; i < rows * 7; i++) {
            int dayNumber = i - offset + 1;

            FrameLayout cell = new FrameLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize;
            params.height = cellSize;
            params.rowSpec = GridLayout.spec(i / 7);
            params.columnSpec = GridLayout.spec(i % 7);
            cell.setLayoutParams(params);

            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                Calendar cellCal = (Calendar) displayedMonth.clone();
                cellCal.set(Calendar.DAY_OF_MONTH, dayNumber);
                String dateStr = sdf.format(cellCal.getTime());

                TextView tvDay = new TextView(this);
                tvDay.setText(String.valueOf(dayNumber));
                tvDay.setTextSize(13);
                tvDay.setGravity(Gravity.CENTER);

                FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(
                        dpToPx(34), dpToPx(34));
                tvParams.gravity = Gravity.CENTER;
                tvDay.setLayoutParams(tvParams);

                if (dateStr.equals(today)) {
                    tvDay.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.day_cell_today));
                    tvDay.setTextColor(Color.parseColor("#333333"));
                } else if (dateStr.equals(selectedDate)) {
                    tvDay.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.day_cell_selected));
                    tvDay.setTextColor(Color.WHITE);
                } else {
                    tvDay.setBackground(null);
                    tvDay.setTextColor(ThemeUtils.isDarkMode(this)
                            ? Color.WHITE
                            : Color.parseColor("#1A1A2E"));
                }

                cell.addView(tvDay);

                // Красная точка если есть события
                List<Event> dayEvents = EventStorage.getEventsForDate(this, dateStr);
                if (!dayEvents.isEmpty()) {
                    TextView dot = new TextView(this);
                    dot.setBackground(ContextCompat.getDrawable(this,
                            R.drawable.day_cell_has_event));
                    FrameLayout.LayoutParams dotParams = new FrameLayout.LayoutParams(
                            dpToPx(6), dpToPx(6));
                    dotParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    dotParams.bottomMargin = dpToPx(2);
                    dot.setLayoutParams(dotParams);
                    cell.addView(dot);
                }

                final String finalDateStr = dateStr;
                cell.setOnClickListener(v -> {
                    selectedDate = finalDateStr;
                    buildCalendarGrid();
                    refreshEvents();
                });
            }

            calendarGrid.addView(cell);
        }
    }

    // ─── RecyclerView ──────────────────────────────────────
    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        List<Event> events = EventStorage.getEventsForDate(this, selectedDate);
        eventAdapter = new EventAdapter(this, events);
        rv.setAdapter(eventAdapter);

        eventAdapter.setOnEventLongClickListener((event, position) ->
                new AlertDialog.Builder(this)
                        .setTitle("Удалить событие?")
                        .setMessage(event.getTime() + " — " + event.getName())
                        .setPositiveButton("Удалить", (d, w) -> {
                            NotificationHelper.cancelNotification(this, event.getId());
                            EventStorage.deleteEvent(this, event.getId());
                            refreshEvents();
                            buildCalendarGrid();
                            Toast.makeText(this, R.string.event_deleted,
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Отмена", null)
                        .show());
    }

    private void refreshEvents() {
        if (eventAdapter != null) {
            List<Event> events = EventStorage.getEventsForDate(this, selectedDate);
            eventAdapter.updateEvents(events);
        }
    }

    // ─── Кнопки ───────────────────────────────────────────
    private void setupButtons() {
        // Плюс — добавить событие
        ImageView btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra("selected_date", selectedDate);
            startActivity(intent);
        });

        // Мышца — советы
        ImageView ivMuscle = findViewById(R.id.ivMuscle);
        ivMuscle.setOnClickListener(v ->
                startActivity(new Intent(this, TipsActivity.class)));

        // Настройки
        ImageView btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        // Удалить прошедшие
        TextView btnDeletePast = findViewById(R.id.btnDeletePast);
        btnDeletePast.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Удалить прошедшие?")
                        .setMessage("Удалить все события до сегодняшнего дня?")
                        .setPositiveButton("Удалить", (d, w) -> {
                            EventStorage.deletePastEvents(this);
                            refreshEvents();
                            buildCalendarGrid();
                            Toast.makeText(this, "Прошедшие события удалены",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Отмена", null)
                        .show());
    }

    // ─── Вспомогательное ──────────────────────────────────
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}