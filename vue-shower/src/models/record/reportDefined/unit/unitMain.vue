


<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表','']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="unitList"
          style="width: 100%">
          <el-table-column
            prop="unit_id"
            align="center"
            width="100"
            label="编号">
          </el-table-column>
          <el-table-column
            prop="unit_name"
            align="center"
            label="单元名称">
          </el-table-column>
          <el-table-column
            prop="unit_describe"
            align="left"
            label="单元说明">
          </el-table-column>
          <el-table-column
            prop="unit_type"
            align="left"
            :formatter="formatterDefinedType"
            label="单元类型">
          </el-table-column>
          <el-table-column
            prop="create_date"
            align="center"
            width="200"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="create_user"
            align="center"
            width="200"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="definedUnit(scope.row.unit_id,scope.row.unit_type)" size="small">报送项</el-button>
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
        unitList:[],
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        definedId:'',
        reportDefinedUnitType:{
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
          url:'reportUnitDefined/reportUnitDefinedList',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum,reportDefinedId:this.definedId}
        }).then(response=>{
          $this.unitList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      viewDefined(){

      },
      editDefined(){

      },
      deleteDefined(){

      },
      definedUnit(unitId,defined_type){
        let editUrl = ''
        if(defined_type=='1'){
          editUrl = '/record/reportDefined/oneDimensionsStatic'
        }else if(defined_type=='2'){

        }else if(defined_type=='3'){

        }else if(defined_type=='4'){

        }else{
          return
        }
        this.$router.push({
          path: editUrl,
          query:{
            'unitId':unitId
          }
        });
      },
      formatterDefinedType(row){
        return this.reportDefinedUnitType[row['unit_type']]
      }
    },
    mounted:function(){
      this.definedId = this.$route.query.definedId
      this.getTableData(1)
    }
  }
</script>

<style scoped>

</style>
