<template>
  <div id="users">
    <el-table :data="users">
      <el-table-column label="注册日期" width="200" header-align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span style="margin-left: 10px">{{ combination(scope.row.insertTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="学工号" width="100" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.teacherNo}}</span>
        </template>
      </el-table-column>
      <el-table-column label="姓名" width="80" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.teacherName}}</span>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="80" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.male ? '男' : '女'}}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" width="110" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.phone}}</span>
        </template>
      </el-table-column>
      <el-table-column label="职称" width="80" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.title}}</span>
        </template>
      </el-table-column>
      <el-table-column label="管理员" width="80" header-align="left">
        <template slot-scope="scope">
          <span>{{scope.row.manager?'是':'否'}}</span>
        </template>
      </el-table-column>
      <el-table-column label="简介" width="80" header-align="left">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="top">
            <p>{{ scope.row.intro }}</p>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">简介</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="编辑" header-align="left">
        <template slot-scope="scope">
          <el-button size="mini" type="info" @click="operate(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
      <span slot="empty">{{emptyText}}</span>
    </el-table>
    <el-dialog title="修改个人信息" :visible.sync="operating" width="50%" style="margin-right:100px" :before-close="removeAction">
      <el-form ref="form" :model="action" :rules="rules">
        <el-form-item label="学工号" label-width="100px" prop="teacherNo">
          <el-input v-model.number="action.teacherNo" />
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
          <el-input v-model="action.phone" minlength="11" maxlength="11" />
        </el-form-item>
        <el-form-item label="职称" label-width="100px" prop="title">
          <el-input v-model.trim="action.title" />
        </el-form-item>
        <el-form-item label="个人简介" label-width="100px" prop="intro">
          <el-input type="textarea" v-model.trim="action.intro" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="removeAction">取 消</el-button>
        <el-button type="primary" @click="submitAction">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Bus from '@/util/Bus'
import Axios from '@/util/Axios'
import Regexps from '@/util/Regexps'

export default {
  props: { user: Object },
  data() {
    return {
      users: [],
      action: {
        id: 0,
        teacherNo: 0,
        loginPassword: '',
        teacherName: '',
        manager: false,
        male: true,
        phone: '',
        title: '',
        intro: ''
      },
      rules: {
        teacherNo: [
          { required: true, message: '请输入学工号', trigger: 'blur' },
          {
            type: 'number',
            min: 1000000000,
            max: 9999999999,
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
    operating() {
      return this.action.id !== 0
    },
    emptyText() {
      return this.user && this.user.manager ? '暂无数据' : '禁止访问'
    }
  },
  methods: {
    deleteUser(id) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        Axios({
          method: 'delete',
          url: '/user/' + id
        })
          .then(response => {
            this.$notify({
              title: '删除成功',
              type: 'success',
              duration: 5000,
              position: 'top-right'
            })
            this.users = this.users.filter(u => u.id != id)
          })
          .catch(error => {
            if (error.response && error.response.status === 404)
              Axios({
                method: 'get',
                url: '/user/' + id
              }).catch(e => {
                if (e.response && e.response.status === 404)
                  this.$notify({
                    title: '用户不存在',
                    type: 'info',
                    duration: 5000,
                    position: 'top-right'
                  })
              })
            else
              this.$notify({
                title: '未知的错误',
                type: 'warning',
                duration: 5000,
                position: 'top-right'
              })
          })
      })
    },
    combination(dispersedDate) {
      var date =
        String(dispersedDate.date.year) +
        '年' +
        String(dispersedDate.date.month) +
        '月' +
        String(dispersedDate.date.day) +
        '日' +
        (dispersedDate.time.hour < 10 ? '0' : '') +
        String(dispersedDate.time.hour) +
        ':' +
        (dispersedDate.time.minute < 10 ? '0' : '') +
        String(dispersedDate.time.minute) +
        ':' +
        (dispersedDate.time.second < 10 ? '0' : '') +
        String(dispersedDate.time.second)
      return date
    },
    removeAction() {
      this.action = {
        id: 0,
        teacherNo: 0,
        teacherName: '',
        loginPassword: '',
        manager: false,
        male: true,
        phone: '',
        title: '',
        intro: ''
      }
      this.$refs['form'].resetFields()
    },
    submitAction() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          var old = this.users.filter(u => u.id === this.action.id)[0]
          for (var p in this.action) {
            if (this.action.hasOwnProperty(p) && this.action[p] !== old[p]) {
              Axios({
                url: '/user/' + this.action.id,
                method: 'put',
                params: this.action
              })
                .then(response =>
                  this.$notify({
                    title: '修改成功',
                    type: 'success',
                    duration: 5000,
                    position: 'top-right'
                  })
                )
                .catch(error => {
                  this.$notify({
                    title: '未知的错误',
                    type: 'warning',
                    duration: 5000,
                    position: 'top-right'
                  })
                })
              Axios({
                method: 'get',
                url: '/user'
              }).then(response => {
                this.users = response.data.users.filter(
                  u => u.id && u.id != this.user.id
                )
              })
              return
            }
          }
          this.$notify({
            title: '您并未修改任何信息',
            type: 'info',
            duration: 5000,
            position: 'top-right'
          })
        }
      })
    },
    operate(u) {
      if (this.action.id !== 0) return
      this.action = {
        id: u.id,
        teacherNo: u.teacherNo,
        teacherName: u.teacherName,
        loginPassword: u.loginPassword,
        manager: u.manager,
        male: u.male,
        phone: u.phone,
        title: u.title,
        intro: u.intro
      }
    }
  },
  mounted: function() {
    if (this.user != null)
      Axios({
        method: 'get',
        url: '/user',
        params: {
          count: 1000
        }
      }).then(response => {
        this.users = response.data.users.filter(
          u => u.id && u.id != this.user.id
        )
      })
  }
}
</script>

<style scoped>
input {
  width: 200px;
}
table {
  width: auto;
}
#users {
  padding-left: 15%;
  width: 65%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
