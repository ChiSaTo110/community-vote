<template>
  <view class="profile">
    <!-- 未登录 -->
    <view v-if="!userStore.isLogin" class="guest">
      <image class="guest-avatar" src="/static/logo.png" mode="aspectFill" />
      <text class="guest-tip">登录后查看个人信息和投票记录</text>
      <button class="login-btn" @click="goLogin">去登录</button>
    </view>

    <!-- 已登录 -->
    <view v-else class="content">
      <view class="card user-card">
        <image class="avatar" :src="avatarUrl" mode="aspectFill" />
        <view class="user-main">
          <text class="nickname">{{ displayName }}</text>
          <text class="username">@{{ accountName }}</text>
        </view>
        <text class="edit" @click="handleEdit">编辑</text>
      </view>

      <view class="card detail">
        <view class="row">
          <text class="label">邮箱</text>
          <text class="value">{{ emailText }}</text>
        </view>
        <view class="row">
          <text class="label">用户 ID</text>
          <text class="value">{{ userStore.userId }}</text>
        </view>
        <view class="row">
          <text class="label">注册时间</text>
          <text class="value">{{ joinDate }}</text>
        </view>
      </view>

      <view class="card stats">
        <view class="stat" @click="goMyTopics">
          <text class="stat-num">{{ topicCount }}</text>
          <text class="stat-label">我发起的</text>
        </view>
        <view class="v-line" />
        <view class="stat" @click="goMyVotes">
          <text class="stat-num">{{ voteCount }}</text>
          <text class="stat-label">我参与的</text>
        </view>
      </view>

      <view class="card menu">
        <view class="menu-item" @click="goMyTopics">
          <text class="menu-text">我的话题</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" @click="goMyVotes">
          <text class="menu-text">我的投票</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, getMyTopics, getMyVotes } from '@/utils/api/user'

const userStore = useUserStore()

const topicCount = ref(0)
const voteCount = ref(0)

const avatarUrl = computed(() => userStore.avatar || '/static/logo.png')
// 注意：isLogin 只代表有 token，userInfo 仍可能是 null（资料未拉到或存储读取失败），
// 所以模板里所有字段都走这里取值，避免直接访问 null 报错
const info = computed(() => userStore.userInfo || {})
const accountName = computed(() => info.value.username || '')
const emailText = computed(() => info.value.email || '未填写')
const displayName = computed(() => userStore.nickname || info.value.username || '未设置昵称')
const joinDate = computed(() => {
  const t = info.value.createdAt
  if (!t) return '-'
  return String(t).replace('T', ' ').slice(0, 16)
})

// 每次进入页面都拉一次，保证资料最新（改完昵称返回时能立刻看到）
const loadProfile = async () => {
  try {
    const info = await getUserInfo()
    if (info) userStore.setUserInfo(info)
  } catch (e) {
    // 401 时 request.js 已清除 token 并跳转登录页，这里不用处理
  }
}

const loadStats = async () => {
  try {
    const [topics, votes] = await Promise.all([getMyTopics(), getMyVotes()])
    topicCount.value = Array.isArray(topics) ? topics.length : 0
    voteCount.value = Array.isArray(votes) ? votes.length : 0
  } catch (e) {
    // 统计失败不影响页面展示，保持 0
  }
}

onShow(() => {
  if (!userStore.isLogin) {
    topicCount.value = 0
    voteCount.value = 0
    return
  }
  loadProfile()
  loadStats()
})

const goLogin = () => uni.navigateTo({ url: '/pages/login/login' })
const goMyTopics = () => uni.navigateTo({ url: '/pages/profile/my-topics' })
const goMyVotes = () => uni.navigateTo({ url: '/pages/profile/my-votes' })

const handleEdit = () => {
  uni.showModal({
    title: '修改昵称',
    editable: true,
    placeholderText: '请输入新昵称',
    success: async (res) => {
      if (!res.confirm) return
      const nickname = (res.content || '').trim()
      if (!nickname) return
      try {
        await updateUserInfo({ nickname })
        // 后端该接口不返回新资料，本地合并写入，避免再请求一次
        userStore.setUserInfo({ ...userStore.userInfo, nickname })
        uni.showToast({ title: '已更新', icon: 'success' })
      } catch (e) {
        uni.showToast({ title: (e && e.message) || '修改失败', icon: 'none' })
      }
    }
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (!res.confirm) return
      userStore.logout()
      uni.showToast({ title: '已退出登录', icon: 'success' })
    }
  })
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f6f8;
  padding: 30rpx 24rpx;
  box-sizing: border-box;
}

.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
}

/* 用户信息卡 */
.user-card {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background-color: #eeeeee;
  flex-shrink: 0;
}

.user-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 24rpx;
}

.nickname {
  font-size: 34rpx;
  font-weight: bold;
  color: #222222;
}

.username {
  font-size: 26rpx;
  color: #999999;
  margin-top: 8rpx;
}

.edit {
  font-size: 26rpx;
  color: #2979ff;
  flex-shrink: 0;
}

/* 资料明细 */
.detail {
  padding: 8rpx 28rpx;
}

.row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f2f2f2;
}

.row:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #888888;
}

.value {
  font-size: 28rpx;
  color: #333333;
}

/* 统计 */
.stats {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 36rpx 28rpx;
}

.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 44rpx;
  font-weight: bold;
  color: #2979ff;
}

.stat-label {
  font-size: 26rpx;
  color: #999999;
  margin-top: 10rpx;
}

.v-line {
  width: 1rpx;
  height: 60rpx;
  background-color: #eeeeee;
}

/* 菜单 */
.menu {
  padding: 0 28rpx;
}

.menu-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f2f2f2;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 30rpx;
  color: #333333;
}

.arrow {
  font-size: 36rpx;
  color: #c0c0c0;
}

/* 按钮 */
.login-btn,
.logout-btn {
  margin-top: 40rpx;
  background-color: #2979ff;
  color: #ffffff;
  border-radius: 12rpx;
  font-size: 30rpx;
}

.logout-btn {
  background-color: #ffffff;
  color: #e54d42;
}

/* 未登录 */
.guest {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
}

.guest-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 80rpx;
  opacity: 0.5;
}

.guest-tip {
  font-size: 28rpx;
  color: #999999;
  margin-top: 30rpx;
}
</style>
