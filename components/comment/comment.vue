<template>
  <view class="comment">
    <view class="header">
      <text class="title">💬 评论</text>
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
  topicId: { type: [Number, String], required: true }
})

const list = ref([])
const loading = ref(false)
const text = ref('')
const sending = ref(false)

const canSend = computed(() => !sending.value && text.value.trim().length > 0)

const authorText = (c) => {
  if (c.nickname) return c.nickname
  if (!c.userId) return '匿名用户'
  return `用户 ${c.userId}`
}

const sentimentMap = {
  POSITIVE: { text: '😊 正面', cls: 'positive' },
  NEGATIVE: { text: '😞 负面', cls: 'negative' },
  NEUTRAL: { text: '😐 中性', cls: 'neutral' }
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

defineExpose({ load })
onMounted(load)
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.comment {
  background: #fff;
  border-radius: $card-radius;
  padding: 28rpx;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}
.title { font-size: 32rpx; font-weight: bold; color: $text-main; }
.count {
  font-size: 24rpx;
  color: #fff;
  background: $primary;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  margin-left: 12rpx;
}

.tip, .empty {
  padding: 60rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: $text-light;
}

.item { padding: 24rpx 0; border-bottom: 1rpx solid #f2f2f2; }
.item:last-child { border-bottom: none; }

.item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.author { font-size: 26rpx; color: $text-sub; font-weight: 500; }

.sentiment {
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 18rpx;
}
.positive { color: #19be6b; background: #eaf7ee; }
.negative { color: #e54d42; background: #fdeceb; }
.neutral { color: $text-light; background: #f2f2f2; }

.content {
  display: block;
  font-size: 28rpx;
  color: $text-main;
  line-height: 1.6;
  word-break: break-all;
  margin-bottom: 12rpx;
}
.time { font-size: 22rpx; color: $text-light; }

.input-bar {
  display: flex;
  align-items: center;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f2f2f2;
}

.input {
  flex: 1;
  height: 72rpx;
  background: #f5f6f8;
  border-radius: 36rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
}

.send {
  flex-shrink: 0;
  margin-left: 20rpx;
  padding: 0 32rpx;
  height: 72rpx;
  line-height: 72rpx;
  font-size: 28rpx;
  color: #fff;
  background: $primary;
  border-radius: 36rpx;
}
.disabled { background: #c8d8f5; }
</style>