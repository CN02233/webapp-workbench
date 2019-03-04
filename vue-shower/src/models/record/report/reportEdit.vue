<template>

  <WorkMain :headerItems="['报送管理','报表管理',title]">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="confirmSelected" type="primary">查看所用模板</el-button>
        <el-button @click="confirmSelected" type="primary">刷新公式值</el-button>
        <el-button @click="editSave" type="primary">保存</el-button>
        <el-button @click="confirmSelected" type="primary">提交审批</el-button>
      </el-col>
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
        showModalPage:false,
        baseDataList:[],
        editCellPoint:[],
        baseDataSelected:{
          dataName:'',
          dataVal:'',
          dataType:''
        },
        columNum :0,
        handsontableSetting: {//handsontable设置
          data: [],
          mergeCells:[],
          colHeaders: true,
          // colHeaders: ['日期', '地点', '商品', '单价', '销量'],
          rowHeaders: true,
          contextMenu:true,//开启右键菜单
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
            $this.showReportContext(response)
            setTimeout(() => {
              loading.close();
            }, 1000);

          });
        }

      },
      showReportContext(reportSheets){
        const $this = this
        if(reportSheets!=null){

          reportSheets.forEach(reportSheet=>{
            if(reportSheet!=null){
              console.log(JSON.stringify(reportSheet.sheetData))
              $this.handsontableSetting.data = reportSheet.sheetData
              $this.handsontableSetting.mergeCells = reportSheet.mergedList
            }
          })
        }
      },
      selectBaseData(rowIndex,columnIndex){
        this.showModalPage = true
        this.editCellPoint=[rowIndex,columnIndex]
      },
      confirmSelected(data){
        this.baseDataSelected = data
        this.reportRows[this.editCellPoint[0]][this.editCellPoint[1]].val = data.dataVal
      },
      closeModal:function(){
        this.showModalPage=false
      },
      getBaseData(){
        const loading = this.$loading({
          lock: true,
          text: '获取基础数据中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:"/datas/listAllBaseDatas"
        }).then(response=>{

          function treeMenu(a) {
            this.tree = a || [];
            this.groups = {};
          };
          treeMenu.prototype = {
            init: function(pid) {
              this.group();
              return this.getDom(this.groups[pid]);
            },
            group: function() {
              for(var i = 0; i < this.tree.length; i++) {
                if(this.groups[this.tree[i].parentId]) {
                  this.groups[this.tree[i].parentId].push(this.tree[i]);
                } else {
                  this.groups[this.tree[i].parentId] = [];
                  this.groups[this.tree[i].parentId].push(this.tree[i]);
                }
              }
            },
            getDom: function(a) {
              if(!a) {
                return ''
              }
              for(var i = 0; i < a.length; i++) {
                a[i].label = a[i].dataName;
                a[i].children = this.getDom(this.groups[a[i].dataId])
              };
              return a;
            }
          };

          this.baseDataList = new treeMenu(response).init("0");
          setTimeout(() => {
            loading.close();
          }, 1000);
        });
      },
      editSave(){
        // console.log(this.$refs.reportEditRef.hotInstance.getCellsMeta())
        const mergedCells = this.hotTable.getPlugin('mergeCells').mergedCellsCollection.mergedCells;//获取合并单元格信息
        console.log(mergedCells)
        const cellDatas = this.hotTable.getData()//获取所有单元格数据
        console.log(cellDatas)

      },
      mergeCells(cellRange,mergeParent,auto){
        console.log(this.hotTable.getSettings())
        console.log(mergeParent)
        console.log(auto)

      }
      // editSave(){
      //   const loading = this.$loading({
      //     lock: true,
      //     text: '报表信息保存中.......',
      //     spinner: 'el-icon-loading',
      //     background: 'rgba(0, 0, 0, 0.7)'
      //   });
      //
      //   const $this = this
      //   const sendReportCells = []
      //   this.reportRows.forEach(reportRow=>{
      //     reportRow.forEach(reportCell=>{
      //       sendReportCells.push(reportCell)
      //     })
      //   })
      //   this.BaseRequest({
      //     url:"/report/editSave",
      //     method:'POST',
      //     data:{
      //       reportId:this.reportId,
      //       reportCells:sendReportCells
      //     }
      //   }).then(response=>{
      //     $this.showReportContext(response)
      //     setTimeout(() => {
      //       loading.close();
      //     }, 1000);
      //
      //   });
      // }
    },
    mounted() {
      this.templateId = this.$route.query.template
      this.reportName = this.$route.query.reportName
      this.isCreate = this.$route.query.isCreate
      this.reportId = this.$route.query.reportId
      if(!this.isCreate)
        this.title = '修改报表'
      this.getReportContext()
      this.getBaseData()
      this.hotTable =  this.$refs.reportEditRef.hotInstance;
    }

  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
