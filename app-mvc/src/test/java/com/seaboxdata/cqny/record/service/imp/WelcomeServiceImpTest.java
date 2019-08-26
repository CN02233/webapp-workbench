package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.WelcomeService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class WelcomeServiceImpTest extends AbstractTestService {

    @Resource
    private WelcomeService welcomeService;

    @Test
    public void testJobList() {

//        welcomeService.jobList(13585728,1,20);
        welcomeService.jobList(55643376,1,20);
    }

    @Test
    public void testGetReportSumInfo() {
        List<Map<String, Integer>> result = welcomeService.getReportSumInfo(3701432);
        System.out.println(result);
    }
}