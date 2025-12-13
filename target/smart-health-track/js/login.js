
document.getElementById('toRegister').onclick = () => location.href = 'register.html';

document.getElementById('loginBtn').onclick = async () => {
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value.trim();
  if (!email || !password) { alert('email & password required'); return; }

  try {
    const res = await fetch('/smart-health-track/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (res.status === 200 && data.token) {
     
      localStorage.setItem('sha_token', data.token);
      location.href = 'home.html';
    } else {
      alert(data.error || 'Login failed');
    }
  } catch (e) { console.error(e); alert('Network error') }
};
