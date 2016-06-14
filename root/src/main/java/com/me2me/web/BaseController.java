package com.me2me.web;

import com.me2me.common.web.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
@Slf4j
public class BaseController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Response init(RuntimeException e){
        for(StackTraceElement element : e.getStackTrace()){
            log.error("stack trace: {}",element);
        }
        return Response.failure(e.getMessage());
    }
}
