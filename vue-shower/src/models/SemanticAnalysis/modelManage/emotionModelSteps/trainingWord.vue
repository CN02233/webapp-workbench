<template>
  <div>
    <el-row>
      <el-col :span="20">

        <el-input
          v-model="content"
          type="textarea"
          :autosize="{ minRows: 8}"
          placeholder="请输入内容">
        </el-input>

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
              content:""
            }
        },
        methods: {
          subNextStep(){
            this.Request({
              url:'/model/sentiment/evaluate',
              method:'post',
              data:{"content":this.content}
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',responseData);
              }
            })
          }
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
