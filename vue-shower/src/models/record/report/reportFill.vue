<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表填写']">
    <div class="fill-root">
      <div class="fill-steps">
        <el-steps process-status="finish"	direction="vertical" :active="activeStepNum">
          <el-step v-if="checkUnitShow(unitEntity)"
                   :class="{'bold-step':activeStepNum==unitNum}" style="font-weight: bold;font-color:black"
                   :status="validateResult[unitEntity.unit_id]!=null?validateResult[unitEntity.unit_id]:'finish'"
                   @click.native="e => stepClick(e, unitNum) "
                   :key="unitNum"
                   v-for="(unitEntity,unitNum) in unitEntities"
                   :title="unitEntity.unit_name"></el-step>
        </el-steps>
      </div>
      <div class="fill-context">
        <div class="fill-context-children">
          <ReportContextRoot
                             :ref="'reportContextRef'+unitEntity.unit_id"
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
          <!--<el-button v-if="isView!='Y'" @click="doSubmitContext('VALIDATE')" type="warning">提交</el-button>-->
          <el-button v-if="isView!='Y'" @click="confirmSubmit" type="warning">提交</el-button>
          <el-button v-if="auth=='Y'" @click="handlePass" type="success">通过</el-button>
          <el-button v-if="auth=='Y'" @click="handleReject" type="danger">驳回</el-button>
        </div>

      </div>
    </div>


    <el-dialog
      :title="showReadMe?'真实性声明':'请签名'"
      :visible.sync="showSignInfos">
      <div v-if="showReadMe" class="sign-readme">
        <div class="readme-context">
          本人确保表中所有的数据和信息真实准确，应对其负责。
        </div>

        <div  class="readme-footer">
          <el-row style="text-align:left;margin:5px 0 0 10px;">
            <el-col :span="24">
              <el-checkbox v-model="agreeReadMe">
                <span class="sign-readme-checkinfo">
                  我已阅读以上条款，并保证提交数据的真实性以及准确性。
                </span>
              </el-checkbox>
            </el-col>
          </el-row>
          <el-row style="text-align: right">
            <el-button type="primary" :disabled="!agreeReadMe" @click="confirmReadMe">确 认</el-button>
          </el-row>
        </div>
      </div>



      <div v-if="!showReadMe" class="sign-customers">
        <el-form label-position="right" label-width="30%" ref="siginForm">
          <el-form-item  label="填报人签名" :error="signInfomationsError.reportCustName" prop="reportCustName">
            <el-col :span="18">
              <el-input placeholder="请输入填报人姓名" v-model="signInfomations.reportCustName"></el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="财务人员签名" :error="signInfomationsError.reportAccountName" prop="reportAccountName">
            <el-col :span="18">
              <el-input placeholder="请输入财务人员姓名" v-model="signInfomations.reportAccountName"></el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="公司领导签名" :error="signInfomationsError.reportLeaderName" prop="reportLeaderName">
            <el-col :span="18">
              <el-input placeholder="请输入公司领导姓名" v-model="signInfomations.reportLeaderName"></el-input>
            </el-col>
          </el-form-item>



        </el-form>




      </div>

      <div v-if="!showReadMe" slot="footer" class="dialog-footer">
        <el-button type="primary"  @click="doSign">确定</el-button>
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
        showSignInfos:false,
        signInfomationsError:{
          reportCustName:'',
          reportAccountName:'',
          reportLeaderName:''
        },
        agreeReadMe:true,
        notAgreeReadMe:false,
        showReadMe:true,
        signInfomations:{
          reportCustName:'',
          reportAccountName:'',
          reportLeaderName:''
        },
        currUser:{}
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
            loading.close()
            // this.selectActiveStep(active_unit,true,'N')

          }
        });
      },
      getCurrUserInfo(){
        this.BaseRequest({
          url:"/cqnyUser/getCurrUserOrigin"
        }).then(response=>{
          if(response){
            this.currUser = response.user
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
      confirmReadMe(){
        if(this.agreeReadMe){
          this.showReadMe = false
        }else{
          this.Message("请勾选下方同意选项")
        }
      },
      doSign(){
        let errorTag =false
        if(this.signInfomations.reportCustName==null||this.signInfomations.reportCustName==''||(this.signInfomations.reportCustName==undefined) ){
          this.signInfomationsError.reportCustName='填报人不允许为空'
          errorTag = true
        }
        if(this.signInfomations.reportAccountName==null||this.signInfomations.reportAccountName==''||(this.signInfomations.reportAccountName==undefined) ){
          this.signInfomationsError.reportAccountName='财务人员不允许为空'
          errorTag = true
        }
        if(this.signInfomations.reportLeaderName==null||this.signInfomations.reportLeaderName==''||(this.signInfomations.reportLeaderName==undefined) ){
          this.signInfomationsError.reportLeaderName='公司负责人不允许为空'
          errorTag = true
        }

        if(errorTag){
          return
        }

        this.BaseRequest({
          url:"/reportCust/signReport",
          method:"post",
          data:{
            report_id:this.reportId,
            report_cust_name:this.signInfomations.reportCustName,
            report_account_name:this.signInfomations.reportAccountName,
            report_leader_name:this.signInfomations.reportLeaderName
          }
        }).then(response=>{
          console.log(response)
          if(response&&response=='SUCCESS'){
            this.showSignInfos = false
            this.showReadMe = true
            this.doSubmitContext('VALIDATE')
          }else{
            this.Message.error("签名失败")
          }
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
      checkUnitShow(unitEntity){

        // 对哪类用户展示该单元
        // 0:填报人员
        // 1:审核人员
        // 2：监管人员
        // 3：审核+监管人员
        const unitShowTYpe = unitEntity.unit_show_type

        // 1：填报用户
        // 2：监管用户
        // 0：审核用户
        const userType = this.currUser.user_type
        if(unitShowTYpe==0){
          return true
        }else{
          if(unitShowTYpe==1){
            if(userType==0){
              return true
            }
          }
          if(unitShowTYpe==2){
            if(userType==2){
              return true
            }
          }
          if(unitShowTYpe==3){
            if(userType==0||userType==2){
              return true
            }
          }
        }
        return false
      }
    },

    mounted() {
      this.reportId = this.$route.query.reportId
      if(this.$route.query.isView!=null&&this.$route.query.isView!=''){
        this.isView = this.$route.query.isView
      }

      if(this.$route.params.auth!=null&&this.$route.params.auth!=''){
        this.auth = this.$route.params.auth
      }
      if(this.$route.query.reportStats!=null&&this.$route.query.reportStats!=''){
        this.reportStats = this.$route.query.reportStats
      }
      this.checkUnitStep()
      this.getCurrUserInfo()
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
    /*border:1px solid #1f69c8;*/
    height: 380px;
    padding:0 10px 0 10px;
    text-align: left;
  }

  .readme-context{
    width:100%;
    height:300px;
    overflow: auto;
  }

  .readme-footer{
    width:100%;
    height:80px;
  }

  .sign-readme-checkinfo{
    font-size: 12px;
  }

  .hide-root{
    display: none;
  }

</style>
