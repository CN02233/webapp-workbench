<template>
  <!--title-->
  <div>
    <el-row v-for="(value,key) in columTemplateData.treeData">
      <el-col v-for="index in totalColNum" class="tree_colum" :span="colNum" >
        <el-input v-if="(columTemplateData.level-1)==index"></el-input>
        <span v-else> -- </span>
      </el-col>
    </el-row>


    <TreeDimShowerTitle :colNum="colNum" :totalColNum="totalColNum" :columTemplateData="columTemplateDataChildren" v-for="columTemplateDataChildren in columTemplateData.children"></TreeDimShowerTitle>
  </div>

</template>

<script>
  export default {
    name: 'TreeDimShowerTitle',
    props:{
      colNum:{
        type: Number
      },
      totalColNum:{
        type: Number
      },
      columTemplateData:{
        type: Object
      }
    },
    data() {
      return {}
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
