package com.me2me.activity.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dao.LiveForActivityDao;
import com.me2me.activity.dto.*;
import com.me2me.activity.model.*;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.EncryUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.sms.dto.AwardXMDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.service.ChannelType;
import com.me2me.user.dto.ActivityModelDto;
import com.me2me.user.model.User;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
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

    private static final String SEVENDAY_KEY = "key:sevenday";

    private static final String BRID_KEY = "key:brid";

    @Autowired
    private ActivityMybatisDao activityMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LiveForActivityDao liveForActivityDao;

    @Autowired
    private CacheService cacheService;

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
            activityElement.setContentType(activity.getTyp());
            activityElement.setContentUrl(activity.getLinkUrl());
            activityElement.setType(4);
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

//    public static void main(String[] args) {
//        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
//        Matcher matcher = pattern.matcher("#中国人#");
//        boolean v = matcher.matches();
//        System.out.println(v);
//        int i = matcher.groupCount();
//        System.out.println(i);
//        String value = matcher.group(2);
//        System.out.println(value);
//        Random random = new Random();
//        float randomPro = (float)random.nextInt(2);
//        System.out.println(randomPro);
//    }

    @Override
    public Response luckAward(long uid ,String ip ,int activityName ,String channel ,String version) {

        UserProfile userProfile = userService.getUserProfileByUid(uid);
        //根据前台传来的活动id获取活动信息
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //判断第一次(表是否有数据)
        List<LuckAct> luckacts = activityMybatisDao.getAllLuckAct();
        //根据awardid判断是否有中奖的(不在0内的)如果有这个人就再也不能中奖
        LuckAct luck = activityMybatisDao.getLuckActByAwardId2(uid);
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
                    return awardCommon(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName ,luckStatus);
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
                    return awardCommon(uid,user,luckacts,luckCount2,luck,userProfile,ip,activityName,luckStatus);
                } else {
                    //否则返回错误信息 今天次数已经用完
                    return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                }
            }
            //如果数据库存在中奖情况都话，直接返回个谢谢参与什么都信息，此操作不保存数据库中
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(6, 0.2f, 100));
            AwardDto award2 = lotteryLastDay(awards2);
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
    public Response checkIsAward(long uid, int activityName, String channel, String version ,String token) {
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
            //DES加密
            String secruityUid = EncryUtil.encrypt(Long.toString(uid));
            String secruityToken = EncryUtil.encrypt(token);
            System.out.println("加密后secruityUid：" + secruityUid+" secruityToken："+secruityToken);

            activityModelDto.setActivityUrl("http://webapp.me-to-me.com/web/lottery/awardJoin?uid="+secruityUid+"&&token="+secruityToken);
            log.info("get awardurl success");
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

    @Override
    public Response getAwardStatus(int activityName) {
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        AwardStatusDto awardStatusDto = new AwardStatusDto();
        if(luckStatus !=null){
            awardStatusDto.setActivityName(luckStatus.getActivityName());
            awardStatusDto.setChannel(luckStatus.getChannel());
            awardStatusDto.setVersion(luckStatus.getVersion());
        }
        return Response.success(awardStatusDto);
    }

    public Response awardCommon(long uid ,User user ,List<LuckAct> luckacts ,LuckCount luckCount2 ,LuckAct luck ,UserProfile userProfile ,String ip ,int activityName ,LuckStatus luckStatus){
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
        LuckPrize prize5 = activityMybatisDao.getPrize5();
        LuckPrize prize6 = activityMybatisDao.getPrize6();

        //概率
        List<AwardDto> awards = new ArrayList<>();
        awards.add(new AwardDto(1, prize1.getAwardChance(), prize1.getNumber()));
        awards.add(new AwardDto(2, prize2.getAwardChance(), prize2.getNumber()));
        awards.add(new AwardDto(3, prize3.getAwardChance(), prize3.getNumber()));
        awards.add(new AwardDto(4, prize4.getAwardChance(), prize4.getNumber()));
        awards.add(new AwardDto(5, prize5.getAwardChance(), prize5.getNumber()));
        awards.add(new AwardDto(6, prize6.getAwardChance(), prize6.getNumber()));
//        awards.add(new AwardDto(7, 0.5f, 100));
//        awards.add(new AwardDto(8, 0.5f, 100));
//        System.out.println("恭喜您，抽到了：" + lottery(awards).id);

        if (luckacts.size() == 0) {
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(6, 0.2f, 100));
            AwardDto award2 = lotteryLastDay(awards2);
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
                //需要取数据库的总概率值 控制平均
                int sum = luckStatus.getAwardSumChance();
                AwardDto award = lottery(awards,sum);
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
                    int num = prize4.getNumber() - 1;
                    if (num >= 0) {
                        prize4.setNumber(num);
                        activityMybatisDao.updatePrize(prize4);
                        luckAct.setAwardId(4);
                    } else {
                        prize4.setNumber(0);
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
               }else if (award.id == 5) {
                    int num = prize5.getNumber() - 1;
                    if (num >= 0) {
                        prize5.setNumber(num);
                        activityMybatisDao.updatePrize(prize5);
                        luckAct.setAwardId(5);
                    } else {
                        prize5.setNumber(0);
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
                }
                else {//没中奖的话award_id为空
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
                awards2.add(new AwardDto(6, 0.2f, 100));
                AwardDto award2 = lotteryLastDay(awards2);
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
        List<LuckPrize> prize5 = activityMybatisDao.getPrize5Black();

        if (luckacts.size() == 0) {
            List<AwardDto> awards2 = new ArrayList<>();
            awards2.add(new AwardDto(6, 0.2f, 100));
            AwardDto award2 = lotteryLastDay(awards2);
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
                if (prize5.size()>0) {
                    int num = prize5.get(0).getNumber() - 1;
                    if (num >= 0) {
                        prize5.get(0).setNumber(num);
                        activityMybatisDao.updatePrize(prize5.get(0));
                        luckAct.setAwardId(5);
                    } else {
                        prize5.get(0).setNumber(0);
                        luckAct.setAwardId(0);
                    }
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 5等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(5);
                    award.setAwardName(prize.getAwardName());
                    award.setId(5);
                }else if (prize4.size()>0) {
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
                }else {
                    luckAct.setAwardId(0);
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户："+user.getUserName()+" 获得了 6等奖");
                    award.setId(6);
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
                awards2.add(new AwardDto(6, 0.2f, 100));
                AwardDto award2 = lotteryLastDay(awards2);
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

    public static AwardDto lottery(List<AwardDto> awards ,int sum){
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
        float randomPro= (float)random1(proSection.get(proSection.size()-1) ,proSection.get(proSection.size()-2) ,sum);
        //判断取到的随机数在哪个奖品的概率区间中
        for (int i = 0,size = proSection.size(); i < size; i++) {
            if(randomPro >= proSection.get(i)
                    && randomPro < proSection.get(i + 1)){
                System.out.println("下标:"+i);
                return awards.get(i);
            }
        }
        return null;
    }

    //不中奖的情况调用此方法
    public static AwardDto lotteryLastDay(List<AwardDto> awards){
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
                log.info("award info randomPro: "+randomPro+" proSection.get(i): "+proSection.get(i)+" i: "+i+" 总数:"+proSection.get(proSection.size()-1));
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

    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

	@Override
	public Response getWinners(int activityName) {
		List<LuckAct> list = activityMybatisDao.getWinnersByActivityName(activityName);
		ShowLuckActsDTO dto = new ShowLuckActsDTO();
		if(null != list && list.size() > 0){
			List<LuckPrize> pList = activityMybatisDao.getPrizeListByActivityName(activityName);
			Map<String, LuckPrize> pMap = new HashMap<String, LuckPrize>();
			if(null != pList && pList.size() > 0){
				for(LuckPrize lp : pList){
					pMap.put(lp.getAwardId()+"", lp);
				}
			}
			LuckPrize p = null;
			for(LuckAct la : list){
				ShowLuckActsDTO.LuckActElement e = ShowLuckActsDTO.createLuckActElement();
				e.setActivityName(la.getActivityName());
				e.setActivityNameStr(getNameFromInt2String(la.getActivityName()));
				e.setAvatar(la.getAvatar());
				e.setAwardId(la.getAwardId());
				e.setCreatTime(la.getCreatTime());
				e.setIpAddress(la.getIpAddress());
				e.setMobile(la.getMobile());
				e.setNickName(la.getNickName());
				e.setProof(la.getProof());
				e.setUid(la.getUid());
				if(la.getAwardId() > 0){
					p = pMap.get(la.getAwardId()+"");
					if(null != p){
						e.setAwardName(getAwardNameFromInt2String(p.getAwardId()));
						e.setAwardPrize(p.getAwardName());
					}
				}
				dto.getResult().add(e);
			}
		}
		return Response.success(dto);
	}
	
	@Override
	public Response getWinnersCommitInfo(int activityName) {
		List<LuckWinners> list = activityMybatisDao.getLuckWinnersByActivityName(activityName);
		ShowLuckWinnersDTO dto = new ShowLuckWinnersDTO();
		if(null != list && list.size() > 0){
			for(LuckWinners w : list){
				ShowLuckWinnersDTO.LuckWinnersElement e = ShowLuckWinnersDTO.createLuckWinnersElement();
				e.setActivityName(w.getActivityName());
				e.setAwardId(w.getAwardId());
				e.setAwardName(w.getAwardName());
				e.setCreateTime(w.getCreateTime());
				e.setMobile(w.getMobile());
				e.setUid(w.getUid());
				e.setActivityNameStr(getNameFromInt2String(w.getActivityName()));
				dto.getResult().add(e);
			}
		}
		
		return Response.success(dto);
	}

    @Override
    public Response addWinners(long uid ,int activityName, String mobile, int awardId, String awardName) {
        LuckWinners winners = new LuckWinners();
        winners.setUid(uid);
        winners.setActivityName(activityName);
        winners.setMobile(mobile);
        winners.setAwardId(awardId);
        winners.setAwardName(awardName);
        activityMybatisDao.addWinners(winners);
        return Response.success();
    }

    @Override
    public Response getActivityUser(long uid) {
    	Auser auser = activityMybatisDao.getAuserByUid(uid);
        QiActivityDto qiActivityDto = new QiActivityDto();
        if(auser !=null){
            if(auser.getStatus() == 3){
                qiActivityDto.setMobile(auser.getMobile());
                //查询活动王国信息
                Atopic atopicSingle = activityMybatisDao.getAtopicByAuidAndSingle(auser.getId());
                Atopic atopicDouble = activityMybatisDao.getAtopicByAuidDouble(auser.getId());
                //第一阶段
                AactivityStage aactivityStage1 = activityMybatisDao.getAactivityStageByStage(auser.getActivityId() ,1);
                //第二阶段
                AactivityStage aactivityStage2 = activityMybatisDao.getAactivityStageByStage(auser.getActivityId() ,2);
                //第三阶段
                AactivityStage aactivityStage3 = activityMybatisDao.getAactivityStageByStage(auser.getActivityId() ,3);

                Map<String,Object> topicSingle =null;
                Map<String,Object> topicDouble =null;
                if(atopicSingle!=null){
                    topicSingle = liveForActivityDao.getTopicById(atopicSingle.getTopicId());
                }
                if(atopicDouble!=null){
                    topicDouble = liveForActivityDao.getTopicById(atopicDouble.getTopicId());
                }
                    //单人王国返回信息 不在第一阶段就行
                    if(atopicSingle !=null){
                        if(aactivityStage1 == null) {
                            if (topicSingle != null) {
                                QiActivityDto.TopicElement topicElement = qiActivityDto.createElement();
                                topicElement.setLiveImage((String)topicSingle.get("live_image"));
                                topicElement.setTitle((String)topicSingle.get("title"));
                                topicElement.setTopicId((Long)topicSingle.get("id"));
                                topicElement.setStage(Specification.ASevenDayType.A_DOUBLE_STAGE.index);
                                log.info("single topic get success ...");
                                qiActivityDto.getTopicList().add(topicElement);
                            }
                        }
                    }
                    if(atopicDouble !=null){
                        //是双人王国并且处于第三阶段
                        if(aactivityStage3 != null){
                            if(topicDouble != null){
                                QiActivityDto.TopicElement topicElement = qiActivityDto.createElement();
                                topicElement.setLiveImage((String)topicDouble.get("live_image"));
                                topicElement.setTitle((String)topicDouble.get("title"));
                                topicElement.setTopicId((Long)topicDouble.get("id"));
                                topicElement.setStage(Specification.ASevenDayType.A_THREE_STAGE.index);
                                log.info("double topic get success ...");
                                qiActivityDto.getTopicList().add(topicElement);
                            }
                        }
                    }
                    if(atopicSingle ==null && atopicDouble ==null){
                        //都不存在返回为
                        return Response.success(ResponseStatus.TOPIC_GET_FAILURE.status,ResponseStatus.TOPIC_GET_FAILURE.message);
                    }
                    if(aactivityStage3 == null){
                        //不是第三阶段返回不处于第三阶段
                        return Response.success(ResponseStatus.NOT_THREE_STAGE.status,ResponseStatus.NOT_THREE_STAGE.message,qiActivityDto);
                    }
                        //返回王国信息
                        return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status,ResponseStatus.SEARCH_ATOPIC_SUCCESS.message,qiActivityDto);
            }else if(auser.getStatus() == 2){
                return Response.success(ResponseStatus.AUDIT_FAILURE.status,ResponseStatus.AUDIT_FAILURE.message);
            }else if(auser.getStatus() == 1){
                return Response.success(ResponseStatus.IN_AUDIT.status,ResponseStatus.IN_AUDIT.message);
            }
        }

        return Response.success(ResponseStatus.QIACITIVITY_NOT_INFO_SUCCESS.status,ResponseStatus.QIACITIVITY_NOT_INFO_SUCCESS.message);
    }
    
    @Override
	public Auser getAuserByUid(long uid) {
    	Auser auser = activityMybatisDao.getAuserByUid(uid);
		return auser;
	}
    
    @Override
	public Aactivity getAactivityById(long id) {
		return activityMybatisDao.getAactivity(id);
	}
    
	@Override
	public Atopic getAtopicByUidAndType(long uid, int type) {
		return activityMybatisDao.getAtopicByUidAndType(uid, type);
	}
	
	@Override
	public Response checkUserActivityKindom(long uid, int type, long uid2) {
		Date now = new Date();
		Aactivity aa = this.getAactivityById(1);
		if(null == aa || null == aa.getStartTime() || null == aa.getEndTime() 
				|| aa.getStartTime().getTime()>now.getTime() 
				|| aa.getEndTime().getTime()<now.getTime()){
			log.info("now is out of activity!");
			return Response.failure("不在活动期");
		}
		//1先判断当前用户是否为我们活动报名用户，并审核通过
		Auser auser = this.getAuserByUid(uid);
		if(null == auser){
			log.info("uid[" + uid + "] is not activity user!");
			return Response.failure("用户为报名");
		}
		if(auser.getStatus() != 3){
			log.info("uid["+uid+"] is not audit success!");
			return Response.failure("用户为审核通过");
		}
		if(type == Specification.ActivityKingdomType.SINGLEKING.index){//单人王国
			Atopic singleKingdom = this.getAtopicByUidAndType(uid, type);
			if(null != singleKingdom){
				log.info("user["+uid+"] already has single kingdom");
				return Response.failure("用户已经有单人王国了");
			}
		}else if(type == Specification.ActivityKingdomType.DOUBLEKING.index){//双人王国
			if(uid2 <= 0){
				log.info("uid2 less 1");
				return Response.failure("双人王国小王名字必须传递");
			}
			List<Long> uids = new ArrayList<Long>();
			uids.add(uid);
			uids.add(uid2);
			List<Atopic> topics = activityMybatisDao.getAtopicsByUids(uids);
			Map<String, Atopic> tMap = new HashMap<String, Atopic>();
			if(null != topics && topics.size() > 0){
				for(Atopic t : topics){
					tMap.put(t.getUid()+"_"+t.getType(), t);
				}
			}
			Atopic t = tMap.get(uid+"_"+Specification.ActivityKingdomType.SINGLEKING.index);
			if(null == t){
				return Response.failure("用户必须创建单人王国才能创建双人王国");
			}
			t = tMap.get(uid2+"_"+Specification.ActivityKingdomType.SINGLEKING.index);
			if(null == t){
				return Response.failure("对方必须创建单人王国才能创建双人王国");
			}
			t = tMap.get(uid+"_"+Specification.ActivityKingdomType.DOUBLEKING.index);
			if(null != t){
				return Response.failure("用户已经有双人王国了，不能再创建了");
			}
			t = tMap.get(uid2+"_"+Specification.ActivityKingdomType.DOUBLEKING.index);
			if(null != t){
				return Response.failure("对方已经有双人王国了，不能再创建了");
			}
			List<AdoubleTopicApply> applyList = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid(uid, uid2);
			boolean isCan = false;
			if(null != applyList && applyList.size() > 0){
				for(AdoubleTopicApply a : applyList){
					if(a.getStatus() == 2){//同意
						isCan = true;
						break;
					}
				}
			}
			if(!isCan){
				return Response.failure("你和对方的双人王国申请未通过，不能创建");
			}
		}else{
			log.info("invalid type");
			return Response.failure("无效的王国类型");
		}
		return Response.success();
	}
	
	@Override
	public void createActivityKingdom(long topicId, long uid, int type,
			long uid2) {
		Date now = new Date();
		Auser u1 = activityMybatisDao.getAuserByUid(uid);
		//先创大王的
		Atopic t = new Atopic();
		t.setAuid(u1.getId());
		t.setUid(uid);
		if(type == Specification.ActivityKingdomType.DOUBLEKING.index){
			t.setUid2(uid2);
		}else{
			t.setUid2(0l);
		}
		t.setTopicId(topicId);
		t.setType(type);
		t.setRights(1);
		t.setCreateTime(now);
		t.setHot(0l);
		t.setStatus(0);
		activityMybatisDao.createAtopic(t);
		
		if(type == Specification.ActivityKingdomType.DOUBLEKING.index){
			//双人王国还要创建小王的对应关系
			Auser u2 = activityMybatisDao.getAuserByUid(uid2);
			t = new Atopic();
			t.setAuid(u2.getId());
			t.setUid(uid2);
			t.setUid2(uid);
			t.setTopicId(topicId);
			t.setType(type);
			t.setRights(2);
			t.setCreateTime(now);
			t.setHot(0l);
			t.setStatus(0);
			activityMybatisDao.createAtopic(t);
		}
	}
	
	@Override
	public List<Atopic> getAtopicsByUidsAndType(List<Long> uids, int type) {
		return activityMybatisDao.getAtopicsByUidsAndType(uids, type);
	}

    @Override
    public Response enterActivity(QiUserDto qiUserDto) {

        VerifyDto verifyDto = new VerifyDto();
        //验证为1
        verifyDto.setAction(Specification.VerifyAction.CHECK.index);
        verifyDto.setMobile(qiUserDto.getMobile());
        verifyDto.setVerifyCode(qiUserDto.getVerifyCode());
        Response response = userService.verify(verifyDto);
        if (response.getCode() != ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status) {
        	return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }

        Aactivity aactivity = activityMybatisDao.getAactivity(qiUserDto.getActivityId());
        if(null == aactivity || null == aactivity.getStartTime() || null == aactivity.getEndTime()){
        	return Response.success(ResponseStatus.QIACTIVITY_NOT_START.status, ResponseStatus.QIACTIVITY_NOT_START.message);
        }
        Date nowDate = new Date();
        if(nowDate.compareTo(aactivity.getStartTime()) < 0 || nowDate.compareTo(aactivity.getEndTime()) > 0){
        	return Response.success(ResponseStatus.QIACTIVITY_NOT_START.status, ResponseStatus.QIACTIVITY_NOT_START.message);
        }
        
        AactivityStage aactivityStage1 = activityMybatisDao.getAactivityStageByStage(qiUserDto.getActivityId() ,1);
        if(null == aactivityStage1 || aactivityStage1.getType() != 0){
        	return Response.success(ResponseStatus.NOT_FIRST_STAGE.status, ResponseStatus.NOT_FIRST_STAGE.message);
        }
        
        Auser activityUser = activityMybatisDao.getAuserByMobile(qiUserDto.getMobile());
        if(null != activityUser){
        	return Response.success(ResponseStatus.CAN_ONLY_SIGN_UP_ONCE.status, ResponseStatus.CAN_ONLY_SIGN_UP_ONCE.message);
        }
        
        Auser auser = new Auser();
        BeanUtils.copyProperties(qiUserDto, auser);
        //默认审核状态
        auser.setStatus(1);
        UserProfile userProfile = userService.getUserProfileByMobile(qiUserDto.getMobile());
        //手机号存在才会强行绑定
        if (userProfile != null) {
        	Auser u = activityMybatisDao.getAuserByUid(userProfile.getUid());
        	if(null == u){//没绑的要默认绑上
        		auser.setUid(userProfile.getUid());
        	}
        }
        activityMybatisDao.createAuser(auser);
        return Response.success(ResponseStatus.REGISTRATION_SUCCESS.status, ResponseStatus.REGISTRATION_SUCCESS.message);
    }

    @Override
    public Response bindGetActivity(long uid ,String mobile ,String verifyCode) {

        VerifyDto verifyDto = new VerifyDto();
        //验证为1
        verifyDto.setAction(ChannelType.NORMAL_SMS.index);
        verifyDto.setMobile(mobile);
        verifyDto.setVerifyCode(verifyCode);
        Response response = userService.verify(verifyDto);

        if (response.getCode() == ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status) {
            QiStatusDto qiStatusDto = new QiStatusDto();
            Auser auser = activityMybatisDao.getAuserByMobile(mobile);
            if(null == auser){
            	return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
            }
            qiStatusDto.setStatus(auser.getStatus());
            qiStatusDto.setAuid(auser.getId());
            
            if(uid > 0){//APP过来的
                    //需要判断默认绑定
                    if (auser.getUid() == 0) {//还没有绑定的需要绑定
                        auser.setUid(uid);
                        activityMybatisDao.updateAuser(auser);
                    }
            }
            return Response.success(ResponseStatus.QI_QUERY_SUCCESS.status, ResponseStatus.QI_QUERY_SUCCESS.message, qiStatusDto);
        }else {
            //验证码不正确
            return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }
    }

    @Override
    public Response getActivityInfo(long activityId) {
        QiActivityInfoDto infoDto = new QiActivityInfoDto();
        Aactivity aactivity = activityMybatisDao.getAactivity(activityId);
        if(!StringUtils.isEmpty(aactivity)){
            infoDto.setName(aactivity.getName());
            //第一阶段
            AactivityStage aactivityStage1 = activityMybatisDao.getAactivityStageByStage(activityId ,1);
            if(aactivityStage1 != null){
                infoDto.setStage(aactivityStage1.getStage());
                return Response.success(infoDto);
            }else {
                //不在第一阶段
                return Response.success(ResponseStatus.NOT_SINGLE_STAGE.status,ResponseStatus.NOT_SINGLE_STAGE.message);
            }
        }
        return Response.success(ResponseStatus.QIACTIVITY_NOT_START.status,ResponseStatus.QIACTIVITY_NOT_START.message);
    }

    @Override
    public Response oneKeyAudit() {
        List<Auser> auserList = activityMybatisDao.getAuserList();
        //发短信给每个用户 告知审核通过了
        if(auserList.size() > 0 && auserList != null){
            List mobileList = Lists.newArrayList();
            for(Auser auser : auserList){
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());
            }
            AwardXMDto awardXMDto = new AwardXMDto();
            awardXMDto.setMobileList(mobileList);
            userService.sendQIauditMessage(awardXMDto);
            //修改每个用户未审核通过
            activityMybatisDao.updateAuser();
            log.info("update Auser success");
            return Response.success(ResponseStatus.AWARD_MESSAGE_SUCCESS.status,ResponseStatus.AWARD_MESSAGE_SUCCESS.message);
        }

        return Response.success(ResponseStatus.CANNOT_FIND_AUSER.status,ResponseStatus.CANNOT_FIND_AUSER.message);
    }

    /**
	 * 本方法暂时使用，这个抽奖活动名字以后肯定是用一张表来存储的，暂时先这样，下次有活动的时候再修改
	 * @param i
	 * @return
	 */
	private String getNameFromInt2String(int i){
		if(i == 1){
			return "小米活动";
		}
		return "未知";
	}
	
	/**
	 * 本方法暂时使用，这个抽奖活动奖品名字以后肯定是用一张表来存储的，暂时先这样，下次有活动的时候再修改
	 * @param i
	 * @return
	 */
	private String getAwardNameFromInt2String(int i){
		if(i == 1){
			return "一等奖";
		}else if(i == 2){
			return "二等奖";
		}else if(i == 3){
			return "三等奖";
		}else if(i == 4){
			return "四等奖";
		}else if(i == 5){
			return "五等奖";
		}else if(i == 6){
			return "六等奖";
		}else if(i == 7){
			return "七等奖";
		}else if(i == 8){
			return "八等奖";
		}else if(i == 9){
			return "九等奖";
		}else if(i == 10){
			return "十等奖";
		}
		return "未知";
	}

    /**
     * 随机方法1
     * 主要是：随机0-10之间，如果随机在0-8之间，则随机0-80这个方法，
     * 如果随机8-10，则随机80-100
     * @return
     */
    public static int random1(float big ,float small ,int sum){
        int i=randoms(sum);
        System.out.println("randoms: "+i);
        if(i==0){
            return randomone(small);
        }else{
            return randomtwo(big,small);
        }

    }

    /**
     * 产生随机数0---10
     * 如果当天有300人抽奖 这个改成100完全没问题 如果当天有3000人抽奖 这个改成1000完全没问题 百分之一 千分之一
     */
    public static int randoms(int sum){
        Random random = new Random();
        int i=random.nextInt(sum);//数据库luck_status表award_sum_chance字段控制概率
        return i;
    }
    /**
     * 99%中奖
     * @return
     */
    public static int randomone(float small){
        Random random = new Random();
        //需要判断最后一个数据是否为0,为0的话随机1 因为0不能用nextInt会有异常
        if (small == 0){
            small = 1f;
        }
        int i=random.nextInt((int)small);
        System.out.println("中奖范围: "+i);
        return i;
    }
    /**
     * 肯定不中奖
     * @return
     */
    public static int randomtwo(float big ,float small){
        Random random = new Random();
        if (small == 0){
            small = 1f;
        }
        int i=random.nextInt((int)small);//list最后一个数据 //只能随机到1-XXX +1表示不能为0
        if(i == 0){
            i = 1;
        }
        int j=(int)big-(int)small;//最大值-中奖最小值 这样永远是不中奖几率 前提不中奖数据必须>中奖数据
        return j;
    }

	@Override
	public Response getLuckActStatList(int activityName) {
		List<LuckPrize> pList = activityMybatisDao.getPrizeListByActivityName(activityName);
		Map<String, LuckPrize> pMap = new HashMap<String, LuckPrize>();
		if(null != pList && pList.size() > 0){
			for(LuckPrize lp : pList){
				pMap.put(lp.getAwardId()+"", lp);
			}
		}
		
		ShowLuckActStatDTO slasDTO = new ShowLuckActStatDTO();
		//过去1小时内统计
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		Date lastOneHour = cal.getTime();
		LuckActStatDTO stat = activityMybatisDao.getLuckActStat(activityName, lastOneHour, now);
		List<LuckAct> luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, lastOneHour, now);
		ShowLuckActStatDTO.LuckActStatElement e = new ShowLuckActStatDTO.LuckActStatElement();
		e.setDateStr("1小时内");
		e.setEnterUV(stat.getEnterUV());
		e.setEnterPV(stat.getEnterPV());
		if(null != luckActList && luckActList.size() > 0){
			e.setPrizeNum(luckActList.size());
			e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
		}else{
			e.setPrizeNum(0);
			e.setPrizeNames("");
		}
		slasDTO.getResult().add(e);
		//过去2小时内
		cal.setTime(now);
		cal.add(Calendar.HOUR_OF_DAY, -2);
		Date lastTwoHour = cal.getTime();
		stat = activityMybatisDao.getLuckActStat(activityName, lastTwoHour, now);
		luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, lastTwoHour, now);
		e = new ShowLuckActStatDTO.LuckActStatElement();
		e.setDateStr("2小时内");
		e.setEnterUV(stat.getEnterUV());
		e.setEnterPV(stat.getEnterPV());
		if(null != luckActList && luckActList.size() > 0){
			e.setPrizeNum(luckActList.size());
			e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
		}else{
			e.setPrizeNum(0);
			e.setPrizeNames("");
		}
		slasDTO.getResult().add(e);
		//历史到当前天的按天统计
		//获取所有中奖纪录
		luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, null, null);
		List<LuckActStat2DTO> list2 = activityMybatisDao.getLuckActStat2List(activityName);
		if(null != list2 && list2.size() > 0){
			for(LuckActStat2DTO dto2 : list2){
				e = new ShowLuckActStatDTO.LuckActStatElement();
				e.setDateStr(dto2.getDateStr());
				e.setEnterPV(dto2.getEnterPV());
				e.setEnterUV(dto2.getEnterUV());
				e.setPrizeNum(dto2.getPrizeNum());
				if(dto2.getPrizeNum() > 0){
					e.setPrizeNames(this.getPrizeNames(luckActList, pMap, dto2.getDateStr()));
				}else{
					e.setPrizeNames("");
				}
				slasDTO.getResult().add(e);
			}
		}
		//总计
		stat = activityMybatisDao.getLuckActStat(activityName, null, null);
		e = new ShowLuckActStatDTO.LuckActStatElement();
		e.setDateStr("总计");
		e.setEnterUV(stat.getEnterUV());
		e.setEnterPV(stat.getEnterPV());
		if(null != luckActList && luckActList.size() > 0){
			e.setPrizeNum(luckActList.size());
			e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
		}else{
			e.setPrizeNum(0);
			e.setPrizeNames("");
		}
		slasDTO.getResult().add(e);
		
		return Response.success(slasDTO);
	}
	
	private String getPrizeNames(List<LuckAct> luckActList, Map<String, LuckPrize> pMap, String dateStr){
		List<LuckAct> list = new ArrayList<LuckAct>();
		String date = null;
		for(LuckAct la : luckActList){
			date = DateUtil.date2string(la.getCreatTime(), "yyyy-MM-dd");
			if(date.equals(dateStr)){
				list.add(la);
			}
		}
		
		return this.getPrizeNames(list, pMap);
	}
	
	private String getPrizeNames(List<LuckAct> luckActList, Map<String, LuckPrize> pMap){
		//PrizeName,Num
		Map<String, Integer> map = new HashMap<String, Integer>();
		LuckPrize p = null;
		String pname = null;
		Integer pNum = null;
		for(LuckAct la : luckActList){
			p = pMap.get(String.valueOf(la.getAwardId()));
			if(null != p){
				pname = p.getAwardName();
			}else{
				pname = this.getAwardNameFromInt2String(la.getAwardId());
			}
			if(null != pname && pname.length() > 0){
				pNum = map.get(pname);
				if(null != pNum){
					pNum = Integer.valueOf(pNum+1);
				}else{
					pNum = Integer.valueOf(1);
				}
				map.put(pname, pNum);
			}
		}
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			if(entry.getValue() > 1){
				sb.append(";").append(entry.getKey()).append("X").append(entry.getValue());
			}else{
				sb.append(";").append(entry.getKey());
			}
		}
		String result = sb.toString();
		if(result.length() > 0){
			result = result.substring(1);
		}
		return result;
	}

	@Override
	public Response getAwardStatusList(int activityName) {
		Integer aname = null;
		if(activityName > 0){
			aname = Integer.valueOf(activityName);
		}
		List<LuckStatus> list = activityMybatisDao.getLuckStatusListByName(aname);
		ShowLuckStatusDTO dto = new ShowLuckStatusDTO();
		if(null != list && list.size() > 0){
			LuckStatusDTO e = null;
			for(LuckStatus ls : list){
				e = new LuckStatusDTO();
				e.setActivityName(ls.getActivityName());
				e.setAwardStatus(ls.getAwardStatus());
				e.setAwardSumChance(ls.getAwardSumChance());
				e.setAwardTerm(ls.getAwardTerm());
				e.setChannel(ls.getChannel());
				e.setCreateTime(ls.getCreateTime());
				e.setEndTime(ls.getEndTime());
				e.setId(ls.getId());
				e.setOperateMobile(ls.getOperateMobile());
				e.setStartTime(ls.getStartTime());
				e.setVersion(ls.getVersion());
				e.setActivityNameStr(this.getNameFromInt2String(ls.getActivityName()));
				dto.getResult().add(e);
			}
		}
		
		return Response.success(dto);
	}

	@Override
	public Response getLuckStatusById(int id) {
		LuckStatus ls = activityMybatisDao.getLuckStatusById(id);
		LuckStatusDTO dto = new LuckStatusDTO();
		dto.setActivityName(ls.getActivityName());
		dto.setAwardStatus(ls.getAwardStatus());
		dto.setAwardSumChance(ls.getAwardSumChance());
		dto.setAwardTerm(ls.getAwardTerm());
		dto.setChannel(ls.getChannel());
		dto.setCreateTime(ls.getCreateTime());
		dto.setEndTime(ls.getEndTime());
		dto.setId(ls.getId());
		dto.setOperateMobile(ls.getOperateMobile());
		dto.setStartTime(ls.getStartTime());
		dto.setVersion(ls.getVersion());
		dto.setActivityNameStr(this.getNameFromInt2String(ls.getActivityName()));
		
		return Response.success(dto);
	}

	@Override
	public Response updateLuckStatus(LuckStatusDTO dto) {
		LuckStatus ls = new LuckStatus();
		ls.setActivityName(dto.getActivityName());
		ls.setAwardStatus(dto.getAwardStatus());
		ls.setAwardSumChance(dto.getAwardSumChance());
		ls.setChannel(dto.getChannel());
		ls.setEndTime(dto.getEndTime());
		ls.setId(dto.getId());
		ls.setOperateMobile(dto.getOperateMobile());
		ls.setStartTime(dto.getStartTime());
		ls.setVersion(dto.getVersion());
		activityMybatisDao.updateLuckStatus(ls);
		return Response.success();
	}

	@Override
	public Response getLuckPrizeList(int activityName) {
		List<LuckPrize> list = activityMybatisDao.getPrizeListByActivityName(activityName);
		ShowLuckPrizeDTO dto = new ShowLuckPrizeDTO();
		if(null != list && list.size() > 0){
			ShowLuckPrizeDTO.LuckPrizeElement e = null;
			for(LuckPrize lp : list){
				e = new ShowLuckPrizeDTO.LuckPrizeElement();
				e.setActivityName(lp.getActivityName());
				e.setAwardChance(lp.getAwardChance());
				e.setAwardId(lp.getAwardId());
				e.setAwardName(lp.getAwardName());
				e.setId(lp.getId());
				e.setNumber(lp.getNumber());
				e.setActivityNameStr(this.getNameFromInt2String(lp.getActivityName()));
				dto.getResult().add(e);
			}
		}
		
		return Response.success(dto);
	}

	@Override
	public Response getLuckActList(int activityName, Date startTime,
			Date endTime) {
		List<LuckPrize> pList = activityMybatisDao.getPrizeListByActivityName(activityName);
		Map<String, LuckPrize> pMap = new HashMap<String, LuckPrize>();
		if(null != pList && pList.size() > 0){
			for(LuckPrize lp : pList){
				pMap.put(lp.getAwardId()+"", lp);
			}
		}
		
		ShowLuckActStatDTO slasDTO = new ShowLuckActStatDTO();
		LuckActStatDTO stat = activityMybatisDao.getLuckActStat(activityName, startTime, endTime);
		List<LuckAct> luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, startTime, endTime);
		ShowLuckActStatDTO.LuckActStatElement e = new ShowLuckActStatDTO.LuckActStatElement();
		e.setDateStr("时间段");
		e.setEnterUV(stat.getEnterUV());
		e.setEnterPV(stat.getEnterPV());
		if(null != luckActList && luckActList.size() > 0){
			e.setPrizeNum(luckActList.size());
			e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
		}else{
			e.setPrizeNum(0);
			e.setPrizeNames("");
		}
		slasDTO.getResult().add(e);
		
		return Response.success(slasDTO);
	}

    @Override
    public Atopic getAtopicByTopicId(long topicId) {
        return activityMybatisDao.getAtopicByTopicId(topicId);
    }

    @Override
    public void updateAtopicStatus(Map map) {
        activityMybatisDao.updateAtopicStatus(map);
    }

    @Override
    public Response getAliveInfo(long uid ,String topicName ,String nickName ,int pageNum ,int pageSize) {
        //判断当前是男性还是女性，查询出相反性别
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        Map map = Maps.newHashMap();
        AtopicInfoDto atopicInfoDto = new AtopicInfoDto();
        map.put("titleName",topicName);
        map.put("nickName",nickName);
        if(pageNum != 0){
            pageNum = pageNum*pageSize;
        }
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);

        if(userProfile != null){
            if(userProfile.getGender() == 0) {
                //查询总记录数
                map.put("gender",1);
                int total = activityMybatisDao.getAliveList(map);
                atopicInfoDto.setTotal(total);
                //0女 查询男
                List<BlurSearchDto> boyList = activityMybatisDao.getTopicByBoy(map);
                if(boyList.size()>0 && boyList != null){
                    for(BlurSearchDto blurSearchDto : boyList){
                        atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                    }
                    return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message,atopicInfoDto);
                }

            }else{
                map.put("gender",0);
                int total = activityMybatisDao.getAliveList(map);
                atopicInfoDto.setTotal(total);
                //1男 //查询女
                List<BlurSearchDto> girlList = activityMybatisDao.getTopicByGirl(map);
                if(girlList.size()>0 && girlList != null){
                    for(BlurSearchDto blurSearchDto : girlList){
                        atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                    }
                    return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message,atopicInfoDto);
                }
            }
        }
        return Response.success(ResponseStatus.SEARCH_ATOPIC_FAILURE.status, ResponseStatus.SEARCH_ATOPIC_FAILURE.message,atopicInfoDto);
    }

    @Override
    public Response createDoubleLive(long uid, long targetUid ,long activityId) {
        AactivityStage aactivityStage4 = activityMybatisDao.getAactivityStageByStage(activityId,4);
        if(aactivityStage4 != null){
            //单人王国
            Atopic ownerTopicSingle = activityMybatisDao.getAtopicByUid1(uid);
            Atopic targetTopicSingle = activityMybatisDao.getAtopicByUid1(targetUid);
            //双人王国
            Atopic ownerTopicDouble = activityMybatisDao.getAtopicByUid2(uid);
            Atopic targetTopicDouble = activityMybatisDao.getAtopicByUid2(targetUid);
            //申请次数 长度<5 为5次
            List<AdoubleTopicApply> applyOwner = activityMybatisDao.getTopicApply(uid);
            //满足只建立了单人王国，都没建立双人王国
            if(ownerTopicSingle != null && targetTopicSingle != null && ownerTopicDouble ==null && targetTopicDouble == null){
                //申请次数
                String num = cacheService.get(SEVENDAY_KEY);
                if(applyOwner.size()<Integer.parseInt(num)){
                    //请求
                    AdoubleTopicApply applyReq = new AdoubleTopicApply();
                    applyReq.setUid(uid);
                    applyReq.setTargetUid(targetUid);
                    //配对类型为1
                    applyReq.setType(1);
                    activityMybatisDao.createAdoubleTopicApply(applyReq);
                    return Response.success(ResponseStatus.APPLICATION_SUCCESS.status, ResponseStatus.APPLICATION_SUCCESS.message);
                }else{
                    return Response.success(ResponseStatus.NUMBER_IS_BOUND.status, ResponseStatus.NUMBER_IS_BOUND.message);
                }
            }
        }
        return Response.success(ResponseStatus.NOT_THREE_STAGE.status, ResponseStatus.NOT_THREE_STAGE.message);
    }

    @Override
    public Response getApplyInfo(long uid, int type ,int pageNum ,int pageSize) {
        ApplyListDto applyListDto = new ApplyListDto();
        if(type == 0){
            //我发出的 不包含删除
            List<AdoubleTopicApply> sendList = activityMybatisDao.getAdoubleTopicApplyByUid(uid);
            //我接收到的 包含删除的
            List<AdoubleTopicApply> receiveList = activityMybatisDao.getAdoubleTopicApplyByUidReceive(uid ,pageNum ,pageSize);
            //我接收到的总条数
            int total = activityMybatisDao.getReceiveList(uid);
            applyListDto.setTotal(total);
            //我同意的
            List<AdoubleTopicApply> agreeList = activityMybatisDao.getAdoubleTopicApplyByUidAgree(uid);
            //我发出去的<=5
            if(sendList.size() > 0 && sendList != null){
                List<ApplyListDto.ApplyElement> lists = applyListDto.getSendList();
                for(AdoubleTopicApply apply : sendList){
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getTargetUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile,applyElement);
                    applyElement.setId(apply.getId());
                    applyElement.setStatus(apply.getStatus());
                    lists.add(applyElement);
                }
            }
            //接收到可能很多需要分页
            if(receiveList.size() > 0 && receiveList != null){
                List<ApplyListDto.ApplyElement> lists = applyListDto.getReceiveList();
                for(AdoubleTopicApply apply : receiveList){
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile,applyElement);
                    applyElement.setId(apply.getId());
                    applyElement.setStatus(apply.getStatus());
                    lists.add(applyElement);
                }
            }
            //我同意的<=1
            if(agreeList.size() > 0 && agreeList != null){
                List<ApplyListDto.ApplyElement> lists = applyListDto.getAgreeList();
                for(AdoubleTopicApply apply : agreeList){
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getTargetUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile,applyElement);
                    applyElement.setId(apply.getId());
                    applyElement.setStatus(apply.getStatus());
                    lists.add(applyElement);
                }
            }
            return Response.success(ResponseStatus.APPLY_LIST_SUCCESS.status, ResponseStatus.APPLY_LIST_SUCCESS.message,applyListDto);
        }
        return null;
    }

    @Override
    public Response applyDoubleLive(long uid, int applyId ,int operaStatus) {
        //查询自己同意的条数 只能一条 ，接收方查询是targetUid
        List<AdoubleTopicApply> lists = activityMybatisDao.getAdoubleTopicApplyByUid2(uid);
        //2同意，3拒绝，4删除
        if(operaStatus ==2){
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            if(lists.size() < 1) {
                //同意时，需要判断对方是否已经创建了双人王国，如果已经创建了，则无法同意了。
                if (topicApply != null) {
                    //查看对方是否有双人王国
                    Atopic atopic = activityMybatisDao.getAtopicByAuidDoubleByUid(topicApply.getUid());
                    if (atopic == null) {
                        topicApply.setStatus(operaStatus);
                        activityMybatisDao.updateAdoubleTopicApply(topicApply);
                    } else {
                        return Response.success(ResponseStatus.TARGET_CREATE_TOPIC.status, ResponseStatus.TARGET_CREATE_TOPIC.message);
                    }
                }
            }else{
                return Response.success(ResponseStatus.ONLY_AGREE_ONE_PEOPLE.status, ResponseStatus.ONLY_AGREE_ONE_PEOPLE.message);
            }
        }else if(operaStatus ==3){
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            if(topicApply != null){
                topicApply.setStatus(operaStatus);
                activityMybatisDao.updateAdoubleTopicApply(topicApply);
            }
        } else if(operaStatus ==4){
            //删除需要符合条件的才能删除，首先必须是自己发出的申请，并且对方还没有同意的申请才能删除，
            // 或者对方同意了但是已经和别人创建 双人王国了也能删除。
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            Atopic atopic = activityMybatisDao.getAtopicByAuidDoubleByUid(topicApply.getUid());
            if((topicApply.getUid() == uid && topicApply.getStatus() ==1) ||
                    (topicApply.getStatus() ==2 && atopic != null)){
                topicApply.setStatus(operaStatus);
                activityMybatisDao.updateAdoubleTopicApply(topicApply);
            }else {
                return Response.success(ResponseStatus.CANT_DELETE.status, ResponseStatus.CANT_DELETE.message);
            }
        }
        return Response.success(ResponseStatus.UPDATE_STATE_SUCCESS.status, ResponseStatus.UPDATE_STATE_SUCCESS.message);
    }

    @Override
    public Response bridApply(long uid, long targetUid) {
        Atopic ownerTopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid ,2);
        Atopic targetTopic = activityMybatisDao.getAtopicByUidandTypeBrid(targetUid ,2);
        String bridKey = cacheService.get(BRID_KEY);
        if(bridKey != null) {
            //申请人没有双人王国，接收人有双人王国，才能抢亲 只能5次
            if (ownerTopic == null && targetTopic != null && Integer.parseInt(bridKey) < 5) {
                AdoubleTopicApply apply = new AdoubleTopicApply();
                apply.setType(2);//2是抢亲
                apply.setUid(uid);
                apply.setTargetUid(targetUid);
                activityMybatisDao.createAdoubleTopicApply(apply);
                log.info("brid success");
                return Response.success(ResponseStatus.APPLY_BRID_SUCCESS.status, ResponseStatus.APPLY_BRID_SUCCESS.message);
            }
        }
        return Response.success(ResponseStatus.CANT_APPLY_BRID.status, ResponseStatus.CANT_APPLY_BRID.message);
    }

    @Override
    public Response bridSearch(long uid) {
        List<AdoubleTopicApply> applyList = activityMybatisDao.getAdoubleTopicApplyByUidandTypeBrid(uid,2);
        BridListDto bridListDto = new BridListDto();
        List<BridListDto.ApplyElement> lists = bridListDto.getBridList();
        if(applyList.size() > 0 && applyList != null){
            for(AdoubleTopicApply topicApply:applyList){
                BridListDto.ApplyElement applyElement = bridListDto.createApplyElement();
                UserProfile userProfile = userService.getUserProfileByUid(topicApply.getTargetUid());
                BeanUtils.copyProperties(userProfile,applyElement);
                applyElement.setId(topicApply.getId());
                applyElement.setStatus(topicApply.getStatus());
                lists.add(applyElement);
            }
            return Response.success(ResponseStatus.BRID_GET_LIST_SUCCESS.status, ResponseStatus.BRID_GET_LIST_SUCCESS.message,bridListDto);
        }
        return Response.success(ResponseStatus.BRID_GET_LIST_FAILURE.status, ResponseStatus.BRID_GET_LIST_FAILURE.message);
    }

    /**
     * 乱七八糟的逻辑。。一个巨大的深坑。。后来人注意
     */
	@Override
	public Response genActivity7DayMiliList(Activity7DayMiliDTO dto) {
		Show7DayMiliDTO respDTO = new Show7DayMiliDTO();
		
		//一次性获取所有活动米粒语料（不在后面每次获取）
		Map<String, List<AmiliData>> miliMap = new HashMap<String, List<AmiliData>>();
		List<AmiliData> allMiliDatas = activityMybatisDao.getAllAmiliData();
		if(null != allMiliDatas && allMiliDatas.size() > 0){
			List<AmiliData> list = null;
			for(AmiliData data : allMiliDatas){
				list = miliMap.get(data.getKey());
				if(null == list){
					list = new ArrayList<AmiliData>();
					miliMap.put(data.getKey(), list);
				}
				list.add(data);
			}
		}
		
		if(miliMap.size() == 0){
			return Response.success(respDTO);
		}
		
		//1 公共部分
		//1.1 进入模板语料
		this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ENTER_COMMON.key, null);
		//1.2 是否首次进入
		this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FIRST_ENTER.key, null);
		//1.3 活动介绍和状态
		this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_INFO.key, null);
		//1.4 下载链接等
		this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.APP_DOWNLOAD.key, null);
		//1.5 系统运营文章
		this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SYSTEM_ARTICLE.key, null);
		
		
		Map<String, AactivityStage> stageMap = new HashMap<String, AactivityStage>();
		List<AactivityStage> allStages = activityMybatisDao.getAactivityStage(1);//7天活动
		if(null != allStages && allStages.size() > 0){
			for(AactivityStage s : allStages){
				stageMap.put(String.valueOf(s.getStage()), s);
			}
		}
		
		List<String> params = null;
		Auser activityUser = null;
		Atopic singleKingdom = null;
		Atopic doubleKingdom = null;
		if(dto.getAuid() > 0){
			activityUser = activityMybatisDao.getAuser(dto.getAuid());
			if(null != activityUser && activityUser.getUid() > 0){
				singleKingdom = activityMybatisDao.getAtopicByAuidAndSingle(dto.getAuid());
				if(null != singleKingdom){
					doubleKingdom = activityMybatisDao.getAtopicByAuidDouble(dto.getAuid());
				}
			}
		}
		
		Date now = new Date();
		
		//2 报名阶段
		AactivityStage stage1 = stageMap.get("1");
		AactivityStage stage2 = stageMap.get("2");
		boolean isCheckStage1 = false;
		if(null != stage1 && stage1.getType() == 0){
			isCheckStage1 = true;
			if(null != activityUser){
				if(activityUser.getStatus() == 1){//审核中
					this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_1.key, null);
				}else if(activityUser.getStatus() == 3){
					if(null == singleKingdom){
						if(dto.getIsApp() == 1){//APP内
							this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, null);
						}else{
							this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, null);
						}
					}
				}
			}else{
				if(dto.getIsApp() == 1){//APP内
					this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_0_APP.key, null);
				}else{
					this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_0_BROWSER.key, null);
				}
			}
			if(null != stage2){
				if(now.compareTo(stage2.getStartTime()) < 0){
					long dayNum = DateUtil.getDaysBetween2Date(now, stage2.getStartTime());
					if(dayNum > 0){
						params = new ArrayList<String>();
						params.add(String.valueOf(dayNum));
						this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_COUNTDOWN.key, params);
					}
				}
			}
		}
		
		//3 单人阶段
		if(null != stage2 && stage2.getType() == 0){
			if(!isCheckStage1){
				if(null != activityUser && activityUser.getStatus() == 3 && null == singleKingdom){
					if(dto.getIsApp() == 1){//APP内
						this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, null);
					}else{
						this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, null);
					}
				}
			}
			if(dto.getIsApp() == 1){//APP内才有的消息
				if(null != singleKingdom){
					Map<String,Object> singleTopic = null;
				}
					
			}
		}
		
		
		
		return Response.success(respDTO);
	}
	
	private void genMili(Show7DayMiliDTO respDTO, Map<String, List<AmiliData>> miliMap, String key, List<String> params){
		List<AmiliData> miliList = miliMap.get(key);
		if(null != miliList && miliList.size() > 0){
			Show7DayMiliDTO.MiliElement e = null;
			for(AmiliData m : miliList){
				e = new Show7DayMiliDTO.MiliElement();
				e.setContent(replaceMiliData(m.getContent(), key, params));
				e.setLinkUrl(m.getLinkUrl());
				e.setOrder(m.getOrderby());
				e.setType(m.getType());
				respDTO.getResult().add(e);
			}
		}
	}
	
	private String replaceMiliData(String content, String key, List<String> params){
		if(null == params || params.size() == 0){
			return content;
		}
		
		return content;
	}

}
