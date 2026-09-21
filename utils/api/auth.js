import request from '../request.js'

// 登录
export const login = (data) => request.post('/auth/login', data)

// 注册
export const register = (data) => request.post('/auth/register', data)
