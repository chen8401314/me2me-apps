package com.me2me.sms.service;

import com.me2me.sms.dto.VerifyDto;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface SmsService {

    void send(VerifyDto verifyDto);

    boolean verify(VerifyDto verifyDto);

    boolean sendMessage(String nickName ,String awardName ,String mobile ,String OperateMobile);

    boolean sendQIMessage(String mobile);

    boolean sendQIauditMessage(List mobileList);

//    void sendTest(String mobile ,String templateId, String[] datas);

    //报名成功
    void send7daySignUp(String mobile);

    //审核成功 list
    void send7dayApply(List mobileList);

}
