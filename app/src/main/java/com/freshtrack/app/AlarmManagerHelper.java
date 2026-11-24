package com.freshtrack.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmManagerHelper {

    private static final String TAG = "AlarmManagerHelper";

    public static boolean scheduleAlarm(Context context, long alarmId, long foodId, String foodName, String reminderType, String dateStr, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateStr + " " + timeStr);
            if (date == null) return false;

            long triggerAtMillis = date.getTime();
            long now = System.currentTimeMillis();

            if (triggerAtMillis <= now) {
                Log.w(TAG, "Alarm date/time is in the past: " + dateStr + " " + timeStr);
                return false;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return false;

            // Check exact alarm permission on Android 12+ (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "SCHEDULE_EXACT_ALARM permission not granted by user.");
                }
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("alarm_id", alarmId);
            intent.putExtra("food_id", foodId);
            intent.putExtra("food_name", foodName);
            intent.putExtra("reminder_type", reminderType);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, intent, flags);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }

            Log.d(TAG, "Successfully scheduled alarm ID=" + alarmId + " for " + foodName + " at " + dateStr + " " + timeStr);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling alarm: " + e.getMessage(), e);
            return false;
        }
    }

    public static void cancelAlarm(Context context, long alarmId) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, AlarmReceiver.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, intent, flags);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Alarm cancelled for ID=" + alarmId);
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling alarm: " + e.getMessage(), e);
        }
    }

    public static void rescheduleAllAlarms(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT a.id, a.food_id, a.reminder_type, a.reminder_date, a.reminder_time, f.name "
                + "FROM " + DatabaseHelper.TABLE_ALARMS + " a "
                + "JOIN " + DatabaseHelper.TABLE_FOODS + " f ON a.food_id = f.id "
                + "WHERE a.is_active = 1 AND f.status = 'Aktif'";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            int count = 0;
            while (cursor.moveToNext()) {
                long alarmId = cursor.getLong(0);
                long foodId = cursor.getLong(1);
                String reminderType = cursor.getString(2);
                String reminderDate = cursor.getString(3);
                String reminderTime = cursor.getString(4);
                String foodName = cursor.getString(5);

                boolean success = scheduleAlarm(context, alarmId, foodId, foodName, reminderType, reminderDate, reminderTime);
                if (success) count++;
            }
            Log.d(TAG, "Rescheduled " + count + " active alarms on system boot.");
        } catch (Exception e) {
            Log.e(TAG, "Error rescheduling alarms: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
