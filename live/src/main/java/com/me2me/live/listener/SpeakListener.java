package com.me2me.live.listener;

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
import com.me2me.live.event.CacheLiveEvent;
import com.me2me.live.event.SpeakEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
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

    private final LiveMybatisDao liveMybatisDao;

    @Autowired
    public SpeakListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, CacheService cacheService, JPushService jPushService){
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
    public void speak(SpeakEvent speakEvent) {
        log.info("SpeakEvent speak start ...");
        List<LiveFavorite> liveFavorites = liveMybatisDao.getFavoriteAll(speakEvent.getTopicId());
        LiveLastUpdate liveLastUpdate = new LiveLastUpdate(speakEvent.getTopicId(),"1");
        //有更新时通知国王
        Topic topic = liveMybatisDao.getTopicById(speakEvent.getTopicId());
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(topic.getUid(), speakEvent.getTopicId() + "", "1");
        log.info("speak by master start update hset cache key{} field {} value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
        // 通知所有的订阅者
        for(LiveFavorite liveFavorite : liveFavorites) {
            MyLivesStatusModel livesStatusModel = new MyLivesStatusModel(liveFavorite.getUid(),"1");
            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
            cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId() + "", "1");
            log.info("speak by master start update hset cache key{} field {} value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            //如果缓存存在时间失效，推送
            if(StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))&&speakEvent.getType()!=Specification.LiveSpeakType.FANS.index&&liveFavorite.getUid()!=speakEvent.getUid()) {
                log.info("update live start");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
                String alias = String.valueOf(liveFavorite.getUid());

                jPushService.payloadByIdExtra(alias, "你加入的王国:" + topic.getTitle() + "更新了", JPushUtils.packageExtra(jsonObject));
                log.info("update live end");
            }
        }
        //设置缓存时间
        if(StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))) {
            log.info("set cache timeout");
            cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
            cacheService.expire(liveLastUpdate.getKey(), 3600);
        }
        log.info("SpeakEvent speak end ...");
    }

}
