<template>
  <div :headerItems="['系统管理','敏感词库规则']">
    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="openAddModal" type="primary">新增规则</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="wordDataList"
          style="width: 100%">
          <el-table-column
            prop="id"
            align="left"
            label="主键">
          </el-table-column>
          <el-table-column
            prop="matchCN"
            align="left"
            label="关系">
          </el-table-column>
          <el-table-column
            prop="total"
            align="left"
            label="出现次数">
          </el-table-column>
          <el-table-column
            prop="relationCN"
            align="left"
            label="关联">
          </el-table-column>
          <el-table-column
            prop="enabledCN"
            align="left"
            label="是否有效">
          </el-table-column>
          <el-table-column
            prop="descriptionCN"
            align="left"
            label="规则描述">
          </el-table-column>
          <el-table-column
            label="操作"
            align="left">
            <template slot-scope="scope">
              <el-button type="text" @click="openEditModal(scope.row)" size="small">修改</el-button>
              <el-button type="text" @click="delWord(scope.row.id)" size="small">删除</el-button>
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
      <el-form ref="editForm" class="modal-form" :label-position="left" label-width="20%" :model="formData">
        <el-form-item label="规则描述" >
          <el-select style="width:100%" v-model="formData.description" placeholder="请选择规则描述" @change="ruleDesChange" >
            <el-option
              :change=ruleDesChange
              v-for="item in ruledictList.description"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关系" >
          <el-select style="width:100%" v-model="formData.match" placeholder="请选择关系">
            <el-option
            v-for="item in ruledictList.match"
            :key="item.value"
            :label="item.name"
            :value="item.value">
          </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出现次数" >
          <el-input v-model="formData.total" auto-complete="off" :disabled="formData.description=='0'"></el-input>
        </el-form-item>
        <el-form-item label="关联" >
          <el-select style="width:100%" v-model="formData.relation" placeholder="请选择关联">
            <el-option
            v-for="item in ruledictList.relation"
            :key="item.value"
            :label="item.name"
            :value="item.value">
          </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否有效" >
          <el-select style="width:100%" v-model="formData.enabled" placeholder="是否有效">
            <el-option label="是" value="true"></el-option>
            <el-option label="否" value="false"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeModal">取 消</el-button>
        <el-button type="primary" @click="subWordForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import WorkTablePager from "@/models/SemanticAnalysis/public/WorkTablePager"
  import WorkMain from "@/models/SemanticAnalysis/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  export default {
    name: 'SensitiveWordRule',

    data() {
      return {
        wordDataList:[],
        seg:'',
        tableDataUrl:'rulelist',
        ruledictList:{},
        currPageNum:1,
        totalPage:1,
        showModalPage:false,
        isEditModal:false,
        editFormData:{
          description:'',
          match:'',
          relation:'',
          enabled:'',
          total:''
        },
        addFormData:{
          description:'',
          match:'',
          relation:'',
          enabled:'',
          total:''
        }
      }
    },
    validations:{
      formData:{
        description:{
          required
        },
        match:{
          required
        },
        relation:{
          required
        },
        description:{
          required
        },
        total:{
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
      },
      modalPageTitle(){
        if(this.isEditModal){
          return "修改规则"
        }else
          return "新增规则"
      }
    },
    components: {
      WorkTablePager
    },
    methods: {
      getTableData:function(pageNum){
//        if(this.seg==null||this.seg==''){
//          this.Message.success("请输入查询条件")
//          return
//        }

        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this
        this.BaseRequest({
          url:this.tableDataUrl,
          method:"get",
          params:{pageindx:pageNum,pagenums:10,seg:this.seg||""}
        }).then(reponse=>{
          $this.totalPage = parseInt(reponse.pagecount||1)
          $this.refreshTableList(reponse.dataList)
          if(reponse.dataList){
            reponse.dataList.forEach(function(responseData){
              const relation = responseData['relation']
              const match = responseData['match']
              const enabled = responseData['enabled']
              const description = responseData['description']
              $this.ruledictList.relation.forEach(function(relationData){
                const relationDataName = relationData.name
                const relationDataValue = relationData.value

                if(relationDataValue===relation){
                  responseData.relationCN = relationDataName
                }
              })

              $this.ruledictList.description.forEach(function(descriptionData){
                const descriptionName = descriptionData.name
                const descriptionValue = descriptionData.value
                if(descriptionValue===description){
                  responseData.descriptionCN = descriptionName
                }
              })

              $this.ruledictList.match.forEach(function(matchData){
                const matchDataName = matchData.name
                const matchDataValue = matchData.value
                if(matchDataValue===match){
                  responseData.matchCN = matchDataName
                }
              })

              if(enabled==='true'){
                responseData.enabledCN = "是"
              }else{
                responseData.enabledCN = "否"
              }
            })
          }

        })
      },
      refreshTableList:function(dataList){
        this.wordDataList = dataList
      },
      delWord:function(editWordKey){
        this.BaseRequest({
          url:'rulelist',
          method:'delete',
          data:{id:editWordKey}
        }).then(reponse=>{
          if(reponse.status=='0'){
            this.Message.success("删除成功")
          }else{
            this.Message.error(reponse.msg)
          }
          this.getTableData()
        })
      },
      openAddModal:function(){
        this.showModalPage = true
        this.isEditModal = false
      },
      openEditModal:function(editWord){
        this.showModalPage = true
        this.isEditModal = true
        this.editFormData = editWord
      },
      closeModal:function(){
        this.showModalPage=false
        this.isEditModal=false
        Object.keys(this.formData).forEach(objKey=>{
          this.formData[objKey] = ""
      })
      },
      subWordForm:function(){
        if(this.checkInputNull()){
          //DO NOTHING
          return
        }

        let sendData = this.addFormData
        if(this.isEditModal){
          sendData = this.editFormData
          this.BaseRequest({
            url:'rulelist',
            method:'put',
            data:sendData
          }).then(response=>{
            if(response.status=='0'){
              this.Message.success("保存成功")
            }else{
              this.Message.error(response.msg)
            }
            this.getTableData()
            this.closeModal();

          })

        }else{
          sendData.id=''
          this.BaseRequest({
            url:"rulelist",
            method:'put',
            data:sendData
          }).then(response=>{
            if(response.status=='0'){
            this.Message.success("保存成功")
          }else{
            this.Message.error("保存失败")
          }
            this.getTableData()
            this.closeModal();
          })
        }

      },
      checkInputNull(){
        const checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>规则描述、关系、出现次数、关联'
          })
        }
        return checkResult
      },
      ruleDesChange(changeVal){
        if(changeVal==='0'){
          this.formData.total = "敏感词频率"
        }else{
          this.formData.total = ""
        }
      }
    },
    mounted:function(){
      const $this = this
      this.wordDataList = []
      this.BaseRequest({
        url:"ruledict",
        method:'get'
      }).then(response=>{
        $this.ruledictList = response
        $this.getTableData(1)
      })

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
    width:99%;
    height:$tableRowHeight;
    overflow: auto;
  }

  .pager-row{
    height:$pagerRowHeight;
  }
</style>
