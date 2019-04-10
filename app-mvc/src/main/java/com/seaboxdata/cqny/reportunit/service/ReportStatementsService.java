package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportDefinedStatus;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(StatementsEntity reportDefined);

    void deleteById(String unitId);

    PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id);

    StatementsEntity getReportDefinedById(Integer integer);

    void saveDefinedAndOriginAssign(String[] originIds, String definedId);

    List<String> getDefinedAndOriginAssignById(String definedId);

    void delDefinedAndOriginAssign(String definedId);

    void changeDeindStatus(String definedId, ReportDefinedStatus status);
}
