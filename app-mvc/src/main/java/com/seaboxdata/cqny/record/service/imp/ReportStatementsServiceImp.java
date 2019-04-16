package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.dao.IReportStatementsDao;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("reportStatements")
public class ReportStatementsServiceImp implements ReportStatementsService {
    private static Logger logger = LoggerFactory.getLogger(ReportStatementsServiceImp.class);

    @Autowired
    private IReportStatementsDao reportStatementsDao;

    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

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
    public ReportDefinedEntity getReportDefinedById(Integer definedId) {
        ReportDefinedEntity reportDefined = reportStatementsDao.getReportDefinedById(definedId);
        return reportDefined;
    }
}