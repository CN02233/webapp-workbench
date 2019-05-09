<template>
  <div>
    <el-form ref="form" label-width="40%">
      <div v-for="(group,ig) in definedGroup">
        <div v-if="ig>0" style="border-top:1px dashed #d0d0d0;margin:0 60px 10px 60px;"></div>

        <el-form-item size="mini"  :label="group.colum_name">
          <el-input style="width:50%;float: left;" :disabled="isView=='Y'" v-model="group.report_data" class="group" ></el-input>

        </el-form-item>
        <el-form-item v-for="col in group.children" size="mini" :label="col.colum_name_cn" :error="col.validate_error">
          <el-tooltip class="item" effect="dark" :content="col.colum_desc" placement="top">
          <el-input  v-model="col.report_data" :disabled="col.colum_type==0||isView=='Y'" style="width:50%;float: left;" >
            <template v-if="col.colum_point!=null&&col.colum_point!=''" slot="append">{{col.colum_point}}</template>
          </el-input>
          </el-tooltip>
        </el-form-item>
      </div>
    </el-form>
    <el-button type="warning" @click="addGroup">新增管道</el-button>
  </div>

</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "GroupdimRecord",
    describe:"一维动态报表填报单元",
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
        definedGroup:[],
        definedColums:[],
        baseGroup:[],
        columDatas:{},
        last_dim_id:0,
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
          // if(loading){
          //   loading.close();
          // }
          //
          this.definedGroup = []
          this.definedColums = []
          this.baseGroup = []
          let report_id = null
          if(response){
            const $t = this
            if(response.columDatas){
              if(response.columDatas.length>0)
                report_id = response.columDatas[0].report_id
                response.columDatas.forEach(columData=>{

                  if(justRefreshFomular){
                    const responseColumDatas= response.columDatas
                    responseColumDatas.forEach(responseColumData=>{
                      const unitId = responseColumData.unit_id
                      const colum_id = responseColumData.colum_id
                      const dimensions_id = responseColumData.dimensions_id

                      response.definedColums.forEach(definedColum=>{

                        if(definedColum.unit_id==unitId&&definedColum.colum_id==colum_id&&definedColum.colum_type=='0'){

                          this.columDatas[unitId+"_"+colum_id+"_"+dimensions_id].report_data = responseColumData.report_data
                        }
                      })
                    })
                    // return
                  }else{
                    const columKey = columData.unit_id + "_"+columData.colum_id + "_" + (columData.dimensions_id || '0')
                    $t.columDatas[columKey] = columData
                  }
                })
            }
            response.definedColums.forEach(x=>{
              if(x.group_id == null){
                $t.baseGroup.push(x)
              }else{
                $t.definedColums.push(x)
              }
            })
            $t.baseGroup.forEach(t=>{
              let responseColumDatas = response.columDatas
              if(justRefreshFomular){
                responseColumDatas = Object.values(this.columDatas)
              }

              responseColumDatas.forEach(x=>{

                if(t.colum_id == x.colum_id){
                  $t.last_dim_id = x.dimensions_id
                  let tt = Object.assign({children:[],dimensions_id:$t.last_dim_id}, t)
                  tt.report_data = x.report_data || x.colum_name_cn
                  $t.definedColums.forEach(c=>{
                    if(tt.colum_id == c.group_id){
                      let cc = Object.assign({}, c)
                      let key = c.unit_id + '_' + c.colum_id + '_' + $t.last_dim_id
                      if($t.columDatas[key]){
                        cc.report_data = $t.columDatas[key].report_data
                      }else if(report_id){
                        //补充遗漏数据
                        let newCC = Object.assign({report_id:report_id,report_data:'',report_group_id:c.group_id,dimensions_id:$t.last_dim_id}, c)
                        $t.columDatas[key] = newCC
                      }
                      tt.children.push(cc)
                    }
                  })
                  $t.definedGroup.push(tt)
                }
              })
            })
          }


        }).catch(error=>{
          console.log(error)
            this.Message.success(error)
            loading.close()
          }
        );
      },
      doSaveUnitContext(processName){
        // const loading = this.$loading({
        //   lock: true,
        //   text: '保存报送信息中.......',
        //   spinner: 'el-icon-loading',
        //   background: 'rgba(0, 0, 0, 0.7)'
        // });
        console.log(this.columDatas)

        const $this = this
        $this.definedGroup.forEach(g=>{
          let key0 = g.unit_id + '_' + g.colum_id + '_' + g.dimensions_id
          if($this.columDatas[key0])
            $this.columDatas[key0].report_data = g.report_data
          g.children.forEach(x=>{
            let key1 = x.unit_id+'_'+x.colum_id + '_' + g.dimensions_id
            if($this.columDatas[key1])
              $this.columDatas[key1].report_data = x.report_data
          })
        })

        console.log(this.definedColums)
        console.log(Object.values(this.columDatas))


        this.BaseRequest({
          url:"/reportCust/saveGroupUnitContext",
          method:'post',
          data:{
            definedColums:this.definedColums,
            columDatas:Object.values(this.columDatas)
          }
        }).then(response=>{
          // loading.close();
          // this.$emit("refreshSaveLoading",this.unitId,"保存成功")
          // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
          this.$emit("saveReportsCallBack",this.unitId,processName)
        }).catch(error => {
          this.$emit("saveReportsCallBack",this.unitId,processName,error)
        });
      },
      doValidateUnitContext(processName){

        const $this = this
        $this.definedGroup.forEach(g=>{
          let key0 = g.unit_id + '_' + g.colum_id + '_' + g.dimensions_id
          if($this.columDatas[key0])
            $this.columDatas[key0].report_data = g.report_data
          g.children.forEach(x=>{
            let key1 = x.unit_id+'_'+x.colum_id + '_' + g.dimensions_id
            if($this.columDatas[key1])
              $this.columDatas[key1].report_data = x.report_data
          })
        })


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
            columDatas:Object.values(this.columDatas)
          }
        }).then(response=>{
          valloading.close();

          console.log(this.columDatas)

          let failtMes = ""
          let validateFailed = false
          if(response!=null){
            const validateFailedKeys = Object.keys(response)
            if(validateFailedKeys!=null&&validateFailedKeys.length>0){
              validateFailed = true
            }
          }else{
            response = {}
          }
          const groupKeys = Object.keys(this.definedGroup)

          groupKeys.forEach(groupKey=>{
            const groupDatga = this.definedGroup[groupKey]
            const columDataObjs = groupDatga.children
            columDataObjs.forEach(columData=>{
              if(response[columData.colum_id]){
                columData.validate_error = response[columData.colum_id]
              }else{
                columData.validate_error = null
              }
            })
          })

          // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
          let failedMessage = null
          if(validateFailed){
            // this.$emit("refreshSaveLoading",this.unitId,"有输入错误")
            failedMessage = "有输入错误"

          }
          const dataTmp = this.definedGroup
          this.definedGroup = null
          this.definedGroup = dataTmp
          this.$emit("validateReportsCallBack",this.unitId,processName,failedMessage)
        });
      },
      saveUnitContext(needUpdateStep){
        const $this = this
        $this.definedGroup.forEach(g=>{
          let key0 = g.unit_id + '_' + g.colum_id + '_' + g.dimensions_id
          if($this.columDatas[key0])
            $this.columDatas[key0].report_data = g.report_data
          g.children.forEach(x=>{
            let key1 = x.unit_id+'_'+x.colum_id + '_' + g.dimensions_id
            if($this.columDatas[key1])
              $this.columDatas[key1].report_data = x.report_data
          })
        })
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
            columDatas:Object.values(this.columDatas)
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
              url:"/reportCust/saveGroupUnitContext",
              method:'post',
              data:{
                definedColums:this.definedColums,
                columDatas:Object.values(this.columDatas)
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
        console.log('next step is running....'+this.unitId)
        console.log("data is "+JSON.stringify(this.columDatas))
        // this.saveUnitContext(true)
      },
      addGroup(){
        const $t = this
        $t.last_dim_id = parseInt($t.last_dim_id) + 1
        $t.baseGroup.forEach((t, i) => {
          let key0 = t.unit_id + '_' + t.colum_id + '_' + $t.last_dim_id
          $t.columDatas[key0] = Object.assign({report_id:$t.reportId,dimensions_id:$t.last_dim_id,report_data:t.colum_name_cn}, t)
          let tt = Object.assign({children:[],dimensions_id:$t.last_dim_id,report_data:t.colum_name_cn}, t)
          $t.definedColums.forEach(c=>{
            if(tt.colum_id == c.group_id){
              let cc = Object.assign({}, c)
              let key = c.unit_id + '_' + c.colum_id + '_' + $t.last_dim_id
              $t.columDatas[key] = Object.assign({report_id:$t.reportId,dimensions_id:$t.last_dim_id,report_data:''}, c)
              $t.columDatas[key].report_group_id = $t.columDatas[key].group_id
              cc.report_data = ''
              tt.children.push(cc)
            }
          })
          $t.definedGroup.push(tt)
        })
      },
      checkStepAndSave(saveLink){
        console.log(saveLink.unit_id)
        console.log("data is "+JSON.stringify(this.columDatas))
        this.$emit("checkStepAndSave",saveLink.nextUnit)
      },
      setSaveFlag(saveFlag){
        this.saveFlag = saveFlag
      }
    },
    mounted:function(){
      this.getUnitContext()
    },
    activated(){
      this.saveFlag = this.$route.params.saveFlag

    }
  }
</script>

<style lang="css">
  .el-form-item__label{
    font-size:12px !important;
  }
</style>

<style scoped>
  .el-form-item{
    margin:0 0 15px 0;
  }
</style>

