package com.me2me.mgmt.task.billboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.cache.service.CacheService;
import com.me2me.content.service.ContentService;

@Component("colorfulKingdomBillBoardTask")
public class ColorfulKingdomBillBoardTask {

	private static final Logger logger = LoggerFactory.getLogger(ColorfulKingdomBillBoardTask.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	
	public void doTask(){
		logger.info("[最丰富多彩的王国]榜单任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("[最丰富多彩的王国]榜单任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("[最丰富多彩的王国]榜单任务结束，共耗时"+(e-s)/1000+"秒");
	}
	
	private void execTask(){
		
	}
}
