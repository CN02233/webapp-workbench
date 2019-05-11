package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.config.ReportDefinedStatus;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import com.webapp.support.page.PageResult;

import java.util.HashMap;
import java.util.List;

public interface ReportStatementsService {

    PageResult listReportStatements(int currPage, int pageSize);

    void addReportStatements(ReportDefinedEntity reportDefined);

    void deleteById(String unitId);

    PageResult listReportStatementsByUser(int currPage, int pageSize, int user_id,String originId);

    ReportDefinedEntity getReportDefinedById(Integer integer);

    void saveDefinedAndOriginAssign(List originIds, String definedId);

    List<String> getDefinedAndOriginAssignById(String definedId);

    void delDefinedAndOriginAssign(String definedId);

    void changeDeindStatus(String definedId, ReportDefinedStatus status);

    List<Origin> getDefinedOriginsById(String definedId);

    List<HashMap<String,String>> getOriginsByUserId(int user_id);

    void copyReportDefined(String definedId);

    void updateReportDefined(ReportDefinedEntity reportDefined);

    List<Origin> getAuthOriginTree(String reportDefinedId);

    Origin getDefinedOriginTreeById(String definedId);
}
