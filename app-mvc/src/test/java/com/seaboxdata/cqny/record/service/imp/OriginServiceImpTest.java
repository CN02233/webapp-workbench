package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Random;

public class OriginServiceImpTest extends AbstractTestService {

    @Resource
    private OriginService originService;

    public void testListAllOrigin() {
    }

    @Test
    public void testListOrigin() {
        PageResult result = originService.listOrigin(1, 10);
        System.out.println(result);
    }

    @Test
    public void testCreateOrigin() {

        for(int parent=0;parent<3;parent++){
            Origin parentOri  = new Origin();
            parentOri.setOrigin_name("父机构"+parent);
            parentOri.setOrigin_status("0");
            parentOri.setParent_origin_id(0);

            originService.createOrigin(parentOri);
            Integer parentId = parentOri.getOrigin_id();
            Random childRandom = new Random();
            int childCount = childRandom.nextInt(10);
            for(int child=0;child<childCount;child++){
                Origin childOri  = new Origin();
                childOri.setParent_origin_id(parentId);
                childOri.setOrigin_name("子机构"+parent+"-"+childCount);
                parentOri.setOrigin_status("0");
                originService.createOrigin(childOri);
            }
        }
    }

    @Test
    public void testGetOriginById() {
        System.out.println(originService.getOriginById(48));
    }
}