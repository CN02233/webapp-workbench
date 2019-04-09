package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.dao.IOriginDao;
import com.seaboxdata.cqny.record.dao.IReportApproveDao;
import com.seaboxdata.cqny.record.dao.IReportDao;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.entity.ReportStatus;
import com.seaboxdata.cqny.record.service.ReportApproveService;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("reportApproveService")
public class ReportApproveServiceImp implements ReportApproveService {

    Logger logger = LoggerFactory.getLogger(ReportApproveServiceImp.class);

    @Autowired
    private IOriginDao iOriginDao;

    @Autowired
    private IReportDao iReportDao;

    @Autowired
    private IReportApproveDao iReportApproveDao;

    @Override
    public PageResult pageReportApproves(Integer userId, Integer currPage, Integer pageSize) {

        List finalOriginList = getOrigins(userId);

        Page<ReportInfo> approveList = iReportApproveDao.getApproveList(1, 10, ReportStatus.SUBMIT.toString(), finalOriginList);
        logger.debug("approveList :{}",approveList);
        PageResult pageResult = PageResult.pageHelperList2PageResult(approveList);

        return pageResult;
    }



    @Override
    public PageResult pageReportConfirms(int user_id, Integer currPage, Integer pageSize) {

        List finalOriginList = getOrigins(user_id);

        Page<ReportInfo> approveList = iReportApproveDao.getApproveList(1, 10, ReportStatus.REVIEW.toString(), finalOriginList);
        logger.debug("approveList :{}",approveList);
        PageResult pageResult = PageResult.pageHelperList2PageResult(approveList);

        return pageResult;
    }

    private List getOrigins(int user_id) {
        Origin origin = iOriginDao.getOriginByUserId(user_id);
        Map<String, Object> originTree = iOriginDao.getOriginById(origin.getOrigin_id());
        List finalOriginList = new ArrayList();
        checkOrigins(originTree,finalOriginList);
        return finalOriginList;
    }

    @Override
    public void doReview(String reportId,String reviewStatus) {
        if(!Strings.isNullOrEmpty(reviewStatus)&&"approve".equals(reviewStatus)){
            iReportDao.reviewReport(ReportStatus.REVIEW.toString(),reportId,((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        }else{
            iReportDao.reviewReport(ReportStatus.NORMAL.toString(),reportId,((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        }
    }

    @Override
    public void doConfirm(String reportId, String reviewStatus) {
        if(!Strings.isNullOrEmpty(reviewStatus)&&"approve".equals(reviewStatus)){
            iReportDao.confirmReport(ReportStatus.APPROVE.toString(),reportId,((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        }else{
            iReportDao.confirmReport(ReportStatus.NORMAL.toString(),reportId,((User) SessionSupport.checkoutUserFromSession()).getUser_id());
        }
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
}
