webpackJsonp([53],{JCAS:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});n("28Pe");var s=n("kzGw"),a=(n("+cKO"),{name:"HotWordFind",data:function(){return{wordDataList:[]}},computed:{},components:{WorkMain:s.a},methods:{},mounted:function(){var t=this,e=this;this.textAreaVal;this.Request({url:"/hotword",method:"get"}).then(function(n){"0"!=n.status?t.Message.error(n.message):e.wordDataList=n.result})}}),r={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("WorkMain",{attrs:{headerItems:["NLP应用","热词发现"]}},t._l(t.wordDataList,function(e,s){return n("el-row",{staticClass:"table-row"},[n("el-col",{staticStyle:{color:"blue","font-weight":"bold"},attrs:{span:2}},[t._v(t._s(s+1))]),t._v(" "),n("el-col",{attrs:{span:10}},[t._v(t._s(e.term))]),t._v(" "),n("el-col",{attrs:{span:10}},[t._v(t._s(e.count))])],1)}))},staticRenderFns:[]};var o=n("VU/8")(a,r,!1,function(t){n("nK+K")},"data-v-0cb4ff3b",null);e.default=o.exports},"nK+K":function(t,e){}});
//# sourceMappingURL=53.bf4963b578d69d5293d5.js.map