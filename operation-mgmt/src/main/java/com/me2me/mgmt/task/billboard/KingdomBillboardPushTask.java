package com.me2me.mgmt.task.billboard;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.model.BillBoard;
import com.me2me.content.model.BillBoardRelation;
import com.me2me.content.service.ContentService;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
import com.me2me.user.service.UserService;

@Component
public class KingdomBillboardPushTask {
	
	private static final Logger logger = LoggerFactory.getLogger(KingdomBillboardPushTask.class);

	@Autowired
	private ContentService contentService;
	@Autowired
	private LiveService liveService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private UserService userService;
	
	@Scheduled(cron="0 0 8-18 * * ?")
	public void push(){
		logger.info("王国榜单推送任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			List<BillBoard> bList = contentService.getBillBoardList4kingdomPushTask();
			if(null != bList && bList.size() > 0){
				List<BillBoardRelation> rList = null;
				List<Long> tidList = null;
				List<Topic> topicList = null;
				for(BillBoard b : bList){
					rList = contentService.getBillBoardRelationByBid(b.getId());
					if(null != rList && rList.size() > 0){
						tidList = new ArrayList<Long>();
						for(BillBoardRelation r : rList){
							tidList.add(r.getTargetId());
						}
						if(tidList.size() > 0){
							topicList = liveService.getTopicListByIds(tidList);
							if(null != topicList && topicList.size() > 0){
								for(Topic topic : topicList){
									//判断是否需要推送。一个用户在7天内只能推送一次
									boolean needPush = false;
									String key = "user:billboard:push:" + topic.getUid().toString();
									if(StringUtils.isEmpty(cacheService.get(key))){
										needPush = true;
										
										cacheService.set(key, "1");
										cacheService.expire(key, 7*24*60*60);//一周超时时间
									}
									
									if(needPush){
										JsonObject jsonObject = new JsonObject();
										jsonObject.addProperty("type", Specification.PushObjectType.BILLBOARD.index);
										jsonObject.addProperty("messageType",1);//没有任何定义
										jsonObject.addProperty("listId", b.getId());
										jsonObject.addProperty("subType", 1);//王国榜单
										userService.pushWithExtra(topic.getUid().toString(), "『"+topic.getTitle()+"』上“"+b.getName()+"”了", JPushUtils.packageExtra(jsonObject));
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("王国榜单推送任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("王国榜单推送任务结束，共耗时"+(e-s)/1000+"秒");
	}
}
