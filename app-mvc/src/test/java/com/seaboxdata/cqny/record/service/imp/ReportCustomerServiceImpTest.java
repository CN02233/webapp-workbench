package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testSignReport() {
        Map<String,String> siginInfos = new HashMap<>();
        siginInfos.put("report_id","1");
        siginInfos.put("report_cust_name","测试2");
        siginInfos.put("report_account_name","测试11");
        siginInfos.put("report_leader_name","测21");
        reportCustomerService.signReport(siginInfos);
    }
}