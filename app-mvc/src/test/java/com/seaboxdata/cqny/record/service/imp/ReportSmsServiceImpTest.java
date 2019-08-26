package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.ReportSmsConfig;
import com.seaboxdata.cqny.record.service.ReportSmsService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ReportSmsServiceImpTest extends AbstractTestService {

    @Resource
    private ReportSmsService reportSmsService;

    @Test
    public void testCreateSmsJob() {
        ReportSmsConfig reportSMsConfig = new ReportSmsConfig();
        reportSMsConfig.setPre_days("10");
        reportSMsConfig.setReport_defined_id("26");
        reportSMsConfig.setCross_holiday("Y");
        reportSMsConfig.setConfig_name("Junit测试插入");
        reportSMsConfig.setSend_time_str("09:30");
        reportSMsConfig.setSms_template_id("SMS_000001");
//        reportSmsService.createSmsJob(reportSMsConfig);
    }

    @Test
    public void testQueryTemplateContext() {
        System.out.println(reportSmsService.queryTemplateContext("SMS_168125050"));
    }

    @Test
    public void testSendSmsForCustomer() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.parse("2019-12-18");
//        reportSmsService.sendSmsForCustomer("SMS_20190618","26",format.parse("2019-12-18"));
    }
}