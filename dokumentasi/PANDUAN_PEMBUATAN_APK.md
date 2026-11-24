# Panduan Pembuatan APK FreshTrack melalui VS Code

Panduan langkah demi langkah untuk mengompilasi kode sumber **FreshTrack** menjadi file APK (*Android Package Kit*) siap pakai menggunakan terminal Visual Studio Code di Windows tanpa menggunakan Android Studio.

---

## 1. Persiapan Lingkungan

Pastikan variabel lingkungan Windows telah disetel ke JDK dan Android SDK:

```powershell
# Jalankan di PowerShell jika belum disetel secara permanen:
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Periksa versi Gradle Wrapper yang terpasang di proyek:
```powershell
cd C:\Users\ASUS\.gemini\antigravity\scratch\FreshTrack
.\gradlew.bat --version
```

---

## 2. Membersihkan Build Lama
Sebelum membuat file APK baru, bersihkan cache build terdahulu:
```powershell
.\gradlew.bat clean
```

---

## 3. Mengompilasi APK Debug
Jalankan perintah kompilasi Gradle untuk modul `:aplikasi`:
```powershell
.\gradlew.bat assembleDebug
```

Setelah proses kompilasi selesai dengan keterangan:
```text
BUILD SUCCESSFUL in Xm Ys
```

File APK siap pakai dapat ditemukan di lokasi:
📍 `aplikasi/build/outputs/apk/debug/aplikasi-debug.apk`

---

## 4. Memasang APK ke HP Android Fisik via USB Debugging

1. **Aktifkan Mode Pengembang di HP:**
   - Masuk ke **Pengaturan** -> **Tentang Ponsel**.
   - Ketuk kolom **Nomor Bentukan (Build Number)** sebanyak 7 kali hingga muncul pesan "Anda sekarang adalah seorang pengembang".
2. **Aktifkan USB Debugging:**
   - Masuk ke **Pengaturan Sistem** -> **Opsi Pengembang**.
   - Aktifkan tombol **USB Debugging**.
3. **Hubungkan HP ke Komputer:**
   - Sambungkan menggunakan kabel data USB.
   - Pada layar HP, centang "Selalu izinkan dari komputer ini" lalu tekan **Izinkan**.
4. **Verifikasi Sambungan di Terminal VS Code:**
   ```powershell
   adb devices
   ```
   *(Pastikan status perangkat muncul dengan keterangan `device`)*.
5. **Pasang File APK:**
   ```powershell
   adb install -r aplikasi/build/outputs/apk/debug/aplikasi-debug.apk
   ```

---

## 5. Menjalankan Aplikasi dan Memantau Logcat
Untuk langsung membuka aplikasi di ponsel:
```powershell
adb shell am start -n com.freshtrack.aplikasi.debug/com.freshtrack.aplikasi.inti.AktivitasUtama
```

Untuk memantau aktivitas proses dan pesan JavaScript:
```powershell
adb logcat -s FreshTrack FreshTrackJS PembantuBasisData PengelolaMakanan PenerimaAlarm
```
