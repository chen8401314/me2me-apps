package com.me2me.sms.service;

import com.google.common.collect.Maps;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageIosDto;
import com.tencent.xinge.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/30
 * Time :16:37
 */
@Service
public class XgPushServiceImpl implements XgPushService {

    private static final String SECRET_KEY = "735da3540ee6dfa534e3549e8367c46f";

    //private static final String SECRET_KEY = "f918d18b3a2a6fcd4940c51b09001095";


    private static final String SECRET_KEY_IOS = "1a4120d4fc7cfa4dde4705f0d2f14c4a";

    //private static final String SECRET_KEY_IOS = "f18275d8ed27a934a053a15ad1d4658f";


    private static final long ACCESS_ID = 2100199603L;

    //private static final long ACCESS_ID = 2100202664L;

    private static final long ACCESS_ID_IOS = 2200199604L;

    //private static final long ACCESS_ID_IOS = 2200202665L;

    private static final int EXPIRE_TIME = 86400;

    private static final int EXPIRE_TIME_IOS = 86400;

    private static final int BADGE_IOS = 1;

    @Value("#{app.IOS_PUSH_ENV}")
    private String IOS_PUSH_ENV;


    @Override
    public PushLogDto pushSingleDevice(PushMessageAndroidDto pushMessageAndroidDto) {
        Message message = buildMessage(pushMessageAndroidDto);
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.pushSingleDevice(pushMessageAndroidDto.getToken(),message);
        return getPushLog(pushMessageAndroidDto.getContent(), ret);
    }

    @Override
    public PushLogDto pushAllDevice(PushMessageAndroidDto pushMessageAndroidDto) {
        Message message = buildMessage(pushMessageAndroidDto);
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.pushAllDevice(0, message);
        return getPushLog(pushMessageAndroidDto.getContent(), ret);
    }

    private PushLogDto getPushLog(String content, JSONObject ret) {
        int result = 0;
        try {
            result = ret.getInt("ret_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(result != 0){
            PushLogDto pushLogDto = new PushLogDto();
            pushLogDto.setContent(content);
            pushLogDto.setRetCode(result);
            return pushLogDto;
        }else{
            return null;
        }
    }

    @Override
    public PushLogDto pushSingleDeviceIOS(PushMessageIosDto pushMessageIosDto) {
        MessageIOS message = buildMessageIOS(pushMessageIosDto);
        XingeApp xinge = new XingeApp(ACCESS_ID_IOS, SECRET_KEY_IOS);
        JSONObject ret = null;
        if(("product").equals(IOS_PUSH_ENV)){
            ret = xinge.pushSingleDevice(pushMessageIosDto.getToken(), message,XingeApp.IOSENV_PROD);
        }else{
            ret = xinge.pushSingleDevice(pushMessageIosDto.getToken(), message,XingeApp.IOSENV_DEV);
        }
        return getPushLog(pushMessageIosDto.getContent(), ret);
    }

    @Override
    public PushLogDto pushAllDeviceIOS(PushMessageIosDto pushMessageIosDto)  {
        MessageIOS message = buildMessageIOS(pushMessageIosDto);
        XingeApp xinge = new XingeApp(ACCESS_ID_IOS, SECRET_KEY_IOS);
        JSONObject ret = null;
        if(IOS_PUSH_ENV.equals("product")){
            ret = xinge.pushAllDevice(0,message,XingeApp.IOSENV_PROD);
        }else{
            ret = xinge.pushAllDevice(0,message,XingeApp.IOSENV_DEV);
        }
        return getPushLog(pushMessageIosDto.getContent(), ret);
    }

    private Message buildMessage(PushMessageAndroidDto pushMessageAndroidDto) {
        Message message = new Message();
        message.setContent(pushMessageAndroidDto.getContent());
        message.setType(Message.TYPE_NOTIFICATION);
        message.setExpireTime(EXPIRE_TIME);
        Map<String,Object> map = Maps.newHashMap();
        map.put("messageType",pushMessageAndroidDto.getMessageType());
        message.setCustom(map);
        message.setStyle(new Style(1,1,0,1,0));
        return message;
    }

    private MessageIOS buildMessageIOS(PushMessageIosDto pushMessageIosDto) {
        MessageIOS message = new MessageIOS();
        message.setExpireTime(EXPIRE_TIME_IOS);
        message.setAlert(pushMessageIosDto.getContent());
        message.setBadge(BADGE_IOS);
        Map<String,Object> map = Maps.newHashMap();
        map.put("messageType",pushMessageIosDto.getMessageType());
        message.setCustom(map);
        TimeInterval acceptTime = new TimeInterval(0,0,23,59);
        message.addAcceptTime(acceptTime);
        return message;
    }



//    public static void main(String[] args) {
//        XgPushServiceImpl push = new XgPushServiceImpl();
//        for (int i = 0 ;i <= 1 ;i++ ) {
//            PushMessageAndroidDto pushMessageDto = new PushMessageAndroidDto();
//            pushMessageDto.setTitle("xinge test new" +i);
//            pushMessageDto.setContent("xinge test new" +i);
//            //pushMessageDto.setToken("
//            // ");
//            pushMessageDto.setToken("5948d751e20f5b1e46edaec58feaa5ef3ba35128");
//            pushMessageDto.setMessageType(Message.TYPE_NOTIFICATION);
//            push.pushSingleDevice(pushMessageDto);
//        }
//
//        for (int i = 0 ;i <= 1 ;i++ ) {
//            PushMessageIosDto pushMessageDto = new PushMessageIosDto();
//            pushMessageDto.setTitle("xinge test new" +i);
//            pushMessageDto.setContent("xinge test new" +i);
//            //pushMessageDto.setToken("c242772fa7ff6d3bf93fecba2b220dcf6c176cd70a2ae6994b5ac0b104beaea1");
//            //pushMessageDto.setToken("5231f01ba563ed1045bfe1628f9838464d966877f11ec4f754accf804bd5bd24");
//            pushMessageDto.setToken("4b0c1ddd375498328e5f35b9da5c284cfc4a4a4ca7a14512e25e02ee5ed4b68e");
//            push.pushSingleDeviceIOS(pushMessageDto);
//        }
//    }
}
