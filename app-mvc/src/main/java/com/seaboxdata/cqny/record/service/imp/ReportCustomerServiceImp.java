package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.ReportUnitCustomerContext;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitService;
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
    private ReportDefinedUnitService reportDefinedUnitService;


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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSimpleUnitContext(ArrayList<SimpleColumDefined> simpleColumDefineds,ArrayList<ReportCustomerData> columDatas) {
        Map<String,SimpleColumDefined> fomularsTmp = new HashMap<>();
        Map<String,String> fomularMap = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            simpleColumDefineds.forEach(simpleColumDefined->{
                Integer columType = new Integer(simpleColumDefined.getColum_data_type());
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
                if(fomularsTmp.containsKey(unitId+"_"+columnId)){
                    fomularMap.put(new StringBuilder().append(reportId).append("_").append(unitId).append("_").append(columnId).toString(),
                            fomularsTmp.get(unitId+"_"+columnId).getColum_formula());
                }else
                    reportCustomerDao.updateUnitContext(columData);
            });
        }

        if(fomularMap!=null&&fomularMap.size()>0){
            fomularMap.keySet().forEach(fomularKey->{
                String[] params = fomularKey.split("_");
                String reportId = params[0];
                String unitId = params[1];
                String columnId = params[2];
                Object fomularDataResult = refreshSimpleFomularData(reportId, fomularMap.get(fomularKey));
                ReportCustomerData comularData = new ReportCustomerData();
                comularData.setReport_id(new Integer(reportId));
                comularData.setUnit_id(unitId);
                comularData.setColum_id(columnId);
                comularData.setReport_data(String.valueOf(fomularDataResult));
                reportCustomerDao.updateUnitContext(comularData);
            });
        }


    }

    @Override
    public Object checkUnitStep(String reportId) {
        ReportCustomer reportCustomer = reportCustomerDao.checkReportCustomer(reportId);

        return null;
    }

    public Object refreshSimpleFomularData(String reportId,String columFomular){

        int value = -1;
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
            fomularColums.forEach(fomularColum->{
                String fomularColumTmp = fomularColum.replace(".", "_");
                String[] infos = fomularColumTmp.split("_");
                String unitId = infos[0];
                String columId = infos[1];
                ReportCustomerData reportCustomerData = reportCustomerDao.getSimpleReportCustomerData( reportId, unitId, columId);
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
                        new StringBuilder().append("FL").append(unitId).append("_").append(columId).append("FL").toString(),
                        dataFormatter);
            });
        }

        String fomular = columFomular.replace("#", "FL").replace(".", "_");
        Expression expression= AviatorEvaluator.compile(fomular);
        Object result = expression.execute(fomularParams);

        return result;
    }
}
