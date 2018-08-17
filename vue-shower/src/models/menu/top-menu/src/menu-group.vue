<template>
  <div class="menu-group-root">
    <WorkLogo :sysName="sysName" class="work-menu-logo"></WorkLogo>
    <div v-if="menuList != null" class="menu-group-context">
      <WorkTopMenu :menu="menuObj" v-for="menuObj in menuList" :key="menuObj"></WorkTopMenu>
    </div>
    <!--<slot></slot>-->
  </div>
</template>

<script>
  import WorkTopMenu from "./menu"
  import WorkLogo from "./menu-logo"
  export default {
    name: 'WorkTopMenuGroup',
    components: {
      WorkTopMenu,
      WorkLogo
    },
    props: {
      menuList:{
        type:Array
      },
      sysName:{
        type:String
      }
    },
    data() {
        return {}
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .menu-group-root{
    width:100%;
    height:100%;
  }

  .work-menu-logo{
    width:170px;
    height:100%;
    float:left;
    margin:0 0 0 30px;
  }

  .menu-group-context{
    width:calc(100% - 200px);
    height: 100%;
    float:left;
  }
</style>
