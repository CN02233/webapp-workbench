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

    <el-row class="table-row">
      <table  class="report-table">
        <tr v-for="(reportRow,reportRowIndex) in reportRows">
          <td border="1" class="excel-colum" v-bind:rowspan="reportColum.rowspan" v-bind:colspan="reportColum.colspan" v-for="(reportColum,reportColumIndex) in reportRow">
            <!--<div  v-if="reportColum.input">-->
            <div >
              <el-tooltip effect="dark" content="点击输入栏后方小图标可选择基础数据" placement="top-start">
                <el-input style="width:90%" class="report-input" size="mini" v-model="reportRows[reportRowIndex][reportColumIndex].val" >
                  <template slot="suffix">
                    <i class="el-icon-edit el-input__icon my-edit-icon"
                    slot="suffix"
                    @click="selectBaseData(reportRowIndex,reportColumIndex)">
                    </i>
                  </template>
                </el-input>
              </el-tooltip>
            </div>
            <!--<div v-else>{{reportColum.val}}</div>-->
          </td>
        </tr>
      </table>
    </el-row>

    <el-dialog title="基础数据选择" :visible.sync="showModalPage" >

      <el-row>
        <el-col :span="10">
          <el-tree :data="baseDataList" @node-click="confirmSelected" ></el-tree>
        </el-col>
        <el-col :span="10">
          <el-row>
            <el-col :span="10">数据名称</el-col>
            <el-col :span="10">{{baseDataSelected.dataName}}</el-col>
          </el-row>
          <el-row>
            <el-col :span="10">数据类型</el-col>
            <el-col :span="10">{{baseDataSelected.dataType}}</el-col>
          </el-row>
          <el-row>
            <el-col :span="10">数据值</el-col>
            <el-col :span="10">{{baseDataSelected.dataVal}}</el-col>
          </el-row>
        </el-col>
      </el-row>


      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">完 成</el-button>
        <!--<el-button type="primary" @click="confirmSelected()">确 定</el-button>-->
      </div>
    </el-dialog>

  </WorkMain>



</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'

  export default {
    name: "report-create.vue",
    components: {
      WorkTablePager,
      WorkMain
    },
    data() {
      return {
        title:'创建报表',
        isCreate:true,
        templateId:'',
        reportId:'',
        reportName:'',
        reportContext:[],
        reportRows:[],
        showModalPage:false,
        baseDataList:[],
        editCellPoint:[],
        baseDataSelected:{
          dataName:'',
          dataVal:'',
          dataType:''
        }
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
            url:"/report/loadReport",
            params:{
              reportIdOrName:$this.reportId
            }
          }).then(response=>{
            $this.reportContext = response
            $this.showReportContext()
            setTimeout(() => {
              loading.close();
            }, 1000);

          });
        }

      },
      showReportContext(){
        const $this = this
        if(this.reportContext!=null){
          this.reportContext.forEach(reportSheet=>{
            if(reportSheet!=null){
              $this.reportRows = reportSheet
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
        const loading = this.$loading({
          lock: true,
          text: '报表信息保存中.......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        const $this = this
        this.BaseRequest({
          url:"/report/editSave",
          method:'POST',
          data:{
            reportCells:this.reportRows,
            reportCellsJson:JSON.stringify(this.reportRows),
            reportId:this.reportId
          }
        }).then(response=>{
          $this.reportContext = response
          $this.showReportContext()
          setTimeout(() => {
            loading.close();
          }, 1000);

        });
      }
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

    }
  }
</script>

<style>
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";

  .el-row{
    margin-top:20px;
  }

  $seachRowHeight : 50px;
  $pagerRowHeight : 50px;
  $tableRowHeight : calc(100% - #{$seachRowHeight+$pagerRowHeight+10});
  .search-row{
    height:$seachRowHeight;
  }

  .table-row{
    width:99%;
    height:$tableRowHeight;
    overflow: auto;
  }

  .report-table{
    border-collapse: collapse;
    font-size:12px;
  }

  .report-table tr {
    height:20px;
  }
  .report-table td, .report-table th {
    border: 1px solid black;
    min-width:100px;
  }
  .report-table tr:first-child th {
    border-top: 0;
  }
  .report-table tr:last-child td {
    border-bottom: 0;
  }
  .report-table tr td:first-child,
  .report-table tr th:first-child {
    border-left: 1;
  }
  .report-table tr td:last-child,
  .report-table tr th:last-child {
    border-right: 1;
  }

  .my-autocomplete{
    padding:0 5px 0 5px;
    height:10px !important;
  }

  .my-edit-icon{
    cursor: pointer;
  }


</style>
