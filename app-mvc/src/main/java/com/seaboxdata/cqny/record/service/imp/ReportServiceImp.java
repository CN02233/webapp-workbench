package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.io.Files;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.dao.IReportDao;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.entity.ReportInfo;
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

    @Override
    public String createReport(String templateIdOrName,String reportName) throws IOException {
        String userId = isMock?"testUser":
                String.valueOf(((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        File template = loadTemplateFileFromDisk(templateIdOrName);
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

        File reportFile = new File(reportInfo.getReportPath());

        List<List<List<ReportCell>>> templateContent = this.readFile(reportFile);

        return templateContent;
    }

    @Override
    public PageResult reportList(int userId, int currPage, int pageSize) {

        Page<ReportInfo> reportList = iReportDao.reportList(currPage, pageSize,userId);

        PageResult pageResult = PageResult.pageHelperList2PageResult(reportList);

        return pageResult;
    }

    @Override
    public List<List<List<ReportCell>>> editSave(ArrayList<ArrayList<ReportCell>> reportCells, String reportId) {
        ReportInfo reportInfo = iReportDao.getReportInfoById(reportId);

        File reportFile = new File(reportInfo.getReportPath());

        this.writeFile(reportCells,reportFile);

        List<List<List<ReportCell>>> templateContent = this.readFile(reportFile);

        return templateContent;
    }

    /**
     * 读取excel文件内容
     * {
     *     row:1,//行号
     *     column:1,//列号
     *     val:123455,//值
     *     merged:true,//是否为合并单元格
     *     input:true,//是否为输入项
     * }
     * @param templateFile
     * @return
     */
    private List<List<List<ReportCell>>> readFile(File templateFile){
        try (InputStream inp = new FileInputStream(templateFile)) {
            List<List<List<ReportCell>>> reportSheets = new ArrayList();

            Workbook wb = WorkbookFactory.create(inp);
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            while(sheetIterator.hasNext()){
                Sheet sheet = sheetIterator.next();
                List<List<ReportCell>> reportRows = new ArrayList();
                for (int currRowNum = 0; currRowNum<sheet.getLastRowNum(); currRowNum++) {
                    Row row = sheet.getRow(currRowNum);
                    List<ReportCell> reportCells = new ArrayList<>();
//                    logger.debug("当前行号:{}，起始列{}，结束列{}",row.getRowNum(),row.getFirstCellNum(),row.getLastCellNum());
                    for (int columnNum = 0;columnNum<row.getLastCellNum();columnNum++) {
                        Cell cell = row.getCell(columnNum);
                        ReportCell reportCell = new ReportCell();
                        reportCells.add(reportCell);

                        reportCell.setRow(currRowNum);
                        reportCell.setColumn(columnNum);

                        if(cell==null){
                            reportCell.setVal("");
                            continue;
                        }
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
                                reportCell.setInput(true);
                                break;
                            case FORMULA:
                                try{
                                    cellStrVal.append(cell.getNumericCellValue());
                                }catch (IllegalStateException e){
                                    try{
                                        cellStrVal.append(cell.getStringCellValue());
                                    }catch (IllegalStateException e1){
                                        cellStrVal.append("公式计算失败,公式:"+cell.getCellFormula());
//                                        throw e1;
                                    }
                                }
                                break;
                        }
                        reportCell.setVal(cellStrVal.toString());
                    }
                    reportRows.add(reportCells);
                }

                mergeCell(reportRows,sheet.getMergedRegions());

                reportSheets.add(reportRows);
            }
            Sheet sheet = wb.getSheetAt(0);



            logger.debug("当前报表{},数据为：{}",templateFile.getName(),reportSheets);
            return reportSheets;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void writeFile(ArrayList<ArrayList<ReportCell>> dataList , File reportFile){
        try (InputStream inp = new FileInputStream(reportFile)) {
            logger.debug("开始写入报表:{}",reportFile.getPath());

            Workbook wb = WorkbookFactory.create(inp);
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();

                dataList.forEach(dataRow->{
                    dataRow.forEach(
                            reportCell -> {
                                int rowNum = reportCell.getRow();
                                int columnNum = reportCell.getColumn();
                                String cellVal = reportCell.getVal();
                                Cell cell = sheet.getRow(rowNum).getCell(columnNum);
                                if(cell==null){
                                    cell = sheet.getRow(rowNum).createCell(columnNum);
                                }
                                logger.debug("写入报表第{}行,第{}列,数据内容:{},表格原值：{}",rowNum,columnNum,cellVal,cell.toString());
                                switch (cell.getCellTypeEnum()){
                                    case STRING:
                                        cell.setCellValue(cellVal);
                                    case NUMERIC:
                                        cell.setCellValue(new Integer(cellVal));
                                        break;
                                    case BLANK:
                                        cell.setCellValue(cellVal);
                                        break;
                                }
                            }
                    );
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

    /**
     * 从当前服务器硬盘获取模板文件
     * @param templateName
     * @return
     */
    private File loadTemplateFileFromDisk(String templateName){
        String path = recordConfig.getTemplateFilePath();
        logger.info("报表模板文件路径:{}",path);
        StringBuilder sb = new StringBuilder(path);
        sb.append("/");
        sb.append(templateName);
        File templateFile = new File(sb.toString());
        return templateFile;
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

    private void mergeCell(List<List<ReportCell>> reportRows,List<CellRangeAddress> allMergeRegions){
        Map<Integer,Integer> hasRemoveOffsetMap = new HashMap<>();

        allMergeRegions.forEach(mergeRegion->{
//            logger.debug("合并单元格信息:起始行号{}截止行号{},起始列号{}截止列号{}",
//                    mergeRegion.getFirstRow(),mergeRegion.getLastRow(),mergeRegion.getFirstColumn(),mergeRegion.getLastColumn());
            int mergedStartRow = mergeRegion.getFirstRow();
            int mergedEndRow = mergeRegion.getLastRow();
            int mergedStartColumn = mergeRegion.getFirstColumn();
            int mergedEndColumn = mergeRegion.getLastColumn();
            int mergedRowsOffset = mergedEndRow-mergedStartRow;
            int mergedColumnOffset = mergedEndColumn-mergedStartColumn;
            if(mergedRowsOffset>0){//合并行
                List<ReportCell> startRow = reportRows.get(mergedStartRow);//获取合并的起始行
                ReportCell reportStartCell = startRow.get(mergedStartColumn);//定位到需要合并的起始列
                reportStartCell.setRowspan(mergedRowsOffset+1);
                for(int i=1;i<(mergedRowsOffset+1);i++){//删除已经合并的单元格
                    List<ReportCell> reportRow = reportRows.get(mergedStartRow + i);//获取到行
                    if(mergedColumnOffset>0){//合并列
                        reportStartCell.setColspan(mergedColumnOffset+1);
                        for(int column=0;column<(mergedColumnOffset-1);column++){
                            reportRow.remove(mergedStartColumn+1);
                        }
                    }else
                        reportRow.remove(mergedStartColumn);
                }
            }
            else if(mergedColumnOffset>0){//合并列 合并3~5（2~4） mergedColumnOffset：2 mergedStartColumn：2
                int mergeColumnNum = mergedStartColumn;
                if(hasRemoveOffsetMap.containsKey(mergedStartRow)){
                    mergeColumnNum = mergedStartColumn-hasRemoveOffsetMap.get(mergedStartRow);
                    hasRemoveOffsetMap.put(mergedStartRow,hasRemoveOffsetMap.get(mergedStartRow)+mergedColumnOffset);
                }else{
                    hasRemoveOffsetMap.put(mergedStartRow,mergedColumnOffset);
                }
                List<ReportCell> reportRow = reportRows.get(mergedStartRow);
                ReportCell reportCell = reportRow.get(mergeColumnNum);
                reportCell.setColspan(mergedColumnOffset+1);
//                logger.debug("reportRow value :{}",reportRow);
                for(int i=1;i<(mergedColumnOffset+1);i++){
                    reportRow.remove(mergeColumnNum+1);//移除两次 第四个元素 3
                }

            }
        });

        logger.debug("合并后excel格式为:{}",reportRows);
    }

}
