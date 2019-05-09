<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表填写']">
    <div class="fill-root">
      <div class="fill-steps">
        <el-steps process-status="finish"	direction="vertical" :active="activeStepNum">
          <el-step :status="validateResult[unitEntity.unit_id]!=null?validateResult[unitEntity.unit_id]:'finish'"
                   @click.native="e => stepClick(e, unitNum) "
                   v-for="(unitEntity,unitNum) in unitEntities"
                   :title="unitEntity.unit_name"></el-step>
        </el-steps>
      </div>
      <div class="fill-context">
        <div class="fill-context-children">
          <ReportContextRoot :ref="'reportContextRef'+unitEntity.unit_id"
            :reportId="reportId"
            :unitEntity="unitEntity"
            :isView="isView"
            @saveReportsCallBack="saveReportsCallBack"
            @validateReportsCallBack="validateReportsCallBack"
            @submitReportsCallBack="submitReportsCallBack"
            class="fill-context-child"
            v-bind:class="{'fill-context-hide':currUnitId!=unitEntity.unit_id}"
            v-for="unitEntity in unitEntities">{{unitEntity.unit_name}}
          ></ReportContextRoot>
        </div>
        <!--<div v-if="isView!='Y'" class="fill-context-options">-->
        <div v-if="isView!='Y'" class="fill-context-options">
          <!--当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存-->

          <!--<el-button  @click="saveContext" type="danger">保存</el-button>-->
          <el-button  @click="doSaveContext" type="danger">保存</el-button>
          <!--<el-button  @click="validateContext" type="success">校验</el-button>-->
          <el-button  @click="doValidateContext" type="success">校验</el-button>
          <!--<el-button  @click="submitContext" type="warning">提交</el-button>-->
          <el-button  @click="doSubmitContext('SAVE')" type="warning">提交</el-button>
        </div>

      </div>
    </div>



  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"
  import ReportContextRoot from "@/models/record/report/reportContextRoot"

  export default {
    inject:['reload'],
    name: "ReportFill",
    describe:"报送填报主页面",
    components: {
      WorkMain,
      ReportContextRoot
    },
    data() {
      return {
        reportId:"",
        reportDefinedId:"",
        isView:'N',
        activeStepNum:1,
        currUnitId:'',
        reportCust:{},
        unitEntities:[],
        unitEntityLink:{},
        doSomethinLoading:null,
        doneCount:0,
        doneExcetionMessage:null,
        validateResult:{}
      }
    },
    methods:{
      checkUnitStep(){
        const loading = this.$loading({
          lock: true,
          text: '获取填报单元信息中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/reportCust/checkUnitStep",
          params:{
            reportId:this.reportId
          }
        }).then(response=>{
          if(response){
            this.reportCust = response
            const active_unit = response.active_unit
            this.unitEntities = response.unitEntities
            if(this.unitEntities!=null&&this.unitEntities.length>0){
              this.currUnitId = this.unitEntities[0].unit_id
              this.reportDefinedId = this.unitEntities[0].report_defined_id
            }
            // this.selectActiveStep(active_unit,true,'N')

          }
        });
      },
      //2019 04 26修改 应客户要求，去掉步骤逻辑，用户可随意点选任意填报步骤。
      stepClick(clickObj,unitNum){
        const active_unit = this.unitEntities[unitNum].unit_id
        this.currUnitId = active_unit
        this.activeStepNum = unitNum
        // this.selectActiveStep(active_unit,true,'N')
      },
      //2019 04 26修改 应客户要求，去掉步骤逻辑，用户可随意点选任意填报步骤。
      //保存逻辑修改为只保存用户填写信息，不刷新公式信息，另外需要保存所有步骤的信息（用户填写步骤1 后切换到步骤3 步骤1没有点保存按钮 步骤3点了 需要将1的也保存）
      doSaveContext(){
        this.validateResult = {}
        this.doneCount=0
        this.doneExcetionMessage = null
        this.doSomethinLoading = this.$loading({
          lock: true,
          text: '保存中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        this.unitEntities.forEach(unitEntity=>{
          const unitId = unitEntity.unit_id
          const reportContextRef = this.$refs['reportContextRef'+unitId][0]
          reportContextRef.doSaveContext()
        })
      },
      doValidateContext(){
        this.doneCount=0
        this.doneExcetionMessage = null
        this.doSomethinLoading = this.$loading({
          lock: true,
          text: '校验中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        this.unitEntities.forEach(unitEntity=>{
          const unitId = unitEntity.unit_id
          const reportContextRef = this.$refs['reportContextRef'+unitId][0]
          reportContextRef.doValidateUnitContext()
        })
      },
      doRefreshFomular(needSubmit){
        this.doSomethinLoading = this.$loading({
          lock: true,
          text: '刷新公式中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        this.BaseRequest({
          url:"/reportCust/refreshFomular",
          params:{
            reportDefinedId:this.reportDefinedId,
            reportId:this.reportId,
          }
        }).then(response=>{
          this.doSomethinLoading.close();
          this.$notify({
            title: '公式刷新完毕',
            type: 'success',
            message: "动态计算项已按照您的填写更新"
          });
          if(needSubmit){
            this.reportCommitAuth()
          }else{//非提交，只校验 需要刷新页面公式的值
            this.unitEntities.forEach(unitEntity=>{
              const unitId = unitEntity.unit_id
              const reportContextRef = this.$refs['reportContextRef'+unitId][0]
              reportContextRef.getFolumarData()
            })
          }
        }).catch(error=>{
          this.doSomethinLoading.close();
          this.$notify({
            title: '公式刷新失败',
            type: 'error',
            message: error
          });
        });
      },
      doSubmitContext(processName){
        // this.validateResult = {}
        this.doneCount=0
        this.doneExcetionMessage = null
        const $this = this
        function doSubmit(){
          $this.doSomethinLoading = $this.$loading({
            lock: true,
            text: '提交中.......',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          })
          $this.unitEntities.forEach(unitEntity=>{
            const unitId = unitEntity.unit_id
            const reportContextRef = $this.$refs['reportContextRef'+unitId][0]
            reportContextRef.doSubmitContext(processName)
          })
        }

        if(processName=='VALIDATE'){
          doSubmit()
        }else{
          this.$confirm('提交操作将使该报送报表进入审批流程，进入审批流程后将无法修改填报数据。请确认数据正确性！', '提示', {
            confirmButtonText: '确定提交',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString:true,
            type: 'warning'
          }).then(() => {
            doSubmit()
          }).catch(() => {

          });
        }

      },
      saveReportsCallBack(unitId,saveException){
        this.doneCount = this.doneCount+1
        if(saveException){
          if(this.doneExcetionMessage){
            this.doneExcetionMessage = this.doneExcetionMessage+"<br>"+saveException
          }else{
            this.doneExcetionMessage = saveException
          }
        }
        if(this.doneCount==this.unitEntities.length){
          this.doSomethinLoading.close()
          this.doSomethinLoading = null
          this.doneCount=0
          if(this.doneExcetionMessage){
            this.$notify({
              title: '保存失败',
              type: 'error',
              dangerouslyUseHTMLString: true,
              message: this.doneExcetionMessage
            });
          }else{
            this.$notify({
              title: '保存成功',
              type: 'success',
              message: "您已成功保存报表信息"
            });
          }
        }
      },
      validateReportsCallBack(unitId,unitName,saveException){
        this.doneCount = this.doneCount+1
        if(saveException){
          this.validateResult[unitId] = "error"
          saveException = unitName+":"+saveException
          if(this.doneExcetionMessage){
            this.doneExcetionMessage = this.doneExcetionMessage+"<br>"+saveException
          }else{
            this.doneExcetionMessage = saveException
          }
        }else{
          this.validateResult[unitId] = "success"
        }
        if(this.doneCount==this.unitEntities.length){
          this.doSomethinLoading.close()
          this.doSomethinLoading = null
          this.doneCount=0
          const unitEntitiesTmp = this.unitEntities
          this.unitEntities = null
          this.unitEntities = unitEntitiesTmp
          if(this.doneExcetionMessage){
            this.$notify({
              title: '校验失败',
              type: 'error',
              dangerouslyUseHTMLString: true,
              message: this.doneExcetionMessage
            });
          }else{
            this.$notify({
              title: '校验成功',
              type: 'success',
              message: "所有输入项均校验通过"
            });
            this.doRefreshFomular()
          }
        }
      },
      submitReportsCallBack(unitId,unitName,saveException,processName){
        this.doneCount = this.doneCount+1
        if(saveException){
          if(processName=='VALIDATE'){
            this.validateResult[unitId] = "error"
          }
          saveException = unitName+":"+saveException
          if(this.doneExcetionMessage){
            this.doneExcetionMessage = this.doneExcetionMessage+"<br>"+saveException
          }else{
            this.doneExcetionMessage = saveException
          }
        }else{
          if(processName=='VALIDATE'){
            this.validateResult[unitId] = "success"
          }
        }
        if(this.doneCount==this.unitEntities.length){
          this.doSomethinLoading.close()
          this.doSomethinLoading = null
          this.doneCount=0
          let processTitle = "保存"
          if(processName=='VALIDATE'){
            processTitle="校验"
            const unitEntitiesTmp = this.unitEntities
            this.unitEntities = null
            this.unitEntities = unitEntitiesTmp
          }
          if(this.doneExcetionMessage){
            this.$notify({
              title: processTitle+'失败',
              dangerouslyUseHTMLString: true,
              type: 'error',
              message: this.doneExcetionMessage
            });
          }else{

            let processmessage = "您已成功保存报表信息"
            if(processName=='VALIDATE'){
              processmessage="所有输入项均校验通过"
            }
            this.$notify({
              title: processTitle+'成功',
              type: 'success',
              message: processmessage
            });
            if(processName=="SAVE"){
              this.doSubmitContext("VALIDATE")
            }else if(processName=="VALIDATE"){
              this.doRefreshFomular("YES")
            }

          }
        }
      },
      reportCommitAuth(){
        const reportId = this.reportId
        const $this = this
        const loading = $this.$loading({
          lock: true,
          text: '提交审批',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/reportCust/doCommitAuth",
          method:'get',
          params:{
            reportId:reportId
          }
        }).then(response=>{
          loading.close()
          this.$notify({
            title: '提交成功',
            type: 'success',
            message: "您的报表已提交审批，请等待上级审批"
          });
          this.$router.push({
            path: "/record/report/reportMain"
          });
        });
      }
    },

    mounted() {
      this.reportId = this.$route.query.reportId
      if(this.$route.query.isView!=null&&this.$route.query.isView!=''){
        this.isView = this.$route.query.isView
      }
      this.checkUnitStep()
    },
    activated() {
    }
  }
</script>

<style lang="css">
  .el-step__main>div{
    font-size:13px !important;
    font-weight: normal !important;
  }
</style>

<style scoped>
  .fill-root{
    width:100%;
    height:100%;
    padding:0 0 0 0 ;
  }
  .fill-steps{
    width:200px;
    height:80%;
    padding:50px 0 0 100px;
    float: left;
    /*position:absolute;*/
    /*z-index: 10086;*/
  }
  .fill-context{
    width:calc(100% - 340px);
    height:90%;
    margin:40px 0 0 0px;
    float: right;
    overflow: auto;
  }

  .fill-context-children{
    width: 100%;
    height:calc(100% - 50px);
    float: left;
  }

  .fill-context-child{
    width: 100%;
    overflow: auto;
  }

  .fill-context-hide{
    display: none;
  }

  .fill-context-options{
    width: 100%;
    height:30px;
    margin:10px 0 0 0 ;
    float: left;
  }

  .el-step{
    cursor: pointer;
  }

</style>
