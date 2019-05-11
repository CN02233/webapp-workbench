package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.ReportStatementsService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

public class ReportStatementsServiceImpTest extends AbstractTestService {

    @Resource
    private ReportStatementsService reportStatementsService;

    @Test
    public void testCopyReportDefined() {
        reportStatementsService.copyReportDefined("1");
    }

    @Test
    public void testGetDefinedOriginTreeById() {
        Origin result = reportStatementsService.getDefinedOriginTreeById("1");
        System.out.println(result);
    }
}