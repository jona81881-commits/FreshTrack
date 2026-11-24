package com.freshtrack.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        long foodId = intent.getLongExtra("food_id", 0);
        long alarmId = intent.getLongExtra("alarm_id", 0);
        String foodName = intent.getStringExtra("food_name");
        String reminderType = intent.getStringExtra("reminder_type");

        if (foodName == null) {
            foodName = "Makanan Anda";
        }
        if (reminderType == null) {
            reminderType = "Pengingat Kedaluwarsa";
        }

        Log.d(TAG, "FreshTrack Alarm fired for food: " + foodName + ", type: " + reminderType);

        String title = "FreshTrack: " + foodName;
        String message = "Perhatian! " + foodName + " memerlukan perhatian Anda (" + reminderType + "). Segera periksa dan gunakan sebelum kedaluwarsa!";

        NotificationHelper.showNotification(context, (int) (alarmId > 0 ? alarmId : System.currentTimeMillis() % 10000), title, message, foodId);
    }
}
