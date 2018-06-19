<template>
  <div style="padding-left:3%">
    <el-tabs tab-position="top" style="width: 1000px">
      <el-tab-pane label="新增考试">
        <div style="width:500px">
          <el-card shadow="always">
            <el-form ref="newlyForm" :rules="newlyRules" inline :model="newly">
              <el-form-item label="科目" prop="course">
                <el-input v-model="newly.course"></el-input>
              </el-form-item>
              <el-form-item label="教学楼" prop="area">
                <el-select v-model="newly.area">
                  <el-option v-for="a in ['丹青楼','锦绣楼','成栋楼']" :key="a" :value="a" />
                </el-select>
              </el-form-item>
              <el-form-item label="教室号" prop="classroomNo">
                <el-input v-model="newly.classroomNo" />
              </el-form-item>
              <el-form-item label="日期" prop="examDate">
                <el-date-picker v-model="newlyDate" @change="()=>this.newly.examDate=new Date(this.newlyDate.valueOf()+86400000).toISOString().split('T')[0]" />
              </el-form-item>
              <el-form-item :label=" newlyNoLabel ">
                <div style="width:420px;padding-left:16px ">
                  <el-slider v-model="newlyNo " height="200px " range show-stops :min="1 " :max="12 ">
                  </el-slider>
                </div>
              </el-form-item>
            </el-form>
            <el-button @click="submitNewly">提交</el-button>
          </el-card>
        </div>
      </el-tab-pane>
      <el-tab-pane label="安排监考 ">
        <div style="width:500px ">
          <el-card class="box-card ">
            <div slot="header">
              <span>筛选</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="submitCondition">确认</el-button>
            </div>
            <el-form :rules="conditionRules" size="small" ref="conditionForm" inline :model="condition">
              <el-collapse>
                <el-collapse-item title="考试时间 " name="time ">
                  <el-form-item label="起止日期： ">
                    <el-date-picker type="daterange" v-model="conditionRange" range-separator="至 " start-placeholder="开始日期 " end-placeholder="结束日期 "></el-date-picker>
                  </el-form-item>
                  <el-form-item label="起止节数： ">
                    <span>{{conditionNoLabel}}</span>
                    <div style="width:420px;padding-left:16px ">
                      <el-slider height="200px " v-model="conditionNo" range show-stops :min="1 " :max="12 ">
                      </el-slider>
                    </div>
                  </el-form-item>
                </el-collapse-item>
                <el-collapse-item title="考试地点 " name="location ">
                  <el-form-item label="教学楼： " prop="area ">
                    <el-select v-model="condition.area ">
                      <el-option v-for="a in [ '丹青楼', '锦绣楼', '成栋楼'] " :key="a " :value="a " />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="教室号： " prop="classroomNo">
                    <el-input v-model="condition.classroomNo " />
                  </el-form-item>
                </el-collapse-item>
              </el-collapse>
            </el-form>
          </el-card>
        </div>
        <div style="width:800px">
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
            <el-table-column label="监考">
              <template slot-scope="scope">
                <el-button type="info" size="small" @click="setAction(scope.row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    <el-dialog title="修改监考信息" width="50%" style="margin-right:100px" :visible.sync="operating" :before-close="removeAction">
      <el-transfer :titles="['全部教师', '监考教师']" v-model="selected" v-if="action" :data="users" @change="checkConflict" :props="{
        key: 'id',
        label: 'teacherName'
      }"></el-transfer>
      <span slot="footer" class="dialog-footer">
        <el-button @click="removeAction">取 消</el-button>
        <el-button type="primary">确 定</el-button>
      </span>
    </el-dialog>
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
      rendered: false,
      activeNewly: false,
      newlyDate: null,
      newly: {
        course: null,
        examDate: null,
        begNo: 1,
        endNo: 12,
        area: null,
        classroomNo: null
      },
      newlyRules: {
        area: [{ required: true, message: '请选择', trigger: 'change' }],
        course: [{ required: true, message: '请选择', trigger: 'blur' }],
        classroomNo: [
          {
            pattern: Regexps.exam.classroomNo,
            message: '错误的格式',
            trigger: 'blur'
          },
          { required: true, message: '请填写', trigger: 'blur' }
        ],
        examDate: [
          { required: true, message: '请选择', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (new Date(value).valueOf() < Date.now() - 86400000)
                callback(new Error('过去的日期'))
              else callback()
            },
            trigger: 'blur'
          }
        ]
      },
      condition: {
        begDate: null,
        endDate: null,
        area: null,
        classroomNo: null,
        begNo: 1,
        endNo: 12,
        count: 1000
      },
      conditionRules: {
        classroomNo: [
          {
            pattern: Regexps.exam.classroomNo,
            message: '错误的格式',
            trigger: 'blur'
          }
        ]
      },
      exams: [],
      action: null,
      users: [],
      selected: [],
      supervisors: []
    }
  },
  computed: {
    operating() {
      return this.action !== null
    },
    newlyNoLabel() {
      if (this.newlyNo[0] === this.newlyNo[1])
        return '时间(第' + this.newlyNo[0] + '节)'
      else return '时间(第' + this.newlyNo[0] + '-' + this.newlyNo[1] + '节)'
    },
    newlyNo: {
      get: function() {
        return [this.newly.begNo, this.newly.endNo]
      },
      set: function(val) {
        this.newly.begNo = val[0]
        this.newly.endNo = val[1]
      }
    },
    conditionNoLabel() {
      if (this.conditionNo[0] === this.conditionNo[1])
        return '第' + this.conditionNo[0] + '节'
      else return '第' + this.conditionNo[0] + '-' + this.conditionNo[1] + '节'
    },
    conditionNo: {
      get: function() {
        return [this.condition.begNo, this.condition.endNo]
      },
      set: function(val) {
        this.condition.begNo = val[0]
        this.condition.endNo = val[1]
      }
    },
    conditionRange: {
      get: function() {
        if (!this.condition.begDate) return [null, null]
        var beg = this.condition.begDate.split('-')
        var end = this.condition.endDate.split('-')
        return [
          new Date(Number(beg[0]), Number(beg[1] - 1), Number(beg[2])),
          new Date(Number(end[0]), Number(end[1] - 1), Number(end[2]))
        ]
      },
      set: function(val) {
        this.condition.begDate = new Date(val[0].valueOf() + 86400000)
          .toISOString()
          .split('T')[0]
        this.condition.endDate = new Date(val[1].valueOf() + 86400000)
          .toISOString()
          .split('T')[0]
      }
    }
  },
  methods: {
    submitNewly() {
      if (!this.user || !this.user.manager) return
      this.$refs['newlyForm'].validate(valid => {
        if (valid) {
          if (!this.validNewlyNo()) {
            this.$message({
              type: 'error',
              message: '考试时间无法间隔'
            })
            return
          }
          Axios({
            url: '/examination',
            method: 'post',
            params: this.newly
          })
            .then(response => {
              this.$notify({
                type: 'success',
                title: '新增成功',
                duration: 5000,
                position: 'top-right'
              })
            })
            .catch(error => {
              if (error.response && error.response.status === 422)
                this.$notify({
                  type: 'error',
                  title: '存在冲突',
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
        }
      })
    },
    submitCondition() {
      this.$refs['conditionForm'].validate(valid => {
        if (valid) {
          Axios({
            url: '/examination',
            method: 'get',
            params: this.condition
          })
            .then(response => {
              this.exams = response.data.examinations
              this.$notify({
                type: 'success',
                title: '数据已更新',
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
        }
      })
    },
    validNewlyNo() {
      if (this.newly.begNo <= 4) return this.newly.endNo <= 4
      else if (this.newly.begNo <= 8) return 4 < this.newly.endNo <= 8
      else return 8 < this.newly.endNo <= 12
    },
    validConditionNo() {
      if (this.condition.begNo <= 4) return this.condition.endNo <= 4
      else if (this.condition.begNo <= 8) return 4 < this.condition.endNo <= 8
      else return 8 < this.condition.endNo <= 12
    },
    removeAction() {
      this.action = null
    },
    setAction(exam) {
      this.action = exam
      Vue.set(exam, 'supervisors')
      Axios({
        url: '/user',
        method: 'get',
        params: {
          count: 1000,
          orderBy: 'busyness',
          asc: true
        }
      }).then(response => {
        this.users = response.data.users
        Axios({
          url: '/supervisor',
          method: 'get',
          params: {
            examId: this.action.id
          }
        }).then(response => {
          this.selected = response.data.supervisors.map(usr => usr.id)
          this.supervisors = response.data.supervisors
        })
      })
    },
    checkConflict() {
      this.selected
        .map(id => this.users.find(usr => usr.id === id))
        .filter(
          usr => this.supervisors.find(item => item.id === usr.id) === undefined
        )
        .forEach(usr => {
          var date = new Date(
            this.action.examDate.year,
            this.action.examDate.month - 1,
            this.action.examDate.day + 1
          )
            .toISOString()
            .split('T')[0]
          Axios({
            url: '/examination',
            method: 'get',
            params: {
              begDate: date,
              endDate: date,
              begNo: this.action.begNo,
              endNo: this.action.endNo,
              teacherNo: usr.teacherNo
            }
          }).then(response => {
            this.$notify({
              type: 'info',
              title: usr.teacherName + '的时间存在冲突',
              duration: 5000,
              position: 'top-right'
            })
          })
        })
    }
  }
}
</script>

<style>
</style>
