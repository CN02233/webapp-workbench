package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

public interface ReportApproveService {

    PageResult pageReportApproves(Integer userId, Integer currPage, Integer pageSize);

    void doReview(String reportId,String reviewStatus);

    void doConfirm(String reportId, String reviewStatus);

    PageResult pageReportConfirms(int user_id, Integer currPage, Integer pageSize);
}
