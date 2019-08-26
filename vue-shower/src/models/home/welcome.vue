<template>
    <div class="welcome-root">
      <div class="self-report"></div>
      <div class="self-report-chart"></div>
      <div class="son-origin-report"></div>
    </div>
</template>

<script>
    export default {
        name: 'Welcome',
        data() {
            return {}
        },
        methods: {}
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .welcome-root{
    width:100%;
    height:100%;
    /*background: url("/static/image/bga_bc72234.png") no-repeat center center fixed;*/
    background: url("/nlp/static/image/bga_bc72234.png") no-repeat center center fixed;
    background-size: 100%;
  }

  .self-report{
    width:100%;
    height:40px;
    /*border:1px solid black;*/
  }

  .self-report-chart,.son-origin-report{
    width:50%;
    height:calc(100% - 40px);
    /*border:1px solid black;*/
  }

</style>
