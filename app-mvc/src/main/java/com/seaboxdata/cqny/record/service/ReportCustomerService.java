package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.ReportUnitCustomerContext;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.webapp.support.page.PageResult;

import java.util.ArrayList;
import java.util.Map;

public interface ReportCustomerService {
    PageResult pagerReport( Integer currPage, Integer pageSize, Integer userId);
    ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType);

    void updateOrInsertSimpleUnitContext(ArrayList<SimpleColumDefined> simpleColumDefineds, ArrayList<ReportCustomerData> columDatas,boolean isUpdate);

    ReportCustomer checkReportCustomer(String reportId);

    Integer checkNextStepUnitId(String reportId);

    void updateStep(String reportId);

    Map<String,String> validateSimpleUnitByColum(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    Map<String,String> validateSimpleUnitByDimensions(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    void overrideSimpleUnitContext(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    void updateOrInsertGridUnitContext(ArrayList<GridColumDefined> simpleColumDefineds, ArrayList<ReportCustomerData> columDatas, boolean isUpdate);
}
