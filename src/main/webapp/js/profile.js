document.addEventListener("DOMContentLoaded", () => {

    const token = localStorage.getItem("sha_token");
    if (!token) { location.href = "login.html"; return; }

   
   fetch("/smart-health-track/profile", {
    headers: { "Authorization": "Bearer " + token }
  })
  .then(res => {
    if (!res.ok) throw new Error("Failed to load profile");
    return res.json();
  })
  .then(data => {

    document.getElementById("firstName").textContent = data.firstName ?? "";
    document.getElementById("role").textContent = data.role ?? "";

    document.getElementById("age").value =
      data.age !== null ? data.age : "";

    document.getElementById("weight").value =
      data.weight !== null ? data.weight : "";

    document.getElementById("exerciseGoal").value =
      data.weeklyExerciseGoal !== null ? data.weeklyExerciseGoal : "";

    document.getElementById("waterGoal").value =
      data.dailyWaterGoal !== null ? data.dailyWaterGoal : "";

    document.getElementById("sleepGoal").value =
      data.dailySleepGoal !== null ? data.dailySleepGoal : "";
  })
  .catch(err => {
    console.error(err);
    alert("Could not load profile data");
  });



   
    document.getElementById("saveBtn").onclick = () => {

        const body = {
            age: document.getElementById("age").value || null,
            weight: document.getElementById("weight").value || null,
            weeklyExerciseGoal: document.getElementById("exerciseGoal").value || null,
            dailyWaterGoal: document.getElementById("waterGoal").value || null,
            dailySleepGoal: document.getElementById("sleepGoal").value || null
        };

        fetch("/smart-health-track/update-profile", {   
            method: "POST",                               
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(body)
        })
        .then(r => r.json())
        .then(data => {
            if (data.status === "success") {
                alert("Profile updated successfully!");
            } else {
                alert("Update failed");
            }
        })
        .catch(err => console.error(err));
    };


  

});
