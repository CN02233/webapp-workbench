package com.seaboxdata.cqny.record.controller;


import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @RequestMapping("reportList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult reportList(int currPage, int pageSize){
        User nowUser = SessionSupport.checkoutUserFromSession();
        PageResult reportInfo = reportService.reportList(nowUser.getUser_id(),currPage,pageSize);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

    @RequestMapping("createReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult createReport(String templateIdOrName,String reportName) throws IOException {
        try {
            String reportIdOrName = reportService.createReport(templateIdOrName,reportName);
            JsonResult response = JsonSupport.makeJsonpResult(
                    JsonResult.RESULT.SUCCESS, "获取成功", null, reportIdOrName);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof FileNotFoundException){
                JsonResult response = JsonSupport.makeJsonpResult(
                        JsonResult.RESULT.FAILD, "获取成功", null, e.getMessage());
                return response;
            }
            throw e;
        }

    }


    @RequestMapping("loadReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult loadReport(String reportIdOrName){
        ReportInfo reportInfo = reportService.loadReport(reportIdOrName);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

    @RequestMapping("loadReportData")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult loadReportData(String reportIdOrName){
        ReportInfo reportInfo = reportService.loadReportData(reportIdOrName);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

    @RequestMapping("loadReportBasic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult loadReportBasic(String reportId){
        ReportInfo reportInfo = reportService.loadReportBasic(reportId);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }


    @RequestMapping("editSave")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSave(@RequestBody ReportCells reportCells){
        reportService.editSave(reportCells.getReportCells(),reportCells.getReportId(),reportCells.getSheetId());
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "保存成功", null, null);
        return response;
    }

    @RequestMapping("fullEditSave")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult fullEditSave(@RequestBody ReportCells reportCells){
        reportService.fullEditSave(reportCells.getReportCells(),reportCells.getReportMerged(),reportCells.getReportId(),reportCells.getTemplateId(),reportCells.getSheetId());
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "保存成功", null, null);
        return response;
    }

    @RequestMapping("lockReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult lockReport(String reportId){
        int userId = ((User) SessionSupport.checkoutUserFromSession()).getUser_id();
        reportService.lockReport(reportId,userId);
        return  JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "锁定成功", null, null);
    }

    @RequestMapping("submitReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult submitReport(String reportId){
        int userId = ((User) SessionSupport.checkoutUserFromSession()).getUser_id();
        reportService.submitReport(reportId,userId);
        return  JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "提交成功", null, null);
    }

    @RequestMapping("reviewReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult reviewReport(String reportId){
        int userId = ((User) SessionSupport.checkoutUserFromSession()).getUser_id();
        reportService.reviewReport(reportId,userId);
        return  JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "审批通过", null, null);
    }

    @RequestMapping("confirmReport")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult confirmReport(String reportId){
        int userId = ((User) SessionSupport.checkoutUserFromSession()).getUser_id();
        reportService.confirmReport(reportId,userId);
        return  JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "复核通过", null, null);
    }


    class ReportCells{
        private ArrayList<ArrayList<String>> reportCells = new ArrayList<>();

        private ArrayList<Map<String,String>> reportMerged = new ArrayList<>();

        private String reportId;

        private String templateId;

        private String sheetId;

        public ArrayList<ArrayList<String>> getReportCells() {
            return reportCells;
        }

        public void setReportCells(ArrayList<ArrayList<String>> reportCells) {
            this.reportCells = reportCells;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public String getSheetId() {
            return sheetId;
        }

        public void setSheetId(String sheetId) {
            this.sheetId = sheetId;
        }

        public ArrayList<Map<String, String>> getReportMerged() {
            return reportMerged;
        }

        public void setReportMerged(ArrayList<Map<String, String>> reportMerged) {
            this.reportMerged = reportMerged;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }

}
