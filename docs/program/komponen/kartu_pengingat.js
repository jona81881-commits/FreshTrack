/**
 * Notification / Alarm Card Component
 */
function createAlarmCardHTML(alarm) {
  const formattedDate = FoodUtils.formatDateID(alarm.reminder_date);
  const imageSrc = alarm.image_path ? alarm.image_path : 'images/default_food.svg';

  return `
    <div class="alarm-item-card" id="alarm-item-${alarm.id}">
      <div class="alarm-thumb">
        <img src="${imageSrc}" alt="${alarm.food_name}" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'48\\' height=\\'48\\' viewBox=\\'0 0 24 24\\' fill=\\'none\\' stroke=\\'%232C5EAD\\' stroke-width=\\'2\\'><path d=\\'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9\\'/><path d=\\'M13.73 21a2 2 0 0 1-3.46 0\\'/></svg>'">
      </div>
      <div class="alarm-info">
        <div class="alarm-food-name">${alarm.food_name}</div>
        <div class="alarm-datetime">${formattedDate} • ${alarm.reminder_time}</div>
        <div class="alarm-type-text">${alarm.reminder_type} sebelum kedaluwarsa</div>
      </div>
      <label class="switch">
        <input type="checkbox" ${alarm.is_active ? 'checked' : ''} onchange="AlarmPage.toggleAlarm(${alarm.id}, this.checked)">
        <span class="slider"></span>
      </label>
    </div>
  `;
}
