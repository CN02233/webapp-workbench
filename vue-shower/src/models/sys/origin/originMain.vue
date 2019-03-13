<template>
  <WorkMain :headerItems="['权限管理','机构管理']">

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

        </el-table>
      </el-col>
    </el-row>

    <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
    <WorkTablePager @refreshData="getTableData"
                    :pageCount="totalPage">
    </WorkTablePager>

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
        tableDataUrl:'origin/listOrigin',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        isEditModal:false
      }
    },
    validations:{
      formData:{
        super_module_id:{
          required
        },
        module_name:{
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
      }
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
        this.dataSourceList = dataList
      }
    },
    mounted:function(){
      this.originDataList = []
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
