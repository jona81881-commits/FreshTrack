/**
 * Inventory Page Logic
 */
const InventoryPage = (function() {
  let currentCategory = "Semua";
  let currentSearch = "";
  let currentSort = "Terdekat Kedaluwarsa";

  async function loadInventory() {
    const listContainer = document.getElementById('inventory-list');
    if (!listContainer) return;

    listContainer.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--text-secondary);">Memuat inventaris...</div>';

    try {
      const foods = await Database.getFoods(currentCategory, currentSearch, currentSort);
      if (!foods || foods.length === 0) {
        listContainer.innerHTML = `
          <div class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
              <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
            </svg>
            <div style="font-weight: 600; margin-bottom: 4px;">Belum Ada Makanan</div>
            <div style="font-size: 12px; color: var(--text-secondary);">Tambahkan makanan baru untuk mulai memantau tanggal kedaluwarsanya.</div>
          </div>
        `;
        return;
      }

      let html = '';
      foods.forEach(food => {
        html += createFoodCardHTML(food);
      });
      listContainer.innerHTML = html;
    } catch (e) {
      listContainer.innerHTML = '<div style="color: red; text-align: center; padding: 20px;">Gagal memuat inventaris</div>';
    }
  }

  function init() {
    // Setup chips
    FilterHelper.renderCategoryChips('inventory-category-chips', currentCategory, (cat) => {
      currentCategory = cat;
      loadInventory();
    });

    // Setup search
    const searchInput = document.getElementById('inventory-search-input');
    if (searchInput) {
      searchInput.addEventListener('input', SearchHelper.debounce((e) => {
        currentSearch = e.target.value;
        loadInventory();
      }, 300));
    }

    // Setup sort selector
    const sortSelect = document.getElementById('inventory-sort-select');
    if (sortSelect) {
      sortSelect.addEventListener('change', (e) => {
        currentSort = e.target.value;
        loadInventory();
      });
    }

    loadInventory();
  }

  return {
    init,
    reload: loadInventory
  };
})();
