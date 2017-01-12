package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.me2me.activity.model.AactivityStage;
import com.me2me.activity.model.AkingDom;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Specification;
import com.me2me.sms.service.JPushService;
import com.me2me.user.service.UserService;

@Component("springPushNoticeAndHotTask")
public class SpringPushNoticeAndHotTask {

	private static final Logger logger = LoggerFactory.getLogger(SpringPushNoticeAndHotTask.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
	private UserService userService;
	@Autowired
	private JPushService jPushService;
	
	@Value("${webappUrl}")
	private String webappUrl;
	
	public void doTask(){
		logger.info("春节活动提醒以及热度推送任务开始...");
		long s = System.currentTimeMillis();
		try{
			Date now = new Date();
			List<AactivityStage> list = activityService.getAllStage(2);
			if(null != list && list.size() > 0){
				for(AactivityStage stage : list){
					if(stage.getStage() == 1 && checkInStage(now, stage) && DateUtil.isSameDay(now, stage.getEndTime())){
						//处在第一阶段，并且是第一阶段的最后一天（也即活动开始的前一天）
						noticePush();
						break;
					}else if(stage.getStage() == 2 && checkInStage(now, stage)){
						//在活动期间
						hotPush();
						break;
					}
				}
			}
		}catch(Exception e){
			logger.error("春节活动提醒以及热度推送任务执行失败", e);
		}
		long e = System.currentTimeMillis();
		logger.info("春节活动提醒以及热度推送任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	private boolean checkInStage(Date date, AactivityStage stage) {
        if (null == date || null == stage || null == stage.getStartTime() || null == stage.getEndTime()) {
            return false;
        }

        if (date.compareTo(stage.getStartTime()) > 0 && date.compareTo(stage.getEndTime()) < 0) {
            return true;
        }

        return false;
    }
	
	private void noticePush(){
		logger.info("提醒推送...");
		//查询建立春节王国的人
		List<Long> kingdomUids = activityService.getSpringKingdomUids();
		List<Long> pushList = new ArrayList<Long>();
		if(null != kingdomUids && kingdomUids.size() > 0){
			logger.info("push has kingdom...total ["+kingdomUids.size()+"]");
			//推送
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			map.put("link_url", webappUrl+Specification.LinkPushType.HAS_KINGDOM_PUSH.linkUrl);
			
			for(Long u : kingdomUids){
				pushList.add(u);
				if(pushList.size() >= 500){
					this.push(pushList, Specification.LinkPushType.HAS_KINGDOM_PUSH.message, map);
					logger.info("push " + pushList.size() + "...");
					pushList.clear();
				}
			}
			if(pushList.size() > 0){
				this.push(pushList, Specification.LinkPushType.HAS_KINGDOM_PUSH.message, map);
				logger.info("push " + pushList.size() + "...");
				pushList.clear();
			}
			logger.info("push has kingdom end.");
		}else{
			kingdomUids = new ArrayList<Long>();
		}
		//获取系统所有用户uid
		List<Long> allUids = userService.getAllUids();
		if(null != allUids && allUids.size() > 0){
			logger.info("push no kingdom...total ["+(allUids.size()-kingdomUids.size())+"]");
			
			//推送
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			map.put("link_url", webappUrl+Specification.LinkPushType.NO_KINGDOM_PUSH.linkUrl);
			
			for(Long u : allUids){
				if(kingdomUids.contains(u)){
					continue;
				}
				pushList.add(u);
				if(pushList.size() >= 500){
					this.push(pushList, Specification.LinkPushType.NO_KINGDOM_PUSH.message, map);
					logger.info("push " + pushList.size() + "...");
					pushList.clear();
				}
			}
			if(pushList.size() > 0){
				this.push(pushList, Specification.LinkPushType.NO_KINGDOM_PUSH.message, map);
				logger.info("push " + pushList.size() + "...");
				pushList.clear();
			}
			logger.info("push no kingdom end.");
		}
		logger.info("提醒推送完成");
	}
	
	private void hotPush(){
		logger.info("热度推送...");
		//获取不符合条件的春节王国用户，更新数<5
		List<AkingDom> noListKingdomList = activityService.getAkingDomsByConditions(0, 2);
		//获取TOP50
		List<AkingDom> top50KingdomList = activityService.getAkingdomsTop(2, 50);
		
		List<Long> pushList = new ArrayList<Long>();
		if(null != noListKingdomList && noListKingdomList.size() > 0){
			logger.info("push no list user...total ["+(noListKingdomList.size())+"]");
			
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			map.put("link_url", webappUrl+Specification.LinkPushType.NO_LIST_PUSH.linkUrl);
			
			for(AkingDom k : noListKingdomList){
				pushList.add(k.getUid());
				if(pushList.size() >= 500){
					this.push(pushList, Specification.LinkPushType.NO_LIST_PUSH.message, map);
					logger.info("push " + pushList.size() + "...");
					pushList.clear();
				}
			}
			if(pushList.size() > 0){
				this.push(pushList, Specification.LinkPushType.NO_LIST_PUSH.message, map);
				logger.info("push " + pushList.size() + "...");
				pushList.clear();
			}
			logger.info("push no list user end.");
		}else{
			logger.info("没有待推送的不符合条件的用户 ");
		}
		
		if(null != top50KingdomList && top50KingdomList.size() > 0){
			logger.info("push top 3 user...total ["+(top50KingdomList.size()>=3?3:top50KingdomList.size())+"]");
			
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			map.put("link_url", webappUrl+Specification.LinkPushType.LIST_TOP3_PUSH.linkUrl);
			
			for(int i=0;i<top50KingdomList.size()&&i<3;i++){
				pushList.add(top50KingdomList.get(i).getUid());
			}
			
			if(pushList.size() > 0){
				this.push(pushList, Specification.LinkPushType.LIST_TOP3_PUSH.message, map);
				logger.info("push " + pushList.size() + "...");
				pushList.clear();
			}
			logger.info("push top 3 user end.");
		}else{
			logger.info("没有TOP 3的用户待推送");
		}
		
		if(null != top50KingdomList && top50KingdomList.size() > 3){
			logger.info("push top 50(not top 3) user...total ["+(top50KingdomList.size()-3)+"]");
			
			Map<String, String> map = Maps.newHashMap();
			map.put("type", "4");
			map.put("messageType", "13");
			map.put("link_url", webappUrl+Specification.LinkPushType.LIST_TOP50_PUSH.linkUrl);
			
			String message = null;
			for(int i=3;i<top50KingdomList.size();i++){
				message = Specification.LinkPushType.LIST_TOP50_PUSH.message.replace("#{1}#", String.valueOf(i+1));
				jPushService.payloadByIdExtra(String.valueOf(top50KingdomList.get(i).getUid()), message, map);
			}
			logger.info("push top 50(not top 3) user end.");
		}else{
			logger.info("没有TOP50（不包含TOP3）的用户待推送");
		}
	}
	
	private void push(List<Long> uids, String message, Map<String, String> map){
		String[] temp = new String[uids.size()];
		for(int i=0;i<uids.size();i++){
			temp[i] = String.valueOf(uids.get(i));
		}
		jPushService.payloadByIdsExtra(false, temp, message, map);
	}
}
