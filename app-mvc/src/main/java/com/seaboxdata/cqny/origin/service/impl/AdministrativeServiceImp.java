package com.seaboxdata.cqny.origin.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.IAdministrativeDao;
import com.seaboxdata.cqny.origin.entity.Administrative;
import com.seaboxdata.cqny.origin.service.AdministrativeService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("administrative")
public class AdministrativeServiceImp implements AdministrativeService {

    @Autowired
    private IAdministrativeDao administrativeDao;


    @Override
    public PageResult listAdministrative(int currPage, int pageSize) {
        Page<Administrative> administrativePage = administrativeDao.listAdministrative(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(administrativePage);
        return pageResult;
    }

    @Override
    public void addAdministrative(Administrative administrative) {
        if(administrative.getOrganization_id()!=null){
            administrativeDao.updateAdministrative(administrative);
        }else{
            administrativeDao.addAdministrative(administrative);
        }
    }

    @Override
    public void deleteById(String originId) {
        administrativeDao.deleteById(originId);
    }

    @Override
    public void userOrganizationSave(Integer organizationId, Integer userId) {
        administrativeDao.removeUserOrganization(userId);
        administrativeDao.userOrganizationSave(organizationId,userId);
    }

    @Override
    public Administrative getOrganizationByUser(Integer userId) {
        return administrativeDao.getOrganizationByUser(userId);
    }
}
