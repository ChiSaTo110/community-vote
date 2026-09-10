/**
 * 统一 API 封装
 * - 自动在请求头添加 Authorization: Bearer <token>
 * - 统一响应处理和错误提示
 * - 401 自动跳转登录页
 */

const TOKEN_KEY = 'vote_token';
const USER_KEY = 'vote_user';

// 创建 axios 实例
const api = axios.create({
    baseURL: '',
    timeout: 15000
});

// 请求拦截器：自动添加 Token
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem(TOKEN_KEY);
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

// 响应拦截器：统一处理
api.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response) {
            if (error.response.status === 401) {
                localStorage.removeItem(TOKEN_KEY);
                localStorage.removeItem(USER_KEY);
                if (!window.location.pathname.endsWith('login.html') &&
                    !window.location.pathname.endsWith('register.html')) {
                    showToast('登录已过期，请重新登录', 'warning');
                    setTimeout(() => navigateTo('login.html'), 1000);
                }
            }
        }
        return Promise.reject(error);
    }
);

// ========== 认证相关 ==========
const AuthAPI = {
    // 登录 POST /api/auth/login
    login(username, password) {
        return api.post('/api/auth/login', { username, password });
    },
    // 注册 POST /api/auth/register
    register(username, password, nickname, email) {
        return api.post('/api/auth/register', { username, password, nickname, email });
    },
    // 获取当前用户信息 GET /api/users/me
    getCurrentUser() {
        return api.get('/api/users/me');
    },
    // 退出登录
    logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }
};

// ========== 用户相关 ==========
const UserAPI = {
    // 获取当前用户信息 GET /api/users/me
    getProfile() {
        return api.get('/api/users/me');
    },
    // 更新用户信息 PUT /api/users/me
    updateProfile(data) {
        return api.put('/api/users/me', data);
    },
    // 我发起的话题 GET /api/users/me/topics
    getMyTopics() {
        return api.get('/api/users/me/topics');
    },
    // 我参与的话题 GET /api/users/me/votes
    getMyVotes() {
        return api.get('/api/users/me/votes');
    }
};

// ========== 话题相关 ==========
const TopicAPI = {
    // 随机话题列表 GET /api/topics/random
    getRandom() {
        return api.get('/api/topics/random');
    },
    // 热门话题 GET /api/topics/hot
    getHot() {
        return api.get('/api/topics/hot');
    },
    // 搜索话题 GET /api/topics/search?keyword=xxx
    search(keyword) {
        return api.get(`/api/topics/search?keyword=${encodeURIComponent(keyword)}`);
    },
    // 话题详情 GET /api/topics/{id}
    getDetail(id) {
        return api.get(`/api/topics/${id}`);
    },
    // 创建话题 POST /api/topics
    create(data) {
        return api.post('/api/topics', data);
    },
    // 我发起的话题（兼容调用，实际走UserAPI）
    getMyCreated() {
        return UserAPI.getMyTopics();
    },
    // 我参与的话题（兼容调用，实际走UserAPI）
    getMyJoined() {
        return UserAPI.getMyVotes();
    }
};

// ========== 投票相关 ==========
const VoteAPI = {
    // 提交投票 POST /api/vote
    submit(data) {
        return api.post('/api/vote', data);
    },
    // 投票结果 GET /api/topics/{id}/results
    getResults(id) {
        return api.get(`/api/topics/${id}/results`);
    }
};

// ========== 评论相关 ==========
const CommentAPI = {
    // 评论列表 GET /api/comments/{topicId}
    getList(topicId) {
        return api.get(`/api/comments/${topicId}`);
    },
    // 发表评论 POST /api/comments (form参数)
    add(topicId, content) {
        return api.post(`/api/comments?topicId=${topicId}&content=${encodeURIComponent(content)}`);
    }
};

// ========== AI 相关 ==========
const AiAPI = {
    // AI报告 GET /api/ai/report/{id}
    getReport(id) {
        return api.get(`/api/ai/report/${id}`, { responseType: 'text' });
    }
};

// ========== 登录状态管理 ==========
const Auth = {
    // 保存登录信息
    setLogin(token, user) {
        localStorage.setItem(TOKEN_KEY, token);
        if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    // 获取Token
    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },
    // 获取用户信息
    getUser() {
        try {
            return JSON.parse(localStorage.getItem(USER_KEY)) || null;
        } catch (e) {
            return null;
        }
    },
    // 更新用户信息
    setUser(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    // 是否已登录
    isLoggedIn() {
        return !!localStorage.getItem(TOKEN_KEY);
    },
    // 退出登录
    logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }
};
