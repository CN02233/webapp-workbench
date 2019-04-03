<template>
  <!--title-->
  <div >
    <div>
      <el-col :span="colNum" v-for="forTime in totalColNum">
        <el-input v-if="columGroupData[forTime-1]" :placeholder="columGroupData[forTime-1].colum_name_cn"></el-input>
        <span v-else> -- </span>
      </el-col>


      <!--<el-row v-if="columGroupData.children!=null">-->
        <!--<TreeDimShowerContext :columGroupData="columGroupData.children" :totalColNum="totalColNum"  :colNum="colNum" ></TreeDimShowerContext>-->
      <!--</el-row>-->
    </div>

  </div>

</template>


<script>
  export default {
    name: 'TreeDimShowerContext',
    props:{
      colNum:{
        type: Number
      },
      totalColNum:{
        type: Number
      },
      columGroupData:{
        type: Array
      }
    },
    data() {
      return {
        columGroupDataChildren : []
      }
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .tree_title{
    padding:10px 5px 10px 5px;
    background: #8cc5ff;
    border:1px solid white;
  }

  .tree_colum{
    padding:5px 2px 5px 2px;
  }
</style>
