package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.entity.SubmitReportRequestEntity;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 报送报表的新增 修改 删除 查询
 */
@Controller
@RequestMapping("reportStatements")
public class ReportStatementsController {

    private Logger logger = LoggerFactory.getLogger(ReportStatementsService.class);

    @Autowired
    private ReportStatementsService reportStatementsService;

    @Autowired
    private SubmitReportService reportDefinedSubmitService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    public ReportStatementsController() {
    }

    /**
     * 列表查询展示
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportStatements")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportStatements(int currPage, int pageSize){
        PageResult originList = reportStatementsService.listReportStatements(currPage, pageSize);
        List<ReportDefinedEntity> reportDefinedList = originList.getDataList();
        if(reportDefinedList!=null){
            for (ReportDefinedEntity reportDefinedEntity : reportDefinedList) {
                Integer reportDefinedId = reportDefinedEntity.getDefined_id();
                Map<String, Object> reportBaseInfo = reportCustomerService.getReportBaseInfo(null, reportDefinedId.toString());
                if(reportBaseInfo!=null){
                    reportDefinedEntity.setReportDataEnd(String.valueOf(reportBaseInfo.get("reportDataEnd")!=null?reportBaseInfo.get("reportDataEnd"):""));
                    reportDefinedEntity.setReportDataStart(String.valueOf(reportBaseInfo.get("reportDataStart")!=null?reportBaseInfo.get("reportDataStart"):""));
                    reportDefinedEntity.setReportStartDate(String.valueOf(reportBaseInfo.get("reportStartDate")!=null?reportBaseInfo.get("reportStartDate"):""));
                    reportDefinedEntity.setReportEndDate(String.valueOf(reportBaseInfo.get("reportEndDate")!=null?reportBaseInfo.get("reportEndDate"):""));
                }
            }
        }
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
    }

    /**
     * 新增/修改报送报表
     * @param reportDefined
     * @return
     */
    @RequestMapping("addReportStatements")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addReportStatements(@RequestBody ReportDefinedEntity reportDefined){
        User user = SessionSupport.checkoutUserFromSession();
        reportDefined.setCreate_user(user.getUser_id());
        reportStatementsService.addReportStatements(reportDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,reportDefined);
        return jsonResult;
    }

    /**
     * 保存关联
     * @return
     */
    @RequestMapping("saveDefinedAndOriginAssign")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    @Transactional
    public JsonResult saveDefinedAndOriginAssign(@RequestBody Map<String,Object> definedOrigins){
        List<String> originIds = (ArrayList<String>) definedOrigins.get("originIds");
        Double definedIdD = (Double) definedOrigins.get("definedId");
        String definedId = definedIdD.toString();
        delDefinedAndOriginAssign(definedId);
        reportStatementsService.saveDefinedAndOriginAssign(originIds,definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    /**
     * 删除关联
     * @return
     */
    @RequestMapping("delDefinedAndOriginAssign")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult delDefinedAndOriginAssign(String definedId){
        reportStatementsService.delDefinedAndOriginAssign(definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    /**
     * 获取关联
     * @return
     */
    @RequestMapping("getDefinedAndOriginAssignById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getDefinedAndOriginAssignById(String definedId){
        List<String> result=reportStatementsService.getDefinedAndOriginAssignById(definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,result);
        return jsonResult;
    }

    @RequestMapping("getDefinedOriginsById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getDefinedOriginsById(String definedId){
        List<Origin> result=reportStatementsService.getDefinedOriginsById(definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,result);
        return jsonResult;
    }

    @RequestMapping("getDefinedOriginTreeById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getDefinedOriginTreeById(String definedId){
        Origin resultList =reportStatementsService.getDefinedOriginTreeById(definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,resultList);
        return jsonResult;
    }


    /**
     * 删除报送报表
     * @param definedId
     * @return
     */
    @RequestMapping("delById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    @Transactional
    public JsonResult deleteById( String definedId){
        reportStatementsService.deleteById(definedId);
        reportStatementsService.delDefinedAndOriginAssign(definedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

    /**
     * 报送监管列表查询展示
     * 通过行政用户的ID->行政机构->报送机构->属于报送机构的所有报送报表
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportStatementsByUser")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportStatementsByUser(int currPage, int pageSize,String originId){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult originList = reportStatementsService.listReportStatementsByUser(currPage, pageSize,user.getUser_id(),originId);
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
    }


//    reportDefinedSubmitService

    @RequestMapping("sumitReportDefined")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String sumitReportDefined(@RequestBody SubmitReportRequestEntity submitReportEntity){
        logger.info("更新报表状态为发布中-->{}",submitReportEntity.getDefined_id());
        reportStatementsService.changeDeindStatus(submitReportEntity.getDefined_id(), ReportDefinedStatus.SUBMITING);

        new Thread(() -> {
            try {
                reportDefinedSubmitService.doSubmit(submitReportEntity);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("报表发布出现异常->{},回滚报表状态为正常",submitReportEntity.getDefined_id());
                reportStatementsService.changeDeindStatus(submitReportEntity.getDefined_id(), ReportDefinedStatus.NORMAL);
            }
        }).start();
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "已开始发布", null, null);
        return jsonpResponse;
    }

    /**
     * 获取用户的报送机构name-id
     * @return
     */
    @RequestMapping("getOriginsByUserId")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String getOriginsByUserId(){
        User user = SessionSupport.checkoutUserFromSession();
        List<HashMap<String,String>> originMap = reportStatementsService.getOriginsByUserId(user.getUser_id());
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originMap);
        return jsonpResponse;
    }

    public JsonResult copyReportDefind(String reportDefinedId){

        JsonResult responseJsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, null);

        return responseJsonResult;
    }



    @RequestMapping("getAuthOriginTree")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getAuthOriginTree(String reportDefinedId){
        List<Origin> orginList = reportStatementsService.getAuthOriginTree(reportDefinedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

}
