<template>
  <el-popover placement="bottom-end" width="auto" trigger="click">
    <table>
      <tr>
        <td>学工号：</td>
        <td>
          <el-input v-model="user.teacherNo" placeholder="学工号" name="teacherNo" />
        </td>
      </tr>
      <tr>
        <td>密码：</td>
        <td>
          <el-input v-model="user.loginPassword" @keydown.enter="login" type="password" placeholder="本系统或教务处密码" name="loginPassword" />
        </td>
      </tr>
    </table>
    <div id="login-btn">
      <el-button :loading="submitting" @click="process($event)" size="small">确定</el-button>
    </div>
    <el-button slot="reference">{{btnText}}</el-button>
  </el-popover>
</template>

<script>
import Axios from '@/util/Axios'
import Bus from '@/util/Bus'
import Validator from '@/util/Validator'

export default {
  data: function() {
    return {
      user: {
        teacherNo: '',
        loginPassword: ''
      },
      submitting: false,
      authenticated: false
    }
  },
  computed: {
    btnText: function() {
      return this.authenticated ? '退出' : '登录'
    }
  },
  methods: {
    process(event) {
      return this.authenticated ? this.logout() : this.login()
    },
    login() {
      var errorFields = Validator.userValidate(this.user)
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
          loginPassword: this.user.loginPassword,
          teacherNo: this.user.teacherNo
        }
      })
        .then(response => {
          this.$notify({
            title: '登录成功',
            type: 'success',
            duration: 5000,
            position: 'top-right'
          })
          this.login = true
          Bus.$emit(Bus.login, this.user.teacherNo)
          this.submitting = false
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
          this.login = false
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
</style>

