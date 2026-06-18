package com.example.russiancalendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.russiancalendar.R;
import com.example.russiancalendar.models.Event;
import com.example.russiancalendar.utils.EventStorage;
import com.example.russiancalendar.utils.NotificationHelper;
import com.example.russiancalendar.utils.ThemeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class AddEventActivity extends AppCompatActivity {

    private EditText etTime, etEvent;
    private String selectedDate;
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?\\d|2[0-3]):([0-5]\\d)$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            // Default to today
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        etTime = findViewById(R.id.etTime);
        etEvent = findViewById(R.id.etEvent);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveEvent());

        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    private void saveEvent() {
        String time = etTime.getText().toString().trim();
        String name = etEvent.getText().toString().trim();

        if (TextUtils.isEmpty(time) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TIME_PATTERN.matcher(time).matches()) {
            Toast.makeText(this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event();
        event.setTime(time);
        event.setName(name);
        event.setDate(selectedDate);

        EventStorage.addEvent(this, event);

        // Schedule notification if enabled
        if (ThemeUtils.areNotificationsEnabled(this)) {
            NotificationHelper.scheduleNotification(this, event);
        }

        Toast.makeText(this, R.string.event_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}
