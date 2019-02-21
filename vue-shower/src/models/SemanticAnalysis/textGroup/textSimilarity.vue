<template>
  <WorkMain :headerItems="['NLP基础','文本相似度']">
    <el-row>
      <el-col :span="20">
        <el-col style="text-align: center;" :span="5">
          文本1
        </el-col>
        <el-col :span="18">
          <el-input
            type="textarea"
            :autosize="{ minRows: 5}"
            placeholder="请输入内容"
            v-model="textAreaVal1">
          </el-input>
        </el-col>

      </el-col>
    </el-row>

    <el-row>
      <el-col :span="20">
        <el-col style="text-align: center;" :span="5">
          文本2
        </el-col>
        <el-col :span="18">
          <el-input
            type="textarea"
            :autosize="{ minRows: 5}"
            placeholder="请输入内容"
            v-model="textAreaVal2">
          </el-input>
        </el-col>
      </el-col>
    </el-row>

    <el-row >
      <el-col style="text-align: center" :span="24">
        <el-button type="primary" @click="subTextSimilarity">提交</el-button>
        <el-button type="primary">重置</el-button>
      </el-col>
    </el-row>
  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

  export default {
    name: 'TextSimilarity',
    data() {
        return {
          textAreaVal1:"",
          textAreaVal2:""
        }
    },
    components: {
      WorkMain
    },
    methods: {
      subTextSimilarity(){
        const sendData = {"text1":this.textAreaVal1,"text2":this.textAreaVal2}
        this.Request({
          url:'/similar',
          method:'post',
          data:sendData
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            this.$router.push({"name":"textSimilarityResult","params":{"responseData":response.result}})
          }
        })
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-col{
    margin-top:10px;
    text-align: left;
  }
  .el-radio{
    margin-top:10px;
    margin-bottom:10px;
  }
</style>
