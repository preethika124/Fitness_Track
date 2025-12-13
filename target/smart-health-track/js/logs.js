document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("sha_token");
  if (!token) {
    location.href = "login.html";
    return;
  }

  // Correct IDs from HTML
  const waterEl = document.getElementById("waterIntake");
  const sleepEl = document.getElementById("sleepHours");
  const notesEl = document.getElementById("notes");
  const dateEl = document.getElementById("logDate");
  const saveBtn = document.getElementById("saveLog");

  // OPTIONAL: Section to render today logs (add container with id="todayLog" in HTML)
  const display = document.getElementById("todayLog");

  // Default date = today
  if (!dateEl.value) {
    dateEl.value = new Date().toISOString().slice(0, 10);
  }

  // Load Today’s Log
  async function loadToday() {
    if (!display) return; // If your page does not need display

    try {
      const res = await fetch("/smart-health-track/logs/today", {
        headers: { "Authorization": "Bearer " + token }
      });

      if (!res.ok) throw new Error("Failed to load logs");

      const data = await res.json();
      render(data);

    } catch (err) {
      console.error(err);
      display.innerHTML = "<p class='muted'>Could not load logs.</p>";
    }
  }

  function render(d) {
    if (!display) return;

    if (!d) {
      display.innerHTML = "<p class='muted'>No logs for today.</p>";
      return;
    }

    display.innerHTML = `
      <div><strong>Water:</strong> ${d.waterIntake || 0} L</div>
      <div><strong>Sleep:</strong> ${d.sleepHours || 0} hrs</div>
      <div><strong>Notes:</strong> ${escapeHtml(d.notes || "")}</div>
    `;
  }

  // Save Log
  saveLog.addEventListener("click", async (ev) => {
    ev.preventDefault();

    const body = {
      waterIntake: parseFloat(waterEl.value) || 0,
      sleepHours: parseFloat(sleepEl.value) || 0,
      notes: notesEl.value.trim(),
      date: dateEl.value || new Date().toISOString().slice(0, 10)
    };

    try {
      const res = await fetch("/smart-health-track/logs/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        alert("Log saved!");
        loadToday();
      } else {
        const d = await res.json().catch(() => ({}));
        alert(d.error || "Failed to save log");
      }

    } catch (err) {
      console.error(err);
      alert("Network error");
    }
  });

  function escapeHtml(s) {
    return String(s || "").replace(/[&<>"']/g, c => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      "\"": "&quot;",
      "'": "&#39;"
    }[c]));
  }

  loadToday();
});
