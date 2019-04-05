<template>
  <div>
    <el-row>
      <el-col align="left" :span="24">
        <el-button @click="addGroup">复制组</el-button>
      </el-col>
    </el-row>
    <el-form ref="form" label-width="40%">
      <div v-for="group in definedGroup">
        <div style="text-align:left;margin-bottom: 22px;margin-left:20%;">
          <el-input v-model="group.report_data" style="width:150px;"></el-input>
        </div>
        <el-form-item v-for="col in group.children" :label="col.colum_name_cn">
          <el-tooltip class="item" effect="dark" :content="col.colum_desc" placement="top">
          <el-input v-model="col.report_data" :disabled="col.colum_type==0" style="width:50%;float: left;" >
            <template v-if="col.colum_point!=null&&col.colum_point!=''" slot="append">{{col.colum_point}}</template>
          </el-input>
          </el-tooltip>
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
        baseGroup:[],
        columDatas:{},
        last_dim_id:0,
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
          this.baseGroup = []
          if(response){
            const $t = this
            if(response.columDatas){
              response.columDatas.forEach(columData=>{
                const columKey = columData.unit_id + "_"+columData.colum_id + "_" + (columData.dimensions_id || '0')
                $t.columDatas[columKey] = columData
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
              response.columDatas.forEach(x=>{
                if(t.colum_id == x.colum_id){
                  $t.last_dim_id = x.dimensions_id
                  let tt = Object.assign({children:[],dimensions_id:$t.last_dim_id}, t)
                  tt.report_data = x.report_data || x.colum_name_cn
                  $t.definedColums.forEach(c=>{
                    if(tt.colum_id == c.group_id){
                      let cc = Object.assign({}, c)
                      let key = c.unit_id + '_' + c.colum_id + '_' + $t.last_dim_id
                      cc.report_data = $t.columDatas[key].report_data
                      tt.children.push(cc)
                    }
                  })
                  $t.definedGroup.push(tt)
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
          $this.columDatas[g.unit_id + '_' + g.colum_id + '_' + g.dimensions_id].report_data = g.report_data
          g.children.forEach(x=>{
            $this.columDatas[x.unit_id+'_'+x.colum_id + '_' + g.dimensions_id].report_data = x.report_data
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
      },
      addGroup(){
        const $t = this
        $t.last_dim_id = parseInt($t.last_dim_id) + 1
        $t.baseGroup.forEach(t => {
          let key0 = t.unit_id + '_' + t.colum_id + '_' + $t.last_dim_id
          $t.columDatas[key0] = Object.assign({dimensions_id:$t.last_dim_id,report_data:t.colum_name_cn}, t)
          let tt = Object.assign({children:[],dimensions_id:$t.last_dim_id,report_data:t.colum_name_cn}, t)
          $t.definedColums.forEach(c=>{
            if(tt.colum_id == c.group_id){
              let cc = Object.assign({}, c)
              let key = c.unit_id + '_' + c.colum_id + '_' + $t.last_dim_id
              $t.columDatas[key] = Object.assign({dimensions_id:$t.last_dim_id,report_data:''}, c)
              cc.report_data = ''
              tt.children.push(cc)
            }
          })
          $t.definedGroup.push(tt)
        })
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

