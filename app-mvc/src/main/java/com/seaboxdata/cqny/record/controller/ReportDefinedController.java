package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("reportDefined")
public class ReportDefinedController {

    @Autowired
    private ReportDefinedService reportDefinedService;

    @RequestMapping("reportDefinedList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult reportDefinedList(Integer currPage,Integer pageSize   ){
        PageResult definedpageLIst = reportDefinedService.reportDefinedList(currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

}
