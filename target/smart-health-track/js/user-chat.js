const token = localStorage.getItem("sha_token");
const trainerId = new URLSearchParams(location.search).get("trainerId");

if (!token) {
    window.location.href = 'login.html';
}

const chatBox = document.getElementById("chatBox");
const msgInput = document.getElementById("msg");
const sendBtn = document.getElementById("sendBtn");

async function load() {
  if (!trainerId) return;
  try {
    const res = await fetch(`/smart-health-track/chat/messages?trainerId=${trainerId}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error("Failed to load messages");
    const list = await res.json();
    
    const isAtBottom = chatBox.scrollHeight - chatBox.scrollTop <= chatBox.clientHeight + 100;

    chatBox.innerHTML = list.map(m => {
      const isUser = m.senderRole === 'USER';
      return `<div class="msg ${isUser ? 'user' : 'trainer'}">
                <b>${isUser ? 'You' : 'Trainer'}:</b> ${escapeHtml(m.message)}
              </div>`;
    }).join("");

    if (isAtBottom) {
        chatBox.scrollTop = chatBox.scrollHeight;
    }
  } catch (e) {
    console.error(e);
  }
}

async function sendMessage() {
  const message = msgInput.value.trim();
  if (!message || !trainerId) return;

  try {
    const res = await fetch("/smart-health-track/chat/send", {
      method: "POST",
      headers: {
        Authorization: "Bearer " + token,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        trainerId: parseInt(trainerId),
        message: message
      })
    });
    
    if (res.ok) {
      msgInput.value = "";
      await load();
      chatBox.scrollTop = chatBox.scrollHeight;
    }
  } catch (e) {
    console.error(e);
  }
}

if (sendBtn) sendBtn.onclick = sendMessage;
if (msgInput) {
    msgInput.onkeypress = (e) => {
        if (e.key === 'Enter') sendMessage();
    };
}

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

load();
setInterval(load, 3000);