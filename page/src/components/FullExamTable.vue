<template>
  <div>
    <div style="width:500px">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>筛选</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="submit">确认</el-button>
        </div>
        <el-form size="small" :rules="rules" inline :model="condition" ref="form">
          <el-collapse v-model="activedPart">
            <el-collapse-item title="考试时间" name="time">
              <el-form-item label="起止日期：">
                <el-date-picker v-model="pickDate" @change="setRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
              </el-form-item>
              <el-form-item label="起止节数：">
                <span>{{'第'+condition.begNo+'-'+condition.endNo+'节'}}</span>
                <div style="width:420px;padding-left:16px">
                  <el-slider v-model="pickNo" @change="setNo" height="200px" range show-stops :min="1" :max="12">
                  </el-slider>
                </div>
              </el-form-item>
            </el-collapse-item>
            <el-collapse-item title="监考老师" name="supervisor">
              <el-form-item label="工号：" prop="teacherNo">
                <el-input v-model="condition.teacherNo" />
              </el-form-item>
            </el-collapse-item>
            <el-collapse-item title="考试地点" name="location">
              <el-form-item label="教学楼：" prop="area">
                <el-select v-model="condition.area">
                  <el-option v-for="a in ['丹青楼','锦绣楼','成栋楼']" :key="a" :value="a" />
                </el-select>
              </el-form-item>
              <el-form-item label="教室号：" prop="classroomNo">
                <el-input v-model="condition.classroomNo" />
              </el-form-item>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </el-card>
    </div>
    <div style="width:800px">
      <el-table v-if="exams&&exams.length" :data="exams" @expand-change="setSupervisors">
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
        <el-table-column type="expand" label="监考老师">
          <template slot-scope="scope">
            <el-card v-if="scope.row.supervisors&&scope.row.supervisors.length" shadow="always">
              <el-table :data="scope.row.supervisors">
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
            </el-card>
            <el-card v-else shadow="always">
              <span>暂未安排监考老师</span>
            </el-card>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import Regexps from '@/util/Regexps'
import Axios from '@/util/Axios'
import Vue from 'vue'

export default {
  props: {
    user: Object
  },
  data: function() {
    return {
      dialogVisible: false,
      pickDate: ['', ''],
      pickNo: [1, 12],
      activedPart: [],
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
          { pattern: Regexps.exam.area, message: '错误的格式', trigger: 'blur' }
        ],
        classroomNo: [
          {
            pattern: Regexps.exam.classroomNo,
            message: '错误的格式',
            trigger: 'blur'
          }
        ],
        teacherNo: [
          {
            pattern: Regexps.user.teacherNo,
            message: '错误的格式',
            trigger: 'blur'
          }
        ]
      },
      exams: null
    }
  },
  methods: {
    setRange() {
      var oneday = 24 * 60 * 60 * 1000
      this.condition.begDate = new Date(this.pickDate[0].valueOf() + oneday)
        .toISOString()
        .split('T')[0]
      this.condition.endDate = new Date(this.pickDate[1].valueOf() + oneday)
        .toISOString()
        .split('T')[0]
    },
    setNo() {
      this.condition.begNo = this.pickNo[0]
      this.condition.endNo = this.pickNo[1]
    },
    submit() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          Axios({
            url: '/examination',
            method: 'get',
            params: this.condition
          })
            .then(response => {
              this.exams = response.data.examinations
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
                  type: 'error',
                  title: '未知的错误',
                  duration: 5000,
                  position: 'top-right'
                })
            })
          this.$refs['form'].resetFields()
        }
      })
    },
    setSupervisors(row, expandedRow) {
      Axios({
        url: '/supervisor',
        method: 'get',
        params: {
          examId: row.id
        }
      })
        .then(response => {
          console.log(response.data)
          Vue.set(row, 'supervisors', response.data.supervisors)
        })
        .catch(error => {
          if (error.response && error.response.status === 404)
            Vue.set(row, 'supervisors', [])
        })
    }
  }
}
</script>

<style scoped>
</style>
