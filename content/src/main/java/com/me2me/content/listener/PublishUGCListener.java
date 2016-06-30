package com.me2me.content.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.event.PublishUGCEvent;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
public class PublishUGCListener {

    private final ApplicationEventBus applicationEventBus;

    private final ContentService contentService;


    @Autowired
    public PublishUGCListener(ApplicationEventBus applicationEventBus, ContentService contentService){
        this.applicationEventBus = applicationEventBus;
        this.contentService = contentService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void like(PublishUGCEvent publishUGCEvent){
        LikeDto likeDto = new LikeDto();
        likeDto.setCid(publishUGCEvent.getCid());
        likeDto.setAction(0);
        likeDto.setType(Specification.LikesType.CONTENT.index);
        contentService.robotLikes(likeDto);

    }


}
