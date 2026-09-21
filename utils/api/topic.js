import request from '@/utils/request'

// 话题列表（分页）
export const getTopicList = (params) => request.get('/topics', params)

// 热门话题
export const getHotTopics = (params) => request.get('/topics/hot', params)

// 搜索话题
export const searchTopic = (params) => request.get('/topics/search', params)

// 话题详情
export const getTopicDetail = (id) => request.get(`/topics/${id}`)
