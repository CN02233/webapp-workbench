<template>
  <div class="work-main-context">
    <slot></slot>
  </div>
</template>

<script>
    export default {
        name: 'WorkMainContext',
        data() {
            return {}
        },
        methods: {}
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .work-main-context{
    height:calc(100% - 60px);
    margin:0 20px 0 20px;
  }
</style>
