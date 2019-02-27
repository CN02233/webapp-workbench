package com.seaboxdata.cqny.record.service;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.BaseData;

import java.util.List;

public interface BaseDataService {

    Page<BaseData> listBaseDatas(int currPage,int pageSize);

    List<BaseData> listAllBaseDatas();

    Integer createBaseData(BaseData baseData);

}
