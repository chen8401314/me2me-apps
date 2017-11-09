package com.me2me.sms.service;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

public class SpecailPushFactory {

	public static SpecialPushService getSpecialPushService(int type){
		SpecialPushService instance = null;
		if(type == Specification.PushType.XIAOMI.index){//小米推送
			instance = SpringContextHolder.getBean(XiaomiPushServiceImpl.class);
		} else if(type == Specification.PushType.HUAWEI.index){//华为推送
			instance = SpringContextHolder.getBean(HuaweiPushServiceImpl.class);
		} else{
			throw new RuntimeException("参数非法....");
		}
		return instance;
	}
}
