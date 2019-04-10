<template>
  <div>

    <div v-for="(groupDatas,groupKey) in definedColumsGroup">
      <el-row v-if="Object.keys(groupDatas.elColumDefineds).length>1">
        <el-col align="left" :span="24">
          <el-button @click="addRootNode(groupKey)">添加{{groupDatas.rootTreeNodeName}}</el-button>
        </el-col>
      </el-row>

      <el-row >
        <el-col class="tree_title" v-for="(elColumDefined,key) in groupDatas.elColumDefineds"
                :span="Object.keys(groupDatas.elColumDefineds).length>1?groupDatas.colSpan:4">
          {{elColumDefined.colum_name_cn}}
          {{elColumDefined.colum_point!=null&&elColumDefined.colum_point!=''?'('+elColumDefined.colum_point+')':''}}
        </el-col>
        <el-col align="center" class="tree_title" v-if="Object.keys(groupDatas.elColumDefineds).length>1" :span="(24-groupDatas.colSpan*groupDatas.definedColumsTotal)">
          操作
        </el-col>
      </el-row>

      <el-row  v-for="(elColDatas,dataRowNum) in groupDatas.elRowDatas">
        <el-col  align="left" class="tree_colum"
                 :span="groupDatas.elRowDatas.length>1?groupDatas.colSpan:4"
                 v-for="elColData in elColDatas">
          <el-input v-if="elColData!=null" :disabled="elColData.colum_type==0" v-model="elColData.report_data"></el-input>
          <span v-else> -- </span>
        </el-col>
        <!--{{groupDatas.columNameLink[]}}-->
        <el-col align="center" v-if="groupDatas.elRowDatas.length>1" class="tree_colum" :span="(24-groupDatas.colSpan*groupDatas.definedColumsTotal)">
          <el-button @click="addSonNode(elColDatas,dataRowNum,groupKey)">添加子项</el-button>
          <!--<el-button @click="cpTmpNode(elColDatas,dataRowNum,groupKey)">复制</el-button>-->
          <el-button @click="delTmpNode(elColDatas,dataRowNum,groupKey)">删除</el-button>
        </el-col>
      </el-row>
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
        colSpan:0,
        definedColumsGroup:{
          '123':{
            definedColumsTotal:0,
            definedColums:{},
            elRowDatas:[],
            elColumDefineds:{},
            columNameLink:{
              '1-2':{pre:'管线',name:'上气点',next:'下气点'}
            },
            minLevel:0,
            maxLevel:0,
            rootTreeNodeName:""
          }
        },

        minLevel:0,
        maxLevel:0,
        rootTreeNodeName:""
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
            console.log("response running....")
            this.definedColums = response.definedColums

            //分组
            const definedColumsGroup = {}
            const treeDatasGroup = {}

            response.definedColums.forEach(definedColum=>{
              const groupId = definedColum['group_id']
              if(definedColumsGroup[groupId]==null){
                definedColumsGroup[groupId] = []
                treeDatasGroup[groupId]={
                  definedColumsTotal:0,
                  colSpan:0,
                  definedColums:[],
                  elColumDefineds:{},
                  columNameLink:{},
                  columDatas:[],
                  elRowDatas:[],
                  minLevel:0,
                  maxLevel:0,
                  rootTreeNodeName:''
                }
              }
              definedColumsGroup[groupId].push(definedColum)
              const groupArray = definedColumsGroup[groupId]
              console.log(groupArray)
              const columNameLink = treeDatasGroup[groupId].columNameLink
              columNameLink[definedColum.unit_id+'-'+definedColum.colum_id] = {}
              columNameLink[definedColum.unit_id+'-'+definedColum.colum_id].name = definedColum.colum_name_cn

              if(groupArray.length>1){
                const preDefinedColum = groupArray[groupArray.length-2]
                const preUnitId = preDefinedColum.unit_id
                const preColumId = preDefinedColum.colum_id
                columNameLink[definedColum.unit_id+'-'+definedColum.colum_id].pre =  columNameLink[preUnitId+'-'+preColumId].name
                columNameLink[preUnitId+'-'+preColumId].next = definedColum.colum_name_cn
              }
            })


            const groupKeys = Object.keys(definedColumsGroup)
            groupKeys.forEach(groupKey=>{
              const defineds = definedColumsGroup[groupKey]
              const definedColumsTotal = defineds.length
              const colSpan = Math.floor(24/(definedColumsTotal+1))
              const elColumDefineds = this.elColumDefined(defineds,0,0,treeDatasGroup[groupKey])
              treeDatasGroup[groupKey].definedColums = defineds
              treeDatasGroup[groupKey].definedColumsTotal = definedColumsTotal
              treeDatasGroup[groupKey].colSpan = colSpan
              treeDatasGroup[groupKey].elColumDefineds = elColumDefineds
              treeDatasGroup[groupKey].rootTreeNodeName = ''+this.rootTreeNodeName

            })

            response.columDatas.forEach(columData=>{
              const unit_id = columData['unit_id']
              const dimensions_id = columData['dimensions_id']
              response.definedColums.forEach(definedColum=>{
                if(unit_id==definedColum['unit_id']&&dimensions_id==definedColum['colum_id']){
                  console.log(unit_id+"--"+dimensions_id+"--"+definedColum['unit_id']+"---"+definedColum['colum_id'])
                  treeDatasGroup[definedColum['group_id']].columDatas.push(columData)
                }
              })
            })

            groupKeys.forEach(groupKey=>{
              const columDatas = treeDatasGroup[groupKey].columDatas
              const groupResult = this.groupDatas(columDatas)
              const elRowDatas = this.makeElRows(groupResult,treeDatasGroup[groupKey])
              treeDatasGroup[groupKey].elRowDatas = elRowDatas

            })

            this.definedColumsGroup = treeDatasGroup
            //
            // this.definedColumsTotal = response.definedColums.length
            // this.colSpan = Math.floor(24/(this.definedColumsTotal+1))
            // this.elColumDefineds = this.elColumDefined(response.definedColums,0,0,0)
            // const groupResult = this.groupDatas(response.columDatas)
            // this.elRowDatas = this.makeElRows(groupResult)
          }
        }).catch(error=>{
            this.Message.success(error)
            loading.close()
          }
        );
      },
      saveUnitContext(needUpdateStep){
        const $this = this

        const saveColums = new Array()

        const groupIds = Object.keys(this.definedColumsGroup)
        groupIds.forEach(groupId=>{
          const saveObjTmp={}
          const saveColumDatasTmp = []
          const elRowDatas = this.definedColumsGroup[groupId].elRowDatas
          console.log(elRowDatas)
          elRowDatas.forEach((elRowData,rowNum)=>{
            elRowData.forEach(elColumCol=>{
              if(elColumCol!=null){
                elColumCol.colum_id = rowNum
                saveColumDatasTmp.push(elColumCol)
              }
            })
          })
          saveObjTmp.groupId = groupId
          saveObjTmp.columDatas = saveColumDatasTmp
          saveObjTmp.definedColums = this.definedColumsGroup[groupId].definedColums
          saveColums.push(saveObjTmp)
        })

        const loading = this.$loading({
          lock: true,
          text: '保存报送信息中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/treeReportCust/saveTreeData",
          method:'post',
          data:saveColums
        }).then(response=>{
          loading.close();
          $this.Message.success("保存成功")
          if(needUpdateStep){
            $this.updateStep()
          }else{
            $this.getUnitContext()
          }
        });

        //重写colum_id 每一行的行号作为column_id
        // this.elRowDatas.forEach((elRowData,rowNum)=>{
        //   elRowData.forEach(elColumCol=>{
        //     if(elColumCol!=null){
        //       elColumCol.colum_id = rowNum
        //       saveColums.push(elColumCol)
        //     }
        //   })
        // })
        //
        //
        // // validateSimpleUnitContext
        // const valloading = this.$loading({
        //   lock: true,
        //   text: '数据校验中.......',
        //   spinner: 'el-icon-loading',
        //   background: 'rgba(0, 0, 0, 0.7)'
        // });
        // this.BaseRequest({
        //   url:"/reportCust/validateSimpleUnitByDimensions",
        //   method:'post',
        //   data:{
        //     definedColums:this.definedColums,
        //     columDatas:saveColums
        //   }
        // }).then(response=>{
        //   valloading.close();
        //   let validateFailed = false
        //   if(response!=null){
        //     const validateFailedKeys = Object.keys(response)
        //     let failtMes = ""
        //     if(validateFailedKeys!=null&&validateFailedKeys.length>0){
        //       validateFailed = true
        //       validateFailedKeys.forEach(validateFailedKey=>{
        //         const failedReason = response[validateFailedKey]
        //         failtMes+=(validateFailedKey+":"+failedReason+"<br><br>")
        //       })
        //
        //       this.$notify({
        //         dangerouslyUseHTMLString: true,
        //         message: '<span style="font-size:15px;color:red;font-weight: bold">'+failtMes+'</span>'
        //       })
        //     }
        //   }
        //
        //   if(!validateFailed){
        //     const loading = this.$loading({
        //       lock: true,
        //       text: '保存报送信息中.......',
        //       spinner: 'el-icon-loading',
        //       background: 'rgba(0, 0, 0, 0.7)'
        //     });
        //     this.BaseRequest({
        //       url:"/reportCust/overrideSimpleUnitContext",
        //       method:'post',
        //       data:{
        //         definedColums:this.definedColums,
        //         columDatas:saveColums
        //       }
        //     }).then(response=>{
        //       loading.close();
        //       $this.Message.success("保存成功")
        //       if(needUpdateStep){
        //         $this.updateStep()
        //       }else{
        //         $this.getUnitContext()
        //       }
        //     });
        //   }
        // });
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
      makeElRows(groupDatas,treeGroup){
        //单元-组-行-列
        // definedColumsTotal:0,
        //   colSpan:0,
        //   elColumDefineds:{},
        // columDatas:[],
        //   elRowDatas:[]

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
              const colArray =new Array(treeGroup.definedColumsTotal).fill(null);
              rowData.forEach(colData=>{
                const report_id = colData['report_id']
                const unit_id = colData['unit_id']
                const report_group_id = colData['report_group_id']
                const colum_id = colData['colum_id']
                const dimensions_id = colData['dimensions_id']
                const report_data = colData['report_data']
                if(treeGroup.elColumDefineds[unit_id+'-'+dimensions_id]!=null){
                  const elColumDefined = treeGroup.elColumDefineds[unit_id+'-'+dimensions_id]
                  const columOrder = elColumDefined['columOrder']
                  const colum_type = elColumDefined['colum_type']
                  colData.colum_type = colum_type
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
      elColumDefined(definedColums, startParentId,level,treeDatasGroup){
        const resultObj = {}
        if(definedColums!=null&&definedColums.length>0){
          definedColums.forEach((definedColum,forTime)=>{
            const unit_id = definedColum.unit_id
            const colum_id = definedColum.colum_id
            const parent_id = definedColum.parent_id
            if(parent_id==startParentId){
              if(level<treeDatasGroup.minLevel){
                treeDatasGroup.minLevel = level
              }

              if(startParentId==0){
                this.rootTreeNodeName = definedColum.colum_name_cn
              }

              if(level>treeDatasGroup.maxLevel){
                treeDatasGroup.maxLevel = level
              }
              definedColum.level = level
              definedColum.columOrder = forTime

              resultObj[unit_id+'-'+colum_id] = definedColum

              Object.assign(resultObj,this.elColumDefined(definedColums,colum_id,level+1,treeDatasGroup))
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
      addRootNode(groupKey){
        this.definedColumsGroup[groupKey].elRowDatas.splice(0,0,JSON.parse(JSON.stringify(this.definedColumsGroup[groupKey].elRowDatas[0])))
      },

      addSonNode(parentElRowData, parentRowNum,groupId){
        let parentRowLevel = 0
        let sonRowLevel = 0
        let insertRowNum = 0

        if(parentRowNum==this.definedColumsGroup[groupId].maxLevel){
          return
        }

        const parentBaseInfo = {}

        parentElRowData.forEach(parentElColData=>{
          if(parentElColData!=null){
            const unit_id = parentElColData.unit_id
            const dimensions_id = parentElColData.dimensions_id
            const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id+"-"+dimensions_id]
            parentRowLevel = elColumDefined.level
            sonRowLevel = parentRowLevel+1
            parentBaseInfo.report_group_id = parentElColData.report_group_id
            parentBaseInfo.report_id = parentElColData.report_id
            parentBaseInfo.unit_id = parentElColData.unit_id
          }
        })

        let insertRowData = null

        for(let rowIndex = 0;rowIndex<this.definedColumsGroup[groupId].elRowDatas.length;rowIndex++){
          const checkRow = this.definedColumsGroup[groupId].elRowDatas[rowIndex]
          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<checkRow.length;colIndex++) {
            const elColTMp = checkRow[colIndex]
            if (elColTMp != null) {
              const report_group_id = elColTMp.report_group_id
              const report_id = elColTMp.report_id
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              if(report_group_id==parentBaseInfo.report_group_id&&
                report_id==parentBaseInfo.report_id&&
                unit_id==parentBaseInfo.unit_id&&
                checkRowLevel == sonRowLevel
              ){
                insertRowData = JSON.parse(JSON.stringify(checkRow))
              }
              break
            }
          }
        }

        //循环找到第一个当前需要增加子节点的节点的行 在该行前面插入需要增加的子节点
        for(let insertIndex = (parentRowNum+1);insertIndex<this.definedColumsGroup[groupId].elRowDatas.length;insertIndex++){
          const checkRow = this.definedColumsGroup[groupId].elRowDatas[insertIndex]
          console.log(checkRow)

          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<checkRow.length;colIndex++) {
            const elColTMp = checkRow[colIndex]
            if (elColTMp != null) {
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              break
            }
          }

          if(checkRowLevel == parentRowLevel){//前插
            insertRowNum = insertIndex
            break;
          }else if(checkRowLevel < parentRowLevel){//前插
            insertRowNum = insertIndex
            break;
          }else if(checkRowLevel==this.definedColumsGroup[groupId].maxLevel){//最后插
            insertRowNum = insertIndex+1
            break;
          }
        }

        insertRowData.forEach(insertColData=>{
          if(insertColData!=null){
            insertColData.report_data = ""
          }
        })

        this.definedColumsGroup[groupId].elRowDatas.splice((insertRowNum),0,insertRowData)
      },

      cpTmpNode(cpElRowData, cpRowNum,groupId){
        let cpRowLevel = 0

        cpElRowData.forEach(cpColData=>{
          if(cpColData!=null){
            const unit_id = cpColData.unit_id
            const dimensions_id = cpColData.dimensions_id
            const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id+"-"+dimensions_id]
            cpRowLevel = elColumDefined.level
            //array.splice(2, 0, "three");
          }
        })

        const cpArrayTmp = []
        let endRowNum = 0
        for(let cpIndex = (cpRowNum+1);cpIndex<this.definedColumsGroup[groupId].elRowDatas.length;cpIndex++){
          const elRowDataTmp =  this.definedColumsGroup[groupId].elRowDatas[cpIndex]
          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<elRowDataTmp.length;colIndex++) {
            const elColTMp = elRowDataTmp[colIndex]
            if (elColTMp != null) {
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              break
            }
          }
          if (cpRowLevel < checkRowLevel) {//需要复制
            cpArrayTmp.push(elRowDataTmp)
            console.log("need")
          } else {
            endRowNum = cpIndex
            console.log("not need")

            break
          }
        }
        this.definedColumsGroup[groupId].elRowDatas.splice(endRowNum,0,cpElRowData)
        cpArrayTmp.forEach((cpTmp,i)=>{
          this.definedColumsGroup[groupId].elRowDatas.splice((endRowNum+i+1),0,cpTmp)
        })
      },
      delTmpNode(delElRowData, delRowNum,groupId){
        let delRowLevel = 0
        delElRowData.forEach(delColData=>{
          if(delColData!=null){
            const unit_id = delColData.unit_id
            const dimensions_id = delColData.dimensions_id
            const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id+"-"+dimensions_id]
            delRowLevel = elColumDefined.level
          }
        })

        const delArrayTmp = []
        let endRowNum = 0
        for(let delIndex = (delRowNum+1);delIndex<this.definedColumsGroup[groupId].elRowDatas.length;delIndex++){
          const elRowDataTmp =  this.definedColumsGroup[groupId].elRowDatas[delIndex]
          let checkRowLevel = 0
          for(let colIndex = 0;colIndex<elRowDataTmp.length;colIndex++) {
            const elColTMp = elRowDataTmp[colIndex]
            if (elColTMp != null) {
              const unit_id = elColTMp.unit_id
              const dimensions_id = elColTMp.dimensions_id
              const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
              checkRowLevel = elColumDefined.level
              break
            }
          }
          if (delRowLevel < checkRowLevel) {//需要删除
          } else {
            endRowNum = delIndex

            break
          }
        }
        this.definedColumsGroup[groupId].elRowDatas.splice(delRowNum,(endRowNum-delRowNum))
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
    /*text-align: left;*/
  }

  .backgroud-type1{
    background-color: #8cc5ff;
  }

  .backgroud-type2{
    background-color: #00ffff;
  }

  .skip_line{
    border-top:1px solid black;
  }
</style>
