package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportUnitCustomerContext;
import com.webapp.support.page.PageResult;

public interface ReportCustomerService {
    PageResult pagerReport( Integer currPage, Integer pageSize, Integer userId);
    ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType);
}
