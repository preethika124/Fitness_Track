const API_BASE = '/smart-health-track/api';
let token = localStorage.getItem('sha_token');
let editMode = false;
let editId = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    editId = urlParams.get('edit');
    
    if (editId) {
        editMode = true;
        document.getElementById('pageTitle').textContent = 'Edit Post';
        document.getElementById('submitBtn').textContent = 'Update Post';
        loadPostForEdit(editId);
    }

    setupForm();
  
    setupCharCount();
    setupLogout();
});

function setupLogout() {
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('sha_token');
        window.location.href = 'login.html';
    });
}

function setupCharCount() {
    const titleInput = document.getElementById('title');
    const titleCount = document.getElementById('titleCount');
    
    titleInput.addEventListener('input', () => {
        titleCount.textContent = titleInput.value.length;
    });
}


function setupForm() {
    document.getElementById('blogForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.disabled = true;
        submitBtn.textContent = editMode ? 'Updating...' : 'Publishing...';

        const title = document.getElementById('title').value.trim();
        const category = document.getElementById('category').value;
        const content = document.getElementById('content').value.trim();
       

        try {
            const url = editMode ? `${API_BASE}/blogs/${editId}` : `${API_BASE}/blogs/`;
            const method = editMode ? 'PUT' : 'POST';
            
            const res = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ title, category, content })
            });

            const data = await res.json();

            if (res.ok && data.success) {
                const successMsg = document.getElementById('successMsg');
                successMsg.textContent = editMode ? 'Post updated successfully!' : 'Post published successfully!';
                successMsg.style.display = 'block';
                
                setTimeout(() => {
                    if (editMode) {
                        window.location.href = `post.html?id=${editId}`;
                    } else {
                        window.location.href = `post.html?id=${data.id}`;
                    }
                }, 1500);
            } else {
                throw new Error(data.error || 'Failed to save post');
            }
        } catch (err) {
            alert(err.message);
            submitBtn.disabled = false;
            submitBtn.textContent = editMode ? 'Update Post' : 'Publish Post';
        }
    });
}

async function loadPostForEdit(id) {
    try {
        const res = await fetch(`${API_BASE}/blogs/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const data = await res.json();
        
        if (!data.blog) {
            throw new Error('Post not found');
        }

        const blog = data.blog;
        document.getElementById('title').value = blog.title;
        document.getElementById('titleCount').textContent = blog.title.length;
        document.getElementById('category').value = blog.category || '';
        document.getElementById('content').value = blog.content;
        
      
    } catch (err) {
        alert('Error loading post: ' + err.message);
        window.location.href = 'blog.html';
    }
}
