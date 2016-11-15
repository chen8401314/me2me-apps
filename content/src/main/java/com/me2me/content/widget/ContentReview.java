package com.me2me.content.widget;

import com.me2me.cache.service.CacheService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ReviewDelDTO;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.event.ReviewEvent;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;

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
    private ApplicationEventBus applicationEventBus;
    @Autowired
    private CacheService cacheService;

    public Response createReview(ReviewDto reviewDto) {
        log.info("ContentReview createReview start ...");
        String isOnline = cacheService.get("version:2.1.0:online");
        contentService.createReview2(reviewDto);
        Content content = contentService.getContentById(reviewDto.getCid());
        //更新评论数量
        log.info("update reviewCount");
        content.setReviewCount(content.getReviewCount() +1);
        contentService.updateContentById(content);
        log.info("remind success");
        //自己的日记被评论提醒
        ReviewEvent event = new ReviewEvent();
        event.setContent(content);
        event.setReviewDto(reviewDto);
        event.setIsOnline(isOnline);
        applicationEventBus.post(event);
        log.info("push success");
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }

	@Override
	public Response delReview(ReviewDelDTO delDTO) {
		return contentService.delContentReview(delDTO);
	}
}
