<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表填写']">
    <div class="fill-root">
      <div class="fill-steps">
        <el-steps direction="vertical" :active="activeStepNum">
          <el-step @click.native="e => stepClick(e, unitNum) " v-for="(unitEntity,unitNum) in unitEntities" :title="unitEntity.unit_name"
                   :icon="getStepIcon(unitNum)"></el-step>
        </el-steps>
      </div>
      <div class="fill-context">
        <div class="fill-context-child">
          <router-view ref="fillContext" @refreshReportFill="checkUnitStep" :key="$route.fullPath"></router-view>
        </div>
        <div class="fill-context-options">
          <!--当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存-->

          <el-button v-if="isView!='Y'" @click="saveContext" type="info">保存</el-button>
          <!--<el-button type="primary">上一步</el-button>-->
          <el-button v-if="nextStepBtnType=='NEXT'&&isView!='Y'" @click="nextStep" type="success">下一步</el-button>
          <el-button v-if="nextStepBtnType=='SUBMIT'&&isView!='Y'" @click="reportCommitAuth" type="success">提交</el-button>
        </div>

      </div>
    </div>



  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "ReportFill",
    describe:"报送填报主页面",
    components: {
      WorkMain
    },
    data() {
      return {
        reportId:"",
        isView:'N',
        activeStepNum:1,
        lastStepNum:1,
        currUnitId:'',
        nextStepBtnType:'NONE',//NEXT下一步 SUBMIT提交 NONE 不显示
        reportCust:{},
        unitEntities:[]
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
            this.selectActiveStep(active_unit,true)
          }
        });
      },
      selectActiveStep(active_unit,isRefresh){
        if(this.unitEntities){
          this.unitEntities.forEach((unitEntity,eachNum)=>{
            const unitId = unitEntity.unit_id
            const unitType = unitEntity.unit_type
            //当前步骤是最后一步显示提交，已点下一步的步骤不显示下一步只显示保存
            if(active_unit===unitId){//当前步骤为选择的步骤
              const unitOrder = unitEntity.unit_order //获取当前单元排序
              this.activeStepNum = eachNum
              if(isRefresh){
                this.lastStepNum = eachNum
                this.currUnitId = active_unit
              }
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
                  unitAddress = '/record/onedim/onedimRecord'
                }else if(unitType=='2'){
                  unitAddress = '/record/groupdim/groupdimRecord'
                }else if(unitType=='3'){//多维静态
                  unitAddress = '/record/multdim/griddimRecord'
                }else if(unitType=='4'){//多维树状
                  unitAddress = '/record/treedim/treedimRecord'
                }
                const lastStep = (this.currUnitId===active_unit)
                this.$router.push({
                  path: unitAddress+"?reportId="+this.reportId+"&unitId="+unitId+"&unitType="+unitType+"&lastStep="+lastStep+"&isView="+this.isView
                });

              }
            }
          })
        }
      },
      stepClick(clickObj,unitNum){
        const active_unit = this.unitEntities[unitNum].unit_id
        if(this.isView=='Y'){
          this.selectActiveStep(active_unit)
          return
        }

        if(unitNum<=this.lastStepNum){
          if(unitNum==this.activeStepNum){

            //do nothing.....
          }else{
            this.selectActiveStep(active_unit)
          }
        }
        else{
        }
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
      saveContext(){
        this.$refs.fillContext.saveUnitContext(false)
      },
      nextStep(){
        this.$refs.fillContext.nextStep()
      },
      reportCommitAuth(){
        const reportId = this.reportId
        const $this = this
        this.$confirm('提交操作将使该报送报表进入审批流程，进入审批流程后将无法修改填报数据。请确认数据正确性！', '提示', {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
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
        }).catch(() => {
        });


      },
    },

    mounted() {
      this.reportId = this.$route.query.reportId
      if(this.$route.query.isView!=null&&this.$route.query.isView!=''){
        this.isView = this.$route.query.isView
      }
      this.checkUnitStep()
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
