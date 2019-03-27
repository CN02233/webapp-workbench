package com.seaboxdata.cqny.origin.service;

import com.seaboxdata.cqny.origin.entity.Administrative;
import com.seaboxdata.cqny.origin.test.EntityTree;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface AdministrativeService {

    PageResult listAdministrative(int currPage, int pageSize);

    void addAdministrative(Administrative administrative);

    void deleteById(String originId);

    void userOrganizationSave(Integer organizationId, Integer userId);

    Administrative getOrganizationByUser(Integer userId);
}
