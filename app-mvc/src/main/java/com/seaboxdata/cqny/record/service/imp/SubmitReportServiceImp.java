package com.seaboxdata.cqny.record.service.imp;

import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubmitReportServiceImp implements SubmitReportService {

    @Autowired
    private ReportDefinedService reportDefinedService;

    @Autowired
    private ReportUnitService reportUnitService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Override
    public void doSubmit(String reportDefinedId) {
        ReportDefined reportDefined = getReportDefined(reportDefinedId);
        List<Integer> alOrigin = getAllOrigin(reportDefinedId);
        List<Integer> reportIds = createReportBaseData(reportDefined, alOrigin);
    }

    private ReportDefined getReportDefined(String reportDefinedId){
        ReportDefined reportDefined = reportDefinedService.getReportDefinedById(new Integer(reportDefinedId));
        List<UnitEntity> unitList = reportUnitService.getUnitDefinedByReportDefindId(reportDefinedId);
        if(unitList!=null){
            for (UnitEntity unitEntity : unitList) {
                List columList = reportUnitService.getDefinedColums(unitEntity.getUnit_id().toString(), unitEntity.getUnit_type().toString());
                unitEntity.setColums(columList);
            }
        }
//        reportDefined.setUnits(unitList);
        return reportDefined;
    }

    private List<Integer> getAllOrigin(String reportDefindId){
        return null;
    }

    private List<Integer> createReportBaseData(ReportDefined reportDefined, List<Integer> allOrigin){
        List<Integer> reportBaseIds = new ArrayList<>();
        ReportCustomer reportCustomer = new ReportCustomer();
        reportCustomer.setReport_defined_id(reportDefined.getDefined_id());
        reportCustomer.setReport_name(reportDefined.getDefined_name());
        reportCustomer.setReport_origin(0);
        reportCustomer.setCreate_date(new Date());
        for (Integer originId : allOrigin) {
            reportCustomer.setReport_origin(originId);
            reportCustomerService.createReportCustomer(reportCustomer);
            reportBaseIds.add(reportCustomer.getReport_id());
        }
        return reportBaseIds;

//        reportCustomer.setReport_end_date(de);
    }

    private void createReportDefaultData(ReportDefined reportDefined,List<Integer> reportIds){
        List<UnitDefined> unitDefinds = reportDefined.getUnits();
        for (UnitDefined unitDefind : unitDefinds) {

        }
    }
}
