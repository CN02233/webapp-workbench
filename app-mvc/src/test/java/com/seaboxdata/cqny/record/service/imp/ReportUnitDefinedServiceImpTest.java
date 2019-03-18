package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportUnitDefinedServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedUnitService reportDefinedUnitService;
    @Test
    public void testPagerReportUnitDefinedList() {
        PageResult result = reportDefinedUnitService.pagerReportUnitDefinedList(1, 10, 1);
        System.out.println(result);
    }
}