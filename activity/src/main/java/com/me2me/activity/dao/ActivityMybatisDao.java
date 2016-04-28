package com.me2me.activity.dao;

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


    public List<ActivityWithBLOBs> showActivity(int page, int pageSize) {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        example.setOrderByClause("issue desc limit "+ ((page-1)*pageSize) + " , "+pageSize );
        return activityMapper.selectByExampleWithBLOBs(example);
    }

    public int total() {
        ActivityExample example = new ActivityExample();
        ActivityExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        return activityMapper.countByExample(example);
    }
}
