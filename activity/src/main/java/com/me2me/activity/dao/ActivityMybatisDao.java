package com.me2me.activity.dao;

import com.google.common.base.Strings;
import com.me2me.activity.mapper.*;
import com.me2me.activity.model.*;
import com.me2me.common.web.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Repository
public class ActivityMybatisDao {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserActivityMapper userActivityMapper;

    @Autowired
    private ActivityReviewMapper activityReviewMapper;

    @Autowired
    private ActivityTagsDetailsMapper activityTagsDetailsMapper;

    @Autowired
    private ActivityLikesDetailsMapper activityLikesDetailsMapper;


    public void saveActivity(ActivityWithBLOBs activity){
        activityMapper.insertSelective(activity);
    }


    public List<ActivityWithBLOBs> showActivity(int page, int pageSize,String keyword) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
//        criteria.andStatusEqualTo(0);
        if(!Strings.isNullOrEmpty(keyword)){
            criteria.andActivityTitleLike("%"+keyword+"%");
        }
        example.setOrderByClause("issue desc limit "+ ((page-1)*pageSize) + " , "+pageSize );
        return activityMapper.selectByExampleWithBLOBs(example);
    }

    public int total(String keyword) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        if(!Strings.isNullOrEmpty(keyword)){
            criteria.andActivityTitleLike("%"+keyword+"%");
        }
        return activityMapper.countByExample(example);
    }

    public List<ActivityWithBLOBs> getActivityTop5(){
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Specification.ActivityStatus.NORMAL.index);
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        ActivityExample.Criteria criteria2 = example.createCriteria();
        criteria2.andInternalStatusEqualTo(Specification.ActivityInternalStatus.NOTICED.index);
        criteria2.andStatusEqualTo(Specification.ActivityStatus.NORMAL.index);
        example.or(criteria2);
        example.setOrderByClause(" issue desc limit 4 ");
        return activityMapper.selectByExampleWithBLOBs(example);
    }

    public List<ActivityWithBLOBs> getActivity(long sinceId){
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Specification.ActivityStatus.NORMAL.index);
        criteria.andIdLessThan(sinceId);
        example.setOrderByClause(" id desc limit 10 ");
        return activityMapper.selectByExampleWithBLOBs(example);
    }

    public ActivityWithBLOBs getActivityById(long id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    public ActivityWithBLOBs getActivityByHashTitle(String hashTitle) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Specification.ActivityStatus.NORMAL.index);
        criteria.andActivityHashTitleEqualTo(hashTitle);
        List<ActivityWithBLOBs> list = activityMapper.selectByExampleWithBLOBs(example);
        return list!=null&&list.size()>0?list.get(0):null;
    }

    public void updateActivity(ActivityWithBLOBs activity) {
        activityMapper.updateByPrimaryKeySelective(activity);
    }

    public void createUserActivity(UserActivity userActivity){
        userActivityMapper.insertSelective(userActivity);
    }

    public boolean isEnd(long id) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andEndTimeGreaterThan(new Date());
        criteria.andIdEqualTo(id);
        List<ActivityWithBLOBs> list = activityMapper.selectByExampleWithBLOBs(example);
        return list!=null&&list.size()>0?true:false;
    }

    public void createActivityReview(ActivityReview activityReview){
        activityReviewMapper.insertSelective(activityReview);
    }

    public void createActivityTagsDetails(ActivityTagsDetails activityTagsDetails){
        activityTagsDetailsMapper.insertSelective(activityTagsDetails);
    }

    public void createActivityLikesDetails(ActivityLikesDetails activityLikesDetails){
        activityLikesDetailsMapper.insertSelective(activityLikesDetails);
    }
}
