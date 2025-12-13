const API_BASE = '/smart-health-track/api';
let token = localStorage.getItem('sha_token') || null;
let currentCategory = '';
// Default featured articles by category (shown if API returns empty)
const DEFAULT_FEATURED = [
    {
        id: 'featured-1',
        title: 'Top 10 Nutrition Tips for Better Health',
        content: 'Discover essential nutrition guidelines to improve your overall health and wellness. Learn about balanced diets, macronutrients, and healthy eating habits...',
        category: 'Nutrition',
        authorName: 'Dr. Sarah Johnson',
        authorEmail: 'sarah@smarthealth.com',
        authorRole: 'trainer',
        createdAt: new Date().toISOString(),
        likes: 245
    },
    {
        id: 'featured-2',
        title: 'Effective Home Workout Routines',
        content: 'Stay fit at home with these scientifically-proven workout routines. No equipment needed, just dedication and 30 minutes a day...',
        category: 'Fitness',
        authorName: 'Coach Michael',
        authorEmail: 'michael@smarthealth.com',
        authorRole: 'trainer',
        createdAt: new Date().toISOString(),
        likes: 198
    },
    {
        id: 'featured-3',
        title: 'Mental Health Awareness and Self-Care',
        content: 'Mental wellness is just as important as physical health. Learn mindfulness techniques, stress management, and when to seek professional help...',
        category: 'Mental Health',
        authorName: 'Dr. Emily Chen',
        authorEmail: 'emily@smarthealth.com',
        authorRole: 'trainer',
        createdAt: new Date().toISOString(),
        likes: 312
    },
    {
        id: 'featured-4',
        title: 'Quick Health Tips for Busy Professionals',
        content: 'Maintain your health even with a hectic schedule. Quick tips, meal prep ideas, and time-efficient exercises for professionals...',
        category: 'Tips',
        authorName: 'Wellness Team',
        authorEmail: 'team@smarthealth.com',
        authorRole: 'admin',
        createdAt: new Date().toISOString(),
        likes: 156
    },
    {
        id: 'featured-5',
        title: 'General Wellness: A Holistic Approach',
        content: 'Achieve overall wellness by balancing nutrition, exercise, sleep, and mental health. A comprehensive guide to sustainable healthy living through this',
        category: 'General',
        authorName: 'Health Expert',
        authorEmail: 'expert@smarthealth.com',
        authorRole: 'admin',
        createdAt: new Date().toISOString(),
        likes: 289
    }
];

// Default posts by category (shown if API returns empty for a category)
const DEFAULT_POSTS_BY_CATEGORY = {
    'Nutrition': [
        {
            id: 'post-n1',
            title: 'Hydration: The Key to Healthy Living',
            content: 'Water is essential for every function in your body. Learn how much water you should drink daily and the benefits of staying hydrated',
            category: 'Nutrition',
            authorName: 'Nutritionist Amy',
            authorEmail: 'amy@smarthealth.com',
            authorRole: 'trainer',
            createdAt: new Date().toISOString(),
            likes: 87
        }
    ],
    'Fitness': [
        {
            id: 'post-f1',
            title: 'Strength Training for Beginners',
            content: 'Start your strength training journey with these beginner-friendly exercises. Build muscle, increase metabolism, and boost confidence...',
            category: 'Fitness',
            authorName: 'Trainer John',
            authorEmail: 'john@smarthealth.com',
            authorRole: 'trainer',
            createdAt: new Date().toISOString(),
            likes: 134
        }
    ],
    'Mental Health': [
        {
            id: 'post-m1',
            title: 'Understanding Stress and Anxiety',
            content: 'Learn the difference between normal stress and anxiety disorders. Practical coping strategies and when to seek professional support...',
            category: 'Mental Health',
            authorName: 'Counselor Lisa',
            authorEmail: 'lisa@smarthealth.com',
            authorRole: 'trainer',
            createdAt: new Date().toISOString(),
            likes: 156
        }
    ],
    'Tips': [
        {
            id: 'post-t1',
            title: '5-Minute Morning Routine for Energy',
            content: 'Start your day right with this quick 5-minute routine that boosts energy and sets a positive tone for the day...',
            category: 'Tips',
            authorName: 'Life Coach',
            authorEmail: 'coach@smarthealth.com',
            authorRole: 'admin',
            createdAt: new Date().toISOString(),
            likes: 203
        }
    ],
    'General': [
        {
            id: 'post-g1',
            title: 'Welcome to SmartHealth Blog',
            content: 'Your trusted source for health, fitness, and wellness information. Explore articles from certified professionals and health experts...',
            category: 'General',
            authorName: 'SmartHealth',
            authorEmail: 'info@smarthealth.com',
            authorRole: 'admin',
            createdAt: new Date().toISOString(),
            likes: 412
        }
    ]
};
// safe date parser: returns Date object or null
function safeParseDate(dateString) {
    if (!dateString) return null;
    try {
        const d = new Date(dateString);
        if (isNaN(d.getTime())) return null;
        return d;
    } catch (e) {
        return null;
    }
}

// format date as "Dec 11, 2025"
function formatDate(dateString) {
    const d = safeParseDate(dateString);
    if (!d) return 'Unknown date';
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// format datetime as "Dec 11, 2025, 10:30 AM"
function formatDateTime(dateString) {
    const d = safeParseDate(dateString);
    if (!d) return 'Unknown date';
    return d.toLocaleString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

document.addEventListener('DOMContentLoaded', () => {
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    loadFeaturedPosts();
    loadAllPosts();
    setupCategoryTabs();
    setupCreateButton();
    setupLogout();
});

function setupCategoryTabs() {
    const tabs = document.querySelectorAll('.category-tab');
    if (!tabs || tabs.length === 0) return;

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            currentCategory = tab.dataset.category || '';
            loadAllPosts(currentCategory);
        });
    });
}

function setupCreateButton() {
    const btn = document.getElementById('createBtn');
    if (btn) {
        btn.addEventListener('click', () => {
            window.location.href = 'create.html';
        });
    }
}

function setupLogout() {
    const btn = document.getElementById('logoutBtn');
    if (btn) {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('sha_token');
            window.location.href = 'login.html';
        });
    }
}

async function loadFeaturedPosts() {
   

   let featured = DEFAULT_FEATURED;
    if (currentCategory && currentCategory.trim()) {
        featured = DEFAULT_FEATURED.filter(blog => blog.category === currentCategory);
    }
    displayFeaturedPosts(DEFAULT_FEATURED);
}
function displayFeaturedPosts(blogs) {
    const grid = document.getElementById('featuredGrid');
    if (!grid) {
        console.warn('featuredGrid element not found');
        return;
    }

    if (!blogs || blogs.length === 0) {
        grid.innerHTML = '<div class="empty-state">No featured articles available</div>';
        return;
    }

    grid.innerHTML = blogs.map(blog => createBlogCard(blog, true)).join('');
    addCardListeners();
}

async function loadAllPosts(category = '') {
    try {
        let url = `${API_BASE}/blogs`;
        if (category && category.trim()) {
            url += `?category=${encodeURIComponent(category)}`;
        }

        const res = await fetch(url);

        if (!res.ok) {
            console.warn('Posts load failed:', res.status);
            return;
        }

        const blogs = await res.json();
        const grid = document.getElementById('postsGrid');

        if (!grid) return;

        if (!blogs || blogs.length === 0) {
            grid.innerHTML = '<div class="empty-state">No posts in this category yet. Be the first to share!</div>';
            return;
        }

        grid.innerHTML = blogs.map(blog => createBlogCard(blog, false)).join('');
        addCardListeners();
    } catch (err) {
        console.error('Error loading posts:', err);
        const grid = document.getElementById('postsGrid');
        if (grid) grid.innerHTML = '<div class="empty-state">Error loading posts</div>';
    }
}

function createBlogCard(blog, isFeatured) {
    // safe defaults for missing fields
    const authorName = blog.authorName || blog.authorEmail || 'Unknown';
    const initials = (authorName)
        .split(' ')
        .map(n => n && n[0] ? n[0].toUpperCase() : '')
        .join('')
        .slice(0, 2) || 'U';

    const excerpt = blog.content
        ? (blog.content.length > 120 ? blog.content.substring(0, 120) + '...' : blog.content)
        : '(no content)';

    const date = formatDate(blog.createdAt);
    const category = blog.category || 'General';
    const likes = (blog.likes || 0);
    if (isFeatured) {
        // lightweight card for featured items: only category, title and full content
        return `
            <div class="blog-card featured-card" data-id="${escapeHtmlAttr(blog.id)}">
                <div class="blog-card-content">
                    <span class="blog-card-category">${escapeHtml(category)}</span>
                    <h3 class="blog-card-title">${escapeHtml(blog.title)}</h3>
                    <p class="blog-card-excerpt">${escapeHtml(blog.content)}</p>
                </div>
            </div>
        `;
    }

    // check if author is verified (admin or trainer)
    const isVerified = blog.authorRole && ['admin', 'trainer'].includes(blog.authorRole.toLowerCase());
    const verifiedBadge = isVerified ? '<span class="verified-badge">✓</span>' : '';

   

    return `
        <div class="blog-card" data-id="${escapeHtmlAttr(blog.id)}">
         
            <div class="blog-card-content">
                <span class="blog-card-category">${escapeHtml(category)}</span>
                <h3 class="blog-card-title">${escapeHtml(blog.title || '(no title)')}</h3>
                <p class="blog-card-excerpt">${escapeHtml(excerpt)}</p>
                <div class="blog-card-meta">
                    <div class="blog-card-author">
                        <div class="author-avatar">${escapeHtml(initials)}</div>
                        <span>${escapeHtml(authorName)}${verifiedBadge}</span>
                    </div>
                    <div class="blog-card-stats">
                        <div class="stat-item"> ${likes} Likes</div>
                        <div class="stat-item">${date}</div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function addCardListeners() {
    document.querySelectorAll('.blog-card:not(.featured-card)').forEach(card => {
        if (card._clickHandler) card.removeEventListener('click', card._clickHandler);

        const handler = () => {
            const id = card.dataset.id;
            if (!id) return;
            // if this is a featured card, add featured=true so post.js can render minimal view
            const isFeaturedCard = card.classList.contains('featured-card');
            const url = isFeaturedCard
                ? `post.html?id=${encodeURIComponent(id)}&featured=true`
                : `post.html?id=${encodeURIComponent(id)}`;
            window.location.href = url;
        };

        card._clickHandler = handler;
        card.addEventListener('click', handler);
    });
}

// escape HTML text content
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = String(text);
    return div.innerHTML;
}

// escape HTML attribute values
function escapeHtmlAttr(attr) {
    if (!attr) return '';
    return String(attr).replace(/"/g, '&quot;').replace(/'/g, '&#39;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}