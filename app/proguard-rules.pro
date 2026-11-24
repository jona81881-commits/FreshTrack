# Proguard rules for FreshTrack
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.freshtrack.app.WebAppBridge {
    public *;
}
