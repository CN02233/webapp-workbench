package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(StatementsEntity reportDefined);

    void deleteById(String unitId);

    PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id);

    void saveDefinedAndOriginAssign(String[] originIds, String definedId);

    List<String> getDefinedAndOriginAssignById(String definedId);

    void delDefinedAndOriginAssign(String definedId);
}
