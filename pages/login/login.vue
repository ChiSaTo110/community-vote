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
import { login } from '@/utils/api/auth'
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
    // 走统一的 api 模块；request.js 已解包 {code,msg,data}，这里拿到的就是 data
    const data = await login({
      username: form.username,
      password: form.password
    })
    // 后端 /auth/login 返回: { token, userId, username, nickname, avatar }
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
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 500)
  } catch (e) {
    // request.js 内部已经弹过 toast，这里不再重复弹
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