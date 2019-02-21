package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportServiceImpTest extends AbstractTestService {

    @Resource
    private ReportService reportService;

    @Test
    public void testLoadReport() {
        reportService.loadReport("test.xlsx");
    }

    @Test
    public void testCreateReport() {
        reportService.createReport("test.xlsx");
    }
}