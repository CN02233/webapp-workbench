<template>
  <!--title-->
  <div>
    <el-col class="tree_title" :span="colNum" >
      {{columTemplateData.colum_name_cn}}
    </el-col>

    <TreeDimShowerTitle :key="columTemplateDataChildren" :colNum="colNum" :columTemplateData="columTemplateDataChildren" v-for="columTemplateDataChildren in columTemplateData.children"></TreeDimShowerTitle>
  </div>

</template>

<script>
  export default {
    name: 'TreeDimShowerTitle',
    props:{
      colNum:{
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
