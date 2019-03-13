package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.service.ReportApproveService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("reportApprove")
public class ReportApproveController {

    @Autowired
    private ReportApproveService reportApproveService;

    /**
     * 获取已提交的报表列表
     * @return
     */
    @RequestMapping("submitReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getSubmitList(Integer currPage,Integer pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult reportInfo = reportApproveService.pageReportApproves(user.getUser_id(), currPage, pageSize);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

    @RequestMapping("confirmList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult confirmList(Integer currPage,Integer pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult reportInfo = reportApproveService.pageReportConfirms(user.getUser_id(), currPage, pageSize);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

    @RequestMapping("doReview/{reviewStatus}")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult doReview(@RequestParam String reportId, @PathVariable String reviewStatus){
        reportApproveService.doReview(reportId,reviewStatus);

        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, null);
        return response;
    }

    @RequestMapping("doConfirm/{reviewStatus}")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult doConfirm(@RequestParam String reportId, @PathVariable String reviewStatus){
        reportApproveService.doConfirm(reportId,reviewStatus);

        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, null);
        return response;
    }

}
