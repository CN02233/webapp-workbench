package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitMultDimService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("unitMultDimColum")
public class ReportDefinedUnitMultDimController {

    @Autowired
    private ReportDefinedUnitMultDimService reportDefinedUnitMultDimService;

    @RequestMapping("pagerMultdimList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerMultdimList(Integer currPage,Integer pageSize,Integer unitId ,Integer columId ){
        PageResult definedpageLIst = reportDefinedUnitMultDimService.pagerMultdimList(unitId,columId, currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("pagerDimList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerDimList(Integer currPage,Integer pageSize,Integer unitId){
        PageResult definedpageLIst = reportDefinedUnitMultDimService.pagerDimList(unitId, currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("getOnedimColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getOnedimColumn(String columId){
        GridColumDefined simpleColumDefined = reportDefinedUnitMultDimService.getOnedimColumn(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null, simpleColumDefined);
        return jsonResult;
    }


    @RequestMapping("getUnits")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getUnits(String originId){
        List<UnitDefined> unitList = reportDefinedUnitMultDimService.getUnitsByOrigin(originId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,unitList);
        return jsonResult;
    }

    @RequestMapping("getInputColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getInputColumn(String unitId, String unitType){
        List<GridColumDefined> colums = reportDefinedUnitMultDimService.getMultColumByUnit(unitId, unitType);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,colums);
        return jsonResult;
    }

    @RequestMapping("getGroup")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getGroup(String unitId){
        List<Map> groupList = reportDefinedUnitMultDimService.getGroupByUnit(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,groupList);
        return jsonResult;
    }

    @RequestMapping("pagerOnedimListDynamic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerOnedimListDynamic(Integer currPage ,Integer pageSize, Integer unitId, @RequestParam Map<String,Object> paramMap){
        PageResult definedpageLIst = reportDefinedUnitMultDimService.pagerOnedimListDynamic(currPage,pageSize, unitId, paramMap);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("editSaveMultdim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveMultdim(@RequestBody Map<String, List<GridColumDefined>> maps){
        reportDefinedUnitMultDimService.editSaveMultdim(maps);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("editSaveMultdim_dim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveMultdim_dim(@RequestBody GridColumDefined maps){
        reportDefinedUnitMultDimService.editSaveMultdim_dim(maps);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("addSaveMultdim_dim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addSaveMultdim_dim(@RequestBody GridColumDefined maps){
        reportDefinedUnitMultDimService.addSaveMultdim_dim(maps);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("deleteSaveMultdim_dim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteSaveMultdim_dim(@RequestBody GridColumDefined maps){
        JsonResult jsonResult = reportDefinedUnitMultDimService.deleteSaveMultdim_dim(maps);
        return jsonResult;
    }

    @RequestMapping("deleteMultdim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteMultdim(Integer unitId, Integer columId){
        reportDefinedUnitMultDimService.deleteMultdim(unitId, columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

    @RequestMapping("pagerMultdimListStatic")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerMultdimListStatic(Integer currPage,Integer pageSize, Integer unitId, @RequestParam Map<String,Object> paramMap){
        PageResult definedpageLIst = reportDefinedUnitMultDimService.pagerMultdimListStatic(currPage,pageSize, unitId, paramMap);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }
}
