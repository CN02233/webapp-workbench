package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.Origin;
import com.webapp.support.page.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OriginService {

    List<Origin> listAllOrigin();
    PageResult listOrigin(int currPage, int pageSize);
    void createOrigin(Origin origin);
    Map<String,Object> getOriginById(Integer origin_id);

    void userOriginSave(Integer originId, Integer userId);

    Origin getOriginByUser(Integer userId);

    List<Origin> checkAllChildren(Integer originId);

    List<Origin> checkoutSons(Integer parentOriginId,List<Origin> originList);

    Map<String,Origin> getFist2Origin(Integer checkOrigin,List<Origin> allOrigins);

    Collection<Map<String, Object>> checkProvAndCity(List<Origin> allOrigins);

    List<Origin> getOriginByName(String searchOriginName);

    Origin getOriginTree(List<Integer> childrenOrigins, List<Origin> allOrigins);
}
