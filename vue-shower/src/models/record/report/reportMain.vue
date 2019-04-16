<template>
  <WorkMain :headerItems="['报送填报','报送填报列表']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        &nbsp;
      </el-col>
    </el-row>
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
            prop="report_start_date_str"
            align="left"
            label="报送开始时间">
          </el-table-column>
          <el-table-column
            prop="report_end_date_str"
            align="left"
            label="报送结束时间">
          </el-table-column>

          <el-table-column
            label="操作"
            align="left"
          >
            <template slot-scope="scope">
              <el-button size="mini" v-if="scope.row.report_status == 0" @click="reportFIll(scope.row.report_id)">填报</el-button>
              <el-button size="mini" v-if="scope.row.report_status == 9" type="danger" @click="reportCommitAuth(scope.row.report_id)">提交</el-button>
              <!--<el-button size="mini" type="danger" @click="reportCommitAuth( scope.row.report_id)">提交</el-button>-->
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
      reportCommitAuth(reportId){
        const $this = this
        this.$confirm('提交操作将使该报送报表进入审批流程，进入审批流程后将无法修改填报数据。请确认数据正确性！', '提示', {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          const loading = $this.$loading({
            lock: true,
            text: '提交审批',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          this.BaseRequest({
            url:"/reportCust/doCommitAuth",
            method:'get',
            params:{
              reportId:reportId
            }
          }).then(response=>{
            loading.close();
            $this.Message.success("提交成功")
            $this.getTableData(1)
          });
        }).catch(() => {
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
