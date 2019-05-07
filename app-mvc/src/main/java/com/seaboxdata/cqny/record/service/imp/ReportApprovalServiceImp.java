package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.dao.IReportApprovalDao;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("reportApproval")
public class ReportApprovalServiceImp implements ReportApprovalService {
    Logger logger = LoggerFactory.getLogger(ReportApprovalServiceImp.class);

    @Autowired
    private IReportApprovalDao reportApprovalDao;
    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

    @Override
    public PageResult listReportApproval(String reportStatus,int userId,int currPage, int pageSize) {
        Set finalOriginSet = new HashSet();

        if(ReportStatus.REVIEW.compareTo(reportStatus)){
            finalOriginSet = getOrigins(userId);
        }else{
            Origin origin = submitauthorityDao.getOriginByUserId(userId);
            finalOriginSet.add(origin.getOrigin_id());
        }

        Page<ReportCustomer> approveList = reportApprovalDao.listReportApproval(currPage, pageSize, reportStatus, finalOriginSet);
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

    private Set getOrigins(int user_id) {
        Origin origin;
        origin = submitauthorityDao.getOriginByUserId(user_id);
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
            reportApprovalDao.reportUpdateStatus(reportId, ReportStatus.APPROVE.getValue());
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
