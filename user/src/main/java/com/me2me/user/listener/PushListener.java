package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.PushExtraEvent;
import com.me2me.user.model.UserLastChannel;

@Component
@Slf4j
public class PushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	private final UserMybatisDao userMybatisDao;
	
	@Autowired
	public PushListener(ApplicationEventBus applicationEventBus, JPushService jPushService, UserMybatisDao userMybatisDao){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
		this.userMybatisDao = userMybatisDao;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void pushWithExtra(PushExtraEvent event){
		log.info("push with extra...begin...");
		//极光推送都会推
		if(null != event.getExtraMaps() && event.getExtraMaps().size() > 0){
			jPushService.payloadByIdExtra(event.getUid(), event.getMessage(), event.getExtraMaps());
		}else{
			jPushService.payloadByIdForMessage(event.getUid(), event.getMessage());
		}
		//如果是小米的有特殊推送
		UserLastChannel ulc = userMybatisDao.getUserLastChannelByUid(Long.valueOf(event.getUid()));
		if(null != ulc && !StringUtils.isEmpty(ulc.getChannel())){
			if("xiaomi".equals(ulc.getChannel())){//小米的推送
				jPushService.specialPush(event.getUid(), event.getMessage(), event.getExtraMaps(), Specification.PushType.XIAOMI.index);
			}
		}
		
		log.info("push with extra...end.");
	}
}
