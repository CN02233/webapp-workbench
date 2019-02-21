<template>


  <div>
    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        header-align="center"
        align="center"
        prop="createTime"
        label="创建日期">
      </el-table-column>
      <el-table-column
        prop="infoTitle"
        header-align="center"
        align="center"
        label="信息类别">
      </el-table-column>
      <el-table-column
        prop="fileName"
        header-align="center"
        align="center"
        label="文件名称">
      </el-table-column>
      <el-table-column
        fixed="right"
        align="center"
        label="操作">
        <template slot-scope="scope">
          <el-button @click="viewInfo(scope.row.fileName)" type="text" size="small">查看</el-button>
          <el-button @click="editInfo(scope.row.fileName)" type="text" size="small">编辑</el-button>
          <el-button @click="delInfo(scope.row.fileName)" type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      layout="prev, pager, next"
      @current-change="changePage"
      :page-count="pageCount">
    </el-pagination>

    <el-dialog @close="closeDialog" :visible.sync="viewInfoFlag" >
      <infoInput :inputDataList="viewInfoDatas" v-if="viewInfoFlag"
                 :inputFileName="viewInfoFileName"
                 :isView="isViewFlag"
                 :inputTitle="viewInfoTitle"
                 @refreshList="changePage"></infoInput>
    </el-dialog>
  </div>

</template>

<script>
  import infoInput from './infoInput'
  export default {
    name: 'InfoList',
    data() {
      let pageCountValue = 1;
      let isView = false;
      return {
        tableData: [],
        pageCount:pageCountValue,
        viewInfoFlag:false,
        viewInfoDatas : [],
        viewInfoTitle : "",
        viewInfoFileName : "",
        isViewFlag:false
      }
    },
    components:{
      infoInput
    },
    methods: {
      changePage:function(pageNum){
        this.refreshData(pageNum);
        this.viewInfoFlag = false
      },
      viewInfo:function(fileName){
        console.log(fileName)
        this.getInfoData(fileName)
        this.isViewFlag = true
      },
      editInfo:function(fileName){
        this.getInfoData(fileName)
        this.isViewFlag = false
      },
      editSaveInfo:function(fileName){
        this.getInfoData(fileName)
      },
      getInfoData:function(fileName){
        const $this = this;
        this.Request({
          url:"/info/getFileInfo.do",
          params:{
            fileName:fileName
          }
        }).then(response=>{
          console.log(response)
        $this.viewInfoFlag = !$this.viewInfoFlag
        $this.viewInfoDatas = response.infoList
        $this.viewInfoTitle = response.title
        $this.viewInfoFileName = response.fileName
      });
      },
      closeDialog:function(){
        this.viewInfoFlag = false;
        this.isViewFlag = false;
      },
      delInfo:function(fileName){
        const $this = this;
        this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.Request({
              url:"/info/delInfo.do",
              params:{
                fileName:fileName
              }
            }).then(response=>{
              $this.Message.success("删除成功")
            $this.refreshData(1)
          });

        }).catch(() => {
            this.Message({
            type: 'info',
            message: '已取消删除'
          });
        });


      },
      refreshData:function(pageNum){
        if(pageNum&&pageNum!==''){}
        else pageNum = 1;
        const thisObj = this;
        this.Request({
          url:"/info/listInfos.do",
          params:{
            pageNum:pageNum,
            pageSize:10,
          }
        }).then(response=>{
          thisObj.pageCount = response.pageCount
          thisObj.tableData = response.dataList
          console.log(thisObj.pageCountValue);
        });
      }
    },
    mounted:function(){
      this.refreshData(1);
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-pagination{
    margin-top:30px;
  }
  .param-name{
    text-align: right;
    margin-right:10px;
    width:40%;
  }
  .param-value{
    text-align: left;
    width:40%;
  }
</style>
