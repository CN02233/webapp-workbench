<template>
  <div class="pre-split-word">
    <el-row>
      <el-col style="text-align: right;margin-bottom:20px;" :span="3">分析结果：</el-col>
      <el-col style="text-align: left;margin-bottom:20px;" :span="20">{{validateSplitWordList}}</el-col>
    </el-row>

    <el-row class="next-step-btn">
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="goToPreStep">上一步</el-button>
        <el-button type="primary" @click="goToNextStep">下一步</el-button>
      </el-col>
    </el-row>

  </div>

</template>

<script>
  export default {
    name: 'ValidateSplitWord',
    data() {
      return {
        validateSplitWordList:this.$route.params.result,
        splitWordList:[
          '卡丽熙',
          '风暴降生丹尼莉丝',
          '不焚者',
          '弥林女王',
          '安达尔人，罗伊那人和先民的女王',
          '七国君王',
          '疆域保护者',
          '多斯拉克大草原的卡丽熙',
          '碎镣者',
          '龙母',
          '以及不可描述的你懂得'
        ]
      }
    },
    methods: {
      goToNextStep:function(){
        const sendData = {"uuid":this.$route.params.uuid}
        this.Request({
          url:'/process/term',
          method:'post',
          data:sendData
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            this.$emit('nextStep',response.uuid,response.result);
          }
        })
      },
      goToPreStep:function(){
        this.$emit('preStep',this.$route.params.uuid,null)
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .pre-split-word-title{
    float:left;
    width:100px;
  }
  .pre-split-word{
    margin:100px 15% 0 15%;
    width:70%;
    text-align: center;
  }
  .next-step-btn{
    margin:100px 0 0 0;
    text-align: center;
  }
  .el-breadcrumbs{
    width:calc(100% - 120px);
    float:right;
  }

  .el-breadcrumbs>span{
    margin-bottom:10px;
  }
</style>
