
<template>
    <el-container class="home" >
        <el-header>
          <div class="work-left-menu-header">
            {{sysName}}
          </div>
          <div class="personal-infos">
            <div  @click="logout" class="logout-icon" >
              <icon class="fa-icon" name="logout2"></icon>
              <div class="logout-font">退出</div>
            </div>
            <div class="login-user">
              当前登录用户：{{loginUserInfo.user_name}}
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
  .home{
    width:100%;
    height:100%;
  }

  .el-container{
    height:100%;
    width: 100%;
  }

  .menu{
    background-color: #2e3d50;
    height:100%;
  }

  .work-left-menu-header{
    width:calc(100% - 200px);
    height:100%;
    float: left;
    text-align: left;
    font-size:20px;
  }

  .personal-infos{
    width:280px;
    height:100%;
    float:left;
    color:white;
    text-align: left;
  }

  .el-main{
    width:calc(100% - 200px);
    height:100%;
  }

  .el-header {
    background-color: #2b5ca9;
    color: #ffffff;
    line-height: 60px;
    padding:0 0 0 0;
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

</style>
