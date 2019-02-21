<template>
  <div>
    <el-row v-for="(cacheCorpus,index) in cacheCorpusArray">
      <el-col :span="8">

        <el-input placeholder="请输入内容" disabled="true" :value="cacheCorpus.topic">
          <template slot="prepend">主题分类：</template>
        </el-input>

      </el-col>
      <el-col :span="2"></el-col>
      <el-col :span="10">
        <el-input placeholder="请输入内容" disabled="true" :value="cacheCorpus.corpus">
          <template slot="prepend">主题分类文件：</template>
        </el-input>
      </el-col>

      <el-col style="text-align: center;" :span="4">
        <el-button type="success" @click="removeParams(index)" icon="el-icon-minus" size="small" circle></el-button>
      </el-col>

    </el-row>
    <el-row>
      <el-col :span="8">

        <el-input placeholder="请输入内容" v-model="topic">
          <template slot="prepend">主题分类：</template>
        </el-input>

      </el-col>
      <el-col :span="2"></el-col>
      <el-col :span="10">

        <!--<el-input placeholder="请输入内容" v-model="corpus">-->
          <!--<template slot="prepend">分类文件：</template>-->
        <!--</el-input>-->

        <el-input :disabled="true" placeholder="请选取主题分类文件"  v-model="corpus">
          <!--<template slot="prepend">分类文件：</template>-->
          <template slot="append">
            <el-upload
              :show-file-list="false"
              :auto-upload = 'false'
              :on-change="corpusFileChange"
              ref="uploadFile">
              <el-button slot="trigger" size="small" type="success">
                <!--<span v-if="corpusNeg.length>0">重新选取反向语料文件</span>-->
                <span >选取文件</span>
                <!--<span v-else>选取反向语料文件</span>-->
              </el-button>
            </el-upload>
          </template>
        </el-input>


      </el-col>

      <el-col style="text-align: center;" :span="4">
        <el-button type="success" @click="addParams" icon="el-icon-plus" size="small" circle></el-button>
      </el-col>
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
              topic:"",
              corpus:"",
              corpusFile:"",
              cacheCorpusArray:[]
            }
        },
        methods: {
          addParams(){
            if(this.topic==""||this.topic==null||this.corpusFile==""||this.corpusFile==null){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span>' +
                '<br>主题分类、分类文件'
              })
              return
            }
            this.cacheCorpusArray.push({"topic":this.topic,"corpus":this.corpus,"corpusFile":this.corpusFile})
            this.topic = ""
            this.corpus = ""
            this.corpusFile = null
          },
          removeParams(removeIndex){
            this.cacheCorpusArray.splice(removeIndex,(removeIndex+1))
          },
          subNextStep(){
            if(this.topic==""||this.topic==null||this.corpusFile==""||this.corpusFile==null){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span>' +
                '<br>主题分类、主题分类文件'
              })
              return
            }


            if(this.topic!=null&&this.topic!=''&&this.corpus!=null&&this.corpus!=''){
              this.cacheCorpusArray.push({"topic":this.topic,"corpus":this.corpus,"corpusFile":this.corpusFile})
            }

            const sendForm = new FormData()

            this.cacheCorpusArray.forEach((cacheCorpusData,i)=>{
              sendForm.append("topic"+i,cacheCorpusData.topic)
              sendForm.append("corpus"+i,cacheCorpusData.corpusFile.raw)
            })

            this.Request({
              url:'/model/topic/corpus',
              method:'post',
              data:sendForm
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',null);
              }
            })

            this.$emit('nextStep',this.importWords);
          },
          corpusFileChange(file, fileList){
            this.corpus = file.name
            var index= this.corpus.lastIndexOf(".");
            var ext = this.corpus.substr(index+1);

            if(ext!='txt'){
              this.$notify.error("错误的文件类型！目前仅支持：txt")
              this.corpus = ""
              return
            }
            this.corpusFile = fileList[fileList.length-1]
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
