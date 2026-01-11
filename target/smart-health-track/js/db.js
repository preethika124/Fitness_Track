document.addEventListener("DOMContentLoaded", async () => {

    const token = localStorage.getItem("sha_token");
    if (!token) {
        location.href = "login.html";
        return;
    }

    let profileGoals;
    let workouts;
    let logs;

    const headers = { "Authorization": "Bearer " + token };

    // ⬇️ LOAD EVERYTHING FIRST
    const [profileRes, workoutRes, logRes] = await Promise.all([
        fetch("/smart-health-track/profile", { headers }),
        fetch("/smart-health-track/analytics/workouts", { headers }),
        fetch("/smart-health-track/analytics/logs", { headers })
    ]);

    profileGoals = await profileRes.json();
    workouts = await workoutRes.json();
    logs = await logRes.json();

    /* ================= SUMMARY ================= */
    fetch("/smart-health-track/dashboard/summary", { headers })
        .then(r => r.json())
        .then(d => {
            setText("todayCalories", d.todayCalories);
            setText("weeklyBurned", d.weeklyBurned);
        });


        /* ================= NUTRITION ================= */
fetch("/smart-health-track/analytics/nutrition", { headers })
    .then(r => r.json())
    .then(data => {

        console.log("Nutrition:", data);

        if (!data.length) return;

        const calorieCanvas = document.getElementById("calorieChart");
        if (!calorieCanvas) return;

        new Chart(calorieCanvas, {
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
    })
    .catch(err => console.error("Nutrition error", err));


    /* ================= WORKOUTS ================= */
    const workoutDays = workouts.length;
    setText("workoutDays", workoutDays);

    updateExerciseGoal(profileGoals, workoutDays);

    const workoutCanvas = document.getElementById("workoutChart");
    if (workoutCanvas) {
        new Chart(workoutCanvas, {
            type: "bar",
            data: {
                labels: workouts.map(d => d.date),
                datasets: [{
                    label: "Workouts",
                    data: workouts.map(d => d.count),
                    backgroundColor: "#1b8c40"
                }]
            }
        });
    }

    /* ================= LOGS ================= */
    if (logs.length) {
        const avgWater = logs.reduce((s, d) => s + d.water, 0) / logs.length;
        const avgSleep = logs.reduce((s, d) => s + d.sleep, 0) / logs.length;

        updateWaterGoal(profileGoals, avgWater);
        updateSleepGoal(profileGoals, avgSleep);

        const logCanvas = document.getElementById("logChart");
        if (logCanvas) {
            new Chart(logCanvas, {
                type: "line",
                data: {
                    labels: logs.map(d => d.date),
                    datasets: [
                        {
                            label: "Water (L)",
                            data: logs.map(d => d.water),
                            borderColor: "#1b8c40",
                            fill: false
                        },
                        {
                            label: "Sleep (hrs)",
                            data: logs.map(d => d.sleep),
                            borderColor: "#4a90e2",
                            fill: false
                        }
                    ]
                }
            });
        }
    }
const durationCanvas = document.getElementById("durationChart");
if (durationCanvas) {
    new Chart(durationCanvas, {
        type: "line",
        data: {
            labels: workouts.map(d => d.date),
            datasets: [{
                label: "Workout Duration (min)",
                data: workouts.map(d => Number(d.minutes || 0)),
                borderColor: "#9b59b6",
                backgroundColor: "rgba(155, 89, 182, 0.15)",
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: "Minutes"
                    }
                }
            }
        }
    });
}


});
function updateExerciseGoal(goals, days) {
    if (!goals.weeklyExerciseGoal) return;

    const percent = Math.min((days / goals.weeklyExerciseGoal) * 100, 100);

    document.getElementById("exerciseText").innerText =
        `${days} / ${goals.weeklyExerciseGoal} days`;

    updateGoalBar("exerciseBar", percent);

    if (percent < 50) {
        addInsight("🏃 Increase workout frequency to reach your weekly exercise goal");
    }
}

function updateWaterGoal(goals, avgWater) {
    if (!goals.dailyWaterGoal) return;

    const percent = Math.min((avgWater / goals.dailyWaterGoal) * 100, 100);

    document.getElementById("waterText").innerText =
        `${avgWater.toFixed(1)} / ${goals.dailyWaterGoal} L`;

    updateGoalBar("waterBar", percent);

    if (percent < 60) {
        addInsight("💧 Drink more water to meet your daily water goal");
    }
}

function updateSleepGoal(goals, avgSleep) {
    if (!goals.dailySleepGoal) return;

    const percent = Math.min((avgSleep / goals.dailySleepGoal) * 100, 100);

    document.getElementById("sleepText").innerText =
        `${avgSleep.toFixed(1)} / ${goals.dailySleepGoal} hrs`;

    updateGoalBar("sleepBar", percent);

    if (percent < 60) {
        addInsight("😴 Improve sleep duration to reach your daily sleep goal");
    }
}

function setText(id, value) { const el = document.getElementById(id); if (el) el.innerText = value ?? "--"; }
function addInsight(text) { const box = document.getElementById("insights"); if (!box) return; const div = document.createElement("div"); div.className = "insight"; div.innerText = text; box.appendChild(div); }
function updateGoalBar(barId, percent) {
    const bar = document.getElementById(barId);
    if (!bar) return;

    bar.style.width = percent + "%";

    if (percent < 40) {
        bar.style.background = "#e74c3c";      // red
    } else if (percent < 75) {
        bar.style.background = "#f39c12";      // orange
    } else {
        bar.style.background = "#1b8c40";      // green
    }
}
