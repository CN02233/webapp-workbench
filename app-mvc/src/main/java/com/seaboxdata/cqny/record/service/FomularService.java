package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.FomularTmpEntity;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;

import java.util.List;

public interface FomularService {

    Object getFomularData(FomularTmpEntity fomularTmpEntity);

    Object getSumValue(FomularTmpEntity fomularTmpEntity);

    Object runFomular(FomularTmpEntity fomularTmpEntity);

    ReportCustomerData makeReportCustDataByFomular(FomularTmpEntity fomularTmpEntity);

    List<ReportCustomerData> refreshFomularForCustInput(List<ReportCustomerData> reportCustomerDatas);

}
