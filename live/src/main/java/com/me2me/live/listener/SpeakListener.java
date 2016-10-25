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
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.SpeakEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserProfile;
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

    private final UserService userSerivce;

    private final LiveMybatisDao liveMybatisDao;

    @Autowired
    public SpeakListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, CacheService cacheService, JPushService jPushService, UserService userSerivce){
        this.applicationEventBus = applicationEventBus;
        this.cacheService = cacheService;
        this.jPushService = jPushService;
        this.liveMybatisDao = liveMybatisDao;
        this.userSerivce = userSerivce;
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
            cacheModel = new MySubscribeCacheModel(topic.getUid(), speakEvent.getTopicId() + "", "1");
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
        }
        LiveLastUpdate liveLastUpdate = new LiveLastUpdate(speakEvent.getTopicId(),"1");
        for(LiveFavorite liveFavorite : liveFavorites){
            MyLivesStatusModel livesStatusModel = new MyLivesStatusModel(liveFavorite.getUid(),"1");
            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
            if(liveFavorite.getUid()!=speakEvent.getUid()) {
                cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId() + "", "1");
                log.info("speak by master start update hset cache key{} field {} value {}", cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }
            //如果缓存存在时间失效，推送
            if(StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))&&liveFavorite.getUid()!=speakEvent.getUid()) {
                log.info("update live start");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId",speakEvent.getTopicId());
                String alias = String.valueOf(liveFavorite.getUid());

                jPushService.payloadByIdExtra(alias,  "『"+topic.getTitle() + "』有更新", JPushUtils.packageExtra(jsonObject));
                log.info("update live end");
            }
        }

        //设置缓存时间
        if(StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))) {
            log.info("set cache timeout");
            cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
            cacheService.expire(liveLastUpdate.getKey(), 3600);
        }
    }

    private void fansSpeak(SpeakEvent speakEvent) {
        Topic topic = liveMybatisDao.getTopicById(speakEvent.getTopicId());
        JSONArray cores =JSON.parseArray(topic.getCoreCircle());
        for(int i=0;i<cores.size();i++){
            long cid = cores.getLongValue(i);
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(cid, speakEvent.getTopicId() + "", "1");
            log.info("speak by fans start update hset cache key{} field {} value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            jsonObject.addProperty("topicId",speakEvent.getTopicId());
            String alias = String.valueOf(cid);

            jPushService.payloadByIdExtra(alias,  "有人评论了『"+topic.getTitle()+"』", JPushUtils.packageExtra(jsonObject));
        }
    }



}
