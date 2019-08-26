<template>
  <WorkMain :headerItems="['报送管理','报送复核']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="24">
        <el-cascader :change-on-select="true" v-model="seachOriginList" :clearable="true"
                     :options="cityTree">
        </el-cascader>

        <el-input placeholder="请输入机构名称" style="width:180px"  v-model="seachOriginName"></el-input>
        <el-button @click="getTableData(1)" type="success">查询</el-button>
        <el-button @click="batchReview('pass')" type="warning">批量通过</el-button>
        <el-button @click="batchReview('reject')" type="info">批量驳回</el-button>
        <el-button @click="batchReview('refill')" type="info">批量返回重填</el-button>
      </el-col>
    </el-row>
    <el-row class="table-page-root-outoptions">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          header-row-class-name="table-header-style"
          row-class-name="mini-font-size" stripe
          row-style="height:20px"
          @selection-change="selectChange"
          style="width: 100%;">
          <el-table-column
            type="selection"
            width="55">
          </el-table-column>
          <el-table-column
            prop="report_name"
            align="left"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="report_origin"
            align="left"
            :formatter="formattOriginName"
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
            prop="user_name_cn"
            align="left"
            label="填报人">
          </el-table-column>
          <el-table-column
            align="left"
            :formatter="checkPhone"
            label="联系方式">
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
              <el-button
                size="mini"
                @click="viewReportFill(scope.row.report_id,scope.row.report_status)">查看</el-button>
              <el-button
                size="mini"
                @click="exportExcel(scope.row.report_id,scope.row.report_name,scope.row.report_origin)">导出excel</el-button>
              <el-button
                size="mini"
                @click="viewReportSign(scope.row.report_id)">签字信息</el-button>
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


    <!-- 新增、编辑 弹窗-->
    <el-dialog class="table-options-modal" title="签字信息" :visible.sync="showSignModalPage" >
      <el-form label-position="right" label-width="30%" ref="siginForm">
        <el-form-item  label="填报人签名"  prop="reportCustName">
          <el-col :span="18">
            <el-input :disabled="true"  v-model="signInfomations.report_cust_name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="财务人员签名"  prop="reportAccountName">
          <el-col :span="18">
            <el-input  :disabled="true"  v-model="signInfomations.report_account_name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="公司领导签名"  prop="reportLeaderName">
          <el-col :span="18">
            <el-input  :disabled="true"  v-model="signInfomations.report_leader_name"></el-input>
          </el-col>
        </el-form-item>


      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary"  @click="showSignModalPage=false">确定</el-button>
      </div>
    </el-dialog>
  </WorkMain>
</template>

<script>
import WorkTablePager from '@/models/public/WorkTablePager'
import WorkMain from '@/models/public/WorkMain'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import axios from 'axios'

export default {
  name: 'ReportView',
  data () {
    return {
      reportDataList: [],
      definedDataObjs: {},
      origins: {},
      seachOriginList: [],
      seachOriginName: '',
      cityTree:{},
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
      },
      tableDataUrl: 'reportApproval/listReportReview',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: '',
      multipleSelection:[],
      showSignModalPage:false,
      signInfomations:{
        report_cust_name:'',
        report_account_name:'',
        report_leader_name:''
      }
    }
  },
  validations: {
    // 提交前的验证
  },
  computed: {
    // 初始化加载
  },
  components: {
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
      let seachOriginId = null
      if(this.seachOriginList!=null&&this.seachOriginList.length>0){
        seachOriginId = this.seachOriginList[this.seachOriginList.length-1]
      }
      this.BaseRequest({
        url: this.tableDataUrl,
        method: 'get',
        params: {currPage: pageNum, pageSize: this.eachPageNum,
          searchOriginId:seachOriginId,
          searchOriginName:this.seachOriginName}
      }).then(response => {
        /* if (response.dataList != null) {
            response.dataList.forEach(definedObj => {
              $this.definedDataObjs[definedObj.organization_id] = definedObj
            })
          } */
        $this.reportDataList = response.dataList
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
    refreshTableList: function (dataList) {
      this.reportDataList = dataList
    },
    closeModal: function () {
      this.showModalPage = false
      this.isEditModal = false
    },
    viewReportFill(reportId,reportStats){
      this.$router.push({
        path: "/record/report/reportFill?reportId="+reportId+"&isView=Y&auth=Y&reportStats="+reportStats
      });
    },
    exportExcel(reportId,reportName,report_origin){

      const instance = axios.create({
        baseURL: process.env.BASE_API,
        timeout: 120000,
        responseType: 'blob',
        withCredentials:true
      });

      instance({
        url: "/reportApproval/exportReportExcel",
        method: 'GET',
        params:{"reportId":reportId},
        responseType: 'blob', // important
      }).then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', reportName+'_'+this.origins[report_origin]+'.xls');
        document.body.appendChild(link);
        link.click();
      });
    },
    viewReportSign(reportId){
      this.BaseRequest({
        url: '/reportCust/reportSignInfos',
        method: 'get',
        params: {'reportId': reportId}
      }).then((response) => {
        this.showSignModalPage = true
        this.signInfomations = response
      })
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
    },
    getOriginList () {
      const loading = this.$loading({
        lock: true,
        text: '获取机构列表中.......',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      this.BaseRequest({
        url: 'submitAU/listAllSubmitauthority',
        method: 'get'
      }).then(response => {
        loading.close();
        if (response != null && response.length > 0) {
          const originTmp = new Object()

          function getChildOrigin(originList){
            originList.forEach(origin=>{
              originTmp[origin.id] = origin.label
              if(origin.children){
                getChildOrigin(origin.children)
              }
            })
          }
          getChildOrigin(response)

          this.origins = originTmp
        }
      })
    },
    getReportStatus(rowData){
      return this.status_cn[rowData.report_status]
    },

    formattOriginName(reportData){
      return this.origins[reportData.report_origin]
    },
    checkPhone(rowData){
      if(rowData.user_mobile_phone&&rowData.user_mobile_phone!=''){
        return rowData.user_mobile_phone
      }else{
        return rowData.user_office_phone
      }
    },
    fomartterReportDataDate(rowData){
      return rowData.report_data_start_str+'~'+rowData.report_data_end_str
    },
    selectChange(selection){
      this.multipleSelection = selection;
    },
    batchReview(proccess){
      if(this.multipleSelection!=null&&this.multipleSelection.length>0){
        const sendReportIds = []

        this.multipleSelection.forEach(multipleSelectionObj=>{
          const reportId = multipleSelectionObj.report_id
          sendReportIds.push(reportId)
        })

        this.BaseRequest({
          url: 'reportApproval/batchReportReviewOperator',
          method: 'post',
          data:{
            report_id_list:sendReportIds,
            operator:proccess
          }
        }).then(response => {
          this.Message.success("批量复核完成")
          this.getTableData(1)
        })

      }else{
        this.Message("请勾选需要复核的报表")
      }
    }
  },
  mounted: function () { // 初始化
    this.reportDataList = []
    this.getTableData(1)
    this.getOriginList()
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
