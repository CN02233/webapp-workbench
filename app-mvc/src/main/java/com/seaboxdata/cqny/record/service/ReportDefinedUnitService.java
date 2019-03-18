package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

public interface ReportDefinedUnitService {
    PageResult pagerReportUnitDefinedList(Integer currPage, Integer pageSize,Integer reportDefinedId);
}
