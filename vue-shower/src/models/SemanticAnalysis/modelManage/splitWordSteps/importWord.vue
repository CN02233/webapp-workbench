<template>
  <div>
    <el-row>
      <el-col :span="20">

        <el-input placeholder="请输入内容" v-model="importWords">
          <template slot="prepend">语料库：</template>
        </el-input>

      </el-col>
    </el-row>

    <el-row style="text-align: left;">
      格式说明：<br>
      1、语料是纯文本文件，文件中每一行代表一自然段或者一个标题。<br>
      2、一篇文章里面的段落之间是不空行的，在两篇文章之间，会有一个空行，表示文章的分界线。<br>
      3、正文部分按照规范已经切分成词，并且加上标注，标注的格式为“词语/词性”，即词语后面加单斜线，再紧跟词性标记。词与词之间用2个单字节空格隔开。每段最后的词，在标记之后也有2个单字节空格，保持格式一致。<br>
      4、语料中除了词性标记以外，还有“短语标记”，这种情况一般出现在机构团体名称、成语等情况中。
    </el-row>

    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subNextStep">下一步</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
    export default {
        name: 'ImportWord',
        data() {
            return {
              importWords:""
            }
        },
        methods: {
          subNextStep(){
            this.Request({
              url:'/model/segment/corpus',
              method:'post',
              data:{"corpus":this.importWords}
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',responseData);
              }
            })

          }
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
