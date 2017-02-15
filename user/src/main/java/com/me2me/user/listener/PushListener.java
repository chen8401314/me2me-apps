package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.event.PushExtraEvent;

@Component
@Slf4j
public class PushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	
	@Autowired
	public PushListener(ApplicationEventBus applicationEventBus, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void pushWithExtra(PushExtraEvent event){
		log.info("push with extra...begin...");
		if(null != event.getExtraMaps() && event.getExtraMaps().size() > 0){
			jPushService.payloadByIdExtra(event.getUid(), event.getMessage(), event.getExtraMaps());
		}else{
			jPushService.payloadByIdForMessage(event.getUid(), event.getMessage());
		}
		log.info("push with extra...end.");
	}
}
