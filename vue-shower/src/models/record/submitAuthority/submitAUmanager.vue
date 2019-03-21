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
          :data="originDataList"
          style="width: 100%">
          <el-table-column
            prop="origin_id"
            align="left"
            label="机构编号">
          </el-table-column>
          <el-table-column
            prop="origin_name"
            align="left"
            hidden="hidden"
            label="机构名称">
          </el-table-column>
          <el-table-column
            prop="parent_origin_id"
            align="left"
            label="上级机构编号">
          </el-table-column>
          <el-table-column
            prop="parent_origin_name"
            align="left"
            label="上级机构名称"
            :formatter="formatterSuperName"
          >
          </el-table-column>
          <el-table-column
            label="操作"
            align="left"
            >
            <template slot-scope="scope">
              <el-button
                size="mini"
                @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
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
        <el-col :sm="12" >
          <el-row>
            <el-input
              placeholder="输入机构名称快速查找机构"
              v-model="filterText">
            </el-input>
            <el-tree
              accordion
              class="filter-tree"
              :data="data"
              :props="defaultProps"
              ref="searchConditionRef"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick">
            </el-tree>

          </el-row>
        </el-col>
        <el-col :sm="12">
          <el-row>
            <el-col :span="6" :offset="1">上级机构</el-col>
            <el-col :span="17">
              <el-input placeholder="上级机构" v-model="formSubmitData.parent_origin_name" class="input-with-select" readonly="true"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6" :offset="1">机构名称</el-col>
            <el-col :span="17">
              <el-input placeholder="机构名称" v-model="formSubmitData.origin_name" class="input-with-select" ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6" :offset="1">机构状态</el-col>
            <el-col :span="17">
              <el-select v-model="formSubmitData.origin_status" placeholder="请选择机构状态">
                <el-option
                  v-for="item in options"
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
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'OriginMain',
    data() {
      return {
        originDataList:[],
        originDataObjs:{},
        tableDataUrl:'submitAU/listSubmitauthority',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        dialogTitle:'',
        formSubmitData:{
          origin_name:'',
          parent_origin_id:'',
          parent_origin_name:'',
          origin_status:''
        },
        search: '',
        defaultProps: {
          children: 'childList',
          label: 'name'
        },
        filterText: '',
        data: [],
        options: [{
          value: "1",
          label: '正常'
        }, {
          value: "0",
          label: '禁用'
        },{
          value:"9",
          label:'失效'
        }],
        value: ''
      }
    },
    watch: {//监听节点搜索的内容
      filterText(val) {
        this.$refs.searchConditionRef.filter(val);
      }
    },
    validations:{//提交前的验证
      formSubmitData:{
        origin_name:{
          required
        },
        origin_status:{
          required
        }
      }
    },
    computed:{
      //初始化加载
    },
    components: {
      WorkTablePager,
      WorkMain
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
          params:{currPage:pageNum,pageSize:this.eachPageNum}
        }).then(response=>{
          if(response.dataList!=null){
            response.dataList.forEach(originObj =>{
              $this.originDataObjs[originObj.origin_id] = originObj
            })
          }
          $this.originDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      formatterSuperName:function(row){
        return this.originDataObjs[row.parent_origin_id]!=null?this.originDataObjs[row.parent_origin_id].origin_name:"无"
      },
      refreshTableList:function(dataList){
        this.originDataList = dataList
      },
      openAddModal:function(){
        this.clearData();
        this.dialogTitle='新增机构'
        this.getOriginList();
        this.showModalPage = true
        this.isEditModal = false
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
      },
      getOriginList(){//弹出model触发、获取机构树状展示
        this.BaseRequest({
          url:'submitAU/listAllSubmitauthority',
          method:"get"
        }).then(response=>{
          if(response!=null&&response.length>0){
            this.data=[];
            this.data=response;
          }
        })
      },
      handleNodeClick(data) {//点击树的节点进行赋值
        this.formSubmitData.parent_origin_id=data.id;
        this.formSubmitData.parent_origin_name=data.name;
      },
      filterNode(value, data) {//树节点的过滤
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      handleInsert(){//添加、修改确定按钮触发
        if(this.checkInputNull()){
          return
        }
        const loading = this.$loading({
          lock: true,
          text: '保存中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:'/submitAU/addSubmitauthority',
          method:'post',
          data:this.formSubmitData,
        }).then(response=>{
          this.Message.success("保存成功")
          loading.close()
          this.closeModal();
          this.getTableData()
        }).catch(error=>{
          console.log(error)
          loading.close()
          this.Message.error("保存失败"+error)
        })
      },
      handleEdit(index, row) {//修改
        this.dialogTitle='修改机构'
        this.showModalPage = true
        this.isEditModal = true
        this.formSubmitData.origin_id= row.origin_id;
        this.formSubmitData.origin_name= row.origin_name;
        this.formSubmitData.origin_status= row.origin_status;
        this.formSubmitData.parent_origin_id= row.parent_origin_id;
        this.formSubmitData.parent_origin_name= this.formatterSuperName(row);
      },
      clearData(){//每次添加之前清空数据、
        /*//this.formSubmitData= {};
        // this.formSubmitData.origin_status= '';
        // this.formSubmitData.parent_origin_id= '';
        // this.formSubmitData.parent_origin_name= '';*/
        this.formSubmitData={
            origin_name:null,
            parent_origin_id:null,
            parent_origin_name:null,
            origin_status:null
        }
      },
      handleDelete(index, row) {//删除
        this.$confirm('确定删除机构【'+row.origin_name+"】？", '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          this.BaseRequest({
            url:"/submitAU/delById",
            method:'get',
            params:{"originId":row.origin_id}
          }).then(reponse=>{
            this.Message.success("删除成功")
            this.getTableData()
          })

        }).catch(() => {
        });
      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>机构名称、机构状态'
          })
        }
        return checkResult
      }
    },
    mounted:function(){//初始化
      this.originDataList = []
      this.getTableData(1)
      this.getOriginList();
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
