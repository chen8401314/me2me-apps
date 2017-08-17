package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.cache.service.CacheService;
import com.me2me.mgmt.dao.LocalJdbcDao;

@Component
public class CacheDataTask {

	private static final Logger logger = LoggerFactory.getLogger(CacheDataTask.class);
	
	@Autowired
	private CacheService cacheService;
	@Autowired
	private LocalJdbcDao localJdbcDao;
	
	@Scheduled(cron="0 17 * * * ?")
	public void cacheSysTagCountInfo(){
		logger.info("系统标签总价值缓存任务开始");
		long s = System.currentTimeMillis();
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT t.*,(select  sum(price) from topic where id in (");
			sql.append("select DISTINCT dt.topic_id from topic_tag_detail dt where dt.status=0");
			sql.append(" and dt.tag_id in(select id from topic_tag p where tag=t.tag");
			sql.append(" UNION ALL select id from topic_tag c where c.pid=(select id from topic_tag p where tag=t.tag) ");
			sql.append("))) price FROM topic_tag t where t.is_sys=1");
			List<Map<String, Object>> result = localJdbcDao.queryEvery(sql.toString());
			if(null == result){
				result = new ArrayList<Map<String, Object>>();
			}
			cacheService.cacheJavaObject("OBJ:SYSTAGCOUNTINFO", result);
		}catch(Exception e){
			logger.error("系统标签总价值缓存任务失败", e);
		}
		long e = System.currentTimeMillis();
		logger.info("系统标签总价值缓存任务结束，共耗时["+(e-s)/1000+"]秒");
	}
}
