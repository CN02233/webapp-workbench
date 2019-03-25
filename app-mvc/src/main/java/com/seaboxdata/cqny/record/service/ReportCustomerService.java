package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.ReportUnitCustomerContext;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.webapp.support.page.PageResult;

import java.util.ArrayList;

public interface ReportCustomerService {
    PageResult pagerReport( Integer currPage, Integer pageSize, Integer userId);
    ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType);

    void updateSimpleUnitContext(ArrayList<SimpleColumDefined> simpleColumDefineds,ArrayList<ReportCustomerData> columDatas);
}
