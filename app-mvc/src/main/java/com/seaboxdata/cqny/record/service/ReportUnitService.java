package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface ReportUnitService {

    PageResult listReportUnit(int currPage, int pageSize,String reportDefindId);

    void addReportUnit(UnitDefined reportUnit);

    void deleteById(String unitId,String unitType);

    UnitDefined getReportUnit(String unitId);

    List getDefinedColums(String unitId, String unitType);

    List<UnitDefined> getUnitDefinedByReportDefindId(String reportDefinedId);

    Map<Integer, UnitDefined> copyReportUnit(Integer fromDefindId, Integer toDefindId);
}
