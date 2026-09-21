import request from '../request.js'

// 提交投票
export const submitVote = (data) => request.post('/vote/submit', data)

// 获取投票结果
export const getVoteResult = (topicId) => request.get(`/vote/result/${topicId}`)
