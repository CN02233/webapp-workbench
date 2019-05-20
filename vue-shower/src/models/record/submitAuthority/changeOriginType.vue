<template>
  <WorkMain :headerItems="['机构类型','机构类型编辑']">
    <el-form  class="change-origin-type-root" :label-position="left" label-width="20%" :model="formData">
      <el-form-item :size="small" label="当前机构类型" >
        <el-input :disabled="true" :value="originTypeObj[currOriginInfo.origin_type]"></el-input>
      </el-form-item>

      <el-form-item :size="small" label="修改机构类型" >
        <el-select v-model="selectOriginType.origin_type" style="width:100%;" placeholder="请选择机构类型">
          <el-option
            v-for="item in originType"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
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
          origin_type:'1'
        },
        originTypeObj:{
          '1':'燃气企业',
          '2':'管输企业'
        },
        originType: [ {
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
        const originCode = this.selectOriginType['origin_type']
        const origin_type_name = this.originTypeObj[''+originCode]
        this.$confirm('确定您的企业类型为【'+origin_type_name+'】？', '提示', {
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
            method:"get",
            params:this.selectOriginType
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
          this.currOriginInfo = response
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
