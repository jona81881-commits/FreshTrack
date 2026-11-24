/**
 * Navigation and Routing Engine
 */
const Navigation = (function() {
  let currentPage = "beranda";
  const historyStack = [];

  const bottomNavPages = ["beranda", "inventaris", "alarm", "riwayat", "profil", "analitik"];

  function navigate(pageName, param = null, pushToHistory = true) {
    if (pushToHistory && currentPage && currentPage !== pageName) {
      historyStack.push({ page: currentPage, param: null });
    }

    currentPage = pageName;

    // Hide all pages
    document.querySelectorAll('.page-view').forEach(view => {
      view.classList.remove('active');
    });

    // Show target page
    const targetEl = document.getElementById(`page-${pageName}`);
    if (targetEl) {
      targetEl.classList.add('active');
      window.scrollTo(0, 0);
    }

    // Toggle bottom navbar visibility
    const bottomNav = document.getElementById('bottom-nav-bar');
    if (bottomNav) {
      if (bottomNavPages.includes(pageName)) {
        bottomNav.style.display = 'flex';
        Navbar.setActive(pageName === 'analitik' ? 'beranda' : pageName);
      } else {
        bottomNav.style.display = 'none';
      }
    }

    // Initialize or refresh page logic
    onPageActivated(pageName, param);
  }

  function onPageActivated(pageName, param) {
    switch (pageName) {
      case "beranda":
        App.loadDashboard();
        break;
      case "inventaris":
        InventoryPage.init();
        break;
      case "tambah-makanan":
        if (param) {
          FoodFormPage.loadForEdit(param);
        } else {
          FoodFormPage.resetForm();
        }
        break;
      case "detail-makanan":
        if (param) {
          FoodDetailPage.loadDetail(param);
        }
        break;
      case "alarm":
        AlarmPage.loadAlarms();
        break;
      case "riwayat":
        HistoryPage.init();
        break;
      case "analitik":
        AnalyticsPage.loadAnalytics();
        break;
      case "profil":
        ProfilePage.loadProfile();
        break;
    }
  }

  function back() {
    if (historyStack.length > 0) {
      const prev = historyStack.pop();
      navigate(prev.page, prev.param, false);
      return true;
    } else {
      if (currentPage !== "beranda") {
        navigate("beranda", null, false);
        return true;
      }
      return false; // Exit app
    }
  }

  function goToDetail(foodId) {
    navigate("detail-makanan", foodId);
  }

  return {
    navigate,
    back,
    goToDetail,
    getCurrentPage: () => currentPage
  };
})();

// Hook for Android Native integration
window.navigateTo = function(page, param) {
  Navigation.navigate(page, param);
};

window.handleAndroidBack = function() {
  return Navigation.back();
};
