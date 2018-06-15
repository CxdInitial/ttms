<template>
  <div>
    <el-form>
      <el-form-item label="起止日期：">
        <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item></el-form-item>
      <el-form-item></el-form-item>
      <el-form-item></el-form-item>
      <el-form-item></el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data: function() {
    return {
      pickDate: ['', '']
    }
  }
}
</script>

<style scoped>
</style>
