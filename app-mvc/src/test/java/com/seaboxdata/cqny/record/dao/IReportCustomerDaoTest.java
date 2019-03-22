package com.seaboxdata.cqny.record.dao;

import com.AbstractTestService;
import com.github.pagehelper.Page;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class IReportCustomerDaoTest extends AbstractTestService {

    @Resource
    private IReportCustomerDao reportCustomerDao;

    @Test
    public void testtttt(){
        Object pageData = reportCustomerDao.pageReport(1, 10, 1);
        System.out.println(pageData);
    }
}