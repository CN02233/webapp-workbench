package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IBaseDataDao;
import com.seaboxdata.cqny.record.entity.BaseData;
import com.seaboxdata.cqny.record.service.BaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("baseDataService")
public class BaseDataServiceImp implements BaseDataService {

    @Autowired
    private IBaseDataDao baseDataDao;

    @Override
    public Page<BaseData> listBaseDatas(int currPage,int pageSize) {
        return baseDataDao.pageingBaseDatas(currPage,pageSize);
    }

    @Override
    public List<BaseData> listAllBaseDatas() {
        List<BaseData> dataList = baseDataDao.listAllBaseDatas();
        return dataList;
    }

    @Override
    public Integer createBaseData(BaseData baseData) {
        baseDataDao.createBaseDatas(baseData);
        return baseData.getDataId();
    }
}
