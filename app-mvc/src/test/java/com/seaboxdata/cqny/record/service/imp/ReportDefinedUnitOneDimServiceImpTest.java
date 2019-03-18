package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ReportDefinedUnitOneDimServiceImpTest extends AbstractTestService {

    @Resource
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @Test
    public void testAddSaveOnedim() {
        ColumDefined columDefined = new ColumDefined();
        columDefined.setColum_name("insertId");
        columDefined.setColum_name_cn("测试插入");
        columDefined.setColum_data_type("1");
        columDefined.setMin_value(0);
        columDefined.setMin_value(100);
        reportDefinedUnitOneDimService.addSaveOnedim(columDefined);
    }
}