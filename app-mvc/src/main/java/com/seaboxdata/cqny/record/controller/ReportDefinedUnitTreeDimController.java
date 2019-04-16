package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("unitTreeDimColum")
public class ReportDefinedUnitTreeDimController {

//    @Autowired
//    private ReportDefinedUnitTreeDimService reportDefinedUnitTreeDimService;

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitSimpleDimService;

    @RequestMapping("pagerTreedimList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult pagerTreedimList(Integer currPage,Integer pageSize,Integer unitId   ){
        PageResult definedpageLIst = reportDefinedUnitSimpleDimService.pagerOnedimList(unitId,currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,definedpageLIst);
        return jsonResult;
    }

    @RequestMapping("addSaveTreedim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addSaveTreedim(@RequestBody SimpleColumDefined simpleColumDefined){
        Integer groupId = simpleColumDefined.getGroup_id();
        if(groupId!=null&&groupId>0){

        }else{
            SimpleDateFormat format = new SimpleDateFormat("HHmmdd");
            String nowDate = format.format(new Date());
            simpleColumDefined.setGroup_id(new Integer(nowDate));
        }
        reportDefinedUnitSimpleDimService.addSaveOnedim(simpleColumDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("editSaveTreedim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveOnedim(@RequestBody SimpleColumDefined simpleColumDefined){
        reportDefinedUnitSimpleDimService.editSaveOnedim(simpleColumDefined);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }

    @RequestMapping("getTreedimColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getOnedimColumn(String columId){
        SimpleColumDefined simpleColumDefined = reportDefinedUnitSimpleDimService.getOnedimColumn(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null, simpleColumDefined);
        return jsonResult;
    }

    @RequestMapping("deleteTreeDim")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteOneDim( String columId){
        reportDefinedUnitSimpleDimService.deleteOneDim(columId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

    @RequestMapping("getUnits")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getUnits(String originId){
        List<UnitDefined> unitList = reportDefinedUnitSimpleDimService.getUnitsByOrigin(originId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,unitList);
        return jsonResult;
    }

    @RequestMapping("getInputColumn")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getInputColumn(String unitId){
        List<GridColumDefined> colums = reportDefinedUnitSimpleDimService.getColumByUnit(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,colums);
        return jsonResult;
    }

    @RequestMapping("getGroup")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getGroup(String unitId){
        List<Map> groupList = reportDefinedUnitSimpleDimService.getGroupByUnit(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null,groupList);
        return jsonResult;
    }

}
