/**
 * Add / Edit Food Form Logic
 */
const FoodFormPage = (function() {
  let editMode = false;
  let editId = null;
  let selectedImagePath = "";

  function resetForm() {
    editMode = false;
    editId = null;
    selectedImagePath = "";

    const titleEl = document.getElementById('form-header-title');
    if (titleEl) titleEl.innerText = "Tambah Makanan";

    const submitBtn = document.getElementById('form-submit-btn');
    if (submitBtn) submitBtn.innerText = "Simpan Makanan";

    // Set default dates
    const today = new Date().toISOString().split('T')[0];
    const nextWeek = new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0];

    document.getElementById('input-name').value = "";
    document.getElementById('input-category').value = "Susu";
    document.getElementById('input-quantity').value = "1";
    document.getElementById('input-unit').value = "Kotak";
    document.getElementById('input-purchase-date').value = today;
    document.getElementById('input-expiry-date').value = nextWeek;
    document.getElementById('input-location').value = "Kulkas";
    document.getElementById('input-price').value = "";
    document.getElementById('input-notes').value = "";

    setPackageStatusRadio("Belum Dibuka");
    updatePhotoPreview("");
  }

  async function loadForEdit(id) {
    resetForm();
    editMode = true;
    editId = id;

    const titleEl = document.getElementById('form-header-title');
    if (titleEl) titleEl.innerText = "Ubah Data Makanan";

    const submitBtn = document.getElementById('form-submit-btn');
    if (submitBtn) submitBtn.innerText = "Perbarui Makanan";

    const food = await Database.getFoodById(id);
    if (!food) {
      Database.showToast("Data makanan tidak ditemukan.");
      Navigation.navigate('inventaris');
      return;
    }

    document.getElementById('input-name').value = food.name || "";
    document.getElementById('input-category').value = food.category || "Susu";
    document.getElementById('input-quantity').value = food.quantity || 1;
    document.getElementById('input-unit').value = food.unit || "Kotak";
    document.getElementById('input-purchase-date').value = food.purchase_date || "";
    document.getElementById('input-expiry-date').value = food.expiry_date || "";
    document.getElementById('input-location').value = food.storage_location || "Kulkas";
    document.getElementById('input-price').value = food.price || "";
    document.getElementById('input-notes').value = food.notes || "";

    setPackageStatusRadio(food.package_status || "Belum Dibuka");
    selectedImagePath = food.image_path || "";
    updatePhotoPreview(selectedImagePath);
  }

  function setPackageStatusRadio(val) {
    document.querySelectorAll('.radio-pill').forEach(pill => {
      const input = pill.querySelector('input');
      if (input.value === val) {
        input.checked = true;
        pill.classList.add('selected');
      } else {
        input.checked = false;
        pill.classList.remove('selected');
      }
    });
  }

  function updatePhotoPreview(path) {
    const box = document.getElementById('photo-upload-preview');
    if (!box) return;

    if (path && path.trim() !== "") {
      box.innerHTML = `<img src="${path}" alt="Preview Foto" onerror="FoodFormPage.resetPhotoPrompt()">`;
    } else {
      resetPhotoPrompt();
    }
  }

  function resetPhotoPrompt() {
    const box = document.getElementById('photo-upload-preview');
    if (!box) return;
    box.innerHTML = `
      <div class="upload-prompt">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28">
          <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
          <circle cx="12" cy="13" r="4"/>
        </svg>
        <span>Tambah Foto</span>
      </div>
    `;
  }

  function handleImageSelected(path) {
    selectedImagePath = path;
    updatePhotoPreview(path);
  }

  async function submitForm() {
    const name = document.getElementById('input-name').value.trim();
    const category = document.getElementById('input-category').value;
    const quantity = parseFloat(document.getElementById('input-quantity').value);
    const unit = document.getElementById('input-unit').value;
    const purchaseDate = document.getElementById('input-purchase-date').value;
    const expiryDate = document.getElementById('input-expiry-date').value;
    const location = document.getElementById('input-location').value;
    const price = parseFloat(document.getElementById('input-price').value) || 0;
    const notes = document.getElementById('input-notes').value.trim();

    const pkgRadio = document.querySelector('input[name="package_status"]:checked');
    const packageStatus = pkgRadio ? pkgRadio.value : "Belum Dibuka";

    const formData = {
      name,
      category,
      quantity,
      unit,
      purchase_date: purchaseDate,
      expiry_date: expiryDate,
      storage_location: location,
      package_status: packageStatus,
      price,
      notes,
      image_path: selectedImagePath
    };

    const validation = ValidationHelper.validateFoodForm(formData);
    if (!validation.valid) {
      Database.showToast(validation.message);
      return;
    }

    if (editMode) {
      formData.id = editId;
      const ok = await Database.updateFood(formData);
      if (ok) {
        Database.showToast("Data makanan berhasil diperbarui!");
        Navigation.navigate('detail-makanan', editId);
      } else {
        Database.showToast("Gagal memperbarui data makanan.");
      }
    } else {
      const res = await Database.addFood(formData);
      if (res && res.success) {
        Database.showToast("Makanan baru berhasil ditambahkan!");
        Navigation.navigate('inventaris');
      } else {
        Database.showToast("Gagal menambahkan makanan: " + (res.error || ""));
      }
    }
  }

  return {
    resetForm,
    loadForEdit,
    setPackageStatusRadio,
    updatePhotoPreview,
    resetPhotoPrompt,
    handleImageSelected,
    submitForm
  };
})();

// Global callback for native image picker
window.onImagePicked = function(filePath) {
  FoodFormPage.handleImageSelected(filePath);
};
