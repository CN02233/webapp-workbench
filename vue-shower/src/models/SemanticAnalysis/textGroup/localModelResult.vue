<template>
  <WorkMain :headerItems="['NLP基础','方言模式']">
    <el-row >
      <el-col style="text-align: right;" :span="3">分词结果：</el-col>
      <el-col :span="20">{{responseData}}</el-col>
    </el-row>

  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'LocalModelResult',
    props:['responseData'],
    data() {
      return {
        responseData:{}
      }
    },
    components: {
      WorkMain
    },
    methods: {},
    mounted:function(){
      this.responseData = this.$route.params.responseData
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-row{
    margin:20px 0 0 0;
  }

  .el-col{
    text-align: left;
  }
</style>
