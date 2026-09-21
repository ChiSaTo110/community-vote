<template>
  <view class="comment">
    <view class="header">
      <text class="title">评论</text>
      <text class="count">{{ list.length }}</text>
    </view>

    <view v-if="loading" class="tip">加载中...</view>

    <view v-else-if="list.length === 0" class="empty">还没有评论，来说两句吧</view>

    <view v-else class="list">
      <view class="item" v-for="c in list" :key="c.id">
        <view class="item-head">
          <text class="author">{{ authorText(c) }}</text>
          <text v-if="sentimentMap[c.sentiment]" class="sentiment" :class="sentimentMap[c.sentiment].cls">
            {{ sentimentMap[c.sentiment].text }}
          </text>
        </view>
        <text class="content">{{ c.content }}</text>
        <text class="time">{{ formatTime(c.createTime) }}</text>
      </view>
    </view>

    <view class="input-bar">
      <input class="input" v-model="text" placeholder="说点什么..." :maxlength="200" />
      <text class="send" :class="{ disabled: !canSend }" @click="handleSend">
        {{ sending ? '发送中' : '发送' }}
      </text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCommentList, addComment } from '@/utils/api/comment'

const props = defineProps({
  topicId: {
    type: [Number, String],
    required: true
  }
})

const list = ref([])
const loading = ref(false)
const text = ref('')
const sending = ref(false)

const canSend = computed(() => !sending.value && text.value.trim().length > 0)

// 后端 Comment 只返回 userId，不带昵称，所以这里只能展示 userId
const authorText = (c) => {
  if (!c.userId) return '匿名用户'
  return `用户 ${c.userId}`
}

// 后端 AI 情感标注：POSITIVE 正面 / NEGATIVE 负面 / NEUTRAL 中性
const sentimentMap = {
  POSITIVE: { text: '正面', cls: 'positive' },
  NEGATIVE: { text: '负面', cls: 'negative' },
  NEUTRAL: { text: '中性', cls: 'neutral' }
}

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').slice(0, 16)
}

const load = async () => {
  if (!props.topicId) return
  loading.value = true
  try {
    const res = await getCommentList(props.topicId)
    list.value = Array.isArray(res) ? res : []
  } catch (e) {
    uni.showToast({ title: '评论加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const handleSend = async () => {
  if (!canSend.value) return
  sending.value = true
  try {
    await addComment(props.topicId, text.value.trim())
    text.value = ''
    uni.showToast({ title: '发布成功', icon: 'success' })
    await load()
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '发布失败', icon: 'none' })
  } finally {
    sending.value = false
  }
}

// 父页面需要手动刷新时可通过 ref 调用
defineExpose({ load })

onMounted(load)
</script>

<style scoped>
.comment {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
}

.header {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 20rpx;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #222222;
}

.count {
  font-size: 26rpx;
  color: #999999;
  margin-left: 12rpx;
}

.tip,
.empty {
  padding: 40rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: #aaaaaa;
}

.item {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f2f2f2;
}

.item:last-child {
  border-bottom: none;
}

.item-head {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.author {
  font-size: 26rpx;
  color: #666666;
}

.sentiment {
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 18rpx;
}

.positive {
  color: #19be6b;
  background-color: #eaf7ee;
}

.negative {
  color: #e54d42;
  background-color: #fdeceb;
}

.neutral {
  color: #999999;
  background-color: #f2f2f2;
}

.content {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-top: 14rpx;
  line-height: 1.6;
  word-break: break-all;
}

.time {
  display: block;
  font-size: 22rpx;
  color: #bbbbbb;
  margin-top: 12rpx;
}

.input-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f2f2f2;
}

.input {
  flex: 1;
  height: 68rpx;
  background-color: #f5f6f8;
  border-radius: 34rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.send {
  flex-shrink: 0;
  margin-left: 20rpx;
  padding: 0 28rpx;
  height: 68rpx;
  line-height: 68rpx;
  font-size: 28rpx;
  color: #ffffff;
  background-color: #2979ff;
  border-radius: 34rpx;
}

.disabled {
  background-color: #c8d8f5;
}
</style>
