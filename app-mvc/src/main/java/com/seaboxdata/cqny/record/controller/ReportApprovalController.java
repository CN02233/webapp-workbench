package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 报送审批
 */
@Controller
@RequestMapping("reportApproval")
public class ReportApprovalController {

    @Autowired
    private ReportApprovalService reportApprovalService;

    /**
     * 审核报表列表查询展示
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportApproval")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportApproval(int currPage, int pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult originList = reportApprovalService.listReportApproval(ReportStatus.SUBMIT.toString(),user.getUser_id(),currPage, pageSize);
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
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
    public String listReportReview(int currPage, int pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult originList = reportApprovalService.listReportApproval(ReportStatus.REVIEW.toString(),user.getUser_id(),currPage, pageSize);
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
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
        reportApprovalService.reportApprovalOperator(reportId,reportStatus);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
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
        reportApprovalService.reportReviewOperator(reportId,reportStatus);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }
}
