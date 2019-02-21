<template>
  <div>
    <el-row>
      <el-col :span="20">

        分词词典原来{{old}}个词条，新增加{{newWord}}个词条。<br>
        模型概率：{{probability}}

      </el-col>
    </el-row>

    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subNextStep">查看样本</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
    export default {
        name: 'ResultScore',
        data() {
            return {
              old:"",
              newWord:"",
              probability:""
            }
        },
        methods: {
          subNextStep(){
            this.Request({
              url:'/model/segment/sample',
              method:'get'
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',responseData.result);
              }
            })
          }
        },
        mounted:function(){
          this.old = this.$route.params.result.old
          this.newWord = this.$route.params.result.new
          this.probability = this.$route.params.result.probability
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-row{
    margin:20px 0 0 0;
  }

  .el-col{
    margin-top:10px;
    text-align: left;
  }
  .el-radio{
    margin-top:10px;
    margin-bottom:10px;
  }

  .base-modal-btns{
    text-align: center;
    margin-top:40px;
  }
</style>
