package com.me2me.activity.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.me2me.activity.event.TaskPushEvent;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class ActivityTaskPushListener {

	
	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	private final UserService userService;
	
	@Autowired
	public ActivityTaskPushListener(ApplicationEventBus applicationEventBus, JPushService jPushService, UserService userService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
		this.userService = userService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void taskPush(TaskPushEvent event){
		log.info("task push start...");
		if(null != event.getUids() && event.getUids().size() > 0){
			long total = event.getUids().size();
			log.info("total..["+total+"]..");
			List<Long> uidList = new ArrayList<Long>();
			for(Long uid : event.getUids()){
				uidList.add(uid);
				if(uidList.size() >= 200){
					push(uidList, event);
					total = total - uidList.size();
					log.info("push "+uidList.size()+", remain "+total+".");
					uidList.clear();
				}
			}
			if(uidList.size() > 0){
				push(uidList, event);
				log.info("push "+uidList.size()+", remain 0.");
			}
		}
		log.info("task push end...");
	}
	
	private void push(List<Long> uids, TaskPushEvent event){
		if(null == uids || uids.size() == 0){
			return;
		}
		List<UserToken> tokens = userService.getUserTokenByUids(uids);
		if(null != tokens && tokens.size() > 0){
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			for(UserToken ut : tokens){
				map.put("link_url", event.getLinkUrl()+"?uid="+ut.getUid()+"&token="+ut.getToken());
		        String alias = String.valueOf(ut.getUid());
		        jPushService.payloadByIdExtra(alias,  event.getMessage(), map);
			}
		}
	}
}
