package com.freshtrack.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

public class SettingsManager {

    private static final String PREF_NAME = "freshtrack_prefs";
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = DatabaseHelper.getInstance(this.context);
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_SETTINGS,
                    new String[]{DatabaseHelper.COL_SETTING_VAL},
                    DatabaseHelper.COL_SETTING_KEY + " = ?",
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
        return prefs.getString(key, defaultValue);
    }

    public boolean setSetting(String key, String value) {
        try {
            // Save to SQLite
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COL_SETTING_KEY, key);
            cv.put(DatabaseHelper.COL_SETTING_VAL, value);
            db.insertWithOnConflict(DatabaseHelper.TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

            // Also mirror to SharedPreferences
            prefs.edit().putString(key, value).apply();
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
