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
            prop="module_id"
            align="left"
            label="模板名称">
          </el-table-column>
          <el-table-column
            prop="module_name"
            align="left"
            label="上传时间">
          </el-table-column>
          <el-table-column
            prop="super_module_name"
            align="left"
            label="创建人"
            :formatter="formatterSuperName"
          >
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="left"
            label="操作">
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
        <el-col :span="15">
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
      <!--<el-row>-->
        <!--<el-col :span="5" style="text-align: right">-->
          <!--<el-upload-->
            <!--:show-file-list="false"-->
            <!--:auto-upload = 'false'-->
            <!--:on-change="fileChange"-->
            <!--ref="uploadFile">-->
            <!--<el-button slot="trigger" size="small" type="primary">-->
              <!--<span v-if="uploadFilePath.length>0">重新选取文件</span>-->
              <!--<span v-else>选取文件</span>-->
            <!--</el-button>-->
          <!--</el-upload>-->
        <!--</el-col>-->
        <!--<el-col>-->
          <!--<el-col :span="10">-->
          <!--</el-col>-->

        <!--</el-col>-->
      <!--</el-row>-->
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="subWordForm()">确 定</el-button>
      </div>
    </el-dialog>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'template-main',
    data() {
      return {
        recordDataList:[],
        tableDataUrl:'report/reportList',
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        showModalPage:false,
        uploadFilePath:'',
        filePath:'',
        isEditModal:false
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
          if(response.dataList!=null){
            response.dataList.forEach(menuObj =>{
              $this.menuDataObjs[menuObj.module_id] = menuObj
            })
          }
          $this.recordDataList = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      formatterSuperName:function(row){
        return this.menuDataObjs[row.super_module_id]!=null?this.menuDataObjs[row.super_module_id].module_name:"无"
      },
      refreshTableList:function(dataList){
        this.recordDataList = dataList
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
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
        this.uploadFile = fileList[fileList.length-1]
      },
      removeFile(){
        this.uploadFilePath = ""
        this.uploadFile = null
      },
    },
    mounted:function(){
      this.menuDataList = []
      this.getTableData(1)
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
