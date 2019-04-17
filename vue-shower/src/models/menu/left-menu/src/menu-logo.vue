<template>
  <div class="menu-logo">
    <div class="logo-pic"></div>
    <div class="logo-content">{{sysName}}</div>

  </div>
</template>

<script>
  export default {
    name: 'WorkLeftLogo',
    components: {},
    data() {
      return {}
    },
    props:{
      sysName:{
        type:String
      }
    },
    methods: {}
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .menu-logo{
    text-align: left;
    font-size:20px;
    font-weight: bold;
    /*color:#ffffff;*/
    color:black;
  }

  .logo-pic{
    /*background:url('/static/image/logo.png') no-repeat 0 13px;*/
    /*background:url('/nlp/static/image/logo.png') no-repeat 0 13px;*/
    background-size: 26px 26px;
    width:40px;
    height:70px;
    float: left;
  }

  .logo-content{
    float: right;
    width:calc(100% - 42px);
    height: 65px;
    line-height:65px;
  }

</style>
