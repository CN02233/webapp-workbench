<template>
    <div class="menu-logo">
      {{sysName}}
    </div>
</template>

<script>
    export default {
      name: 'WorkLogo',
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
    color:#ffffff;
  }
</style>
