<template>
  <div>
    <el-row>
      <el-col :span="20">
        {{sentiment.content}}<br>
        情感分析结果：{{sentiment.sentiment}}
      </el-col>
    </el-row>

    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subNextStep">完成</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
    export default {
        name: 'ResultScore',
        data() {
            return {
              content:"",
              sentiment:""
            }
        },
        methods: {
          subNextStep(){
            this.$emit('nextStep',this.$route.params.result);
          }
        },
        mounted:function(){
          this.content = this.$route.params.result.content
          this.sentiment = this.$route.params.result.sentiment
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
