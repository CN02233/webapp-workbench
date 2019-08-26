package com.seaboxdata.cqny.record.controller;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.entity.ReportSmsConfig;
import com.seaboxdata.cqny.record.service.ReportSmsService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sms")
public class ReportSmsController {

    @Autowired
    private ReportSmsService reportSmsService;

    @RequestMapping("pagerSms")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerSms(Integer currPage, Integer pageSize){

        PageResult pagerData = reportSmsService.pagerSms(currPage,pageSize);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,pagerData);
        return jsonResult;
    }

    @RequestMapping("getSmsTemplates")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getSmsTemplates(){
        List<Map<String,Object>> aliSmsTemplateList = reportSmsService.getAliSmsTemplates();
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,aliSmsTemplateList);
        return jsonResult;
    }


    @RequestMapping("createSmsJob")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult createSmsJob(@RequestBody ReportSmsConfig reportSMsConfig ){
        String faildReson = reportSmsService.createSmsJob(reportSMsConfig);
        if(Strings.isNullOrEmpty(faildReson)){
            JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,JsonResult.RESULT.SUCCESS);
            return jsonResult;
        }else{
            JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, faildReson, faildReson,faildReson);
            return jsonResult;
        }

    }


    @RequestMapping("deleteSmsJob")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult createSmsJob(String smsId){
        reportSmsService.deleteSmsJob(smsId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,JsonResult.RESULT.SUCCESS);
        return jsonResult;

    }
}
