package com.me2me.sms.service;
import com.google.common.base.Splitter;
import com.me2me.cache.service.CacheService;
import com.me2me.common.sms.YunXinSms;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.channel.MessageClient;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.event.VerifyEvent;
import com.me2me.sms.listener.VerifyCodeListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Autowired
    private CacheService cacheService;

    private static final String VERIFY_PREFIX = "verify:";

    private Splitter splitter = Splitter.on("@").trimResults();

    @Autowired
    private VerifyCodeListener verifyCodeListener;

    @Autowired
    private MessageClient messageClient;

    /**
     * 发送验证码
     * @param verifyDto
     */
    @Override
    public void send(VerifyDto verifyDto){
        // applicationEventBus.post(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel()));
        verifyCodeListener.send(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel(),verifyDto.getIsTest()));
    }

    /**
     * 校验验证码
     * @param verifyDto
     */
    @Override
    public boolean verify(VerifyDto verifyDto) {
        // 网易云信通道验证
        if(verifyDto.getChannel()== ChannelType.NET_CLOUD_SMS.index) {
            return YunXinSms.verify(verifyDto.getMobile(), verifyDto.getVerifyCode());
        }
        // 获取redis中的数据
        String verifyCodeAndSendTimeMillis = cacheService.get(VERIFY_PREFIX+verifyDto.getMobile());
        if(!StringUtils.isEmpty(verifyCodeAndSendTimeMillis)){
            String verifyCode = splitter.splitToList(verifyCodeAndSendTimeMillis).get(0);
            if(verifyDto.getVerifyCode().equals(verifyCode))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String nickName ,String awardName ,String mobile ,String OperateMobile) {
        Boolean isTrue = YunXinSms.sendSms2(nickName ,awardName ,mobile ,OperateMobile);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean sendQIMessage(String mobile) {
        Boolean isTrue = YunXinSms.sendSms3(mobile);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean sendQIauditMessage(List mobileList) {
        Boolean isTrue = YunXinSms.sendSms4(mobileList);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void send7daySignUp(String mobile) {
        HashMap<String,Object> result = null;
        //没有需要传的数据传空，否则报错
        result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile,"142378",new String[]{""});
        System.out.println(result);
    }

    @Override
    public void send7dayApply(List mobileList) {
        String mobiles = getListToString(mobileList);
        HashMap<String,Object> result = null;
        result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobiles,"106877",new String[]{"测试","5"});
    }

    @Override
    public void send7dayCommon(String templateId ,String mobile ,List mobileList ,List message) {
        String messages;
        //审核走List 多个手机号
        if(mobileList != null){
            String mobiles = getListToString(mobileList);
            if(message != null){
                messages = getListToString(message);
            }else {
                messages = "";
            }
            getResult(templateId,mobiles,messages);
        }
        else{
        //其余模板走通用
            if(!org.springframework.util.StringUtils.isEmpty(message)){
                messages = getListToString(message);
            }else {
                messages = "";
            }
                getResult(templateId,mobile,messages);
        }
    }

    public HashMap<String,Object> getResult(String templateId ,String mobiles ,String message){
        HashMap<String,Object> result = null;
        result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobiles,templateId,new String[]{message});
        return result;
    }

    /**
     * List转换成String逗号分隔的形式
     *
     * @param list
     * @return
     */
    public String getListToString(List list) {
        return StringUtils.join(list.toArray(), ",");
    }
}
