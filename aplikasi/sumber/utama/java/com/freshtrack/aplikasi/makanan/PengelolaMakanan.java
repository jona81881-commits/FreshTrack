package com.freshtrack.aplikasi.makanan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.freshtrack.aplikasi.alarm.PembantuAlarm;
import com.freshtrack.aplikasi.data.basis_data.PembantuBasisData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PengelolaMakanan {

    private static final String TAG = "PengelolaMakanan";
    private final Context context;
    private final PembantuBasisData dbHelper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public PengelolaMakanan(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = PembantuBasisData.getInstance(this.context);
    }

    public PriorityInfo calculatePriority(String expiryDateStr, String packageStatus, String openedDeadlineStr) {
        try {
            Date today = dateFormat.parse(dateFormat.format(new Date()));
            Date expiryDate = dateFormat.parse(expiryDateStr);
            Date referenceDate = expiryDate;

            if ("Sudah Dibuka".equalsIgnoreCase(packageStatus) && openedDeadlineStr != null && !openedDeadlineStr.trim().isEmpty()) {
                Date openedDeadline = dateFormat.parse(openedDeadlineStr);
                if (openedDeadline != null && openedDeadline.before(referenceDate)) {
                    referenceDate = openedDeadline;
                }
            }

            if (referenceDate == null || today == null) {
                return new PriorityInfo("Normal", "Aman", 999, referenceDate != null ? dateFormat.format(referenceDate) : expiryDateStr);
            }

            long diffMillis = referenceDate.getTime() - today.getTime();
            long daysRemaining = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

            String priorityLevel;
            String badgeText;

            if (daysRemaining <= 0) {
                priorityLevel = "Kritis";
                badgeText = daysRemaining < 0 ? "Kedaluwarsa" : "Hari Ini!";
            } else if (daysRemaining == 1) {
                priorityLevel = "Tinggi";
                badgeText = "Prioritas";
            } else if (daysRemaining <= 3) {
                priorityLevel = "Sedang";
                badgeText = "Perhatian";
            } else {
                priorityLevel = "Normal";
                badgeText = "Aman";
            }

            return new PriorityInfo(priorityLevel, badgeText, (int) daysRemaining, dateFormat.format(referenceDate));
        } catch (Exception e) {
            return new PriorityInfo("Normal", "Aman", 999, expiryDateStr);
        }
    }

    public static class PriorityInfo {
        public final String level;
        public final String badge;
        public final int daysRemaining;
        public final String referenceDate;

        public PriorityInfo(String level, String badge, int daysRemaining, String referenceDate) {
            this.level = level;
            this.badge = badge;
            this.daysRemaining = daysRemaining;
            this.referenceDate = referenceDate;
        }
    }

    public JSONArray getFoods(String category, String searchQuery, String sortBy) {
        JSONArray array = new JSONArray();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + PembantuBasisData.TABLE_FOODS + " WHERE status = 'Aktif' ");
        java.util.List<String> argsList = new java.util.ArrayList<>();

        if (category != null && !category.isEmpty() && !"Semua".equalsIgnoreCase(category)) {
            query.append("AND category = ? ");
            argsList.add(category);
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.append("AND name LIKE ? ");
            argsList.add("%" + searchQuery.trim() + "%");
        }

        if ("Nama Makanan".equalsIgnoreCase(sortBy)) {
            query.append("ORDER BY name ASC");
        } else if ("Terbaru Ditambahkan".equalsIgnoreCase(sortBy)) {
            query.append("ORDER BY id DESC");
        } else {
            query.append("ORDER BY expiry_date ASC, id ASC");
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query.toString(), argsList.toArray(new String[0]));
            while (cursor.moveToNext()) {
                JSONObject obj = cursorToFoodJson(cursor);
                array.put(obj);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching foods: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }

        if ("Prioritas Konsumsi".equalsIgnoreCase(sortBy)) {
            return sortFoodsByPriority(array);
        }

        return array;
    }

    private JSONArray sortFoodsByPriority(JSONArray input) {
        try {
            java.util.List<JSONObject> list = new java.util.ArrayList<>();
            for (int i = 0; i < input.length(); i++) {
                list.add(input.getJSONObject(i));
            }
            list.sort((a, b) -> {
                int daysA = a.optInt("days_remaining", 999);
                int daysB = b.optInt("days_remaining", 999);
                return Integer.compare(daysA, daysB);
            });
            JSONArray sorted = new JSONArray();
            for (JSONObject obj : list) {
                sorted.put(obj);
            }
            return sorted;
        } catch (Exception e) {
            return input;
        }
    }

    public JSONObject getFoodById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    PembantuBasisData.TABLE_FOODS,
                    null,
                    PembantuBasisData.COL_FOOD_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToFoodJson(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting food by id: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public long addFood(JSONObject food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String now = dateTimeFormat.format(new Date());
            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_FOOD_NAME, food.getString("name"));
            cv.put(PembantuBasisData.COL_FOOD_CATEGORY, food.getString("category"));
            cv.put(PembantuBasisData.COL_FOOD_IMAGE, food.optString("image_path", ""));
            cv.put(PembantuBasisData.COL_FOOD_QUANTITY, food.getDouble("quantity"));
            cv.put(PembantuBasisData.COL_FOOD_UNIT, food.getString("unit"));
            cv.put(PembantuBasisData.COL_FOOD_PURCHASE_DATE, food.getString("purchase_date"));
            cv.put(PembantuBasisData.COL_FOOD_EXPIRY_DATE, food.getString("expiry_date"));
            cv.put(PembantuBasisData.COL_FOOD_LOCATION, food.getString("storage_location"));
            cv.put(PembantuBasisData.COL_FOOD_PKG_STATUS, food.optString("package_status", "Belum Dibuka"));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DATE, food.optString("opened_date", null));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DAYS, food.optInt("opened_usage_days", 0));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DEADLINE, food.optString("opened_usage_deadline", null));
            cv.put(PembantuBasisData.COL_FOOD_PRICE, food.optDouble("price", 0.0));
            cv.put(PembantuBasisData.COL_FOOD_NOTES, food.optString("notes", ""));
            cv.put(PembantuBasisData.COL_FOOD_CREATED_AT, now);
            cv.put(PembantuBasisData.COL_FOOD_UPDATED_AT, now);
            cv.put(PembantuBasisData.COL_FOOD_STATUS, "Aktif");

            long id = db.insert(PembantuBasisData.TABLE_FOODS, null, cv);

            if (id > 0) {
                createDefaultAlarmsForFood(db, id, food.getString("name"), food.getString("expiry_date"));
                db.setTransactionSuccessful();
            }
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding food: " + e.getMessage(), e);
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateFood(JSONObject food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long id = food.getLong("id");
            String now = dateTimeFormat.format(new Date());

            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_FOOD_NAME, food.getString("name"));
            cv.put(PembantuBasisData.COL_FOOD_CATEGORY, food.getString("category"));
            if (food.has("image_path")) {
                cv.put(PembantuBasisData.COL_FOOD_IMAGE, food.getString("image_path"));
            }
            cv.put(PembantuBasisData.COL_FOOD_QUANTITY, food.getDouble("quantity"));
            cv.put(PembantuBasisData.COL_FOOD_UNIT, food.getString("unit"));
            cv.put(PembantuBasisData.COL_FOOD_PURCHASE_DATE, food.getString("purchase_date"));
            cv.put(PembantuBasisData.COL_FOOD_EXPIRY_DATE, food.getString("expiry_date"));
            cv.put(PembantuBasisData.COL_FOOD_LOCATION, food.getString("storage_location"));
            cv.put(PembantuBasisData.COL_FOOD_PKG_STATUS, food.optString("package_status", "Belum Dibuka"));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DATE, food.optString("opened_date", null));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DAYS, food.optInt("opened_usage_days", 0));
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DEADLINE, food.optString("opened_usage_deadline", null));
            cv.put(PembantuBasisData.COL_FOOD_PRICE, food.optDouble("price", 0.0));
            cv.put(PembantuBasisData.COL_FOOD_NOTES, food.optString("notes", ""));
            cv.put(PembantuBasisData.COL_FOOD_UPDATED_AT, now);

            int rows = db.update(PembantuBasisData.TABLE_FOODS, cv, PembantuBasisData.COL_FOOD_ID + " = ?", new String[]{String.valueOf(id)});

            if (rows > 0) {
                refreshAlarmsForFood(id, food.getString("name"), food.getString("expiry_date"));
            }
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating food: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteFood(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            JSONObject food = getFoodById(id);
            if (food != null) {
                recordActivity(id, food.getString("name"), "Dihapus dari Inventaris", food.getDouble("quantity"), "Dihapus manual oleh pengguna");
            }

            cancelAlarmsForFood(id);

            int rows = db.delete(PembantuBasisData.TABLE_FOODS, PembantuBasisData.COL_FOOD_ID + " = ?", new String[]{String.valueOf(id)});
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting food: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean markAsOpened(long id, String openedDate, int usageDays) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date openDate = sdf.parse(openedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(openDate != null ? openDate : new Date());
            cal.add(Calendar.DAY_OF_YEAR, usageDays);
            String deadline = sdf.format(cal.getTime());

            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_FOOD_PKG_STATUS, "Sudah Dibuka");
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DATE, openedDate);
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DAYS, usageDays);
            cv.put(PembantuBasisData.COL_FOOD_OPENED_DEADLINE, deadline);
            cv.put(PembantuBasisData.COL_FOOD_UPDATED_AT, dateTimeFormat.format(new Date()));

            int rows = db.update(PembantuBasisData.TABLE_FOODS, cv, PembantuBasisData.COL_FOOD_ID + " = ?", new String[]{String.valueOf(id)});
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error marking as opened: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean recordActivity(long foodId, String foodName, String activityType, double quantity, String notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String now = dateTimeFormat.format(new Date());
            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_HIST_FOOD_ID, foodId);
            cv.put(PembantuBasisData.COL_HIST_FOOD_NAME, foodName);
            cv.put(PembantuBasisData.COL_HIST_TYPE, activityType);
            cv.put(PembantuBasisData.COL_HIST_QTY, quantity);
            cv.put(PembantuBasisData.COL_HIST_DATE, now);
            cv.put(PembantuBasisData.COL_HIST_NOTES, notes);

            long histId = db.insert(PembantuBasisData.TABLE_HISTORY, null, cv);

            if (foodId > 0 && ("Dikonsumsi".equals(activityType) || "Dimasak".equals(activityType) || "Dibuang".equals(activityType))) {
                JSONObject food = getFoodById(foodId);
                if (food != null) {
                    double currentQty = food.getDouble("quantity");
                    double newQty = Math.max(0, currentQty - quantity);
                    ContentValues updateCv = new ContentValues();
                    updateCv.put(PembantuBasisData.COL_FOOD_QUANTITY, newQty);
                    updateCv.put(PembantuBasisData.COL_FOOD_UPDATED_AT, now);
                    if (newQty <= 0) {
                        updateCv.put(PembantuBasisData.COL_FOOD_STATUS, "Dibuang".equals(activityType) ? "Dibuang" : "Habis");
                        cancelAlarmsForFood(foodId);
                    }
                    db.update(PembantuBasisData.TABLE_FOODS, updateCv, PembantuBasisData.COL_FOOD_ID + " = ?", new String[]{String.valueOf(foodId)});
                }
            }

            db.setTransactionSuccessful();
            return histId > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error recording activity: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public JSONArray getHistory(String filterType, String searchQuery) {
        JSONArray array = new JSONArray();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + PembantuBasisData.TABLE_HISTORY + " WHERE 1=1 ");
        java.util.List<String> args = new java.util.ArrayList<>();

        if (filterType != null && !filterType.isEmpty() && !"Semua".equalsIgnoreCase(filterType)) {
            query.append("AND activity_type = ? ");
            args.add(filterType);
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.append("AND food_name LIKE ? ");
            args.add("%" + searchQuery.trim() + "%");
        }

        query.append("ORDER BY id DESC");

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
            while (cursor.moveToNext()) {
                JSONObject obj = new JSONObject();
                obj.put("id", cursor.getLong(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_ID)));
                obj.put("food_id", cursor.getLong(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_FOOD_ID)));
                obj.put("food_name", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_FOOD_NAME)));
                obj.put("activity_type", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_TYPE)));
                obj.put("quantity", cursor.getDouble(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_QTY)));
                obj.put("activity_date", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_DATE)));
                obj.put("notes", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_HIST_NOTES)));
                array.put(obj);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting history: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return array;
    }

    public JSONObject getDashboardSummary() {
        JSONObject summary = new JSONObject();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            int total = 0;
            int unopened = 0;
            int opened = 0;
            int urgent = 0;

            String query = "SELECT id, expiry_date, package_status, opened_usage_deadline FROM "
                    + PembantuBasisData.TABLE_FOODS + " WHERE status = 'Aktif'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    total++;
                    String pkgStatus = cursor.getString(2);
                    String expiry = cursor.getString(1);
                    String openedDeadline = cursor.getString(3);

                    if ("Sudah Dibuka".equalsIgnoreCase(pkgStatus)) {
                        opened++;
                    } else {
                        unopened++;
                    }

                    PriorityInfo p = calculatePriority(expiry, pkgStatus, openedDeadline);
                    if (p.daysRemaining <= 3) {
                        urgent++;
                    }
                }
                cursor.close();
            }

            summary.put("total_food", total);
            summary.put("unopened", unopened);
            summary.put("opened", opened);
            summary.put("urgent", urgent);

            JSONArray priorityFoods = new JSONArray();
            JSONArray allFoods = getFoods("Semua", "", "Prioritas Konsumsi");
            for (int i = 0; i < Math.min(5, allFoods.length()); i++) {
                priorityFoods.put(allFoods.getJSONObject(i));
            }
            summary.put("priority_foods", priorityFoods);

            JSONObject nextAlarm = getNextUpcomingAlarm();
            summary.put("next_alarm", nextAlarm != null ? nextAlarm : JSONObject.NULL);

        } catch (Exception e) {
            Log.e(TAG, "Error getting dashboard summary: " + e.getMessage(), e);
        }
        return summary;
    }

    public JSONObject getAnalytics(String timeRange) {
        JSONObject analytics = new JSONObject();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Calendar cal = Calendar.getInstance();
            if ("7 Hari".equalsIgnoreCase(timeRange)) {
                cal.add(Calendar.DAY_OF_YEAR, -7);
            } else if ("1 Tahun".equalsIgnoreCase(timeRange)) {
                cal.add(Calendar.YEAR, -1);
            } else {
                cal.add(Calendar.DAY_OF_YEAR, -30);
            }
            String minDate = dateFormat.format(cal.getTime());

            int consumedCount = 0;
            double estimatedSavedMoney = 0;
            String consumedQuery = "SELECT h.quantity, f.price, f.quantity as origQty "
                    + "FROM " + PembantuBasisData.TABLE_HISTORY + " h "
                    + "LEFT JOIN " + PembantuBasisData.TABLE_FOODS + " f ON h.food_id = f.id "
                    + "WHERE h.activity_type IN ('Dikonsumsi', 'Dimasak') AND h.activity_date >= ?";
            Cursor c1 = db.rawQuery(consumedQuery, new String[]{minDate});
            if (c1 != null) {
                while (c1.moveToNext()) {
                    consumedCount++;
                    double qty = c1.getDouble(0);
                    double price = c1.getDouble(1);
                    double origQty = c1.getDouble(2);
                    if (price > 0 && origQty > 0) {
                        estimatedSavedMoney += (qty / origQty) * price;
                    } else if (price > 0) {
                        estimatedSavedMoney += price;
                    }
                }
                c1.close();
            }

            int wastedCount = 0;
            Map<String, Integer> categoryWasteMap = new HashMap<>();
            String wastedQuery = "SELECT h.quantity, f.category, f.price "
                    + "FROM " + PembantuBasisData.TABLE_HISTORY + " h "
                    + "LEFT JOIN " + PembantuBasisData.TABLE_FOODS + " f ON h.food_id = f.id "
                    + "WHERE h.activity_type = 'Dibuang' AND h.activity_date >= ?";
            Cursor c2 = db.rawQuery(wastedQuery, new String[]{minDate});
            if (c2 != null) {
                while (c2.moveToNext()) {
                    wastedCount++;
                    String cat = c2.getString(1);
                    if (cat == null || cat.isEmpty()) cat = "Lainnya";
                    categoryWasteMap.put(cat, categoryWasteMap.getOrDefault(cat, 0) + 1);
                }
                c2.close();
            }

            int totalAction = consumedCount + wastedCount;
            double wastePercent = totalAction > 0 ? ((double) wastedCount / totalAction) * 100.0 : 0.0;
            double optimalPercent = totalAction > 0 ? ((double) consumedCount / totalAction) * 100.0 : 100.0;

            analytics.put("total_consumed", consumedCount);
            analytics.put("total_wasted", wastedCount);
            analytics.put("waste_percentage", Math.round(wastePercent * 10.0) / 10.0);
            analytics.put("optimal_percentage", Math.round(optimalPercent * 10.0) / 10.0);
            analytics.put("estimated_saved", Math.round(estimatedSavedMoney > 0 ? estimatedSavedMoney : 45000));

            String topCat = "Sayuran";
            int topCatPercent = 40;
            if (!categoryWasteMap.isEmpty()) {
                String highestCat = "";
                int highestCount = 0;
                for (Map.Entry<String, Integer> entry : categoryWasteMap.entrySet()) {
                    if (entry.getValue() > highestCount) {
                        highestCount = entry.getValue();
                        highestCat = entry.getKey();
                    }
                }
                topCat = highestCat;
                topCatPercent = (int) Math.round(((double) highestCount / wastedCount) * 100);
            }
            analytics.put("top_wasted_category", topCat);
            analytics.put("top_wasted_percentage", topCatPercent);

        } catch (Exception e) {
            Log.e(TAG, "Error calculating analytics: " + e.getMessage(), e);
        }
        return analytics;
    }

    public JSONArray getAlarms() {
        JSONArray array = new JSONArray();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT a.*, f.name, f.image_path, f.expiry_date "
                + "FROM " + PembantuBasisData.TABLE_ALARMS + " a "
                + "JOIN " + PembantuBasisData.TABLE_FOODS + " f ON a.food_id = f.id "
                + "ORDER BY a.reminder_date ASC, a.reminder_time ASC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                JSONObject obj = new JSONObject();
                obj.put("id", cursor.getLong(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_ID)));
                obj.put("food_id", cursor.getLong(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_FOOD_ID)));
                obj.put("food_name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
                obj.put("image_path", cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                obj.put("reminder_type", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_TYPE)));
                obj.put("reminder_date", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_DATE)));
                obj.put("reminder_time", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_TIME)));
                obj.put("is_active", cursor.getInt(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_IS_ACTIVE)) == 1);
                obj.put("expiry_date", cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
                array.put(obj);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting alarms: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return array;
    }

    public boolean toggleAlarm(long alarmId, boolean isActive) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_ALARM_IS_ACTIVE, isActive ? 1 : 0);
            int rows = db.update(PembantuBasisData.TABLE_ALARMS, cv, PembantuBasisData.COL_ALARM_ID + " = ?", new String[]{String.valueOf(alarmId)});

            if (rows > 0) {
                if (isActive) {
                    String q = "SELECT a.food_id, a.reminder_type, a.reminder_date, a.reminder_time, f.name "
                            + "FROM " + PembantuBasisData.TABLE_ALARMS + " a "
                            + "JOIN " + PembantuBasisData.TABLE_FOODS + " f ON a.food_id = f.id "
                            + "WHERE a.id = ?";
                    Cursor c = db.rawQuery(q, new String[]{String.valueOf(alarmId)});
                    if (c != null && c.moveToFirst()) {
                        PembantuAlarm.jadwalkanAlarm(context, alarmId, c.getLong(0), c.getString(4), c.getString(1), c.getString(2), c.getString(3));
                        c.close();
                    }
                } else {
                    PembantuAlarm.batalkanAlarm(context, alarmId);
                }
            }
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error toggling alarm: " + e.getMessage(), e);
            return false;
        }
    }

    public JSONObject getNextUpcomingAlarm() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT a.*, f.name "
                + "FROM " + PembantuBasisData.TABLE_ALARMS + " a "
                + "JOIN " + PembantuBasisData.TABLE_FOODS + " f ON a.food_id = f.id "
                + "WHERE a.is_active = 1 AND f.status = 'Aktif' "
                + "ORDER BY a.reminder_date ASC, a.reminder_time ASC LIMIT 1";
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                JSONObject obj = new JSONObject();
                obj.put("id", c.getLong(c.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_ID)));
                obj.put("food_name", c.getString(c.getColumnIndexOrThrow("name")));
                obj.put("reminder_date", c.getString(c.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_DATE)));
                obj.put("reminder_time", c.getString(c.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_TIME)));
                obj.put("reminder_type", c.getString(c.getColumnIndexOrThrow(PembantuBasisData.COL_ALARM_TYPE)));
                return obj;
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    private void createDefaultAlarmsForFood(SQLiteDatabase db, long foodId, String foodName, String expiryDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(expiryDateStr);
            if (expiry == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTime(expiry);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String hMinus1 = sdf.format(cal.getTime());

            ContentValues cv = new ContentValues();
            cv.put(PembantuBasisData.COL_ALARM_FOOD_ID, foodId);
            cv.put(PembantuBasisData.COL_ALARM_TYPE, "H-1");
            cv.put(PembantuBasisData.COL_ALARM_DATE, hMinus1);
            cv.put(PembantuBasisData.COL_ALARM_TIME, "20:00");
            cv.put(PembantuBasisData.COL_ALARM_IS_ACTIVE, 1);
            cv.put(PembantuBasisData.COL_ALARM_CREATED_AT, dateTimeFormat.format(new Date()));

            long alarmId = db.insert(PembantuBasisData.TABLE_ALARMS, null, cv);
            if (alarmId > 0) {
                PembantuAlarm.jadwalkanAlarm(context, alarmId, foodId, foodName, "H-1", hMinus1, "20:00");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating default alarm: " + e.getMessage(), e);
        }
    }

    private void refreshAlarmsForFood(long foodId, String foodName, String expiryDateStr) {
        cancelAlarmsForFood(foodId);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        createDefaultAlarmsForFood(db, foodId, foodName, expiryDateStr);
    }

    private void cancelAlarmsForFood(long foodId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PembantuBasisData.TABLE_ALARMS, new String[]{PembantuBasisData.COL_ALARM_ID},
                PembantuBasisData.COL_ALARM_FOOD_ID + " = ?", new String[]{String.valueOf(foodId)}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long alarmId = cursor.getLong(0);
                PembantuAlarm.batalkanAlarm(context, alarmId);
            }
            cursor.close();
        }
    }

    private JSONObject cursorToFoodJson(Cursor cursor) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", cursor.getLong(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_ID)));
            obj.put("name", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_NAME)));
            obj.put("category", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_CATEGORY)));
            obj.put("image_path", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_IMAGE)));
            obj.put("quantity", cursor.getDouble(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_QUANTITY)));
            obj.put("unit", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_UNIT)));
            obj.put("purchase_date", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_PURCHASE_DATE)));
            obj.put("expiry_date", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_EXPIRY_DATE)));
            obj.put("storage_location", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_LOCATION)));
            obj.put("package_status", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_PKG_STATUS)));
            obj.put("opened_date", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_OPENED_DATE)));
            obj.put("opened_usage_days", cursor.getInt(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_OPENED_DAYS)));
            obj.put("opened_usage_deadline", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_OPENED_DEADLINE)));
            obj.put("price", cursor.getDouble(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_PRICE)));
            obj.put("notes", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_NOTES)));
            obj.put("status", cursor.getString(cursor.getColumnIndexOrThrow(PembantuBasisData.COL_FOOD_STATUS)));

            PriorityInfo p = calculatePriority(
                    obj.getString("expiry_date"),
                    obj.getString("package_status"),
                    obj.optString("opened_usage_deadline", null)
            );
            obj.put("priority_level", p.level);
            obj.put("priority_badge", p.badge);
            obj.put("days_remaining", p.daysRemaining);
            obj.put("reference_date", p.referenceDate);

            return obj;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing cursor to JSON: " + e.getMessage(), e);
            return new JSONObject();
        }
    }
}
