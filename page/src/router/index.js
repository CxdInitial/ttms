import Vue from 'vue'
import Router from 'vue-router'
import Users from '@/components/Users'
import Index from '@/components/Index'
import Register from '@/components/Register'
import OwnExam from '@/components/OwnExam'
import AllExam from '@/components/AllExam'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Index',
      component: Index
    },
    {
      path: '/user/others',
      name: 'Users',
      component: Users
    },
    {
      path: '/user/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/exam/own',
      name: 'OwnExam',
      component: OwnExam
    },
    {
      path: '/exam/all',
      name: 'AllExam',
      component: AllExam
    }
  ]
})
