<template>
  <div>
    <el-form ref="form"  label-width="40%">
      <el-form-item size="mini" v-for="dataColum in dataObject" :label="dataColum.colum_name_cn">
        <el-col :span="23">
          <el-tooltip class="item" effect="dark" :content="dataColum.colum_desc" placement="top">
            <el-input v-model="dataColum.report_data"
                      :disabled="dataColum.colum_type==0||isView=='Y'" style="width:50%;float: left;" >
              <template v-if="dataColum.colum_point!=null&&dataColum.colum_point!=''" slot="append">{{dataColum.colum_point}}</template>
            </el-input>
          </el-tooltip>
        </el-col>
      </el-form-item>
    </el-form>

    <!--<el-button v-if="isView!='Y'" @click="saveUnitContext(false)" type="info">保存</el-button>-->
    <!--&lt;!&ndash;<el-button type="primary">上一步</el-button>&ndash;&gt;-->
    <!--<el-button v-if="isView!='Y'" @click="nextStep" type="success">下一步</el-button>-->
  </div>
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
        lastStep:false,
        isView:'N',
        definedColums:[],
        columDatas:{},
        dataObject:[]
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
            const defindObj = {}
            this.definedColums.forEach(definedColum=>{
              const defindColumId = definedColum.colum_id
              defindObj['C'+defindColumId] = definedColum
            })

            if(response.columDatas){
              response.columDatas.forEach(columData=>{
                const columKey = columData.unit_id + "_"+columData.colum_id
                this.columDatas[columKey] = columData
                // if(!this.dataObject[columData.unit_id]){
                //   this.dataObject[columData.unit_id] = {}
                // }
                columData.colum_name_cn = defindObj['C'+columData.colum_id].colum_name_cn
                columData.colum_desc = defindObj['C'+columData.colum_id].colum_desc
                columData.colum_point = defindObj['C'+columData.colum_id].colum_point
                columData.colum_type = defindObj['C'+columData.colum_id].colum_type
              })
              this.dataObject = response.columDatas
            }
          }
        }).catch(error=>{
            this.Message.success(error)
            loading.close()
          }
        );
      },
      saveUnitContext(needUpdateStep){
        const $this = this

        // validateSimpleUnitContext
        const valloading = this.$loading({
          lock: true,
          text: '数据校验中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/reportCust/validateSimpleUnitContext",
          method:'post',
          data:{
            definedColums:this.definedColums,
            columDatas:this.dataObject
          }
        }).then(response=>{
          valloading.close();
          let validateFailed = false
          if(response!=null){
            const validateFailedKeys = Object.keys(response)
            let failtMes = ""
            if(validateFailedKeys!=null&&validateFailedKeys.length>0){
              validateFailed = true
              validateFailedKeys.forEach(validateFailedKey=>{
                const failedReason = response[validateFailedKey]
                failtMes+=(validateFailedKey+":"+failedReason+"<br><br>")
              })

              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">'+failtMes+'</span>'
              })
            }
          }

          if(!validateFailed){
            const loading = this.$loading({
              lock: true,
              text: '保存报送信息中.......',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            this.BaseRequest({
              url:"/reportCust/saveSimpleUnitContext",
              method:'post',
              data:{
                definedColums:this.definedColums,
                columDatas:this.dataObject
              }
            }).then(response=>{
              loading.close();
              $this.Message.success("保存成功")
              if(needUpdateStep){
                $this.updateStep()
              }else{
                $this.getUnitContext()
              }
            });
          }
        });



      },
      updateStep(){
        const loading = this.$loading({
          lock: true,
          text: '更新报送步骤.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        const $this = this
        this.BaseRequest({
          url:"/reportCust/updateStep",
          method:'get',
          params:{
            reportId:this.reportId
          }
        }).then(response=>{
          loading.close();
          $this.Message.success("更新成功")

          this.$emit("refreshReportFill")
          // this.$router.push({
          //   path: '/record/report/reportFill',
          //   query:{
          //     'reportId':this.reportId
          //   }
          // });
        });
      },
      nextStep(){
        this.saveUnitContext(true)
      }
    },
    mounted:function(){
      this.reportId = this.$route.query.reportId
      this.unitId = this.$route.query.unitId
      this.unitType = this.$route.query.unitType
      this.lastStep = this.$route.query.lastStep
      this.isView = this.$route.query.isView
      this.getUnitContext()
    }
  }
</script>

<style lang="css">
  .el-form-item__label{
    font-size:12px !important;
  }
</style>

<style scoped>
  .el-form{
    margin:0 0 30px 0;
  }

  .el-form-item{
    margin:0 0 10px 0;
  }
</style>
