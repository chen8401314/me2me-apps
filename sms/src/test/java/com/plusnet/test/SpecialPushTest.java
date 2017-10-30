package com.plusnet.test;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

public class SpecialPushTest {

	public static void main(String[] args){
		try{
			xiaomiPush();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("===完成了");
	}
	
	public static void xiaomiPush() throws Exception{
		String APP_SECRET_KEY = "JHxXzRex1lmBOANZPajbHQ==";
		String MY_PACKAGE_NAME = "com.mao.zx.metome";
		
		Constants.useOfficial();
		Sender sender = new Sender(APP_SECRET_KEY);
		String title = "米汤啦啦啦";
		String messagePayload = "你关注的王国《大爷的》更新了";
		String description = "你关注的王国《大爷的》更新了";
		String alias = "1118"; // alias非空白，不能包含逗号，长度小于128。
		Message.Builder builder = new Message.Builder().title(title)
				.description(description).payload(messagePayload)
				.restrictedPackageName(MY_PACKAGE_NAME).notifyType(1); // 使用默认提示音提示
		builder.extra("messageType", "9");
		builder.extra("type", "2");
		builder.extra("topicId", "8745");
		builder.extra("contentType", "1000");
		builder.extra("internalStatus", "0");
		
		Message message = builder.build();
		Result result = sender.sendToAlias(message, alias, 0); // 根据alias，发送消息到指定设备上，不重试。
		System.out.println(("Server response: "+"MessageId: " + result.getMessageId()
                + " ErrorCode: " + result.getErrorCode().getValue()
                + " ErrorCodeDesc" + result.getErrorCode().getFullDescription()
                + " Reason: " + result.getReason()));
	}
}
