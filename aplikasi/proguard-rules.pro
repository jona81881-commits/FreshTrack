# Aturan Proguard untuk FreshTrack
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.freshtrack.aplikasi.penghubung.PenghubungAplikasiWeb {
    public *;
}
