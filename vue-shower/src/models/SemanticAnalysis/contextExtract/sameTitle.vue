<template>
  <WorkMain :headerItems="['NLP应用','共同主题']" class="table-page-root">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新建共同主题分析</el-button>
        <el-button @click="getTableData()" type="primary">刷新</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="jobDataList"
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
            label="任务状态">
          </el-table-column>
          <el-table-column
            label="分析结果"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="showResult(scope.row)" size="small">查看结果</el-button>
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
    <el-dialog title="新建共同主题分析" :visible.sync="showModalPage" >
      <el-form class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item :size="small" label="任务名称" >
          <el-input v-model="formData.task" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="数据源" >
          <el-select style="width: 100%;" v-model="formData.datasource" placeholder="请选择">
            <el-option
              v-for="item in dataSourceList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="库名" >
          <el-input v-model="formData.db" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="表名" >
          <el-input v-model="formData.table" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="文档字段名" >
          <el-input v-model="formData.filed" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="subJob();closeModal()">确 定</el-button>
      </div>
    </el-dialog>
  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

  export default {
    name: 'SameTitle',

    data() {
      return {
        jobDataList:[],
        tableDataUrl:'textrank',
        currPageNum:1,
        showModalPage:false,
        isEditModal:false,
        addFormData:{
          task:'',
          datasource:'',
          db:'',
          table:'',
          filed:''
        },
        dataSourceList: []
      }
    },
    computed:{
      formData:function(){
        return this.addFormData
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
          params:{page:pageNum,pagesize:10,wordName:this.wordNameForSearch}
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            $this.refreshTableList(response.result)
          }
      })
      },
      refreshTableList:function(dataList){
        this.jobDataList = dataList
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      showResult:function(rowData){
        if(rowData.result){
          this.$alert(rowData.result, '任务名称：'+rowData.task, {
            confirmButtonText: '确定'
          });
        }else{
          this.$alert('任务正在后台运行，请关注结果页面查看分析结果！', '共同主题分析', {
            confirmButtonText: '确定'
          });
        }

      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
      },
      subJob:function(){
        let sendData = this.addFormData
        let sendUrl = "textrank"
        const $this = this
        this.Request({
          url:sendUrl,
          method:"post",
          data:sendData
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            this.Message.success("保存成功")
            $this.getTableData(1)
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
      }
    },
    mounted:function(){
      this.getTableData(1)
      this.getDataSourceList()
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

  .table-row{
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
