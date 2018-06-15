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
import Week from './Week'
import Exams from './Exams'

export default {
  props: {
    user: Object
  },
  components: {
    calendar: Week,
    exam: Exams
  }
}
</script>

<style scoped>
</style>
