import request from '@/utils/request'

// 获取当前用户信息
export const getProfile = () => request.get('/users/me')

// 更新用户信息
export const updateProfile = (data) => request.put('/users/me', data)

// 我的话题
export const getMyTopics = () => request.get('/users/me/topics')

// 我的投票
export const getMyVotes = () => request.get('/users/me/votes')