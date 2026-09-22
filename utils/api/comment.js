import request from '@/utils/request'

// 评论列表（后端: GET /api/comments/{topicId}）
export const getCommentList = (topicId) => request.get(`/comments/${topicId}`)

// 发布评论（后端: POST /api/comments）
export const addComment = (topicId, content) => request.post('/comments', {
  topicId,
  content
})