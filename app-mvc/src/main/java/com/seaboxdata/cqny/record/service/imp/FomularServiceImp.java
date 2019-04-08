package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.FomularTmpEntity;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.FomularService;
import com.seaboxdata.cqny.reportunit.dao.IReportUnitDao;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("fomularService")
public class FomularServiceImp implements FomularService {

    private Logger logger = LoggerFactory.getLogger(FomularServiceImp.class);

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private IReportUnitDao reportUnitDao;

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    @Override
    public Object getFomularData(FomularTmpEntity fomularTmpEntity) {
        String columFomularTmp = fomularTmpEntity.getFomularScript();
        if(columFomularTmp.indexOf("SUM:")>=0){
            return this.getSumValue(fomularTmpEntity);
        }else{
            return this.runFomular(fomularTmpEntity);
        }
    }

    @Override
    public Object getSumValue(FomularTmpEntity fomularTmpEntity) {
        String columFomularTmp = fomularTmpEntity.getFomularScript();
        Integer reportId = fomularTmpEntity.getReportId();
        String unitId = fomularTmpEntity.getUnitId();
        BigDecimal result = null;
        String[] fomularColumArray = columFomularTmp.replace("#", "").replace(".","_").split("_");
        if(!Strings.isNullOrEmpty(fomularTmpEntity.getDimensionsId())){
            result = reportCustomerDao.sumColumForDimensions(reportId.toString(), unitId, fomularColumArray[1]);
            logger.debug("{}",result);
        }else{
            result = reportCustomerDao.sumColumForDimensions(reportId.toString(), unitId,fomularColumArray[1]);
            logger.debug("{}",result);
        }
        return result;

    }

    @Override
    public Object runFomular(FomularTmpEntity fomularTmpEntity) {
        String columFomularTmp = fomularTmpEntity.getFomularScript();

        //#19.16#+#18.25#提取出 19.16 18.25 放到fomularColums中
        List<String> fomularColums = fomularParamNames(columFomularTmp);

        if(fomularColums!=null&&fomularColums.size()>0){
            Map<String, Object> fomularParams = this.makeFomularParams(fomularColums, fomularTmpEntity);

            String fomular = fomularTmpEntity.getFomularScript().replace("#", "FL").replace(".", "_");
            Expression expression= AviatorEvaluator.compile(fomular);
            Object result = expression.execute(fomularParams);
            return result;
        }

        return null;
    }


    public List<ReportCustomerData> refreshFomularForCustInput(List<ReportCustomerData> reportCustomerDatas){
        List<SimpleColumDefined> allFomularDefineds = this.checkFomularsForCustInput(reportCustomerDatas);
        Integer reportId = reportCustomerDatas.get(0).getReport_id();
        String selfUnitId = reportCustomerDatas.get(0).getUnit_id();
        Map<Integer, UnitEntity> unitTmp = new HashMap<>();
        Map<String, SimpleColumDefined> fomularsTmp = this.getFomularsTmp(allFomularDefineds);
        List<FomularTmpEntity> fomularArray = new ArrayList<>();
        for (SimpleColumDefined fomularDefined : allFomularDefineds) {
            Integer unitId = fomularDefined.getUnit_id();
            if(unitId.toString().equals(selfUnitId)){
                continue;
            }

            if(!unitTmp.containsKey(unitId)){
                UnitEntity unitEntity = reportUnitDao.getReportUnit(unitId.toString());
                unitTmp.put(unitId,unitEntity);
            }

            Integer unitType = unitTmp.get(unitId).getUnit_type();

            FomularTmpEntity fomularTmpEntity = new FomularTmpEntity();
            fomularTmpEntity.setReportId(reportId);
            fomularTmpEntity.setUnitId(unitId.toString());
            fomularTmpEntity.setFomularScript(fomularDefined.getColum_formula());

            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)||
                    UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)){
                fomularTmpEntity.setColumId(fomularDefined.getColum_id().toString());
            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitType)){
                fomularTmpEntity.setDimensionsId(fomularDefined.getColum_id().toString());
            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){

            }
            fomularArray.add(fomularTmpEntity);
        }


        List<ReportCustomerData> reportCustDatas = new ArrayList<>();
        if(fomularArray!=null){
            for (FomularTmpEntity fomularTmpEntity : fomularArray) {
                ReportCustomerData reportCustData = this.makeReportCustDataByFomular(fomularTmpEntity);
                Integer unitType = unitTmp.get(new Integer(reportCustData.getUnit_id())).getUnit_type();
                if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)||
                        UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)||
                        UnitDefinedType.MANYDIMTREE.compareWith(unitType)){
                    reportCustomerDao.updateUnitContextSimple(reportCustData);
                }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){

                }
                reportCustDatas.add(reportCustData);
            }
        }
        return reportCustDatas;
    }

    public List<FomularTmpEntity> getFomularArray(List<ReportCustomerData> reportCustomerDatas,Map<String, SimpleColumDefined> fomularsTmp){
        List<FomularTmpEntity> fomularArray = new ArrayList<>();
        if(reportCustomerDatas!=null&&reportCustomerDatas.size()>0){
            for (ReportCustomerData columData : reportCustomerDatas) {
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

                    String fomularScriptVal = fomularsTmp.get(unitId + "_" + dimensionsId) != null ?
                            fomularsTmp.get(unitId + "_" + dimensionsId).getColum_formula() :
                            fomularsTmp.get(unitId + "_" + columnId).getColum_formula();
                    fomularTmpEntity.setFomularScript(fomularScriptVal);
                    fomularArray.add(fomularTmpEntity);
                }
            }

        }
        return fomularArray;

    }


    public List<SimpleColumDefined> checkFomularsForCustInput(List<ReportCustomerData> reportCustomerDatas){
        if(reportCustomerDatas!=null){
            List<SimpleColumDefined> simpleDefineds = new ArrayList<>();
            for (ReportCustomerData reportCustomerData : reportCustomerDatas) {
                String unitId = reportCustomerData.getUnit_id();
                String columId = reportCustomerData.getColum_id();
                String dimensionsId = reportCustomerData.getDimensions_id();
                String unitIdAndColumId = new StringBuilder().append(unitId).append(".").append(columId).toString();
                String unitIdAndDimId = new StringBuilder().append(unitId).append(".").append(dimensionsId).toString();

                List<SimpleColumDefined> simpleDefinedsDim = reportDefinedUnitOneDimDao.checkSimpleDefinedFomulars(unitIdAndDimId);
                List<SimpleColumDefined> simpleDefinedsColum = reportDefinedUnitOneDimDao.checkSimpleDefinedFomulars(unitIdAndColumId);
                if(simpleDefinedsDim!=null)
                    simpleDefineds.addAll(simpleDefinedsDim);
                if(simpleDefinedsColum!=null)
                    simpleDefineds.addAll(simpleDefinedsColum);
            }
            return simpleDefineds;

        }
        return null;
    }

    public Map<String,SimpleColumDefined> getFomularsTmp(List<SimpleColumDefined> simpleColumDefineds){
        Map<String,SimpleColumDefined> fomularsTmp = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            simpleColumDefineds.forEach(simpleColumDefined->{
                Integer columType = new Integer(simpleColumDefined.getColum_type());
                if(ColumType.FORMULA.compareWith(columType)){
                    fomularsTmp.put(simpleColumDefined.getUnit_id()+"_"+simpleColumDefined.getColum_id(),simpleColumDefined);
                }
            });
        }
        return fomularsTmp;
    }

    private UnitEntity checkUnit(String unitId){
        UnitEntity unitEntity = reportUnitDao.getReportUnit(unitId);
        return unitEntity;
    }

    private Map<String, Object> makeFomularParams(List<String> fomularColums, FomularTmpEntity fomularTmpEntity){
        Map<String,Object> fomularParams = new HashMap<>();

        for (String fomularColum : fomularColums) {//获取公式中各个运算参数的具体值

            String fomularColumTmp = fomularColum.replace(".", "_");

            //每一个运算参数由两部分构成 第一部分是unit_id 第二部分是colum_id或dimensions_id
            String[] infos = fomularColumTmp.split("_");
            String unitId = infos[0];
            String columOrDimensionsIdDefined = infos[1];

            UnitEntity unitDefined = this.checkUnit(unitId);
            Integer unitType = unitDefined.getUnit_type();

            ReportCustomerData reportCustomerData = null;
            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)||
                    UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)){
                reportCustomerData = reportCustomerDao.getSimpleReportCustomerData(
                        fomularTmpEntity.getReportId().toString(), unitId, columOrDimensionsIdDefined);
            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitType)){
                reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataBydimensions(
                        fomularTmpEntity.getReportId().toString(), unitId,
                        fomularTmpEntity.getColumId(),columOrDimensionsIdDefined);
                if(reportCustomerData==null){
                    reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataByDimId(fomularTmpEntity.getReportId().toString(),
                            unitId, columOrDimensionsIdDefined);
                }

            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){

            }

            Object formatData = dataFormat(reportCustomerData.getReport_data());
            fomularParams.put(
                    new StringBuilder().append("FL").append(unitId).append("_").append(columOrDimensionsIdDefined).append("FL").toString(),
                    formatData);
        }

        return fomularParams;
    }

    private List<String> fomularParamNames(String columFomularTmp){
        int value = -1;
        List<String> fomularColums = new ArrayList<>();

        while(( value = columFomularTmp.indexOf("#"))>=0){
            columFomularTmp = columFomularTmp.replaceFirst("#","");
            int columEnd = columFomularTmp.indexOf("#");
            fomularColums.add(columFomularTmp.substring(value, columEnd));
            columFomularTmp = columFomularTmp.replaceFirst("#","");
        }
        return fomularColums;
    }

    private Object dataFormat(String dataVal){
        Object dataFormatter = 0;
        try{
            dataFormatter = new Integer(dataVal);
        }catch (NumberFormatException e){
            try{
                dataFormatter = new Long(dataVal);
            }catch (NumberFormatException e1){
                try{
                    dataFormatter = new Float(dataVal);
                }catch(NumberFormatException e2){
                    try{
                        dataFormatter = new Double(dataVal);
                    }catch(NumberFormatException e3){
                        dataFormatter = new BigDecimal(dataVal);
                    }
                }
            }
        }

        return dataFormatter;
    }

    public ReportCustomerData makeReportCustDataByFomular(FomularTmpEntity fomularTmpEntity){
        Object fomularDataResult = getFomularData(fomularTmpEntity);
        ReportCustomerData comularData = new ReportCustomerData();
        comularData.setReport_id(fomularTmpEntity.getReportId());
        comularData.setUnit_id(fomularTmpEntity.getUnitId());
        comularData.setColum_id(fomularTmpEntity.getColumId());
        comularData.setDimensions_id(fomularTmpEntity.getDimensionsId());
        comularData.setReport_data(String.valueOf(fomularDataResult));
        comularData.setReport_group_id(fomularTmpEntity.getReportGroupId());
        return comularData;
    }


}
