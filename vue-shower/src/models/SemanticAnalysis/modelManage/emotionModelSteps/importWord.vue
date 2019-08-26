<template>
  <div>
    <el-row>
      <el-col :span="20">

        <el-input :disabled="true" placeholder="请选取正向语料文件" v-model="corpusPos">
          <template slot="append">
            <el-upload
              :show-file-list="false"
              :auto-upload = 'false'
              :on-change="corpusPosFileChange"
              ref="corpusUploadFile">
              <el-button slot="trigger" size="small" type="primary">
                <span v-if="corpusPos.length>0">重新选取正向语料文件</span>
                <!--<span>选取文件</span>-->
                <span v-else>选取正向语料文件</span>
              </el-button>
            </el-upload>
          </template>
        </el-input>

      </el-col>
    </el-row>
    <el-row>
      <el-col :span="20">
        <el-input :disabled="true" placeholder="请选取反向语料文件"  v-model="corpusNeg">
          <template slot="append">
            <el-upload
              :show-file-list="false"
              :auto-upload = 'false'
              :on-change="corpusNegFileChange"
              ref="uploadFile">
              <el-button slot="trigger" size="small" type="primary">
                <span v-if="corpusNeg.length>0">重新选取反向语料文件</span>
                <!--<span>选取文件</span>-->
                <span v-else>选取反向语料文件</span>
              </el-button>
            </el-upload>
          </template>
        </el-input>

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
              corpusPos:"",
              corpusNeg:"",
              corpusUploadFile:null,
              corpusNegUploadFile:null
            }
        },
        methods: {
          subNextStep(){
            if(this.corpusUploadFile==null||this.corpusNegUploadFile==null){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span>' +
                '<br>正向语料文件、反向语料文件'
              })
              return
            }

            const sendDataForm = new FormData()
            sendDataForm.append("pos",this.corpusUploadFile.raw||{})
            sendDataForm.append("neg",this.corpusNegUploadFile.raw||{})


            this.Request({
              url:'/model/sentiment/corpus',
              method:'post',
              data:sendDataForm
            }).then(responseData=>{
              if(responseData.status!='0'){
                this.Message.error(responseData.message)
              }else{
                this.$emit('nextStep',null);
              }
            })

          },
          corpusPosFileChange(file, fileList){
            this.corpusPos = file.name
            var index= this.corpusPos.lastIndexOf(".");
            var ext = this.corpusPos.substr(index+1);

            if(ext!='txt'){
              this.$notify.error("错误的文件类型！目前仅支持：txt")
              this.corpusPos = ""
              return
            }
            this.corpusUploadFile = fileList[fileList.length-1]
          },
          corpusNegFileChange(file, fileList){
            this.corpusNeg = file.name
            var index= this.corpusNeg.lastIndexOf(".");
            var ext = this.corpusNeg.substr(index+1);

            if(ext!='txt'){
              this.$notify.error("错误的文件类型！目前仅支持：txt")
              this.corpusNeg = ""
              return
            }
            this.corpusNegUploadFile = fileList[fileList.length-1]
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
