package com.seaboxdata.cqny.record.controller;

import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.service.OriginService;
import com.seaboxdata.cqny.record.service.WelcomeService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("welcome")
public class WelcomeController {

    @Autowired
    private WelcomeService welcomeService;

    @Autowired
    private OriginService originService;

    /**
     * 获取待办事项
     * @param
     * @return
     */
    @RequestMapping("jobList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult jobList(int currPage,int pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        Origin userOrigin = originService.getOriginByUser(user.getUser_id());
        Integer userOriginId = userOrigin.getOrigin_id();
        PageResult reportCustomers = welcomeService.jobList(userOriginId, currPage, pageSize);
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("currPage",reportCustomers.getCurrPage());
        responseMap.put("pageSize",reportCustomers.getPageSize());
        responseMap.put("totalPage",reportCustomers.getTotalPage());
        responseMap.put("totalNum",reportCustomers.getTotalNum());
        responseMap.put("dataList",reportCustomers.getDataList());

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,reportCustomers);

        return jsonResult;
    }
}
