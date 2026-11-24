# Dokumentasi Database SQLite FreshTrack

Aplikasi FreshTrack menggunakan database lokal SQLite murni bernama `freshtrack.db` yang dikelola melalui kelas `PembantuBasisData.java`. Database ini mendukung transaksi aman, query berparameter (*parameterized query*), serta pemaksaan kunci asing (*foreign key constraints*).

---

## 1. Skema Tabel `foods` (Inventaris Makanan)

Menyimpan daftar makanan yang dikelola pengguna.

| Nama Kolom | Tipe Data | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Pengenal unik makanan |
| `name` | TEXT NOT NULL | Nama bahan/produk makanan |
| `category` | TEXT NOT NULL | Kategori (Buah, Sayuran, Susu, Daging, dsb.) |
| `image_path` | TEXT | Path direktori penyimpanan foto |
| `quantity` | REAL NOT NULL | Jumlah persediaan |
| `unit` | TEXT NOT NULL | Satuan (Kotak, Buah, Gram, Bungkus, dsb.) |
| `purchase_date` | TEXT NOT NULL | Tanggal beli format YYYY-MM-DD |
| `expiry_date` | TEXT NOT NULL | Tanggal kedaluwarsa format YYYY-MM-DD |
| `storage_location` | TEXT NOT NULL | Kulkas, Freezer, Suhu Ruang, Lemari |
| `package_status` | TEXT NOT NULL | 'Belum Dibuka' atau 'Sudah Dibuka' |
| `opened_date` | TEXT | Tanggal pertama kali dibuka YYYY-MM-DD |
| `opened_usage_days` | INTEGER DEFAULT 0 | Batas masa simpan produsen (hari) |
| `opened_usage_deadline` | TEXT | Tanggal batas konsumsi setelah dibuka |
| `price` | REAL DEFAULT 0 | Estimasi harga beli |
| `notes` | TEXT | Catatan khusus pengguna |
| `created_at` | TEXT | Waktu pembuatan data YYYY-MM-DD HH:mm:ss |
| `updated_at` | TEXT | Waktu pembaruan data |
| `status` | TEXT DEFAULT 'Aktif' | Status stok ('Aktif', 'Habis', 'Dibuang') |

---

## 2. Skema Tabel `alarms` (Jadwal Pengingat)

Menyimpan jadwal alarm yang terhubung ke item makanan.

| Nama Kolom | Tipe Data | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Pengenal unik jadwal alarm |
| `food_id` | INTEGER NOT NULL | Kunci asing ke `foods(id)` (ON DELETE CASCADE) |
| `reminder_type` | TEXT NOT NULL | Jenis pengingat ('H-7', 'H-3', 'H-1', 'Hari-H', 'Khusus') |
| `reminder_date` | TEXT NOT NULL | Tanggal pemicu format YYYY-MM-DD |
| `reminder_time` | TEXT NOT NULL | Jam pemicu format HH:mm |
| `is_active` | INTEGER DEFAULT 1 | 1 = Aktif, 0 = Nonaktif / Selesai |
| `created_at` | TEXT | Waktu penjadwalan |

---

## 3. Skema Tabel `history` (Riwayat Aktivitas)

Mencatat seluruh rekam jejak konsumsi, masak, atau pembuangan makanan secara permanen.

| Nama Kolom | Tipe Data | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Pengenal unik riwayat |
| `food_id` | INTEGER | ID makanan terkait (tetap tersimpan meski makanan dihapus) |
| `food_name` | TEXT NOT NULL | Nama makanan saat tindakan terjadi |
| `activity_type` | TEXT NOT NULL | 'Dikonsumsi', 'Dimasak', 'Dibuang', 'Dihapus dari Inventaris' |
| `quantity` | REAL NOT NULL | Kuantitas yang diproses |
| `activity_date` | TEXT NOT NULL | Waktu kejadian YYYY-MM-DD HH:mm:ss |
| `notes` | TEXT | Keterangan tambahan |

---

## 4. Skema Tabel `settings` (Pengaturan Pengguna)

Menyimpan preferensi konfigurasi aplikasi.

| Nama Kolom | Tipe Data | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | ID pengaturan |
| `setting_key` | TEXT UNIQUE NOT NULL | Kunci identifikasi (cth: user_name, default_alarm_time) |
| `setting_value` | TEXT | Nilai pengaturan tersimpan |
