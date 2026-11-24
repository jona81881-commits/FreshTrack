# Dokumentasi Struktur Proyek FreshTrack

Dokumen ini menjelaskan struktur direktori resmi proyek **FreshTrack** berbasis Bahasa Indonesia sesuai dengan standar arsitektur Android WebView + Java Native + SQLite.

---

## Hirarki Direktori Proyek

```text
FRESHTRACK/
│
├── aplikasi/                               # Modul utama aplikasi Android
│   └── sumber/                             # Seluruh berkas kode sumber
│       ├── utama/                          # Sumber daya rilis utama
│       │   ├── aset/                       # Aset web aplikasi
│       │   │   └── web/
│       │   │       ├── halaman_utama.html  # Shell utama aplikasi SPA
│       │   │       │
│       │   │       ├── gaya/               # Seluruh berkas stylesheet (CSS)
│       │   │       │   ├── dasar/          # Variabel, reset, & responsivitas
│       │   │       │   ├── komponen/       # Tombol, badge, modal, kartu, tata letak
│       │   │       │   ├── beranda/        # Gaya visual beranda & 4 metrik
│       │   │       │   ├── inventaris/     # Gaya visual pencarian & chip kategori
│       │   │       │   ├── formulir_makanan/ # Input foto, form, radio status
│       │   │       │   ├── detail_makanan/ # Banner hero, countdown, action dock
│       │   │       │   ├── alarm/          # Kartu pengingat & toggle switch
│       │   │       │   ├── riwayat/        # Log riwayat & ikon aksi
│       │   │       │   ├── analitik/       # SVG Donut chart & grafik batang
│       │   │       │   └── profil/         # Pengaturan suara, getaran, avatar
│       │   │       │
│       │   │       ├── program/            # Seluruh logika JavaScript modular
│       │   │       │   ├── inti/           # Mesin routing navigasi & inisialisasi
│       │   │       │   ├── komponen/       # Bilah navigasi, kartu, & modal
│       │   │       │   ├── layanan/        # Jembatan SQLite native & kalkulasi
│       │   │       │   ├── beranda/        # Kontroler dashboard
│       │   │       │   ├── inventaris/     # Kontroler pencarian & filter inventaris
│       │   │       │   ├── formulir_makanan/ # Kontroler form input & edit
│       │   │       │   ├── detail_makanan/ # Kontroler detail & tanggal dibuka
│       │   │       │   ├── alarm/          # Kontroler aktivasi & preset alarm
│       │   │       │   ├── riwayat/        # Kontroler log aktivitas
│       │   │       │   ├── analitik/       # Kontroler kalkulasi persentase & chart
│       │   │       │   ├── profil/         # Kontroler pengaturan pengguna
│       │   │       │   └── bantuan/        # Panduan & kebijakan privasi
│       │   │       │
│       │   │       ├── gambar/             # Aset ilustrasi vektor makanan & logo
│       │   │       └── huruf/              # Tempat berkas tipografi kustom
│       │   │
│       │   ├── java/                       # Lapisan backend Java Android Native
│       │   │   └── com/freshtrack/aplikasi/
│       │   │       ├── inti/               # AktivitasUtama.java (WebView & runtime)
│       │   │       ├── data/
│       │   │       │   ├── basis_data/     # PembantuBasisData.java (SQLite OpenHelper)
│       │   │       │   └── penyimpanan/    # PengelolaPenyimpanan.java (SharedPrefs)
│       │   │       ├── penghubung/         # PenghubungAplikasiWeb.java (@JavascriptInterface)
│       │   │       ├── makanan/            # PengelolaMakanan.java (Bisnis inventaris)
│       │   │       ├── alarm/              # PembantuAlarm.java & Receiver
│       │   │       ├── notifikasi/         # PembantuNotifikasi.java (Channel & Dialog)
│       │   │       ├── gambar/             # PembantuGambar.java (Penyalin foto internal)
│       │   │       └── pengaturan/         # PengelolaPengaturan.java
│       │   │
│       │   ├── res/                        # Sumber daya Android (icons, strings, colors)
│       │   └── AndroidManifest.xml         # Konfigurasi perizinan & komponen Android
│       │
│       └── pengujian/                      # Berkas pengujian unit & instrumentasi
│
├── dokumentasi/                            # Berkas dokumentasi teknis & panduan
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
