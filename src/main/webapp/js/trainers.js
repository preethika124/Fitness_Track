document.addEventListener("DOMContentLoaded", () => {
  const findBtn = document.getElementById('findBtn');
  const matchesEl = document.getElementById('matches');

  findBtn.onclick = async () => {
    const goal = document.getElementById('goal').value;
    // map goal to keywords
    const map = {
      weight_loss: 'weight loss',
      muscle_gain: 'Muscle gain',
      flexibility: 'Flexibility',
      endurance: 'Endurance'
    };
    const q = map[goal] || '';
    try {
      const res = await fetch("/smart-health-track/trainers" + (q ? ("?q=" + encodeURIComponent(q)) : ""));
      if (!res.ok) throw new Error("HTTP " + res.status);
      const list = await res.json();
      if (!list.length) { matchesEl.innerHTML = "<p>No trainers found</p>"; return; }
      matchesEl.innerHTML = list.map(t => `
        <div class="post-card">
          <div class="post-header">
           
            <p >Trainer Email: ${escapeHtml(t.userEmail)}</p>

          </div>
          <p>Specialisation: ${escapeHtml(t.specialization)} </p>
          <p>Experience:  ${t.experienceYears ? t.experienceYears + ' yrs' : ''}</p>
          <p>Availability: ${escapeHtml(t.availability || '')}</p>
        </div>
      `).join('');
    } catch (e) {
      console.error(e);
      matchesEl.innerHTML = "<p>Error fetching trainers</p>";
    }
  };

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
