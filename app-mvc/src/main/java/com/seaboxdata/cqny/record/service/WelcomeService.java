package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

public interface WelcomeService {
    PageResult jobList(Integer currOriginId, int currPage, int pageSize);
}
