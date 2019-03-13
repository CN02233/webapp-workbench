package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.service.TemplateService;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TemplateServiceImpTest extends AbstractTestService {

    @Resource
    private TemplateService templateServcice;

    @Test
    public void testLoadTemplate() {
        ExcelTemplate result = templateServcice.loadTemplate("2");
        System.out.println(result.toString());
    }

    public void testLoadTemplate1() {
    }

    @Test
    public void testUploadTemplate() throws IOException {
//        String createResult = reportService.createReport("test.xlsx", "测试报表111");
        File file = new File("/home/song/myfiles/jobfiles/DFJX/codings/cqnytemplates/realtest.xlsx");
        templateServcice.uploadTemplate("junit上传1","48",file);
    }

    @Test
    public void testLoadTemplateBasicInfo() {
        List<ExcelTemplate> result = templateServcice.loadTemplateBasicInfo("2");
        System.out.println(result);
    }
}