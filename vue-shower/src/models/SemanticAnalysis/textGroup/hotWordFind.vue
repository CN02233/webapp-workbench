<template>
  <WorkMain :headerItems="['NLP应用','热词发现']">
    <el-row v-for="(wordData,index) in wordDataList" class="table-row">
      <el-col style="color: blue;font-weight: bold;" :span="2">{{index+1}}</el-col>
      <el-col :span="10">{{wordData.term}}</el-col>
      <el-col :span="10">{{wordData.count}}</el-col>
    </el-row>
  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'HotWordFind',

    data() {
      return {
        wordDataList:[]
      }
    },
    computed:{
    },
    components: {
      WorkMain
    },
    methods: {
    },
    mounted:function(){
      const $this = this
      const sendData = {"text":this.textAreaVal}
      this.Request({
        url:'/hotword',
        method:'get'
      }).then(response=>{
        if(response.status!='0'){
          this.Message.error(response.message)
        }else{
          $this.wordDataList = response.result
        }

//          $this.wordDataList=[{"term":"希尔瓦娜斯", "count":"1"}, {"term":"洛萨", "count":"2"}]
      })
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";

  .el-row{
    margin-top:20px;
    text-align: left;
  }

  $seachRowHeight : 50px;
  $pagerRowHeight : 50px;
  $tableRowHeight : calc(100% - #{$seachRowHeight+$pagerRowHeight+10});
  .search-row{
    height:$seachRowHeight;
  }

  .table-row{
    width:60%;
    height:$tableRowHeight;
    overflow: auto;
    margin:20px 0 0 30%;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
