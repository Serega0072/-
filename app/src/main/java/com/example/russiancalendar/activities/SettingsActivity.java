package com.example.russiancalendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.russiancalendar.R;
import com.example.russiancalendar.utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchTheme = findViewById(R.id.switchTheme);
        Switch switchNotifications = findViewById(R.id.switchNotifications);

        switchTheme.setChecked(ThemeUtils.isDarkMode(this));
        switchNotifications.setChecked(ThemeUtils.areNotificationsEnabled(this));

        switchTheme.setOnCheckedChangeListener((btn, isChecked) -> {
            ThemeUtils.setDarkMode(this, isChecked);
            // Перезапускаем всё приложение с главного экрана
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        switchNotifications.setOnCheckedChangeListener((btn, isChecked) ->
                ThemeUtils.setNotificationsEnabled(this, isChecked));

        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
}