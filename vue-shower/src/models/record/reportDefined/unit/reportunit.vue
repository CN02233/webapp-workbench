<template>
  <WorkMain :headerItems="['报送管理','报送单元']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left table-button-group" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
        <el-button @click="$router.go(-1)" type="warning">返回</el-button>
      </el-col>
    </el-row>
    <el-row class="table-page-root">
      <el-col :span="24">
        <el-table
          :data="unitDataList"
          row-class-name="mini-font-size" stripe
          style="width: 100%">
          <el-table-column
            prop="unit_name"
            align="left"
            label="报送单元名称">
          </el-table-column>
          <el-table-column
            prop="status"
            align="left"
            label="报送单元状态"
            :formatter="formatStatus"
          >
          </el-table-column>
          <!--<el-table-column
            prop="origin_name"
            align="left"
            label="所属报送机构">
          </el-table-column>-->
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
            fixed="right"
            label="操作"
            align="left"
            width="230"
            >
            <template slot-scope="scope">
              <div v-if="isView=='N'">
                <el-button
                  size="mini"
                  @click="definedUnit(scope.row.unit_id,scope.row.unit_type)"
                >录入项</el-button>
                <el-button
                  size="mini"
                  @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  @click="handleDelete(scope.$index, scope.row)">删除</el-button>
              </div>
              <div v-if="isView=='Y'">
                <el-button
                  size="mini"
                  @click="definedUnit(scope.row.unit_id,scope.row.unit_type)"
                >查看录入项</el-button>
              </div>
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
      <el-row :gutter="16">

        <el-col :sm="12">
          <el-row>
            <el-col :span="8" :offset="1">报送单元名称</el-col>
            <el-col :span="15">
              <el-input placeholder="报送单元名称" v-model="formSubmitData.unit_name" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <!--<el-row>
            <el-col :span="8" :offset="1">所属报送机构</el-col>
            <el-col :span="15">
              <div id="app">
                <treeselect v-model="formSubmitData.origin_id"  :options="options" />
              </div>
            </el-col>
          </el-row>-->
          <el-row>
            <el-col :span="8" :offset="1">报送单元状态</el-col>
            <el-col :span="15">
              <el-select v-model="formSubmitData.status" placeholder="请选择报送单元状态">
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8" :offset="1">报送单元类型</el-col>
            <el-col :span="15">
              <el-select v-model="formSubmitData.unit_type" placeholder="请选择报送单元类型">
                <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
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
  name: 'ReportUnit',
  data () {
    return {
      definedId: '',
      isView:'N',//Y:只读 N:非自读 可编辑
      unitDataList: [],
      unitDataObjs: {},
      tableDataUrl: 'reportUnit/listReportUnit',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: '',
      formSubmitData: {
        unit_id: null,
        unit_name: null,
        origin_name: null,
        status: null,
        unit_type: null
      },
      options: [],
      statusOptions: [{
        value: '1',
        label: '正常'
      }, {
        value: '0',
        label: '停用'
      }, {
        value: '9',
        label: '注销'
      }],
      typeOptions: [{
        value: 1,
        label: '一维静态'
      }, {
        value: 2,
        label: '一维动态'
      }, {
        value: 3,
        label: '多维静态'
      }, {
        value: 4,
        label: '树状动态'
      }]
    }
  },
  validations: {// 提交前的验证
    formSubmitData: {
      unit_name: {
        required
      },
      status: {
        required
      },
      unit_type: {
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
    formatStatus: function (row, column) {
      if (row.status === '1') {
        return '正常'
      }
      if (row.status === '0') {
        return '停用'
      }
      if (row.status === '9') {
        return '注销'
      }
    },
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
        params: {currPage: pageNum, pageSize: this.eachPageNum,definedId:this.definedId}
      }).then(response => {
        if (response.dataList != null) {
          response.dataList.forEach(unitObj => {
            $this.unitDataObjs[unitObj.organization_id] = unitObj
          })
        }
        $this.unitDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.unitDataList = dataList
    },
    openAddModal: function () {
      this.clearData()
      this.dialogTitle = '新增报送单元'
      // this.getOriginList()
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
        url: '/reportUnit/addReportUnit',
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
      this.clearData()
      this.dialogTitle = '修改报送单元'
      this.showModalPage = true
      this.isEditModal = true
      this.formSubmitData.report_defined_id = row.report_defined_id
      this.formSubmitData.unit_id = row.unit_id
      this.formSubmitData.unit_name = row.unit_name
      // this.formSubmitData.origin_id = row.origin_id
      this.formSubmitData.status = row.status
      console.log(row)
      this.formSubmitData.unit_type = row.unit_type
    },
    clearData () { // 每次添加之前清空数据、
      /* //this.formSubmitData= {};
        // this.formSubmitData.unit_status= '';
        // this.formSubmitData.parent_unit_id= '';
        // this.formSubmitData.parent_unit_name= ''; */
      this.formSubmitData = {
        report_defined_id: this.definedId,
        unit_id: null,
        unit_name: null,
        unit_type: null,
        status: null,
        origin_name: null
      }
    },
    handleDelete (index, row) { // 删除
      this.$confirm('确定删除报送单元【' + row.unit_name + '】？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true,
        type: 'warning'
      }).then(() => {
        this.BaseRequest({
          url: '/reportUnit/delById',
          method: 'get',
          params: {'unitId': row.unit_id, 'unitType': row.unit_type}
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
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>报送单元名称、所属报送机构、状态'
        })
      }
      return checkResult
    },
    definedUnit (unitId, defined_type) { // add by SongChaoqun 编辑该单元的报送项内容
      let editUrl = ''
      if (defined_type == '1') {
        editUrl = '/record/reportDefined/oneDimensionsStatic'
      } else if (defined_type == '2') {
        editUrl = '/record/reportDefined/oneDimensionsDynamic'
      } else if (defined_type == '3') {
        editUrl = '/record/reportDefined/multDimensionsStatic'
      } else if (defined_type == '4') {
        editUrl = '/record/reportDefined/treeMultDiensionsDynamic'
      } else {
        return
      }
      this.$router.push({
        path: editUrl,
        query: {
          'unitId': unitId,
          'isView':this.isView
        }
      })
    }
  },
  mounted: function () { // 初始化
    this.definedId = this.$route.query.definedId
    if(this.$route.query.isView){
      this.isView = this.$route.query.isView
    }
    this.unitDataList = []
    this.getTableData(1)
    // this.getOriginList()
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
</style>
