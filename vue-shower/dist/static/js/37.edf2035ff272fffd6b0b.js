webpackJsonp([37],{"3CC4":function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i={name:"TypeModel",components:{WorkMain:s("kzGw").a},data:function(){return{active:0,uuid:null,nextStepResult:null,steps:[{stepTitle:"步骤 1",stepDescription:"导入语料",stepUrl:"typeModelImportWord"},{stepTitle:"步骤 2",stepDescription:"训练模型",stepUrl:"typeModelTrainingWord"},{stepTitle:"步骤 3",stepDescription:"效果评估",stepUrl:"typeModelResultScore"}]}},methods:{nextStep:function(t){this.nextStepResult=t,this.active++>=this.steps.length-1&&(this.active=0),this.goToStepPage()},goToStepPage:function(){this.$router.push({name:this.steps[this.active].stepUrl,params:{result:this.nextStepResult}})}},mounted:function(){this.goToStepPage()}},n={render:function(){var t=this.$createElement,e=this._self._c||t;return e("WorkMain",{attrs:{headerItems:["模型管理","主题分类模型"]}},[e("div",{staticClass:"work-steps"},[e("el-steps",{attrs:{active:this.active,"align-center":"true","finish-status":"success"}},this._l(this.steps,function(t){return e("el-step",{key:t.stepTitle,attrs:{title:t.stepTitle,description:t.stepDescription}})}))],1),this._v(" "),e("div",{staticClass:"steps"},[e("router-view",{on:{nextStep:this.nextStep}})],1)])},staticRenderFns:[]};var p=s("VU/8")(i,n,!1,function(t){s("YNqI")},"data-v-4a5b99ac",null);e.default=p.exports},YNqI:function(t,e){}});
//# sourceMappingURL=37.edf2035ff272fffd6b0b.js.map