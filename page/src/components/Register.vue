<template>
  <div id="register">
    <header>
      <h1>注册新用户</h1>
    </header>
    <el-form ref="form" :model="action" :rules="rules" :disabled="!authenticated">
      <el-form-item label="学工号" label-width="100px" prop="teacherNo">
        <el-input v-model="action.teacherNo" />
      </el-form-item>
      <el-form-item label="姓名" label-width="100px" prop="teacherName">
        <el-input v-model.trim="action.teacherName" minlength="2" maxlength="5" />
      </el-form-item>
      <el-form-item label="性别" label-width="100px" prop="male">
        <el-radio-group v-model="action.male">
          <el-radio :label="true">男</el-radio>
          <el-radio :label="false">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="密码" label-width="100px" prop="loginPassword">
        <el-input v-model="action.loginPassword" minlength="6" />
      </el-form-item>
      <el-form-item label="管理员权限" label-width="100px" prop="manager">
        <el-checkbox v-model="action.manager" />
      </el-form-item>
      <el-form-item label="手机号" label-width="100px" prop="phone">
        <el-input v-model="action.phone" minlength="11" maxlength="11" prop="phone" />
      </el-form-item>
      <el-form-item label="职称" label-width="100px" prop="title">
        <el-input v-model.trim="action.title" />
      </el-form-item>
      <el-form-item label="个人简介" label-width="100px" prop="intro">
        <el-input type="textarea" v-model.trim="action.intro" />
      </el-form-item>
    </el-form>
    <footer>
      <el-button @click="resetForm" :disabled="!authenticated">重 置</el-button>
      <el-button type="primary" @click="submitForm" :disabled="!authenticated">提 交</el-button>
    </footer>
  </div>
</template>

<script>
import Axios from '@/util/Axios'
import Regexps from '@/util/Regexps'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      action: {
        teacherNo: '',
        teacherName: '',
        loginPassword: '',
        manager: null,
        male: null,
        phone: '',
        title: '',
        intro: ''
      },
      rules: {
        teacherNo: [
          { required: true, message: '请输入学工号', trigger: 'blur' },
          {
            pattern: Regexps.user.teacherNo,
            message: '错误的格式',
            trigger: 'blur'
          }
        ],
        teacherName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          {
            pattern: Regexps.user.teacherName,
            message: '错误的格式',
            trigger: 'blur'
          }
        ],
        loginPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {
            pattern: Regexps.user.loginPassword,
            message: '错误的格式',
            trigger: 'blur'
          }
        ],
        manager: [{ type: 'boolean', message: '请选择', trigger: 'blur' }],
        male: [{ type: 'boolean', message: '请选择性别', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          {
            pattern: Regexps.user.phone,
            message: '错误的格式',
            trigger: 'blur'
          }
        ],
        title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
        intro: [{ required: true, message: '请输入简介', trigger: 'blur' }]
      }
    }
  },
  computed: {
    authenticated() {
      return Boolean(this.user && this.user.manager)
    }
  },
  methods: {
    resetForm() {
      this.action = {
        teacherNo: '',
        teacherName: '',
        loginPassword: '',
        manager: null,
        male: null,
        phone: '',
        title: '',
        intro: ''
      }
      this.$refs['form'].resetFields()
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          Axios({
            url: '/user',
            method: 'post',
            params: this.action
          })
            .then(response => {
              this.$notify({
                title: '注册成功',
                type: 'success',
                duration: 5000,
                position: 'top-right'
              })
              this.resetForm()
            })
            .catch(error => {
              this.$notify({
                title: '错误(学工号、手机号已经被使用)',
                type: 'warning',
                duration: 5000,
                position: 'top-right'
              })
            })
        }
      })
    }
  }
}
</script>

<style scoped>
#register {
  margin: 2% 15%;
  border: 1px solid #ebebeb;
  border-radius: 3px;
  transition: 0.2s;
  padding: 24px;
}
footer {
  display: flex;
  justify-content: flex-end;
}
footer button {
  margin: 10px 20px;
}
header {
  display: flex;
  justify-content: center;
}
</style>
