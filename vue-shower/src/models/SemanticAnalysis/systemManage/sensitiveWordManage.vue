<template>
  <div :headerItems="['系统管理','敏感词库设置']">
    <el-row class="search-row" :gutter="20">
      <el-col :span="6">
        <el-input v-model="seg" placeholder="输入敏感词"></el-input>
      </el-col>
      <el-col class="align-left" :span="17">
        <el-button @click="getTableData()">查询</el-button>
        <el-button @click="openAddModal" type="primary">新增敏感词</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="wordDataList"
          style="width: 100%">
          <el-table-column
            prop="name"
            align="left"
            label="名称">
          </el-table-column>
          <el-table-column
            prop="freg"
            align="left"
            label="频次">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">设置</el-button>
              <el-button type="text" @click="delWord(scope.row.name)" size="small">删除</el-button>
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
      <el-form ref="editForm" class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item label="名称" >
          <el-input v-model="formData.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="频次" >
          <el-input v-model="formData.freg" auto-complete="off"></el-input>
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
    name: 'SensitiveWordManage',

    data() {
      return {
        wordDataList:[],
        seg:'',
        tableDataUrl:'jiebaset',
        currPageNum:1,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        editFormData:{
          name:'',
          freg:''
        },
        addFormData:{
          name:'',
          freg:'1'
        }
      }
    },
    validations:{
      formData:{
        freg:{
          required
        },
        name:{
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
          return "修改词条"
        }else
          return "新增词条"
      }
    },
    components: {
      WorkTablePager
    },
    methods: {
      getTableData:function(pageNum){
//        if(this.seg==null||this.seg==''){
//          this.Message.success("请输入查询条件")
//          return
//        }

        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this
        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{pageindx:pageNum,pagenums:10,seg:this.seg||""}
        }).then(reponse=>{
          $this.totalPage = parseInt(reponse.pagecount)
          $this.refreshTableList(reponse.datalist)
        })
      },
      refreshTableList:function(dataList){
        this.wordDataList = dataList
      },
      delWord:function(editWordKey){
        this.BaseRequest({
          url:this.tableDataUrl,
          method:'delete',
          data:{seg:editWordKey}
        }).then(reponse=>{
          if(reponse.status=='0'){
            this.Message.success("删除成功")
          }else{
            this.Message.error("删除失败")
          }
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
          this.BaseRequest({
            url:this.tableDataUrl,
            method:'put',
            data:{"seg":sendData.name,"freg":sendData.freg}
          }).then(response=>{
            if(response.status=='0'){
              this.Message.success("保存成功")
            }else{
              this.Message.error("保存失败")
            }
            this.getTableData()
            this.closeModal();

          })

        }else{
          this.BaseRequest({
            url:"secadd",
            method:'put',
            data:{"seg":sendData.name,"freg":sendData.freg}
          }).then(response=>{
            if(response.status=='0'){
            this.Message.success("保存成功")
          }else{
            this.Message.error("保存失败")
          }
            this.getTableData()
            this.closeModal();

          })
        }

      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>名称、词频'
          })
        }
        return checkResult
      }
    },
    mounted:function(){
      this.wordDataList = []
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
    width:99%;
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
