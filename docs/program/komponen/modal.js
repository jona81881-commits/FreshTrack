/**
 * Modal & Bottom Sheet Controller
 */
const ModalController = (function() {
  const overlay = document.getElementById('modal-overlay');
  const sheetContent = document.getElementById('sheet-content');

  function openSheet(htmlContent) {
    if (!overlay || !sheetContent) return;
    sheetContent.innerHTML = htmlContent;
    overlay.classList.add('active');
  }

  function closeSheet() {
    if (!overlay) return;
    overlay.classList.remove('active');
  }

  // Close when clicking outside content
  if (overlay) {
    overlay.addEventListener('click', function(e) {
      if (e.target === overlay) {
        closeSheet();
      }
    });
  }

  return {
    openSheet,
    closeSheet
  };
})();
