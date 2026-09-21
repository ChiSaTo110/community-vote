<template>
  <view class="search-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="input-wrap">
        <text class="iconfont">🔍</text>
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

    <!-- 搜索结果 -->
    <view v-if="searched" class="result-area">
      <view v-if="loading" class="loading-text">搜索中...</view>
      <view v-else-if="topicList.length === 0" class="empty-text">没有找到相关话题</view>

      <view v-else class="topic-list">
        <view
          v-for="item in topicList"
          :key="item.id"
          class="topic-card"
          @click="goDetail(item.id)"
        >
          <view class="topic-title">{{ item.title }}</view>
          <view class="topic-desc">{{ item.description }}</view>
          <view class="topic-meta">
            <text class="meta-item">{{ item.creatorNickname }}</text>
            <text class="meta-item">{{ item.voteCount }}人参与</text>
            <text class="meta-item">{{ item.commentCount }}条评论</text>
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
import { searchTopic } from '@/utils/api/topic.js'

const keyword = ref('')
const topicList = ref([])
const loading = ref(false)
const searched = ref(false)

const doSearch = () => {
  const kw = keyword.value.trim()
  if (!kw) {
    uni.showToast({ title: '请输入关键词', icon: 'none' })
    return
  }
  loading.value = true
  searched.value = true
  searchTopic({ keyword: kw })
    .then(res => {
      topicList.value = res || []
    })
    .catch(() => {
      topicList.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

const clearKeyword = () => {
  keyword.value = ''
  topicList.value = []
  searched.value = false
}

const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/detail/detail?id=${id}` })
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

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
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 24rpx;
  height: 64rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  margin-left: 12rpx;
}

.clear-btn {
  font-size: 28rpx;
  color: #999;
  padding: 8rpx;
}

.search-btn {
  font-size: 28rpx;
  color: #fff;
  background: #4a90d9;
  border-radius: 36rpx;
  padding: 0 36rpx;
  height: 64rpx;
  line-height: 64rpx;
  margin: 0;
}

.hint-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
  gap: 24rpx;
}

.hint-icon {
  font-size: 80rpx;
}

.hint-text {
  font-size: 28rpx;
  color: #999;
}

.result-area {
  padding: 20rpx;
}

.loading-text,
.empty-text {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  padding: 60rpx 0;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.topic-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.topic-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.topic-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-meta {
  display: flex;
  gap: 24rpx;
}

.meta-item {
  font-size: 24rpx;
  color: #999;
}
</style>
