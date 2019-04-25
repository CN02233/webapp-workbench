<template>
  <div class="welcome-root">
    <div class="self-report">
      <el-tag class="slef-report-tag" v-for="(value,name,index) in selfReportInfo" :type="tagTypeArray[((index)%3)]">
        {{reportStatus[name]}}:{{value}}
      </el-tag>

    </div>
    <div class="self-report-chart">
      <echart ref="chart1" :options="pieOptions" ></echart>
    </div>
    <div class="son-origin-report">
      <el-row class="table-row">
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
  </div>
</template>


<script>
  import WorkTablePager from '@/models/public/WorkTablePager'

  export default {
    name: 'Welcome',
    data() {
        return {
          reportStatus:{
            'NORMAL':'填报中',
            'SUBMIT':'审批中',
            'REVIEW':'复核中',
            // 'LOCK':'锁定',
            // 'REMOVE':'失效',
            'APPROVE':'填报结束',
            // 'UP_SIGIN':'待上传签名',
            // 'TOO_EARLY':'未到填写日期',
            // 'OVER_TIME':'过期',
            'REPORT_DONE':'填报完成',
          },
          currPageNum: 1,
          eachPageNum: 10,
          totalPage: 1,
          tagTypeArray:['success','info','warning','danger'	],
          selfReportInfo:{},
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
      WorkTablePager
    },
    methods: {
      getSelfReportInfo(){
        this.BaseRequest({
          url: 'reportCust/getReportInfos',
          method: 'get'
        }).then(response => {
          let chartDatas = []
          const selfReportInfoTmp = {}
          Object.keys(response).forEach(reportCode=>{
            if(this.reportStatus[reportCode]){
              selfReportInfoTmp[reportCode] = response[reportCode]
              chartDatas.push({name:this.reportStatus[reportCode],value:response[reportCode]+1})
            }
          })
          this.pieOptions.series[0].data = chartDatas
          this.selfReportInfo = selfReportInfoTmp
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
          }
        })
      }
    },
    mounted() {
      this.getSelfReportInfo()
      this.getChildenOriginReportInfo()
    }

  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .welcome-root{
    width:100%;
    height:100%;
    background-size: 100%;
  }

  .self-report{
    width:100%;
    height:60px;
    text-align: left;
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
    overflow: auto;
    height:calc(100% - 60px);
    float:left;
  }


</style>
