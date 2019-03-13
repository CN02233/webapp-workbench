<template>

  <WorkMain :headerItems="['报送管理','模板管理','模板查看']">
    <el-row>
      <el-tabs  type="border-card" v-model="showSeet" @tab-click="handleClick">
        <el-tab-pane v-for="sheetObj in sheetList"  :label="sheetObj" :name="sheetObj">
        </el-tab-pane>
      </el-tabs>
    </el-row>

    <el-row>
        <hot-table :root="root" ref="reportEditRef"  :settings="handsontableSetting" ></hot-table>
    </el-row>


  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { HotTable } from '@handsontable/vue';
  import 'handsontable/dist/handsontable.full.min.css'
  import 'handsontable/languages/zh-CN'

  export default {
    name: "templateView",
    components: {
      WorkTablePager,
      WorkMain,
      HotTable
    },
    data() {
      return {
        root: 'template-view',
        title:'创建报表',
        isCreate:true,
        templateId:'',
        showModalPage:false,
        showSeet:"",
        sheetList:[],
        sheetDatas:{},
        handsontableSetting: {//handsontable设置
          data: [[]],
          mergeCells:[],
          colHeaders: true,
          // colHeaders: ['日期', '地点', '商品', '单价', '销量'],
          rowHeaders: true,
          contextMenu:false,//开启右键菜单
          language: 'zh-CN',
          stretchH: 'all',
          width: '100%',
          autoWrapRow: true,
          height: 487,
          maxRows: 1000,
          colWidths: 150,
          manualRowResize: true,
          manualColumnResize: true,
          rowHeaders: true,
          afterMergeCells:this.mergeCells
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
          $this.showReportContext(response)
          setTimeout(() => {
            loading.close();
          }, 1000);

        });

      },
      showReportContext(template){
        const $this = this
        if(template!=null){
          const templateSheets = template.excelTemplateSheets

          if(templateSheets!=null&&templateSheets.length>0){
            templateSheets.forEach((templateSheet,i)=>{
              const sheet_num = templateSheet.sheet_num
              let sheet_name = templateSheet.sheet_name
              if(sheet_name==null||sheet_name===''){
                sheet_name = "sheet-"+sheet_num
              }
              if(i==0){
                $this.showSeet = sheet_name
                $this.putDataToExcel(templateSheet)
              }
              $this.sheetDatas[sheet_name] = templateSheet
              $this.sheetList.push(sheet_name)
            })
          }
        }

      },
      putDataToExcel(excelTemplateSheet){
        // this.hotTable.clear()
        const excelTemplateCells = excelTemplateSheet.excelTemplateCells
        if(excelTemplateCells!=null&&excelTemplateCells.length>0){
          const excelData = new Array(excelTemplateSheet.row_num)
          excelTemplateCells.forEach(excelTemplateCell=>{
            const row = excelTemplateCell.template_row
            const column = excelTemplateCell.template_col
            const context = excelTemplateCell.template_context
            if(excelData[row]==null)
              excelData[row] = new Array(excelTemplateSheet.colum_num)
            excelData[row][column] = context
          })
          this.hotTable.loadData(excelData)
          this.handsontableSetting.mergeCells = excelTemplateSheet.excelTemplateCellMergeds
        }

      },
      handleClick(tab, event){
        this.putDataToExcel(this.sheetDatas[tab.name])
      },
      mergeCells(cellRange,mergeParent,auto){

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
