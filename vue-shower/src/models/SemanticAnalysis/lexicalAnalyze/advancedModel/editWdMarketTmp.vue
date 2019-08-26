<template>
  <div>
    <el-form :inline="true" :model="formInline" class="edit-word-market-form">
      <div v-for="(wordMarketTmp,index) in wordMarketTmpList">
        <el-form-item class="word-markget-item" label="词条：">
          <el-input v-model="wordMarketTmp.term" placeholder="词条"></el-input>
        </el-form-item>
        <el-form-item class="word-markget-item" label="词性：">
          <el-select v-model="wordMarketTmp.pos" placeholder="词性">
            <el-option :key="key" v-for="(value, key) in wordType" :label="value" :value="key"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="word-markget-item" label="词频：">
          <el-input v-model="wordMarketTmp.freq" placeholder="词频"></el-input>
        </el-form-item>
        <el-form-item class="word-markget-item">
          <el-button type="danger" @click="removeItem(index)" icon="el-icon-delete" circle></el-button>
        </el-form-item>
      </div>
    </el-form>

    <el-row class="next-step-btn">
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="addItem">新增</el-button>
          <el-button type="primary" @click="goToNextStep">下一步</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
      name: 'EditWdMarketTmp',
      data() {
          return {
            wordMarketTmpList:[],
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
          }
      },
      methods: {
        addItem:function(){
          this.wordMarketTmpList.push({term:"",pos:"",freq:""})
        },
        removeItem:function(delIndex){
          this.wordMarketTmpList.splice(delIndex,1);
        },
        goToNextStep:function(){
          let checkNullPass = true
          this.wordMarketTmpList.forEach((wordMarketTmp,index)=>{
            if( wordMarketTmp.term==null|| wordMarketTmp.term==''||
                wordMarketTmp.pos==null|| wordMarketTmp.pos==''||
                wordMarketTmp.freq==null|| wordMarketTmp.freq=='' ){
              this.Message({
                message: "有参数为空",
                type: 'error',
                duration: 2 * 1000
              })
              checkNullPass = false
            }
          })
          if(!checkNullPass){
            return
          }

          const sendData = {"term":JSON.stringify(this.wordMarketTmpList),"uuid":this.$route.params.uuid}
          this.Request({
            url:'/process/test',
            method:'post',
            data:sendData
          }).then(response=>{
            if(response.status!='0'){
              this.Message.error(response.message)
            }else{
              this.$emit('nextStep',response.uuid,response.result);
            }
          })
        }
      },
      mounted:function(){
        const unKnowWordsList = this.$route.params.result
        if(unKnowWordsList!=null&&unKnowWordsList.length>0){
          unKnowWordsList.forEach(unKnowWord=>{
            this.wordMarketTmpList.push({"term":unKnowWord,pos:"",freq:"1"})
          })
        }
      }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .edit-word-market-form{
    margin:40px 0 0 0;
  }

  .word-markget-item{
    margin-right:20px;
  }
</style>
