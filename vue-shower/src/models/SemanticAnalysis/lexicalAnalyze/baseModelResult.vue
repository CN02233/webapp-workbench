<template>
  <WorkMain :headerItems="['NLP基础','词法分析']">
 <el-row>
<el-tabs v-model="defaultActiveTab" type="card" class=" context-position" >
  <el-tab-pane label="分词" name="segment">
   <el-row >
     <el-col :span="3">分词结果：</el-col>
     <el-col :span="20">{{resultData.segment}}</el-col>
   </el-row>
  </el-tab-pane>
  <el-tab-pane v-if="resultData.lan==='zh'" label="复合词分解" name="compound">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.compound}}</el-col>
    </el-row>
  </el-tab-pane>
  <el-tab-pane  label="词性标注" name="pos">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.pos}}</el-col>
    </el-row>
  </el-tab-pane>
  <el-tab-pane v-if="resultData.lan==='zh'" label="命名实体识别" name="ner">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.segment}}</el-col>
    </el-row>
    <el-row :key="nerVal" v-for="(nerVal,index) in resultData.ner">
      <el-col v-if="index==0" :span="3">命名实体：</el-col>
      <el-col v-else :span="3">&nbsp;</el-col>
      <el-col :span="20">{{nerVal}}</el-col>
    </el-row>
  </el-tab-pane>
  <el-tab-pane v-if="resultData.lan==='en'" label="拼写纠正" name="suggest">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.segment}}</el-col>
    </el-row>
    <el-row v-if="resultData.suggest!=null&&resultData.suggest.length>0" :key="suggestVal" v-for="(suggestVal,index) in resultData.suggest">
      <el-col v-if="index==0" :span="3">拼写纠正建议：</el-col>
      <el-col v-else :span="20"></el-col>
      <el-col :span="20">{{suggestVal}}</el-col>
    </el-row>
    <!--<el-row>-->
      <!--<el-col :span="3">拼写纠正建议：</el-col>-->
      <!--<el-col :span="20">&nbsp;</el-col>-->
    <!--</el-row>-->
    <!--<el-row >-->
      <!--<el-col :span="3">纠正后：</el-col>-->
      <!--<el-col :span="20">{{resultData.correct}}</el-col>-->
    <!--</el-row>-->
  </el-tab-pane>

  <el-tab-pane v-if="resultData.lan==='zh'" label="同义词检测" name="synonym">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.segment}}</el-col>
    </el-row>
    <el-row >
      <el-col :span="3">同义词：</el-col>
      <el-col :span="20"><span v-for="synonymItem in resultData.synonym">{{synonymItem}}<br></span></el-col>
    </el-row>
  </el-tab-pane>

  <el-tab-pane v-if="resultData.lan==='zh'" label="词频" name="freq">
    <el-row >
      <el-col :span="3">词频：</el-col>

      <el-col :span="20"><span v-for="sfreqItem in resultData.freq">{{sfreqItem}}<br></span></el-col>
    </el-row>
  </el-tab-pane>

  <el-tab-pane v-if="resultData.lan==='en'" label="词干提取" name="stem">
    <el-row >
      <el-col :span="3">分词结果：</el-col>
      <el-col :span="20">{{resultData.segment}}</el-col>
    </el-row>
    <el-row >
      <el-col :span="3">词干提取：</el-col>
      <el-col :span="20"><span v-for="stemItem in resultData.stem">{{stemItem}}<br></span></el-col>
    </el-row>
  </el-tab-pane>

  </el-tabs>
 </el-row>
  </WorkMain>
</template>
<script>
import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
export default{
  name: 'baseModelResult',
  components: {
    WorkMain
  },
  data() {
    return {
      resultData : {},
      defaultActiveTab:"segment"
    };
  },
  methods: {},
  mounted:function(){
    this.resultData = this.$route.params.resultData
  }
}
</script>
<style  scoped>

 .context-position{
  text-align: left;
 }
 .update-word-row:first-child{
   margin-top:50px;
 }

 .update-word-row{
   margin-bottom:30px;
 }

  .el-row{
    margin-bottom:10px;
  }

  .el-col:first-child{
    text-align: left;
    width:120px;
  }

 .el-col:last-child{
   text-align: left;
   width: calc(100% - 150px);
 }

</style>
