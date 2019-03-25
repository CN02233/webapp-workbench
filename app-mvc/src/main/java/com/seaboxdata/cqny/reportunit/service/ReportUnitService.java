package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.webapp.support.page.PageResult;

public interface ReportUnitService {

    PageResult listReportUnit(int currPage, int pageSize);

    void addReportUnit(UnitEntity reportUnit);

    void deleteById(String unitId);
}
