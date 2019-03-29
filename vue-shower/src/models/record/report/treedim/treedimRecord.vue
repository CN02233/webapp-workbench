<template>
  <div>

    <div v-for="columDataGroup in columDatasTree">
      <div v-for="columData in columDataGroup">
        <el-row>
          <ColumTitles :columTemplateData="columData" :colNum="colNum" ></ColumTitles>
        </el-row>
        <ColumContext :columTemplateData="columData" :totalColNum="definedColumsTotal"  :colNum="colNum" ></ColumContext>
      </div>


    </div>



    <el-button @click="saveUnitContext(false)" type="info">保存</el-button>
    <!--<el-button type="primary">上一步</el-button>-->
    <el-button v-if="lastStep=='true'" @click="nextStep" type="success">下一步</el-button>
  </div>

</template>



<script>
  import WorkMain from "@/models/public/WorkMain"

  import ColumTitles from "@/models/record/report/treedim/treeDimShowerTitle"
  import ColumContext from "@/models/record/report/treedim/treeDimShowerContext"

  export default {
    name: "treedimRecord",
    describe:"树状报表填报单元",
    components: {
      WorkMain,
      ColumTitles,
      ColumContext
    },
    data() {
      return {
        reportId:"",
        unitId:"",
        unitType:"",
        lastStep:false,
        definedColums:{},
        columDatas:{},
        columGroups:{},
        treeLevelTmp:{},
        forTime:0,
        colNum:0,
        definedColumsTotal:0,
        definedColumsTree:[],
        columDatasTree:[]
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
            // this.definedColums = response.definedColums

            this.definedColumsTree = this.treeColumsDefined(response.definedColums,0)
            this.definedColumsTotal = response.definedColums.length

            const groupDatas = this.groupDatas(response.columDatas)
            const groups = Object.keys(groupDatas)
            if(groups!=null&&groups.length>0){
              this.columDatasTree = []
              groups.forEach(groupKey=>{
                this.columDatasTree.push(this.treeColumsData(groupDatas[groupKey],this.definedColumsTree.slice(),1))
              })

            }


            let treeLevelTmp = {}

            let forTag = true
            let lastParentIdTmp = 0
            let levelTmp = 0
            while(forTag){
              let definedColumIdTmp = null
              response.definedColums.forEach(definedColum=>{
                const parent_id= definedColum.parent_id
                if(parent_id==lastParentIdTmp){
                  if(treeLevelTmp['L-'+levelTmp]==null){
                    treeLevelTmp['L-'+levelTmp] = {level:levelTmp,ids:[]}
                  }

                  treeLevelTmp['L-'+levelTmp].ids.push(definedColum)
                  definedColumIdTmp = definedColum.colum_id
                  this.definedColums[definedColum.colum_id] = definedColum
                }
              })

              if(!definedColumIdTmp){
                forTag = false
              }

              lastParentIdTmp = definedColumIdTmp
              levelTmp++
            }

            this.treeLevelTmp = treeLevelTmp

            this.colNum = Math.floor(24/(response.definedColums.length+1))

            if(response.columDatas){
              response.columDatas.forEach(columData=>{
                const groupId = columData['report_group_id']
                if(this.columGroups[groupId]==null){
                  this.columGroups[groupId] = {}
                }

                const unit_id = columData.unit_id
                const colum_id = columData.colum_id
                const columKey = groupId+'-'+unit_id+'-'+colum_id
                if(this.columGroups[groupId][columKey]==null){
                  this.columGroups[groupId][columKey] = new Array()
                }

                this.columGroups[groupId][columKey].push(columData)
              })
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
        });
      },
      nextStep(){
        this.saveUnitContext(true)
      },
      countLevelIds(ids){
        console.log("count level ids :"+ids)
      },
      treeColumsDefined(definedColums, startParentId){
        const columTreeArray = []
        if(definedColums!=null&&definedColums.length>0){
          definedColums.forEach(definedColum=>{
            let columTree = {}

            const unit_id = definedColum.unit_id
            const colum_id = definedColum.colum_id
            const colum_name = definedColum.colum_name
            const colum_name_cn = definedColum.colum_name_cn
            const parent_id = definedColum.parent_id

            if(parent_id==startParentId){
              columTree = definedColum
              const getChild = this.treeColumsDefined(definedColums,colum_id)
              columTree.children = getChild
              columTreeArray.push(columTree)
            }
          })
        }
        return columTreeArray
      },
      groupDatas(columDatas){
        let groupDataTmp = {}

        if(columDatas!=null&&columDatas.length>0){
          columDatas.forEach(columData=>{
            const report_group_id = columData.report_group_id
            if(groupDataTmp[report_group_id]==null)
              groupDataTmp[report_group_id] = {}

            const unit_id = columData.unit_id
            const colum_id = columData.colum_id
            const dimensions_id = columData.dimensions_id
            const report_data = columData.report_data

            if(groupDataTmp[report_group_id][unit_id+'-'+dimensions_id]==null){
              groupDataTmp[report_group_id][unit_id+'-'+dimensions_id] = {}
            }

            groupDataTmp[report_group_id][unit_id+'-'+dimensions_id][colum_id] = report_data

          })
        }
        return groupDataTmp
      },
      //项目组合-单元-项目=数组
      treeColumsData(groupDataTree,definedColumsTree,level){

        if(definedColumsTree!=null&&definedColumsTree.length>0){
          definedColumsTree.forEach(columDefinedTree=>{
            const unit_id = columDefinedTree.unit_id
            const colum_id = columDefinedTree.colum_id
            const colum_name = columDefinedTree.colum_name
            const colum_name_cn = columDefinedTree.colum_name_cn
            const parent_id = columDefinedTree.parent_id
            const children = columDefinedTree.children
            if(groupDataTree[unit_id+'-'+colum_id]!=null){
              columDefinedTree.treeData = groupDataTree[unit_id+'-'+colum_id]
              columDefinedTree.level = level
            }
            if(children!=null&&children.length>0){
              this.treeColumsData(groupDataTree,children,level+1)
            }
          })
        }
        return definedColumsTree
      },
      checkInput(index,treeLevel){
        let levelIdsSize = treeLevel.ids.length
        const level = treeLevel.level
        if((index>=level)&&(index<(level+levelIdsSize))){
          return true
        }else
          return false
      }
    },
    mounted:function(){
      this.reportId = this.$route.query.reportId
      this.unitId = this.$route.query.unitId
      this.unitType = this.$route.query.unitType
      this.lastStep = this.$route.query.lastStep
      this.getUnitContext()
    }
  }
</script>

<style scoped>

</style>
