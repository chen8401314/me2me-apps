package com.me2me.web.handler;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.me2me.common.security.SecurityUtils;
import com.me2me.core.exception.AccessSignNotMatchException;
import com.me2me.core.exception.AppIdException;
import com.me2me.core.exception.TokenNullException;
import com.me2me.core.exception.UidAndTokenNotMatchException;
import com.me2me.user.model.ApplicationSecurity;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;
import com.me2me.web.
        JsonSecurity;
import com.qiniu.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
public class AccessSecurityHandler extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    private static List<String> WHITE_LIST = Lists.newArrayList();

    private static List<String> INTERNAL_WHITE_LIST = Lists.newArrayList();

    private static List<String> TRUST_REQUEST_LIST = Lists.newArrayList();

    static {
        WHITE_LIST.add("/api/user/login");
        WHITE_LIST.add("/api/user/signUp");
        WHITE_LIST.add("/api/user/findEncrypt");
        WHITE_LIST.add("/api/user/verify");
        WHITE_LIST.add("/api/user/getBasicDataByType");
        WHITE_LIST.add("/api/user/versionControl");

        INTERNAL_WHITE_LIST.add("/api/console/showContents");
        INTERNAL_WHITE_LIST.add("/api/console/showActivity");
        INTERNAL_WHITE_LIST.add("/api/content/publish");
        INTERNAL_WHITE_LIST.add("/api/console/createActivity");
        INTERNAL_WHITE_LIST.add("/api/console/option");
        INTERNAL_WHITE_LIST.add("/api/console/createActivityNotice");
        INTERNAL_WHITE_LIST.add("/api/console/showDetails");
        INTERNAL_WHITE_LIST.add("/api/console/bindAccount");
        INTERNAL_WHITE_LIST.add("/api/io/getQiniuAccessToken");

        TRUST_REQUEST_LIST.add("/api/user/getSpecialUserProfile");


    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String is_skip = request.getParameter("is_skip");
        if(TRUST_REQUEST_LIST.contains(request.getRequestURI())){
            is_skip = "ok";
        }
        if(!"ok".equals(is_skip)){
            if(!INTERNAL_WHITE_LIST.contains(request.getRequestURI())) {
                String value = request.getParameter("security");
                JsonSecurity jsonSecurity = JSON.parse(value, JsonSecurity.class);
                // 检测签名
                ApplicationSecurity applicationSecurity = userService.getApplicationSecurityByAppId(jsonSecurity.getAppId());
                if (applicationSecurity == null) {
                    throw new AppIdException("appId not exists!");
                } else {
                    String secretKey = applicationSecurity.getSecretKey();
                    String sign = SecurityUtils.sign(jsonSecurity.getAppId(), secretKey, String.valueOf(jsonSecurity.getCurrentTime()), jsonSecurity.getNonce());
                    if (!sign.equals(jsonSecurity.getSign())) {
                        throw new AccessSignNotMatchException("app access sign not match,please check your application!");
                    }
                }
            }
        }
        if(!WHITE_LIST.contains(request.getRequestURI())) {
            String uid = request.getParameter("uid");
            String token = request.getParameter("token");
            if (Strings.isNullOrEmpty(uid) || Strings.isNullOrEmpty(token)) {
                throw new TokenNullException("uid or token is null!");
            } else {
                long tempUid = Long.valueOf(uid);
                UserToken userToken = userService.getUserByUidAndToken(tempUid, token);
                if (userToken == null) {
                    throw new UidAndTokenNotMatchException("uid and token not matches!");
                } else {
                    return true;
                }
            }
        }else{
            return true;
        }
    }
}
