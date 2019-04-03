package com.seaboxdata.cqny.record.service.imp;

import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportStatementsService;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void doSubmit(String reportDefinedId) {
        logger.info("报表发布->{}：获取报表定义中",reportDefinedId);
        StatementsEntity reportDefined = getReportDefined(reportDefinedId);
        logger.info("报表发布->{}：报表定义数据获取成功=>{}",reportDefinedId,reportDefined);
        if(reportDefined==null){
            return;
        }
        logger.info("报表发布->{}：获取报表对应机构列表",reportDefinedId);
        List<String> alOrigin = getAllOrigin(reportDefinedId);
        logger.info("报表发布->{}：机构列表获取成功=>{}",reportDefinedId,alOrigin);
        logger.info("报表发布->{}：生成报表基础信息",reportDefinedId);
        List<Integer> reportIds = createReportBaseData(reportDefined, alOrigin);
        logger.info("报表发布->{}：报表基础信息生成完毕，生成的报表id列别为=>{}",reportDefinedId,reportIds);
        logger.info("报表发布->{}：生成报表缺省数据中",reportDefinedId);
        createReportDefaultData(reportDefined,reportIds);
    }

    /**
     * 获取报表定义
     * @param reportDefinedId
     * @return
     */
    private StatementsEntity getReportDefined(String reportDefinedId){
        StatementsEntity reportDefined = reportStatementsService.getReportDefinedById(new Integer(reportDefinedId));
        List<UnitEntity> unitList = reportUnitService.getUnitDefinedByReportDefindId(reportDefinedId);
        if(unitList!=null){
            for (UnitEntity unitEntity : unitList) {
                List columList = reportUnitService.getDefinedColums(unitEntity.getUnit_id().toString(), unitEntity.getUnit_type().toString());
                unitEntity.setColums(columList);
            }
        }
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
    private List<Integer> createReportBaseData(StatementsEntity reportDefined, List<String> allOrigin){
        List<Integer> reportBaseIds = new ArrayList<>();
        ReportCustomer reportCustomer = new ReportCustomer();
        reportCustomer.setReport_defined_id(reportDefined.getDefined_id());
        reportCustomer.setReport_name(reportDefined.getDefined_name());
        reportCustomer.setReport_origin(0);
        reportCustomer.setCreate_date(new Date());
        for (String origin : allOrigin) {
            reportCustomer.setReport_origin(new Integer(origin));
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
    private void createReportDefaultData(StatementsEntity reportDefined,List<Integer> reportIds){
        List<UnitEntity> unitDefinds = reportDefined.getUnits();
        for (UnitEntity unitDefind : unitDefinds) {
            Integer unitTypeInt = unitDefind.getUnit_type();
            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)){//一维静态
                logger.info("报表发布->{}：报送单元【{}】为【一维报送】单元,生成缺省数据中",reportDefined.getDefined_id(),unitDefind.getUnit_name());
                ArrayList<SimpleColumDefined> oneColumDefinedsList = (ArrayList<SimpleColumDefined>) unitDefind.getColums();
                ArrayList<ReportCustomerData> dataList = createOneDimDatas(oneColumDefinedsList, reportIds);
                logger.info("报表发布->{}：报送单元【{}】缺省数据生成完毕=>{}",reportDefined.getDefined_id(),unitDefind.getUnit_name(),dataList);
            }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)){//一维动态

            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)){//多维静态

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

    /**
     * 生成一维报表数据
     * @param columDefineds
     * @param reportIds
     */
    private ArrayList<ReportCustomerData> createOneDimDatas(ArrayList<SimpleColumDefined> columDefineds, List<Integer> reportIds){
        ArrayList<ReportCustomerData> dataList = new ArrayList<>();
        if(columDefineds!=null){
            for (SimpleColumDefined columDefined : columDefineds) {
                for (Integer reportId : reportIds) {
                    ReportCustomerData reportCustomerData = new ReportCustomerData();
                    reportCustomerData.setColum_id(columDefined.getColum_id().toString());
                    reportCustomerData.setUnit_id(columDefined.getUnit_id().toString());
                    reportCustomerData.setReport_id(reportId);
                    reportCustomerData.setReport_data("1");
                    dataList.add(reportCustomerData);
                }

            }
        }
        reportCustomerService.updateOrInsertSimpleUnitContext(columDefineds,dataList,false);
        return dataList;
    }

    /**
     * 生成多维树状结构报表数据
     * @param columDefineds
     * @param reportIds
     */
    private ArrayList<ReportCustomerData> createTreeDimDatas(ArrayList<SimpleColumDefined> columDefineds, List<Integer> reportIds){
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        String reportGroupId = format.format(new Date());
        ArrayList<Map<String, Object>> dimTree = makeDimTree(columDefineds, 0);

        ArrayList<ReportCustomerData> dataList = new ArrayList<>();
        for (Integer reportId : reportIds) {
            dataList.addAll(this.makeTreeDatas(dimTree, reportId, reportGroupId, 0));
        }

        reportCustomerService.updateOrInsertSimpleUnitContext(columDefineds,dataList,false);
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
            reportCustomerData.setReport_data("1");
            dataList.add(reportCustomerData);
            ArrayList<Map<String, Object>> children = (ArrayList<Map<String, Object>>) stringObjectMap.get("children");
            dataList.addAll(this.makeTreeDatas(children,reportId,reportGroupId,offset));
        }
        return dataList;
    }
}
