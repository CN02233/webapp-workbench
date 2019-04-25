<template>
  <WorkMain :headerItems="['报送填报','报送填报列表']">
    <el-row class="table-page-root-outoptions">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          header-row-class-name="table-header-style"
          row-class-name="mini-font-size" stripe
          row-style="height:20px"
          style="width: 100%;">
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
            prop="report_status"
            align="left" width="100"
            :formatter="getReportStatus"
            label="报送状态">
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
              <el-button size="mini" v-if="scope.row.report_status != 0" @click="viewReportFill(scope.row.report_id)">查看</el-button>
              <!--<el-button size="mini" v-if="scope.row.report_status == 9" type="danger" @click="reportCommitAuth(scope.row.report_id)">提交</el-button>-->
              <!--<el-button size="mini" v-if="scope.row.report_status == 0" type="danger"  @click="reportCommitAuth( scope.row.report_id)">提交</el-button>-->
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
        totalPage: 1,
        status_cn:{
           '0' : '正常',
           '1':'审批中',
           '2':'复核中',
           '3':'锁定',
           '4':'失效',
           '5':'报表发布',
           '6':'待上传签名',
           '7':'未到填写日期',
           '8':'过期',
           '9':'填报完成'
        }
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
          path: "/record/report/reportFill?reportId="+reportId+"&isView=N"
        });
      },
      viewReportFill(reportId){
        this.$router.push({
          path: "/record/report/reportFill?reportId="+reportId+"&isView=Y"
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
      },
      getReportStatus(rowData){
        return this.status_cn[rowData.report_status]
      }
    },
    mounted: function () { // 初始化
      this.reportDataList = []
      this.getTableData(1)
    }
  }
</script>

<style lang="css">
  .mini-font-size{
    font-size: 12px !important;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";
</style>
