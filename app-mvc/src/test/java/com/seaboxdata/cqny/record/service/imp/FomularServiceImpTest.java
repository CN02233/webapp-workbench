package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.FomularService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class FomularServiceImpTest extends AbstractTestService {

    @Resource
    private FomularService fomularService;

    @Test
    public void testRefreshFomularDatas() {
        fomularService.refreshFomularDatas("1","1");
    }
}