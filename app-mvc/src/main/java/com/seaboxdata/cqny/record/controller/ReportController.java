package com.seaboxdata.cqny.record.controller;


import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @RequestMapping("loadReport")
    @ResponseBody
    public JsonResult loadReport(String reportIdOrName){
        List<List<ReportCell>> reportInfo = reportService.loadReport(reportIdOrName);
        JsonResult response = JsonSupport.makeJsonpResult(
                JsonResult.RESULT.SUCCESS, "获取成功", null, reportInfo);
        return response;
    }

}
