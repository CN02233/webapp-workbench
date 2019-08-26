package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface WelcomeService {
    PageResult jobList(Integer currOriginId, int currPage, int pageSize);

    List<Map<String, Integer>> getReportSumInfo(Integer currUserId);
}
