package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.core.exception.UserGagException;
import com.me2me.sms.exception.SendMessageLimitException;
import com.me2me.sms.exception.SendMessageTimeException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
public class BaseController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Response init(RuntimeException e){
        if(e instanceof SendMessageLimitException || e instanceof SendMessageTimeException){
            return Response.success(20094,e.getMessage());
        }
        if(e instanceof UserGagException){//禁言返回
        	return Response.failure(ResponseStatus.USER_IS_GAGGED.status, ResponseStatus.USER_IS_GAGGED.message);
        }
        e.printStackTrace();
        return Response.failure(e.getMessage());
    }

}
