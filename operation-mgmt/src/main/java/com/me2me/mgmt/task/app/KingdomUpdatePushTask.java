package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Specification;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;

@Component("kingdomUpdatePushTask")
public class KingdomUpdatePushTask {

	private static final Logger logger = LoggerFactory.getLogger(KingdomUpdatePushTask.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
    private UserService userService;
	@Autowired
	private JPushService jPushService;
	
	@Value("${systemMode}")
	private int mode;
	
	public void doTask(){
		logger.info("7天未更新王国推送任务开始...");
		long s = System.currentTimeMillis();
		
		if(mode != 2){
			logger.info("防止测试环境存在正式用户，故本推送任务不在非正式环境下执行");
			long e = System.currentTimeMillis();
			logger.info("7天未更新王国推送任务结束，共耗时["+(e-s)/1000+"]秒");
			return;
		}
		
		List<Long> uids = activityService.get7dayKingdomUpdateUids();
		if(null != uids && uids.size() > 0){
			long total = uids.size();
			logger.info("total..["+total+"]..");
			List<Long> uidList = new ArrayList<Long>();
			for(Long uid : uids){
				uidList.add(uid);
				if(uidList.size() >= 200){
					push(uidList);
					total = total - uidList.size();
					logger.info("push "+uidList.size()+", remain "+total+".");
					uidList.clear();
				}
			}
			if(uidList.size() > 0){
				push(uidList);
				logger.info("push "+uidList.size()+", remain 0.");
			}
		}
		
		long e = System.currentTimeMillis();
		logger.info("7天未更新王国推送任务结束，共耗时["+(e-s)/1000+"]秒");
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
				map.put("link_url", "http://webapp.me-to-me.com"+Specification.LinkPushType.KINGDOM_NOT_UPDATE.linkUrl+"?uid="+ut.getUid()+"&token="+ut.getToken());

		        String alias = String.valueOf(ut.getUid());

		        jPushService.payloadByIdExtra(alias,  Specification.LinkPushType.KINGDOM_NOT_UPDATE.message, map);
			}
		}
	}
}
