package com.seaboxdata.cqny.datamove.controller;

import com.seaboxdata.cqny.datamove.ReportDataMoveService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Desc: b01-b15报表数据迁移
 * @Author: z.h.c
 * @Date: 2019/8/8 13:49
 * @Version: 1.0
 */

@Controller
@RequestMapping("/move")
public class DataMoveController {

    @Autowired
    private ReportDataMoveService reportDataMoveService;

    @RequestMapping("/dataMove")
    @ResponseBody
    public String listReportApproval() {
        reportDataMoveService.doMove();
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "data move success", null, null);
        return jsonpResponse;
    }
}
