package com.seaboxdata.cqny.origin.controller;

import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.service.UserService;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("cqnyUser")
public class CqnyUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CqnyUserService cqnyUserService;

    @Autowired
    private OriginService originService;

    @RequestMapping("listUserPage")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult getUserByPage(int currPage, int pageSize, User user, String searchOriginId, String searchOriginName) {
        List<Origin> allReportOrigins = originService.listAllOrigin();

        List<Integer> originSqlParams = new ArrayList<>();

//        List<Integer> originParams = new ArrayList<>();
        if (Strings.isNullOrEmpty(searchOriginId) && Strings.isNullOrEmpty(searchOriginName)) {//全量

        } else if (!Strings.isNullOrEmpty(searchOriginId)) {//有机构查询条件
            originSqlParams.add(new Integer(searchOriginId));
            List<Origin> allChildrenOrigins = originService.checkoutSons(new Integer(searchOriginId), allReportOrigins);
            for (Origin originChild : allChildrenOrigins) {
                originSqlParams.add(originChild.getOrigin_id());
            }
        }

        if (!Strings.isNullOrEmpty(searchOriginName)) {
            List<Origin> origins = originService.getOriginByName(searchOriginName);
            List<Integer> originParamsTmp = new ArrayList<>();

            for (Origin origin : origins) {
                Integer originObj = origin.getOrigin_id();
                if (originSqlParams != null && originSqlParams.size() > 0) {
                    if (originSqlParams.contains(originObj)) {
                        originParamsTmp.add(originObj);
                    }
                } else {
                    originParamsTmp.add(originObj);
                }
            }
            originSqlParams = originParamsTmp;

        }

        PageResult pageResult = cqnyUserService.pageCqnyUser(currPage,
                pageSize,
                user.getUser_name_cn(),
                user.getUser_type(),
                originSqlParams != null && originSqlParams.size() > 0 ? originSqlParams : null);

        List<CqnyUser> cqnyResultData = pageResult.getDataList();
        for (CqnyUser userTmp : cqnyResultData) {
            Integer reportOriginId = userTmp.getOrigin_id();

            Map<String, Origin> result = originService.getFist2Origin(reportOriginId, allReportOrigins);
            if (result.get("cityOrigin") != null)
                userTmp.setOrigin_city(result.get("cityOrigin").getOrigin_name());
            if (result.get("provinceOrigin") != null)
                userTmp.setOrigin_province(result.get("provinceOrigin").getOrigin_name());
        }

        Collection<Map<String, Object>> first2Origin = originService.checkProvAndCity(allReportOrigins);


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("currPage", pageResult.getCurrPage());
//        responseMap.put("dataList",resultData);
        responseMap.put("dataList", cqnyResultData);
        responseMap.put("pageSize", pageResult.getPageSize());
        responseMap.put("totalNum", pageResult.getTotalNum());
        responseMap.put("totalPage", pageResult.getTotalPage());
        responseMap.put("first2Origin", first2Origin);

        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, responseMap);
        return response;
//
//
//        String jsonResult = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, pageResult);
//        return jsonResult;
    }

    @RequestMapping("resetPwd")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult resetPwd(String userId) {
        if(!Strings.isNullOrEmpty(userId)){
            userService.resetPwd(new Integer(userId));
        }

        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, null);
        return response;
    }
}