
<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表','一维动态报送单元']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="7">
        <el-button @click="addDefined()" type="primary">新增</el-button>
      </el-col>
      <el-col class="align-right" :span="17">
        <el-select v-model="group_id" placeholder="输入项组">
          <el-option selected label="请选择输入项组" value=""></el-option>
          <el-option v-for="x in groupnameData" :key="x.group_id" :label="x.group_name" :value="x.group_id"></el-option>
        </el-select>
        <el-button @click="getTableData(1)" type="primary">查询</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row"><!--:span-method="mergeRow"-->
      <el-col :span="24">
        <el-table
          :data="unitColums"
          id="list" :span-method="mergeRow" @cell-mouse-leave="cellMouseLeave" @cell-mouse-enter="cellMouseEnter" :row-class-name="cellAddClass"
          style="width: 100%">
          <el-table-column prop="group_name" align="left" label="输入项组名"></el-table-column>
          <el-table-column prop="colum_id" align="left" label="编号" width="80"></el-table-column>
          <el-table-column prop="colum_name_cn" align="left" label="输入项名"></el-table-column>
          <el-table-column prop="colum_type" align="left" :formatter="formatterDataType" label="输入项类型"></el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <!--<el-button type="text" @click="viewDefined()" size="small">查看</el-button>-->
              <el-button type="text" @click="editDefined(scope.row.group_id,scope.row.group_name)" size="small">编辑</el-button>
              <el-button type="text" @click="deleteDefined(scope.row.group_id)" size="small">删除</el-button>
              <!--<el-button type="text" @click="openEditModal(scope.row)" size="small">清空</el-button>-->
            </template>
          </el-table-column>

        </el-table>
      </el-col>
    </el-row>

    <!-- 分页 refreshData:点击页码上一页下一页时调用的方法、pageCount:总页数-->
    <WorkTablePager @refreshData="getTableData"
                    :pageCount="totalPage">
    </WorkTablePager>

    <!--新增输入项弹窗--><!--:rules="scope.row.colum_type=='0'?{required:true,message:'必填字段'}:{required:false}"-->
    <el-dialog :title="isEditModal?'编辑输入项':'新增输入项'" :visible.sync="addOrEditModelOpend" width="80%" >
      <el-form :rules="editModel.rules" :model="editModel"  ref="form">
      <el-row style="margin:5px;">
        <el-col :span="24">
          <label class="el-form-item__label" style="width:20%;">输入项名称</label>
          <div style="margin-left:20%;"><el-input v-model="editModel.groupModel.colum_name_cn"  auto-complete="off" placeholder="输入项组" ></el-input></div>
        </el-col>
      </el-row>
      <el-row class="table-row">
        <el-col :span="24">
          <el-table
            :data="editModel.tableData"
            ref="table"
            tooltip-effect="dark"
            border
            stripe
            style="width: 100%">
            <el-table-column label="序号"  type="index" width="60" fixed align="center"></el-table-column>
            <el-table-column  label="输入项名称" align="center" width="150">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_name'" :rules='editModel.rules.colum_name'>
                  <el-input v-model="scope.row.colum_name"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="输入项中文名称" width="250">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_name_cn'" :rules='editModel.rules.colum_name_cn'>
                  <el-input v-model="scope.row.colum_name_cn"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="输入项数据类型" width="120">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_type'" :rules='editModel.rules.colum_type'>
                  <el-select v-model="scope.row.colum_type" style="width:100%;" placeholder="请选择数据类型">
                  <el-option :key="key" v-for="(value, key) in columDataType" :label="value" :value="key"></el-option>
                </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="最小值" width="90">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.min_value'" :rules="scope.row.colum_type=='1'?{required:true,message:'必填字段'}:{required:false}" >
                  <el-input v-if="scope.row.colum_type=='1'" v-model="scope.row.min_value"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="最大值" width="90">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.max_value'" :rules="scope.row.colum_type=='1'?{required:true,message:'必填字段'}:{required:false}" >
                  <el-input v-if="scope.row.colum_type=='1'" v-model="scope.row.max_value"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="公式" width="200">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_formula_desc'" :rules="scope.row.colum_type=='0'?{required:true,message:'必填字段',validator: validateFormula}:{required:false}">
                  <el-popover v-if="scope.row.colum_type=='0'" placement="top-start" width="200" trigger="hover" :content="scope.row.colum_formula_desc">
                  </el-popover>
                  <el-input v-if="scope.row.colum_type=='0'" v-model="scope.row.colum_formula_desc" readonly="readonly" effect="gray" auto-complete="off" >
                    <el-button slot="append" icon="el-icon-edit-outline" @click="openFormulaEditor(scope.$index,scope.row)"></el-button>
                  </el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="输入项单位" width="100">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_point'">
                  <el-input v-model="scope.row.colum_point"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="输入项备注" width="150">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.colum_desc'">
                  <el-input v-model="scope.row.colum_desc"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" fixed="right">
              <template slot-scope="scope">
                <el-button type="danger" @click="deleteRow(scope.$index, scope.row)" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

        </el-col>
      </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div style="display: inline;width:30%;float:left;text-align:left;">
          <el-button type="primary" @click="insertRow">新增行</el-button>
        </div>
        <el-button @click="addOrEditModelOpend=false">取 消</el-button>
        <el-button type="primary" @click="columSave()">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="公式设定" :close-on-press-escape='false' :show-close='false'	:visible.sync="isOpenFormulaEditor" >
      <el-form class="modal-form" label-position="right" label-width="100px" >
        <el-form-item label="选择输入项" >
          <el-cascader
            ref="unitSelectRef"
            :options="otherUnits"
            v-model="fomularColumnTmp"
            :show-all-levels="false"
            :change-on-select="false"
            @active-item-change="handleItemChange"
          ></el-cascader>
          <el-button @click="formulaColumConfirm" v-if="isFormulaEmpty">确定输入项</el-button>
        </el-form-item>
        <el-form-item label="设置运算符" v-if="isFormulaEmpty">
          <el-button @click="formulaAdd('+')">+</el-button>
          <el-button @click="formulaAdd('-')">-</el-button>
          <!--<el-button @click="formulaAdd('%')">%</el-button>-->
          <el-button @click="formulaAdd('*')">*</el-button>
          <el-button @click="formulaAdd('/')">/</el-button>
          <el-button @click="formulaAdd('(')">(</el-button>
          <el-button @click="formulaAdd(')')">)</el-button>
          <el-button @click="formulaBack"><-(回退)</el-button>
        </el-form-item>

        <el-form-item label="公式内容" >
          <el-input
            type="textarea"
            :rows="10"
            :disabled="true"
            v-model="editModel.selectRow.colum_formula_desc">
          </el-input>
        </el-form-item>
        <el-form-item label="公式试算" >
          <el-row :key="formulaColumnDesc.context" v-for="(formulaColumnDesc,i) in formulaDescContext" v-if="!formulaColumnDesc.isSymbol">
            <el-col><el-input v-model="formularOprationColums[formulaContext[i].columKey]" :placeholder="'请输入 '+formulaColumnDesc.context"></el-input>
            </el-col>
          </el-row>

        </el-form-item>
      </el-form>
      <el-row>
        <el-button type="danger" @click="fomularClear">重新定义公式</el-button>
        <el-button @click="isOpenFormulaEditor = false">取消</el-button>
        <el-button @click="fomularConfirm">确定</el-button>
        <el-button @click="fomularOperation" v-if="isFormulaEmpty">试算</el-button>
      </el-row>

    </el-dialog>

  </WorkMain>
</template>
<style>
  .el-table__body tr.hover-row:hover td{background-color: #ecf5ff;}
</style>
<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  import "@/styles/table-page.scss";

  export default {
    name: "OneDimensionsDynamicMain",
    describe:"一维动态填报项/入口",
    components: {
      WorkTablePager,
      WorkMain
    },
    data() {
      var validateFormula = (rule, value, callback) => {
        console.log(value)
        if (value === '') {
          callback(new Error('必填字段'));
        }else{
          callback();
        }
      };
      return {
        unitColums:[],
        currPageNum:1,
        eachPageNum:10,
        totalPage:1,
        unitId:'',
        group_id:'',
        columDataType:{
          '0':'公式'  ,
          '1':'数值'  ,
          '2':'字符串'  ,
          '3':'日期'
        },
        addOrEditModelOpend:false,
        colum_formula_array:[],
        colum_formula_desc_array:[],
        isEditModal:false,
        addFormData:{
          'colum_name':'',
          'colum_name_cn':'',
          'colum_type':'1',
          'min_value':'',
          'max_value':'',
          'colum_formula':'',
          'colum_formula_desc':'',
          'unit_id':'',
          'group_id':null,
          'group_name':'',
          'colum_point':'',
          'colum_desc':''
        },
        editFormData:{
          'colum_id':'',
          'colum_name':'',
          'colum_name_cn':'',
          'colum_type':'1',
          'min_value':'',
          'max_value':'',
          'colum_formula':'',
          'colum_formula_desc':'',
          'unit_id':'',
          'group_id':'',
          'group_name':'',
          'colum_point':'',
          'colum_desc':''
        },
        isOpenFormulaEditor:false,
        isFormulaEmpty:true,
        formulaDescContext:[],
        formulaColumnDescContext:[],
        formulaDescContextTmp:'',
        formulaContext:[],
        otherUnits:[],
        fomularColumnTmp :[],
        formularOprationColums:{},
        mergeMap:{},
        groupnameData: [], //搜索输入项组
        tableData: [], //输入项组数据
        editModel:{
          rules:{
            colum_name:{ type:"string",required:true,message:"必填字段",trigger:"change"},
            colum_name_cn:{ type:"string",required:true,message:"必填字段",trigger:"change"},
            colum_type:{ type:"string",required:true,message:"必填字段",trigger:"change"},
            colum_formula_desc:{validator: validateFormula, trigger: 'blur'}
          },
          group_id:null,
          selectRow:{},
          selectNo:-1,
          groupModel:{
            colum_name:'',
            colum_name_cn:'',
            colum_type:'2'
          },
          tableData:[],
          delData:[]
        },
        editSaveData:{'add':[],'edit':[],del:[],group:[]}    //输入项组保存数据
      }
    },
    validations:{
      formData:{
        colum_name:{
          required
        },
        colum_name_cn:{
          required
        },
        colum_type:{
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
          url:'unitOneDimColum/pagerOnedimListDynamic',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum,unitId:this.unitId, group_id:this.group_id}
        }).then(response=>{
          $this.unitColums = response.dataList
          $this.totalPage = response.totalPage
          $this.rowspan()
        })
      },
      addDefined(){
        this.addOrEditModelOpend = true
        this.isEditModal = false
        this.editModel.groupModel.colum_name = ''
        this.editModel.groupModel.colum_name_cn = ''
        this.clearEditModel()
      },
      viewDefined(){

      },
      editDefined(group_id, group_name){
        const loading = this.$loading({
          lock: true,
          text: '获取数据中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:'unitOneDimColum/pagerOnedimListDynamic',
          method:'get',
          params:{unitId:this.unitId,group_id:group_id}
        }).then(response=>{
          loading.close()
          this.editModel.groupModel.colum_name_cn = group_name
          this.editModel.groupModel.colum_name = group_name
          this.editModel.group_id = group_id
          this.editModel.tableData = response.dataList
          this.addOrEditModelOpend = true
          this.isEditModal = true
        }).catch(error=>{
          console.log(error)
          loading.close()
          this.Message.error("删除失败"+error)
        })
      },
      deleteDefined(group_id){

        this.$confirm('确定删除该输入项及所属项组内其他输入项？', '提示', {
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
            url:'unitOneDimColum/deleteOneDimDynamic',
            method:'get',
            params:{unitId:this.unitId,'group_id':group_id}
          }).then(response=>{
            this.Message.success("删除成功")
            loading.close()
            this.getTableData(1)
            this.getGroupname()
          }).catch(error=>{
            console.log(error)
            loading.close()
            this.Message.error("删除失败"+error)
          })
        }).catch(() => {
        });
      },
      formatterDataType(row){
        return this.columDataType[row['colum_type']]
      },
      columSave(){
        if(!this.subCheck()){
          return false
        }
        const $this = this
        $this.clearSaveData()
        $this.editModel.groupModel.unit_id = $this.unitId
        $this.editModel.groupModel.colum_id = $this.editModel.group_id
        $this.editSaveData.group.push($this.editModel.groupModel)
        $this.editModel.tableData.forEach((x, i) => {
            if(x.colum_type=='0'){
              x.max_value=null
              x.min_value=null
            }else if(x.colum_type=='1'){
              x.colum_formula=null
              x.colum_formula_desc=null
            }else{
              x.max_value=null
              x.min_value=null
              x.colum_formula=null
              x.colum_formula_desc=null
            }
            if(x.colum_id){
              $this.editSaveData.edit.push(x)
            }else{
              $this.editSaveData.add.push(x)
            }
        })
        $this.editSaveData.del = $this.editModel.delData
        this.subSave($this.editSaveData,'unitOneDimColum/editSaveOnedimDynamic')
      },
      subSave(sendData,sendUrl){
        const $this = this

        const loading = $this.$loading({
          lock: true,
          text: '保存中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:sendUrl,
          method:'post',
          data:sendData
        }).then(response=>{
          loading.close()
          $this.Message.success("保存成功")
          $this.getTableData(1)
          $this.addOrEditModelOpend = false

          $this.clearSaveData()
          $this.editModel.delData.length = 0
          $this.getGroupname()
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      subCheck(){
        let checkRow = true
        let checkResult = this.$refs["form"].validate((valid,model)=>{
          if(!valid)
            checkRow = false
        })||true
        checkResult = checkResult && checkRow
        if(checkResult){
          if(this.editModel.group_name==''){
            checkResult = false
            this.$notify({
              dangerouslyUseHTMLString: true,
              message: '<span style="font-size:15px;color:red;font-weight: bold">输入项组名称不允许为空</span>'
            })
          }
          if(this.editModel.tableData.length<1){
            checkResult = false
            this.$notify({
              dangerouslyUseHTMLString: true,
              message: '<span style="font-size:15px;color:red;font-weight: bold">请先点击【新增行】输入项目</span>'
            })
          }
        }
        return checkResult
      },
      openFormulaEditor(i,row){
        this.editModel.selectNo = i
        this.editModel.selectRow = this.editModel.tableData[i]
        this.editModel.selectRow.colum_formula_desc = this.editModel.selectRow.colum_formula_desc || ''
        this.editModel.selectRow.colum_formula = this.editModel.selectRow.colum_formula || ''
        this.isOpenFormulaEditor = true
        if(this.editModel.selectRow.colum_formula != '')
          this.isFormulaEmpty = false
      },
      handleItemChange(unitArray) {
        // console.log('active item:', val);
        this.getReportColums(unitArray[0])
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
        const $this = this
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
          this.otherUnits.forEach((unitData,i)=>{
            if(unitData.value == unitId){
              const columArray = []
              if(response){
                response.forEach(responseData=>{
                  const colum_id = responseData.colum_id
                  const colum_name = responseData.colum_name_cn
                  const colum_key = responseData.colum_name
                  columArray.push({label:colum_name,value:colum_id,columKey:colum_key})
                })
                this.otherUnits[i].children = columArray
              }
            }
          })
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      formulaColumConfirm(){
        const columtClickId = this.fomularColumnTmp[this.fomularColumnTmp.length-1]
        const unitClickId = this.fomularColumnTmp[0]
        this.otherUnits.forEach(unitData=>{
          if(unitData.value == unitClickId){
            if(unitData.children){
              unitData.children.forEach(columData=>{
                if(columData.value == columtClickId) {
                  const finalContext = unitData.label+"."+columData.label
                  this.formulaDescContext.push({"context":finalContext,"isSymbol":false})
                  this.formulaContext.push({"context":unitData.value+"_"+columData.value,"columKey":unitData.label+'_'+columData.columKey,"isSymbol":false})
                  this.editModel.selectRow.colum_formula_desc +=finalContext
                  this.editModel.selectRow.colum_formula +=("#"+unitData.value+"."+columData.value+"#")
                }
              })
            }

          }
        })
      },
      formulaAdd(addContext){
        this.formulaDescContext.push({"context":addContext,"isSymbol":true})
        this.formulaContext.push({"context":addContext,"isSymbol":true})
        // this.formulaDescContextTmp+=addContext
        this.editModel.selectRow.colum_formula_desc +=addContext
        this.editModel.selectRow.colum_formula +=addContext
      },
      formulaBack(){
        this.formulaContext.pop()
        this.formulaDescContext.pop()
        this.formulaDescContextTmp = ''
        this.formulaDescContext.forEach((formulaDesc,i)=>{
          // this.formulaDescContextTmp+=formulaDesc.context
          const formulaContext = this.formulaContext[i].isSymbol?this.formulaContext[i].context:("#"+this.formulaContext[i].context+"#")
          this.editModel.selectRow.colum_formula_desc +=formulaDesc.context
          this.editModel.selectRow.colum_formula +=formulaContext
        })
      },
      fomularConfirm(){
        this.colum_formula_array = this.formulaContext
        this.colum_formula_desc_array = this.formulaDescContext
        // this.formulaContext = []
        // this.formulaDescContext = []
        this.editModel.tableData[this.editModel.selectNo].colum_formula = this.editModel.selectRow.colum_formula
        this.editModel.tableData[this.editModel.selectNo].colum_formula_desc = this.editModel.selectRow.colum_formula_desc
        this.isOpenFormulaEditor = false
      },
      fomularOperation(){
        const $this = this
        let fomular = ""
        if(this.formulaContext){
          this.formulaContext.forEach(formulaData=>{
            if(formulaData.columKey){
              fomular+=formulaData.columKey
            }else{
              fomular+=formulaData.context
            }
          })
        }

        const loading = $this.$loading({
          lock: true,
          text: '计算中......',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:"/reportDefined/formalOperation",
          method:'post',
          data:{'fomular':fomular,'operaionValus':this.formularOprationColums}
        }).then(response=>{
          loading.close()
          this.$message({
            showClose: true,
            message: '试算结果:'+response
          });
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      fomularClear(){
        this.formulaContext.length = 0
        this.formulaDescContext.length = 0
        this.editModel.selectRow.colum_formula = ''
        this.editModel.selectRow.colum_formula_desc = ''
        this.isFormulaEmpty = true
      },
      getGroupname(){
        const $this = this
        $this.BaseRequest({
          url:"/unitOneDimColum/getGroup",
          method:'get',
          params:{'unitId':$this.unitId}
        }).then(response=>{
          this.groupnameData = response || [];
        }).catch(error=>{
          $this.Message.success("输入项组加载失败:"+error)
        });
      },
      cellAddClass({row,rowIndex}){
        row.className = 'el-table__row hover-row'
      },
      cellMouseLeave(row){
        let k = row.group_id, m = this.mergeMap[k], trs = document.querySelectorAll("#list .el-table__body .el-table__row")
        if(m){
          m.mr.forEach((x,i)=>{
            trs[i].className = 'el-table__row'
          })
        }
      },
      cellMouseEnter(row){
        let k = row.group_id, m = this.mergeMap[k], trs = document.querySelectorAll("#list .el-table__body .el-table__row")
        if(m){
          for(let i = 0; i < trs.length; i++){
            if(trs[i].className.indexOf('hover-row') > -1)
              trs[i].className = 'el-table__row'
          }
          m.mr.forEach(x=>{
            trs[x].className = 'el-table__row hover-row'
          })
        }
      },
      rowspan(){
        this.mergeMap = {}
        this.unitColums.forEach((x,i)=>{
          let val = x.group_id
          if(!this.mergeMap[val]){
            this.mergeMap[val] = {r:i, sp:1,mr:[i] }
          }else{
            this.mergeMap[val].mr.push(i)
            this.mergeMap[val].sp=this.mergeMap[val].sp+1
          }
        })
      },
      mergeRow({ row, column, rowIndex, columnIndex }){
        if (columnIndex === 0 || columnIndex === 4) {
          let id = row.group_id, m = this.mergeMap[id]
          if(m == null)
            return [1, 1]
          else if(m.r == rowIndex)
            return [m.sp, 1]
          else
            return [0, 0]
        }
      },
      insertRow(){
        let addObj = Object.assign({},this.addFormData)
        this.editModel.tableData.push(addObj)
      },
      deleteRow(i,row){
        this.editModel.tableData.splice(i, 1)
        if(row.colum_id){
          this.editModel.delData.push({colum_id:row.colum_id})
        }
        this.$refs.table.clearSelection()
      },
      clearEditModel(){
        this.editModel.delData = []
        this.editModel.tableData = []
        this.editModel.group_id = null
        this.editModel.group_name = null
      },
      clearSaveData(){
        this.editSaveData.add.length = 0
        this.editSaveData.edit.length = 0
        this.editSaveData.del.length = 0
        this.editSaveData.group.length = 0
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
    },
    mounted:function(){
      this.unitId = this.$route.query.unitId
      this.addFormData.unit_id = this.$route.query.unitId
      this.getTableData(1)
      this.getUnits()
      this.getGroupname()
    }
  }
</script>

