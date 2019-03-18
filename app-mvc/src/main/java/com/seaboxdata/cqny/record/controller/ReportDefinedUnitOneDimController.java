package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("unitOneDimColum")
public class ReportDefinedUnitOneDimController {

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @RequestMapping("pagerOnedimList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerOnedimList(Integer currPage,Integer pageSize,Integer unitId   ){
        PageResult definedpageLIst = reportDefinedUnitOneDimService.pagerOnedimList(unitId,currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("addSaveOnedim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addSaveOnedim(@RequestBody ColumDefined columDefined){
        reportDefinedUnitOneDimService.addSaveOnedim(columDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

}
