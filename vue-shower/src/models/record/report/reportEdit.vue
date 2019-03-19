<template>

  <WorkMain class="WorkMain" :headerItems="['报送管理','报表管理',title]">

    <el-tabs  type="border-card" v-model="showSeet" @tab-click="handleClick">
      <el-tab-pane v-for="sheetObj in sheetList" :key="sheetObj" :label="sheetObj" :name="sheetObj">
        <div style="width: 100%;text-align: left;">
          <el-button @click="backList" size="mini" type="warning">返回上一级</el-button>
          <el-button @click="confirmSelected" size="mini" type="primary">下载模板文件</el-button>
          <el-button @click="editSave" size="mini" type="primary">保存</el-button>
          <el-button @click="submitReport" size="mini" type="primary">提交审批</el-button>

          <el-button @click="cpGroup(allowCopyGroup.group_list)" size="mini" :key="allowCopyGroup.group_name" v-for="allowCopyGroup in allowCopys[showSeet]">
            新增{{allowCopyGroup.group_name}}
          </el-button>
          <el-button @click="delGroup(allowCopyGroup.group_list)" size="mini" :key="allowCopyGroup.group_name" v-for="allowCopyGroup in allowCopys[showSeet]">
            删除{{allowCopyGroup.group_name}}
          </el-button>
          <span style="font-size:12px;color:red;margin:0 0 0 30px;"> 提示：保存后自动刷新公式值</span>
        </div>
      </el-tab-pane>
    </el-tabs>
    <hot-table :root="root" ref="reportEditRef"  :settings="handsontableSetting" ></hot-table>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { HotTable } from '@handsontable/vue';
  import 'handsontable/dist/handsontable.full.min.css'
  import 'handsontable/languages/zh-CN'

  export default {
    name: "report-create.vue",
    components: {
      WorkTablePager,
      WorkMain,
      HotTable
    },
    data() {
      return {
        root: 'report-edit',
        title:'创建报表',
        isCreate:true,
        templateId:'',
        reportId:'',
        reportName:'',
        reportRows:[],
        showSeet:"",
        sheetList:[],
        sheetDatas:{},
        showModalPage:false,
        allowCopys:{},
        baseDataList:[],
        editCellPoint:[],
        baseDataSelected:{
          dataName:'',
          dataVal:'',
          dataType:''
        },
        columNum :0,
        tableFormatChanged:false,
        handsontableSetting: {//handsontable设置
          data: [],
          mergeCells:[],
          colHeaders: true,
          // colHeaders: ['日期', '地点', '商品', '单价', '销量'],
          rowHeaders: true,
          contextMenu:true,//开启右键菜单
          language: 'zh-CN',
          // stretchH: 'all',
          // height:100,
          // width: '100%',
          // autoWrapRow: true,
          // autoRowSize: {syncLimit: '40%', allowSampleDuplicates: true},
          // height: '100%',
          maxRows: 1000,
          // copyPaste: true,
          // // Enables the plugin with custom values
          // copyPaste: {
          //   columnsLimit: 25,
          //   rowsLimit: 50,
          //   pasteMode: 'shift_down',
          // },
          afterLoadData:this.mergeTableCells,
          colWidths: 150,
          // manualRowResize: true,
          // manualColumnResize: true,
          // rowHeaders: true,
          allowInsertRow:true
          // autoInsertRow:true
        },
        hotTable:{}
      }
    },
    methods: {
      getReportContext() {
        const $this = this
        if(this.isCreate){//获取模板信息
          const loading = this.$loading({
            lock: true,
            text: '正在根据所选模板创建报表.......',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          this.BaseRequest({
            url:"/report/createReport",
            params:{
              templateIdOrName:this.templateId,
              reportName:this.reportName
            }
          }).then(response=>{
            setTimeout(() => {
              loading.close();
            }, 1000);

            this.reportId = response
            requestReportContext(this.reportId)
          });
        }else{//获取报表信息
          requestReportContext(this.reportId)
        }
        function requestReportContext(){
          const loading = $this.$loading({
            lock: true,
            text: '加载报表信息中.......',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          $this.BaseRequest({
            url:"/report/loadReportData",
            params:{
              reportIdOrName:$this.reportId
            }
          }).then(response=>{
            $this.showReportContext($this,response)
            setTimeout(() => {
              loading.close();
            }, 1000);

          });
        }

      },
      showReportContext($this,template){
        if(template!=null){
          const templateSheets = template.excelReportSheets

          if(templateSheets!=null&&templateSheets.length>0){
            templateSheets.forEach((templateSheet,i)=>{
              const sheet_num = templateSheet.sheet_num
              const sheet_id = templateSheet.id
              const template_id = templateSheet.template_id
              let sheet_name = templateSheet.sheet_name
              const excelCopyGroup = templateSheet.excelCopyGroup
              this.allowCopys[sheet_name] = excelCopyGroup

              if(sheet_name==null||sheet_name===''){
                sheet_name = "sheet-"+sheet_num
              }
              const excelDataObj = this.dataFormat(templateSheet)
              excelDataObj.sheetId = sheet_id
              excelDataObj.templateId = template_id
              if(i==0){
                $this.showSeet = sheet_name
                $this.hotTable.loadData(excelDataObj.datas)
                $this.handsontableSetting.mergeCells = excelDataObj.mergeCells
              }
              $this.sheetDatas[sheet_name] = excelDataObj
              $this.sheetList.push(sheet_name)
            })
          }
        }
      },
      dataFormat(excelReportSheet){
        const excelDataObj = {datas:[],mergeCells:[]}
        const excelTemplateCells = excelReportSheet.excelReportCells
        if(excelTemplateCells!=null&&excelTemplateCells.length>0){
          const excelData = new Array(excelReportSheet.row_num)
          excelTemplateCells.forEach(excelTemplateCell=>{
            const row = excelTemplateCell.report_row
            const column = excelTemplateCell.report_colum
            const context = excelTemplateCell.report_context
            if(excelData[row]==null)
              excelData[row] = new Array(excelReportSheet.colum_num)
            excelData[row][column] = context
          })
          excelDataObj.datas = excelData
          excelDataObj.mergeCells = excelReportSheet.excelTemplateCellMergeds

        }
        return excelDataObj
      },
      mergeTableCells(){
        console.log("here is running....."+this.showSeet)
        if(this.sheetDatas[this.showSeet]){
          this.handsontableSetting.mergeCells = this.sheetDatas[this.showSeet].mergeCells
        }
      },
      beforeHandClick(){
        // if(!this.hotTable.getData){//页面初始化的时候会运行一次这个方法
        //   return
        // }
        const changedDatas = this.hotTable.getData()
        this.sheetDatas[this.showSeet].datas = changedDatas
      },
      handleClick(tab, event){
        // const changedDatas = this.hotTable.getData()
        this.hotTable.loadData(this.sheetDatas[tab.name].datas)
      },
      selectBaseData(rowIndex,columnIndex){
        this.showModalPage = true
        this.editCellPoint=[rowIndex,columnIndex]
      },
      backList(){
        this.$router.push({
          path: "/record/report"
        });
      },
      confirmSelected(data){
        this.baseDataSelected = data
        this.reportRows[this.editCellPoint[0]][this.editCellPoint[1]].val = data.dataVal
      },
      closeModal:function(){
        this.showModalPage=false
      },
      editSave(){
        const allSheetNames = Object.keys(this.sheetDatas)
        const $this = this

        console.log(this.hotTable.getPlugin('mergeCells').mergedCellsCollection.mergedCells)
        if(this.tableFormatChanged){
          this.fullSave($this,allSheetNames)
        }else{

        }
      },

      fullSave($this,allSheetNames){//表格格式改变，整体重新保存
        if(allSheetNames!=null&&allSheetNames.length>0){
          const sheetCount = allSheetNames.length
          doSave(0,sheetCount,this.sheetDatas)
        }

        function doSave(saveSheetCount,sheetCount,sheetDatas){
          if(saveSheetCount>=sheetCount){
            return
          }else{
            const loading = $this.$loading({
              lock: true,
              text: '保存【'+allSheetNames[saveSheetCount]+'】中',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            const sheetContext = sheetDatas[allSheetNames[saveSheetCount]]
            const sheetData = sheetContext.datas
            const templateId = sheetContext.templateId
            const sheetId = sheetContext.sheetId
            $this.BaseRequest({
              url:"/report/fullEditSave",
              method:'post',
              data:{
                reportId:$this.reportId,
                templateId:templateId,
                sheetId:sheetId,
                reportCells:sheetData,
                reportMerged:$this.hotTable.getPlugin('mergeCells').mergedCellsCollection.mergedCells
              }
            }).then(response=>{
              loading.close()
              $this.Message.success("保存【"+allSheetNames[saveSheetCount]+"】成功")
              saveSheetCount++
              doSave(saveSheetCount,sheetCount,sheetDatas)

            }).catch(error=>{
              loading.close()
              $this.Message.success("保存【"+allSheetNames[saveSheetCount]+"】失败:"+error)
            });
          }
        }
      },
      changeSave(){//表格格式不变，只改变数据内容,部分更新

      },
      submitReport(){
        const loading = this.$loading({
          lock: true,
          text: '提交审批中.....',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/report/submitReport",
          method:'get',
          params:{
            reportId:this.reportId
          }
        }).then(response=>{
          loading.close()
          this.Message.success('提交成功....')
          this.backList()

        }).catch(error=>{
          loading.close()
          this.Message.success("提交失败:"+error)
        });
      },
      cpGroup(groupRows){
        if(groupRows){
          const insertStart = parseInt(groupRows[groupRows.length-1])+1
          const insertOffset = groupRows.length
          console.log(insertStart+"---"+insertOffset)
          console.log(this.hotTable)
          this.hotTable.alter('insert_row', 3,10)
          // this.tableFormatChanged = true
          // groupRows.forEach((groupRow,inserRow)=>{
          //   const rowINdex = parseInt(groupRow)
          //   const rowData = this.hotTable.getDataAtRow(rowINdex)
          //   if(rowData){
          //     rowData.forEach((cellData,index)=>{
          //       this.hotTable.setDataAtCell(insertStart+inserRow, index, cellData)
          //       // console.log(this.hotTable.getCellMeta(insertStart+inserRow, index))
          //       // this.hotTable.populateFromArray(rowINdex,0,cellData)
          //     })
          //
          //   }
          // })
        }

      },
      delGroup(){

      }

    },
    mounted() {
      function getUrlKey(name) {
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null
      }
      this.templateId = this.$route.query.template
      this.reportName = this.$route.query.reportName
      this.isCreate = this.$route.query.isCreate
      this.reportId = this.$route.query.reportId

      if(!this.isCreate||this.isCreate==='false'){//F5刷新页面需要从url中获取参数值
        this.reportId = getUrlKey('reportId')
        this.isCreate = false
      }

      if(!this.isCreate)
        this.title = '修改报表'
      this.getReportContext()
      this.hotTable =  this.$refs.reportEditRef.hotInstance;
    }

  }
</script>

<style lang="css">
  .colorized{
    background: black;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  .WorkMain{
    width:100%;
    height:100%;
  }

</style>
