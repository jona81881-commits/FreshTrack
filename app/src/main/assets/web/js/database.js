/**
 * FreshTrack Database and Native Bridge Wrapper
 * Mengelola komunikasi antara JavaScript dengan Java Android SQLite (via window.AndroidBridge)
 * serta menyediakan fallback otomatis saat diuji di browser/VS Code.
 */
const Database = (function() {
  const isAndroid = typeof window.AndroidBridge !== 'undefined';

  // In-memory / LocalStorage fallback untuk pengujian browser
  function getLocalData(key, defaultVal) {
    try {
      const data = localStorage.getItem('freshtrack_' + key);
      return data ? JSON.parse(data) : defaultVal;
    } catch (e) {
      return defaultVal;
    }
  }

  function setLocalData(key, val) {
    try {
      localStorage.setItem('freshtrack_' + key, JSON.stringify(val));
    } catch (e) {}
  }

  // Inisialisasi data demonstrasi lokal jika belum ada di browser
  if (!isAndroid && !localStorage.getItem('freshtrack_foods')) {
    const today = new Date().toISOString().split('T')[0];
    const initialFoods = [
      { id: 1, name: "Susu UHT", category: "Susu", image_path: "images/susu.jpg", quantity: 1, unit: "Kotak", purchase_date: today, expiry_date: new Date(Date.now() + 86400000).toISOString().split('T')[0], storage_location: "Kulkas", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 18000, notes: "Susu rendah lemak", status: "Aktif", days_remaining: 1, priority_level: "Tinggi", priority_badge: "Prioritas" },
      { id: 2, name: "Roti Tawar", category: "Makanan Siap Saji", image_path: "images/roti.jpg", quantity: 1, unit: "Bungkus", purchase_date: today, expiry_date: new Date(Date.now() + 3*86400000).toISOString().split('T')[0], storage_location: "Suhu Ruang", package_status: "Sudah Dibuka", opened_date: today, opened_usage_days: 3, opened_usage_deadline: new Date(Date.now() + 3*86400000).toISOString().split('T')[0], price: 15000, notes: "Simpan rapat", status: "Aktif", days_remaining: 3, priority_level: "Sedang", priority_badge: "Perhatian" },
      { id: 3, name: "Telur", category: "Daging", image_path: "images/telur.jpg", quantity: 10, unit: "Butir", purchase_date: today, expiry_date: new Date(Date.now() + 7*86400000).toISOString().split('T')[0], storage_location: "Kulkas", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 25000, notes: "Telur ayam segar", status: "Aktif", days_remaining: 7, priority_level: "Normal", priority_badge: "Aman" },
      { id: 4, name: "Apel Fuji", category: "Buah", image_path: "images/apel.jpg", quantity: 3, unit: "Buah", purchase_date: today, expiry_date: new Date(Date.now() + 8*86400000).toISOString().split('T')[0], storage_location: "Suhu Ruang", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 22000, notes: "Apel manis", status: "Aktif", days_remaining: 8, priority_level: "Normal", priority_badge: "Aman" },
      { id: 5, name: "Daging Ayam Filet", category: "Daging", image_path: "images/ayam.jpg", quantity: 500, unit: "Gram", purchase_date: today, expiry_date: new Date(Date.now() + 2*86400000).toISOString().split('T')[0], storage_location: "Kulkas", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 32000, notes: "Untuk sop", status: "Aktif", days_remaining: 2, priority_level: "Sedang", priority_badge: "Perhatian" },
      { id: 6, name: "Wortel Organik", category: "Sayuran", image_path: "images/wortel.jpg", quantity: 4, unit: "Buah", purchase_date: today, expiry_date: new Date(Date.now() + 10*86400000).toISOString().split('T')[0], storage_location: "Kulkas", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 12000, notes: "Wortel segar", status: "Aktif", days_remaining: 10, priority_level: "Normal", priority_badge: "Aman" },
      { id: 7, name: "Keju Cheddar", category: "Susu", image_path: "images/keju.jpg", quantity: 1, unit: "Kotak", purchase_date: today, expiry_date: new Date(Date.now() + 5*86400000).toISOString().split('T')[0], storage_location: "Kulkas", package_status: "Sudah Dibuka", opened_date: today, opened_usage_days: 5, opened_usage_deadline: new Date(Date.now() + 5*86400000).toISOString().split('T')[0], price: 24000, notes: "Tutup rapat", status: "Aktif", days_remaining: 5, priority_level: "Normal", priority_badge: "Aman" },
      { id: 8, name: "Ikan Salmon", category: "Ikan", image_path: "images/ikan.jpg", quantity: 2, unit: "Porsi", purchase_date: today, expiry_date: new Date(Date.now() + 30*86400000).toISOString().split('T')[0], storage_location: "Freezer", package_status: "Belum Dibuka", opened_date: null, opened_usage_days: 0, opened_usage_deadline: null, price: 65000, notes: "Ikan beku", status: "Aktif", days_remaining: 30, priority_level: "Normal", priority_badge: "Aman" }
    ];
    setLocalData('foods', initialFoods);

    const initialAlarms = [
      { id: 1, food_id: 1, food_name: "Susu UHT", image_path: "images/susu.jpg", reminder_type: "H-1", reminder_date: today, reminder_time: "20:00", is_active: true, expiry_date: initialFoods[0].expiry_date },
      { id: 2, food_id: 2, food_name: "Roti Tawar", image_path: "images/roti.jpg", reminder_type: "H-3", reminder_date: today, reminder_time: "08:00", is_active: true, expiry_date: initialFoods[1].expiry_date }
    ];
    setLocalData('alarms', initialAlarms);

    const initialHistory = [
      { id: 1, food_id: 1, food_name: "Susu UHT", activity_type: "Dikonsumsi", quantity: 0.5, activity_date: today + " 08:30:00", notes: "Diminum saat sarapan" },
      { id: 2, food_id: 2, food_name: "Roti Tawar", activity_type: "Dimasak", quantity: 2, activity_date: today + " 07:15:00", notes: "Roti bakar keju" },
      { id: 3, food_id: 0, food_name: "Bayam Hijau", activity_type: "Dibuang", quantity: 1, activity_date: today + " 10:00:00", notes: "Layu di kulkas" }
    ];
    setLocalData('history', initialHistory);
  }

  return {
    isNative: isAndroid,

    async getDashboardSummary() {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getDashboardSummary();
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getDashboardSummary error", e);
        }
      }
      // Fallback
      const foods = getLocalData('foods', []).filter(f => f.status === 'Aktif');
      const unopened = foods.filter(f => f.package_status === 'Belum Dibuka').length;
      const opened = foods.filter(f => f.package_status === 'Sudah Dibuka').length;
      const urgent = foods.filter(f => f.days_remaining <= 3).length;
      const alarms = getLocalData('alarms', []);
      const nextAlarm = alarms.find(a => a.is_active) || null;

      return {
        total_food: foods.length,
        unopened: unopened,
        opened: opened,
        urgent: urgent,
        priority_foods: foods.slice(0, 5),
        next_alarm: nextAlarm
      };
    },

    async getFoods(category = "Semua", searchQuery = "", sortBy = "Terdekat Kedaluwarsa") {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getFoods(category, searchQuery, sortBy);
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getFoods error", e);
        }
      }
      // Fallback
      let list = getLocalData('foods', []).filter(f => f.status === 'Aktif');
      if (category && category !== "Semua") {
        list = list.filter(f => f.category === category);
      }
      if (searchQuery && searchQuery.trim() !== "") {
        const q = searchQuery.toLowerCase();
        list = list.filter(f => f.name.toLowerCase().includes(q));
      }
      if (sortBy === "Nama Makanan") {
        list.sort((a, b) => a.name.localeCompare(b.name));
      } else if (sortBy === "Terbaru Ditambahkan") {
        list.sort((a, b) => b.id - a.id);
      } else {
        list.sort((a, b) => (a.days_remaining || 0) - (b.days_remaining || 0));
      }
      return list;
    },

    async getFoodById(id) {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getFoodById(Number(id));
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getFoodById error", e);
        }
      }
      const foods = getLocalData('foods', []);
      return foods.find(f => f.id === Number(id)) || null;
    },

    async addFood(foodData) {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.addFood(JSON.stringify(foodData));
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge addFood error", e);
          return { success: false, error: e.message };
        }
      }
      const foods = getLocalData('foods', []);
      const newId = foods.length > 0 ? Math.max(...foods.map(f => f.id)) + 1 : 1;
      const newFood = { ...foodData, id: newId, status: "Aktif" };
      foods.unshift(newFood);
      setLocalData('foods', foods);
      return { success: true, id: newId };
    },

    async updateFood(foodData) {
      if (isAndroid) {
        try {
          return window.AndroidBridge.updateFood(JSON.stringify(foodData));
        } catch (e) {
          console.error("Native bridge updateFood error", e);
          return false;
        }
      }
      const foods = getLocalData('foods', []);
      const idx = foods.findIndex(f => f.id === Number(foodData.id));
      if (idx !== -1) {
        foods[idx] = { ...foods[idx], ...foodData };
        setLocalData('foods', foods);
        return true;
      }
      return false;
    },

    async deleteFood(id) {
      if (isAndroid) {
        try {
          return window.AndroidBridge.deleteFood(Number(id));
        } catch (e) {
          console.error("Native bridge deleteFood error", e);
          return false;
        }
      }
      let foods = getLocalData('foods', []);
      foods = foods.filter(f => f.id !== Number(id));
      setLocalData('foods', foods);
      return true;
    },

    async markAsOpened(id, openedDate, usageDays) {
      if (isAndroid) {
        try {
          return window.AndroidBridge.markAsOpened(Number(id), openedDate, Number(usageDays));
        } catch (e) {
          console.error("Native bridge markAsOpened error", e);
          return false;
        }
      }
      const foods = getLocalData('foods', []);
      const food = foods.find(f => f.id === Number(id));
      if (food) {
        food.package_status = "Sudah Dibuka";
        food.opened_date = openedDate;
        food.opened_usage_days = Number(usageDays);
        const d = new Date(openedDate);
        d.setDate(d.getDate() + Number(usageDays));
        food.opened_usage_deadline = d.toISOString().split('T')[0];
        setLocalData('foods', foods);
        return true;
      }
      return false;
    },

    async recordActivity(foodId, foodName, activityType, quantity, notes = "") {
      if (isAndroid) {
        try {
          return window.AndroidBridge.recordActivity(Number(foodId), foodName, activityType, Number(quantity), notes);
        } catch (e) {
          console.error("Native bridge recordActivity error", e);
          return false;
        }
      }
      const history = getLocalData('history', []);
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
      history.unshift({
        id: history.length + 1,
        food_id: Number(foodId),
        food_name: foodName,
        activity_type: activityType,
        quantity: Number(quantity),
        activity_date: now,
        notes: notes
      });
      setLocalData('history', history);

      // Kurangi stok jika dikonsumsi/dimasak/dibuang
      const foods = getLocalData('foods', []);
      const food = foods.find(f => f.id === Number(foodId));
      if (food) {
        food.quantity = Math.max(0, food.quantity - Number(quantity));
        if (food.quantity <= 0) {
          food.status = activityType === "Dibuang" ? "Dibuang" : "Habis";
        }
        setLocalData('foods', foods);
      }
      return true;
    },

    async getHistory(filterType = "Semua", searchQuery = "") {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getHistory(filterType, searchQuery);
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getHistory error", e);
        }
      }
      let list = getLocalData('history', []);
      if (filterType && filterType !== "Semua") {
        list = list.filter(h => h.activity_type === filterType);
      }
      if (searchQuery && searchQuery.trim() !== "") {
        const q = searchQuery.toLowerCase();
        list = list.filter(h => h.food_name.toLowerCase().includes(q));
      }
      return list;
    },

    async getAlarms() {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getAlarms();
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getAlarms error", e);
        }
      }
      return getLocalData('alarms', []);
    },

    async toggleAlarm(alarmId, isActive) {
      if (isAndroid) {
        try {
          return window.AndroidBridge.toggleAlarm(Number(alarmId), Boolean(isActive));
        } catch (e) {
          console.error("Native bridge toggleAlarm error", e);
          return false;
        }
      }
      const alarms = getLocalData('alarms', []);
      const a = alarms.find(item => item.id === Number(alarmId));
      if (a) {
        a.is_active = Boolean(isActive);
        setLocalData('alarms', alarms);
        return true;
      }
      return false;
    },

    async getAnalytics(timeRange = "30 Hari") {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getAnalytics(timeRange);
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getAnalytics error", e);
        }
      }
      const hist = getLocalData('history', []);
      const consumed = hist.filter(h => h.activity_type === 'Dikonsumsi' || h.activity_type === 'Dimasak').length;
      const wasted = hist.filter(h => h.activity_type === 'Dibuang').length;
      const total = consumed + wasted;
      const wastePct = total > 0 ? Math.round((wasted / total) * 100) : 0;
      const optimalPct = total > 0 ? Math.round((consumed / total) * 100) : 100;

      return {
        total_consumed: consumed,
        total_wasted: wasted,
        waste_percentage: wastePct,
        optimal_percentage: optimalPct,
        estimated_saved: 45000,
        top_wasted_category: "Sayuran",
        top_wasted_percentage: 40
      };
    },

    async getSettings() {
      if (isAndroid) {
        try {
          const res = window.AndroidBridge.getSettings();
          return JSON.parse(res);
        } catch (e) {
          console.error("Native bridge getSettings error", e);
        }
      }
      return {
        user_name: "Budi Santoso",
        user_email: "budi.santoso@email.com",
        default_alarm_time: "20:00",
        sound_enabled: true,
        vibration_enabled: true,
        theme: "light",
        language: "id"
      };
    },

    async saveSetting(key, val) {
      if (isAndroid) {
        try {
          return window.AndroidBridge.saveSetting(key, String(val));
        } catch (e) {
          return false;
        }
      }
      const s = await this.getSettings();
      s[key] = val;
      setLocalData('settings', s);
      return true;
    },

    pickImage() {
      if (isAndroid) {
        window.AndroidBridge.pickImage();
      } else {
        // Fallback untuk browser testing: input file buatan
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.onchange = e => {
          const file = e.target.files[0];
          if (file) {
            const reader = new FileReader();
            reader.onload = ev => {
              if (window.onImagePicked) {
                window.onImagePicked(ev.target.result);
              }
            };
            reader.readAsDataURL(file);
          }
        };
        input.click();
      }
    },

    showToast(message) {
      if (isAndroid) {
        window.AndroidBridge.showToast(message);
      }
      // Also show in HTML toast
      const container = document.getElementById('toast-container');
      if (container) {
        const toast = document.createElement('div');
        toast.className = 'toast';
        toast.innerText = message;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 2800);
      }
    }
  };
})();
