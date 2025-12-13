document.addEventListener("DOMContentLoaded", () => {
    // Redirect unauthorized users
const token = localStorage.getItem("sha_token");

if (!token) {
    alert("You must log in first!");
    window.location.href = "login.html";
}

    const height = document.getElementById("height");
    const weight = document.getElementById("weight");
    const resultCard = document.getElementById("bmiResult");
    const bmiValue = document.getElementById("bmiValue");
    const category = document.getElementById("bmiCategory");
    const suggest = document.getElementById("bmiSuggestion");
    const backBtn = document.getElementById("backBtn");

    document.getElementById("calcBtn").onclick = () => {

        const h = parseFloat(height.value);
        const w = parseFloat(weight.value);

        if (!h || !w || h <= 0 || w <= 0) {
            alert("Enter valid height & weight");
            return;
        }

        const bmi = (w / ((h / 100) ** 2)).toFixed(1);
        bmiValue.textContent = bmi;

        let type = "";
        let advice = "";
        let color = "";

        if (bmi < 18.5) {
            type = "Underweight";
            advice = "Increase nutrient-dense meals with protein & healthy fats.";
            color = "#c91818";
        } else if (bmi <= 24.9) {
            type = "Normal";
            advice = "Maintain balanced diet & daily movement. Great job!";
            color = "#1b8c40";
        } else if (bmi <= 29.9) {
            type = "Overweight";
            advice = "Cut processed sugar & increase hydration and activity.";
            color = "#cc7a00";
        } else {
            type = "Obese";
            advice = "Monitor calories & consult wellness planning for safety.";
            color = "#c91818";
        }

        category.textContent = type;
        category.style.color = color;
        suggest.textContent = advice;

        resultCard.style.display = "flex";
        suggest.style.display = "block";

        const body = {
    bmiValue: parseFloat(bmi),
    status: type
};

fetch("/smart-health-track/bmi/save", {
    method: "POST",
    headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    },
    body: JSON.stringify(body)
}).then(res => {
    if (!res.ok) return alert("Failed to save BMI");
    console.log("BMI saved successfully");
});
    };
    



    backBtn.onclick = () => location.href = "home.html";

});
