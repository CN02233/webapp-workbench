<template>
  <div class="work-main">
    <WorkMainHeader :headerItems="headerItems"></WorkMainHeader>
    <WorkMainContext>
      <slot></slot>
    </WorkMainContext>
  </div>
</template>

<script>
  import WorkMainHeader from "@/models/public/WorkMainHeader"
  import WorkMainContext from "@/models/public/WorkMainContext"
  export default {
    name: 'WorkMain',
    components: {
      WorkMainHeader,
      WorkMainContext
    },
    props:{
      headerItems:{
        type:Array
      }
    },
    data() {
        return {}
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .work-main{
    width:100%;
    height:100%;
    overflow: hidden;
    /*margin:0 20px 0 20px;*/
  }
</style>
