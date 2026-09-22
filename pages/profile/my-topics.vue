<template>
  <view class="list-page">
    <view v-if="loading" class="tip">加载中...</view>
    <view v-else-if="list.length === 0" class="empty">
      <text class="empty-icon">📝</text>
      <text class="empty-text">你还没有发起过话题</text>
    </view>
    <view v-else class="topic-list">
      <view
        v-for="item in list"
        :key="item.id"
        class="topic-card"
        @click="goDetail(item.id)"
      >
        <view class="card-top">
          <text class="badge" :class="item.type === 2 ? 'badge-multi' : ''">
            {{ item.type === 2 ? '☑ 多选' : '◉ 单选' }}
          </text>
        </view>
        <view class="card-title">{{ item.title }}</view>
        <view class="card-stats">
          <text class="stat">📋 {{ item.optionCount || 0 }} 选项</text>
          <text class="stat">👥 {{ item.voteCount || 0 }} 人参与</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const list = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await request.get('/users/me/topics')
    list.value = Array.isArray(res) ? res : (res.data || [])
  } catch (e) {
    list.value = []
  } finally {
    loading.value = false
  }
})

const goDetail = (id) => uni.navigateTo({ url: `/pages/topic/detail?id=${id}` })
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.list-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }
.tip, .empty { text-align: center; padding: 120rpx 0; color: $text-light; font-size: 28rpx; }
.empty-icon { font-size: 100rpx; display: block; margin-bottom: 20rpx; }
.topic-list { display: flex; flex-direction: column; gap: 20rpx; }
.topic-card {
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 28rpx;
}
.card-top { margin-bottom: 16rpx; }
.card-title { font-size: 32rpx; font-weight: bold; color: $text-main; margin-bottom: 20rpx; }
.card-stats { display: flex; gap: 32rpx; padding-top: 20rpx; border-top: 1rpx solid #f2f2f2; }
.stat { font-size: 24rpx; color: $text-light; }
</style>