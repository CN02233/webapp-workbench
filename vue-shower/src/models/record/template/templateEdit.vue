<template>

  <WorkMain :headerItems="['报送管理','模板管理','模板编辑']">
    <el-row>
      <el-tabs  type="border-card" v-model="showSeet" @tab-click="handleClick">
        <el-tab-pane v-for="sheetObj in sheetList" :key="sheetObj" :label="sheetObj" :name="sheetObj">
          <div style="width: 100%;text-align: left;">
            <el-button @click="backList" size="mini" type="warning">返回上一级</el-button>
            <el-button @click="copyGroupEdit" size="mini" type="primary">设定可复制行组</el-button>
            <el-button @click="saveTemplate" size="mini" type="primary">保存</el-button>
            <el-tag
              v-for="cancopyRow in cancopyGroup[showSeet]"
              :key="cancopyRow.name"
              closable>
              {{cancopyRow.name}}
            </el-tag>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-row>

    <el-row>
        <hot-table :root="root" ref="reportEditRef"  :settings="handsontableSetting" ></hot-table>
    </el-row>


    <!-- 新增、编辑 弹窗-->
    <el-dialog title="设定组" :visible.sync="showModalPage" >
      <el-form class="modal-form" label-position="left" label-width="20%">
        <el-form-item size="small" label="组名称" >
          <el-input :disabled="false"  v-model="copyGroupTmp.name" auto-complete="off" ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="confirmGroup()">确 定</el-button>
      </div>
    </el-dialog>


  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { HotTable } from '@handsontable/vue';
  import 'handsontable/dist/handsontable.full.min.css'
  import 'handsontable/languages/zh-CN'

  export default {
    name: "templateEdit",
    components: {
      WorkTablePager,
      WorkMain,
      HotTable
    },
    data() {
      return {
        root: 'template-edit',
        title:'编辑报表',
        isCreate:true,
        templateId:'',
        showModalPage:false,
        showSeet:"",
        sheetList:[],
        sheetDatas:{},
        cancopyGroup:{},
        copyGroupTmp:{name:"",rows:[]},
        handsontableSetting: {//handsontable设置
          data: [],
          mergeCells:[],
          colHeaders: true,
          // colHeaders: ['日期', '地点', '商品', '单价', '销量'],
          rowHeaders: true,
          contextMenu:true,//开启右键菜单
          language: 'zh-CN',
          stretchH: 'all',
          // height:100,
          // width: '100%',
          // autoWrapRow: true,
          // autoRowSize: {syncLimit: '40%', allowSampleDuplicates: true},
          // height: '100%',
          maxRows: 1000,
          copyPaste: true,
          // Enables the plugin with custom values
          copyPaste: {
            columnsLimit: 25,
            rowsLimit: 50,
            pasteMode: 'shift_down',
          },
          afterLoadData:this.mergeTableCells,
          colWidths: 150,
          manualRowResize: true,
          manualColumnResize: true,
          outsideClickDeselects:false,
          rowHeaders: true
        },
        hotTable:{}
      }
    },
    methods: {
      getReportContext() {
        const $this = this
        const loading = $this.$loading({
          lock: true,
          text: '加载报表信息中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:"/template/getTemplateContext",
          params:{
            templateId:$this.templateId
          }
        }).then(response=>{
          $this.showReportContext($this,response)
          setTimeout(() => {
            loading.close();
          }, 1000);

        });

      },
      showReportContext($this,template){
        if(template!=null){
          const templateSheets = template.excelTemplateSheets

          if(templateSheets!=null&&templateSheets.length>0){
            templateSheets.forEach((templateSheet,i)=>{
              const sheet_num = templateSheet.sheet_num
              const sheet_id = templateSheet.id
              let sheet_name = templateSheet.sheet_name
              const excelCopyGroup = templateSheet.excelCopyGroup
              if(excelCopyGroup){
                excelCopyGroup.forEach(copyGroup=>{
                  if(!this.cancopyGroup[sheet_name]){
                    this.cancopyGroup[sheet_name] = new Array()
                  }
                  this.cancopyGroup[sheet_name].push({name:copyGroup.group_name,rows:copyGroup.group_list})

                })
              }

              if(sheet_name==null||sheet_name===''){
                sheet_name = "sheet-"+sheet_num
              }
              const excelDataObj = this.dataFormat(templateSheet)
              excelDataObj.sheetId = sheet_id
              if(i==0){
                console.log(excelDataObj)
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
        const excelTemplateCells = excelReportSheet.excelTemplateCells
        if(excelTemplateCells!=null&&excelTemplateCells.length>0){
          const excelData = new Array(excelReportSheet.row_num)
          excelTemplateCells.forEach(excelTemplateCell=>{
            const row = excelTemplateCell.template_row
            const column = excelTemplateCell.template_col
            const context = excelTemplateCell.template_context
            if(excelData[row]==null)
              excelData[row] = new Array(excelReportSheet.colum_num)
            excelData[row][column] = context
          })
          excelDataObj.datas = excelData
          excelDataObj.mergeCells = excelReportSheet.excelTemplateCellMergeds
        }
        return excelDataObj
      },
      handleClick(tab, event){
        console.log(this.sheetDatas[tab.name].datas.length)
        this.hotTable.loadData(this.sheetDatas[tab.name].datas)
      },
      mergeTableCells(){
        if(this.sheetDatas[this.showSeet]){
          this.handsontableSetting.mergeCells = this.sheetDatas[this.showSeet].mergeCells
          console.log("here is running....."+JSON.stringify(this.sheetDatas[this.showSeet].mergeCells))

        }
      },
      backList(){
        this.$router.push({
          path: "/record/template"
        });
      },
      copyGroupEdit(){
        const allSelected = this.hotTable.getSelected()
        if(allSelected&&allSelected.length>0){
          this.$confirm('设置以下行为可编辑组？<p><span>设定该组后可在报表中多次插入该组包含的行数据，未设定组的行无法进行复制、删除、合并单元格等操作</span>', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString:true,
            type: 'warning'
          }).then(() => {
            this.showModalPage = true
            this.copyGroupTmp.rows = []
            allSelected.forEach(selectedRow=>{
              const rowNum = selectedRow[0]
              this.copyGroupTmp.rows.push(rowNum)
            })

          }).catch(() => {
          });
        }else{
          this.$alert('当前未选择任何行，请在表格中选择行后点击该按钮', '提示', {});
        }
      },
      confirmGroup(){
        if(!this.cancopyGroup[this.showSeet]){
          this.cancopyGroup[this.showSeet] = new Array()
        }
        this.cancopyGroup[this.showSeet].push({name:this.copyGroupTmp.name,rows:this.copyGroupTmp.rows})
        this.copyGroupTmp.name = ""
        this.copyGroupTmp.rows = []
        this.showModalPage = false
      },
      closeModal(){
        this.showModalPage = false
      },
      saveTemplate(){
        //editSaveTemplate

        const allSheetNames = Object.keys(this.sheetDatas)
        const $this = this
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
            const sheetId = sheetContext.sheetId
            const sendData = {
              templateId:$this.templateId,
              sheetId:sheetId,
              templateContext:sheetData,
              copyGroups:$this.cancopyGroup[allSheetNames[saveSheetCount]]
            }
            console.log(sendData)
            $this.BaseRequest({
              url:"/template/editSaveTemplate",
              method:'post',
              data:sendData
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
      }
    },
    mounted() {
      this.templateId = this.$route.query.templateId
      this.getReportContext()
      console.log(this.$refs)
      this.hotTable =  this.$refs.reportEditRef.hotInstance;
    }

  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
