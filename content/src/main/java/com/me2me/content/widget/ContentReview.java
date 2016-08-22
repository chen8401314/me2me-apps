package com.me2me.content.widget;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :17:11
 */
@Component
@Slf4j
public class ContentReview implements Review{

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private JPushService jPushService;

    public Response createReview(ReviewDto reviewDto) {
        log.info("ContentReview createReview start ...");
        contentService.createReview2(reviewDto);
        Content content = contentService.getContentById(reviewDto.getCid());
        //更新评论数量
        content.setReviewCount(content.getReviewCount() +1);
        contentService.updateContentById(content);
        log.info("update reviewCount");
        log.info("remind success");
        //自己的日记被评论提醒
        if(reviewDto.getIsAt() == 1) {
            //兼容老版本
            if(reviewDto.getAtUid() != 0) {
                contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview(), reviewDto.getAtUid());
                //更换推送为极光推送
                //userService.push(reviewDto.getAtUid(), reviewDto.getUid(), Specification.PushMessageType.AT.index, reviewDto.getReview());
                JpushToken jpushToken = userService.getJpushTokeByUid(reviewDto.getAtUid());
                if(jpushToken == null){
                    //兼容老版本，如果客户端没有更新则还走信鸽push
                    userService.push(reviewDto.getAtUid(), reviewDto.getUid(), Specification.PushMessageType.AT.index, reviewDto.getReview());
                }else {
                    UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("messageType",Specification.PushMessageType.AT.index+"");
                    String alias = String.valueOf(reviewDto.getAtUid());
                    jPushService.payloadByIdExtra(alias,userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
                }
            }
            if(reviewDto.getAtUid() != content.getUid()) {
                contentService.remind(content, reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index, reviewDto.getReview());
            }
        }else{
            //添加提醒
            UserProfile userProfile = userService.getUserProfileByUid(reviewDto.getUid());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType",Specification.PushMessageType.REVIEW.index+"");
            String alias = String.valueOf(reviewDto.getAtUid());
            jPushService.payloadByIdExtra(alias,userProfile.getNickName() + "评论了你", JPushUtils.packageExtra(jsonObject));
            contentService.remind(content,reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index,reviewDto.getReview());
        }
        log.info("push success");
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }
}
