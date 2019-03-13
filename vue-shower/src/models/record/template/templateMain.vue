<template>
  <WorkMain :headerItems="['报送管理','报表模板管理']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="recordDataList"
          style="width: 100%">
          <el-table-column
            prop="template_id"
            align="center"
            width="100"
            label="模板编号">
          </el-table-column>
          <el-table-column
            prop="template_name"
            align="center"
            label="模板名称">
          </el-table-column>
          <el-table-column
            prop="source_file_name"
            align="left"
            label="模板文件名称">
          </el-table-column>
          <el-table-column
            prop="import_user_name"
            align="center"
            width="200"
            label="创建人">
          </el-table-column>
          <el-table-column
            prop="import_date_format"
            align="center"
            width="200"
            label="创建时间">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="viewTemplate(scope.row)" size="small">查看</el-button>
              <el-button type="text" @click="editTemplate(scope.row)" size="small">编辑</el-button>
              <el-button type="text" @click="delTemplate(scope.row)" size="small">删除</el-button>
              <!--<el-button type="text" @click="openEditModal(scope.row)" size="small">查看</el-button>-->
            </template>
          </el-table-column>

        </el-table>
      </el-col>
    </el-row>

    <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
    <WorkTablePager @refreshData="getTableData"
                    :pageCount="totalPage">
    </WorkTablePager>

    <!-- 新增、编辑 弹窗-->
    <el-dialog :title="modalPageTitle" :visible.sync="showModalPage" >
      <el-row>
        <el-col :span="5">模板名称</el-col>
        <el-col :span="10">
          <el-input placeholder="模板名稱" v-model="template" class="input-with-select" ></el-input>
        </el-col>
      </el-row>



      <el-row>
        <el-col :span="5">所属机构</el-col>
        <el-col :span="10">
          <el-cascader style="width: 100%"
            expand-trigger="hover"
            :change-on-select = "true"
            :options="originList"
            v-model="origin">
          </el-cascader>

          <!--<el-select v-model="origin" style="width:100%;" placeholder="请选择所属机构">-->
            <!--<el-option :key="item.origin_id" v-for="item in originList" :label="item.origin_name" :value="item.origin_id"></el-option>-->
          <!--</el-select>-->
        </el-col>
      </el-row>



      <el-row>
        <el-col :span="5">模板文件路径</el-col>
        <el-col :span="10">
          <el-input :disabled="true" placeholder="请输入文件路径" v-model="uploadFilePath" class="input-with-select" ></el-input>
        </el-col>
        <el-col :span="5">
          <el-upload
            :show-file-list="false"
            :auto-upload = 'false'
            :on-change="fileChange"
            ref="uploadFile">
            <el-button type="primary">
              <span v-if="uploadFilePath.length>0">重新选取文件</span>
              <span v-else>选取文件</span>
            </el-button>
          </el-upload>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="uploadFile()">确 定</el-button>
      </div>
    </el-dialog>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'ReportTemplateMain',
    data() {
      return {
        recordDataList:[],
        originList:[],
        tableDataUrl:'template/listTemplates',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        uploadFilePath:'',
        fileForUpload:{},
        filePath:'',
        isEditModal:false,
        template:'',
        origin:[],
        templateList:[
          {id:'test.xlsx',name:'测试模板'},
          {id:'realtest.xlsx',name:'真实模板'},
          {id:'test1.xlsx',name:'测试模板1'},
          {id:'test2.xlsx',name:'测试模板2'}
        ]
      }
    },
    validations:{
      formData:{
        super_module_id:{
          required
        },
        module_name:{
          required
        }
      }
    },
    computed:{
      formData:function(){
        if(this.isEditModal){
          return this.editFormData
        }else{
          return this.addFormData
        }
      }
    },
    components: {
      WorkTablePager,
      WorkMain
    },
    methods: {
      getTableData:function(pageNum){
        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this

        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum}
        }).then(response=>{
          $this.recordDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      formatterSuperName:function(row){
        return this.originDataObjs[row.super_module_id]!=null?this.originDataObjs[row.super_module_id].module_name:"无"
      },
      refreshTableList:function(dataList){
        this.recordDataList = dataList
      },
      getOriginList(){
        this.BaseRequest({
          url:'origin/listAllOrigin',
          method:"get"
        }).then(response=>{
          if(response!=null&&response.length>0){
            response.forEach(originData=>{

              function treeMenu(a) {
                this.tree = a || [];
                this.groups = {};
              };
              treeMenu.prototype = {
                init: function(pid) {
                  this.group();
                  return this.getDom(this.groups[pid]);
                },
                group: function() {
                  for(var i = 0; i < this.tree.length; i++) {
                    if(this.groups[this.tree[i].parent_origin_id]) {//{value:this.tree[i].origin_id,label:this.tree[i].origin_name}
                      this.groups[this.tree[i].parent_origin_id].push(this.tree[i]);
                    } else {
                      this.groups[this.tree[i].parent_origin_id] = [];
                      this.groups[this.tree[i].parent_origin_id].push(this.tree[i]);
                    }
                  }
                },
                getDom: function(a) {
                  if(!a) {
                    return ''
                  }
                  for(var i = 0; i < a.length; i++) {
                    a[i].label = a[i].origin_name;
                    a[i].value = a[i].origin_id;
                    a[i].children = this.getDom(this.groups[a[i].origin_id])
                  };
                  return a;
                }
              };
              this.originList = new treeMenu(response).init("0");
            })
          }

        })
      },
      viewTemplate(viewRow){
        this.$router.push({
          path: "/record/template/templateView",
          query:{
            'templateId':viewRow.template_id
          }
        });
      },
      editTemplate(viewRow){
        this.$router.push({
          path: "/record/template/templateEdit",
          query:{
            'templateId':viewRow.template_id
          }
        });
      },
      delTemplate(viewRow){

        this.$confirm('删除当前模板将导致报表无法找到创建报表时使用的模板文件，确定删除？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(() => {
          const loading = this.$loading({
            lock: true,
            text: '删除中',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });

          this.BaseRequest({
            url:'template/templteDelete',
            method:'post',
            params:{'templateId':viewRow.template_id}
          }).then(response=>{
            this.Message.success("删除成功")
            loading.close()
            this.closeModal();
            this.getTableData(1)
          }).catch(error=>{
            console.log(error)
            loading.close()
            this.Message.error("删除失败"+error)
          })
        }).catch(() => {
        });


      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal(editRow){
        this.reportId = editRow.reportId
        this.isEditModal = true
        this.goToEditPage()
      },
      fileChange:function(file, fileList){
        this.uploadFilePath = file.name
        var index= this.uploadFilePath.lastIndexOf(".");
        var ext = this.uploadFilePath.substr(index+1);

        if(ext!='txt'&&ext!='html'&&ext!='pdf'&&ext!='doc'&&ext!='docx'&&ext!='xls'&&ext!='xlsx'&&ext!='pptx'&&ext!='eml'){
          this.$notify.error("错误的文件类型！目前仅支持：txt、html、pdf、doc、docx、xls、xlsx、pptx、eml")
          this.uploadFilePath = ""
          return
        }
        this.fileForUpload = fileList[fileList.length-1]
      },
      removeFile(){
        this.uploadFilePath = ""
        this.fileForUpload = null
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
      },
      uploadFile(){
        const formData = new FormData()

        formData.append("templateFile",this.fileForUpload.raw)
        formData.append("templateName",this.template)
        formData.append("originId",this.origin[0])

        const loading = this.$loading({
          lock: true,
          text: '保存中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        this.BaseRequest({
          url:'/template/uploadTemplate',
          method:'post',
          data:formData
        }).then(response=>{
          this.Message.success("保存成功")
          loading.close()
          this.closeModal();
          this.getTableData(1)
        }).catch(error=>{
          console.log(error)
          loading.close()
          this.Message.error("保存失败"+error)
        })
      }
    },
    mounted:function(){
      this.menuDataList = []
      this.getTableData(1)
      this.getOriginList()
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";

  .el-row{
    margin-top:20px;
  }

  $seachRowHeight : 50px;
  $pagerRowHeight : 50px;
  $tableRowHeight : calc(100% - #{$seachRowHeight+$pagerRowHeight+10});
  .search-row{
    height:$seachRowHeight;
  }

  .table-row{
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
