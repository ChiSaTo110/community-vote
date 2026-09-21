<template>
  <view class="page">
    <view v-if="loading" class="tip">加载中...</view>

    <view v-else-if="list.length === 0" class="empty">
      <text class="empty-text">还没有发起过话题</text>
      <text class="empty-sub">去首页创建一个投票话题吧</text>
    </view>

    <view v-else class="list">
      <view class="item" v-for="item in list" :key="item.id" @click="goDetail(item.id)">
        <view class="item-head">
          <text class="title">{{ item.title }}</text>
          <text class="status" :class="item.status === 1 ? 'ongoing' : 'ended'">
            {{ item.status === 1 ? '进行中' : '已结束' }}
          </text>
        </view>
        <text class="desc">{{ item.description || '暂无描述' }}</text>
        <view class="item-foot">
          <text class="tag">{{ typeText(item.type) }}</text>
          <text class="time">{{ formatTime(item.createdAt) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyTopics } from '@/utils/api/user'

const list = ref([])
const loading = ref(false)

// 后端 type: 1单选 2多选 3填空
const typeMap = { 1: '单选投票', 2: '多选投票', 3: '填空投票' }
const typeText = (type) => typeMap[type] || '投票'

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').slice(0, 16)
}

const load = async () => {
  loading.value = true
  try {
    const res = await getMyTopics()
    list.value = Array.isArray(res) ? res : []
  } catch (e) {
    // 401 时 request.js 已跳转登录页，其余情况提示即可
    if (!(e && e.message && e.message.indexOf('登录') >= 0)) {
      uni.showToast({ title: '加载失败', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/topic/detail?id=${id}` })
}

onShow(load)
</script>

<style scoped>
.page {
  min-height: 100vh;
  background-color: #f5f6f8;
  padding: 24rpx;
  box-sizing: border-box;
}

.tip {
  padding-top: 80rpx;
  text-align: center;
  font-size: 28rpx;
  color: #999999;
}

.list {
  display: flex;
  flex-direction: column;
}

.item {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
}

.item-head {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: space-between;
}

.title {
  flex: 1;
  font-size: 32rpx;
  font-weight: bold;
  color: #222222;
  line-height: 1.4;
}

.status {
  flex-shrink: 0;
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  margin-left: 20rpx;
}

.ongoing {
  color: #19be6b;
  background-color: #eaf7ee;
}

.ended {
  color: #999999;
  background-color: #f2f2f2;
}

.desc {
  display: block;
  font-size: 26rpx;
  color: #888888;
  margin-top: 16rpx;
  line-height: 1.5;
}

.item-foot {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
}

.tag {
  font-size: 22rpx;
  color: #2979ff;
  background-color: #eaf1ff;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.time {
  font-size: 24rpx;
  color: #bbbbbb;
}

.empty {
  padding-top: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.empty-text {
  font-size: 30rpx;
  color: #999999;
}

.empty-sub {
  font-size: 26rpx;
  color: #c0c0c0;
  margin-top: 16rpx;
}
</style>
