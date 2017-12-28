package com.me2me.web.log;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.me2me.user.dto.AppHttpAccessDTO;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
		
		try{
			List<Object> args = Lists.newArrayList();
	        Object[] os = joinPoint.getArgs();
	        for(Object o : os) {
	            args.add(o);
	        }
	        String origin = JSON.toJSONString(args);
	        List<Map> paramsMap = JSON.parseArray(origin,Map.class);
	        if(null != paramsMap && paramsMap.size() > 0){
	        	Map<String, Object> m = paramsMap.get(0);//我们只有一个参数对象
	        	for(Map.Entry<String, Object> entry : m.entrySet()){
	        		if("uid".equals(entry.getKey())){
	        			uid = entry.getValue().toString();
	        		}
	        		paramMap.put(entry.getKey(), entry.getValue().toString());
	        	}
	        }
		}catch(Exception e){
			log.error("解析参数错误", e);
		}

		String httpParams = JSON.toJSONString(paramMap);
		long currentTime = System.currentTimeMillis();
        long execTime = currentTime - startTime.get();
        log.info("[{}]-[{}]-[{}], EXECUTE TIME : [{}ms]", uid, request.getRequestURI(), httpParams, execTime);
        
        // 过滤一下接口
        if(request.getRequestURI().startsWith("/api/console")
                || request.getRequestURI().startsWith("/api/home/initSquareUpdateId")
                || request.getRequestURI().startsWith("/api/mobile")
                || request.getRequestURI().startsWith("/api/spread")
                || request.getRequestURI().startsWith("/api/live/getUpdate")//王国详情轮询接口
                || request.getRequestURI().startsWith("/api/user/noticeReddotQuery")//通知红点轮询接口
                ){
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
