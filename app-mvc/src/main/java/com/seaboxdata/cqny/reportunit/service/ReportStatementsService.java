package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(ReportDefinedEntity reportDefined);

    void deleteById(String unitId);

    PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id);

    ReportDefinedEntity getReportDefinedById(Integer integer);

    void saveDefinedAndOriginAssign(String[] originIds, String definedId);

    List<String> getDefinedAndOriginAssignById(String definedId);

    void delDefinedAndOriginAssign(String definedId);

    void changeDeindStatus(String definedId, ReportDefinedStatus status);

    List<Origin> getDefinedOriginsById(String definedId);
}
