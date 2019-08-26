<template>
  <WorkMain :headerItems="['NLP基础','规则分类']" class="table-page-root">
           <el-row class="table-row" style="margin-top:50px;">
             <el-col :span="24">
               <el-table
                 :data="dataSourceList"
                 >
                 <el-table-column
                   prop="index"
                   align="left"
                   label="标题">
                 </el-table-column>
                 <el-table-column
                   prop="type"
                   align="left"
                   label="分类">
                 </el-table-column>
               </el-table>
             </el-col>
           </el-row>

           <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
           <WorkTablePager @refreshData="getTableData"
                           :pageCount="totalPage">
           </WorkTablePager>
         </WorkMain>
</template>

<script>
import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

    export default {
        name: 'RoleGroupResult',
        data() {
            return {
              dataSourceList:[],
              tableDataUrl:'classify/rule',
              currPageNum:1,totalPage:1,
              taskId:this.$route.query.taskId,
              taskName:''
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
                url:this.tableDataUrl+"/"+this.taskId,
                params:{page:pageNum,pagesize:10}
              }).then(response=>{
                if(response.status!='0'){
                  this.Message.error(response.message)
                }else{
                  $this.refreshTableList(response.result)
                  $this.totalPage = response.total
                  $this.taskName = response.task
                }
            })
            },
        refreshTableList:function(dataList){
              this.dataSourceList = dataList
            }
        },
        mounted:function(){
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

  .text-gs{
        padding-left: 10px;
        padding-right: 10px;
        padding-bottom:9px;

  }

  .table-row{
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
