package com.freshtrack.aplikasi.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.freshtrack.aplikasi.data.basis_data.PembantuBasisData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PembantuAlarm {

    private static final String TAG = "PembantuAlarm";

    public static boolean jadwalkanAlarm(Context context, long alarmId, long foodId, String foodName, String reminderType, String dateStr, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateStr + " " + timeStr);
            if (date == null) return false;

            long triggerAtMillis = date.getTime();
            long now = System.currentTimeMillis();

            if (triggerAtMillis <= now) {
                return false;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "Izin SCHEDULE_EXACT_ALARM belum diberikan.");
                }
            }

            Intent intent = new Intent(context, PenerimaAlarm.class);
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

            Log.d(TAG, "Berhasil menjadwalkan alarm ID=" + alarmId + " untuk " + foodName + " pada " + dateStr + " " + timeStr);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error menjadwalkan alarm: " + e.getMessage(), e);
            return false;
        }
    }

    public static void batalkanAlarm(Context context, long alarmId) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, PenerimaAlarm.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, intent, flags);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Alarm dibatalkan ID=" + alarmId);
        } catch (Exception e) {
            Log.e(TAG, "Error membatalkan alarm: " + e.getMessage(), e);
        }
    }

    public static void jadwalkanUlangSemuaAlarm(Context context) {
        PembantuBasisData dbHelper = PembantuBasisData.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT a.id, a.food_id, a.reminder_type, a.reminder_date, a.reminder_time, f.name "
                + "FROM " + PembantuBasisData.TABLE_ALARMS + " a "
                + "JOIN " + PembantuBasisData.TABLE_FOODS + " f ON a.food_id = f.id "
                + "WHERE a.is_active = 1 AND f.status = 'Aktif'";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                long alarmId = cursor.getLong(0);
                long foodId = cursor.getLong(1);
                String reminderType = cursor.getString(2);
                String reminderDate = cursor.getString(3);
                String reminderTime = cursor.getString(4);
                String foodName = cursor.getString(5);

                jadwalkanAlarm(context, alarmId, foodId, foodName, reminderType, reminderDate, reminderTime);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error menjadwalkan ulang alarm: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
