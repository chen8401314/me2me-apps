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
import com.me2me.activity.model.AactivityStage;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Specification;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;

@Component("recommandPushTask")
public class RecommandPushTask {

	private static final Logger logger = LoggerFactory.getLogger(RecommandPushTask.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
    private UserService userService;
	@Autowired
	private JPushService jPushService;
	
	@Value("${systemMode}")
	private int mode;
	
	public void doTask(){
		logger.info("7天活动推荐异性任务开始...");
		long s = System.currentTimeMillis();

		if(mode != 2){
			logger.info("防止测试环境存在正式用户，故本推送任务不在非正式环境下执行");
			long e = System.currentTimeMillis();
			logger.info("7天活动推荐异性任务结束，共耗时["+(e-s)/1000+"]秒");
			return;
		}
		
		boolean can = false;
		List<AactivityStage> all = activityService.getAllStage(1);
		if(null != all && all.size() > 0){
			for(AactivityStage stage : all){
				if(stage.getType() == 0){
					if(stage.getStage() == 2 || stage.getStage() == 3 || stage.getStage() == 5){
						can = true;
						break;
					}
				}
			}
		}
		
		if(can){//符合阶段条件的才推荐
			//获取所有待推荐的人（有单人王国，没有双人王国）
			List<Long> uids = activityService.getParingUser();
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
		}
		
		long e = System.currentTimeMillis();
		logger.info("7天活动推荐异性任务结束，共耗时["+(e-s)/1000+"]秒");
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
				map.put("link_url", "http://webapp.me-to-me.com"+Specification.LinkPushType.FORCED_PAIRING.linkUrl+"?uid="+ut.getUid()+"&token="+ut.getToken());

		        String alias = String.valueOf(ut.getUid());

		        userService.pushWithExtra(alias,  Specification.LinkPushType.FORCED_PAIRING.message, map);
			}
		}
	}
}
