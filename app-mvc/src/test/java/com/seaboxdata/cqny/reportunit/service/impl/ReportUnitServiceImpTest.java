package com.seaboxdata.cqny.reportunit.service.impl;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportUnitService;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportUnitServiceImpTest extends AbstractTestService {

    @Resource
    private ReportUnitService reportUnitService;

    @Test
    public void testGetReportUnit() {
        System.out.println(reportUnitService.getReportUnit("1"));
    }
}