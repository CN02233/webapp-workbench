package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("reportUnitDefined")
public class ReportUnitDefinedController {

    @Autowired
    private ReportUnitService reportDefinedUnitService;

    @RequestMapping("reportUnitDefinedList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult reportUnitDefinedList(Integer currPage,Integer pageSize ,Integer reportDefinedId  ){
//        PageResult definedUnitpageLIst = reportDefinedUnitService.listReportUnit(currPage,pageSize,reportDefinedId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,null);
        return jsonResult;
    }

}
