<template>
  <view class="result-page">
    <view v-if="loading" class="tip">加载中...</view>

    <template v-else>
      <!-- 话题信息卡片 -->
      <view class="card header-card">
        <view class="badges">
          <text class="badge" :class="topic.type === 2 ? 'badge-multi' : ''">
            {{ topic.type === 2 ? '☑ 多选' : '◉ 单选' }}
          </text>
          <text class="badge badge-success">进行中</text>
        </view>
        <view class="title">{{ topic.title }}</view>
        <view v-if="topic.description" class="desc">{{ topic.description }}</view>
      </view>

      <!-- 统计卡片 -->
      <view class="stats-row">
        <view class="stat-card">
          <text class="stat-number">{{ totalVotes }}</text>
          <text class="stat-label">总投票数</text>
        </view>
        <view class="stat-card">
          <text class="stat-number">{{ optionCount }}</text>
          <text class="stat-label">选项数</text>
        </view>
        <view class="stat-card">
          <text class="stat-number">{{ topOption }}</text>
          <text class="stat-label">领先项</text>
        </view>
      </view>

      <!-- 图表切换 -->
      <view class="card">
        <view class="chart-tabs">
          <text
            class="chart-tab"
            :class="{ active: chartType === 'pie' }"
            @click="chartType = 'pie'"
          >饼图</text>
          <text
            class="chart-tab"
            :class="{ active: chartType === 'column' }"
            @click="chartType = 'column'"
          >柱状图</text>
        </view>

        <view v-if="totalVotes === 0" class="no-data">暂无投票数据</view>
        <view v-else class="chart-wrap">
          <!-- 饼图 -->
          <qiun-data-charts
            v-if="chartType === 'pie'"
            type="pie"
            :chartData="pieData"
            :opts="pieOpts"
          />
          <!-- 柱状图 -->
          <qiun-data-charts
            v-if="chartType === 'column'"
            type="column"
            :chartData="columnData"
            :opts="columnOpts"
          />
        </view>
      </view>

      <!-- AI 分析报告 -->
      <view class="card ai-card">
        <view class="ai-header">
          <text class="ai-title">🤖 AI 分析报告</text>
          <text class="ai-refresh" @click="loadAiReport">🔄 重新生成</text>
        </view>
        <view v-if="aiLoading" class="ai-loading">
          <text>AI 正在分析投票数据，请稍等...</text>
        </view>
        <view v-else-if="aiReport" class="ai-content">{{ aiReport }}</view>
        <view v-else class="ai-error">
          <text>报告生成失败</text>
          <text class="retry" @click="loadAiReport">重试</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getTopicDetail } from '@/utils/api/topic'
import request from '@/utils/request'

const topic = ref({})
const chartType = ref('pie')
const pieData = ref({ series: [] })
const columnData = ref({ categories: [], series: [] })
const totalVotes = ref(0)
const optionCount = ref(0)
const topOption = ref('-')
const aiReport = ref('')
const aiLoading = ref(false)
const loading = ref(true)
const topicId = ref(null)

// 饼图配置
const pieOpts = ref({
  color: ['#4A90D9', '#36CFC9', '#F7BA1E', '#F56C6C', '#909399', '#722ED1'],
  padding: [5, 5, 5, 5],
  legend: { show: true, position: 'bottom' },
  extra: {
    pie: {
      activeOpacity: 0.5,
      activeRadius: 10,
      offsetAngle: 0,
      labelWidth: 15,
      border: false,
      borderWidth: 3,
      borderColor: '#FFFFFF'
    }
  }
})

// 柱状图配置
const columnOpts = ref({
  color: ['#4A90D9'],
  padding: [15, 15, 0, 5],
  legend: { show: false },
  xAxis: {
    disableGrid: true,
    fontColor: '#666666',
    fontSize: 11,
    rotateLabel: true
  },
  yAxis: {
    gridType: 'dash',
    dashLength: 2,
    data: [{ min: 0 }]
  },
  extra: {
    column: {
      type: 'group',
      width: 30,
      activeBgColor: '#000000',
      activeBgOpacity: 0.08
    }
  }
})

onLoad(async (query) => {
  topicId.value = query.id
  try {
    const [detail, results] = await Promise.all([
      getTopicDetail(query.id),
      request.get(`/topics/${query.id}/results`)
    ])
    topic.value = detail || {}

    const labels = results.labels || []
    const values = results.values || []
    optionCount.value = labels.length
    totalVotes.value = results.total || 0

    // 饼图数据格式：{ series: [{ name, data: 单个数 }] }
    pieData.value = {
      series: labels.map((name, i) => ({
        name,
        data: values[i] || 0
      }))
    }

    // 柱状图数据格式：{ categories: [...], series: [{ name, data: [...] }] }
    columnData.value = {
      categories: labels,
      series: [{ name: '票数', data: values }]
    }

    // 领先项
    if (labels.length > 0) {
      let maxIdx = 0
      values.forEach((v, i) => { if (v > values[maxIdx]) maxIdx = i })
      const name = labels[maxIdx] || '-'
      topOption.value = name.length > 4 ? name.slice(0, 4) + '...' : name
    }
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
  loadAiReport()
})

const loadAiReport = async () => {
  if (!topicId.value) return
  aiLoading.value = true
  aiReport.value = ''
  try {
    const res = await request.get(`/ai/report/${topicId.value}`, {}, { timeout: 60000 })
    aiReport.value = typeof res === 'string' ? res : (res && res.data) || ''
  } catch (e) {
    console.error('AI报告失败', e)
  } finally {
    aiLoading.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.result-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }

.tip { text-align: center; padding: 100rpx; color: $text-light; }

.header-card { padding: 32rpx; }
.badges { margin-bottom: 20rpx; }
.title { font-size: 36rpx; font-weight: bold; color: $text-main; margin-bottom: 12rpx; }
.desc { font-size: 28rpx; color: $text-sub; line-height: 1.6; }

.stats-row { display: flex; gap: 20rpx; margin-bottom: 20rpx; }
.stat-card {
  flex: 1;
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 32rpx 16rpx;
  text-align: center;
}
.stat-number { display: block; font-size: 40rpx; font-weight: bold; color: $primary; margin-bottom: 8rpx; }
.stat-label { font-size: 24rpx; color: $text-light; }

.chart-tabs { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.chart-tab {
  padding: 12rpx 32rpx;
  border-radius: 36rpx;
  font-size: 26rpx;
  color: $text-sub;
  background: #f5f6f8;
}
.chart-tab.active { background: $primary; color: #fff; }

.chart-wrap { width: 100%; height: 500rpx; }
.no-data { text-align: center; padding: 100rpx 0; color: $text-light; font-size: 28rpx; }

.ai-card { padding: 32rpx; }
.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.ai-title { font-size: 30rpx; font-weight: bold; color: $text-main; }
.ai-refresh { font-size: 24rpx; color: $primary; }
.ai-loading { text-align: center; padding: 40rpx 0; color: $text-light; font-size: 26rpx; }
.ai-content {
  font-size: 28rpx;
  color: $text-sub;
  line-height: 1.8;
  white-space: pre-wrap;
}
.ai-error { text-align: center; padding: 40rpx 0; color: $text-light; font-size: 26rpx; }
.retry { color: $primary; margin-left: 16rpx; }
</style>