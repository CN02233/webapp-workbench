package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.SubmitReportRequestEntity;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.ParseException;

public class SubmitReportServiceImpTest extends AbstractTestService {

    @Resource
    private SubmitReportService submitReportService;

    @Test
    public void testDoSubmit() throws ParseException {
        SubmitReportRequestEntity submitReportRequestEntity = new SubmitReportRequestEntity();
        submitReportRequestEntity.setDefined_id("1");
        submitReportRequestEntity.setReport_data_start("20190504");
        submitReportRequestEntity.setReport_data_end("20190518");
        submitReportRequestEntity.setReport_start_date("20190501");
        submitReportRequestEntity.setReport_end_date("20190531");



        submitReportService.doSubmit(submitReportRequestEntity);
    }
}