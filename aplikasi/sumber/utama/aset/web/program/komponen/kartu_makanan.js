/**
 * Komponen Kartu Makanan (Food Card)
 * Sesuai Desain Modern FreshTrack:
 * - Thumbnail dengan latar belakang pastel lembut
 * - Judul makanan tebal
 * - 3 Baris Metadata dengan Ikon (Jumlah/Kemasan, Tanggal Kedaluwarsa, Lokasi Penyimpanan)
 * - Badge Prioritas (Prioritas, Perhatian, Aman) dengan Ikon Bulat
 * - Panah Chevron Kanan (>)
 */
function createFoodCardHTML(food) {
  const formattedDate = FoodUtils.formatDateID(food.expiry_date);
  let imageSrc = food.image_path ? food.image_path : 'gambar/default_food.svg';
  if (imageSrc.startsWith('images/')) {
    imageSrc = imageSrc.replace('images/', 'gambar/');
  }

  // Tentukan warna latar belakang thumbnail berdasarkan kategori / nama makanan
  let thumbBgClass = 'thumb-bg-blue';
  const cat = (food.category || '').toLowerCase();
  const name = (food.name || '').toLowerCase();

  if (cat.includes('susu') || name.includes('susu') || name.includes('keju')) {
    thumbBgClass = 'thumb-bg-blue';
  } else if (cat.includes('roti') || name.includes('roti') || cat.includes('kering')) {
    thumbBgClass = 'thumb-bg-orange';
  } else if (name.includes('telur') || cat.includes('sayur') || cat.includes('buah')) {
    thumbBgClass = 'thumb-bg-green';
  } else if (cat.includes('daging') || cat.includes('ikan')) {
    thumbBgClass = 'thumb-bg-pink';
  }

  // Tentukan badge prioritas dan ikonnya
  const days = food.days_remaining !== undefined ? food.days_remaining : 999;
  let badgeClass = 'badge-aman';
  let badgeText = 'Aman';
  let badgeIcon = `
    <svg viewBox="0 0 24 24" fill="#16A34A" width="11" height="11">
      <circle cx="12" cy="12" r="10"/>
      <polyline points="8 12 11 15 16 9" stroke="#FFFFFF" stroke-width="2.5" fill="none"/>
    </svg>
  `;

  if (days <= 1 || food.priority_badge === 'Prioritas' || food.priority_level === 'Tinggi') {
    badgeClass = 'badge-prioritas';
    badgeText = 'Prioritas';
    badgeIcon = `
      <svg viewBox="0 0 24 24" fill="#DC2626" width="11" height="11">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="8" x2="12" y2="12" stroke="#FFFFFF" stroke-width="2.5"/>
        <circle cx="12" cy="16" r="1.2" fill="#FFFFFF"/>
      </svg>
    `;
  } else if (days <= 3 || food.priority_badge === 'Perhatian' || food.priority_level === 'Sedang') {
    badgeClass = 'badge-perhatian';
    badgeText = 'Perhatian';
    badgeIcon = `
      <svg viewBox="0 0 24 24" fill="#D97706" width="11" height="11">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="8" x2="12" y2="12" stroke="#FFFFFF" stroke-width="2.5"/>
        <circle cx="12" cy="16" r="1.2" fill="#FFFFFF"/>
      </svg>
    `;
  }

  return `
    <div class="food-card" onclick="Navigation.goToDetail(${food.id})">
      <div class="food-card-thumb ${thumbBgClass}">
        <img src="${imageSrc}" alt="${food.name}" onerror="this.src='gambar/default_food.svg'">
      </div>
      <div class="food-card-body">
        <div class="food-card-title">${food.name}</div>
        <div class="food-meta-row">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" y1="22.08" x2="12" y2="12"/>
          </svg>
          <span>${food.quantity} ${food.unit}</span>
        </div>
        <div class="food-meta-row">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
          <span>Kedaluwarsa: ${formattedDate}</span>
        </div>
        <div class="food-meta-row">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
            <circle cx="12" cy="10" r="3"/>
          </svg>
          <span>Di ${food.storage_location}</span>
        </div>
      </div>
      <div class="food-card-right">
        <span class="badge ${badgeClass}">
          ${badgeIcon}
          <span>${badgeText}</span>
        </span>
        <svg class="card-chevron" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="2.5" width="15" height="15">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </div>
    </div>
  `;
}
