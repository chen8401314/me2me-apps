package com.me2me.mgmt.task.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.activity.service.ActivityService;

@Component
public class AutoAudit7DayUserTask {

	private static final Logger logger = LoggerFactory.getLogger(AutoAudit7DayUserTask.class);
	
	@Autowired
    private ActivityService activityService;
	
	public void doTask(){
		logger.info("7天活动自动审核通过任务开始...");
		long s = System.currentTimeMillis();
		
		activityService.oneKeyAudit();
		
		long e = System.currentTimeMillis();
		logger.info("7天活动自动审核通过任务结束，共耗时["+(e-s)/1000+"]秒");
	}
}
