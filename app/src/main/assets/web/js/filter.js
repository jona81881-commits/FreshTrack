/**
 * Filter Management Helper
 */
const FilterHelper = (function() {
  const categories = [
    "Semua", "Buah", "Sayuran", "Daging", "Ikan",
    "Susu", "Minuman", "Makanan Siap Saji", "Makanan Kering", "Bumbu", "Lainnya"
  ];

  function renderCategoryChips(containerId, activeCategory, onSelectCallback) {
    const container = document.getElementById(containerId);
    if (!container) return;

    let html = '';
    categories.forEach(cat => {
      const isActive = cat === activeCategory ? 'active' : '';
      html += `<div class="chip ${isActive}" data-category="${cat}">${cat}</div>`;
    });
    container.innerHTML = html;

    container.querySelectorAll('.chip').forEach(el => {
      el.addEventListener('click', () => {
        container.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
        el.classList.add('active');
        if (onSelectCallback) {
          onSelectCallback(el.getAttribute('data-category'));
        }
      });
    });
  }

  return {
    categories,
    renderCategoryChips
  };
})();
