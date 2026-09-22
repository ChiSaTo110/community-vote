<template>
  <view class="auth-page">
    <view class="auth-card">
      <view class="auth-logo">📝</view>
      <view class="auth-title">创建账号</view>
      <view class="auth-subtitle">加入投票大厅，表达你的观点</view>

      <view class="form-group">
        <text class="input-icon">👤</text>
        <input v-model="form.username" class="input" placeholder="用户名（3-20位）" placeholder-class="ph" />
      </view>
      <view class="form-group">
        <text class="input-icon">🏷️</text>
        <input v-model="form.nickname" class="input" placeholder="昵称" placeholder-class="ph" />
      </view>
      <view class="form-group">
        <text class="input-icon">📧</text>
        <input v-model="form.email" class="input" placeholder="邮箱（选填）" placeholder-class="ph" />
      </view>
      <view class="form-group">
        <text class="input-icon">🔒</text>
        <input v-model="form.password" class="input" type="password" placeholder="密码（至少6位）" placeholder-class="ph" />
      </view>
      <view class="form-group">
        <text class="input-icon">🔒</text>
        <input v-model="form.confirmPassword" class="input" type="password" placeholder="确认密码" placeholder-class="ph" />
      </view>

      <button class="auth-submit" @click="handleRegister">注 册</button>

      <view class="auth-footer">
        已有账号？
        <text class="link" @click="goLogin">去登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { register } from '@/utils/api/auth'

const form = reactive({ username: '', nickname: '', email: '', password: '', confirmPassword: '' })

const handleRegister = async () => {
  if (form.username.length < 3) return uni.showToast({ title: '用户名至少3位', icon: 'none' })
  if (!form.nickname) return uni.showToast({ title: '请输入昵称', icon: 'none' })
  if (form.password.length < 6) return uni.showToast({ title: '密码至少6位', icon: 'none' })
  if (form.password !== form.confirmPassword) return uni.showToast({ title: '两次密码不一致', icon: 'none' })

  try {
    await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 800)
  } catch (e) {}
}

const goLogin = () => uni.navigateTo({ url: '/pages/login/login' })
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.auth-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #eaf3fc 0%, #f5f6f8 60%);
  padding: 80rpx 48rpx 0;
}

.auth-card {
  background: #fff;
  border-radius: 32rpx;
  box-shadow: 0 12rpx 40rpx rgba(74, 144, 217, 0.12);
  padding: 50rpx 48rpx;
}

.auth-logo { font-size: 100rpx; text-align: center; margin-bottom: 24rpx; }
.auth-title { font-size: 44rpx; font-weight: bold; color: $text-main; text-align: center; margin-bottom: 12rpx; }
.auth-subtitle { font-size: 26rpx; color: $text-light; text-align: center; margin-bottom: 50rpx; }

.form-group {
  display: flex;
  align-items: center;
  background: #f5f6f8;
  border-radius: 44rpx;
  padding: 0 28rpx;
  height: 88rpx;
  margin-bottom: 24rpx;
}

.input-icon { font-size: 30rpx; margin-right: 16rpx; }
.input { flex: 1; font-size: 28rpx; color: $text-main; }
.ph { color: $text-placeholder; }

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
  margin-top: 32rpx;
  border: none;
}

.auth-footer { text-align: center; margin-top: 36rpx; font-size: 26rpx; color: $text-light; }
.link { color: $primary; font-weight: bold; }
</style>