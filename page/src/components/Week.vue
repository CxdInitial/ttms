<template>
  <div>
    <div style="margin:25px 10px">
      <span>请选择周：</span>
      <el-date-picker :picker-options="option" date-picker="1" @change="setDate" v-model="pickDate" type="week" format="yyyy年 第W周" placeholder="选择周" />
      <p>
        <span>当前：</span>{{this.condition.begDate+'到'+this.condition.endDate}}</p>
    </div>
    <el-table :data="exams" :row-style="setBorder" border>
      <el-table-column width="70" type="index" :index="indexStr" />
      <el-table-column v-for="(s,i) in ['星期一','星期二','星期三','星期四','星期五','星期六','星期日']" :key="i" :label="s">
        <template slot-scope="scope ">
          <el-table v-if="scope.row[i].length" @cell-click="showExam" :data="scope.row[i]" max-height="120px" :show-header="false" :cell-class-name="setColor" :cell-style="setSize">
            <el-table-column>
              <template slot-scope="inner">
                <span>{{inner.row.course+'('+inner.row.area+'-'+inner.row.classroomNo+')'}}</span>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import Colors from '@/style/Colors'
import Axios from '@/util/Axios'
import '@/style/cell.css'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      option: {
        firstDayOfWeek: 1
      },
      pickDate: new Date(Date.now()),
      colors: Colors,
      condition: {
        teacherNo: 0,
        begDate: '',
        endDate: '',
        count: '1000'
      },
      exams: []
    }
  },
  methods: {
    retrieve() {
      if (!this.user || !this.user.id) return
      this.exams.forEach(week => {
        for (var i = 0; i < 7; i++) week[i] = new Array()
      })
      Axios({
        url: '/examination',
        method: 'get',
        params: this.condition
      })
        .then(response => {
          response.data.examinations.forEach((exam, index) => {
            Object.defineProperty(exam, 'color', {
              value: this.colors[index % this.colors.length],
              writable: true,
              enumerable: true,
              configurable: true
            })
            var i =
              exam.examDate.day - Number(this.condition.begDate.split('-')[2])
            var offset
            if (exam.begNo <= 4) offset = -1
            else if (exam.begNo <= 8) offset = 0
            else offset = 1
            for (var j = exam.begNo; j <= exam.endNo; j++)
              this.exams[j + offset][i].push(exam)
          })
          this.exams = this.exams.filter(() => true)
          this.$notify({
            title: '数据已更新',
            type: 'success',
            duration: 5000,
            position: 'top-right'
          })
        })
        .catch(error => {
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
    },
    setDate() {
      var oneDay = 24 * 60 * 60 * 1000
      var l = Date.parse(this.pickDate)
      this.condition.begDate = new Date(l).toISOString().split('T')[0]
      this.condition.endDate = new Date(l + 6 * oneDay)
        .toISOString()
        .split('T')[0]
      this.retrieve()
    },
    indexStr(index) {
      if (index <= 3) return '第' + (index + 1) + '节'
      else if (index >= 5 && index <= 8) return '第' + index + '节'
      else if (index > 9) return '第' + (index - 1) + '节'
    },
    getIndex(i) {
      if (i <= 3) return '第' + (i + 1) + '节'
      else if (i === 4) return ''
      else if (i <= 8) return '第' + i + '节'
      else if (i === 9) return ''
      else return '第' + (i + 1) + '节'
    },
    setColor(cell) {
      return cell.row.color.class
    },
    setSize(cell) {
      return {
        padding: 0,
        'font-size': '10px'
      }
    },
    setBorder(item) {
      if (item.rowIndex === 4 || item.rowIndex === 9)
        return {
          'background-color': '#f5f7fa'
        }
    },
    showExam(row, column, cell, event) {
      this.$message({
        type: 'info',
        message:
          row.course +
          ' (' +
          row.area +
          '-' +
          row.classroomNo +
          ') ' +
          '考试时间: 第' +
          row.begNo +
          '-' +
          row.endNo +
          '节',
        customClass: row.color.class
      })
    }
  },
  created: function() {
    if (this.user) this.condition.teacherNo = this.user.id
    this.exams = new Array(14)
    for (var i = 0; i < 14; i++) {
      this.exams[i] = new Array(7)
      for (var j = 0; j < 7; j++) this.exams[i][j] = new Array()
    }
    var oneDay = 24 * 60 * 60 * 1000
    var now = Date.now()
    var no = new Date(now).getDay()
    var mon = new Date(Math.round(now - (no - 1) * oneDay))
    if (mon.getDay() == 2) mon = new Date(Math.round(now - no * oneDay))
    this.condition.begDate = mon.toISOString().split('T')[0]
    var sun = new Date(Math.round(now + (7 - no) * oneDay))
    if (sun.getDay() == 1) sun = new Date(Math.round(now + (6 - no) * oneDay))
    this.condition.endDate = sun.toISOString().split('T')[0]
  },
  mounted: function() {
    this.retrieve()
  }
}
</script>
<style scoped>
</style>
