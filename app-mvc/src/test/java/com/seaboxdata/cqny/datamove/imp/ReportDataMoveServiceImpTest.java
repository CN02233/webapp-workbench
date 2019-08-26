package com.seaboxdata.cqny.datamove.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.datamove.ReportDataMoveService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDataMoveServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDataMoveService reportDataMoveService;

    @Test
    public void testDoMove() {
        reportDataMoveService.doMove();
    }
}