<template>
  <WorkMain :headerItems="['模型管理','情感模型']">

    <div class="work-steps">
      <el-steps :active="active" align-center="true"  finish-status="success">
        <el-step :key="step.stepTitle"  v-for="step in steps" :title="step.stepTitle" :description="step.stepDescription"></el-step>
      </el-steps>
    </div>

    <div class="steps">
      <router-view @nextStep="nextStep"></router-view>
    </div>

  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'EmotionModel',
    components: {
      WorkMain
    },
    data() {
      return {
        active: 0,
        uuid:null,
        nextStepResult:null,
        steps:[
          {
            stepTitle:'步骤 1',
            stepDescription:'导入语料',
            stepUrl:'emotionImportWord'
          },{
            stepTitle:'步骤 2',
            stepDescription:'训练模型',
            stepUrl:'emotionTrainingWord'
          },{
            stepTitle:'步骤 3',
            stepDescription:'效果评估',
            stepUrl:'emotionResultScore'
          }
        ]
      };
    },
    methods: {
      nextStep(result) {
        this.nextStepResult = result
        if (this.active++ >= (this.steps.length-1)) this.active = 0;
        this.goToStepPage()
      },
      goToStepPage:function(){
        this.$router.push({name: this.steps[this.active].stepUrl,params:{result:this.nextStepResult}});
      }
    },
    mounted:function(){
      this.goToStepPage()
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .work-steps{
    width:100%;
    margin:40px 0 0 0;
    text-align: left;
  }
  .steps{
    margin:40px 20% 0 20%;
  }
</style>
