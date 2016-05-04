package com.me2me.activity.service;

import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.model.Activity;
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

    Response getActivity(int sinceId);


}
