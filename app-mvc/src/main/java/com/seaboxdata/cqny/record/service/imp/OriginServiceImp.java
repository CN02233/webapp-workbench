package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seaboxdata.cqny.record.dao.IOriginDao;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("origin")
public class OriginServiceImp implements OriginService {

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
}
