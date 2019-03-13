package com.seaboxdata.cqny.record.controller;


import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.entity.TemplateImport;
import com.seaboxdata.cqny.record.service.ExcelFileOptionsService;
import com.seaboxdata.cqny.record.service.TemplateService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    @RequestMapping("templteDelete")
    @CrossOrigin(allowCredentials="true")
    @ResponseBody
    public JsonResult templteDelete(String templateId){
        templateService.templteDelete(templateId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null, null);
        return jsonResult;
    }

    @RequestMapping("getTemplatesByUser")
    @CrossOrigin(allowCredentials="true")
    @ResponseBody
    public JsonResult getTemplatesByUser(){
        User user = SessionSupport.checkoutUserFromSession();
        List<ExcelTemplate> templateList = templateService.getTemplatesByUser(user.getUser_id());
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, templateList);
        return jsonResult;
    }


    @RequestMapping("getTemplateContext")
    @CrossOrigin(allowCredentials="true")
    @ResponseBody
    public JsonResult getTemplateContext(String templateId){
        ExcelTemplate templateInfo = templateService.loadTemplate(templateId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, templateInfo);
        return jsonResult;
    }

    @RequestMapping("uploadTemplate")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult uploadTemplate(TemplateImport templateImport) throws IOException {

        File templateFilePath = excelFileOptionsService.loadTemplateFileFromDisk(templateImport.getTemplateFile().getOriginalFilename());

        if(!templateFilePath.getParentFile().exists()){
            templateFilePath.getParentFile().mkdirs();
        }
        templateFilePath.createNewFile();

        templateImport.getTemplateFile().transferTo(templateFilePath);

        String createResult = templateService.uploadTemplate(templateImport.getTemplateName(), templateImport.getOriginId(),templateFilePath);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "創建成功", null, createResult);
//        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "創建成功", null, "1");

        return jsonResult;

    }

    @RequestMapping("editSaveTemplate")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult editSaveTemplate(@RequestBody TemplateInfo templateInfo){
        templateService.editSaveTemplate(templateInfo.getTemplateId(),templateInfo.getSheetId(),templateInfo.getTemplateContext(),templateInfo.getCopyGroups());
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null, null);
//        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "創建成功", null, "1");

        return jsonResult;

    }

    class TemplateInfo {
        private String templateId;
        private String sheetId;
        private ArrayList<ArrayList<String>> templateContext;
        private ArrayList<HashMap<String,Object>> copyGroups;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getSheetId() {
            return sheetId;
        }

        public void setSheetId(String sheetId) {
            this.sheetId = sheetId;
        }

        public ArrayList<ArrayList<String>> getTemplateContext() {
            return templateContext;
        }

        public void setTemplateContext(ArrayList<ArrayList<String>> templateContext) {
            this.templateContext = templateContext;
        }

        public ArrayList<HashMap<String, Object>> getCopyGroups() {
            return copyGroups;
        }

        public void setCopyGroups(ArrayList<HashMap<String, Object>> copyGroups) {
            this.copyGroups = copyGroups;
        }
    }
}
