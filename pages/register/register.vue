<template>
  <view class="register">
    <view class="form">
      <input v-model="form.username" placeholder="请输入用户名" />
      <input v-model="form.password" type="password" placeholder="请输入密码" />
      <input v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
      <button @click="handleRegister">注册</button>
      <button class="login-btn" @click="goLogin">返回登录</button>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import request from '@/utils/request'

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.confirmPassword) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  try {
    await request.post('/api/auth/register', {
      username: form.username,
      password: form.password
    })
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/login/login' })
    }, 500)
  } catch (e) {
    uni.showToast({ title: e.message || '注册失败', icon: 'none' })
  }
}

const goLogin = () => {
  uni.navigateTo({ url: '/pages/login/login' })
}
</script>

<style scoped>
.register {
  padding: 60rpx 40rpx;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}
input {
  border: 1px solid #ddd;
  padding: 20rpx;
  border-radius: 10rpx;
}
button {
  margin-top: 20rpx;
}
.login-btn {
  background-color: #f0f0f0;
  color: #333;
}
</style>