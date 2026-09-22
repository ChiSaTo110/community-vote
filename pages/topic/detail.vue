<template>
  <view class="detail-page">
    <!-- 话题头部卡片 -->
    <view class="card header-card">
      <view class="creator-row">
        <view class="avatar">{{ (topic.creatorNickname || '匿')[0] }}</view>
        <view class="creator-info">
          <text class="nickname">{{ topic.creatorNickname || '匿名用户' }}</text>
          <text class="time">{{ formatTimeAgo(topic.createdAt) }}</text>
        </view>
        <text class="badge" :class="topic.type === 2 ? 'badge-multi' : ''">
          {{ topic.type === 2 ? '☑ 多选' : '◉ 单选' }}
        </text>
      </view>

      <view class="title">{{ topic.title }}</view>
      <view v-if="topic.description" class="desc">{{ topic.description }}</view>

      <view class="meta">
        <text>📋 {{ topic.options?.length || 0 }} 个选项</text>
        <text>👥 {{ topic.voteCount || 0 }} 人参与</text>
        <text>💬 {{ topic.commentCount || 0 }} 评论</text>
      </view>

      <!-- 多选提示 -->
      <view v-if="topic.type === 2" class="multi-tip">
        💡 可多选，点击选项切换选中状态
      </view>
    </view>

    <!-- 选项卡片 -->
    <view class="card">
      <view
        v-for="opt in topic.options"
        :key="opt.id"
        class="option"
        :class="{ selected: isSelected(opt.id) }"
        @click="toggleOption(opt.id)"
      >
        <view
          class="option-check"
          :class="[
            topic.type === 2 ? 'checkbox' : 'radio',
            { checked: isSelected(opt.id) }
          ]"
        >
          <text v-if="isSelected(opt.id)">✓</text>
        </view>
        <text class="option-text">{{ opt.optionText }}</text>
      </view>

      <button class="vote-btn" @click="submitVote">提交投票</button>
    </view>

    <!-- 评论区 -->
    <view class="card comment-wrap" v-if="topicId">
      <Comment :topic-id="topicId" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getTopicDetail } from '@/utils/api/topic'
import request from '@/utils/request'
import Comment from '@/components/comment/comment.vue'

const topic = ref({ options: [] })
const selectedIds = ref([])   // 用数组，兼容单选和多选
const topicId = ref(null)

onLoad(async (query) => {
  topicId.value = query.id
  try {
    const res = await getTopicDetail(query.id)
    topic.value = res || {}
  } catch (e) {}
})

// 判断某个选项是否被选中
const isSelected = (id) => selectedIds.value.includes(id)

// 点击选项：单选直接替换，多选切换
const toggleOption = (id) => {
  if (topic.value.type === 2) {
    // 多选：toggle
    const idx = selectedIds.value.indexOf(id)
    if (idx >= 0) {
      selectedIds.value.splice(idx, 1)
    } else {
      selectedIds.value.push(id)
    }
  } else {
    // 单选：只能选一个
    selectedIds.value = [id]
  }
}

const submitVote = async () => {
  if (selectedIds.value.length === 0) {
    uni.showToast({
      title: topic.value.type === 2 ? '请至少选择一个选项' : '请选择一个选项',
      icon: 'none'
    })
    return
  }
  try {
    await request.post('/vote', {
      topicId: Number(topicId.value),
      optionIds: selectedIds.value,   // 传数组
      fillText: ''
    })
    uni.showToast({ title: '投票成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: `/pages/topic/result?id=${topicId.value}` })
    }, 500)
  } catch (e) {}
}

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

.detail-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }

.header-card { padding: 32rpx; }

.creator-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}
.creator-info { flex: 1; margin-left: 16rpx; }
.nickname { font-size: 28rpx; color: $text-main; font-weight: 500; display: block; }
.time { font-size: 22rpx; color: $text-light; display: block; margin-top: 4rpx; }

.title {
  font-size: 38rpx;
  font-weight: bold;
  color: $text-main;
  line-height: 1.4;
  margin-bottom: 16rpx;
}
.desc {
  font-size: 28rpx;
  color: $text-sub;
  line-height: 1.6;
  margin-bottom: 24rpx;
}

.meta {
  display: flex;
  gap: 32rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f2f2f2;
  font-size: 24rpx;
  color: $text-light;
}

.multi-tip {
  margin-top: 20rpx;
  padding: 16rpx 20rpx;
  background: #fff8e6;
  border-radius: 10rpx;
  font-size: 24rpx;
  color: #b8860b;
}

.option {
  display: flex;
  align-items: center;
  padding: 28rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  transition: all 0.2s;
}
.option.selected {
  border-color: $primary;
  background: $primary-light;
}

.option-check {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  font-size: 24rpx;
}
.option-check.radio { border-radius: 50%; }
.option-check.checkbox { border-radius: 8rpx; }
.option-check.checked {
  background: $primary;
  border-color: $primary;
  color: #fff;
}

.option-text {
  font-size: 30rpx;
  color: $text-main;
  flex: 1;
}

.vote-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, $primary, #5da9e9);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  box-shadow: 0 8rpx 24rpx rgba(74, 144, 217, 0.3);
  margin-top: 32rpx;
  border: none;
}

.comment-wrap { margin-top: 20rpx; }
</style>