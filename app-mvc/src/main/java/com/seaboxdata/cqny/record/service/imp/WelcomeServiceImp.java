package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.dao.IWelcomeDao;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportApprovalService;
import com.seaboxdata.cqny.record.service.WelcomeService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("welcomeService")
public class WelcomeServiceImp implements WelcomeService {

    @Autowired
    private IWelcomeDao welcomeDao;

    @Autowired
    private OriginService originService;

    @Autowired
    private ReportApprovalService reportApprovalService;

    @Override
    public PageResult jobList(Integer currUserId, int currPage, int pageSize) {

        Origin userOrigin = originService.getOriginByUser(currUserId);
        Integer userOriginId = userOrigin.getOrigin_id();

        List<Origin> allChildren = originService.checkAllChildren(userOriginId);

        List<Integer> allChildrenIds= new ArrayList();
        for (Origin allChild : allChildren) {
            allChildrenIds.add(allChild.getOrigin_id());
        }

        if(allChildrenIds!=null&&allChildrenIds.size()<1)
            allChildrenIds = null;
        if(userOrigin.getParent_origin_id()==1){
            Page<ReportCustomer> reportCustomerList = welcomeDao.jobList(
                    currPage,
                    pageSize,
                    userOriginId,
                    ReportStatus.NORMAL.getValue(),
                    allChildrenIds,
                    null,
                    ReportStatus.REVIEW.getValue());
            PageResult pageResult = PageResult.pageHelperList2PageResult(reportCustomerList);
            return pageResult;

        }else{
            Page<ReportCustomer> reportCustomerList = welcomeDao.jobList(
                    currPage,
                    pageSize,
                    userOriginId,
                    ReportStatus.NORMAL.getValue(),
                    allChildrenIds,
                    ReportStatus.SUBMIT.getValue(),
                    null);
            PageResult pageResult = PageResult.pageHelperList2PageResult(reportCustomerList);
            return pageResult;

        }

    }
}
