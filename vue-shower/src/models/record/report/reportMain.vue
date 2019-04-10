<template>
  <WorkMain :headerItems="['报送填报','报送填报列表']">
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          style="width: 100%">
          <el-table-column
            prop="report_name"
            align="left"
            width="200"
            label="报表名称">
          </el-table-column>
          <el-table-column
              prop="report_origin"
              align="left"
              width="230"
              label="所属机构">
            </el-table-column>
          <el-table-column
            prop="report_start_date"
            align="left"
            width="200"
            label="报送开始时间">
          </el-table-column>
          <el-table-column
            prop="report_end_date"
            align="left"
            width="200"
            label="报送结束时间">
          </el-table-column>

          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="250"
          >
            <template slot-scope="scope">
              <el-button
                size="mini" @click="definedUnit(scope.row.defined_id)"
              >报送单元</el-button>
              <el-button
                size="mini"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
            </template>
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
  export default {
    name: "ReportMain",
    describe:"报送填报列表页面",
    data () {
      return {
        reportDataList: [],
        currPageNum: 1,
        eachPageNum: 10,
        totalPage: 1
      }
    },
    methods: {
      getTableData(pageNum){
        if (pageNum && pageNum !== '') {
          this.currPageNum = pageNum
        } else {
          pageNum = this.currPageNum
        }
        const $this = this
        this.BaseRequest({
          url: "reportCust/pagerReport",
          method: 'get',
          params: {currPage: pageNum, pageSize: this.eachPageNum}
        }).then(response => {
          $this.reportDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      }
    },
    mounted: function () { // 初始化
      this.reportDataList = []
      this.getTableData(1)
    }
  }
</script>

<style scoped>

</style>
