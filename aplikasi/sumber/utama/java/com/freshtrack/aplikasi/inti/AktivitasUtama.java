package com.freshtrack.aplikasi.inti;

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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.freshtrack.aplikasi.data.basis_data.PembantuBasisData;
import com.freshtrack.aplikasi.gambar.PembantuGambar;
import com.freshtrack.aplikasi.notifikasi.PembantuNotifikasi;
import com.freshtrack.aplikasi.penghubung.PenghubungAplikasiWeb;

public class AktivitasUtama extends AppCompatActivity {

    private static final String TAG = "AktivitasUtama";
    private WebView webView;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PembantuNotifikasi.buatSaluranNotifikasi(this);
        PembantuBasisData.getInstance(this).getWritableDatabase();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String localPath = PembantuGambar.simpanGambarKePenyimpananInternal(this, uri);
                        if (localPath != null && webView != null) {
                            String escapedPath = localPath.replace("\\", "\\\\").replace("'", "\\'");
                            webView.evaluateJavascript("window.onImagePicked && window.onImagePicked('" + escapedPath + "');", null);
                        }
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Izin notifikasi diberikan.");
                    } else {
                        Toast.makeText(this, "Izin notifikasi diperlukan agar pengingat kedaluwarsa berfungsi.", Toast.LENGTH_LONG).show();
                    }
                }
        );

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

        webView.addJavascriptInterface(new PenghubungAplikasiWeb(this), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "FreshTrack WebView selesai memuat: " + url);

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
                Log.d("FreshTrackJS", consoleMessage.message() + " -- Baris "
                        + consoleMessage.lineNumber() + " dari " + consoleMessage.sourceId());
                return true;
            }
        });

        // Muat halaman_utama.html dari struktur aset web baru
        webView.loadUrl("file:///android_asset/web/halaman_utama.html");

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

        requestSystemPermissions();
    }

    public void launchImagePicker() {
        if (imagePickerLauncher != null) {
            imagePickerLauncher.launch("image/*");
        }
    }

    public void requestSystemPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

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
