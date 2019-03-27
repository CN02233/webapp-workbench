<template>
  <WorkMain :headerItems="['用户管理','用户列表']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>

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
            align="center">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">编辑</el-button>
              <el-button type="text" @click="openAuthModal(scope.row)" size="small">权限</el-button>
              <el-button type="text" @click="delUser(scope.row)" size="small">删除</el-button>
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
      <el-form  class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <!--
        <el-form-item :size="small" label="用户登陆名" >
          <el-input   auto-complete="off" ></el-input>
        </el-form-item>
        -->
        <el-form-item :size="small" label="用户名称" >
          <el-input v-model="formData.user_name" auto-complete="off" ></el-input>
        </el-form-item>

        <el-form-item :size="small" label="行政机构" >
          <el-select v-model="formData.organization_id" style="width:100%;" placeholder="请选择行政机构">
            <el-option :key="administrativeData.organization_id" v-for="administrativeData in administrativeList" :label="administrativeData.organization_name" :value="administrativeData.organization_id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="submitDataForm()">确 定</el-button>
      </div>
    </el-dialog>

    <!--用户权限设置-->
    <el-dialog title="用户权限" :visible.sync="showUserAuth" >
      <el-form  class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item :size="small" label="用户名称" >
          <el-input :disabled="true"  :value="formData.user_name" auto-complete="off" ></el-input>
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
  </WorkMain>
</template>

<script>
import WorkTablePager from '@/models/public/WorkTablePager'
import WorkMain from '@/models/public/WorkMain'
import { required } from 'vuelidate/lib/validators'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'UserMain',
  data () {
    return {
      userDataList: [],
      originList: [],
      userType: 2,
      tableDataUrl: 'sys/user/listUserPage',
      currPageNum: 1,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      showUserAuth: false,
      showOrigin: false,
      allRoleList: [],
      user_role_id: '',
      administrativeList: [],
      organization_id: '',
      formData: {
        user_id: null,
        user_name: null,
        organization_id: null
      },
      options: []
    }
  },
  validations: {
    formData: {
      user_name: {
        required
      },
      organization_id: {
        required
      }
    }
  },
  computed: {
    modalPageTitle () {
      if (this.isEditModal) {
        return '修改用户'
      } else { return '新增用户' }
    }
  },
  components: {
    Treeselect,
    WorkTablePager,
    WorkMain
  },
  methods: {
    getTableData: function (pageNum) {
      if (pageNum && pageNum !== '') {
        this.currPageNum = pageNum
      } else {
        pageNum = this.currPageNum
      }
      const $this = this
      this.BaseRequest({
        url: this.tableDataUrl,
        method: 'get',
        params: {currPage: pageNum, pageSize: 10, userType: this.userType}
      }).then(reponse => {
        $this.totalPage = reponse.totalPage
        $this.refreshTableList(reponse.dataList)
      })
    },
    getOriginList () { // 弹出model触发、获取机构树状展示
      this.BaseRequest({
        url: 'submitAU/listAllSubmitauthority',
        method: 'get'
      }).then(response => {
        if (response != null && response.length > 0) {
          this.data = []
          this.options = response
          this.data = response
        }
      })
    },
    refreshTableList: function (dataList) {
      this.userDataList = dataList
    },
    delUser: function (row) {
      this.$confirm('确定删除用户【' + row.user_name + '】？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true,
        type: 'warning'
      }).then(() => {
        this.BaseRequest({
          url: 'sys/user/delUserByUserId',
          method: 'get',
          params: {'user_id': row.user_id}
        }).then(() => {
          this.Message.success('删除成功')
          this.getTableData()
        })
      }).catch(() => {
      })
    },
    openAddModal: function () {
      this.clearData()
      this.showModalPage = true
      this.isEditModal = false
    },
    clearData: function () {
      this.formData = {
        user_id: null,
        user_name: null,
        organization_id: null
      }
    },
    openEditModal: function (row) { // user edit
      this.clearData()
      this.showModalPage = true
      this.isEditModal = true
      this.formData.user_name = row.user_name
      this.formData.user_id = row.user_id
      // set options value
      // select current user relation to  organizationId
      this.BaseRequest({
        url: 'administrative/getOrganizationByUser',
        method: 'get',
        params: {'userId': row.user_id}
      }).then(reponse => {
        this.formData.organization_id = reponse.organization_id
      })
    },
    submitDataForm: function () { // user submit
      if (this.checkInputNull()) {
        return
      }
      if (this.isEditModal) { // update
        this.BaseRequest({
          url: 'sys/user/updateSaveUser',
          method: 'POST',
          data: {'user_name': this.formData.user_name, 'user_id': this.formData.user_id, 'user_type': this.userType}
        }).then(() => {
          this.Message.success('保存成功')
          // add user——organization relation
          this.changeOrigin()
          this.getTableData()
          this.closeModal()
        })
      } else { // insert
        this.BaseRequest({
          url: 'sys/user/saveNewUser',
          method: 'get',
          params: {'user_name': this.formData.user_name, 'user_type': this.userType}
        }).then((response) => {
          this.Message.success('保存成功')
          // add user——organization relation
          this.formData.user_id = response.user_id
          this.changeOrigin()
          this.getTableData()
          this.closeModal()
        })
      }
    },
    changeOrigin () {
      this.BaseRequest({
        url: 'administrative/userOrganizationSave',
        method: 'get',
        params: {'userId': this.formData.user_id, 'organizationId': this.formData.organization_id}
      }).then(response => {
        this.Message.success('保存成功')
        this.closeModal()
      })
    },
    openAuthModal: function (row) { // edit
      const $this = this
      this.showUserAuth = true
      // this.formData = row
      this.formData.user_id = row.user_id
      this.formData.user_name = row.user_name

      this.BaseRequest({
        url: 'sys/userRole/getRoleByUserId',
        method: 'get',
        params: {user_id: this.formData.user_id}
      }).then(response => {
        if (response != null && response.length > 0) {
          // set value for update
          $this.user_role_id = response[0]['user_role_id']
          $this.formData.user_role_id = response[0]['user_role_id']
        }
      })
    },
    saveUserRole: function () { // 权限保存修改
      this.BaseRequest({
        url: 'sys/userRole/updateUserRole',
        method: 'get',
        params: {user_id: this.formData.user_id, user_role_id: this.user_role_id, old_user_role_id: this.formData.user_role_id}
      }).then(() => {
        this.Message.success('保存成功')
        this.showUserAuth = false
      })
    },
    closeModal: function () {
      this.showModalPage = false
      this.isEditModal = false
    },
    closeUserAuthModal: function () {
      this.showUserAuth = false
    },

    checkInputNull () {
      const checkResult = this.$v.$invalid
      if (checkResult) {
        this.$notify({
          dangerouslyUseHTMLString: true,
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>用户名称、所属机构'
        })
      }
      return checkResult
    }
  },
  mounted: function () {
    this.userDataList = []
    this.getTableData(1)
    const $this = this
    this.getOriginList()
    this.BaseRequest({
      url: 'sys/role/rolePageData',
      method: 'get',
      params: {currPage: 1, pageSize: 100}
    }).then(response => {
      $this.allRoleList = response.dataList
    })
    // get administrativeList
    this.BaseRequest({
      url: '/administrative/listAdministrative',
      method: 'get',
      params: {currPage: 1, pageSize: 100}
    }).then(response => {
      console.log(response)
      $this.administrativeList = response.dataList
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
