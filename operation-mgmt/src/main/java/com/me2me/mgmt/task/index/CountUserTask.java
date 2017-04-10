package com.me2me.mgmt.task.index;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.user.service.UserService;
/**
 * 用来统计用户信息,如关注数，粉丝数等。
 * @author zhangjiwei
 * @date Apr 7, 2017
 */
@Component
public class CountUserTask{
	private static final Logger logger = LoggerFactory.getLogger(CountUserTask.class);
	
	@Autowired
	private UserService userService;
	private boolean userIndexRunning=false;
	
	@Scheduled(cron="0 0 2 * * ?")		// 启动时执行，每天夜晚2点执行。
	public void userIndexJob() {
		if(userIndexRunning){
			return ;
		}
		try{
			userIndexRunning = true;
			  logger.info("启动用户信息统计任务");
			userService.countUserByDay();
            logger.info("任务完成");
		}catch(Exception e){
        	logger.error("任务执行失败", e);
        }
		userIndexRunning=false;
	}
	
}
