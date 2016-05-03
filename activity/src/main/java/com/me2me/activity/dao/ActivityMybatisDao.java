package com.me2me.activity.dao;

import com.google.common.base.Strings;
import com.me2me.activity.mapper.ActivityMapper;
import com.me2me.activity.model.Activity;
import com.me2me.activity.model.ActivityExample;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.common.web.Specification;
import com.me2me.user.dto.*;
import com.me2me.user.mapper.*;
import com.me2me.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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


    public void saveActivity(ActivityWithBLOBs activity){
        activityMapper.insertSelective(activity);
    }


    public List<ActivityWithBLOBs> showActivity(int page, int pageSize,String keyword) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
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
        example.setOrderByClause(" issue desc limit 4 ");
        return activityMapper.selectByExampleWithBLOBs(example);
    }

    public List<ActivityWithBLOBs> getActivity(long sinceId){
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Specification.ActivityStatus.NORMAL.index);
        criteria.andIdLessThan(sinceId);
        return activityMapper.selectByExampleWithBLOBs(example);
    }
}
