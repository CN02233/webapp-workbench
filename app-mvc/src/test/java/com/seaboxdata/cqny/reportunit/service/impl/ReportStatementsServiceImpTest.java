package com.seaboxdata.cqny.reportunit.service.impl;

import com.AbstractTestService;
import com.seaboxdata.cqny.reportunit.service.ReportStatementsService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportStatementsServiceImpTest extends AbstractTestService {

    @Resource
    private ReportStatementsService reportStatementsService;

    @Test
    public void testGetReportDefinedById() {
        System.out.println(reportStatementsService.getDefinedOriginsById("1"));
    }
}