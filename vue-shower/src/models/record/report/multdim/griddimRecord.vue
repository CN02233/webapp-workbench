<template>
  <div>
    <el-form ref="form"  label-width="40%">
      <el-table
        :data="definedCells"
        tooltip-effect="dark"
        border
        stripe
        style="width: 100%">
        <el-table-column label="序号"  type="index" width="60" align="center"></el-table-column>
        <el-table-column label="项目" prop="colum_name_cn"></el-table-column>
        <el-table-column v-for="col in definedDimensions" :label="col.colum_name_cn" width="160" >
          <template slot-scope="scope">
            <el-input v-model="scope.row[col.dim_id]"></el-input>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <br/>
    <el-button @click="saveUnitContext(false)" type="info">保存</el-button>
    <!--<el-button type="primary">上一步</el-button>-->
    <el-button v-if="lastStep=='true'" @click="nextStep" type="success">下一步</el-button>
  </div>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "griddimRecord",
    describe:"多维静态报表填报单元",
    components: {
      WorkMain
    },
    data() {
      return {
        reportId:"",
        unitId:"",
        unitType:"",
        lastStep:false,
        definedIndexs:[],
        definedDimensions:[],
        definedCells:[],
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
            const $t = this
            let imap = {}, dmap = {}
            $t.definedIndexs = []
            $t.definedDimensions = []
            $t.definedCells = []
            $t.definedColums = []
            response.definedColums.forEach(x=>{
              if(x.colum_meta_type == 2){
                $t.definedIndexs.push(x)
                imap[x.colum_id] = x
              }else if(x.colum_meta_type == 3){
                $t.definedDimensions.push(x)
                dmap[x.dim_id] = x
              }
            })
            response.definedColums.forEach(x=>{
              if(x.colum_meta_type == 1){
                let xx = Object.assign({}, x)
                xx.colum_name = imap[xx.colum_id].colum_name
                xx.colum_name_cn = imap[xx.colum_id].colum_name_cn
                xx.dim_name = dmap[xx.dim_id].colum_name
                xx.dim_name_cn = dmap[xx.dim_id].colum_name_cn
                $t.definedColums.push(xx)
              }
            })
            $t.definedCells = $t.definedIndexs.map(x=>{
              let xx = Object.assign({}, x)
              response.columDatas.forEach(c=>{
                if(xx.colum_id == c.colum_id){
                  xx[c.dimensions_id] =  c.report_data || ''
                }
              })
              return xx
            })
            response.columDatas.forEach(columData=>{
              const columKey = columData.unit_id + "_"+columData.colum_id + "_"+columData.dimensions_id
              $t.columDatas[columKey] = columData
              $t.columDatas[columKey].report_data = $t.columDatas[columKey].report_data || ''
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
        let validDatas = this.getValidContext()
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
            definedColums:validDatas.definedColums,
            columDatas:validDatas.columDatas
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
              url:"/reportCust/saveGridUnitContext",
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
      getValidContext(){
        const $t = this
        $t.definedCells.forEach(x=>{
          $t.definedDimensions.forEach(y=>{
            const key = y.unit_id + '_' + x.colum_id + '_' + y.dim_id
            $t.columDatas[key].report_data = x[y.dim_id]
          })
        })
        let cols = [], ds = []
        $t.definedColums.forEach((x,i)=>{
          let xx = Object.assign({}, x)
          xx.colum_name_cn = x.colum_name_cn + '-' + x.dim_name_cn
          xx.colum_id = i + 1
          cols.push(xx)
          let key = x.unit_id + '_' + x.colum_id + '_' + x.dim_id
          let cc = Object.assign({}, $t.columDatas[key])
          cc.colum_id = xx.colum_id
          ds.push(cc)
        })
        return {
          definedColums:cols,
          columDatas:ds
        }
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
