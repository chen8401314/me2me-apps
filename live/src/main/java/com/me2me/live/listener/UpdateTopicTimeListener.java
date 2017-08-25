package com.me2me.live.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.event.UpdateTopicTimeEvent;
import com.me2me.live.service.UpdateTopicTimeHandler;

@Component
@Slf4j
public class UpdateTopicTimeListener {

	private final ApplicationEventBus applicationEventBus;
	
	private final UpdateTopicTimeHandler updateTopicTimeHandler;
	
	@Autowired
    public UpdateTopicTimeListener(ApplicationEventBus applicationEventBus, UpdateTopicTimeHandler updateTopicTimeHandler){
        this.applicationEventBus = applicationEventBus;
        this.updateTopicTimeHandler = updateTopicTimeHandler;
    }
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void updateTopicTime(UpdateTopicTimeEvent event){
		updateTopicTimeHandler.updateTime(event.getTopicId(), event.getLongTime(), event.getOutTime());
	}
	
}
