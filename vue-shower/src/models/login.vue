<template>

  <div class="login-container" >
    <vue-particle-line class="back-root">
      <div class="login-root" v-bind:class="loadingLogin" >
        <div class="cust-input-area">
          <el-form v-if="!resetPwd&&!selectType&&!forgetPwd&&!forgetResetPwd" class="login-form" autoComplete="on" ref="loginForm" label-position="left">
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
            <el-form-item>
              <el-button type="success" style="width:100%;" :loading="loading" @click="forgetPwdFunction">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                忘记密码
              </el-button>
            </el-form-item>
          </el-form>

          <el-form v-if="forgetPwd" class="login-form" autoComplete="on" ref="loginForm" label-position="left">
            <h3 class="title">请输入验证信息</h3>
            <!--<h3 class="title">欢迎！</h3>-->
            <el-form-item :error="forgetPwdObjError.user_name" prop="user_name">
              <el-input  name="user_name" type="text" v-model="forgetPwdObj.user_name" autoComplete="on"
                         placeholder="请输入用户名"/>
            </el-form-item>
            <el-form-item :error="forgetPwdObjError.phone_num" prop="phone_num">
              <el-input placeholder="请输入手机号" v-model="forgetPwdObj.phone_num"></el-input>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>
            <el-form-item :error="forgetPwdObjError.validate_code" prop="validate_code">
              <!--class="verify-code-form-item"-->
              <el-input class="verify-code-input" placeholder="请输入右侧验证码" v-model="forgetPwdObj.validate_code"></el-input>
              <Verify class="work-verify-code" ></Verify>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>

            <el-form-item :error="forgetPwdObjError.sms_validate_code" prop="sms_validate_code">
              <el-input placeholder="请输入短信验证码" v-model="forgetPwdObj.sms_validate_code">
              </el-input>
              <!--<span class="show-pwd" @click="showPwd"><svg-icon icon-class="eye" /></span>-->
            </el-form-item>

            <el-form-item>
              <el-button type="success" style="width:100%;" :loading="loading" @click="doForgetPwd">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                获取短信验证码
              </el-button>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" @click="doForgetPwd">
                <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
                确定
              </el-button>
            </el-form-item>
          </el-form>


          <el-form v-if="resetPwd||forgetResetPwd" class="login-form" autoComplete="on" ref="loginForm" label-position="left">
            <h3 v-if="resetPwd" class="title">您的密码已过期,请输入您的密码</h3>
            <h3 v-if="forgetResetPwd" class="title">请输入新的密码</h3>
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
            <el-row>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.user_name_cn" prop="office_phone">
                  <el-input placeholder="请输入真实姓名" v-model="selectOriginType.user_name_cn"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.social_code" prop="social_code">
                  <el-input placeholder="请输入统一社会信用代码" v-model="selectOriginType.social_code"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.office_phone" prop="office_phone">
                  <el-input placeholder="请输入办公电话" v-model="selectOriginType.office_phone"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.mobile_phone" prop="mobile_phone">
                  <el-input placeholder="请输入手机号" v-model="selectOriginType.mobile_phone"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="11">
                <el-form-item  :error="selectOriginTypeError.origin_type" prop="origin_type">
                  <el-select  v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择您的企业类型">
                    <el-option :key="key" v-for="(value, key) in originTypes" :label="value" :value="key"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.origin_nature" prop="origin_type">
                  <el-select  v-model="selectOriginType.origin_nature" style="width:100%;" placeholder="请选择您的企业性质">
                    <el-option :key="originNature.origin_nature_code"
                               v-for="originNature in originNatures" :label="originNature.origin_nature_name" :value="originNature.origin_nature_code">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="24">
                <el-form-item  :error="selectOriginTypeError.company_address" prop="social_code">
                  <el-cascader v-show="selectOriginType.modify_address" style="width: 100%;"
                               :placeholder = "selectOriginType.origin_address_str"
                               v-model="selectOriginType.company_address"
                               visible-change="changeCompanyAddress"
                               :props="areaCodeProps"
                  ></el-cascader>
                  <el-input v-show="!selectOriginType.modify_address"
                            :disabled="true" style="width: 100%;" v-model="selectOriginType.origin_address_str">
                    <template slot="append">
                      <el-button type="primary" @click="selectOriginType.modify_address=true">修改</el-button>
                    </template>
                  </el-input>

                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.origin_address_detail" prop="origin_address_detail">
                  <el-input placeholder="请输入企业注册地址" v-model="selectOriginType.origin_address_detail"> </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                &nbsp;
              </el-col>
              <el-col :span="11">
                <el-form-item :error="selectOriginTypeError.email" prop="email">
                  <el-input placeholder="请输入邮箱地址" v-model="selectOriginType.email"></el-input>
                </el-form-item>
              </el-col>

            </el-row>

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
  import { Loading } from 'element-ui'
  import Verify from '@/models/public/WorkVerifyCode'

  import staticCss from '../../static/custCss/static.css'

  export default {
  name: 'login',
    components: {
      Verify
    },
  data() {
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
        forgetPwdObj:{
          user_name:'',
          phone_num:'',
          validate_code:'',
          sms_validate_code:''
        },
        forgetPwdObjError:{
          user_name:'',
          phone_num:'',
          validate_code:'',
          sms_validate_code:''
        },
        areaCodeObj:{},
        areaCodeProps: {
          lazy: true,
          vueHandler :this,
          value:'area_code',
          // value:'area_name',
          label:'area_name',
          children:'children',
          lazyLoad (node, resolve) {
            const sendParams = {}
            if(node&&node.data&&node.data.area_code){
              sendParams['parentId'] = node.data.area_code
            }
            this.vueHandler.BaseRequest({
              url:"cqnyUser/getAreaData",
              method:"get",
              params:sendParams
            })
              .then(response=>{

                resolve(response)
                response.forEach(areaCodeObjTmp =>{
                  this.vueHandler.areaCodeObj[areaCodeObjTmp.area_code] = areaCodeObjTmp.area_name
                })
              })
              .catch(errorMsg=>{
                //console.log("response ......")
              });
          }
        },
      currOriginInfo:{},
      selectOriginType: {
          origin_type:'1',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:'',
          origin_nature:'',
          modify_address:false,
          company_address:[],
          origin_address_str:"请选择您的企业所在街道",
          origin_address_detail:''
        },
        selectOriginTypeError:{
          origin_type:'',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:'',
          origin_nature:'',
          modify_address:false,
          company_address:'',
          origin_address_str:"",
          origin_address_detail:''
        },
        originTypes:{
          '1':'燃气企业',
          '2':'管输企业'
        },
        originNatures:[],
        resetPwd:false,
        forgetResetPwd:false,
        selectType:false,
        forgetPwd:false,
        loading: false,
        loadingLogin:{
          'loading-login':true
        },
        pwdType: 'user_pwd',
        CanvasVerifyCodeObj:{}
    }
  },
  created: function () {

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
              this.checkUserOrigin()
              this.getOriginNature()
            }else if('USER_STATS_NOT_NORMAL'==response){
              $this.selectType = true
              this.selectOriginType.user_name = this.loginForm.user_name
              this.checkUserOrigin()
              this.getOriginNature()
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
    checkUserOrigin(){
      this.BaseRequest({
        url:"/cqnyUser/getCurrUserOrigin",
        method:"get"
      })
        .then(response=>{
          // debugger
          this.currOriginInfo = response.origin
          const currUserInfo = response.user
          this.selectOriginType.mobile_phone = currUserInfo.mobile_phone
          this.selectOriginType.user_name_cn = currUserInfo.user_name_cn
          this.selectOriginType.email = currUserInfo.email
          this.selectOriginType.office_phone = currUserInfo.office_phone
          this.selectOriginType.social_code = currUserInfo.social_code


          if(this.currOriginInfo.origin_address_province!=null&&
            this.currOriginInfo.origin_address_city!=null&&
            this.currOriginInfo.origin_address_area!=null&&
            this.currOriginInfo.origin_address_street!=null){
            this.selectOriginType.company_address = [
              this.currOriginInfo.origin_address_province,
              this.currOriginInfo.origin_address_city,
              this.currOriginInfo.origin_address_area,
              this.currOriginInfo.origin_address_street
            ]
          }else{
            this.selectOriginType.company_address = []
          }

          this.selectOriginType.origin_address_detail = this.currOriginInfo.origin_address_detail
          this.selectOriginType.origin_nature = this.currOriginInfo.origin_nature
          if(this.currOriginInfo.origin_address!=null&&this.currOriginInfo.origin_address!=''){
            this.selectOriginType.origin_address_str = this.currOriginInfo.origin_address
            this.selectOriginType.modify_address = false
          }else{
            this.selectOriginType.origin_address_str = '请选择企业所在街道'
            this.selectOriginType.modify_address = true
          }
        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
    },
    getOriginNature(){
      this.BaseRequest({
        url:"/cqnyUser/getOriginNature",
        method:"get"
      })
        .then(response=>{
          console.log(response)
          this.originNatures = response
        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
    },
    doForgetPwd(){
      let errorTag = false
      this.forgetPwdObjError.user_name = ""
      this.forgetPwdObjError.phone_num = ""
      this.forgetPwdObjError.validate_code = ""
      this.forgetPwdObjError.sms_validate_code = ""

      if(this.forgetPwdObj.user_name==null||this.forgetPwdObj.user_name==''||(this.forgetPwdObj.user_name==undefined) ){
        this.forgetPwdObjError.user_name='用户名不允许为空'
        errorTag = true
      }

      if(this.forgetPwdObj.phone_num==null||this.forgetPwdObj.phone_num==''||(this.forgetPwdObj.phone_num==undefined) ){
        this.forgetPwdObjError.phone_num='手机号不允许为空'
        errorTag = true
      }else{
        let regFormat = /^[1][3578][0-9]{9}$/; //正确手机号

        if (!(regFormat.test(this.forgetPwdObj.phone_num))) {
          this.forgetPwdObjError.phone_num = "请输入正确的手机号"
          errorTag = true
        }
      }

      if(this.forgetPwdObj.validate_code==null||this.forgetPwdObj.validate_code==''||(this.forgetPwdObj.validate_code==undefined) ){
        this.forgetPwdObjError.validate_code='验证码不允许为空'
        errorTag = true
      }

      if(this.forgetPwdObj.sms_validate_code==null||this.forgetPwdObj.sms_validate_code==''||(this.forgetPwdObj.sms_validate_code==undefined) ){
        this.forgetPwdObjError.sms_validate_code='短信验证码不允许为空'
        errorTag = true
      }

      if(errorTag){
        return
      }

      this.$http({
        url:process.env.BASE_API+"cqnyUser/forgetPwd",
        method:"post",
        withCredentials:true,
        data:this.forgetPwdObj
      })
        .then(response=>{
          console.log(response)
          if(response&&response.data){
            if(response.data.result!='SUCCESS'){
              this.Message.error(response.data.resultData)
            }else{
              this.forgetPwd = false
              this.selectType = false
              this.loading = false
              this.forgetResetPwd = true
            }

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
            withCredentials:true,
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
      this.selectOriginTypeError.origin_nature = ""
      this.selectOriginTypeError.company_address = ""
      this.selectOriginTypeError.origin_address_str = ""
      this.selectOriginTypeError.origin_address_detail = ""

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
        this.selectOriginTypeError.user_name_cn='姓名不允许为空'
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

      if(this.selectOriginType.company_address==null||this.selectOriginType.company_address==''||
        (this.selectOriginType.company_address==undefined)||(this.selectOriginType.company_address.length<1) ){
        this.selectOriginTypeError.company_address='企业注册地址不允许为空'
        errorTag = true
      }if(this.selectOriginType.origin_address_detail==null||this.selectOriginType.origin_address_detail==''||
        (this.selectOriginType.origin_address_detail==undefined) ){
        this.selectOriginTypeError.origin_address_detail='企业详细地址不允许为空'
        errorTag = true
      }

      if(this.selectOriginType.origin_nature==null||this.selectOriginType.origin_nature==''||(this.selectOriginType.origin_nature==undefined) ){
        this.selectOriginTypeError.origin_nature='企业性质不允许为空'
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

      if(this.selectOriginType.modify_address){
        const origin_address_province = this.selectOriginType.company_address[0]
        const origin_address_city = this.selectOriginType.company_address[1]
        const origin_address_area = this.selectOriginType.company_address[2]
        const origin_address_street = this.selectOriginType.company_address[3]

        if(this.areaCodeObj[origin_address_province]&&this.areaCodeObj[origin_address_city]&&this.areaCodeObj[origin_address_area]
          &&this.areaCodeObj[origin_address_street]){
          const origin_address = this.areaCodeObj[origin_address_province]+"/"+
            this.areaCodeObj[origin_address_city]+"/"+
            this.areaCodeObj[origin_address_area]+"/"+
            this.areaCodeObj[origin_address_street]


          this.selectOriginType["origin_address_province"] = origin_address_province
          this.selectOriginType["origin_address_city"] = origin_address_city
          this.selectOriginType["origin_address_area"] = origin_address_area
          this.selectOriginType["origin_address_street"] = origin_address_street
          this.selectOriginType["origin_address"] = origin_address
        }
      }

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
          url:"cqnyUser/changeSelfOriginType",
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
    },
    forgetPwdFunction:function(){
      this.forgetPwd = true
    },
    getSmsValidate(){

      let errorTag = false
      this.forgetPwdObjError.user_name = ""
      this.forgetPwdObjError.phone_num = ""
      this.forgetPwdObjError.validate_code = ""
      this.forgetPwdObjError.sms_validate_code = ""


      if(this.forgetPwdObj.user_name==null||this.forgetPwdObj.user_name==''||(this.forgetPwdObj.user_name==undefined) ){
        this.forgetPwdObjError.user_name='用户名不允许为空'
        errorTag = true
      }

      if(this.forgetPwdObj.phone_num==null||this.forgetPwdObj.phone_num==''||(this.forgetPwdObj.phone_num==undefined) ){
        this.forgetPwdObjError.phone_num='手机号不允许为空'
        errorTag = true
      }else{
        let regFormat = /^[1][3578][0-9]{9}$/; //正确手机号

        if (!(regFormat.test(this.forgetPwdObj.phone_num))) {
          this.forgetPwdObjError.phone_num = "请输入正确的手机号"
          errorTag = true
        }
      }

      if(this.forgetPwdObj.validate_code==null||this.forgetPwdObj.validate_code==''||(this.forgetPwdObj.validate_code==undefined) ){
        this.forgetPwdObjError.validate_code='验证码不允许为空'
        errorTag = true
      }

      if(errorTag){
        return
      }

      this.$http({
        url:process.env.BASE_API+"cqnyUser/getSmsValidateCode",
        method:"get",
        params:this.forgetPwdObj
      })
      .then(response=>{
        console.log(response)
        if(response&&response.data){
          if(response.data.result!='SUCCESS'){
            this.Message.error(response.data.resultData)
          }
        }else{
          this.Message.success("验证码获取成功，有效期5分钟")
        }
      })
      .catch(errorMsg=>{
        //console.log("response ......")
      });
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
            height: 47px;
            width: 100%;
            input {
                background: transparent !important;
                border: 0px;
                -webkit-appearance: none;
                border-radius: 0px;
                padding: 12px 5px 12px 35px;
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
          width: 500px;
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

  .verify-code-input{
    width:calc(100% - 155px) !important;
    padding-left:23px;
  }

  .work-verify-code{
    height:44px;
    margin:1px 0 0 0;
    width:130px !important;
    float:right;
  }

  /*.wx-image {*/
    /*position:absolute;*/
    /*bottom:0;*/
    /*right:0;*/
  /*}*/


  .verify-code-form-item{
    border: 0px !important;
    background-color:rgba(0,0,0,0) !important;
    border-radius: 5px;
    color: #454545;
  }


</style>
