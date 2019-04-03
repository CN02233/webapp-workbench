package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.SubmitReportService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class SubmitReportServiceImpTest extends AbstractTestService {

    @Resource
    private SubmitReportService submitReportService;

    @Test
    public void testDoSubmit() {
        submitReportService.doSubmit("1");
    }
}