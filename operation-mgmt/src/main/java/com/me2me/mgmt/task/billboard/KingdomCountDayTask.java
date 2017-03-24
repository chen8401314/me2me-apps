package com.me2me.mgmt.task.billboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.live.service.LiveService;

@Component("kingdomCountDayTask")
public class KingdomCountDayTask {

	private static final Logger logger = LoggerFactory.getLogger(KingdomCountDayTask.class);
	
	@Autowired
	private LiveService liveService;
	
	public void doTask(){
		logger.info("王国信息统计任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("王国信息统计任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("王国信息统计任务结束，共耗时"+(e-s)/1000+"秒");
	}
	
	private void execTask(){
		liveService.statKingdomCountDay();
	}
}
