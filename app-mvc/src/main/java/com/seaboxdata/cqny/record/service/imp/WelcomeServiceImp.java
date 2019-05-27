package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if(userOrigin==null){
            PageResult result = new PageResult();
            result.setCurrPage(1);
            result.setPageSize(1);
            result.setTotalNum(1);
            result.setTotalPage(1);
            ArrayList<Object> resultList = Lists.newArrayList();
            resultList.addAll(new ArrayList<>());
            result.setDataList(resultList);
            return result;
        }
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

    @Override
    public List<Map<String, Integer>> getReportSumInfo(Integer currUserId) {
        Origin userOrigin = originService.getOriginByUser(currUserId);
        if(userOrigin==null){
            Map<String,Integer> resultMap = new HashMap<>();
            List<Map<String,Integer>> resultList =new ArrayList<>();
            resultList.add(resultMap);
            return resultList;
        }else{
            Integer userOriginId = userOrigin.getOrigin_id();
            List<Origin> allChildren = originService.checkAllChildren(userOriginId);

            List<Integer> allChildrenIds= new ArrayList();
            for (Origin allChild : allChildren) {
                allChildrenIds.add(allChild.getOrigin_id());
            }

            if(allChildrenIds==null)
                allChildrenIds = new ArrayList<>();


            List<Map<String, Integer>> sumResult = welcomeDao.getReportSumInfo(allChildrenIds);
            return sumResult;
        }
    }
}
