package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportCustomerServiceImpTest extends AbstractTestService {

    @Resource
    private ReportCustomerService reportCustomerService;

    @Test
    public void testPagerReport() {
//        reportCustomerService.pagerReport(1,10,1);
    }

    @Test
    public void testGetUnitContext() {
        reportCustomerService.getUnitContext("1","1", "1");
    }

    @Test
    public void testCheckUnitStep() {
        reportCustomerService.checkReportCustomer("1");
    }

    public void testDoRefreshSimpleFomular() {
    }

    public void testPagerReport1() {
    }

    @Test
    public void testGetGridContext() {
        System.out.println (reportCustomerService.getGridContext("1","244"));
    }
}