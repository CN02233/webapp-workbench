package com.seaboxdata.cqny.datamove.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.datamove.ReportDataMoveService;
import com.seaboxdata.cqny.datamove.dao.IReportDataMoveDao;
import com.seaboxdata.cqny.datamove.entity.DataMoveConfig;
import com.seaboxdata.cqny.datamove.entity.DataMoveConfigGroup;
import com.seaboxdata.cqny.datamove.entity.DataMoveRowConfigGroup;
import com.seaboxdata.cqny.datamove.entity.DataMovieUnitConfigGroup;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.seaboxdata.cqny.record.service.ReportUnitService;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("reportDataMoveService")
public class ReportDataMoveServiceImp implements ReportDataMoveService {

    private Logger logger = LoggerFactory.getLogger(ReportDataMoveServiceImp.class);

    @Autowired
    private IReportDataMoveDao reportDataMoveDao;
    @Autowired
    private ReportCustomerService reportCustomerService;

    @Autowired
    private ReportUnitService reportUnitService;

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @Autowired
    private OriginService originService;

    private Map<String,Boolean> hasCleanedOneDyn = new HashMap<>();

    private List<String> createOneTime = new ArrayList<>();

    public void doMove(){
        //获取各个报表与现系统各个单元的对应关系
        Map<String, Map<String, DataMoveConfigGroup>> reportDefinedConfigTmp = this.getDataMoveConfig();

        Map<String,Integer> reportIdTmp = new HashMap<>();

        if(reportDefinedConfigTmp!=null){
            for (String reportDefinedId : reportDefinedConfigTmp.keySet()) {
                System.out.println("******************************************************************************************************************");
                System.out.println("                                           报表:"+reportDefinedId+"                                                ");
                System.out.println("******************************************************************************************************************");

                hasCleanedOneDyn.clear();

                logger.warn("报送模板id:{}", reportDefinedId);

                Map<String, DataMoveConfigGroup> configMapTmp = reportDefinedConfigTmp.get(reportDefinedId);

                logger.info("开始按表迁移数据");

                for (String tableNameTmp : configMapTmp.keySet()) {
                    System.out.println("******************************************************************************************************************");
                    System.out.println("                                           迁移表:"+tableNameTmp+"                                                     ");
                    System.out.println("******************************************************************************************************************");
                    logger.warn("开始迁移表:{}", tableNameTmp);
//
                    logger.warn("获取表{}下的机构列表", tableNameTmp);
                    List<Map<String, Object>> orgList = reportDataMoveDao.getOldDataForTableOrgs(tableNameTmp);
                    logger.warn("表{}下机构列表:{}", tableNameTmp, orgList);
//
                    String unitId = configMapTmp.get(tableNameTmp).getUnitId();
                    UnitDefined unitDefined = reportUnitService.getReportUnit(unitId);
                    Integer unitType = unitDefined.getUnit_type();
                    logger.warn("当前数据单元的填报类型为：{}", UnitDefinedType.getTypeByValue(unitType).toString());

                    for (Map<String, Object> stringObjectMap : orgList) {
                        Object originId = stringObjectMap.get("BANKID");

                        List<Map<String, Object>> oldTableDatas = reportDataMoveDao.getOldDataForTable(tableNameTmp, originId);
                        logger.info("机构{}下总计数据条数:{}",originId,oldTableDatas!=null? oldTableDatas.size():0);

                        if (UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)) {//一维表格
                            Map<String, Map<String, Map<String, Object>>> oldTableDataMapTmp = new HashMap<>();

                            if (oldTableDatas != null) {
                                for (Map<String, Object> oldTableData : oldTableDatas) {
                                    String ddate = (String) oldTableData.get("DDATE");
                                    if (!oldTableDataMapTmp.containsKey(ddate)) {
                                        oldTableDataMapTmp.put(ddate, new HashMap<>());
                                    }
                                    String rid = (String) oldTableData.get("RID");
                                    oldTableDataMapTmp.get(ddate).put(rid, oldTableData);
                                }
                            }

                            logger.debug("{}",oldTableDataMapTmp);

                            this.oneDImsStaticMove(oldTableDataMapTmp, originId, reportIdTmp, configMapTmp, tableNameTmp,new Integer(reportDefinedId));
                        }


                        else if (UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)) {//多维表格
                            logger.info("将待迁移数据按行分组");
                            Map<String, Map<String, Map<String, Object>>> oldTableDataMapTmp = new HashMap<>();
                            if (oldTableDatas != null) {
                                for (Map<String, Object> oldTableData : oldTableDatas) {
                                    String ddate = (String) oldTableData.get("DDATE");
                                    if (!oldTableDataMapTmp.containsKey(ddate)) {
                                        oldTableDataMapTmp.put(ddate, new HashMap<>());
                                    }
                                    String rid = (String) oldTableData.get("RID");
                                    oldTableDataMapTmp.get(ddate).put(rid, oldTableData);
                                }
                            }
                            logger.info("待迁移数据按行分组-->{}", oldTableDataMapTmp);
                            logger.info("按机构生成报表基础数据");
                            this.manyDImsStaticMove(oldTableDataMapTmp, originId, reportIdTmp, configMapTmp, tableNameTmp,new Integer(reportDefinedId));
                        }
                    else if (UnitDefinedType.MANYDIMTREE.compareWith(unitType)) {//动态树状
                            logger.info("将待迁移数据按行分组");
                            Map<String, Map<String, List<Map<String, Object>>>> oldTableDataMapTmp = new HashMap<>();

                            if (oldTableDatas != null) {
                                for (Map<String, Object> oldTableData : oldTableDatas) {
                                    String ddate = (String) oldTableData.get("DDATE");
                                    String rid = (String) oldTableData.get("RID");
                                    if (!oldTableDataMapTmp.containsKey(ddate)) {
                                        Map<String, List<Map<String, Object>>> rowDataTmpMap = new HashMap<>();
                                        List<Map<String, Object>> rowDataTmpList = new ArrayList<>();
                                        rowDataTmpList.add(oldTableData);
                                        rowDataTmpMap.put(rid, rowDataTmpList);
                                        oldTableDataMapTmp.put(ddate, rowDataTmpMap);
                                        continue;
                                    }

                                    Map<String, List<Map<String, Object>>> ddDateTmpMap = oldTableDataMapTmp.get(ddate);

                                    if (!ddDateTmpMap.containsKey(rid)) {
                                        List<Map<String, Object>> rowDataTmpList = new ArrayList<>();
                                        rowDataTmpList.add(oldTableData);
                                        ddDateTmpMap.put(rid, rowDataTmpList);
                                        continue;
                                    }

                                    ddDateTmpMap.get(rid).add(oldTableData);
                                }

                                logger.info("待迁移数据按行分组-->{}", oldTableDataMapTmp);
                                logger.info("按机构生成报表基础数据");
                                this.oneDImDynamicMove(oldTableDataMapTmp, originId, reportIdTmp, configMapTmp, tableNameTmp,new Integer(reportDefinedId));

                            }
                        }
                        else {
                            logger.error("暂不支持当前类型的数据迁移:{}", UnitDefinedType.getTypeByValue(unitType).toString());
                        }
                    }
                }
            }
        }

    }

    public void oneDImsStaticMove(Map<String, Map<String, Map<String, Object>>> oldTableDataMapTmp,
                                  Object originId, Map<String, Integer> reportIdTmp,
                                  Map<String, DataMoveConfigGroup> configMapTmp,
                                  String tableNameTmp,Integer reportDefinedId) {

        DataMoveConfigGroup tableConfigGroup = configMapTmp.get(tableNameTmp);
        String unitId = tableConfigGroup.getUnitId();
        Map<String, DataMovieUnitConfigGroup> rows = tableConfigGroup.getDataMovieUnitConfigGroup();

        for (String ddate : oldTableDataMapTmp.keySet()) {
            Map<String, Map<String, Object>> rowDatas = oldTableDataMapTmp.get(ddate);
            Integer reportId = getReportId(reportIdTmp,reportDefinedId, originId, ddate);

            if (reportId == null) {
                logger.warn("未找到机构{}对应的迁移数据报表", originId);
                continue;
            }


            Map<String, Map<String, Object>> excelRowDatas = oldTableDataMapTmp.get(ddate);

            Set<String> excelRowNumbers = excelRowDatas.keySet();
            for (String excelRowNumber : excelRowNumbers) {
                Map<String, Object> excelRowData = excelRowDatas.get(excelRowNumber);
                DataMovieUnitConfigGroup rowColumRelation = null;

                if(rows.containsKey(excelRowNumber)){
                    rowColumRelation = rows.get(excelRowNumber);
                }else if(rows.containsKey("EMPTY")){
                    rowColumRelation = rows.get("EMPTY");
                }else{
                    logger.error("未找到RID:{}对应的配置项",excelRowNumber);
                    continue;
                }

                Map<String, DataMoveRowConfigGroup> excelColumNumbers = rowColumRelation.getDataMoveRowConfigGroup();
                for (String excelColumNumber : excelColumNumbers.keySet()) {
                    DataMoveRowConfigGroup dataMoveRowConfigGroup = excelColumNumbers.get(excelColumNumber);
                    String oneDimColumId = dataMoveRowConfigGroup.getDimId();
                    String tableColumName = dataMoveRowConfigGroup.getTableColumName();
                    Object reportData = excelRowData.get(tableColumName);
                    String reportDataStr = null;
                    if(reportData!=null){
                        reportDataStr = String.valueOf(reportData);
                    }
                    if(Strings.isNullOrEmpty(reportDataStr)){
                        continue;
                    }
                    Integer unitConfig = dataMoveRowConfigGroup.getRmbUnit();
                    String dataValue = String.valueOf(excelRowData.get(tableColumName));
                    try{
                        BigDecimal reportDataBigValue = new BigDecimal(dataValue);
                        reportDataBigValue = reportDataBigValue.divide(new BigDecimal(unitConfig));
                        dataValue = reportDataBigValue.toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setUnit_id(unitId);
                    reportCustomerData.setColum_id(oneDimColumId);
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setReport_data(dataValue);
                    reportDataMoveDao.updateReportCustomerData(reportCustomerData);
                }

            }

            for (String rid : rows.keySet()) {
                DataMovieUnitConfigGroup dataMovieUnitConfigGroup = rows.get(rid);
                Map<String, Object> tableRowData = rowDatas.get(rid);
            }



        }
    }

    /**
     * 获取各个报表与现系统各个单元的对应关系
     */
    private Map<String, Map<String, DataMoveConfigGroup>> getDataMoveConfig(){
        Map<String,Map<String,DataMoveConfigGroup>> reportDefinedMoveConfig = new LinkedHashMap<>();


        List<DataMoveConfig> allDataMoveConfigList = reportDataMoveDao.getAllDataMoveConfigOneTable();
        for (DataMoveConfig dataMoveConfig : allDataMoveConfigList) {
            String definedId = dataMoveConfig.getDefined_id();//现系统报送定义
            Map<String,DataMoveConfigGroup> excelMoveConfig = null;
            if(reportDefinedMoveConfig.containsKey(definedId)){
                excelMoveConfig = reportDefinedMoveConfig.get(definedId);
            }else{
                excelMoveConfig = new LinkedHashMap<>();
                reportDefinedMoveConfig.put(definedId,excelMoveConfig);
            }

            String tableName = dataMoveConfig.getOld_unit_table_name();//原报表编号,例如B01
            String unitId = dataMoveConfig.getNew_unit_id();//先报送单元编号

            String rid = dataMoveConfig.getOld_unit_row_name();//原报表行号,例如 7
            String columId = dataMoveConfig.getNew_unit_colum_id();

            String tableColumName = dataMoveConfig.getOld_unit_colum_name();
            String dimId = dataMoveConfig.getNew_unit_dim_id();

            Integer rmbUnit = dataMoveConfig.getRmb_unit();

            if(!excelMoveConfig.containsKey(tableName)){
                DataMoveConfigGroup dataMoveConfigGroup = new DataMoveConfigGroup();
                dataMoveConfigGroup.setTableName(tableName);
                dataMoveConfigGroup.setUnitId(unitId);
                excelMoveConfig.put(tableName,dataMoveConfigGroup);
            }

            Map<String, DataMovieUnitConfigGroup> dataMovieUnitConfigGroupMap = excelMoveConfig.get(tableName).getDataMovieUnitConfigGroup();
            if(!dataMovieUnitConfigGroupMap.containsKey(rid)){
                DataMovieUnitConfigGroup dataMovieUnitConfigGroup = new DataMovieUnitConfigGroup();
                dataMovieUnitConfigGroup.setRid(rid);
                dataMovieUnitConfigGroup.setColumId(columId);
                dataMovieUnitConfigGroupMap.put(rid,dataMovieUnitConfigGroup);
            }

            DataMovieUnitConfigGroup dataMovieUnitConfigGroup = dataMovieUnitConfigGroupMap.get(rid);
            Map<String, DataMoveRowConfigGroup> dataMOveRowConfigGroup = dataMovieUnitConfigGroup.getDataMoveRowConfigGroup();

            if(!dataMOveRowConfigGroup.containsKey(tableColumName)){
                DataMoveRowConfigGroup dataMoveRowConfigGroup = new DataMoveRowConfigGroup();
                dataMoveRowConfigGroup.setTableColumName(tableColumName);
                dataMoveRowConfigGroup.setDimId(dimId);
                dataMoveRowConfigGroup.setRmbUnit(rmbUnit);
                dataMOveRowConfigGroup.put(tableColumName,dataMoveRowConfigGroup);
            }

        }
        
        return reportDefinedMoveConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    public void manyDImsStaticMove(Map<String,Map <String, Map<String, Object>>> oldTableDataMapTmp,
                               Object originId,
                               Map<String,Integer> reportIdTmp,
                               Map<String, DataMoveConfigGroup> configMapTmp,
                               String tableNameTmp,Integer reportDefinedId){

        //根据对应关系，迁移数据 旧报送系统-报名=新报送系统-unitid
        //旧报送系统-RID=新报送系统-columid
        //旧报送系统-C5、C6、C7....=新报送系统-dimid
        //旧报送系统中存储的值对应新报送系统下的值 rd_b01.c5下值 = (report_data = rd_b01.c5 where unit_id = unitid and columid = columid and dimensions_id = dimid)
        for (String ddate : oldTableDataMapTmp.keySet()) {
            Map<String, Map<String, Object>> rowDatas = oldTableDataMapTmp.get(ddate);
            Integer reportId = getReportId(reportIdTmp,reportDefinedId, originId, ddate);

            if(reportId==null){
                logger.warn("未找到机构{}对应的迁移数据报表",originId);
                continue;
            }

            DataMoveConfigGroup tableConfigGroup = configMapTmp.get(tableNameTmp);
            String unitId = tableConfigGroup.getUnitId();
            Map<String, DataMovieUnitConfigGroup> rows = tableConfigGroup.getDataMovieUnitConfigGroup();
            for (String rid : rows.keySet()) {
                DataMovieUnitConfigGroup dataMovieUnitConfigGroup = rows.get(rid);
                String columId = dataMovieUnitConfigGroup.getColumId();
                Map<String, Object> tableRowData = rowDatas.get(rid);
                if(tableRowData==null)
                    continue;

                Map<String, DataMoveRowConfigGroup> dims = dataMovieUnitConfigGroup.getDataMoveRowConfigGroup();
                for (String tableColumId : dims.keySet()) {
                    DataMoveRowConfigGroup dataMoveRowConfigGroup = dims.get(tableColumId);
                    String oldDimId = dataMoveRowConfigGroup.getTableColumName();
                    String dimId = dataMoveRowConfigGroup.getDimId();

                    String dataValue = String.valueOf(tableRowData.get(oldDimId));
                    if(Strings.isNullOrEmpty(dataValue)){
                        logger.info("空值,不做处理 {}-->{}",rid,oldDimId);
                        continue;
                    }

                    logger.debug("update report_customer_data set report_data = {} " +
                            "where " +
                            "report_id={} and " +
                            "unit_id={} and " +
                            "colum_id={} and " +
                            "dimensions_id={}",tableRowData.get(oldDimId),reportId,unitId,columId,dimId);

                    Integer unitConfig = dataMoveRowConfigGroup.getRmbUnit();
                    String dataValueStr = String.valueOf(tableRowData.get(oldDimId));
                    try{
                        BigDecimal reportDataBigValue = new BigDecimal(dataValueStr);
                        reportDataBigValue = reportDataBigValue.divide(new BigDecimal(unitConfig));
                        dataValueStr = reportDataBigValue.toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setUnit_id(unitId);
                    reportCustomerData.setColum_id(columId);
                    reportCustomerData.setDimensions_id(dimId);
                    reportCustomerData.setReport_data(dataValueStr);
                    reportDataMoveDao.updateReportCustomerData(reportCustomerData);
                }
            }

        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void oneDImDynamicMove(Map<String, Map<String, List<Map<String, Object>>>> oldTableDataMapTmp,
                                  Object originId,
                                  Map<String,Integer> reportIdTmp,
                                  Map<String, DataMoveConfigGroup> configMapTmp,
                                  String tableNameTmp,Integer reportDefinedId){
        String unitId = configMapTmp.get(tableNameTmp).getUnitId();
        List<SimpleColumDefined> dynamicTreeColums = reportDefinedUnitOneDimService.getColumByUnit(unitId);
        Map<String,SimpleColumDefined> dynmicTreeColumsMapTmp = new HashMap<>();
        for (SimpleColumDefined dynamicTreeColum : dynamicTreeColums) {
            dynmicTreeColumsMapTmp.put(dynamicTreeColum.getColum_id().toString(),dynamicTreeColum);
        }

        //key:rid value:rid,columid,dims
        Map<String, DataMovieUnitConfigGroup> dataMoveUnitConfigGroup = configMapTmp.get(tableNameTmp).getDataMovieUnitConfigGroup();
        DataMovieUnitConfigGroup columRelations = dataMoveUnitConfigGroup.get("EMPTY");
        Map<String, DataMoveRowConfigGroup> columRelation = columRelations.getDataMoveRowConfigGroup();
        Map<String,String> dimId2ExcelColumMapTmp = new HashMap<>();
        for (String excelColumIndex : columRelation.keySet()) {
            dimId2ExcelColumMapTmp.put(columRelation.get(excelColumIndex).getDimId(),excelColumIndex);
        }

        for (String ddate : oldTableDataMapTmp.keySet()) {
            Map<String, List<Map<String, Object>>> ridListsMap = oldTableDataMapTmp.get(ddate);

            Integer reportId = getReportId(reportIdTmp, reportDefinedId,originId, ddate);

            if(reportId==null){
                logger.warn("未找到机构{}对应的迁移数据报表",originId);
                continue;
            }

            //各级公式项 缓存

            if(hasCleanedOneDyn.containsKey(unitId)&&hasCleanedOneDyn.get(unitId)){

            }else{
                reportDataMoveDao.removeTreeData(reportId,unitId);
                hasCleanedOneDyn.put(unitId,true);
            }

            ReportUnitCustomerContext unitContext = reportCustomerService.getUnitContext(reportId.toString(), unitId, UnitDefinedType.ONEDIMDYNAMIC.getValue().toString());
            if(unitContext!=null){
                List<SimpleColumDefined> columDefineds = unitContext.getDefinedColums();

                //<definedGroupKey:<columId,level>>
                Map<Integer,Map<String,Integer>> formularDefinedGroupCache = new LinkedHashMap<>();

                for (SimpleColumDefined columDefined : columDefineds) {
                    String columId = columDefined.getColum_id().toString();
                    Integer definedGroupKey = columDefined.getGroup_id();

                    if(!formularDefinedGroupCache.containsKey(definedGroupKey)){
                        Map<String,Integer> columLevelMap = new LinkedHashMap<>();
                        formularDefinedGroupCache.put(definedGroupKey,columLevelMap);
                    }
                    Integer parentId = columDefined.getParent_id();
                    if(parentId==0){
                        formularDefinedGroupCache.get(definedGroupKey).put(columId,0);
                    }else{
                        formularDefinedGroupCache.get(definedGroupKey).put(columId,1);
                    }
                }

                Set<Integer> reportDefinedGroupKeys = formularDefinedGroupCache.keySet();
                int groupIndex = 0;
                for (Integer reportDefinedGroupKey : reportDefinedGroupKeys) {

                    Map<String, Integer> columDefinedLevels = formularDefinedGroupCache.get(reportDefinedGroupKey);

                    Set<String> ridSet = ridListsMap.keySet();
                    Map<String,Integer> reportDefindGroupColumRowNumber = new HashMap<>();
                    Map<String,String> reportGroupDataMap = new HashMap<>();

                    for (String rid : ridSet) {
                        SimpleDateFormat format = new SimpleDateFormat("HHmmSSS");

                        String reportDataGroupId = new StringBuilder().append(format.format(new Date())).append(groupIndex).toString();

                        Integer rowColumIdex = 1;
                        boolean rootLevelDonothing = true;

                        if(reportDefindGroupColumRowNumber.containsKey(rid)){
                            rowColumIdex = reportDefindGroupColumRowNumber.get(rid);
                        }

                        if(reportGroupDataMap.containsKey(rid)){
                            reportDataGroupId = reportGroupDataMap.get(rid);
                        }else{
                            reportGroupDataMap.put(rid,reportDataGroupId);
                        }

                        List<Map<String, Object>> excelRowDatas = ridListsMap.get(rid);//RID相同 RRID不同
                        for (Map<String, Object> excelRowData : excelRowDatas) {
                            Set<String> columDefinedIds = columDefinedLevels.keySet();

                            for (String columDefinedId : columDefinedIds) {

                                Integer dimLevel = columDefinedLevels.get(columDefinedId);
                                if(reportDefindGroupColumRowNumber.containsKey(rid)&&dimLevel==0) {
                                    continue;
                                }else{
                                    Object reportData = 0;

                                    if(dimId2ExcelColumMapTmp.containsKey(columDefinedId)){//与一期有对应关系
                                        String excelColumIndex = dimId2ExcelColumMapTmp.get(columDefinedId);//获取C1 C2 C3等编号
                                        reportData = excelRowData.get(excelColumIndex);
                                    }
                                    else{
                                        if(dimLevel>0){
                                            if(rootLevelDonothing){
                                                continue;
                                            }
                                        }else{
                                            if(columDefinedIds.size()==1){
                                                if(!createOneTime.contains(columDefinedId)){
                                                    createOneTime.add(columDefinedId);
                                                }else{
                                                    continue;
                                                }
                                            }
                                        }

//                                        if(reportDefindGroupColumRowNumber.containsKey(rid)){
//                                            continue;
//                                        }
                                    }

                                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                                    reportCustomerData.setReport_id(reportId);
                                    reportCustomerData.setDimensions_id(columDefinedId);
                                    reportCustomerData.setReport_group_id(reportDataGroupId);
                                    reportCustomerData.setUnit_id(unitId);
                                    reportCustomerData.setReport_data(reportData!=null?String.valueOf(reportData):"0");
                                    if(dimLevel==0){
                                        reportCustomerData.setColum_id(String.valueOf(rowColumIdex));
                                        rootLevelDonothing = false;
                                    }else{
                                        reportCustomerData.setColum_id(String.valueOf(rowColumIdex+1));
                                    }

                                    logger.info("insert into report_customer_data (report_data,report_id,unit_id,report_group_id,colum_id,dimensions_id)" +
                                                    " values ({},{},{},{},{},{})" ,reportCustomerData.getReport_data(),reportCustomerData.getReport_id(),
                                            reportCustomerData.getUnit_id(),reportCustomerData.getReport_group_id(),reportCustomerData.getColum_id(),reportCustomerData.getDimensions_id());

                                    reportDataMoveDao.saveReportCustomerData(reportCustomerData);
                                }



                            }
                            rowColumIdex++;
                            reportDefindGroupColumRowNumber.put(rid,rowColumIdex);
                        }
                    }

                    groupIndex++;
                }


            }
        }


    }

//    @Transactional(rollbackFor = Exception.class)
//    public void oneDImDynamicMove(Map<String, Map<String, List<Map<String, Object>>>> oldTableDataMapTmp,
//                                   Object originId,
//                                   Map<String,Integer> reportIdTmp,
//                                   Map<String, DataMoveConfigGroup> configMapTmp,
//                                   String tableNameTmp,Integer reportDefinedId){
//        String unitId = configMapTmp.get(tableNameTmp).getUnitId();
//        List<SimpleColumDefined> dynamicTreeColums = reportDefinedUnitOneDimService.getColumByUnit(unitId);
//        Map<String,SimpleColumDefined> dynmicTreeColumsMapTmp = new HashMap<>();
//        for (SimpleColumDefined dynamicTreeColum : dynamicTreeColums) {
//            dynmicTreeColumsMapTmp.put(dynamicTreeColum.getColum_id().toString(),dynamicTreeColum);
//        }
//
//        //key:rid value:rid,columid,dims
//        Map<String, DataMovieUnitConfigGroup> dataMoveUnitConfigGroup = configMapTmp.get(tableNameTmp).getDataMovieUnitConfigGroup();
//
//        for (String ddate : oldTableDataMapTmp.keySet()) {
//            Map<String, List<Map<String, Object>>> ridListsMap = oldTableDataMapTmp.get(ddate);
//
//            Integer reportId = getReportId(reportIdTmp, reportDefinedId,originId, ddate);
//
//            if(reportId==null){
//                logger.warn("未找到机构{}对应的迁移数据报表",originId);
//                continue;
//            }
//
////            report_id,unit_id
//
//            if(hasCleanedOneDyn.containsKey(unitId)&&hasCleanedOneDyn.get(unitId)){
//            }else{
//                reportDataMoveDao.removeTreeData(reportId,unitId);
//                hasCleanedOneDyn.put(unitId,true);
//            }
//
//            int ridCount = 1;
//
//            for (String rid : ridListsMap.keySet()) {
//                List<Map<String, Object>> rowDataLists = ridListsMap.get(rid);
//                //key:Cx value:
//                DataMovieUnitConfigGroup dataMoveUnitConfig = dataMoveUnitConfigGroup.get("EMPTY");
//                Map<String, DataMoveRowConfigGroup> oldTableColums = dataMoveUnitConfig.getDataMoveRowConfigGroup();
//
////                ridCount = 0;
//                for (Map<String, Object> rowDataList : rowDataLists) {
//                    Set<String> c_numbers = oldTableColums.keySet();
//                    String rrid = String.valueOf(rowDataList.get("RRID"));
//                    String rridAppend = new StringBuilder().append("R").append(rrid).toString();
//                    boolean crossParent = false;
//                    if(!rid.equals(rridAppend)){
//                        crossParent = true;
//                    }
//
//                    Map<Integer,Integer> reportDataIdTMp = new HashMap<>();
//
//                    for (String c_number : c_numbers) {
//                        DataMoveRowConfigGroup dataMoveRowConfigGroup = oldTableColums.get(c_number);
//                        String dimId = dataMoveRowConfigGroup.getDimId();
//                        String cValue = String.valueOf(rowDataList.get(c_number));
//
//                        ReportCustomerData reportCustomerData = new ReportCustomerData();
//                        reportCustomerData.setReport_id(reportId);
//                        reportCustomerData.setColum_id(String.valueOf(ridCount));
//                        reportCustomerData.setDimensions_id(dimId);
//                        reportCustomerData.setReport_group_id(rid);
//                        reportCustomerData.setUnit_id(unitId);
//                        reportCustomerData.setReport_data(cValue);
//
//                        SimpleColumDefined treeColumDefined = dynmicTreeColumsMapTmp.get(dimId);
//                        if(treeColumDefined.getParent_id() == 0){//
//                            if(crossParent){
//                                continue;
//                            }else{
//                                Integer groupId = treeColumDefined.getGroup_id();
//                                if(reportDataIdTMp.containsKey(groupId)){
//                                    Integer columId = reportDataIdTMp.get(groupId);
//                                    reportCustomerData.setColum_id(columId.toString());
//                                }else{
//                                    reportDataIdTMp.put(groupId,ridCount);
//                                    reportCustomerData.setColum_id(String.valueOf(ridCount));
//                                    ridCount++;
//                                }
//                            }
//
//                        }
//
//                        logger.info("insert into report_customer_data (report_data,report_id,unit_id,colum_id,dimensions_id)" +
//                                        " values ({},{},{},{},{})" ,reportCustomerData.getReport_data(),reportCustomerData.getReport_id(),
//                                reportCustomerData.getUnit_id(),reportCustomerData.getColum_id(),reportCustomerData.getDimensions_id());
//
//                        reportDataMoveDao.saveReportCustomerData(reportCustomerData);
//                    }
//
//                    ridCount++;
//                }
//            }
//        }
//
//
//    }

    private Integer getReportId(Map<String,Integer> reportIdTmp,Integer reportDefinedId,Object originId,String ddate){
        Integer reportId = null;
        String cacheKey = new StringBuilder().append(originId).append("-").append(ddate).toString();
//        if(reportIdTmp.containsKey(cacheKey)){
//            reportId = reportIdTmp.get(cacheKey);
//            logger.info("缓存找到报表id：{}",reportId);
//        }else{
            logger.info("缓存未找到报送基本数据,查询数据库{}",originId);

            ReportCustomer reportCustomer = reportDataMoveDao.getOriginReportCustomerByReportDefined(reportDefinedId,originId);
            if(reportCustomer==null){
                logger.error("未找到当前机构{}对应的报表数据",originId);
                return null;
            }

            reportId = reportCustomer.getReport_id();
            reportIdTmp.put(cacheKey,reportId);
            logger.info("查询数据库报表id完成：{}",reportId);

//        }

        return reportId;
    }


}
