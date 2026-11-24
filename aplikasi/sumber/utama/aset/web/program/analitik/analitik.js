/**
 * Analytics (Statistik & Analitik) Page Logic
 */
const AnalyticsPage = (function() {
  let currentTimeRange = "30 Hari";

  async function loadAnalytics() {
    try {
      const data = await Database.getAnalytics(currentTimeRange);
      renderAnalytics(data);
    } catch (e) {
      console.error("Error loading analytics:", e);
    }
  }

  function renderAnalytics(data) {
    const optimalPct = data.optimal_percentage !== undefined ? data.optimal_percentage : 85;
    const consumedCount = data.total_consumed !== undefined ? data.total_consumed : 17;
    const wastedCount = data.total_wasted !== undefined ? data.total_wasted : 3;
    const savedMoney = data.estimated_saved !== undefined ? data.estimated_saved : 45000;
    const topCat = data.top_wasted_category || "Sayuran";
    const topCatPct = data.top_wasted_percentage !== undefined ? data.top_wasted_percentage : 40;

    // Update Donut Chart
    const circle = document.getElementById('donut-circle-progress');
    const percentEl = document.getElementById('donut-percent-text');
    if (circle && percentEl) {
      const radius = 60;
      const circumference = 2 * Math.PI * radius; // ~376.99
      circle.style.strokeDasharray = `${circumference} ${circumference}`;
      const offset = circumference - (optimalPct / 100) * circumference;
      circle.style.strokeDashoffset = offset;
      percentEl.innerText = `${Math.round(optimalPct)}%`;
    }

    // Update Legend Counts
    const legendConsumed = document.getElementById('legend-consumed-count');
    const legendWasted = document.getElementById('legend-wasted-count');
    if (legendConsumed) legendConsumed.innerText = consumedCount;
    if (legendWasted) legendWasted.innerText = wastedCount;

    // Update Savings
    const savingsEl = document.getElementById('analytics-savings-amount');
    if (savingsEl) {
      savingsEl.innerText = FoodUtils.formatCurrency(savedMoney);
    }

    // Update Top Wasted Category
    const topCatEl = document.getElementById('analytics-top-cat-name');
    const topCatPctEl = document.getElementById('analytics-top-cat-pct');
    if (topCatEl) topCatEl.innerText = topCat;
    if (topCatPctEl) topCatPctEl.innerText = `(${topCatPct}%)`;
  }

  function setTimeRange(range) {
    currentTimeRange = range;
    document.querySelectorAll('.time-filter-btn').forEach(btn => {
      if (btn.getAttribute('data-range') === range) {
        btn.classList.add('active');
        btn.style.backgroundColor = 'var(--primary-color)';
        btn.style.color = 'var(--white)';
      } else {
        btn.classList.remove('active');
        btn.style.backgroundColor = 'var(--white)';
        btn.style.color = 'var(--text-secondary)';
      }
    });
    loadAnalytics();
  }

  return {
    loadAnalytics,
    setTimeRange
  };
})();
