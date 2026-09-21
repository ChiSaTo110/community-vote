import { defineStore } from 'pinia'

// 本地存储的 key
// token 必须和 utils/request.js 里读取的 key 保持一致，否则登录后请求仍不带 Authorization
const TOKEN_KEY = 'token'
const USER_INFO_KEY = 'userInfo'

/**
 * 读取本地存储，读取失败或为空时返回兜底值
 * 小程序端不存在的 key 会返回空字符串，这里统一兜底避免 state 被污染
 */
function readStorage(key, fallback) {
  try {
    const val = uni.getStorageSync(key)
    if (val === '' || val === null || val === undefined) return fallback
    return val
  } catch (e) {
    return fallback
  }
}

export const useUserStore = defineStore('user', {
  // ------------------------------------------------------------------
  // state：初始化时直接从本地存储恢复，保证刷新页面登录状态不丢失
  // ------------------------------------------------------------------
  state: () => ({
    token: readStorage(TOKEN_KEY, ''),
    userInfo: readStorage(USER_INFO_KEY, null)
  }),

  getters: {
    // 有 token 即视为已登录
    isLogin: (state) => !!state.token,
    // 常用字段快捷访问，页面里省去判空
    userId: (state) => (state.userInfo && state.userInfo.id) || null,
    nickname: (state) => (state.userInfo && state.userInfo.nickname) || '',
    avatar: (state) => (state.userInfo && state.userInfo.avatar) || ''
  },

  actions: {
    /**
     * 设置 token
     * @param {String} token 传空值时视为清除
     */
    setToken(token) {
      this.token = token || ''
      try {
        if (this.token) {
          uni.setStorageSync(TOKEN_KEY, this.token)
        } else {
          uni.removeStorageSync(TOKEN_KEY)
        }
      } catch (e) {
        // 存储失败不阻塞内存态，页面仍可正常使用
      }
    },

    /**
     * 设置用户信息
     * @param {Object} info 传空值时视为清除
     */
    setUserInfo(info) {
      this.userInfo = info || null
      try {
        if (this.userInfo) {
          uni.setStorageSync(USER_INFO_KEY, this.userInfo)
        } else {
          uni.removeStorageSync(USER_INFO_KEY)
        }
      } catch (e) {}
    },

    /**
     * 登录成功后一次性写入 token + 用户信息
     * 登录页可直接调用这个，比分开调用更省事
     * @param {Object} payload { token, userInfo }
     */
    setLoginData(payload = {}) {
      const { token, userInfo } = payload
      if (token) this.setToken(token)
      if (userInfo) this.setUserInfo(userInfo)
    },

    /**
     * 退出登录：同时清空内存态和本地存储
     */
    logout() {
      this.token = ''
      this.userInfo = null
      try {
        uni.removeStorageSync(TOKEN_KEY)
        uni.removeStorageSync(USER_INFO_KEY)
      } catch (e) {}
    }
  }
})
