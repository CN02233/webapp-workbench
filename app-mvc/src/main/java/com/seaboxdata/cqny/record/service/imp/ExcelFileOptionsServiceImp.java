package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.entity.ExcelContext;
import com.seaboxdata.cqny.record.entity.ExcelTemplateCellMerged;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.service.ExcelFileOptionsService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service("excelFileOptionsService")
public class ExcelFileOptionsServiceImp implements ExcelFileOptionsService {

    private Logger logger = LoggerFactory.getLogger(ExcelFileOptionsServiceImp.class);

    @Autowired
    private RecordConfig recordConfig;


    @Override
    public List<ExcelContext> loadFileByFilePath(String filePath) {

        File reportFile = new File(filePath);

        List<ExcelContext> loadResult = loadFile(reportFile);

        return loadResult;
    }

    @Override
    public List<List<List<ReportCell>>> loadReport(String filePath) {

        File reportFile = new File(filePath);

        List<List<List<ReportCell>>> reslt = readFile(reportFile);

        return reslt;
    }

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

                        reportCell.setAligin(cell.getCellStyle().getAlignmentEnum().toString());

                        try{
                            String cellVal = this.checkCellVal(cell);
                            if(Strings.isNullOrEmpty(cellVal))
                                reportCell.setInput(true);
                            else
                                reportCell.setInput(false);
                            reportCell.setVal(cellVal);

                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
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
    private List<ExcelContext> loadFile(File templateFile){
        try (InputStream inp = new FileInputStream(templateFile)) {

            Workbook wb = WorkbookFactory.create(inp);
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            List<ExcelContext> sheets = new ArrayList<>();
            while(sheetIterator.hasNext()){
                int columRows = 0;
                Sheet sheet = sheetIterator.next();
                List<List<String>> reportRows = new ArrayList();
                Map<String,String> formulaMap = new HashMap<>();
                for (int currRowNum = 0; currRowNum<sheet.getLastRowNum(); currRowNum++) {
                    Row row = sheet.getRow(currRowNum);
                    List<String> reportCells = new ArrayList<>();
                    if(row==null){
                        logger.debug("当前行号：{},空.....",currRowNum);
                        continue;
                    }
                    if(columRows<row.getLastCellNum())
                        columRows = row.getLastCellNum()+1;

                    logger.debug("当前行号:{}，起始列{}，结束列{}",row.getRowNum(),row.getFirstCellNum(),row.getLastCellNum());
                    for (int columnNum = 0;columnNum<row.getLastCellNum();columnNum++) {
                        Cell cell = row.getCell(columnNum);

                        if(cell==null){
                            reportCells.add("");
                            continue;
                        }else{
                            if(cell.getCellComment()!=null)
                                logger.debug("cell coment is not null : {}",cell.getCellComment().getString().toString());

                            if(cell.getCellTypeEnum() == CellType.FORMULA){
                                formulaMap.put(currRowNum+"-"+columnNum,cell.getCellFormula());
                            }
                            try{
                                String cellVal = this.checkCellVal(cell);
                                reportCells.add(cellVal);
                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    reportRows.add(reportCells);
                }

                ExcelContext excelContext = new ExcelContext();
                excelContext.setReportRows(reportRows);
                excelContext.setSheetName(sheet.getSheetName());
                excelContext.setSheetRows(sheet.getLastRowNum()+1);
                excelContext.setSheetColums(columRows);
                excelContext.setFormulas(formulaMap);
//                {row: 1, col: 1, rowspan: 2, colspan: 2}
                List<CellRangeAddress> allMergedRegisions = sheet.getMergedRegions();
                List<ExcelTemplateCellMerged> mergedList = new ArrayList<>();
                allMergedRegisions.forEach(mergeRegion-> {
//              logger.debug("合并单元格信息:起始行号{}截止行号{},起始列号{}截止列号{}",
//                    mergeRegion.getFirstRow(),mergeRegion.getLastRow(),mergeRegion.getFirstColumn(),mergeRegion.getLastColumn());
                    int mergedStartRow = mergeRegion.getFirstRow();
                    int mergedEndRow = mergeRegion.getLastRow();
                    int mergedStartColumn = mergeRegion.getFirstColumn();
                    int mergedEndColumn = mergeRegion.getLastColumn();
                    int mergedRowsOffset = mergedEndRow-mergedStartRow;
                    int mergedColumnOffset = mergedEndColumn-mergedStartColumn;
                    ExcelTemplateCellMerged excelTemplateCellMerged = new ExcelTemplateCellMerged();
                    excelTemplateCellMerged.setRow(mergedStartRow);
                    excelTemplateCellMerged.setCol(mergedStartColumn);
                    excelTemplateCellMerged.setRowspan(mergedRowsOffset>0?(mergedRowsOffset+1):1);
                    excelTemplateCellMerged.setColspan(mergedColumnOffset>0?mergedColumnOffset:1);
                    mergedList.add(excelTemplateCellMerged);
                });

                excelContext.setMergedList(mergedList);

                sheets.add(excelContext);
            }


            return sheets;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        return null;
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

    /**
     * 从当前服务器硬盘获取模板文件
     * @param templateName
     * @return
     */
    @Override
    public File loadTemplateFileFromDisk(String templateName){
        String path = recordConfig.getTemplateFilePath();
        logger.info("报表模板文件路径:{}",path);
        StringBuilder sb = new StringBuilder(path);
        sb.append("/");
        sb.append(templateName);
        File templateFile = new File(sb.toString());
        return templateFile;
    }

}
