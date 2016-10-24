package com.me2me.activity.service;

import com.alibaba.fastjson.JSON;
import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dto.*;
import com.me2me.activity.model.*;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.user.dto.ActivityModelDto;
import com.me2me.user.model.User;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMybatisDao activityMybatisDao;

    @Autowired
    private UserService userService;

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
        activity.setStatus(Specification.ActivityStatus.STOP.index);
        activityMybatisDao.saveActivity(activity);
        return Response.success();
    }

    @Override
    public Response showActivity(int page, int pageSize,String keyword) {
        log.info("show activity ... start ");
        ShowActivityDto showActivityDto = new ShowActivityDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.showActivity(page,pageSize,keyword);
        for(ActivityWithBLOBs activity : list){
            ShowActivityDto.ActivityElement element = showActivityDto.createElement();
            element.setUid(activity.getUid());
            element.setId(activity.getId());
            element.setStartTime(activity.getStartTime());
            element.setEndTime(activity.getEndTime());
            element.setIssue(activity.getIssue());
            element.setHashTitle(activity.getActivityHashTitle());
            element.setContent(activity.getActivityContent());
            element.setTitle(activity.getActivityTitle());
            element.setStatus(activity.getStatus());
            element.setInternalStatus(activity.getInternalStatus());
            element.setActivityNoticeTitle(activity.getActivityNoticeTitle());
            element.setActivityResult(activity.getActivityResult());
            element.setActivityCover(Constant.QINIU_DOMAIN + "/" +activity.getActivityNoticeCover());
            showActivityDto.getResult().add(element);
        }
        showActivityDto.setTotal(activityMybatisDao.total(keyword));
        int totalPage = (activityMybatisDao.total(keyword) + pageSize - 1)/pageSize;
        showActivityDto.setTotalPage(totalPage);
        log.info("show activity ... end ");
        return Response.success(showActivityDto);
    }

    @Override
    public List<ActivityWithBLOBs> getActivityTop5(){
        return activityMybatisDao.getActivityTop5();
    }

    @Override
    public Response getActivity(int sinceId,long uid) {
        log.info("getActivity start ...");
        ShowActivitiesDto showActivitiesDto = new ShowActivitiesDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.getActivity(sinceId);
        log.info("getActivity data success");
        for(ActivityWithBLOBs activity : list){
            ShowActivitiesDto.ActivityElement activityElement = ShowActivitiesDto.createActivityElement();
            activityElement.setUid(activity.getUid());
            activityElement.setTitle(activity.getActivityHashTitle());
            String cover = activity.getActivityCover();
            if(!StringUtils.isEmpty(cover)) {
                activityElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            UserProfile userProfile = userService.getUserProfileByUid(activity.getUid());
            activityElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            activityElement.setNickName(userProfile.getNickName());
            activityElement.setId(activity.getId());
            activityElement.setUpdateTime(activity.getUpdateTime());
            activityElement.setIsFollowed(userService.isFollow(activity.getUid(),uid));
            showActivitiesDto.getActivityData().add(activityElement);
        }
        log.info("getActivity end ...");
        return Response.success(showActivitiesDto);
    }

    @Override
    public ActivityWithBLOBs loadActivityById(long id) {
        return activityMybatisDao.getActivityById(id);
    }

    @Override
    public void modifyActivity(ActivityWithBLOBs activity) {
        activityMybatisDao.updateActivity(activity);
    }

    @Override
    public void createActivityNotice(CreateActivityNoticeDto createActivityNoticeDto) {
        ActivityWithBLOBs activityWithBLOBs = loadActivityById(createActivityNoticeDto.getId());
        activityWithBLOBs.setActivityNoticeCover(createActivityNoticeDto.getActivityNoticeCover());
        activityWithBLOBs.setActivityNoticeTitle(createActivityNoticeDto.getActivityNoticeTitle());
        activityWithBLOBs.setActivityResult(createActivityNoticeDto.getActivityResult());
        activityWithBLOBs.setInternalStatus(Specification.ActivityInternalStatus.NOTICED.index);
        activityMybatisDao.updateActivity(activityWithBLOBs);
    }

    /**
     * 参与活动
     * @param content
     */
    @Override
    public void joinActivity(String content,long uid) {
        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
        Matcher matcher = pattern.matcher(content);
        boolean result = matcher.matches();
        if(result){
            String hashTitle = matcher.group(2);
            // 获取hash title
            ActivityWithBLOBs activityWithBLOBs = activityMybatisDao.getActivityByHashTitle(hashTitle);
            if(activityWithBLOBs==null){
                return;
            }
            // 判断当前活动是否过期
            if(activityMybatisDao.isEnd(activityWithBLOBs.getId())) {
                activityWithBLOBs.setPersonTimes(activityWithBLOBs.getPersonTimes() + 1);
                activityMybatisDao.updateActivity(activityWithBLOBs);
                UserActivity userActivity = new UserActivity();
                userActivity.setActivityId(activityWithBLOBs.getId());
                userActivity.setUid(uid);
                activityMybatisDao.createUserActivity(userActivity);
            }
        }
    }

    @Override
    public ActivityH5Dto getActivityH5(long id) {
        ActivityH5Dto activityH5Dto = new ActivityH5Dto();
        ActivityWithBLOBs activityWithBLOBs = activityMybatisDao.getActivityById(id);
        if(activityWithBLOBs == null){
            return null;
        }
        int internalStatus = activityWithBLOBs.getInternalStatus();
        if(internalStatus == Specification.ActivityInternalStatus.NOTICED.index){
            activityH5Dto.setActivityContent(activityWithBLOBs.getActivityResult());
            activityH5Dto.setTitle(activityWithBLOBs.getActivityNoticeTitle());
            activityH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + activityWithBLOBs.getActivityNoticeCover());
        }else {
            activityH5Dto.setActivityContent(activityWithBLOBs.getActivityContent());
            activityH5Dto.setTitle(activityWithBLOBs.getActivityTitle());
            activityH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + activityWithBLOBs.getActivityCover());
        }
        UserProfile userProfile = userService.getUserProfileByUid(activityWithBLOBs.getUid());
        activityH5Dto.setNickName(userProfile.getNickName());
        activityH5Dto.setPublishTime(activityWithBLOBs.getCreateTime());
        activityH5Dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        return activityH5Dto;
    }

    @Override
    public ActivityDto getActivity(long id) {
        return null;
    }

    @Override
    public void createActivityReview(long id, long uid, String review,long atUid) {
        ActivityReview activityReview = new ActivityReview();
        activityReview.setActivityId(id);
        activityReview.setReview(review);
        activityReview.setUid(uid);
        activityReview.setAtUid(atUid);
        activityMybatisDao.createActivityReview(activityReview);
    }

    @Override
    public void createActivityTagsDetails(long id, long uid, long tid) {
        ActivityTagsDetails activityTagsDetails = new ActivityTagsDetails();
        activityTagsDetails.setUid(uid);
        activityTagsDetails.setActivityId(id);
        activityTagsDetails.setTid(tid);
        activityMybatisDao.createActivityTagsDetails(activityTagsDetails);
    }

    @Override
    public void createActivityLikesDetails(long id, long uid) {
        ActivityLikesDetails activityLikesDetails = new ActivityLikesDetails();
        activityLikesDetails.setUid(uid);
        activityLikesDetails.setActicityId(id);
        activityMybatisDao.createActivityLikesDetails(activityLikesDetails);

    }

    @Override
    public int getLikeCount(long id) {
        return activityMybatisDao.getLikeCount(id);
    }

    @Override
    public int getReviewCount(long id) {
        return activityMybatisDao.getReviewCount(id);
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
        Matcher matcher = pattern.matcher("#中国人#");
        boolean v = matcher.matches();
        System.out.println(v);
        int i = matcher.groupCount();
        System.out.println(i);
        String value = matcher.group(2);
        System.out.println(value);
    }

    @Override
    public Response luckAward(long uid ,String ip ,int activityName ,String channel ,String version) {

        UserProfile userProfile = userService.getUserProfileByUid(uid);
        //根据前台传来的活动id获取活动信息
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //判断第一次(表是否有数据)
        List<LuckAct> luckacts = activityMybatisDao.getAllLuckAct();
        //根据awardid判断是否有中奖的(不在0内的)如果有这个人就再也不能中奖
        LuckAct luck = activityMybatisDao.getLuckActByAwardId();
        //活动开始和结束时间
        Date startDate = null;
        Date endDate = null;
        Date nowDate = new Date();
        if(luckStatus !=null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate =luckStatus.getEndTime();//运维后台设置的活动结束时间
        }else{
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status ,ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }

        //用户信息(在活动时间内的用户)
        User user = userService.getUserByUidAndTime(uid,startDate,endDate);

        Date lastDay = new Date();
        //如果是活动最后一天
        Boolean isTrue = isSameDate(lastDay,endDate);
        if(isTrue){
            if(luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                    && luckStatus.getVersion().equals(version)
                    && user !=null && nowDate.compareTo(startDate) > 0
                    && nowDate.compareTo(endDate) < 0){
                //查询luckCount表是否有用户数据
                LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
                //如果luckCount表没这个用户信息的话新增一条
                if (luckCount == null) {
                    LuckCount count = new LuckCount();
                    count.setNum(3);
                    count.setCreatTime(new Date());
                    count.setUid(uid);
                    count.setActivityName(activityName);
                    activityMybatisDao.createLuckCount(count);
                }//再根据uid去查询用户信息获得次数
                LuckCount luckCount2 = activityMybatisDao.getLuckCountByUid(uid);
                //判断是否是第二天 是的话初始化为3次
                Date date = luckCount2.getCreatTime();
                if (isToday(date) == false && new Date().compareTo(date) > 0) {
                    luckCount2.setCreatTime(new Date());
                    luckCount2.setNum(3);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //更新完毕后获取最新次数信息
                    LuckCount luckCount3 = activityMybatisDao.getLuckCountByUid(uid);
                    if (luckCount3.getNum() > 0) {
                        //处理抽奖通用方法
                        return awardCommonLastDay(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName);
                    } else {
                        //否则返回错误信息 今天次数已经用完
                        return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                    }
                }
                //有抽奖次数才能继续往下执行
                if(luckCount2.getNum()>0) {
                    return awardCommonLastDay(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName);
                }
                else {
                    //否则返回错误信息 今天次数已经用完
                    return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                }
            } else if(nowDate.compareTo(startDate) < 0 ){
                return Response.success(ResponseStatus.AWARD_ISNOT_START.status,ResponseStatus.AWARD_ISNOT_START.message);
            }
            else if(nowDate.compareTo(endDate) > 0){
                //返回活动已结束
                return Response.success(ResponseStatus.AWARD_IS_END.status,ResponseStatus.AWARD_IS_END.message);
            }
            else if(!luckStatus.getChannel().equals(channel) || user ==null || !luckStatus.getVersion().equals(version) ){
                //不具备抽奖条件
                return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status,ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
            }
            //awardStatus=0的情况下返回未开始
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status,ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }

        //如果当日奖品已经发出去4个了就不能再发了 否则继续发放奖品
        //还有个逻辑就是  如果是活动最后一天了直接送奖品出去
        Date newDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(newDate);
        String startDate1 = start+" 00:00:00";
        String endDate1 = start+" 23:59:59";
        SimpleDateFormat sim =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d1 =null;
        Date d2 =null;
        try {
            d1 = sim.parse(startDate1);
            d2 = sim.parse(endDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //判断活动是否在活动开始时间内 渠道是否是小米 是否是活动时间内注册的用户 在的话可以继续参加活动 否则直接返回活动过期或者别的提示
        if(luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                && luckStatus.getVersion().equals(version)
                && user !=null && nowDate.compareTo(startDate) > 0
                && nowDate.compareTo(endDate) < 0){

            //查询luckCount表是否有用户数据
            LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
            //如果luckCount表没这个用户信息的话新增一条
            if (luckCount == null) {
                LuckCount count = new LuckCount();
                count.setNum(3);
                count.setCreatTime(new Date());
                count.setUid(uid);
                count.setActivityName(activityName);
                activityMybatisDao.createLuckCount(count);
            }//再根据uid去查询用户信息获得次数
            LuckCount luckCount2 = activityMybatisDao.getLuckCountByUid(uid);
            //获取用户创建的时间(用此时间来判断是否是当天)
            Date date = luckCount2.getCreatTime();

            List<LuckAct> acts = activityMybatisDao.getLuckActByToday(d1,d2);
            //如果是当天 并且当天发出的奖品不能超过4次 才能中奖
            if (isToday(date) && acts.size() < 4) {
                if (luckCount2.getNum() > 0) {
                    //处理抽奖通用方法
                    return awardCommon(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName);
                } else {
                    //否则返回错误信息 今天次数已经用完
                    return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                }
            }//如果不在当天 并且 现在时间>数据库存入的时间 说明是第二天了
            else if (isToday(date) == false && new Date().compareTo(date) > 0) {
                luckCount2.setCreatTime(new Date());
                luckCount2.setNum(3);
                activityMybatisDao.updateLuckCount(luckCount2);
                //更新完毕后获取最新次数信息
                LuckCount luckCount3 = activityMybatisDao.getLuckCountByUid(uid);
                if (luckCount3.getNum() > 0) {
                    //处理抽奖通用方法
                    return awardCommon(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName);
                } else {
                    //否则返回错误信息 今天次数已经用完
                    return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                }
            }
            //如果数据库存在中奖情况都话，直接返回个谢谢参与什么都信息，此操作不保存数据库中
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(5, 0.2f, 100));
            awards2.add(new AwardDto(6, 0.3f, 100));
            awards2.add(new AwardDto(7, 0.4f, 100));
            AwardDto award2 = lottery(awards2);
            //如果没有抽奖机会了直接返回
            if(luckCount2.getNum() == 0){
                return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
            }
            log.info("用户："+user.getUserName()+" 获得了 "+award2.id+"等奖");
            //每次抽奖机会-1
            luckCount2.setNum(luckCount2.getNum() - 1);
            activityMybatisDao.updateLuckCount(luckCount2);

            //如果是当天 并且当天发出的奖品超过3个 不能不会中奖了
            String proof = UUID.randomUUID().toString();
            LuckAct luckAct = new LuckAct();
            luckAct.setUid(uid);
            luckAct.setMobile(user.getUserName());
            luckAct.setProof(proof);
            luckAct.setAvatar(userProfile.getAvatar());
            luckAct.setNickName(userProfile.getNickName());
            luckAct.setIpAddress(ip);
            luckAct.setActivityName(activityName);
            luckAct.setAwardId(0);
            activityMybatisDao.createLuckAct(luckAct);

            //获取剩余抽奖次数返回给前台
            LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
            award2.setAwardCount(remain.getNum());

            //获取所有奖品库存给前台
            List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
            award2.setPrizeNumber(JSON.toJSONString(allPrize));
            award2.setAvatar(userProfile.getAvatar());
            award2.setNickName(userProfile.getNickName());
            return Response.success(award2);
        }else if(nowDate.compareTo(startDate) < 0 ){
            //两个Date类型的变量可以通过compareTo方法来比较。此方法的描述是这样的：如果参数 Date 等于此 Date，则返回值 0；
            // 如果此 Date 在 Date 参数之前，则返回小于 0 的值；如果此 Date 在 Date 参数之后，则返回大于 0 的值。
            //返回活动还未开始
            return Response.success(ResponseStatus.AWARD_ISNOT_START.status,ResponseStatus.AWARD_ISNOT_START.message);
        }
        else if(nowDate.compareTo(endDate) > 0){
            //返回活动已结束
            return Response.success(ResponseStatus.AWARD_IS_END.status,ResponseStatus.AWARD_IS_END.message);
        }
        else if(!luckStatus.getChannel().equals(channel) || user ==null || !luckStatus.getVersion().equals(version) ){
            //不具备抽奖条件
            return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status,ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
        }
        //awardStatus=0的情况下返回未开始
        return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status,ResponseStatus.AWARD_ISNOT_EXISTS.message);
    }

    @Override
    public Response getAwardCount(long uid) {
        AwardDto awardDto = new AwardDto();
        LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
        if(luckCount !=null && isToday(luckCount.getCreatTime())){
            //用户存在并且是当天直接查询抽奖次数返回
            awardDto.setAwardCount(luckCount.getNum());
            log.info("get award count successs");
            return Response.success(awardDto);
        }else {
            //否则用户不存在或者不是当天都设置为初始值3次
            awardDto.setAwardCount(3);
            log.info("if not user set user award count 3");
            return Response.success(awardDto);
        }
    }

    @Override
    public Response awardShare(long uid ,int activityName) {
        LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //活动开始和结束时间
        Date startDate = null;
        Date endDate = null;
        Date nowDate = new Date();
        if(luckStatus !=null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate =luckStatus.getEndTime();//运维后台设置的活动结束时间
        }else{
            log.info("award activity is not exists");
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status ,ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
        if(luckCount !=null && nowDate.compareTo(startDate) > 0 && nowDate.compareTo(endDate) < 0){
            //如果luckCount不等于空的话，说明肯定满足了所有抽奖条件了这个用户，不然不会存在这个表里
            luckCount.setNum(luckCount.getNum()+1);
            activityMybatisDao.updateLuckCount(luckCount);
            log.info("award share success num + 1");
            return Response.success(ResponseStatus.AWARD_SHARE_SUCCESS.status,ResponseStatus.AWARD_SHARE_SUCCESS.message);
        }else{
            log.info("do not meet the conditions");
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status,ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
    }

    @Override
    public Response checkIsAward(long uid, int activityName, String channel, String version) {
        //根据前台传来的活动id获取活动信息
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //活动开始和结束时间
        Date startDate = null;
        Date endDate = null;
        Date nowDate = new Date();
        if(luckStatus !=null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate =luckStatus.getEndTime();//运维后台设置的活动结束时间
        }else{
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status ,ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
        //用户信息(在活动时间内的用户)
        User user = userService.getUserByUidAndTime(uid,startDate,endDate);
        //判断活动是否在活动开始时间内 渠道是否是小米 是否是活动时间内注册的用户 在的话可以继续参加活动 否则直接返回活动过期或者别的提示
        if(luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                && luckStatus.getVersion().equals(version)
                && user !=null && nowDate.compareTo(startDate) > 0
                && nowDate.compareTo(endDate) < 0){
            log.info("meet the conditions you can award");
            ActivityModelDto activityModelDto = new ActivityModelDto();
            activityModelDto.setActivityUrl("www.baidu.com");
            return Response.success(ResponseStatus.APPEASE_AWARD_TERM.status ,ResponseStatus.APPEASE_AWARD_TERM.message,activityModelDto);
        }else {
            return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status, ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
        }
    }

    @Override
    public Response getUserAwardInfo(long uid) {
        LuckAct luckAct = activityMybatisDao.getLuckActByAwardId2(uid);
        AwardDto awardDto = new AwardDto();
        if(luckAct !=null){
            int awardId = luckAct.getAwardId();
            LuckPrize luckPrize = activityMybatisDao.getPrizeByAwardId(awardId);
            awardDto.setId(awardId);
            awardDto.setAwardName(luckPrize.getAwardName());
            awardDto.setProof(luckAct.getProof());
            log.info("get user award info success");
            return Response.success(ResponseStatus.USER_AWARD_INFO.status, ResponseStatus.USER_AWARD_INFO.message,awardDto);
        }else{
            return Response.success(ResponseStatus.USER_AWARD_NOT_INFO.status, ResponseStatus.USER_AWARD_NOT_INFO.message);
        }
    }

    public Response awardCommon(long uid ,User user ,List<LuckAct> luckacts ,LuckCount luckCount2 ,LuckAct luck ,UserProfile userProfile ,String ip ,int activityName){
        //可以抽奖
        String proof = UUID.randomUUID().toString();
        LuckAct luckAct = new LuckAct();
        luckAct.setUid(uid);
        luckAct.setMobile(user.getUserName());
        luckAct.setProof(proof);
        luckAct.setAvatar(userProfile.getAvatar());
        luckAct.setNickName(userProfile.getNickName());
        luckAct.setIpAddress(ip);
        luckAct.setActivityName(activityName);

        //查询奖品数量 1 2 3 等奖
        LuckPrize prize1 = activityMybatisDao.getPrize1();
        LuckPrize prize2 = activityMybatisDao.getPrize2();
        LuckPrize prize3 = activityMybatisDao.getPrize3();
        LuckPrize prize4 = activityMybatisDao.getPrize4();

        //概率
        List<AwardDto> awards = new ArrayList<>();
        awards.add(new AwardDto(1, prize1.getAwardChance(), prize1.getNumber()));
        awards.add(new AwardDto(2, prize2.getAwardChance(), prize2.getNumber()));
        awards.add(new AwardDto(3, prize3.getAwardChance(), prize3.getNumber()));
        awards.add(new AwardDto(4, prize4.getAwardChance(), prize4.getNumber()));
        awards.add(new AwardDto(5, 0.2f, 100));
        awards.add(new AwardDto(6, 0.3f, 100));
        awards.add(new AwardDto(7, 0.5f, 100));
//        System.out.println("恭喜您，抽到了：" + lottery(awards).id);

        if (luckacts.size() == 0) {
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(5, 0.2f, 100));
            awards2.add(new AwardDto(6, 0.3f, 100));
            awards2.add(new AwardDto(7, 0.4f, 100));
            AwardDto award2 = lottery(awards2);
//            System.out.println("恭喜您，抽到了：" + award2.id);
            log.info("用户："+user.getUserName()+" 获得了 "+award2.id+"等奖");
            luckAct.setAwardId(0);
            activityMybatisDao.createLuckAct(luckAct);
            //第一次进来用了一次机会，第一次抽奖限制了不能中奖
            luckCount2.setNum(luckCount2.getNum() - 1);
            activityMybatisDao.updateLuckCount(luckCount2);
            //获取剩余次数返回给前台
            LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
            award2.setAwardCount(remain.getNum());

            //获取所有奖品库存给前台
            List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
//            award2.setPrizeNumber(JSON.toJSONString(allPrize));
            award2.setAvatar(userProfile.getAvatar());
            award2.setNickName(userProfile.getNickName());
            return Response.success(award2);
        } else {
            if (luck == null) {
                AwardDto award = lottery(awards);
                if (award.id == 1) {
                    int num = prize1.getNumber() - 1;
                    if (num >= 0) {
                        prize1.setNumber(num);
                        activityMybatisDao.updatePrize(prize1);
                        luckAct.setAwardId(1);
                    } else {
                        prize1.setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 "+award.id+"等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
                } else if (award.id == 2) {
                    int num = prize2.getNumber() - 1;
                    if (num >= 0) {
                        prize2.setNumber(num);
                        activityMybatisDao.updatePrize(prize2);
                        luckAct.setAwardId(2);
                    } else {
                        prize2.setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 "+award.id+"等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
                } else if (award.id == 3) {
                    int num = prize3.getNumber() - 1;
                    if (num >= 0) {
                        prize3.setNumber(num);
                        activityMybatisDao.updatePrize(prize3);
                        luckAct.setAwardId(3);
                    } else {
                        prize3.setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 "+award.id+"等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
                } else if (award.id == 4) {
                    int num = prize3.getNumber() - 1;
                    if (num >= 0) {
                        prize3.setNumber(num);
                        activityMybatisDao.updatePrize(prize3);
                        luckAct.setAwardId(4);
                    } else {
                        prize3.setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 "+award.id+"等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
               }else {//没中奖的话award_id为空
                    luckAct.setAwardId(0);
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 "+award.id+"等奖");
                }
                //获取剩余次数返回给前台
                LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
                award.setAwardCount(remain.getNum());

                //获取所有奖品库存给前台
                List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
//                award.setPrizeNumber(JSON.toJSONString(allPrize));
                award.setAvatar(userProfile.getAvatar());
                award.setNickName(userProfile.getNickName());
                award.setProof(proof);
                award.setMe_number(userService.getUserNoByUid(uid));
                return Response.success(award);
            }
            else {
                //每次抽奖机会-1
                luckCount2.setNum(luckCount2.getNum() - 1);
                activityMybatisDao.updateLuckCount(luckCount2);
                //日志记录中奖过了，返回前台还是抽奖的信息，但是不会中奖
                log.info("用户："+user.getUserName()+" 已经中奖过了，不能继续中奖");
                List<AwardDto> awards2 = new ArrayList<>();
                awards2.add(new AwardDto(5, 0.2f, 100));
                awards2.add(new AwardDto(6, 0.3f, 100));
                awards2.add(new AwardDto(7, 0.5f, 100));
                AwardDto award2 = lottery(awards2);
                //获取剩余次数返回给前台
                LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
                award2.setAwardCount(remain.getNum());
                //获取所有奖品库存给前台
                List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
//                award2.setPrizeNumber(JSON.toJSONString(allPrize));
                award2.setAvatar(userProfile.getAvatar());
                award2.setNickName(userProfile.getNickName());
                return Response.success(award2);
            }
        }
    }

    public Response awardCommonLastDay(long uid ,User user ,List<LuckAct> luckacts ,LuckCount luckCount2 ,LuckAct luck ,
                                       UserProfile userProfile , String ip ,int activityName){
        //可以抽奖
        String proof = UUID.randomUUID().toString();
        LuckAct luckAct = new LuckAct();
        luckAct.setUid(uid);
        luckAct.setMobile(user.getUserName());
        luckAct.setProof(proof);
        luckAct.setAvatar(userProfile.getAvatar());
        luckAct.setNickName(userProfile.getNickName());
        luckAct.setIpAddress(ip);
        luckAct.setActivityName(activityName);

        //查询奖品数量 1 2 3 等奖
        List<LuckPrize> prize1 = activityMybatisDao.getPrize1Black();
        List<LuckPrize> prize2 = activityMybatisDao.getPrize2Black();
        List<LuckPrize> prize3 = activityMybatisDao.getPrize3Black();
        List<LuckPrize> prize4 = activityMybatisDao.getPrize4Black();

        if (luckacts.size() == 0) {
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(5, 0.2f, 100));
            awards2.add(new AwardDto(6, 0.3f, 100));
            awards2.add(new AwardDto(7, 0.4f, 100));
            AwardDto award2 = lottery(awards2);
            log.info("用户："+user.getUserName()+" 获得了 "+award2.id+"等奖");
            luckAct.setAwardId(0);
            activityMybatisDao.createLuckAct(luckAct);
            //第一次进来用了一次机会，第一次抽奖限制了不能中奖
            luckCount2.setNum(luckCount2.getNum() - 1);
            activityMybatisDao.updateLuckCount(luckCount2);
            //获取剩余次数返回给前台
            LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
            award2.setAwardCount(remain.getNum());

            //获取所有奖品库存给前台
            List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
//            award2.setPrizeNumber(JSON.toJSONString(allPrize));
            award2.setAvatar(userProfile.getAvatar());
            award2.setNickName(userProfile.getNickName());
            return Response.success(award2);
        } else {
            if (luck == null) {
                AwardDto award = new AwardDto();
                if (prize4.size()>0) {
                    int num = prize4.get(0).getNumber() - 1;
                    if (num >= 0) {
                        prize4.get(0).setNumber(num);
                        activityMybatisDao.updatePrize(prize4.get(0));
                        luckAct.setAwardId(4);
                    } else {
                        prize4.get(0).setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 4等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(4);
                    award.setAwardName(prize.getAwardName());
                    award.setId(4);
                } else if (prize3.size() >0) {
                    int num = prize3.get(0).getNumber() - 1;
                    if (num >= 0) {
                        prize3.get(0).setNumber(num);
                        activityMybatisDao.updatePrize(prize3.get(0));
                        luckAct.setAwardId(3);
                    } else {
                        prize3.get(0).setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 3等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(3);
                    award.setAwardName(prize.getAwardName());
                    award.setId(3);
                } else if (prize2.size() >0) {
                    int num = prize2.get(0).getNumber() - 1;
                    if (num >= 0) {
                        prize2.get(0).setNumber(num);
                        activityMybatisDao.updatePrize(prize2.get(0));
                        luckAct.setAwardId(2);
                    } else {
                        prize2.get(0).setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户："+user.getUserName()+" 获得了 2等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(2);
                    award.setAwardName(prize.getAwardName());
                    award.setId(2);
                } else if (prize1.size() >0) {
                    int num = prize1.get(0).getNumber() - 1;
                    if (num >= 0) {
                        prize1.get(0).setNumber(num);
                        activityMybatisDao.updatePrize(prize1.get(0));
                        luckAct.setAwardId(1);
                    } else {
                        prize1.get(0).setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 1等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(1);
                    award.setAwardName(prize.getAwardName());
                    award.setId(1);
                }else {//没中奖的话award_id为空
                    luckAct.setAwardId(0);
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 5等奖");
                    award.setId(5);
                }
                //获取剩余次数返回给前台
                LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
                award.setAwardCount(remain.getNum());

                //获取所有奖品库存给前台
                List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
                //奖品所有剩余数量json
//                award.setPrizeNumber(JSON.toJSONString(allPrize));
                award.setAvatar(userProfile.getAvatar());
                award.setNickName(userProfile.getNickName());
                award.setProof(proof);
                award.setMe_number(userService.getUserNoByUid(uid));
                return Response.success(award);
            }
            else {
                //每次抽奖机会-1
                luckCount2.setNum(luckCount2.getNum() - 1);
                activityMybatisDao.updateLuckCount(luckCount2);
                //日志记录中奖过了，返回前台还是抽奖的信息，但是不会中奖
                log.info("用户："+user.getUserName()+" 已经中奖过了，不能继续中奖");
                List<AwardDto> awards2 = new ArrayList<>();
                awards2.add(new AwardDto(5, 0.2f, 100));
                awards2.add(new AwardDto(6, 0.3f, 100));
                awards2.add(new AwardDto(7, 0.5f, 100));
                AwardDto award2 = lottery(awards2);
                //获取剩余次数返回给前台
                LuckCount remain = activityMybatisDao.getLuckCountByUid(uid);
                award2.setAwardCount(remain.getNum());
                //获取所有奖品库存给前台
                List<LuckPrize> allPrize = activityMybatisDao.getAllPrize();
//                award2.setPrizeNumber(JSON.toJSONString(allPrize));
                award2.setAvatar(userProfile.getAvatar());
                award2.setNickName(userProfile.getNickName());
                return Response.success(award2);
            }
        }
    }

    public static AwardDto lottery(List<AwardDto> awards){
        //总的概率区间
        float totalPro = 0f;
        //存储每个奖品新的概率区间
        List<Float> proSection = new ArrayList<Float>();
        proSection.add(0f);
        //遍历每个奖品，设置概率区间，总的概率区间为每个概率区间的总和
        for (AwardDto award : awards) {
            //每个概率区间为奖品概率乘以1000（把三位小数转换为整）再乘以剩余奖品数量(奖品库存为0的话 是永远不会拿到奖品了)
            totalPro += award.probability * 10 * award.count;
            proSection.add(totalPro);
        }
        //获取总的概率区间中的随机数
        Random random = new Random();
        float randomPro = (float)random.nextInt((int)totalPro);
        //判断取到的随机数在哪个奖品的概率区间中
        for (int i = 0,size = proSection.size(); i < size; i++) {
            if(randomPro >= proSection.get(i)
                    && randomPro < proSection.get(i + 1)){
                return awards.get(i);
            }
        }
        return null;
    }

    //判断是否在当天时间
    public static boolean isToday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH)+1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if(year1 == year2 && month1 == month2 && day1 == day2){
            return true;
        }
        return false;
    }

    //判断是两个日期否是同一天
    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }

    public void setStartEndTime(String startDate ,String endDate) {
        Date newDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(newDate);
        startDate = start+" 00:00:00";
        endDate = start+" 23:59:59";
    }
}
