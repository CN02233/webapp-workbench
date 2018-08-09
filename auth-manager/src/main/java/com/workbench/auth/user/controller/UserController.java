package com.workbench.auth.user.controller;

import com.github.pagehelper.Page;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.session.SessionSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.workbench.auth.menu.entity.Menu;
import com.workbench.auth.user.service.UserService;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by pc on 2017/6/30.
 */
@Controller
@RequestMapping("sys/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("listUserPage")
    @ResponseBody
    @JsonpCallback
    public String getUserByPage(int currPage, int pageSize,User user){
        Page<User> userPageList = userService.listUsersForPage(currPage, pageSize,user);
        PageResult pageResult = PageResult.pageHelperList2PageResult(userPageList);
        String jsonResult = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, pageResult);
        return jsonResult;
    }

    @RequestMapping("listAllUser")
    @ResponseBody
    @JsonpCallback
    public String listAllUser(){
        List<User> userList = userService.listAllUser();
        String jsonResult = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, userList);
        return jsonResult;
    }

    @RequestMapping("userMenuList")
    @ResponseBody
    @JsonpCallback
    public String getMenuList4User(){
        User user = SessionSupport.checkoutUserFromSession();
        JsonResult jsonResult = JsonResult.getInstance();
        if(user!=null){
            List<Menu> allMenu = userService.getMenuList4User(user.getUser_name());
            jsonResult.setResult(JsonResult.RESULT.SUCCESS);
            jsonResult.setResult_msg("获取菜单数据成功");
            jsonResult.setResultData(allMenu);
        }else{
            jsonResult.setResult(JsonResult.RESULT.FAILD);
            jsonResult.setFaild_reason("USER_NOT_LOGIN");
            jsonResult.setResult_msg("用户未登录,请重新登录");
        }
//        String resultJson = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, jsonResult);

        logger.debug("get user menu list,result jsonp value :{}",jsonResult.toString());

        return jsonResult.toString();

    }


    @RequestMapping("saveNewUser")
    @ResponseBody
    @JsonpCallback
    public String saveNewUser(User user){
        userService.createUser(user);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(JsonResult.RESULT.SUCCESS);
        jsonResult.setResult_msg("保存成功");
        logger.debug("user bean information after create :{}, and json value is 【{}】",user.toString(),jsonResult.toString());

        return jsonResult.toString();
    }

    @RequestMapping("delUserByUserId")
    @ResponseBody
    @JsonpCallback
    public String delUserByUserId(Integer user_id){
        userService.delUserById(user_id);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(JsonResult.RESULT.SUCCESS);
        jsonResult.setResult_msg("删除成功");
        logger.debug("jsonResult information after delete :{}",jsonResult.toString());

        return jsonResult.toString();
    }

    @RequestMapping("getUserByUserId")
    @ResponseBody
    @JsonpCallback
    public String getUserByUserId(Integer user_id){
        User user = userService.getUserByUserId(user_id);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(JsonResult.RESULT.SUCCESS);
        jsonResult.setResult_msg("获取成功");
        jsonResult.setResultData(user);
        logger.debug("jsonResult information after delete :{}",jsonResult.toString());

        return jsonResult.toString();
    }

    @RequestMapping("updateSaveUser")
    @ResponseBody
    @JsonpCallback
    public String updateSaveUser(User user){
        userService.updateUser(user);

        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(JsonResult.RESULT.SUCCESS);
        jsonResult.setResult_msg("保存成功");
        logger.debug("jsonResult information after delete :{}",jsonResult.toString());

        return jsonResult.toString();
    }
}
