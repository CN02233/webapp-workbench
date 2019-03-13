
<template>
    <el-container class="home" >

      <el-header style="text-align: right; font-size: 12px">
        <div class="work-menu-group">
          <WorkLeftMenuGroup :sysName="sysName" ></WorkLeftMenuGroup>
        </div>
        <div class="personal-infos">
          <div  @click="logout" class="logout-icon" >
            <icon class="fa-icon" name="logout2"></icon>
            <div class="logout-font">退出</div>
          </div>
          <div class="login-user">
            <el-tooltip class="item" effect="dark"
                        placement="bottom-start">
              <div slot="content">{{loginUserInfo.user_name}}</div>
              <el-row class="loginUserInfoRow">
                <div class="login-user-infomation">
                  当前登录用户：{{loginUserInfo.user_name}}
                </div>
              </el-row>
            </el-tooltip>
          </div>

        </div>

      </el-header>

        <el-container>
            <el-aside width="225px" class="menu">
                <el-menu
                  background-color="#2e3d50"
                  text-color="#ffffff"
                  active-text-color="#3f8aff">
                    <WorkLeftMenu v-for="menuObj in menuList" :key="menuObj.id" :menuData="menuObj"></WorkLeftMenu>
                </el-menu>
            </el-aside>

            <el-main >
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
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
      return {}
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

              }
            }else{
              $this.Message.success("登出成功")
              $this.$router.push({'path':'/'})
            }
          })
        })


      }
    },
    mounted:function(){
      if(this.$route.fullPath=='/home'){
        this.$router.push({"path":"welcome"})
      }
    }
  };
</script>


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
    width:100%;
    height:100%;
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
    color:white;
    text-align: left;
  }

  .menu{
    background-color: rgb(238, 241, 246);
  }

  .el-header {
    /*background-color: #2b5ca9;*/
    color: #333;
    height:54px !important;
    background-image: url("/static/image/header_backgroud.png");
    /*background-image: url("/nlp/static/image/header_backgroud.png");*/
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

  .logout-font{
    float: right;
    font-size: 14px;
    line-height: 54px;
  }

  .login-user{
    float: right;
    margin:0 44px 0 0;
    width:178px;
    height:54px;
    line-height: 54px;
    font-size: 14px;
    color:#ffffff;
    overflow:hidden;text-overflow:ellipsis;white-space:nowrap;

  }

  .login-user-infomation{
    width:178px;
    height:54px;
    line-height: 54px;
    font-size: 14px;
    color:#ffffff;
    overflow:hidden;text-overflow:ellipsis;white-space:nowrap;
  }

  .logout-icon{
    float: right;
    cursor: pointer;
    width:58px;
  }

  .loginUserInfoRow{
    margin:0 !important;
  }


</style>
