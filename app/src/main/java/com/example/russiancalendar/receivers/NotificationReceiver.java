package com.example.russiancalendar.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.russiancalendar.R;
import com.example.russiancalendar.utils.NotificationHelper;
import com.example.russiancalendar.utils.ThemeUtils;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if notifications are enabled in settings
        if (!ThemeUtils.areNotificationsEnabled(context)) return;

        long eventId = intent.getLongExtra("event_id", 0);
        String eventName = intent.getStringExtra("event_name");
        String eventTime = intent.getStringExtra("event_time");

        String title = context.getString(R.string.notification_title);
        String text = eventTime + " — " + eventName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 300, 200, 300});

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) eventId, builder.build());
        }
    }
}
