package com.me2me.activity.service;

import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.ShowActivityDto;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.common.web.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        activity.setUid(createActivityDto.getUid());
        activityMybatisDao.saveActivity(activity);
        return Response.success(200,"活动创建成功！");
    }

    @Override
    public Response showActivity(int page, int pageSize,String keyword) {
        ShowActivityDto showActivityDto = new ShowActivityDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.showActivity(page,pageSize,keyword);
        for(ActivityWithBLOBs activity : list){
            ShowActivityDto.ActivityElement element = showActivityDto.createElement();
            element.setUid(activity.getUid());
            element.setStartTime(activity.getStartTime());
            element.setEndTime(activity.getEndTime());
            element.setIssue(activity.getIssue());
            element.setHashTitle(activity.getActivityHashTitle());
            element.setContent(activity.getActivityContent());
            element.setTitle(activity.getActivityTitle());
            showActivityDto.getResult().add(element);
        }
        showActivityDto.setTotal(activityMybatisDao.total(keyword));
        int totalPage = (activityMybatisDao.total(keyword) + pageSize - 1)/pageSize;
        showActivityDto.setTotalPage(totalPage);
        return Response.success(200,"数据加载成功！",showActivityDto);
    }

    @Override
    public List<ActivityWithBLOBs> getActivityTop5(){
        return activityMybatisDao.getActivityTop5();
    }

    @Override
    public List<ActivityWithBLOBs> getActivity(int sinceId) {

        return null;
    }
}
