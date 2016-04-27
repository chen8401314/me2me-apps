package com.me2me.activity.service;

import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.model.Activity;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.common.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMybatisDao activityMybatisDao;

    @Override
    public Response createActivity(CreateActivityDto createActivityDto) {
        ActivityWithBLOBs activity = new ActivityWithBLOBs();
        activity.setActivityCover(createActivityDto.getCover());
        activity.setActivityHashTitle(createActivityDto.getHashTitle());
        activity.setActivityTitle(createActivityDto.getTitle());
        activity.setStartTime(createActivityDto.getStartTime());
        activity.setEndTime(createActivityDto.getEndTime());
        activity.setIssue(createActivityDto.getIssue());
        activity.setActivityContent(createActivityDto.getContent());
        activityMybatisDao.saveActivity(activity);
        return Response.success(200,"活动创建成功！");
    }
}
