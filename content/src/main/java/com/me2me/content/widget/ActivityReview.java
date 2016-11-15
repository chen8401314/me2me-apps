package com.me2me.content.widget;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ReviewDelDTO;
import com.me2me.content.dto.ReviewDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :23:06
 */
@Component
@Slf4j
public class ActivityReview implements Review{

    @Autowired
    private ActivityService activityService;

    @Override
    public Response createReview(ReviewDto reviewDto) {
        log.info("ActivityService createReview");
        activityService.createActivityReview(reviewDto.getId(),reviewDto.getUid(),reviewDto.getReview(),reviewDto.getAtUid());
        log.info("create review success");
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }

	@Override
	public Response delReview(ReviewDelDTO delDTO) {
		return Response.failure("nonsupport!!!");
	}
}
