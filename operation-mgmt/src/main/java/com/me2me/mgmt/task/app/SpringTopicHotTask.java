package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.activity.dto.KingdomHotDTO;
import com.me2me.activity.dto.TopicCountDTO;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.DateUtil;

@Component("springTopicHotTask")
public class SpringTopicHotTask {

	private static final Logger logger = LoggerFactory.getLogger(SpringTopicHotTask.class);
	
	@Autowired
    private ActivityService activityService;
	
	public void doTask(){
		logger.info("春节活动王国热度任务开始...");
		long s = System.currentTimeMillis();
		
		//获取当前时间key
		String dayKey = DateUtil.date2string(new Date(), "yyyyMMdd");
		logger.info("当前计算王国热度的统计时间key=" + dayKey);
		
		logger.info("删除当前时间戳的榜单表记录...");
		activityService.deleteKingdomListByDayKey(dayKey);
		logger.info("删除当前时间戳的榜单表记录完成");
		
		//获取所有待计算的春节王国
		List<Map<String,Object>> topicList = activityService.getActivityTopicIds(2);
		
		if(null != topicList && topicList.size() > 0){
			logger.info("有效春节王国共["+topicList.size()+"]个");
			List<Map<String,Object>> runItems = new ArrayList<Map<String,Object>>();
			for(Map<String,Object> map : topicList){
				runItems.add(map);
				if(runItems.size() >= 500){
					this.execHot(runItems, dayKey);
					logger.info("处理了["+runItems.size()+"]个");
					runItems.clear();
				}
			}
			if(runItems.size() > 0){
				this.execHot(runItems, dayKey);
				logger.info("处理了["+runItems.size()+"]个");
			}
		}else{
			logger.info("有效春节王国共[0]个");
		}
		
		long e = System.currentTimeMillis();
		logger.info("春节活动王国热度任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	private void execHot(List<Map<String,Object>> topicList, String dayKey){
		//topicId-->uid
		Map<String,String> tMap = new HashMap<String, String>();
		for(Map<String,Object> m : topicList){
			tMap.put(String.valueOf((Long)m.get("topic_id")), String.valueOf((Long)m.get("uid")));
		}
		
		List<Long> topicIds = new ArrayList<Long>();
		for(Map.Entry<String,String> entry : tMap.entrySet()){
			topicIds.add(Long.valueOf(entry.getKey()));
		}
		
		//获取今天所有王国评论和更新数
		String day = DateUtil.date2string(new Date(), "yyyy-MM-dd");
		List<TopicCountDTO> list = activityService.getTopicCountsByTopicIdsAndTime(topicIds, day+" 00:00:00", day+" 23:59:59");
		
		List<KingdomHotDTO> batchUpdateList = new ArrayList<KingdomHotDTO>();
		List<KingdomHotDTO> batchInsertList = new ArrayList<KingdomHotDTO>();
		
		List<Long> noUpdateTopicIds = new ArrayList<Long>();
		noUpdateTopicIds.addAll(topicIds);
		
		if(null != list && list.size() > 0){
			KingdomHotDTO updateDTO = null;
			KingdomHotDTO insertDTO = null;
			Long tid = null;
			for(TopicCountDTO dto : list){
				updateDTO = new KingdomHotDTO();
				insertDTO = new KingdomHotDTO();
				
				int conditions = 0;
				if(dto.getUpdateCount() >= 5){
					conditions = 1;
				}
				
				long hot = (dto.getUpdateCount()*4+dto.getReviewCount()*3)*51;
				
				tid = Long.valueOf(dto.getTopicId());
				noUpdateTopicIds.remove(tid);
				
				updateDTO.setTopicId(dto.getTopicId());
				updateDTO.setConditions(conditions);
				updateDTO.setHot(hot);
				batchUpdateList.add(updateDTO);
				
				insertDTO.setActivityId(2);
				insertDTO.setConditions(conditions);
				insertDTO.setDayKey(dayKey);
				insertDTO.setHot(hot);
				insertDTO.setTopicId(dto.getTopicId());
				insertDTO.setUid(Long.valueOf(tMap.get(String.valueOf(dto.getTopicId()))));
				batchInsertList.add(insertDTO);
			}
		}
		
		if(noUpdateTopicIds.size() > 0){
			logger.info("no update today size ["+noUpdateTopicIds.size()+"], update start...");
			activityService.updateKingdomHotInitByTopicIds(noUpdateTopicIds);
			logger.info("update end");
		}
		
		if(batchUpdateList.size() > 0){
			logger.info("batch update start..["+batchUpdateList.size()+"]");
			activityService.batchUpdateKingdomHot(batchUpdateList);
			logger.info("batch update end.");
		}
		if(batchInsertList.size() > 0){
			logger.info("batch insert start..["+batchInsertList.size()+"]");
			activityService.batchInsertKingdomList(batchInsertList);
			logger.info("batch insert end.");
		}
	}
}
