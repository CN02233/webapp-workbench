package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seaboxdata.cqny.record.dao.IOriginDao;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("origin")
public class OriginServiceImp implements OriginService {

    private Logger logger = LoggerFactory.getLogger(OriginServiceImp.class);

    @Autowired
    private IOriginDao originDao;

    @Override
    public List<Origin> listAllOrigin() {
        return originDao.listAllOrigin();
    }

    @Override
    public PageResult listOrigin(int currPage, int pageSize) {
        Page<Origin> originPage = originDao.listOrigin(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(originPage);
        return pageResult;
    }

    public void createOrigin(Origin origin){
        originDao.createOrigin(origin);
    }

    @Override
    public Map<String, Object> getOriginById(Integer origin_id) {
        return originDao.getOriginById(origin_id);
    }

    @Override
    public void userOriginSave(Integer originId, Integer userId) {
        originDao.removeUserOrigin(userId);
        originDao.userOriginSave(originId,userId);
    }

    @Override
    public Origin getOriginByUser(Integer userId) {
        return originDao.getOriginByUserId(userId);
    }


    @Override
    public List<Origin> checkAllChildren(Integer originId){
        List<Origin> allOriginList = originDao.listAllOrigin();
        List<Origin> allSons = checkoutSons(originId, allOriginList);
        return allSons;
    }

    @Override
    public List<Origin> checkoutSons(Integer parentOriginId,List<Origin> originList){
        List<Origin> sons = new ArrayList();
        for (Origin origin : originList) {
            Integer parentId = origin.getParent_origin_id();
            if(parentOriginId.equals(parentId)){
                sons.add(origin);
                sons.addAll(this.checkoutSons(origin.getOrigin_id(),originList));
            }
        }
        return sons;
    }

    public Map<String,Origin> getFist2Origin(Integer checkOriginId,List<Origin> allOrigins){
//        logger.debug("getFist2Origin:{}",checkOriginId);
        Origin cityOrigin = null;
        Origin provinceOrigin = null;

        Map<Integer,Origin> originTmp = new HashMap<>();
        for (Origin allOrigin : allOrigins) {
            originTmp.put(allOrigin.getOrigin_id(),allOrigin);
        }

        Origin checkOrigin = originTmp.get(checkOriginId);

        Integer parentOriginId = checkOrigin.getParent_origin_id();


        Map<String,Origin> resultOrigin = new HashMap<>();
        resultOrigin.put("cityOrigin",null);
        resultOrigin.put("provinceOrigin",null);

        if(originTmp.containsKey(parentOriginId)){
            Origin parentOrigin = originTmp.get(parentOriginId);
            if(parentOrigin.getOrigin_id()==1){//判断当前检查的机构是否为省级机构
                resultOrigin.put("cityOrigin",null);
                resultOrigin.put("provinceOrigin",checkOrigin);
                return resultOrigin;
            }

            if(parentOrigin.getParent_origin_id()==1){//判断当前检查的机构是否为市级机构
                resultOrigin.put("cityOrigin",checkOrigin);
                resultOrigin.put("provinceOrigin",parentOrigin);
                return resultOrigin;
            }
        }else{//无上级机构信息，当球按机构为全国
            return resultOrigin;
        }

        while(true){
            cityOrigin = originTmp.get(parentOriginId);
            provinceOrigin = originTmp.get(cityOrigin.getParent_origin_id());
//            logger.debug("city origin {},{}",cityOrigin.getOrigin_id(),cityOrigin.getParent_origin_id());
            if(provinceOrigin.getParent_origin_id()==1){
                break;
            }
            parentOriginId = cityOrigin.getParent_origin_id();
        }

        resultOrigin.put("cityOrigin",cityOrigin);
        resultOrigin.put("provinceOrigin",provinceOrigin);

        return resultOrigin;


    }

    public Collection<Map<String, Object>> checkProvAndCity(List<Origin> allOrigins){

        Map<Integer,Map<String,Object>> provinceOriginTmp = new HashMap<>();

        for (Origin allOrigin : allOrigins) {
            if(allOrigin.getParent_origin_id()==1){
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("province",allOrigin);
                tmp.put("citys",new ArrayList<Origin>());
                provinceOriginTmp.put(allOrigin.getOrigin_id(),tmp);
            }
        }

        for (Origin allOrigin : allOrigins) {
            Integer parentOriId = allOrigin.getParent_origin_id();
            if(provinceOriginTmp.containsKey(parentOriId)){
                ArrayList<Origin> cityList = (ArrayList<Origin>) provinceOriginTmp.get(parentOriId).get("citys");
                cityList.add(allOrigin);
            }
        }

        return provinceOriginTmp.values();


    }

    @Override
    public List<Origin> getOriginByName(String searchOriginName) {
        List<Origin> result = originDao.getOriginByName(searchOriginName);
        return result;
    }
}
