<template>
  <div>

    <el-row >
      <el-col class="tree_title" v-for="(elColumDefined,key) in elColumDefineds" :span="colSpan">{{elColumDefined.colum_name_cn}}</el-col>
    </el-row>

    <el-row v-for="(elColDatas,dataRowNum) in elRowDatas">
      <el-col class="tree_colum" :span="colSpan" v-for="elColData in elColDatas">
        <el-input v-if="elColData!=null" v-model="elColData.report_data"></el-input>
        <span v-else> -- </span>
      </el-col>

      <el-col class="tree_colum" :span="(24-colSpan*definedColumsTotal)">
        <el-button @click="cpTmpNode(elColDatas,dataRowNum)">复制</el-button>
        <el-button @click="delTmpNode(elColDatas,dataRowNum)">删除</el-button>
      </el-col>
    </el-row>
    
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
        colSpan:0,
        definedColumsTotal:0,
        elRowDatas:[],
        elColumDefineds:{},
        minLevel:0,
        maxLevel:0
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
            this.definedColumsTotal = response.definedColums.length
            this.colSpan = Math.floor(24/(this.definedColumsTotal+1))
            this.elColumDefineds = this.elColumDefined(response.definedColums,0,0,0)
            const groupResult = this.groupDatas(response.columDatas)
            this.elRowDatas = this.makeElRows(groupResult)
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
      treeColumsDefined(definedColums, startParentId,level){
        const columTreeArray = []
        if(definedColums!=null&&definedColums.length>0){
          definedColums.forEach((definedColum,forTime)=>{
            definedColums.level = level
            let columTree = {}

            const unit_id = definedColum.unit_id
            const colum_id = definedColum.colum_id
            const colum_name = definedColum.colum_name
            const colum_name_cn = definedColum.colum_name_cn
            const parent_id = definedColum.parent_id

            if(parent_id==startParentId){
              columTree = definedColum
              const getChild = this.treeColumsDefined(definedColums,colum_id,level+1)
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
            const unit_id = columData.unit_id
            const colum_id = columData.colum_id
            if(groupDataTmp[unit_id]==null)
              groupDataTmp[unit_id] = {}



            if(groupDataTmp[unit_id][report_group_id]==null){
              groupDataTmp[unit_id][report_group_id] = {}
            }

            if(groupDataTmp[unit_id][report_group_id][colum_id]==null){
              groupDataTmp[unit_id][report_group_id][colum_id] = []
            }

            groupDataTmp[unit_id][report_group_id][colum_id].push(columData)

          })
        }
        return groupDataTmp
      },
      makeElRows(groupDatas){
        //单元-组-行-列
        const elRows = new Array()
        const $this = this
        const groupIds = Object.keys(groupDatas)
        groupIds.forEach(groupId=>{
          const unitDatas = groupDatas[groupId]
          const unitDataKeys = Object.keys(unitDatas)
          unitDataKeys.forEach(unitDataKey=>{
            const rows = unitDatas[unitDataKey]
            const rowKeys = Object.keys(rows)
            rowKeys.forEach(rowKey=>{
              const rowData = rows[rowKey]
              const colArray =new Array(this.definedColumsTotal).fill(null);
              rowData.forEach(colData=>{
                const report_id = colData['report_id']
                const unit_id = colData['unit_id']
                const report_group_id = colData['report_group_id']
                const colum_id = colData['colum_id']
                const dimensions_id = colData['dimensions_id']
                const report_data = colData['report_data']
                if(this.elColumDefineds[unit_id+'-'+dimensions_id]!=null){
                  const elColumDefined = this.elColumDefineds[unit_id+'-'+dimensions_id]
                  const columOrder = elColumDefined['columOrder']
                  colArray[columOrder] = colData
                }

              })

              elRows.push(colArray)
            })
          })
        })
        return elRows
      },

      //项目组合-单元-项目=数组
      treeColumsData(groupDataTree,definedColumsTree,level){

        if(definedColumsTree!=null&&definedColumsTree.length>0){
          definedColumsTree.forEach(columDefinedTree=>{
            const unit_id = columDefinedTree.unit_id
            const colum_id = columDefinedTree.colum_id
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
      elColumDefined(definedColums, startParentId,level){
        const resultObj = {}
        if(definedColums!=null&&definedColums.length>0){
          definedColums.forEach((definedColum,forTime)=>{
            const unit_id = definedColum.unit_id
            const colum_id = definedColum.colum_id
            const parent_id = definedColum.parent_id
            if(parent_id==startParentId){
              if(level<this.minLevel){
                this.minLevel = level
              }

              if(level>this.maxLevel){
                this.maxLevel = level
              }
              definedColum.level = level
              definedColum.columOrder = forTime

              resultObj[unit_id+'-'+colum_id] = definedColum

              Object.assign(resultObj,this.elColumDefined(definedColums,colum_id,level+1))
            }
          })
        }
        return resultObj
      },

      /**
       * @param groupDataTree 全量报送数据OBJECT
       * @param definedColumsTree 当前树状结构层级下的节点定义列表ARRAY
       * @param level 当前节点数组所在树状结构中的层级 INT 0开始
       */
      elColumDatas(groupDataTree,definedColumsTree,level,resultRowArray){
        const colArray =new Array(6).fill(null);

        resultRowArray.push(colArray)

        definedColumsTree.forEach((columDefinedTree,forTime)=> {
          const unit_id = columDefinedTree.unit_id
          const dimensions_id = columDefinedTree.colum_id
          const children = columDefinedTree.children

          const dataObj = groupDataTree[unit_id+'-'+dimensions_id]
          const columKeys = Object.keys(dataObj)
          if(columKeys.length>1){
            columKeys.forEach(columKey=>{
              const dataVal = dataObj[columKey]
              const cloneCOlumDefined = JSON.parse(JSON.stringify(columDefinedTree));
              cloneCOlumDefined.data_value = dataVal

            })
          }

          columDefinedTree.treeData = groupDataTree[unit_id+'-'+colum_id]
          columDefinedTree.level = level

          colArray[level+forTime] = columDefinedTree;


          if(children!=null&&children.length>0){

            this.elColumDatas(groupDataTree,children,level+1,resultRowArray)
          }
        })
      },
      cpTmpNode(cpElRowData, cpRowNum){
        let cpRowLevel = 0

        cpElRowData.forEach(cpColData=>{
          if(cpColData!=null){
            const unit_id = cpColData.unit_id
            const dimensions_id = cpColData.dimensions_id
            const elColumDefined = this.elColumDefineds[unit_id+"-"+dimensions_id]
            cpRowLevel = elColumDefined.level
            //array.splice(2, 0, "three");
          }
        })

        const cpArrayTmp = []
        let endRowNum = 0
        for(let cpIndex = (cpRowNum+1);cpIndex<this.elRowDatas.length;cpIndex++){
          const elRowDataTmp =  this.elRowDatas[cpIndex]
          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<elRowDataTmp.length;colIndex++) {
            const elColTMp = elRowDataTmp[colIndex]
            if (elColTMp != null) {
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              break
            }
          }
          if (cpRowLevel <= checkRowLevel) {//需要复制
            cpArrayTmp.push(elRowDataTmp)
            console.log("need")
          } else {
            endRowNum = cpIndex
            console.log("not need")

            break
          }
        }
        this.elRowDatas.splice(endRowNum,0,cpElRowData)
        cpArrayTmp.forEach((cpTmp,i)=>{
          this.elRowDatas.splice((endRowNum+i+1),0,cpTmp)
        })
      },
      delTmpNode(delElRowData, delRowNum){
        let delRowLevel = 0

        delElRowData.forEach(delColData=>{
          if(delColData!=null){
            const unit_id = delColData.unit_id
            const dimensions_id = delColData.dimensions_id
            const elColumDefined = this.elColumDefineds[unit_id+"-"+dimensions_id]
            delRowLevel = elColumDefined.level
          }
        })

        const delArrayTmp = []
        let endRowNum = 0
        for(let delIndex = (delRowNum+1);delIndex<this.elRowDatas.length;delIndex++){
          const elRowDataTmp =  this.elRowDatas[delIndex]
          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<elRowDataTmp.length;colIndex++) {
            const elColTMp = elRowDataTmp[colIndex]
            if (elColTMp != null) {
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              break
            }
          }
          if (delRowLevel <= checkRowLevel) {//需要删除
            console.log("need")
          } else {
            endRowNum = delIndex
            console.log("not need")

            break
          }
        }
        this.elRowDatas.splice(delRowNum,(endRowNum-delRowNum))
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
  .tree_title{
    padding:10px 5px 10px 5px;
    background: #8cc5ff;
    border:1px solid white;
  }

  .tree_colum{
    padding:5px 2px 5px 2px;
  }
</style>
