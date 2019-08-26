<template>
  <div>
    <el-form ref="form"  label-width="40%">
      <el-form-item :key="dataColum" size="mini" v-for="dataColum in dataObject" :label="dataColum.colum_name_cn" :error="dataColum.validate_error">
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
    name: "OnedimRecord",
    describe:"一维报表填报单元",
    components: {
      WorkMain
    },
    props:{
      reportId:{
        type:String
      },
      unitId:{
        type:Number
      },
      unitType:{
        type:Number
      },
      isView:{
        type:String
      }

    },
    data() {
      return {
        lastStep:false,
        saveFlag:'N',//载入页面后是否保存当前页面数据 Y:保存 N：不做任何动作仅仅载入 S:提交 V：校验
        definedColums:[],
        columDatas:{},
        dataObject:[],
        hasMounted:false
      }
    },
    methods:{
      getUnitContext(justRefreshFomular){
        let loading = null
        if(this.saveFlag=='N') {
          loading = this.$loading({
            lock: true,
            text: '获取填报单元信息中.......',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          })
        }
        this.BaseRequest({
          url:"/reportCust/getUnitContext",
          params:{
            reportId:this.reportId,
            unitId:this.unitId,
            unitType:this.unitType
          }
        }).then(response=>{
          if(loading){
            loading.close();
          }
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
                // if(!this.dataObject[columData.unit_id]){
                //   this.dataObject[columData.unit_id] = {}
                // }
                // debugger
                if(justRefreshFomular){
                  const colum_type = defindObj['C'+columData.colum_id].colum_type
                  if(colum_type=='0'){
                      this.columDatas[columKey] = columData
                      columData.colum_name_cn = defindObj['C'+columData.colum_id].colum_name_cn
                      columData.colum_desc = defindObj['C'+columData.colum_id].colum_desc
                      columData.colum_point = defindObj['C'+columData.colum_id].colum_point
                      columData.colum_type = defindObj['C'+columData.colum_id].colum_type
                  }
                }else{
                  this.columDatas[columKey] = columData
                  columData.colum_name_cn = defindObj['C'+columData.colum_id].colum_name_cn
                  columData.colum_desc = defindObj['C'+columData.colum_id].colum_desc
                  columData.colum_point = defindObj['C'+columData.colum_id].colum_point
                  columData.colum_type = defindObj['C'+columData.colum_id].colum_type
                }
              })
              this.dataObject = Object.values(this.columDatas)
            }



          }
        }).catch(error=>{
            this.Message.success(error)
            loading.close()
          }
        );
      },
      doSomething(){

      },
      doSaveUnitContext(processName){
        this.BaseRequest({
          url:"/reportCust/saveSimpleUnitContext",
          method:'post',
          data:{
            definedColums:this.definedColums,
            columDatas:this.dataObject
          }
        }).then(response=>{
          // this.$emit("refreshSaveLoading",this.unitId,"保存成功")
          // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
          console.log(response)
          if(response){
            this.$emit("saveReportsCallBack",this.unitId,processName)
          }else{
            this.$emit("saveReportsCallBack",this.unitId,processName,"保存失败")
          }
        }).catch(error => {
          this.$emit("saveReportsCallBack",this.unitId,processName,error)
        });
      },

      doValidateUnitContext(processName){
        this.BaseRequest({
          url:"/reportCust/validateSimpleUnitContext",
          method:'post',
          data:{
            definedColums:this.definedColums,
            columDatas:this.dataObject
          }
        }).then(response=>{
          if(response){
          }else{
            this.$emit("validateReportsCallBack",this.unitId,processName,"校验出现异常")
          }

          let validateFailed = false
          let failtMes = ""
          if(response!=null){
            const validateFailedKeys = Object.keys(response)
            if(validateFailedKeys!=null&&validateFailedKeys.length>0){
              validateFailed = true
            }
          }else{
            response = {}
          }

          this.dataObject.forEach(columData=>{
            if(response[columData.colum_id]){
              columData.validate_error = response[columData.colum_id]
            }else{
              columData.validate_error = null
            }
          })

          // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
          let failedMessage = null
          if(validateFailed){
            // this.$emit("refreshSaveLoading",this.unitId,"有输入错误")
            failedMessage = "有输入错误"
          }
          const dataTmp = this.dataObject
          this.dataObject = null
          this.dataObject = dataTmp
          this.$emit("validateReportsCallBack",this.unitId,processName,failedMessage)

        });
      },
      doSubmitContext(){
        this.doSaveUnitContext(true)
      },
      setSaveFlag(saveFlag){
        this.saveFlag = saveFlag
      }
    },
    mounted:function(){
      this.getUnitContext()
    },
    activated(){
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
    margin:0 0 15px 0;
  }
</style>
