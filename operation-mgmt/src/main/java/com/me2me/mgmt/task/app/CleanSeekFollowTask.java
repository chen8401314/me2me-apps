package com.me2me.mgmt.task.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.user.service.UserService;

/**
 * 清除求关注超7天的记录任务
 * @author pc340
 *
 */
@Component
public class CleanSeekFollowTask {

	private static final Logger logger = LoggerFactory.getLogger(CleanSeekFollowTask.class);
	
	private static final int CLEAN_HOUR = 7 * 24;//7天
	
	@Autowired
	private UserService userService;
	
	@Scheduled(cron="0 0 */1 * * ?")
	public void clean(){
		logger.info("清除求关注超时的记录任务开始");
		long s = System.currentTimeMillis();
		try{
			userService.cleanSeekFollow(CLEAN_HOUR);
		}catch(Exception ex){
			logger.error("任务出错", ex);
		}
		long e = System.currentTimeMillis();
		logger.info("清除求关注超时的记录任务完成,共耗时["+(e-s)+"]毫秒");
	}
}
