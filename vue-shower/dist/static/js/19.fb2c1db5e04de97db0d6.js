webpackJsonp([19],{Tmpo:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[n("el-row",[n("el-col",{attrs:{span:20}},[n("el-input",{attrs:{type:"textarea",autosize:{minRows:8},placeholder:"请输入内容"},model:{value:t.content,callback:function(e){t.content=e},expression:"content"}})],1)],1),t._v(" "),n("el-row",[n("el-col",{staticClass:"base-modal-btns",attrs:{span:24}},[n("el-button",{attrs:{type:"primary"},on:{click:t.subNextStep}},[t._v("下一步")])],1)],1)],1)},staticRenderFns:[]};var s=n("VU/8")({name:"TrainingWord",data:function(){return{content:""}},methods:{subNextStep:function(){var t=this;this.Request({url:"/model/sentiment/evaluate",method:"post",data:{content:this.content}}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.$emit("nextStep",e)})}}},a,!1,function(t){n("XdrS")},"data-v-7ed57380",null);e.default=s.exports},XdrS:function(t,e){}});
//# sourceMappingURL=19.fb2c1db5e04de97db0d6.js.map