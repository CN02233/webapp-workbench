package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.dao.IReportDao;
import com.seaboxdata.cqny.record.entity.ExcelTemplateCell;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.entity.ReportStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("report")
public class ReportServiceImp implements ReportService {

    private Logger logger = LoggerFactory.getLogger(ReportServiceImp.class);

    private boolean isMock = false;

    @Autowired
    private RecordConfig recordConfig;

    @Autowired
    private IReportDao iReportDao;

    @Autowired
    private ExcelFileOptionsService excelFileOptionsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createReport(String templateId,String reportName) throws IOException {
        String userId = isMock?"testUser":
                String.valueOf(((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setReportName(reportName);
        reportInfo.setReportCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        reportInfo.setReportCreate(userId);
        reportInfo.setReportTemplateId(templateId);


        iReportDao.createReport(reportInfo);
        iReportDao.copyTemplateContext(reportInfo.getReportId(),new Integer(templateId));
        iReportDao.copyTemplateMerged(new Integer(templateId));

        return reportInfo.getReportId().toString();
    }


    @Override
    public ReportInfo loadReport(String reportIdOrName) {

        ReportInfo reportInfo = iReportDao.getReportInfoById(reportIdOrName);

//        List<List<List<ReportCell>>> templateContent = excelFileOptionsService.loadReport(reportInfo.getReportPath());

        return reportInfo;
    }

    @Override
    public ReportInfo loadReportData(String reportID) {
        ReportInfo reportInfo = iReportDao.getReportInfoById(reportID);

//        List<ExcelContext> templateContent = excelFileOptionsService.loadFileByFilePath(reportInfo.getReportPath());

        return reportInfo;
    }

    @Override
    public PageResult reportList(int userId, int currPage, int pageSize) {

        Page<ReportInfo> reportList = iReportDao.reportList(currPage, pageSize,userId);

        PageResult pageResult = PageResult.pageHelperList2PageResult(reportList);

        return pageResult;
    }

    @Override
    @Transactional
    public void editSave(ArrayList<ArrayList<String>> reportCells, String reportId,String sheetId) {

        if(reportCells!=null&&reportCells.size()>0){
            for(int rowNum=0;rowNum<reportCells.size();rowNum++){
                ArrayList<String> rowData = reportCells.get(rowNum);
                if(rowData!=null&&reportCells.size()>0){
                    for(int cellNum = 0;cellNum<rowData.size();cellNum++){
                        String cellVal = rowData.get(cellNum);
                        iReportDao.updateReport(reportId,sheetId,rowNum,cellNum, Strings.isNullOrEmpty(cellVal)?"":cellVal);
                    }
                }
            }
        }

        return ;
    }


    public void lockReport(String reportId,Integer userId){
        logger.debug("lock report :{},{}",reportId,ReportStatus.LOCK);
        iReportDao.changeReportStatus(ReportStatus.LOCK.toString(),reportId,userId);
    }

    @Override
    public ReportInfo loadReportBasic(String reportId) {
        return iReportDao.getBasicReportInfo(reportId);
    }

    @Override
    public void submitReport(String reportId, int userId) {
        iReportDao.changeReportStatus(ReportStatus.SUBMIT.toString(),reportId,null);
    }

    @Override
    public void reviewReport(String reportId, int userId) {
        iReportDao.changeReportStatus(ReportStatus.REVIEW.toString(),reportId,null);
    }

    @Override
    public void confirmReport(String reportId, int userId) {
        iReportDao.changeReportStatus(ReportStatus.APPROVE.toString(),reportId,null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fullEditSave(ArrayList<ArrayList<String>> reportCells,
                             ArrayList<Map<String, String>> reportMerged,
                             String reportId,
                             String templateId,
                             String sheetId) {
        iReportDao.removeReportContext(reportId,sheetId);
        iReportDao.removeReportMerged(reportId,sheetId);
        if(reportCells!=null){
            for(int reportRow = 0;reportRow<reportCells.size();reportRow++){
                if(reportCells.get(reportRow)!=null){
                    for(int reportColum = 0;reportColum<reportCells.get(reportRow).size();reportColum++) {
                        String context = reportCells.get(reportRow).get(reportColum);
                        //(#{report_id},#{template_id},#{template_sheet_id},#{report_row},#{report_colum},#{report_context})
                        Map<String,Object> paramMap = new HashMap<>();
                        paramMap.put("report_id",reportId);
                        paramMap.put("template_id",templateId);
                        paramMap.put("template_sheet_id",sheetId);
                        paramMap.put("report_row",reportRow);
                        paramMap.put("report_colum",reportColum);
                        paramMap.put("report_context",context);
                        iReportDao.insertReportContext(paramMap);

                    }
                }
            }
            reportMerged.forEach(reportMergedCell->{
                reportMergedCell.put("sheet_id",sheetId);
                iReportDao.insertReportMerged(reportMergedCell);
            });
        }
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
