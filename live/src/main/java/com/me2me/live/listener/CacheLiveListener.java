package com.me2me.live.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.cache.service.CacheService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.event.CacheLiveEvent;
import com.me2me.live.service.LiveService;
import com.me2me.user.model.UserFollow;
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
    public CacheLiveListener(ApplicationEventBus applicationEventBus, UserService userService,CacheService cacheService,LiveService liveService){
        this.applicationEventBus = applicationEventBus;
        this.userService = userService;
        this.cacheService = cacheService;
        this.liveService = liveService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void cacheLive(CacheLiveEvent cacheLiveEvent) {
        log.info("invocation by event bus ... ");
        List<UserFollow> list = userService.getFans(cacheLiveEvent.getUid());
        log.info("get user fans ... ");
        for(UserFollow userFollow : list) {
            //主播的粉丝强制订阅
            liveService.setLive3(userFollow.getSourceUid(),cacheLiveEvent.getTopicId());
            //所有订阅的人显示有红点
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(userFollow.getSourceUid(), cacheLiveEvent.getTopicId() + "", "1");
            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
        }
    }

}
