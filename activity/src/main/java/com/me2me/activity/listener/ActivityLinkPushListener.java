package com.me2me.activity.listener;

import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.me2me.activity.event.LinkPushEvent;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;

@Component
@Slf4j
public class ActivityLinkPushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	
	@Autowired
	public ActivityLinkPushListener(ApplicationEventBus applicationEventBus, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void linkPush(LinkPushEvent event){
		log.info("link push start...");
		Map<String, String> map = Maps.newHashMap();
		map.put("type", "4");
		map.put("messageType", "13");
		map.put("link_url", event.getLinkUrl());

        String alias = String.valueOf(event.getMessage());

        jPushService.payloadByIdExtra(alias,  event.getMessage(), map);
		log.info("link push end...");
	}
}
