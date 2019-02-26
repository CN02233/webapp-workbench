<template>
  <WorkMain :headerItems="['报送管理','报表管理','创建报表']">

    <table class="excel-table show-border">
      <tr v-for="reportRow in reportRows">
        <td class="show-border" v-bind:rowspan="reportColum.rowspan" v-bind:colspan="reportColum.colspan" v-for="reportColum in reportRow">
          <div v-if="reportColum.input"><el-input disabled="true" size="mini" suffix-icon="el-icon-edit"></el-input></div>
          <div v-else>{{reportColum.val}}</div>
        </td>
      </tr>
    </table>

    <!--<table>-->
      <!--<tr>-->
        <!--<td class="show-border" rowspan="2">11</td>-->
        <!--<td class="show-border" colspan="2">12</td>-->
        <!--<td class="show-border" rowspan="2">13</td>-->
        <!--<td class="show-border" >14</td>-->
      <!--</tr>-->
      <!--<tr>-->
        <!--<td class="show-border">22</td>-->
        <!--<td class="show-border">23</td>-->
        <!--<td class="show-border">24</td>-->
      <!--</tr>-->
    <!--</table>-->

    <!--<div class="excel-table show-border">-->

      <!--<div class="excel-row" v-for="reportRow in reportRows">-->
        <!--<div class="show-border excel-colum" v-bind="{}" v-for="reportColum in reportRow">-->


        <!--</div>-->
      <!--</div>-->
    <!--</div>-->

  </WorkMain>

</template>

<script>
  import WorkTablePager from "@/models/public/WorkTablePager"
  import WorkMain from "@/models/public/WorkMain"
  import { required } from 'vuelidate/lib/validators'

  export default {
    name: "report-create.vue",
    components: {
      WorkTablePager,
      WorkMain
    },
    data() {
      return {
        isCreate:true,
        templateId:'',
        reportId:'',
        reportName:'',
        reportContext:[],
        reportRows:[]
      }
    },
    methods: {
      getReportContext() {
        const $this = this
        if(this.isCreate){//获取模板信息
          const loading = this.$loading({
            lock: true,
            text: '正在根据所选模板创建报表.......',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          this.BaseRequest({
            url:"/report/createReport",
            params:{
              templateIdOrName:this.templateId,
              reportName:this.reportName
            }
          }).then(response=>{
            setTimeout(() => {
              loading.close();
            }, 1000);

            this.reportId = response
            requestReportContext(this.reportId)
          });
        }else{//获取报表信息
          requestReportContext(this.reportId)
        }
        function requestReportContext(){
          $this.BaseRequest({
            url:"/report/loadReport",
            params:{
              reportIdOrName:$this.reportId
            }
          }).then(response=>{
            $this.reportContext = response
            $this.showReportContext()
          });
        }

      },
      showReportContext(){
        const $this = this
        if(this.reportContext!=null){
          this.reportContext.forEach(reportSheet=>{
            if(reportSheet!=null){
              $this.reportRows = reportSheet
            }
          })
        }
      }
    },
    mounted() {
      this.templateId = this.$route.query.template
      this.reportName = this.$route.query.reportName
      this.isCreate = this.$route.query.isCreate
      this.reportId = this.$route.query.reportId
      this.getReportContext()
    }
  }
</script>

<style>
</style>

<style scoped>
  .show-border{
    border:1px solid black;
  }
  .excel-table{
    width:100%;
    height:100%;
  }

  .excel-row{
    float:left;
    width:100%;
    overflow: auto;
  }

  .excel-colum{
    float: left;
    width:100px;
    height:20px;
    padding:10px 0 10px 0;
  }
</style>
