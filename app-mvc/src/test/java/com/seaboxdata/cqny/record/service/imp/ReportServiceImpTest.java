package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

public class ReportServiceImpTest extends AbstractTestService {

    @Resource
    private ReportService reportService;

    @Test
    public void testLoadReport() {
        reportService.loadReport("8");
    }

    @Test
    public void testCreateReport() throws IOException {
        String createResult = reportService.createReport("test.xlsx", "测试报表111");
        System.out.println(createResult);
    }

    @Test
    public void testReportList() {
        PageResult result = reportService.reportList(2, 2, 20);
        System.out.println(result);
    }
}