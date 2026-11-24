/**
 * Bottom Navbar Component
 */
const Navbar = (function() {
  const tabs = [
    { id: "beranda", label: "Beranda", icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>` },
    { id: "inventaris", label: "Inventaris", icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>` },
    { id: "alarm", label: "Alarm", icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2"/><path d="M5 3L2 6"/><path d="M22 6l-3-3"/><path d="M6.38 18.7 4 21"/><path d="M17.64 18.67 20 21"/></svg>` },
    { id: "riwayat", label: "Riwayat", icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>` },
    { id: "profil", label: "Profil", icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>` }
  ];

  function render(activeTabId = "beranda") {
    const navEl = document.getElementById('bottom-nav-bar');
    if (!navEl) return;

    let html = '';
    tabs.forEach(tab => {
      const isActive = tab.id === activeTabId ? 'active' : '';
      html += `
        <div class="nav-item ${isActive}" data-tab="${tab.id}" onclick="Navigation.navigate('${tab.id}')">
          ${tab.icon}
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
