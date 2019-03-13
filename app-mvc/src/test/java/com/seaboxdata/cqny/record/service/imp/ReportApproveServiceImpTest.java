package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportApproveService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportApproveServiceImpTest extends AbstractTestService {

    @Resource
    private ReportApproveService reportApproveService;

    @Test
    public void testPageReportApproves() {
        reportApproveService.pageReportApproves(1,1,10);
    }
}