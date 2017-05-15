package com.me2me.content.listener;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.cache.UgcPushStatus;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.event.PublishUGCEvent;
import com.me2me.content.event.ReviewEvent;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
@Log
public class PublishUGCListener {

    private final ApplicationEventBus applicationEventBus;

    private final ContentService contentService;

    private final UserService userService;
    
    private final CacheService cacheService;


    @Autowired
    public PublishUGCListener(ApplicationEventBus applicationEventBus, ContentService contentService,UserService userService,CacheService cacheService){
        this.applicationEventBus = applicationEventBus;
        this.contentService = contentService;
        this.userService = userService;
        this.cacheService = cacheService;
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

    @Subscribe
	public void sendMessage(ReviewEvent reviewEvent) {
		ReviewDto reviewDto = reviewEvent.getReviewDto();
		Content content = reviewEvent.getContent();
		if (reviewDto.getIsAt() == 1) {
			// 兼容老版本
			if (reviewDto.getAtUid() > 0) {
				long atUid = reviewDto.getAtUid();
				if ("1".equals(reviewEvent.getIsOnline())) {
					contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.UGCAT.index, reviewDto.getReview(), atUid);
				} else {
					contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), atUid);
				}

				UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("messageType", Specification.PushMessageType.AT.index);
				jsonObject.addProperty("type", Specification.PushObjectType.UGC.index);
				jsonObject.addProperty("cid", content.getId());
				String alias = String.valueOf(atUid);
				userService.pushWithExtra(alias, userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
			}
		} else {
			// 添加提醒
			if (content.getUid() != reviewDto.getUid()) {
				log.info("review you start ... from " + reviewDto.getUid() + " to " + content.getUid());
				contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview());
				
				//有人评论需要推送通知作者
				//这里也有个一小时的机制，如果1小时内有评论则不推送，如果1小时外再推送，则继续推送
				UgcPushStatus ups = new UgcPushStatus(content.getId(), "1");
				boolean canPush = true;
				if(!StringUtils.isEmpty(cacheService.get(ups.getKey()))){
					canPush = false;
				}
				
				if(canPush){
					cacheService.set(ups.getKey(), ups.getValue());
					cacheService.expire(ups.getKey(), 3600);//一小时超时时间
					
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("messageType", Specification.PushMessageType.REVIEW.index);
					jsonObject.addProperty("type", Specification.PushObjectType.UGC.index);
					jsonObject.addProperty("cid", content.getId());
					String alias = String.valueOf(content.getUid());
					userService.pushWithExtra(alias, "你有新的评论", JPushUtils.packageExtra(jsonObject));
				}
				
				log.info("review you end");
			}
		}
	}


}
