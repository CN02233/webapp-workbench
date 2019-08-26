<template>
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }"><span style="#448aff">首页</span></el-breadcrumb-item>
    <el-breadcrumb-item :key="headerItem" v-for="headerItem in headerItems">{{headerItem}}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
  export default {
    name: 'WorkMainHeader',
    props:{
      headerItems:{
        type:Array
      }
    },
    data() {
      return {}
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-breadcrumb{
    margin-top:15px;
    margin-bottom:10px;

  }
  .el-breadcrumb-item{
    color:#448aff;
  }

</style>

