<template>
    <div >
        <div class="input-group">
            信息类别名称：
            <el-input class="input input-title"
                      size="small"
                      placeholder="请输入信息类别名称"
                      v-model="infoTitle">
            </el-input>
        </div>

        <div class="added-input-group" v-for="(item,index) in addedList">
            <el-input class="input"
                      size="small"
                      :value="item.infoName">
            </el-input>
            <el-input class="input"
                      size="small"
                      :value="item.infoValue">
            </el-input>
            <el-button type="danger" @click="delParams(index)" icon="el-icon-delete" circle></el-button>
        </div>

        <div class="input-group">
            <el-input class="input"
                      size="small"
                      placeholder="请输入参数名"
                      v-model="infoName">
            </el-input>
            <el-input class="input"
                      size="small"
                      placeholder="请输入参数内容"
                      v-model="infoValue">
            </el-input>
            <el-button type="success" @click="addParams" icon="el-icon-check" circle></el-button>
        </div>

        <div class="option-group">
            <el-button @click="confirmParams" type="primary">提交<i class="el-icon--right"></i></el-button>
        </div>

    </div>
</template>

<script>
export default {
  name:'infoInput',
    props: {
        // route配置json
        item: {
            type: Object,
            required: true
        }
    },
    data(){
      return {
        infoTitle:'',
        infoName:'',
        infoValue:'',
        addedList:[]
      }
    },
    methods:{
      addParams : function(){
        this.addedList.push({infoName:this.infoName,infoValue:this.infoValue});
        this.infoName = '';
        this.infoValue = '';
        console.log(JSON.stringify(item));
    },
    delParams: function(delIndex){
      debugger;
      this.addedList.splice(delIndex,(delIndex+1));
    },
    confirmParams : function(){
      var sendData = {
        'title':this.infoTitle,
        'infomationList':this.addedList
      };
      debugger;
      this.addedList.push({infoName:this.infoName,infoValue:this.infoValue});
      var thisObj = this;
      this.$http.post('http://localhost:8083/service/info/saveInfo.do',sendData).then(
//      this.$http.post('http://localhost:8083/service/info/outCros.do',sendData).then(
        response => {console.log(response);
        var data = response.data;
          if(data&&data=='SUCCESS'){
            thisObj.Message('保存成功');
            thisObj.addedList = [];
            thisObj.infoName = '';
            thisObj.infoValue = '';
            thisObj.infoTitle = '';
          }else{
            thisObj.Message('保存失败');
          }
        }).catch(error => console.log(error));
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
    .input-group,.added-input-group,.option-group{
        width:calc(100% - 60px);
        margin:10px 30px 10px 30px;
        height:50px;
        line-height: 50px;
        text-align: left;
    }

    .option-group{
        width:100px;
        margin:50px 0 0 890px;
    }

    .input{
        width:400px;
        margin:0 30px 0 0;
    }

    .input-title{
        width:715px;
    }
</style>