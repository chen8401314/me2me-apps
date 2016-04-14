package com.me2me.web.handler;

import com.google.common.base.Strings;
import com.me2me.core.exception.TokenNullException;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        String uid = request.getParameter("uid");
        String token = request.getParameter("token");
        if(Strings.isNullOrEmpty(uid)||Strings.isNullOrEmpty(token)){
            throw new TokenNullException("token为空");
        }else{
            return true;
        }
    }
}
