/**
 * Logika Bantuan & Panduan
 */
const BantuanPage = (function() {
  function showPanduan() {
    ProfilePage.showHelpModal();
  }

  function showKebijakanPrivasi() {
    ProfilePage.showPrivacyModal();
  }

  function showTentang() {
    ProfilePage.showAboutModal();
  }

  return {
    showPanduan,
    showKebijakanPrivasi,
    showTentang
  };
})();
