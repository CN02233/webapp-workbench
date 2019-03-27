package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDefinedUnitOneDimServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @Test
    public void testAddSaveOnedim() {
        SimpleColumDefined simpleColumDefined = new SimpleColumDefined();
        simpleColumDefined.setColum_name("insertId");
        simpleColumDefined.setColum_name_cn("测试插入");
        simpleColumDefined.setColum_type("1");
        simpleColumDefined.setMin_value(0);
        simpleColumDefined.setMin_value(100);
        reportDefinedUnitOneDimService.addSaveOnedim(simpleColumDefined);
    }
}