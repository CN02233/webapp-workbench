package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.io.Files;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.dao.IReportDao;
import com.seaboxdata.cqny.record.entity.ExcelContext;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.service.ExcelFileOptionsService;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("report")
public class ReportServiceImp implements ReportService {

    private Logger logger = LoggerFactory.getLogger(ReportServiceImp.class);

    private boolean isMock = true;

    @Autowired
    private RecordConfig recordConfig;

    @Autowired
    private IReportDao iReportDao;

    @Autowired
    private ExcelFileOptionsService excelFileOptionsService;

    @Override
    public String createReport(String templateIdOrName,String reportName) throws IOException {
        String userId = isMock?"testUser":
                String.valueOf(((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        File template = excelFileOptionsService.loadTemplateFileFromDisk(templateIdOrName);
        try {
            if(!template.exists()){
                new FileNotFoundException("模板未找到");
            }
            String templateFileName = template.getName();
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
            Files.copy(template,reportFile);

            ReportInfo reportInfo = new ReportInfo();
            reportInfo.setReportName(reportName);
            reportInfo.setReportPath(reportFile.getPath());
            reportInfo.setReportCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            reportInfo.setReportCreate(userId);
            reportInfo.setReportTemplateName(template.getName());

            int reportId = iReportDao.createReport(reportInfo);

            return reportInfo.getReportId().toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public List<List<List<ReportCell>>> loadReport(String reportIdOrName) {

        ReportInfo reportInfo = iReportDao.getReportInfoById(reportIdOrName);

        List<List<List<ReportCell>>> templateContent = excelFileOptionsService.loadReport(reportInfo.getReportPath());

        return templateContent;
    }

    @Override
    public List<ExcelContext> loadReportData(String reportID) {
        ReportInfo reportInfo = iReportDao.getReportInfoById(reportID);

        List<ExcelContext> templateContent = excelFileOptionsService.loadFileByFilePath(reportInfo.getReportPath());

        return templateContent;
    }

    @Override
    public PageResult reportList(int userId, int currPage, int pageSize) {

        Page<ReportInfo> reportList = iReportDao.reportList(currPage, pageSize,userId);

        PageResult pageResult = PageResult.pageHelperList2PageResult(reportList);

        return pageResult;
    }

    @Override
    public List<List<List<ReportCell>>> editSave(ArrayList<ReportCell> reportCells, String reportId) {
        ReportInfo reportInfo = iReportDao.getReportInfoById(reportId);

        List<List<List<ReportCell>>> templateContent = excelFileOptionsService.loadReport(reportInfo.getReportPath());

        return templateContent;
    }





    public void writeFile(ArrayList<ReportCell> dataList , File reportFile){
        try (InputStream inp = new FileInputStream(reportFile)) {
            logger.debug("开始写入报表:{}",reportFile.getPath());

            Workbook wb = WorkbookFactory.create(inp);
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();

                dataList.forEach(reportCell->{
                    int rowNum = reportCell.getRow();
                    int columnNum = reportCell.getColumn();
                    String cellVal = reportCell.getVal();
                    Cell cell = sheet.getRow(rowNum).getCell(columnNum);
                    if(cell==null){
                        cell = sheet.getRow(rowNum).createCell(columnNum);
                    }
                    logger.debug("写入报表第{}行,第{}列,数据内容:{},表格原值：{},表格类型:{}",rowNum,columnNum,cellVal,cell.toString(),cell.getCellTypeEnum());
                    switch (cell.getCellTypeEnum()){
                        case STRING:
                            cell.setCellValue(cellVal);
                            break;
                        case NUMERIC:
                            try{
                                cell.setCellValue(new Integer(cellVal));
                            }catch (NumberFormatException e ){
                                try{
                                    cell.setCellValue(new Double(cellVal));
                                }catch (NumberFormatException e1){
                                    try{
                                        cell.setCellValue(new Long(cellVal));
                                    }catch (NumberFormatException e2){
                                        e.printStackTrace();
                                    }
                                }
                            }

                            break;
                        case BLANK:
                            cell.setCellValue(cellVal);
                            break;
                    }
                });
            }
            inp.close();
            try (FileOutputStream fileOut = new FileOutputStream(reportFile)){
                wb.write(fileOut);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }


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

    public Set checkAllMergedCell(Sheet sheet){
        Set mergedSets = new HashSet();

        List<CellRangeAddress> allMergeRegions = sheet.getMergedRegions();
        allMergeRegions.forEach(mergeRegion->{
            logger.debug("合并单元格信息:起始行号{}截止行号{},起始列号{}截止列号{}",
                    mergeRegion.getFirstRow(),mergeRegion.getLastRow(),mergeRegion.getFirstColumn(),mergeRegion.getLastColumn());
            int mergedRows = mergeRegion.getLastRow()-mergeRegion.getFirstRow();
            int mergedColumns = mergeRegion.getLastColumn()-mergeRegion.getFirstColumn();
            for(int mergeRowOffset=0;mergeRowOffset<=mergedRows;mergeRowOffset++){
                int mergedRowNum = mergeRegion.getFirstRow() + mergeRowOffset;
                for(int mergeColumnOffset=0;mergeColumnOffset<=mergedColumns;mergeColumnOffset++) {
                    int mergedColumnNum = mergeRegion.getFirstColumn() + mergeColumnOffset;
                    mergedSets.add(new StringBuilder().append(mergedRowNum).append("-").append(mergedColumnNum).toString());
                }
            }
        });

        logger.debug("{}",mergedSets);

        return mergedSets;
    }


    private String checkCellVal(Cell cell) throws IllegalStateException{
        StringBuilder cellStrVal = new StringBuilder();
        switch (cell.getCellTypeEnum()){
            case STRING:
                cellStrVal.append(cell.getStringCellValue());
                break;
            case NUMERIC:
                cellStrVal.append(cell.getNumericCellValue());
                break;
            case BLANK:
                cellStrVal.append("");
                break;
            case FORMULA:
                try{
                    cellStrVal.append(cell.getNumericCellValue());
                }catch (IllegalStateException e){
                    try{
                        cellStrVal.append(cell.getStringCellValue());
                    }catch (IllegalStateException e1){
                        cellStrVal.append("公式计算失败,公式:"+cell.getCellFormula());
                    }
                }
                break;
        }


        return cellStrVal.toString();
    }

}
