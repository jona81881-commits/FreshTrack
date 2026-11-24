/**
 * FreshTrack Food Utilities & Helpers
 */
const FoodUtils = (function() {
  const MONTHS_ID = [
    "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
    "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
  ];

  function formatDateID(dateString) {
    if (!dateString) return "-";
    try {
      const parts = dateString.split("-");
      if (parts.length === 3) {
        const day = parseInt(parts[2], 10);
        const monthIndex = parseInt(parts[1], 10) - 1;
        const year = parts[0];
        return `${day} ${MONTHS_ID[monthIndex] || parts[1]} ${year}`;
      }
      return dateString;
    } catch (e) {
      return dateString;
    }
  }

  function formatCurrency(amount) {
    if (isNaN(amount) || amount === null) return "Rp 0";
    return "Rp " + Number(amount).toLocaleString("id-ID");
  }

  function getPriorityBadgeInfo(daysRemaining) {
    if (daysRemaining <= 0) {
      return {
        level: "Kritis",
        badge: daysRemaining < 0 ? "Kedaluwarsa" : "Hari Ini!",
        className: "badge-kritis"
      };
    } else if (daysRemaining === 1) {
      return {
        level: "Tinggi",
        badge: "Prioritas",
        className: "badge-priority"
      };
    } else if (daysRemaining <= 3) {
      return {
        level: "Sedang",
        badge: "Perhatian",
        className: "badge-perhatian"
      };
    } else {
      return {
        level: "Normal",
        badge: "Aman",
        className: "badge-aman"
      };
    }
  }

  function getCategoryIcon(category) {
    // Returns SVG icon string for categories
    switch (category) {
      case "Susu":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 2h8v4H8zM7 6h10l1 16H6L7 6zM7 12h10"/></svg>`;
      case "Buah":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="14" r="8"/><path d="M12 6V2c2 1 4 2 5 2"/></svg>`;
      case "Sayuran":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2.5 16.5l8-8M12 4l-4 4M14 6l6-2-2 6-4 4"/></svg>`;
      case "Daging":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="8" cy="12" r="5"/><circle cx="16" cy="12" r="5"/></svg>`;
      case "Ikan":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 12c3-4 8-6 13-3 2 1 4 1 7-1-1 3-1 5 1 7-3-2-5-2-7-1-5 3-10 1-13-2z"/></svg>`;
      case "Makanan Siap Saji":
      case "Makanan Kering":
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="16" rx="3"/></svg>`;
      default:
        return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>`;
    }
  }

  return {
    formatDateID,
    formatCurrency,
    getPriorityBadgeInfo,
    getCategoryIcon
  };
})();
