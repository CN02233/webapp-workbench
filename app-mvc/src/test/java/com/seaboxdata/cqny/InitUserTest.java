package com.seaboxdata.cqny;

import com.AbstractTestService;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.origin.service.CqnyUserService;
import com.seaboxdata.cqny.origin.service.impl.CqnyUserServiceImp;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.service.OriginService;
import com.webapp.support.encryption.MD5;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.entity.UserRole;
import com.workbench.auth.user.service.UserRoleService;
import com.workbench.auth.user.service.UserService;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class InitUserTest extends AbstractTestService {
    @Resource
    private UserService userService;

    @Resource
    private OriginService originService;

    @Resource
    private UserRoleService userRoleService;

//    INSERT INTO `cqny_record`.`sys_origin`
//            (`origin_id`,
//            `origin_name`,
//            `parent_origin_id`,
//            `origin_status`,
//            `create_date`,
//            `origin_smp_name`)
//    SELECT INST_ID,INST_NAME,PARENT_INST_ID,'0',current_date(),INST_SMP_NAME FROM cqny_record.BAPP_INST;

//    update sys_origin set parent_origin_id=1 where parent_origin_id = 0 and origin_id !=1;


//    @Test
    public void loadUserFromDisk() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/song/myfiles/jobfiles/DFJX/usermoreinfos.csv"));//换成你的文件名
        reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
        String line = null;
        User user = new User();
        user.setUser_name("admin");
        user.setUser_type("1");
        user.setUser_status("0");
        user.setReg_date(new Date());
        user.setUser_pwd(MD5.getMD5Value("123456"));
        user.setUser_name_cn("超级管理员");

        userService.createUser(user);

        while((line=reader.readLine())!=null){
            String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分

            user.setUser_name(item[1]);
            user.setUser_type("1");
            user.setUser_status("0");
            user.setReg_date(new Date());
            user.setUser_pwd(MD5.getMD5Value(item[6]));
            user.setUser_name_cn(item[7]);

            String originId = item[8];

            userService.createUser(user);

            originService.userOriginSave(new Integer(originId),user.getUser_id());
        }
    }

    @Test
    public void updateUserInfos() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/song/myfiles/jobfiles/DFJX/usermoreinfos.csv"));//换成你的文件名
        reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
        String line = null;
        while((line=reader.readLine())!=null) {
            String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
            String userName = item[1];

            String MOBILE = null;
            String tel = null;
            String email = null;
            if (item.length == 9) {//没有任何联系信息
                continue;
            } else if (item.length == 10) {
                MOBILE = item[9];
            } else if (item.length == 11) {
                MOBILE = item[9];
                tel = item[10];
            } else if (item.length == 12) {
                MOBILE = item[9];
                tel = item[10];
                email = item[11];
            }

            if("13935352368".equals(MOBILE)){
                System.out.println("debuge");
            }

            if(!Strings.isNullOrEmpty(MOBILE)){

                MOBILE = MOBILE.replaceAll(" ", "");
                if(MOBILE.length()!=11){
                    String[] splitReslut = MOBILE.split("/");
                    if(splitReslut.length>1){
                        MOBILE = splitReslut[0];
                        System.out.println("手机号码格式不符:"+userName+"---"+MOBILE);
                        if(Strings.isNullOrEmpty(tel)){
                            tel = splitReslut[1];
                        }else{
                            tel = tel+" "+splitReslut[1];
                        }
                        System.out.println("处理后的座机号:"+userName+"---"+tel);
                    }else{
                        System.out.println("手机号码格式不符:"+userName+"---"+MOBILE);
                        if(Strings.isNullOrEmpty(tel)){
                            tel = MOBILE;
                        }else{
                            tel = tel+" "+MOBILE;
                        }
                        System.out.println("处理后的座机号:"+userName+"---"+tel);
                    }
                    MOBILE = null;
                }
            }

            User userInfo = userService.getUserByUserNm(userName);
            if(userInfo!=null){
                userInfo.setMobile_phone(MOBILE);
                userInfo.setOffice_phone(tel);
                userInfo.setEmail(email);
                userService.updateUser(userInfo);

            }

        }

    }

    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }


    //    @Test
    public void getManagerUsers(){
        List<Origin> originList = originService.listAllOrigin();
        List<Integer> managerOrigins = new ArrayList<>();
        List<Integer> lastParents = new ArrayList<>();
        for (Origin origin : originList) {
            if(origin.getParent_origin_id().toString().equals("0")){
                managerOrigins.add(origin.getOrigin_id());
            }else if(origin.getParent_origin_id().toString().equals("1")){
                managerOrigins.add(origin.getOrigin_id());
                lastParents.add(origin.getOrigin_id());
            }else{
                Integer parentId = origin.getParent_origin_id();
                if(lastParents.contains(parentId)){
                    managerOrigins.add(origin.getOrigin_id());
                }
            }
        }

        System.out.println(managerOrigins);

        List<User> allUser = userService.listAllUser();
        for (User user : allUser) {
            int userId = user.getUser_id();
            Origin originForUser = originService.getOriginByUser(userId);
            if(originForUser!=null){
                if(managerOrigins.contains(originForUser.getOrigin_id())){
                    user.setUser_type("0");
                    userService.updateUser(user);
                    System.out.println(user);
                }
            }
        }
    }

//    @Test
    public void testChangeUserRole(){
        List<User> allUser = userService.listAllUser();
        for (User user : allUser) {
            String user_type = user.getUser_type();
            UserRole userRole = new UserRole();
            userRole.setUser_id(user.getUser_id());
            if(user_type.equals(0)){
                userRole.setUser_role_id(3);
                userRoleService.saveUserRole(userRole);
            }else{
                userRole.setUser_role_id(2);
                userRoleService.saveUserRole(userRole);
            }
        }

    }

    @Test
    public void  ttt(){
        String ss = MD5.getMD5Value("111111");
        System.out.println(ss);
    }

}
