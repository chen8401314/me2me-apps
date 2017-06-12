package com.me2me.mgmt.task.billboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.content.model.BillBoardList;
import com.me2me.content.service.ContentService;

@Component("popularPeopleBillboardTask")
public class PopularPeopleBillboardTask {

	private static final Logger logger = LoggerFactory.getLogger(PopularPeopleBillboardTask.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	
	public void doTask(){
		logger.info("[最受追捧的米汤大咖]榜单任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("[最受追捧的米汤大咖]榜单任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("[最受追捧的米汤大咖]榜单任务结束，共耗时"+(e-s)/1000+"秒");
	}
	
	private void execTask(){
		Map<String, Long> resultUserDataMap = new HashMap<String, Long>();
		
		//获取所有关注增量
		StringBuilder followSql = new StringBuilder();
		followSql.append("select f.target_uid as uid,count(1) as cc");
		followSql.append(" from user_follow f");
		followSql.append(" where f.create_time>date_add(now(), interval -14 day)");
		followSql.append(" group by f.target_uid");
		
		List<Map<String, Object>> followList = contentService.queryEvery(followSql.toString());
		if(null != followList && followList.size() > 0){
			logger.info("共有"+followList.size()+"个人有关注增量");
			for(Map<String, Object> m : followList){
				resultUserDataMap.put(String.valueOf(m.get("uid")), (Long)m.get("cc"));
			}
		}else{
			logger.info("共有0个关注增量");
		}
		logger.info("处理人关注增量完成");
		 
		//获取所有增量成员
		StringBuilder favoriteSql = new StringBuilder();
		favoriteSql.append("select t.*,m.c from topic t,(");
		favoriteSql.append("select lf.topic_id,count(1) as c");
		favoriteSql.append(" from live_favorite lf");
		favoriteSql.append(" where lf.create_time>date_add(now(), interval -14 day)");
		favoriteSql.append(" group by lf.topic_id ) m");
		favoriteSql.append(" where t.id=m.topic_id");
		List<Map<String, Object>> favoriteList = contentService.queryEvery(favoriteSql.toString());
		if(null != favoriteList && favoriteList.size() > 0){
			logger.info("共有"+favoriteList.size()+"个王国有订阅增量");
			String coreCircle = null;
			JSONArray array = null;
			Long uid = null;
			Long count = null;
			for(Map<String, Object> m : favoriteList){
				coreCircle = (String)m.get("core_circle");
				if(StringUtils.isNotBlank(coreCircle)){
					array = JSON.parseArray(coreCircle);
			        for (int i = 0; i < array.size(); i++) {
			        	uid = array.getLong(i);
			        	count = resultUserDataMap.get(uid.toString());
			        	if(null == count){
			        		count = (Long)m.get("c");
			        	}else{
			        		count = Long.valueOf(count.longValue() + ((Long)m.get("c")).longValue());
			        	}
			        	resultUserDataMap.put(uid.toString(), count);
			        }
				}
			}
		}else{
			logger.info("共有0个王国有订阅增量");
		}
		logger.info("处理王国订阅增量完成");
		
		logger.info("共有"+resultUserDataMap.size()+"个用户有相关增量数据");
		
		//获取所有带“米汤客服”的UID
		StringBuilder filterUidSql = new StringBuilder();
		filterUidSql.append("select u.uid from user_profile u where u.nick_name like '%米汤客服%'");
		List<Map<String, Object>> filterList = contentService.queryEvery(filterUidSql.toString());
		
		List<Long> filterUidList = new ArrayList<Long>();
		if(null != filterList && filterList.size() > 0){
			for(Map<String, Object> m : filterList){
				filterUidList.add((Long)m.get("uid"));
			}
		}
		
		//排序
		List<UserCountItem> resultList = new ArrayList<UserCountItem>();
		UserCountItem item = null;
		for(Map.Entry<String, Long> entry : resultUserDataMap.entrySet()){
			if(filterUidList.contains(Long.valueOf(entry.getKey()))){
				continue;
			}
			item = new UserCountItem();
			item.setUid(Long.valueOf(entry.getKey()));
			item.setCount(entry.getValue());
			resultList.add(item);
		}
		
		if(resultList.size() > 0){
			Collections.sort(resultList);
			
			//存入数据库
			//1 查询当前列表key
			String nextCacheKey = null;
			String currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_POPULAR_PEOPLE);
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
			String listKey = Constant.BILLBOARD_KEY_POPULAR_PEOPLE + nextCacheKey;
			int s = 1;
			for(int i=0;i<resultList.size()&&i<100;i++){
				item = resultList.get(i);
				bbl = new BillBoardList();
				bbl.setListKey(listKey);
				bbl.setTargetId(item.getUid());
				bbl.setType(0);
				bbl.setSinceId(s);
				insertList.add(bbl);
				s++;
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
