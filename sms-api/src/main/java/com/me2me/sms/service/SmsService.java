package com.me2me.sms.service;

import com.me2me.common.web.Response;
import com.me2me.sms.dto.ImRefreshDto;
import com.me2me.sms.dto.ImSendMessageDto;
import com.me2me.sms.dto.ImUserInfoDto;
import com.me2me.sms.dto.VerifyDto;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface SmsService {

	Response send(VerifyDto verifyDto);

    boolean verify(VerifyDto verifyDto);

    boolean sendMessage(String nickName ,String awardName ,String mobile ,String OperateMobile);

    boolean sendQIMessage(String mobile);

    boolean sendQIauditMessage(List mobileList);

    //报名成功
    void send7daySignUp(String mobile);

    //审核成功 list
    void send7dayApply(List mobileList);

    //七天活动封装短信接口
    void send7dayCommon(String templateId, List<String> mobileList, List<String> messageList);

    ImUserInfoDto getIMUsertoken(long uid,String nickName,String avatar) throws Exception;
    
    public ImSendMessageDto sendSysMessage(String userId, String content) throws Exception;

    public ImRefreshDto refreshUser(String userId, String name, String portraitUri) throws Exception;
}
