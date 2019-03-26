package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
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
    public JsonResult addSaveOnedim(@RequestBody SimpleColumDefined simpleColumDefined){
        reportDefinedUnitOneDimService.addSaveOnedim(simpleColumDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("editSaveOnedim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveOnedim(@RequestBody SimpleColumDefined simpleColumDefined){
        reportDefinedUnitOneDimService.editSaveOnedim(simpleColumDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("getOnedimColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getOnedimColumn(String columId){
        SimpleColumDefined simpleColumDefined = reportDefinedUnitOneDimService.getOnedimColumn(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null, simpleColumDefined);
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
        List<SimpleColumDefined> colums = reportDefinedUnitOneDimService.getColumByUnit(unitId);
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
    public JsonResult editSaveOnedimDynamic(@RequestBody Map<String,List<SimpleColumDefined>> maps){
        SimpleColumDefined group = null;
        if(maps.containsKey("group")){
            List<SimpleColumDefined> list = maps.get("group");
            if(list.size()>0)
                group = list.get(0);
        }
        if(group == null) {
            JsonResult err = new JsonResult();
            err.setFaild_reason("格式不正确");
            return err;
        }
        reportDefinedUnitOneDimService.editSaveOnedimDynamic(group, maps);
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

    @RequestMapping("pagerMultdimListStatic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerMultdimListStatic(Integer currPage,Integer pageSize, Integer unitId, String group_id){
        PageResult definedpageLIst = reportDefinedUnitOneDimService.pagerMultdimListStatic(currPage,pageSize, unitId, group_id);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }
}
