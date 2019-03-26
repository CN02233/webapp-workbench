package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.webapp.support.page.PageResult;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(StatementsEntity reportStatements);

    void deleteById(String unitId);
}
