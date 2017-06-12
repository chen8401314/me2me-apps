package com.me2me.mgmt.task.app;

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
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Specification;
import com.me2me.sms.service.JPushService;

@Component("springPushTop10Task")
public class SpringPushTop10Task {

	private static final Logger logger = LoggerFactory.getLogger(SpringPushTop10Task.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
	private JPushService jPushService;
	
	@Value("${webappUrl}")
	private String webappUrl;
	
	public void doTask(){
		logger.info("春节活动TOP10推送任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			Date now = new Date();
			//活动第二天到最后，以及活动结束的第一天
			boolean canPush = false;
			List<AactivityStage> list = activityService.getAllStage(2);
			if(null != list && list.size() > 0){
				for(AactivityStage stage : list){
					if(stage.getStage() == 2){//活动期间除第一天，其他都要有推送
						if(checkInStage(now, stage) && !DateUtil.isSameDay(now, stage.getStartTime())){
							canPush = true;
							break;
						}
					}else if(stage.getStage() == 3){//结束阶段第一天也有推送
						if(checkInStage(now, stage) && DateUtil.isSameDay(now, stage.getStartTime())){
							canPush = true;
							break;
						}
					}
				}
			}
			if(canPush){
				logger.info("start push...");
				Date date = DateUtil.addDay(now, -1);
				String daykey = DateUtil.date2string(date, "yyyyMMdd");
				//全部用户推送
				Map<String, String> map = Maps.newHashMap();
				map.put("type", "4");
				map.put("messageType", "13");
				map.put("link_url", webappUrl+Specification.LinkPushType.TOP10_PUSH.linkUrl+"?day=" + daykey);
				
				jPushService.payloadByIdsExtra(true, null, Specification.LinkPushType.TOP10_PUSH.message, map);
//				String[] uids = new String[]{"446"};
//				jPushService.payloadByIdsExtra(false, uids, Specification.LinkPushType.TOP10_PUSH.message, map);
			}else{
				logger.info("当前不处在TOP10推送阶段");
			}
		}catch(Exception e){
			logger.error("春节活动TOP10推送任务执行失败", e);
		}
		
		long e = System.currentTimeMillis();
		logger.info("春节活动TOP10推送任务结束，共耗时["+(e-s)/1000+"]秒");
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
}
