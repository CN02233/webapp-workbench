package com.seaboxdata.cqny.record.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.seaboxdata.cqny.origin.service.SubmitauthorityService;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 报送审批
 */
@Controller
@RequestMapping("reportApproval")
public class ReportApprovalController {

    @Autowired
    private ReportApprovalService reportApprovalService;

    @Autowired
    private OriginService originService;

    @Autowired
    private SubmitauthorityService submitauthorityService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    /**
     * 审核报表列表查询展示
     * update 20190508
     * 报送审批工作由地市用户审批
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportApproval")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportApproval(int currPage, int pageSize,String searchOriginId,String searchOriginName){
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();
        String userType = currUser.getUser_type();


        if(!"0".equals(userType)){//0:审核用户
            String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, null);
            return jsonpResponse;
        }else{
            Origin originUser = originService.getOriginByUser(currUserId);
            PageResult emptyResult = new PageResult();
            emptyResult.setCurrPage(currPage);
            emptyResult.setPageSize(pageSize);
            emptyResult.setTotalNum(1);
            emptyResult.setTotalPage(1);
            emptyResult.setDataList(null);

            if(originUser==null){
                String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, emptyResult);
                return jsonpResponse;
            }

            Integer parentOriginId = originUser.getParent_origin_id();



            if(parentOriginId == 1){//省级机构 无权限
                String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, emptyResult);
                return jsonpResponse;
            }else{//市级机构 无权限

            }

//        if(ReportStatus.REVIEW.compareTo(reportStatus)){
//            if(parentOriginId == 1){//省级机构 有权限
//            }else{//市级机构 无权限
//                return emptyResult;
//            }
//        }else{
//
//        }

            Origin userOrigin = originService.getOriginByUser(currUserId);
            Integer userOriginId = userOrigin.getOrigin_id();

            List<Origin> allOrigins = originService.listAllOrigin();
            List<Origin> childrenOrigin = null;

            List<Integer> originParams = new ArrayList<>();
            if(Strings.isNullOrEmpty(searchOriginId)&&Strings.isNullOrEmpty(searchOriginName)){
                childrenOrigin = originService.checkoutSons(userOriginId,allOrigins);
                originParams.add(userOriginId);
            }else if(!Strings.isNullOrEmpty(searchOriginId)){
                childrenOrigin = originService.checkoutSons(new Integer(searchOriginId),allOrigins);
            }

            if(Strings.isNullOrEmpty(searchOriginId)&&!Strings.isNullOrEmpty(searchOriginName)){

                childrenOrigin = originService.checkoutSons(userOriginId,allOrigins);
                childrenOrigin.add(userOrigin);
                List<Integer> childrenOriginIds = new ArrayList<>();
                for (Origin origin : childrenOrigin) {
                    childrenOriginIds.add(origin.getOrigin_id());
                }

                List<Origin> origins = originService.getOriginByName(searchOriginName);
                for (Origin origin : origins) {
                    if(childrenOriginIds.contains(origin.getOrigin_id())&&!originParams.contains(origin.getOrigin_id())){
                        originParams.add(origin.getOrigin_id());
                    }else if(userOriginId.equals(origin.getOrigin_id())){
                        originParams.add(userOriginId);
                    }
                }
            }else{
                if(childrenOrigin!=null){
                    for (Origin origin : childrenOrigin) {
                        originParams.add(origin.getOrigin_id());
                    }
                }
            }
            PageResult pageResult = reportApprovalService.listReportApproval(
                    ReportStatus.SUBMIT.getValue(),
                    currUser.getUser_id(),
                    currPage,
                    pageSize,originParams);

            List<ReportCustomer> resultData = pageResult.getDataList();
            for (ReportCustomer resultDatum : resultData) {
                Integer reportOriginId = resultDatum.getReport_origin();
                Map<String, Origin> result = originService.getFist2Origin(reportOriginId, allOrigins);
                if(result.get("cityOrigin")!=null)
                    resultDatum.setOrigin_city(result.get("cityOrigin").getOrigin_name());
                if(result.get("provinceOrigin")!=null)
                    resultDatum.setOrigin_province(result.get("provinceOrigin").getOrigin_name());
            }


            Collection<Map<String, Object>> first2Origin = originService.checkProvAndCity(allOrigins);


            Map<String,Object> responseMap = new HashMap<>();
            responseMap.put("currPage",pageResult.getCurrPage());
            responseMap.put("dataList",pageResult.getDataList());
            responseMap.put("pageSize",pageResult.getPageSize());
            responseMap.put("totalNum",pageResult.getTotalNum());
            responseMap.put("totalPage",pageResult.getTotalPage());
            responseMap.put("origins",childrenOrigin);
            responseMap.put("first2Origin",first2Origin);

            String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, responseMap);
            return jsonpResponse;
        }


    }
    /**
     * 复核报表列表查询展示
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportReview")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportReview(int currPage, int pageSize,String searchOriginId,String searchOriginName){

        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();
        String userType = currUser.getUser_type();


        if(!"0".equals(userType)){//0:审核用户
            String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, null);
            return jsonpResponse;
        }else{
            Origin originUser = originService.getOriginByUser(currUserId);
            PageResult emptyResult = new PageResult();
            emptyResult.setCurrPage(currPage);
            emptyResult.setPageSize(pageSize);
            emptyResult.setTotalNum(1);
            emptyResult.setTotalPage(1);
            emptyResult.setDataList(null);

            if(originUser==null){
                String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, emptyResult);
                return jsonpResponse;
            }

            Integer parentOriginId = originUser.getParent_origin_id();


            if(parentOriginId == 1){//省级机构 有权限
            }else{//市级机构 无权限
                String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, emptyResult);
                return jsonpResponse;
            }
            Origin userOrigin = originService.getOriginByUser(currUserId);
            Integer userOriginId = userOrigin.getOrigin_id();

            List<Origin> allOrigins = originService.listAllOrigin();
            List<Origin> childrenOrigin = null;

            List<Integer> originParams = new ArrayList<>();
            if(Strings.isNullOrEmpty(searchOriginId)&&Strings.isNullOrEmpty(searchOriginName)){
                childrenOrigin = originService.checkoutSons(userOriginId,allOrigins);
                originParams.add(userOriginId);
            }else if(!Strings.isNullOrEmpty(searchOriginId)){
                childrenOrigin = originService.checkoutSons(new Integer(searchOriginId),allOrigins);
            }

            if(Strings.isNullOrEmpty(searchOriginId)&&!Strings.isNullOrEmpty(searchOriginName)){

                childrenOrigin = originService.checkoutSons(userOriginId,allOrigins);
                childrenOrigin.add(userOrigin);
                List<Integer> childrenOriginIds = new ArrayList<>();
                for (Origin origin : childrenOrigin) {
                    childrenOriginIds.add(origin.getOrigin_id());
                }

                List<Origin> origins = originService.getOriginByName(searchOriginName);
                for (Origin origin : origins) {
                    if(childrenOriginIds.contains(origin.getOrigin_id())&&!originParams.contains(origin.getOrigin_id())){
                        originParams.add(origin.getOrigin_id());
                    }else if(userOriginId.equals(origin.getOrigin_id())){
                        originParams.add(userOriginId);
                    }
                }
            }else{
                if(childrenOrigin!=null){
                    for (Origin origin : childrenOrigin) {
                        originParams.add(origin.getOrigin_id());
                    }
                }
            }

            PageResult pageResult = null;

            if(originParams!=null&&originParams.size()>0){
                pageResult = reportApprovalService.listReportApproval(
                        ReportStatus.REVIEW.getValue(),
                        currUser.getUser_id(),
                        currPage,
                        pageSize,originParams);

                List<ReportCustomer> resultData = pageResult.getDataList();
                for (ReportCustomer resultDatum : resultData) {
                    Integer reportOriginId = resultDatum.getReport_origin();
                    Map<String, Origin> result = originService.getFist2Origin(reportOriginId, allOrigins);
                    if(result.get("cityOrigin")!=null)
                        resultDatum.setOrigin_city(result.get("cityOrigin").getOrigin_name());
                    if(result.get("provinceOrigin")!=null)
                        resultDatum.setOrigin_province(result.get("provinceOrigin").getOrigin_name());
                }
            }else{
                pageResult = new PageResult();
                pageResult.setCurrPage(currPage);
                pageResult.setPageSize(pageSize);
                pageResult.setTotalNum(0);
                pageResult.setTotalPage(1);
                ArrayList<Object> resultList = Lists.newArrayList();
                resultList.addAll(resultList);
                pageResult.setDataList(resultList);
            }




            Collection<Map<String, Object>> first2Origin = originService.checkProvAndCity(allOrigins);


            Map<String,Object> responseMap = new HashMap<>();
            responseMap.put("currPage",pageResult.getCurrPage());
            responseMap.put("dataList",pageResult.getDataList());
            responseMap.put("pageSize",pageResult.getPageSize());
            responseMap.put("totalNum",pageResult.getTotalNum());
            responseMap.put("totalPage",pageResult.getTotalPage());
            responseMap.put("origins",childrenOrigin);
            responseMap.put("first2Origin",first2Origin);

            String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, responseMap);
            return jsonpResponse;
        }

    }

    @RequestMapping("listAllSupervision")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult listAllSupervision(int currPage, int pageSize, String searchOriginId, String searchOriginName){
        //获取当前用户所属行政机构
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();
        String userType = currUser.getUser_type();
        if(!"2".equals(userType)){
            JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, null);
            return response;
        }

        List<String> allReportOriginIds = submitauthorityService.getReportOriginForOrganizationUser(currUserId);

        if(allReportOriginIds!=null&&allReportOriginIds.size()>0){

        }else{
            JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, null);
            return response;
        }

        //根据行政机构获取报送机构

        List<Origin> allReportOrigins = originService.listAllOrigin();
        List<Integer>  reportOrigins = new ArrayList<Integer>();
        if(allReportOriginIds!=null&&allReportOriginIds.size()>0){
            for (String allReportOriginId : allReportOriginIds) {
                Integer allReportOriginIdInt = new Integer(allReportOriginId);
                List<Origin> allChildrens = originService.checkoutSons(allReportOriginIdInt, allReportOrigins);
                if(allChildrens!=null&&allChildrens.size()>0){
                    for (Origin childOrin : allChildrens) {
                        if(!reportOrigins.contains(childOrin.getOrigin_id())){
                            reportOrigins.add(childOrin.getOrigin_id());
                        }
                    }
                }
            }
        }

        List<Integer> searchOriginList = new ArrayList<>();

        if(!Strings.isNullOrEmpty(searchOriginId)){
            List<Origin> searchOrigins = originService.checkoutSons(new Integer(searchOriginId), allReportOrigins);
            for (Origin searchOrigin : searchOrigins) {
                searchOriginList.add(searchOrigin.getOrigin_id());
            }
        }else{
            if(!Strings.isNullOrEmpty(searchOriginName)){
                List<Origin> searchOrigins = originService.getOriginByName(searchOriginName);
                for (Origin searchOrigin : searchOrigins) {
                    searchOriginList.add(searchOrigin.getOrigin_id());
                }
            }
        }

        if(searchOriginList!=null&&searchOriginList.size()>0){
            for (Integer searchReportOriginId : searchOriginList) {
                if(!reportOrigins.contains(searchReportOriginId)){
                    searchOriginList.remove(searchReportOriginId);
                }
            }
            reportOrigins = searchOriginList;
        }

//        int splitOrigins = reportOrigins.size() / 1000;

        PageResult pageResult = reportCustomerService.pagerReport(currPage, pageSize, reportOrigins);

        List<ReportCustomer> resultData = pageResult.getDataList();
        for (ReportCustomer resultDatum : resultData) {
            Integer reportOriginId = resultDatum.getReport_origin();
            Map<String, Origin> result = originService.getFist2Origin(reportOriginId, allReportOrigins);
            if(result.get("cityOrigin")!=null)
                resultDatum.setOrigin_city(result.get("cityOrigin").getOrigin_name());
            if(result.get("provinceOrigin")!=null)
                resultDatum.setOrigin_province(result.get("provinceOrigin").getOrigin_name());
        }


        Collection<Map<String, Object>> first2Origin = originService.checkProvAndCity(allReportOrigins);


        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("currPage",pageResult.getCurrPage());
        responseMap.put("dataList",pageResult.getDataList());
        responseMap.put("pageSize",pageResult.getPageSize());
        responseMap.put("totalNum",pageResult.getTotalNum());
        responseMap.put("totalPage",pageResult.getTotalPage());
        responseMap.put("first2Origin",first2Origin);

        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, responseMap);
        return response;
    }

    /**
     * 审核报送报表
     * @param reportId
     * @param reportStatus
     * 审核通过 reportStatus="pass"
     * 驳回 reportStatus="reject"
     * @return
     */
    @RequestMapping("ReportApprovalOperator")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult ReportApprovalOperator( String reportId,String reportStatus){
        ReportCustomer reportCust = reportCustomerService.checkReportCustomer(reportId);

        String passReview = reportCust.getPass_review();

        if("Y".equals(passReview)){
            if("pass".equals(reportStatus)){
                reportCustomerService.updateReportCustomerStatus(reportId,ReportStatus.REPORT_DONE);
                JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "审核成功", null,null);
                return jsonResult;
            }
        }

        reportApprovalService.reportApprovalOperator(reportId,reportStatus);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "审核成功", null,null);
        return jsonResult;
    }

    /**
     * 复核报送报表
     * @param reportId
     * @param reportStatus
     * 复核通过 reportStatus="pass"
     * 驳回 reportStatus="reject"
     * 重填 reportStatus="refill"
     * @return
     */
    @RequestMapping("ReportReviewOperator")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult reportReviewOperator( String reportId,String reportStatus){
        ReportCustomer reportCust = reportCustomerService.checkReportCustomer(reportId);
        String passApprove = reportCust.getPass_approve();
        if("Y".equals(passApprove)){//免审核，驳回需要直接回退到填报中
            if("reject".equals(reportStatus)) {
                reportCustomerService.updateReportCustomerStatus(reportId,ReportStatus.NORMAL);
                JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "复核成功", null,null);
                return jsonResult;
            }
        }
        reportApprovalService.reportReviewOperator(reportId,reportStatus);


        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "复核成功", null,null);
        return jsonResult;
    }


    @RequestMapping("batchReportApprovalOperator")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult batchReportApprovalOperator(@RequestBody Map<String,Object> approveParams){
        List<Double> reportIdList = null;
        String operator = null;
        if(approveParams.containsKey("report_id_list")){
            reportIdList = (List<Double>) approveParams.get("report_id_list");
        }

        if(approveParams.containsKey("operator")){
            operator = (String) approveParams.get("operator");
        }

        List<String> reportIdStrLIst =new ArrayList<>();
        for (Double reportIdDouble : reportIdList) {
            reportIdStrLIst.add(reportIdDouble.toString());
        }

        reportApprovalService.batchReportApprovalOperator(reportIdStrLIst,operator);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "批量审核完成", null,JsonResult.RESULT.SUCCESS);
        return jsonResult;
    }

    @RequestMapping("batchReportReviewOperator")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult batchReportReviewOperator(@RequestBody Map<String,Object> reviewParams){
        List<Double> reportIdList = null;
        String operator = null;
        if(reviewParams.containsKey("report_id_list")){
            reportIdList = (List<Double>) reviewParams.get("report_id_list");
        }

        if(reviewParams.containsKey("operator")){
            operator = (String) reviewParams.get("operator");
        }

        List<String> reportIdStrLIst =new ArrayList<>();
        for (Double reportIdDouble : reportIdList) {
            reportIdStrLIst.add(reportIdDouble.toString());
        }

        reportApprovalService.batchReportReviewOperator(reportIdStrLIst,operator);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "批量审核完成", null,JsonResult.RESULT.SUCCESS);
        return jsonResult;
    }
}
