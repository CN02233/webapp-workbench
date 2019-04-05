package com.seaboxdata.cqny.origin.service;

import com.seaboxdata.cqny.origin.entity.Administrative;
import com.webapp.support.page.PageResult;

import java.util.List;


public interface AdministrativeService {

    PageResult listAdministrative(int currPage, int pageSize);

    void addAdministrative(Administrative administrative);

    void deleteById(String originId);

    void userOrganizationSave(Integer organizationId, Integer userId);

    Administrative getOrganizationByUser(Integer userId);

    void saveOrganizationAndOriginAssign(String[] originIds, String organizationId);

    List<String> getOrganizationAndOriginAssignById(String organizationId);

    void delOrganizationAndOriginAssign(String organizationId);
}
