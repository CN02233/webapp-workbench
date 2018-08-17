
<template>
  <el-container class="home" >

    <el-header style="text-align: right; font-size: 12px">
      <div class="work-menu-group">
        <WorkTopMenuGroup :sysName="sysNameStr" :menuList="menuList" ></WorkTopMenuGroup>
      </div>
      <div class="personal-infos">
        <el-dropdown >
          <i class="el-icon-setting" style="margin-right: 15px"></i>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>查看</el-dropdown-item>
            <el-dropdown-item>新增</el-dropdown-item>
            <el-dropdown-item>删除</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <span>宋超群</span>
      </div>

    </el-header>
    <el-container>

      <el-main >
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>


<script>
  import WorkTopMenuGroup from "./menu/top-menu-group"
  import WorkTopMenu from "./menu/top-menu"
  export default {
    name:"MenuTopHome",
    data() {
      const item = {
      };
      const menuList = [
        {id:"M1",name:"菜单1",url:"/infoInput"},
        {id:"M2",name:"菜单2",url:"/infoInput1"}
      ];
      const sysNameStr = "XXXX系统";
      return {
        tableData: Array(20).fill(item),
        sysNameStr: sysNameStr,
        menuList : menuList
      }
    },
    components: {
      WorkTopMenuGroup,
      WorkTopMenu
    },
    methods:{
      mainPageChange:function(pageName){
        console.log(pageName);
      }
    }
  };
</script>


<style>
  .home{
    width:100%;
    height:100%;
  }

  .work-menu-group{
    width:calc(100% - 200px);
    height:100%;
    float: left;
  }

  .personal-infos{
    width:200px;
    height:100%;
    float:right;
  }


  .menu{
    background-color: rgb(238, 241, 246);
  }

  .el-header {
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
    height:60px !important;
  }

  .el-aside {
    color: #333;
  }
</style>
