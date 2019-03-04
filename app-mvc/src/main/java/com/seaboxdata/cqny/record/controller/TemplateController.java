package com.seaboxdata.cqny.record.controller;


import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.entity.TemplateImport;
import com.seaboxdata.cqny.record.service.ExcelFileOptionsService;
import com.seaboxdata.cqny.record.service.TemplateService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ExcelFileOptionsService excelFileOptionsService;

    @RequestMapping("listTemplates")
    @CrossOrigin(allowCredentials="true")
    @ResponseBody
    public JsonResult listTemplates(int currPage,int pageSize){
        PageResult templateList = templateService.pageTempaltes(currPage,pageSize);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, templateList);
        return jsonResult;
    }

    @RequestMapping("uploadTemplate")
    @CrossOrigin(allowCredentials="true")
    @ResponseBody
    public JsonResult uploadTemplate(TemplateImport templateImport) throws IOException {

        String tempalteName = templateImport.getTempalteName();
        MultipartFile templateMuliFile = templateImport.getTemplateFile();

        File templateFilePath = excelFileOptionsService.loadTemplateFileFromDisk("");

        if(!templateFilePath.getParentFile().exists()){
            templateFilePath.getParentFile().mkdirs();
        }
        templateFilePath.createNewFile();

//        String createResult = templateService.uploadTemplate(tempalteName, templateFilePath);
//        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "創建成功", null, createResult);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "創建成功", null, "1");

        return jsonResult;

    }

}
