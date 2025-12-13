document.addEventListener("DOMContentLoaded", () => {

  const token = localStorage.getItem("sha_token");
  if (!token) {
    location.href = "login.html";
    return;
  }

  // Correct HTML IDs
  const mealType = document.getElementById("mealType");
  const caloriesEl = document.getElementById("calorieCount");
  const proteinEl = document.getElementById("protein");
  const carbsEl = document.getElementById("carbs");
  const fatsEl = document.getElementById("fats");
  const dateEl = document.getElementById("mealDate");
  const saveBtn = document.getElementById("saveMeal");
  const list = document.getElementById("todayMeals");

  // Default date
  if (!dateEl.value) {
    dateEl.value = new Date().toISOString().slice(0, 10);
  }

  // Load today's meals
  async function loadToday() {
    try {
      const res = await fetch("/smart-health-track/meals/today", {
        headers: { "Authorization": "Bearer " + token }
      });

      if (!res.ok) throw new Error("Failed to load meals");

      const data = await res.json();
      render(data);

    } catch (err) {
      console.error(err);
      list.innerHTML = "<p class='muted'>Could not load today's meals.</p>";
    }
  }

  // Render meal list
  function render(items = []) {
    if (!items.length) {
      list.innerHTML = "<p class='muted'>No meals recorded for today.</p>";
      return;
    }

    list.innerHTML = items.map(m => `
      <div class="card-row">
        <div class="card-left">${escapeHtml(m.mealType)}</div>
        <div class="card-right">${m.calories || 0} kcal</div>
      </div>
    `).join("");
  }

  // Save Meal
  saveMeal.addEventListener("click", async (e) => {
    e.preventDefault();

    const body = {
      mealType: mealType.value.trim(),
      calories: parseFloat(caloriesEl.value) || 0,  // FIXED
      protein: parseFloat(proteinEl.value) || 0,
      carbs: parseFloat(carbsEl.value) || 0,
      fats: parseFloat(fatsEl.value) || 0,
      date: dateEl.value
    };

    try {
      const res = await fetch("/smart-health-track/meals/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        alert("Meal saved!");

        caloriesEl.value = "";
        proteinEl.value = "";
        carbsEl.value = "";
        fatsEl.value = "";

        loadToday();
      } else {
        const d = await res.json().catch(() => ({}));
        alert(d.error || "Failed to save meal");
      }

    } catch (err) {
      console.error(err);
      alert("Network error");
    }
  });

  function escapeHtml(s) {
    return String(s || "").replace(/[&<>"']/g, c => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    }[c]));
  }

  loadToday();
});
