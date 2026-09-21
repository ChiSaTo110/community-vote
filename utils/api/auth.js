import request from '../request.js'

// 用户登录
export const login = (data) => request.post('/auth/login', data)

// 用户注册
export const register = (data) => request.post('/auth/register', data)
