import request from '../request.js'

// 评论列表
export const getCommentList = (params) => request.get('/comment/list', params)

// 发布评论
export const addComment = (data) => request.post('/comment/add', data)
