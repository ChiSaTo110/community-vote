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
    timeout: 35000  // AI 报告较慢，设置 35 秒
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

// ========== 评论相关（修改点：适配后端统一返回格式） ==========
const CommentAPI = {
    // 评论列表 GET /api/comments/{topicId}
    // 后端返回：{code:200, data:[...]} → 提取 data 数组
    getList(topicId) {
        return api.get(`/api/comments/${topicId}`).then(res => {
            if (res && res.code === 200 && Array.isArray(res.data)) {
                return res.data;
            }
            return [];
        });
    },
    // 发表评论 POST /api/comments (form参数)
    // 后端返回：{code:200, data:{...}} → 提取 data 对象
    add(topicId, content) {
        return api.post(`/api/comments?topicId=${topicId}&content=${encodeURIComponent(content)}`)
            .then(res => {
                if (res && res.code === 200) {
                    return res.data;
                }
                throw new Error(res && res.msg ? res.msg : '评论失败');
            });
    }
};

// ========== AI 相关（修改点：适配后端统一返回格式） ==========
const AiAPI = {
    // AI报告 GET /api/ai/report/{id}
    // 后端返回：{code:200, data:"报告文本"} → 提取 data 文本
    getReport(id) {
        return api.get(`/api/ai/report/${id}`).then(res => {
            if (res && res.code === 200 && res.data) {
                return res.data;
            }
            throw new Error(res && res.msg ? res.msg : 'AI报告生成失败');
        });
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