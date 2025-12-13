document.addEventListener("DOMContentLoaded", () => {

    fetch("/smart-health-track/health/tip")
        .then(res => res.json())
        .then(data => {
            document.getElementById("dailyTip").innerText = data.text;

            // Set background image
            const card = document.getElementById("tipCard");
            card.style.backgroundImage = `url('${data.image}')`;
            card.style.backgroundSize = "cover";
            card.style.backgroundPosition = "center";
            card.style.padding = "40px";
            card.style.borderRadius = "18px";
            card.style.color = "white";
            card.style.textShadow = "0px 1px 3px rgba(0,0,0,0.5)";
        })
        .catch(err => console.error("Error loading tip:", err));


         document.getElementById("logoutBtn").onclick = () => {
        localStorage.removeItem("sha_token");
        location.href = "login.html";
    };

});
