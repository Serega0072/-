package com.example.russiancalendar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.russiancalendar.utils.NotificationHelper;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule all saved events after device reboot
            NotificationHelper.rescheduleAll(context);
        }
    }
}
