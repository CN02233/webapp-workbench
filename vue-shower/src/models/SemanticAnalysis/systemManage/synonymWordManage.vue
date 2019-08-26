<template>
  <div :headerItems="['语义知识库','同义词管理']">
    <el-row class="search-row" :gutter="20">
      <el-col :span="6">
        <el-input v-model="wordNameForSearch" placeholder="输入词条"></el-input>
      </el-col>
      <el-col class="align-left" :span="17">
        <el-button @click="getTableData()">查询</el-button>
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="wordDataList"
          style="width: 100%">
          <el-table-column
            prop="id"
            align="left"
            label="编号">
          </el-table-column>
          <el-table-column
            prop="term"
            align="left"
            label="同义词组">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
              <el-button type="text" @click="delWord(scope.row)" size="small">删除</el-button>
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
    <el-dialog :title="modalPageTitle" :visible.sync="showModalPage" >
      <el-form class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item label="编号" >
          <el-input :disabled="isEditModal" v-model="formData.id" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item  label="同义词组" >
          <el-input v-model="formData.term" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
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
    name: 'SynonymWordManage',

    data() {
      return {
        wordDataList:[],
        wordNameForSearch:'',
        tableDataUrl:'manage/dict_synonym',
        currPageNum:1,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        editFormData:{
          term:'',
          id:''
        },
        addFormData:{
          term:'',
          id:''
        }
      }
    },
    validations:{
      formData:{
        term:{
          required
        },
        id:{
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
          return "修改同义词"
        }else
          return "新增同义词"
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
          params:{page:pageNum,pagesize:10,search:this.wordNameForSearch||""}
        }).then(reponse=>{
          $this.totalPage = reponse.total
        $this.refreshTableList(reponse.result)
      })
      },
      refreshTableList:function(dataList){
        this.wordDataList = dataList
      },
      delWord:function(editWord){
        this.Request({
          url:this.tableDataUrl,
          method:'delete',
          data:{"id":editWord.id}
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
      },
      subWordForm:function(){
        if(this.checkInputNull()){
          //DO NOTHING
          return
        }

        let sendData = this.addFormData
        if(this.isEditModal){
          sendData = this.editFormData
          this.Request({
            url:this.tableDataUrl,
            method:'put',
            data:sendData
          }).then(response=>{
            if(response.status!='0'){
              this.Message.error(response.message)
            }else{
              this.Message.success("保存成功")
            }

            this.getTableData()
            this.closeModal();

        })

        }else{
          this.Request({
            url:this.tableDataUrl,
            method:'post',
            data:sendData
          }).then(response=>{
            if(response.status!='0'){
              this.Message.error(response.message)
            }else{
              this.Message.success("保存成功")
            }
            this.closeModal()
            this.getTableData()
          })
        }

      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>编号、同义词组'
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
