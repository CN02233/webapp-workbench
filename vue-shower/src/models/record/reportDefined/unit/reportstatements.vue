<template>
  <WorkMain :headerItems="['报送管理','报送报表']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left table-button-group" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>
    <el-row class="table-page-root">
      <el-col :span="24">
        <el-table
          :data="reportDataList"
          header-row-class-name="table-header-style"
          row-class-name="mini-font-size" stripe
          style="width: 100%">
          <el-table-column
            prop="defined_name"
            align="left"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="status"
            align="left"
            label="状态"
            width="100"
            :formatter="formatStatus"
          >
          </el-table-column>
        <!--  <el-table-column
            prop="origin_name"
            align="left"
            width="230"
            label="所属机构">
          </el-table-column>-->
          <el-table-column
            prop="create_date"
            align="left"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="reportStartDate"
            align="left"
            label="报送起始日期">
          </el-table-column>
          <el-table-column
            prop="reportEndDate"
            align="left"
            label="报送截止日期">
          </el-table-column>
          <el-table-column
            prop="reportDataStart"
            align="left"
            label="填报区间开始日期">
          </el-table-column>
          <el-table-column
            prop="reportDataEnd"
            align="left"
            label="填报区间截止日期">
          </el-table-column>
          <!--<el-table-column-->
            <!--prop="user_name"-->
            <!--align="left"-->
            <!--label="创建人">-->
          <!--</el-table-column>-->
          <el-table-column
            fixed="right"
            label="操作"
            align="left"
            >
            <template slot-scope="scope">
              <el-button
                size="mini" v-if="scope.row.status==5" @click="getTableData(1)"
                >刷新状态</el-button>
              <el-button
                size="mini" v-if="scope.row.status==0" @click="definedUnit(scope.row.defined_id)"
                >报送单元</el-button>
              <el-button
                size="mini" v-if="scope.row.status==4" @click="viewDefinedUnit(scope.row.defined_id)"
              >报送单元查看</el-button>
              <el-button
                size="mini" v-if="scope.row.status==4" @click="viewSubDefined(scope.row.defined_id)"
              >查看</el-button>
              <el-button
                size="mini" v-if="scope.row.status==4" @click="viewReSubDefined(scope.row)"
              >补发</el-button>
              <el-button v-if="scope.row.status==0"
                size="mini"
                @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button v-if="scope.row.status==0"
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
              <el-button v-if="scope.row.status==0"
                size="mini"
                type="success"
                @click="openSubmitPrams(scope.row)">发布</el-button>
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
    <el-dialog class="table-options-modal" :title="dialogTitle" :visible.sync="showModalPage" >

      <el-form style="width:80%" ref="form" :model="form" label-width="200px">
        <el-form-item label="报表名称">
          <el-input size="small" placeholder="报送单元名称" v-model="formSubmitData.defined_name" class="input-with-select" ></el-input>
        </el-form-item>

        <el-form-item label="报表类型">
          <!--<el-select style="width:100%" @change="changeReportType" v-model="formSubmitData.report_type" placeholder="请选择报送类型">-->
          <el-select style="width:100%" v-model="formSubmitData.report_type" placeholder="请选择报送类型">
            <el-option
              v-for="item in originType"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所属报送机构">
          <el-tree
            accordion
            class="filter-tree"
            :data="treeData"
            show-checkbox
            :props="defaultProps"
            node-key = "id"
            ref="treeRef"
            :filter-node-method="filterNode">
          </el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="handleInsert">确 定</el-button>
      </div>
    </el-dialog>

    <!--发布设置弹窗-->
    <el-dialog class="table-options-modal" title="发布报表设置" :visible.sync="submitModel" >
      <el-row :gutter="16">
        <el-col :sm="20">
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报表定义编号</el-col>
            <el-col :span="15">
              <el-input placeholder="报表定义编号" :disabled="true" v-model="submitParams.defined_id" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
          <el-col :span="8" :offset="1">报表定义名称</el-col>
            <el-col :span="15">
              <el-input placeholder="报表定义名称" :disabled="true" v-model="submitParams.defined_name" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报送起始日期</el-col>
            <el-col :span="15">
              <el-date-picker style="width: 100%;"
                v-model="submitParams.report_start_date_str"
                type="date" value-format="yyyyMMdd"
                placeholder="选择报送起始日期">
              </el-date-picker>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报送截止日期</el-col>
            <el-col :span="15">
              <el-date-picker style="width: 100%;"
                v-model="submitParams.report_end_date_str"
                type="date" value-format="yyyyMMdd"
                placeholder="选择报送截止日期">
              </el-date-picker>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">填报区间开始日期</el-col>
            <el-col :span="15">
              <el-date-picker style="width: 100%;"
                              v-model="submitParams.report_data_start_str"
                              type="date" value-format="yyyyMMdd"
                              placeholder="选择填报区间开始日期">
              </el-date-picker>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">填报区间截止日期</el-col>
            <el-col :span="15">
              <el-date-picker style="width: 100%;"
                              v-model="submitParams.report_data_end_str"
                              type="date" value-format="yyyyMMdd"
                              placeholder="选择填报区间截止日期">
              </el-date-picker>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-row class="table-options-modal-item">

            <el-col :span="8" :offset="1">不需审核机构</el-col>
            <el-col align="left" :span="15">
              <!--defined_origins-->
              <el-tree
                :data="submitParams.defined_origins"
                :props="definedTreeProps"
                ref="approveCheckOrigins"
                show-checkbox
              >
              </el-tree>
            </el-col>
          </el-row>
            <el-col :span="8" :offset="1">不需复核机构</el-col>
            <el-col align="left" :span="15">
              <el-tree
                :data="submitParams.defined_origins"
                :props="definedTreeProps"
                ref="reviewCheckOrigins"
                show-checkbox >
              </el-tree>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeSubmitModal">取 消</el-button>
        <el-button type="primary" @click="submitReport()">发 布</el-button>
      </div>
    </el-dialog>


    <!--补发设置弹窗-->
    <el-dialog class="table-options-modal" title="补发报表设置" :visible.sync="reSubmitModel" >
      <el-row :gutter="16">
        <el-col :sm="20">
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报表定义名称</el-col>
            <el-col :span="15">
              <el-input placeholder="报表定义名称" :disabled="true" v-model="reSubmitParams.defined_name" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报送起始日期</el-col>
            <el-col :span="15">
              <el-input style="width: 100%;"  :disabled="true"
                              v-model="reSubmitParams.report_start_date_str">
              </el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">报送截止日期</el-col>
            <el-col :span="15">
              <el-input style="width: 100%;"  :disabled="true"
                              v-model="reSubmitParams.report_end_date_str">
              </el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">填报区间开始日期</el-col>
            <el-col :span="15">
              <el-input style="width: 100%;"  :disabled="true"
                              v-model="reSubmitParams.report_data_start_str">
              </el-input>
            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">填报区间截止日期</el-col>
            <el-col :span="15">
              <el-input style="width: 100%;" :disabled="true"
                              v-model="reSubmitParams.report_data_end_str">
              </el-input>
            </el-col>
          </el-row>

          <el-row class="table-options-modal-item">

              <el-col :span="8" :offset="1">不需审核机构</el-col>
              <el-col align="left" :span="15">
                <!--defined_origins-->
                <el-select style="width: 100%;" v-model="reSubmitParams.approve_check_origins" multiple placeholder="请选择">
                  <el-option
                    v-for="item in reSubmitParams.resubmit_checked_origins"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">不需复核机构</el-col>
            <el-col align="left" :span="15">
              <el-select style="width: 100%;" v-model="reSubmitParams.review_check_origins" multiple placeholder="请选择">
                <el-option
                  v-for="item in reSubmitParams.resubmit_checked_origins"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-col>
          </el-row>

          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">补发机构</el-col>
            <el-col :span="15">

              <el-tree
                accordion
                class="filter-tree"
                :data="reSubmitParams.resubmit_origin_tree"
                show-checkbox
                :props="defaultProps"
                node-key = "id"
                ref="reSubmitTreeRef"
                @check-change="handleNodeClick"
              >
              </el-tree>

            </el-col>
          </el-row>

        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reSubmitModel=false">取 消</el-button>
        <el-button type="primary" @click="submitForOrigins()">发 布</el-button>
      </div>
    </el-dialog>


    <el-dialog class="table-options-modal" title="已发布报表设置查看" :visible.sync="submitedModel" >
      <el-row :gutter="16">
        <el-col :sm="20">
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">不需审核机构</el-col>
            <el-col align="left" :span="15">
              <el-tag v-for="originName in submitedParams.passApproveOriginNames">{{originName}}</el-tag>

            </el-col>
          </el-row>
          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">不需复核机构</el-col>
            <el-col align="left" :span="15">
              <el-tag v-for="originName in submitedParams.passReviewOriginNames">{{originName}}</el-tag>

            </el-col>
          </el-row>

          <el-row class="table-options-modal-item">
            <el-col :span="8" :offset="1">发布到的机构</el-col>



            <el-col align="left" :span="15">

              <el-tree
                accordion
                class="filter-tree"
                :data="submitedParams.allSubOriginTree"
                :props="defaultProps"
                node-key = "id">
              </el-tree>

              <!--<el-tag v-for="originInfo in submitedParams.allSubOrigins">{{originInfo.originName}}</el-tag>-->
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submitedModel = false">关 闭</el-button>
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
  name: 'ReportDefind',
  data () {
    return {
      reportDataList: [],
      definedDataObjs: {},
      tableDataUrl: 'reportStatements/listReportStatements',
      currPageNum: 1,
      eachPageNum: 10,
      totalPage: 1,
      showModalPage: false,
      submitModel: false,
      reSubmitModel: false,
      submitedModel: false,
      isEditModal: false,
      dialogTitle: '',
      origin_ids: [],
      formSubmitData: {
        defined_id: null,
        defined_name: null,
        origin_name: null,
        report_type: null,
        status: 0
      },

      treeDataCache: [],
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
      treeData: [],
      submitParams: {
        defined_id: '',
        defined_name: '',
        report_start_date_str: '',
        report_end_date_str: '',
        report_data_start_str: '',
        report_data_end_str: '',
        approve_check_origins: [],
        review_check_origins: [],
        defined_origins: []
      },
      reSubmitParams: {
        defined_id: '',
        defined_name: '',
        report_start_date_str: '',
        report_end_date_str: '',
        report_data_start_str: '',
        report_data_end_str: '',
        resubmit_origin_tree:[],
        resubmit_checked_origins:[],
        approve_check_origins: [],
        review_check_origins: []
      },
      submitedParams:{
        reportStartDate: '',
        reportEndDate: '',
        reportDataStart: '',
        reportDataEnd: '',
        allSubOrigins: [],
        passApproveOriginNames: [],
        passReviewOriginNames: [],
        allSubOriginTree:[]
      },
      definedTreeProps:{
        children: 'children',
        label: 'origin_name',
        id: 'origin_id'
      },
      originType: [{
        value: '0',
        label: '全部'
      }, {
        value: '1',
        label: '燃气企业'
      }, {
        value: '2',
        label: '管输企业'
      }]
    }
  },
  watch: {// 监听节点搜索的内容
    filterText (val) {
      this.$refs.treeRef.filter(val)
    }
  },
  validations: {// 提交前的验证
    formSubmitData: {
      defined_name: {
        required
      },
      report_type: {
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
    formatStatus: function (row, column) {
      if (row.status === '0') {
        return '正常'
      }
      if (row.status === '1') {
        return '失效'
      }
      if (row.status === '2') {
        return '锁定'
      }
      if (row.status === '4') {
        return '已发布'
      }
      if (row.status === '5') {
        return '发布中'
      }
      return '未知'
    },
    handleNodeClick (data, checked, indeterminate) { // 点击树的节点进行赋值
      const $this = this


      function checkOrUncheckNode(checkData){
        if(checked){
          // this.resubmit_checked_origins
          const originId = checkData.id
          const orginName = checkData.label
          $this.reSubmitParams.resubmit_checked_origins.push({value:originId,label:orginName})
        }else{
          if($this.reSubmitParams.resubmit_checked_origins){
            const originId = checkData.id


            $this.reSubmitParams.resubmit_checked_origins.forEach((checkedOrigin,index)=>{
              if(checkedOrigin.value == originId){
                $this.reSubmitParams.resubmit_checked_origins.splice(index,1);
              }
            })

            $this.reSubmitParams.approve_check_origins.forEach((approveCheckedOrigin,index)=>{
              if(approveCheckedOrigin == originId){
                $this.reSubmitParams.approve_check_origins.splice(index,1)
              }
            })

            $this.reSubmitParams.review_check_origins.forEach((reviewCheckedOrigin,index)=>{
              if(reviewCheckedOrigin == originId){
                $this.reSubmitParams.review_check_origins.splice(index,1)
              }
            })
          }
        }
      }

      if(data.children){
        function checkSons(parentOrigin){
          if(parentOrigin.children){
            parentOrigin.children.forEach(childData=>{
              checkSons(childData)
            })
          }else{
            checkOrUncheckNode(parentOrigin)
          }
        }
        checkSons(data)
      }else{
        checkOrUncheckNode(data)
      }

      // console.log(this.$refs.treeRef.getCheckedNodes())
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
        $this.reportDataList = response.dataList
        $this.totalPage = response.totalPage
      })
    },
    refreshTableList: function (dataList) {
      this.reportDataList = dataList
    },
    openAddModal: function () {
      this.dialogTitle = '新增报送报表'
      // this.getOriginList()
      this.showModalPage = true
      this.isEditModal = false
      this.$nextTick(function () {
        this.$refs.treeRef.setCheckedKeys([])
        this.clearData()
      })
    },
    closeModal: function () {
      this.showModalPage = false
      this.isEditModal = false
    },
    closeSubmitModal: function () {
      this.submitModel = false
    },
    getOriginList () { // 弹出model触发、获取机构树状展示
      this.BaseRequest({
        url: 'submitAU/listAllSubmitauthority',
        method: 'get'
      }).then(response => {
        if (response != null && response.length > 0) {
          this.treeData = []
          this.treeData = response
        }
      })
    },
    getDefinedAndOriginAssign (definedId, thisRef) { // 获取选择的机构id
      this.BaseRequest({
        url: '/reportStatements/getDefinedAndOriginAssignById',
        method: 'get',
        params: {'definedId': definedId}
      }).then(response => {
        if (response != null && response.length > 0) {
          thisRef.$refs.treeRef.setCheckedKeys(response)
          // this.options = response
          // this.data = response
        }
      })
    },
    getTreeNode () {
      this.origin_ids = []
      let nodeData = this.$refs.treeRef.getCheckedNodes()
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
        method: 'post',
        data: {'definedId': this.formSubmitData.defined_id, 'originIds': this.origin_ids}
      }).then(() => {
        this.Message.success('保存成功')
        this.closeModal()
      })
    },
    handleEdit (index, row) { // 修改
      // this.getOriginList()
      this.dialogTitle = '修改报送报表'
      this.showModalPage = true
      this.isEditModal = true
      this.formSubmitData.defined_id = row.defined_id
      this.formSubmitData.defined_name = row.defined_name
      this.formSubmitData.report_type = row.report_type
      this.formSubmitData.status = row.status
      this.$nextTick(function () {
        this.$refs.treeRef.setCheckedKeys([])
      })
      this.getDefinedAndOriginAssign(row.defined_id, this)
    },
    clearData () { // 每次添加之前清空数据、
      /* //this.formSubmitData= {};
        // this.formSubmitData.defined_status= '';
        // this.formSubmitData.parent_defined_id= '';
        // this.formSubmitData.parent_defined_name= ''; */
      this.formSubmitData = {
        defined_id: null,
        defined_name: null,
        status: 0,
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
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>报送单元名称、所属报送机构、报送类型、状态'
        })
      }
      return checkResult
    },
    definedUnit (definedId) {
      this.$router.push({
        path: '/record/reportUnit',
        query: {
          'definedId': definedId,
          'isView': 'N'
        }
      })
    },
    viewDefinedUnit (definedId) {
      this.$router.push({
        path: '/record/reportUnit',
        query: {
          'definedId': definedId,
          'isView': 'Y'
        }
      })
    },
    viewSubDefined(reportDefinedId){
      this.submitedModel = true
      //reportDefinedId
      this.BaseRequest({
        url: '/reportCust/getReportBaseInfo',
        params: {
          reportDefinedId: reportDefinedId
        }
      }).then(response => {
        this.submitedParams = response
        // allSubOriginTree
        const allSubOriginsObj = {}
        if(this.submitedParams.allSubOrigins){
          this.submitedParams.allSubOrigins.forEach(subOrigin=>{
            const originId = subOrigin.originId
            const originName = subOrigin.originName
            allSubOriginsObj[originId] = originName
          })
        }

        const reSubOrigins = JSON.parse(JSON.stringify(this.treeData))

        const reSubOriginTree = []

        function checkLiftOrigin(parentOrigins){
          const newChildren = new Array()
          if(parentOrigins&&parentOrigins.length>0){
            parentOrigins.forEach(parentOrigin=>{
              const parentOriginChildren = parentOrigin.children
              if(parentOriginChildren!=null&&parentOriginChildren.length>0){//非叶子节点
                parentOrigin.children = checkLiftOrigin(parentOriginChildren)
                newChildren.push(parentOrigin)
              }else{//叶子节点
                if(allSubOriginsObj[parentOrigin.id]){
                  newChildren.push(parentOrigin)
                }
              }
            })
          }
          return newChildren
        }

        if(reSubOrigins&&reSubOrigins.length>0){
          this.submitedParams.allSubOriginTree = checkLiftOrigin(reSubOrigins)

        }


        // allSubOriginTree
      })
    },
    viewReSubDefined(repoortDefinedInfos){
      this.BaseRequest({
        url: '/reportCust/getReportBaseInfo',
        params: {
          reportDefinedId: repoortDefinedInfos.defined_id
        }
      }).then(response => {
        this.reSubmitModel = true

        this.reSubmitParams.defined_id = repoortDefinedInfos.defined_id
        this.reSubmitParams.defined_name = repoortDefinedInfos.defined_name

        const allSubOrigins= response.allSubOrigins

        this.reSubmitParams.report_data_start_str = repoortDefinedInfos.reportDataStart
        this.reSubmitParams.report_data_end_str = repoortDefinedInfos.reportDataEnd
        this.reSubmitParams.report_start_date_str = repoortDefinedInfos.reportStartDate
        this.reSubmitParams.report_end_date_str = repoortDefinedInfos.reportEndDate

        const allSubOriginObj = {}
        if(allSubOrigins){
          allSubOrigins.forEach(allSubOrigin=>{
            const originId = allSubOrigin.originId
            const originName = allSubOrigin.originName
            allSubOriginObj[originId] = originName
          })
        }
        function forEachTree(treeDataArray){
          treeDataArray.forEach(treeOption=>{
            const treeOriginId = treeOption.id
            if(treeOption.children&&treeOption.children.length>0){
              treeOption.disabled = true
              if(treeOption.children){
                forEachTree(treeOption.children)
              }
            }else{
              if(allSubOriginObj[treeOriginId]){
                treeOption.disabled = true
              }
            }
          })
        }

        const reSubOrigins = JSON.parse(JSON.stringify(this.treeData))
        forEachTree(reSubOrigins)
        this.reSubmitParams.resubmit_origin_tree = reSubOrigins
      })


    },
    openSubmitPrams (definedData) {
      this.submitModel = true
      this.submitParams.defined_id = definedData.defined_id
      this.submitParams.defined_name = definedData.defined_name
      this.BaseRequest({
        url: '/reportStatements/getDefinedOriginTreeById',
        params: {
          definedId: definedData.defined_id
        }
      }).then(response => {
        console.log(response)
        this.submitParams.defined_origins = new Array()
        this.submitParams.defined_origins.push(response)
        console.log(this.submitParams.defined_origins)
      })
    },
    submitReport () {
      if (this.submitParams.report_start_date_str == null || this.submitParams.report_start_date_str == '' ||
        this.submitParams.report_end_date_str == null || this.submitParams.report_end_date_str == ''||
        this.submitParams.report_data_start_str == null ||this.submitParams.report_data_start_str == ''||
        this.submitParams.report_data_end_str == null ||this.submitParams.report_data_end_str == ''
      ) {
        this.$notify({
          dangerouslyUseHTMLString: true,
          message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>起始日期、结束日期、填报区间起始日期、填报区间截止日期'
        })
        return
      }

      const loading = this.$loading({
        lock: true,
        text: '发布中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      // approveCheckOrigins
      this.submitParams.approve_check_origins = []
      let approveNodeData = this.$refs.approveCheckOrigins.getCheckedNodes()
      for (let i = 0; i < approveNodeData.length; i++) {
        this.submitParams.approve_check_origins.push(approveNodeData[i].origin_id)
      }

      this.submitParams.review_check_origins = []
      let review_check_origins = this.$refs.reviewCheckOrigins.getCheckedNodes()
      for (let i = 0; i < review_check_origins.length; i++) {
        this.submitParams.review_check_origins.push(review_check_origins[i].origin_id)
      }

      this.BaseRequest({
        url: '/reportStatements/sumitReportDefined',
        method: 'post',
        data: {
          'defined_id': this.submitParams.defined_id,
          'report_start_date': this.submitParams.report_start_date_str,
          'report_end_date': this.submitParams.report_end_date_str,
          'report_data_start': this.submitParams.report_data_start_str,
          'report_data_end': this.submitParams.report_data_end_str,
          'approve_check_origins': this.submitParams.approve_check_origins,
          'review_check_origins': this.submitParams.review_check_origins
        }
      }).then(response => {
        loading.close()
        this.Message.success('发布流程已启动')
        this.getTableData(1)
        this.closeSubmitModal()
      })
    },
    submitForOrigins(){
      const loading = this.$loading({
        lock: true,
        text: '补发中....',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      const reSubmitCheckedOrigins = this.reSubmitParams.resubmit_checked_origins

      const submit_origins = new Array()

      if(reSubmitCheckedOrigins&&reSubmitCheckedOrigins.length>0){
        reSubmitCheckedOrigins.forEach(reSubmitCheckedOrigin=>{
          submit_origins.push(reSubmitCheckedOrigin.value)
        })
      }else{
        this.Message.error("请选择需要补发的机构")
        return
      }


      this.BaseRequest({
        url: '/reportStatements/submitForOrigins',
        method: 'post',
        data: {
          'defined_id': this.reSubmitParams.defined_id,
          'report_start_date': this.reSubmitParams.report_start_date_str.replace(/-/g, ""),
          'report_end_date': this.reSubmitParams.report_end_date_str.replace(/-/g, ""),
          'report_data_start': this.reSubmitParams.report_data_start_str.replace(/-/g, ""),
          'report_data_end': this.reSubmitParams.report_data_end_str.replace(/-/g, ""),
          'submit_origins': submit_origins,
          'approve_check_origins': this.reSubmitParams.approve_check_origins,
          'review_check_origins': this.reSubmitParams.review_check_origins
        }
      }).then(response => {
        loading.close()
        this.reSubmitModel = false
        this.Message.success('报表已补发')
        this.getTableData(1)
      })
    }


  },
  mounted: function () { // 初始化
    this.$nextTick(function () {
      this.reportDataList = []
      this.getTableData(1)
      this.getOriginList()
    })
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
