webpackJsonp([25],{"2liR":function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",[s("el-row",[s("el-col",{attrs:{span:20}},[t._v("\n\n      语料验证：语料库"),s("span",{staticStyle:{"font-weight":"bold",color:"red"}},[t._v(t._s(t.corpus))]),t._v("的验证结果是"),s("span",{staticStyle:{"font-weight":"bold",color:"blue"}},[t._v(t._s(t.valid))])])],1),t._v(" "),s("el-row",[s("el-col",{staticClass:"base-modal-btns",attrs:{span:24}},[s("el-button",{attrs:{type:"primary"},on:{click:t.subNextStep}},[t._v("下一步")])],1)],1)],1)},staticRenderFns:[]};var o=s("VU/8")({name:"TrainingWord",data:function(){return{corpus:"",valid:""}},methods:{subNextStep:function(){var t=this;this.Request({url:"/model/segment/evaluate",method:"post",data:{corpus:this.corpus}}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.$emit("nextStep",e)})}},mounted:function(){this.corpus=this.$route.params.result.corpus,this.valid=this.$route.params.result.valid}},a,!1,function(t){s("foCm")},"data-v-7672dc06",null);e.default=o.exports},foCm:function(t,e){}});
//# sourceMappingURL=25.642c43cca203e73e6e1f.js.map