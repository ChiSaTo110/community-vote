<template>
  <view class="auth-page">
    <view class="auth-card">
      <view class="auth-logo">🗳️</view>
      <view class="auth-title">欢迎回来</view>
      <view class="auth-subtitle">登录后参与投票、创建话题</view>

      <view class="form-group">
        <text class="input-icon">👤</text>
        <input
          v-model="form.username"
          class="input"
          placeholder="请输入用户名"
          placeholder-class="ph"
        />
      </view>
      <view class="form-group">
        <text class="input-icon">🔒</text>
        <input
          v-model="form.password"
          class="input"
          type="password"
          placeholder="请输入密码"
          placeholder-class="ph"
        />
      </view>

      <button class="auth-submit" @click="handleLogin">登 录</button>

      <view class="auth-footer">
        还没有账号？
        <text class="link" @click="goRegister">立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { login } from '@/utils/api/auth'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  try {
    const data = await login({ username: form.username, password: form.password })
    userStore.setLoginData({
      token: data.token,
      userInfo: {
        id: data.userId,
        username: data.username,
        nickname: data.nickname,
        avatar: data.avatar
      }
    })
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 500)
  } catch (e) {}
}

const goRegister = () => uni.navigateTo({ url: '/pages/register/register' })
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.auth-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #eaf3fc 0%, #f5f6f8 60%);
  padding: 120rpx 48rpx 0;
}

.auth-card {
  background: #fff;
  border-radius: 32rpx;
  box-shadow: 0 12rpx 40rpx rgba(74, 144, 217, 0.12);
  padding: 60rpx 48rpx;
}

.auth-logo {
  font-size: 100rpx;
  text-align: center;
  margin-bottom: 24rpx;
}

.auth-title {
  font-size: 44rpx;
  font-weight: bold;
  color: $text-main;
  text-align: center;
  margin-bottom: 12rpx;
}

.auth-subtitle {
  font-size: 26rpx;
  color: $text-light;
  text-align: center;
  margin-bottom: 60rpx;
}

.form-group {
  display: flex;
  align-items: center;
  background: #f5f6f8;
  border-radius: 44rpx;
  padding: 0 28rpx;
  height: 88rpx;
  margin-bottom: 28rpx;
}

.input-icon {
  font-size: 30rpx;
  margin-right: 16rpx;
}

.input {
  flex: 1;
  font-size: 28rpx;
  color: $text-main;
}

.ph {
  color: $text-placeholder;
}

.auth-submit {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, $primary, #5da9e9);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  box-shadow: 0 8rpx 24rpx rgba(74, 144, 217, 0.3);
  margin-top: 40rpx;
  border: none;
}

.auth-footer {
  text-align: center;
  margin-top: 40rpx;
  font-size: 26rpx;
  color: $text-light;
}

.link {
  color: $primary;
  font-weight: bold;
}
</style>