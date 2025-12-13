
document.addEventListener("DOMContentLoaded", () => {

    console.log("REGISTER.JS READY");

    // ERROR LABELS FIX
    const errFirst   = document.getElementById("errFirst");
    const errLast    = document.getElementById("errLast");
    const errEmail   = document.getElementById("errEmail");
    const errPass    = document.getElementById("errPass");
    const errConfirm = document.getElementById("errConfirm");

    document.getElementById("toLogin").onclick = () =>
        location.href = "login.html";

    document.getElementById("registerBtn").onclick = async () => {

        console.log("REGISTER BTN CLICKED");

        const firstName = document.getElementById("firstName").value.trim();
        const lastName  = document.getElementById("lastName").value.trim();
        const email     = document.getElementById("email").value.trim();
        const password  = document.getElementById("password").value.trim();
        const confirmPassword = document.getElementById("confirmPassword").value.trim();
        const role      = document.getElementById("dropdownSelected").textContent.trim().toUpperCase();

        // Clear old errors
        document.querySelectorAll(".error-msg").forEach(e => e.textContent = "");

        let ok = true;

        if (!firstName) { errFirst.textContent = "First name required"; ok = false; }
        if (!lastName)  { errLast.textContent = "Last name required"; ok = false; }
        if (!email)     { errEmail.textContent = "Email required"; ok = false; }
        if (!password)  { errPass.textContent = "Password required"; ok = false; }

        // ✅ CONFIRM PASSWORD CHECK
        if (!confirmPassword) {
            errConfirm.textContent = "Confirm password required";
            ok = false;
        } else if (password !== confirmPassword) {
            errConfirm.textContent = "Passwords do not match";
            ok = false;
        }

        if (!ok) return;

        // SEND TO BACKEND
        try {
            const res = await fetch("/smart-health-track/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ firstName, lastName, email, password, role })
            });

            const data = await res.json();
            console.log(data);

            if (res.status === 201) {
                alert("Account created successfully!");
                location.href = "login.html";
            } else {
                alert(data.error || "Registration failed");
            }
        } catch (err) {
            alert("Network error");
            console.error(err);
        }
    };
});
