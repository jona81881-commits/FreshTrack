/**
 * FreshTrack Main App Initialization
 */
const App = (function() {
  async function init() {
    console.log("FreshTrack Application Initializing...");

    // Render Bottom Navbar
    Navbar.render("beranda");

    // Format current date on Beranda header
    updateDateDisplay();

    // Check if user has already seen welcome screen
    const isFirstTime = localStorage.getItem('freshtrack_visited');
    if (!isFirstTime) {
      Navigation.navigate("welcome");
    } else {
      Navigation.navigate("beranda");
    }

    // Bind event listeners
    bindEvents();
  }

  function updateDateDisplay() {
    const dateEl = document.getElementById('dashboard-date-str');
    if (dateEl) {
      const now = new Date();
      const days = ["Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"];
      const months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];
      const dayName = days[now.getDay()];
      const dayNum = now.getDate();
      const monthName = months[now.getMonth()];
      const year = now.getFullYear();
      dateEl.innerText = `${dayName}, ${dayNum} ${monthName} ${year}`;
    }
  }

  async function loadDashboard() {
    try {
      const summary = await Database.getDashboardSummary();

      // Update 4 Metric counts
      document.getElementById('metric-total').innerText = summary.total_food || 0;
      document.getElementById('metric-unopened').innerText = summary.unopened || 0;
      document.getElementById('metric-opened').innerText = summary.opened || 0;
      document.getElementById('metric-urgent').innerText = summary.urgent || 0;

      // Update Priority List
      const priorityList = document.getElementById('dashboard-priority-list');
      if (priorityList) {
        if (!summary.priority_foods || summary.priority_foods.length === 0) {
          priorityList.innerHTML = `
            <div style="background: var(--white); border-radius: var(--radius-md); padding: 16px; text-align: center; color: var(--text-secondary); font-size: 13px; border: 1px solid var(--border-color);">
              Semua persediaan dalam kondisi aman.
            </div>
          `;
        } else {
          let html = '';
          summary.priority_foods.forEach(food => {
            html += createFoodCardHTML(food);
          });
          priorityList.innerHTML = html;
        }
      }

      // Update Next Upcoming Reminder
      const nextReminderEl = document.getElementById('dashboard-next-reminder');
      if (nextReminderEl) {
        if (summary.next_alarm) {
          nextReminderEl.innerHTML = `
            <div class="reminder-banner" onclick="Navigation.navigate('alarm')">
              <div class="reminder-banner-left">
                <div class="reminder-clock-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div>
                  <div class="reminder-title">${summary.next_alarm.food_name}</div>
                  <div class="reminder-subtitle">Hari ini • ${summary.next_alarm.reminder_time}</div>
                </div>
              </div>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18" color="var(--text-muted)"><path d="m9 18 6-6-6-6"/></svg>
            </div>
          `;
        } else {
          nextReminderEl.innerHTML = `
            <div class="reminder-banner" onclick="Navigation.navigate('alarm')">
              <div class="reminder-banner-left">
                <div class="reminder-clock-icon" style="background: #EDF2F7; color: var(--text-secondary);">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div>
                  <div class="reminder-title">Tidak ada alarm terdekat</div>
                  <div class="reminder-subtitle">Atur alarm untuk menerima pengingat</div>
                </div>
              </div>
            </div>
          `;
        }
      }

    } catch (e) {
      console.error("Error loading dashboard summary:", e);
    }
  }

  function bindEvents() {
    // Welcome screen Mulai Sekarang button
    const btnStart = document.getElementById('btn-welcome-start');
    if (btnStart) {
      btnStart.addEventListener('click', () => {
        localStorage.setItem('freshtrack_visited', 'true');
        Navigation.navigate("login");
      });
    }

    // Welcome screen Masuk button
    const btnWelcomeLogin = document.getElementById('btn-welcome-login');
    if (btnWelcomeLogin) {
      btnWelcomeLogin.addEventListener('click', () => {
        localStorage.setItem('freshtrack_visited', 'true');
        Navigation.navigate("login");
      });
    }

    // Login screen buttons
    const btnLogin = document.getElementById('btn-login-submit');
    if (btnLogin) {
      btnLogin.addEventListener('click', () => {
        Database.showToast("Berhasil masuk!");
        Navigation.navigate("beranda");
      });
    }

    const btnGuest = document.getElementById('btn-login-guest');
    if (btnGuest) {
      btnGuest.addEventListener('click', () => {
        Database.showToast("Masuk sebagai Tamu.");
        Navigation.navigate("beranda");
      });
    }

    // Package status radio buttons in form
    document.querySelectorAll('.radio-pill').forEach(pill => {
      pill.addEventListener('click', () => {
        const radio = pill.querySelector('input');
        if (radio) {
          FoodFormPage.setPackageStatusRadio(radio.value);
        }
      });
    });
  }

  return {
    init,
    loadDashboard
  };
})();

// Start application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  App.init();
});
