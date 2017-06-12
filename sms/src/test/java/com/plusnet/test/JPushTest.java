package com.plusnet.test;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JPushTest {
	
	private static final String masterSecret = "467e198daf63ff0596b0784d";

    private static final String appKey = "9222161c4591256016b4efee";
	
	public static void main(String[] args){
		ClientConfig config = ClientConfig.getInstance();
        config.setTimeToLive(86400 * 10);
		JPushClient jPushClient = new JPushClient(masterSecret,appKey,null,config);
		
		Options options = Options.newBuilder().setApnsProduction(false).build();
		Map<String,String> extraMaps = new HashMap<String, String>();
		extraMaps.put("type", "4");
		extraMaps.put("messageType", "13");
//		extraMaps.put("link_url", "https://testwebapp.me-to-me.com/NewYear/my/rank?day=20170131");
		extraMaps.put("link_url", "https://testwebapp.me-to-me.com/NewYear/my/main");
		
		String[] uids = new String[]{"446"};
		
		PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(uids))
                .setNotification(Notification.newBuilder()
                        .setAlert("我就是测试一下,要不你点一下")
                         // android 平台
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .addExtras(extraMaps).build())
                        // ios 平台
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(extraMaps).build())
                        .build()).setOptions(options)
                .build();
        try {
            jPushClient.sendPush(payload);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }
        
        System.out.println("好了呀，不用看了");
	}

}
