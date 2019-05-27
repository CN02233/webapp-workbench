<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表填写']">
    <div class="fill-root">
      <div class="fill-steps">
        <el-steps process-status="finish"	direction="vertical" :active="activeStepNum">
          <el-step :class="{'bold-step':activeStepNum==unitNum}" style="font-weight: bold;font-color:black" :status="validateResult[unitEntity.unit_id]!=null?validateResult[unitEntity.unit_id]:'finish'"
                   @click.native="e => stepClick(e, unitNum) "
                   :key="unitNum"
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
                             @saveAndValidateCallBack="saveAndValidateCallBack"
                             class="fill-context-child" :key="unitEntity.unit_id"
                             v-bind:class="{'fill-context-hide':currUnitId!=unitEntity.unit_id}"
                             v-for="unitEntity in unitEntities">{{unitEntity.unit_name}}
            ></ReportContextRoot>
        </div>
        <!--<div v-if="isView!='Y'" class="fill-context-options">-->
        <div  class="fill-context-options">
          <!--当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存-->

          <!--<el-button  @click="saveContext" type="danger">保存</el-button>-->
          <el-button v-if="isView!='Y'" @click="doSaveContext" type="danger">保存</el-button>
          <!--<el-button  @click="validateContext" type="success">校验</el-button>-->
          <el-button v-if="isView!='Y'" @click="doSaveAndValidate('VALIDATE')" type="success">校验</el-button>
          <!--<el-button  @click="submitContext" type="warning">提交</el-button>-->
          <el-button v-if="isView!='Y'" @click="doSubmitContext('VALIDATE')" type="warning">提交</el-button>
          <!--<el-button v-if="isView!='Y'" @click="confirmSubmit" type="warning">新提交</el-button>-->
          <el-button v-if="auth=='Y'" @click="handlePass" type="danger">通过</el-button>
          <el-button v-if="auth=='Y'" @click="handleReject" type="success">驳回</el-button>
        </div>

      </div>
    </div>


    <el-dialog
      title="请阅读以下内容并确认"
      :visible.sync="showSignInfos">
      <div class="sign-readme">
        <span>
          这是一段信息 <br>
          条款之类的在这列显示 <br>
          条款之类的在这列显示 <br>
          条款之类的在这列显示 <br>
          条款之类的在这列显示 <br>
          条款之类的在这列显示 <br>
          条款之类的在这列显示
        </span>
      </div>

      <span slot="footer" class="dialog-footer">
         <el-row style="text-align:left;">
          <el-col :span="24">
            <el-checkbox v-model="checked">
              <span class="sign-readme-checkinfo">
                我已阅读以上条款，并保证提交数据的真实性以及准确性。
              </span>
            </el-checkbox>
          </el-col>
        </el-row>
        <el-row style="text-align:left;">
          <el-col :span="24">
            <el-checkbox v-model="checked">
              <span class="sign-readme-checkinfo">
                不同意以上条款
              </span>
            </el-checkbox>
          </el-col>
        </el-row>

        <el-button type="primary" @click="showSignInfos = false">确 认</el-button>

      </span>

      <div class="sign-customers">
        <el-form v-if="selectType" class="login-form-selecttype"
                 :model="selectOriginType"
                 autoComplete="on" ref="selectTypeForm" label-position="left">
          <h3 class="title">请完善您的信息</h3>
          <!--<h3 class="title">欢迎！</h3>-->
          <el-form-item  prop="origin_type">
            <el-select  v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择您的企业类型">
              <el-option :key="key" v-for="(value, key) in originTypes" :label="value" :value="key"></el-option>
            </el-select>

          </el-form-item>

          <el-form-item :error="selectOriginTypeError.user_name_cn" prop="office_phone">
            <el-input placeholder="请输入真实姓名" v-model="selectOriginType.user_name_cn"></el-input>
          </el-form-item>
          <el-form-item :error="selectOriginTypeError.office_phone" prop="office_phone">
            <el-input placeholder="请输入办公电话" v-model="selectOriginType.office_phone"></el-input>
          </el-form-item>
          <el-form-item :error="selectOriginTypeError.mobile_phone" prop="mobile_phone">
            <el-input placeholder="请输入手机号" v-model="selectOriginType.mobile_phone"></el-input>
          </el-form-item>
          <el-form-item :error="selectOriginTypeError.email" prop="email">
            <el-input placeholder="请输入邮箱地址" v-model="selectOriginType.email"></el-input>
          </el-form-item>
          <el-form-item :error="selectOriginTypeError.social_code" prop="social_code">
            <el-input placeholder="请输入统一社保代码" v-model="selectOriginType.social_code"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" style="width:100%;" :loading="loading" @click="changeOriginType">
              <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
              确定
            </el-button>
          </el-form-item>
        </el-form>

      </div>


    </el-dialog>



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
        auth:'N',//是否为审批用户查看
        reportStats:'',
        activeStepNum:0,
        currUnitId:'',
        reportCust:{},
        unitEntities:[],
        unitEntityLink:{},
        doSomethinLoading:null,
        doneCount:0,
        doneExcetionMessage:null,
        validateResult:{},
        showSignInfos:false
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
      doSaveAndValidate(processName){//先SAVE 再VALIDATE 再REFRESH
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
          reportContextRef.doSaveAndValidate(processName)
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
          if(response){
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
          }else{
            this.doSomethinLoading.close();
            this.$notify({
              title: '公式刷新失败',
              type: 'error',
              message: "刷新公式异常"
            });
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
      confirmSubmit(){
        this.showSignInfos = true
        // console.log(this.showSignInfos)
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

        if(processName=='SAVE'){
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
      saveAndValidateCallBack(unitId,unitName,saveException,processName){
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
            //console.log(processName+"---"+this.doneCount)

            if(processName=="SAVE"){
              this.doRefreshFomular()
            }else if(processName=="VALIDATE"){
              this.doSaveAndValidate("SAVE")
            }

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
              this.doRefreshFomular("YES")
            }else if(processName=="VALIDATE"){
              this.doSubmitContext("SAVE")
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
      },
      handlePass () { // 通过

        this.$confirm('确认【通过】审批该报表？', '提示', {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          let url = 'ReportReviewOperator'
          let returnUrl = 'reportApproval'
          if(this.reportStats=='1'){
            url = 'ReportApprovalOperator'
            returnUrl = 'reportApproval'
          }else if(this.reportStats=='2'){
            url = 'ReportReviewOperator'
            returnUrl = 'reportReview'
          }else{
            this.Message.error("任务状态丢失")
            return
          }

          this.BaseRequest({
            url: '/reportApproval/'+url,
            method: 'get',
            params: {'reportId': this.reportId, 'reportStatus': 'pass'}
          }).then(() => {
            this.Message.success("审批成功")
            this.$router.push({
              path: "/record/"+returnUrl
            });
          })
        }).catch(() => {

        });


      },
      handleReject () { // 驳回
        this.$confirm('确认【驳回】审批该报表？', '提示', {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          let url = 'ReportReviewOperator'
          let returnUrl = 'reportApproval'
          if(this.reportStats=='1'){
            url = 'ReportApprovalOperator'
            returnUrl = 'reportApproval'
          }else if(this.reportStats=='2'){
            url = 'ReportReviewOperator'
            returnUrl = 'reportReview'
          }else{
            this.Message.error("任务状态丢失")
            return
          }

          this.BaseRequest({
            url: '/reportApproval/'+url,
            method: 'get',
            params: {'reportId': this.reportId, 'reportStatus': 'reject'}
          }).then(() => {
            this.Message.success('驳回成功')
            this.$router.push({
              path: "/record/"+returnUrl
            });
          })
        }).catch(() => {

        });



      },
    },

    mounted() {
      this.reportId = this.$route.query.reportId
      if(this.$route.query.isView!=null&&this.$route.query.isView!=''){
        this.isView = this.$route.query.isView
      }
      if(this.$route.query.auth!=null&&this.$route.query.auth!=''){
        this.auth = this.$route.query.auth
      }
      if(this.$route.query.reportStats!=null&&this.$route.query.reportStats!=''){
        this.reportStats = this.$route.query.reportStats
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
    /*font-weight: normal !important;*/
  }

  .bold-step div{
    font-weight:900 !important;
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

  .sign-readme{
    border:1px solid #1f69c8;
    min-height: 200px;
  }

  .sign-readme-checkinfo{
    font-size: 12px;
  }

</style>
