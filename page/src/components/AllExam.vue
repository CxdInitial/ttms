<template>
  <el-tabs type="border-card">
    <el-tab-pane label="监考月历">
      <calendar :user="user" />
    </el-tab-pane>
    <el-tab-pane label="监考列表"></el-tab-pane>
  </el-tabs>
</template>

<script>
import Month from './Month'

export default {
  props: {
    user: Object
  },
  components: {
    calendar: Month
  }
}
</script>

<style>
</style>
