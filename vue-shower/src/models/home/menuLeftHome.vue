
<template>
    <div class="home" >
      <div class="container-header">
        <el-header style="text-align: right; font-size: 12px">
          <div class="work-menu-group">
            <WorkLeftMenuGroup :sysName="sysName" ></WorkLeftMenuGroup>
          </div>
          <div class="personal-infos">
            <!--<icon name="home"></icon>-->
            <div @click="logout" class="icon-mount">
              <el-tooltip class="item" effect="dark" content="退出系统" placement="bottom-end">
                <icon  name="logoutallfill"></icon>
              </el-tooltip>
            </div>
            <div @click="gotoWelcome" class="icon-mount">
              <el-tooltip class="item" effect="dark" content="主页" placement="bottom-end">
                <icon name="homeallfill"></icon>
              </el-tooltip>
            </div>
            <div v-if="!fullScreen" @click="changeScreen" class="icon-mount">
              <el-tooltip class="item" effect="dark" content="全屏" placement="bottom-end">
                <icon name="fullscreen" class="fa-icon-changescreen"></icon>
              </el-tooltip>
            </div>
            <div v-if="fullScreen" @click="changeScreen" class="icon-mount">
              <el-tooltip class="item" effect="dark" content="退出全屏" placement="bottom-end">
                <icon name="outfullscreen" class="fa-icon-changescreen"></icon>
              </el-tooltip>
            </div>

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
        isCollapse:true,
        fullScreen:false
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
      gotoWelcome(){
        this.$router.push({'path':'/'})
      },
      changeScreen(){
        let isFullScreen = document.fullscreenElement || document.mozFullScreenElement||document.webkitFullscreenElement

        this.fullScreen = !isFullScreen
        const element = document.documentElement
        if(this.fullScreen){//全屏
          if (element.requestFullscreen) {
            element.requestFullscreen();
          } else if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
          } else if (element.webkitRequestFullscreen) {
            element.webkitRequestFullscreen();
          } else if (element.msRequestFullscreen) {
            element.msRequestFullscreen();
          }
        }else{
          if (document.exitFullscreen) {
            document.exitFullscreen();
          } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
          } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
          }
        }
      },
      exitHandler(){
        console.log("ehere")
        if (!document.webkitIsFullScreen &&!document.mozFullScreen &&!document.msFullscreenElement) {
          this.fullScreen = false
          console.log("mmmmmmm")
        }else{
          console.log("yyyyy")
        }
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
      const $this = this
      document.onkeydown = function(e) {
        let key = window.event.keyCode;
        console.log(key)
        console.log($this.fullScreen)
        if (key== 122 ) {//屏蔽F11快捷键
          return false
        }
      }
      if (document.addEventListener) {
        document.addEventListener('webkitfullscreenchange', this.exitHandler, false);
        document.addEventListener('mozfullscreenchange', this.exitHandler, false);
        document.addEventListener('fullscreenchange', this.exitHandler, false);
        document.addEventListener('MSFullscreenChange', this.exitHandler, false);
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

  .home{
    width:calc(100% - 40px);
    height:calc(100% - 40px);
    padding:20px;
    background-color: #3CD2E6 !important;
  }

  .work-menu-group{
    width:50%;
    height:100%;
    float: left;
  }

  .personal-infos{
    width:50%;
    height:100%;
    float:right;
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
    box-shadow:    0px 0px 0px 0px black,
    0px 0px 0px 0px #3bee17,
    0px 0px 0px 0px #2279ee,
    0px 10px 10px 0px black;
    float:left;
  }

  .container-root{
    width:100%;
    height:calc(100% - 68px);
    background-color: #ffffff;
    margin-top:3px;
    box-shadow:    0px 0px 0px 0px blueviolet,
    0px 3px 10px 0px blueviolet,
    0px 3px 10px 0px blueviolet,
    0px 3px 10px 0px blueviolet;
    float:left;
  }

  .container-root-menu{
    height:100%;
    float:left;
    background-color: #EBF2FE;
    box-shadow:    0px 0px 0px 0px blueviolet,
    0px 0px 0px 0px blueviolet,
    0px 0px 10px 0px blueviolet,
    0px 0px 0px 0px blueviolet;
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

  .icon-mount{
    float: right;
  }

  .fa-icon {
    width:40px;
    height:40px;
    margin:10px 10px 0 10px;
    cursor: pointer;
  }

  .fa-icon-changescreen{
    width:42px;
    height:42px;
    margin:8px 10px 0 10px;
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
