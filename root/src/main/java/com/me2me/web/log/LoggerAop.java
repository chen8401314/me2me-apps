package com.me2me.web.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.monitor.dto.AccessLoggerDto;
import com.me2me.monitor.service.MonitorService;
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
    private MonitorService monitorService;

    public void before(JoinPoint joinPoint){
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        StringBuilder headers = new StringBuilder();
        Enumeration<String> enumeration = request.getHeaderNames();
        Map<String,Object> map = Maps.newConcurrentMap();
        while (enumeration.hasMoreElements()){
            String header = enumeration.nextElement();
            headers.append(header).append(":").append(request.getHeader(header)).append("\n");
            map.put(header,request.getHeader(header));
        }
        log.info("========================================================================================");
        log.info("= REQUEST HEADERS : \n" + headers);
        log.info("= REQUEST URL : " + request.getRequestURL());
        log.info("= REQUEST METHOD : " + request.getMethod());
        log.info("= REQUEST INVOKE METHOD : " +joinPoint.getSignature().getDeclaringTypeName()+"."+ joinPoint.getSignature().getName());
        log.info("= REQUEST ARGUMENTS : " + Arrays.toString(joinPoint.getArgs()));
        AccessLoggerDto accessLoggerDto = new AccessLoggerDto();
        accessLoggerDto.setHeaders(JSON.toJSONString(map));
        accessLoggerDto.setMethod(request.getMethod());
        List<Object> args = Lists.newArrayList();
        Object[] os = joinPoint.getArgs();
        for(Object o : os) {
            args.add(o);
        }
        try{
            String origin = JSON.toJSONString(args);
            List<Map> paramsMap = JSON.parseArray(origin,Map.class);
            for(Map m : paramsMap) {
                if(m.get("uid")!=null) {
                    long uid = Long.valueOf(m.get("uid").toString());
                    accessLoggerDto.setUid(uid);
                    break;
                }
            }
            accessLoggerDto.setUri(request.getRequestURI());
            accessLoggerDto.setParams(JSON.toJSONString(joinPoint.getArgs()));
            monitorService.saveAccessLog(accessLoggerDto);
        }catch (Exception e){
            log.info("ex ignore :"+e.getMessage());
        }
    }
    public void after(JoinPoint joinPoint){
        long execTime = System.currentTimeMillis() - startTime.get();
        log.info("= EXECUTE TIME IS : " + execTime);
        log.info("========================================================================================");
    }


}
