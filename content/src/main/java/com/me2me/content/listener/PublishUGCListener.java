package com.me2me.content.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.event.PublishUGCEvent;
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
    public void like(PublishUGCEvent publishUGCEvent){
        LikeDto likeDto = new LikeDto();
        likeDto.setCid(publishUGCEvent.getCid());
        likeDto.setAction(0);
        likeDto.setType(Specification.LikesType.CONTENT.index);
        contentService.robotLikes(likeDto);
    }

    @Subscribe
    public void sendMessage(ReviewEvent reviewEvent){
        ReviewDto reviewDto = reviewEvent.getReviewDto();
        Content content = reviewEvent.getContent();
        if(reviewDto.getIsAt() == 1) {
//            JSONArray array = null;
            //兼容老版本
            if(reviewDto.getAtUid() > 0) {
            	long atUid = reviewDto.getAtUid();
                if ("1".equals(reviewEvent.getIsOnline())) {
                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.UGCAT.index, reviewDto.getReview(), atUid);
                } else {
                    contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), atUid);
                }

                UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.AT.index);
                jsonObject.addProperty("type",Specification.PushObjectType.UGC.index);
                jsonObject.addProperty("cid",content.getId());
                String alias = String.valueOf(atUid);
                jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
            	
            	
            	
//                String extra = reviewDto.getExtra();
//                if(StringUtils.isEmpty(extra)){
//                    array = new JSONArray();
//                    array.add(reviewDto.getAtUid());
//                }else {
//                    JSONObject json = JSON.parseObject(extra);
//                    array = json.containsKey("atArray") ? json.getJSONArray("atArray") : null;
//                    if (array == null) {
//                        return;
//                    }
//                }
//                for(int i=0;i<array.size();i++) {
//                    long atUid = array.getLongValue(i);
//                    if ("1".equals(reviewEvent.getIsOnline())) {
//                        contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.UGCAT.index, reviewDto.getReview(), atUid);
//                    } else {
//                        contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), atUid);
//                    }
//
//                    UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("messageType", Specification.PushMessageType.AT.index);
//                    jsonObject.addProperty("type",Specification.PushObjectType.UGC.index);
//                    jsonObject.addProperty("cid",content.getId());
//                    String alias = String.valueOf(atUid);
//                    jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
//                }
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
            if(content.getUid()!=reviewDto.getUid()) {
                log.info("review you start ... from "+reviewDto.getUid()+" to "+content.getUid());
               UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
               JsonObject jsonObject = new JsonObject();
               jsonObject.addProperty("messageType",Specification.PushMessageType.REVIEW.index);
               jsonObject.addProperty("type",Specification.PushObjectType.UGC.index);
                jsonObject.addProperty("cid",content.getId());
               String alias = String.valueOf(content.getUid());
                jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "评论了你", JPushUtils.packageExtra(jsonObject));
               contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview());

               log.info("review you end");
            }
        }
    }


}
