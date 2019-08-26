package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.dao.IReportApprovalDao;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("reportApproval")
public class ReportApprovalServiceImp implements ReportApprovalService {
    Logger logger = LoggerFactory.getLogger(ReportApprovalServiceImp.class);

    @Autowired
    private IReportApprovalDao reportApprovalDao;
    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

    @Autowired
    private ReportCustomerService reportCustomerService;


    @Override
    public PageResult listReportApproval(String reportStatus,int userId,int currPage, int pageSize,List<Integer> originIds) {
        Set finalOriginSet = new HashSet();

        Page<ReportCustomer> approveList = reportApprovalDao.listReportApproval(currPage, pageSize, reportStatus, originIds);
        logger.debug("approveList :{}",approveList);
        PageResult pageResult = PageResult.pageHelperList2PageResult(approveList);
        return pageResult;

    }

    private void checkOrigins(Map<String, Object> origin,Set finalOriginSet){
        List<Map<String, Object>> children = (List) origin.get("childrens");
        finalOriginSet.add(origin.get("origin_id"));
        if(children!=null&&children.size()>0){
            children.forEach(child->{
                checkOrigins(child,finalOriginSet);
            });
        }
    }

    public Set getChildrenOriginsTree(int user_id){
        return this.getOrigins(user_id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchReportApprovalOperator(List<String> reportIdList, String operator) {
        for (String reportId : reportIdList) {
            ReportCustomer reportCust = reportCustomerService.checkReportCustomer(reportId);

            String passReview = reportCust.getPass_review();

            if("Y".equals(passReview)){//是否跳过复核
                if("pass".equals(operator)){
                    reportCustomerService.updateReportCustomerStatus(reportId,ReportStatus.REPORT_DONE);
                    continue;
                }
            }

            this.reportApprovalOperator(reportId,operator);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchReportReviewOperator(List<String> reportIdList, String operator) {
        for (String reportId : reportIdList) {
            ReportCustomer reportCust = reportCustomerService.checkReportCustomer(reportId);
            String passApprove = reportCust.getPass_approve();
            if("Y".equals(passApprove)){//免审核，驳回需要直接回退到填报中
                if("reject".equals(operator)) {
                    reportCustomerService.updateReportCustomerStatus(reportId,ReportStatus.NORMAL);
                    continue;
                }
            }

            this.reportReviewOperator(reportId,operator);
        }
    }

    private Set getOrigins(int user_id) {
        Origin origin;
        origin = submitauthorityDao.getOriginByUserId(user_id);
//        origin = originService.getOriginByUser(user_id);
        
        if (origin==null){
            return null;
        }else{
            Map<String, Object> originTree = submitauthorityDao.getOriginById(origin.getOrigin_id().toString());
            Set finalOriginSet = new HashSet();
            finalOriginSet.add(origin.getOrigin_id());
            checkOrigins(originTree,finalOriginSet);
            return finalOriginSet;
        }
    }

    @Override
    public void reportReviewOperator(String reportId, String reportStatus) {
        if(reportStatus.equals("pass")){
            reportApprovalDao.reportUpdateStatus(reportId, ReportStatus.REPORT_DONE.getValue());
        }
        if(reportStatus.equals("reject")){
            reportApprovalDao.reportUpdateStatus(reportId,ReportStatus.SUBMIT.getValue());
        }
        if(reportStatus.equals("refill")){
            reportApprovalDao.reportUpdateStatus(reportId,ReportStatus.NORMAL.getValue());
        }
    }

    @Override
    public void reportApprovalOperator(String reportId, String reportStatus) {
        
        if(reportStatus.equals("pass")){
            reportApprovalDao.reportUpdateStatus(reportId,ReportStatus.REVIEW.getValue());
        }
        if(reportStatus.equals("reject")){
            reportApprovalDao.reportUpdateStatus(reportId,ReportStatus.NORMAL.getValue());
        }
    }


}
