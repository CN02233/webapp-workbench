package com.seaboxdata.cqny.origin.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ISubmitauthorityDao;
import com.seaboxdata.cqny.origin.entity.Submitauthority;
import com.seaboxdata.cqny.origin.service.SubmitauthorityService;
import com.seaboxdata.cqny.origin.tree.EntityTree;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("submitauthority")
public class SubmitauthorityServiceImp implements SubmitauthorityService {

    @Autowired
    private ISubmitauthorityDao submitauthorityDao;

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Override
    public List<EntityTree> listAllSubmitauthority() {
        return submitauthorityDao.listAllSubmitauthority();
    }

    @Override
    public PageResult listSubmitauthority(int currPage, int pageSize) {
        Page<Submitauthority> submitauthorityPage = submitauthorityDao.listSubmitauthority(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(submitauthorityPage);
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSubmitauthority(Submitauthority submitauthority) {
        if(submitauthority.getOrigin_id()!=null){
            submitauthorityDao.updateSubmitauthority(submitauthority);
            String originType = submitauthority.getOrigin_type();
            //检查该机构下的报表，如果报表类型与修改后的机构类型不符。将报表置为失效
            List<Integer> origins = new ArrayList<>();
            origins.add(submitauthority.getOrigin_id());
            List<ReportCustomer> reportCustomers  = reportCustomerService.allReportForOrigin(submitauthority.getOrigin_id().toString());
            if(reportCustomers!=null){
                for (ReportCustomer reportCustomer : reportCustomers) {
                    String oldStatus = reportCustomer.getReport_status();

                    String reportType = reportCustomer.getReport_type();
                    if(reportType.equals("0")){//所有企业都要填的报表
                        if(ReportStatus.REMOVE.compareTo(oldStatus)){
                            reportCustomerService.updateReportCustomerStatus(
                                    reportCustomer.getReport_id().toString(), ReportStatus.NORMAL);
                        }
                    }else{
                        if(!reportType.equals(originType)){
                            reportCustomerService.updateReportCustomerStatus(
                                    reportCustomer.getReport_id().toString(), ReportStatus.REMOVE);
                        }else{
                            if(ReportStatus.REMOVE.compareTo(oldStatus)){
                                reportCustomerService.updateReportCustomerStatus(
                                        reportCustomer.getReport_id().toString(), ReportStatus.NORMAL);
                            }
                        }
                    }
                }
            }

        }else{
            submitauthorityDao.addSubmitauthority(submitauthority);
        }
    }

    @Override
    public void deleteById(String originId) {
        //获取originId下的所有报送机构
        Map<String, Object> originTree = submitauthorityDao.getOriginById(originId);
        List finalOriginList = new ArrayList();
        checkOrigins(originTree,finalOriginList);
        submitauthorityDao.deleteByListId(finalOriginList);
    }

    @Override
    public List<String> getReportOriginForOrganizationUser(int currUserId) {
        return submitauthorityDao.getOriginIdListByUserId(currUserId);
    }

    private void checkOrigins(Map<String, Object> origin,List finalOriginList){
        List<Map<String, Object>> children = (List)origin.get("childrens");
        finalOriginList.add(origin.get("origin_id"));
        if(children!=null&&children.size()>0){
            children.forEach(child->{
                checkOrigins(child,finalOriginList);
            });
        }
    }
}
