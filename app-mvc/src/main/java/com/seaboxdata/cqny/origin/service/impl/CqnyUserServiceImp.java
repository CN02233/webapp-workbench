package com.seaboxdata.cqny.origin.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.dao.ICqnyUserDao;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import com.seaboxdata.cqny.record.config.ReportStatus;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.page.PageResult;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.entity.UserStatus;
import com.workbench.auth.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("cqnyUserService")
public class CqnyUserServiceImp implements CqnyUserService {

    @Autowired
    private ICqnyUserDao cqnyUserDao;

    @Autowired
    private OriginService originService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Override
    public PageResult pageCqnyUser(Integer currPage, Integer pageSize,String user_name,String user_type, List<Integer> originList) {
        Page<CqnyUser> userPage = cqnyUserDao.pageCqnyUser(currPage, pageSize,user_name, user_type, originList);

        PageResult pageResult = PageResult.pageHelperList2PageResult(userPage);
        
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectOriginType(String userId, String originType) {
        //获取到该用户对应的机构
        Origin originByUser = originService.getOriginByUser(new Integer(userId));
        //获取到机构下的所有用户
        List<User> allUsers = originService.getUsersByOrigin(originByUser.getOrigin_id());
        //修改该机构下的所有用户状态为 征程
        if(allUsers!=null){
            for (User userObj : allUsers) {
                userObj.setUser_status(String.valueOf(UserStatus.NORMAL.getStatus()));
                userService.updateUser(userObj);
            }
        }
        //修改机构的类型
        originByUser.setOrigin_type(originType);
        originService.updateOrigin(originByUser);

        //检查该机构下的报表，如果报表类型与修改后的机构类型不符。将报表置为失效
//        List<Integer> origins = new ArrayList<>();
//        origins.add(originByUser.getOrigin_id());
        List<ReportCustomer> reportCustomers = reportCustomerService.allReportForOrigin(originByUser.getOrigin_id().toString());
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

    }
}
