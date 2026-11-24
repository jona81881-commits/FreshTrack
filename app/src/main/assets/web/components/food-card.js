/**
 * Food Card Component
 * Menghasilkan elemen kartu makanan sesuai desain referensi FreshTrack
 */
function createFoodCardHTML(food) {
  const badgeInfo = FoodUtils.getPriorityBadgeInfo(food.days_remaining !== undefined ? food.days_remaining : 999);
  const formattedDate = FoodUtils.formatDateID(food.expiry_date);
  const imageSrc = food.image_path ? food.image_path : 'images/default_food.svg';

  return `
    <div class="food-card" onclick="Navigation.goToDetail(${food.id})">
      <div class="food-card-thumb">
        <img src="${imageSrc}" alt="${food.name}" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'64\\' height=\\'64\\' viewBox=\\'0 0 24 24\\' fill=\\'none\\' stroke=\\'%232C5EAD\\' stroke-width=\\'2\\'><rect width=\\'18\\' height=\\'18\\' x=\\'3\\' y=\\'3\\' rx=\\'2\\'/><circle cx=\\'8.5\\' cy=\\'8.5\\' r=\\'1.5\\'/><path d=\\'m21 15-5-5L5 21\\'/></svg>'">
      </div>
      <div class="food-card-body">
        <div class="food-card-title">${food.name}</div>
        <div class="food-card-qty">${food.quantity} ${food.unit}</div>
        <div class="food-card-meta">
          <span>Kedaluwarsa: ${formattedDate}</span>
          <span>Di ${food.storage_location}</span>
        </div>
      </div>
      <div class="food-card-right">
        <span class="badge ${badgeInfo.className}">${badgeInfo.badge}</span>
      </div>
    </div>
  `;
}
