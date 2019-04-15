
<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表','多维静态报送单元']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left" :span="5">
        <el-button @click="addDefined()" type="primary">新增</el-button>
        <el-button @click="$router.go(-1)" type="warning">返回</el-button>
      </el-col>
      <el-col class="align-left" :span="7">
        <el-button @click="openDimList()" type="primary">维度列表</el-button>
      </el-col>
    </el-row>

    <el-row class="table-row">
      <el-col :span="24">
        <el-table
          :data="unitColums"
          style="width: 100%">
          <el-table-column prop="colum_id" align="left" label="编号" width="60"></el-table-column>
          <el-table-column prop="colum_name_cn" align="left" label="输入项名"></el-table-column>
          <el-table-column prop="dim_name_cn" align="left" label="维度列表"></el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <!--<el-button type="text" @click="viewDefined()" size="small">查看</el-button>-->
              <el-button type="text" @click="editDefined(scope.row.colum_id,scope.row.colum_name)" size="small">编辑</el-button>
              <el-button type="text" @click="deleteDefined(scope.row.colum_id)" size="small">删除</el-button>
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
    <el-dialog :title="isEditModal?'编辑输入项':'新增输入项'" :visible.sync="addOrEditModelOpend" width="80%" >
      <el-form :rules="editModel.rules" :model="editModel"  ref="form">
      <el-row class="table-row">
          <el-col :span="24">
            <div class="nav" style="float:left;width:70%;">输入项列表</div>
            <!--<div style="width:30%;float:left;text-align:right;">
              <el-button type="primary" icon="el-icon-circle-plus-outline" size="mini" @click="openDimAdd()">新增维度</el-button>
            </div>-->
            <el-table
              :data="editModel.datas"
              ref="table"
              tooltip-effect="dark"
              border
              stripe
              style="width: 100%">
              <el-table-column label="序号"  type="index" width="60" align="center"></el-table-column>
              <el-table-column label="项目" prop="colum_name_cn">
                <template slot-scope="scope">
                  <el-form-item :prop="'datas.' + scope.$index + '.colum_name_cn'" :rules='editModel.rules.colum_name_cn'>
                  <el-input v-model="scope.row.colum_name_cn" readonly="readonly">
                    <el-button slot="append" icon="el-icon-edit-outline" @click="openColAdd(scope.$index,scope.row)"></el-button>
                  </el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column v-for="col in editModel.dimData" :prop="col.dim_name" :label="col.dim_name_cn" width="160" >
                <!--<template slot="header" slot-scope="scope">
                  <el-button type="text" @click="openDimAdd(index,col)">{{col.dim_name_cn}}</el-button>
                  <el-button icon="el-icon-delete" size="mini" circle @click="deleteDimRow(index, col)"></el-button>
                </template>-->
                <template slot-scope="scope">
                  <el-form-item :prop="'datas.' + scope.$index + '.' + col.dim_name + '.colum_type'" :rules='editModel.rules.colum_type'>
                    <el-input v-model="scope.row[col.dim_name].colum_type_name" readonly="readonly">
                      <el-button slot="append" icon="el-icon-edit-outline" @click="openCellAdd(scope.$index,scope.row,col.dim_name)"></el-button>
                    </el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <!--<el-table-column label="操作" width="90">
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteColRow(scope.$index, scope.row)" size="small">删除</el-button>
                </template>
              </el-table-column>-->
            </el-table>
            <!--<div style="width:30%;float:left;text-align:left;">
              <el-button icon="el-icon-circle-plus-outline" type="primary" @click="openColAdd()" size="mini">新增项目</el-button>
            </div>-->
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
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
          <el-button @click="formulaColumConfirm">确定输入项</el-button>
        </el-form-item>
        <el-form-item label="设置运算符">
          <el-button @click="formulaAdd('+')" size="mini">+</el-button>
          <el-button @click="formulaAdd('-')" size="mini">-</el-button>
          <el-button @click="formulaAdd('*')" size="mini">*</el-button>
          <el-button @click="formulaAdd('/')" size="mini">/</el-button>
          <el-button @click="formulaAdd('(')" size="mini">(</el-button>
          <el-button @click="formulaAdd(')')" size="mini">)</el-button>
          <el-button @click="formulaAdd('0')" size="mini">0</el-button>
          <el-button @click="formulaAdd('1')" size="mini">1</el-button>
          <el-button @click="formulaAdd('2')" size="mini">2</el-button>
          <el-button @click="formulaAdd('3')" size="mini">3</el-button>
          <el-button @click="formulaAdd('4')" size="mini">4</el-button>
          <el-button @click="formulaAdd('5')" size="mini">5</el-button>
          <el-button @click="formulaAdd('6')" size="mini">6</el-button>
          <el-button @click="formulaAdd('7')" size="mini">7</el-button>
          <el-button @click="formulaAdd('8')" size="mini">8</el-button>
          <el-button @click="formulaAdd('9')" size="mini">9</el-button>
          <el-button @click="formulaAdd('.')" size="mini">.</el-button>
          <el-button @click="formulaBack" size="mini"><-(回退)</el-button>
        </el-form-item>

        <el-form-item label="公式内容" >
          <el-input
            type="textarea"
            :rows="10"
            :disabled="true"
            v-model="datForm.colum_formula_desc">
          </el-input>
        </el-form-item>
        <el-form-item label="公式试算" >
          <el-row :key="formulaColumnDesc.context" v-for="(formulaColumnDesc,i) in formulaDescContext" v-if="!formulaColumnDesc.isSymbol">
            <el-col><el-input v-model="formularOprationColums[formulaContext[i].columKey]" :placeholder="'请输入 '+formulaColumnDesc.context"></el-input></el-col>
          </el-row>

        </el-form-item>
      </el-form>
      <el-row>
        <el-button @click="isOpenFormulaEditor = false">取消</el-button>
        <el-button @click="fomularConfirm">确定</el-button>
        <el-button @click="fomularOperation">试算</el-button>
      </el-row>

    </el-dialog>

    <el-dialog title="维度列表" :close-on-press-escape='false'	:visible.sync="isOpenDimList" >
      <div style="width:30%;text-align:left;">
        <el-button type="primary" @click="openDimAdd(-1)" size="mini">新增维度</el-button>
      </div>
      <el-form :rules="editModel.rules" :model="editModel" ref="dlform">
        <el-row class="table-row">
          <el-col :span="24">
            <el-table
              :data="editModel.dimData"
              ref="dtable"
              tooltip-effect="dark"
              border
              stripe
              style="width: 100%">
              <el-table-column label="序号"  type="index" width="60" align="center"></el-table-column>
              <el-table-column prop="dim_name" label="维度名称" align="center"></el-table-column>
              <el-table-column prop="dim_name_cn" label="维度中文名称"></el-table-column>
              <el-table-column label="操作" width="150">
                <template slot-scope="scope">
                  <el-button type="primary" @click="openDimAdd(scope.$index, scope.row)" size="small">编辑</el-button>
                  <el-button type="danger" @click="deleteDimRow(scope.$index, scope.row)" size="small">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>

    <el-dialog :title="dimIndex>-1?'编辑维度':'新增维度'" :close-on-press-escape='false' :show-close='false'	:visible.sync="isOpenDimEditor" >
      <el-form :rules="editModel.rules" :model="dimForm" ref="dform" class="modal-form" label-position="right" label-width="120px" >
        <el-form-item label="维度名称" :rules='editModel.rules.colum_name' prop="dim_name" >
          <el-input v-model="dimForm.dim_name">
          </el-input>
        </el-form-item>
        <el-form-item label="维度中文名称" :rules='editModel.rules.colum_name_cn' prop="dim_name_cn">
          <el-input v-model="dimForm.dim_name_cn">
          </el-input>
        </el-form-item>
      </el-form>
      <el-row>
        <el-button @click="isOpenDimEditor = false">取消</el-button>
        <el-button type="primary" @click="saveDimRow()">确定</el-button>
      </el-row>
    </el-dialog>

    <el-dialog :title="colForm.colum_name_cn!=null?'编辑项目':'新增项目'" :close-on-press-escape='false' :show-close='false'	:visible.sync="isOpenColEditor" >
      <el-form :rules="editModel.rules" :model="colForm" ref="cform" class="modal-form" label-position="right" label-width="120px" >
        <el-form-item label="项目名称" :rules='editModel.rules.colum_name' prop="colum_name" >
          <el-input v-model="colForm.colum_name"></el-input>
        </el-form-item>
        <el-form-item label="项目中文名称" :rules='editModel.rules.colum_name_cn' prop="colum_name_cn" >
          <el-input v-model="colForm.colum_name_cn">
          </el-input>
        </el-form-item>
        <el-form-item label="项目单位" prop="colum_point" >
          <el-input v-model="colForm.colum_point">
          </el-input>
        </el-form-item>
        <el-form-item label="项目备注" prop="colum_desc" >
          <el-input v-model="colForm.colum_desc">
          </el-input>
        </el-form-item>
      </el-form>
      <el-row>
        <el-button @click="isOpenColEditor = false">取消</el-button>
        <el-button type="primary" @click="insertColRow()">确定</el-button>
      </el-row>
    </el-dialog>

    <el-dialog :title="(colIndex>-1&&datField!='')?'编辑输入框':'新增输入框'" :close-on-press-escape='false' :show-close='false'	:visible.sync="isOpenDatEditor" >
      <el-form ref="uform" :rules="editModel.rules" :model="datForm" class="modal-form" label-position="left" label-width="30%">
        <el-form-item label="输入项数据类型" :rules='editModel.rules.colum_type' prop="colum_type">
          <el-select v-model="datForm.colum_type" style="width:100%;" placeholder="请选择数据类型">
            <el-option :key="key" v-for="(value, key) in columDataType" :label="value" :value="key"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="datForm.colum_type=='1'" label="最小值" :rules="datForm.colum_type=='1'?{required:true,message:'必填字段'}:{required:false}" prop="min_value" >
          <el-input v-model="datForm.min_value" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item v-if="datForm.colum_type=='1'" label="最大值" :rules="datForm.colum_type=='1'?{required:true,message:'必填字段'}:{required:false}" prop="max_value" >
          <el-input v-model="datForm.max_value" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item v-if="datForm.colum_type=='0'" label="公式" :rules="datForm.colum_type=='0'?{required:true,message:'必填字段'}:{required:false}" prop="colum_formula_desc" >
          <el-input
            type="textarea"
            :rows="6"
            v-model="datForm.colum_formula_desc" :disabled="true" auto-complete="off" >
          </el-input>
        </el-form-item>
        <el-form-item label="是否记忆用户输入" prop="need_remember">
          <el-select v-model="datForm.need_remember" style="width:100%;" placeholder="请选择">
            <el-option  label="是" value="Y"></el-option>
            <el-option  label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-tooltip v-if="datForm.colum_type=='0'" slot="append" class="item" effect="dark" content="点此设置公式" placement="top">
          <el-button @click="openFormulaEditor()" icon="el-icon-edit" v-if="isFormulaEmpty">定义公式</el-button>
          <el-button type="danger" @click="fomularClear()" icon="el-icon-edit" v-if="!isFormulaEmpty">重新定义公式</el-button>
        </el-tooltip>
        <el-button @click="isOpenDatEditor=false">取 消</el-button>
        <el-button type="primary" @click="insertDataCell()">确 定</el-button>
      </div>
    </el-dialog>
  </WorkMain>
</template>
<style>
  .nav{font-size:16px;font-weight: bold;text-align:left;height:28px;line-height:28px;}
  .el-table__body tr.hover-row:hover td{background-color: #ecf5ff;}
  .el-button--mini.is-circle{border:0;}
  input.read[readonly]{background-color: #efefef;}
</style>
<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'
  import "@/styles/table-page.scss";

  export default {
    name: "MultDimensionsStaticMain",
    describe:"多维静态填报项/入口",
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
        addOrEditModelOpend:false,
        colum_formula_array:[],
        colum_formula_desc_array:[],
        isEditModal:false,
        addFormData:{
          'colum_name':'',
          'colum_name_cn':'',
          'colum_type':'1',
          'min_value':null,
          'max_value':null,
          'colum_formula':'',
          'colum_formula_desc':'',
          'colum_id':null,
          'dim_id':null,
          'unit_id':null,
        },
        addCellData:{
          colum_type:'1',
          min_value:null,
          max_value:null
        },
        isOpenFormulaEditor:false,
        isOpenDimEditor:false,
        isOpenColEditor:false,
        isOpenDatEditor:false,
        isOpenDimList:false,
        isFormulaEmpty:true,
        formulaDescContext:[],
        formulaColumnDescContext:[],
        formulaDescContextTmp:'',
        formulaContext:[],
        otherUnits:[],
        fomularColumnTmp :[],
        formularOprationColums:{},
        unitMap:{},
        otherUnitsTmp:{},
        dimIndex:-1,
        colIndex:-1,
        datField:'',
        addColForm:{
          colum_id:null,
          colum_name:'',
          colum_name_cn:'',
          colum_point:'',
          colum_desc:'',
          unit_id:null
        },
        addDimForm:{
          dim_id:null,
          dim_name:'',
          dim_name_cn:''
        },
        addDataForm:{
          'colum_type':'',
          'min_value':0,
          'max_value':9999,
          'colum_formula':'',
          'colum_formula_desc':'',
          'colum_point':'',
          'colum_desc':'',
          'need_remember':'N',
          'colum_id':null,
          'dim_id':null,
          'unit_id':null,
        },
        colForm:{},
        dimForm:{},
        datForm:{},
        editModel:{
          rules:{
            colum_name:{ type:"string",required:true,message:"必填字段",trigger:"change"},
            colum_name_cn:{ type:"string",required:true,message:"必填字段",trigger:"change"},
            colum_type:{ type:"string",required:true,message:"必填字段",trigger:"change"}
          },
          datas:[],
          colData:[],
          dimData:[],
          dels:{dim:[],dat:[],col:[]}
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
          url:'unitMultDimColum/pagerMultdimListStatic',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum,unitId:this.unitId}
        }).then(response=>{
          $this.unitColums = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      addDefined(){
        const $this = this
        $this.addOrEditModelOpend = true
        $this.isEditModal = false
        $this.clearEditModel()
        $this.getDimData(1).then(()=>{
          $this.addColRow()
        })
      },
      viewDefined(){

      },
      editDefined(colum_id, colum_name){
        const loading = this.$loading({
          lock: true,
          text: '获取数据中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:'unitMultDimColum/pagerMultdimList',
          method:'get',
          params:{unitId:this.unitId,columId:colum_id}
        }).then(response=>{
          loading.close()
          if(response.dataList == null)
            return;
          const $t = this
          $t.clearEditModel()
          const $e = this.editModel
          let idat = {}, idim = {}, icol = {}
          response.dataList.forEach((x,i) => {
            if(!idim[x.dim_id]){
              $e.dimData.push(x)
              idim[x.dim_id] = i+1
            }
            if(!icol[x.colum_id]){
              $e.colData.push({colum_id:x.colum_id, colum_name:x.colum_name, colum_name_cn:x.colum_name_cn, unit_id:$t.unitId, colum_point:x.colum_point, colum_desc:x.colum_desc})
              icol[x.colum_id] = i+1
              idat[x.colum_id] = {}
            }
            idat[x.colum_id][x.dim_id] = $t.copyDataForm(x)
          })
          $e.colData.forEach(x=>{
            let z = Object.assign({},x)
            $e.dimData.forEach(col=>{
              z[col.dim_name] = idat[z.colum_id][col.dim_id]
            })
            $e.datas.push(z)
          })
          this.addOrEditModelOpend = true
          this.isEditModal = true
        }).catch(error=>{
          console.log(error)
          loading.close()
          this.Message.error("加载数据失败"+error)
        })
      },
      deleteDefined(colum_id){
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
            url:'unitMultDimColum/deleteMultdim',
            method:'get',
            params:{unitId:this.unitId,columId:colum_id}
          }).then(response=>{
            this.Message.success("删除成功")
            loading.close()
            this.getTableData(1)
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
        let $e = this.editModel
        let saveData = {add_col:[],edit_col:[],add_data:[],edit_data:[]}
        $e.colData.forEach((x, i) => {
            x.unit_id = this.unitId
            x.colum_id_no = i
            if(!x.colum_id){
              saveData.add_col.push(x)
            }else{
              saveData.edit_col.push(x)
            }
        })
        $e.datas.forEach((d, i) => {
          $e.dimData.forEach((n,j)=>{
            let x = d[n.dim_name]
            x.dim_id = n.dim_id
            x.unit_id = this.unitId
            x.colum_id_no = i
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
            if(!x.colum_id){
              saveData.add_data.push(x)
            }else{
              saveData.edit_data.push(x)
            }
          })
        })
        this.subSave(saveData,'unitMultDimColum/editSaveMultdim')
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
          //clear
          $this.editModel.delData.length = 0
          $this.otherUnits.forEach(u=>{
            u.children = []
          })
          $this.otherUnitsTmp = {}
        }).catch(error=>{
          loading.close()
          $this.Message.success("保存失败:"+error)
        });
      },
      subCheck(){
        let checkResult = this.validForm("form")
        if(checkResult){
          if(this.editModel.colData.length<1){
            this.$notify({
              dangerouslyUseHTMLString: true,
              message: '<span style="font-size:15px;color:red;font-weight: bold">请先点击【新增项目】输入项目</span>'
            })
            checkResult = false
          }
          if(this.editModel.dimData.length<1){
            this.$notify({
              dangerouslyUseHTMLString: true,
              message: '<span style="font-size:15px;color:red;font-weight: bold">请先新增维度</span>'
            })
            checkResult = false
          }
        }
        return checkResult
      },
      getDimData:function(pageNum){
        if(pageNum&&pageNum!=''){
          this.currPageNum = pageNum;
        }else{
          pageNum = this.currPageNum;
        }
        const $this = this

        return this.BaseRequest({
          url:'unitMultDimColum/pagerDimList',
          method:"get",
          params:{currPage:pageNum,pageSize:this.eachPageNum,unitId:this.unitId}
        }).then(response=>{
          $this.editModel.dimData = response.dataList
          $this.totalPage = response.totalPage
        })
      },
      openDimList(){
        this.isOpenDimList = true
        this.getDimData(1)
      },
      openDimAdd(i, row){
        row = row || this.addDimForm
        this.isOpenDimEditor = true
        this.dimIndex = i
        this.dimForm = Object.assign({},row)
      },
      openColAdd(i, row){
        this.isOpenColEditor = true
        this.colIndex = i
        this.colForm = Object.assign({},row)
      },
      openCellAdd(i,row,field){
        if(!this.editModel.colData[i].colum_name){
          this.$notify({
            dangerouslyUseHTMLString: true,
            message: '<span style="font-size:15px;color:red;font-weight: bold">请先输入项目</span>'
          })
          return
        }
        this.datField = field
        this.colIndex = i
        let o = row[field]
        this.datForm = o.colum_type=='' ? this.copyDataForm(this.addCellData) : Object.assign({},o)
        this.isOpenDatEditor = true
        if(this.datForm.colum_formula != '')
          this.isFormulaEmpty = false
      },
      saveDimRow(){
        if(!this.validForm("dform")) return
        let url = this.dimIndex == -1 ? 'unitMultDimColum/addSaveMultdim_dim' : 'unitMultDimColum/editSaveMultdim_dim'
        this.saveDim(url, this.dimForm, '保存')
      },
      saveDim(url, data, msg){
        const $this = this
        const loading = $this.$loading({
          lock: true,
          text: msg+'中',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        $this.BaseRequest({
          url:url,
          method:'post',
          data:data
        }).then(response=>{
          loading.close()
          $this.Message.success(msg+"成功")
          $this.isOpenDimEditor = false
          $this.getDimData(1)
        }).catch(error=>{
          loading.close()
          $this.Message.success(msg+"失败:"+error)
        });
      },
      addColRow(){
        const $this = this
        let z = Object.assign({},$this.addColForm)
        let addCol = Object.assign({},$this.addColForm)
        this.editModel.colData.push(addCol)
        this.editModel.dimData.forEach(x=>{
          let addDim = $this.newDataForm({},x)
          z[x.dim_name] = addDim
        })
        this.editModel.datas.push(z)
      },
      insertColRow(){
        if(!this.validForm("cform")) return
        let $t = this
        let keys = ['colum_name','colum_name_cn','colum_point','colum_desc']
        keys.forEach(k=>{
          $t.editModel.colData[$t.colIndex][k] = $t.colForm[k]
          $t.editModel.datas[$t.colIndex][k] = $t.colForm[k]
        })
        $t.otherUnitsTmp[$t.colIndex] = Object.assign({}, $t.colForm) //公式用
        this.isOpenColEditor = false
      },
      insertColRowOld(){
        if(!this.validForm("cform")) return
        const $this = this
        if($this.colIndex == -1){
          let z = Object.assign({},$this.colForm)
          let addCol = Object.assign({},$this.colForm)
          this.editModel.colData.push(addCol)
          this.editModel.dimData.forEach(x=>{
            let addDim = $this.newDataForm()
            z[x.dim_name] = addDim
          })
          this.editModel.datas.push(z)
        }else{
          this.editModel.colData[this.colIndex].colum_name = this.colForm.colum_name
          this.editModel.colData[this.colIndex].colum_name_cn = this.colForm.colum_name_cn
          //rename colum_name
        }
        this.isOpenColEditor = false
      },
      deleteDimRow(i,row){
        this.$confirm('确定删除该维度？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString:true,
          type: 'warning'
        }).then(()=>{
          this.saveDim('unitMultDimColum/deleteSaveMultdim_dim', row, '删除')
        })
      },
      insertDataCell(){
        if(!this.validForm("uform")) return
        const $t = this
        $t.editModel.datas[$t.colIndex][$t.datField] = $t.copyDataForm($t.datForm)
        this.isOpenDatEditor = false
        this.formulaContext = []
        this.formulaDescContext = []
      },
      copyDataForm(o){
        o = o || {}
        let addObj = Object.assign({},this.addDataForm)
        for(let n in addObj){
          if(o[n]||o[n]===0)
            addObj[n] = o[n]
        }
        addObj.colum_type_name = this.formatterDataType(addObj)
        return addObj
      },
      newDataForm(o,dim){
        o = o || {}
        dim = dim || {}
        let addObj = Object.assign(o,this.addDataForm, dim)
        addObj.colum_type_name = this.formatterDataType(addObj)
        return addObj
      },
      validForm(k){
        let b = true
        this.$refs[k].validate((valid,m)=>{
          if(!valid)
            b = false
        })
        return b
      },
      clearEditModel(){
        this.editModel.delData = []
        this.editModel.colData = []
        this.editModel.dimData = []
        this.editModel.datas = []
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
          url:"/unitMultDimColum/getUnits",
          method:'get',
          params:{originId:this.unitId}
        }).then(response=>{
          loading.close()
          if(response){
            this.otherUnits = []
            response.forEach(unitData=>{
              this.unitMap['u'+unitData.unit_id] = unitData
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
        let unitType = $this.unitMap['u'+unitId] ? $this.unitMap['u'+unitId].unit_type : '1'
        $this.BaseRequest({
          url:"/unitMultDimColum/getInputColumn",
          method:'get',
          params:{'unitId':unitId,'unitType':unitType}
        }).then(response=>{
          loading.close()
          if(unitType != 3){
            this.otherUnits.forEach((unitData,i)=>{
              if(unitData.value == unitId){
                const columArray = []
                if(response){
                  response.forEach(responseData=>{
                    let colum_id = responseData.colum_id
                    let colum_name = responseData.colum_name_cn
                    columArray.push({label:colum_name,value:colum_id})
                    $this.unitMap['c'+colum_id] = colum_name
                  })
                  this.otherUnits[i].children = columArray
                }
              }
            })
          }
          else{
            let datas = [], dims = [], cols = []
            response.forEach(responseData=>{
              if(responseData.colum_meta_type=='1'){
                datas.push(responseData)
              }else if(responseData.colum_meta_type == '2'){
                cols.push(responseData)
              }else if(responseData.colum_meta_type == '3'){
                dims.push(responseData)
              }
            })
            datas.forEach(s=>{
              cols.forEach(c=>{
                if(c.colum_id == s.colum_id){
                  s.colum_name = c.colum_name
                  s.colum_name_cn = c.colum_name_cn
                }
              })
              dims.forEach(d=>{
                if(d.dim_id == s.dim_id){
                  s.dim_name = d.colum_name
                  s.dim_name_cn = d.colum_name_cn
                }
              })
            })

            this.otherUnits.forEach((unitData,i)=>{
              if(unitData.value == unitId){
                const columArray = []
                datas.forEach(d=>{
                  let colum_id = d.colum_id + '.' + d.dim_id
                  let colum_name = d.colum_name_cn + '.' + d.dim_name_cn
                  columArray.push({label:colum_name,value:colum_id})
                  $this.unitMap['c'+colum_id] = colum_name
                })
                //添加未保存本行项目
                let tmpObj = this.otherUnitsTmp[this.colIndex]
                if(tmpObj){
                  let colum_no = '?' + this.colIndex
                  dims.forEach(d=>{
                    let tmp_colum_id = colum_no + '.' + d.dim_id
                    let tmp_colum_name = tmpObj.colum_name_cn + '.' + d.colum_name_cn
                    columArray.push({label:tmp_colum_name,value:tmp_colum_id})
                    $this.unitMap['c'+tmp_colum_id] = tmp_colum_name
                  })
                }
                this.otherUnits[i].children = columArray
              }
            })

          }
        }).catch(error=>{
          loading.close()
          $this.Message.success("读取失败:"+error)
        });
      },
      openFormulaEditor(){
        this.isOpenFormulaEditor = true
      },
      handleItemChange(unitArray) {
        // console.log('active item:', val);
        this.getReportColums(unitArray[0])
        // this.getReportColums(val)
      },
      formulaColumConfirm(){
        const columtClickId = this.fomularColumnTmp[this.fomularColumnTmp.length-1]
        const unitClickId = this.fomularColumnTmp[0]
        const unitLabel = this.unitMap['u'+unitClickId].unit_name
        const columLabel = this.unitMap['c'+columtClickId]
        const finalContext = unitLabel+"."+columLabel
        this.formulaDescContext.push({"context":finalContext,"isSymbol":false})
        this.formulaContext.push({"context":unitClickId+"_"+columtClickId,"columKey":unitLabel+'_'+columtClickId.toString().replace(/\./g,"_"),"isSymbol":false})
        this.datForm.colum_formula_desc +=finalContext
        this.datForm.colum_formula +=("#"+unitClickId+"."+columtClickId+"#")
        console.log(this.formulaContext)
      },
      formulaAdd(addContext){
        this.formulaDescContext.push({"context":addContext,"isSymbol":true})
        this.formulaContext.push({"context":addContext,"isSymbol":true})
        this.datForm.colum_formula_desc +=addContext
        this.datForm.colum_formula +=addContext
      },
      formulaBack(){
        this.formulaContext.pop()
        this.formulaDescContext.pop()
        this.formulaDescContextTmp = ''
        if(this.formulaDescContext.length == 0){
          this.datForm.colum_formula_desc = ''
          this.datForm.colum_formula = ''
          return
        }
        this.formulaDescContext.forEach((formulaDesc,i)=>{
          // this.formulaDescContextTmp+=formulaDesc.context
          const formulaContext = this.formulaContext[i].isSymbol?this.formulaContext[i].context:("#"+this.formulaContext[i].context+"#")
          if(i==0){
            this.datForm.colum_formula_desc =formulaDesc.context
            this.datForm.colum_formula = formulaContext
          }else{
            this.datForm.colum_formula_desc +=formulaDesc.context
            this.datForm.colum_formula +=formulaContext
          }
        })
      },
      fomularConfirm(){
        this.colum_formula_array = this.formulaContext
        this.colum_formula_desc_array = this.formulaDescContext
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
        this.datForm.colum_formula = ''
        this.datForm.colum_formula_desc = ''
        this.isFormulaEmpty = true
        this.isOpenFormulaEditor = true
      },
    },
    computed:{
      formData:function(){
        return this.addFormData
      },
    },
    mounted:function(){
      this.unitId = this.$route.query.unitId
      this.addFormData.unit_id = this.$route.query.unitId
      this.addDimForm.unit_id = this.unitId
      this.getTableData(1)
      this.getUnits()
    }
  }
</script>
