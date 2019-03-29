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
        <router-view @refreshReportFill="checkUnitStep" :key="$route.fullPath"></router-view>
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
        activeStepNum:1,
        lastStepNum:1,
        unitEntities:[],
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
            console.log("response has got "+response)
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
            if(active_unit===unitId){
              this.activeStepNum = eachNum
              if(isRefresh){
                this.lastStepNum = eachNum

              }
              let unitAddress = ""
              if(unitId){
                if(unitType=='1'){
                  unitAddress = '/record/onedim/onedimRecord'
                }else if(unitType=='2'){

                }else if(unitType=='3'){//多维静态

                }else if(unitType=='4'){//多维树状
                  unitAddress = '/record/treedim/treedimRecord'
                }
                const lastStep = (unitType==='4')
                console.log(lastStep)
                this.$router.push({
                  path: unitAddress+"?reportId="+this.reportId+"&unitId="+unitId+"&unitType="+unitType+"&lastStep="+lastStep
                });

              }
            }
          })
        }
      },
      stepClick(clickObj,unitNum){
        const active_unit = this.unitEntities[unitNum].unit_id
        if(unitNum<=this.lastStepNum){
          if(unitNum==this.activeStepNum){
            //do nothing.....
            console.log("do nothing....")
          }else{
            this.selectActiveStep(active_unit)
            console.log("select step "+active_unit)
          }
        }
        else{
          console.log("not yet.....")
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
      }
    },

    mounted() {
      console.log("report fill is running.....")
      this.reportId = this.$route.query.reportId
      this.checkUnitStep()
    }
  }
</script>

<style scoped>
  .fill-root{
    width:100%;
    height:100%;
    padding:30px 0 0 0 ;
  }
  .fill-steps{
    width:150px;
    height:70%;
    position:absolute;
    z-index: 10086;
  }
  .fill-context{
    width:calc(100% - 150px);
    height:70%;
    margin:0 0 0 150px;
    overflow: auto;
  }
  .el-step{
    cursor: pointer;
  }
</style>
