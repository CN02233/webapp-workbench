package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
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

//    @Test
    public void testCreateOrigin() {

        String[] orginCnName = {"重","川","钱","金融","四川","河北","河南","华北","吉林","北京","实施","施工"};
        Random rand=new Random();

        for (int root =0;root<orginCnName.length;root++){
            Origin parentOri  = new Origin();
            String randomPre = orginCnName[root];

            parentOri.setOrigin_name(randomPre+"集团公司");
            parentOri.setOrigin_status("0");
            parentOri.setParent_origin_id(0);
            originService.createOrigin(parentOri);

            int childLevel = new Random().nextInt(6);
            while(childLevel<2){
                childLevel = new Random().nextInt(6);
            }

            this.createSonOrigin(parentOri,parentOri.getOrigin_name(),1,childLevel);
        }
    }

    private Origin createSonOrigin(Origin parentOrigin,String randomPre,Integer level,Integer maxLevel){
        Random childRandom = new Random();
        int childCount = childRandom.nextInt(10);

        for(int child=0;child<childCount;child++){
            Origin childOri  = new Origin();
            childOri.setParent_origin_id(parentOrigin.getOrigin_id());
            childOri.setOrigin_name(randomPre+"下属"+level+"-"+child+"有限责任公司");
            childOri.setOrigin_status("0");
            originService.createOrigin(childOri);
            if(maxLevel.equals(level)){
                return null;
            }else{
                this.createSonOrigin(childOri,randomPre,level+1,maxLevel);
            }
        }



        return null;
    }

    @Test
    public void testGetOriginById() {
        System.out.println(originService.getOriginById(48));
    }

    @Test
    public void testCheckAllChildren() {
        List<Origin> resultOrigins = originService.checkAllChildren(55);
        System.out.println(resultOrigins);
    }
}