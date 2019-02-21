<template>
  <WorkMain :headerItems="['NLP基础','规则分类']" class="table-page-root">
        <el-row class="search-row" :gutter="20">
          <el-col class="align-left" :span="17">
            <el-button @click="addTxtRul" type="primary">新增文本规则分类分析</el-button>
            <el-button @click="getTableData()" type="primary">刷新</el-button>
          </el-col>
        </el-row>

        <el-row class="table-row">
          <el-col :span="24">
            <el-table
              :data="roleGroupList"
              style="width: 100%">
              <el-table-column
                prop="task"
                align="left"
                label="任务名称">
              </el-table-column>
              <el-table-column
                prop="start"
                align="left"
                label="开始时间">
              </el-table-column>
              <el-table-column
                prop="end"
                align="left"
                label="结束时间">
                </el-table-column>
              <el-table-column
                prop="status"
                align="left"
                label="执行状态">
              </el-table-column>
              <el-table-column
                label="分析结果"
                align="left">
                <template slot-scope="scope">
                  <el-button type="text" @click="viewResult(scope.row)" size="small">查看结果</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>

        <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
        <WorkTablePager @refreshData="getTableData"
                        :pageCount="totalPage">
        </WorkTablePager>

        <!-- 新增、编辑 弹窗-->
     <el-dialog :title="modalPageTitle" :visible.sync="showModalPage" >
          <el-form class="modal-form" :label-position="left" label-width="20%" :model="ruleData">
            <el-form-item label="任务名称" >
              <el-input v-model="ruleData.task" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="数据源" >
             <el-select class="select-ty"  v-model="ruleData.datasource" placeholder="请选择数据源">
              <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
             </el-form-item>
            <el-form-item label="库名" >
             <el-input v-model="ruleData.db" auto-complete="off"></el-input>
            </el-form-item>
             <el-form-item label="表名" >
             <el-input v-model="ruleData.table" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="文档字段名" >
             <el-input v-model="ruleData.filed" auto-complete="off"></el-input>
            </el-form-item>
             <el-form-item label="标题段名" >
              <el-input v-model="ruleData.index" auto-complete="off"></el-input>
             </el-form-item>
              <el-form-item label="分类规则组" >
              <el-select class="select-ty"  v-model="ruleData.rule" placeholder="请选择分类规则组">
                <el-option v-for="item in ruleList" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
             </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="closeModal">取 消</el-button>
            <el-button type="primary" @click="subWordForm();closeModal()">确 定</el-button>
          </div>
        </el-dialog>

       <!-- 查看结果的提示-->
       <el-dialog :title="viewTitle" :visible.sync="refuseView" >
             <span>任务正在后台运行，请关注结果页面查看分析结果！</span>
                 <div slot="footer" class="dialog-footer">
                   <el-button @click="closeModal">取 消</el-button>
                 </div>
               </el-dialog>
      </WorkMain>
</template>

<script>
 import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
 import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

 export default {
        name: 'RoleGroup',
        data() {
            return {
            dataSourceList:[], ruleList:[],roleGroupList:[],
            tableDataUrl:'classify/rule',
            currPageNum:1,
              totalPage:1,
            showModalPage:false,
            refuseView:false,
            isEditModal:false,
            editFormData:{
               task:'任务名称',
               datasource:'',
               db:'库名',
               table:'表名',
              filed:'文档字段名称',
              index:'标题名',
              rule:'',
              },
            addFormData:{
              task:'',
              datasource:'',
              db:'',
              table:'',
              filed:'',
              index:'',
              rule:'',
            }
            }
        },
        computed:{
        ruleData:function(){
            if(this.isEditModal){
              return this.editFormData
            }else{
              return this.addFormData
            }
          },
        modalPageTitle(){
          if(this.isEditModal){
            return "编辑文本规则分类分析"
          }else
            return "新建文本规则分类分析"
        },
        viewTitle(){
           return "文本规则分类分析"
        }
        },
        components: {
          WorkTablePager,
          WorkMain
          },
        methods: {
         getTableData:function(pageNum){
                if(pageNum&&pageNum!=''){
                  this.currPageNum = pageNum;
                }else{
                  pageNum = this.currPageNum;
                }
                const $this = this
                this.Request({
                  url:this.tableDataUrl,
                  params:{page:pageNum,pagesize:10}
                }).then(reponse=>{
                  $this.refreshTableList(reponse.result)
                  $this.totalPage = reponse.total
              })
              },
              refreshTableList:function(dataList){
                this.roleGroupList = dataList
              },
              viewResult:function(rowData){
                    if(rowData.status=="完成"){
                  this.$router.push({name:'roleGroupResult',query:{taskId:rowData.id}})
                    }else{
                      this.refuseView = true
                    }
              },
              addTxtRul:function(){
               this.showModalPage = true
               this.isEditModal = false
             },
            closeModal:function(){
               this.showModalPage=false
               this.isEditModal=false
               this.refuseView=false
             },
             subWordForm:function(){
                let sendData = this.addFormData
                let sendUrl = "classify/rule"
                let sendType = "post"//新增
                if(this.isEditModal){
                  sendData = this.editFormData
                  sendType = 'put'
                }
                const $this = this
                this.Request({
                  url:sendUrl,
                  method:sendType,
                  data:sendData
                }).then(response=>{
                  if(response.status!='0'){
                   this.Message.error(response.message)
                 }else{
                   this.Message.success("保存成功")
                   $this.getTableData()
                 }
             })
             },
            getDataSourceList(){
              this.Request({
                url:'/datasource/list',
                method:"get"
              }).then(response=>{
                if(response.status!='0'){
                  this.Message.error(response.message)
                }else{
                  const dataSourceResponse = response.result
                  dataSourceResponse.forEach(dataSourceData=>{
                      this.dataSourceList.push({"value":dataSourceData.id,"label":dataSourceData.db})
                  })
                }


              })
            },
            getRuleList(){
              this.Request({
                url:'/rule/list',
                method:"get"
              }).then(response=>{
                if(response.status!='0'){
                  this.Message.error(response.message)
                }else{
                  const ruleListResponse = response.result
                  ruleListResponse.forEach(ruleData=>{
                      this.ruleList.push({"value":ruleData.id,"label":ruleData.group})
                })
              }
              })
            }
        },
         mounted:function(){
              this.getDataSourceList()
              this.getRuleList()
              this.getTableData(1)
    }
    }
</script>

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

.select-ty{
    width:100%;
  }

  .table-row{
    width:99%;
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
