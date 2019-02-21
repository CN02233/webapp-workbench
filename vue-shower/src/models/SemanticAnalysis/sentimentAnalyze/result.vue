<template>
  <WorkMain class="sentiment-analyze-root" :headerItems="['情感分析']">
    <el-row class="update-word-row">
      <el-col :span="4">文档内容：</el-col>
      <el-col :span="18">{{resultData.content}}</el-col>
    </el-row>
    <el-row class="update-word-row">
      <el-col :span="4">情感分析结果：</el-col>
      <el-col :span="18">{{resultData.sentiment}}</el-col>
    </el-row>
  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'SentimentAnalyzeResult',
    components: {
      WorkMain
    },
    data() {
        return {
          resultData : this.$route.params.resultData
        }
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .update-word-row:first-child{
    margin-top:50px;
  }

  .update-word-row{
    margin-bottom:30px;
  }
</style>
