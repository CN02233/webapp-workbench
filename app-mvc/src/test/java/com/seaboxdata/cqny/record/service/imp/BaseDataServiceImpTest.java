package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.BaseData;
import com.seaboxdata.cqny.record.service.BaseDataService;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

public class BaseDataServiceImpTest extends AbstractTestService {

    @Resource
    private BaseDataService baseDataService;

    @Test
    public void testListAllBaseDatas() {
        List<BaseData> result = baseDataService.listAllBaseDatas();
        System.out.println(result);
    }

    @Test
    public void testListBaseDatas() {
        Page<BaseData> result = baseDataService.listBaseDatas(1, 10);
        System.out.println(result);
    }

    @Test
    public void testCreateBaseData() {
        Random random = new Random();
        for(int i=0;i<20;i++){
            int randowmVal = random.nextInt(10);
            BaseData baseData = new BaseData();
            baseData.setCreateTime("2019-02-20");
            baseData.setCreateUser("11");
            baseData.setDataName("测试数据名称"+randowmVal+i);
            baseData.setDataType("NUM");
            baseData.setDataVal(String.valueOf(random.nextInt()));
            baseData.setParentId(randowmVal);
            baseDataService.createBaseData(baseData);
        }

    }
}