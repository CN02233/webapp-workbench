package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("editSaveOnedim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveOnedim(@RequestBody ColumDefined columDefined){
        reportDefinedUnitOneDimService.editSaveOnedim(columDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("getOnedimColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getOnedimColumn(String columId){
        ColumDefined columDefined = reportDefinedUnitOneDimService.getOnedimColumn(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,columDefined);
        return jsonResult;
    }

    @RequestMapping("deleteOneDim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteOneDim( String columId){
        reportDefinedUnitOneDimService.deleteOneDim(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

    @RequestMapping("getUnits")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getUnits(String originId){
        List<UnitDefined> unitList = reportDefinedUnitOneDimService.getUnitsByOrigin(originId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,unitList);
        return jsonResult;
    }

    @RequestMapping("getInputColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getInputColumn(String unitId){
        List<ColumDefined> colums = reportDefinedUnitOneDimService.getColumByUnit(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,colums);
        return jsonResult;
    }

    @RequestMapping("getGroup")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getGroup(String unitId){
        List<Map> groupList = reportDefinedUnitOneDimService.getGroupByUnit(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,groupList);
        return jsonResult;
    }

    @RequestMapping("pagerOnedimListDynamic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerOnedimListDynamic(Integer currPage,Integer pageSize, Integer unitId, String group_id){
        PageResult definedpageLIst = reportDefinedUnitOneDimService.pagerOnedimListDynamic(currPage,pageSize, unitId, group_id);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("editSaveOnedimDynamic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveOnedimDynamic(@RequestBody Map<String,List<ColumDefined>> maps){

        reportDefinedUnitOneDimService.editSaveOnedimBat(maps);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("deleteOneDimDynamic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteOneDimDynamic(Integer unitId, String group_id){
        reportDefinedUnitOneDimService.deleteOneDimDynamic(unitId, group_id);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }
}
