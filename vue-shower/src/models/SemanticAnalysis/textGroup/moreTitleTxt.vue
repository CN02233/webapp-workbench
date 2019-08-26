<template>
  <WorkMain :headerItems="['NLP基础','文本多主题']">
    <el-row>
      <el-col :span="12">
        <el-radio v-model="radio" label="textarea">文本</el-radio>
        <el-input
          type="textarea"
          :autosize="{ minRows: 12}"
          placeholder="请输入内容"
          v-model="textAreaVal">
        </el-input>
      </el-col>
      <el-col :offset="1" :span="11">
        <el-col :span="10">
          <el-radio v-model="radio" label="uploadFile">上传文件</el-radio>
        </el-col>
        <el-col :span="5" style="text-align: right">
          <el-button @click="removeFile" size="small">删除已选文件</el-button>
        </el-col>
        <el-col :span="5" style="text-align: right">
          <el-upload
            :show-file-list="false"
            :auto-upload = 'false'
            :on-change="fileChange"
            ref="uploadFile">
            <el-button slot="trigger" size="small" type="primary">
              <span v-if="uploadFilePath.length>0">重新选取文件</span>
              <span v-else>选取文件</span>
            </el-button>
          </el-upload>
        </el-col>
        <el-col>
          <el-col :span="20">
            <el-input :disabled="true" placeholder="请输入文件路径" v-model="uploadFilePath" class="input-with-select" ></el-input>
          </el-col>

        </el-col>
        <!--<el-col :span="20">-->
        <!--<el-radio  v-model="radio" label="filePath">文件路径</el-radio>-->
        <!--<el-input placeholder="请输入文件路径" v-model="filePath" clearable></el-input>-->
        <!--</el-col>-->
        <el-col :span="20">
          <el-radio v-model="radio" label="hadoopPath">Hadoop文件路径</el-radio>
          <el-input placeholder="请输入Hadoop文件路径" v-model="hadoopPath" clearable></el-input>
        </el-col>
        <el-col >
          说明:支持txt、html、pdf、doc、docx、xls、xlsx、pptx、eml格式
        </el-col>
      </el-col>
    </el-row>
    <el-row>
      <el-col class="base-modal-btns" :span="24">
        <el-button type="primary" @click="subBaseInfos">提交</el-button>
        <el-button type="primary">重置</el-button>
      </el-col>
    </el-row>
  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  export default {
    name: 'MoreTitle',
    components: {
      WorkMain
    },
    data() {
      return {
        radio: 'textarea',
        uploadFilePath:'',
        filePath:'',
        hadoopPath:'',
        textAreaVal:'',
        uploadFile:null
      }
    },
    methods: {
      subBaseInfos:function(){
        if(this.radio=='textarea'){
          if(checkInfoNull(this.textAreaVal,'请输入文本内容',this.Message)){
            const sendData = {"text":this.textAreaVal}
            this.Request({
              url:'/topic/input/text',
              method:'post',
              data:sendData
            }).then(response=>{
              if(response.status!='0'){
                this.Message.error(response.message)
              }else{
                this.nextStep(response)
              }
            })
          }
        }else if(this.radio=='uploadFile'){
          if(checkInfoNull(this.uploadFilePath,'请上传文件',this.Message)){
            const sendDataForm = new FormData()
            sendDataForm.append("file",this.uploadFile.raw||{})
            this.Request({
              url:'/topic/input/upload',
              method:'post',
              data:sendDataForm
            }).then(response=>{
              if(response.status!='0'){
                this.Message.error(response.message)
              }else{
                this.nextStep(response)
              }
          })
          }
        }else if(this.radio=='filePath'){
          if(checkInfoNull(this.filePath,'请输入文件路径',this.Message)){
            const sendData = {"text":this.filePath}
            this.Request({
              url:'/topic/input/hdfs',
              method:'post',
              data:sendData
            }).then(response=>{
              if(response.status!='0'){
                this.Message.error(response.message)
              }else{
                this.nextStep(response)
              }
            })
          }
        }else if(this.radio=='hadoopPath'){
          if(checkInfoNull(this.hadoopPath,'请输入Hadoop文件路径',this.Message)){
            const sendData = {"text":this.hadoopPath}
            this.Request({
              url:'/topic/input/hdfs',
              method:'post',
              data:sendData
            }).then(response=>{
              if(response.status!='0'){
                this.Message.error(response.message)
              }else{
                this.nextStep(response)
              }
          })
          }
        }
        function checkInfoNull(dataValue,errorMessage,messageAlter){
          if(dataValue&&dataValue!=''){
            return true;
          }else{
            messageAlter.error(errorMessage);
            return false;
          }
        }
      },
      fileChange:function(file, fileList){
        this.uploadFilePath = file.name
        var index= this.uploadFilePath.lastIndexOf(".");
        var ext = this.uploadFilePath.substr(index+1);

        if(ext!='txt'&&ext!='html'&&ext!='pdf'&&ext!='doc'&&ext!='docx'&&ext!='xls'&&ext!='xlsx'&&ext!='pptx'&&ext!='eml'){
          this.$notify.error("错误的文件类型！目前仅支持：txt、html、pdf、doc、docx、xls、xlsx、pptx、eml")
          this.uploadFilePath = ""
          return
        }
        this.uploadFile = fileList[fileList.length-1]
      },
      removeFile(){
        this.uploadFilePath = ""
        this.uploadFile = null
      },
      nextStep(responseData){
        if(responseData.status!='0'){
          this.Message.error(responseData.message)
        }else{
          this.$router.push({name:"moreTitleTxtResult",params:{moreTitleResult:responseData}})
        }
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
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
