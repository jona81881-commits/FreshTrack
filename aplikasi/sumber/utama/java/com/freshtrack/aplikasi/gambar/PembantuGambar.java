package com.freshtrack.aplikasi.gambar;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PembantuGambar {

    private static final String TAG = "PembantuGambar";
    private static final String DIREKTORI_GAMBAR = "gambar_makanan";

    public static String simpanGambarKePenyimpananInternal(Context konteks, Uri sumberUri) {
        try {
            File dir = new File(konteks.getFilesDir(), DIREKTORI_GAMBAR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String namaBerkas = "makanan_" + System.currentTimeMillis() + ".jpg";
            File berkasTujuan = new File(dir, namaBerkas);

            InputStream in = konteks.getContentResolver().openInputStream(sumberUri);
            if (in == null) return null;

            OutputStream out = new FileOutputStream(berkasTujuan);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            out.close();
            in.close();

            Log.d(TAG, "Gambar makanan tersimpan di: " + berkasTujuan.getAbsolutePath());
            return berkasTujuan.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Gagal menyimpan gambar: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean hapusGambar(String path) {
        if (path == null || path.isEmpty()) return false;
        try {
            File berkas = new File(path);
            if (berkas.exists() && berkas.isFile()) {
                return berkas.delete();
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
