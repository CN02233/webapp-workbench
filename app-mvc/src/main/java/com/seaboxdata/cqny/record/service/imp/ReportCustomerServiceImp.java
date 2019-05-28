package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitMultDimDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.entity.treedim.TreeUnitContext;
import com.seaboxdata.cqny.record.service.*;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service("reportCustomerService")
public class ReportCustomerServiceImp implements ReportCustomerService {

    private Logger logger = LoggerFactory.getLogger(ReportCustomerServiceImp.class);

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private ReportUnitService reportDefinedUnitService;

    @Autowired
    private FomularService fomularService;

    @Autowired
    private RememberCustDataService rememberCustDataService;

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    @Autowired
    private IReportDefinedUnitMultDimDao reportDefinedUnitMultDimDao;

    @Override
    public PageResult pagerReport(Integer currPage, Integer pageSize,  List<Integer> originIds) {
        Page<ReportCustomer> pagerData = reportCustomerDao.pageReportByOrigins(currPage,pageSize,originIds);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pagerData);
        Date currDate = new Date();
        List<ReportCustomer> dataList = pageResult.getDataList();
        for (ReportCustomer reportCustomer : dataList) {
            Date startDate = reportCustomer.getReport_start_date();
            Date endDate = reportCustomer.getReport_end_date();
            if(currDate.compareTo(startDate)<0){//未到填报日期
                reportCustomer.setReport_status(ReportStatus.TOO_EARLY.getValue());
            }
            if(currDate.compareTo(endDate)>0){//已过期
                reportCustomer.setReport_status(ReportStatus.OVER_TIME.getValue());
                reportCustomerDao.updateReportCustomer(reportCustomer);
            }
        }

        return pageResult;
//        return null;
    }


    /**
     * 获取报送报表信息：报表定义信息、报表填写信息
     */
    public ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType){
        List definedColums = reportDefinedUnitService.getDefinedColums(unitId, unitType);
        List<ReportCustomerData> columDatas = reportCustomerDao.getColumDatas(reportId,unitId);

        if(columDatas!=null&&columDatas.size()>0){
            for (ReportCustomerData columData : columDatas) {
                if(!Strings.isNullOrEmpty(columData.getReport_data())){
                    if(columData.getReport_data().replace(".","_").indexOf("_")>0){
                        BigDecimal bigData = new BigDecimal(columData.getReport_data());
                        bigData = bigData.setScale(2, RoundingMode.HALF_UP);
                        columData.setReport_data(bigData.toString());
                    }
                }
            }
        }

        ReportUnitCustomerContext reportUnitCustomerContext = new ReportUnitCustomerContext();
        reportUnitCustomerContext.setDefinedColums(definedColums);
        reportUnitCustomerContext.setColumDatas(columDatas);
        return reportUnitCustomerContext;
    }

    @Override
    public void createReportCustomer(ReportCustomer reportCustomer) {
        reportCustomerDao.createReportCustomer(reportCustomer);
    }

    /**
     * 检查本次输入项是公式项还是用户输入项
     * @param simpleColumDefineds
     * @param columDatas
     * @return Map.key = custDataArray 用户输入项列表,Map.value = List<ReportCustomerData>
     *         Map.key = fomularArray 公式项列表,Map.value = List<FomularTmpEntity>
     */
    public Map<String,Object> checkCustOrFomular(ArrayList<SimpleColumDefined> simpleColumDefineds,
                                                 ArrayList<ReportCustomerData> columDatas){

        List<ReportCustomerData> custDataArray = new ArrayList<>();
        List<FomularTmpEntity> fomularArray = new ArrayList();

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("custDataArray",custDataArray);
        resultMap.put("fomularArray",fomularArray);

        Map<String,SimpleColumDefined> fomularsTmp = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            for (SimpleColumDefined simpleColumDefined : simpleColumDefineds) {
                Integer columType = new Integer(simpleColumDefined.getColum_type());
                if(ColumType.FORMULA.compareWith(columType)){
                    fomularsTmp.put(simpleColumDefined.getUnit_id()+"_"+simpleColumDefined.getColum_id(),simpleColumDefined);
                }
            }
        }


        if(columDatas!=null&&columDatas.size()>0){
            for (ReportCustomerData columData : columDatas) {
                Integer reportId = columData.getReport_id();
                String unitId = columData.getUnit_id();
                String columnId = columData.getColum_id();
                String dimensionsId = columData.getDimensions_id();
                //unitId+"_"+columnId: 一维静态公式刷新 unitId+"_"+dimensionsId:多维树状公式刷新
                if(fomularsTmp.containsKey(unitId+"_"+columnId)||fomularsTmp.containsKey(unitId+"_"+dimensionsId)){

                    FomularTmpEntity fomularTmpEntity = new FomularTmpEntity();
                    fomularTmpEntity.setReportId(reportId);
                    fomularTmpEntity.setUnitId(unitId);
                    fomularTmpEntity.setColumId(columnId);
                    fomularTmpEntity.setDimensionsId(dimensionsId);
                    fomularTmpEntity.setReportGroupId(columData.getReport_group_id());
                    fomularTmpEntity.setOld_report_data(columData.getReport_data());

                    String fomularScriptVal = fomularsTmp.get(unitId + "_" + dimensionsId) != null ?
                            fomularsTmp.get(unitId + "_" + dimensionsId).getColum_formula() :
                            fomularsTmp.get(unitId + "_" + columnId).getColum_formula();
                    fomularTmpEntity.setFomularScript(fomularScriptVal);
                    fomularArray.add(fomularTmpEntity);
                } else{//无公式值刷新
                    custDataArray.add(columData);
                }
            }
        }

        return resultMap;
    }

    public Map<String,Object> checkCustOrFomularByGridDim(ArrayList<GridColumDefined> simpleColumDefineds,
                                                 ArrayList<ReportCustomerData> columDatas){

        List<ReportCustomerData> custDataArray = new ArrayList<>();
        List<FomularTmpEntity> fomularArray = new ArrayList();

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("custDataArray",custDataArray);
        resultMap.put("fomularArray",fomularArray);

        Map<String,GridColumDefined> fomularsTmp = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            simpleColumDefineds.forEach(simpleColumDefined->{
                Integer columType = new Integer(simpleColumDefined.getColum_type());
                if(ColumType.FORMULA.compareWith(columType)){
                    fomularsTmp.put(simpleColumDefined.getUnit_id()+"_"+simpleColumDefined.getColum_id()+"_"+simpleColumDefined.getDim_id(),simpleColumDefined);
                }
            });
        }

        if(columDatas!=null&&columDatas.size()>0){
            columDatas.forEach(columData->{
                Integer reportId = columData.getReport_id();
                String unitId = columData.getUnit_id();
                String columnId = columData.getColum_id();
                String dimensionsId = columData.getDimensions_id();
                //unitId+"_"+columnId: 一维静态公式刷新 unitId+"_"+dimensionsId:多维树状公式刷新
                String key = unitId+"_"+columnId+"_"+dimensionsId;
                if(fomularsTmp.containsKey(key)){

                    FomularTmpEntity fomularTmpEntity = new FomularTmpEntity();
                    fomularTmpEntity.setReportId(reportId);
                    fomularTmpEntity.setUnitId(unitId);
                    fomularTmpEntity.setColumId(columnId);
                    fomularTmpEntity.setDimensionsId(dimensionsId);
                    fomularTmpEntity.setReportGroupId(columData.getReport_group_id());

                    String fomularScriptVal = fomularsTmp.get(key).getColum_formula();
                    fomularTmpEntity.setFomularScript(fomularScriptVal);
                    fomularArray.add(fomularTmpEntity);
                } else{//无公式值刷新
                    custDataArray.add(columData);
                }
            });
        }
        //倒序
        Collections.reverse(fomularArray);

        return resultMap;
    }
    /**
     *
     * @param simpleColumDefineds 输入项的定义
     * @param columDatas 用户录入数据集合
     * @param isUpdate true:更新原数据值 false：插入新数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrInsertSimpleUnitContext(
            ArrayList<SimpleColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,
            boolean isUpdate) {

        Map<String, Object> custOrFomular = checkCustOrFomular(simpleColumDefineds, columDatas);

        List<ReportCustomerData> custDataArray = (List<ReportCustomerData>) custOrFomular.get("custDataArray");
        List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) custOrFomular.get("fomularArray");


        for (ReportCustomerData columData : custDataArray) {
            if(isUpdate){
                logger.info("{}",columData);
                List<ReportCustomerData> reportCustData =
                        reportCustomerDao.getColumDatas(columData.getReport_id().toString(), columData.getUnit_id().toString());
                reportCustomerDao.updateUnitContext(columData);
            }else{
                reportCustomerDao.insertUnitContext(columData);
            }
        }

//        if(Strings.isNullOrEmpty(saveType)){
//            if("CUST".equals(saveType)){
//                //保存用户录入项内容
//
//            }else {
//                if(fomularArray!=null&&fomularArray.size()>0){
//                    for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                        ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                        if(isUpdate){
//                            reportCustomerDao.updateUnitContext(comularData);
//                        }else{
//                            reportCustomerDao.insertUnitContext(comularData);
//                        }
//                    }
//                }
//            }
//        }

        //刷新公式项内容


        //检查用户输入项是否被其他公式关联，并刷新关联公式的内容
//        User currUser = SessionSupport.checkoutUserFromSession();
//        new Thread(new Runnable(){
//            public void run(){
//                logger.info("刷新关联到用户输入项的公式值");
//                List<ReportCustomerData> needRefresDatas = fomularService.refreshFomularForCustInput(custDataArray);
//                logger.info("需要刷新的内容{}",needRefresDatas);
//                if(needRefresDatas!=null){
//                    for (ReportCustomerData needRefresData : needRefresDatas) {
//                        reportCustomerDao.updateUnitContext(needRefresData);
//                    }
//                }
//
//                logger.info("查看用户录入项是否需要自动记忆，如需要，将用户输入数据保存");
//                rememberCustDataService.rememberCustData(simpleColumDefineds,columDatas,currUser.getUser_id());
//                logger.info("用户录入记忆完成");
//            }
//        }).start();

    }


    @Override
    public ReportCustomer checkReportCustomer(String reportId) {
        ReportCustomer reportCustomer = reportCustomerDao.checkReportCustomer(reportId);
        return reportCustomer;
    }

    @Override
    public Integer checkNextStepUnitId(String reportId) {
        ReportCustomer reportCustomer = reportCustomerDao.checkReportCustomer(reportId);
        Integer currUnitId = reportCustomer.getActive_unit();
        Integer currUnitOrder = null;
        Integer nexyCurrUnitId = null;
        List<UnitDefined> allUnit = reportCustomer.getUnitEntities();
        boolean nextCheckOut = false;
        for (UnitDefined unitEntity : allUnit) {
             if(unitEntity.getUnit_id().equals(currUnitId)){
                nextCheckOut = true;
                 currUnitOrder = unitEntity.getUnit_order();
                continue;
            }
            if(nextCheckOut){
                nexyCurrUnitId = unitEntity.getUnit_id();
                break;
            }
        }

        if(nexyCurrUnitId==null){
            if(currUnitOrder.equals(allUnit.size())){
                return Integer.MAX_VALUE;
            }
        }

        return nexyCurrUnitId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStep(String reportId) {
        Integer nextStepUnit = this.checkNextStepUnitId(reportId);
        if(nextStepUnit.equals(Integer.MAX_VALUE)){
            nextStepUnit = 0;
            this.updateReportCustomerStatus(reportId,ReportStatus.REPORT_DONE);
        }
        reportCustomerDao.updateStep(reportId,nextStepUnit);
    }

    @Override
    public void updateReportCustomerStatus(String reportId, ReportStatus reportStatus){
        reportCustomerDao.updateReportCustomerStatus(reportId,reportStatus.getValue());
    }

    @Override
    public Map<ReportStatus, Integer> getReportInfos(Integer userOriginId) {
        List<ReportCustomer> reportList = reportCustomerDao.getAllReportInfoByOrigin(userOriginId);

        Map<ReportStatus,Integer> reportStatusCountMap = new HashMap<>();

        ReportStatus[] reportStatusArray = ReportStatus.values();
        for (ReportStatus status : reportStatusArray) {
            reportStatusCountMap.put(status,0);
        }

        if(reportList!=null){
            for (ReportCustomer reportCustomer : reportList) {
                String reportStatus = reportCustomer.getReport_status();
                ReportStatus statusEnum = ReportStatus.getReportStatus(reportStatus);
                Integer countVal = reportStatusCountMap.get(statusEnum);
                reportStatusCountMap.put(statusEnum,countVal+1);
            }
        }

        return reportStatusCountMap;
    }

    @Override
    public PageResult getChildrenReportInfos(Integer currPage, Integer pageSize, List<Integer> origins ) {
        if(origins!=null&&origins.size()>0){

        }else {
            PageResult pagerResult = new PageResult();
            pagerResult.setCurrPage(currPage);
            pagerResult.setPageSize(pageSize);
            List<Map<String,Object>> resultList = new ArrayList<>();
            pagerResult.setDataList(resultList);
            return pagerResult;
        }

        List<ReportCustomer> allReportsInfo = reportCustomerDao.getAllReportInfoByOrigins(origins);

        Map<String,HashMap<String,Object>> reportOriginStatsCountMap = new HashMap<>();
        Map<String,HashMap<String,Object>> responseMap = new HashMap<>();

        HashMap<String,Object> reportStatusCountMap = new HashMap<>();

        ReportStatus[] reportStatusArray = ReportStatus.values();
        for (ReportStatus status : reportStatusArray) {
            reportStatusCountMap.put(status.toString(),new Integer(0));
        }

        Integer startIndex = (currPage-1)*pageSize;
        Integer endIndex = startIndex+(pageSize-1);

        if(allReportsInfo!=null){
            int tmpCount = 0;
            for (ReportCustomer reportCustomer : allReportsInfo) {
                String reportOrigin = reportCustomer.getReport_origin_name();
                if(!reportOriginStatsCountMap.containsKey(reportOrigin)){
                    reportOriginStatsCountMap.put(reportOrigin, (HashMap<String, Object>) reportStatusCountMap.clone());
                    if(tmpCount>=startIndex&&tmpCount<=endIndex){
                        responseMap.put(reportOrigin,reportOriginStatsCountMap.get(reportOrigin));
                    }
                    tmpCount++;

                }


                HashMap<String, Object> reportStatusCountMapClone = reportOriginStatsCountMap.get(reportOrigin);

                String reportStatus = reportCustomer.getReport_status();
                ReportStatus statusEnum = ReportStatus.getReportStatus(reportStatus);
                Integer countVal = (Integer) reportStatusCountMapClone.get(statusEnum.toString());
                reportStatusCountMapClone.put(statusEnum.toString(),countVal+1);
            }
        }

        PageResult pagerResult = new PageResult();
        pagerResult.setCurrPage(currPage);
        pagerResult.setPageSize(pageSize);
        float pageTotal = (float)reportOriginStatsCountMap.size()/pageSize;
        pagerResult.setTotalPage(new Double(Math.ceil(pageTotal)).intValue());
        pagerResult.setTotalNum(reportOriginStatsCountMap.size());
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (String originName : responseMap.keySet()) {
            responseMap.get(originName).put("origin_name",originName);
            resultList.add(responseMap.get(originName));
        }
        pagerResult.setDataList(resultList);

        return pagerResult;
    }

    @Override
    public void refreshFomular(String reportDefindId,String reportId){
        fomularService.refreshFomularDatas(reportDefindId,reportId);
    }

//    public void refreshFomular(String reportDefindId,String reportId) {
//        List<UnitDefined> reportUnits = reportCustomerDao.getAllUnitEntityByReportId(reportDefindId);
//        for (UnitDefined reportUnit : reportUnits) {
//            Integer unitTypeInt = reportUnit.getUnit_type();
//            ArrayList<ReportCustomerData> reportCustomerDatas = (ArrayList<ReportCustomerData>) reportCustomerDao.getColumDatas(reportId, reportUnit.getUnit_id().toString());
//
//            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)){//一维静态
//                ArrayList<SimpleColumDefined> reportColums = (ArrayList<SimpleColumDefined>) reportDefinedUnitOneDimDao.getColumByUnit(String.valueOf(reportUnit.getUnit_id()));
//                Map<String, Object> checkResult = checkCustOrFomular(reportColums, reportCustomerDatas);
//                List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) checkResult.get("fomularArray");
//                if(fomularArray!=null&&fomularArray.size()>0){
//                    for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                        ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                        reportCustomerDao.updateUnitContext(comularData);
//                    }
//                }
//            }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)){//一维动态
//                ArrayList<SimpleColumDefined> reportColums =
//                        (ArrayList<SimpleColumDefined>) reportDefinedUnitOneDimDao.getColumByUnit(String.valueOf(reportUnit.getUnit_id()));
//                Map<String, Object> checkResult = checkCustOrFomular(reportColums, reportCustomerDatas);
//                List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) checkResult.get("fomularArray");
//                if(fomularArray!=null&&fomularArray.size()>0){
//                    for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                        ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                        reportCustomerDao.updateGridUnitContext(comularData);
//                    }
//                }
//
//            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)){//多维静态
//                ArrayList<GridColumDefined> reportColums =
//                        (ArrayList<GridColumDefined>) reportDefinedUnitMultDimDao.getGridColumDefindsByUNit(String.valueOf(reportUnit.getUnit_id()));
//                Map<String, Object> checkResult = checkCustOrFomularByGridDim(reportColums, reportCustomerDatas);
//                List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) checkResult.get("fomularArray");
//                if(fomularArray!=null&&fomularArray.size()>0){
//                    for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                        ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                        reportCustomerDao.updateGridUnitContext(comularData);
//                    }
//                }
//
//            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitTypeInt)){//多维动态树
//                if(reportCustomerDatas!=null){
//                    ArrayList<SimpleColumDefined> reportDefindColums =
//                            (ArrayList<SimpleColumDefined>) reportDefinedUnitOneDimDao.getColumByUnit(String.valueOf(reportUnit.getUnit_id()));
//
//                    Map<String,TreeUnitContext> reportGroupMapTmp = new HashMap();
//                    for (ReportCustomerData reportCustomerData : reportCustomerDatas) {
//                        if(reportGroupMapTmp.containsKey(reportCustomerData.getReport_group_id())){
//                            TreeUnitContext treeUnitContext = reportGroupMapTmp.get(reportCustomerData.getReport_group_id());
//                            treeUnitContext.getColumDatas().add(reportCustomerData);
//                        }
//                    }
//
//                    for (String groupId : reportGroupMapTmp.keySet()) {
//                        TreeUnitContext treeUnitContext = reportGroupMapTmp.get(groupId);
//                        ArrayList<ReportCustomerData> reportColums = treeUnitContext.getColumDatas();
//                        Map<String, Object> checkResult = checkCustOrFomular(reportDefindColums, reportColums);
//                        List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) checkResult.get("fomularArray");
//                        if(fomularArray!=null&&fomularArray.size()>0){
//                            for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                                ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                                reportCustomerDao.insertUnitContext(comularData);
//                            }
//                        }
//                    }
//                }
//            }else{
//
//            }
//        }
//    }

    @Override
    public Map<String, Object> getReportBaseInfo(String reportId,String reportDefinedId) {
        List<ReportCustomer> allReportCustomer = new ArrayList<>();
        if(reportDefinedId!=null){
            allReportCustomer = reportCustomerDao.getReportBaseInfoByDefinedId(reportDefinedId);
        }else{
            allReportCustomer = reportCustomerDao.getReportBaseInfo(reportId);

        }
        Map<String,Object> baseInfoMap = new HashMap<>();
        List<String> passApproveOriginNames = new ArrayList<>();
        List<String> passReviewOriginNames = new ArrayList<>();
        List<Map<String,String>> allSubOrigins = new ArrayList<>();
        int count=0;
        for (ReportCustomer reportCustomer : allReportCustomer) {
            String reportStartDate = reportCustomer.getReport_start_date_str();
            String reportEndDate = reportCustomer.getReport_end_date_str();
            String reportDataStart = reportCustomer.getReport_data_start_str();
            String reportDataEnd = reportCustomer.getReport_data_end_str();
            if(count==0){
                baseInfoMap.put("reportStartDate",reportStartDate);
                baseInfoMap.put("reportEndDate",reportEndDate);
                baseInfoMap.put("reportDataStart",reportDataStart);
                baseInfoMap.put("reportDataEnd",reportDataEnd);
            }
            count++;
            Map<String,String> originInfo = new HashMap<>();
            originInfo.put("originId",reportCustomer.getReport_origin().toString());
            originInfo.put("originName",reportCustomer.getReport_origin_name());
            allSubOrigins.add(originInfo);
            if("Y".equals(reportCustomer.getPass_approve())){
                passApproveOriginNames.add(reportCustomer.getReport_origin_name());
            }
            if("Y".equals(reportCustomer.getPass_review())){
                passReviewOriginNames.add(reportCustomer.getReport_origin_name());
            }
        }
        baseInfoMap.put("allSubOrigins",allSubOrigins);
        baseInfoMap.put("passApproveOriginNames",passApproveOriginNames);
        baseInfoMap.put("passReviewOriginNames",passReviewOriginNames);
        return baseInfoMap;
    }

    @Override
    public Map<String, Object> getGridContext(String reportId, String unitId) {
        ReportCustomer reportCust = reportCustomerDao.getReportCustomerByReportID(reportId);
        Integer reportDefinedId = reportCust.getReport_defined_id();
        List<GridColumDefined> columDefineds = reportCustomerDao.getGridColumDefiend(unitId);
        List<GridColumDefined> dimDefineds = reportCustomerDao.getGridDimDefiend(unitId);
        List<GridColumDefined> multDefiends = reportCustomerDao.getGridMultDefiend(unitId);

        List<ReportCustomerData> reportCustDatas = reportCustomerDao.getColumDatas(reportId, unitId);

        Map<String,Object> gridContext = new HashMap<>();
        gridContext.put("columDefineds",columDefineds);
        gridContext.put("dimDefineds",dimDefineds);
        gridContext.put("multDefiends",multDefiends);
        gridContext.put("reportCustDatas",reportCustDatas);

        return gridContext;
    }

    @Override
    public List<ReportCustomer> allReportForOrigin(String originId) {
        return reportCustomerDao.getAllReportInfoByOrigin(new Integer(originId));
    }

    @Override
    public void updateReportCustomerSubmitUser(String reportId, int user_id) {
        reportCustomerDao.updateReportCustomerSubmitUser(reportId,user_id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signReport( Map<String,String> signInfos) {
        String report_id = signInfos.get("report_id");
        String report_cust_name = signInfos.get("report_cust_name");
        String report_account_name = signInfos.get("report_account_name");
        String report_leader_name = signInfos.get("report_leader_name");
        reportCustomerDao.removeOldSign(report_id);
        reportCustomerDao.saveSignInfo(report_id,report_cust_name,report_account_name,report_leader_name);
    }

    @Override
    public Map<String,String> validateSimpleUnitByColum(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas) {

        Map<String,List<String>> dataTmp = new HashMap<>();
        for (ReportCustomerData columData : columDatas) {
            String unitId = columData.getUnit_id();
            String columId = columData.getColum_id();
            String tmpKey = unitId+"_"+columId;
            if(!dataTmp.containsKey(tmpKey)){
                dataTmp.put(tmpKey,new ArrayList<String>());
            }
            dataTmp.get(tmpKey).add(columData.getReport_data());
        }



        Map<String,String> validateResult = validateSimpleUnit(dataTmp, definedColums);

        return validateResult;
    }

    @Override
    public Map<String,String> validateSimpleUnitByDimensions(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas) {
        Map<String,List<String>> dataTmp = new HashMap<>();
        for (ReportCustomerData columData : columDatas) {
            String unitId = columData.getUnit_id();
            String columId = columData.getDimensions_id();
            String tmpKey = unitId+"_"+columId;
            if(!dataTmp.containsKey(tmpKey)){
                dataTmp.put(tmpKey,new ArrayList<String>());
            }
            dataTmp.get(tmpKey).add(columData.getReport_data());
        }

        Map<String,String> validateResult = validateSimpleUnit(dataTmp, definedColums);
        return validateResult;
    }

    @Override
    public Map<String, String> validateGridUnit(ArrayList<GridColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas) {
        Map<String,List<String>> dataTmp = new HashMap<>();
        for (ReportCustomerData columData : columDatas) {
            String unitId = columData.getUnit_id();
            String columId = columData.getColum_id();
            String dimId = columData.getDimensions_id();
            String tmpKey = unitId+"_"+columId+"_"+dimId;
            if(!dataTmp.containsKey(tmpKey)){
                dataTmp.put(tmpKey,new ArrayList<String>());
            }
            dataTmp.get(tmpKey).add(columData.getReport_data());
        }

        Map<String,String> validateResult  = new HashMap<>();

        for (GridColumDefined definedColum : definedColums) {
            Integer unitId = definedColum.getUnit_id();
            Integer columId = definedColum.getColum_id();
            Integer dimId = definedColum.getDim_id();

            if(dataTmp.containsKey(unitId+"_"+columId+"_"+dimId)){
                Integer columTypeINT = new Integer(definedColum.getColum_type());

                List<String> dataList = dataTmp.get(unitId + "_" + columId+"_"+dimId);

                for (String dataValue : dataList) {
                    if(Strings.isNullOrEmpty(dataValue)&&!ColumType.FORMULA.compareWith(new Integer(definedColum.getColum_type()))){
                        validateResult.put(definedColum.getColum_id().toString(),"数据不允许为空");
                        continue;
                    }

                    if(ColumType.NUMBER.compareWith(columTypeINT)){
                        BigDecimal maxValue = definedColum.getMax_value();
                        Integer minValue = definedColum.getMin_value();

                        try{
                            Integer dataInt = new Integer(dataValue);
                            BigDecimal dataBig = new BigDecimal(dataValue);
                            logger.debug("{},{}",dataInt,definedColum);

                            if(dataBig.compareTo(maxValue)<=0&&dataInt>=minValue){
                            }else{
                                validateResult.put(definedColum.getColum_id().toString()+"_"+definedColum.getDim_id().toString()
                                        ,"数据应在"+minValue+"-"+maxValue+"区间");
                            }
                        }catch (NumberFormatException e){
                            try{
                                Long dataFormatter = new Long(dataValue);
                                if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                }else{
                                    validateResult.put(definedColum.getColum_id().toString()
                                            +"_"+definedColum.getDim_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                }
                            }catch (NumberFormatException e1){
                                try{
                                    Float dataFormatter = new Float(dataValue);
                                    if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                    }else{
                                        validateResult.put(definedColum.getColum_id().toString()
                                                +"_"+definedColum.getDim_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                    }
                                }catch(NumberFormatException e2){
                                    try{
                                        Double dataFormatter = new Double(dataValue);
                                        if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                        }else{
                                            validateResult.put(definedColum.getColum_id().toString()
                                                    +"_"+definedColum.getDim_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                        }
                                    }catch(NumberFormatException e3){
                                        try{
                                            BigDecimal dataFormatter = new BigDecimal(dataValue);
                                            if((dataFormatter.compareTo(new BigDecimal(minValue))>=0)&&(dataFormatter.compareTo(maxValue)<=0)){
                                            }else{
                                                validateResult.put(definedColum.getColum_id().toString()
                                                        +"_"+definedColum.getDim_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                            }
                                        }catch(NumberFormatException e4){
                                            validateResult.put(definedColum.getColum_id().toString()+
                                                    "_"+definedColum.getDim_id().toString(),"数据格式错误");
                                        }

                                    }
                                }
                            }
                        }
                        if(validateResult.containsKey(definedColum.getColum_name_cn())){
                            logger.debug("校验×××{} 值{}×××区间{}~{}校验失败.....",definedColum.getColum_name_cn(),dataValue,minValue,maxValue);
                        }else
                            logger.debug("校验-->{} 值{}<--区间{}~{}校验PASS.....",definedColum.getColum_name_cn(),dataValue,minValue,maxValue);

                    }else if(ColumType.STRING.compareWith(columTypeINT)){

                    }else if(ColumType.DATE.compareWith(columTypeINT)){

                    }
                }

            }
        }

        return validateResult;
    }

    /**
     * @param dataTmp key：unitId+"_"+columId 对应 SimpleColumDefined.unit_id和SimpleColumDefined.colum_id
     *                value:data 数据值
     * @param definedColums
     */
    public Map<String, String> validateSimpleUnit(Map<String,List<String>> dataTmp, ArrayList<SimpleColumDefined> definedColums){
        Map<String,String> validateResult  = new HashMap<>();

        for (SimpleColumDefined definedColum : definedColums) {
            Integer unitId = definedColum.getUnit_id();
            Integer columId = definedColum.getColum_id();

            if(dataTmp.containsKey(unitId+"_"+columId)){
                Integer columTypeINT = new Integer(definedColum.getColum_type());

                List<String> dataList = dataTmp.get(unitId + "_" + columId);

                for (String dataValue : dataList) {
                    if(Strings.isNullOrEmpty(dataValue)&&!ColumType.FORMULA.compareWith(new Integer(definedColum.getColum_type()))){
                        validateResult.put(definedColum.getColum_id().toString(),"数据不允许为空");
                        continue;
                    }

                    if(ColumType.NUMBER.compareWith(columTypeINT)){
                        BigDecimal maxValue = definedColum.getMax_value();
                        Integer minValue = definedColum.getMin_value();

                        try{
                            Integer dataInt = new Integer(dataValue);
                            BigDecimal dataBig = new BigDecimal(dataValue);
                            logger.debug("{},{}",dataInt,definedColum);

                            if(dataBig.compareTo(maxValue)<=0&&dataInt>=minValue){
                            }else{
                                validateResult.put(definedColum.getColum_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                            }
                        }catch (NumberFormatException e){
                            try{
                                Long dataFormatter = new Long(dataValue);
                                if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                }else{
                                    validateResult.put(definedColum.getColum_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                }
                            }catch (NumberFormatException e1){
                                try{
                                    Float dataFormatter = new Float(dataValue);
                                    if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                    }else{
                                        validateResult.put(definedColum.getColum_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                    }
                                }catch(NumberFormatException e2){
                                    try{
                                        Double dataFormatter = new Double(dataValue);
                                        if(maxValue.compareTo(new BigDecimal(dataFormatter))>=0&&dataFormatter>=minValue){
                                        }else{
                                            validateResult.put(definedColum.getColum_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                        }
                                    }catch(NumberFormatException e3){
                                        try{
                                            BigDecimal dataFormatter = new BigDecimal(dataValue);
                                            if((dataFormatter.compareTo(new BigDecimal(minValue))>=0)&&(dataFormatter.compareTo(maxValue)<=0)){
                                            }else{
                                                validateResult.put(definedColum.getColum_id().toString(),"数据应在"+minValue+"-"+maxValue+"区间");
                                            }
                                        }catch(NumberFormatException e4){
                                            validateResult.put(definedColum.getColum_id().toString(),"数据格式错误");
                                        }

                                    }
                                }
                            }
                        }
                        if(validateResult.containsKey(definedColum.getColum_name_cn())){
                            logger.debug("校验×××{} 值{}×××区间{}~{}校验失败.....",definedColum.getColum_name_cn(),dataValue,minValue,maxValue);
                        }else
                            logger.debug("校验-->{} 值{}<--区间{}~{}校验PASS.....",definedColum.getColum_name_cn(),dataValue,minValue,maxValue);

                    }else if(ColumType.STRING.compareWith(columTypeINT)){

                    }else if(ColumType.DATE.compareWith(columTypeINT)){

                    }
                }

            }
        }

        return validateResult;
    }

    /**
     * 覆盖数据，按单元删除旧数据，将新数据保存
     * @param definedColums
     * @param columDatas
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void overrideSimpleUnitContext(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas) {
    }

    public Object getSimpleFomularData(FomularTmpEntity fomularTmpEntity){
        Object result = fomularService.getFomularData(fomularTmpEntity);
        return result;
    }

    /**
     * 一维动态数据更新，report_data含有非数字项
     * @param simpleColumDefineds 输入项的定义
     * @param columDatas 用户录入数据集合
     * @param isUpdate true:更新原数据值 false：插入新数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrInsertGroupUnitContext(
            ArrayList<SimpleColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,boolean isUpdate) {
        Map<String, Object> custOrFomular = checkCustOrFomular(simpleColumDefineds, columDatas);

        List<ReportCustomerData> custDataArray = (List<ReportCustomerData>) custOrFomular.get("custDataArray");
        List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) custOrFomular.get("fomularArray");

        Integer reportId = columDatas.get(0).getReport_id();
        String unitId = columDatas.get(0).getUnit_id();
        List<ReportCustomerData> dbcolumData = reportCustomerDao.getColumDatas(reportId.toString(), unitId);

        //保存用户录入项内容
        for (ReportCustomerData columData : columDatas) {
            boolean bAdd = true;
            for(ReportCustomerData dbData : dbcolumData){
                if(columData.getColum_id().equals(dbData.getColum_id()) &&
                        columData.getDimensions_id().equals(dbData.getDimensions_id())){
                    bAdd = false;
                    break;
                }
            }
            if(bAdd){
                reportCustomerDao.insertUnitContext(columData);
            }else{
                reportCustomerDao.updateGridUnitContext(columData);
            }
        }

        //刷新公式项内容
//        if(fomularArray!=null&&fomularArray.size()>0){
//            for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                if(isUpdate){
//                    reportCustomerDao.updateGridUnitContext(comularData);
//                }else{
//                    reportCustomerDao.insertUnitContext(comularData);
//                }
//            }
//        }

        //检查用户输入项是否被其他公式关联，并刷新关联公式的内容
//        User currUser = SessionSupport.checkoutUserFromSession();
//        new Thread(() -> {
//            logger.info("刷新关联到用户输入项的公式值");
//            List<ReportCustomerData> needRefresDatas = fomularService.refreshFomularForCustInput(custDataArray);
//            logger.info("需要刷新的内容{}",needRefresDatas);
//            if(needRefresDatas!=null){
//                for (ReportCustomerData needRefresData : needRefresDatas) {
//                    reportCustomerDao.updateGridUnitContext(needRefresData);
//                }
//            }
//
//            logger.info("查看用户录入项是否需要自动记忆，如需要，将用户输入数据保存");
//            rememberCustDataService.rememberCustData(simpleColumDefineds,
//                    columDatas,currUser.getUser_id());
//            logger.info("用户录入记忆完成");
//        }).start();

    }

    /**
     * 多维静态数据更新
     * @param simpleColumDefineds 输入项的定义
     * @param columDatas 用户录入数据集合
     * @param isUpdate true:更新原数据值 false：插入新数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrInsertGridUnitContext(
            ArrayList<GridColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,boolean isUpdate) {
        Map<String, Object> custOrFomular = checkCustOrFomularByGridDim(simpleColumDefineds, columDatas);

        List<ReportCustomerData> custDataArray = (List<ReportCustomerData>) custOrFomular.get("custDataArray");
        List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) custOrFomular.get("fomularArray");

        for (ReportCustomerData columData : custDataArray) {
            if(isUpdate){
                reportCustomerDao.updateGridUnitContext(columData);
            }else{
                reportCustomerDao.insertUnitContext(columData);
            }
        }

//        if(fomularArray!=null&&fomularArray.size()>0){
//            for (FomularTmpEntity fomularTmpEntity : fomularArray) {
//                ReportCustomerData comularData = fomularService.makeReportCustDataByFomular(fomularTmpEntity);
//                if(isUpdate){
//                    reportCustomerDao.updateGridUnitContext(comularData);
//                }else{
//                    reportCustomerDao.insertUnitContext(comularData);
//                }
//            }
//        }

        //检查用户输入项是否被其他公式关联，并刷新关联公式的内容
//        User currUser = SessionSupport.checkoutUserFromSession();
//        new Thread(() -> {
//            logger.info("刷新关联到用户输入项的公式值");
//            List<ReportCustomerData> needRefresDatas = fomularService.refreshFomularForCustInput(custDataArray);
//            logger.info("需要刷新的内容{}",needRefresDatas);
//            if(needRefresDatas!=null){
//                for (ReportCustomerData needRefresData : needRefresDatas) {
//                    reportCustomerDao.updateGridUnitContext(needRefresData);
//                }
//            }
//
//            logger.info("查看用户录入项是否需要自动记忆，如需要，将用户输入数据保存");
//            ArrayList<SimpleColumDefined> simpleColumDefineds2 = convertToSimpleArray(simpleColumDefineds);
//            rememberCustDataService.rememberCustDataByGrid(simpleColumDefineds2,
//                    columDatas,currUser.getUser_id());
//            logger.info("用户录入记忆完成");
//        }).start();
    }

    private ArrayList<SimpleColumDefined> convertToSimpleArray(ArrayList<GridColumDefined> gridColumDefineds){
        ArrayList<SimpleColumDefined> simpleColumDefineds = new ArrayList<>();
        for(GridColumDefined grid : gridColumDefineds){
            if(grid.getColum_meta_type().equals("1")){
                SimpleColumDefined simple = new SimpleColumDefined();
                simple.setColum_type(grid.getColum_type());
                simple.setMin_value(grid.getMin_value());
                simple.setMax_value(grid.getMax_value());
                simple.setColum_formula(grid.getColum_formula());
                simple.setColum_formula_desc(grid.getColum_formula_desc());
                simple.setColum_type(grid.getColum_type());
                simple.setColum_point(grid.getColum_point());
                simple.setNeed_remember(grid.getNeed_remember());
                simple.setUnit_id(grid.getUnit_id());
                simple.setColum_name(grid.getColum_name());
                simple.setColum_name_cn(grid.getColum_name_cn());
                simple.setColum_id(grid.getColum_id());
                simple.setGroup_id(grid.getDim_id());
                simple.setGroup_name(grid.getDim_name());
                simpleColumDefineds.add(simple);
            }
        }
        return simpleColumDefineds;
    }

}
