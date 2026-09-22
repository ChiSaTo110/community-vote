import java.util.Properties

// MySQL 连接配置
val mysqlUrl = "jdbc:mysql://localhost:3306/vote_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
val props = new Properties()
props.setProperty("user", "root")
props.setProperty("password", "317916")
props.setProperty("driver", "com.mysql.cj.jdbc.Driver")

// 读取 4 张核心表
val voteRecordDF = spark.read.jdbc(mysqlUrl, "vote_record", props)
val topicDF = spark.read.jdbc(mysqlUrl, "topic", props)
val userDF = spark.read.jdbc(mysqlUrl, "user", props)
val commentDF = spark.read.jdbc(mysqlUrl, "comment", props)

voteRecordDF.createOrReplaceTempView("vote_record")
topicDF.createOrReplaceTempView("topic")
userDF.createOrReplaceTempView("user")
commentDF.createOrReplaceTempView("comment")

println("========== 数据加载完成 ==========")

// ========== 分析1：用户活跃度 TOP10 ==========
val userRankDF = spark.sql("""
    SELECT u.id AS user_id, u.nickname, COUNT(v.id) AS vote_count
    FROM user u
    LEFT JOIN vote_record v ON u.id = v.user_id
    GROUP BY u.id, u.nickname
    ORDER BY vote_count DESC
    LIMIT 10
""")
userRankDF.write.mode("append").jdbc(mysqlUrl, "ana_user_rank", props)
println("✅ 分析1完成：用户活跃度")

// ========== 分析2：每日投票量趋势 ==========
val dailyVotesDF = spark.sql("""
    SELECT SUBSTR(created_at, 1, 10) AS day, COUNT(*) AS vote_count
    FROM vote_record
    GROUP BY SUBSTR(created_at, 1, 10)
    ORDER BY day
""")
val dailyVotesInsertDF = dailyVotesDF.selectExpr("day", "vote_count", "0 as user_count")
dailyVotesInsertDF.write.mode("append").jdbc(mysqlUrl, "ana_daily_votes", props)
println("✅ 分析2完成：每日投票量")

// ========== 分析3：热门话题 TOP10 ==========
val hotTopicsDF = spark.sql("""
    SELECT t.id AS topic_id, t.title,
           COUNT(DISTINCT v.id) AS vote_count,
           COUNT(DISTINCT c.id) AS comment_count,
           COUNT(DISTINCT v.id) + COUNT(DISTINCT c.id) * 2 AS heat_score
    FROM topic t
    LEFT JOIN vote_record v ON t.id = v.topic_id
    LEFT JOIN comment c ON t.id = c.topic_id
    GROUP BY t.id, t.title
    ORDER BY heat_score DESC
    LIMIT 10
""")
val hotTopicsInsertDF = hotTopicsDF.selectExpr("topic_id", "title", "vote_count", "comment_count", "heat_score", "0 as rank_no")
hotTopicsInsertDF.write.mode("append").jdbc(mysqlUrl, "ana_topic_hot", props)
println("✅ 分析3完成：热门话题")

// ========== 分析4：话题类型分布 ==========
val typeDistDF = spark.sql("""
    SELECT t.type,
           CASE t.type WHEN 1 THEN '单选题' WHEN 2 THEN '多选题' WHEN 3 THEN '填空题' END AS type_name,
           COUNT(DISTINCT t.id) AS topic_count,
           COUNT(v.id) AS vote_count
    FROM topic t
    LEFT JOIN vote_record v ON t.id = v.topic_id
    GROUP BY t.type
    ORDER BY t.type
""")
typeDistDF.write.mode("append").jdbc(mysqlUrl, "ana_type_dist", props)
println("✅ 分析4完成：话题类型分布")

// ========== 分析5：评论情感分布 ==========
val sentimentDF = spark.sql("""
    SELECT sentiment, COUNT(*) AS `count`
    FROM comment
    GROUP BY sentiment
""")
sentimentDF.write.mode("append").jdbc(mysqlUrl, "ana_sentiment", props)
println("✅ 分析5完成：评论情感分布")

// ========== 分析6：投票时段分布 ==========
val hourlyVotesDF = spark.sql("""
    SELECT CAST(SUBSTR(created_at, 12, 2) AS INT) AS hour, COUNT(*) AS vote_count
    FROM vote_record
    GROUP BY SUBSTR(created_at, 12, 2)
    ORDER BY hour
""")
hourlyVotesDF.write.mode("append").jdbc(mysqlUrl, "ana_hourly_votes", props)
println("✅ 分析6完成：投票时段分布")

println("========== ✅ 所有分析完成，结果已写入 MySQL ==========")