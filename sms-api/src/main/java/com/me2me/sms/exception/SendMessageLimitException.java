package com.me2me.sms.exception;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/4.
 */
public class SendMessageLimitException extends RuntimeException {

    public SendMessageLimitException(String message){
        super(message);
    }

}
