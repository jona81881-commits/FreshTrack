# FreshTrack – Aplikasi Pengelolaan Persediaan dan Pengingat Kedaluwarsa Makanan

Aplikasi Android modern, minimalis, dan profesional untuk mencatat persediaan makanan rumah tangga, memantau tanggal kedaluwarsa, melacak batas konsumsi setelah kemasan dibuka, menghitung prioritas konsumsi, serta menjadwalkan alarm & notifikasi otomatis menggunakan **VS Code, HTML5, CSS3, JavaScript, Java Native, dan SQLite**.

---

## 1. Arsitektur Proyek
- **Frontend / WebView:** HTML5, CSS3, JavaScript modular (SPA). Memuat antarmuka dari aset lokal `file:///android_asset/web/halaman_utama.html`.
- **Native Bridge:** `com.freshtrack.aplikasi.penghubung.PenghubungAplikasiWeb` menghubungkan UI web dengan backend Java melalui `@JavascriptInterface`.
- **Penyimpanan Data Lokal:** `SQLite` (`freshtrack.db`) dengan tabel `foods`, `alarms`, `history`, dan `settings`.
- **Pengingat & Alarm:** Android `AlarmManager`, `BroadcastReceiver` (`PenerimaAlarm`), `NotificationManager` (`PembantuNotifikasi`), dan `PenerimaBoot` untuk restore alarm otomatis saat reboot.
- **Manajemen Gambar:** `PembantuGambar` menyalin foto makanan dari galeri ke penyimpanan internal aplikasi secara permanen.

---

## 2. Struktur Direktori Proyek Baru

```text
FRESHTRACK/
│
├── aplikasi/                               # Modul utama aplikasi Android
│   └── sumber/                             # Seluruh berkas kode sumber
│       ├── utama/                          # Sumber daya rilis utama
│       │   ├── aset/                       # Aset web aplikasi
│       │   │   └── web/
│       │   │       ├── halaman_utama.html  # Shell utama antarmuka SPA
│       │   │       │
│       │   │       ├── gaya/               # Seluruh berkas stylesheet (CSS)
│       │   │       │   ├── dasar/
│       │   │       │   ├── komponen/
│       │   │       │   ├── beranda/
│       │   │       │   ├── inventaris/
│       │   │       │   ├── formulir_makanan/
│       │   │       │   ├── detail_makanan/
│       │   │       │   ├── alarm/
│       │   │       │   ├── riwayat/
│       │   │       │   ├── analitik/
│       │   │       │   └── profil/
│       │   │       │
│       │   │       ├── program/            # Seluruh logika JavaScript modular
│       │   │       │   ├── inti/
│       │   │       │   ├── komponen/
│       │   │       │   ├── layanan/
│       │   │       │   ├── beranda/
│       │   │       │   ├── inventaris/
│       │   │       │   ├── formulir_makanan/
│       │   │       │   ├── detail_makanan/
│       │   │       │   ├── alarm/
│       │   │       │   ├── riwayat/
│       │   │       │   ├── analitik/
│       │   │       │   ├── profil/
│       │   │       │   └── bantuan/
│       │   │       │
│       │   │       ├── gambar/             # Aset ilustrasi vektor makanan & logo
│       │   │       └── huruf/              # Berkas tipografi kustom
│       │   │
│       │   ├── java/                       # Backend Java Android Native
│       │   │   └── com/freshtrack/aplikasi/
│       │   │       ├── inti/               # AktivitasUtama.java
│       │   │       ├── data/
│       │   │       │   ├── basis_data/     # PembantuBasisData.java
│       │   │       │   └── penyimpanan/    # PengelolaPenyimpanan.java
│       │   │       ├── penghubung/         # PenghubungAplikasiWeb.java
│       │   │       ├── makanan/            # PengelolaMakanan.java
│       │   │       ├── alarm/              # PembantuAlarm.java & Receiver
│       │   │       ├── notifikasi/         # PembantuNotifikasi.java
│       │   │       ├── gambar/             # PembantuGambar.java
│       │   │       └── pengaturan/         # PengelolaPengaturan.java
│       │   │
│       │   ├── res/                        # Resource Android
│       │   └── AndroidManifest.xml         # Konfigurasi perizinan & komponen
│       │
│       └── pengujian/                      # Berkas pengujian unit
│
├── dokumentasi/                            # Berkas dokumentasi teknis
│   ├── STRUKTUR_PROYEK.md
│   ├── DAFTAR_FITUR.md
│   ├── DOKUMENTASI_DATABASE.md
│   └── PANDUAN_PEMBUATAN_APK.md
│
├── berkas_proyek/                          # Dokumen desain, referensi & cadangan
│   ├── desain/
│   ├── referensi/
│   └── cadangan/
│
├── gradle/                                 # Wrapper biner Gradle
├── build.gradle                            # Skrip build root proyek
├── gradle.properties                       # Konfigurasi JVM Gradle
├── settings.gradle                         # Konfigurasi modul aplikasi
├── gradlew.bat                             # Skrip build Windows CLI
├── .gitignore
└── README.md
```

---

## 3. Menjalankan di Browser (Live Demo)
Aplikasi web lokal FreshTrack sedang aktif di:
👉 **http://localhost:3000**

---

## 4. Membangun APK Melalui Terminal VS Code
1. Bersihkan build terdahulu:
   ```powershell
   .\gradlew.bat clean
   ```
2. Bangun APK debug:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
3. Pasang ke HP Android:
   ```powershell
   adb install -r aplikasi/build/outputs/apk/debug/aplikasi-debug.apk
   ```
