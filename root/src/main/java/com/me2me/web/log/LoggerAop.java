package com.me2me.web.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created by pc329 on 2017/4/20.
 */
@Component
@Slf4j
public class LoggerAop {


    private ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    public void before(JoinPoint joinPoint){
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        StringBuilder headers = new StringBuilder();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String header = enumeration.nextElement();
            headers.append(header).append(":").append(request.getHeader(header)).append("\n");
        }
        log.info("========================================================================================");
        log.info("= REQUEST HEADERS : \n" + headers);
        log.info("= REQUEST URL : " + request.getRequestURL());
        log.info("= REQUEST METHOD : " + request.getMethod());
        log.info("= REQUEST INVOKE METHOD : " +joinPoint.getSignature().getDeclaringTypeName()+"."+ joinPoint.getSignature().getName());
        log.info("= REQUEST ARGUMENTS : " + Arrays.toString(joinPoint.getArgs()));

    }
    public void after(JoinPoint joinPoint){
        long execTime = System.currentTimeMillis() - startTime.get();
        log.info("= EXECUTE TIME IS : " + execTime);
        log.info("========================================================================================");
    }


}
