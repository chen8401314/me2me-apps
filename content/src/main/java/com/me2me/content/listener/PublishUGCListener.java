package com.me2me.content.listener;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.event.ReviewEvent;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private final JPushService jPushService;


    @Autowired
    public PublishUGCListener(ApplicationEventBus applicationEventBus, ContentService contentService,UserService userService,JPushService jPushService){
        this.applicationEventBus = applicationEventBus;
        this.contentService = contentService;
        this.userService = userService;
        this.jPushService = jPushService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void sendMessage(ReviewEvent reviewEvent){
        ReviewDto reviewDto = reviewEvent.getReviewDto();
        Content content = reviewEvent.getContent();
        if(reviewDto.getIsAt() == 1) {
            //兼容老版本
            if(reviewDto.getAtUid() != 0) {

                if(reviewEvent.getIsOnline().equals("1")) {
                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.UGCAT.index, reviewDto.getReview(), reviewDto.getAtUid());
                }else{
                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), reviewDto.getAtUid());
                }
                //更换推送为极光推送
                //userService.push(reviewDto.getAtUid(), reviewDto.getUid(), Specification.PushMessageType.AT.index, reviewDto.getReview());
                JpushToken jpushToken = userService.getJpushTokeByUid(reviewDto.getAtUid());
                if(jpushToken == null){
                    //兼容老版本，如果客户端没有更新则还走信鸽push
                    userService.push(reviewDto.getAtUid(), reviewDto.getUid(), Specification.PushMessageType.AT.index, reviewDto.getReview());
                }else {
                  jpush(reviewDto.getUid(),reviewDto.getAtUid());
                }
            }
//            if(reviewDto.getAtUid() != content.getUid()) {
//                if(isOnline.equals("1")) {
//                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.UGCAT.index, reviewDto.getReview(), reviewDto.getAtUid());
//                }else{
//                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), reviewDto.getAtUid());
//                }
//            }
        }else{
            //添加提醒
            log.info("review you start");
            UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType",Specification.PushMessageType.REVIEW.index);
            String alias = String.valueOf(content.getUid());
            if(content.getUid()!=reviewDto.getUid()) {
                jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "评论了你", JPushUtils.packageExtra(jsonObject));
                contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview());
            }
            log.info("review you end");
        }
    }

    private void jpush(long uid,long atUid) {
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageType",Specification.PushMessageType.AT.index);
        String alias = String.valueOf(atUid);
        jPushService.payloadByIdExtra(alias,userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
    }


}
