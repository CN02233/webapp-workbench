package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitTreeDimService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDefinedUnitTreeDimServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedUnitTreeDimService reportDefinedUnitTreeDimService;

    @Test
    public void testPagerTreedimList() {
        PageResult result = reportDefinedUnitTreeDimService.pagerTreedimList(9, 1, 10);
        System.out.println(result);
    }
}