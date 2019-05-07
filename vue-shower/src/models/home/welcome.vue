<template>
  <div class="welcome-root">

    <WorkMain :contextClass="'welcome-table-context'" class="self-report" :noNeedHome="true" :headerItems="['待办事项']">

      <el-row class="table-page-root-outoptions">
        <el-col :span="24">
          <el-table
            :data="jobReportList"
            header-row-class-name="table-header-style"
            row-class-name="mini-font-size" stripe
            row-style="height:20px"
            style="width: 100%;">
            <el-table-column
              prop="report_origin_name"
              align="left"
              label="机构名称">
            </el-table-column>
            <el-table-column
              prop="report_name"
              align="left"
              label="报表名称">
            </el-table-column>
            <el-table-column
              align="left"
              :formatter="getDataDates"
              label="数据日期区间">
            </el-table-column>
            <el-table-column
              align="left"
              :formatter="getReportDates"
              label="填报截止日期">
            </el-table-column>
            <el-table-column
              prop="report_status"
              align="left" width="100"
              :formatter="getReportStatus"
              label="报送状态">
            </el-table-column>

            <el-table-column
              label="操作"
              align="left"
            >
              <template slot-scope="scope">

                <el-button size="mini" v-if="scope.row.report_status == 0" @click="reportFIll(scope.row.report_id)">填报</el-button>
                <el-button size="mini" v-if="scope.row.report_status == 1" @click="reportApprove(scope.row.report_id)">审批</el-button>
                <el-button size="mini" v-if="scope.row.report_status == 2" @click="reportReview(scope.row.report_id)">审核</el-button>
              </template>
            </el-table-column>
          </el-table>

        </el-col>
      </el-row>

      <WorkTablePager @refreshData="getJobReportList"
                      :pageCount="jobTotalPage">
      </WorkTablePager>

    </WorkMain>

    <WorkMain :contextClass="'welcome-table-context'" class="self-report" :noNeedHome="true" :headerItems="['报表统计']">
      <div class="self-report-chart">
       <echart ref="chart1" :options="pieOptions" ></echart>
      </div>
      <div class="son-origin-report">
        <el-row class="table-page-root-outoptions">
          <el-col :span="24">
            <el-table
              :data="childrenReportInfo">
              <el-table-column
                prop="origin_name"
                align="left"
                label="机构名称">
              </el-table-column>
              <el-table-column v-for="(value,key) in reportStatus"
                               :prop="key"
                               align="left" width="100"
                               :label="value">
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
        <WorkTablePager @refreshData="getChildenOriginReportInfo"
                        :pageCount="totalPage">
        </WorkTablePager>
      </div>
    </WorkMain>
  </div>
</template>


<script>
  import WorkTablePager from '@/models/public/WorkTablePager'
  import WorkMain from '@/models/public/WorkMain'

  export default {
    name: 'Welcome',
    data() {
        return {
          reportStatus:{
            'NORMAL':'填报中',
            'SUBMIT':'审批中',
            'REVIEW':'审核中',
            // 'LOCK':'锁定',
            // 'REMOVE':'失效',
            // 'UP_SIGIN':'待上传签名',
            // 'TOO_EARLY':'未到填写日期',
            // 'OVER_TIME':'过期',
            'REPORT_DONE':'填报完成',
          },
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
          },
          currPageNum: 1,
          eachPageNum: 10,
          totalPage: 1,
          jobCurrPageNum: 1,
          jobTotalPage: 1,
          tagTypeArray:['success','info','warning','danger'	],
          selfReportInfo:{},
          jobReportList:[],
          childrenReportInfo:[],
          pieOptions : {
            color:[ '#5f97ff', '#998ff3','#94cf87', '#ffe582', '#9aeedc'],
            series: [
              {
                name:'访问来源',
                type:'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                  normal: {
                    show: false,
                    position: 'center'
                  },
                  emphasis: {
                    show: true,
                    textStyle: {
                      fontSize: '20',
                      fontWeight: 'bold'
                    }
                  }
                },
                labelLine: {
                  normal: {
                    show: false
                  }
                },
                data:[]
              }
            ]
          }
        }
    },
    components: {
      WorkTablePager,
      WorkMain
    },
    methods: {
      getJobReportList(pageNum){
        if (pageNum && pageNum !== '') {
          this.jobCurrPageNum = pageNum
        } else {
          pageNum = this.jobCurrPageNum
        }

        const $this = this
        this.BaseRequest({
          url: "welcome/jobList",
          method: 'get',
          params: {currPage: pageNum, pageSize: this.eachPageNum}
        }).then(response => {
          if(response){
            this.jobTotalPage = response.totalPage
            this.jobReportList = response.dataList
          }
        })
      },
      getChildenOriginReportInfo(pageNum){
        if (pageNum && pageNum !== '') {
          this.currPageNum = pageNum
        } else {
          pageNum = this.currPageNum
        }

        const $this = this
        this.BaseRequest({
          url: "reportCust/getChildrenReportInfos",
          method: 'get',
          params: {currPage: pageNum, pageSize: this.eachPageNum}
        }).then(response => {
          if(response){
            this.totalPage = response.totalPage
            this.childrenReportInfo = response.dataList

            let chartDatas = []
            const childrenReportInfoTmp = {}
            if(this.childrenReportInfo!=null){
              this.childrenReportInfo.forEach(childReportInfo=>{
                const reportStatusCodes = Object.keys(childReportInfo)
                reportStatusCodes.forEach(reportStatusCode=>{
                  if(this.reportStatus[reportStatusCode]){
                    const reportStatusCn = this.reportStatus[reportStatusCode]
                    // debugger
                    if(childrenReportInfoTmp[reportStatusCn])
                      childrenReportInfoTmp[reportStatusCn] = childrenReportInfoTmp[reportStatusCn]+childReportInfo[reportStatusCode]
                    else{
                      childrenReportInfoTmp[reportStatusCn] = childReportInfo[reportStatusCode]+1
                    }
                  }

                })
              })
            }
            console.log(childrenReportInfoTmp)
            if(childrenReportInfoTmp!=null){
              const statusCns = Object.keys(childrenReportInfoTmp)
              statusCns.forEach(statusCn=>{
                chartDatas.push({name:statusCn,value:childrenReportInfoTmp[statusCn]})
              })
            }
            this.pieOptions.series[0].data = chartDatas
          }
        })
      },
      getReportStatus(rowData){
        return this.status_cn[rowData.report_status]
      },
      getDataDates(rowData){
        return rowData.report_data_start_str+"~"+rowData.report_data_end_str
      },
      getReportDates(rowData){
        return rowData.report_start_date_str+"~"+rowData.report_end_date_str
      },
      reportFill(reportId){
        this.$router.push({
          path: "/record/report/reportFill?reportId="+reportId+"&isView=N"
        });
      },
      reportReview(){
        this.$router.push({
          path: "record/reportReview"
        });
      },
      reportApprove(){
        this.$router.push({
          path: "record/reportApproval"
        });
      }
    },
    mounted() {
      this.getJobReportList()
      // this.getSelfReportInfo()
      this.getChildenOriginReportInfo()
    }

  }
</script>

<style lang="css">
  .welcome-table-context{
    height:calc(100% - 50px) !important;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";

  .welcome-root{
    width:100%;
    height:100%;
    background-size: 100%;
  }

  .self-report{
    width:100%;
    height:50% !important;
    overflow: auto;
  }

  .slef-report-tag{
    height:30px;
    line-height: 30px;
    float:left;
    margin:10px 0 0 50px;
    font-size: 15px;
  }

  .echarts{
    width:100%;
    height:100%;
  }

  .self-report-chart{
    width:30%;
    height:100%;
    max-height: 300px;
    float:left;
  }

  .son-origin-report{
    width:70%;
    overflow: hidden;
    height:100%;
    float:left;
  }


</style>
