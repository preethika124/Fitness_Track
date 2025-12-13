const API_BASE = '/smart-health-track/api';
let token = localStorage.getItem('sha_token');
let currentBlogId = null;
let currentUserId = null;
let currentUserRole = null;
let isLiked = false;

document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    currentBlogId = urlParams.get('id');
    
    if (!currentBlogId) {
        window.location.href = 'blog.html';
        return;
    }

    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            currentUserId = payload.userId;
            currentUserRole = payload.role;
        } catch (e) {
            console.error('Error parsing token:', e);
        }
    }

    loadPost();
    loadComments();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('likeBtn').addEventListener('click', toggleLike);
    document.getElementById('shareBtn').addEventListener('click', showShareModal);
    document.getElementById('commentForm').addEventListener('submit', postComment);
    document.getElementById('copyBtn').addEventListener('click', copyShareLink);
    document.getElementById('closeModal').addEventListener('click', hideShareModal);
    
    document.getElementById('shareModal').addEventListener('click', (e) => {
        if (e.target.id === 'shareModal') hideShareModal();
    });

    if (document.getElementById('logoutBtn')) {
        document.getElementById('logoutBtn').addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('sha_token');
            window.location.href = 'login.html';
        });
    }
}

async function loadPost() {
    try {
        const headers = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const res = await fetch(`${API_BASE}/blogs/${currentBlogId}`, { headers });
        const data = await res.json();
        
        if (!data.blog) {
            throw new Error('Post not found');
        }

        const blog = data.blog;
        isLiked = data.liked || false;

        if (blog.imageUrl) {
            document.getElementById('postImage').src = blog.imageUrl;
        } else {
            document.getElementById('postImage').style.display = 'none';
        }

        document.getElementById('postCategory').textContent = blog.category || 'General';
        document.getElementById('postTitle').textContent = blog.title;
        document.getElementById('postBody').textContent = blog.content;
        
        const initials = blog.authorName.split(' ').map(n => n[0]).join('').toUpperCase();
        document.getElementById('authorAvatar').textContent = initials;
        document.getElementById('authorName').textContent = blog.authorName;
        
        const roleText = blog.authorRole;
        const isVerified = ['admin', 'trainer'].includes(roleText?.toLowerCase());
        document.getElementById('authorRole').textContent = roleText + (isVerified ? ' ✓' : '');
        
        const date = new Date(blog.createdAt).toLocaleDateString('en-US', { 
            month: 'long', day: 'numeric', year: 'numeric' 
        });
        document.getElementById('postDate').textContent = date;
        
        document.getElementById('likeCount').textContent = blog.likes;
        document.getElementById('commentCount').textContent = data.commentCount;
        
        updateLikeButton();
        
        const actionsDiv = document.getElementById('postActions');
        const canModify = currentUserId === blog.authorId || 
                         ['admin', 'trainer'].includes(currentUserRole?.toLowerCase());
        
        if (canModify && currentUserId === blog.authorId) {
            const editBtn = document.createElement('button');
            editBtn.className = 'action-btn edit-btn';
            editBtn.innerHTML = 'Edit';
            editBtn.onclick = () => window.location.href = `create.html?edit=${currentBlogId}`;
            actionsDiv.appendChild(editBtn);
        }
        
        if (canModify) {
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'action-btn delete-btn';
            deleteBtn.innerHTML = 'Delete';
            deleteBtn.onclick = deletePost;
            actionsDiv.appendChild(deleteBtn);
        }

    } catch (err) {
        console.error('Error loading post:', err);
        document.getElementById('postTitle').textContent = 'Error loading post';
    }
}

async function loadComments() {
    try {
        const res = await fetch(`${API_BASE}/comments/?blogId=${currentBlogId}`);
        const comments = await res.json();
        
        const list = document.getElementById('commentsList');
        document.getElementById('commentCount').textContent = comments.length;
        
        if (comments.length === 0) {
            list.innerHTML = '<div class="empty-comments">No comments yet. Be the first to comment!</div>';
            return;
        }

        list.innerHTML = comments.map(comment => {
            const initials = comment.userName.split(' ').map(n => n[0]).join('').toUpperCase();
            const time = new Date(comment.commentedAt).toLocaleDateString('en-US', {
                month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
            });
            
            const canDelete = currentUserId === comment.userId || 
                             ['admin', 'trainer'].includes(currentUserRole?.toLowerCase());
            
            return `
                <div class="comment-item" data-id="${comment.id}">
                    <div class="comment-avatar">${initials}</div>
                    <div class="comment-content">
                        <div class="comment-author">
                            ${escapeHtml(comment.userName)}
                            ${canDelete ? `<span class="comment-delete" onclick="deleteComment(${comment.id})">Delete</span>` : ''}
                        </div>
                        <div class="comment-text">${escapeHtml(comment.comment)}</div>
                        <div class="comment-time">${time}</div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('Error loading comments:', err);
    }
}

async function postComment(e) {
    e.preventDefault();
    
    if (!token) {
        alert('Please login to comment');
        window.location.href = 'login.html';
        return;
    }

    const input = document.getElementById('commentInput');
    const comment = input.value.trim();
    
    if (!comment) return;

    try {
        const res = await fetch(`${API_BASE}/comments/`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ blogId: parseInt(currentBlogId), comment })
        });

        const data = await res.json();
        
        if (res.ok && data.success) {
            input.value = '';
            loadComments();
        } else {
            throw new Error(data.error || 'Failed to post comment');
        }
    } catch (err) {
        alert(err.message);
    }
}

async function deleteComment(commentId) {
    if (!confirm('Delete this comment?')) return;

    try {
        const res = await fetch(`${API_BASE}/comments/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (res.ok) {
            loadComments();
        } else {
            const data = await res.json();
            throw new Error(data.error || 'Failed to delete comment');
        }
    } catch (err) {
        alert(err.message);
    }
}

async function toggleLike() {
    if (!token) {
        alert('Please login to like posts');
        window.location.href = 'login.html';
        return;
    }

    try {
        if (isLiked) {
            const res = await fetch(`${API_BASE}/likes/${currentBlogId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (res.ok) {
                isLiked = false;
                const count = parseInt(document.getElementById('likeCount').textContent);
                document.getElementById('likeCount').textContent = Math.max(0, count - 1);
            }
        } else {
            const res = await fetch(`${API_BASE}/likes/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ blogId: parseInt(currentBlogId) })
            });
            
            if (res.ok) {
                isLiked = true;
                const count = parseInt(document.getElementById('likeCount').textContent);
                document.getElementById('likeCount').textContent = count + 1;
            }
        }
        
        updateLikeButton();
    } catch (err) {
        console.error('Error toggling like:', err);
    }
}

function updateLikeButton() {
    const btn = document.getElementById('likeBtn');
   
    
    if (isLiked) {
        btn.classList.add('liked');
       
    } else {
        btn.classList.remove('liked');
    
    }
}

async function deletePost() {
    if (!confirm('Are you sure you want to delete this post? This action cannot be undone.')) return;

    try {
        const res = await fetch(`${API_BASE}/blogs/${currentBlogId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (res.ok) {
            alert('Post deleted successfully');
            window.location.href = 'blog.html';
        } else {
            const data = await res.json();
            throw new Error(data.error || 'Failed to delete post');
        }
    } catch (err) {
        alert(err.message);
    }
}

function showShareModal() {
    const modal = document.getElementById('shareModal');
    const link = document.getElementById('shareLink');
    link.value = window.location.href;
    modal.style.display = 'flex';
}

function hideShareModal() {
    document.getElementById('shareModal').style.display = 'none';
}

function copyShareLink() {
    const link = document.getElementById('shareLink');
    link.select();
    document.execCommand('copy');
    
    const btn = document.getElementById('copyBtn');
    btn.textContent = 'Copied!';
    setTimeout(() => btn.textContent = 'Copy', 2000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

window.deleteComment = deleteComment;
