<template>
  <el-tabs type="border-card">
    <el-tab-pane label="监考周历">
      <calendar :user="user" />
    </el-tab-pane>
    <el-tab-pane label="监考列表">
      <exam :user="user" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import WeekCalendar from './WeekCalendar.vue'
import OwnExamTable from './OwnExamTable.vue'

export default {
  props: {
    user: Object
  },
  components: {
    calendar: WeekCalendar,
    exam: OwnExamTable
  }
}
</script>

<style scoped>
</style>
