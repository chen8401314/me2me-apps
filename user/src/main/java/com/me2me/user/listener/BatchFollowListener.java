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
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.BatchFollowEvent;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class BatchFollowListener {

	private final ApplicationEventBus applicationEventBus;
	private final UserMybatisDao userMybatisDao;
	private final JPushService jPushService;
	private final UserService userService;
	
	@Autowired
	public BatchFollowListener(ApplicationEventBus applicationEventBus, UserMybatisDao userMybatisDao, JPushService jPushService, UserService userService){
		this.applicationEventBus = applicationEventBus;
		this.userMybatisDao = userMybatisDao;
		this.jPushService = jPushService;
		this.userService = userService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void follow(BatchFollowEvent event){
		log.info("batch follow push start...");
		
		if(null != event.getTargetUids() && event.getTargetUids().size() > 0){
			UserProfile sourceUser = userMybatisDao.getUserProfileByUid(event.getSourceUid());
			String message = sourceUser.getNickName() + "关注了你！";
			
			JsonObject jsonObject = new JsonObject();
	        jsonObject.addProperty("messageType",Specification.PushMessageType.FOLLOW.index);
	        jsonObject.addProperty("type",Specification.PushObjectType.SNS_CIRCLE.index);
	        
	        JsonObject redDot = new JsonObject();
	        redDot.addProperty("fansCount","1");
	        
	        String alias = null;
			for(Long targetUid : event.getTargetUids()){
				//关注提醒
				alias = targetUid.toString();
				userService.pushWithExtra(alias, message, JPushUtils.packageExtra(jsonObject));
				//粉丝红点提醒
				userService.pushWithExtra(alias,redDot.toString(), null);
			}
		}
		
		log.info("batch follow push end!");
	}
}
