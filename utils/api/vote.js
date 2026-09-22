import request from '../request.js'

// 提交投票
export const submitVote = (data) => request.post('/vote', data)

// 投票结果
export const getVoteResult = (topicId) => request.get(`/topics/${topicId}/results`)
