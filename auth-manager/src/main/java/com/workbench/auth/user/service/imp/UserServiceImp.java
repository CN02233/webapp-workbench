package com.workbench.auth.user.service.imp;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.page.PageResult;
import com.workbench.auth.menu.entity.Menu;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.service.UserService;
import com.workbench.auth.user.dao.IUserServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by pc on 2017/6/29.
 */
@Service("userService")
public class UserServiceImp implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    private IUserServiceDao userServiceDao;

    public User checkUser(String userNm, String password) {
        User resultUser = userServiceDao.checkUserByUserNm(userNm);
        logger.debug("check user result {}",resultUser);
        return resultUser;
    }

    public List<User> listAllUser(){
        List<User> allUser = userServiceDao.listAllUser();
        logger.debug("check user result {}",allUser.toString());
        return allUser;
    }

    public Page<User> listUsersForPage(int currPage,int pageSize,User user){

        Page<User> allUser = userServiceDao.listUsersForPage(currPage,pageSize,user.getUser_id(),user.getUser_name());

        return allUser;
    }

    public User createUser(User user){
        SimpleDateFormat format = new SimpleDateFormat("ssSSS");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(Calendar.getInstance().getTime()));
        builder.append(new Random().nextInt(50));
        int user_id = new Integer(builder.toString());
        user_id = user_id<<(new Random().nextInt(5));
        user.setUser_id(user_id);
        logger.debug("save user info {}",user);
        userServiceDao.saveNewUser(user);
        return user;
    }

    public void updateUser(User user){
        userServiceDao.updateSave(user);
    }

    public void delUserById(int user_id){
        userServiceDao.delUserById(user_id);
    }

    public User getUserByUserId(Integer userId){
        User resultUser = userServiceDao.getUserByUserId(userId);
        logger.debug(resultUser.toString());
        return resultUser;
    }

    public User getUserByUserNm(String userName){
        User resultUser = userServiceDao.checkUserByUserNm(userName);
        logger.debug(resultUser!=null?resultUser.toString():"user is null");
        return resultUser;
    }


    public List<Menu> getMenuList4User(String user_nm) {

        List<Menu> allMenuList = userServiceDao.getMenuList4User(user_nm);

        return allMenuList;
    }
}
