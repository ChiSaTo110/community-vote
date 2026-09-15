# 分析接口契约

基础路径：/api/analytics
跨域：@CrossOrigin(origins = "*")
认证：Spring Security 放行 /api/analytics/**

## 1. GET /user-rank
返回：
[
  {"userId":3,"userName":"张三","voteCount":210,"rankNo":1},
  ...
]

## 2. GET /daily-votes
返回：
[
  {"id":1,"voteDate":"2026-08-01","voteCount":12},
  ...
]

## 3. GET /topic-hot
返回：
[
  {"topicId":5,"topicTitle":"xxx","voteCount":300,"rankNo":1},
  ...
]

## 4. GET /type-dist
返回：
[
  {"id":1,"topicType":"单选","topicCount":6},
  ...
]

## 5. GET /sentiment
返回：
[
  {"id":1,"sentimentType":"positive","commentCount":10},
  ...
]

## 6. GET /hourly-votes
返回：
[
  {"id":1,"hourOfDay":0,"voteCount":5},
  ...
]

## 7. GET /summary
返回：
{
  "totalVotes":1521,
  "totalUsers":8,
  "totalTopics":10,
  "analysisTime":"2026-09-15T10:00:00"
}