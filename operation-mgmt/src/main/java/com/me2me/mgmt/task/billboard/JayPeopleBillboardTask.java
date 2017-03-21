package com.me2me.mgmt.task.billboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.content.model.BillBoardList;
import com.me2me.content.service.ContentService;

@Component("jayPeopleBillboardTask")
public class JayPeopleBillboardTask {

	private static final Logger logger = LoggerFactory.getLogger(JayPeopleBillboardTask.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	
	public void doTask(){
		logger.info("[最爱叨逼叨的话痨国王]榜单任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("[最爱叨逼叨的话痨国王]榜单任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("[最爱叨逼叨的话痨国王]榜单任务结束，共耗时"+(e-s)/1000+"秒");
	}
	
	private void execTask(){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select f.uid,count(1) as cc from topic_fragment f,topic t");
		searchSql.append(" where f.topic_id=t.id and FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
		searchSql.append(" and f.create_time>date_add(now(), interval -7 day)");
		searchSql.append(" group by f.uid order by cc desc limit 100");
		
		List<UserCountItem> resultList = new ArrayList<UserCountItem>();
		
		List<Map<String, Object>> searchList = contentService.queryEvery(searchSql.toString());
		if(null != searchList && searchList.size() > 0){
			logger.info("共有"+searchList.size()+"个国王存在增量数据");
			UserCountItem item = null;
			for(Map<String, Object> m : searchList){
				item = new UserCountItem();
				item.setUid((Long)m.get("uid"));
				item.setCount((Long)m.get("cc"));
				resultList.add(item);
			}
		}else{
			logger.info("共有0个国王存在增量数据");
		}
		
		if(resultList.size() > 0){
			Collections.sort(resultList);
			
			//存入数据库
			//1 查询当前列表key
			String nextCacheKey = null;
			String currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_JAY_PEOPLE);
			if(StringUtils.isNotBlank(currentCacheKey)){
				if(currentCacheKey.equals(Constant.BILLBOARD_KEY_TARGET2)){
					nextCacheKey = Constant.BILLBOARD_KEY_TARGET1;
				}else{
					nextCacheKey = Constant.BILLBOARD_KEY_TARGET2;
				}
			}else{
				nextCacheKey = Constant.BILLBOARD_KEY_TARGET2;
			}
			//2处理待插入对象
			List<BillBoardList> insertList = new ArrayList<BillBoardList>();
			BillBoardList bbl = null;
			UserCountItem item = null;
			String listKey = Constant.BILLBOARD_KEY_JAY_PEOPLE + nextCacheKey;
			for(int i=0;i<resultList.size()&&i<100;i++){
				item = resultList.get(i);
				bbl = new BillBoardList();
				bbl.setListKey(listKey);
				bbl.setTargetId(item.getUid());
				bbl.setType(0);
				bbl.setSinceId((int)item.getCount());
				insertList.add(bbl);
			}
			//3存入数据库
			contentService.insertBillboardList(insertList, listKey);
			//4将新的列表提交到缓存
			cacheService.set(Constant.BILLBOARD_KEY_POPULAR_PEOPLE, nextCacheKey);
		}
	}
	
	@Data
	private class UserCountItem implements Comparable<UserCountItem>{
		private long uid;
		private long count;
		
		@Override
		public int compareTo(UserCountItem o) {
			if(this.count < o.getCount()){
				return 1;
			}else if(this.count > o.getCount()){
				return -1;
			}else{
				return 0;
			}
		}

	}
}
