package com.seaboxdata.cqny.origin.service;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import com.webapp.support.page.PageResult;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CqnyUserService {

    public PageResult pageCqnyUser(Integer currPage, Integer pageSize,String user_name,String user_type, List<Integer> originList);

    void selectOriginType(String userId, String originType);
}
