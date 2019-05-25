package com.seaboxdata.cqny.origin.controller;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.entity.UserStatus;
import com.workbench.auth.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("selectOriginType")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult selectOriginType(@RequestBody Map<String,Object> selectOriginMap){
//        String userId, String user_name, String origin_type
        String userId = selectOriginMap.containsKey("userId")?(String)selectOriginMap.get("userId"):null;
        String user_name = selectOriginMap.containsKey("user_name")?(String)selectOriginMap.get("user_name"):null;
        String origin_type = selectOriginMap.containsKey("origin_type")?(String)selectOriginMap.get("origin_type"):null;


        if(Strings.isNullOrEmpty(userId)){
            if(!Strings.isNullOrEmpty(user_name)){
                User user = SessionSupport.checkoutUserFromSession();
                if(user.getUser_name().equals(user_name)){
                    userId = String.valueOf(user.getUser_id());
                }else{
                    user = userService.getUserByUserNm(user_name);
                    if(user!=null){
                        userId = String.valueOf(user.getUser_id());
                    }else{
                        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "用户未找到", "用户未找到",
                                "USER_NOT_NULL");
                        return response;
                    }
                }
            }else{
                JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "用户为空", "用户为空",
                        "USER_NULL");
                return response;
            }
        }

        if(Strings.isNullOrEmpty(origin_type)){
            JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD, "请选择企业类型", "请选择企业类型",
                    "ORIGIN_TYPE_NULL");
            return response;
        }

        cqnyUserService.selectOriginType(userId,origin_type);

        changeUserInfo(selectOriginMap,userId);

        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "修改成功",
                null, JsonResult.RESULT.SUCCESS);
        return response;
    }

    @RequestMapping("changeSelfOriginType")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult changeSelfOriginType(@RequestBody Map<String,Object> requestMap){
        String origin_type = (String) requestMap.get("origin_type");
        User currUser = SessionSupport.checkoutUserFromSession();
//        Map<String,Object> selectOriginMap = new HashMap<>();
        requestMap.put("userId",String.valueOf(currUser.getUser_id()));
        requestMap.put("origin_type",origin_type);
        return this.selectOriginType(requestMap);
    }

    @RequestMapping("getCurrUserOrigin")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult getCurrUserOrigin(){
        User user = SessionSupport.checkoutUserFromSession();
        user = userService.getUserByUserId(user.getUser_id());
        Origin origin = originService.getOriginByUser(user.getUser_id());
        Map<String,Object> responeMap = new HashMap<>();
        responeMap.put("origin",origin);
        responeMap.put("user",user);
        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功",
                null, responeMap);
        return response;
    }

    @RequestMapping("getUserInfo")
    @ResponseBody
    @CrossOrigin(allowCredentials = "true")
    public JsonResult getUserInfo(String user_name){
        User userInfo = userService.getUserByUserNm(user_name);
        JsonResult response = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功",
                null, userInfo);
        return response;
    }

    private void changeUserInfo(Map<String,Object> selectOriginMap,String userId){
        String user_name_cn = selectOriginMap.containsKey("user_name_cn")?(String)selectOriginMap.get("user_name_cn"):null;
        String mobile_phone = selectOriginMap.containsKey("mobile_phone")?(String)selectOriginMap.get("mobile_phone"):null;
        String office_phone = selectOriginMap.containsKey("office_phone")?(String)selectOriginMap.get("office_phone"):null;
        String email = selectOriginMap.containsKey("email")?(String)selectOriginMap.get("email"):null;
        String social_code = selectOriginMap.containsKey("social_code")?(String)selectOriginMap.get("social_code"):null;
        User userFromDb = userService.getUserByUserId(new Integer(userId));
        userFromDb.setUser_name_cn(user_name_cn);
        userFromDb.setMobile_phone(mobile_phone);
        userFromDb.setOffice_phone(office_phone);
        userFromDb.setEmail(email);
        userFromDb.setSocial_code(social_code);
        userService.updateUser(userFromDb);

        SessionSupport.addUserToSession(userFromDb);
    }
}