document.addEventListener("DOMContentLoaded", () => {
  const findBtn = document.getElementById('findBtn');
  const matchesEl = document.getElementById('matches');
const activeChatsList = document.getElementById('activeChatsList');
  const token = localStorage.getItem("sha_token");
  if (!token) {
    window.location.href = 'login.html';
    return;
  }
  loadUserChats();
  // Load existing trainer chats for the user
  async function loadUserChats() {
    
      try {
          // Re-using the same endpoint logic if possible, or needing a user-specific chat list
          const res = await fetch("/smart-health-track/user/chats", {
              headers: { Authorization: "Bearer " + token }
          });
          if (!res.ok) throw new Error("Failed to load chat list");
          const list = await res.json();
          if (!list.length) {
              activeChatsList.innerHTML = "<p>No active conversations yet.</p>";
              return;
          }
          activeChatsList.innerHTML = list.map(t => `
              <div class="trainer-card" style="display:flex; justify-content:space-between; align-items:center;">
                  <div>
                      <h3>${escapeHtml(t.name || 'Trainer')}</h3>
                      <p style="font-size:12px; color:#666;">Last active chat</p>
                  </div>
                  <button class="primary" onclick="startChat(${t.id})">Open Chat</button>
              </div>
          `).join("");
      } catch (e) {
          console.error("Chat load error:", e);
          activeChatsList.innerHTML = "<p>Could not load conversations.</p>";
      }
  }

  findBtn.onclick = async () => {
    const goal = document.getElementById('goal').value;

    const map = {
      weight_loss: 'weight loss',
      muscle_gain: 'muscle',
      flexibility: 'flexibility',
      endurance: 'endurance'
    };

    const q = map[goal] || '';

    try {
      const res = await fetch(
        "/smart-health-track/trainers" +
        (q ? ("?q=" + encodeURIComponent(q)) : "")
      );

      if (!res.ok) throw new Error("HTTP " + res.status);

      const list = await res.json();

      if (!list.length) {
        matchesEl.innerHTML = "<p>No trainers found</p>";
        return;
      }

      matchesEl.innerHTML = list.map(t => `
        <div class="trainer-card">
          <h3>${escapeHtml(t.name || 'Trainer')}</h3>

          <p><b>Email:</b> ${escapeHtml(t.userEmail)}</p>
          <p><b>Specialisation:</b> ${escapeHtml(t.specialization)}</p>
          <p><b>Experience:</b> ${t.experienceYears || 0} yrs</p>
          <p><b>Availability:</b> ${escapeHtml(t.availability || 'N/A')}</p>

          <button class="primary"
            onclick="startChat(${t.id})"
            style="margin-top:10px;">
            💬 Chat with Trainer
          </button>
        </div>
      `).join("");

    } catch (e) {
      console.error(e);
      matchesEl.innerHTML = "<p>Error fetching trainers</p>";
    }
  };

});

/* ================= CHAT REDIRECT ================= */

function startChat(trainerId) {
  // redirect to user chat page with trainerId

  window.location.href = `user-chat.html?trainerId=${trainerId}`;
}
/* ================= SECURITY ================= */

function escapeHtml(s) {
  if (!s) return "";
  return s.replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c]));
}
