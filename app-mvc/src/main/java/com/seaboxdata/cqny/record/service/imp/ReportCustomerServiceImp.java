package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.ReportUnitCustomerContext;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("reportCustomerService")
public class ReportCustomerServiceImp implements ReportCustomerService {

    private Logger logger = LoggerFactory.getLogger(ReportCustomerServiceImp.class);

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private ReportUnitService reportDefinedUnitService;


    @Override
    public PageResult pagerReport(Integer currPage, Integer pageSize, Integer userId) {
        Page pagerData = reportCustomerDao.pageReport(currPage,pageSize,userId);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pagerData);
        return pageResult;
//        return null;
    }

    /**
     * 获取报送报表信息：报表定义信息、报表填写信息
     */
    public ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType){
        List definedColums = reportDefinedUnitService.getDefinedColums(unitId, unitType);
        List columDatas = reportCustomerDao.getColumDatas(reportId,unitId);
        ReportUnitCustomerContext reportUnitCustomerContext = new ReportUnitCustomerContext();
        reportUnitCustomerContext.setDefinedColums(definedColums);
        reportUnitCustomerContext.setColumDatas(columDatas);
        return reportUnitCustomerContext;
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
            ArrayList<ReportCustomerData> columDatas,boolean isUpdate) {
        Map<String,SimpleColumDefined> fomularsTmp = new HashMap<>();
        Map<String,String> fomularMap = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            simpleColumDefineds.forEach(simpleColumDefined->{
                Integer columType = new Integer(simpleColumDefined.getColum_type());
                if(ColumType.FORMULA.compareWith(columType)){
                    fomularsTmp.put(simpleColumDefined.getUnit_id()+"_"+simpleColumDefined.getColum_id(),simpleColumDefined);
                }
            });
        }

        if(columDatas!=null&&columDatas.size()>0){
            columDatas.forEach(columData->{
                Integer reportId = columData.getReport_id();
                String unitId = columData.getUnit_id();
                String columnId = columData.getColum_id();
                String dimensionsId = columData.getDimensions_id();
                if(fomularsTmp.containsKey(unitId+"_"+columnId)){//一维静态公式刷新
                    fomularMap.put(new StringBuilder().append(reportId).append("_").append(unitId).append("_").append(columnId).toString(),
                            fomularsTmp.get(unitId+"_"+columnId).getColum_formula());
                }else if(fomularsTmp.containsKey(unitId+"_"+dimensionsId)){//树状多维公式刷新
                    fomularMap.put(
                            new StringBuilder().append(reportId).append("_")
                                    .append(unitId).append("_")
                                    .append(columnId)
                                    .append("_")
                                    .append(dimensionsId)
                                    .toString(),
                            fomularsTmp.get(unitId+"_"+dimensionsId).getColum_formula());
                } else{//无公式值刷新
                    if(isUpdate){
                        reportCustomerDao.updateUnitContext(columData);
                    }else{
                        reportCustomerDao.insertUnitContext(columData);
                    }

                }
            });
        }

        if(fomularMap!=null&&fomularMap.size()>0){
            fomularMap.keySet().forEach(fomularKey->{
                String[] params = fomularKey.split("_");
                Object fomularDataResult = null;
                String reportId = params[0];
                String unitId = params[1];
                String columnId = params[2];
                String dimensionsId = null;
                if(params.length==4){
                    dimensionsId = params[3];
                    fomularDataResult = refreshDimensionsFomularData(reportId,columnId, fomularMap.get(fomularKey));
                }else if (params.length==3){
                    fomularDataResult = refreshSimpleFomularData(reportId, fomularMap.get(fomularKey));
                }

                ReportCustomerData comularData = new ReportCustomerData();
                comularData.setReport_id(new Integer(reportId));
                comularData.setUnit_id(unitId);
                comularData.setColum_id(columnId);
                comularData.setDimensions_id(dimensionsId);
                comularData.setReport_data(String.valueOf(fomularDataResult));
                reportCustomerDao.updateUnitContext(comularData);
            });
        }


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
        Integer nexyCurrUnitId = null;
        List<UnitEntity> allUnit = reportCustomer.getUnitEntities();
        boolean nextCheckOut = false;
        for (UnitEntity unitEntity : allUnit) {
            Integer unitOrder = unitEntity.getUnit_order();
             if(unitEntity.getUnit_id().equals(currUnitId)){
                nextCheckOut = true;
                continue;
            }
            if(nextCheckOut){
                nexyCurrUnitId = unitEntity.getUnit_id();
                break;
            }
        }

        return nexyCurrUnitId;
    }

    @Override
    public void updateStep(String reportId) {
        Integer nextStepUnit = this.checkNextStepUnitId(reportId);
        reportCustomerDao.updateStep(reportId,nextStepUnit);
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
                    if(Strings.isNullOrEmpty(dataValue)){
                        validateResult.put(definedColum.getColum_name_cn(),"数据不允许为空");
                        continue;
                    }

                    if(ColumType.NUMBER.compareWith(columTypeINT)){
                        Integer maxValue = definedColum.getMax_value();
                        Integer minValue = definedColum.getMin_value();

                        try{
                            Integer dataInt = new Integer(dataValue);
                            if(dataInt<=maxValue&&dataInt>=minValue){
                            }else{
                                validateResult.put(definedColum.getColum_name_cn(),"数据应在"+minValue+"-"+maxValue+"区间");
                            }
                        }catch (NumberFormatException e){
                            try{
                                Long dataFormatter = new Long(dataValue);
                                if(dataFormatter<=maxValue&&dataFormatter>=minValue){
                                }else{
                                    validateResult.put(definedColum.getColum_name_cn(),"数据应在"+minValue+"-"+maxValue+"区间");
                                }
                            }catch (NumberFormatException e1){
                                try{
                                    Float dataFormatter = new Float(dataValue);
                                    if(dataFormatter<=maxValue&&dataFormatter>=minValue){
                                    }else{
                                        validateResult.put(definedColum.getColum_name_cn(),"数据应在"+minValue+"-"+maxValue+"区间");
                                    }
                                }catch(NumberFormatException e2){
                                    try{
                                        Double dataFormatter = new Double(dataValue);
                                        if(dataFormatter<=maxValue&&dataFormatter>=minValue){
                                        }else{
                                            validateResult.put(definedColum.getColum_name_cn(),"数据应在"+minValue+"-"+maxValue+"区间");
                                        }
                                    }catch(NumberFormatException e3){
                                        try{
                                            BigDecimal dataFormatter = new BigDecimal(dataValue);
                                            if((dataFormatter.compareTo(new BigDecimal(minValue))>=0)&&(dataFormatter.compareTo(new BigDecimal(maxValue))<=0)){
                                            }else{
                                                validateResult.put(definedColum.getColum_name_cn(),"数据应在"+minValue+"-"+maxValue+"区间");
                                            }
                                        }catch(NumberFormatException e4){
                                            validateResult.put(definedColum.getColum_name_cn(),"数据格式错误");
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
        reportCustomerDao.removeUnitContextData(columDatas.get(0).getUnit_id());
        this.updateOrInsertSimpleUnitContext(definedColums,columDatas,false);
    }

    public Object refreshSimpleFomularData(String reportId,String columFomular){
        return doRefreshSimpleFomular(reportId,null,columFomular);
    }


    private Object refreshDimensionsFomularData(String reportId, String columnId, String columFomular) {
        return doRefreshSimpleFomular(reportId,columnId,columFomular);
    }

    private Object doRefreshSimpleFomular(String reportId, String columnId, String columFomular){
        int value = -1;
        String fomularColumId = null;
        List<String> fomularColums = new ArrayList<>();
        String columFomularTmp = columFomular;

        Map<String,Object> fomularParams = new HashMap<>();

        while(( value = columFomularTmp.indexOf("#"))>=0){
            columFomularTmp = columFomularTmp.replaceFirst("#","");
            int columEnd = columFomularTmp.indexOf("#");
            fomularColums.add(columFomularTmp.substring(value, columEnd));
            columFomularTmp = columFomularTmp.replaceFirst("#","");
        }

        if(fomularColums!=null&&fomularColums.size()>0){
            for (String fomularColum : fomularColums) {
                String fomularColumTmp = fomularColum.replace(".", "_");
                String[] infos = fomularColumTmp.split("_");
                String unitId = infos[0];
                ReportCustomerData reportCustomerData = null;
                if(Strings.isNullOrEmpty(columnId)){
                    String columIdDefined = infos[1];
                    fomularColumId = columIdDefined;
                    reportCustomerData = reportCustomerDao.getSimpleReportCustomerData( reportId, unitId, columIdDefined);
                }else{
                    String dimensionsId = infos[1];
                    fomularColumId = dimensionsId;
                    reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataBydimensions( reportId, unitId, columnId,dimensionsId);
                }
                Object dataFormatter = 0;
                try{
                    dataFormatter = new Integer(reportCustomerData.getReport_data());
                }catch (NumberFormatException e){
                    try{
                        dataFormatter = new Long(reportCustomerData.getReport_data());
                    }catch (NumberFormatException e1){
                        try{
                            dataFormatter = new Float(reportCustomerData.getReport_data());
                        }catch(NumberFormatException e2){
                            try{
                                dataFormatter = new Double(reportCustomerData.getReport_data());
                            }catch(NumberFormatException e3){
                                dataFormatter = new BigDecimal(reportCustomerData.getReport_data());
                            }
                        }
                    }
                }
                fomularParams.put(
                        new StringBuilder().append("FL").append(unitId).append("_").append(fomularColumId).append("FL").toString(),
                        dataFormatter);
            }
        }

        String fomular = columFomular.replace("#", "FL").replace(".", "_");
        Expression expression= AviatorEvaluator.compile(fomular);
        Object result = expression.execute(fomularParams);

        return result;
    }
}
