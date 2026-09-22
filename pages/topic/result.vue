<template>
  <view class="result-page">
    <!-- 加载中 -->
    <view v-if="loading" class="loading-text">加载中...</view>

    <template v-else>
      <!-- 投票标题 -->
      <view class="topic-header">
        <view class="topic-title">{{ topicTitle }}</view>
        <view class="topic-total">共 {{ total }} 人参与</view>
      </view>

      <!-- 饼图 -->
      <view class="chart-wrap" v-if="hasData">
        <qiun-data-charts
          type="pie"
          :opts="chartOpts"
          :chartData="chartData"
          canvasId="votePie"
        />
      </view>

      <!-- 选项列表 + 进度条 -->
      <view class="option-list">
        <view
          v-for="(item, index) in optionList"
          :key="index"
          class="option-item"
        >
          <view class="option-info">
            <text class="option-label">{{ item.label }}</text>
            <text class="option-count">{{ item.value }} 票 ({{ item.percent }}%)</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: item.percent + '%' }"></view>
          </view>
        </view>
      </view>

      <!-- 无数据 -->
      <view v-if="!hasData" class="empty-text">暂无投票数据</view>
    </template>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getVoteResult } from '@/utils/api/vote.js'
import { getTopicDetail } from '@/utils/api/topic.js'

const loading = ref(true)
const topicTitle = ref('')
const labels = ref([])
const values = ref([])
const total = ref(0)

onLoad((options) => {
  const topicId = options.id
  loadResult(topicId)
  loadTopicInfo(topicId)
})

const loadResult = (topicId) => {
  getVoteResult(topicId)
    .then(res => {
      labels.value = res.labels || []
      values.value = res.values || []
      total.value = res.total || 0
    })
    .catch(() => {
      uni.showToast({ title: '加载结果失败', icon: 'none' })
    })
    .finally(() => {
      loading.value = false
    })
}

const loadTopicInfo = (topicId) => {
  getTopicDetail(topicId)
    .then(res => {
      topicTitle.value = res.title || '投票结果'
    })
    .catch(() => {
      topicTitle.value = '投票结果'
    })
}

const hasData = computed(() => labels.value.length > 0 && total.value > 0)

const optionList = computed(() => {
  return labels.value.map((label, i) => {
    const value = values.value[i] || 0
    const percent = total.value > 0 ? Math.round((value / total.value) * 100) : 0
    return { label, value, percent }
  })
})

const chartData = computed(() => {
  const series = labels.value.map((label, i) => ({
    name: label,
    data: values.value[i] || 0
  }))
  return { categories: labels.value, series }
})

const chartOpts = {
  color: ['#4a90d9', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2'],
  padding: [20, 20, 20, 20],
  legend: { show: true, position: 'bottom' }
}
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.loading-text {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  padding: 100rpx 0;
}

.topic-header {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.topic-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.topic-total {
  font-size: 26rpx;
  color: #999;
}

.chart-wrap {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  height: 500rpx;
}

.option-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.option-item {
  margin-bottom: 24rpx;
}

.option-item:last-child {
  margin-bottom: 0;
}

.option-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.option-label {
  font-size: 28rpx;
  color: #333;
}

.option-count {
  font-size: 26rpx;
  color: #4a90d9;
}

.progress-bar {
  height: 16rpx;
  background: #f0f0f0;
  border-radius: 8rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, #4a90d9, #69b1ff);
  border-radius: 8rpx;
  transition: width 0.5s ease;
}

.empty-text {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  padding: 60rpx 0;
}
</style>
