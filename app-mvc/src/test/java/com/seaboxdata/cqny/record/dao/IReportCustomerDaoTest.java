package com.seaboxdata.cqny.record.dao;

import com.AbstractTestService;
import com.github.pagehelper.Page;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;

public class IReportCustomerDaoTest extends AbstractTestService {

    @Resource
    private IReportCustomerDao reportCustomerDao;

    @Test
    public void testtttt(){
        Object pageData = reportCustomerDao.pageReport(1, 10, 1);

        Object result = reportCustomerDao.sumColumForDimensions("1", "19","61");
        System.out.println(result);

    }
}