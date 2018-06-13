import Vue from 'vue'
import Router from 'vue-router'
import Users from '@/components/Users'
import Index from '@/components/Index'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/user',
      name: 'Users',
      component: Users
    },
    {
      path: '/',
      name: 'Index',
      component: Index
    }
  ]
})
