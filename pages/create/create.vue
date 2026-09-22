<template>
  <view class="create-page">
    <!-- 标题 -->
    <view class="card">
      <view class="form-item">
        <text class="label">投票标题 <text class="required">*</text></text>
        <input
          v-model="form.title"
          type="text"
          class="input"
          placeholder="例如：你最喜欢的编程语言是？"
          placeholder-class="ph"
          maxlength="200"
        />
      </view>

      <view class="form-item">
        <text class="label">投票描述</text>
        <textarea
          v-model="form.description"
          class="textarea"
          placeholder="补充说明投票背景、规则等（可选）"
          placeholder-class="ph"
          maxlength="500"
        />
      </view>
    </view>

    <!-- 题型选择 -->
    <view class="card">
      <text class="label">题型 <text class="required">*</text></text>
      <view class="type-cards">
        <view
          class="type-card"
          :class="{ active: form.type === 1 }"
          @click="form.type = 1"
        >
          <text class="type-icon">◉</text>
          <text class="type-name">单选题</text>
          <text class="type-desc">只能选一个</text>
        </view>
        <view
          class="type-card"
          :class="{ active: form.type === 2 }"
          @click="form.type = 2"
        >
          <text class="type-icon">☑</text>
          <text class="type-name">多选题</text>
          <text class="type-desc">可选多个</text>
        </view>
      </view>
    </view>

    <!-- 选项列表 -->
    <view class="card">
      <text class="label">投票选项 <text class="required">*</text></text>
      <view
        v-for="(opt, idx) in form.options"
        :key="idx"
        class="option-row"
      >
        <input
          v-model="form.options[idx]"
          type="text"
          class="option-input"
          :placeholder="`选项 ${idx + 1}`"
          placeholder-class="ph"
          maxlength="100"
        />
        <text
          v-if="form.options.length > 2"
          class="del-btn"
          @click="removeOption(idx)"
        >✕</text>
      </view>
      <button class="add-btn" @click="addOption">+ 添加选项</button>
    </view>

    <!-- 提交 -->
    <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
      {{ submitting ? '创建中...' : '发起投票' }}
    </button>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import request from '@/utils/request'

const submitting = ref(false)

const form = reactive({
  title: '',
  description: '',
  type: 1,
  options: ['', '']
})

const addOption = () => {
  if (form.options.length >= 8) {
    uni.showToast({ title: '最多 8 个选项', icon: 'none' })
    return
  }
  form.options.push('')
}

const removeOption = (idx) => {
  if (form.options.length <= 2) return
  form.options.splice(idx, 1)
}

const handleSubmit = async () => {
  if (!form.title.trim()) {
    uni.showToast({ title: '请输入投票标题', icon: 'none' })
    return
  }
  const validOptions = form.options.filter(o => o.trim())
  if (validOptions.length < 2) {
    uni.showToast({ title: '请至少填写2个选项', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await request.post('/topics', {
      title: form.title.trim(),
      description: form.description.trim(),
      type: form.type,
      options: validOptions
    })
    uni.showToast({ title: '创建成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 800)
  } catch (e) {
    // request.js 已弹提示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/theme.scss';

.create-page { padding: 20rpx; background: $bg-page; min-height: 100vh; }

.form-item { margin-bottom: 32rpx; }
.form-item:last-child { margin-bottom: 0; }

.label {
  font-size: 28rpx;
  color: $text-main;
  font-weight: 500;
  display: block;
  margin-bottom: 16rpx;
}
.required { color: $danger; }

/* ============ 标题输入框 ============ */
.input {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: $text-main;
  box-sizing: border-box;
  display: block;
}

/* ============ 描述文本域 ============ */
.textarea {
  width: 100%;
  height: 180rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: $text-main;
  box-sizing: border-box;
}

/* 占位符样式 */
.ph {
  color: $text-placeholder;
  font-size: 28rpx;
}

.type-cards { display: flex; gap: 20rpx; }
.type-card {
  flex: 1;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  padding: 28rpx 20rpx;
  text-align: center;
  transition: all 0.2s;
}
.type-card.active {
  border-color: $primary;
  background: $primary-light;
}
.type-icon { font-size: 40rpx; display: block; margin-bottom: 12rpx; }
.type-name { font-size: 28rpx; font-weight: 500; display: block; margin-bottom: 6rpx; }
.type-desc { font-size: 22rpx; color: $text-light; display: block; }

.option-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}
.option-input {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: $text-main;
  box-sizing: border-box;
}
.del-btn {
  font-size: 32rpx;
  color: $danger;
  padding: 0 20rpx;
}

.add-btn {
  width: 100%;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 36rpx;
  background: #f0f8ff;
  color: $primary;
  font-size: 28rpx;
  border: 2rpx dashed $primary;
  margin-top: 16rpx;
}

.submit-btn {
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
</style>