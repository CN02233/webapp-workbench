<template>
  <WorkMain :headerItems="['报送填报','报送填报列表']">
    <!--<el-row class="search-row" :gutter="20">-->
      <!--<el-col class="align-left" :span="17">-->
        <!--<el-cascader :change-on-select="true" v-model="seachOriginList" :clearable="true"-->
                     <!--:options="cityTree">-->
        <!--</el-cascader>-->

        <!--<el-input placeholder="请输入机构名称" style="width:180px"  v-model="seachOriginName"></el-input>-->
        <!--<el-button @click="getTableData(1)" type="success">查询</el-button>-->
      <!--</el-col>-->
    <!--</el-row>-->
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
              prop="origin_province"
              align="left"
              label="所属省">
            </el-table-column>
          <el-table-column
              prop="origin_city"
              align="left"
              label="所属市">
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
            prop="report_start_date_str"
            :formatter="fomartterReportDataDate"
            align="left"
            label="报表期间">
          </el-table-column>

          <el-table-column
            label="操作"
            align="left"
          >
            <template slot-scope="scope">
              <el-button size="mini" v-if="scope.row.report_status == 0 &&loginUserType=='1'" @click="reportFIll(scope.row.report_id)">填报</el-button>
              <el-button size="mini" v-if="scope.row.report_status != 0||loginUserType=='0'" @click="viewReportFill(scope.row.report_id)">查看</el-button>
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
        seachOriginList: [],
        seachOriginName: '',
        cityTree:{},
        currPageNum: 1,
        eachPageNum: 10,
        totalPage: 1,
        loginUserType:'',
        status_cn:{
           '0' : '待填写',
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

        let seachOriginId = null
        if(this.seachOriginList!=null&&this.seachOriginList.length>0){
          seachOriginId = this.seachOriginList[this.seachOriginList.length-1]
        }

        const $this = this
        this.BaseRequest({
          url: "reportCust/pagerReport",
          method: 'get',
          params: {
            currPage: pageNum,
            pageSize: this.eachPageNum,
            searchOriginId:seachOriginId,
            searchOriginName:this.seachOriginName
          }
        }).then(response => {
          response.origins.forEach(origin=>{
            $this.origins[origin['origin_id']] = origin
          })
          $this.reportDataList = response.dataList
          // $this.origins = response.origins
          $this.totalPage = response.totalPage
          const cityTree = []
          response.first2Origin.forEach(proOrigin=>{
            const province = proOrigin['province']
            const citys = proOrigin['citys']
            const children = []

            citys.forEach(city=>{
              children.push({value: city['origin_id'],
                label: city['origin_name']})
            })

            cityTree.push({value: province['origin_id'],
              label: province['origin_name'],
              children: children})
          })

          $this.cityTree = cityTree

        })
      },
      checkUserType(){
        this.BaseRequest({
          url:'/sys/login/checkLoginUser',
          method:"get"
        }).then(loginUserInfo=>{
          // console.log(JSON.stringify(loginUserInfo))
          if(loginUserInfo!=null){
            this.loginUserType = loginUserInfo.user_type
          }
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
      },
      fomartterReportDataDate(rowData){
        return rowData.report_data_start_str+'~'+rowData.report_data_end_str
      }
    },
    mounted: function () { // 初始化
      this.reportDataList = []
      this.getTableData(1)
      this.checkUserType()
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

  .search-row{
    margin:5px 0 0 0;
  }
</style>
