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

    @Autowired
    private LuckActMapper luckActMapper;

    @Autowired
    private LuckPrizeMapper luckPrizeMapper;

    @Autowired
    private LuckCountMapper luckCountMapper;

    @Autowired
    private LuckStatusMapper luckStatusMapper;


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

    public int getLikeCount(long id){
        ActivityLikesDetailsExample example = new ActivityLikesDetailsExample();
        ActivityLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andActicityIdEqualTo(id);
        return activityLikesDetailsMapper.countByExample(example);
    }

    public int getReviewCount(long id){
        ActivityReviewExample example = new ActivityReviewExample();
        ActivityReviewExample.Criteria criteria = example.createCriteria();
        criteria.andActivityIdEqualTo(id);
        return activityReviewMapper.countByExample(example);
    }

    public void createLuckAct(LuckAct luck){
        luckActMapper.insertSelective(luck);
    }

    public List<LuckPrize> getAllPrize(){
        return luckPrizeMapper.selectByExample(null);
    }

    public LuckPrize getPrizeByAwardId(int awardId){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(awardId);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public LuckPrize getPrize1(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(1);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public LuckPrize getPrize2(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(2);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public LuckPrize getPrize3(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(3);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public LuckPrize getPrize4(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(4);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public List<LuckPrize> getPrize1Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(1);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize2Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(2);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize3Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(3);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize4Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(4);
        return luckPrizeMapper.selectByExample(example);
    }

    public void updatePrize(LuckPrize prize){
        luckPrizeMapper.updateByPrimaryKeySelective(prize);
    }

    public List<LuckAct> getAllLuckAct(){
        LuckActExample example = new LuckActExample();
        LuckActExample.Criteria criteria = example.createCriteria();
        return luckActMapper.selectByExample(example);
    }

    public List<LuckAct> getLuckActByToday(Date startDate ,Date endDate){
        LuckActExample example = new LuckActExample();
        LuckActExample.Criteria criteria = example.createCriteria();
        criteria.andCreatTimeGreaterThan(startDate);
        criteria.andCreatTimeLessThan(endDate);
        criteria.andAwardIdNotEqualTo(0);
        return luckActMapper.selectByExample(example);
    }

    public LuckAct getLuckActByAwardId(){
        LuckActExample example = new LuckActExample();
        LuckActExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdNotEqualTo(0);
        List<LuckAct> luckActs = luckActMapper.selectByExample(example);
        return (luckActs.size()>0 && luckActs!=null)?luckActs.get(0):null;
    }

    public LuckAct getLuckActByAwardId2(long uid){
        LuckActExample example = new LuckActExample();
        LuckActExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andAwardIdNotEqualTo(0);
        List<LuckAct> luckActs = luckActMapper.selectByExample(example);
        return (luckActs.size()>0 && luckActs!=null)?luckActs.get(0):null;
    }

    public LuckCount getLuckCountByUid(long uid) {
        LuckCountExample example = new LuckCountExample();
        LuckCountExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<LuckCount> luckCounts = luckCountMapper.selectByExample(example);
        return (luckCounts.size()>0 && luckCounts !=null)?luckCounts.get(0):null;
    };

    public void createLuckCount(LuckCount luckCount){
        luckCountMapper.insertSelective(luckCount);
    }

    public void updateLuckCount(LuckCount luckCount){
        luckCountMapper.updateByPrimaryKeySelective(luckCount);
    }

    public LuckStatus getLuckStatusByName(int activityName){
        LuckStatusExample example = new LuckStatusExample();
        LuckStatusExample.Criteria criteria = example.createCriteria();
        criteria.andActivityNameEqualTo(activityName);
        List<LuckStatus> luckStatuses = luckStatusMapper.selectByExample(example);
        return (luckStatuses.size()>0 && luckStatuses !=null)?luckStatuses.get(0):null;
    };
}
