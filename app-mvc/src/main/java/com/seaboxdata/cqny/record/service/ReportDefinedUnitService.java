package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

import java.util.List;

public interface ReportDefinedUnitService {
    PageResult pagerReportUnitDefinedList(Integer currPage, Integer pageSize,Integer reportDefinedId);
    List getDefinedColums(String unitId, String unitType);
}
