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
  

 
  function escapeHtml(s){ if(!s) return ""; return s.replace(/[&<>"']/g, c=>({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c])); }
});
