package com.seaboxdata.cqny.origin.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ICqnyUserDao;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cqnyUserService")
public class CqnyUserServiceImp implements CqnyUserService {

    @Autowired
    private ICqnyUserDao cqnyUserDao;

    @Override
    public PageResult pageCqnyUser(Integer currPage, Integer pageSize,String user_name,String user_type, List<Integer> originList) {
        Page<CqnyUser> userPage = cqnyUserDao.pageCqnyUser(currPage, pageSize,user_name, user_type, originList);

        PageResult pageResult = PageResult.pageHelperList2PageResult(userPage);
        
        return pageResult;
    }
}
