package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.event.NoticePushEvent;

@Component
@Slf4j
public class NoticePushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	
	@Autowired
	public NoticePushListener(ApplicationEventBus applicationEventBus, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void noticePush(NoticePushEvent event){
		log.info("user notice push..begin");
		JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("count", "1");
        String alias = String.valueOf(event.getUid());
        jPushService.payloadByIdForMessage(alias, jsonObject.toString());
        log.info("user notice push..end");
	}
}
