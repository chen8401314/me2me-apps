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

		//先处理单人王国
		logger.info("先处理单人王国");
		List<Long> singleList = activityService.get7dayTopicIdsByType(1);
		if(null != singleList && singleList.size() > 0){
			logger.info("有效单人王国共["+singleList.size()+"]个");
			TopicCountDTO dto = null;
			for(Long tid : singleList){
				dto = activityService.getTopicCount(tid);
				if(null != dto){
					int hot = dto.getReadCount()+dto.getUpdateCount()*4+dto.getLikeCount()+dto.getReviewCount()*3;
					activityService.updateTopicHot(tid, hot);
				}
			}
		}else{
			logger.info("有效单人王国共[0]个");
		}
		logger.info("单人王国处理完成，下面处理双人王国");
		List<Long> doubleList = activityService.get7dayTopicIdsByType(2);
		if(null != doubleList && doubleList.size() > 0){
			logger.info("有效双人王国共["+doubleList.size()+"]个");
			TopicCountDTO dto = null;
			for(Long tid : doubleList){
				//先算自身热度
				dto = activityService.getTopicCount(tid);
				if(null != dto){
					int hot = dto.getReadCount()+dto.getUpdateCount()*4+dto.getLikeCount()+dto.getReviewCount()*3;
					//再加上双人双方的单人王国的热度
					
				}
			}
		}else{
			logger.info("有效双人王国共[0]个");
		}
		logger.info("双人王国处理完成");
		long e = System.currentTimeMillis();
		logger.info("7天活动王国热度任务结束，共耗时["+(e-s)/1000+"]秒");
	}
}
