package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("reportCust")
public class ReportCustomerController {

    private ReportCustomerService reportCustomerService;

    /**
     * 获取当前用户权限下报送列表
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("pagerReport")
    public JsonResult pagerReport(Integer currPage,Integer pageSize){
        User currUser = SessionSupport.checkoutUserFromSession();
        int currUserId = currUser.getUser_id();

        PageResult pageResult = reportCustomerService.pagerReport(currPage, pageSize, currUserId);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取欧成功", null,pageResult);
        return jsonResult;
    }
}
