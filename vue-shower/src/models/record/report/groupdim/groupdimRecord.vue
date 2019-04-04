<template>
  <div>
    <el-form ref="form" label-width="40%">
      <div v-for="group in definedGroup">
        <el-form-item :label="group.colum_name_cn"></el-form-item>
        <el-form-item v-for="col in group.children" :label="col.colum_name_cn">
          <el-input v-model="col.report_data" style="width:50%;float: left;" ></el-input>
        </el-form-item>
      </div>
    </el-form>

    <el-button @click="saveUnitContext(false)" type="info">保存</el-button>
    <!--<el-button type="primary">上一步</el-button>-->
    <el-button v-if="lastStep=='true'" @click="nextStep" type="success">下一步</el-button>
  </div>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "groupdimRecord",
    describe:"一维动态报表填报单元",
    components: {
      WorkMain
    },
    data() {
      return {
        reportId:"",
        unitId:"",
        unitType:"",
        lastStep:false,
        definedGroup:[],
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
          this.definedGroup = []
          this.definedColums = []
          if(response){
            const $t = this
            if(response.columDatas){
              response.columDatas.forEach(columData=>{
                const columKey = columData.unit_id + "_"+columData.colum_id
                $t.columDatas[columKey] = columData
              })
            }
            response.definedColums.forEach(x=>{
              if(x.group_id == null){
                let xx = Object.assign({children:[]}, x)
                $t.definedGroup.push(xx)
              }else{
                $t.definedColums.push(x)
              }
            })
            $t.definedGroup.forEach(t=>{
              response.definedColums.forEach(x=>{
                if(t.colum_id == x.group_id){
                  let xx = Object.assign({}, x)
                  if($t.columDatas[x.unit_id+'_'+x.colum_id])
                    xx.report_data = $t.columDatas[x.unit_id+'_'+x.colum_id].report_data
                  t.children.push(xx)
                }
              })
            })
          }
        }).catch(error=>{
            this.Message.success(error)
            loading.close()
          }
        );
      },
      saveUnitContext(needUpdateStep){
        const $this = this
        $this.definedGroup.forEach(g=>{
          g.children.forEach(x=>{
            if(x.report_data)
              $this.columDatas[x.unit_id+'_'+x.colum_id].report_data = x.report_data
          })
        })
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
              url:"/reportCust/saveSimpleUnitContext",
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
        this.saveUnitContext(true)
      }
    },
    mounted:function(){
      this.reportId = this.$route.query.reportId
      this.unitId = this.$route.query.unitId
      this.unitType = this.$route.query.unitType
      this.unitType = this.$route.query.unitType
      this.lastStep = this.$route.query.lastStep
      this.getUnitContext()
    }
  }
</script>

<style scoped>

</style>

