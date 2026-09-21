const BASE_URL = '/api'
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
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error('登录已过期，请重新登录'))
          return
        }

        // HTTP 状态码非 2xx
        if (statusCode < 200 || statusCode >= 300) {
          uni.showToast({
            title: data?.msg || data?.message || '请求失败',
            icon: 'none',
            duration: 2000
          })
          reject(new Error(data?.msg || '请求失败'))
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
      fail: () => {
        uni.showToast({
          title: '网络连接异常',
          icon: 'none',
          duration: 2000
        })
        reject(new Error('网络连接异常'))
      }
    })
  })
}

request.get = (url, data, options = {}) => request({ url, method: 'GET', data, ...options })
request.post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
request.put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
request.delete = (url, data, options = {}) => request({ url, method: 'DELETE', data, ...options })

export default request
