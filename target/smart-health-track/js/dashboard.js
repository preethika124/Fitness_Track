document.addEventListener("DOMContentLoaded", () => {
  console.log("fgf");
  const token = localStorage.getItem("sha_token");
  if (!token) { location.href = "login.html"; return; }

  const workoutsEl = document.getElementById("dashWorkout");

  const mealsEl = document.getElementById("dashMeals");
  const logEl = document.getElementById("dashLog");

  async function loadDashboard() {
    try {
      const res = await fetch("/smart-health-track/dashboard", {
        headers: { "Authorization": "Bearer " + token }
      });

      const data = await res.json();
      console.log(data.workouts,data.meals,data.log);

      renderWorkouts(data.workouts);
      renderMeals(data.meals);
      renderLog(data.log);

    } catch (err) {
      console.error(err);
    }
  }

  function renderWorkouts(list) {
    console.log(list);
    if (!list || list.length === 0) {
      workoutsEl.innerHTML = "<p class='muted'>No workouts today</p>";
      return;
    }

    workoutsEl.innerHTML = list.map(w =>
      `<div class="mini">
         ${escapeHtml(w.workoutType)} • ${w.durationMinutes} min • ${w.caloriesBurned} kcal
       </div>`
    ).join("");
  }

  function renderMeals(list) {
     console.log(list);
    if (!list || list.length === 0) {
      mealsEl.innerHTML = "<p class='muted'>No meals today</p>";
      return;
    }

    mealsEl.innerHTML = list.map(m =>
      `<div class="mini">
         ${escapeHtml(m.mealType)} • ${m.calories} kcal
       </div>`
    ).join("");
  }

  function renderLog(log) {
     console.log(log);
    if (!log) {
      logEl.innerHTML = "<p class='muted'>No log today</p>";
      return;
    }

    logEl.innerHTML = `
      <div><strong>Water:</strong> ${log.waterIntake} L</div>
      <div><strong>Sleep:</strong> ${log.sleepHours} hours</div>
      <div><strong>Notes:</strong> ${escapeHtml(log.notes || "")}</div>
    `;
  }

  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;',
      '"': '&quot;', "'": '&#39;'
    }[c]));
  }

  loadDashboard();
});
