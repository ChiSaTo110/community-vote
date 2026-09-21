import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
export function createApp() {
  const app = createSSRApp(App)
  // 注册 Pinia，否则页面里调用 useUserStore() 会报
  // "getActivePinia was called with no active Pinia"
  app.use(createPinia())
  return {
    app
  }
}
// #endif