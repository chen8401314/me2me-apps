package com.me2me.live.listener;

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.LiveLastUpdate;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.TopicNoticeEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.sms.service.JPushService;

@Component
@Slf4j
public class TopicNoticeListener {

	private final ApplicationEventBus applicationEventBus;
    private final CacheService cacheService;
    private final JPushService jPushService;
    private final LiveMybatisDao liveMybatisDao;
    
    @Autowired
    public TopicNoticeListener(ApplicationEventBus applicationEventBus, CacheService cacheService,
    		JPushService jPushService, LiveMybatisDao liveMybatisDao){
    	this.applicationEventBus = applicationEventBus;
    	this.cacheService = cacheService;
    	this.jPushService = jPushService;
    	this.liveMybatisDao = liveMybatisDao;
    }
    
    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
    
    @Subscribe
    public void topicNoitce(TopicNoticeEvent event) {
    	log.info("TopicNoticeListener start... topic["+event.getTopicId()+"], uid["+event.getUid()+"]");
    	
    	Topic topic = liveMybatisDao.getTopicById(event.getTopicId());
    	if(null == topic){
    		log.info("topic is deleted!");
    		return;
    	}
    	
    	//判断是否核心圈
    	boolean isCoreUser = false;
		if(event.getUid() == topic.getUid().longValue() || this.isInCore(event.getUid(), topic.getCoreCircle())){
			isCoreUser = true;
		}
    	
		MySubscribeCacheModel cacheModel= null;
		if(isCoreUser){//核心圈发言，通知其他收录者
	    	//非国王发言，提醒国王红点
	    	if(topic.getUid().longValue() != event.getUid()){
	            cacheModel = new MySubscribeCacheModel(topic.getUid(), String.valueOf(event.getTopicId()), "1");
	            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
	        }
	    	
	    	LiveLastUpdate liveLastUpdate = new LiveLastUpdate(event.getTopicId(),"1");
	        
	    	boolean isInvalid = true;//1小时缓存是否已失效
	    	if(!StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))){
	        	isInvalid = false;
	        }
	    	//已失效的重新设置缓存时间
	        if(isInvalid) {
	            log.info("set cache timeout");
	            cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
	            cacheService.expire(liveLastUpdate.getKey(), 3600);
	        }
			
			List<LiveFavorite> liveFavorites = liveMybatisDao.getFavoriteAll(event.getTopicId());
			if(null != liveFavorites && liveFavorites.size() > 0){
				for(LiveFavorite liveFavorite : liveFavorites){
					if(liveFavorite.getUid().longValue() != event.getUid()) {
						//一级菜单红点
						MyLivesStatusModel livesStatusModel = new MyLivesStatusModel(liveFavorite.getUid(),"1");
			            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
			            //王国列表的单个王国上红点
						cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId().toString(), "1");
						cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
						
						//推送
						if(isInvalid && this.checkTopicPush(event.getTopicId(), liveFavorite.getUid())){
							JsonObject jsonObject = new JsonObject();
			                jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
			                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
			                jsonObject.addProperty("topicId",event.getTopicId());
			                jsonObject.addProperty("contentType", topic.getType());
			                jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, liveFavorite.getUid()));
			                jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//主播发言的，都是核心圈
			                String alias = String.valueOf(liveFavorite.getUid());
			                jPushService.payloadByIdExtra(alias,  "『"+topic.getTitle() + "』有更新", JPushUtils.packageExtra(jsonObject));
						}
					}
				}
			}
		}else{//非核心圈的，通知核心圈
			JSONArray cores =JSON.parseArray(topic.getCoreCircle());
			for(int i=0;i<cores.size();i++){
				long coreUid = cores.getLongValue(i);
				if(coreUid == event.getUid()){
					continue;
				}
				cacheModel = new MySubscribeCacheModel(coreUid, String.valueOf(event.getTopicId()), "1");
	            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
	            
	            if(this.checkTopicPush(event.getTopicId(), coreUid)){
		            JsonObject jsonObject = new JsonObject();
		            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
		            jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
		            jsonObject.addProperty("topicId", event.getTopicId());
		            jsonObject.addProperty("contentType", topic.getType());
		            jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//这里是给核心圈的通知，所以直接显示核心圈即可
		            jsonObject.addProperty("fromInternalStatus", 0);//评论人相对于王国的身份
		            String alias = String.valueOf(coreUid);
		
		            jPushService.payloadByIdExtra(alias,  "有人评论了『"+topic.getTitle()+"』", JPushUtils.packageExtra(jsonObject));
	            }
			}
		}
    	
    	log.info("TopicNoticeListener end.");
    }
    
    private boolean isInCore(long uid, String coreCircle){
		boolean result = false;
		if(null != coreCircle && !"".equals(coreCircle)){
			JSONArray array = JSON.parseArray(coreCircle);
	        for (int i = 0; i < array.size(); i++) {
	            if (array.getLong(i).longValue() == uid) {
	            	result = true;
	                break;
	            }
	        }
		}
		return result;
	}
    
    private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
    	return true;
    }
    
    private int getInternalStatus(Topic topic, long uid) {
        String coreCircle = topic.getCoreCircle();
        JSONArray array = JSON.parseArray(coreCircle);
        int internalStatus = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == uid) {
                internalStatus = Specification.SnsCircle.CORE.index;
                break;
            }
        }
        return internalStatus;
    }
}
