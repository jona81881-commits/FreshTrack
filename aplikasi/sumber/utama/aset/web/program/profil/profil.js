/**
 * Profile and Settings Page Logic
 */
const ProfilePage = (function() {
  async function loadProfile() {
    try {
      const settings = await Database.getSettings();

      const nameEl = document.getElementById('profile-user-name');
      const emailEl = document.getElementById('profile-user-email');
      const alarmTimeEl = document.getElementById('profile-alarm-time');
      const soundSwitch = document.getElementById('profile-switch-sound');
      const vibrationSwitch = document.getElementById('profile-switch-vibration');

      if (nameEl) nameEl.innerText = settings.user_name || "Budi Santoso";
      if (emailEl) emailEl.innerText = settings.user_email || "budi.santoso@email.com";
      if (alarmTimeEl) alarmTimeEl.value = settings.default_alarm_time || "20:00";
      if (soundSwitch) soundSwitch.checked = settings.sound_enabled !== false;
      if (vibrationSwitch) vibrationSwitch.checked = settings.vibration_enabled !== false;
    } catch (e) {
      console.error("Error loading profile settings", e);
    }
  }

  async function updateSetting(key, val) {
    await Database.saveSetting(key, val);
    Database.showToast("Pengaturan berhasil disimpan.");
  }

  function showAboutModal() {
    const content = `
      <div class="sheet-handle"></div>
      <div style="text-align: center; margin-bottom: 16px;">
        <div style="font-size: 20px; font-weight: 800; color: var(--primary-color);">FreshTrack</div>
        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 4px;">Versi 1.0.0 (Build Native SQLite)</div>
      </div>
      <div style="font-size: 13px; color: var(--text-primary); line-height: 1.6; margin-bottom: 20px;">
        FreshTrack adalah aplikasi cerdas pengelolaan persediaan dan pengingat kedaluwarsa makanan.
        Dirancang khusus untuk meminimalkan pemborosan pangan rumah tangga, melacak masa simpan setelah kemasan dibuka,
        serta memberikan notifikasi terjadwal yang tepat waktu.
      </div>
      <button class="btn btn-primary btn-block" onclick="ModalController.closeSheet()">Tutup</button>
    `;
    ModalController.openSheet(content);
  }

  function showPrivacyModal() {
    const content = `
      <div class="sheet-handle"></div>
      <div style="font-size: 18px; font-weight: 700; margin-bottom: 12px;">Kebijakan Privasi</div>
      <div style="font-size: 13px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 20px;">
        Aplikasi FreshTrack menghargai privasi Anda sepenuhnya. Seluruh data inventaris, gambar makanan,
        alarm, dan riwayat aktivitas disimpan secara lokal di perangkat Anda menggunakan SQLite dan tidak dikirimkan ke server eksternal mana pun.
      </div>
      <button class="btn btn-primary btn-block" onclick="ModalController.closeSheet()">Tutup</button>
    `;
    ModalController.openSheet(content);
  }

  function showHelpModal() {
    const content = `
      <div class="sheet-handle"></div>
      <div style="font-size: 18px; font-weight: 700; margin-bottom: 12px;">Bantuan & Panduan</div>
      <div style="font-size: 13px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 20px;">
        <b>1. Menambah Makanan:</b> Tekan tombol '+' di inventaris atau beranda.<br>
        <b>2. Tanggal Dibuka:</b> Tekan detail makanan dan pilih 'Tandai Sudah Dibuka' untuk menghitung batas aman pemakaian.<br>
        <b>3. Alarm & Pengingat:</b> Atur alarm pada tab Alarm agar notifikasi muncul sebelum kedaluwarsa.
      </div>
      <button class="btn btn-primary btn-block" onclick="ModalController.closeSheet()">Mengerti</button>
    `;
    ModalController.openSheet(content);
  }

  function logout() {
    Database.showToast("Anda telah keluar ke halaman selamat datang.");
    Navigation.navigate('welcome');
  }

  return {
    loadProfile,
    updateSetting,
    showAboutModal,
    showPrivacyModal,
    showHelpModal,
    logout
  };
})();
