document.addEventListener("DOMContentLoaded", () => {

  const token = localStorage.getItem("sha_token");
  if (!token) { 
      location.href = "login.html"; 
      return; 
  }

  const typeEl = document.getElementById("exerciseType");
  const durationEl = document.getElementById("duration");
  const caloriesEl = document.getElementById("calories");
  const dateEl = document.getElementById("workoutDate");
  const saveBtn = document.getElementById("saveWorkout");  // ✔ correct button

  // Auto-fill date
  if (!dateEl.value) {
      dateEl.value = new Date().toISOString().slice(0,10);
  }

  saveWorkout.addEventListener("click", async (e) => {
    console.log("HI");
    e.preventDefault();

    const body = {
  workoutType: typeEl.value.trim(),        // FIXED
  durationMinutes: parseInt(durationEl.value,10) || 0,  // FIXED
  caloriesBurned: parseFloat(caloriesEl.value) || 0,    // FIXED
  date: dateEl.value
};


    try {
      const res = await fetch("/smart-health-track/workout/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        alert("Workout added successfully!");
        typeEl.value = "Running";
        durationEl.value = "";
        caloriesEl.value = "";
      } else {
        alert("Failed to add workout");
      }

    } catch (err) {
      console.error(err);
      alert("Network error");
    }
  });

});
