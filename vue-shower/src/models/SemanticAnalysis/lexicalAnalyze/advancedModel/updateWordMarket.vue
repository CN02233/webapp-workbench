<template>
  <div class="update-word-market-root">
    <el-row :key="updateWord.wordName"  v-for="updateWord in updateWordMarketList" class="update-word-row">
      <el-col :span="4">词条：{{updateWord.term}}</el-col>
      <el-col :span="4">词性：{{wordType[updateWord.pos]}}</el-col>
      <el-col :span="4">词频：{{updateWord.freq}}</el-col>
    </el-row>

    <el-row class="next-step-btn">
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="confirm();goToNextStep()">提交</el-button>
      </el-col>
    </el-row>
  </div>

</template>

<script>
    export default {
        name: 'UpdateWordMarket',
        data() {
            return {
              updateWordMarketList:this.$route.params.result,
              wordType:{
                'n':'名词',
                'v':'动词',
                'a':'形容词',
                'r':'代词',
                'm':'数词',
                'q':'量词',
                'd':'副词',
                'p':'介词',
                'c':'连词',
                'u':'助词',
                'e':'叹词',
                'y':'语气词',
                'o':'拟声词'
              },
              updateWordList:[
                {wordName:'111',wordType:'aaa',wordRate:'99'},
                {wordName:'222',wordType:'b',wordRate:'99'},
                {wordName:'3',wordType:'c',wordRate:'99'},
                {wordName:'4',wordType:'d',wordRate:'99'},
                {wordName:'5',wordType:'e',wordRate:'99'}
              ]
            }
        },
        methods: {
          confirm(){
            const sendData = {"uuid":this.$route.params.uuid}
            this.Request({
              url:'/process/save',
              method:'post',
              data:sendData
            }).then(response=>{
              if(response.status!='0'){
                this.Message.error(response.message)
              }else{
                this.Message("保存成功")
                this.$emit('nextStep')
              }
            })
          },
          goToNextStep(){
            this.$emit('nextStep')
          }
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .update-word-market-root{
    padding:50px 0 0 0;
  }
  .update-word-row{
    margin:20px 0 20px 0;
  }
</style>
