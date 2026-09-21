import request from '../request.js'

// 获取个人信息
export const getUserInfo = () => request.get('/users/me')

// 我的话题列表
export const getMyTopics = (params) => request.get('/users/me/topics', params)

// 我的投票记录
export const getMyVotes = (params) => request.get('/users/me/votes', params)
