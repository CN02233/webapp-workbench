package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;


public interface ReportApprovalService {

    PageResult listReportApproval(String reportStatus,int user_id,int currPage, int pageSize);

    void ReportReviewOperator(String reportId, String reportStatus);

    void ReportApprovalOperator(String reportId, String reportStatus);
}
