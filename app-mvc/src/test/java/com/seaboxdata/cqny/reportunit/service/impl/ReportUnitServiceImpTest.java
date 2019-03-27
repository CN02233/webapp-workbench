package com.seaboxdata.cqny.reportunit.service.impl;

import com.AbstractTestService;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class ReportUnitServiceImpTest extends AbstractTestService {

    @Resource
    private ReportUnitService reportUnitService;

    @Test
    public void testGetReportUnit() {
        System.out.println(reportUnitService.getReportUnit("1"));
    }
}