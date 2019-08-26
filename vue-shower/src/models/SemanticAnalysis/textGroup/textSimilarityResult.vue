<template>
  <WorkMain :headerItems="['NLP基础','文本相似度']">
    <el-row >
      <el-col style="text-align: right;" :span="3">相似度：</el-col>
      <el-col :span="20">{{responseData.similar}}</el-col>
    </el-row>

    <el-row >
      <el-col style="text-align: right;" :span="3">文本1：</el-col>
      <el-col :span="20">{{responseData.text1}}</el-col>
    </el-row>

    <el-row >
      <el-col style="text-align: right;" :span="3">文本2：</el-col>
      <el-col :span="20">{{responseData.text2}}</el-col>
    </el-row>

  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'TextSimilarityResult',
    props:['responseData'],
    data() {
        return {
          responseData:{}
        }
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
