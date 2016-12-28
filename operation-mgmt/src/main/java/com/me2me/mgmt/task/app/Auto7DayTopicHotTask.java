package com.me2me.mgmt.task.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.activity.dto.TopicCountDTO;
import com.me2me.activity.service.ActivityService;

@Component("auto7DayTopicHotTask")
public class Auto7DayTopicHotTask {

	private static final Logger logger = LoggerFactory.getLogger(Auto7DayTopicHotTask.class);
	
	@Autowired
    private ActivityService activityService;
	
	public void doTask(){
		logger.info("7天活动王国热度任务开始...");
		long s = System.currentTimeMillis();

		List<Long> list = activityService.get7dayTopicIds();
		if(null != list && list.size() > 0){
			TopicCountDTO dto = null;
			for(Long tid : list){
				dto = activityService.getTopicCount(tid);
				if(null != dto){
					int hot = dto.getReadCount()+dto.getUpdateCount()*4+dto.getLikeCount()+dto.getReviewCount()*3;
					activityService.updateTopicHot(tid, hot);
				}
			}
		}
		
		long e = System.currentTimeMillis();
		logger.info("7天活动王国热度任务结束，共耗时["+(e-s)/1000+"]秒");
	}
}
