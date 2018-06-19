<template>
  <div>
    <div style="width:75%">
      <el-table :data="[{}]" :show-header="false">
        <el-table-column type="expand" @expand-change="setExpand">
          <template slot-scope="scope">
            <div style="padding-right:66%">
              <el-form label-position="left" ref="form" :model="condition" :disabled="false" :inline="true">
                <el-form-item label="教学楼：" prop="area">
                  <el-select v-model="condition.area" placeholder="请选择">
                    <el-option v-for="(i,index) in ['丹青楼','锦绣楼','成栋楼']" :key="index" :value="i" :label="i" />
                  </el-select>
                </el-form-item>
                <el-form-item label="教室号：" prop="classroomNo">
                  <el-input v-model="condition.classroomNo" minlength="3" max="4" />
                </el-form-item>
              </el-form>
            </div>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <span>起止日期：</span>
            <el-date-picker v-model="pickDate" type="daterange" @change="setDate" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <el-button type="primary" :loading="submitting" @click="retrieve" size="small">确 认</el-button>
          </template>
        </el-table-column>
      </el-table>
      <br>
      <el-table v-if="exams&&exams.length" :data="exams">
        <el-table-column type="index" />
        <el-table-column label="科目">
          <template slot-scope="scope">
            <span>{{scope.row.course}}</span>
          </template>
        </el-table-column>
        <el-table-column label="教室">
          <template slot-scope="scope">
            <span>{{scope.row.area+'-'+scope.row.classroomNo}}</span>
          </template>
        </el-table-column>
        <el-table-column label="日期">
          <template slot-scope="scope">
            <span>{{scope.row.examDate.year+'-'+scope.row.examDate.month+'-'+scope.row.examDate.day}}</span>
          </template>
        </el-table-column>
        <el-table-column label="时间">
          <template slot-scope="scope">
            <span>{{'第'+scope.row.begNo+'-'+scope.row.endNo+'节'}}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import Regexp from '@/util/Regexps'
import Axios from '@/util/Axios'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      expand: false,
      submitting: false,
      pickDate: ['', ''],
      condition: {
        teacherNo: null,
        begDate: null,
        endDate: null,
        area: null,
        classroomNo: null,
        begNo: 1,
        endNo: 12,
        count: 1000
      },
      rules: {
        area: [
          { pattern: Regexp.exam.area, message: '错误的格式', trigger: 'blur' }
        ],
        classroomNo: [
          {
            pattern: Regexp.exam.classroomNo,
            message: '错误的格式',
            trigger: 'blur'
          }
        ]
      },
      exams: null
    }
  },
  mounted: function() {
    if (this.user) this.condition.teacherNo = this.user.teacherNo
  },
  methods: {
    setExpand() {
      this.expand = !this.expand
      if (!this.expand) this.condition.area = this.condition.classroomNo = null
    },
    setDate() {
      if (this.pickDate && this.pickDate.length) {
        var oneday = 24 * 60 * 60 * 1000
        this.condition.begDate = new Date(this.pickDate[0].valueOf() + oneday)
          .toISOString()
          .split('T')[0]
        this.condition.endDate = new Date(this.pickDate[1].valueOf() + oneday)
          .toISOString()
          .split('T')[0]
      }
    },
    getCurrentNo() {
      var now = new Date(Date.now())
      if (now.getHours() < 8) return 1
      else if (now.getHours() <= 8 && now.getMinutes() <= 50) return 2
      else if (now.getHours() <= 9 && now.getMinutes() <= 55) return 3
      else if (now.getHours() <= 10 && now.getMinutes() <= 45) return 4
      else if (now.getHours() <= 13 && now.getMinutes() <= 40) return 5
      else if (now.getHours() <= 14 && now.getMinutes() <= 30) return 6
      else if (now.getHours() <= 15 && now.getMinutes() <= 35) return 7
      else if (now.getHours() <= 16 && now.getMinutes() <= 25) return 8
      else if (now.getHours() < 18) return 9
      else if (now.getHours() <= 18 && now.getMinutes() <= 50) return 10
      else if (now.getHours() <= 19 && now.getMinutes() <= 55) return 11
      else if (now.getHours() <= 20 && now.getMinutes() <= 45) return 12
    },
    retrieve() {
      this.expand = false
      this.submitting = true
      var valid =
        this.user && this.user.id && this.pickDate && this.pickDate.length
      if (this.expand) this.$refs['form'].validate(v => (valid = valid && v))
      if (!valid) return
      Axios({
        url: '/examination',
        method: 'get',
        params: this.condition
      })
        .then(response => {
          if (!this.expand)
            this.condition.area = this.condition.classroomNo = null
          this.submitting = false
          this.exams = response.data.examinations
          this.$notify({
            title: '数据已更新',
            type: 'success',
            duration: 5000,
            position: 'top-right'
          })
        })
        .catch(error => {
          if (!this.expand)
            this.condition.area = this.condition.classroomNo = null
          this.submitting = false
          if (error.response && error.response.status === 404)
            this.$notify({
              title: '未找到符合条件的记录',
              type: 'info',
              duration: 5000,
              position: 'top-right'
            })
          else
            this.$notify({
              title: '未知的错误',
              type: 'error',
              duration: 5000,
              position: 'top-right'
            })
        })
    }
  }
}
</script>

<style scoped>
</style>
