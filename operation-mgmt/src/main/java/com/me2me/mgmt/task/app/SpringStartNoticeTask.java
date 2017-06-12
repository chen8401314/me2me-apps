package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.me2me.activity.service.ActivityService;
import com.me2me.sms.service.SmsService;

@Component("springStartNoticeTask")
public class SpringStartNoticeTask {

	private static final Logger logger = LoggerFactory.getLogger(SpringStartNoticeTask.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
	private SmsService smsService;
	
	public void doTask(){
		logger.info("春节活动即将开始通知任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.springStartNotice();
		}catch(Exception e){
			logger.error("春节活动即将开始通知任务执行失败", e);
		}
		
		long e = System.currentTimeMillis();
		logger.info("春节活动即将开始通知任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
    private void springStartNotice(){
    	//先获取所有手机用户手机号
    	List<String> mobileList = activityService.getAllUserMobilesInApp();
    	if(null == mobileList || mobileList.size() == 0){
    		logger.info("no mobile user in app.");
    		mobileList = new ArrayList<String>();
    	}else{
    		logger.info("total ["+mobileList.size()+"] mobiles in app..");
    	}
    	
    	//获取7天活动所有报名手机号
    	List<String> sdayMobileList = activityService.getAll7DayMobiles();
    	if(null != sdayMobileList && sdayMobileList.size() > 0){
    		logger.info("total ["+sdayMobileList.size()+"] mobile in 7day activity!");
    		for(String m : sdayMobileList){
    			if(!mobileList.contains(m)){
    				mobileList.add(m);
    			}
    		}
    	}else{
    		logger.info("no mobile in 7day activity!");
    	}
    	
    	logger.info("total ["+mobileList.size()+"] mobiles need to send message!");
    	
    	List<String> msgList = new ArrayList<String>();
    	msgList.add("1");
        msgList.add("27");
    	
    	long total = 0;
    	List<String> sendList = new ArrayList<String>();
    	for(String mobile : mobileList){
    		if(checkMobile(mobile)){
    			total++;
    			sendList.add(mobile);
    			if(sendList.size() >= 180){
    				smsService.send7dayCommon("148537", sendList, msgList);
                    logger.info("send [" + sendList.size() + "] user!");
                    sendList.clear();
    			}
    		}
    	}
    	if(sendList.size() > 0){
    		smsService.send7dayCommon("148537", sendList, msgList);
            logger.info("send [" + sendList.size() + "] user!");
            sendList.clear();
    	}
    	logger.info("total ["+total+"] mobiles send!");
    }
    
    private boolean checkMobile(String mobile){
    	if(!StringUtils.isEmpty(mobile)){
    		if(!mobile.startsWith("100") && !mobile.startsWith("111")
    				&& !mobile.startsWith("123") && !mobile.startsWith("1666")
    				&& !mobile.startsWith("180000") && !mobile.startsWith("18888888")
    				&& !mobile.startsWith("18900") && !mobile.startsWith("19000")
    				&& !mobile.startsWith("2") && !mobile.startsWith("8")){
    			return true;
    		}
    	}
    	return false;
    }
}
