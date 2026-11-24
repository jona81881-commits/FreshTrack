package com.freshtrack.aplikasi.penghubung;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.freshtrack.aplikasi.inti.AktivitasUtama;
import com.freshtrack.aplikasi.makanan.PengelolaMakanan;
import com.freshtrack.aplikasi.pengaturan.PengelolaPengaturan;

import org.json.JSONArray;
import org.json.JSONObject;

public class PenghubungAplikasiWeb {

    private final AktivitasUtama activity;
    private final PengelolaMakanan foodManager;
    private final PengelolaPengaturan settingsManager;
    private final Handler mainHandler;

    public PenghubungAplikasiWeb(AktivitasUtama activity) {
        this.activity = activity;
        this.foodManager = new PengelolaMakanan(activity);
        this.settingsManager = new PengelolaPengaturan(activity);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @JavascriptInterface
    public String getDashboardSummary() {
        return foodManager.getDashboardSummary().toString();
    }

    @JavascriptInterface
    public String getFoods(String category, String searchQuery, String sortBy) {
        return foodManager.getFoods(category, searchQuery, sortBy).toString();
    }

    @JavascriptInterface
    public String getFoodById(long id) {
        JSONObject food = foodManager.getFoodById(id);
        return food != null ? food.toString() : "{}";
    }

    @JavascriptInterface
    public String addFood(String foodJsonStr) {
        try {
            JSONObject foodJson = new JSONObject(foodJsonStr);
            long id = foodManager.addFood(foodJson);
            JSONObject res = new JSONObject();
            res.put("success", id > 0);
            res.put("id", id);
            return res.toString();
        } catch (Exception e) {
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @JavascriptInterface
    public boolean updateFood(String foodJsonStr) {
        try {
            JSONObject foodJson = new JSONObject(foodJsonStr);
            return foodManager.updateFood(foodJson);
        } catch (Exception e) {
            return false;
        }
    }

    @JavascriptInterface
    public boolean deleteFood(long id) {
        return foodManager.deleteFood(id);
    }

    @JavascriptInterface
    public boolean markAsOpened(long id, String openedDate, int usageDays) {
        return foodManager.markAsOpened(id, openedDate, usageDays);
    }

    @JavascriptInterface
    public boolean recordActivity(long foodId, String foodName, String activityType, double quantity, String notes) {
        return foodManager.recordActivity(foodId, foodName, activityType, quantity, notes);
    }

    @JavascriptInterface
    public String getHistory(String filterType, String searchQuery) {
        return foodManager.getHistory(filterType, searchQuery).toString();
    }

    @JavascriptInterface
    public String getAlarms() {
        return foodManager.getAlarms().toString();
    }

    @JavascriptInterface
    public boolean toggleAlarm(long alarmId, boolean isActive) {
        return foodManager.toggleAlarm(alarmId, isActive);
    }

    @JavascriptInterface
    public String getAnalytics(String timeRange) {
        return foodManager.getAnalytics(timeRange).toString();
    }

    @JavascriptInterface
    public String getSettings() {
        return settingsManager.getAllSettingsAsJson();
    }

    @JavascriptInterface
    public boolean saveSetting(String key, String value) {
        return settingsManager.setSetting(key, value);
    }

    @JavascriptInterface
    public void pickImage() {
        mainHandler.post(activity::launchImagePicker);
    }

    @JavascriptInterface
    public void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    @JavascriptInterface
    public void requestNotificationPermission() {
        mainHandler.post(activity::requestSystemPermissions);
    }
}
