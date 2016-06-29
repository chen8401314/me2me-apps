package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Response createReview(ReviewDto reviewDto) {
        log.info("ContentReview createReview start ...");
        contentService.createReview2(reviewDto);
        Content content = contentService.getContentById(reviewDto.getCid());
        //更新评论数量
        content.setReviewCount(content.getReviewCount() +1);
        contentService.updateContentById(content);
        log.info("update reviewCount");
        //添加提醒
        contentService.remind(content,reviewDto.getUid(), Specification.UserNoticeType.REVIEW.index,reviewDto.getReview());
        log.info("remind success");
        //自己的日记被评论提醒
        //userService.push(content.getUid(),reviewDto.getUid(),Specification.PushMessageType.REVIEW.index,content.getTitle());
        log.info("push success");
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }
}
