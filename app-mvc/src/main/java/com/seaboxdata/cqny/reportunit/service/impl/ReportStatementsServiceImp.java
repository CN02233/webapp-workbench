package com.seaboxdata.cqny.reportunit.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.reportunit.dao.IReportStatementsDao;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportStatementsService;
import com.webapp.support.page.PageResult;
import com.workbench.auth.user.service.imp.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Service("reportStatements")
public class ReportStatementsServiceImp implements ReportStatementsService {
    private static Logger logger = LoggerFactory.getLogger(ReportStatementsServiceImp.class);

    @Autowired
    private IReportStatementsDao reportStatementsDao;


    @Override
    public PageResult listReportStatements(int currPage, int pageSize) {
        Page<StatementsEntity> reportStatementsPage = reportStatementsDao.listReportStatements(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
    }

    @Override
    public void addReportStatements(StatementsEntity reportDefined) {

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
    public PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id) {
        Page<StatementsEntity> reportStatementsPage = reportStatementsDao.listReportStatementsByUser(currPage, pageSize,user_id);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
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
}
