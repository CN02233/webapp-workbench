package com.seaboxdata.cqny.origin.service.impl;

import com.AbstractTestService;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CqnyUserServiceImpTest extends AbstractTestService {

    @Resource
    private CqnyUserService cqnyUserService;

    @Test
    public void testPageCqnyUser() {
        List<Integer> orginLIst = new ArrayList();
        orginLIst.add(new Integer("1100000"));
        orginLIst.add(new Integer("1101000"));
        orginLIst.add(new Integer("1100000"));
        orginLIst.add(new Integer("1100000"));
//        cqnyUserService.pageCqnyUser(1,10,null,orginLIst);
    }

    @Test
    public void testSelectOriginType() {
        cqnyUserService.selectOriginType("5566352","2");
    }
}