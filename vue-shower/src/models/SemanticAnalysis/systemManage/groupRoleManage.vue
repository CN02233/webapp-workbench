<template>
  <div :headerItems="['系统管理','分类规则管理']">
    <el-row class="search-row" :gutter="20">
      <!--<el-col :span="6">-->
      <!--<el-input v-model="wordNameForSearch" placeholder="输入词条"></el-input>-->
      <!--</el-col>-->
      <el-col class="align-left" :span="17">
        <!--<el-button @click="getTableData">查询</el-button>-->
        <el-button @click="openAddModal" type="primary">新增分类规则</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="groupRoleList"
          style="width: 100%">
          <el-table-column
            prop="group"
            align="left"
            label="分类组">
          </el-table-column>
          <el-table-column
            prop="type"
            align="left"
            label="分类名称">
          </el-table-column>
          <el-table-column
            prop="file"
            align="left"
            label="分类规则文件">
            <template  slot-scope="scope">
              <div style="cursor: pointer" @click="downloadFile(scope.row.file)">
                <el-tag  type="success">{{ scope.row.file }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="expression"
            align="left"
            label="分类规则表达式">
          </el-table-column>
          <el-table-column
            prop="create"
            align="left"
            label="创建日期">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <!--<el-button type="text" @click="openEditModal(scope.row.wordName)" size="small">编辑</el-button>-->
              <el-button type="text" @click="delGroup(scope.row)" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
    <WorkTablePager @refreshData="getTableData"
                    :pageCount="totalPage">
    </WorkTablePager>

    <div style="width: 0;height: 0;">
      <a href="javascript:;" style="color:white;"></a>
    </div>
    <!-- 新增、编辑 弹窗-->
    <el-dialog :title="modalPageTitle" :visible.sync="showModalPage" >
      <el-form class="modal-form" :label-position="left" label-width="30%" :model="formData">
        <el-form-item label="分类组" >
          <el-input v-model="formData.group" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类名称" >
          <el-input v-model="formData.type" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类规则表达式" >
          <el-input v-model="formData.expression" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类规则文件" >
          <el-input :disabled="true" size="small" placeholder="请输入文件路径" v-model="uploadFilePath" class="input-with-select">
            <!--<el-button slot="append" icon="el-icon-search"></el-button>-->
            <el-upload slot="append"
                       :auto-upload = 'false'
                       :show-file-list="false"
                        :on-change="fileChange"
              ref="uploadFile">
              <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            </el-upload>
          </el-input>

        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <span style="color:red;">分类规则表达式支持and or not逻辑表达式,支持* ? +正则表达式。例如：通州？and 昌平*</span>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="subWordForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'GroupRoleManage',

    data() {
      return {
        groupRoleList:[],
        tableDataUrl:'manage/rule',
        currPageNum:1,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        uploadFilePath:'',
        uploadFile:null,
        editFormData:{
          group:'',
          type:'',
          file:null,
          expression:''
        },
        addFormData:{
          group:'',
          type:'',
          file:null,
          expression:''
        }
      }
    },
    validations:{
      formData:{
        group:{
          required
        },
        type:{
          required
        },
        file:{
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
      },
      modalPageTitle(){
        if(this.isEditModal){
          return "修改分类规则"
        }else
          return "新增分类规则"
      }
    },
    components: {
      WorkTablePager
    },
    methods: {
      getTableData:function(pageNum){
        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this
        this.Request({
          url:this.tableDataUrl,
          method:"get",
          params:{page:pageNum,pagesize:10}
        }).then(reponse=>{
          $this.totalPage = reponse.total
          $this.refreshTableList(reponse.result)
      })
      },
      refreshTableList:function(dataList){
        this.groupRoleList = dataList
      },
      delGroup:function(groupData){
        this.Request({
          url:this.tableDataUrl,
          method:'delete',
          data:{id:groupData.id,type:groupData.type}
        }).then(reponse=>{
          this.Message.success("删除成功")
        this.getTableData()

      })
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal:function(editWord){
        this.showModalPage = true
        this.isEditModal = true
        this.editFormData = editWord
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
        Object.keys(this.formData).forEach(objKey=>{
            this.formData[objKey] = ""
        })
        this.uploadFilePath = ""
      },
      subWordForm:function(){
        if(this.checkInputNull()){
          //DO NOTHING
          return
        }

        let sendData = this.addFormData
        let formDataKeys = Object.keys(sendData)

        if(this.isEditModal){
          sendData = this.editFormData
          formDataKeys = Object.keys(sendData)

          const formData = new FormData()
          formDataKeys.forEach(dataKey=>{
            formData.append(dataKey,sendData[dataKey])
          })

          this.Request({
            url:this.tableDataUrl,
            method:'put',
            data:formData
          }).then(response=>{
            this.Message.success("保存成功")
            this.closeModal();
            this.getTableData()
          })
        }else{
          const formData = new FormData()
          formDataKeys.forEach(dataKey=>{
              formData.append(dataKey,sendData[dataKey])
          })

          this.Request({
            url:this.tableDataUrl,
            method:'post',
            data:formData
          }).then(response=>{
            this.Message.success("保存成功")
            this.closeModal();
            this.getTableData()
          })
        }

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

        if(this.isEditModal) {
          this.editFormData.file = fileList[fileList.length-1].raw
        }else{
          this.addFormData.file = fileList[fileList.length-1].raw
        }
      },
      downloadFile(fileName){
        let iframe = document.createElement('iframe')
        iframe.style.display = 'none'
        iframe.src = process.env.BASE_API+"/nlpdownload/rule/"+fileName
        iframe.onload = function () {
          document.body.removeChild(iframe)
        }
        document.body.appendChild(iframe)
      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>分类组、分类名称、分类规则文件'
          })
        }
        return checkResult
      }
    },

    mounted:function(){
      this.getTableData(1)
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
