package com.me2me.sms.channel;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.me2me.common.sms.YunXinSms;
import com.me2me.sms.service.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/2.
 */
@Component
@Slf4j
public class MessageChannel {

    @Autowired
    private MessageClient messageClient;

    private static final String VOICE_DISPLAY_TIMES = "3";



    /**
     * 语音通道
     */
    interface VoiceVerify{
        void verify();
    }

    /**
     * 短信通道
     */
    interface SmsVerify{
        void verify();
    }

    interface Check{
        boolean check();
    }


    public void send(int channel,String code,String mobile){
        HashMap<String,Object> result = null;
        if(channel==ChannelType.NORMAL_SMS.index){
            // 短信验证
            result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile,"106877",new String[]{code,"5"});
        }else if(channel== ChannelType.VOICE_SMS.index){
            // 语音验证
            result = messageClient.getCcpRestSDK().voiceVerify(code,mobile,"021-54070708",VOICE_DISPLAY_TIMES,"","","");
        }else if(channel==ChannelType.NET_CLOUD_SMS.index){
            // 网易云信
            boolean sendResult = YunXinSms.sendSms(mobile);
            log.info("yun xin sms send result {}",sendResult);
        }
        log.info("short message send result {}",result);
    }


}
