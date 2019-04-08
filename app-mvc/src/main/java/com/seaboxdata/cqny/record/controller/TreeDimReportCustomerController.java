package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.FomularTmpEntity;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.treedim.TreeUnitContext;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.TreeDimReportCustomerService;
import com.seaboxdata.cqny.record.service.imp.ReportCustomerServiceImp;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("treeReportCust")
public class TreeDimReportCustomerController {

    @Autowired
    private TreeDimReportCustomerService treeDimReportCustomerService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    @RequestMapping("saveTreeData")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult saveTreeData(@RequestBody ArrayList<TreeUnitContext> treeUnitContexts){
        Map<String,Map<String,Object>> treeUnitContextMap = new HashMap<>();

        treeDimReportCustomerService.saveTreeData(treeUnitContexts);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,null);

        return jsonResult;
    }

    @RequestMapping("validateTreeData")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult validateTreeData(@RequestBody ArrayList<TreeUnitContext> treeUnitContexts) {
        Map<String,Object> validateResult = new HashMap<>();

        if(treeUnitContexts!=null){
            for (TreeUnitContext treeUnitContext : treeUnitContexts) {
                ArrayList<ReportCustomerData> columDatas = treeUnitContext.getColumDatas();
                ArrayList<SimpleColumDefined> definedColums = treeUnitContext.getDefinedColums();
                Map<String, String> validateResultTmp = reportCustomerService.validateSimpleUnitByDimensions(definedColums, columDatas);
                if(validateResultTmp!=null)
                    validateResult.putAll(validateResultTmp);
            }
        }

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "校验完成", null,validateResult);

        return jsonResult;
    }

}
