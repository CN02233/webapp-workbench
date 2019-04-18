<template>
  <div class="menu-logo">
    <div class="logo">
      <icon name="logo"></icon>

    </div>
    <div class="logo-content">{{sysName}}</div>

  </div>
</template>

<script>
  import '@/styles/logo.css'


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
    color:#ffffff;
    /*color:black;*/
  }

  .fa-icon {
    width:40px;
    height:40px;
    margin:15px 10px 0 10px;
  }

  .logo{
    float: left;
  }

  .logo-content{
    float: left;
    /*width:calc(100% - 42px);*/
    height: 65px;
    line-height:70px;
    margin-left:20px;
  }

</style>
