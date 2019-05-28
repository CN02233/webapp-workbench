<template>

  <div class="login-container" >
    <vue-particle-line class="back-root">
      <div class="login-root" v-bind:class="loadingLogin" >
        <div class="cust-input-area">
          <el-form v-if="!resetPwd&&!selectType" class="login-form" autoComplete="on" ref="loginForm" label-position="left">
            <h3 class="title">天然气输配价格监管系统</h3>
            <!--<h3 class="title">欢迎！</h3>-->
            <el-form-item  prop="user_name">
              <el-input  name="user_name" type="text" v-model="loginForm.user_name" autoComplete="on"
                         placeholder="请输入用户名"/>
            </el-form-item>
            <el-form-item  prop="user_pwd">
              <el-input placeholder="请输入密码" v-model="loginForm.user_pwd" show-password></el-input>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>

            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" @click.native.prevent="handleLogin">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                登入系统
              </el-button>
            </el-form-item>
          </el-form>

          <el-form v-if="resetPwd" class="login-form" autoComplete="on" ref="loginForm" label-position="left">
            <h3 class="title">您的密码已过期,请输入您的密码</h3>
            <!--<h3 class="title">欢迎！</h3>-->
            <el-form-item  prop="user_name">
              <el-input  name="user_name" type="text" v-model="changePwdForm.user_name" autoComplete="on"
                         placeholder="请输入用户名"/>
            </el-form-item>
            <el-form-item  prop="user_pwd">
              <el-input placeholder="请输入密码" v-model="changePwdForm.user_pwd" show-password></el-input>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>
            <el-form-item  prop="user_pwd">
              <el-input placeholder="请确认密码" v-model="changePwdForm.validate_user_pwd" show-password></el-input>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>


            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" @click="changePwd">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                确定
              </el-button>
            </el-form-item>
          </el-form>

          <el-form v-if="selectType" class="login-form-selecttype"
                   :model="selectOriginType"
                   autoComplete="on" ref="selectTypeForm" label-position="left">
            <h3 class="title">请完善您的信息</h3>
            <!--<h3 class="title">欢迎！</h3>-->
            <el-form-item  prop="origin_type">
              <el-select  v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择您的企业类型">
                <el-option :key="key" v-for="(value, key) in originTypes" :label="value" :value="key"></el-option>
              </el-select>

            </el-form-item>

            <el-form-item :error="selectOriginTypeError.user_name_cn" prop="office_phone">
              <el-input placeholder="请输入真实姓名" v-model="selectOriginType.user_name_cn"></el-input>
            </el-form-item>
            <el-form-item :error="selectOriginTypeError.office_phone" prop="office_phone">
              <el-input placeholder="请输入办公电话" v-model="selectOriginType.office_phone"></el-input>
            </el-form-item>
            <el-form-item :error="selectOriginTypeError.mobile_phone" prop="mobile_phone">
              <el-input placeholder="请输入手机号" v-model="selectOriginType.mobile_phone"></el-input>
            </el-form-item>
            <el-form-item :error="selectOriginTypeError.email" prop="email">
              <el-input placeholder="请输入邮箱地址" v-model="selectOriginType.email"></el-input>
            </el-form-item>
            <el-form-item :error="selectOriginTypeError.social_code" prop="social_code">
              <el-input placeholder="请输入统一社保代码" v-model="selectOriginType.social_code"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" @click="changeOriginType">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                确定
              </el-button>
            </el-form-item>
          </el-form>
        </div>



      </div>





    </vue-particle-line>

    <div class="wx-image-area">
      <div class="wx-image">
        <div></div>
      </div>
    </div>


  </div>


</template>

<script>
  import { Loading } from 'element-ui';

  import staticCss from '../../static/custCss/static.css'

  export default {
  name: 'login',
  data() {

    // var phoneValidator = (rule, value, callback) => {
    //   let regFormat = /^[1][3578][0-9]{9}$/; //正确手机号
    //
    //
    //   if (!value) {
    //     console.log(value+"why")
    //     return callback(new Error('请输入手机号'));
    //   }
    //   if (!(regFormat.test(value))) {
    //     callback(new Error('请输入正确手机号'));
    //   } else {
    //     if (value < 18) {
    //       callback(new Error('必须大于18岁'));
    //     }else {
    //       callback();
    //     }
    //   }
    // };

    var emailValidator = (rule, value, callback)  => {
      let mal = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
      console.log(value+"???")

      if (!value) {
        console.log(value+"yes")

        return callback(new Error('请输入邮箱地址'));
      }
      if(!(mal.test(value))) {
        callback(new Error('请输入正确邮箱'));
      }else{
        callback();
      }
    };
    return {
        loginForm: {
            user_name: '',
            user_pwd: ''
        },
        changePwdForm: {
          user_name: '',
          user_pwd: '',
          validate_user_pwd: ''
        },
        selectOriginType: {
          origin_type:'1',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:''
        },
        selectOriginTypeError:{
          origin_type:'1',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:''
        },
        originTypes:{
          '1':'燃气企业',
          '2':'管输企业'
        },
        resetPwd:false,
        selectType:false,
        loading: false,
        loadingLogin:{
          'loading-login':true
        },
        pwdType: 'user_pwd'
    }
  },
  methods: {
    showPwd() {
        if (this.pwdType === 'user_pwd') {
            this.pwdType = ''
        } else {
            this.pwdType = 'user_pwd'
        }
    },
    handleLogin() {
      const $this = this
      try{
        this.BaseRequest({
          url:"sys/login/doLogin.do",
          method:"get",
          params:this.loginForm
        })
        .then(response=>{
            if('LOGIN_SUCCESS'==response){
              $this.forwardToHome()
            }else if('PWD_EXPIRED'==response){
              $this.resetPwd = true
              this.changePwdForm.user_name = this.loginForm.user_name
              this.checkUser()
            }else if('NEVER_LOGIN'==response){
              $this.selectType = true
              this.selectOriginType.user_name = this.loginForm.user_name
              this.checkUser()
            }else if('USER_STATS_NOT_NORMAL'==response){
              $this.selectType = true
              this.selectOriginType.user_name = this.loginForm.user_name
              this.checkUser()
            }

        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
      }catch(e){
        //console.log("catch ......"+e)
      }
    },
    checkUser(){
      this.BaseRequest({
        url:"cqnyUser/getUserInfo",
        method:"get",
        params:{'user_name':this.loginForm.user_name}
      })
        .then(response=>{
          if(response){
            this.selectOriginType.mobile_phone = response.mobile_phone
            this.selectOriginType.user_name_cn = response.user_name_cn
            this.selectOriginType.email = response.email
            this.selectOriginType.office_phone = response.office_phone
            this.selectOriginType.social_code = response.social_code
          }
        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
    },
    changePwd(){
      if(this.changePwdForm&&this.changePwdForm.user_name&&this.changePwdForm.user_pwd&&this.changePwdForm.validate_user_pwd){
        if(this.changePwdForm.user_pwd!=this.changePwdForm.validate_user_pwd)
        {
          this.Message.error("两次输入密码不一致")
          this.changePwdForm.user_pwd = ""
          this.changePwdForm.validate_user_pwd = ""
        }else{
          this.BaseRequest({
            url:"sys/user/changePwd",
            method:"post",
            data:this.changePwdForm
          })
          .then(response=>{
            if('SUCCESS'==response){
              this.forwardToHome()
            }else{
              if(response.faild_reason){
                this.Message.error(response.faild_reason)

              }else if(response.result_msg){
                this.Message.error(response.result_msg)

              }else{
                this.Message.error("登陆失败")
              }
            }
          })
          .catch(errorMsg=>{
            //console.log("response ......")
          });
        }
      }else{
        this.Message.error("请输入用户名以及密码")
      }
    },
    changeOriginType(){
      let errorTag = false
      this.selectOriginTypeError.mobile_phone = ""
      this.selectOriginTypeError.office_phone = ""
      this.selectOriginTypeError.email = ""
      this.selectOriginTypeError.social_code = ""
      this.selectOriginTypeError.user_name_cn = ""

      if(this.selectOriginType.mobile_phone==null||this.selectOriginType.mobile_phone==''||(this.selectOriginType.mobile_phone==undefined) ){
        this.selectOriginTypeError.mobile_phone='手机号不允许为空'
        errorTag = true
      }else{
        let regFormat = /^[1][3578][0-9]{9}$/; //正确手机号

        if (!(regFormat.test(this.selectOriginType.mobile_phone))) {
          this.selectOriginTypeError.mobile_phone = "请输入正确的手机号"
          errorTag = true
        }
      }
      if(this.selectOriginType.user_name_cn==null||this.selectOriginType.user_name_cn==''||(this.selectOriginType.user_name_cn==undefined) ){
        this.selectOriginTypeError.user_name='姓名不允许为空'
        errorTag = true
      }
      if(this.selectOriginType.office_phone==null||this.selectOriginType.office_phone==''||(this.selectOriginType.office_phone==undefined) ){
        this.selectOriginTypeError.office_phone='办公电话不允许为空'
        errorTag = true
      }
      if(this.selectOriginType.email==null||this.selectOriginType.email==''||(this.selectOriginType.email==undefined) ){
        this.selectOriginTypeError.email='邮箱地址不允许为空'
        errorTag = true
      }else{
        let mal = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;

        if(!(mal.test(this.selectOriginType.email))) {
          this.selectOriginTypeError.email = "请输入正确的邮箱地址"
          errorTag = true
        }
      }

      if(this.selectOriginType.social_code==null||this.selectOriginType.social_code==''||(this.selectOriginType.social_code==undefined) ){
        this.selectOriginTypeError.social_code='统一社会信用代码不允许为空'
        errorTag = true
      }

      if(errorTag){
        return
      }

      this.$refs['selectTypeForm'].validate((valid) => {
        if (valid) {
          return true
        } else {
          return false;
        }
      });

      this.$confirm('确定您的企业类型为【'+this.originTypes[this.selectOriginType.origin_type]+'】？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString:true,
        type: 'warning'
      }).then(() => {
        const loading = this.$loading({
          lock: true,
          text: '设定中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        this.BaseRequest({
          url:"cqnyUser/selectOriginType",
          method:"post",
          data:this.selectOriginType
        })
        .then(response=>{
          // debugger
          loading.close()
          if(!response){
            this.selectType = false
            this.resetPwd = false
          }

          if('SUCCESS'==response){
            this.forwardToHome()
          }else{
            if(response.faild_reason){
              this.Message.error(response.faild_reason)

            }else if(response.result_msg){
              this.Message.error(response.result_msg)

            }else{
              this.Message.error("登陆失败")
            }
          }
        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
      }).catch(() => {
      });


    },
    forwardToHome:function(){
      this.$router.push({'path':'/home'})
    }
  },
  mounted:function () {
    //检查用户是否已经登录
    let loadingInstance = Loading.service({ fullscreen: true,background:'rgba(0, 0, 0, 0.7)',	text:'加载中........' });
    const $this = this
    this.$http.post(process.env.BASE_API+"/sys/user/userMenuList.do",{},{withCredentials: true})
      .then(response => {
        let res = response.data
        try{
          res = JSON.parse(response.data)
        }catch(e){
          //console.log(e)
        }

        if (res.result !== 'SUCCESS') {
          if (res.faild_reason === 'USER_NOT_LOGIN') {
            freeLoading()
          }else{
            freeLoading()
          }
        }else{
          // cqnyUser/getUserInfo
          if(res.resultData=='PWD_EXPIRED'){
            //console.log("重定向到修改密码页面")
          }else if(res.resultData=='NEVER_LOGIN'){//用户首次登陆
            //console.log("重定向到修改密码页面")
          }else if(res.resultData=='USER_STATS_NOT_NORMAL'){//用户需要选择企业类型
            //console.log("重定向到修改密码页面")
          }else{
            freeLoading()
            this.forwardToHome()
          }
        }
      })
      .catch(error=>{
        $this.Message.error("获取用户登录状态过程中出现异常："+error)
        freeLoading()
      })

    function freeLoading(){
      if(loadingInstance.visible){
        loadingInstance.close()
        $this.loadingLogin['loading-login']=false
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
    $bg: #2d3a4b;
    $light_gray: #eee;

    /* reset element-ui css */
    .login-container {
        .el-input {
            display: inline-block;
            height: 47px;
            width: 85%;
            input {
                background: transparent;
                border: 0px;
                -webkit-appearance: none;
                border-radius: 0px;
                padding: 12px 5px 12px 15px;
                color: $light_gray;
                height: 47px;
                &:-webkit-autofill {
                    -webkit-box-shadow: 0 0 0px 1000px $bg inset !important;
                    -webkit-text-fill-color: #fff !important;
                }
            }
        }
        .el-form-item {
            border: 1px solid rgba(255, 255, 255, 0.1);
            background: rgba(0, 0, 0, 0.1);
            border-radius: 5px;
            color: #454545;
        }
    }

</style>

<style rel="stylesheet/scss" lang="scss" scoped>

    $bg: #2d3a4b;
    $dark_gray: #889aa4;
    $light_gray: #eee;
    .login-container {
        position: fixed;
        height: 100%;
        width: 100%;
        background-color: $bg;
        .login-form {
          position: absolute;
          z-index: 10086;
          left: 0;
          right: 0;
          width: 300px;
          padding: 35px 35px 15px 35px;
          margin: 120px auto;
        }
        .login-form-selecttype{
          position: absolute;
          z-index: 10086;
          left: 0;
          right: 0;
          width: 300px;
          padding: 35px 35px 15px 35px;
          margin: 60px auto;
        }
        .tips {
            font-size: 14px;
            color: #fff;
            margin-bottom: 10px;
            span {
                &:first-of-type {
                    margin-right: 16px;
                }
            }
        }
        .svg-container {
            padding: 6px 5px 6px 15px;
            color: $dark_gray;
            vertical-align: middle;
            width: 30px;
            display: inline-block;
            &_login {
                font-size: 20px;
            }
        }
        .title {
            font-size: 20px;
            font-weight: 100;
            color: $light_gray;
            margin: 0px auto 40px auto;
            text-align: center;
            font-weight: bold;
        }
        .show-pwd {
            position: absolute;
            right: 10px;
            top: 7px;
            font-size: 16px;
            color: $dark_gray;
            cursor: pointer;
            user-select: none;
        }
    }
  .loading-login{
    visibility:hidden;
  }

  .vue-particle-line>div{
    width:100%;
    height: 100% !important;
  }

  .login-root{
    width:100%;
    height: 100%;
  }

  .cust-input-area{
    width:100%;
    height:calc(100% - 200px) !important;
    float:left;
    /*border:1px solid black;*/
  }



  /*.wx-image {*/
    /*position:absolute;*/
    /*bottom:0;*/
    /*right:0;*/
  /*}*/



</style>
