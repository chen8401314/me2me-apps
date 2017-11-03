package com.me2me.sms.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;

/**
 * 小米Push
 * @author pc340
 *
 */
@Slf4j
@Component
public class XiaomiPushServiceImpl implements SpecialPushService {
	
	private static final String APP_SECRET_KEY = "JHxXzRex1lmBOANZPajbHQ==";
	private static final String MY_PACKAGE_NAME = "com.mao.zx.metome";
	
	private static final String TITLE = "米汤";

	@Override
	public void payloadByIdExtra(String uid, String content,
			Map<String, String> extraMaps) {
		Constants.useOfficial();
		
		Sender sender = new Sender(APP_SECRET_KEY);
		
		
		Message.Builder builder = new Message.Builder().title(TITLE)
				.description(content).payload(content)
				.restrictedPackageName(MY_PACKAGE_NAME).notifyType(1);
		if(null != extraMaps && extraMaps.size() > 0){
			builder.passThrough(0);//通知栏消息
			for(Map.Entry<String, String> entry : extraMaps.entrySet()){
				builder.extra(entry.getKey(), entry.getValue());
			}
		}else{
			builder.passThrough(1);//透传
		}
		try{
			sender.sendToAlias(builder.build(), uid, 1);
		}catch(Exception e){
			log.error("小米推送失败", e);
		}
	}

}
