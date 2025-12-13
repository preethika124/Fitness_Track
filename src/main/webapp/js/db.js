document.addEventListener("DOMContentLoaded", () => {

    console.log("✅ Dashboard JS loaded");

    const token = localStorage.getItem("sha_token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const headers = {
        "Authorization": "Bearer " + token
    };

    

    /* ================= SUMMARY ================= */
    fetch("/smart-health-track/dashboard/summary", {         headers: { "Authorization": "Bearer " + token }})
        .then(r => r.json())
        .then(d => {
            setText("todayCalories", d.todayCalories);
            setText("weeklyBurned", d.weeklyBurned);
        });

    /* ================= WORKOUTS ================= */
    fetch("/smart-health-track/analytics/workouts", {         headers: { "Authorization": "Bearer " + token } })
        .then(r => r.json())
        .then(data => {

            console.log("Workouts:", data);

            const days = data.length;
            setText("workoutDays", days);

        

            if (days < 3) {
                addInsight("⚠ You worked out less than 3 days this week");
            }

            const canvas = document.getElementById("workoutChart");
            if (canvas) {
                new Chart(canvas, {
                    type: "bar",
                    data: {
                        labels: data.map(d => d.date),
                        datasets: [{
                            label: "Workouts",
                            data: data.map(d => d.count),
                            backgroundColor: "#1b8c40"
                        }]
                    }
                });
            }
        });

    /* ================= NUTRITION ================= */
    fetch("/smart-health-track/analytics/nutrition", {         headers: { "Authorization": "Bearer " + token } })
        .then(r => r.json())
        .then(data => {
            const canvas = document.getElementById("calorieChart");
            if (canvas) {
                new Chart(canvas, {
                    type: "line",
                    data: {
                        labels: data.map(d => d.date),
                        datasets: [
                            {
                                label: "Consumed",
                                data: data.map(d => d.consumed),
                                borderColor: "#f2994a",
                                fill: false
                            },
                            {
                                label: "Burned",
                                data: data.map(d => d.burned),
                                borderColor: "#1b8c40",
                                fill: false
                            }
                        ]
                    }
                });
            }
        });

    /* ================= LOGS ================= */
    fetch("/smart-health-track/analytics/logs", {         headers: { "Authorization": "Bearer " + token } })
        .then(r => r.json())
        .then(data => {

            if (!data.length) return;

            const avgWater =
                data.reduce((s, d) => s + d.water, 0) / data.length;

            

            if (avgWater < 2) {
                addInsight("💧 Increase your daily water intake");
            }

            const canvas = document.getElementById("logChart");
            if (canvas) {
                new Chart(canvas, {
                    type: "line",
                    data: {
                        labels: data.map(d => d.date),
                        datasets: [
                            {
                                label: "Water (L)",
                                data: data.map(d => d.water),
                                borderColor: "#1b8c40",
                                fill: false
                            },
                            {
                                label: "Sleep (hrs)",
                                data: data.map(d => d.sleep),
                                borderColor: "#4a90e2",
                                fill: false
                            }
                        ]
                    }
                });
            }
        });
});

/* ================= HELPERS ================= */
function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value ?? "--";
}



function addInsight(text) {
    const box = document.getElementById("insights");
    if (!box) return;

    const div = document.createElement("div");
    div.className = "insight";
    div.innerText = text;
    box.appendChild(div);
}
