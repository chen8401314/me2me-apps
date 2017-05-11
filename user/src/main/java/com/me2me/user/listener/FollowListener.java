package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.FollowEvent;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class FollowListener {
	
	private final ApplicationEventBus applicationEventBus;
	private final UserMybatisDao userMybatisDao;
	private final CacheService cacheService;
	private final UserService userService;
	private final JPushService jPushService;
	
	@Autowired
	public FollowListener(ApplicationEventBus applicationEventBus, UserMybatisDao userMybatisDao, CacheService cacheService, UserService userService, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.userMybatisDao = userMybatisDao;
		this.cacheService = cacheService;
		this.userService = userService;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

	@Subscribe
	public void follow(FollowEvent event){
		log.info("follow push start...");
		
		//关注推送1小时内不再推送
		boolean needPush = false;
		String key = "follow:push:status:" + event.getTargetUid();
		if(!StringUtils.isEmpty(cacheService.get(key))){
			needPush = true;
			
			cacheService.set(key, "1");
			cacheService.expire(key, 3600);//一小时超时时间
		}
		
		JsonObject jsonObject = null;
		//关注提醒,一小时内只提醒一次
		if(needPush){
			UserProfile sourceUser = userMybatisDao.getUserProfileByUid(event.getSourceUid());
			jsonObject = new JsonObject();
	        jsonObject.addProperty("messageType",Specification.PushMessageType.FOLLOW.index);
	        jsonObject.addProperty("type",Specification.PushObjectType.SNS_CIRCLE.index);
	        userService.pushWithExtra(String.valueOf(event.getTargetUid()), sourceUser.getNickName() + "关注了你！", JPushUtils.packageExtra(jsonObject));
		}

        //粉丝数量红点，红点什么时候都要的
        log.info("follow fans add push start");
        jsonObject = new JsonObject();
        jsonObject.addProperty("fansCount","1");
        jPushService.payloadByIdForMessage(String.valueOf(event.getTargetUid()),jsonObject.toString());
        log.info("follow fans add push end ");

        log.info("follow push end!");
	}
}
