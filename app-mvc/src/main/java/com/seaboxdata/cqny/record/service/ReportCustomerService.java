package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.*;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.webapp.support.page.PageResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ReportCustomerService {
    PageResult pagerReport( Integer currPage, Integer pageSize, List<Integer> originIds);
    ReportUnitCustomerContext getUnitContext(String reportId, String unitId, String unitType);
    void createReportCustomer(ReportCustomer reportCustomer);
    void updateOrInsertSimpleUnitContext(ArrayList<SimpleColumDefined> simpleColumDefineds, ArrayList<ReportCustomerData> columDatas,boolean isUpdate);

    ReportCustomer checkReportCustomer(String reportId);

    Integer checkNextStepUnitId(String reportId);

    void updateStep(String reportId);

    Map<String,String> validateSimpleUnitByColum(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    Map<String,String> validateSimpleUnitByDimensions(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    void overrideSimpleUnitContext(ArrayList<SimpleColumDefined> definedColums, ArrayList<ReportCustomerData> columDatas);
    void updateOrInsertGroupUnitContext(ArrayList<SimpleColumDefined> simpleColumDefineds, ArrayList<ReportCustomerData> columDatas,boolean isUpdate);
    void updateOrInsertGridUnitContext(ArrayList<GridColumDefined> simpleColumDefineds, ArrayList<ReportCustomerData> columDatas, boolean isUpdate);
    Map<String,Object> checkCustOrFomular(ArrayList<SimpleColumDefined> simpleColumDefineds,
                                          ArrayList<ReportCustomerData> columDatas);

    Object getSimpleFomularData(FomularTmpEntity fomularTmpEntity);

    void updateReportCustomerStatus(String reportId, ReportStatus reportStatus);

    Map<ReportStatus, Integer> getReportInfos(Integer userOriginId);

    PageResult getChildrenReportInfos(Integer currPage, Integer pageSize, List<Integer> origins );
}
