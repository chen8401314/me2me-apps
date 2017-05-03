package com.me2me.mgmt.task.app;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.content.service.ContentService;
import com.me2me.user.service.UserService;

/**
 * 清除求关注超7天的记录任务
 * @author pc340
 *
 */
@Component
public class AppCleanDataTask {

	private static final Logger logger = LoggerFactory.getLogger(AppCleanDataTask.class);
	
	private static final int CLEAN_HOUR = 7 * 24;//7天
	
	@Autowired
	private UserService userService;
	@Autowired
	private ContentService contentService;
	
	/**
	 * 清除求关注超时的记录任务
	 */
	@Scheduled(cron="0 0 */1 * * ?")
	public void cleanSeekFollow(){
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
	
	
	/**
	 * 清除无效的userProfile记录
	 */
	//@Scheduled(cron="0 0,15,30,45 * * * ?")
	@Scheduled(cron="0 */1 * * * ?")
	public void cleanUselessUserProfile(){
		logger.info("清除无效的userProfile记录任务开始");
		long s = System.currentTimeMillis();
		try{
			//先将无效的记录查询出来
			StringBuilder sb = new StringBuilder();
			sb.append("select p.uid,max(p.id) as mid,count(1) as cc");
			sb.append(" from user_profile p group by p.uid");
			sb.append(" having count(1)>1");
			List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
			if(null != list && list.size() > 0){
				logger.info("本次共["+list.size()+"]个需要清除的数据");
				Long id = null;
				for(Map<String, Object> m : list){
					id = (Long)m.get("mid");
					userService.deleteUserProfile(id);
				}
			}else{
				logger.info("本次无需要清除的数据");
			}
		}catch(Exception ex){
			logger.error("任务出错", ex);
		}
		long e = System.currentTimeMillis();
		logger.info("清除无效的userProfile记录任务完成,共耗时["+(e-s)+"]毫秒");
	}
}
