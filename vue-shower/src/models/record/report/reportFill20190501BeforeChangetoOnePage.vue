<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表填写']">
    <div class="fill-root">
      <div class="fill-steps">
        <el-steps direction="vertical" :active="activeStepNum">
          <el-step @click.native="e => stepClick(e, unitNum) " v-for="(unitEntity,unitNum) in unitEntities" :title="unitEntity.unit_name"></el-step>
        </el-steps>
      </div>
      <div class="fill-context">
        <div class="fill-context-child">
          <keep-alive>
            <router-view ref="fillContext"
                         @checkStepAndSave="checkStepAndSave"
                         @refreshSaveLoading="refreshSaveLoading"
                         @refreshReportFill="checkUnitStep" :key="$route.fullPath"></router-view>
          </keep-alive>
        </div>
        <div v-if="isView!='Y'" class="fill-context-options">
          <!--当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存-->

          <el-button v-if="isView!='Y'" @click="saveContext" type="danger">保存</el-button>
          <el-button v-if="isView!='Y'" @click="validateContext" type="success">校验</el-button>
          <el-button v-if="isView!='Y'" @click="submitContext" type="warning">提交</el-button>
          <!--<el-button type="primary">上一步</el-button>-->
          <!--<el-button v-if="nextStepBtnType=='NEXT'&&isView!='Y'" @click="nextStep" type="success">下一步</el-button>-->
          <!--<el-button v-if="nextStepBtnType=='SUBMIT'&&isView!='Y'" @click="reportCommitAuth" type="success">提交</el-button>-->
        </div>

      </div>
    </div>



  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    inject:['reload'],
    name: "ReportFill",
    describe:"报送填报主页面",
    components: {
      WorkMain
    },
    data() {
      return {
        reportId:"",
        reportDefinedId:"",
        isView:'N',
        activeStepNum:1,
        lastStepNum:1,
        currUnitId:'',
        afterToDoUnitId:'',
        nextStepBtnType:'NONE',//NEXT下一步 SUBMIT提交 NONE 不显示
        reportCust:{},
        unitEntities:[],
        unitEntityLink:{},
        saveLoadingContext:'',
        saveMessageTmp:null,
        hasCheckedStep:{}
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
          loading.close();
          if(response){
            this.reportCust = response
            const active_unit = response.active_unit
            this.unitEntities = response.unitEntities
            this.selectActiveStep(active_unit,true,'N')

          }
        });
      },
      selectActiveStep(active_unit,isRefresh,saveFlag){
        if(this.unitEntities){
          const saveLink = {}
          let preUnitTmp = null
          let preUnitId = null
          this.unitEntities.forEach((unitEntity,eachNum)=>{
            const unitId = unitEntity.unit_id
            if(!this.reportDefinedId){
              this.reportDefinedId = unitEntity.report_defined_id
            }
            if(preUnitTmp==null){
              saveLink.unit_id = unitId
              const nextObj = {}
              preUnitTmp = nextObj
              saveLink.nextUnit = nextObj
            }else{
              preUnitTmp.unit_id = unitId
              const nextObj = {}
              preUnitTmp.nextUnit = nextObj
              preUnitTmp = nextObj
            }

            if(preUnitId){
              this.unitEntityLink[preUnitId].nextUnit = unitEntity.unit_id
            }
            preUnitId = unitEntity.unit_id
            this.unitEntityLink[unitEntity.unit_id] = {"unit_name":unitEntity.unit_name}

            const unitType = unitEntity.unit_type
            //当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存
            if(active_unit===unitId){//当前步骤为选择的步骤
              const unitOrder = unitEntity.unit_order //获取当前单元排序
              this.activeStepNum = eachNum
              if(isRefresh){
                this.lastStepNum = eachNum
              }
              this.currUnitId = active_unit
              if(unitOrder == this.unitEntities.length){
                this.nextStepBtnType = 'SUBMIT'
              }else{
                if(this.lastStepNum==eachNum){
                  this.nextStepBtnType = 'NEXT'
                }else{
                  this.nextStepBtnType = 'NONE'
                }
              }
              let unitAddress = ""
              if(unitId){
                if(unitType=='1'){
                  unitAddress = 'oneDimensionsStaticRecord'
                }else if(unitType=='2'){
                  unitAddress = 'oneDimensionsDynamicRecord'
                }else if(unitType=='3'){//多维静态
                  unitAddress = 'multDimensionsStaticRecord'
                }else if(unitType=='4'){//多维树状
                  unitAddress = 'treeDimensionsDynRecord'
                }
                this.hasCheckedStep[unitId]=""

                this.$router.push({
                  name: unitAddress,
                  query:{
                    "reportId":this.reportId,
                    "unitId":unitId,
                    "unitType":unitType,
                    "isView":this.isView
                  },
                  params: {
                    "saveFlag": saveFlag
                  }
                });

              }
            }
          })
        }
      },
      //2019 04 26修改 应客户要求，去掉步骤逻辑，用户可随意点选任意填报步骤。
      stepClick(clickObj,unitNum){
        const active_unit = this.unitEntities[unitNum].unit_id
        this.selectActiveStep(active_unit,true,'N')
      },
      getStepIcon(unitNum){
        if(unitNum<this.lastStepNum){
            return 'el-icon-success'
        }else if(unitNum>this.lastStepNum){
          return ''
        }else{
          return 'el-icon-edit'
        }
      },
      //2019 04 26修改 应客户要求，去掉步骤逻辑，用户可随意点选任意填报步骤。
      //保存逻辑修改为只保存用户填写信息，不刷新公式信息，另外需要保存所有步骤的信息（用户填写步骤1 后切换到步骤3 步骤1没有点保存按钮 步骤3点了 需要将1的也保存）
      saveContext(){//保存
        this.saveLoadingContext = ""
        if(this.saveMessageTmp){
          this.saveMessageTmp.close()
          this.saveMessageTmp = null
        }
        this.afterToDoUnitId = this.currUnitId

        this.checkStepAndSave(null,'Y')
        // this.$refs.fillContext.saveUnitContext(false)
      },
      validateContext(){
        this.saveLoadingContext = ""
        if(this.saveMessageTmp){
          this.saveMessageTmp.close()
          this.saveMessageTmp = null
        }
        this.afterToDoUnitId = this.currUnitId
        this.checkStepAndSave(null,'V')

      },
      submitContext(){
        this.saveLoadingContext = ""
        if(this.saveMessageTmp){
          this.saveMessageTmp.close()
          this.saveMessageTmp = null
        }
        this.afterToDoUnitId = this.currUnitId
        this.$confirm('提交操作将使该报送报表进入审批流程，进入审批流程后将无法修改填报数据。请确认数据正确性！', '提示', {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          this.checkStepAndSave(null,'S')
        }).catch(() => {
        });


      },
      /**
       * @param savedUnitId 待执行单元id
       * @param saveFlag 保存标志 Y保存 V校验 S提交
       */
      checkStepAndSave(savedUnitId,saveFlag){//上一个被保存的unitid
        if(saveFlag!=null&&saveFlag=='S'){
          saveFlag = "S-Y"
        }

        let nextUnitId = null
        //unitId为空，第一个
        if(!savedUnitId)
          nextUnitId = this.unitEntities[0].unit_id
        else
          nextUnitId = this.unitEntityLink[savedUnitId].nextUnit

        if(nextUnitId){//判断是否存在下一个不存在则已经全部保存完
          //判断该unit是否被点击过，没被点击过的肯定没有修改 跳过
          if(saveFlag=='Y'||saveFlag=='S-Y'){
            if(this.hasCheckedStep[nextUnitId]!=null) {//点击过
              if(nextUnitId==this.currUnitId){//判断该unit是否为当前正在显示的unit 如果是，直接调用子组件保存方法
                this.$refs.fillContext.setSaveFlag(saveFlag)
                this.$refs.fillContext.doSaveUnitContext()
              }else{//非当前显示unit 跳转到该unit 保存该unit内容
                this.selectActiveStep(nextUnitId,false,saveFlag)
              }
            }else{//没有点击过
              this.refreshSaveLoading(nextUnitId,"保存中....")
              this.refreshSaveLoading(nextUnitId,"保存成功")
              this.checkStepAndSave(nextUnitId,saveFlag)
            }
          }else if(saveFlag=='V'||saveFlag=='S-V'){
            if(nextUnitId==this.currUnitId){//判断该unit是否为当前正在显示的unit 如果是，直接调用子组件保存方法
              this.$refs.fillContext.setSaveFlag(saveFlag)
              this.$refs.fillContext.doValidateUnitContext()
            }else{//非当前显示unit 跳转到该unit 保存该unit内容
              this.selectActiveStep(nextUnitId,false,saveFlag)
            }
          }else{

          }

        }else{
          if(saveFlag=='S-Y'){
            saveFlag = 'S-V'
            this.saveLoadingContext = ""
            if(this.saveMessageTmp){
              this.saveMessageTmp.close()
              this.saveMessageTmp = null
            }
            this.checkStepAndSave(null,saveFlag)
            return
          }

          const $this = this
          this.selectActiveStep(this.afterToDoUnitId,false,"N")
          setTimeout(function(){
            if($this.saveMessageTmp){
              $this.saveMessageTmp.close()
            }
          },10000)
          if(saveFlag=='V'||saveFlag=='S-V'){//校验完成后刷新公式
            // refreshFomular
            if(this.saveLoadingContext!=null&&this.saveLoadingContext!=''&&this.saveLoadingContext.length>0){

            }else{
              const loading = this.$loading({
                lock: true,
                text: '校验成功,刷新公式值.......',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
              });
              this.BaseRequest({
                url:"/reportCust/refreshFomular",
                params:{
                  reportDefinedId:this.reportDefinedId,
                  reportId:this.reportId,
                }
              }).then(response=>{
                loading.close();
                if(saveFlag=='S-V'){
                  this.reportCommitAuth()
                }
                else{
                  // this.$router.go(0);
                  $this.reload()
                }
              });
            }
          }
        }
      },

      nextStep(){//下一步
        this.$refs.fillContext.nextStep()
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
          loading.close();
          $this.Message.success("提交成功")
          this.$router.push({
            path: "/record/report/reportMain"
          });
        });
      },
      refreshSaveLoading(unitId,refreshText){
        if(this.saveLoadingContext!=null&&this.saveLoadingContext.length>0){
          this.saveLoadingContext = this.saveLoadingContext+"<br/><br/>"+this.unitEntityLink[unitId].unit_name+":"+refreshText
        }else{
          this.saveLoadingContext = this.unitEntityLink[unitId].unit_name+":"+refreshText
        }
        // this.saveMessageTmp.close()
        if(this.saveMessageTmp){
          this.saveMessageTmp.message = this.saveLoadingContext
        }else{
          this.saveMessageTmp = this.$message({
            dangerouslyUseHTMLString: true,
            message: this.saveLoadingContext,
            type: 'aaa',
            duration:0,
            showClose:true
          });
        }


      }
    },

    mounted() {
      console.log("mounted is running......")
      this.reportId = this.$route.query.reportId
      if(this.$route.query.isView!=null&&this.$route.query.isView!=''){
        this.isView = this.$route.query.isView
      }
      this.checkUnitStep()
    },
    activated() {
      console.log("activate is runnig......")
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

  .fill-context-child{
    width: 100%;
    height:calc(100% - 50px);
    float: left;
    overflow: auto;
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
