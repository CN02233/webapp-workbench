<template>
  <div>

    <div v-for="(groupDatas,groupKey) in definedColumsGroup">
      <el-row v-if="(Object.keys(groupDatas.elColumDefineds).length>1)&&isView!='Y'">
        <el-col align="left" :span="24">
          <!--<el-button type="success" size="small" v-if="isView!='Y'" @click="addRootNode(groupKey)">添加{{groupDatas.rootTreeNodeName}}</el-button>-->
          <el-button type="success" size="small" v-if="isView!='Y'" @click="addRootNode(groupKey)">新增组</el-button>
        </el-col>
      </el-row>
      <el-form ref="form"  label-width="0px">
        <table>
          <thead class="tree_title" >
          <tr>
            <td v-if="elColumDefined.colum_show=='Y'" v-for="(elColumDefined,key) in groupDatas.elColumDefineds">
            <!--<td  v-for="(elColumDefined,key) in groupDatas.elColumDefineds">-->
              <el-tooltip class="item" effect="dark"
                          :content="elColumDefined.colum_point!=null?'单位:'+elColumDefined.colum_point:''"
                          placement="top-start">
                <span>{{elColumDefined.colum_name_cn}}</span>
              </el-tooltip>
            </td>
            <td v-if="Object.keys(groupDatas.elColumDefineds).length>1">
              操作
            </td>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(elColDatas,dataRowNum) in groupDatas.elRowDatas">

            <td v-if="elColData!=null?elColData.colum_show=='Y':true" v-for="elColData in elColDatas">
            <!--<td v-for="elColData in elColDatas">-->
              <el-form-item  v-if="elColData!=null" size="mini" :class="{'error_row':elColData.validate_error!=null}" :error="elColData.validate_error">
                <el-input size="mini" style="width:100%" :disabled="elColData.colum_type==0||isView=='Y'" v-model="elColData.report_data"></el-input>
              </el-form-item>
              <span v-else> -- </span>
            </td>
            <td v-if="checkOptionsShow(groupKey)">
              <!--{{definedColumsGroup[groupKey].maxLevel}}&#45;&#45;{{dataRowNum}}-->
              <el-button size="mini" v-if="checkRowLevel(elColDatas,groupKey)"  @click="addSonNode(elColDatas,dataRowNum,groupKey)">添加子项</el-button>
              <!--<el-button @click="cpTmpNode(elColDatas,dataRowNum,groupKey)">复制</el-button>-->
              <el-button size="mini" v-if="checkShowDelete(elColDatas,groupKey)"  @click="delTmpNode(elColDatas,dataRowNum,groupKey)">删除</el-button>
            </td>
          </tr>
          </tbody>
        </table>
      </el-form>

    </div>

  </div>

</template>



<script>
  import WorkMain from "@/models/public/WorkMain"

  import ColumTitles from "@/models/record/report/treedim/treeDimShowerTitle"
  import ColumContext from "@/models/record/report/treedim/treeDimShowerContext"

  export default {
    name: "TreedimRecord",
    describe:"树状报表填报单元",
    components: {
      WorkMain,
      ColumTitles,
      ColumContext
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
        colSpan:0,
        groupLevelRowCount:{
          "groupId":{
            "level":0
          }
        },
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
            // //console.log(justRefreshFomular)

            if(justRefreshFomular){
              const groups = Object.keys(this.definedColumsGroup)
              groups.forEach(groupKey=>{
                const groupContext = this.definedColumsGroup[groupKey]
                ////console.log(groupContext)
                const elColumDefineds = groupContext.elColumDefineds
                response.columDatas.forEach(columData=>{
                  const unit_id = columData.unit_id
                  const dimensions_id = columData.dimensions_id
                  const colum_id = columData.colum_id

                  if(elColumDefineds[unit_id+"-"+dimensions_id]){
                    const columType = elColumDefineds[unit_id+"-"+dimensions_id].colum_type
                    if(columType==0){
                      groupContext.elRowDatas.forEach(elRowData=>{
                        elRowData.forEach(elData=>{
                          if(elData){
                            // //debugger
                            if(elData.dimensions_id==dimensions_id&&elData.unit_id==unit_id&&elData.colum_id==colum_id){
                              // //console.log("response "+unit_id+"--"+colum_id+"--"+dimensions_id)
                              // //console.log("el data "+elData.unit_id+"--"+elData.colum_id+"--"+elData.dimensions_id)
                              elData.report_data = columData.report_data
                            }
                          }
                        })
                      })
                    }
                  }
                })
              })
              return
            }


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
              let definedColumsTotal = 0
              if(defineds){
                defineds.forEach(definedColumsGroupDt=>{
                  if(definedColumsGroupDt.colum_show=='Y'){
                    definedColumsTotal++
                  }
                })
              }


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
                  ////console.log(unit_id+"--"+dimensions_id+"--"+definedColum['unit_id']+"---"+definedColum['colum_id'])
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
      doSaveUnitContext(processName){
        const $this = this

        const saveColums = new Array()

        const groupIds = Object.keys(this.definedColumsGroup)
        groupIds.forEach(groupId=>{
          const saveObjTmp={}
          const saveColumDatasTmp = []
          const elRowDatas = this.definedColumsGroup[groupId].elRowDatas
          ////console.log(elRowDatas)
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

        this.BaseRequest({
          url:"/treeReportCust/saveTreeData",
          method:'post',
          data:saveColums
        }).then(response=>{
          // this.$emit("refreshSaveLoading",this.unitId,"保存成功")
          // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
          if(response){
            this.$emit("saveReportsCallBack",this.unitId,processName)
          }else{
            this.$emit("saveReportsCallBack",this.unitId,processName,"保存失败")
          }
        }).catch(error => {
          this.getUnitContext(false)
          this.$emit("saveReportsCallBack",this.unitId,processName,error)
        });
      },
      doValidateUnitContext(processName){
        const saveColums = new Array()
        const groupIds = Object.keys(this.definedColumsGroup)
        groupIds.forEach(groupId=>{
          const saveObjTmp={}
          const saveColumDatasTmp = []
          const elRowDatas = this.definedColumsGroup[groupId].elRowDatas
          if(elRowDatas){
            elRowDatas.forEach(elRowData=>{
              if(elRowData){
                elRowData.forEach(colData=>{
                  if(colData){
                    saveColumDatasTmp.push(colData)
                  }
                })
              }
            })
          }
          saveObjTmp.groupId = groupId
          saveObjTmp.columDatas = saveColumDatasTmp
          saveObjTmp.definedColums = this.definedColumsGroup[groupId].definedColums
          saveColums.push(saveObjTmp)
        })

        this.BaseRequest({
          url:"/treeReportCust/validateTreeData",
          method:'post',
          data:saveColums
        }).then(response=>{

          if(response){
          }else{
            this.$emit("validateReportsCallBack",this.unitId,processName,"校验出现异常")
          }


          let validateFailed = false
          if(response!=null){
            const validateFailedKeys = Object.keys(response)
            if(validateFailedKeys!=null&&validateFailedKeys.length>0){
              validateFailed = true
            }
          }else{
            response = {}
          }

          const groupIds =  Object.keys(this.definedColumsGroup)
          groupIds.forEach(groupId=>{
            const groupContext = this.definedColumsGroup[groupId]
            const columDatas = groupContext.columDatas
            columDatas.forEach(columData=>{
              if(response[columData.dimensions_id]){
                columData.validate_error = response[columData.dimensions_id]
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
          const dataTmp = this.definedColumsGroup
          this.definedColumsGroup = null
          this.definedColumsGroup = dataTmp
          this.$emit("validateReportsCallBack",this.unitId,processName,failedMessage)

        });
      },
      saveUnitContext(needUpdateStep){
        const $this = this

        const saveColums = new Array()

        const groupIds = Object.keys(this.definedColumsGroup)
        groupIds.forEach(groupId=>{
          const saveObjTmp={}
          const saveColumDatasTmp = []
          const elRowDatas = this.definedColumsGroup[groupId].elRowDatas
          ////console.log(elRowDatas)
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
      checkStepAndSave(saveLink){
        ////console.log(saveLink.unit_id)
        ////console.log("data is "+JSON.stringify(this.definedColumsGroup))
        this.$emit("checkStepAndSave",saveLink.nextUnit)
      },
      nextStep(){
        ////console.log('next step is running....'+this.unitId)
        ////console.log("data is "+JSON.stringify(this.definedColumsGroup))

        // this.saveUnitContext(true)
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
            let report_group_id = columData.report_group_id
            report_group_id = report_group_id+'G'
            const unit_id = columData.unit_id
            const colum_id = columData.colum_id
            if(groupDataTmp[unit_id]==null)
              groupDataTmp[unit_id] = {}



            if(groupDataTmp[unit_id][report_group_id]==null){
              //console.log(report_group_id)

              groupDataTmp[unit_id][report_group_id] = {}
            }

            if(groupDataTmp[unit_id][report_group_id][colum_id]==null){
              groupDataTmp[unit_id][report_group_id][colum_id] = []
            }

            groupDataTmp[unit_id][report_group_id][colum_id].push(columData)

          })
        }
        //console.log(groupDataTmp)


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

        const groupLevelCount = {}

        groupIds.forEach(groupId=>{
          const unitDatas = groupDatas[groupId]
          const unitDataKeys = Object.keys(unitDatas)
          unitDataKeys.forEach(unitDataKey=>{
            //console.log("unitDataKey : "+unitDataKey)
            if(groupLevelCount[unitDataKey]){

            }else{
              groupLevelCount[unitDataKey] = {}
            }

            //console.log(groupLevelCount)


            const rows = unitDatas[unitDataKey]
            const rowKeys = Object.keys(rows)
            rowKeys.forEach(rowKey=>{
              const rowData = rows[rowKey]
              const colArray =new Array(treeGroup.definedColumsTotal).fill(null);
              let rowLevel = 0

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
                  const colum_show = elColumDefined['colum_show']
                  rowLevel = elColumDefined.level
                  // groupLevelRowCount

                  colData.colum_type = colum_type
                  colData.colum_show = colum_show
                  colArray[columOrder] = colData
                }

              })
              if(!groupLevelCount[unitDataKey][rowLevel]){
                groupLevelCount[unitDataKey][rowLevel]= 0
              }

              groupLevelCount[unitDataKey][rowLevel] = groupLevelCount[unitDataKey][rowLevel]+1

              elRows.push(colArray)
              // //console.log("groupId : "+groupId)
              // if(!this.groupLevelRowCount[groupId]){
              //   this.groupLevelRowCount[groupId] = {}
              // }
              // if(!this.groupLevelRowCount[groupId][rowLevel]){
              //   this.groupLevelRowCount[groupId][rowLevel] = 0
              // }
              // this.groupLevelRowCount[groupId][rowLevel] = this.groupLevelRowCount[groupId][rowLevel]+1
            })
          })
        })

        this.groupLevelRowCount = Object.assign(this.groupLevelRowCount ,groupLevelCount )

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
        console.log(this.groupLevelRowCount[groupKey+"G"])

        this.groupLevelRowCount[groupKey+"G"]["0"] = this.groupLevelRowCount[groupKey+"G"]["0"]+1

      },

      addSonNode(parentElRowData, parentRowNum,groupId){

        let parentRowLevel = 0
        let parentColumNum = 0
        let sonRowLevel = 0
        let insertRowNum = 0

        if(parentRowNum==this.definedColumsGroup[groupId].maxLevel){
          return
        }

        const parentBaseInfo = {}

        //检查当前增加子节点的节点信息
        parentElRowData.forEach((parentElColData,index)=>{
          if(parentElColData!=null){
            const unit_id = parentElColData.unit_id
            const dimensions_id = parentElColData.dimensions_id
            const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id+"-"+dimensions_id]
            parentRowLevel = elColumDefined.level
            sonRowLevel = parentRowLevel+1
            parentBaseInfo.report_group_id = parentElColData.report_group_id
            parentBaseInfo.report_id = parentElColData.report_id
            parentBaseInfo.unit_id = parentElColData.unit_id
            if(parentElColData.colum_show=='Y'){
              parentColumNum++
              //console.log(parentColumNum)

            }
          }
        })

        let insertRowData = null

        const definedColums =  this.definedColumsGroup[groupId].definedColums
        definedColums.forEach(definedColum=>{
          if(definedColum){
            if(sonRowLevel==definedColum.level){
              if(insertRowData==null){
                insertRowData =new Array(parentColumNum).fill(null);
              }

              insertRowData.push({
                'colum_id':null,
                'colum_show':definedColum.colum_show,
                'colum_type':definedColum.colum_type,
                'dimensions_id':definedColum.colum_id,
                'report_data':"",
                'report_group_id':groupId,
                'report_id':this.reportId,
                'unit_id':this.unitId,
              })
            }
          }
        })

        //直接在当前节点下加新节点
        this.definedColumsGroup[groupId].elRowDatas.splice(parentRowNum+1,0,insertRowData)

        this.groupLevelRowCount[groupId+"G"][sonRowLevel] = this.groupLevelRowCount[groupId+"G"][sonRowLevel]+1


        //根据当前节点查找已存在的子节点 复制子节点内容
        // for(let rowIndex = 0;rowIndex<this.definedColumsGroup[groupId].elRowDatas.length;rowIndex++){
        //   const checkRow = this.definedColumsGroup[groupId].elRowDatas[rowIndex]
        //   let checkRowLevel = 0
        //   for(let colIndex = 0;colIndex<checkRow.length;colIndex++) {
        //     const elColTMp = checkRow[colIndex]
        //     if (elColTMp != null) {
        //       const report_group_id = elColTMp.report_group_id
        //       const report_id = elColTMp.report_id
        //       const unit_id = elColTMp.unit_id
        //       const dimensions_id = elColTMp.dimensions_id
        //       const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
        //       checkRowLevel = elColumDefined.level
        //       if(report_group_id==parentBaseInfo.report_group_id&&
        //         report_id==parentBaseInfo.report_id&&
        //         unit_id==parentBaseInfo.unit_id&&
        //         checkRowLevel == sonRowLevel
        //       ){
        //         insertRowData = JSON.parse(JSON.stringify(checkRow))
        //       }
        //       break
        //     }
        //   }
        // }



        //循环找到第一个当前需要增加子节点的节点的行 在该行前面插入需要增加的子节点
        // for(let insertIndex = (parentRowNum+1);insertIndex<this.definedColumsGroup[groupId].elRowDatas.length;insertIndex++){
        //   const checkRow = this.definedColumsGroup[groupId].elRowDatas[insertIndex]
        //   ////console.log(checkRow)
        //
        //   let checkRowLevel = 0
        //   for(let colIndex = 0;colIndex<checkRow.length;colIndex++) {
        //     const elColTMp = checkRow[colIndex]
        //     if (elColTMp != null) {
        //       const unit_id = elColTMp.unit_id
        //       const dimensions_id = elColTMp.dimensions_id
        //       const elColumDefined = this.definedColumsGroup[groupId].elColumDefineds[unit_id + "-" + dimensions_id]
        //       checkRowLevel = elColumDefined.level
        //       break
        //     }
        //   }
        //
        //   if(checkRowLevel == parentRowLevel){//前插
        //     insertRowNum = insertIndex
        //     break;
        //   }else if(checkRowLevel < parentRowLevel){//前插
        //     insertRowNum = insertIndex
        //     break;
        //   }else if(checkRowLevel==this.definedColumsGroup[groupId].maxLevel){//最后插
        //     insertRowNum = insertIndex+1
        //     break;
        //   }
        // }
        // insertRowData.forEach(insertColData=>{
        //   if(insertColData!=null){
        //     insertColData.report_data = ""
        //     insertColData.colum_id = null
        //   }
        // })
        //
        //
        //
        // this.definedColumsGroup[groupId].elRowDatas.splice((insertRowNum),0,insertRowData)
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
            ////console.log("need")
          } else {
            endRowNum = cpIndex
            ////console.log("not need")

            break
          }
        }
        this.definedColumsGroup[groupId].elRowDatas.splice(endRowNum,0,cpElRowData)
        cpArrayTmp.forEach((cpTmp,i)=>{
          this.definedColumsGroup[groupId].elRowDatas.splice((endRowNum+i+1),0,cpTmp)
        })
      },
      checkOptionsShow(groupId){
        const elColumDefineds = this.definedColumsGroup[groupId].elColumDefineds
        // //console.log(groupId+" ---  "+this.definedColumsGroup[groupId])
        //console.log(groupId+"  "+Object.keys(elColumDefineds).length)
        if(Object.keys(elColumDefineds).length<2){
          return false
        }else
          return true
      },
      checkRowLevel(elRowDatas,groupId){
        const groupMaxLevel = this.definedColumsGroup[groupId].maxLevel
        let showOrNot = true
        if(elRowDatas){
          elRowDatas.forEach(elColData=>{
            if(elColData){
              const unitId = elColData.unit_id
              const dimensionsId = elColData.dimensions_id
              const elColumDefineds = this.definedColumsGroup[groupId].elColumDefineds
              const currLevel = elColumDefineds[unitId+'-'+dimensionsId].level
              //console.log(currLevel>=groupMaxLevel)
              if(currLevel>=groupMaxLevel){
                showOrNot = false
                return
              }
            }
          })
        }
        return showOrNot
      },

      checkShowDelete(elColDatas,groupId){
        const maxLevel = this.definedColumsGroup[groupId].maxLevel
        if(this.definedColumsGroup[groupId].elRowDatas.length<=(maxLevel+1)){
          return false
        }else{
          let checkGroupId = null

          const levelCount = {}

          let nowRowLevel = 0

          elColDatas.forEach(elColData=>{
            if(elColData){
              const unitId = elColData.unit_id
              const dimensions_id = elColData.dimensions_id
              nowRowLevel = this.definedColumsGroup[groupId].elColumDefineds[unitId+'-'+dimensions_id].level
            }
          })

          this.definedColumsGroup[groupId].elRowDatas.forEach(elRowData=>{
            let checkRowLevel = 1000
            if(elRowData){
              elRowData.forEach(elColData=>{
                if(elColData){
                  const unitId = elColData.unit_id
                  checkGroupId = elColData.report_group_id
                  const dimensions_id = elColData.dimensions_id
                  checkRowLevel = this.definedColumsGroup[groupId].elColumDefineds[unitId+'-'+dimensions_id].level
                  return
                }

              })
              if(!levelCount[checkRowLevel]){
                levelCount[checkRowLevel] = 0
              }
              levelCount[checkRowLevel]++
            }
          })


          if(levelCount[nowRowLevel]>1){
            return true
          }else{
            return false
          }
        }
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
        if(endRowNum==0){
          this.definedColumsGroup[groupId].elRowDatas.splice(delRowNum,delRowNum)
        }else{
          this.definedColumsGroup[groupId].elRowDatas.splice(delRowNum,(endRowNum-delRowNum))
        }

        this.groupLevelRowCount[groupId+"G"][delRowLevel] = this.groupLevelRowCount[groupId+"G"][delRowLevel] -1

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
      if(this.hasMounted){
        if(this.saveFlag!=null&&this.saveFlag!=undefined){
          if(this.saveFlag=='Y'||this.saveFlag=='S-Y'){
            this.$emit("refreshSaveLoading",this.unitId,"保存中....")
            this.doSaveUnitContext()
          }else if(this.saveFlag=='S'){

          }else if(this.saveFlag=='V'||this.saveFlag=='S-V'){
            this.doValidateUnitContext()
          }
        }
      }


    }
  }
</script>

<style scoped>
  .tree_title{
    padding:5px 5px 5px 5px;
    background: #8cc5ff;
    border:1px solid white;
    font-size: 12px;
  }

  .tree_colum{
    padding:5px 2px 5px 2px;
    font-size: 12px;
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
  .error_row{
    margin:0 0 15px 0;
  }
  .el-form-item{
    margin:0;
  }
</style>
