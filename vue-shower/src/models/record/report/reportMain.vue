<template>
  <WorkMain :headerItems="['报送管理','报表管理']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="recordDataList"
          style="width: 100%">
          <el-table-column
            prop="reportName"
            align="left"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="reportTemplateName"
            align="left"
            label="模板">
          </el-table-column>
          <el-table-column
            prop="reportCreateDate"
            align="left"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="reportCreate"
            align="left"
            label="创建人">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="left"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
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
    <el-dialog title="新增" :visible.sync="showModalPage" >
      <el-form class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item :size="small" label="报表名称" >
          <el-input :disabled="false"  v-model="newReportName" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item :size="small" label="报表模板" >
          <el-select v-model="template" style="width:100%;" placeholder="请选择报表模板">
            <el-option :key="item.id" v-for="item in templateList" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="goToEditPage()">确 定</el-button>
      </div>
    </el-dialog>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'report-main',
    data() {
      return {
        recordDataList:[],
        tableDataUrl:'report/reportList',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        uploadFilePath:'',
        filePath:'',
        isEditModal:false,
        template:'',
        newReportName:'',
        templateList:[
          {id:'test.xlsx',name:'测试模板'},
          {id:'test1.xlsx',name:'测试模板1'},
          {id:'test2.xlsx',name:'测试模板2'}
        ]
      }
    },
    validations:{
      formData:{
        super_module_id:{
          required
        },
        module_name:{
          required
        }
      }
    },
    computed:{
      formData:function(){
        if(this.isEditModal){
          return this.editFormData
        }else{
          return this.addFormData
        }
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

        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum}
        }).then(response=>{
          $this.recordDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      formatterSuperName:function(row){
        return this.menuDataObjs[row.super_module_id]!=null?this.menuDataObjs[row.super_module_id].module_name:"无"
      },
      refreshTableList:function(dataList){
        this.recordDataList = dataList
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal(editRow){
        this.reportId = editRow.reportId
        this.isEditModal = true
        this.goToEditPage()
      },
      fileChange:function(file, fileList){
        this.uploadFilePath = file.name
        var index= this.uploadFilePath.lastIndexOf(".");
        var ext = this.uploadFilePath.substr(index+1);

        if(ext!='txt'&&ext!='html'&&ext!='pdf'&&ext!='doc'&&ext!='docx'&&ext!='xls'&&ext!='xlsx'&&ext!='pptx'&&ext!='eml'){
          this.$notify.error("错误的文件类型！目前仅支持：txt、html、pdf、doc、docx、xls、xlsx、pptx、eml")
          this.uploadFilePath = ""
          return
        }
        this.uploadFile = fileList[fileList.length-1]
      },
      removeFile(){
        this.uploadFilePath = ""
        this.uploadFile = null
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
        Object.keys(this.formData).forEach(objKey=>{
          this.formData[objKey] = ""
        })
      },
      goToEditPage(){
        this.$router.push({
            path: "/record/report/edit",
            query:{
            'template':this.template,
            'reportId':this.reportId,
            'reportName':this.newReportName,
            'isCreate':!this.isEditModal
            }
          });
      }
    },
    mounted:function(){
      this.menuDataList = []
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

  .table-row{
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
