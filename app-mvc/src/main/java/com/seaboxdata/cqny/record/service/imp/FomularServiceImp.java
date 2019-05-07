package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitMultDimDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.ColumDefined;
import com.seaboxdata.cqny.record.entity.FomularTmpEntity;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.service.FomularService;
import com.seaboxdata.cqny.record.dao.IReportUnitDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service("fomularService")
public class FomularServiceImp implements FomularService {

    private Logger logger = LoggerFactory.getLogger(FomularServiceImp.class);

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private IReportUnitDao reportUnitDao;

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    @Autowired
    private IReportDefinedUnitMultDimDao reportDefinedUnitMultDimDao;


    @Transactional(rollbackFor = Exception.class)
    public void refreshFomularDatas(String reportDefinedId,String reportId){
        //获取当前报表的所有报送单元
        List<UnitDefined> allUnitDefinds = reportUnitDao.getUnitDefinedByReportDefindId(reportDefinedId);
        Map<Integer,UnitDefinedType> unitTypeTmp = new HashMap<>();
        //逐个检查报送单元中的报送项是否为公式
        Map<ColumDefined, Integer> allFomularColums = new HashMap<ColumDefined, Integer>();
        for (UnitDefined unitDefind : allUnitDefinds) {
            Integer unitTypeInt = unitDefind.getUnit_type();
            unitTypeTmp.put(unitDefind.getUnit_id(),UnitDefinedType.getTypeByValue(unitTypeInt));
            if (UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)||
                    UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)||
                    UnitDefinedType.MANYDIMTREE.compareWith(unitTypeInt)) {
                List<SimpleColumDefined> columDefineds = reportDefinedUnitOneDimDao.getColumByUnit(String.valueOf(unitDefind.getUnit_id()));
                Map<ColumDefined, Integer> columDefinedsMap = checkFomularColum(columDefineds);
                allFomularColums.putAll(columDefinedsMap);
            }else if (UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)) {//一维静态
                List<GridColumDefined> columDefineds = reportDefinedUnitMultDimDao.getGridColumDefindsByUNit(String.valueOf(unitDefind.getUnit_id()));
                Map<ColumDefined, Integer> columDefinedsMap = checkFomularColum(columDefineds);
                allFomularColums.putAll(columDefinedsMap);
            }
        }
        //分析所有公式项是否被其他公式引用
        Map<String, ColumDefined> fomularKeyTmp = countFomularLinked(allFomularColums);
        logger.debug("公式相互引用情况:{}",fomularKeyTmp);
        //被引用次数最多的公式最先刷新
        ArrayList<Map.Entry<Integer,String>> list = new ArrayList(allFomularColums.entrySet());
        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        logger.debug("公式引用情况按大到小排序:{}",fomularKeyTmp);

        //计算公式过程中检查公式内容是否包含了公式，检查该公式是否已经更新完毕，更新未完毕的话 当前公式项暂时不动
        while(allFomularColums.size()>0){
            logger.debug("剩余公式组数据：{}",allFomularColums);
            HashSet<Object> fomularColumRempved = new HashSet<>();
            Set<ColumDefined> fomularColums = allFomularColums.keySet();
            for (ColumDefined fomularColum : fomularColums) {
                String fomular = fomularColum.getColum_formula();
                if(fomular.indexOf("SUM")<0){
                    List<String> fomularParams = fomularParamNames(fomular);
                    boolean refreshAfter = false;
                    for (String fomularParam : fomularParams) {
                        if(fomularKeyTmp.containsKey(fomularParam)){//当前公式中使用的其他项也是个公式
                            refreshAfter = true;
                        }
                    }
                    if(!refreshAfter){
                        Map<String, Map<String, Object>> fomularGroups = this.getFomularContext(fomularParams, reportId);
                        Map<String,Object> fomularGroupResult = new HashMap<>();
                        for(String col : fomularParams){
                            String vars = "#" + col + "#";
                            fomular = fomular.replace(vars, vars.replace(".","_"));
                        }
                        fomular = fomular.replace("#","FL");
                        Expression expression= AviatorEvaluator.compile(fomular);
                        Set<String> groups = fomularGroups.keySet();
                        logger.debug("刷新公式：{}",fomular);
                        logger.debug("公式参数：{}",fomularGroups);

                        for (String group : groups) {
                            Map<String, Object> fomularContext = fomularGroups.get(group);
                            Object result = expression.execute(fomularContext);
                            fomularGroupResult.put(group,result);
                        }
                        fomularColumRempved.add(fomularColum);
                        logger.debug("刷新组信息:{}",fomularGroups);
                        for (String fomularGroup : fomularGroupResult.keySet()) {
                            Object fomularGroupDATA = fomularGroupResult.get(fomularGroup);
                            UnitDefinedType unitTypeInt = unitTypeTmp.get(fomularColum.getUnit_id());
                            if (UnitDefinedType.ONEDIMSTATIC.equals(unitTypeInt)){
                                SimpleColumDefined fomularColumOne = (SimpleColumDefined) fomularColum;
                                reportCustomerDao.updateReportCustomerData(
                                        reportId,
                                        fomularColum.getUnit_id().toString(),
                                        fomularColumOne.getColum_id().toString(),null,null,fomularGroupDATA);

                            }else if (UnitDefinedType.MANYDIMTREE.equals(unitTypeInt)){
                                SimpleColumDefined fomularColumOne = (SimpleColumDefined) fomularColum;
                                String[] whereParams = fomularGroup.split("_");
                                String reportGroupId = whereParams[0];
                                String columId = whereParams[1];
                                reportCustomerDao.updateReportCustomerData(
                                        reportId,
                                        fomularColum.getUnit_id().toString(),
                                        columId,fomularColumOne.getColum_id().toString(),reportGroupId,fomularGroupDATA);

                            }else if (UnitDefinedType.ONEDIMDYNAMIC.equals(unitTypeInt)){
                                SimpleColumDefined fomularColumOne = (SimpleColumDefined) fomularColum;
                                reportCustomerDao.updateReportCustomerData(
                                        reportId,
                                        fomularColum.getUnit_id().toString(),
                                        fomularColumOne.getColum_id().toString(),fomularGroup,null,fomularGroupDATA);

                            }
                            else if (UnitDefinedType.MANYDIMSTATIC.equals(unitTypeInt)){
                                GridColumDefined fomularColumGrid = (GridColumDefined) fomularColum;
                                reportCustomerDao.updateReportCustomerData(
                                        reportId,
                                        fomularColum.getUnit_id().toString(),
                                        fomularColumGrid.getColum_id().toString(),
                                        fomularColumGrid.getDim_id().toString()
                                        ,null,fomularGroupDATA);

                            }
                        }
                        
                    }
                }else{
                    logger.debug("刷新公式：{}",fomular);
                    FomularTmpEntity fomularTmpEntity = new FomularTmpEntity();
                    fomularTmpEntity.setFomularScript(fomularColum.getColum_formula());
                    fomularTmpEntity.setReportId(new Integer(reportId));
                    fomularTmpEntity.setUnitId(fomularColum.getUnit_id().toString());
                    Object sumValue = getSumValue(fomularTmpEntity);
                    logger.debug("刷新组信息:{}",sumValue);
                    fomularColumRempved.add(fomularColum);
                }
            }

            for(Object fomularColumRemove:fomularColumRempved){
                allFomularColums.remove(fomularColumRemove);
                for (String fomularKey : fomularKeyTmp.keySet()) {
                    ColumDefined columDefined = fomularKeyTmp.get(fomularKey);
                    if(columDefined.equals(fomularColumRemove)){
                        fomularKeyTmp.remove(fomularKey);
                        break;
                    }
                }
            }

        }
        
        //循环 直到所有公式刷新完成
    }

    private Map<ColumDefined, Integer> checkFomularColum(List columDefineds){
        if(columDefineds!=null&&columDefineds.size()>0){
            Map<ColumDefined,Integer> columDefindLinkTmp = new HashMap<>();
            List<ColumDefined> simpleDefinedColums = (List<ColumDefined>) columDefineds;
            for (ColumDefined simpleDefinedColum : simpleDefinedColums) {
                if(ColumType.FORMULA.compareWith(new Integer(simpleDefinedColum.getColum_type()))){
                    if(!Strings.isNullOrEmpty(simpleDefinedColum.getColum_formula())){
                        columDefindLinkTmp.put(simpleDefinedColum,0);
                    }
                }
            }
            return columDefindLinkTmp;
        }
        return null;
    }

    private Map<String, ColumDefined> countFomularLinked(Map<ColumDefined,Integer> columDefindLinked){
        Map<String,ColumDefined> tmpMap = new HashMap<>();
        for (ColumDefined columDefined : columDefindLinked.keySet()) {
            StringBuilder keyBuilder = new StringBuilder();
            if(columDefined instanceof GridColumDefined){
                GridColumDefined gridColumDefined = (GridColumDefined) columDefined;
                keyBuilder.append(gridColumDefined.getUnit_id()).append(".").append(gridColumDefined.getColum_id())
                        .append(".").append(gridColumDefined.getDim_id());
            }else{
                SimpleColumDefined simpleColumDefined = (SimpleColumDefined) columDefined;
                keyBuilder.append(simpleColumDefined.getUnit_id()).append(".").append(simpleColumDefined.getColum_id());
            }
            tmpMap.put(keyBuilder.toString(),columDefined);
        }

        for (ColumDefined columDefined : columDefindLinked.keySet()) {
            String fomulaScript = columDefined.getColum_formula();
            String[] fomularParams = fomulaScript.split("#");
            for (String fomularParam : fomularParams) {
                if(fomularParam.indexOf(".")<0){}
                else{
                    if(tmpMap.containsKey(fomularParam)){
                        ColumDefined columDefinedLink = tmpMap.get(fomularParam);
                        Integer oldValue = columDefindLinked.get(columDefinedLink);
                        columDefindLinked.put(columDefinedLink,oldValue+1);
                    }
                }
            }
        }

        return tmpMap;
    }

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
            //循环替换，支持带点常数 #15.41#*0.2
            //String fomular = fomularTmpEntity.getFomularScript().replace("#", "FL").replace(".", "_");
            String fomular = fomularTmpEntity.getFomularScript();
            for(String col : fomularColums){
                String vars = "#" + col + "#";
                fomular = fomular.replace(vars, vars.replace(".","_"));
            }
            fomular = fomular.replace("#","FL");
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
        Map<Integer, UnitDefined> unitTmp = new HashMap<>();
        Map<String, SimpleColumDefined> fomularsTmp = this.getFomularsTmp(allFomularDefineds);
        List<FomularTmpEntity> fomularArray = new ArrayList<>();
        for (SimpleColumDefined fomularDefined : allFomularDefineds) {
            Integer unitId = fomularDefined.getUnit_id();
            if(unitId.toString().equals(selfUnitId)){
                continue;
            }

            if(!unitTmp.containsKey(unitId)){
                UnitDefined unitEntity = reportUnitDao.getReportUnit(unitId.toString());
                unitTmp.put(unitId,unitEntity);
            }

            Integer unitType = unitTmp.get(unitId).getUnit_type();

            FomularTmpEntity fomularTmpEntity = new FomularTmpEntity();
            fomularTmpEntity.setReportId(reportId);
            fomularTmpEntity.setUnitId(unitId.toString());
            fomularTmpEntity.setFomularScript(fomularDefined.getColum_formula());

            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)){
                fomularTmpEntity.setColumId(fomularDefined.getColum_id().toString());
            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitType)) {
                fomularTmpEntity.setDimensionsId(fomularDefined.getColum_id().toString());
            }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)) {
                fomularTmpEntity.setColumId(fomularDefined.getColum_id().toString());
                fomularTmpEntity.setDimensionsId(fomularDefined.getColum_id().toString());
            }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){
                fomularTmpEntity.setColumId(fomularDefined.getColum_id().toString());
                fomularTmpEntity.setDimensionsId(fomularDefined.getColum_id().toString());
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

    private UnitDefined checkUnit(String unitId){
        UnitDefined unitEntity = reportUnitDao.getReportUnit(unitId);
        return unitEntity;
    }


    private Map<String,Map<String,Object>> getFomularContext(List<String> fomularColums,String reportId){

        Map<String,Map<String,Object>> fomularGroupTmp = new HashMap<>();

        for (String fomularColum : fomularColums) {//获取公式中各个运算参数的具体值

            String fomularColumTmp = fomularColum.replace(".", "_");

            //每一个运算参数由两部分构成 第一部分是unit_id 第二部分是colum_id或dimensions_id
            String[] infos = fomularColumTmp.split("_");
            String unitId = infos[0];
            String columOrDimensionsIdDefined = infos[1];
            String dimensionsGridId = "";

            UnitDefined unitDefined = this.checkUnit(unitId);
            Integer unitType = unitDefined.getUnit_type();

            List<ReportCustomerData> reportCustomerDatas = new ArrayList<>();
            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)){
                reportCustomerDatas = reportCustomerDao.getSimpleReportCustomerDatasByColId(
                        reportId, unitId, columOrDimensionsIdDefined);
            }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitType)){
                reportCustomerDatas =
                        reportCustomerDao.getSimpleReportCustomerDatasByDimId(
                                reportId,
                                unitId, columOrDimensionsIdDefined);


            } else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)){
                reportCustomerDatas =
                        reportCustomerDao.getSimpleReportCustomerDatasByColId(reportId,
                        unitId, columOrDimensionsIdDefined);


            } else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){
                dimensionsGridId = infos.length > 2 ? infos[2] : "";
                ReportCustomerData reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataBydimensions(reportId
                        , unitId, columOrDimensionsIdDefined, dimensionsGridId);
                reportCustomerDatas.add(reportCustomerData);
            }

            for (ReportCustomerData customerData : reportCustomerDatas) {
                String reportGroupId = customerData.getReport_group_id();
                if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)){
                    reportGroupId = customerData.getDimensions_id();
                }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitType)){
                    reportGroupId = reportGroupId+"_"+customerData.getColum_id();
                }
                if(!fomularGroupTmp.containsKey(reportGroupId)){
                    Map<String,Object> fomularParams = new HashMap<>();
                    fomularGroupTmp.put(reportGroupId,fomularParams);
                }
                Map<String,Object> fomularParams = fomularGroupTmp.get(reportGroupId);
                Object formatData = dataFormat(customerData.getReport_data());
                if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){
                    fomularParams.put(
                            new StringBuilder().append("FL").append(unitId).append("_").append(columOrDimensionsIdDefined).append("_").append(dimensionsGridId).append("FL").toString(),
                            formatData);
                }else{
                    fomularParams.put(
                            new StringBuilder().append("FL").append(unitId).append("_").append(columOrDimensionsIdDefined).append("FL").toString(),
                            formatData);
                }
            }
        }

        if(fomularGroupTmp.keySet().size()>1){

            if(fomularGroupTmp.get(null)!=null&&fomularGroupTmp.get(null).size()>0){
                for (String fomularGroup : fomularGroupTmp.keySet()) {
                    if(fomularGroup!=null){
                        fomularGroupTmp.get(fomularGroup).putAll(fomularGroupTmp.get(null));
                    }
                }
            }
            fomularGroupTmp.remove(null);
        }

        return fomularGroupTmp;
    }

    private Map<String, Object> makeFomularParams(List<String> fomularColums, FomularTmpEntity fomularTmpEntity){
        Map<String,Object> fomularParams = new HashMap<>();

        for (String fomularColum : fomularColums) {//获取公式中各个运算参数的具体值

            String fomularColumTmp = fomularColum.replace(".", "_");

            //每一个运算参数由两部分构成 第一部分是unit_id 第二部分是colum_id或dimensions_id
            String[] infos = fomularColumTmp.split("_");
            String unitId = infos[0];
            String columOrDimensionsIdDefined = infos[1];
            String dimensionsGridId = "";

            UnitDefined unitDefined = this.checkUnit(unitId);
            Integer unitType = unitDefined.getUnit_type();

            ReportCustomerData reportCustomerData = null;
            if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitType)){
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
            } else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitType)){
                String dimensionsId = fomularTmpEntity.getDimensionsId();
                reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataBydimensions(
                        fomularTmpEntity.getReportId().toString(), unitId, columOrDimensionsIdDefined, dimensionsId);
            } else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){
                dimensionsGridId = infos.length > 2 ? infos[2] : fomularTmpEntity.getDimensionsId();
                reportCustomerData = reportCustomerDao.getSimpleReportCustomerDataBydimensions(
                        fomularTmpEntity.getReportId().toString(), unitId, columOrDimensionsIdDefined, dimensionsGridId);
            }

            Object formatData = dataFormat(reportCustomerData.getReport_data());
            if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitType)){
                fomularParams.put(
                        new StringBuilder().append("FL").append(unitId).append("_").append(columOrDimensionsIdDefined).append("_").append(dimensionsGridId).append("FL").toString(),
                        formatData);
            }else{
                fomularParams.put(
                        new StringBuilder().append("FL").append(unitId).append("_").append(columOrDimensionsIdDefined).append("FL").toString(),
                        formatData);
            }
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
