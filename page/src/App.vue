<template>
  <div id="app">
    <el-container>
      <el-header>
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
          <el-menu-item index="/">主页</el-menu-item>
          <el-menu-item index="/user">人员管理</el-menu-item>
          <el-menu-item index="/exam">考试管理</el-menu-item>
          <el-submenu index="/task">
            <template slot="title">任务中心</template>
            <el-menu-item index="/task/2-1">选项1</el-menu-item>
            <el-menu-item index="/task/2-2">选项2</el-menu-item>
            <el-menu-item index="/task/2-3">选项3</el-menu-item>
            <el-submenu index="/task/2-4">
              <template slot="title">选项4</template>
              <el-menu-item index="/task/2-4-1">选项1</el-menu-item>
              <el-menu-item index="/task/2-4-2">选项2</el-menu-item>
              <el-menu-item index="/task/2-4-3">选项3</el-menu-item>
            </el-submenu>
          </el-submenu>
        </el-menu>
        <span>
          <login :user="user" />
        </span>
      </el-header>
      <el-main>
        <router-view :user="user" />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import MyLogin from '@/components/Login'
import Bus from '@/util/Bus'

export default {
  components: {
    login: MyLogin
  },
  data: function() {
    return {
      user: null,
      activeIndex: ''
    }
  },
  watch: {
    $route(to, from) {
      this.activeIndex = to.path
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      this.$router.push({ path: key })
    }
  },
  beforeCreate: function() {
    Bus.$on(Bus.login, user => {
      this.user = user
      this.$router.push({ path: '/' })
    })
    Bus.$on(Bus.logout, () => {
      this.user = null
      this.$router.push({ path: '/' })
    })
  }
}
</script>

<style scoped>
header {
  color: aliceblue;
  font-size: 20px;
  background-color: #545c64;
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
#app {
  margin: 0;
  padding: 0;
  font-family: Helvetica, sans-serif;
  text-align: left;
}
</style>
