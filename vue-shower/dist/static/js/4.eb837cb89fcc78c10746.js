webpackJsonp([4],{XYWM:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var l={name:"baseModelResult",components:{WorkMain:e("kzGw").a},data:function(){return{resultData:{},defaultActiveTab:"segment"}},methods:{},mounted:function(){this.resultData=this.$route.params.resultData}},s={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("WorkMain",{attrs:{headerItems:["NLP基础","词法分析"]}},[e("el-row",[e("el-tabs",{staticClass:" context-position",attrs:{type:"card"},model:{value:t.defaultActiveTab,callback:function(a){t.defaultActiveTab=a},expression:"defaultActiveTab"}},[e("el-tab-pane",{attrs:{label:"分词",name:"segment"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.segment))])],1)],1),t._v(" "),"zh"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"复合词分解",name:"compound"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.compound))])],1)],1):t._e(),t._v(" "),e("el-tab-pane",{attrs:{label:"词性标注",name:"pos"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.pos))])],1)],1),t._v(" "),"zh"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"命名实体识别",name:"ner"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.segment))])],1),t._v(" "),t._l(t.resultData.ner,function(a,l){return e("el-row",{key:a},[e("el-col",{attrs:{span:3}},0==l?[t._v("命名实体：")]:[t._v(" ")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(a))])],1)})],2):t._e(),t._v(" "),"en"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"拼写纠正",name:"suggest"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.segment))])],1),t._v(" "),t._l(t.resultData.suggest,function(a,l){return null!=t.resultData.suggest&&t.resultData.suggest.length>0?e("el-row",{key:a},[0==l?e("el-col",{attrs:{span:3}},[t._v("拼写纠正建议：")]):e("el-col",{attrs:{span:20}}),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(a))])],1):t._e()})],2):t._e(),t._v(" "),"zh"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"同义词检测",name:"synonym"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.segment))])],1),t._v(" "),e("el-row",[e("el-col",{attrs:{span:3}},[t._v("同义词：")]),t._v(" "),e("el-col",{attrs:{span:20}},t._l(t.resultData.synonym,function(a){return e("span",[t._v(t._s(a)),e("br")])}))],1)],1):t._e(),t._v(" "),"zh"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"词频",name:"freq"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("词频：")]),t._v(" "),e("el-col",{attrs:{span:20}},t._l(t.resultData.freq,function(a){return e("span",[t._v(t._s(a)),e("br")])}))],1)],1):t._e(),t._v(" "),"en"===t.resultData.lan?e("el-tab-pane",{attrs:{label:"词干提取",name:"stem"}},[e("el-row",[e("el-col",{attrs:{span:3}},[t._v("分词结果：")]),t._v(" "),e("el-col",{attrs:{span:20}},[t._v(t._s(t.resultData.segment))])],1),t._v(" "),e("el-row",[e("el-col",{attrs:{span:3}},[t._v("词干提取：")]),t._v(" "),e("el-col",{attrs:{span:20}},t._l(t.resultData.stem,function(a){return e("span",[t._v(t._s(a)),e("br")])}))],1)],1):t._e()],1)],1)],1)},staticRenderFns:[]};var n=e("VU/8")(l,s,!1,function(t){e("oxjb")},"data-v-faa07a9c",null);a.default=n.exports},oxjb:function(t,a){}});
//# sourceMappingURL=4.eb837cb89fcc78c10746.js.map