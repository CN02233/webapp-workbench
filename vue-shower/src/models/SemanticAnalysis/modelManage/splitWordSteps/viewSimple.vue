<template>
  <div>
    <el-row v-for="simpleData in simpleResult">
      <el-col :span="20">
        {{simpleData}}
      </el-col>
    </el-row>

    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subNextStep">完成</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
    name: 'ViewSimple',
    data() {
      return {
        simpleResult:""
      }
    },
    methods: {
      subNextStep(){
        this.$emit('nextStep',null);
      }
    },
    mounted:function(){
      this.simpleResult = this.$route.params.result.result
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-row{
    margin:20px 0 0 0;
  }

  .el-col{
    margin-top:10px;
    text-align: left;
  }
  .el-radio{
    margin-top:10px;
    margin-bottom:10px;
  }

  .base-modal-btns{
    text-align: center;
    margin-top:40px;
  }
</style>
