package com.seaboxdata.cqny.record.dao;

import com.AbstractTestService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class IReportDaoTest extends AbstractTestService {

    @Resource
    private IReportDao reportDao;

    @Test
    public void testCopyTemplateContext(){
        reportDao.copyTemplateContext(16,8);
    }

}