<template>
  <WorkMain :headerItems="['报送管理','报送监管']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-select v-model="originId" placeholder="请选择报送机构">
          <el-option
            v-for="item in originOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-button @click="queryByOriginId" type="primary">查询</el-button>
      </el-col>
    </el-row>
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          style="width: 100%">
          <el-table-column
            prop="report_id"
            align="left"
            width="150"
            label="报表ID">
          </el-table-column>
          <el-table-column
            prop="report_name"
            align="left"
            width="150"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="active_unit"
            align="left"
            width="150"
            label="报表步骤数">
          </el-table-column>
          <el-table-column
            prop="report_status"
            align="left"
            width="150"
            label="状态"
            :formatter="formatStatus">
          </el-table-column>
          <el-table-column
            prop="report_end_date"
            align="left"
            width="180"
            label="最后保存日期">
          </el-table-column>
          <el-table-column
            prop="last_modify_user"
            align="left"
            width="100"
            label="最后填报人">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="150"
          >
            <template slot-scope="scope">
              <el-button @click="reportShow(scope.row.report_id)"
                size="mini"
              >详情</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="showModalPage" >
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
    name: 'ReportSupervision',
    data () {
      return {
        reportDataList: [],
        definedDataObjs: {},
        tableDataUrl: 'reportStatements/listReportStatementsByUser',
        currPageNum: 1,
        eachPageNum: 10,
        totalPage: 1,
        showModalPage: false,
        isEditModal: false,
        dialogTitle: '',
        originOptions: [],
        originId: ''
      }
    },
    validations: {// 提交前的验证
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
        if (row.report_status === '0') {
          return '正常'
        }
        if (row.report_status === '1') {
          return '审核中'
        }
        if (row.report_status === '2') {
          return '复核中'
        }
        if (row.report_status === '3') {
          return '锁定'
        }
        if (row.report_status === '4') {
          return '报表发布'
        }
        if (row.report_status === '5') {
          return '锁定'
        }
        if (row.report_status === '6') {
          return '待上传签名'
        }
      },
      queryByOriginId: function () {
        this.BaseRequest({
          url: this.tableDataUrl,
          method: 'get',
          params: { 'currPage': 1, 'pageSize': this.eachPageNum, 'originId': this.originId }
        }).then(response => {
          this.reportDataList = response.dataList
          this.totalPage = response.totalPage
        })
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
          params: { 'currPage': pageNum, 'pageSize': this.eachPageNum }
        }).then(response => {
          if (response.dataList != null) {
            response.dataList.forEach(definedObj => {
              $this.definedDataObjs[definedObj.organization_id] = definedObj
            })
          }
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
      checkInputNull () {
        const checkResult = this.$v.$invalid
        if (checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>报送单元名称、所属报送机构、状态'
          })
        }
        return checkResult
      },
      getOrigin: function () {
        this.BaseRequest({
          url: '/reportStatements/getOriginsByUserId',
          method: 'get',
          params: {}
        }).then(response => {
          this.originOptions.push({
            value: null,
            label: '全部'
          })
          for (let i = 0; i < response.length; i++) {
            this.originOptions.push({
              value: response[i].origin_id,
              label: response[i].origin_name
            })
          }
          console.log(response)
        })
      },
      reportShow(reportId){
        this.$router.push({
          path: "/record/report/reportFill?reportId="+reportId+"&isView=Y"
        });
      }
    },
    mounted: function () { // 初始化
      this.reportDataList = []
      this.getTableData(1)
      this.getOrigin()
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
