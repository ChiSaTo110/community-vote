<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="card user-card">
      <view class="user-top">
        <view class="avatar-lg">{{ (userInfo.nickname || userInfo.username || 'U')[0] }}</view>
        <view class="user-info">
          <text class="user-nickname">{{ userInfo.nickname || userInfo.username || '用户' }}</text>
          <text class="user-username">@{{ userInfo.username }}</text>
        </view>
        <text class="edit-btn" @click="openEdit">✏️ 编辑</text>
      </view>
      <view class="user-email">
        <text class="label">📧 邮箱</text>
        <text class="value">{{ userInfo.email || '未绑定' }}</text>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-row">
      <view class="stat-card" @click="goMyTopics">
        <text class="stat-number">{{ createdCount }}</text>
        <text class="stat-label">发起话题</text>
      </view>
      <view class="stat-card" @click="goMyVotes">
        <text class="stat-number">{{ joinedCount }}</text>
        <text class="stat-label">参与投票</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="card menu-card">
      <view class="menu-item" @click="goMyTopics">
        <text class="menu-icon">📝</text>
        <text class="menu-text">我发起的话题</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goMyVotes">
        <text class="menu-icon">🗳</text>
        <text class="menu-text">我参与的话题</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- 编辑资料弹窗 -->
    <view v-if="showEdit" class="modal-mask" @click="closeEdit">
      <view class="modal-content" @click.stop>
        <view class="modal-title">编辑资料</view>
        <view class="modal-form">
          <view class="modal-item">
            <text class="modal-label">昵称</text>
            <input
              v-model="editForm.nickname"
              type="text"
              class="modal-input"
              placeholder="请输入昵称"
              placeholder-class="ph"
              maxlength="20"
            />
          </view>
          <view class="modal-item">
            <text class="modal-label">邮箱</text>
            <input
              v-model="editForm.email"
              type="text"
              class="modal-input"
              placeholder="请输入邮箱（选填）"
              placeholder-class="ph"
              maxlength="100"
            />
          </view>
        </view>
        <view class="modal-actions">
          <button class="modal-btn cancel" @click="closeEdit">取消</button>
          <button class="modal-btn confirm" :disabled="saving" @click="saveEdit">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile, getMyTopics, getMyVotes } from '@/utils/api/user'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo || {})
const createdCount = ref(0)
const joinedCount = ref(0)

const showEdit = ref(false)
const saving = ref(false)
const editForm = reactive({
  nickname: '',
  email: ''
})

onMounted(async () => {
  try {
    const me = await getProfile()
    if (me) {
      userInfo.value = me
      userStore.setUserInfo(me)
    }
    const [topics, votes] = await Promise.all([
      getMyTopics().catch(() => []),
      getMyVotes().catch(() => [])
    ])
    createdCount.value = (topics || []).length
    joinedCount.value = (votes || []).length
  } catch (e) {}
})

const openEdit = () => {
  editForm.nickname = userInfo.value.nickname || ''
  editForm.email = userInfo.value.email || ''
  showEdit.value = true
}

const closeEdit = () => {
  showEdit.value = false
}

const saveEdit = async () => {
  if (!editForm.nickname.trim()) {
    uni.showToast({ title: '昵称不能为空', icon: 'none' })
    return
  }
  saving.value = true
  try {
    await updateProfile({
      nickname: editForm.nickname.trim(),
      email: editForm.email.trim()
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
    // 更新本地状态
    userInfo.value = { ...userInfo.value, nickname: editForm.nickname, email: editForm.email }
    userStore.setUserInfo(userInfo.value)
    showEdit.value = false
  } catch (e) {
  } finally {
    saving.value = false
  }
}

const goMyTopics = () => uni.navigateTo({ url: '/pages/profile/my-topics' })
const goMyVotes = () => uni.navigateTo({ url: '/pages/profile/my-votes' })

const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.profile-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }

.user-card { padding: 40rpx 32rpx; }
.user-top { display: flex; align-items: center; margin-bottom: 32rpx; }
.avatar-lg {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, $primary, $success);
  color: #fff;
  font-size: 48rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}
.user-info { flex: 1; }
.user-nickname { font-size: 36rpx; font-weight: bold; color: $text-main; display: block; margin-bottom: 8rpx; }
.user-username { font-size: 26rpx; color: $text-light; }
.edit-btn {
  font-size: 26rpx;
  color: $primary;
  padding: 8rpx 20rpx;
  border: 2rpx solid $primary;
  border-radius: 30rpx;
}

.user-email {
  display: flex;
  justify-content: space-between;
  padding-top: 24rpx;
  border-top: 1rpx solid #f2f2f2;
  font-size: 28rpx;
}
.label { color: $text-light; }
.value { color: $text-main; }

.stats-row { display: flex; gap: 20rpx; margin-bottom: 20rpx; }
.stat-card {
  flex: 1;
  background: #fff;
  border-radius: $card-radius;
  box-shadow: $card-shadow;
  padding: 36rpx 16rpx;
  text-align: center;
}
.stat-number { display: block; font-size: 44rpx; font-weight: bold; color: $primary; margin-bottom: 8rpx; }
.stat-label { font-size: 24rpx; color: $text-light; }

.menu-card { padding: 0; overflow: hidden; }
.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f2f2f2;
}
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: $text-main; }
.menu-arrow { font-size: 36rpx; color: $text-light; }

.logout-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #fff;
  color: $danger;
  font-size: 30rpx;
  border: 2rpx solid #fee;
  margin-top: 20rpx;
}

/* ============ 编辑弹窗 ============ */
.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-content {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
}
.modal-title {
  font-size: 34rpx;
  font-weight: bold;
  color: $text-main;
  text-align: center;
  margin-bottom: 32rpx;
}
.modal-form { margin-bottom: 32rpx; }
.modal-item { margin-bottom: 24rpx; }
.modal-item:last-child { margin-bottom: 0; }
.modal-label {
  font-size: 26rpx;
  color: $text-sub;
  display: block;
  margin-bottom: 12rpx;
}
.modal-input {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: $text-main;
  box-sizing: border-box;
}
.ph { color: $text-placeholder; }

.modal-actions {
  display: flex;
  gap: 20rpx;
}
.modal-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: none;
}
.modal-btn.cancel {
  background: #f0f0f0;
  color: $text-sub;
}
.modal-btn.confirm {
  background: linear-gradient(135deg, $primary, #5da9e9);
  color: #fff;
}
</style>