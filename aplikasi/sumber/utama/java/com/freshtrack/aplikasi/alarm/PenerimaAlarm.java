package com.freshtrack.aplikasi.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freshtrack.aplikasi.notifikasi.PembantuNotifikasi;

public class PenerimaAlarm extends BroadcastReceiver {

    private static final String TAG = "PenerimaAlarm";

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

        Log.d(TAG, "Alarm FreshTrack berbunyi untuk makanan: " + foodName + ", jenis: " + reminderType);

        String judul = "FreshTrack: " + foodName;
        String pesan = "Perhatian! " + foodName + " memerlukan perhatian Anda (" + reminderType + "). Segera periksa dan gunakan sebelum kedaluwarsa!";

        PembantuNotifikasi.tampilkanNotifikasi(context, (int) (alarmId > 0 ? alarmId : System.currentTimeMillis() % 10000), judul, pesan, foodId);
    }
}
