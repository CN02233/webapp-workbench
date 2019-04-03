<template>
  <WorkMain :headerItems="['报送管理','报送报表']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="definedDataList"
          style="width: 100%">
          <el-table-column
            prop="defined_name"
            align="left"
            width="150"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="status"
            align="left"
            width="150"
            label="状态">
          </el-table-column>
          <el-table-column
            prop="origin_name"
            align="left"
            width="230"
            label="所属机构">
          </el-table-column>
          <el-table-column
            prop="create_time"
            align="left"
            width="180"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="user_name"
            align="left"
            width="100"
            label="创建人">
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            width="250"
            >
            <template slot-scope="scope">
              <el-button
                size="mini" @click="definedUnit(scope.row.defined_id)"
                >报送单元</el-button>
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
      <el-row :gutter="16">
        <el-col :sm="20">
          <el-row>
            <el-col :span="8" :offset="1">报表名称</el-col>
            <el-col :span="15">
              <el-input placeholder="报送单元名称" v-model="formSubmitData.defined_name" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8" :offset="1">所属报送机构</el-col>
            <el-col :span="15">
               <!-- <treeselect   multiple v-model="formSubmitData.origin_id"  :options="options" />-->
                <el-tree
                  accordion
                  class="filter-tree"
                  :data="data"
                  show-checkbox
                  :props="defaultProps"
                  node-key = "id"
                  ref="tree"
                  :filter-node-method="filterNode"
                  @node-click="handleNodeClick">
                </el-tree>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8" :offset="1">报表状态</el-col>
            <el-col :span="8" >
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
  name: 'OriginMain',
  data () {
    return {
      definedDataList: [],
      definedDataObjs: {},
      tableDataUrl: 'reportStatements/listReportStatements',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      isEditModal: false,
      dialogTitle: '',
      origin_ids: [],
      formSubmitData: {
        defined_id: null,
        defined_name: null,
        origin_name: null,
        status: null
      },
      options: [],
      statusOptions: [{
        value: '100',
        label: '编辑中'
      }, {
        value: '200',
        label: '已发布'
      }, {
        value: '300',
        label: '已使用'
      }],
      search: '',
      defaultProps: {
        id: 'id',
        children: 'children',
        label: 'label'
      },
      filterText: '',
      data: []
    }
  },
  watch: {// 监听节点搜索的内容
    filterText (val) {
      this.$refs.tree.filter(val)
    }
  },
  validations: {// 提交前的验证
    formSubmitData: {
      defined_name: {
        required
      },
      status: {
        required
      }
    },
    origin_ids: {
      required
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
    handleNodeClick (data) { // 点击树的节点进行赋值
      // console.log(data)
      // console.log(this.$refs.tree.getCheckedNodes())
    },
    filterNode (value, data) { // 树节点的过滤
      if (!value) return true
      return data.name.indexOf(value) !== -1
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
        params: {currPage: pageNum, pageSize: this.eachPageNum}
      }).then(response => {
        if (response.dataList != null) {
          response.dataList.forEach(definedObj => {
            $this.definedDataObjs[definedObj.organization_id] = definedObj
          })
        }
        $this.definedDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.definedDataList = dataList
    },
    openAddModal: function () {
      this.clearData()
      this.dialogTitle = '新增报送报表'
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
          this.options = response
          this.data = response
        }
      })
    },
    getDefinedAndOriginAssign (definedId, thisRef) { // 获取选择的机构id
      thisRef.$refs.tree.setCheckedKeys([])
      this.BaseRequest({
        url: '/reportStatements/getDefinedAndOriginAssignById',
        method: 'get',
        params: {'definedId': definedId}
      }).then(response => {
        if (response != null && response.length > 0) {
          thisRef.$refs.tree.setCheckedKeys(response)
          // this.data = []
          // this.options = response
          // this.data = response
        }
      })
    },
    getTreeNode () {
      this.origin_ids = []
      let nodeData = this.$refs.tree.getCheckedNodes()
      for (let i = 0; i < nodeData.length; i++) {
        this.origin_ids.push(nodeData[i].id)
      }
      JSON.stringify(this.origin_ids)
    },
    handleInsert () { // 添加、修改确定按钮触发
      this.getTreeNode()
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
        url: '/reportStatements/addReportStatements',
        method: 'post',
        data: this.formSubmitData
      }).then((response) => {
        this.Message.success('保存成功')
        this.formSubmitData.defined_id = response.defined_id
        this.saveAssign()
        loading.close()
        this.closeModal()
        this.getTableData()
      }).catch(error => {
        console.log(error)
        loading.close()
        this.Message.error('保存失败' + error)
      })
    },
    saveAssign () {
      this.BaseRequest({
        url: 'reportStatements/saveDefinedAndOriginAssign',
        method: 'get',
        params: {'definedId': this.formSubmitData.defined_id, 'originIds': this.origin_ids.join()}
      }).then(() => {
        this.Message.success('保存成功')
        this.closeModal()
      })
    },
    handleEdit (index, row) { // 修改
      this.getOriginList()
      this.dialogTitle = '修改报送报表'
      this.showModalPage = true
      this.isEditModal = true
      this.formSubmitData.defined_id = row.defined_id
      this.formSubmitData.defined_name = row.defined_name
      this.formSubmitData.status = row.status
      this.getDefinedAndOriginAssign(row.defined_id, this)
    },
    clearData () { // 每次添加之前清空数据、
      /* //this.formSubmitData= {};
        // this.formSubmitData.defined_status= '';
        // this.formSubmitData.parent_defined_id= '';
        // this.formSubmitData.parent_defined_name= ''; */
      this.$refs.tree.setCheckedKeys([]);
      this.formSubmitData = {
        defined_id: null,
        defined_name: null,
        status: null,
        origin_name: null
      }
    },
    handleDelete (index, row) { // 删除
      this.$confirm('确定删除报送单元【' + row.defined_name + '】？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true,
        type: 'warning'
      }).then(() => {
        this.BaseRequest({
          url: '/reportStatements/delById',
          method: 'get',
          params: {'definedId': row.defined_id}
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
    definedUnit(definedId){
      this.$router.push({
        path: '/record/reportUnit',
        query:{
          'definedId':definedId
        }
      });
    }
  },
  mounted: function () { // 初始化
    this.definedDataList = []
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
