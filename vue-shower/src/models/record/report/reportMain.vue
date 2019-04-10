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
            label="报表名称">
          </el-table-column>
          <el-table-column
              prop="report_origin"
              align="left"
              :formatter="getOriginName"
              label="报送机构">
            </el-table-column>
          <el-table-column
            prop="report_start_date"
            align="left"
            label="报送开始时间">
          </el-table-column>
          <el-table-column
            prop="report_end_date"
            align="left"
            label="报送结束时间">
          </el-table-column>

          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="250"
          >
            <template slot-scope="scope">
              <el-button size="mini" @click="reportFIll(scope.row.report_id)">填报</el-button>
              <el-button size="mini" type="danger" @click="handleEdit(scope.$index, scope.row)">提交</el-button>
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
  import WorkTablePager from '@/models/public/WorkTablePager'
  import WorkMain from '@/models/public/WorkMain'
  export default {
    name: "ReportMain",
    describe:"报送填报列表页面",
    data () {
      return {
        reportDataList: [],
        origins: {},
        currPageNum: 1,
        eachPageNum: 10,
        totalPage: 1
      }
    },
    components: {
      WorkTablePager,
      WorkMain
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
          response.origins.forEach(origin=>{
            $this.origins[origin['origin_id']] = origin
          })
          $this.reportDataList = response.dataList
          // $this.origins = response.origins
          $this.totalPage = response.totalPage

        })
      },
      reportFIll(reportId){
        this.$router.push({
          path: "/record/report/reportFill?reportId="+reportId
        });
      },
      getOriginName(rowData){
        return this.origins[rowData.report_origin].origin_name
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
