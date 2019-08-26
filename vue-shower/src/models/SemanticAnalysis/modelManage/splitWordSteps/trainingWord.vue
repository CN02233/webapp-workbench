<template>
  <div>
    <el-row>
      <el-col :span="20">

        语料验证：语料库<span style="font-weight: bold;color:red;">{{corpus}}</span>的验证结果是<span style="font-weight: bold;color:blue">{{valid}}</span>

      </el-col>
    </el-row>

    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subNextStep">下一步</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
    export default {
        name: 'TrainingWord',
        data() {
            return {
              corpus:"",
              valid:""
            }
        },
        methods: {
          subNextStep(){
            this.Request({
              url:'/model/segment/evaluate',
              method:'post',
              data:{"corpus":this.corpus}
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',responseData);
              }
            })

          }
        },
        mounted:function(){
          this.corpus = this.$route.params.result.corpus
          this.valid = this.$route.params.result.valid
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
