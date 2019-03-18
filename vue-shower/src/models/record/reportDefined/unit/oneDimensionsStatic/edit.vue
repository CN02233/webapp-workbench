
<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送单元','一维单元设置']">


  </WorkMain>


</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: "oneDimensionsStaticEdit",
    describe:"一维静态报表单元修改",
    components: {
      WorkTablePager,
      WorkMain
    }
  }
</script>

<style scoped>

</style>
