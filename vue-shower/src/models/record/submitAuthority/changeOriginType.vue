<template>
  <WorkMain  :headerItems="['机构类型','基本信息']">
    <el-row class="change-origin-type-root">
      <el-form  class="change-origin-type-form" label-width="30%" :model="selectOriginType">
        <el-form-item label="当前机构类型" >
          <el-input :disabled="true" :value="originTypeObj[currOriginInfo.origin_type]"></el-input>
        </el-form-item>

        <el-form-item label="机构类型" >
          <el-select  v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择您的企业类型">
            <el-option :key="originTypesObj.label" v-for="originTypesObj in originTypes" :label="originTypesObj.label" :value="originTypesObj.value"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="企业性质" :error="selectOriginTypeError.origin_nature" prop="social_code">
          <el-select  v-model="selectOriginType.origin_nature" style="width:100%;" placeholder="请选择您的企业性质">
            <el-option :key="originNature.origin_nature_code"
                       v-for="originNature in originNatures" :label="originNature.origin_nature_name" :value="originNature.origin_nature_code">
            </el-option>
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
        <el-form-item label="统一社会信用代码" :error="selectOriginTypeError.social_code" prop="social_code">
          <el-input placeholder="请输入统一社会信用代码" v-model="selectOriginType.social_code"></el-input>
        </el-form-item>

        <el-form-item label="企业注册街道"  :error="selectOriginTypeError.company_address" prop="social_code">
          <el-cascader v-show="selectOriginType.modify_address" style="width: 100%;"
                       :placeholder = "selectOriginType.origin_address_str"
                       v-model="selectOriginType.company_address"
                       visible-change="changeCompanyAddress"
                       :props="areaCodeProps"
          ></el-cascader>
          <el-input v-show="!selectOriginType.modify_address" :disabled="true" style="width: 100%;" v-model="selectOriginType.origin_address_str">
            <template slot="append"><el-button @click="selectOriginType.modify_address=true">修改</el-button></template>
          </el-input>

        </el-form-item>

        <el-form-item label="企业注册地址" :error="selectOriginTypeError.origin_address_detail" prop="social_code">
          <el-input style="width: 100%;" v-model="selectOriginType.origin_address_detail">
          </el-input>
        </el-form-item>


        <el-form-item>
          <el-button type="primary" style="width:100px;" @click="changeOriginType">
            <!--<el-button type="primary" style="width:100%;" :loading="loading">-->
            确定
          </el-button>
        </el-form-item>
      </el-form>

    </el-row>
  </WorkMain>


</template>

<script>
  import WorkMain from '@/models/public/WorkMain'

  export default {
    name: "ChangeOriginType",

    data() {
      return {
        currOriginInfo:{},
        originNatures:[],
        selectOriginType: {
          origin_type:'1',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:'',
          modify_address:false,
          company_address:[],
          origin_address_str:"",
          origin_address_detail:'',
          origin_nature:''
        },
        selectOriginTypeError:{
          origin_type:'1',
          user_name:'',
          user_name_cn:'',
          mobile_phone:'',
          office_phone:'',
          email:'',
          social_code:'',
          company_address:'',
          origin_address_detail:'',
          origin_nature:''
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
        }],
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
        }
      }
    },
    components: {
      WorkMain
    },
    methods: {
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
      },
      changeCompanyAddress(){
        console.log("changeCompanyAddress is running....")
        this.selectOriginType.modify_address = false

      }
    },
    computed:{
      formatterCompanyAddress(){
        // console.log('active item:', val);
        let showAddress = ""
        if(this.selectOriginType.company_address&&this.selectOriginType.company_address.length>0){
          this.selectOriginType.company_address.forEach((compamyAddressObj,index) =>{
            if(index>0)
              showAddress+="/"
            showAddress+=compamyAddressObj
          })
        }
        return showAddress
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


          this.selectOriginType.company_address = [
            this.currOriginInfo.origin_address_province,
            this.currOriginInfo.origin_address_city,
            this.currOriginInfo.origin_address_area,
            this.currOriginInfo.origin_address_street
          ]
          this.selectOriginType.origin_address_detail = this.currOriginInfo.origin_address_detail
          this.selectOriginType.origin_address_str = this.currOriginInfo.origin_address
          this.selectOriginType.origin_nature = this.currOriginInfo.origin_nature

        })
        .catch(errorMsg=>{
          //console.log("response ......")
        });
      this.getOriginNature()

    }
  }
</script>

<style scoped>
  .change-origin-type-root{
    /*max-width: 500px;*/
    text-align: center;
    height: 100%;
    margin: 50px 0 0 50px ;
    overflow: auto;
  }

  .change-origin-type-form{
    max-width:500px;
  }
</style>
