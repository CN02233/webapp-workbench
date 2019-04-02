package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportDefinedService {
    PageResult reportDefinedList(Integer currPage, Integer pageSize);
    ReportDefined getReportDefinedById(Integer definedId);
    List<UnitDefined> getUnitDefinedsByRepotDefinedId(Integer definedId);
    void activeReportDefined(String definedId);
}
