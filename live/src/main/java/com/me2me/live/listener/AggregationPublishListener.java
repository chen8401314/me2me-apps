package com.me2me.live.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.event.AggregationPublishEvent;

@Component
@Slf4j
public class AggregationPublishListener {

	private final ApplicationEventBus applicationEventBus;
	
	@Autowired
	public AggregationPublishListener(ApplicationEventBus applicationEventBus){
		this.applicationEventBus = applicationEventBus;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void publish(AggregationPublishEvent event) {
		log.info("aggregation publish begin...uid:"+event.getUid()+",topicId:"+event.getTopicId()+",fid:"+event.getFid());
		//获取所有子王国
		
		
		log.info("aggregation publish end...");
	}
}
