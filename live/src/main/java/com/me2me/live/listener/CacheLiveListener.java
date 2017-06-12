package com.me2me.live.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.LiveLastUpdate;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.event.CacheLiveEvent;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/15
 * Time :17:01
 */
@Component
@Slf4j
public class CacheLiveListener {

    private final ApplicationEventBus applicationEventBus;

    private final UserService userService;

    private final CacheService cacheService;

    private final LiveService liveService;


    @Autowired
    public CacheLiveListener(ApplicationEventBus applicationEventBus,
                             UserService userService,
                             CacheService cacheService,
                             LiveService liveService){
        this.applicationEventBus = applicationEventBus;
        this.userService = userService;
        this.cacheService = cacheService;
        this.liveService = liveService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    /**
     * sync process after work
     * @param cacheLiveEvent
     */
    @Subscribe
    public void cacheLive(CacheLiveEvent cacheLiveEvent) {
        log.info("invocation by event bus ... ");
        
        //增加缓存，当创建王国后一个小时之内的发言不再推送
        String key = "topic:update:push:status:" + cacheLiveEvent.getTopicId();
        cacheService.set(key, "1");
		cacheService.expire(key, 3600);//一小时超时时间
        
        //判断这个王国是不是聚合王国，如果是聚合王国，则通知粉丝，不是聚合王国则不需要通知
        Topic topic = liveService.getTopicById(cacheLiveEvent.getTopicId());
        if(null == topic || topic.getType().intValue() != Specification.KingdomType.AGGREGATION.index){
        	return;//非聚合王国，不需要推送
        }
        
        List<UserFollow> list = userService.getFans(cacheLiveEvent.getUid());
        log.info("get user fans ... ");
        
        UserProfile userProfile = userService.getUserProfileByUid(cacheLiveEvent.getUid());
        String message = userProfile.getNickName()+"新建了聚合王国《"+topic.getTitle()+"》";
        for (UserFollow userFollow : list) {
            //所有订阅的人显示有红点
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(userFollow.getSourceUid(), String.valueOf(cacheLiveEvent.getTopicId()), "1");
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", Specification.LiveSpeakType.FOLLOW.index);
            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            jsonObject.addProperty("topicId",cacheLiveEvent.getTopicId());
            jsonObject.addProperty("contentType", topic.getType());
            jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, userFollow.getSourceUid()));
            jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//主播创建的，肯定是核心圈
            userService.pushWithExtra(userFollow.getSourceUid().toString(),  message, JPushUtils.packageExtra(jsonObject));
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
}
