package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("reportExportService")
public class ReportExportServiceImp implements ReportExportService {

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Autowired
    private ReportStatementsService reportStatementsService;

    @Autowired
    private ReportUnitService reportUnitService;
    
    @Autowired
    private OriginService originService;

    @Autowired
    private RecordConfig recordConfig;

    private static final Integer START_ROW_NUM = 1;//0 ~ Integer.MAX

    private ThreadLocal<Sheet> sheetLocal = new ThreadLocal<>();

    private Logger logger = LoggerFactory.getLogger(ReportExportServiceImp.class);

    public String doExport(String reportId){
        ReportCustomer reportCustomer = reportCustomerService.checkReportCustomer(reportId);
        Integer reportDefinedId = reportCustomer.getReport_defined_id();

        //获取报表定义信息
        ReportDefinedEntity reportDefined = this.getReportDefined(reportDefinedId);
        reportDefined.setUnits(reportCustomer.getUnitEntities());

        //获取报表填报数据信息
        Map<UnitDefined, ReportUnitCustomerContext> reportUnitDataMap = this.getReportCustData(reportCustomer);

        //获取报表所属单位
        Map<String, Object> origin = originService.getOriginById(reportCustomer.getReport_origin());

        //设置sheet页蛇形
        this.makeExcelSheet(reportCustomer.getReport_name());

        //生成标题
        String origin_name = (String) origin.get("origin_name");
        String reportName = reportCustomer.getReport_name();
        String reportDataDate = new StringBuilder()
                .append(reportCustomer.getReport_data_start_str())
                .append("~")
                .append(reportCustomer.getReport_data_end_str()).toString();

        Integer offsetRowNum = this.createTitle(origin_name, reportName, reportDataDate);//offsetRowNum 已生成的标题行数


        //生成每行内容
        this.createContext(reportCustomer.getUnitEntities(),reportUnitDataMap,START_ROW_NUM+offsetRowNum+1);


        OutputStream os = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat timeFormart = new SimpleDateFormat("HHmmss");
            Date nowDate = new Date();
            String dateStr = dateFormat.format(nowDate);
            String timeStr = timeFormart.format(nowDate);

            StringBuilder filePathSb = new StringBuilder();
            filePathSb.append(recordConfig.getReportExportPath()).append("/").
                    append(dateStr).append("/").
                    append(origin_name).append("/");
            StringBuilder fileNameSb = new StringBuilder();
            fileNameSb.append(reportDefined.getDefined_name()).
                    append("_").append(timeStr).
                    append(".xls");

            File filePath = new File(filePathSb.toString());
            if (!filePath.exists()){
                filePath.mkdirs();
            }
            
            os = new FileOutputStream(filePathSb.append(fileNameSb).toString());
            sheetLocal.get().getWorkbook().write(os);
            return filePathSb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void makeExcelSheet(String reportName) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(reportName);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);


//        cellStyle.setWrapText(true);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//
//        cellStyle.setBorderLeft(BorderStyle.THIN);-+
//        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

        sheet.setColumnWidth(0, 20*256);
        sheet.setDefaultColumnStyle(0, cellStyle);

        sheet.setColumnWidth(1, 40*256);
        sheet.setDefaultColumnStyle(1, cellStyle);

        sheet.setColumnWidth(2, 5*256);
        sheet.setDefaultColumnStyle(2, cellStyle);

        sheet.setColumnWidth(3, 10*256);
        sheet.setDefaultColumnStyle(3, cellStyle);

        sheet.setColumnWidth(4, 10*256);
        sheet.setDefaultColumnStyle(4, cellStyle);

        sheet.setColumnWidth(5, 20*256);
        sheet.setDefaultColumnStyle(5, cellStyle);

        sheet.setColumnWidth(6, 20*256);
        sheet.setDefaultColumnStyle(6, cellStyle);

        sheet.setColumnWidth(7, 20*256);
        sheet.setDefaultColumnStyle(7, cellStyle);

        sheetLocal.set(sheet);
    }

    private ReportDefinedEntity getReportDefined(Integer reportDefinedId){
        ReportDefinedEntity reportDefined = reportStatementsService.getReportDefinedById(reportDefinedId);
        return reportDefined;
    }

    private Map<UnitDefined,ReportUnitCustomerContext> getReportCustData(ReportCustomer reportCustomer){
        Map<UnitDefined,ReportUnitCustomerContext> resultReportUnitCustomerContext = new HashMap<>();

        Integer reportId = reportCustomer.getReport_id();
        List<UnitDefined> unitList = reportCustomer.getUnitEntities();
        for (UnitDefined unitDefined : unitList) {
            Integer unitId = unitDefined.getUnit_id();
            Integer unitType = unitDefined.getUnit_type();
            ReportUnitCustomerContext reportUnitCustomerContext =
                    reportCustomerService.getUnitContext(reportId.toString(), unitId.toString(), unitType.toString());
            resultReportUnitCustomerContext.put(unitDefined,reportUnitCustomerContext);
        }

        return resultReportUnitCustomerContext;
    }

    private Integer createTitle(String... titleContext){
        Sheet sheetObj = sheetLocal.get();
        for (int titleIndex = 0;titleIndex<titleContext.length;titleIndex++) {
            Row row = sheetObj.createRow(START_ROW_NUM+titleIndex);
            Cell cell = row.createCell(1);
            sheetObj.addMergedRegion(
                    new CellRangeAddress(START_ROW_NUM+titleIndex, START_ROW_NUM+titleIndex, 1, 7));// 合并单元格
            cell.setCellValue(titleContext[titleIndex]);
            CellStyle cellStyle = sheetObj.getWorkbook().createCellStyle();
            if(titleIndex==1){
                Font font = sheetObj.getWorkbook().createFont();
                font.setFontHeightInPoints((short) 15);

                cellStyle.setFont(font);
                row.setHeightInPoints(20);
            }

            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cell.setCellStyle(cellStyle);
        }

        return titleContext.length;
    }

    private void createContext(List<UnitDefined> unitEntities,
                               Map<UnitDefined, ReportUnitCustomerContext> reportUnitDataMap,
                               Integer contextStartRowNum){
        //生成内容标题
        this.createContextTitle(contextStartRowNum);

        //按单元生成内容
        contextStartRowNum++;

        int titleRowNUM = contextStartRowNum.intValue();

        for (UnitDefined unitDefined : unitEntities) {
            Integer unitType = unitDefined.getUnit_type();
            ReportUnitCustomerContext reporetUnitData = reportUnitDataMap.get(unitDefined);

            if (UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)) {
                List<SimpleColumDefined> definedColums = reporetUnitData.getDefinedColums();
                List<ReportCustomerData> columDatas = reporetUnitData.getColumDatas();

                logger.info("一维报送单元：{}，开始生成excel，起始行号：{}",unitDefined.getUnit_name(),contextStartRowNum);
                contextStartRowNum = this.createOneDimContext(
                        unitDefined,
                        definedColums,
                        columDatas,
                        contextStartRowNum,
                        titleRowNUM);
            }
            else if (UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)) {
                List<SimpleColumDefined> definedColums = reporetUnitData.getDefinedColums();
                List<ReportCustomerData> columDatas = reporetUnitData.getColumDatas();

                logger.info("一维动态报送单元：{}，开始生成excel，起始行号：{}",unitDefined.getUnit_name(),contextStartRowNum);
                contextStartRowNum = this.createOneDimManyContext(
                        unitDefined,
                        definedColums,
                        columDatas,
                        contextStartRowNum,
                        titleRowNUM);
            }
            else if (UnitDefinedType.MANYDIMTREE.compareWith(unitType)) {
                List<SimpleColumDefined> definedColums = reporetUnitData.getDefinedColums();
                List<ReportCustomerData> columDatas = reporetUnitData.getColumDatas();

                logger.info("树状报送单元：{}，开始生成excel，起始行号：{}",unitDefined.getUnit_name(),contextStartRowNum);
                this.createTreeDImContext(
                        unitDefined,
                        definedColums,
                        columDatas
                        );
            }
            else if (UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)) {
                List<GridColumDefined> definedColums = reporetUnitData.getDefinedColums();
                List<ReportCustomerData> columDatas = reporetUnitData.getColumDatas();
                logger.info("多维报送单元：{}，开始生成excel，起始行号：{}",unitDefined.getUnit_name(),contextStartRowNum);

                contextStartRowNum = this.createManydimStaticContext(
                        unitDefined,
                        definedColums,
                        columDatas,
                        contextStartRowNum,
                        titleRowNUM);
            }

        }
    }



    private void createContextTitle(Integer contextStartRowNum){
        Sheet sheetObj = sheetLocal.get();
        Row row = sheetObj.createRow(contextStartRowNum);

        CellStyle cellStyle = sheetObj.getWorkbook().createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font fontObj = sheetObj.getWorkbook().createFont();
        fontObj.setBold(true);
        cellStyle.setFont(fontObj);

        Cell cell1 = row.createCell(0);
        sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 0, 1));
        cell1.setCellValue("项目");
        cell1.setCellStyle(cellStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue("行次");
        cell2.setCellStyle(cellStyle);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue("单位");
        cell3.setCellStyle(cellStyle);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue("数值");
        cell4.setCellStyle(cellStyle);

        Cell cell5 = row.createCell(5);
        sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 5, 7));
        cell5.setCellValue("备注");
        cell5.setCellStyle(cellStyle);

    }

    /**
     * 一维报送单元内容生成
     * @param unitDefined 单元定义
     * @param definedColums 填报项定义
     * @param columDatas 填报内容
     * @param contextStartRowNum excel中开始行号
     * @param titleRowNUM 整个sheet表格头占用的行数
     * @return
     */
    private Integer createOneDimContext(
                                     UnitDefined unitDefined,
                                     List<SimpleColumDefined> definedColums,
                                     List<ReportCustomerData> columDatas,
                                     Integer contextStartRowNum,
                                     int titleRowNUM){
        Sheet sheetObj = sheetLocal.get();

        this.createUNitNameCell(contextStartRowNum,contextStartRowNum+columDatas.size()-1);

        Map<String, ReportCustomerData> reportCustomerDataMap = this.makeReportDataMap(columDatas,true,false,false);

        for (SimpleColumDefined definedColum : definedColums) {
            String columDataValue = "0";

            if(reportCustomerDataMap.containsKey(definedColum.getColum_id().toString())){
                ReportCustomerData columData = reportCustomerDataMap.get(definedColum.getColum_id().toString());
                columDataValue = columData.getReport_data();
            }

            Row contextRow = sheetObj.createRow(contextStartRowNum);

            this.fillUnitNameCell(contextRow,unitDefined.getUnit_name());

            Cell cell1 = contextRow.createCell(1);
//            sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 0, 1));
            cell1.setCellValue(definedColum.getColum_name_cn());

            Cell cell2 = contextRow.createCell(2);
            cell2.setCellValue(contextStartRowNum-(titleRowNUM-1));

            Cell cell3 = contextRow.createCell(3);
            cell3.setCellValue(definedColum.getColum_point());

            Cell cell4 = contextRow.createCell(4);
            cell4.setCellValue(columDataValue);

            Cell cell5 = contextRow.createCell(5);
            sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 5, 7));
            cell5.setCellValue(definedColum.getColum_desc());

            contextStartRowNum++;
        }
        
        return contextStartRowNum;

    }

    /**
     * 一维动态报送单元内容生成
     * @param unitDefined 单元定义
     * @param definedColums 填报项定义
     * @param columDatas 填报内容
     * @param contextStartRowNum excel中开始行号
     * @param titleRowNUM 整个sheet表格头占用的行数
     * @return
     */
    private Integer createOneDimManyContext(UnitDefined unitDefined,
                                            List<SimpleColumDefined> definedColums,
                                            List<ReportCustomerData> columDatas,
                                            Integer contextStartRowNum,
                                            int titleRowNUM) {

        Sheet sheetObj = sheetLocal.get();

        Map<String,Map<String,ReportCustomerData>> reportDataTmp = new HashMap<>();


        for (ReportCustomerData columData : columDatas) {
            String columId = columData.getColum_id();
            String dimId = columData.getDimensions_id();
            String groupId = columData.getReport_group_id();
            if(Strings.isNullOrEmpty(groupId)){
                if(!reportDataTmp.containsKey(columId+dimId)){
                    reportDataTmp.put(columId+dimId,new HashMap<String,ReportCustomerData>());
                }
                reportDataTmp.get(columId+dimId).put(columId,columData);

            }else{
                if(!reportDataTmp.containsKey(groupId+dimId)){
                    reportDataTmp.put(groupId+dimId,new HashMap<String,ReportCustomerData>());
                }
                reportDataTmp.get(groupId+dimId).put(columId,columData);
            }

        }

        this.createUNitNameCell(contextStartRowNum,contextStartRowNum+(reportDataTmp.size()*definedColums.size())-1);


        Set<String> groupKeys = reportDataTmp.keySet();
        for (String groupKey : groupKeys) {
            Map<String, ReportCustomerData> groupDatas = reportDataTmp.get(groupKey);
            for (SimpleColumDefined definedColum : definedColums) {
                String columDataValue = "0";

                if(groupDatas.containsKey(definedColum.getColum_id().toString())){
                    ReportCustomerData columData = groupDatas.get(definedColum.getColum_id().toString());
                    if(Strings.isNullOrEmpty(columData.getReport_group_id())){
                        definedColum.setColum_name_cn(columData.getReport_data());
                        columDataValue="";

                    }else{
                        columDataValue = columData.getReport_data();
                    }
                }

                Row contextRow = sheetObj.createRow(contextStartRowNum);

                this.fillUnitNameCell(contextRow,unitDefined.getUnit_name());

                Cell cell1 = contextRow.createCell(1);
//            sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 0, 1));
                cell1.setCellValue(definedColum.getColum_name_cn());

                Cell cell2 = contextRow.createCell(2);
                cell2.setCellValue(contextStartRowNum-(titleRowNUM-1));

                Cell cell3 = contextRow.createCell(3);
                cell3.setCellValue(definedColum.getColum_point());

                Cell cell4 = contextRow.createCell(4);
                cell4.setCellValue(columDataValue);

                Cell cell5 = contextRow.createCell(5);
                sheetObj.addMergedRegion(new CellRangeAddress(contextStartRowNum, contextStartRowNum, 5, 7));
                cell5.setCellValue(definedColum.getColum_desc());

                contextStartRowNum++;
            }
        }
        

        
        return contextStartRowNum;
    }

    /**
     * 多维报送单元内容生成
     * @param unitDefined 单元定义
     * @param definedColums 填报项定义
     * @param columDatas 填报内容
     * @param contextStartRowNum excel中开始行号
     * @param titleRowNUM 整个sheet表格头占用的行数
     * @return
     */
    private Integer createManydimStaticContext(UnitDefined unitDefined,
                                               List<GridColumDefined> definedColums,
                                               List<ReportCustomerData> columDatas,
                                               Integer contextStartRowNum,
                                               int titleRowNUM) {

        Sheet sheetObj = sheetLocal.get();

        Map<String, ReportCustomerData> reportCustomerDataMap = this.makeReportDataMap(columDatas,true,true,false);

        List<GridColumDefined> colDimAssigns = new ArrayList<>();
        Map<Integer,GridColumDefined> colDefineds = new HashMap<>();
        List<GridColumDefined> dimDefineds = new ArrayList<>();

        for (GridColumDefined definedColum : definedColums) {
            if("1".equals(definedColum.getColum_meta_type())){
                colDimAssigns.add(definedColum);
            }else if("2".equals(definedColum.getColum_meta_type())){
                colDefineds.put(definedColum.getColum_id(),definedColum);
            }else if("3".equals(definedColum.getColum_meta_type())){
                dimDefineds.add(definedColum);
            }
        }

        this.createUNitNameCell(contextStartRowNum,contextStartRowNum+colDefineds.size());

        //生成维度行
        Map<Integer,Integer> dimOrder = new HashMap<>();
        Row dimROw = null;
        for (int dimOrderIndex = 0;dimOrderIndex<dimDefineds.size();dimOrderIndex++) {
            GridColumDefined dimDefined = dimDefineds.get(dimOrderIndex);
            dimOrder.put(dimDefined.getDim_id(),dimOrderIndex);
            String dimName = dimDefined.getDim_name_cn();
            if(dimROw==null){
                dimROw = sheetObj.createRow(contextStartRowNum);

                this.fillUnitNameCell(dimROw,unitDefined.getUnit_name());

                Cell columOrderCell = dimROw.createCell(2);
                columOrderCell.setCellValue(contextStartRowNum-(titleRowNUM-1));
            }
            Cell dimNameCell = dimROw.createCell(4 + dimOrderIndex);
            dimNameCell.setCellValue(dimName);
        }

//        Cell noteCell = dimROw.createCell(4 + dimDefineds.size());
//        noteCell.setCellValue("备注");

        contextStartRowNum++;

        //生成每行数据
        Map<Integer,Row> rowTmpMap = new HashMap<>();
        for (GridColumDefined definedColum : colDimAssigns) {
            Integer columId = definedColum.getColum_id();
            Integer dimId = definedColum.getDim_id();
            String dataKey = new StringBuilder().append(columId).append(dimId).toString();
            if(reportCustomerDataMap.containsKey(dataKey)){
                Row columRow = null;
                if(rowTmpMap.containsKey(columId)){
                    columRow = rowTmpMap.get(columId);
                }else{
                    columRow = sheetObj.createRow(contextStartRowNum);
                    rowTmpMap.put(columId,columRow);

                    this.fillUnitNameCell(columRow,unitDefined.getUnit_name());

                    GridColumDefined columDefined = colDefineds.get(columId);
                    String colNameCN = columDefined.getColum_name_cn();

                    Cell columNameCell = columRow.createCell(1);
                    columNameCell.setCellValue(colNameCN);

                    Cell columOrderCell = columRow.createCell(2);
                    columOrderCell.setCellValue(contextStartRowNum-(titleRowNUM-1));

                    Cell columPointCell = columRow.createCell(3);
                    columPointCell.setCellValue(columDefined.getColum_point());


//                    Cell columNoteCell = columRow.createCell(4+dimOrder.size());
//                    columNoteCell.setCellValue(definedColum.getColum_formula_desc());

                    contextStartRowNum++;
                }

                Integer dimOrderIndex = dimOrder.get(dimId);

                Cell columDimValueCell = columRow.createCell(4+dimOrderIndex);
                columDimValueCell.setCellValue(reportCustomerDataMap.get(dataKey).getReport_data());

            }
        }

        return contextStartRowNum;
    }


    private void createTreeDImContext(UnitDefined unitDefined,
                                      List<SimpleColumDefined> definedColums,
                                      List<ReportCustomerData> columDatas) {

        Map<Integer, Map<String, Object>> formatTreeData = this.formatTreeData(definedColums, columDatas);

        Workbook workbook = sheetLocal.get().getWorkbook();
        Sheet treeSheet = workbook.createSheet(unitDefined.getUnit_name());
        Integer rowNumTMp = START_ROW_NUM;

        CellStyle cellStyle = workbook.createCellStyle();

        Font fontObj = workbook.createFont();
        fontObj.setFontHeightInPoints((short)12);
        cellStyle.setFont(fontObj);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        for (Integer formatGroupId : formatTreeData.keySet()) {
            Map<String, Object> groupContext = formatTreeData.get(formatGroupId);
            List<SimpleColumDefined> definedColumsTmp = (List<SimpleColumDefined>)groupContext.get("definedColums");
            List<List<ReportCustomerData>> rowColumArrayTmp = (List<List<ReportCustomerData>>)groupContext.get("rowDataArray");
            Row titleRowObj = treeSheet.createRow(rowNumTMp);
            titleRowObj.setHeightInPoints((short)25);
            for (int simpleColumDefinedIndex=0;simpleColumDefinedIndex<definedColumsTmp.size();simpleColumDefinedIndex++) {
                String columNameCN = definedColumsTmp.get(simpleColumDefinedIndex).getColum_name_cn();
                Cell titleCell = titleRowObj.createCell(simpleColumDefinedIndex);

                titleCell.setCellValue(columNameCN);
                titleCell.setCellStyle(cellStyle);

                treeSheet.setColumnWidth(simpleColumDefinedIndex, (columNameCN.length())*256*4);

            }
            rowNumTMp++;
            CellStyle cellStyle1 = workbook.createCellStyle();
            cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle1.setAlignment(HorizontalAlignment.CENTER);
            for (List<ReportCustomerData> reportCustomerDatas : rowColumArrayTmp) {
                Row valueRowTmp = treeSheet.createRow(rowNumTMp);
                valueRowTmp.setHeightInPoints((short)20);

                if(reportCustomerDatas!=null){
                    for (int simpleColumDefinedIndex=0;simpleColumDefinedIndex<reportCustomerDatas.size();simpleColumDefinedIndex++) {
                        ReportCustomerData reportCustomerData = reportCustomerDatas.get(simpleColumDefinedIndex);
                        Cell valueCell = valueRowTmp.createCell(simpleColumDefinedIndex);
                        valueCell.setCellStyle(cellStyle1);
                        if(reportCustomerData!=null){
                            valueCell.setCellValue(reportCustomerData.getReport_data());
                        }else{
                            valueCell.setCellValue("--");
                        }
                    }
                }
                rowNumTMp++;
            }
        }

    }

    private Map<Integer, Map<String, Object>> formatTreeData(List<SimpleColumDefined> definedColums,
                                                             List<ReportCustomerData> columDatas){
        //按层级分组树状定义
        //key:geroupId,value:List<SimpleColumDefined>
        Map<Integer,Map<String,Object>> groupDefinedsMapTmp = new LinkedHashMap<>();
        for (SimpleColumDefined definedColum : definedColums) {
            Integer groupId = definedColum.getGroup_id();
            if(!groupDefinedsMapTmp.containsKey(groupId)){
                Map<String,Object> groupDataMapTmp = new HashMap<>();
                groupDataMapTmp.put("definedColums",new ArrayList<SimpleColumDefined>());
                groupDataMapTmp.put("columDatas",new ArrayList<ReportCustomerData>());
                groupDataMapTmp.put("definedColumsTotal",0);
                groupDefinedsMapTmp.put(groupId,groupDataMapTmp);
            }
            String columShow = definedColum.getColum_show();
            if(!Strings.isNullOrEmpty(columShow)&&"Y".equals(columShow)){
                List<SimpleColumDefined> definedColumsTmp = (List<SimpleColumDefined>) groupDefinedsMapTmp.get(groupId).get("definedColums");
                definedColumsTmp.add(definedColum);
                Integer definedColumsTotal = (Integer) groupDefinedsMapTmp.get(groupId).get("definedColumsTotal");
                groupDefinedsMapTmp.get(groupId).put("definedColumsTotal",definedColumsTotal+1);

            }
        }

        for (ReportCustomerData columData : columDatas) {
            String unit_id = columData.getUnit_id();
            String dimensions_id = columData.getDimensions_id();
            for (SimpleColumDefined definedColum : definedColums) {
                String definedUnitId = definedColum.getUnit_id().toString();
                String definedColumId = definedColum.getColum_id().toString();
                String columShow = definedColum.getColum_show();
                if(unit_id.equals(definedUnitId)&&dimensions_id.equals(definedColumId)&&"Y".equals(columShow)){
                    ////console.log(unit_id+"--"+dimensions_id+"--"+definedColum['unit_id']+"---"+definedColum['colum_id'])
                    List<ReportCustomerData> columDatasTmp = (List<ReportCustomerData>)
                            groupDefinedsMapTmp.get(definedColum.getGroup_id()).get("columDatas");
                    columDatasTmp.add(columData);
                }
            }
        }

        //groupData
        //组map report_group_id-->columId-->List<ReportCustomerData>

        Map<Integer,Map<String, Object>> resultRowMap = new LinkedHashMap<>();

        for (Integer groupId : groupDefinedsMapTmp.keySet()) {

            Map<String, Object> groupContext = groupDefinedsMapTmp.get(groupId);
            List<ReportCustomerData> columDatasTmp = (List<ReportCustomerData>)groupContext.get("columDatas");
            List<SimpleColumDefined> definedColumsTmp = (List<SimpleColumDefined>)groupContext.get("definedColums");
            Integer definedColumsTotal = (Integer) groupContext.get("definedColumsTotal");
            if(definedColumsTotal>0){
                resultRowMap.put(groupId,groupContext);
                groupContext.put("rowDataArray",new ArrayList<List<ReportCustomerData>>());

            }else{
                continue;
            }

//            Map<String,Map<String,List<ReportCustomerData>>> groupColumDatasMapTmp = new HashMap<>();
            Map<String, List<ReportCustomerData>> columDatasTmpMap = new LinkedHashMap<>();

            for (ReportCustomerData reportCustomerDataTmp : columDatasTmp) {
                String columId = reportCustomerDataTmp.getColum_id();
                if(!columDatasTmpMap.containsKey(columId)){
                    columDatasTmpMap.put(columId,new ArrayList<>());
                }

                columDatasTmpMap.get(columId).add(reportCustomerDataTmp);
            }



            for (String columId : columDatasTmpMap.keySet()) {
                List<ReportCustomerData> columDatasTMp = columDatasTmpMap.get(columId);
                logger.info("行号{},数量{}",columId,columDatasTMp.size());
                List<ReportCustomerData> rowColumArray = new ArrayList<>();

                for (Integer integer = 0; integer < definedColumsTotal; integer++) {
                    rowColumArray.add(null);
                }

                for (ReportCustomerData reportCustomerData : columDatasTMp) {
                    String dataDImiD = reportCustomerData.getDimensions_id();
                    for(int defindeColumIndex = 0;defindeColumIndex<definedColumsTmp.size();defindeColumIndex++){
                        SimpleColumDefined definedColumsTmpObj = definedColumsTmp.get(defindeColumIndex);
                        Integer definedColumId = definedColumsTmpObj.getColum_id();
                        if(dataDImiD.equals(definedColumId.toString())){
                            rowColumArray.set(defindeColumIndex,reportCustomerData);
                            break;
                        }
                    }
                }


                List rowDataArray = (List) groupContext.get("rowDataArray");
                rowDataArray.add(rowColumArray);
            }

        }

        return resultRowMap;
    }

    private Map<String, ReportCustomerData> makeReportDataMap(List<ReportCustomerData> columDatas,
                                                               boolean needColumId,
                                                               boolean needDimId,
                                                               boolean needGroupId){
        Map<String,ReportCustomerData> reportCustomerDataMap = new HashMap<>();
        for (ReportCustomerData columData : columDatas) {
            StringBuilder mapKeySb = new StringBuilder();
            if(needColumId){
                mapKeySb.append(columData.getColum_id());
            }
            if(needDimId){
                mapKeySb.append(columData.getDimensions_id());
            }
            if(needGroupId){
                mapKeySb.append(columData.getReport_group_id());
            }
            reportCustomerDataMap.put(mapKeySb.toString(),columData);
        }
        return reportCustomerDataMap;
    }

    private void createUNitNameCell(Integer startRowNum,Integer endRowNum){
        this.sheetLocal.get().addMergedRegion(new CellRangeAddress(startRowNum, endRowNum, 0, 0));
    }

    private void fillUnitNameCell(Row contextRow ,String unitName){

        Sheet sheetObj = sheetLocal.get();
        
        CellStyle cellStyle = sheetObj.getWorkbook().createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font fontObj = sheetObj.getWorkbook().createFont();
        fontObj.setBold(true);
        cellStyle.setFont(fontObj);

        Cell cell0 = contextRow.createCell(0);
        cell0.setCellValue(unitName);
        cell0.setCellStyle(cellStyle);

    }
}
