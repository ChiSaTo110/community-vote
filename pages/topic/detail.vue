<template>
  <view class="detail">
    <view class="header">
      <view class="title">{{ topic.title }}</view>
      <view class="desc">{{ topic.description }}</view>
      <view class="meta">
        <text>{{ topic.creatorNickname }}</text>
        <text>{{ topic.voteCount }}人参与</text>
        <text>{{ topic.commentCount }}条评论</text>
      </view>
    </view>

    <view class="options">
      <view
        v-for="opt in topic.options"
        :key="opt.id"
        class="option"
        :class="{ selected: selectedId === opt.id }"
        @click="selectedId = opt.id"
      >
        {{ opt.optionText }}
      </view>
    </view>

    <button class="vote-btn" @click="submitVote">投票</button>

    <!-- 评论区，嵌入 C 的组件 -->
    <view class="comment-wrap">
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
const selectedId = ref(null)
const topicId = ref(null)

onLoad(async (query) => {
  topicId.value = query.id
  try {
    const res = await getTopicDetail(query.id)
    topic.value = res || {}
  } catch (e) {}
})

const submitVote = async () => {
  if (!selectedId.value) {
    uni.showToast({ title: '请选择一个选项', icon: 'none' })
    return
  }
  try {
    await request.post('/vote', {
      topicId: Number(topicId.value),
      optionIds: [selectedId.value],
      fillText: ''
    })
    uni.showToast({ title: '投票成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: `/pages/topic/result?id=${topicId.value}` })
    }, 500)
  } catch (e) {}
}
</script>

<style scoped>
.detail { padding: 30rpx; }
.title { font-size: 36rpx; font-weight: bold; }
.desc { font-size: 28rpx; color: #666; margin-top: 20rpx; }
.meta { display: flex; gap: 24rpx; margin-top: 16rpx; font-size: 24rpx; color: #999; }
.options { margin-top: 40rpx; }
.option {
  padding: 30rpx;
  border: 2rpx solid #ddd;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}
.option.selected { border-color: #4a90d9; background: #f0f8ff; }
.vote-btn { margin-top: 40rpx; }
.comment-wrap { margin-top: 40rpx; }
</style>