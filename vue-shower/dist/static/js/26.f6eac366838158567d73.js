webpackJsonp([26],{DgI0:function(t,e){},UFIo:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var l=a("mvHQ"),s=a.n(l),o={name:"baseModel",components:{WorkMain:a("kzGw").a},data:function(){return{radio:"textarea",uploadFilePath:"",filePath:"",hadoopPath:"",textAreaVal:"",uploadFile:null}},methods:{subBaseInfos:function(){var t=this;if("textarea"==this.radio){if(i(this.textAreaVal,"请输入文本内容",this.Message)){var e={text:this.textAreaVal};this.Request({url:"/extract/input/text",method:"post",data:e}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.nextStep(e)})}}else if("uploadFile"==this.radio){if(i(this.uploadFilePath,"请上传文件",this.Message)){var a=new FormData,l=this.uploadFile.raw;a.append("file",l,l.name),this.Request({url:"/extract/input/upload",method:"post",data:a}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.nextStep(e)})}}else if("filePath"==this.radio){if(i(this.filePath,"请输入文件路径",this.Message)){var s={text:this.filePath};this.Request({url:"/extract/input/path",method:"post",data:s}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.nextStep(e)})}}else if("hadoopPath"==this.radio&&i(this.hadoopPath,"请输入Hadoop文件路径",this.Message)){var o={text:this.hadoopPath};this.Request({url:"/extract/input/hdfs",method:"post",data:o}).then(function(e){"0"!=e.status?t.Message.error(e.message):t.nextStep(e)})}function i(t,e,a){return!(!t||""==t)||(a.error(e),!1)}},fileChange:function(t,e){this.uploadFilePath=t.name;var a=this.uploadFilePath.lastIndexOf("."),l=this.uploadFilePath.substr(a+1);if("txt"!=l&&"html"!=l&&"pdf"!=l&&"doc"!=l&&"docx"!=l&&"xls"!=l&&"xlsx"!=l&&"pptx"!=l&&"eml"!=l)return this.$notify.error("错误的文件类型！目前仅支持：txt、html、pdf、doc、docx、xls、xlsx、pptx、eml"),void(this.uploadFilePath="");this.uploadFile=e[e.length-1]},removeFile:function(){this.uploadFilePath="",this.uploadFile=null},nextStep:function(t){console.log("next step :"+s()(t)),this.$router.push({name:"contextAbstractResult",params:{resultData:t.result}})}}},i={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("WorkMain",{attrs:{headerItems:["NLP应用","内容摘要"]}},[a("el-row",[a("el-col",{attrs:{span:12}},[a("el-radio",{attrs:{label:"textarea"},model:{value:t.radio,callback:function(e){t.radio=e},expression:"radio"}},[t._v("文本")]),t._v(" "),a("el-input",{attrs:{type:"textarea",autosize:{minRows:12},placeholder:"请输入内容"},model:{value:t.textAreaVal,callback:function(e){t.textAreaVal=e},expression:"textAreaVal"}})],1),t._v(" "),a("el-col",{attrs:{offset:1,span:11}},[a("el-col",{attrs:{span:10}},[a("el-radio",{attrs:{label:"uploadFile"},model:{value:t.radio,callback:function(e){t.radio=e},expression:"radio"}},[t._v("上传文件")])],1),t._v(" "),a("el-col",{staticStyle:{"text-align":"right"},attrs:{span:5}},[a("el-button",{attrs:{size:"small"},on:{click:t.removeFile}},[t._v("删除已选文件")])],1),t._v(" "),a("el-col",{staticStyle:{"text-align":"right"},attrs:{span:5}},[a("el-upload",{ref:"uploadFile",attrs:{action:"asdasd/asdasdas","show-file-list":!1,"auto-upload":!1,"on-change":t.fileChange}},[a("el-button",{attrs:{slot:"trigger",size:"small",type:"primary"},slot:"trigger"},[t.uploadFilePath.length>0?a("span",[t._v("重新选取文件")]):a("span",[t._v("选取文件")])])],1)],1),t._v(" "),a("el-col",[a("el-col",{attrs:{span:20}},[a("el-input",{staticClass:"input-with-select",attrs:{disabled:!0,placeholder:"请输入文件路径"},model:{value:t.uploadFilePath,callback:function(e){t.uploadFilePath=e},expression:"uploadFilePath"}})],1)],1),t._v(" "),a("el-col",{attrs:{span:20}},[a("el-radio",{attrs:{label:"hadoopPath"},model:{value:t.radio,callback:function(e){t.radio=e},expression:"radio"}},[t._v("Hadoop文件路径")]),t._v(" "),a("el-input",{attrs:{placeholder:"请输入Hadoop文件路径",clearable:""},model:{value:t.hadoopPath,callback:function(e){t.hadoopPath=e},expression:"hadoopPath"}})],1),t._v(" "),a("el-col",[t._v("\n        说明:支持txt、html、pdf、doc、docx、xls、xlsx、pptx、eml格式\n      ")])],1)],1),t._v(" "),a("el-row",[a("el-col",{staticClass:"base-modal-btns",attrs:{span:24}},[a("el-button",{attrs:{type:"primary"},on:{click:t.subBaseInfos}},[t._v("提交")]),t._v(" "),a("el-button",{attrs:{type:"primary"}},[t._v("重置")])],1)],1)],1)},staticRenderFns:[]};var r=a("VU/8")(o,i,!1,function(t){a("DgI0")},"data-v-742950fb",null);e.default=r.exports}});
//# sourceMappingURL=26.f6eac366838158567d73.js.map