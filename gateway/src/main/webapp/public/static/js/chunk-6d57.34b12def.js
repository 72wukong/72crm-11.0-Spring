(window.webpackJsonp=window.webpackJsonp||[]).push([["chunk-6d57"],{CD14:function(t,e,i){"use strict";var s=i("tNBd");i.n(s).a},FuWY:function(t,e,i){},KmSi:function(t,e,i){"use strict";var s=i("QbLZ"),r=i.n(s),o=i("4XAY"),n=i("ZoMv"),a=i("yPq4"),c=i("O8VQ"),l=i("Ew9n"),h={name:"RecordList",components:{LogCell:c.a,SlideView:l.a,CRMFullScreenDetail:function(){return Promise.all([i.e("chunk-4a1e"),i.e("chunk-687ceb")]).then(i.bind(null,"3z7q"))}},filters:{crmIconClass:function(t){return"wk wk-"+n.a.convertTypeToKey(t)},crmName:function(t){return n.a.convertTypeToName(t)}},props:{crmType:String,request:Function,params:Object,listenerIDs:{type:Array,default:function(){return["crm-main-container"]}},noListenerIDs:{type:Array,default:function(){return[]}},noListenerClass:{type:Array,default:function(){return["el-table__body"]}}},data:function(){return{loading:!1,page:1,noMore:!1,list:[],scrollKey:Date.now(),showFullDetail:!1,relationID:"",relationCrmType:""}},computed:{crmIcon:function(){var t=this.crmType.replace("crm_","");return i("p3hS")("./"+t+".png")},scrollDisabled:function(){return this.loading||this.noMore}},watch:{params:function(){this.refreshList()}},mounted:function(){},methods:{refreshList:function(){this.page=1,this.list=[],this.noMore=!1,this.scrollKey=Date.now()},getList:function(){var t=this;this.loading=!0,this.request(r()({page:this.page,limit:15},this.params)).then(function(e){t.page++,t.list=t.list.concat(e.data.list),t.noMore=e.data.lastPage,t.loading=!1}).catch(function(){t.noMore=!0,t.loading=!1})},checkRelationDetail:function(t,e){var i=arguments.length>2&&void 0!==arguments[2]&&arguments[2];this.relationID=e,this.relationCrmType=i?n.a.convertTypeToKey(t):t,this.showFullDetail=!0},hideView:function(){this.$emit("hide")}}},p=(i("lm3i"),i("KHd+")),u=Object(p.a)(h,function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("slide-view",{staticClass:"d-view",attrs:{"listener-ids":t.listenerIDs,"no-listener-ids":t.noListenerIDs,"no-listener-class":t.noListenerClass,"body-style":{padding:0,height:"100%"}},on:{close:t.hideView}},[i("flexbox",{staticClass:"t-section"},[i("img",{staticClass:"t-img",attrs:{src:t.crmIcon}}),t._v(" "),i("div",{staticClass:"t-name"},[t._v("跟进记录")])]),t._v(" "),i("div",{staticClass:"t-content"},[i("div",{directives:[{name:"infinite-scroll",rawName:"v-infinite-scroll",value:t.getList,expression:"getList"}],key:t.scrollKey,attrs:{"infinite-scroll-disabled":"scrollDisabled","infinite-scroll-distance":"100"}},t._l(t.list,function(e,s){return i("log-cell",{key:s,attrs:{item:e,index:s,"can-delete":!1},on:{"crm-detail":t.checkRelationDetail}},[i("div",{staticClass:"relate-cell",on:{click:function(i){t.checkRelationDetail(e.activityType,e.activityTypeId,!0)}}},[i("i",{staticClass:"relate-cell-icon",class:t._f("crmIconClass")(e.activityType)}),t._v(" "),i("span",{staticClass:"relate-cell-type"},[t._v(t._s(t._f("crmName")(e.activityType))+"-")]),t._v(" "),i("span",{staticClass:"relate-cell-name"},[t._v(t._s(e.crmTypeName))])])])})),t._v(" "),t.loading?i("p",{staticClass:"scroll-bottom-tips"},[t._v("加载中...")]):t._e(),t._v(" "),t.noMore?i("p",{staticClass:"scroll-bottom-tips"},[t._v("没有更多了")]):t._e()]),t._v(" "),i("c-r-m-full-screen-detail",{attrs:{visible:t.showFullDetail,"crm-type":t.relationCrmType,id:t.relationID},on:{"update:visible":function(e){t.showFullDetail=e}}})],1)},[],!1,null,"52a40eb4",null);u.options.__file="RecordList.vue";var d=u.exports,m=i("iWcH"),w=i("L2JU"),y=i("4peS"),f=i.n(y),g=i("dAOq"),v=i("m77o"),b={name:"ReportList",components:{CRMAllDetail:a.a,RecordList:d,WkFieldView:m.default},filters:{dealIcon:function(t){return 1==t?"wk wk-success deal-suc":"wk wk-close deal-un"},dealName:function(t){return 1==t?"已成交":"未成交"}},mixins:[g.a],props:{show:{type:Boolean,default:!1},title:String,placeholder:{type:String,default:"请输入搜索内容"},crmType:String,fieldList:Array,recordRequest:Function,request:Function,params:Object,paging:{type:Boolean,default:!0},sortable:{type:[Boolean,String],default:!1}},data:function(){return{inputContent:"",loading:!1,tableHeight:this.getTableHeight(),list:[],showFieldList:[],sortData:{},currentPage:1,pageSize:f.a.get("crmPageSizes")||15,pageSizes:[15,30,60,100],total:0,rowID:"",rowType:"",showDview:!1,recordParams:{},recordShow:!1}},computed:r()({},Object(w.b)(["crm"]),{showExamineStatus:function(){return"contract"==this.crmType&&"receivables"==this.crmType},showFillColumn:function(){return!this.fieldList||!this.fieldList.length}}),watch:{show:function(t){t&&this.initInfo()}},mounted:function(){this.$el.addEventListener("click",this.handleDocumentClick,!1)},destroyed:function(){},methods:{getTableHeight:function(){var t=document.documentElement.clientHeight;return t-.2*t-200},initInfo:function(){var t=this;this.inputContent="",this.showFieldList=[],this.sortData={},this.$nextTick(function(){t.$refs.crmTable.clearSort()}),this.list=[],this.currentPage=1,window.onresize=function(){t.tableHeight=t.getTableHeight()},this.fieldList?(this.showFieldList=this.fieldList,this.getList()):this.getFieldList()},searchInput:function(){this.currentPage=1,this.getList()},getList:function(){var t=this;this.loading=!0;var e={};this.paging&&(e={page:this.currentPage,limit:this.pageSize}),this.placeholder&&(e.search=this.inputContent),this.sortData.order&&(e.sortField=this.sortData.prop,e.order="ascending"==this.sortData.order?2:1),this.request(r()({},e,this.params)).then(function(e){t.paging?(t.list=e.data.list,t.total=e.data.totalRow):t.list=e.data,t.loading=!1}).catch(function(){t.loading=!1})},getFieldList:function(){var t=this;if(0==this.showFieldList.length){this.loading=!0;var e="business_status"==this.crmType?"business":this.crmType,i={label:n.a[e]};Object(o.z)(i).then(function(e){for(var i=0;i<e.data.length;i++){var s=e.data[i],r=0;r=s.width?s.width:s.name&&s.name.length<=6?15*s.name.length+45:140,t.showFieldList.push({prop:s.fieldName,formType:s.formType,label:s.name,width:r})}t.getList()}).catch(function(){t.loading=!1})}else this.getList()},fieldFormatter:function(t,e,i,s){return this.fieldList&&this.fieldList.length&&"crmType"==e.property?n.a.convertTypeToName(t[e.property]):s?Object(v.a)(s.formType,t[e.property],"--",s):""===t[e.property]||null===t[e.property]?"--":t[e.property]},sortChange:function(t,e,i){this.sortData=t,this.getList()},handleSizeChange:function(t){f.a.set("crmPageSizes",t),this.pageSize=t,this.getList()},handleCurrentChange:function(t){this.currentPage=t,this.getList()},handleRowClick:function(t,e,i){"leads"===this.crmType?"leadsName"===e.property?(this.rowID=t.leadsId,this.showDview=!0):this.showDview=!1:"customer"===this.crmType?"customerName"===e.property?(this.rowID=t.customerId,this.rowType="customer",this.showDview=!0):this.showDview=!1:"contacts"===this.crmType?"customerName"===e.property?(this.rowID=t.customerId,this.rowType="customer",this.showDview=!0):"name"===e.property?(this.rowID=t.contactsId,this.rowType="contacts",this.showDview=!0):this.showDview=!1:"business"===this.crmType||"business_status"===this.crmType?"customerName"===e.property?(this.rowID=t.customerId,this.rowType="customer",this.showDview=!0):"businessName"===e.property?(this.rowID=t.businessId,this.rowType="business",this.showDview=!0):this.showDview=!1:"contract"===this.crmType?"customerName"===e.property?(this.rowID=t.customerId,this.rowType="customer",this.showDview=!0):"businessName"===e.property?(this.rowID=t.businessId,this.rowType="business",this.showDview=!0):"contactsName"===e.property?(this.rowID=t.contactsId,this.rowType="contacts",this.showDview=!0):"num"===e.property||"name"===e.property?(this.rowID=t.contractId,this.rowType="contract",this.showDview=!0):this.showDview=!1:"product"===this.crmType?"name"===e.property?(this.rowID=t.productId,this.showDview=!0):this.showDview=!1:"receivables"===this.crmType?"customerName"===e.property?(this.rowID=t.customerId,this.rowType="customer",this.showDview=!0):"contractNum"===e.property?(this.rowID=t.contractId,this.rowType="contract",this.showDview=!0):"number"===e.property?(this.rowID=t.receivablesId,this.rowType="receivables",this.showDview=!0):this.showDview=!1:"record"===this.crmType&&("count"===e.property&&t.count?(this.rowType="crm_"+n.a.convertTypeToKey(t.crmType),this.recordParams=r()({crmType:t.crmType,queryType:0},this.params),this.recordShow=!0):this.recordShow=!1)},handleHandle:function(t){"alloc"!==t.type&&"get"!==t.type&&"transfer"!==t.type&&"transform"!==t.type&&"delete"!==t.type&&"put_seas"!==t.type||(this.showDview=!1),"edit"!==t.type&&this.getList()},cellClassName:function(t){var e=t.row,i=t.column;t.rowIndex,t.columnIndex;return this.crmType&&("customerName"===i.property||"businessName"===i.property||"name"===i.property||"contactsName"===i.property||"num"===i.property||"contractNum"===i.property||"number"===i.property||"record"===this.crmType&&"count"===i.property&&e[i.property])?"can-visit--underline":""},hideView:function(){this.$emit("update:show",!1),this.$emit("hide")},handleDocumentClick:function(t){var e=!0,i=document.getElementsByClassName("el-table__row");if(i&&e)for(var s=0;s<i.length;s++){if(i[s].contains(t.target)){e=!1;break}}document.getElementById("slide")&&document.getElementById("slide").contains(t.target)&&(e=!1),e&&(this.showDview=!1)}}},D=(i("CD14"),Object(p.a)(b,function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("el-dialog",{attrs:{visible:t.show,"append-to-body":!0,"close-on-click-modal":!1,top:"10vh",width:"80%","custom-class":"no-padding-dialog"},on:{close:t.hideView}},[i("div",{staticClass:"header",attrs:{slot:"title"},on:{click:function(e){t.showDview=!1}},slot:"title"},[i("span",{staticClass:"title"},[t._v(t._s(t.title))]),t._v(" "),t.placeholder?i("el-input",{staticClass:"search-input",attrs:{placeholder:t.placeholder},nativeOn:{keyup:function(e){return"button"in e||!t._k(e.keyCode,"enter",13,e.key,"Enter")?t.searchInput(e):null}},model:{value:t.inputContent,callback:function(e){t.inputContent=e},expression:"inputContent"}},[i("el-button",{attrs:{slot:"append",icon:"el-icon-search"},nativeOn:{click:function(e){return t.searchInput(e)}},slot:"append"})],1):t._e()],1),t._v(" "),i("div",{staticClass:"container"},[i("div",{staticClass:"content"},[i("div",{staticClass:"list-body"},[i("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],ref:"crmTable",staticStyle:{width:"100%"},attrs:{id:"crm-table",data:t.list,height:t.tableHeight,"cell-class-name":t.cellClassName,stripe:"",border:"","highlight-current-row":""},on:{"row-click":t.handleRowClick,"sort-change":t.sortChange}},[t._l(t.showFieldList,function(e,s){return i("el-table-column",{key:s,attrs:{sortable:"poolDay"!=e.prop&&"custom",fixed:0==s,prop:e.prop,label:e.label,width:e.width,"show-overflow-tooltip":""},scopedSlots:t._u([{key:"default",fn:function(s){var r=s.row,o=s.column;return s.$index,["dealStatus"==e.prop?[i("i",{class:t._f("dealIcon")(r[e.prop])}),t._v(" "),i("span",[t._v(t._s(t._f("dealName")(r[e.prop])))])]:"status"==e.prop?[2==r.status?i("i",{staticClass:"wk wk-circle-password customer-lock"}):t._e()]:"checkStatus"==e.prop?[i("span",{staticClass:"status-mark",style:{backgroundColor:t.getStatusColor(r.checkStatus)}}),t._v(" "),i("span",[t._v(t._s(t.getStatusName(r.checkStatus)))])]:i("wk-field-view",{attrs:{props:e,"form-type":e.formType,value:r[o.property]},scopedSlots:t._u([{key:"default",fn:function(i){return i.data,[t._v("\n                  "+t._s(t.fieldFormatter(r,o,r[o.property],e))+"\n                ")]}}])})]}}])})}),t._v(" "),t.showFillColumn?i("el-table-column"):t._e()],2),t._v(" "),t.paging?i("div",{staticClass:"p-contianer"},[i("el-pagination",{staticClass:"p-bar",attrs:{"current-page":t.currentPage,"page-sizes":t.pageSizes,"page-size":t.pageSize,total:t.total,background:"",layout:"prev, pager, next, sizes, total, jumper"},on:{"update:pageSize":function(e){t.pageSize=e},"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}})],1):t._e()],1)]),t._v(" "),i("c-r-m-all-detail",{staticClass:"d-view",attrs:{visible:t.showDview,"crm-type":t.rowType,id:t.rowID},on:{"update:visible":function(e){t.showDview=e},handle:t.handleHandle}}),t._v(" "),t.recordShow?i("record-list",{attrs:{"crm-type":t.rowType,request:t.recordRequest,params:t.recordParams},on:{handle:t.getList,hide:function(e){t.recordShow=!1}}}):t._e()],1)])},[],!1,null,"57c678c2",null));D.options.__file="index.vue";e.a=D.exports},lm3i:function(t,e,i){"use strict";var s=i("FuWY");i.n(s).a},tNBd:function(t,e,i){}}]);