<template>
  <WorkMain :headerItems="['NLP基础','文本多主题']" class="table-page-root">
    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="resultData"
          style="width: 100%">
          <el-table-column
            prop="topic"
            align="left"
            label="分类">
          </el-table-column>

          <el-table-column
            prop="score"
            align="left"
            label="满足度">
          </el-table-column>
          <el-table-column
            prop="user"
            align="left"
            label="用户确认分类">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="saveResult(scope.row)" size="small">保存</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"

  export default {
    name: 'MoreTitleTxtResult',
    props:['moreTitleResult'],
    data() {
      return {
        refuseView:false,
        resultData:[],
        moreTitleTextId:""
      }
    },
    components: {
      WorkTablePager,
      WorkMain
    },
    methods: {
      saveResult:function(rowData){
        this.Request({
          url:'/topic/save',
          method:'get',
          params:{id:this.moreTitleTextId,topic:rowData.topic}
        }).then(response=>{
          if(response.status!='0'){
            this.Message.error(response.message)
          }else{
            this.Message.success("保存成功")
            this.$router.push({"name":"moreTitleTxt"})
          }
        })

      }
    },
    mounted:function(){
      this.resultData = this.$route.params.moreTitleResult.result
      this.moreTitleTextId = this.$route.params.moreTitleResult.id
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

  .select-ty{
    width:100%;
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
