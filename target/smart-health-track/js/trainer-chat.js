const token = localStorage.getItem("sha_token");
let currentUserId = null;

if (!token) {
    window.location.href = 'login.html';
}

const usersListEl = document.getElementById("users");
const chatBox = document.getElementById("chatBox");
const msgInput = document.getElementById("msg");
const currentChatUserHeader = document.getElementById("currentChatUser");

async function fetchUsers() {
  try {
    const res = await fetch("/smart-health-track/trainer/chats", {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error("Failed to load users");
    const list = await res.json();
    usersListEl.innerHTML = list.map(u =>
       `<div class="user-item" id="user-item-${u.userId}" onclick="openChat(${u.userId}, '${escapeHtml(u.userName)}')">
         <div style="width: 40px; height: 40px; background: #eee; border-radius: 50%; margin-right: 12px; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #666;">
           ${u.userName.charAt(0).toUpperCase()}
         </div>
         <span style="font-weight: 500;">${escapeHtml(u.userName)}</span>
       </div>`
    ).join("");
  } catch (e) {
    console.error(e);
  }
}

function openChat(uid, uname) {
  const prevActive = document.querySelector(".user-item.active");
  if (prevActive) prevActive.classList.remove("active");
  
  currentUserId = uid;
  const currentItem = document.getElementById(`user-item-${uid}`);
  if (currentItem) currentItem.classList.add("active");
  
 currentChatUserHeader.innerText = uname;
  chatBox.innerHTML = "<p style='text-align: center; color: #888; margin-top: 20px;'>Loading messages...</p>";
  loadMessages();
}

async function loadMessages() {
  if (!currentUserId) return;
  try {
    const res = await fetch(`/smart-health-track/chat/messages?userId=${currentUserId}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error("Failed to load messages");
    const list = await res.json();
    
    const isAtBottom = chatBox.scrollHeight - chatBox.scrollTop <= chatBox.clientHeight + 100;

    chatBox.innerHTML = list.map(m => {
      const isTrainer = m.senderRole === 'TRAINER';
      return `<div class="msg ${isTrainer ? 'trainer' : 'user'}">
               <div class="msg-info">${isTrainer ? 'You' : 'User'}</div>
                <div>${escapeHtml(m.message)}</div> 
              </div>`;
    }).join("");

    if (isAtBottom) {
        chatBox.scrollTop = chatBox.scrollHeight;
    }
  } catch (e) {
    console.error(e);
  }
}

async function send() {
  const message = msgInput.value.trim();
  if (!message || !currentUserId) return;

  try {
    const res = await fetch("/smart-health-track/chat/send", {
      method: "POST",
      headers: {
        Authorization: "Bearer " + token,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        userId: currentUserId,
        message: message
      })
    });
    
    if (res.ok) {
      msgInput.value = "";
      await loadMessages();
      chatBox.scrollTop = chatBox.scrollHeight;
    }
  } catch (e) {
    console.error(e);
  }
}

if (msgInput) {
    msgInput.onkeypress = (e) => {
        if (e.key === 'Enter') send();
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

fetchUsers();
setInterval(loadMessages, 5000);