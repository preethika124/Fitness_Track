document.addEventListener("DOMContentLoaded", () => {
 

  // Enrollment
  const enrollBtn = document.getElementById('enrollBtn');
  const resultEl = document.getElementById('enrollResult');
  enrollBtn.onclick = async () => {
    const spec = document.getElementById('spec').value.trim();
    const exp = parseInt(document.getElementById('exp').value || "0", 10);
    const avail = document.getElementById('avail').value.trim();

    if (!spec) return alert("Enter specialization");

    const token = localStorage.getItem("sha_token");
    if (!token) { alert("Please login to enroll"); location.href = "login.html"; return; }

    const payload = { specialization: spec, experienceYears: isNaN(exp) ? null : exp, availability: avail || null };
    try {
      const res = await fetch("/smart-health-track/trainers", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization": "Bearer " + token },
        body: JSON.stringify(payload)
      });
      if (res.status === 201) {
        const data = await res.json();
        resultEl.innerHTML = `<div class="post-card">Enrolled as trainer:  ${escapeHtml(data.specialization)}</div>`;
      } else {
        let d;
        try { d = await res.json(); } catch(e){ d = { error: "server returned "+res.status }; }
        resultEl.innerHTML = `<div style="color:red">${escapeHtml(d.error || "Enroll failed")}</div>`;
      }
    } catch (e) {
      console.error(e);
      resultEl.innerHTML = `<div style="color:red">Network error</div>`;
    }
  };

  function escapeHtml(s){ if(!s) return ""; return s.replace(/[&<>"']/g, c=>({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c])); }
});
