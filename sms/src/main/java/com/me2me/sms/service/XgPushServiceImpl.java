package com.me2me.sms.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.common.web.Specification;
import com.me2me.sms.dto.PushMessageDto;
import com.me2me.sms.dto.PushMessageDtoIos;
import com.tencent.xinge.*;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/30
 * Time :16:37
 */
public class XgPushServiceImpl implements XgPushService {

    private static final String SECRET_KEY = "735da3540ee6dfa534e3549e8367c46f";

    private static final String SECRET_KEY_IOS = "1a4120d4fc7cfa4dde4705f0d2f14c4a";

    private static final long ACCESS_ID = 2100199603L;

    private static final long ACCESS_ID_IOS = 2200199604L;

    private static final int EXPIRE_TIME = 86400;

    private static final int EXPIRE_TIME_IOS = 86400;

    private static final int BADGE_IOS = 1;

    @Override
    public JSONObject pushSingleDevice(PushMessageDto pushMessageDto) {
        Message message = buildMessage(pushMessageDto);
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.pushSingleDevice(pushMessageDto.getToken(),message);
        return ret;
    }

    @Override
    public JSONObject pushAllDevice(PushMessageDto pushMessageDto) {
        Message message = buildMessage(pushMessageDto);
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.pushAllDevice(0, message);
        return (ret);
    }

    @Override
    public JSONObject pushSingleDeviceIOS(PushMessageDtoIos pushMessageDtoIos) {
        MessageIOS message = buildMessageIOS(pushMessageDtoIos);
        XingeApp xinge = new XingeApp(ACCESS_ID_IOS, SECRET_KEY_IOS);
        JSONObject ret = xinge.pushSingleDevice(pushMessageDtoIos.getToken(), message, XingeApp.IOSENV_DEV);
        return (ret);
    }

    @Override
    public JSONObject pushAllDeviceIOS(PushMessageDtoIos pushMessageDtoIos)  {
        MessageIOS message = buildMessageIOS(pushMessageDtoIos);
        XingeApp xinge = new XingeApp(ACCESS_ID_IOS, SECRET_KEY_IOS);
        JSONObject ret = xinge.pushAllDevice(0,message,XingeApp.IOSENV_DEV);
        return (ret);
    }

    @Override
    public JSONObject queryPushStatus() {
        List<String> pushIdList = Lists.newArrayList();
        pushIdList.add("390");
        pushIdList.add("389");
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.queryPushStatus(pushIdList);
        return (ret);

    }

    @Override
    public JSONObject queryDeviceCount() {
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        JSONObject ret = xinge.queryDeviceCount();
        return (ret);
    }

    private Message buildMessage(PushMessageDto pushMessageDto) {
        Message message = new Message();
        message.setTitle(pushMessageDto.getTitle());
        message.setContent(pushMessageDto.getContent());
        message.setType(pushMessageDto.getMessageType());
        message.setExpireTime(EXPIRE_TIME);
        message.setCustom(pushMessageDto.getCustom());
        message.setStyle(new Style(1,1,0,1,0));
        return message;
    }

    private MessageIOS buildMessageIOS(PushMessageDtoIos pushMessageDtoIos) {
        MessageIOS message = new MessageIOS();
        message.setExpireTime(EXPIRE_TIME_IOS);
        message.setAlert(pushMessageDtoIos.getContent());
        message.setBadge(BADGE_IOS);
        TimeInterval acceptTime = new TimeInterval(0,0,23,59);
        message.addAcceptTime(acceptTime);
        message.setCustom(pushMessageDtoIos.getCustom());
        return message;
    }


    public static void main(String[] args) {
//        XingeApp app = new XingeApp(ACCESS_ID, SECRET_KEY);
//        List<TagTokenPair> list = Lists.newArrayList();
//        list.add(new TagTokenPair("dandan","c242772fa7ff6d3bf93fecba2b220dcf6c176cd70a2ae6994b5ac0b104beaea1"));
//       / app.BatchSetTag(list);
        XgPushServiceImpl push = new XgPushServiceImpl();
        for (int i = 0 ;i <= 1 ;i++ ) {
            PushMessageDto pushMessageDto = new PushMessageDto();
            pushMessageDto.setTitle("messageTile" +i);
            pushMessageDto.setContent("借酒消愁愁更愁，买根黄瓜抹点油." +i);
            pushMessageDto.setToken("5948d751e20f5b1e46edaec58feaa5ef3ba35128");
            pushMessageDto.setMessageType(Message.TYPE_NOTIFICATION);
            pushMessageDto.getCustom().put("type",Specification.PushMessageType.LIKE.index);
            pushMessageDto.getCustom().put("typeName",Specification.PushMessageType.LIKE.name);
            System.out.println(push.pushSingleDevice(pushMessageDto));
        }

//        for (int i = 0 ;i <= 10 ;i++ ) {
//            PushMessageDtoIos pushMessageDto = new PushMessageDtoIos();
//            pushMessageDto.setTitle("messageTile" +i);
//            pushMessageDto.setContent("借酒消愁愁更愁，买根黄瓜抹点油." +i);
//            pushMessageDto.setToken("c242772fa7ff6d3bf93fecba2b220dcf6c176cd70a2ae6994b5ac0b104beaea1");
//
//            System.out.println(push.pushSingleDeviceIOS(pushMessageDto));
//        }

    }
}
