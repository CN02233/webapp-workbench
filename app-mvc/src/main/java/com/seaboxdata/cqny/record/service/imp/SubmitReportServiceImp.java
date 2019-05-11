package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.service.*;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("submitReportService")
public class SubmitReportServiceImp implements SubmitReportService {

    private static Logger logger = LoggerFactory.getLogger(SubmitReportServiceImp.class);

    @Autowired
    private ReportStatementsService reportStatementsService;

    @Autowired
    private ReportUnitService reportUnitService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private OriginService originService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSubmit(SubmitReportRequestEntity submitReportEntity) throws ParseException {
        String reportDefinedId = submitReportEntity.getDefined_id();
        try {
            logger.info("报表发布->{}：获取报表定义中",reportDefinedId);
            ReportDefinedEntity reportDefined = getReportDefined(reportDefinedId);
            logger.info("报表发布->{}：报表定义数据获取成功=>{}",reportDefinedId,reportDefined);
            if(reportDefined==null){
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            reportDefined.setReport_start_date(format.parse(submitReportEntity.getReport_start_date()));
            reportDefined.setReport_end_date(format.parse(submitReportEntity.getReport_end_date()));
            reportStatementsService.updateReportDefined(reportDefined);

            logger.info("报表发布->{}：获取报表对应机构列表",reportDefinedId);
            List<String> alOrigin = getAllOrigin(reportDefinedId);
            logger.info("报表发布->{}：机构列表获取成功=>{}",reportDefinedId,alOrigin);
            logger.info("报表发布->{}：过滤省市机构,过滤前机构数量{}",reportDefinedId,alOrigin.size());
            alOrigin = this.filterOrigins(alOrigin);
            logger.info("报表发布->{}：过滤后剩余机构数量{},分别为{}",reportDefinedId,alOrigin.size(),alOrigin);
            logger.info("报表发布->{}：生成报表基础信息",reportDefinedId);
            List<Integer> reportIds = createReportBaseData(reportDefined, alOrigin,submitReportEntity);
            logger.info("报表发布->{}：报表基础信息生成完毕，生成的报表id列别为=>{}",reportDefinedId,reportIds);
            logger.info("报表发布->{}：生成报表缺省数据中",reportDefinedId);
            createReportDefaultData(reportDefined,reportIds);

            logger.info("更新报表状态为发布完成-->{}",reportDefinedId);
            reportStatementsService.changeDeindStatus(reportDefinedId, ReportDefinedStatus.SUBMIT);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {

        }


    }

    /**
     * 获取报表定义
     * @param reportDefinedId
     * @return
     */
    private ReportDefinedEntity getReportDefined(String reportDefinedId){
        ReportDefinedEntity reportDefined = reportStatementsService.getReportDefinedById(new Integer(reportDefinedId));
        List<UnitDefined> unitList = reportUnitService.getUnitDefinedByReportDefindId(reportDefinedId);
        if(unitList!=null){
            for (UnitDefined unitEntity : unitList) {
                List columList = reportUnitService.getDefinedColums(unitEntity.getUnit_id().toString(), unitEntity.getUnit_type().toString());
                unitEntity.setColums(columList);
            }
        }
        unitList.sort(new Comparator<UnitDefined>() {
            @Override
            public int compare(UnitDefined o1, UnitDefined o2) {
                Integer o1Order = o1.getUnit_order();
                Integer o2Order = o2.getUnit_order();
                return o1Order-o2Order;
            }
        });

        reportDefined.setUnits(unitList);
//        reportDefined.setUnits(unitList);
        return reportDefined;
    }

    private List<String> getAllOrigin(String reportDefindId){
        List<String> originList = reportStatementsService.getDefinedAndOriginAssignById(reportDefindId);
        return originList;
    }

    /**
     * 创建报表基本信息
     * @param reportDefined
     * @param allOrigin
     * @return
     */
    private List<Integer> createReportBaseData(ReportDefinedEntity reportDefined, List<String> allOrigin, SubmitReportRequestEntity submitReportEntity) throws ParseException {
        List<Integer> reportBaseIds = new ArrayList<>();
//        List<String> passAuthList = submitReportEntity.getCheck_origins();
        List<String> approvePassAuth = submitReportEntity.getApprove_check_origins();
        List<String> reviewPassAuth = submitReportEntity.getReview_check_origins();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        ReportCustomer reportCustomer = new ReportCustomer();
        reportCustomer.setReport_defined_id(reportDefined.getDefined_id());
        reportCustomer.setReport_name(reportDefined.getDefined_name());
        reportCustomer.setReport_origin(0);
        reportCustomer.setCreate_date(new Date());
        reportCustomer.setReport_status(ReportStatus.NORMAL.getValue());

        reportCustomer.setReport_data_start(format.parse(submitReportEntity.getReport_data_start()));
        reportCustomer.setReport_data_end(format.parse(submitReportEntity.getReport_data_end()));

        for (String origin : allOrigin) {
            reportCustomer.setReport_origin(new Integer(origin));
            reportCustomer.setActive_unit(reportDefined.getUnits().get(0).getUnit_id());
            reportCustomer.setReport_start_date(format.parse(submitReportEntity.getReport_start_date()));
            reportCustomer.setReport_end_date(format.parse(submitReportEntity.getReport_end_date()));
            
            
            if(approvePassAuth!=null&&approvePassAuth.contains(origin)){
                reportCustomer.setPass_approve("Y");
            }else{
                reportCustomer.setPass_approve("N");
            }

            if(reviewPassAuth!=null&&reviewPassAuth.contains(origin)){
                reportCustomer.setPass_review("Y");
            }else{
                reportCustomer.setPass_review("N");
            }

            reportCustomerService.createReportCustomer(reportCustomer);
            reportBaseIds.add(reportCustomer.getReport_id());
        }
        return reportBaseIds;

//        reportCustomer.setReport_end_date(de);
    }

    /**
     * 生成报表数据
     * @param reportDefined
     * @param reportIds
     */
    private void createReportDefaultData(ReportDefinedEntity reportDefined, List<Integer> reportIds){
        List<UnitDefined> unitDefinds = reportDefined.getUnits();
        for (UnitDefined unitDefind : unitDefinds) {
            Integer unitTypeInt = unitDefind.getUnit_type();
            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)){//一维静态
                logger.info("报表发布->{}：报送单元【{}】为【一维报送】单元,生成缺省数据中",reportDefined.getDefined_id(),unitDefind.getUnit_name());
                ArrayList<SimpleColumDefined> oneColumDefinedsList = (ArrayList<SimpleColumDefined>) unitDefind.getColums();
                ArrayList<ReportCustomerData> dataList = createOneDimDatas(oneColumDefinedsList, reportIds);
                logger.info("报表发布->{}：报送单元【{}】缺省数据生成完毕=>{}",reportDefined.getDefined_id(),unitDefind.getUnit_name(),dataList);
            }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)){//一维动态
                ArrayList<SimpleColumDefined> oneColumDefinedsList = (ArrayList<SimpleColumDefined>) unitDefind.getColums();
                ArrayList<ReportCustomerData> dataList = createOneDimDynDatas(oneColumDefinedsList, reportIds);
                logger.info("报表发布->{}：报送单元【{}】缺省数据生成完毕=>{}",reportDefined.getDefined_id(),unitDefind.getUnit_name(),dataList);
            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)){//多维静态
                logger.info("报表发布->{}：报送单元【{}】为【多维静态】报送单元,生成缺省数据中",reportDefined.getDefined_id(),unitDefind.getUnit_name());
                ArrayList<GridColumDefined> gridColumDefinedsList = (ArrayList<GridColumDefined>) unitDefind.getColums();
                ArrayList<ReportCustomerData> dataList = createGridDimDatas(gridColumDefinedsList,reportIds);

                logger.info("报表发布->{}：报送单元【{}】缺省数据生成完毕=>{}",reportDefined.getDefined_id(),unitDefind.getUnit_name(),dataList);

            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitTypeInt)){//多维动态树
                logger.info("报表发布->{}：报送单元【{}】为【多维树状】报送单元,生成缺省数据中",reportDefined.getDefined_id(),unitDefind.getUnit_name());
                ArrayList<SimpleColumDefined> oneColumDefinedsList = (ArrayList<SimpleColumDefined>) unitDefind.getColums();
                ArrayList<ReportCustomerData> dataList = createTreeDimDatas(oneColumDefinedsList,reportIds);
                logger.info("报表发布->{}：报送单元【{}】缺省数据生成完毕=>{}",reportDefined.getDefined_id(),unitDefind.getUnit_name(),dataList);
            }else{
                logger.info("报表发布->{}：报送单元【{}】,生成失败，未找到对应的报送单元类型{}",unitTypeInt);
            }
        }
    }

    private ArrayList<ReportCustomerData> createGridDimDatas(ArrayList<GridColumDefined> gridColumDefinedsList, List<Integer> reportIds) {
        ArrayList<ReportCustomerData> columDatas = new ArrayList<>();
        if(gridColumDefinedsList!=null){
            SimpleDateFormat format = new SimpleDateFormat("HHmmss");
            String reportGroupId = format.format(new Date());

            for (GridColumDefined columDefined : gridColumDefinedsList) {
                for (Integer reportId : reportIds) {
                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setColum_id(columDefined.getColum_id().toString());
                    reportCustomerData.setDimensions_id(columDefined.getDim_id().toString());
                    reportCustomerData.setUnit_id(columDefined.getUnit_id().toString());
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setReport_data(columDefined.getDefault_value()!=null?columDefined.getDefault_value():"0");
                    reportCustomerData.setReport_group_id(reportGroupId);
                    columDatas.add(reportCustomerData);
                }

            }
        }
        return saveColumDatas(columDatas);
    }

    /**
     * 生成一维报表数据
     * @param columDefineds
     * @param reportIds
     */
    private ArrayList<ReportCustomerData> createOneDimDatas(ArrayList<SimpleColumDefined> columDefineds, List<Integer> reportIds){
        ArrayList<ReportCustomerData> columDatas = new ArrayList<>();
        if(columDefineds!=null){
            for (SimpleColumDefined columDefined : columDefineds) {
                for (Integer reportId : reportIds) {
                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setColum_id(columDefined.getColum_id().toString());
                    reportCustomerData.setUnit_id(columDefined.getUnit_id().toString());
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setReport_data(Strings.isNullOrEmpty(columDefined.getDefault_value())?"0":columDefined.getDefault_value());
                    reportCustomerData.setColum_order(columDefined.getColum_order());
                    columDatas.add(reportCustomerData);
                }

            }
        }
        return saveColumDatas(columDatas);
    }

    private ArrayList<ReportCustomerData> createOneDimDynDatas(ArrayList<SimpleColumDefined> columDefineds, List<Integer> reportIds){
        ArrayList<ReportCustomerData> columDatas = new ArrayList<>();
        if(columDefineds!=null){
            for (SimpleColumDefined columDefined : columDefineds) {
                for (Integer reportId : reportIds) {
                    Integer groupId = columDefined.getGroup_id();
                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setColum_id(columDefined.getColum_id().toString());
                    reportCustomerData.setDimensions_id("0");
                    if(columDefined.getGroup_id()!=null){
                        reportCustomerData.setReport_group_id(String.valueOf(columDefined.getGroup_id()));
                    }
                    reportCustomerData.setUnit_id(columDefined.getUnit_id().toString());
                    if(groupId!=null){
                        String defaultValue = columDefined.getDefault_value();
                        reportCustomerData.setReport_data(Strings.isNullOrEmpty(defaultValue)?"0":defaultValue);
                    }else{
                        reportCustomerData.setReport_data(columDefined.getColum_name_cn());
                    }
                    columDatas.add(reportCustomerData);
                }//

            }
        }

        return saveColumDatas(columDatas);
    }

    /**
     * 生成多维树状结构报表数据
     * @param columDefineds
     * @param reportIds
     */
    private ArrayList<ReportCustomerData> createTreeDimDatas(ArrayList<SimpleColumDefined> columDefineds, List<Integer> reportIds){
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        String reportFormatDate = format.format(new Date());

        Map<Integer,ArrayList<SimpleColumDefined>> columDefinedGroup = new HashMap<>();
        for (SimpleColumDefined columDefined : columDefineds) {
            Integer columDefinedGroupId = columDefined.getGroup_id();
            if(!columDefinedGroup.containsKey(columDefinedGroupId)){
                columDefinedGroup.put(columDefinedGroupId,new ArrayList<SimpleColumDefined>());
            }
            columDefinedGroup.get(columDefinedGroupId).add(columDefined);
        }
        ArrayList<ReportCustomerData> dataList = new ArrayList<>();

        int groupNum = 0;
        for (Integer columDefinedGroupKey : columDefinedGroup.keySet()) {
            ArrayList<SimpleColumDefined> columDefinedDatas = columDefinedGroup.get(columDefinedGroupKey);
            ArrayList<Map<String, Object>> dimTree = makeDimTree(columDefinedDatas, 0);
            for (Integer reportId : reportIds) {
                dataList.addAll(this.makeTreeDatas(dimTree, reportId,
                        new StringBuilder().append(reportFormatDate).append(groupNum).toString(), 0));
            }
            groupNum++;
        }

        return saveColumDatas(dataList);
    }

    private ArrayList<ReportCustomerData> saveColumDatas(ArrayList<ReportCustomerData> dataList) {
        if(dataList!=null&&dataList.size()>0){
            for (ReportCustomerData columData : dataList) {
                reportCustomerDao.insertUnitContext(columData);
            }
        }
        return dataList;
    }

    /**
     * 格式化树状结构
     * @param columDefineds
     * @param parentId
     * @return
     */
    private ArrayList<Map<String, Object>> makeDimTree(ArrayList<SimpleColumDefined> columDefineds, Integer parentId){
        ArrayList<Map<String,Object>> treeList = new ArrayList<>();
        for (SimpleColumDefined columDefined : columDefineds) {
            Integer checkColumId = columDefined.getColum_id();
            Integer checkParentId = columDefined.getParent_id();
            if(checkParentId.equals(parentId)){
                Map<String,Object> treeMap = new HashMap<>();
                treeMap.put("data",columDefined);
                treeMap.put("children",this.makeDimTree(columDefineds,checkColumId));
                treeList.add(treeMap);
            }
        }

        return treeList;
    }

    private ArrayList<ReportCustomerData> makeTreeDatas(ArrayList<Map<String, Object>> dimTree,Integer reportId,String reportGroupId,int offset){
        ArrayList<ReportCustomerData> dataList = new ArrayList<>();

        offset++;
        for (Map<String, Object> stringObjectMap : dimTree) {
            SimpleColumDefined simpleColumDefined = (SimpleColumDefined) stringObjectMap.get("data");
            ReportCustomerData reportCustomerData = new ReportCustomerData();
            reportCustomerData.setColum_id(String.valueOf(offset));
            reportCustomerData.setDimensions_id(simpleColumDefined.getColum_id().toString());
            reportCustomerData.setUnit_id(simpleColumDefined.getUnit_id().toString());
            reportCustomerData.setReport_id(reportId);
            reportCustomerData.setReport_group_id(reportGroupId);
            String defaultValue = simpleColumDefined.getDefault_value();
            if(Strings.isNullOrEmpty(defaultValue)){
                defaultValue = "0";
            }
            reportCustomerData.setReport_data(defaultValue);
            dataList.add(reportCustomerData);
            ArrayList<Map<String, Object>> children = (ArrayList<Map<String, Object>>) stringObjectMap.get("children");
            dataList.addAll(this.makeTreeDatas(children,reportId,reportGroupId,offset));
        }
        return dataList;
    }

    private List<String> filterOrigins(List<String> alOrigin){
        List<Origin> allReportOrigins = originService.listAllOrigin();
        Map<Integer,Origin> reportOriginTmp = new HashMap<>();
        for (Origin allReportOrigin : allReportOrigins) {
            reportOriginTmp.put(allReportOrigin.getOrigin_id(),allReportOrigin);
        }
        List<String> filterOriginIds = new ArrayList<>();
        for(Origin allReportOrigin : allReportOrigins){
            Integer originId = allReportOrigin.getOrigin_id();
            if(originId==1){//全国 过滤
                filterOriginIds.add(allReportOrigin.getOrigin_id().toString());
            }else if(allReportOrigin.getParent_origin_id()==1){//省 过滤
                filterOriginIds.add(allReportOrigin.getOrigin_id().toString());
            }else if(reportOriginTmp.get(allReportOrigin.getParent_origin_id()).getParent_origin_id()==1){//市 过滤
                filterOriginIds.add(allReportOrigin.getOrigin_id().toString());
            }
        }

        List<String> finalOrigin = new ArrayList();

        for (String reportOrigin : alOrigin) {
            if(!filterOriginIds.contains(reportOrigin)){
                finalOrigin.add(reportOrigin);
            }
        }

        return finalOrigin;
    }
}
