package com.me2me.mgmt.task.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.activity.service.ActivityService;

@Component("springStartNoticeTask")
public class SpringStartNoticeTask {

	private static final Logger logger = LoggerFactory.getLogger(SpringStartNoticeTask.class);
	
	@Autowired
    private ActivityService activityService;
	
	public void doTask(){
		logger.info("春节活动即将开始通知任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			activityService.springStartNotice();
		}catch(Exception e){
			logger.error("春节活动即将开始通知任务执行失败", e);
		}
		
		long e = System.currentTimeMillis();
		logger.info("春节活动即将开始通知任务结束，共耗时["+(e-s)/1000+"]秒");
	}
}
