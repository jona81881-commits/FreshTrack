package com.freshtrack.aplikasi.pengaturan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.freshtrack.aplikasi.data.basis_data.PembantuBasisData;
import com.freshtrack.aplikasi.data.penyimpanan.PengelolaPenyimpanan;

import org.json.JSONObject;

public class PengelolaPengaturan {

    private final Context konteks;
    private final PembantuBasisData pembantuDb;
    private final PengelolaPenyimpanan penyimpanan;

    public PengelolaPengaturan(Context konteks) {
        this.konteks = konteks.getApplicationContext();
        this.pembantuDb = PembantuBasisData.getInstance(this.konteks);
        this.penyimpanan = new PengelolaPenyimpanan(this.konteks);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = pembantuDb.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    PembantuBasisData.TABLE_SETTINGS,
                    new String[]{PembantuBasisData.COL_SETTING_VAL},
                    PembantuBasisData.COL_SETTING_KEY + " = ?",
                    new String[]{key},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        return penyimpanan.ambilTeks(key, defaultValue);
    }

    public boolean setSetting(String key, String value) {
        try {
            SQLiteDatabase db = pembantuDb.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_SETTING_KEY, key);
            cv.put(PembantuBasisData.COL_SETTING_VAL, value);
            db.insertWithOnConflict(PembantuBasisData.TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

            penyimpanan.simpanTeks(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getAllSettingsAsJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_name", getSetting("user_name", "Budi Santoso"));
            obj.put("user_email", getSetting("user_email", "budi.santoso@email.com"));
            obj.put("default_alarm_time", getSetting("default_alarm_time", "20:00"));
            obj.put("sound_enabled", Boolean.parseBoolean(getSetting("sound_enabled", "true")));
            obj.put("vibration_enabled", Boolean.parseBoolean(getSetting("vibration_enabled", "true")));
            obj.put("theme", getSetting("theme", "light"));
            obj.put("language", getSetting("language", "id"));
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }
}
