package com.freshtrack.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView webView;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi Channel Notifikasi
        NotificationHelper.createNotificationChannel(this);

        // Inisialisasi Database SQLite
        DatabaseHelper.getInstance(this).getWritableDatabase();

        // Inisialisasi ActivityResultLauncher untuk Image Picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String localPath = ImagePickerHelper.saveImageToInternalStorage(this, uri);
                        if (localPath != null && webView != null) {
                            String escapedPath = localPath.replace("\\", "\\\\").replace("'", "\\'");
                            webView.evaluateJavascript("window.onImagePicked && window.onImagePicked('" + escapedPath + "');", null);
                        }
                    }
                }
        );

        // Inisialisasi ActivityResultLauncher untuk Izin Notifikasi (Android 13+)
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Notification permission granted.");
                    } else {
                        Toast.makeText(this, "Izin notifikasi diperlukan agar pengingat kedaluwarsa berfungsi.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Setup WebView
        webView = new WebView(this);
        setContentView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);

        // Hubungkan Java Native Bridge
        webView.addJavascriptInterface(new WebAppBridge(this), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "FreshTrack WebView finished loading: " + url);

                // Handle intent extras if opened from notification
                Intent intent = getIntent();
                if (intent != null && intent.hasExtra("target_page")) {
                    String targetPage = intent.getStringExtra("target_page");
                    long foodId = intent.getLongExtra("food_id", 0);
                    webView.evaluateJavascript("window.navigateTo && window.navigateTo('" + targetPage + "', " + foodId + ");", null);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("FreshTrackJS", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }
        });

        // Muat file index.html dari assets
        webView.loadUrl("file:///android_asset/web/index.html");

        // Tangani tombol Back fisik Android
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null) {
                    webView.evaluateJavascript("window.handleAndroidBack && window.handleAndroidBack();", value -> {
                        if ("false".equals(value) || "null".equals(value)) {
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {
                                finish();
                            }
                        }
                    });
                } else {
                    finish();
                }
            }
        });

        // Minta izin runtime yang dibutuhkan
        requestSystemPermissions();
    }

    public void launchImagePicker() {
        if (imagePickerLauncher != null) {
            imagePickerLauncher.launch("image/*");
        }
    }

    public void requestSystemPermissions() {
        // Request POST_NOTIFICATIONS on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Check Exact Alarm on Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.hasExtra("target_page") && webView != null) {
            String targetPage = intent.getStringExtra("target_page");
            long foodId = intent.getLongExtra("food_id", 0);
            webView.evaluateJavascript("window.navigateTo && window.navigateTo('" + targetPage + "', " + foodId + ");", null);
        }
    }
}
