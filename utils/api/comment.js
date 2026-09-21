import request from '../request.js'

// 评论列表
export const getCommentList = (topicId) => request.get(`/comments/${topicId}`)

// 发布评论（后端用 RequestParam，参数拼在 URL 上）
export const addComment = (topicId, content) =>
  request.post(`/comments?topicId=${topicId}&content=${encodeURIComponent(content)}`)
