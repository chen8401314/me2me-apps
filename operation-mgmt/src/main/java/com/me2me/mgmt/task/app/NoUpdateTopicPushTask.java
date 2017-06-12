package com.me2me.mgmt.task.app;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.service.ContentService;
import com.me2me.user.service.UserService;

@Component
public class NoUpdateTopicPushTask {

	private static final Logger logger = LoggerFactory.getLogger(NoUpdateTopicPushTask.class);
	
	private static final int NO_UPDATE_DAY = 5;
	
	@Autowired
	private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private UserService userService;
	
//	@Scheduled(cron="0 30 10 * * ?")
	public void noUpdateTopicPush(){
		logger.info("无更新推送提醒任务开始");
		long s = System.currentTimeMillis();
		try{
			Date now = new Date();
			String searchTime = DateUtil.date2string(DateUtil.addDay(now, (0-NO_UPDATE_DAY)), "yyyy-MM-dd HH:mm:ss");
			String limitTime = DateUtil.date2string(DateUtil.addDay(now, (-5-NO_UPDATE_DAY)), "yyyy-MM-dd HH:mm:ss");
			StringBuilder sb = new StringBuilder();
			sb.append("select t.* from topic t, (");
			sb.append("select f2.topic_id from topic_fragment f2,(");
			sb.append("select f.topic_id, max(f.id) as mid from topic_fragment f");
			sb.append(" where f.type in (0,3,12,13,15,52,55) group by f.topic_id");
			sb.append(") m where f2.id=m.mid and f2.create_time<'").append(searchTime);
			sb.append("' and f2.create_time>'").append(limitTime);
			sb.append("') n where t.id=n.topic_id");
			
			StringBuilder sb2 = new StringBuilder();
			sb2.append("select count(1) as cc from topic_fragment f");
			sb2.append(" where f.topic_id=#topicId# and f.create_time>'").append(limitTime);
			sb2.append("' and f.type=51 and f.content_type=16");
			
			List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
			if(null != list && list.size() > 0){
				String key = "topic:noupdate:map";
				String key2 = "topic:noupdateAndHasFootmark:map";
				String message = "你的王国『#topicName#』已经很久没有更新了哦";
				JsonObject jsonObject = null;
				String zujiSql = null;
				for(Map<String, Object> t : list){
					//判断该王国是否通知过
					Long topicId = (Long)t.get("id");
					Long uid = (Long)t.get("uid");
					if(StringUtils.isEmpty(cacheService.hGet(key, topicId.toString()))){//需要通知
						cacheService.hSet(key, topicId.toString(), "1");
						
						jsonObject = new JsonObject();
		                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
		                jsonObject.addProperty("topicId",topicId);
		                jsonObject.addProperty("contentType", (Integer)t.get("type"));
		                jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//核心圈
		                userService.pushWithExtra(uid.toString(), "你的王国『"+(String)t.get("title")+"』已经很久没有更新了哦", JPushUtils.packageExtra(jsonObject));
					}
					
					//判断这期间是否有足迹，如果超过2个足迹则有额外推送
					if(StringUtils.isEmpty(cacheService.hGet(key2, topicId.toString()))){//推过了就不再推送了
						cacheService.hSet(key2, topicId.toString(), "1");
						
						zujiSql = sb2.toString().replace("#topicId#", topicId.toString());
						List<Map<String, Object>> countList = contentService.queryEvery(zujiSql);
						if(null != countList && countList.size() > 0){
							Map<String, Object> count = countList.get(0);
							Long cc = (Long)count.get("cc");
							if(null != cc && cc.intValue() >= 2){
								jsonObject = new JsonObject();
				                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
				                jsonObject.addProperty("topicId",topicId);
				                jsonObject.addProperty("contentType", (Integer)t.get("type"));
				                jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//核心圈
				                userService.pushWithExtra(uid.toString(), "好多人在『"+(String)t.get("title")+"』", JPushUtils.packageExtra(jsonObject));
							}
						}
					}
				}
			}
		}catch(Exception ex){
			logger.error("无更新推送提醒任务出错", ex);
		}
		long e = System.currentTimeMillis();
		logger.info("无更新推送提醒任务完成,共耗时["+(e-s)+"]毫秒");
	}
}
