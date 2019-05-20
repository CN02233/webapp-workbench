package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitMultDimService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDefinedUnitMultDimServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedUnitMultDimService reportDefinedUnitMultDimService;

    @Test
    public void testGetColumByUnitOutUninon() {
        System.out.println(reportDefinedUnitMultDimService.getColumByUnitOutUninon("244"));
    }
}