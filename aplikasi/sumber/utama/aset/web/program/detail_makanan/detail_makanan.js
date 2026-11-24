/**
 * Food Detail Page Logic
 */
const FoodDetailPage = (function() {
  let currentFood = null;

  async function loadDetail(foodId) {
    const detailContainer = document.getElementById('detail-page-content');
    if (!detailContainer) return;

    detailContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--text-secondary);">Memuat detail makanan...</div>';

    try {
      const food = await Database.getFoodById(foodId);
      if (!food) {
        detailContainer.innerHTML = '<div style="text-align: center; padding: 40px;">Makanan tidak ditemukan.</div>';
        return;
      }

      currentFood = food;
      renderDetail(food);
    } catch (e) {
      detailContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: red;">Gagal memuat detail makanan.</div>';
    }
  }

  function renderDetail(food) {
    const detailContainer = document.getElementById('detail-page-content');
    if (!detailContainer) return;

    const badgeInfo = FoodUtils.getPriorityBadgeInfo(food.days_remaining !== undefined ? food.days_remaining : 999);
    const purchaseDateStr = FoodUtils.formatDateID(food.purchase_date);
    const expiryDateStr = FoodUtils.formatDateID(food.expiry_date);
    const imageSrc = food.image_path ? food.image_path : 'images/default_food.svg';

    let remainingDaysText = food.days_remaining !== undefined ? (food.days_remaining <= 0 ? (food.days_remaining < 0 ? "Sudah Kedaluwarsa" : "Hari Ini!") : `${food.days_remaining} Hari`) : "-";

    let openedTrackingHTML = '';
    if (food.package_status === "Sudah Dibuka") {
      const openedDateFormatted = FoodUtils.formatDateID(food.opened_date);
      const deadlineFormatted = FoodUtils.formatDateID(food.opened_usage_deadline);
      openedTrackingHTML = `
        <div class="opened-tracking-section">
          <div class="opened-tracking-title">Pelacakan Setelah Dibuka</div>
          <div class="opened-tracking-status" style="color: var(--primary-color); font-weight: 600;">
            Dibuka pada: ${openedDateFormatted} (Batas ${food.opened_usage_days} hari)<br>
            Batas Konsumsi: ${deadlineFormatted}
          </div>
          <button class="btn btn-secondary btn-block" onclick="FoodDetailPage.openMarkOpenedModal()">Perbarui Info Pembukaan</button>
        </div>
      `;
    } else {
      openedTrackingHTML = `
        <div class="opened-tracking-section">
          <div class="opened-tracking-title">Pelacakan Setelah Dibuka</div>
          <div class="opened-tracking-status">Belum dicatat (Kemasan masih utuh)</div>
          <button class="btn btn-primary btn-block" onclick="FoodDetailPage.openMarkOpenedModal()">Tandai Sudah Dibuka</button>
        </div>
      `;
    }

    detailContainer.innerHTML = `
      <div class="detail-hero-banner">
        <img class="detail-hero-img" src="${imageSrc}" alt="${food.name}" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'100%\\' height=\\'100%\\' viewBox=\\'0 0 24 24\\' fill=\\'%23C4E2F5\\'><rect width=\\'24\\' height=\\'24\\'/></svg>'">
        <div class="detail-top-actions">
          <div class="detail-circle-btn" onclick="Navigation.back()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="m15 18-6-6 6-6"/></svg>
          </div>
          <div class="detail-circle-btn" onclick="FoodDetailPage.editFood()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
          </div>
        </div>
      </div>

      <div class="detail-content-sheet">
        <div class="detail-header-row">
          <div>
            <div class="detail-main-title">${food.name}</div>
            <div class="detail-category-subtitle">
              <span>${food.category}</span> • <span>${food.quantity} ${food.unit}</span>
            </div>
          </div>
          <span class="badge ${badgeInfo.className}" style="font-size: 12px; padding: 6px 12px;">${badgeInfo.badge}</span>
        </div>

        <div class="detail-info-grid">
          <div class="detail-info-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            <div>
              <div class="detail-item-label">Pembelian</div>
              <div class="detail-item-val">${purchaseDateStr}</div>
            </div>
          </div>
          <div class="detail-info-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 14 14"/></svg>
            <div>
              <div class="detail-item-label">Kedaluwarsa</div>
              <div class="detail-item-val">${expiryDateStr}</div>
            </div>
          </div>
          <div class="detail-info-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
            <div>
              <div class="detail-item-label">Penyimpanan</div>
              <div class="detail-item-val">${food.storage_location}</div>
            </div>
          </div>
          <div class="detail-info-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
            <div>
              <div class="detail-item-label">Status</div>
              <div class="detail-item-val">${food.package_status}</div>
            </div>
          </div>
        </div>

        <div class="remaining-time-card">
          <div class="remaining-icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div>
            <div class="remaining-label">Sisa Waktu Konsumsi Optimal</div>
            <div class="remaining-value">${remainingDaysText}</div>
          </div>
        </div>

        ${openedTrackingHTML}

        ${food.notes ? `<div style="background: var(--bg-secondary); border-radius: var(--radius-md); padding: 12px 14px; margin-bottom: 24px; font-size: 13px; color: var(--text-secondary);"><b style="color: var(--text-primary);">Catatan:</b> ${food.notes}</div>` : ''}
      </div>

      <div class="detail-action-bar">
        <button class="detail-action-btn" onclick="FoodDetailPage.promptActivity('Dikonsumsi')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
          <span>Dikonsumsi</span>
        </button>
        <button class="detail-action-btn" onclick="FoodDetailPage.promptActivity('Dimasak')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4M8 4l2 2M16 4l-2 2M3 10h18M5 10v7a3 3 0 0 0 3 3h8a3 3 0 0 0 3-3v-7"/></svg>
          <span>Dimasak</span>
        </button>
        <button class="detail-action-btn" onclick="FoodDetailPage.editFood()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
          <span>Ubah</span>
        </button>
        <button class="detail-action-btn delete" onclick="FoodDetailPage.confirmDelete()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          <span>Hapus</span>
        </button>
      </div>
    `;
  }

  function openMarkOpenedModal() {
    if (!currentFood) return;
    const today = new Date().toISOString().split('T')[0];

    const content = `
      <div class="sheet-handle"></div>
      <div style="font-size: 16px; font-weight: 700; margin-bottom: 6px;">Pelacakan Tanggal Pertama Kali Dibuka</div>
      <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 16px;">
        Catat tanggal membuka kemasan dan batas penggunaan dari produsen untuk pengingat yang akurat.
      </div>
      <div class="form-group">
        <label class="form-label">Tanggal Dibuka</label>
        <input type="date" id="sheet-open-date" class="form-input" value="${today}">
      </div>
      <div class="form-group">
        <label class="form-label">Batas Masa Simpan Setelah Dibuka (Hari)</label>
        <input type="number" id="sheet-usage-days" class="form-input" value="3" min="1" max="180">
        <div style="font-size: 11px; color: var(--text-secondary); margin-top: 4px;">Contoh: Susu segar umumnya 3-4 hari setelah dibuka.</div>
      </div>
      <div style="display: flex; gap: 10px; margin-top: 20px;">
        <button class="btn btn-outline" style="flex: 1;" onclick="ModalController.closeSheet()">Batal</button>
        <button class="btn btn-primary" style="flex: 2;" onclick="FoodDetailPage.saveMarkOpened()">Simpan Pelacakan</button>
      </div>
    `;
    ModalController.openSheet(content);
  }

  async function saveMarkOpened() {
    const openDate = document.getElementById('sheet-open-date').value;
    const usageDays = parseInt(document.getElementById('sheet-usage-days').value, 10);

    if (!openDate || isNaN(usageDays) || usageDays <= 0) {
      Database.showToast("Tanggal dan batas hari harus valid!");
      return;
    }

    const success = await Database.markAsOpened(currentFood.id, openDate, usageDays);
    if (success) {
      Database.showToast("Berhasil mencatat status kemasan dibuka!");
      ModalController.closeSheet();
      loadDetail(currentFood.id);
    } else {
      Database.showToast("Gagal memperbarui status pembukaan.");
    }
  }

  function promptActivity(activityType) {
    if (!currentFood) return;

    const content = `
      <div class="sheet-handle"></div>
      <div style="font-size: 16px; font-weight: 700; margin-bottom: 6px;">Tandai Makanan: ${activityType}</div>
      <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 16px;">
        Stok makanan akan diperbarui dan aktivitas ini akan dicatat ke riwayat.
      </div>
      <div class="form-group">
        <label class="form-label">Jumlah ${activityType} (${currentFood.unit})</label>
        <input type="number" id="activity-qty" class="form-input" value="${currentFood.quantity}" min="0.1" max="${currentFood.quantity}" step="any">
      </div>
      <div class="form-group">
        <label class="form-label">Keterangan / Catatan (Opsional)</label>
        <input type="text" id="activity-notes" class="form-input" placeholder="Contoh: Diminum pagi hari">
      </div>
      <div style="display: flex; gap: 10px; margin-top: 20px;">
        <button class="btn btn-outline" style="flex: 1;" onclick="ModalController.closeSheet()">Batal</button>
        <button class="btn btn-primary" style="flex: 2;" onclick="FoodDetailPage.submitActivity('${activityType}')">Konfirmasi</button>
      </div>
    `;
    ModalController.openSheet(content);
  }

  async function submitActivity(activityType) {
    const qtyInput = document.getElementById('activity-qty');
    const notesInput = document.getElementById('activity-notes');
    const qty = parseFloat(qtyInput.value);
    const notes = notesInput ? notesInput.value : '';

    if (isNaN(qty) || qty <= 0) {
      Database.showToast("Jumlah tidak valid!");
      return;
    }

    const success = await Database.recordActivity(currentFood.id, currentFood.name, activityType, qty, notes);
    if (success) {
      Database.showToast(`Berhasil menandai makanan ${activityType}!`);
      ModalController.closeSheet();
      Navigation.navigate('inventaris');
    } else {
      Database.showToast("Gagal mencatat aktivitas.");
    }
  }

  function editFood() {
    if (currentFood) {
      Navigation.navigate('tambah-makanan', currentFood.id);
    }
  }

  function confirmDelete() {
    if (!currentFood) return;
    const content = `
      <div class="sheet-handle"></div>
      <div style="font-size: 16px; font-weight: 700; color: var(--status-red); margin-bottom: 6px;">Hapus Makanan?</div>
      <div style="font-size: 13px; color: var(--text-secondary); margin-bottom: 20px;">
        Apakah Anda yakin ingin menghapus <b>${currentFood.name}</b> dari inventaris? Data ini akan dipindahkan ke riwayat.
      </div>
      <div style="display: flex; gap: 10px;">
        <button class="btn btn-outline" style="flex: 1;" onclick="ModalController.closeSheet()">Batal</button>
        <button class="btn btn-danger" style="flex: 1;" onclick="FoodDetailPage.executeDelete()">Ya, Hapus</button>
      </div>
    `;
    ModalController.openSheet(content);
  }

  async function executeDelete() {
    const success = await Database.deleteFood(currentFood.id);
    if (success) {
      Database.showToast("Makanan berhasil dihapus.");
      ModalController.closeSheet();
      Navigation.navigate('inventaris');
    } else {
      Database.showToast("Gagal menghapus makanan.");
    }
  }

  return {
    loadDetail,
    openMarkOpenedModal,
    saveMarkOpened,
    promptActivity,
    submitActivity,
    editFood,
    confirmDelete,
    executeDelete
  };
})();
