<template>
  <WorkMain :headerItems="['报送提醒','短信提醒']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left table-button-group" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>
    <el-row class="table-page-root-outoptions">
      <el-col :span="24">
        <el-table
          :data="smsConfigList"
          header-row-class-name="table-header-style"
          row-class-name="mini-font-size" stripe
          row-style="height:20px"
          style="width: 100%;">
          <el-table-column
            prop="config_name"
            align="left"
            label="配置名称">
          </el-table-column>
          <el-table-column
              prop="report_defined_name"
              align="left"
              label="报送模板">
            </el-table-column>
          <el-table-column
              prop="sms_template_id"
              align="left"
              label="短信模板ID">
            </el-table-column>
          <el-table-column
              prop="send_date_str"
              align="left"
              label="发送日期">
            </el-table-column>
          <el-table-column
            prop="send_time_str"
            align="left" width="100"
            label="发送时间">
          </el-table-column>
          <el-table-column
            prop="pre_days"
            align="left"
            label="截止日前天数发送">
          </el-table-column>
          <el-table-column
            prop="cross_holiday"
            align="left"
            label="是否略过假期"
            :formatter="formatCross"
          >
          </el-table-column>
          <el-table-column
            prop="send_status"
            align="left"
            :formatter="formatSendStatus"
            label="发送状态">
          </el-table-column>

          <el-table-column
            label="操作"
            align="left"
          >
            <template slot-scope="scope">
              <el-button size="mini" @click="deleteSmsConfig(scope.row.id)">删除</el-button>
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


    <!-- 新增、编辑 弹窗-->
    <el-dialog class="table-options-modal" :title="dialogTitle" :visible.sync="showModalPage" >

      <el-form style="width:80%" ref="form" :model="form" label-width="200px">
        <el-form-item label="配置名称">
          <el-input size="small" placeholder="请输入配置名称" v-model="formSubmitData.config_name" class="input-with-select" ></el-input>
        </el-form-item>

        <el-form-item label="报表定义">
          <el-select style="width:100%" v-model="formSubmitData.report_defined_id" placeholder="请输入报送模板">
            <el-option
              v-for="item in reportDefinedList"
              :key="item.defined_id"
              :label="item.defined_name"
              :value="item.defined_id">
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="短信模板">
          <!--<el-select style="width:100%" @change="changeReportType" v-model="formSubmitData.report_type" placeholder="请选择报送类型">-->
          <el-select style="width:100%" v-model="formSubmitData.sms_template_id" placeholder="请选择报送类型">
            <el-option
              v-for="item in smsTemplates"
              :key="item.template_id"
              :label="item.template_name"
              :value="item.template_id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="发送时间">
          <el-time-select style="width: 100%;" v-model="formSubmitData.send_time_str"
            :picker-options="{
              start: '08:30',
              step: '00:15',
              end: '22:30'
            }"
            placeholder="选择发送时间">
          </el-time-select>
        </el-form-item>

        <el-form-item label="截止日前天数发送">
          <el-input size="small" placeholder="请输入配置名称" v-model="formSubmitData.pre_days" class="input-with-select" ></el-input>
        </el-form-item>

        <el-form-item label="是否略过假期">
          <el-select style="width:100%" v-model="formSubmitData.cross_holiday" placeholder="请选择报送类型">
            <el-option key="Y" label="是" value="Y"></el-option>
            <el-option key="N" label="否" value="N"> </el-option>
          </el-select>
        </el-form-item>


      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showModalPage = false">取 消</el-button>
        <el-button type="primary" @click="createSmsJob">确 定</el-button>
      </div>
    </el-dialog>


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
        smsConfigList: [],
        reportDefinedList: [],
        smsTemplates: [],
        currPageNum: 1,
        eachPageNum: 10,
        totalPage: 1,
        showModalPage: false,
        isEditModal: false,
        sendStatus:{
          '0':'待发送',
          '1':'发送中',
          '2':'已发送',
          '3':'发送失败'
        },
        formSubmitData:{
          config_name:'',
          report_defined_id:'',
          sms_template_id:'',
          send_time_str:'',
          cross_holiday:'',
          pre_days:''
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
          url: "sms/pagerSms",
          method: 'get',
          params: {
            currPage: pageNum,
            pageSize: this.eachPageNum
          }
        }).then(response => {
          $this.smsConfigList = response.dataList
          // $this.origins = response.origins
          $this.totalPage = response.totalPage
        })
      },
      getReportDefinedList(){
        const $this = this
        this.BaseRequest({
          url: "reportStatements/listReportStatements",
          method: 'get',
          params: {
            currPage: "1",
            pageSize: "10000"
          }
        }).then(response => {
          $this.reportDefinedList = []
          if(response.dataList!=null){
            response.dataList.forEach(reportDefinedObj=>{
              if(reportDefinedObj.status=='4'){
                $this.reportDefinedList.push(reportDefinedObj)
              }
            })
          }
        })
      },

      getSmsTemplates(){
        const $this = this
        this.BaseRequest({
          url: "sms/getSmsTemplates",
          method: 'get'
        }).then(response => {
          $this.smsTemplates = []
          $this.smsTemplates = response
        })
      },
      createSmsJob(){
        this.BaseRequest({
          url: "sms/createSmsJob",
          method: 'post',
          data:this.formSubmitData
        }).then(response => {
          if(!response){
          }else{
            this.Message.success("创建成功")
            this.getTableData(1)
            this.showModalPage = false
          }
        })
      },
      deleteSmsConfig(smsConfigId){
        this.$confirm('确定删除短信提醒？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString: true,
          type: 'warning'
        }).then(() => {
          this.BaseRequest({
            url: "sms/deleteSmsJob",
            method: 'get',
            params:{"smsId":smsConfigId}
          }).then(response => {
            if(!response){
            }else{
              this.Message.success("删除成功")
              this.getTableData(1)
            }

          })
        }).catch(() => {
        })

      },

      openAddModal: function () {
        // this.getOriginList()
        this.showModalPage = true
        this.isEditModal = false
        this.$nextTick(function () {
          this.$refs.treeRef.setCheckedKeys([])
          this.clearData()
        })
      },
      formatCross: function (row) {
        if (row.cross_holiday === 'Y') {
          return '是'
        }else{
          return '否'
        }
      },
      formatSendStatus: function (row) {
        return this.sendStatus[row.send_status]
      }

    },
    mounted: function () { // 初始化
      this.smsConfigList = []
      this.getTableData(1)
      this.getReportDefinedList()
      this.getSmsTemplates()
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
