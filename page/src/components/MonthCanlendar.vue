<template>
  <div style="width:1050px">
    <calendar :events="events" @eventClick="showExam" @changeMonth="setDate" firstDay="1" lang="zh" />
    <el-dialog title="监考信息" :visible.sync="dialogVisible" :before-close="hideExam">
      <el-card shadow="always">
        <el-form v-if="action">
          <el-form-item label="科目">{{action.course}}</el-form-item>
          <el-form-item label="日期">{{action.examDate.year+'-'+action.examDate.month+'-'+action.examDate.day}}</el-form-item>
          <el-form-item label="时间">{{'第'+action.begNo+'-'+action.endNo+'节'}}</el-form-item>
          <el-form-item label="考场">{{action.area+'-'+action.classroomNo}}</el-form-item>
          <el-form-item label="监考老师" v-if="action.supervisors&&action.supervisors.length">
            <el-table :data="action.supervisors">
              <el-table-column type="expand">
                <template slot-scope="inner">
                  <el-form label-position="left">
                    <el-form-item label="职称">
                      <span style="font-size:12px">{{inner.row.title}}</span>
                    </el-form-item>
                    <el-form-item label="管理员">
                      <span style="font-size:12px">{{inner.row.manager?'是':'否'}}</span>
                    </el-form-item>
                    <el-form-item label="个人简介">
                      <span style="font-size:12px">{{inner.row.intro}}</span>
                    </el-form-item>
                  </el-form>
                </template>
              </el-table-column>
              <el-table-column label="姓名">
                <template slot-scope="inner">
                  {{inner.row.teacherName}}
                </template>
              </el-table-column>
              <el-table-column label="学工号">
                <template slot-scope="inner">
                  {{inner.row.teacherNo}}
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="监考老师" v-else>无 </el-form-item>
        </el-form>
      </el-card>
    </el-dialog>
  </div>
</template>

<script>
import fullCalendar from 'vue-fullcalendar'
import Axios from '@/util/Axios'
import Vue from 'vue'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      events: [],
      dialogVisible: false,
      action: null,
      condition: {
        teacherNo: null,
        begDate: null,
        endDate: null,
        area: null,
        classroomNo: null,
        begNo: 1,
        endNo: 12,
        count: 1000
      }
    }
  },
  components: {
    calendar: fullCalendar
  },
  created: function() {
    var date = new Date()
    this.condition.begDate = new Date(date.getFullYear(), date.getMonth(), 1)
      .toISOString()
      .split('T')[0]
    this.condition.endDate = new Date(
      date.getFullYear(),
      date.getMonth() + 1,
      0
    )
      .toISOString()
      .split('T')[0]
  },
  mounted: function() {
    this.retrieve()
  },
  methods: {
    retrieve() {
      if (!this.user || !this.user.id) return
      Axios({
        url: '/examination',
        method: 'get',
        params: this.condition
      })
        .then(response => {
          this.events = new Array()
          response.data.examinations.forEach(exam => {
            var date =
              exam.examDate.year +
              '-' +
              exam.examDate.month +
              '-' +
              exam.examDate.day
            this.events.push({
              data: exam,
              cssClass: 'el-table__body',
              title:
                exam.course + ' (第' + exam.begNo + '-' + exam.endNo + '节)',
              start: date,
              end: date
            })
          })
        })
        .catch(error => {
          if (error.response && error.response.status === 404)
            this.$notify({
              type: 'info',
              title: '未找到符合条件的记录',
              duration: 5000,
              position: 'top-right'
            })
        })
    },
    showExam(event, jsEvent, pos) {
      this.dialogVisible = true
      this.action = event.data
      Axios({
        url: '/supervisor',
        method: 'get',
        params: {
          examId: event.data.id
        }
      })
        .then(response => {
          Vue.set(this.action, 'supervisors', response.data.supervisors)
        })
        .catch(error => {
          if (error.response && error.response.status === 404)
            Vue.set(this.action, 'supervisors', response.data.supervisors)
          else
            this.$notify({
              type: 'error',
              title: '发生了未知的错误',
              duration: 5000,
              position: 'top-right'
            })
        })
    },
    hideExam() {
      this.action = false
      this.dialogVisible = false
    },
    setDate(beg, end, current) {
      this.condition.begDate = beg
      this.condition.endDate = end
      this.retrieve()
    }
  }
}
</script>

<style scoped>
</style>
