/**
 * Komponen Bilah Navigasi Bawah (Bottom Navigation Bar)
 * 5 Menu Utama: Beranda, Alarm, Catat Makanan, Statistik, Profil
 */
const Navbar = (function() {
  const tabs = [
    {
      id: "beranda",
      label: "Beranda",
      icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`
    },
    {
      id: "alarm",
      label: "Alarm",
      icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>`
    },
    {
      id: "tambah-makanan",
      label: "Catat Makanan",
      icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>`
    },
    {
      id: "analitik",
      label: "Statistik",
      icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>`
    },
    {
      id: "profil",
      label: "Profil",
      icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`
    }
  ];

  function render(activeTabId = "beranda") {
    const navEl = document.getElementById('bottom-nav-bar');
    if (!navEl) return;

    let html = '';
    tabs.forEach(tab => {
      const isActive = tab.id === activeTabId ? 'active' : '';
      html += `
        <div class="nav-item ${isActive}" data-tab="${tab.id}" onclick="Navigation.navigate('${tab.id}')">
          <div class="nav-icon-wrapper">${tab.icon}</div>
          <span>${tab.label}</span>
        </div>
      `;
    });
    navEl.innerHTML = html;
  }

  function setActive(tabId) {
    const items = document.querySelectorAll('.bottom-nav .nav-item');
    items.forEach(item => {
      if (item.getAttribute('data-tab') === tabId) {
        item.classList.add('active');
      } else {
        item.classList.remove('active');
      }
    });
  }

  return {
    render,
    setActive
  };
})();
