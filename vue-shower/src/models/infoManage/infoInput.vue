<template>
    <div style="background-color: black">
      <el-row :gutter="20">
        <el-col style="text-align: right;line-height: 40px" :span="4">信息类别名称:</el-col>
        <el-col :span="14">
          <el-input :disabled="isView"
                    placeholder="请输入信息类别名称"
                    v-model="infoTitle">
          </el-input>
        </el-col>
      </el-row>
      <el-row :gutter="20" :key="addedListData.inforName" v-for="addedListData in addedList">
        <el-col :span="9">
          <el-input :disabled="isView" :value="addedListData.infoName">
          </el-input>
        </el-col>
        <el-col :span="9">
          <el-input :disabled="isView" :value="addedListData.infoValue"></el-input>
        </el-col>
        <el-col v-if="!isView" :span="3">
          <el-button type="danger" @click="delParams(index)" icon="el-icon-delete" circle></el-button>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="9">
          <el-input :disabled="isView" placeholder="请输入参数名"
                    v-model="infoName">
          </el-input>
        </el-col>
        <el-col :span="9">
          <el-input :disabled="isView" placeholder="请输入参数内容"
                      v-model="infoValue">
          </el-input>
        </el-col>
        <el-col v-if="!isView" :span="3">
          <el-button type="success" @click="addParams" icon="el-icon-check" circle></el-button>
        </el-col>

      </el-row>
      <el-row v-if="!isView">
        <el-col style="text-align: right" :span="18">
          <el-button @click="subConfirm" type="primary">提交<i class="el-icon--right"></i></el-button>
        </el-col>
      </el-row>

      <icon class="menu-icon" name="classify-checked"></icon>
      <icon name="classify"></icon>

    </div>
</template>

<script>
export default {
    name:'infoInput',
    data() {
      return {
        infoTitle:"",
        infoName:"",
        infoValue:"",
        addedList:[],
        isEdit:false,
      }
    },
    props:{
      inputTitle:{type:String},
      inputDataList:{type:Array},
      inputFileName:{type:String},
      isView:{type:Boolean}
    },
    methods:{
      addParams : function(){
        this.addedList.push({infoName:this.infoName,infoValue:this.infoValue});
        this.infoName = '';
        this.infoValue = '';
        console.log(JSON.stringify(item));
    },
    delParams: function(delIndex){
      this.addedList.splice(delIndex,(delIndex+1));
    },
    subConfirm:function(){
      if(this.isEdit){
        this.delOldFile()
      }else{
        this.confirmParams()
      }
    },
    delOldFile:function(){
      const $this = this
      this.Request({
        url:"/info/delInfo.do",
        params:{
          fileName:$this.inputFileName
        }
      }).then(response=>{
        $this.confirmParams()
        this.$emit("refreshList");
      });
    },
    confirmParams : function(){
      var sendData = {
        'title':this.infoTitle,
        'infomationList':this.addedList
      };
      this.addedList.push({infoName:this.infoName,infoValue:this.infoValue});
      var thisObj = this;
      this.Request({
        url:'datasource/list',
        data:sendData,
//        headers: { 'content-type': 'application/x-www-form-urlencoded' },
        method:'get'
      }).then(
        response => {console.log(response);
        thisObj.Message('保存成功');
        thisObj.addedList = [];
        thisObj.infoName = '';
        thisObj.infoValue = '';
        thisObj.infoTitle = '';
      }).catch(error => console.log(error));
    }
  },
  mounted:function(){
    const $this = this
    if(this.inputDataList!=null&&typeof this.inputDataList != 'undefined'){
      this.infoTitle = this.inputTitle;
      this.isEdit = true;
      const inputDataSize = this.inputDataList.length;
      this.inputDataList.forEach((inputData,i)=>{
        if(i===(inputDataSize-1)){
          $this.infoName = inputData.infoName
          $this.infoValue = inputData.infoValue
        }else{
          $this.addedList.push({infoName:inputData.infoName,infoValue:inputData.infoValue});
        }
      });
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
    .el-row{
      margin-top:10px;
      margin-bottom:10px;
    }

    .el-col{
      margin:0 10px 0 10px;
    }

    .fa-icon {
      width: auto;
      height: 1em; /* 或任意其它字体大小相对值 */

      /* 要在 Safari 中正常工作，需要再引入如下两行代码 */
      max-width: 100%;
      max-height: 100%;
    }

  .menu-icon{
    width:18px;
    height:18px;
  }
</style>
