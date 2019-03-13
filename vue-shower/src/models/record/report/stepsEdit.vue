<template>
  <WorkMain :headerItems="['报送管理','报表管理','报表编辑']">
    <el-tabs type="border-card" v-model="activeName">
      <el-tab-pane label="配气成本和价格信息" name="first">
        <el-steps finish-status="success" active=2 align-center>
          <el-step title="基本信息" ></el-step>
          <el-step icon="" title="成本信息" ></el-step>
          <el-step icon="el-icon-edit" title="资产信息" ></el-step>
          <el-step title="税费和其他业务信息" ></el-step>
          <el-step title="定价信息" ></el-step>
        </el-steps>
        <el-form ref="form" :model="form" label-width="40%">
          <el-form-item label="（一）实际执行的配气价格（购销差价）">
            <el-input style="width:50%;float: left;" ></el-input>
          </el-form-item>
          <el-form-item label="    （二）营业收入">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="      其中：1.配（售）气收入">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="            2.工程安装收入">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="            3.其他业务收入">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="    （三）税前利润总额">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="      其中：1.配（售）气利润">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="            2.工程安装利润">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
          <el-form-item label="            3.其他业务利润">
            <el-input style="width:50%;float: left;"></el-input>
          </el-form-item>
        </el-form>


        <el-button type="info">保存</el-button>
        <el-button type="primary">上一步</el-button>
        <el-button type="success">下一步</el-button>

      </el-tab-pane>
      <el-tab-pane label="管道运输成本和价格信息" name="second">
        <el-steps active="2" align-center>
          <el-step title="基本信息"  ></el-step>
          <el-step title="成本信息" ></el-step>
          <el-step title="资产信息" ></el-step>
          <el-step title="税费和其他业务信息" ></el-step>
          <el-step title="定价信息" ></el-step>
        </el-steps>
        
        <el-row>
          <el-table
            :data="tableData"
            style="width: 100%">
            <el-table-column
              prop="data_name"
              align="left"
              label="数据名称">
            </el-table-column>
            <el-table-column
              prop="data_row"
              width="100px"
              align="center"
              label='行次'>
            </el-table-column>
            <el-table-column
              prop="data_value"
              align="center"
              width="200px"
              label="数值">
              <template slot-scope="scope">
                <el-input></el-input>
              </template>
            </el-table-column>
            <el-table-column
              prop="data_note"
              align="left"
              label="备注">
            </el-table-column>
          </el-table>

        </el-row>
        <el-row>
          <el-button type="info">保存</el-button>
          <el-button type="primary">上一步</el-button>
          <el-button type="success">下一步</el-button>
        </el-row>

      </el-tab-pane>
      <el-tab-pane label="附表" name="third">
        <el-steps align-center>
          <el-step title="周转量计算" description="附表"></el-step>
        </el-steps>
      </el-tab-pane>
    </el-tabs>



  </WorkMain>
</template>

<script>
  import WorkMain from "@/models/public/WorkMain"

  export default {
    name: "stepsEdit",
    components: {
      WorkMain
    },
    data() {
      return {
        activeName :"first",
        tableData:[
          {
            data_name:"    （一）实际执行的配气价格（购销差价）",
            data_row:"1",
            data_value:"",
            data_note:"政府已经制定配气价格的，填写制定的配气价格；尚未制定配气价格的填写综合购销差价。均填写不含增值税价格。"
          },{
            data_name:"（二）营业收入",
            data_row:"2",
            data_value:"",
            data_note:"（二）=1+2+3"
          },{
            data_name:"      其中：1.配（售）气收入",
            data_row:"3",
            data_value:"",
            data_note:"配气和售气业务分离的只填写配气收入，否则填配售气合计收入"
          },{
            data_name:"            2.工程安装收入",
            data_row:"4",
            data_value:"",
            data_note:""
          },{
            data_name:"            3.其他业务收入",
            data_row:"5",
            data_value:"",
            data_note:""
          },{
            data_name:"    （三）税前利润总额",
            data_row:"6",
            data_value:"",
            data_note:"（三）=1+2+3"
          },{
            data_name:"      其中：1.配（售）气利润",
            data_row:"7",
            data_value:"",
            data_note:"配气和售气业务分离的只填写配气利润，否则填配售气合计利润"
          },{
            data_name:"      其中：1.配（售）气利润",
            data_row:"1",
            data_value:"",
            data_note:"配气和售气业务分离的只填写配气利润，否则填配售气合计利润"
          },{
            data_name:"            2.工程安装利润",
            data_row:"8",
            data_value:"",
            data_note:""
          },{
            data_name:"            3.其他业务利润",
            data_row:"9",
            data_value:"",
            data_note:""
          }
        ]
      }
    }
  }
</script>

<style scoped>

</style>
