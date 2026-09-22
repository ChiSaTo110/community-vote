<template>
  <view class="search-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="input-wrap">
        <text class="icon">🔍</text>
        <input
          v-model="keyword"
          class="search-input"
          placeholder="搜索投票话题"
          confirm-type="search"
          @confirm="doSearch"
        />
        <text v-if="keyword" class="clear-btn" @click="clearKeyword">✕</text>
      </view>
      <button class="search-btn" @click="doSearch">搜索</button>
    </view>

    <!-- 结果统计 -->
    <view v-if="searched && !loading" class="result-count">
      共找到 <text class="count-num">{{ topicList.length }}</text> 个相关话题
    </view>

    <!-- 搜索结果 -->
    <view v-if="searched" class="result-area">
      <view v-if="loading" class="loading-text">搜索中...</view>
      <view v-else-if="topicList.length === 0" class="empty-text">
        <text class="empty-icon">🔍</text>
        <text>没有找到相关话题</text>
      </view>

      <view v-else class="topic-list">
        <view
          v-for="item in topicList"
          :key="item.id"
          class="topic-card"
          @click="goDetail(item.id)"
        >
          <view class="card-top">
            <view class="avatar">{{ (item.creatorNickname || '匿')[0] }}</view>
            <view class="card-user">
              <text class="nickname">{{ item.creatorNickname || '匿名用户' }}</text>
              <text class="time">{{ formatTimeAgo(item.createdAt) }}</text>
            </view>
            <text class="badge" :class="item.type === 2 ? 'badge-multi' : ''">
              {{ item.type === 2 ? '☑ 多选' : '◉ 单选' }}
            </text>
          </view>
          <view class="card-title">{{ item.title }}</view>
          <view v-if="item.description" class="card-desc">{{ item.description }}</view>
          <view class="card-stats">
            <text class="stat">👥 {{ item.voteCount || 0 }} 人参与</text>
            <text class="stat">💬 {{ item.commentCount || 0 }} 评论</text>
            <text class="stat">📋 {{ item.optionCount || 0 }} 选项</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 初始提示 -->
    <view v-else class="hint-area">
      <text class="hint-icon">🔍</text>
      <text class="hint-text">输入关键词搜索投票话题</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { searchTopic } from '@/utils/api/topic'

const keyword = ref('')
const topicList = ref([])
const loading = ref(false)
const searched = ref(false)

const doSearch = () => {
  const kw = keyword.value.trim()
  if (!kw) return uni.showToast({ title: '请输入关键词', icon: 'none' })
  loading.value = true
  searched.value = true
  searchTopic({ keyword: kw })
    .then(res => { topicList.value = res || [] })
    .catch(() => { topicList.value = [] })
    .finally(() => { loading.value = false })
}

const clearKeyword = () => {
  keyword.value = ''
  topicList.value = []
  searched.value = false
}

const goDetail = (id) => uni.navigateTo({ url: `/pages/topic/detail?id=${id}` })

const formatTimeAgo = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  if (isNaN(d.getTime())) return ''
  const diff = Date.now() - d.getTime()
  const min = 60 * 1000, hour = 60 * min, day = 24 * hour
  if (diff < min) return '刚刚'
  if (diff < hour) return Math.floor(diff / min) + '分钟前'
  if (diff < day) return Math.floor(diff / hour) + '小时前'
  return timeStr.slice(0, 10)
}
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.search-page { min-height: 100vh; background: $bg-page; }

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #fff;
  gap: 16rpx;
}

.input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f6f8;
  border-radius: 36rpx;
  padding: 0 24rpx;
  height: 72rpx;
}
.icon { font-size: 28rpx; margin-right: 12rpx; }
.search-input { flex: 1; font-size: 28rpx; }
.clear-btn { font-size: 28rpx; color: $text-light; padding: 8rpx; }

.search-btn {
  font-size: 28rpx;
  color: #fff;
  background: $primary;
  border-radius: 36rpx;
  padding: 0 36rpx;
  height: 72rpx;
  line-height: 72rpx;
  margin: 0;
}

.result-count {
  padding: 20rpx 30rpx;
  font-size: 26rpx;
  color: $text-sub;
}
.count-num { color: $primary; font-weight: bold; margin: 0 6rpx; }

.result-area { padding: 0 20rpx 20rpx; }

.loading-text, .empty-text {
  text-align: center;
  color: $text-light;
  font-size: 28rpx;
  padding: 100rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}
.empty-icon { font-size: 100rpx; }

.topic-list { display: flex; flex-direction: column; gap: 20rpx; }

.topic-card {
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 28rpx;
}
.card-top { display: flex; align-items: center; margin-bottom: 20rpx; }
.card-user { flex: 1; margin-left: 16rpx; }
.nickname { font-size: 26rpx; color: $text-sub; display: block; }
.time { font-size: 22rpx; color: $text-light; display: block; margin-top: 4rpx; }
.card-title { font-size: 32rpx; font-weight: bold; color: $text-main; margin-bottom: 12rpx; }
.card-desc { font-size: 26rpx; color: $text-sub; margin-bottom: 20rpx; }
.card-stats { display: flex; gap: 32rpx; padding-top: 20rpx; border-top: 1rpx solid #f2f2f2; }
.stat { font-size: 24rpx; color: $text-light; }

.hint-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
  gap: 24rpx;
}
.hint-icon { font-size: 100rpx; }
.hint-text { font-size: 28rpx; color: $text-light; }
</style>