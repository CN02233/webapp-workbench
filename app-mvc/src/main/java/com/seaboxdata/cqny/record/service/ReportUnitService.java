package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportUnitService {

    PageResult listReportUnit(int currPage, int pageSize);

    void addReportUnit(UnitDefined reportUnit);

    void deleteById(String unitId);

    UnitDefined getReportUnit(String unitId);

    List getDefinedColums(String unitId, String unitType);

    List<UnitDefined> getUnitDefinedByReportDefindId(String reportDefinedId);
}
