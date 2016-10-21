package com.me2me.sms.service;

import com.me2me.sms.dto.VerifyDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface SmsService {

    void send(VerifyDto verifyDto);

    boolean verify(VerifyDto verifyDto);

    boolean sendMessage(String nickName ,String awardName ,String mobile);

}
