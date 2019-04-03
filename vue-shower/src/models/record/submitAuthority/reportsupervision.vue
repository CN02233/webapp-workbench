<template>
  <WorkMain :headerItems="['报送管理','报送监管']">
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="definedDataList"
          style="width: 100%">
          <el-table-column
            prop="defined_name"
            align="left"
            width="150"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="status"
            align="left"
            width="150"
            label="状态">
          </el-table-column>
          <el-table-column
            prop="origin_name"
            align="left"
            width="230"
            label="所属机构">
          </el-table-column>
          <el-table-column
            prop="create_time"
            align="left"
            width="180"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="user_name"
            align="left"
            width="100"
            label="创建人">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="150"
            >
            <template slot-scope="scope">
              <el-button
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
  name: 'OriginMain',
  data () {
    return {
      definedDataList: [],
      definedDataObjs: {},
      tableDataUrl: 'reportStatements/listReportStatementsByUser',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: ''
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
        if (response.dataList != null) {
          response.dataList.forEach(definedObj => {
            $this.definedDataObjs[definedObj.organization_id] = definedObj
          })
        }
        $this.definedDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.definedDataList = dataList
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
    }
  },
  mounted: function () { // 初始化
    this.definedDataList = []
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
