package com.freshtrack.aplikasi.notifikasi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.freshtrack.aplikasi.inti.AktivitasUtama;

public class PembantuNotifikasi {

    public static final String SALURAN_ID = "freshtrack_saluran_kedaluwarsa";
    public static final String NAMA_SALURAN = "Pengingat Kedaluwarsa Makanan";
    public static final String DESKRIPSI_SALURAN = "Notifikasi pengingat sebelum makanan kedaluwarsa atau harus segera dikonsumsi";

    public static void buatSaluranNotifikasi(Context konteks) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    SALURAN_ID,
                    NAMA_SALURAN,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(DESKRIPSI_SALURAN);
            channel.enableLights(true);
            channel.setLightColor(Color.parseColor("#2C5EAD"));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 400, 200, 400});

            NotificationManager manager = konteks.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void tampilkanNotifikasi(Context konteks, int idNotifikasi, String judul, String pesan, long idMakanan) {
        buatSaluranNotifikasi(konteks);

        Intent intent = new Intent(konteks, AktivitasUtama.class);
        intent.putExtra("target_page", "detail-makanan");
        intent.putExtra("food_id", idMakanan);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(konteks, idNotifikasi, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(konteks, SALURAN_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(judul)
                .setContentText(pesan)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(pesan))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.parseColor("#2C5EAD"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) konteks.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(idNotifikasi, builder.build());
        }
    }
}
