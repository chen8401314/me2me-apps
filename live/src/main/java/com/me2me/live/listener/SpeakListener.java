package com.me2me.live.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.LiveLastUpdate;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.SpeakEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.sms.service.JPushService;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/25
 * Time :10:35
 */
@Component
@Slf4j
public class SpeakListener {

    private final ApplicationEventBus applicationEventBus;

    private final CacheService cacheService;

    private final JPushService jPushService;

    private final UserService userService;

    private final LiveMybatisDao liveMybatisDao;

    @Autowired
    public SpeakListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, CacheService cacheService, JPushService jPushService, UserService userService){
        this.applicationEventBus = applicationEventBus;
        this.cacheService = cacheService;
        this.jPushService = jPushService;
        this.liveMybatisDao = liveMybatisDao;
        this.userService = userService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void speak(SpeakEvent speakEvent) {
        log.info("SpeakEvent speak start ...");
        if(speakEvent.getType()==Specification.LiveSpeakType.ANCHOR.index){
            log.info("author speak ...");
            authorSpeak(speakEvent);
        }else if(speakEvent.getType()==Specification.LiveSpeakType.FANS.index){
            log.info("fans speak ...");
            fansSpeak(speakEvent);
        }else if(speakEvent.getType()==Specification.LiveSpeakType.ANCHOR_AT.index){ //主播@作为主播发言
            log.info("auchor at ...");
            authorSpeak(speakEvent);
        }else if(speakEvent.getType()==Specification.LiveSpeakType.AT_CORE_CIRCLE.index){//核心圈@作为主播发言
            log.info("at core circle ...");
            authorSpeak(speakEvent);
        }else if(speakEvent.getType()==Specification.LiveSpeakType.AT.index){//粉丝@作为粉丝发言
            log.info("at ...");
            fansSpeak(speakEvent);
        }else if(speakEvent.getType()==Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index){//主播贴标作为主播发言
            log.info("author write tag ...");
            authorSpeak(speakEvent);
        }
        log.info("SpeakEvent speak end ...");
    }




    private void authorSpeak(SpeakEvent speakEvent) {
        List<LiveFavorite> liveFavorites = liveMybatisDao.getFavoriteAll(speakEvent.getTopicId());
        Topic topic = liveMybatisDao.getTopicById(speakEvent.getTopicId());
        MySubscribeCacheModel cacheModel= null;
        //非国王发言着提醒国王王国更新红点
        if(topic.getUid()!=speakEvent.getUid()){
            cacheModel = new MySubscribeCacheModel(topic.getUid(), speakEvent.getTopicId() + "", speakEvent.getFragmentId()+"");
            String values = cacheService.hGet(cacheModel.getKey(), cacheModel.getField());
            if(StringUtils.isEmpty(values) || Integer.parseInt(values) == 0){
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }
        }
        LiveLastUpdate liveLastUpdate = new LiveLastUpdate(speakEvent.getTopicId(),"1");
        
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
        
        for(LiveFavorite liveFavorite : liveFavorites){
            MyLivesStatusModel livesStatusModel = new MyLivesStatusModel(liveFavorite.getUid(),"1");
            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
            if(liveFavorite.getUid()!=speakEvent.getUid()) {
                cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId() + "", speakEvent.getFragmentId()+"");
                log.info("speak by master start update hset cache key{} field {} value {}", cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }
            //如果缓存存在时间失效，推送
            if(isInvalid && liveFavorite.getUid()!=speakEvent.getUid()
            		&& speakEvent.getAtUids().indexOf(CommonUtils.wrapString(liveFavorite.getUid(),","))==-1
            		&& this.checkTopicPush(speakEvent.getTopicId(), liveFavorite.getUid())) {
                log.info("update live start");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId",speakEvent.getTopicId());
                jsonObject.addProperty("contentType", topic.getType());
                jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, liveFavorite.getUid()));
                jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//主播发言的，都是核心圈
                String alias = String.valueOf(liveFavorite.getUid());

                userService.pushWithExtra(alias,  "『"+topic.getTitle() + "』有更新", JPushUtils.packageExtra(jsonObject));
                log.info("update live end");
            }
        }
    }

    private void fansSpeak(SpeakEvent speakEvent) {
        Topic topic = liveMybatisDao.getTopicById(speakEvent.getTopicId());
        int fromStatus = this.getInternalStatus(topic, speakEvent.getUid());
        JSONArray cores =JSON.parseArray(topic.getCoreCircle());
        for(int i=0;i<cores.size();i++){
            long cid = cores.getLongValue(i);

            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(cid, speakEvent.getTopicId() + "", speakEvent.getFragmentId()+"");
            log.info("speak by fans start update hset cache key{} field {} value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
            String values = cacheService.hGet(cacheModel.getKey(), cacheModel.getField());
            if(StringUtils.isEmpty(values) || Integer.parseInt(values) == 0){
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }

            if(speakEvent.getAtUids().indexOf(CommonUtils.wrapString(cid,","))>-1){
                continue;
            }
            
            if(this.checkTopicPush(speakEvent.getTopicId(), cid)){
	            JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
	            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
	            jsonObject.addProperty("topicId",speakEvent.getTopicId());
	            jsonObject.addProperty("contentType", topic.getType());
	            jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//这里是给核心圈的通知，所以直接显示核心圈即可
	            jsonObject.addProperty("fromInternalStatus", fromStatus);//评论人相对于王国的身份
	            String alias = String.valueOf(cid);
	
	            userService.pushWithExtra(alias,  "有人评论了『"+topic.getTitle()+"』", JPushUtils.packageExtra(jsonObject));
            }
        }
    }

    //判断核心圈身份
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
//        if (internalStatus == 0) {
//            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
//        }

        return internalStatus;
    }

    private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
    	return true;
    }
}
