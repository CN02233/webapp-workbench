
<template>
  <WorkMain :headerItems="['报送管理','报送设置','报送定义列表','一维静态报送单元']">

    <el-row class="search-row" :gutter="20">
      <el-col class="align-left table-button-group" :span="17">
        <el-button v-if="isView=='N'" @click="addColum()" type="primary">新增</el-button>
        <el-button @click="$router.go(-1)" type="warning">返回</el-button>
      </el-col>
    </el-row>

    <el-row class="table-page-root">
      <el-col :span="24">
        <el-table
          :data="unitColums"
          row-class-name="mini-font-size" stripe
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
            prop="colum_type"
            align="left"
            :formatter="formatterDataType"
            label="输入项类型">
          </el-table-column>
          <el-table-column
            prop="module_url"
            align="center"
            label="操作">
            <template slot-scope="scope">
              <div v-if="isView=='N'">
                <el-button type="text" @click="editDefined(scope.row.colum_id)" size="small">编辑</el-button>
                <el-button type="text" @click="deleteDefined(scope.row.colum_id)" size="small">删除</el-button>
              </div>
              <div v-if="isView=='Y'">
                <el-button type="text" @click="viewDefined(scope.row.colum_id)" size="small">查看</el-button>
              </div>

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
    <el-dialog
      :close-on-click-modal='false'
      :close-on-press-escape="false"
      :title="isView=='Y'?'查看输入项':(isEditModal?'编辑输入项':'新增输入项')"
      :visible.sync="addOrEditModelOpend" >
      <el-form ref="editForm" class="modal-form" label-position="right" label-width="30%" :model="formData">
        <el-form-item size="mini" label="输入项名称" >
          <el-input v-model="formData.colum_name"  :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" label="输入项中文名称" >
          <el-input v-model="formData.colum_name_cn" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" label="输入项数据类型" >
          <el-select v-model="formData.colum_type" :disabled="isView=='Y'" style="width:100%;" placeholder="请选择数据类型">
            <el-option :key="key" v-for="(value, key) in columDataType" :label="value" :value="key"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item size="mini" label="输入项单位" >
          <el-input v-model="formData.colum_point" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" v-if="formData.colum_type!='0'" label="输入项缺省值" >
          <el-input v-model="formData.default_value" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" v-if="formData.colum_type=='1'" label="最小值" >
          <el-input v-model="formData.min_value" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" v-if="formData.colum_type=='1'" label="最大值" >
          <el-input v-model="formData.max_value" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item size="mini" v-if="formData.colum_type!='0'" label="是否需要记忆" >
          <el-select v-model="formData.need_remember" :disabled="isView=='Y'" style="width:100%;" placeholder="请选择">
            <el-option  label="是" value="Y"></el-option>
            <el-option  label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item size="mini" label="输入项备注" >
          <el-input v-model="formData.colum_desc" :disabled="isView=='Y'" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item v-if="formData.colum_type=='0'" label="公式" >
          <el-input
            type="textarea"
            :rows="4"
            v-model="formData.colum_formula_desc" :disabled="true" auto-complete="off" >
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer"  v-if="isView=='N'" class="dialog-footer">
        <el-tooltip v-if="formData.colum_type=='0'&&!isEditModal" slot="append" class="item" effect="dark" content="点此设置公式" placement="top">
          <el-button @click="openFormulaEditor()" icon="el-icon-edit">定义公式</el-button>
        </el-tooltip>
        <el-tooltip v-if="formData.colum_type=='0'&&isEditModal" slot="append" class="item" effect="dark" content="点此设置公式" placement="top">
          <el-button @click="replaceFormulaEditor()" icon="el-icon-edit">重新定义公式</el-button>
        </el-tooltip>
        <el-button @click="addOrEditModelOpend=false">取 消</el-button>
        <el-button type="primary" @click="columSave()">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog width="65%" title="公式设定"  :close-on-click-modal='false'
               :close-on-press-escape="false" :show-close='false'	:visible.sync="isOpenFormulaEditor" >

      <el-form  label-position="right" label-width="20%"  align="left" class="modal-form"  >
        <el-form-item  label="选择输入项" >
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
          <el-button @click="formulaBack">回退</el-button>
          <el-button @click="fomularClear">清空</el-button>
        </el-form-item>

        <el-form-item label="公式内容" >
          <el-input
            type="textarea"
            :rows="4"
            :disabled="true"
            v-model="formData.colum_formula_desc">
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
        <el-button @click="isOpenFormulaEditor = false">取消</el-button>
        <el-button @click="fomularConfirm">确定</el-button>
        <el-button @click="fomularOperation">试算</el-button>
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
        isView:'N',//Y 只读模式 N 非自读模式
        columDataType:{
          '0':'公式'  ,
          '1':'数值'  ,
          '2':'字符串'  ,
          '3':'日期',
          '4':'常量'
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
          'need_remember':'N',
          'colum_formula':'',
          'colum_formula_desc':'',
          'unit_id':'',
          'colum_point':'',
          'colum_desc':''
        },
        editFormData:{
          'colum_name':'',
          'colum_name_cn':'',
          'colum_type':'1',
          'min_value':'',
          'max_value':'',
          'need_remember':'N',
          'colum_formula':'',
          'colum_formula_desc':'',
          'unit_id':'',
          'colum_point':'',
          'colum_desc':''
        },
        isOpenFormulaEditor:false,
        formulaDescContext:[],
        formulaColumnDescContext:[],
        formulaDescContextTmp:'',
        formulaContext:[],
        unitMap:{},
        otherUnits:[],
        fomularColumnTmp :'',
        formularOprationColums:{}
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
        colum_point:{
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
        this.addOrEditModelOpend = true
        this.isEditModal = false
      },
      viewDefined(columnId){
        this.editDefined(columnId)
      },
      editDefined(columnId){
        ///record/reportDefined/oneDimensionsStatic/edit
        const loading = this.$loading({
          lock: true,
          text: '获取数据中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        this.BaseRequest({
          url:'unitOneDimColum/getOnedimColumn',
          method:'get',
          params:{'columId':columnId}
        }).then(response=>{
          loading.close()
          //console.log(response)
          this.editFormData = response
          this.addOrEditModelOpend = true
          this.isEditModal = true
        }).catch(error=>{
          //console.log(error)
          loading.close()
          this.Message.error("删除失败"+error)
        })
      },
      deleteDefined(columnId){

        this.$confirm('确定删除该输入项？', '提示', {
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
            url:'unitOneDimColum/deleteOneDim',
            method:'get',
            params:{'columId':columnId}
          }).then(response=>{
            this.Message.success("删除成功")
            loading.close()
            this.getTableData(1)
          }).catch(error=>{
            //console.log(error)
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
        if(this.isEditModal){
          this.editSave()
        }else{
          this.addSave()
        }
      },
      addSave(){
        if(this.subCheck()){
          return
        }

        const $this = this
        if($this.addFormData.colum_type==='0'){
          $this.addFormData.min_value=null
          $this.addFormData.max_value=null
        }else if($this.addFormData.colum_type==='1'){
          $this.addFormData.colum_formula = null
          $this.addFormData.colum_formula_desc = null
        }else{
          $this.addFormData.min_value=null
          $this.addFormData.max_value=null
          $this.addFormData.colum_formula = null
          $this.addFormData.colum_formula_desc = null
        }
        this.subSave($this.addFormData,'unitOneDimColum/addSaveOnedim')

      },
      editSave(){
        if(this.subCheck()){
          return
        }

        const $this = this
        if($this.editFormData.colum_type==='0'){
          $this.editFormData.min_value=null
          $this.editFormData.max_value=null
        }else if($this.editFormData.colum_type==='1'){
          $this.editFormData.colum_formula = null
          $this.editFormData.colum_formula_desc = null
        }else{
          $this.editFormData.min_value=null
          $this.editFormData.max_value=null
          $this.editFormData.colum_formula = null
          $this.editFormData.colum_formula_desc = null
        }
        this.subSave($this.editFormData,'unitOneDimColum/editSaveOnedim')
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

          $this.addFormData = {
            'colum_name':'',
            'colum_name_cn':'',
            'colum_type':'1',
            'min_value':'',
            'max_value':'',
            'colum_formula':'',
            'colum_formula_desc':'',
            'unit_id':$this.unitId
          }
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
          if(this.formData.colum_type==='0'){
            if(!this.colum_formula_array){
              this.$notify({
                dangerouslyUseHTMLString: true,
                message: '<span style="font-size:15px;color:red;font-weight: bold">公式内容为空</span>'
              })
              checkResult = true
            }
          }else if(this.formData.colum_type==='1'){
            if(this.formData.min_value==null||(''+this.formData.min_value)==''||this.formData.max_value==null||this.formData.max_value==''){
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
      replaceFormulaEditor(){
        this.formulaDescContext = []
        this.formulaContext = []
        // this.formulaDescContextTmp+=addContext
        this.editFormData.colum_formula_desc = ''
        this.editFormData.colum_formula = ''
        this.openFormulaEditor()
      },
      openFormulaEditor(){
        this.isOpenFormulaEditor = true
      },
      handleItemChange(unitArray) {
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
          method:'get'
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


            let unitMapTmp = {}
            this.otherUnits.forEach((unitData,i)=>{
              if(unitData.value == unitId){
                const columArray = []
                datas.forEach(d=>{
                  let colum_id = d.colum_id + '.' + d.dim_id
                  let colum_name = d.colum_name_cn + '.' + d.dim_name_cn
                  columArray.push({label:colum_name,value:colum_id})
                  // $this.unitMap['c'+colum_id] = colum_name
                  unitMapTmp['c'+colum_id] = colum_name
                })

                this.otherUnits[i].children = columArray
              }
            })


            Object.keys(unitMapTmp).forEach(unitMaoKey=>{
              $this.unitMap[unitMaoKey] = unitMapTmp[unitMaoKey]
            })

          }
        }).catch(error=>{
          loading.close()
          $this.Message.success("读取失败:"+error)
        });
      },
      formulaColumConfirm(){
        const columtClickId = this.fomularColumnTmp[this.fomularColumnTmp.length-1]
        const unitClickId = this.fomularColumnTmp[0]
        this.otherUnits.forEach(unitData=>{
          //console.log(unitData)
          if(unitData.value == unitClickId){
            if(unitData.children){
              unitData.children.forEach(columData=>{
                if(columData.value == columtClickId) {
                  const finalContext = unitData.label+"."+columData.label
                  this.formulaDescContext.push({"context":finalContext,"isSymbol":false})
                  this.formulaContext.push({"context":unitData.value+"_"+columData.value,"columKey":unitData.label+'_'+columData.columKey,"isSymbol":false})
                  if(this.isEditModal){
                    this.editFormData.colum_formula_desc +=finalContext
                    this.editFormData.colum_formula +=("#"+unitData.value+"."+columData.value+"#")
                  }else{
                    this.addFormData.colum_formula_desc +=finalContext
                    this.addFormData.colum_formula +=("#"+unitData.value+"."+columData.value+"#")
                  }
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
        if(this.isEditModal){
          this.editFormData.colum_formula_desc +=addContext
          this.editFormData.colum_formula +=addContext
        }else{
          this.addFormData.colum_formula_desc +=addContext
          this.addFormData.colum_formula +=addContext
        }

      },
      formulaBack(){
        this.formulaContext.pop()
        this.formulaDescContext.pop()
        this.formulaDescContextTmp = ''
        if(this.formulaDescContext.length<1){
          if(this.isEditModal){
            this.editFormData.colum_formula_desc =""
            this.editFormData.colum_formula = ""
          }else{
            this.addFormData.colum_formula_desc =""
            this.addFormData.colum_formula = ""
          }
          return
        }

        this.formulaDescContext.forEach((formulaDesc,i)=>{
          // this.formulaDescContextTmp+=formulaDesc.context
          const formulaContext = this.formulaContext[i].isSymbol
            ?this.formulaContext[i].context
            :("#"+this.formulaContext[i].context+"#")
          if(this.isEditModal){
            if(i==0){
              this.editFormData.colum_formula_desc =formulaDesc.context
              this.editFormData.colum_formula = formulaContext
            }else{
              this.editFormData.colum_formula_desc +=formulaDesc.context
              this.editFormData.colum_formula +=formulaContext
            }
          }else{
            if(i==0){
              this.addFormData.colum_formula_desc =formulaDesc.context
              this.addFormData.colum_formula =formulaContext
            }else{
              this.addFormData.colum_formula_desc +=formulaDesc.context
              this.addFormData.colum_formula +=formulaContext
            }
          }
        })
      },
      fomularClear(){
        this.formulaContext = []
        this.formulaDescContext = []
        this.formulaDescContextTmp = ''
        if(this.isEditModal){
          this.editFormData.colum_formula_desc =""
          this.editFormData.colum_formula = ""
        }else{
          this.addFormData.colum_formula_desc =""
          this.addFormData.colum_formula = ""
        }
      },
      fomularConfirm(){
        this.colum_formula_array = this.formulaContext
        this.colum_formula_desc_array = this.formulaDescContext
        // this.formulaContext = []
        // this.formulaDescContext = []
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
      this.isView = this.$route.query.isView
      this.getTableData(1)
      this.getUnits()
    }
  }
</script>

<style lang="css">
  .mini-font-size{
    font-size: 12px !important;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "@/styles/table-page.scss";
  .el-form{
    margin:0 0 30px 0;
  }

  .el-form-item{
    margin:0 0 15px 0;
  }
</style>
