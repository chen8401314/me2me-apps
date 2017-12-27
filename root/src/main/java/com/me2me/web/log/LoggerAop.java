package com.me2me.web.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.monitor.dto.AccessLoggerDto;
import com.me2me.monitor.service.MonitorService;
import com.me2me.user.dto.AppHttpAccessDTO;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpResponse;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc329 on 2017/4/20.
 */
@Component
@Slf4j
public class LoggerAop {


    private ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    @Autowired
    private UserService userService;

    public void before(JoinPoint joinPoint){
        startTime.set(System.currentTimeMillis());
    }
    public void after(JoinPoint joinPoint){
    	
        
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        
        String uid = "0";
		Map<String, String> paramMap = new HashMap<>();
		Enumeration<String> em = request.getParameterNames();
		String paramName = null;
		while (em.hasMoreElements()) {
			paramName = em.nextElement();
			if("uid".equals(paramName)){
				uid = request.getParameter(paramName);
			}
			paramMap.put(paramName, request.getParameter(paramName));
		}
		
		String httpParams = JSON.toJSONString(paramMap);
		long currentTime = System.currentTimeMillis();
        long execTime = currentTime - startTime.get();
        log.info("[{}]-[{}]-[{}], EXECUTE TIME : [{}ms]", uid, request.getRequestURI(), httpParams, execTime);
        
        // 过滤一下接口
        if(request.getRequestURI().startsWith("/api/console")
                ||request.getRequestURI().startsWith("/api/home/initSquareUpdateId")
                || request.getRequestURI().startsWith("/api/mobile")
                || request.getRequestURI().startsWith("/api/spread")){
            return;
        }
        
        long longuid = 0;
		try{
			longuid = Long.valueOf(uid);
		}catch(Exception ignore){}
		
		AppHttpAccessDTO dto = new AppHttpAccessDTO();
		dto.setUid(longuid);
		dto.setRequestUri(request.getRequestURI());
		dto.setRequestMethod(request.getMethod());
		dto.setRequestParams(httpParams);
		dto.setStartTime(startTime.get());
		dto.setEndTime(currentTime);
		userService.saveUserHttpAccess(dto);
    }


}
