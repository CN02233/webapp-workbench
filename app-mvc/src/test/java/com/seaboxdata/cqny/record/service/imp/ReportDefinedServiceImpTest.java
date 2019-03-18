package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDefinedServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedService reportDefinedService;

    @Test
    public void testReportDefinedList() {
        PageResult result = reportDefinedService.reportDefinedList(1, 10);
        System.out.println(result.toString());
    }
}