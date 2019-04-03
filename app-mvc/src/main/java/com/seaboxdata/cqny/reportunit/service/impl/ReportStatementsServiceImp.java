package com.seaboxdata.cqny.reportunit.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.reportunit.dao.IReportStatementsDao;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.seaboxdata.cqny.reportunit.service.ReportStatementsService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reportStatements")
public class ReportStatementsServiceImp implements ReportStatementsService {

    @Autowired
    private IReportStatementsDao reportStatementsDao;


    @Override
    public PageResult listReportStatements(int currPage, int pageSize) {
        Page<StatementsEntity> reportStatementsPage = reportStatementsDao.listReportStatements(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
    }

    @Override
    public void addReportStatements(StatementsEntity reportStatements) {
        if(reportStatements.getStatements_id()!=null){
            reportStatementsDao.updateReportStatements(reportStatements);
        }else{
            reportStatementsDao.addReportStatements(reportStatements);
        }
    }

    @Override
    public void deleteById(String statementsId) {
        reportStatementsDao.deleteById(statementsId);
    }

    @Override
    public PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id) {
        Page<StatementsEntity> reportStatementsPage = reportStatementsDao.listReportStatementsByUser(currPage, pageSize,user_id);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportStatementsPage);
        return pageResult;
    }

    @Override
    public StatementsEntity getReportDefinedById(Integer definedId) {
        StatementsEntity reportDefined = reportStatementsDao.getReportDefinedById(definedId);
        return reportDefined;
    }

    @Override
    public List<Origin> getOriginsByReportDefind(String reportDefindId) {
        return reportStatementsDao.getOriginsByReportDefind(reportDefindId);
    }
}
