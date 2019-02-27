package com.seaboxdata.cqny.record.controller;


import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        List<List<List<ReportCell>>> reportInfo = reportService.loadReport(reportIdOrName);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }


    @RequestMapping("editSave")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public List<List<List<ReportCell>>> editSave(String reportCellsJson, String reportId){
        ArrayList<ArrayList<ReportCell>> reportCells = (ArrayList<ArrayList<ReportCell>>) JsonSupport.jsonToObect(reportCellsJson, ArrayList.class);
        List<List<List<ReportCell>>> reloadFileContext = reportService.editSave(reportCells, reportId);
        return reloadFileContext;
    }

}
