// #ifdef H5
const BASE_URL = '/api'
// #endif

// #ifndef H5
const BASE_URL = 'http://localhost:8080/api'
// #endif
const TIMEOUT = 10000

const request = (options) => {
  return new Promise((resolve, reject) => {
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }
    const token = uni.getStorageSync('token')
    if (token) {
      header.Authorization = `Bearer ${token}`
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      timeout: TIMEOUT,
      success: (res) => {
        const { statusCode, data } = res

        // 401: token 失效，清除登录态并跳转
        if (statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')   // 新增，避免 userInfo 残留
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error('登录已过期，请重新登录'))
          return
        }

        // HTTP 状态码非 2xx
        if (statusCode < 200 || statusCode >= 300) {
          const httpMsg = data?.msg || data?.message || `请求失败 (HTTP ${statusCode})`
          uni.showToast({
            title: httpMsg,
            icon: 'none',
            duration: 2000
          })
          reject(new Error(httpMsg))
          return
        }

        // 后端统一返回格式: { code: 200, msg: "...", data: ... }
        if (data && typeof data === 'object' && 'code' in data) {
          if (data.code !== 200) {
            uni.showToast({
              title: data.msg || '操作失败',
              icon: 'none',
              duration: 2000
            })
            reject(new Error(data.msg || '操作失败'))
            return
          }
          // 业务成功，解包 data 字段
          resolve(data.data)
          return
        }

        // 兜底：直接返回原始数据
        resolve(data)
      },
      fail: (err) => {
        const detail = err && err.errMsg ? err.errMsg : ''
        const isRefused = detail.indexOf('refuse') >= 0 || detail.indexOf('Failed to fetch') >= 0
        const msg = isRefused
          ? '后端服务未启动，请检查 localhost:8080'
          : '网络连接异常'
        uni.showToast({
          title: msg,
          icon: 'none',
          duration: 2500
        })
        reject(new Error(msg))
      }
    })
  })
}

request.get = (url, data, options = {}) => request({ url, method: 'GET', data, ...options })
request.post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
request.put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
request.delete = (url, data, options = {}) => request({ url, method: 'DELETE', data, ...options })

export default request
