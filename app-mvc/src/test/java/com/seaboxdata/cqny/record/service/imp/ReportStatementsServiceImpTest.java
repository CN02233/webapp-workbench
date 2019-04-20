package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class ReportStatementsServiceImpTest extends AbstractTestService {

    @Resource
    private ReportStatementsService reportStatementsService;

    @Test
    public void testCopyReportDefined() {
        reportStatementsService.copyReportDefined("1");
    }
}