/**
 * Alarm & Reminder Page Logic
 */
const AlarmPage = (function() {
  let activeTab = "aktif"; // 'aktif' or 'selesai'
  let cachedAlarms = [];

  async function loadAlarms() {
    const listContainer = document.getElementById('alarm-list-container');
    if (!listContainer) return;

    listContainer.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--text-secondary);">Memuat alarm...</div>';

    try {
      cachedAlarms = await Database.getAlarms();
      renderAlarms();
    } catch (e) {
      listContainer.innerHTML = '<div style="text-align: center; padding: 20px; color: red;">Gagal memuat alarm.</div>';
    }
  }

  function renderAlarms() {
    const listContainer = document.getElementById('alarm-list-container');
    if (!listContainer) return;

    const filtered = cachedAlarms.filter(a => activeTab === "aktif" ? a.is_active : !a.is_active);

    // Update tab counts
    const activeCount = cachedAlarms.filter(a => a.is_active).length;
    const finishedCount = cachedAlarms.filter(a => !a.is_active).length;

    const tabAktif = document.getElementById('tab-alarm-aktif');
    const tabSelesai = document.getElementById('tab-alarm-selesai');
    if (tabAktif) tabAktif.innerText = `Aktif (${activeCount})`;
    if (tabSelesai) tabSelesai.innerText = `Selesai (${finishedCount})`;

    // Update next upcoming alarm banner
    const nextAlarm = cachedAlarms.find(a => a.is_active);
    const nextBanner = document.getElementById('alarm-next-banner-content');
    if (nextBanner) {
      if (nextAlarm) {
        nextBanner.innerHTML = `
          <div style="font-size: 11px; color: var(--primary-color); font-weight: 600; text-transform: uppercase;">Pengingat Berikutnya</div>
          <div style="font-size: 16px; font-weight: 800; color: var(--text-primary); margin: 2px 0;">Hari ini, ${nextAlarm.reminder_time}</div>
          <div style="font-size: 13px; color: var(--text-secondary); font-weight: 500;">${nextAlarm.food_name}</div>
        `;
      } else {
        nextBanner.innerHTML = `
          <div style="font-size: 14px; font-weight: 600; color: var(--text-primary);">Tidak ada alarm aktif</div>
          <div style="font-size: 12px; color: var(--text-secondary);">Semua pengingat telah selesai atau dimatikan.</div>
        `;
      }
    }

    if (!filtered || filtered.length === 0) {
      listContainer.innerHTML = `
        <div class="empty-state" style="padding: 24px 0;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="13" r="8"/>
            <path d="M12 9v4l2 2"/>
            <path d="M5 3L2 6"/>
            <path d="M22 6l-3-3"/>
          </svg>
          <div style="font-weight: 600; font-size: 14px; margin-bottom: 4px;">Tidak Ada Pengingat</div>
          <div style="font-size: 12px; color: var(--text-secondary);">Tidak ada pengingat dalam kategori ini.</div>
        </div>
      `;
      return;
    }

    let html = '';
    filtered.forEach(alarm => {
      html += createAlarmCardHTML(alarm);
    });
    listContainer.innerHTML = html;
  }

  function setTab(tabName) {
    activeTab = tabName;
    document.querySelectorAll('.segmented-tabs .segment-tab').forEach(tab => {
      if (tab.getAttribute('data-tab') === tabName) {
        tab.classList.add('active');
      } else {
        tab.classList.remove('active');
      }
    });
    renderAlarms();
  }

  async function toggleAlarm(alarmId, isActive) {
    const success = await Database.toggleAlarm(alarmId, isActive);
    if (success) {
      Database.showToast(isActive ? "Pengingat diaktifkan" : "Pengingat dinonaktifkan");
      // Update local state
      const a = cachedAlarms.find(item => item.id === alarmId);
      if (a) a.is_active = isActive;
      renderAlarms();
    } else {
      Database.showToast("Gagal mengubah status pengingat.");
      loadAlarms();
    }
  }

  function saveReminderRules() {
    const selectedRule = document.querySelector('input[name="reminder_preset"]:checked');
    const ruleVal = selectedRule ? selectedRule.value : "H-3";
    Database.saveSetting("default_reminder_rule", ruleVal);
    Database.showToast("Pengaturan pengingat berhasil disimpan!");
  }

  return {
    loadAlarms,
    setTab,
    toggleAlarm,
    saveReminderRules
  };
})();
