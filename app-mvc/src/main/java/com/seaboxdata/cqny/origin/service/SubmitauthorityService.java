package com.seaboxdata.cqny.origin.service;

import com.seaboxdata.cqny.origin.entity.Submitauthority;
import com.seaboxdata.cqny.origin.test.EntityTree;
import com.webapp.support.page.PageResult;

import java.util.List;

public interface SubmitauthorityService {

    List<EntityTree> listAllSubmitauthority();

    PageResult listSubmitauthority(int currPage, int pageSize);

    void addSubmitauthority(Submitauthority submitauthority);

    void deleteById(String originId);
}
