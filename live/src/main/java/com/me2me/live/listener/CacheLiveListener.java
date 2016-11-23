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
import com.me2me.sms.service.JPushService;
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

    private final JPushService jPushService;


    @Autowired
    public CacheLiveListener(ApplicationEventBus applicationEventBus,
                             UserService userService,
                             CacheService cacheService,
                             LiveService liveService,
                             JPushService jPushService){
        this.applicationEventBus = applicationEventBus;
        this.userService = userService;
        this.cacheService = cacheService;
        this.liveService = liveService;
        this.jPushService = jPushService;
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
        List<UserFollow> list = userService.getFans(cacheLiveEvent.getUid());
        log.info("get user fans ... ");
        for(UserFollow userFollow : list) {
            //主播的粉丝强制订阅
            liveService.setLive3(userFollow.getSourceUid(),cacheLiveEvent.getTopicId());
        }
        for (UserFollow userFollow : list) {
            //所有订阅的人显示有红点
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(userFollow.getSourceUid(), cacheLiveEvent.getTopicId() + "", "1");
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            UserProfile userProfile = userService.getUserProfileByUid(cacheLiveEvent.getUid());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", Specification.LiveSpeakType.FOLLOW.index);
            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            jsonObject.addProperty("topicId",cacheLiveEvent.getTopicId());
            String alias = String.valueOf(userFollow.getSourceUid());
            Topic topic = liveService.getTopicById(cacheLiveEvent.getTopicId());
            jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, userFollow.getSourceUid()));
            jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//主播创建的，肯定是核心圈
            jPushService.payloadByIdExtra(alias,  userProfile.getNickName() + "新建了『" + topic.getTitle()+"』", JPushUtils.packageExtra(jsonObject));
        }
        //增加缓存，当创建王国后一个小时之内的发言不再推送
        LiveLastUpdate liveLastUpdate = new LiveLastUpdate(cacheLiveEvent.getTopicId(),"1");
        log.info("set cache timeout");
        cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
        cacheService.expire(liveLastUpdate.getKey(), 3600);

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
        if (internalStatus == 0) {
            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
        }

        return internalStatus;
    }
}
