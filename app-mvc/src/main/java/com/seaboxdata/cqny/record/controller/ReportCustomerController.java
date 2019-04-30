package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("reportCust")
public class ReportCustomerController {

    @Autowired
    private ReportCustomerService reportCustomerService;


    @Autowired
    private OriginService originService;


    /**
     * 获取当前用户权限下报送列表
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("pagerReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerReport(Integer currPage, Integer pageSize){
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();

        Origin userOrigin = originService.getOriginByUser(currUserId);
        Integer userOriginId = userOrigin.getOrigin_id();
        List<Origin> childrenOrigin = originService.checkAllChildren(userOriginId);
        List<Integer> originParams = new ArrayList<>();
        originParams.add(userOriginId);
        if(childrenOrigin!=null){
            for (Origin origin : childrenOrigin) {
                originParams.add(origin.getOrigin_id());
            }
        }

        PageResult pageResult = reportCustomerService.pagerReport(currPage, pageSize, originParams);
        childrenOrigin.add(userOrigin);

        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("currPage",pageResult.getCurrPage());
        responseMap.put("dataList",pageResult.getDataList());
        responseMap.put("pageSize",pageResult.getPageSize());
        responseMap.put("totalNum",pageResult.getTotalNum());
        responseMap.put("totalPage",pageResult.getTotalPage());
        responseMap.put("origins",childrenOrigin);


        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,responseMap);
        return jsonResult;
    }

    @RequestMapping("getReportInfos")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getReportInfos(){
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();
        Origin userOrigin = originService.getOriginByUser(currUserId);
        Integer userOriginId = userOrigin.getOrigin_id();
        Map<ReportStatus, Integer> reportInfos = reportCustomerService.getReportInfos(userOriginId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,reportInfos);
        return jsonResult;
    }

    @RequestMapping("getChildrenReportInfos")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getChildrenReportInfos(Integer currPage, Integer pageSize){
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();

        Origin userOrigin = originService.getOriginByUser(currUserId);
        Integer userOriginId = userOrigin.getOrigin_id();
        List<Origin> childrenOrigin = originService.checkAllChildren(userOriginId);
        List<Integer> originParams = new ArrayList<>();
        if(childrenOrigin!=null){
            for (Origin origin : childrenOrigin) {
                originParams.add(origin.getOrigin_id());
            }
        }

        PageResult reportInfos = reportCustomerService.getChildrenReportInfos(currPage, pageSize,originParams);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,reportInfos);
        return jsonResult;
    }

    /**
     * 获取当前报表填报的所在步骤
     * @param reportId
     * @return
     */
    @RequestMapping("checkUnitStep")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult checkUnitStep(String reportId){

        ReportCustomer unitStepInfo = reportCustomerService.checkReportCustomer(reportId);


        JsonResult jsonResult = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取欧成功", null,unitStepInfo);
        return jsonResult;
    }

    @RequestMapping("getReportBaseInfo")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getReportBaseInfo(String reportId,String reportDefinedId){
        Map<String,Object> reportBaseInfo = reportCustomerService.getReportBaseInfo(reportId,reportDefinedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取欧成功", null,reportBaseInfo);
        return jsonResult;
    }

    @RequestMapping("getUnitContext")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getUnitContext(String reportId, String unitId, String unitType){
//        ReportCustomer reportCustomer = reportCustomerService.checkReportCustomer(reportId);
//        Integer currOrder = 0;
//        Integer getUnitOrder = 0;
//        Integer activeUnitId = reportCustomer.getActive_unit();
//        for (UnitDefined unitEntity : reportCustomer.getUnitEntities()) {
//            if(unitEntity.getUnit_id().equals(activeUnitId)){
//                currOrder = unitEntity.getUnit_order();
//            }
//
//            if(unitEntity.getUnit_id().toString().equals(unitId)){
//                getUnitOrder = unitEntity.getUnit_order();
//            }
//
//
//        }
//        if(getUnitOrder!=null&&currOrder!=null&&getUnitOrder>currOrder){
//            JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "该步骤还未填写", "该步骤还未填写",null);
//            return jsonResult;
//
//        }
        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,unitContext);

        return jsonResult;
    }

    @RequestMapping("saveSimpleUnitContext")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult saveSimpleUnitContext(@RequestBody SaveSimpleUnitContext saveSimpleUnitContext){

        reportCustomerService.updateOrInsertSimpleUnitContext(
                saveSimpleUnitContext.getDefinedColums(),
                saveSimpleUnitContext.getColumDatas(),
                true);
//        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,null);

        return jsonResult;
    }

    @RequestMapping("validateSimpleUnitContext")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult validateSimpleUnitContext(@RequestBody SaveSimpleUnitContext saveSimpleUnitContext){

        Map<String, String> validateResult = reportCustomerService.validateSimpleUnitByColum(saveSimpleUnitContext.getDefinedColums(),saveSimpleUnitContext.getColumDatas());
//        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "校验完成", null,validateResult);

        return jsonResult;
    }

    @RequestMapping("validateSimpleUnitByDimensions")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult validateSimpleUnitByDimensions(@RequestBody SaveSimpleUnitContext saveSimpleUnitContext){

        Map<String, String> validateResult = reportCustomerService.validateSimpleUnitByDimensions(saveSimpleUnitContext.getDefinedColums(),saveSimpleUnitContext.getColumDatas());
//        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "校验完成", null,validateResult);

        return jsonResult;
    }

    @RequestMapping("validateGridUnit")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult validateGridUnit(@RequestBody SaveGridUnitContext saveGridUnitContext){

        Map<String, String> validateResult = reportCustomerService.validateGridUnit(saveGridUnitContext.getDefinedColums(),
                saveGridUnitContext.getColumDatas());
//        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "校验完成", null,validateResult);

        return jsonResult;
    }

    @RequestMapping("updateStep")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult updateStep(String reportId){

        reportCustomerService.updateStep(reportId);
//        ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId, unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);

        return jsonResult;
    }

    @RequestMapping("saveGroupUnitContext")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult saveGroupUnitContext(@RequestBody SaveSimpleUnitContext saveUnitContext){
        reportCustomerService.updateOrInsertGroupUnitContext(saveUnitContext.getDefinedColums(),saveUnitContext.getColumDatas(),true);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,null);
        return jsonResult;
    }

    @RequestMapping("saveGridUnitContext")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult saveGridUnitContext(@RequestBody SaveGridUnitContext saveUnitContext){

        reportCustomerService.updateOrInsertGridUnitContext(saveUnitContext.getDefinedColums(),saveUnitContext.getColumDatas(),true);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,null);

        return jsonResult;
    }

    @RequestMapping("refreshFomular")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult refreshFomular(String reportDefinedId,String reportId){
        reportCustomerService.refreshFomular(reportDefinedId,reportId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "刷新成功", null,null);

        return jsonResult;
    }


    @RequestMapping("doCommitAuth")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult doCommitAuth(String reportId){
        ReportCustomer reportCustomer = reportCustomerService.checkReportCustomer(reportId);
        if("Y".equals(reportCustomer.getPass_auth())){
            reportCustomerService.updateReportCustomerStatus(reportId, ReportStatus.APPROVE);
        }else{
            reportCustomerService.updateReportCustomerStatus(reportId, ReportStatus.SUBMIT);
        }
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,null);

        return jsonResult;
    }

    class SaveSimpleUnitContext{
        private ArrayList<ReportCustomerData> columDatas;
        private ArrayList<SimpleColumDefined> definedColums;
        private String saveType;

        SaveSimpleUnitContext(String saveType) {
            this.saveType = saveType;
        }

        public ArrayList<ReportCustomerData> getColumDatas() {
            return columDatas;
        }

        public void setColumDatas(ArrayList<ReportCustomerData> columDatas) {
            this.columDatas = columDatas;
        }

        public ArrayList<SimpleColumDefined> getDefinedColums() {
            return definedColums;
        }

        public void setDefinedColums(ArrayList<SimpleColumDefined> definedColums) {
            this.definedColums = definedColums;
        }

        public String getSaveType() {
            return saveType;
        }

        public void setSaveType(String saveType) {
            this.saveType = saveType;
        }
    }
    class SaveGridUnitContext{
        private ArrayList<ReportCustomerData> columDatas;
        private ArrayList<GridColumDefined> definedColums;

        public ArrayList<ReportCustomerData> getColumDatas() {
            return columDatas;
        }

        public void setColumDatas(ArrayList<ReportCustomerData> columDatas) {
            this.columDatas = columDatas;
        }

        public ArrayList<GridColumDefined> getDefinedColums() {
            return definedColums;
        }

        public void setDefinedColums(ArrayList<GridColumDefined> definedColums) {
            this.definedColums = definedColums;
        }
    }
}
