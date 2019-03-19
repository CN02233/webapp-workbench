
<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表','一维静态报送单元']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="17">
        <el-button @click="addColum()" type="primary">新增</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="unitColums"
          style="width: 100%">
          <el-table-column
            prop="colum_id"
            align="left"
            label="编号">
          </el-table-column>
          <el-table-column
            prop="colum_name_cn"
            align="left"
            label="输入项名">
          </el-table-column>
          <el-table-column
            prop="colum_data_type"
            align="left"
            :formatter="formatterDataType"
            label="输入项类型">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="viewDefined()" size="small">查看</el-button>
              <el-button type="text" @click="editDefined()" size="small">编辑</el-button>
              <el-button type="text" @click="deleteDefined()" size="small">删除</el-button>
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

    <!--新增输入项弹窗-->
    <el-dialog title="新增输入项" :visible.sync="addModelOpend" >
      <el-form ref="editForm" class="modal-form" :label-position="left" label-width="30%" :model="addFormData">
        <el-form-item :size="small" label="输入项名称" >
          <el-input v-model="addFormData.colum_name"  auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item :size="small" label="输入项中文名称" >
          <el-input v-model="addFormData.colum_name_cn" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item :size="small" label="输入项数据类型" >
          <el-select v-model="addFormData.colum_data_type" style="width:100%;" placeholder="请选择数据类型">
            <el-option :key="key" v-for="(value, key) in columDataType" :label="value" :value="key"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="addFormData.colum_data_type=='1'" :size="small" label="最小值" >
          <el-input v-model="addFormData.min_value" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item v-if="addFormData.colum_data_type=='1'" :size="small" label="最大值" >
          <el-input v-model="addFormData.max_value" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item v-if="addFormData.colum_data_type=='0'" :size="small" label="公式" >
          <el-input v-model="addFormData.colum_formula" :disabled="true" auto-complete="off" >
            <el-tooltip slot="append" class="item" effect="dark" content="点此设置公式" placement="top">
              <el-button @click="openFormulaEditor()" icon="el-icon-edit"></el-button>
            </el-tooltip>
          </el-input>

        </el-form-item>
        <el-form-item v-if="addFormData.colum_data_type=='0'" :size="small" label="公式描述" >
          <el-input v-model="addFormData.colum_formula_des" :disabled="true" auto-complete="off" ></el-input>
        </el-form-item>

        <!--<el-form-item :size="small" label="所属机构" >-->
          <!--<el-cascader style="width: 100%"-->
                       <!--:show-all-levels="false"-->
                       <!--expand-trigger="hover"-->
                       <!--:change-on-select = "true"-->
                       <!--:options="originList"-->
                       <!--v-model="formData.origin">-->
          <!--</el-cascader>-->
        <!--</el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addModelOpend=false">取 消</el-button>
        <el-button type="primary" @click="addSave()">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="公式设定" :close-on-press-escape='false' :show-close='false'	:visible.sync="isOpenFormulaEditor" >

      <el-form class="modal-form" :label-position="left" label-width="100px" >
        <el-form-item label="选择输入项" >
          <el-cascader
            ref="unitSelectRef"
            :options="otherUnits"
            v-model="fomularColumnTmp"
            :show-all-levels="false"
            :change-on-select="false"
            @active-item-change="handleItemChange"
          ></el-cascader>
          <el-button @click="formulaColumConfirm">确定输入项</el-button>
        </el-form-item>
        <el-form-item label="设置运算符" >
          <el-button @click="formulaAdd('+')">+</el-button>
          <el-button @click="formulaAdd('-')">-</el-button>
          <!--<el-button @click="formulaAdd('%')">%</el-button>-->
          <el-button @click="formulaAdd('*')">*</el-button>
          <el-button @click="formulaAdd('/')">/</el-button>
          <el-button @click="formulaAdd('(')">(</el-button>
          <el-button @click="formulaAdd(')')">)</el-button>
          <el-button @click="formulaBack"><-(回退)</el-button>
        </el-form-item>
      </el-form>
      <el-row>
        <el-col :span="12">
          <el-input
            type="textarea"
            :rows="10"
            :disabled="true"
            placeholder="请输入内容"
            v-model="formulaDescContextTmp">
          </el-input>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
        <el-col :span="11">
          <el-row><el-col><el-input placeholder="请输入内容"></el-input></el-col></el-row>
          <el-row><el-col><el-input placeholder="请输入内容"></el-input></el-col></el-row>
          <el-row><el-col><el-input placeholder="请输入内容"></el-input></el-col></el-row>
          <el-row><el-col><el-input placeholder="请输入内容"></el-input></el-col></el-row>
          <el-row><el-col><el-input placeholder="请输入内容"></el-input></el-col></el-row>
          <el-row><el-col><el-button>试算</el-button></el-col></el-row>

        </el-col>
      </el-row>
      <el-row>
        <el-button>取消</el-button>
        <el-button @click="fomularConfirm">确定</el-button>
      </el-row>

    </el-dialog>

  </WorkMain>
</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  import "@/styles/table-page.scss";


  export default {
    name: "OneDimensionsStaticMain",
    describe:"一维静态填报项/入口",
    components: {
      WorkTablePager,
      WorkMain
    },
    data() {
      return {
        unitColums:[],
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        unitId:'',
        columDataType:{
          '0':'公式'  ,
          '1':'数值'  ,
          '2':'字符串'  ,
          '3':'日期'
        },
        addModelOpend:false,
        addFormData:{
          'colum_name':'',
          'colum_name_cn':'',
          'colum_data_type':'0',
          'min_value':'',
          'max_value':'',
          'colum_formula':[],
          'colum_formula_des':[],
          'unit_id':''
        },
        isOpenFormulaEditor:false,
        formulaDescContext:[],
        formulaDescContextTmp:'',
        formulaContext:[],
        otherUnits:[],
        fomularColumnTmp :''
      }
    },
    validations:{
      addFormData:{
        colum_name:{
          required
        },
        colum_name_cn:{
          required
        },
        colum_data_type:{
          required
        }
      }
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
          url:'unitOneDimColum/pagerOnedimList',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum,unitId:this.unitId}
        }).then(response=>{
          $this.unitColums = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      addColum(){
        this.addModelOpend = true
      },
      viewDefined(){

      },
      editDefined(){
        ///record/reportDefined/oneDimensionsStatic/edit
      },
      deleteDefined(){

      },
      formatterDataType(row){
        return this.columDataType[row['colum_data_type']]
      },
      addSave(){
        if(this.subCheck()){
          return
        }

        const $this = this
        const loading = $this.$loading({
          lock: true,
          text: '保存中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:"/unitOneDimColum/addSaveOnedim",
          method:'post',
          data:$this.addFormData
        }).then(response=>{
          loading.close()
          $this.Message.success("保存成功")
          $this.getTableData(1)
          $this.addModelOpend = false
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      subCheck(){
        let checkResult = this.$v.$invalid
        if(checkResult) {
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>输入项名称、输入项中文名称、输入项数据类型'
          })
        }else{
          if(this.addFormData.colum_data_type==='0'){
            if(!this.addFormData.colum_formula){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">公式内容为空</span>'
              })
              checkResult = true
            }
          }else if(this.addFormData.colum_data_type==='1'){
            if(!this.addFormData.min_value||!this.addFormData.max_value){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">以下参数不允许为空</span><br>最小值、最大值'
              })
              checkResult = true
            }
          }
        }
        return checkResult
      },
      openFormulaEditor(){
        this.isOpenFormulaEditor = true
      },
      handleItemChange(val) {
        console.log('active item:', val);
        // this.getReportColums(val)
      },
      getUnits(){
        const $this = this
        const loading = $this.$loading({
          lock: true,
          text: '获取中......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:"/unitOneDimColum/getUnits",
          method:'get',
          params:{unitId:''}
        }).then(response=>{
          loading.close()
          if(response){
            this.otherUnits = []
            response.forEach(unitData=>{
              this.otherUnits.push({'label':unitData.unit_name,'value':unitData.unit_id,'children':[]})
            })
          }
          // $this.otherUnits = response
        }).catch(error=>{
          loading.close()
          $this.Message.success("获取其他报送单元失败:"+error)
        });
      },
      getReportColums(unitId){
        const loading = $this.$loading({
          lock: true,
          text: '获取中......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:"/unitOneDimColum/getInputColumn",
          method:'get',
          params:{'unitId':unitId}
        }).then(response=>{
          loading.close()
          $this.Message.success("保存成功")
          $this.getTableData(1)
          $this.addModelOpend = false
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      formulaColumClick(clickObj){
        console.log(clickObj)

      },
      formulaColumConfirm(){
        const columtClickId = this.fomularColumnTmp[this.fomularColumnTmp.length-1]
        console.log("columtClickId === >"+columtClickId)
        this.otherUnits.forEach(unitData=>{
          console.log(unitData)
          if(unitData.value == columtClickId){
            this.formulaDescContext.push(unitData.label)
            this.formulaDescContextTmp+=unitData.label
          }
        })
        let formularColumn = ""
        this.fomularColumnTmp.forEach((fomularColumnData,i)=>{
          if(i>0){
            formularColumn+='.'
          }
          formularColumn+=fomularColumnData
        })
        this.formulaContext.push(formularColumn)
      },
      formulaAdd(addContext){
        this.formulaDescContext.push(addContext)
        this.formulaContext.push(addContext)
        this.formulaDescContextTmp+=addContext
      },
      formulaBack(){
        this.formulaContext.pop()
        this.formulaDescContext.pop()
        this.formulaDescContextTmp = ''
        this.formulaDescContext.forEach(formulaDesc=>{
          this.formulaDescContextTmp+=formulaDesc
        })
      },
      fomularConfirm(){
        this.addFormData.colum_formula = this.formulaContext
        this.addFormData.colum_formula_des = this.formulaDescContext
        this.formulaContext = []
        this.formulaDescContext = []
        this.isOpenFormulaEditor = false
      }
    },
    mounted:function(){
      this.unitId = this.$route.query.unitId
      this.addFormData.unit_id = this.$route.query.unitId
      this.getTableData(1)
      this.getUnits()
    }
  }
</script>

<style scoped>

</style>
