package com.seaboxdata.cqny.reportunit.service;

import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportUnitService {

    PageResult listReportUnit(int currPage, int pageSize);

    void addReportUnit(UnitEntity reportUnit);

    void deleteById(String unitId);

    UnitEntity getReportUnit(String unitId);

    List getDefinedColums(String unitId, String unitType);

    List<UnitEntity> getUnitDefinedByReportDefindId(String reportDefinedId);
}
