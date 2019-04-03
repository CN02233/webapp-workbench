package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(StatementsEntity reportStatements);

    void deleteById(String unitId);

    PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id);

    StatementsEntity getReportDefinedById(Integer integer);

    List<Origin> getOriginsByReportDefind(String reportDefindId);
}
