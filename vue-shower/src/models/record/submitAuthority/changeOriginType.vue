<template>
  <WorkMain :headerItems="['机构类型','基本信息']">
    <el-form  class="change-origin-type-root" :label-position="left" label-width="20%" :model="formData">
      <el-form-item :size="small" label="当前机构类型" >
        <el-input :disabled="true" :value="originTypeObj[currOriginInfo.origin_type]"></el-input>
      </el-form-item>

      <el-form-item label="机构类型" >
        <el-select  v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择您的企业类型">
          <el-option :key="label" v-for="originTypesObj in originTypes" :label="originTypesObj.label" :value="originTypesObj.value"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="真实姓名" :error="selectOriginTypeError.user_name_cn" prop="office_phone">
        <el-input placeholder="请输入办公电话" v-model="selectOriginType.user_name_cn"></el-input>
      </el-form-item>
      <el-form-item label="办公电话"  :error="selectOriginTypeError.office_phone" prop="office_phone">
        <el-input placeholder="请输入办公电话" v-model="selectOriginType.office_phone"></el-input>
      </el-form-item>
      <el-form-item label="手机号" :error="selectOriginTypeError.mobile_phone" prop="mobile_phone">
        <el-input placeholder="请输入手机号" v-model="selectOriginType.mobile_phone"></el-input>
      </el-form-item>
      <el-form-item label="邮箱地址" :error="selectOriginTypeError.email" prop="email">
        <el-input placeholder="请输入邮箱地址" v-model="selectOriginType.email"></el-input>
      </el-form-item>
      <el-form-item label="统一社保代码" :error="selectOriginTypeError.social_code" prop="social_code">
        <el-input placeholder="请输入统一社保代码" v-model="selectOriginType.social_code"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" style="width:100px;" @click="changeOriginType">
          <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
          确定
        </el-button>
      </el-form-item>
    </el-form>
  </WorkMain>


</template>

<script>
  import WorkMain from '@/models/public/WorkMain'

  export default {
    name: "ChangeOriginType",

    data() {
      return {
        currOriginInfo:{},
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
        originTypeObj:{
          '1':'燃气企业',
          '2':'管输企业'
        },
        originTypes: [ {
          value: '1',
          label: '燃气企业'
        }, {
          value: '2',
          label: '管输企业'
        }]
      }
    },
    components: {
      WorkMain
    },
    methods: {
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
        }
        if(this.selectOriginType.social_code==null||this.selectOriginType.social_code==''||(this.selectOriginType.social_code==undefined) ){
          this.selectOriginTypeError.social_code='统一社会信用代码不允许为空'
          errorTag = true
        }

        if(errorTag){
          return
        }

        let regFormat = /^[1][3578][0-9]{9}$/; //正确手机号

        if (!(regFormat.test(this.selectOriginType.mobile_phone))) {
          this.selectOriginTypeError.mobile_phone = "请输入正确的手机号"
          errorTag = true
        }

        let mal = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;

        if(!(mal.test(this.selectOriginType.email))) {
          this.selectOriginTypeError.email = "请输入正确的邮箱地址"
          errorTag = true
        }

        if(errorTag){
          return
        }

        const originCode = this.selectOriginType['origin_type']
        const origin_type_name = this.originTypeObj[''+originCode]
        this.$confirm('确定您的基本信息？', '提示', {
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
            this.currOriginInfo.origin_type = originCode
            this.Message.success("修改成功")

          })
          .catch(errorMsg=>{
            //console.log("response ......")
          });
        }).catch(() => {
        });
      }
    },
    mounted:function () {
      // getCurrUserOrigin
      this.BaseRequest({
        url:"cqnyUser/getCurrUserOrigin",
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
        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
    }
  }
</script>

<style scoped>
  .change-origin-type-root{
    max-width: 500px;
    text-align: center;
    margin: 50px 0 0 50px ;
  }
</style>
