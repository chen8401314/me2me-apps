package com.me2me.sms.channel;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
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
public class MessageChannel {

    @Autowired
    private MessageClient messageClient;

    private static final String VOICE_DISPLAY_TIMES = "3";

    public enum ChannelType{
        NORMAL_SMS(1,"短信验证方式"),

        VOICE_SMS(2,"语音验证方式");

        public int index;

        public String name;

        ChannelType(int index,String name){
            this.index = index;
            this.name = name;
        }
    }

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
            messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile,"1",new String[]{code,"5"});
        }else if(channel==ChannelType.VOICE_SMS.index){
            // 语音验证
            messageClient.getCcpRestSDK().voiceVerify(code,mobile,"",VOICE_DISPLAY_TIMES,"","","");
        }
    }


}
