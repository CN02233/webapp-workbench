<template>
  <div :headerItems="['系统管理','数据源管理']">
    <el-row class="search-row" :gutter="20">
      <!--<el-col :span="6">-->
        <!--<el-input v-model="wordNameForSearch" placeholder="输入词条"></el-input>-->
      <!--</el-col>-->
      <el-col class="align-left" :span="17">
        <!--<el-button @click="getTableData">查询</el-button>-->
        <el-button @click="openAddModal" type="primary">新增数据源</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="dataSourceList"
          style="width: 100%">
          <el-table-column
            prop="db"
            align="left"
            label="数据源名称">
          </el-table-column>
          <el-table-column
            prop="type"
            align="left"
            label="数据库类型">
          </el-table-column>
          <el-table-column
            prop="host"
            align="left"
            label="服务器地址">
          </el-table-column>
          <el-table-column
            prop="user"
            align="left"
            label="用户名">
          </el-table-column>
          <el-table-column
            prop="password"
            align="left"
            label="密码">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
              <el-button type="text" @click="delWord(scope.row.id)" size="small">删除</el-button>
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
        <el-form-item label="数据源名称" >
          <el-input v-model="formData.db" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="数据源类型" >
          <el-select style="width:100%" v-model="formData.type" placeholder="请选择数据源类型">
            <el-option label="mysql" value="mysql"></el-option>
          </el-select>

        </el-form-item>
        <el-form-item label="数据源地址" >
          <el-input v-model="formData.host" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="用户名" >
          <el-input v-model="formData.user" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" >
          <el-input v-model="formData.password" auto-complete="off"></el-input>
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
    name: 'DataSourceManage',

    data() {
      return {
        dataSourceList:[],
        tableDataUrl:'manage/datasource',
        currPageNum:1,
        showModalPage:false,
        isEditModal:false,
        editFormData:{
          db:'',
          type:'',
          host:'',
          user:'',
          password:''
        },
        addFormData:{
          db:'',
          type:'',
          host:'',
          user:'',
          password:''
        }
      }
    },
    validations:{
      formData:{
        db:{
          required
        },
        type:{
          required
        },
        host:{
          required
        },
        user:{
          required
        },
        password:{
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
          return "修改数据源"
        }else
          return "新增数据源"
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
          params:{page:pageNum,pagesize:10}
        }).then(reponse=>{
          if(reponse.status!='0'){
                this.Message.error(reponse.message)
          }else{
              $this.refreshTableList(reponse.result)

            }
      })
      },
      refreshTableList:function(dataList){
        this.dataSourceList = dataList
      },
      delWord:function(editWordKey){
        this.Request({
          url:this.tableDataUrl,
          method:'delete',
          data:{id:editWordKey}
        }).then(reponse=>{
          this.Message.success("删除成功")
          this.getTableData()
        })
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal:function(editWordKey){
        console.log(JSON.stringify(editWordKey))
        this.showModalPage = true
        this.isEditModal = true
        this.editFormData = editWordKey
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
        let sendUrl = "manage/datasource"
        if(this.isEditModal){
          sendData = this.editFormData
//          this.$http.put(process.env.BASE_API+"/"+sendUrl,{data:{sendData}})
//          this.$http.put(process.env.BASE_API+"/"+sendUrl,{headers:{'Content-Type': 'application/json'},data:{sendData}})

          this.Request({
            url:sendUrl,
            method:'put',
            data:sendData
          }).then(response=>{
            if(response.status!='0'){
              this.Message.error(response.message)
            }else{
              this.Message.success("保存成功")
              this.closeModal()
              this.getTableData()
            }
          })

        }else{
          this.Request({
            url:sendUrl,
            method:'post',
            data:sendData
          }).then(response=>{
            if(response.status!='0'){
              this.Message.error(response.message)
            }else{
              this.Message.success("保存成功")
              this.closeModal()
              this.getTableData()
            }
          })
        }
      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span>' +
            '<br>数据源名称、数据源类型、数据源地址、用户名、密码'
          })
        }
        return checkResult
      }
    },
    mounted:function(){
      this.dataSourceList = []
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
