<template>
  <view class="login">
    <view class="form">
      <input v-model="form.username" placeholder="请输入用户名" />
      <input v-model="form.password" type="password" placeholder="请输入密码" />
      <button @click="handleLogin">登录</button>
      <button class="register-btn" @click="goRegister">去注册</button>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  try {
    const res = await request.post('/api/auth/login', form)
    // 根据后端实际返回结构调整
    const data = res.data || res
    userStore.setToken(data.token)
    userStore.setUserInfo(data.user || data.userInfo || {})
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 500)
  } catch (e) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  }
}

const goRegister = () => {
  uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<style scoped>
.login {
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
.register-btn {
  background-color: #f0f0f0;
  color: #333;
}
</style>