package com.me2me.sms;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/8.
 */
public class JpushClientMain {

    private static final String masterSecret = "467e198daf63ff0596b0784d";

    private static final String appKey = "9222161c4591256016b4efee";



    // 120c83f76022301312c
    public static void main(String[] args){
        Message message = Message.content("hello");
        JPushClient jPushClient = new JPushClient(masterSecret,appKey);
        PushPayload pushPayload = PushPayload
                .newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias("297"))
//                .setAudience(Audience.registrationId("1517bfd3f7c16d7fa31"))
                .setNotification(Notification.alert("fdsfds"))
                .setMessage(message)
                .build();
        JsonObject jsonExtra = new JsonObject();
        jsonExtra.addProperty("name","peter");
        jsonExtra.addProperty("age",20);
        jsonExtra.addProperty("gender",1);

        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.registrationId("1517bfd3f7c16d7fa31"))
                .setNotification(Notification.newBuilder()
                        // android 平台
                        .setAlert("您有新的消息，约炮！")
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .addExtra("jsonExtra", jsonExtra)
                                .build())
                        // ios 平台
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra("jsonExtra",jsonExtra).build())
                        .build())
                .build();
        try {
            jPushClient.sendPush(pushPayload);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }
//        try {
//            PushPayload.alertAll("您有10条消息！请注意查收");
//            PushResult pushResult = jPushClient.sendPush(payload);
//        }catch (Exception e){
//            System.out.println(e.getClass());
//        }

    }
}
