<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">
      <span style="height:16px !important;">
        首页
      </span>
      </el-breadcrumb-item>
      <el-breadcrumb-item :key="headerItem" v-for="headerItem in headerItems">{{headerItem}}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>

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
    padding:25px 0 25px 10px;
    background-color: #F5F5F5;
    margin:0 0 0 10px;

  }
  .el-breadcrumb-item{
    color:#448aff;
  }

  .fa-icon {
    width:16px;
    height:16px;
  }
</style>

