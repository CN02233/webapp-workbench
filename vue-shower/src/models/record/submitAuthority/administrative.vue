<template>
  <WorkMain :headerItems="['报送管理','报送机构管理']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="administrativeDataList"
          style="width: 100%">
          <el-table-column
            prop="organization_name"
            align="left"
            label="机构名称">
          </el-table-column>
          <el-table-column
            prop="organization_id"
            align="left"
            v-if="show"
            label="机构ID">
          </el-table-column>
          <el-table-column
            prop="create_time"
            align="left"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="user_name"
            align="left"
            label="创建人">
          </el-table-column>
          <el-table-column
            prop="origin_name"
            align="left"
            label="报送机构">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left"
            width="350"
            fixed="right"
            >
            <template slot-scope="scope">
              <el-button
                size="mini"
                >用户关联</el-button>
              <el-button
                size="mini"
                >报送机构</el-button>
              <el-button
                size="mini"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="showModalPage" >
      <el-form  class="modal-form" :label-position="left" label-width="20%" >
        <el-form-item :size="small" label="机构名称" >
          <el-input placeholder="行政机构名称" v-model="formSubmitData.organization_name" auto-complete="off" ></el-input>
        </el-form-item>
      </el-form>
      <!--
      <el-row :gutter="16">
        <el-col :sm="12">
          <el-row>
            <el-form-item :size="small" label="行政机构名称" >
              <el-input placeholder="行政机构名称" v-model="formSubmitData.organization_name" class="input-with-select" ></el-input>
            </el-form-item>
          </el-row>
          <el-row>
            <el-col :span="6" :offset="1">报送机构</el-col>
            <el-col :span="17">
              <div id="app">
                <treeselect v-model="formSubmitData.origin_id"  :options="options" />
              </div>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="handleInsert">确 定</el-button>
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
  name: 'OriginMain',
  data () {
    return {
      administrativeDataList: [],
      administrativeDataObjs: {},
      tableDataUrl: 'administrative/listAdministrative',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: '',
      formSubmitData: {
        organization_name: '',
        origin_id: null,
        origin_name: ''
      },
      options: []
    }
  },
  validations: {// 提交前的验证
    formSubmitData: {
      organization_name: {
        required
      }
    }
  },
  computed: {
    // 初始化加载
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
        params: {currPage: pageNum, pageSize: this.eachPageNum}
      }).then(response => {
        if (response.dataList != null) {
          response.dataList.forEach(administrativeObj => {
            $this.administrativeDataObjs[administrativeObj.organization_id] = administrativeObj
          })
        }
        $this.administrativeDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.administrativeDataList = dataList
    },
    openAddModal: function () {
      this.clearData()
      this.dialogTitle = '新增机构'
      this.getOriginList()
      this.showModalPage = true
      this.isEditModal = false
    },
    closeModal: function () {
      this.showModalPage = false
      this.isEditModal = false
    },
    getOriginList () { // 弹出model触发、获取机构树状展示
      this.BaseRequest({
        url: 'submitAU/listAllSubmitauthority',
        method: 'get'
      }).then(response => {
        if (response != null && response.length > 0) {
          this.data = []
          console.log(response)
          this.options = response
          this.data = response
        }
      })
    },
    handleInsert () { // 添加、修改确定按钮触发
      if (this.checkInputNull()) {
        return
      }
      const loading = this.$loading({
        lock: true,
        text: '保存中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.BaseRequest({
        url: '/administrative/addAdministrative',
        method: 'post',
        data: this.formSubmitData
      }).then(() => {
        this.Message.success('保存成功')
        loading.close()
        this.closeModal()
        this.getTableData()
      }).catch(error => {
        console.log(error)
        loading.close()
        this.Message.error('保存失败' + error)
      })
    },
    handleEdit (index, row) { // 修改
      this.dialogTitle = '修改机构'
      this.showModalPage = true
      this.isEditModal = true
      this.formSubmitData.organization_id = row.organization_id
      this.formSubmitData.origin_id = row.origin_id
      this.formSubmitData.origin_name = row.origin_name
      this.formSubmitData.organization_name = row.organization_name
    },
    clearData () { // 每次添加之前清空数据、
      /* //this.formSubmitData= {};
        // this.formSubmitData.administrative_status= '';
        // this.formSubmitData.parent_administrative_id= '';
        // this.formSubmitData.parent_administrative_name= ''; */
      this.formSubmitData = {
        organization_name: null,
        organization_id: null,
        origin_id: null,
        origin_name: null
      }
    },
    handleDelete (index, row) { // 删除
      this.$confirm('确定删除机构【' + row.organization_name + '】？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true,
        type: 'warning'
      }).then(() => {
        this.BaseRequest({
          url: '/administrative/delById',
          method: 'get',
          params: {'organizationId': row.organization_id}
        }).then(() => {
          this.Message.success('删除成功')
          this.getTableData()
        })
      }).catch(() => {
      })
    },
    checkInputNull () {
      const checkResult = this.$v.$invalid
      if (checkResult) {
        this.$notify({
          dangerouslyUseHTMLString: true,
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>机构名称、报送机构'
        })
      }
      return checkResult
    }
  },
  mounted: function () { // 初始化
    this.administrativeDataList = []
    this.getTableData(1)
    this.getOriginList()
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
