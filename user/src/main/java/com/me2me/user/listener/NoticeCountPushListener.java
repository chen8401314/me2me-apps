package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.event.NoticeCountPushEvent;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class NoticeCountPushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	private final UserService userService;
	
	@Autowired
	public NoticeCountPushListener(ApplicationEventBus applicationEventBus, JPushService jPushService, UserService userService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
		this.userService = userService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void noticeCountPush(NoticeCountPushEvent event){
		log.info("user notice push..begin");
		JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("count", "1");
        String alias = String.valueOf(event.getUid());
        userService.pushWithExtra(alias, jsonObject.toString(), null);
        log.info("user notice push..end");
	}
}
