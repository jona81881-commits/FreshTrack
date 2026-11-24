/**
 * Form Validation Helper
 */
const ValidationHelper = {
  validateFoodForm(formData) {
    if (!formData.name || formData.name.trim() === "") {
      return { valid: false, message: "Nama makanan wajib diisi!" };
    }
    if (!formData.category || formData.category.trim() === "") {
      return { valid: false, message: "Kategori wajib dipilih!" };
    }
    if (!formData.quantity || isNaN(formData.quantity) || Number(formData.quantity) <= 0) {
      return { valid: false, message: "Jumlah makanan harus lebih dari 0!" };
    }
    if (!formData.unit || formData.unit.trim() === "") {
      return { valid: false, message: "Satuan makanan wajib dipilih!" };
    }
    if (!formData.purchase_date) {
      return { valid: false, message: "Tanggal pembelian wajib diisi!" };
    }
    if (!formData.expiry_date) {
      return { valid: false, message: "Tanggal kedaluwarsa wajib diisi!" };
    }
    if (new Date(formData.expiry_date) < new Date(formData.purchase_date)) {
      return { valid: false, message: "Tanggal kedaluwarsa tidak boleh sebelum tanggal pembelian!" };
    }
    if (formData.price && Number(formData.price) < 0) {
      return { valid: false, message: "Harga makanan tidak boleh negatif!" };
    }
    return { valid: true };
  }
};
