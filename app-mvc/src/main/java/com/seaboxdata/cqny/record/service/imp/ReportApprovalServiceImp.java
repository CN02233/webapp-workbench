package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.dao.IReportApprovalDao;
import com.seaboxdata.cqny.reportunit.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("reportApproval")
public class ReportApprovalServiceImp implements ReportApprovalService {
    Logger logger = LoggerFactory.getLogger(ReportApprovalServiceImp.class);

    @Autowired
    private IReportApprovalDao reportApprovalDao;

    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

    @Override
    public PageResult listReportApproval(String reportStatus,int userId,int currPage, int pageSize) {
        List finalOriginList = getOrigins(userId);
        Page<ReportCustomer> approveList = reportApprovalDao.listReportApproval(1, 10, reportStatus, finalOriginList);
        logger.debug("approveList :{}",approveList);
        PageResult pageResult = PageResult.pageHelperList2PageResult(approveList);
        return pageResult;
    }

    private void checkOrigins(Map<String, Object> origin,List finalOriginList){
        List<Map<String, Object>> children = (List) origin.get("childrens");
        finalOriginList.add(origin.get("origin_id"));
        if(children!=null&&children.size()>0){
            children.forEach(child->{
                checkOrigins(child,finalOriginList);
            });
        }
    }

    private List getOrigins(int user_id) {
        Origin origin;
        origin = submitauthorityDao.getOriginByUserId(user_id);
        Map<String, Object> originTree = submitauthorityDao.getOriginById(origin.getOrigin_id().toString());
        List finalOriginList = new ArrayList();
        checkOrigins(originTree,finalOriginList);
        return finalOriginList;
    }

    @Override
    public void ReportReviewOperator(String reportId, String reportStatus) {
        if(reportStatus.equals("pass")){
            reportApprovalDao.ReportUpdateStatus(reportId, ReportStatus.APPROVE.toString());
        }
        if(reportStatus.equals("reject")){
            reportApprovalDao.ReportUpdateStatus(reportId,ReportStatus.SUBMIT.toString());
        }
        if(reportStatus.equals("refill")){
            reportApprovalDao.ReportUpdateStatus(reportId,ReportStatus.NORMAL.toString());
        }
    }

    @Override
    public void ReportApprovalOperator(String reportId, String reportStatus) {
        if(reportStatus.equals("pass")){
            reportApprovalDao.ReportUpdateStatus(reportId,ReportStatus.REVIEW.toString());
        }
        if(reportStatus.equals("reject")){
            reportApprovalDao.ReportUpdateStatus(reportId,ReportStatus.NORMAL.toString());
        }
    }


}
