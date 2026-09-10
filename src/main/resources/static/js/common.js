/**
 * 投票系统 - 公共工具函数
 */

// ========== 页面加载淡入（pageshow在从bfcache后退恢复时也会触发，避免空白） ==========
window.addEventListener('pageshow', () => {
    document.body.classList.add('page-loaded');
});

// ========== 全局提示 ==========
function showToast(message, type = 'info', duration = 3000) {
    const old = document.querySelector('.toast');
    if (old) old.remove();

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
    toast.innerHTML = `<span>${icons[type] || 'ℹ'}</span><span>${message}</span>`;
    document.body.appendChild(toast);

    requestAnimationFrame(() => toast.classList.add('show'));

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// ========== 页面跳转（直接跳转，避免bfcache后退时页面空白） ==========
function navigateTo(url) {
    window.location.href = url;
}

// ========== URL参数获取 ==========
function getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

// ========== 题型映射 ==========
const TOPIC_TYPE = {
    1: { name: '单选', icon: '◉', desc: '只能选一个' },
    2: { name: '多选', icon: '☑', desc: '可选多个' },
    3: { name: '填空', icon: '✎', desc: '文字作答' }
};

// ========== 状态映射 ==========
const TOPIC_STATUS = {
    1: { text: '进行中', class: 'badge-success' },
    2: { text: '已结束', class: 'badge-secondary' }
};

// ========== 时间格式化 ==========
function formatTime(timeStr) {
    if (!timeStr) return '';
    const d = new Date(timeStr);
    if (isNaN(d.getTime())) return timeStr;
    const pad = n => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

// ========== 复制到剪贴板 ==========
function copyToClipboard(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
        return navigator.clipboard.writeText(text);
    }
    return new Promise((resolve, reject) => {
        try {
            const input = document.createElement('textarea');
            input.value = text;
            input.style.position = 'fixed';
            input.style.opacity = '0';
            document.body.appendChild(input);
            input.select();
            document.execCommand('copy');
            document.body.removeChild(input);
            resolve();
        } catch (e) { reject(e); }
    });
}

// ========== HTML转义 ==========
function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// ========== IP脱敏 ==========
function maskIp(ip) {
    if (!ip) return '';
    const parts = ip.split('.');
    if (parts.length === 4) return `${parts[0]}.*.*.${parts[3]}`;
    return ip;
}

// ========== 最近访问记录存储 ==========
const RecentStore = {
    KEY: 'recent_topics',
    MAX: 20,

    add(id, title, type) {
        try {
            const list = this.get();
            const filtered = list.filter(item => item.id !== id);
            filtered.unshift({
                id: id,
                title: title,
                type: type,
                time: Date.now()
            });
            const trimmed = filtered.slice(0, this.MAX);
            localStorage.setItem(this.KEY, JSON.stringify(trimmed));
        } catch (e) {
            console.warn('RecentStore.add 失败:', e);
        }
    },

    get() {
        try {
            const data = localStorage.getItem(this.KEY);
            return data ? JSON.parse(data) : [];
        } catch (e) {
            return [];
        }
    },

    clear() {
        localStorage.removeItem(this.KEY);
    }
};

// ========== 统一导航栏渲染 ==========
function renderNavbar(activePage = '') {
    const isLoggedIn = typeof Auth !== 'undefined' && Auth.isLoggedIn();
    const user = isLoggedIn ? Auth.getUser() : null;
    const nickname = user ? (user.nickname || user.username || '用户') : '';
    const avatar = user && user.avatar ? user.avatar : null;

    const navHtml = `
        <nav class="navbar">
            <div class="container">
                <div class="navbar-left">
                    <a href="index.html" class="navbar-brand">📊 投票大厅</a>
                </div>
                <div class="navbar-center">
                    <form class="nav-search" onsubmit="handleNavSearch(event)">
                        <input type="text" class="nav-search-input" id="navSearchInput" placeholder="搜索话题..." value="${escapeHtml(getQueryParam('keyword') || '')}">
                        <button type="submit" class="nav-search-btn">🔍</button>
                    </form>
                </div>
                <div class="navbar-right">
                    <a href="create.html" class="btn btn-primary btn-sm nav-create-btn">+ 发起投票</a>
                    ${isLoggedIn ? `
                        <div class="nav-user">
                            <a href="profile.html" class="nav-user-link">
                                ${avatar ? `<img src="${avatar}" class="nav-avatar" alt="avatar">` : `<div class="nav-avatar nav-avatar-default">${nickname.charAt(0).toUpperCase()}</div>`}
                                <span class="nav-username">${escapeHtml(nickname)}</span>
                            </a>
                            <div class="nav-dropdown">
                                <a href="profile.html" class="nav-dropdown-item">👤 个人中心</a>
                                <a href="profile.html?tab=created" class="nav-dropdown-item">📝 我发起的</a>
                                <a href="profile.html?tab=joined" class="nav-dropdown-item">🗳 我参与的</a>
                                <div class="nav-dropdown-divider"></div>
                                <a href="javascript:void(0)" class="nav-dropdown-item" onclick="handleLogout()">🚪 退出登录</a>
                            </div>
                        </div>
                    ` : `
                        <a href="login.html" class="nav-auth-link">登录</a>
                        <a href="register.html" class="btn btn-outline-primary btn-sm">注册</a>
                    `}
                </div>
            </div>
        </nav>
    `;

    const placeholder = document.getElementById('navbar-placeholder');
    if (placeholder) {
        placeholder.outerHTML = navHtml;
    } else {
        document.body.insertAdjacentHTML('afterbegin', navHtml);
    }

    if (activePage) {
        document.querySelectorAll('.navbar a').forEach(a => {
            if (a.getAttribute('href') === `${activePage}.html`) {
                a.classList.add('active');
            }
        });
    }
}

function handleNavSearch(e) {
    e.preventDefault();
    const keyword = document.getElementById('navSearchInput').value.trim();
    if (keyword) {
        navigateTo(`search.html?keyword=${encodeURIComponent(keyword)}`);
    } else {
        showToast('请输入搜索关键词', 'warning');
    }
}

function handleLogout() {
    if (typeof Auth !== 'undefined') {
        Auth.logout();
    } else {
        localStorage.removeItem('vote_token');
        localStorage.removeItem('vote_user');
    }
    showToast('已退出登录', 'success');
    setTimeout(() => navigateTo('index.html'), 800);
}

function requireLogin() {
    if (typeof Auth !== 'undefined' && !Auth.isLoggedIn()) {
        showToast('请先登录', 'warning');
        setTimeout(() => navigateTo('login.html'), 1000);
        return false;
    }
    return true;
}
