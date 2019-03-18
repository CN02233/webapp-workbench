


<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="reportDefinedList"
          style="width: 100%">
          <el-table-column
            prop="defined_id"
            align="center"
            width="100"
            label="编号">
          </el-table-column>
          <el-table-column
            prop="defined_name"
            align="center"
            label="报表名称">
          </el-table-column>
          <el-table-column
            prop="defined_describe"
            align="left"
            label="报表说明">
          </el-table-column>
          <el-table-column
            prop="defined_type"
            align="left"
            :formatter="formatterDefinedType"
            label="报表类型">
          </el-table-column>
          <el-table-column
            prop="create_date"
            align="center"
            width="200"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="创建人"
            align="center"
            width="200"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="definedUnit(scope.row.defined_id,scope.row.defined_type)" size="small">报送单元</el-button>
              <el-button type="text" @click="viewDefined()" size="small">查看</el-button>
              <el-button type="text" @click="editDefined()" size="small">编辑</el-button>
              <el-button type="text" @click="deleteDefined()" size="small">删除</el-button>
              <!--<el-button type="text" @click="openEditModal(scope.row)" size="small">查看</el-button>-->
            </template>
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
    name: "unit-main",
    describe:"一维静态报表单元列表/入口",
    components: {
      WorkTablePager,
      WorkMain
    },
    data() {
      return {
        reportDefinedList:[],
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        reportDefinedType:{
          '1':'一维静态',
          '2':'一维动态',
          '3':'多维静态',
          '4':'多维树状'
        }
      }
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
          url:'reportDefined/reportDefinedList',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum}
        }).then(response=>{
          $this.reportDefinedList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      viewDefined(){

      },
      editDefined(){

      },
      deleteDefined(){

      },
      definedUnit(definedId,defined_type){
        let editUrl = ''
        if(defined_type=='1'){
          editUrl = '/record/reportDefined/oneDimensionsStatic/edit'
        }else if(defined_type=='2'){

        }else if(defined_type=='3'){

        }else if(defined_type=='4'){

        }else{
          return
        }
        this.$router.push({
          path: editUrl,
          query:{
            'definedId':definedId
          }
        });
      },
      formatterDefinedType(row){
        return this.reportDefinedType[row['defined_type']]
      }
    },
    mounted:function(){
      this.getTableData(1)
    }
  }
</script>

<style scoped>

</style>
