<template>
  <el-tabs type="border-card">
    <el-tab-pane label="考试月历">
      <calendar :user="user" />
    </el-tab-pane>
    <el-tab-pane label="考试列表">
      <exams :user="user" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import FullExamTable from './FullExamTable'
import MonthCanlendar from './MonthCanlendar'

export default {
  props: {
    user: Object
  },
  components: {
    calendar: MonthCanlendar,
    exams: FullExamTable
  }
}
</script>

<style>
</style>
