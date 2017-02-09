package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.event.NoticeMessagePushEvent;

@Component
@Slf4j
public class NoticeMessagePushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	
	@Autowired
	public NoticeMessagePushListener(ApplicationEventBus applicationEventBus, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void noticeMessagePush(NoticeMessagePushEvent event){
		log.info("NoticeMessagePush start...");
		JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",Specification.PushObjectType.NOTICE.index);
        jsonObject.addProperty("level", event.getLevel());
        String alias = String.valueOf(event.getTargetUid());
        jPushService.payloadByIdExtra(alias, event.getMessage(), JPushUtils.packageExtra(jsonObject));
		log.info("NoticeMessagePush end...");
	}
}
