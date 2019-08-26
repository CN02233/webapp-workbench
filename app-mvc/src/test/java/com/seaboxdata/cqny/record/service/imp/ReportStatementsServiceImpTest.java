package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.SubmitReportRequestEntity;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

public class ReportStatementsServiceImpTest extends AbstractTestService {

    @Resource
    private ReportStatementsService reportStatementsService;

    @Resource
    private SubmitReportService submitReportService;

    @Test
    public void testCopyReportDefined() throws ParseException {
        Integer newReportDefinedId = reportStatementsService.copyReportDefined("1");
//        Integer newReportDefinedId = reportStatementsService.copyReportDefined("26");
        SubmitReportRequestEntity submitReportRequestEntity = new SubmitReportRequestEntity();
//        submitReportRequestEntity.setDefined_id("1397196");
//        submitReportRequestEntity.setDefined_id("26");
        submitReportRequestEntity.setDefined_id(newReportDefinedId.toString());
        submitReportRequestEntity.setReport_data_start("20190504");
        submitReportRequestEntity.setReport_data_end("20190518");
        submitReportRequestEntity.setReport_start_date("20190501");
        submitReportRequestEntity.setReport_end_date("20190630");
        submitReportService.doSubmit(submitReportRequestEntity);

    }

    @Test
    public void testGetDefinedOriginTreeById() {
        Origin result = reportStatementsService.getDefinedOriginTreeById("1");
        System.out.println(result);
    }
}