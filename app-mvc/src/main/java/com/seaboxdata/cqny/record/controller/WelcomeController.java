package com.seaboxdata.cqny.record.controller;

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

import java.util.ArrayList;
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

//        1：填报用户
//        2：监管用户
//        0：审核用户
        String userType = user.getUser_type();
        if("0".equals(userType)){

        }else if("1".equals(userType)){

        }else if("2".equals(userType)){
            Map<String,Object> responseMap = new HashMap<>();
            responseMap.put("currPage",currPage);
            responseMap.put("pageSize",pageSize);
            responseMap.put("totalPage",1);
            responseMap.put("totalNum",0);
            responseMap.put("dataList",new ArrayList<>());
            responseMap.put("userType",userType);
            JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,responseMap);
            return jsonResult;
        }

        PageResult reportCustomers = welcomeService.jobList(user.getUser_id(), currPage, pageSize);
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("currPage",reportCustomers.getCurrPage());
        responseMap.put("pageSize",reportCustomers.getPageSize());
        responseMap.put("totalPage",reportCustomers.getTotalPage());
        responseMap.put("totalNum",reportCustomers.getTotalNum());
        responseMap.put("dataList",reportCustomers.getDataList());
        responseMap.put("userType",userType);

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,responseMap);

        return jsonResult;
    }

    @RequestMapping("getReportSumInfo")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult getReportSumInfo(){
        User currUser = SessionSupport.checkoutUserFromSession();
//        1：填报用户
//        2：监管用户
//        0：审核用户
        String userType = currUser.getUser_type();

        //只有监管用户和审核用户有数据
        if("0".equals(userType)){

        }else if("1".equals(userType)){
            Map<String,Object> responseMap = new HashMap<>();

            JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,responseMap);
            return jsonResult;
        }else if("2".equals(userType)){

        }

        List<Map<String, Integer>> resultLIst = welcomeService.getReportSumInfo(currUser.getUser_id());

        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "提交成功", null,resultLIst);

        return jsonResult;
    }
}
