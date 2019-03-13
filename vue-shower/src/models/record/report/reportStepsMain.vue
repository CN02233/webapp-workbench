<template>
  <WorkMain :headerItems="['报送管理','报表管理']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">创建报表</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="recordDataList"
          style="width: 100%">
          <el-table-column
            prop="reportName"
            align="left"
            label="报表名称">
          </el-table-column>
          <!--<el-table-column-->
            <!--prop="reportTemplateName"-->
            <!--align="left"-->
            <!--label="模板">-->
          <!--</el-table-column>-->

          <el-table-column
            prop="reportStatus"
            align="center"
            label="状态" :formatter="reportStatusFormatter">
          </el-table-column>

          <el-table-column
            prop="lockUserName"
            align="center"
            label="锁定人">
          </el-table-column>

          <el-table-column
            prop="reportCreateDate"
            align="center"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="reportCreateName"
            align="left"
            label="创建人">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
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
    <el-dialog title="新增" :visible.sync="showModalPage" >
      <el-form class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item :size="small" label="报表名称" >
          <el-input :disabled="false"  v-model="newReportName" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item :size="small" label="报表模板" >
          <el-select v-model="template" style="width:100%;" placeholder="请选择报表模板">
            <el-option :key="item.template_id" v-for="item in templateList" :label="item.template_name" :value="item.template_id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="goToEditPage()">确 定</el-button>
      </div>
    </el-dialog>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'report-main',
    data() {
      return {
        recordDataList:[],
        tableDataUrl:'report/reportList',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        uploadFilePath:'',
        filePath:'',
        isEditModal:false,
        template:'',
        newReportName:'',
        reportStatusArray:{
          0:'正常',
          1:'审批中',
          2:'复核中',
          3:'锁定',
          4:'失效',
          5:'报表发布',
          6:'待上传签名'
        },
        templateList:[
          {id:'test.xlsx',name:'测试模板'},
          {id:'realtest.xlsx',name:'真实模板'},
          {id:'test1.xlsx',name:'测试模板1'},
          {id:'test2.xlsx',name:'测试模板2'}
        ]
      }
    },
    validations:{
      formData:{
        super_module_id:{
          required
        },
        module_name:{
          required
        }
      }
    },
    computed:{
      formData:function(){
        if(this.isEditModal){
          return this.editFormData
        }else{
          return this.addFormData
        }
      }
    },
    components: {
      WorkTablePager,
      WorkMain
    },
    methods: {
      getTableData:function(pageNum){
        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this

        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum}
        }).then(response=>{
          $this.recordDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      formatterSuperName:function(row){
        return this.originDataObjs[row.super_module_id]!=null?this.originDataObjs[row.super_module_id].module_name:"无"
      },
      refreshTableList:function(dataList){
        this.recordDataList = dataList
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal(editRow){
        this.reportId = editRow.reportId
        this.isEditModal = true
        this.goToEditPage()
      },
      fileChange:function(file, fileList){
        this.uploadFilePath = file.name
        var index= this.uploadFilePath.lastIndexOf(".");
        var ext = this.uploadFilePath.substr(index+1);

        if(ext!='txt'&&ext!='html'&&ext!='pdf'&&ext!='doc'&&ext!='docx'&&ext!='xls'&&ext!='xlsx'&&ext!='pptx'&&ext!='eml'){
          this.$notify.error("错误的文件类型！目前仅支持：txt、html、pdf、doc、docx、xls、xlsx、pptx、eml")
          this.uploadFilePath = ""
          return
        }
        this.uploadFile = fileList[fileList.length-1]
      },
      removeFile(){
        this.uploadFilePath = ""
        this.uploadFile = null
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
        Object.keys(this.formData).forEach(objKey=>{
          this.formData[objKey] = ""
        })
      },
      initTemplates(){
        this.BaseRequest({
          url:"/template/getTemplatesByUser",
          method:"get",
          params:{currPage:1,pageSize:1000}
        }).then(response=>{
          this.templateList = response
        })
      },
      goToEditPage(){
        const $this = this

        function doEdit(){
          $this.$router.push({
            path: "/record/stepsEdit",
            query:{
              'template':$this.template,
              'reportId':$this.reportId,
              'reportName':$this.newReportName,
              'isCreate':!$this.isEditModal
            }
          });
        }

        if(!$this.isEditModal){
          doEdit()
        }else{
          this.BaseRequest({
            url:"/report/loadReportBasic",
            method:"get",
            params:{reportId:this.reportId}
          }).then(response=>{
            const reportInfo = response
            if(reportInfo.reportStatus=='3'){
              this.BaseRequest({
                url:'/sys/login/checkLoginUser',
                method:"get"
              }).then(loginUserInfo=>{
                if(loginUserInfo!=null){
                  const currUser = loginUserInfo.user_id
                  if(response.lockedUser===currUser){
                    doEdit()
                  }
                }else{
                  this.$alert('当前报表已被锁定,无法继续编辑', '提示', {});
                }
              })
            }else if(reportInfo.reportStatus=='2'){
              this.$alert('当前报表正在复核,无法修改。', '提示', {});
            }else if(reportInfo.reportStatus=='4'){
              this.$alert('当前报表已失效,无法修改。', '提示', {});
            }else if(reportInfo.reportStatus=='5'){
              this.$alert('当前报表已通过,无法修改', '提示', {});
            }else if(reportInfo.reportStatus=='1'){
              this.$alert('当前报表已提交审批,无法修改', '提示', {});
            }else{
              $this.$confirm('执行编辑操作将锁定该报表，其他人将无法编辑, 是否继续?<p><span style="color:red;font-weight: bold;">提示：</span>请在编辑报表后在报表编辑页面执行解锁操作', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                dangerouslyUseHTMLString:true,
                type: 'warning'
              }).then(() => {
                $this.BaseRequest({
                  url:"/report/lockReport",
                  method:"get",
                  params:{reportId:this.reportId}
                }).then(response=>{
                  doEdit()
                }).catch(error=>{
                  $this.$alert('锁定报表过程出现异常,无法继续修改,请联系系统管理员或稍后再试。', '提示', {});
                })

              }).catch(() => {
              });
            }
          })
        }


      },
      reportStatusFormatter(row,column){
        return this.reportStatusArray[row.reportStatus]
      }
    },
    mounted:function(){
      this.menuDataList = []
      this.getTableData(1)
      this.initTemplates()
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
