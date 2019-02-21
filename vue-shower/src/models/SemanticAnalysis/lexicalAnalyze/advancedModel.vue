<template>
  <WorkMain :headerItems="['NLP基础','新词发现']">

   <div class="work-steps">
     <el-steps :active="active" align-center="true"  finish-status="success">
       <el-step :key="step.stepTitle"  v-for="step in steps" :title="step.stepTitle" :description="step.stepDescription"></el-step>
     </el-steps>
   </div>

    <div>
      <router-view @nextStep="nextStep" @preStep="preStep"></router-view>
    </div>

  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'AdvancedModel',
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
            stepDescription:'选择数据源',
            stepUrl:'chooseDataSource'
          },{
            stepTitle:'步骤 2',
            stepDescription:'预分词',
            stepUrl:'preSplitWord'
          },{
            stepTitle:'步骤 3',
            stepDescription:'临时修改词库',
            stepUrl:'editWdMarketTmp'
          },{
            stepTitle:'步骤 4',
            stepDescription:'验证分词',
            stepUrl:'validateSplitWord'
          },{
            stepTitle:'步骤 5',
            stepDescription:'更新词库',
            stepUrl:'updateWordMarket'
          }
        ]
      };
    },
    methods: {
      nextStep(uuid,result) {
        console.log("uuid:"+uuid+"--result:"+JSON.stringify(result));
        this.uuid = uuid
        this.nextStepResult = result
        if (this.active++ >= (this.steps.length-1)) this.active = 0;
        this.goToStepPage()
      },
      preStep(uuid,datas){
        console.log("uuid:"+uuid+"--datas:"+JSON.stringify(datas));
        this.uuid = uuid
        this.nextStepResult = datas
        if (this.active-- < 0) this.active = 0;
        this.goToStepPage()
      },
      goToStepPage:function(){
//        this.$router.push({path: this.advancedStepBaseUrl+this.steps[this.active].stepUrl});
        this.$router.push({name: this.steps[this.active].stepUrl,params:{"uuid":this.uuid,result:this.nextStepResult}});
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
</style>
