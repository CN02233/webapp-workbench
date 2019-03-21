package com.seaboxdata.cqny.record.controller;

import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

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

    @RequestMapping("formalOperation")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult formalOperation(@RequestBody String format,@RequestBody HashMap<String,Object> operaionValus){
        JsonResult jsonResult = null;
        if(!Strings.isNullOrEmpty(format)){
            if(operaionValus!=null&&operaionValus.size()>0){
                Expression expression= AviatorEvaluator.compile(format);
                Object result = expression.execute(operaionValus);
                jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功 ", null,result);

            }else{
                jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "参数为空 ", null,null);
            }
        }else{
            jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "公式为空 ", null,null);
        }

        return jsonResult;
    }

}
