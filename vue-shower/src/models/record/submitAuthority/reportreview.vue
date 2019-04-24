<template>
  <WorkMain :headerItems="['报送管理','报送复核']">
    <el-row class="table-page-root-outoptions">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          header-row-class-name="table-header-style"
          row-class-name="mini-font-size" stripe
          style="width: 100%">
          <el-table-column
            prop="report_id"
            align="left"
            label="报表ID">
          </el-table-column>
          <el-table-column
            prop="report_name"
            align="left"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="active_unit"
            align="left"
            label="报表步骤数">
          </el-table-column>
          <el-table-column
            prop="report_status"
            align="left"
            label="状态"
            :formatter="formatStatus"
          >
          </el-table-column>
          <el-table-column
            prop="create_date"
            align="left"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="user_name"
            align="left"
            label="创建人">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="250"
          >
            <template slot-scope="scope">
              <el-button
                size="mini"
                @click="handlePass(scope.$index, scope.row)">通过</el-button>
              <el-button
                size="mini"
                @click="handleReject(scope.$index, scope.row)">驳回</el-button>
              <el-button
                size="mini"
                @click="handleRefill(scope.$index, scope.row)">返回重填</el-button>
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
    <el-dialog class="table-options-modal":title="dialogTitle" :visible.sync="showModalPage" >
      <el-row class="table-options-modal-item" :gutter="16">
        <el-col :sm="12">
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="handleInsert">确 定</el-button>
      </div>
    </el-dialog>
  </WorkMain>
</template>

<script>
import WorkTablePager from '@/models/public/WorkTablePager'
import WorkMain from '@/models/public/WorkMain'
import { required } from 'vuelidate/lib/validators'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  name: 'ReportView',
  data () {
    return {
      reportDataList: [],
      definedDataObjs: {},
      tableDataUrl: 'reportApproval/listReportReview',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: ''
    }
  },
  validations: {
    // 提交前的验证
  },
  computed: {
    // 初始化加载
  },
  components: {
    Treeselect,
    WorkTablePager,
    WorkMain
  },
  methods: {
    formatStatus: function (row, column) {
      if (row.report_status === '2') {
        return '复核中'
      }
    },
    getTableData: function (pageNum) {
      if (pageNum && pageNum !== '') {
        this.currPageNum = pageNum
      } else {
        pageNum = this.currPageNum
      }
      const $this = this
      this.BaseRequest({
        url: this.tableDataUrl,
        method: 'get',
        params: {currPage: pageNum, pageSize: this.eachPageNum}
      }).then(response => {
        /* if (response.dataList != null) {
            response.dataList.forEach(definedObj => {
              $this.definedDataObjs[definedObj.organization_id] = definedObj
            })
          } */
        $this.reportDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.reportDataList = dataList
    },
    closeModal: function () {
      this.showModalPage = false
      this.isEditModal = false
    },
    handlePass (index, row) { // 通过
      this.BaseRequest({
        url: '/reportApproval/ReportReviewOperator',
        method: 'get',
        params: {'reportId': row.report_id, 'reportStatus': 'pass'}
      }).then(() => {
        this.Message.success('复核通过成功')
        this.getTableData()
      })
    },
    handleReject (index, row) { // 驳回
      this.BaseRequest({
        url: '/reportApproval/ReportReviewOperator',
        method: 'get',
        params: {'reportId': row.report_id, 'reportStatus': 'reject'}
      }).then(() => {
        this.Message.success('驳回成功')
        this.getTableData()
      })
    },
    handleRefill (index, row) { // 重填
      this.BaseRequest({
        url: '/reportApproval/ReportReviewOperator',
        method: 'get',
        params: {'reportId': row.report_id, 'reportStatus': 'refill'}
      }).then(() => {
        this.Message.success('返回重填执行成功')
        this.getTableData()
      })
    },
    checkInputNull () {
      const checkResult = this.$v.$invalid
      if (checkResult) {
        this.$notify({
          dangerouslyUseHTMLString: true,
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>报送单元名称、所属报送机构、状态'
        })
      }
      return checkResult
    }
  },
  mounted: function () { // 初始化
    this.reportDataList = []
    this.getTableData(1)
    this.getOriginList()
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
