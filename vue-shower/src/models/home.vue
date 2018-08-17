
<template>
    <el-container class="home" >

        <el-header style="text-align: right; font-size: 12px">
            <el-dropdown>
                <i class="el-icon-setting" style="margin-right: 15px"></i>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>查看</el-dropdown-item>
                    <el-dropdown-item>新增</el-dropdown-item>
                    <el-dropdown-item>删除</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <span>宋超群</span>
        </el-header>
        <el-container>
            <el-aside width="200px" class="menu">
                <el-menu :default-openeds="[1]">
                    <el-submenu index="1">
                        <template slot="title"><i class="el-icon-menu"></i>信息管理</template>

                        <el-menu-item index="1-1">
                            <router-link to="/infoInput">信息录入</router-link>
                        </el-menu-item>
                        <el-menu-item index="1-2" @click="mainPageChange('searchInfos')">信息查询</el-menu-item>
                    </el-submenu>
                </el-menu>
            </el-aside>

            <el-main >
                <router-view></router-view>

                <!--<el-table :data="tableData">-->
                    <!--<el-table-column prop="date" label="日期" width="140">-->
                    <!--</el-table-column>-->
                    <!--<el-table-column prop="name" label="姓名" width="120">-->
                    <!--</el-table-column>-->
                    <!--<el-table-column prop="address" label="地址">-->
                    <!--</el-table-column>-->
                <!--</el-table>-->
            </el-main>
        </el-container>
    </el-container>
</template>

<style>
    .home{
        width:100%;
        height:100%;
    }

    .el-container{
        height:calc(100% - 60px);
        width: 100%;
    }

    .menu{
        background-color: rgb(238, 241, 246);
    }

    .el-main{
        width:calc(100% - 200px);
        height:100%;
    }

    .el-header {
        background-color: #B3C0D1;
        color: #333;
        line-height: 60px;
    }

    .el-aside {
        color: #333;
    }
</style>

<script>
export default {
  data() {
    const item = {
        date: '2016-05-02',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1518 弄'
    };
    return {
        tableData: Array(20).fill(item)
    }
  },
  methods:{
    mainPageChange:function(pageName){
        console.log(pageName);
    }
  }
};
</script>