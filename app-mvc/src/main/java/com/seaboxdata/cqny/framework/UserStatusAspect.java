package com.seaboxdata.cqny.framework;

import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.authvalidate.bean.LoginResult;
import com.workbench.auth.authvalidate.controller.AbstractLoginController;
import com.workbench.auth.user.entity.User;
import com.workbench.auth.user.entity.UserStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(100)
public class UserStatusAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void allMethod(){}

    @Around("allMethod()")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        Object targer = joinPoint.getTarget();
        String sigName = joinPoint.getSignature().getName();
        if(!(targer instanceof AbstractLoginController)&&!"changePwd".equals(sigName)
        &&!(targer instanceof AbstractLoginController)&&!"selectOriginType".equals(sigName)
        &&!(targer instanceof AbstractLoginController)&&!"getUserInfo".equals(sigName)
        ){

            Object user = SessionSupport.checkoutUserFromSession();
            if(user!=null){
                User userObj = (User) user;
                String userStatus = userObj.getUser_status();

                if(UserStatus.PWD_EXPIRED.equal(new Integer(userStatus))){
                    Signature signature =  joinPoint.getSignature();
                    Class returnType = ((MethodSignature) signature).getReturnType();

                    if(returnType==JsonResult.class){
                        return JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD,
                                "登陆成功,密码过期需要修改", LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString(),
                                LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                    }
                    return JsonSupport.makeJsonResultStr(JsonResult.RESULT.FAILD,
                            "登陆成功,密码过期需要修改", LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString(),
                            LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                }else if(UserStatus.NEVER_LOGIN.equal(new Integer(userStatus))){
                    Signature signature =  joinPoint.getSignature();
                    Class returnType = ((MethodSignature) signature).getReturnType();

                    if(returnType==JsonResult.class){
                        return JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD,
                                "登陆成功,用户从未登陆过", LoginResult.LOGIN_RESULT.NEVER_LOGIN.toString(),
                                LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                    }
                    return JsonSupport.makeJsonResultStr(JsonResult.RESULT.FAILD,
                            "登陆成功,用户从未登陆过", LoginResult.LOGIN_RESULT.NEVER_LOGIN.toString(),
                            LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                }else if(UserStatus.NOT_NOMAL_TAG.equal(new Integer(userStatus))){
                    Signature signature =  joinPoint.getSignature();
                    Class returnType = ((MethodSignature) signature).getReturnType();

                    if(returnType==JsonResult.class){
                        return JsonSupport.makeJsonpResult(JsonResult.RESULT.FAILD,
                                "登陆成功,用户状态非正常", LoginResult.LOGIN_RESULT.USER_STATS_NOT_NORMAL.toString(),
                                LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                    }
                    return JsonSupport.makeJsonResultStr(JsonResult.RESULT.FAILD,
                            "登陆成功,用户状态非正常", LoginResult.LOGIN_RESULT.USER_STATS_NOT_NORMAL.toString(),
                            LoginResult.LOGIN_RESULT.PWD_EXPIRED.toString());
                }
            }
        }
        return joinPoint.proceed();
    }
}
