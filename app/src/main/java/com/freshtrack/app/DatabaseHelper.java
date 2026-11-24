package com.freshtrack.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "freshtrack.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_FOODS = "foods";
    public static final String TABLE_ALARMS = "alarms";
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_SETTINGS = "settings";

    // Foods columns
    public static final String COL_FOOD_ID = "id";
    public static final String COL_FOOD_NAME = "name";
    public static final String COL_FOOD_CATEGORY = "category";
    public static final String COL_FOOD_IMAGE = "image_path";
    public static final String COL_FOOD_QUANTITY = "quantity";
    public static final String COL_FOOD_UNIT = "unit";
    public static final String COL_FOOD_PURCHASE_DATE = "purchase_date";
    public static final String COL_FOOD_EXPIRY_DATE = "expiry_date";
    public static final String COL_FOOD_LOCATION = "storage_location";
    public static final String COL_FOOD_PKG_STATUS = "package_status";
    public static final String COL_FOOD_OPENED_DATE = "opened_date";
    public static final String COL_FOOD_OPENED_DAYS = "opened_usage_days";
    public static final String COL_FOOD_OPENED_DEADLINE = "opened_usage_deadline";
    public static final String COL_FOOD_PRICE = "price";
    public static final String COL_FOOD_NOTES = "notes";
    public static final String COL_FOOD_CREATED_AT = "created_at";
    public static final String COL_FOOD_UPDATED_AT = "updated_at";
    public static final String COL_FOOD_STATUS = "status"; // 'Aktif', 'Habis', 'Dibuang'

    // Alarms columns
    public static final String COL_ALARM_ID = "id";
    public static final String COL_ALARM_FOOD_ID = "food_id";
    public static final String COL_ALARM_TYPE = "reminder_type"; // 'H-7', 'H-3', 'H-1', 'Hari-H', 'Khusus'
    public static final String COL_ALARM_DATE = "reminder_date"; // YYYY-MM-DD
    public static final String COL_ALARM_TIME = "reminder_time"; // HH:mm
    public static final String COL_ALARM_IS_ACTIVE = "is_active"; // 1 or 0
    public static final String COL_ALARM_CREATED_AT = "created_at";

    // History columns
    public static final String COL_HIST_ID = "id";
    public static final String COL_HIST_FOOD_ID = "food_id";
    public static final String COL_HIST_FOOD_NAME = "food_name";
    public static final String COL_HIST_TYPE = "activity_type"; // 'Dikonsumsi', 'Dimasak', 'Dibuang', 'Dihapus dari Inventaris'
    public static final String COL_HIST_QTY = "quantity";
    public static final String COL_HIST_DATE = "activity_date";
    public static final String COL_HIST_NOTES = "notes";

    // Settings columns
    public static final String COL_SETTING_ID = "id";
    public static final String COL_SETTING_KEY = "setting_key";
    public static final String COL_SETTING_VAL = "setting_value";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating FreshTrack database tables...");

        // Create Foods Table
        String createFoodsTable = "CREATE TABLE " + TABLE_FOODS + " ("
                + COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FOOD_NAME + " TEXT NOT NULL, "
                + COL_FOOD_CATEGORY + " TEXT NOT NULL, "
                + COL_FOOD_IMAGE + " TEXT, "
                + COL_FOOD_QUANTITY + " REAL NOT NULL, "
                + COL_FOOD_UNIT + " TEXT NOT NULL, "
                + COL_FOOD_PURCHASE_DATE + " TEXT NOT NULL, "
                + COL_FOOD_EXPIRY_DATE + " TEXT NOT NULL, "
                + COL_FOOD_LOCATION + " TEXT NOT NULL, "
                + COL_FOOD_PKG_STATUS + " TEXT NOT NULL DEFAULT 'Belum Dibuka', "
                + COL_FOOD_OPENED_DATE + " TEXT, "
                + COL_FOOD_OPENED_DAYS + " INTEGER DEFAULT 0, "
                + COL_FOOD_OPENED_DEADLINE + " TEXT, "
                + COL_FOOD_PRICE + " REAL DEFAULT 0, "
                + COL_FOOD_NOTES + " TEXT, "
                + COL_FOOD_CREATED_AT + " TEXT, "
                + COL_FOOD_UPDATED_AT + " TEXT, "
                + COL_FOOD_STATUS + " TEXT DEFAULT 'Aktif'"
                + ");";
        db.execSQL(createFoodsTable);

        // Create Alarms Table
        String createAlarmsTable = "CREATE TABLE " + TABLE_ALARMS + " ("
                + COL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ALARM_FOOD_ID + " INTEGER NOT NULL, "
                + COL_ALARM_TYPE + " TEXT NOT NULL, "
                + COL_ALARM_DATE + " TEXT NOT NULL, "
                + COL_ALARM_TIME + " TEXT NOT NULL, "
                + COL_ALARM_IS_ACTIVE + " INTEGER DEFAULT 1, "
                + COL_ALARM_CREATED_AT + " TEXT, "
                + "FOREIGN KEY (" + COL_ALARM_FOOD_ID + ") REFERENCES " + TABLE_FOODS + "(" + COL_FOOD_ID + ") ON DELETE CASCADE"
                + ");";
        db.execSQL(createAlarmsTable);

        // Create History Table
        String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + " ("
                + COL_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HIST_FOOD_ID + " INTEGER, "
                + COL_HIST_FOOD_NAME + " TEXT NOT NULL, "
                + COL_HIST_TYPE + " TEXT NOT NULL, "
                + COL_HIST_QTY + " REAL NOT NULL, "
                + COL_HIST_DATE + " TEXT NOT NULL, "
                + COL_HIST_NOTES + " TEXT"
                + ");";
        db.execSQL(createHistoryTable);

        // Create Settings Table
        String createSettingsTable = "CREATE TABLE " + TABLE_SETTINGS + " ("
                + COL_SETTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SETTING_KEY + " TEXT UNIQUE NOT NULL, "
                + COL_SETTING_VAL + " TEXT"
                + ");";
        db.execSQL(createSettingsTable);

        // Seed initial default settings and demonstration data
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());
        String nowTime = dateTimeSdf.format(cal.getTime());

        // Default Settings
        insertSetting(db, "user_name", "Budi Santoso");
        insertSetting(db, "user_email", "budi.santoso@email.com");
        insertSetting(db, "default_alarm_time", "20:00");
        insertSetting(db, "sound_enabled", "true");
        insertSetting(db, "vibration_enabled", "true");
        insertSetting(db, "theme", "light");
        insertSetting(db, "language", "id");

        // Seed Demo Foods
        // 1. Susu UHT (Kritis / Prioritas - Expire Besok / H-1)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String dateSusu = sdf.format(cal.getTime());
        long idSusu = insertFoodRaw(db, "Susu UHT", "Susu", "images/susu.jpg", 1, "Kotak", today, dateSusu, "Kulkas", "Belum Dibuka", null, 0, null, 18000, "Susu rendah lemak");

        // 2. Roti Tawar (Perhatian - Expire 3 hari lagi)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 3);
        String dateRoti = sdf.format(cal.getTime());
        long idRoti = insertFoodRaw(db, "Roti Tawar", "Makanan Siap Saji", "images/roti.jpg", 1, "Bungkus", today, dateRoti, "Suhu Ruang", "Sudah Dibuka", today, 3, dateRoti, 15000, "Simpan rapat");

        // 3. Telur (Aman - Expire 7 hari lagi)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 7);
        String dateTelur = sdf.format(cal.getTime());
        long idTelur = insertFoodRaw(db, "Telur", "Daging", "images/telur.jpg", 10, "Butir", today, dateTelur, "Kulkas", "Belum Dibuka", null, 0, null, 25000, "Telur ayam negeri segar");

        // 4. Apel (Aman - Expire 8 hari lagi)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 8);
        String dateApel = sdf.format(cal.getTime());
        insertFoodRaw(db, "Apel Fuji", "Buah", "images/apel.jpg", 3, "Buah", today, dateApel, "Suhu Ruang", "Belum Dibuka", null, 0, null, 22000, "Apel manis renyah");

        // 5. Daging Ayam (Prioritas - Expire 2 hari lagi di Kulkas)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 2);
        String dateAyam = sdf.format(cal.getTime());
        insertFoodRaw(db, "Daging Ayam Filet", "Daging", "images/ayam.jpg", 500, "Gram", today, dateAyam, "Kulkas", "Belum Dibuka", null, 0, null, 32000, "Untuk sop atau tumisan");

        // 6. Wortel (Aman - Expire 10 hari)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 10);
        String dateWortel = sdf.format(cal.getTime());
        insertFoodRaw(db, "Wortel Organik", "Sayuran", "images/wortel.jpg", 4, "Buah", today, dateWortel, "Kulkas", "Belum Dibuka", null, 0, null, 12000, "Wortel segar kaya vitamin");

        // 7. Keju Cheddar (Sudah dibuka - batas 5 hari)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 5);
        String dateKeju = sdf.format(cal.getTime());
        insertFoodRaw(db, "Keju Cheddar", "Susu", "images/keju.jpg", 1, "Kotak", today, dateKeju, "Kulkas", "Sudah Dibuka", today, 5, dateKeju, 24000, "Tutup dengan plastic wrap");

        // 8. Ikan Salmon (Aman - di Freezer 30 hari)
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 30);
        String dateIkan = sdf.format(cal.getTime());
        insertFoodRaw(db, "Ikan Salmon", "Ikan", "images/ikan.jpg", 2, "Porsi", today, dateIkan, "Freezer", "Belum Dibuka", null, 0, null, 65000, "Ikan segar beku");

        // Seed Sample Alarms
        insertAlarmRaw(db, idSusu, "H-1", today, "20:00", 1, nowTime);
        insertAlarmRaw(db, idRoti, "H-3", today, "08:00", 1, nowTime);

        // Seed Sample History
        insertHistoryRaw(db, idSusu, "Susu UHT", "Dikonsumsi", 0.5, nowTime, "Diminum setengah kotak saat sarapan");
        insertHistoryRaw(db, idRoti, "Roti Tawar", "Dimasak", 2, nowTime, "Dibuat roti bakar keju");
        insertHistoryRaw(db, 0, "Bayam Hijau", "Dibuang", 1, nowTime, "Layu dan berlendir karena lupa dimasak");
    }

    private void insertSetting(SQLiteDatabase db, String key, String val) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SETTING_KEY, key);
        cv.put(COL_SETTING_VAL, val);
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private long insertFoodRaw(SQLiteDatabase db, String name, String category, String image, double qty, String unit,
                               String purchaseDate, String expiryDate, String location, String pkgStatus,
                               String openedDate, int openedDays, String openedDeadline, double price, String notes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String now = sdf.format(new Date());
        ContentValues cv = new ContentValues();
        cv.put(COL_FOOD_NAME, name);
        cv.put(COL_FOOD_CATEGORY, category);
        cv.put(COL_FOOD_IMAGE, image);
        cv.put(COL_FOOD_QUANTITY, qty);
        cv.put(COL_FOOD_UNIT, unit);
        cv.put(COL_FOOD_PURCHASE_DATE, purchaseDate);
        cv.put(COL_FOOD_EXPIRY_DATE, expiryDate);
        cv.put(COL_FOOD_LOCATION, location);
        cv.put(COL_FOOD_PKG_STATUS, pkgStatus);
        cv.put(COL_FOOD_OPENED_DATE, openedDate);
        cv.put(COL_FOOD_OPENED_DAYS, openedDays);
        cv.put(COL_FOOD_OPENED_DEADLINE, openedDeadline);
        cv.put(COL_FOOD_PRICE, price);
        cv.put(COL_FOOD_NOTES, notes);
        cv.put(COL_FOOD_CREATED_AT, now);
        cv.put(COL_FOOD_UPDATED_AT, now);
        cv.put(COL_FOOD_STATUS, "Aktif");
        return db.insert(TABLE_FOODS, null, cv);
    }

    private void insertAlarmRaw(SQLiteDatabase db, long foodId, String type, String date, String time, int isActive, String createdAt) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ALARM_FOOD_ID, foodId);
        cv.put(COL_ALARM_TYPE, type);
        cv.put(COL_ALARM_DATE, date);
        cv.put(COL_ALARM_TIME, time);
        cv.put(COL_ALARM_IS_ACTIVE, isActive);
        cv.put(COL_ALARM_CREATED_AT, createdAt);
        db.insert(TABLE_ALARMS, null, cv);
    }

    private void insertHistoryRaw(SQLiteDatabase db, long foodId, String name, String type, double qty, String date, String notes) {
        ContentValues cv = new ContentValues();
        cv.put(COL_HIST_FOOD_ID, foodId);
        cv.put(COL_HIST_FOOD_NAME, name);
        cv.put(COL_HIST_TYPE, type);
        cv.put(COL_HIST_QTY, qty);
        cv.put(COL_HIST_DATE, date);
        cv.put(COL_HIST_NOTES, notes);
        db.insert(TABLE_HISTORY, null, cv);
    }
}
