package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportCustomerServiceImpTest extends AbstractTestService {

    @Resource
    private ReportCustomerService reportCustomerService;

    @Test
    public void testPagerReport() {
        reportCustomerService.pagerReport(1,10,1);
    }

    @Test
    public void testGetUnitContext() {
        reportCustomerService.getUnitContext("1","1","1");
    }
}