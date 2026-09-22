# Spark 分析运行说明

## 环境
- Spark 3.5.3（C:\spark\spark-3.5.3-bin-hadoop3）
- 运行前切到 JDK8
- MySQL 密码：317916

## MySQL 连接串
jdbc:mysql://localhost:3306/vote_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

## 运行方式
spark-shell 或 spark-submit

## 6 个分析指标
| 序号 | 表名 | 业务含义 |
|---|---|---|
| 1 | ana_user_rank | 谁投票最多（含发起话题、评论数） |
| 2 | ana_daily_votes | 每天投票量和参与人数 |
| 3 | ana_topic_hot | 最热门的话题（含热度分） |
| 4 | ana_type_dist | 各类型话题的数量和票数 |
| 5 | ana_sentiment | 评论情感分布（正/中/负） |
| 6 | ana_hourly_votes | 什么时段投票最多 |

## 写回 MySQL
df.write.mode("overwrite").jdbc(url, "ana_xxx", props)