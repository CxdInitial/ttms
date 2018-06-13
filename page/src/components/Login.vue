<template>
  <div id="log">
    <el-popover placement="bottom-end" width="auto" trigger="hover" v-show="!authenticated">
      <table>
        <tr>
          <td>学工号：</td>
          <td>
            <el-input v-model="teacherNo" placeholder="学工号" name="teacherNo" />
          </td>
        </tr>
        <tr>
          <td>密码：</td>
          <td>
            <el-input v-model="loginPassword" @keydown.enter="login" type="password" placeholder="本系统或教务处密码" name="loginPassword" />
          </td>
        </tr>
      </table>
      <div id="login-btn">
        <el-button :loading="submitting" @click="login" size="small">确定</el-button>
      </div>
      <el-button slot="reference">登录</el-button>
    </el-popover>
    <el-button v-show="authenticated" @click="logout">退出</el-button>
  </div>
</template>

<script>
import Axios from '@/util/Axios'
import Bus from '@/util/Bus'
import Validator from '@/util/Validator'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      teacherNo: '2015224306',
      loginPassword: '2zhaoxuemei',
      submitting: false
    }
  },
  computed: {
    authenticated: function() {
      return this.user && this.user.id
    }
  },
  methods: {
    login() {
      if (this.authenticated) {
        this.$notify({
          title: '您已登录，请退出后重新登录',
          type: 'info',
          duration: 5000,
          position: 'top-right'
        })
        return false
      }
      var errorFields = Validator.userValidate({
        teacherNo: this.teacherNo,
        loginPassword: this.loginPassword
      })
      if (errorFields.length) {
        this.$notify({
          title: '输入格式错误',
          type: 'info',
          duration: 5000,
          position: 'top-right'
        })
        return false
      }
      this.submitting = true
      Axios({
        method: 'post',
        url: '/authentication',
        params: {
          loginPassword: this.loginPassword,
          teacherNo: this.teacherNo
        }
      })
        .then(response => {
          this.$notify({
            title: '登录成功',
            type: 'success',
            duration: 5000,
            position: 'top-right'
          })
          this.submitting = false
          Bus.$emit(Bus.login, response.data.user)
        })
        .catch(error => {
          if (error.response) {
            switch (error.response.status) {
              case 404: {
                this.$notify({
                  title: '请联系管理员注册',
                  type: 'warning',
                  duration: 5000,
                  position: 'top-right'
                })
                break
              }
              case 422: {
                this.$notify({
                  title: '密码错误',
                  type: 'error',
                  duration: 5000,
                  position: 'top-right'
                })
                break
              }
            }
          } else
            this.$notify({
              title: '未知错误',
              type: 'warning',
              duration: 5000,
              position: 'top-right'
            })
          this.submitting = false
        })
    },
    logout() {
      this.submitting = true
      Axios({
        method: 'delete',
        url: '/authentication'
      })
        .then(response => {
          this.$notify({
            title: '退出登录',
            type: 'info',
            duration: 5000,
            position: 'top-right'
          })
          this.submitting = false
          Bus.$emit(Bus.logout)
        })
        .catch(error => {
          this.$notify({
            title: '退出失败',
            type: 'error',
            duration: 5000,
            position: 'top-right'
          })
          this.submitting = false
        })
    }
  }
}
</script>

<style scoped>
#login-btn {
  display: flex;
  justify-content: right;
  margin: 10px 15px 5px 15px;
}
#log {
  display: flex;
  justify-content: flex-end;
  flex-direction: row;
}
</style>

