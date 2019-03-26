<template>
    <router-view></router-view>
</template>

<script>
  export default {
    name: "ReportFill",
    describe:"报送填报主页面",
    data() {
      return {
        reportId:""
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
          url:"/reportCust/getUnitContext",
          params:{
            reportId:this.reportId,
            unitId:this.unitId,
            unitType:this.unitType
          }
        }).then(response=>{
          loading.close();
          if(response){
            this.definedColums = response.definedColums
            if(response.columDatas){
              response.columDatas.forEach(columData=>{
                const columKey = columData.unit_id + "_"+columData.colum_id
                this.columDatas[columKey] = columData
              })
            }
          }
        });
      },
      jumpTo(){

      }
    },
    mounted() {
      this.reportId = this.$route.query.reportId
      this.checkUnitStep()
    }
  }
</script>

<style scoped>

</style>
