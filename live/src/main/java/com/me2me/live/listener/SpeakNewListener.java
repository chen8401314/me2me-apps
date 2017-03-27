package com.me2me.live.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.me2me.cache.service.CacheService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.SpeakNewEvent;
import com.me2me.live.model.Topic;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class SpeakNewListener {

	private final ApplicationEventBus applicationEventBus;
	private final CacheService cacheService;
	private final UserService userService;
	private final LiveMybatisDao liveMybatisDao;
	
	@Autowired
    public SpeakNewListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, CacheService cacheService, UserService userService){
        this.applicationEventBus = applicationEventBus;
        this.cacheService = cacheService;
        this.liveMybatisDao = liveMybatisDao;
        this.userService = userService;
    }
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void speakNew(SpeakNewEvent speakNewEvent) {
		//新的逻辑，这里只要是可见内容的发言，都会通知非自己的人
		//核心圈通知非核心圈1小时逻辑继续生效
		Topic topic = liveMybatisDao.getTopicById(speakNewEvent.getTopicId());
		if(null == topic){
			return;
		}
		
		
	}

}
