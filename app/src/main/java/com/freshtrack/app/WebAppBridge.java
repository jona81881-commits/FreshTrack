package com.freshtrack.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class WebAppBridge {

    private final MainActivity activity;
    private final FoodManager foodManager;
    private final SettingsManager settingsManager;
    private final Handler mainHandler;

    public WebAppBridge(MainActivity activity) {
        this.activity = activity;
        this.foodManager = new FoodManager(activity);
        this.settingsManager = new SettingsManager(activity);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @JavascriptInterface
    public String getDashboardSummary() {
        JSONObject summary = foodManager.getDashboardSummary();
        return summary.toString();
    }

    @JavascriptInterface
    public String getFoods(String category, String searchQuery, String sortBy) {
        JSONArray foods = foodManager.getFoods(category, searchQuery, sortBy);
        return foods.toString();
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
        JSONArray history = foodManager.getHistory(filterType, searchQuery);
        return history.toString();
    }

    @JavascriptInterface
    public String getAlarms() {
        JSONArray alarms = foodManager.getAlarms();
        return alarms.toString();
    }

    @JavascriptInterface
    public boolean toggleAlarm(long alarmId, boolean isActive) {
        return foodManager.toggleAlarm(alarmId, isActive);
    }

    @JavascriptInterface
    public String getAnalytics(String timeRange) {
        JSONObject analytics = foodManager.getAnalytics(timeRange);
        return analytics.toString();
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
