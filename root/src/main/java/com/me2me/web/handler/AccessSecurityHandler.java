package com.me2me.web.handler;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Strings;
import com.me2me.common.security.SecurityUtils;
import com.me2me.core.exception.AccessSignNotMatchException;
import com.me2me.core.exception.AppIdException;
import com.me2me.core.exception.TokenNullException;
import com.me2me.core.exception.UidAndTokenNotMatchException;
import com.me2me.user.model.ApplicationSecurity;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;
import com.me2me.web.JsonSecurity;
import com.qiniu.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
public class AccessSecurityHandler extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String is_skip = request.getParameter("is_skip");
//        if(!"ok".equals(is_skip)){
//            String value = request.getParameter("security");
//            JsonSecurity jsonSecurity = JSON.parse(value, JsonSecurity.class);
//            // 检测签名
//            ApplicationSecurity applicationSecurity = userService.getApplicationSecurityByAppId(jsonSecurity.getAppId());
//            if(applicationSecurity==null){
//                throw new AppIdException("appId not exists!");
//            }else{
//                String secretKey = applicationSecurity.getSecretKey();
//                String sign = SecurityUtils.sign(jsonSecurity.getAppId(),secretKey,String.valueOf(jsonSecurity.getCurrentTime()),jsonSecurity.getNonce());
//                if(!sign.equals(jsonSecurity)){
//                    throw new AccessSignNotMatchException("app access sign not match,please check your application!");
//                }
//            }
//        }

        String uid = request.getParameter("uid");
        String token = request.getParameter("token");
        if(Strings.isNullOrEmpty(uid)||Strings.isNullOrEmpty(token)){
            throw new TokenNullException("uid or token is null!");
        }else{
            long tempUid = Long.valueOf(uid);
            UserToken userToken = userService.getUserByUidAndToken(tempUid,token);
            if(userToken==null){
                throw new UidAndTokenNotMatchException("uid and token not matches!");
            }else {
                return true;
            }
        }
    }
}
