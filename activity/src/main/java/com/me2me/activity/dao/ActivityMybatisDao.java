package com.me2me.activity.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.me2me.activity.dto.BlurSearchDto;
import com.me2me.activity.dto.LuckActStat2DTO;
import com.me2me.activity.dto.LuckActStatDTO;
import com.me2me.activity.mapper.*;
import com.me2me.activity.model.*;
import com.me2me.common.web.Specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private LuckWinnersMapper luckWinnersMapper;

    @Autowired
    private AactivityMapper aactivityMapper;

    @Autowired
    private AactivityStageMapper aactivityStageMapper;

    @Autowired
    private AuserMapper auserMapper;

    @Autowired
    private TchannelMapper tchannelMapper;

    @Autowired
    private AtopicMapper atopicMapper;

    @Autowired
    private AdoubleTopicApplyMapper adoubleTopicApplyMapper;
    
    @Autowired
    private AmiliDataMapper amiliDataMapper;

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

    public LuckPrize getPrize5(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(5);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public LuckPrize getPrize6(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(6);
        List<LuckPrize> prizes = luckPrizeMapper.selectByExample(example);
        return (prizes.size()>0&&prizes!=null)?prizes.get(0):null;
    }

    public List<LuckPrize> getPrize1Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(1);
        criteria.andNumberNotEqualTo(0);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize2Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(2);
        criteria.andNumberNotEqualTo(0);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize3Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(3);
        criteria.andNumberNotEqualTo(0);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize4Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(4);
        criteria.andNumberNotEqualTo(0);
        return luckPrizeMapper.selectByExample(example);
    }

    public List<LuckPrize> getPrize5Black(){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andAwardIdEqualTo(5);
        criteria.andNumberNotEqualTo(0);
        return luckPrizeMapper.selectByExample(example);
    }

    public void updatePrize(LuckPrize prize){
        luckPrizeMapper.updateByPrimaryKeySelective(prize);
    }
    
    public List<LuckPrize> getPrizeListByActivityName(int activityName){
        LuckPrizeExample example = new LuckPrizeExample();
        LuckPrizeExample.Criteria criteria = example.createCriteria();
        criteria.andActivityNameEqualTo(activityName);
        return luckPrizeMapper.selectByExample(example);
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
    
    public List<LuckAct> getWinnersByActivityName(Integer activityName){
    	LuckActExample example = new LuckActExample();
    	LuckActExample.Criteria criteria = example.createCriteria();
    	criteria.andActivityNameEqualTo(activityName);
    	criteria.andAwardIdGreaterThan(0);
    	List<LuckAct> luckActs = luckActMapper.selectByExample(example);
    	return luckActs;
    }
    
    public List<LuckWinners> getLuckWinnersByActivityName(int activityName){
    	LuckWinnersExample example = new LuckWinnersExample();
    	LuckWinnersExample.Criteria criteria = example.createCriteria();
    	criteria.andActivityNameEqualTo(activityName);
    	List<LuckWinners> list = luckWinnersMapper.selectByExample(example);
    	
    	return list;
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

    public void addWinners(LuckWinners winners){
        luckWinnersMapper.insertSelective(winners);
    }

    public Auser getAuser(long aUid){
        AuserExample example = new AuserExample();
        example.createCriteria().andIdEqualTo(aUid);
        List<Auser> list = auserMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public List<Auser> getAuserList(){
        AuserExample example = new AuserExample();
        example.createCriteria().andStatusEqualTo(1);
        return auserMapper.selectByExample(example);
    }

    public void updateAuser(){
        auserMapper.updateAauser();
    }
    
    public void updateAuser(Auser auser){
    	auserMapper.updateByPrimaryKeySelective(auser);
    }

    public Auser getAuserByMobile(String mobile){
        AuserExample example = new AuserExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<Auser> list = auserMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public Aactivity getAactivity(long id){
        AactivityExample example = new AactivityExample();
        example.createCriteria().andIdEqualTo(id).andStatusEqualTo(1);
        List<Aactivity> list = aactivityMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public AactivityStage getAactivityStageByAid(long activityId){
        AactivityStageExample example = new AactivityStageExample();
        example.createCriteria().andActivityIdEqualTo(activityId);
        List<AactivityStage> list = aactivityStageMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public AactivityStage getAactivityStage(long activityId){
        AactivityStageExample example = new AactivityStageExample();
        example.createCriteria().andActivityIdEqualTo(activityId);
        List<AactivityStage> list = aactivityStageMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public AactivityStage getAactivityStageByStage(long activityId ,int stage){
        AactivityStageExample example = new AactivityStageExample();
        example.createCriteria().andActivityIdEqualTo(activityId).andStageEqualTo(stage);
        List<AactivityStage> list = aactivityStageMapper.selectByExample(example);
        return list.size()>0 && list !=null ?list.get(0) : null;
    }

    public Atopic getAtopicByAuid(long Auid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andAuidEqualTo(Auid).andStatusEqualTo(0);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }
    
    public Auser getAuserByUid(long uid){
    	AuserExample example = new AuserExample();
    	example.createCriteria().andUidEqualTo(uid);
    	List<Auser> list = auserMapper.selectByExample(example);
    	return null!=list && list.size()>0 ?list.get(0):null;
    }

    public Atopic getAtopicByAuidAndSingle(long Auid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andAuidEqualTo(Auid).andStatusEqualTo(0).andTypeEqualTo(1);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public Atopic getAtopicByAuidDouble(long Auid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andAuidEqualTo(Auid).andStatusEqualTo(0).andTypeEqualTo(2);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public Atopic getAtopicByUid1(long uid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(0).andTypeEqualTo(1);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public Atopic getAtopicByUid2(long uid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(0).andTypeEqualTo(2);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public List<AdoubleTopicApply> getTopicApply(long uid){
        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
        example.createCriteria().andUidEqualTo(uid).andTypeEqualTo(1);
        return adoubleTopicApplyMapper.selectByExample(example);
    }

    public void createAdoubleTopicApply(AdoubleTopicApply adoubleTopicApply){
        adoubleTopicApplyMapper.insertSelective(adoubleTopicApply);
    }
    
    public Atopic getAtopicByUidAndType(long uid, int type){
    	AtopicExample example = new AtopicExample();
    	AtopicExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	criteria.andTypeEqualTo(type);
    	criteria.andStatusEqualTo(0);
    	List<Atopic> list = atopicMapper.selectByExample(example);
    	return list.size()>0 && list != null ?list.get(0):null;
    }
    
    public List<Atopic> getAtopicsByUids(List<Long> uids){
    	AtopicExample example = new AtopicExample();
    	AtopicExample.Criteria criteria = example.createCriteria();
    	criteria.andUidIn(uids);
    	criteria.andStatusEqualTo(0);
    	return atopicMapper.selectByExample(example);
    }
    
    public List<Atopic> getAtopicsByUidsAndType(List<Long> uids, int type){
    	AtopicExample example = new AtopicExample();
    	AtopicExample.Criteria criteria = example.createCriteria();
    	criteria.andUidIn(uids);
    	criteria.andTypeEqualTo(type);
    	criteria.andStatusEqualTo(0);
    	return atopicMapper.selectByExample(example);
    }
    
    public List<AdoubleTopicApply> getAdoubleTopicApplyByUidAndTargetUid(long uid, long targetUid){
    	AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
    	AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	criteria.andTargetUidEqualTo(targetUid);
    	return adoubleTopicApplyMapper.selectByExample(example);
    }

//    public List<AdoubleTopicApply> getAdoubleTopicApplyByUid(long uid){
//        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
//        AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
//        criteria.andUidEqualTo(uid);
//        return adoubleTopicApplyMapper.selectByExample(example);
//    }

    public AdoubleTopicApply getAdoubleTopicApplyById(long id){
        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
        AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<AdoubleTopicApply> list = adoubleTopicApplyMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public List<AdoubleTopicApply> getAdoubleTopicApplyByUid(long uid){
        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
        AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid).andStatusNotEqualTo(4);
        return adoubleTopicApplyMapper.selectByExample(example);
    }

    public List<AdoubleTopicApply> getAdoubleTopicApplyByUidReceive(long uid ,int pageNum ,int pageSize){
//        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
//        AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
//        criteria.andTargetUidEqualTo(uid);
        if(pageNum != 0){
            pageNum = pageNum*pageSize;
        }
        Map map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        return adoubleTopicApplyMapper.getReceiveList(map);
    }

    public List<AdoubleTopicApply> getAdoubleTopicApplyByUidAgree(long uid){
        AdoubleTopicApplyExample example = new AdoubleTopicApplyExample();
        AdoubleTopicApplyExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid).andStatusEqualTo(2);
        return adoubleTopicApplyMapper.selectByExample(example);
    }

    public void createAuser(Auser auser){
       auserMapper.insertSelective(auser);
    }
    
    public LuckActStatDTO getLuckActStat(int activityName, Date startTime, Date endTime){
    	List<LuckActStatDTO> list = luckActMapper.getLuctActStatByActivityName(activityName, startTime, endTime);
    	if(null != list && list.size() > 0){
    		return list.get(0);
    	}
    	return null;
    }
    
    public List<LuckActStat2DTO> getLuckActStat2List(int activityName){
    	return luckActMapper.getLuctActStat2ByActivityName(activityName);
    }
    
    public List<LuckAct> getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(int activityName, Date startTime, Date endTime){
    	LuckActExample example = new LuckActExample();
    	LuckActExample.Criteria criteria = example.createCriteria();
    	criteria.andActivityNameEqualTo(activityName);
    	criteria.andAwardIdGreaterThan(0);//只要中奖的
    	if(null != startTime){
    		criteria.andCreatTimeGreaterThanOrEqualTo(startTime);
    	}
    	if(null != endTime){
    		criteria.andCreatTimeLessThanOrEqualTo(endTime);
    	}
    	List<LuckAct> luckActs = luckActMapper.selectByExample(example);
    	return luckActs;
    }
    
    public List<LuckStatus> getLuckStatusListByName(Integer activityName){
        LuckStatusExample example = new LuckStatusExample();
        LuckStatusExample.Criteria criteria = example.createCriteria();
        if(null != activityName){
        	criteria.andActivityNameEqualTo(activityName);
        }
        return luckStatusMapper.selectByExample(example);
    }
    
    public LuckStatus getLuckStatusById(int id){
    	return luckStatusMapper.selectByPrimaryKey(id);
    }
    
    public void updateLuckStatus(LuckStatus ls){
    	luckStatusMapper.updateByPrimaryKeySelective(ls);
    }

    public Atopic getAtopicByTopicId(long topicId){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andTopicIdEqualTo(topicId);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public void createAtopic(Atopic atopic){
    	atopicMapper.insertSelective(atopic);
    }
    
    public void updateAtopicStatus(Map map){
        atopicMapper.updateAtopicStatus(map);
    }

    public List<BlurSearchDto> getTopicByBoy(Map map){
      return atopicMapper.getTopicByBoy(map);
    }

    public List<BlurSearchDto> getTopicByGirl(Map map){
        return atopicMapper.getTopicByGirl(map);
    }

    public Atopic getAtopicByAuidDoubleByUid(long uid){
        AtopicExample example = new AtopicExample();
        example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(0).andTypeEqualTo(2);
        List<Atopic> list = atopicMapper.selectByExample(example);
        return list.size()>0 && list != null ?list.get(0):null;
    }

    public void updateAdoubleTopicApply(AdoubleTopicApply topicApply) {
        adoubleTopicApplyMapper.updateByPrimaryKeySelective(topicApply);
    }
    
    public List<AmiliData> getAllAmiliData(){
    	AmiliDataExample example = new AmiliDataExample();
    	example.createCriteria().andStatusEqualTo(1);//可用
    	return amiliDataMapper.selectByExample(example);
    }
}
