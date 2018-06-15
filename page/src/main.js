import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router/index'
import Bus from '@/util/Bus'

Vue.use(ElementUI)

var vue = new Vue({
  el: '#app',
  router,
  beforeCreate: function() {
    Bus.$on(Bus.login, user => {
      this.user = user
      this.$router.push({ path: '/' })
    })
    Bus.$on(Bus.logout, () => {
      this.user = null
      this.$router.push({ path: '/' })
    })
  },
  data: function() {
    return {
      user: null
    }
  },
  render: h => h(App)
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/') {
    if (!vue.user || !vue.user.id) {
      next('/')
      vue.$message({
        type: 'info',
        message: '请先登录'
      })
      return
    } else if (managerPaths.includes(to.path) && !vue.user.manager) {
      next('/')
      vue.$message({
        type: 'warning',
        message: '禁止访问'
      })
      return
    }
  }
  next()
})

var managerPaths = ['/user/others', '/user/register']
