<template>
  <div class="table-page-root">

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="userDataList"
          style="width: 100%">
          <el-table-column
            prop="user_id"
            align="left"
            label="用户ID">
          </el-table-column>
          <el-table-column
            prop="user_name"
            align="left"
            label="用户名称">
          </el-table-column>
          <el-table-column
            prop="reg_date"
            align="left"
            label="首次登入日期">
          </el-table-column>
          <el-table-column
            prop="last_login_time"
            align="left"
            label="最后登入日期">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
              <el-button type="text" @click="openAuthModal(scope.row)" size="small">权限</el-button>
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
        <el-form-item :size="small" label="用户名称" >
          <el-input v-model="formData.user_name" auto-complete="off" ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="subWordForm()">确 定</el-button>
      </div>
    </el-dialog>

    <!--用户权限设置-->
    <el-dialog title="用户权限" :visible.sync="showUserAuth" >
      <el-form ref="editForm" class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item :size="small" label="用户名称" >
          <el-input :disabled="true"  :value="editFormData.user_name" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item :size="small" label="角色" >
          <el-select v-model="user_role_id" style="width:100%;" placeholder="请选择角色">
            <el-option :key="roleData.user_role_id" v-for="roleData in allRoleList" :label="roleData.user_role_name" :value="roleData.user_role_id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeUserAuthModal">取 消</el-button>
        <el-button type="primary" @click="saveUserRole()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import { required } from 'vuelidate/lib/validators'

  export default {
    name: 'UserMain',

    data() {
      return {
        userDataList:[],
        termForSearch:'',
        tableDataUrl:'sys/user/listUserPage',
        currPageNum:1,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        showUserAuth:false,
        allRoleList:[],
        user_role_id:'',
        editFormData:{
          user_id:'',
          user_name:''
        },
        addFormData:{
          term:'',
          pos:'',
          freq:'1'
        }
      }
    },
    validations:{
      formData:{
        user_name:{
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
          return "修改用户"
        }else
          return "新增词条"
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
        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{currPage:pageNum,pageSize:10,search:this.termForSearch||""}
        }).then(reponse=>{
          $this.totalPage = reponse.totalPage
          $this.refreshTableList(reponse.dataList)
        })
      },
      refreshTableList:function(dataList){
        this.userDataList = dataList
      },
      delWord:function(editWordKey){
        this.BaseRequest({
          url:"manage/dict",
          method:'delete',
          data:Qs.stringify({term:editWordKey})
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
      saveUserRole:function(){
        this.BaseRequest({
          url:"sys/userRole/updateUserRole",
          method:'get',
          params:{user_id:this.editFormData.user_id,user_role_id:this.user_role_id,old_user_role_id:this.editFormData.user_role_id}
        }).then(response=>{
          this.Message.success("保存成功")
          this.showUserAuth = false
      })
      },
      openAuthModal:function(editWord){
        const $this = this
        this.showUserAuth = true
        this.editFormData = editWord
        this.BaseRequest({
          url:"sys/userRole/getRoleByUserId",
          method:'get',
          params:{user_id:this.editFormData.user_id}
        }).then(response=>{
          if(response!=null&&response.length>0){
//          user_role_id
            $this.user_role_id = response[0]["user_role_id"]
            $this.editFormData.user_role_id = response[0]["user_role_id"]
          }
        })

      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
        Object.keys(this.formData).forEach(objKey=>{
          this.formData[objKey] = ""
        })
      },
      closeUserAuthModal:function(){
        this.showUserAuth = false
      },
      subWordForm:function(){
        if(this.checkInputNull()){
          //DO NOTHING
          return
        }

        let sendData = this.addFormData
        let sendUrl = "sys/user/updateSaveUser"
        if(this.isEditModal){
          sendData = this.editFormData
//          this.$http.put(process.env.BASE_API+"/"+sendUrl,{data:{sendData}})
//          this.$http.put(process.env.BASE_API+"/"+sendUrl,{headers:{'Content-Type': 'application/json'},data:{sendData}})
          this.BaseRequest({
            url:sendUrl,
            method:'POST',
            data:{"user_name":sendData.user_name,"user_id":sendData.user_id}
          }).then(response=>{
            this.Message.success("保存成功")
            this.getTableData()
            this.closeModal();
        })

        }else{
          this.BaseRequest({
            url:sendUrl,
            method:'post',
            data:{"user_name":sendData.user_name,"user_id":sendData.user_id}
          }).then(response=>{
            this.Message.success("保存成功")
            this.getTableData()
          })
        }

      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>用户名称'
          })
        }
        return checkResult
      }
    },
    mounted:function(){
      this.userDataList = []
      this.getTableData(1)
      const $this = this
      this.BaseRequest({
        url:"sys/role/rolePageData",
        method:'get',
        params:{currPage:1,pageSize:100}
      }).then(response=>{
        $this.allRoleList = response.dataList
      })
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
