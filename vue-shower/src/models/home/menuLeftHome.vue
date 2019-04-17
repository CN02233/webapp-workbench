
<template>
    <div class="home" >
      <div class="container-header">
        <el-header style="text-align: right; font-size: 12px">
          <div class="work-menu-group">
            <WorkLeftMenuGroup :sysName="sysName" ></WorkLeftMenuGroup>
          </div>
          <div class="personal-infos">
            <div @click="logout" class="login-user-name">退出</div>
          </div>
        </el-header>
      </div>


      <div class="container-root">
        <div class="container-root-menu">
          <el-menu :collapse="isCollapse" class="menu-style">
            <el-menu-item v-on:click.native="collapseMenu">
              <i :class="isCollapse?'el-icon-d-arrow-right':'el-icon-d-arrow-left'"></i><span v-if="!isCollapse">&nbsp;收起菜单</span>
            </el-menu-item>


            <WorkLeftMenu v-for="menuObj in menuList" :key="menuObj.id" :menuData="menuObj"></WorkLeftMenu>
          </el-menu>
        </div>

        <div :class="isCollapse?'container-root-context-collapse':'container-root-context'">
          <router-view></router-view>
        </div>




      </div>
    </div>
</template>

<script>
  import WorkLeftMenuGroup from "@/models/menu/left-menu-group"
  import WorkLeftMenu from "@/models/menu/left-menu"
  import { MessageBox } from 'element-ui'

  export default {
    name:"MenuLeftHome",
    props:{
      menuList:{
        type:Array
      },
      sysName:{
        type:String
      },
      loginUserInfo:{
        type:Object
      }
    },
    data() {
      return {
        isCollapse:true
      }
    },
    components: {
      WorkLeftMenuGroup,
      WorkLeftMenu
    },
    methods:{
      mainPageChange:function(pageName){
        console.log(pageName);
      },
      logout:function(commound){
        MessageBox.confirm('退出系统将丢失当前未保存的相关操作，确定退出？', '提示', {
          confirmButtonText: '退出',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const $this = this

          this.$http.post(process.env.BASE_API+"/sys/login/logout.do",{},{withCredentials: true}).then(res => {
            let responseData = res.data ;
            try{
              responseData = JSON.parse(responseData)
            }catch(e){}
            if (responseData.result == 'SUCCESS') {
              if(responseData.faild_reason === 'FORWARD_CAS'){
                let forwardUrl = responseData.resultData
                window.location = forwardUrl
              }else{
                $this.Message.success("登出成功")
                $this.$router.push({'path':'/'})
              }
            }else{
              $this.Message.error(responseData.result)

            }
          })
        })


      },
      collapseMenu(){
        console.log("collapseMenu is running.....")
        this.isCollapse = !this.isCollapse
      }
    },
    mounted:function(){
      if(this.$route.fullPath=='/home'){
        this.$router.push({"path":"welcome"})
      }
    }
  };
</script>


<style lang="css">
  .menu-style:not(.el-menu--collapse){
    background-color:#EBF2FE;
    text-color:"#ffffff";
    active-text-color:"#3f8aff";
    height:95%;
    width: 200px;
  }

  .el-menu--collapse{
    background-color:#EBF2FE;
    height:95%;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>

  .menu{
    background-color: #2e3d50;
    height:100%;
  }

  .work-left-menu-header{
    width:calc(100% - 300px);
    height:100%;
    float: left;
    text-align: left;
    font-size:20px;
  }

  .el-menu{
    /*background-color: #2b5ca9 !important;*/
    text-align: left;
  }

  .el-aside {
    color: #333;
  }

  .el-menu-item a{
    text-decoration:none;
    color:#ffffff;
  }

  .fa-icon {
    width: 18px;
    height: 18px;
  }

  .home{
    width:calc(100% - 40px);
    height:calc(100% - 40px);
    padding:20px;
    background-color: #3CD2E6 !important;
  }

  .work-menu-group{
    width:calc(100% - 300px);
    height:100%;
    float: left;
  }

  .personal-infos{
    width:280px;
    height:100%;
    float:left;
    color:black;
    text-align: right;
  }

  .menu{
    background-color: rgb(238, 241, 246);
  }

  .container-header {
    width:100%;
    color: black;
    background-color: #EBF2FE;
    height:65px !important;
    box-shadow:    0px 0px 0px 0px #ffffff,
    0px 0px 0px 0px #3bee17,
    0px 0px 0px 0px #2279ee,
    0px 10px 10px 0px #00ffff;
    float:left;
  }

  .container-root{
    width:100%;
    height:calc(100% - 68px);
    background-color: #ffffff;
    margin-top:3px;
    box-shadow:    0px 0px 0px 0px #ffffff,
    0px 3px 10px 0px #00ffff,
    0px 3px 10px 0px #00ffff,
    0px 3px 10px 0px #00ffff;
    float:left;
  }

  .container-root-menu{
    height:100%;
    float:left;
    background-color: #EBF2FE;
  }

  .container-root-context-collapse{
    width:calc(100% - 90px);
    height:100%;
    float:left;
  }

  .container-root-context{
    width:calc(100% - 220px);
    height:100%;
    float:left;
  }

  .el-aside {
    color: #333;
  }

  .el-container{
    background-color: #ffffff;
  }

  .fa-icon {
    width:30px;
    height:20px;
    padding:18px 0 0 0;
  }

  .login-user-name{
    width:120px;
    height:40px;
    line-height: 40px;
    border-radius: 15px;
    background-color: #00f2a4;
    margin-top:13px;
    color:white;
    font-size: 18px;
    text-align: center;
    font-weight: bolder;
    padding:0px;
    cursor: pointer;
    float:right;
  }

  .loginUserInfoRow{
    margin:0 !important;
  }


</style>
