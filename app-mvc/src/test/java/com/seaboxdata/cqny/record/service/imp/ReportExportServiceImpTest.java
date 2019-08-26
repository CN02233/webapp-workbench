package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportExportService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportExportServiceImpTest extends AbstractTestService {

    @Autowired
    private ReportExportService reportExportService;

    @Test
    public void testDoExport() {
//        reportExportService.doExport("341");
        reportExportService.doExport("3274");
    }
}