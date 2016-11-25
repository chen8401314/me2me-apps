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
import com.me2me.user.event.FollowEvent;
import com.me2me.user.model.UserProfile;

@Component
@Slf4j
public class FollowListener {
	
	private final ApplicationEventBus applicationEventBus;
	private final UserMybatisDao userMybatisDao;
	private final JPushService jPushService;
	
	@Autowired
	public FollowListener(ApplicationEventBus applicationEventBus, UserMybatisDao userMybatisDao, JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.userMybatisDao = userMybatisDao;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

	@Subscribe
	public void follow(FollowEvent event){
		log.info("follow push start...");
		//关注提醒
		UserProfile sourceUser = userMybatisDao.getUserProfileByUid(event.getSourceUid());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageType",Specification.PushMessageType.FOLLOW.index);
        jsonObject.addProperty("type",Specification.PushObjectType.SNS_CIRCLE.index);
        String alias = String.valueOf(event.getTargetUid());
        jPushService.payloadByIdExtra(alias, sourceUser.getNickName() + "关注了你！", JPushUtils.packageExtra(jsonObject));

        //粉丝数量红点
        log.info("follow fans add push start");
        jsonObject = new JsonObject();
        jsonObject.addProperty("fansCount","1");
        alias = String.valueOf(event.getTargetUid());
        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
        log.info("follow fans add push end ");

        log.info("follow push end!");
	}
}
