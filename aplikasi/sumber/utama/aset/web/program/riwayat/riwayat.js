/**
 * History (Riwayat Aktivitas) Page Logic
 */
const HistoryPage = (function() {
  let currentFilter = "Semua";
  let currentSearch = "";

  async function loadHistory() {
    const container = document.getElementById('history-list-container');
    if (!container) return;

    container.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--text-secondary);">Memuat riwayat...</div>';

    try {
      const items = await Database.getHistory(currentFilter, currentSearch);
      if (!items || items.length === 0) {
        container.innerHTML = `
          <div class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            <div style="font-weight: 600; font-size: 14px; margin-bottom: 4px;">Belum Ada Riwayat</div>
            <div style="font-size: 12px; color: var(--text-secondary);">Aktivitas konsumsi, masak, atau pembuangan makanan akan tercatat di sini.</div>
          </div>
        `;
        return;
      }

      let html = '';
      items.forEach(item => {
        let iconClass = 'dikonsumsi';
        let actionTagColor = 'var(--status-green)';
        let iconSvg = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M20 6L9 17l-5-5"/></svg>';

        if (item.activity_type === "Dimasak") {
          iconClass = 'dimasak';
          actionTagColor = 'var(--primary-color)';
          iconSvg = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M12 2v4M8 4l2 2M16 4l-2 2M3 10h18M5 10v7a3 3 0 0 0 3 3h8a3 3 0 0 0 3-3v-7"/></svg>';
        } else if (item.activity_type === "Dibuang") {
          iconClass = 'dibuang';
          actionTagColor = 'var(--status-red)';
          iconSvg = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>';
        } else if (item.activity_type === "Dihapus dari Inventaris") {
          iconClass = 'dihapus';
          actionTagColor = 'var(--text-secondary)';
          iconSvg = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>';
        }

        html += `
          <div class="history-card">
            <div class="history-icon-box ${iconClass}">
              ${iconSvg}
            </div>
            <div class="history-body">
              <div class="history-header-line">
                <span class="history-food-title">${item.food_name}</span>
                <span class="history-action-tag" style="color: ${actionTagColor}">${item.activity_type}</span>
              </div>
              <div class="history-qty-date">
                <span>Jumlah: ${item.quantity}</span> • <span>${item.activity_date}</span>
              </div>
              ${item.notes ? `<div class="history-notes">${item.notes}</div>` : ''}
            </div>
          </div>
        `;
      });
      container.innerHTML = html;
    } catch (e) {
      container.innerHTML = '<div style="color: red; text-align: center; padding: 20px;">Gagal memuat riwayat</div>';
    }
  }

  function init() {
    // History filter chips
    const filterContainer = document.getElementById('history-filter-chips');
    if (filterContainer) {
      const filters = ["Semua", "Dikonsumsi", "Dimasak", "Dibuang"];
      let html = '';
      filters.forEach(f => {
        const isActive = f === currentFilter ? 'active' : '';
        html += `<div class="chip ${isActive}" data-filter="${f}">${f}</div>`;
      });
      filterContainer.innerHTML = html;

      filterContainer.querySelectorAll('.chip').forEach(el => {
        el.addEventListener('click', () => {
          filterContainer.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
          el.classList.add('active');
          currentFilter = el.getAttribute('data-filter');
          loadHistory();
        });
      });
    }

    // Search bar
    const searchInput = document.getElementById('history-search-input');
    if (searchInput) {
      searchInput.addEventListener('input', SearchHelper.debounce((e) => {
        currentSearch = e.target.value;
        loadHistory();
      }, 300));
    }

    loadHistory();
  }

  return {
    init,
    loadHistory
  };
})();
