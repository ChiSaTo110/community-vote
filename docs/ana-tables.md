# ana_* 分析结果表结构

## 1. ana_user_rank（用户活跃度）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| nickname | varchar(50) | 昵称 |
| vote_count | int | 投票数 |
| topic_count | int | 发起话题数 |
| comment_count | int | 评论数 |
| rank_no | int | 排名 |

## 2. ana_daily_votes（每日投票）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| day | varchar(20) | 日期 |
| vote_count | int | 当日票数 |
| user_count | int | 当日参与人数 |

## 3. ana_topic_hot（热门话题）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| topic_id | bigint | 话题ID |
| title | varchar(200) | 标题 |
| vote_count | int | 票数 |
| comment_count | int | 评论数 |
| heat_score | int | 热度分 |
| rank_no | int | 排名 |

## 4. ana_type_dist（类型分布）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| type | int | 类型编码 |
| type_name | varchar(20) | 类型名 |
| topic_count | int | 话题数 |
| vote_count | int | 总票数 |

## 5. ana_sentiment（情感分布）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| sentiment | varchar(20) | 情感（positive/neutral/negative） |
| count | int | 评论数 |

## 6. ana_hourly_votes（时段分布）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | bigint | 主键 |
| hour | int | 小时 0-23 |
| vote_count | int | 票数 |