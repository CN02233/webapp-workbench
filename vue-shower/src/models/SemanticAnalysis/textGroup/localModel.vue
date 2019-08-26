<template>
  <WorkMain :headerItems="['NLP基础','方言模式']">
    <el-row >
      <el-col :span="5"></el-col>
      <el-col :span="14">
        <el-input
          type="textarea"
          :autosize="{ minRows: 10}"
          placeholder="请输入内容"
          v-model="textAreaVal">
        </el-input>
      </el-col>
      <el-col :span="5"></el-col>
    </el-row>

    <el-row >
      <el-col :span="24">
        <el-button type="primary" @click="subTextSimilarity">提交</el-button>
        <el-button type="primary">重置</el-button>
      </el-col>
    </el-row>
  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

  export default {
    name: 'LocalModel',
    data() {
      return {
        textAreaVal:""
      }
    },
    components: {
      WorkMain
    },
    methods: {
      subTextSimilarity(){
        const sendData = {"text":this.textAreaVal}
        this.Request({
          url:'/topolect',
          method:'post',
          data:sendData
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            this.$router.push({"name":"localModelResult","params":{"responseData":response.result}})
          }
        })
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-col{
    margin-top:10px;
    text-align: center;
  }
  .el-radio{
    margin-top:10px;
    margin-bottom:10px;
  }
  /*.el-row{*/
    /*margin:40px 0 0 0;*/
  /*}*/
</style>
