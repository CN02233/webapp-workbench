<template>
  <WorkMain :headerItems="['报送管理','报表审批']">
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
            prop="reportStatus"
            align="left"
            label="状态" :formatter="reportStatusFormatter">
          </el-table-column>

          <el-table-column
            prop="lockedUser"
            align="left"
            label="提交人">
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
              <!--<el-button type="text" @click="openEditModal(scope.row)" size="small">通过</el-button>-->
              <el-button type="text" @click="reviewReport('approve',scope.row.reportId)" size="small">通过</el-button>
              <el-button type="text" @click="reviewReport('turnDown',scope.row.reportId)" size="small">驳回</el-button>
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
            <el-option :key="item.template_id" v-for="item in templateList" :label="item.template_name" :value="item.template_id"></el-option>
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
    name: 'report-review',
    data() {
      return {
        recordDataList:[],
        tableDataUrl:'reportApprove/submitReport',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        uploadFilePath:'',
        filePath:'',
        isEditModal:false,
        template:'',
        newReportName:'',
        reportStatusArray:{
          0:'正常',
          1:'审批中',
          2:'复核中',
          3:'锁定',
          4:'失效',
          5:'报表发布',
          6:'待上传签名'
        },
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
        return this.originDataObjs[row.super_module_id]!=null?this.originDataObjs[row.super_module_id].module_name:"无"
      },
      refreshTableList:function(dataList){
        this.recordDataList = dataList
      },
      reviewReport(reviewResult,reportId){
        this.BaseRequest({
          url:'reportApprove/doReview/'+reviewResult,
          method:"get",
          params:{reportId:reportId}
        }).then(response=>{
          this.Message.success("保存成功")
          this.getTableData()
        })
      },
      reportStatusFormatter(row,column){
        return this.reportStatusArray[row.reportStatus]
      }
    },
    mounted:function(){
      this.menuDataList = []
      this.getTableData(1)
      this.initTemplates()
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
