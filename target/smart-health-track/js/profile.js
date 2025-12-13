document.addEventListener("DOMContentLoaded", () => {

    const token = localStorage.getItem("sha_token");
    if (!token) { location.href = "login.html"; return; }

   
    fetch("/smart-health-track/profile", {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById("firstName").textContent = data.firstName;
        document.getElementById("role").textContent = data.role;

        document.getElementById("age").value = data.age || "";
        document.getElementById("weight").value = data.weight || "";
        document.getElementById("goals").value = data.goals || "";
    });

   
    document.getElementById("saveBtn").onclick = () => {

        const body = {
            age: document.getElementById("age").value || null,
            weight: document.getElementById("weight").value || null,
            goals: document.getElementById("goals").value || null
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


    document.getElementById("logoutBtn").onclick = () => {
       
        location.href = "home.html";
    };

});
