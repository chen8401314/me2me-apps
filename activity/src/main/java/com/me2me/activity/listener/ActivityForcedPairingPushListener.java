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
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class ActivityForcedPairingPushListener {

	private final ApplicationEventBus applicationEventBus;
	private final JPushService jPushService;
	private final UserService userService;
	
	@Autowired
	public ActivityForcedPairingPushListener(ApplicationEventBus applicationEventBus, JPushService jPushService, UserService userService){
		this.applicationEventBus = applicationEventBus;
		this.jPushService = jPushService;
		this.userService = userService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void forcedPairingPush(List<Long> uids){
		log.info("forcedPairingPush start...");
		if(null != uids && uids.size() > 0){
			long total = uids.size();
			log.info("total..["+total+"]..");
			List<Long> uidList = new ArrayList<Long>();
			for(Long uid : uids){
				uidList.add(uid);
				if(uidList.size() >= 200){
					push(uidList);
					total = total - uidList.size();
					log.info("push "+uidList.size()+", remain "+total+".");
					uidList.clear();
				}
			}
			if(uidList.size() > 0){
				push(uidList);
				log.info("push "+uidList.size()+", remain 0.");
			}
			
		}
		log.info("forcedPairingPush end...");
	}
	
	private void push(List<Long> uids){
		if(null == uids || uids.size() == 0){
			return;
		}
		List<UserToken> tokens = userService.getUserTokenByUids(uids);
		if(null != tokens && tokens.size() > 0){
			for(UserToken ut : tokens){
				Map<String, String> map = Maps.newHashMap();
				map.put("type", "4");
				map.put("messageType", "13");
				map.put("link_url", "http://webapp.me-to-me.com/"+Specification.LinkPushType.FORCED_PAIRING.linkUrl+"?uid="+ut.getUid()+"&token="+ut.getToken());

		        String alias = String.valueOf(ut.getUid());

		        jPushService.payloadByIdExtra(alias,  Specification.LinkPushType.FORCED_PAIRING.message, map);
			}
		}
	}
}
