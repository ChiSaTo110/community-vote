import request from '@/utils/request'

export const getCommentList = (topicId) => request.get(`/comments/${topicId}`)

export const addComment = (topicId, content) =>
  request.post(`/comments?topicId=${topicId}&content=${encodeURIComponent(content)}`)