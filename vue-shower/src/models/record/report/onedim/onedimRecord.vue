<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表编辑']">
    <el-form ref="form"  label-width="40%">
      <el-form-item  v-for="definedColum in definedColums" :label="definedColum.colum_name_cn">
        <el-input v-model="columDatas[definedColum.unit_id+'_'+definedColum.colum_id].report_data"
                  :disabled="definedColum.colum_data_type==0" style="width:50%;float: left;" ></el-input>
      </el-form-item>
    </el-form>

    <el-button @click="saveUnitContext" type="info">保存</el-button>
    <el-button type="primary">上一步</el-button>
    <el-button type="success">下一步</el-button>
  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "onedimRecord",
    describe:"一维报表填报单元",
    components: {
      WorkMain
    },
    data() {
      return {
        reportId:"",
        unitId:"",
        unitType:"",
        definedColums:[],
        columDatas:{}
      }
    },
    methods:{
      getUnitContext(){
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
      saveUnitContext(){
        const loading = this.$loading({
          lock: true,
          text: '保存报送信息中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        const $this = this
        this.BaseRequest({
          url:"/reportCust/saveSimpleUnitContext",
          method:'post',
          data:{
            definedColums:this.definedColums,
            columDatas:Object.values(this.columDatas)
          }
        }).then(response=>{
          loading.close();
          $this.Message.success("保存成功")
          $this.getUnitContext()
        });
      }
    },
    mounted:function(){
      this.reportId = this.$route.query.reportId
      this.unitId = this.$route.query.unitId
      this.unitType = this.$route.query.unitType
      if(true){
        this.reportId = '1'
        this.unitId = '1'
        this.unitType = '1'
      }
      this.getUnitContext()
    }
  }
</script>

<style scoped>

</style>
