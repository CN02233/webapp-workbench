package com.seaboxdata.cqny.record.service;

import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Set;


public interface ReportApprovalService {

    PageResult listReportApproval(String reportStatus, int user_id, int currPage, int pageSize, List<Integer> originIds);

    void reportReviewOperator(String reportId, String reportStatus);

    void reportApprovalOperator(String reportId, String reportStatus);

    Set getChildrenOriginsTree(int userId);
}
