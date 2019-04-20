package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.dao.IReportStatementsDao;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitMultDimService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import com.seaboxdata.cqny.record.service.ReportUnitService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("reportStatements")
public class ReportStatementsServiceImp implements ReportStatementsService {
    private static Logger logger = LoggerFactory.getLogger(ReportStatementsServiceImp.class);

    @Autowired
    private IReportStatementsDao reportStatementsDao;

    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

    @Autowired
    private ReportUnitService reportUnitService;

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @Autowired
    private ReportDefinedUnitTreeDimServiceImp reportDefinedUnitTreeDimServiceImp;

    @Autowired
    private ReportDefinedUnitMultDimService reportDefinedUnitMultDimService;

    @Override
    public PageResult listReportStatements(int currPage, int pageSize) {
        Page<ReportDefinedEntity> reportStatementsPage = reportStatementsDao.listReportStatements(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
    }

    @Override
    public void addReportStatements(ReportDefinedEntity reportDefined) {

        if(reportDefined.getDefined_id()!=null){
            reportStatementsDao.updateReportStatements(reportDefined);
        }else{
            SimpleDateFormat format = new SimpleDateFormat("ssSSS");
            StringBuilder builder = new StringBuilder();
            builder.append(format.format(Calendar.getInstance().getTime()));
            builder.append(new Random().nextInt(50));
            int defined_id = new Integer(builder.toString());
            defined_id = defined_id<<(new Random().nextInt(5));
            reportDefined.setDefined_id(defined_id);
            logger.debug("save reportDefined info {}",reportDefined);
            reportStatementsDao.addReportStatements(reportDefined);
        }
    }

    @Override
    public void deleteById(String definedId) {
        reportStatementsDao.deleteById(definedId);
    }

    @Override
    public PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id,String originId) {
        List<String> originList=submitauthorityDao.getOriginIdListByUserId(user_id);
        //用获得的originList取得机构的所有下属机构id
        Set finalOriginSet = new HashSet();
        if(originId==null){
            for (String origin:originList) {
                Map<String, Object> originTree = submitauthorityDao.getOriginById(origin);
                checkOrigins(originTree,finalOriginSet);
            }
        }else{
            finalOriginSet.add(originId);
        }
        if(finalOriginSet.size()==0){
            return null;
        }
        Page<ReportCustomer> reportStatementsPage = reportStatementsDao.listReportStatementsByUser(currPage, pageSize,finalOriginSet);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
    }
    private void checkOrigins(Map<String, Object> origin,Set finalOriginSet){
        List<Map<String, Object>> children = (List) origin.get("childrens");
        finalOriginSet.add(origin.get("origin_id"));
        if(children!=null&&children.size()>0){
            children.forEach(child->{
                checkOrigins(child,finalOriginSet);
            });
        }
    }
    @Override
    public void saveDefinedAndOriginAssign(String[] originIds, String definedId) {
        reportStatementsDao.saveDefinedAndOriginAssign(originIds,definedId);
    }

    @Override
    public List<String> getDefinedAndOriginAssignById(String definedId) {
        return reportStatementsDao.getDefinedAndOriginAssignById(definedId);
    }

    @Override
    public void delDefinedAndOriginAssign(String definedId) {
        reportStatementsDao.delDefinedAndOriginAssign(definedId);
    }

    @Override
    public void changeDeindStatus(String definedId, ReportDefinedStatus status) {
        reportStatementsDao.changeDeindStatus(definedId,status.value());
    }

    @Override
    public List<Origin> getDefinedOriginsById(String definedId) {
        return reportStatementsDao.getDefinedOriginsById(definedId);
    }

    public List<HashMap<String,String>> getOriginsByUserId(int user_id) {
        List<String> originList=submitauthorityDao.getOriginIdListByUserId(user_id);
        //用获得的originList取得机构的所有下属机构id
        Set finalOriginSet = new HashSet();
        for (String origin:originList) {
            Map<String, Object> originTree = submitauthorityDao.getOriginById(origin);
            checkOrigins(originTree,finalOriginSet);
        }
        if(finalOriginSet.size()==0){
            return null;
        }
        List<HashMap<String,String>> resultMap = reportStatementsDao.getOriginsByOriginSet(finalOriginSet);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyReportDefined(String definedId) {
        Integer fromDefindId = new Integer(definedId);
        Integer toDefinedId = copyReportStatement(fromDefindId);

        Map<Integer, UnitDefined> copyUnits = reportUnitService.copyReportUnit(fromDefindId, toDefinedId);

        List<CopyReportDefinedTmp> oneDimCopys = reportDefinedUnitOneDimService.copyOneDims(copyUnits);
        List<CopyReportDefinedTmp> oneDymCopys = reportDefinedUnitOneDimService.copyDymDims(copyUnits);
        List<CopyReportDefinedTmp> oneTreeCopys = reportDefinedUnitTreeDimServiceImp.copyDims(copyUnits);
        List<CopyReportDefinedTmp> multCopys = reportDefinedUnitMultDimService.copyDims(copyUnits);

        Map<Integer,Map<String,String>> groupResult = new HashMap<>();

        this.groupCopyUnit(oneDimCopys,groupResult);
        this.groupCopyUnit(oneDymCopys,groupResult);
        this.groupCopyUnit(oneTreeCopys,groupResult);
        this.groupCopyUnit(multCopys,groupResult);

        List<UnitDefined> newReportUnits = reportUnitService.getUnitDefinedByReportDefindId(toDefinedId.toString());

        this.updateSimpleDimFomular(newReportUnits,groupResult,copyUnits);

    }

    private void groupCopyUnit(List<CopyReportDefinedTmp> copyReportDefinedTmps,
                               Map<Integer,Map<String,String>> groupResult){
        for (CopyReportDefinedTmp copyReportDefinedTmp : copyReportDefinedTmps) {
            Integer fromUnitId = copyReportDefinedTmp.getFromUnit();
            if(!groupResult.containsKey(fromUnitId)){
                Map<String,String> columAndDimMap = new HashMap<>();
                groupResult.put(fromUnitId,columAndDimMap);
            }
            Map<String, String> columAndDimMap = groupResult.get(fromUnitId);

            Map<Integer, Integer> fromAndToColums = copyReportDefinedTmp.getFromAndToColumId();

            if(copyReportDefinedTmp.getFromAndToColumId()!=null){
                for (Integer fromColumId : fromAndToColums.keySet()) {
                    columAndDimMap.put("colum_"+fromColumId.toString(),"colum_"+fromAndToColums.get(fromColumId).toString());
                }
            }

            if(copyReportDefinedTmp.getFromAndToDimId()!=null&&copyReportDefinedTmp.getFromAndToDimId().size()>0){
                Map<Integer, Integer> fromAndToDims = copyReportDefinedTmp.getFromAndToDimId();

                for (Integer fromDimId : fromAndToDims.keySet()) {
                    columAndDimMap.put("dim_"+fromDimId.toString(),"dim_"+fromAndToDims.get(fromDimId).toString());
                }
            }

        }
    }

    public void updateSimpleDimFomular
            (List<UnitDefined> reportUnits,
             Map<Integer,Map<String,String>> groupResult ,
             Map<Integer, UnitDefined> copyUnits){
        for (UnitDefined reportUnit : reportUnits) {
            if(UnitDefinedType.MANYDIMSTATIC.compareWith(reportUnit.getUnit_type())){

                List<GridColumDefined> gridColumDefineds = reportDefinedUnitMultDimService.getMultDefindByUnit(reportUnit.getUnit_id().toString());
                for (GridColumDefined colum : gridColumDefineds) {
                    if(ColumType.FORMULA.compareWith(new Integer(colum.getColum_type()))){
                        String newFomular = getNewFomular(colum.getColum_formula(),groupResult,copyUnits);
                        colum.setColum_formula(newFomular);
                    }

                }
                Map<String,List<GridColumDefined>> editMultDImMap = new HashMap<>();
                editMultDImMap.put("edit_data",gridColumDefineds);
                reportDefinedUnitMultDimService.editSaveMultdim(editMultDImMap);
            }else{
                List<SimpleColumDefined> colums = (List<SimpleColumDefined>) reportDefinedUnitOneDimService.getColumByUnit(reportUnit.getUnit_id().toString());
                for (SimpleColumDefined colum : colums) {
                    if(ColumType.FORMULA.compareWith(new Integer(colum.getColum_type()))){
                        String newFomular = getNewFomular(colum.getColum_formula(),groupResult,copyUnits);
                        colum.setColum_formula(newFomular);
                        reportDefinedUnitOneDimService.editSaveOnedim(colum);
                    }
                }

            }
        }
    }

    private String getNewFomular(String oldFomular,
                                 Map<Integer,Map<String,String>> groupResult ,
                                 Map<Integer, UnitDefined> copyUnits){
        StringBuilder newFomularSb = new StringBuilder();
        String[] fomularParams = oldFomular.split("#");
        for (String fomularParam : fomularParams) {
            int firstPoint = fomularParam.indexOf(".");
            if(firstPoint>=0){
                String unitId = fomularParam.substring(0, firstPoint);
                Integer unitIdInt = new Integer(unitId);
                String othersId = fomularParam.substring(firstPoint+1);
                Map<String, String> fromUnitGroup = groupResult.get(unitIdInt);

                newFomularSb.append("#").append(copyUnits.get(unitIdInt).getUnit_id());

                if(othersId.indexOf(".")>0){
                    String othersIdReplace = othersId.replace(".", "_");
                    String[] columAndDim = othersIdReplace.split("_");
                    String newColumValue = fromUnitGroup.get("colum_" + columAndDim[0]).replace("colum_","");
                    String newDimValue = fromUnitGroup.get("dim_" + columAndDim[1]).replace("dim_","");
                    newFomularSb.append(".").append(newColumValue).append(".").append(newDimValue).append("#");
                }else{
                    String newColumId = fromUnitGroup.get("colum_" + othersId).replace("colum_", "");
                    newFomularSb.append(".").append(newColumId).append("#");
                }
            }else{
                newFomularSb.append(fomularParam);
            }
        }

        return newFomularSb.toString();
    }

    private Integer copyReportStatement(Integer definedId){
        ReportDefinedEntity reportStatement = reportStatementsDao.getReportDefinedById(new Integer(definedId));
        reportStatement.setDefined_id(null);
        reportStatementsDao.addReportStatements(reportStatement);
        Integer newDefindId = reportStatement.getDefined_id();
        return newDefindId;
    }

    @Override
    public ReportDefinedEntity getReportDefinedById(Integer definedId) {
        ReportDefinedEntity reportDefined = reportStatementsDao.getReportDefinedById(definedId);
        return reportDefined;
    }
}
