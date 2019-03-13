package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.dao.IOriginDao;
import com.seaboxdata.cqny.record.dao.ITemplateDao;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.service.ExcelFileOptionsService;
import com.seaboxdata.cqny.record.service.TemplateService;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("template")
public class TemplateServiceImp implements TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateServiceImp.class);

    @Autowired
    private RecordConfig recordConfig;

    @Autowired
    private ITemplateDao templateDao;

    @Autowired
    private IOriginDao originDao;

    @Autowired
    private ExcelFileOptionsService excelFileOptionsService;

    @Override
    public ExcelTemplate loadTemplate(String templateIdOrName) {

        ExcelTemplate templateInfo = templateDao.getExcelTemplate(templateIdOrName);

        return templateInfo;
    }

    @Override
    public List<ExcelTemplate> loadTemplateBasicInfo(String templateId) {
        return templateDao.getExcelTemplateBasic(templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadTemplate(String tempalteName,String originId, File uploadFile) throws IOException {
        try {
            String userId = String.valueOf(((User) SessionSupport.checkoutUserFromSession()).getUser_id());
//            String userId = "111111";

            if(!uploadFile.exists()){
                new FileNotFoundException("模板未找到");
            }

            String templateFileName = uploadFile.getName();
            logger.debug("file name is {}",templateFileName);
            String[] templateFileNameSplit = templateFileName.split("\\.");
            String fileType = templateFileNameSplit[templateFileNameSplit.length-1];
            String fileName = makeFileName(userId,fileType);
            File reportFile = findReportPath(fileName);
            if(!reportFile.exists()){
                if(!reportFile.getParentFile().exists()){
                    reportFile.getParentFile().mkdirs();
                }
                reportFile.createNewFile();
            }
//            Files.copy(template,reportFile);

            ExcelTemplate excelTemplate = new ExcelTemplate();
            excelTemplate.setTemplate_name(tempalteName);
            excelTemplate.setSource_file_name(uploadFile.getName());
            excelTemplate.setTemplate_source_file(uploadFile.getPath());
            excelTemplate.setImport_date(new Date());
            excelTemplate.setImport_user(userId);

            logger.debug("excel template ======> {}",excelTemplate);

            templateDao.saveTemplate(excelTemplate);
            
            List<ExcelContext> loadResult = excelFileOptionsService.loadFileByFilePath(uploadFile.getPath());
            if(loadResult!=null){
               for(int i=0;i<loadResult.size();i++){
                   ExcelContext excelContext = loadResult.get(i);

                   ExcelTemplateSheet excelTemplateSheet = new ExcelTemplateSheet();
                   excelTemplateSheet.setSheet_num(i+1);
                   excelTemplateSheet.setTemplate_id(excelTemplate.getTemplate_id());
                   excelTemplateSheet.setRow_num(excelContext.getSheetRows());
                   excelTemplateSheet.setColum_num(excelContext.getSheetColums());
                   excelTemplateSheet.setSheet_name(excelContext.getSheetName());
                   templateDao.saveTemplateSheet(excelTemplateSheet);

                   logger.debug("excelTemplateSheet ======> {}",excelTemplateSheet);

                   List<List<String>> reportRows = excelContext.getReportRows();
                   List<ExcelTemplateCellMerged> mergedList = excelContext.getMergedList();

                   int rowNum = 0;
                   int maxColNum = 0;
                   while(reportRows.size()>0){
                       List<String> reportRow = reportRows.remove(0);
                       int columNum = 0;
                       while(reportRow.size()>0){
                           ExcelTemplateCell excelTemplateCell = new ExcelTemplateCell();
                           String reportCell = reportRow.remove(0);
                           excelTemplateCell.setTemplate_row(rowNum);
                           excelTemplateCell.setTemplate_col(columNum);
                           excelTemplateCell.setTemplate_context(reportCell);
                           excelTemplateCell.setSheet_id(excelTemplateSheet.getId());
                           if(excelContext.getFormulas().containsKey(rowNum+"-"+columNum)){
                               excelTemplateCell.setContext_script(excelContext.getFormulas().get(rowNum+"-"+columNum));
                           }
                           excelTemplateCell.setContext_readonly("Y");
                           templateDao.saveTemplateCell(excelTemplateCell);
                           logger.debug("excelTemplateCell ======> {}",excelTemplateCell);

                           columNum++;
                       }
                       if(maxColNum<columNum)
                           maxColNum = columNum;
                       rowNum++;
                   }


                   while(mergedList.size()>0){
                       ExcelTemplateCellMerged mergedData = mergedList.remove(0);
                       mergedData.setSheet_id(excelTemplateSheet.getId());
                       templateDao.saveExcelTemplateCellMerged(mergedData);
                       logger.debug("mergedData ======> {}",mergedData);
                   }
                   
               }
            }

            Map<String, Object> originData = originDao.getOriginById(new Integer(originId));
            saveOriginTemplate(originData,excelTemplate.getTemplate_id());

            return excelTemplate.getTemplate_id().toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveOriginTemplate(Map<String, Object> originData ,Integer templateId){
        if(originData!=null){
            Integer originIdTmp = (Integer) originData.get("origin_id");
            originDao.saveOriginTemplate( originIdTmp, templateId);
            List<Map<String,Object>> children = (List<Map<String, Object>>) originData.get("childrens");
            if(children!=null&&children.size()>0){
                children.forEach(child->{
                    saveOriginTemplate(child,templateId);
                });
            }
        }
    }

    @Override
    public PageResult pageTempaltes(int currPage, int pageSize) {
        Page<ExcelTemplate> pageData = templateDao.pagerTemplates(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public List<ExcelTemplate> getTemplatesByUser(int user_id) {
        List<ExcelTemplate> templates = templateDao.getTemplatesByUser(user_id);
        return templates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSaveTemplate(String templateId, String sheetId, ArrayList<ArrayList<String>> templateContext, ArrayList<HashMap<String,Object>> copyGroups) {
//        templateDao.removeTemplateContext(templateId,sheetId);
//        templateDao.removeTemplateMerged(templateId,sheetId);
        templateDao.removeCopyGroups(templateId,sheetId);
        if(copyGroups!=null&&copyGroups.size()>0){
            copyGroups.forEach(copyGroup->{
                String copyGroupName = (String) copyGroup.get("name");
                List copyGroupRows = (List) copyGroup.get("rows");
                templateDao.saveCopyGroups(templateId,sheetId,copyGroupName,copyGroupRows.toString());
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void templteDelete(String templateId) {
        templateDao.removeTemlateMerged(templateId);
        templateDao.removeTemplateContext(templateId);
        templateDao.removeTemplateCopyGroup(templateId);
        templateDao.removeTemplateSheet(templateId);
        templateDao.removeTemplateInfo(templateId);
    }

    private String makeFileName(String userId,String templateFileType){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuilder sb = new StringBuilder();
        sb.append(format1.format(new Date()));
        sb.append("_");
        sb.append(userId);
        sb.append(".");
        sb.append(templateFileType);
        return sb.toString();
    }

    private File findReportPath(String reportFileName){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        StringBuilder sb = new StringBuilder();
        sb.append(recordConfig.getReportPath());
        sb.append("/");
        sb.append(format.format(new Date()));
        sb.append("/");
        sb.append(reportFileName);
        File reportFilePath = new File(sb.toString());
        return reportFilePath;
    }



}
