<template>
  <div id="app">
    <el-container>
      <el-header>
        <ul>
        </ul>
        <span>
          <login/>
        </span>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import MyLogin from '@/components/Login'

export default {
  components: {
    login: MyLogin
  },
  data: function() {
    return {}
  }
}
</script>

<style>
header {
  color: aliceblue;
  font-size: 20px;
  background-color: #2f2f2f;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
header span {
  margin-right: 5%;
}
header ul {
  margin-left: 5%;
}
body {
  margin: 0;
  padding: 0;
}
#app {
  margin: 0;
  padding: 0;
  font-family: Helvetica, sans-serif;
  text-align: left;
}
</style>
