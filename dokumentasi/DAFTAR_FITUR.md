# Daftar Fitur Lengkap FreshTrack

Dokumen ini memuat seluruh fitur fungsional yang telah diimplementasikan pada aplikasi **FreshTrack**.

---

## 1. Antarmuka Pengguna & Navigasi
- **Layar Selamat Datang (Welcome):** Layar pembuka berlogo resmi dengan opsi "Mulai Sekarang" dan "Masuk".
- **Layar Masuk (Login):** Form autentikasi minimalis dengan opsi "Masuk sebagai Tamu" untuk demo instan.
- **Bilah Navigasi Bawah (Bottom Navigation):** Menggunakan 5 menu utama tetap:
  1. Beranda
  2. Inventaris
  3. Alarm
  4. Riwayat
  5. Profil
- **Penanganan Tombol Back Fisik Android:** Terintegrasi dengan history stack internal aplikasi via `OnBackPressedCallback`.

## 2. Beranda (Dashboard)
- Header biru melengkung proporsional dengan sapaan nama pengguna, waktu, dan tanggal dinamis.
- 4 Kartu Ringkasan Metrik Inventaris:
  - Total Makanan
  - Belum Dibuka
  - Sudah Dibuka
  - Perlu Segera Digunakan (jatuh tempo $\le$ 3 hari)
- Bagian Prioritas Konsumsi dengan urutan otomatis makanan terdekat kedaluwarsa.
- Kartu Pengingat Terdekat dan tombol pintasan "+ Tambah Makanan".
- Pintasan langsung menuju halaman Statistik & Analitik.

## 3. Inventaris Makanan
- Pencarian nama makanan secara langsung (real-time) dengan teknik *debounce*.
- Penyaringan kategori menggunakan chip horizontal (*Semua, Buah, Sayuran, Daging, Ikan, Susu, Minuman, Makanan Siap Saji, Makanan Kering, Bumbu, Lainnya*).
- Pengurutan inventaris berdasarkan:
  - Terdekat Kedaluwarsa
  - Terbaru Ditambahkan
  - Nama Makanan (A-Z)
  - Prioritas Konsumsi
- Tombol mengambang (Floating Action Button / FAB) untuk menambah makanan baru.

## 4. Tambah & Ubah Makanan
- Pemilihan foto galeri perangkat menggunakan `ActivityResultLauncher` Android Native dan disimpan permanen di direktori internal aplikasi.
- Kolom isian lengkap: Nama, Kategori, Jumlah, Satuan, Tanggal Pembelian, Tanggal Kedaluwarsa, Lokasi Penyimpanan, Status Kemasan, Harga, dan Catatan.
- Validasi menyeluruh: tanggal kedaluwarsa tidak boleh mendahului tanggal pembelian, jumlah harus positif.
- Mode ubah (edit) yang langsung memuat data lama secara otomatis.

## 5. Detail Makanan & Pelacakan Tanggal Dibuka
- Tampilan foto hero besar dan kartu informasi 4 kolom (Pembelian, Kedaluwarsa, Penyimpanan, Status Kemasan).
- Kartu hitung mundur sisa hari (*countdown*).
- **Pelacakan Tanggal Pertama Kali Dibuka:**
  - Pengguna dapat mencatat tanggal membuka kemasan dan batas lama konsumsi dari produsen.
  - Sistem otomatis menghitung batas kedaluwarsa baru dan mengambil tanggal terawal antara kedaluwarsa kemasan dan batas pemakaian.
- Tombol tindakan langsung di bagian bawah:
  - **Dikonsumsi:** Mengurangi kuantitas stok dan mencatat riwayat konsumsi.
  - **Dimasak:** Mengurangi kuantitas stok dan mencatat riwayat masak.
  - **Ubah:** Membuka formulir pembaruan data.
  - **Hapus:** Memindahkan data dari inventaris aktif ke log riwayat.

## 6. Pengingat & Alarm Native
- Tab tersegmentasi: *Aktif* dan *Selesai*.
- Kartu alarm interaktif dengan tombol saklar (toggle switch) untuk mengaktifkan/menonaktifkan.
- Integrasi Android Native `AlarmManager.setExactAndAllowWhileIdle()` dengan `PendingIntent`.
- Auto-Restore: Seluruh jadwal alarm otomatis dipulihkan kembali saat perangkat menyala ulang (*reboot*) melalui `PenerimaBoot`.
- Pengaturan preset pengingat: H-7, H-3, H-1, Hari-H, dan Pengingat Khusus.

## 7. Statistik & Analitik
- Filter rentang waktu: *7 Hari*, *30 Hari*, dan *1 Tahun*.
- Grafik lingkaran donat SVG interaktif menampilkan persentase penggunaan optimal.
- Kartu estimasi nominal uang yang berhasil dihemat (berdasarkan harga makanan yang dikonsumsi sebelum terbuang).
- Indikator kategori bahan makanan yang paling sering terbuang beserta grafik batang mini.
- Banner motivasi pencapaian pengurangan pemborosan pangan.

## 8. Riwayat Aktivitas & Profil
- Log pencatatan aktivitas terpisah untuk kategori: *Dikonsumsi, Dimasak, Dibuang, Dihapus dari Inventaris*.
- Pencarian dan filter riwayat aktivitas.
- Pengaturan profil: Waktu alarm default, saklar suara notifikasi, saklar getaran, modal kebijakan privasi, dan panduan bantuan.
