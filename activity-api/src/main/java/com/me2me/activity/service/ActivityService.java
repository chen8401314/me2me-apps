package com.me2me.activity.service;

import com.me2me.activity.dto.ActivityDto;
import com.me2me.activity.dto.ActivityH5Dto;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.CreateActivityNoticeDto;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.common.web.Response;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
public interface ActivityService {


    /**
     * 创建活动
     * @return
     */
    Response createActivity(CreateActivityDto createActivityDto);


    Response showActivity(int page,int pageSize,String keyword);

    List<ActivityWithBLOBs> getActivityTop5();

    Response getActivity(int sinceId,long uid);

    ActivityWithBLOBs loadActivityById(long id);

    void modifyActivity(ActivityWithBLOBs activity);

    void createActivityNotice(CreateActivityNoticeDto createActivityNoticeDto);

    void joinActivity(String content,long uid);

    ActivityH5Dto getActivityH5(long id);

    ActivityDto getActivity(long id);

    void createActivityReview(long id,long uid,String review,long atUid);

    void createActivityTagsDetails(long id,long uid,long tid);

    void createActivityLikesDetails(long id,long uid);

    int getLikeCount(long id);

    int getReviewCount(long id);

    Response luckAward(long uid ,String ip ,int activityName ,String channel ,String version);

    Response getAwardCount(long uid);

    Response awardShare(long uid ,int activityName);

    Response checkIsAward(long uid ,int activityName ,String channel ,String version ,String token);

    Response getUserAwardInfo(long uid);

    Response getAwardStatus(int activityName);
}
