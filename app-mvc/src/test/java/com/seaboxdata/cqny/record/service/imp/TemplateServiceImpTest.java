package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.service.TemplateService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class TemplateServiceImpTest extends AbstractTestService {

    @Resource
    private TemplateService templateServcice;

    @Test
    public void testLoadTemplate() {
        templateServcice.loadTemplate("1");
    }

}