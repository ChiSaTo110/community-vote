<template>
  <view class="index">
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="topics.length === 0" class="empty">暂无话题</view>
    <view v-else class="list">
      <view
        v-for="topic in topics"
        :key="topic.id"
        class="topic-item"
        @click="goDetail(topic.id)"
      >
        <view class="title">{{ topic.title }}</view>
        <view class="desc">{{ topic.description }}</view>
        <view class="meta">
          <text>{{ topic.creatorNickname }}</text>
          <text>{{ topic.voteCount }}人参与</text>
          <text>{{ topic.commentCount }}条评论</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotTopics } from '@/utils/api/topic'

const topics = ref([])
const loading = ref(false)

const loadTopics = async () => {
  loading.value = true
  try {
    // request.js 已解包 data，这里拿到的就是数组
    const res = await getHotTopics()
    topics.value = Array.isArray(res) ? res : (res.records || res.list || [])
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/topic/detail?id=${id}` })
}

onMounted(loadTopics)
</script>

<style scoped>
.index { padding: 20rpx; }
.loading, .empty { text-align: center; padding: 60rpx; color: #999; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.topic-item {
  padding: 30rpx;
  background: #fff;
  border-radius: 16rpx;
}
.title { font-size: 32rpx; font-weight: bold; }
.desc { font-size: 26rpx; color: #666; margin-top: 10rpx; }
.meta { display: flex; gap: 24rpx; margin-top: 16rpx; font-size: 24rpx; color: #999; }
</style>