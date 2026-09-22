<template>
  <view class="index-page">
    <!-- 搜索入口 -->
    <view class="search-entry" @click="goSearch">
      <text class="search-icon">🔍</text>
      <text class="search-text">搜索话题...</text>
    </view>

    <!-- 快捷入口 -->
    <view class="quick-actions">
      <view class="quick-card" @click="goCreate">
        <text class="quick-icon">📝</text>
        <text class="quick-label">发起投票</text>
      </view>
      <view class="quick-card" @click="goAnalytics">
        <text class="quick-icon">📊</text>
        <text class="quick-label">数据分析</text>
      </view>
    </view>

    <!-- 推荐话题标题 -->
    <view class="section-header">
      <text class="section-title">🔥 推荐话题</text>
      <text class="section-action" @click="loadTopics">换一批</text>
    </view>

    <!-- 话题列表 -->
    <view v-if="loading" class="tip">加载中...</view>
    <view v-else-if="topics.length === 0" class="empty">
      <text class="empty-icon">📭</text>
      <text class="empty-text">暂无话题</text>
    </view>

    <view v-else class="topic-list">
      <view
        v-for="topic in topics"
        :key="topic.id"
        class="topic-card"
        @click="goDetail(topic.id)"
      >
        <view class="card-top">
          <view class="avatar">{{ (topic.creatorNickname || '匿')[0] }}</view>
          <view class="card-user">
            <text class="nickname">{{ topic.creatorNickname || '匿名用户' }}</text>
            <text class="time">{{ formatTimeAgo(topic.createdAt) }}</text>
          </view>
          <text class="badge" :class="topic.type === 2 ? 'badge-multi' : ''">
            {{ topic.type === 2 ? '☑ 多选' : '◉ 单选' }}
          </text>
        </view>

        <view class="card-title">{{ topic.title }}</view>
        <view v-if="topic.description" class="card-desc">{{ topic.description }}</view>

        <view class="card-stats">
          <text class="stat">👥 {{ topic.voteCount || 0 }} 人参与</text>
          <text class="stat">💬 {{ topic.commentCount || 0 }} 评论</text>
          <text class="stat">📋 {{ topic.optionCount || 0 }} 选项</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getHotTopics } from '@/utils/api/topic'

const topics = ref([])
const loading = ref(false)

const loadTopics = async () => {
  loading.value = true
  try {
    const res = await getHotTopics()
    topics.value = Array.isArray(res) ? res : (res.records || res.list || [])
  } catch (e) {
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const goDetail = (id) => uni.navigateTo({ url: `/pages/topic/detail?id=${id}` })
const goSearch = () => uni.navigateTo({ url: '/pages/search/search' })
const goCreate = () => uni.navigateTo({ url: '/pages/create/create' })
const goAnalytics = () => uni.showToast({ title: '数据分析大屏在 Web 端', icon: 'none' })

const formatTimeAgo = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  if (isNaN(d.getTime())) return ''
  const diff = Date.now() - d.getTime()
  const min = 60 * 1000, hour = 60 * min, day = 24 * hour
  if (diff < min) return '刚刚'
  if (diff < hour) return Math.floor(diff / min) + '分钟前'
  if (diff < day) return Math.floor(diff / hour) + '小时前'
  if (diff < 30 * day) return Math.floor(diff / day) + '天前'
  return timeStr.slice(0, 10)
}

onMounted(loadTopics)
onPullDownRefresh(loadTopics)
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.index-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }

.search-entry {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 24rpx 30rpx;
  border-radius: 44rpx;
  margin-bottom: 24rpx;
  box-shadow: $card-shadow;
}
.search-icon { font-size: 30rpx; margin-right: 16rpx; }
.search-text { font-size: 28rpx; color: $text-placeholder; }

.quick-actions {
  display: flex;
  gap: 20rpx;
  margin-bottom: 24rpx;
}
.quick-card {
  flex: 1;
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.quick-icon { font-size: 48rpx; }
.quick-label { font-size: 26rpx; color: $text-main; font-weight: 500; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 8rpx;
}
.section-title { font-size: 32rpx; font-weight: bold; color: $text-main; }
.section-action { font-size: 26rpx; color: $primary; }

.tip, .empty { text-align: center; padding: 80rpx 0; color: $text-light; }
.empty-icon { font-size: 100rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 28rpx; }

.topic-list { display: flex; flex-direction: column; gap: 20rpx; }

.topic-card {
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 28rpx;
}

.card-top {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}
.card-user { flex: 1; margin-left: 16rpx; }
.nickname { font-size: 26rpx; color: $text-sub; display: block; }
.time { font-size: 22rpx; color: $text-light; display: block; margin-top: 4rpx; }

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: $text-main;
  margin-bottom: 12rpx;
  line-height: 1.4;
}
.card-desc {
  font-size: 26rpx;
  color: $text-sub;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

.card-stats {
  display: flex;
  gap: 32rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f2f2f2;
}
.stat { font-size: 24rpx; color: $text-light; }
</style>