package com.me2me.activity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dao.LiveForActivityDao;
import com.me2me.activity.dto.*;
import com.me2me.activity.event.LinkPushEvent;
import com.me2me.activity.event.TaskPushEvent;
import com.me2me.activity.model.*;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.EncryUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.service.SmsService;
import com.me2me.user.dto.ActivityModelDto;
import com.me2me.user.model.User;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private SmsService smsService;

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Value("#{app.activity_web_url}")
    private String activityWebUrl;

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
    public void createActivityLive(CreateActivityDto createActivityDto) {
        ActivityWithBLOBs activity = new ActivityWithBLOBs();
        activity.setActivityCover(createActivityDto.getCover());
        activity.setActivityHashTitle(createActivityDto.getHashTitle());
        activity.setActivityTitle(createActivityDto.getTitle());
        activity.setStartTime(createActivityDto.getStartTime());
        activity.setEndTime(createActivityDto.getEndTime());
        activity.setIssue(createActivityDto.getIssue());
        activity.setActivityContent(createActivityDto.getContent());
        activity.setUid(createActivityDto.getUid());
        activity.setStatus(Specification.ActivityStatus.NORMAL.index);
        activity.setCid(createActivityDto.getCid());
        activity.setTyp(createActivityDto.getType());
        activityMybatisDao.saveActivity(activity);
    }

    @Override
    public Response showActivity(int page, int pageSize, String keyword) {
        log.info("show activity ... start ");
        ShowActivityDto showActivityDto = new ShowActivityDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.showActivity(page, pageSize, keyword);
        for (ActivityWithBLOBs activity : list) {
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
            element.setActivityCover(Constant.QINIU_DOMAIN + "/" + activity.getActivityNoticeCover());
            showActivityDto.getResult().add(element);
        }
        showActivityDto.setTotal(activityMybatisDao.total(keyword));
        int totalPage = (activityMybatisDao.total(keyword) + pageSize - 1) / pageSize;
        showActivityDto.setTotalPage(totalPage);
        log.info("show activity ... end ");
        return Response.success(showActivityDto);
    }

    @Override
    public List<ActivityWithBLOBs> getActivityTop5() {
        return activityMybatisDao.getActivityTop5();
    }
    
    @Override
    public List<ActivityWithBLOBs> getHotActivity() {
        return activityMybatisDao.getHotActivity();
    }

    @Override
    public Response getActivity(int sinceId, long uid, int vflag) {
        log.info("getActivity start ...");
        ShowActivitiesDto showActivitiesDto = new ShowActivitiesDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.getActivity(sinceId);
        log.info("getActivity data success");
        for (ActivityWithBLOBs activity : list) {
            ShowActivitiesDto.ActivityElement activityElement = ShowActivitiesDto.createActivityElement();
            if (activity.getTyp() == 2) {
                //王国活动
                activityElement.setTopicId(activity.getCid());
                Map<String, Object> contentMap = liveForActivityDao.getContentByForwordCid(activity.getCid());
                if(null != contentMap){
                	activityElement.setCid((Long) contentMap.get("id"));
                }
                Map<String, Object> topicMap = liveForActivityDao.getTopicById(activity.getCid());
                if(null != topicMap){
	                activityElement.setTopicType((Integer) topicMap.get("type"));
	                activityElement.setTopicInternalStatus(this.getInternalStatus(topicMap, uid));
                }
            }
            activityElement.setUid(activity.getUid());
            activityElement.setTitle(activity.getActivityHashTitle());
            String cover = activity.getActivityCover();
            if (!StringUtils.isEmpty(cover)) {
                activityElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            UserProfile userProfile = userService.getUserProfileByUid(activity.getUid());
            activityElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            activityElement.setNickName(userProfile.getNickName());
            activityElement.setId(activity.getId());
            activityElement.setUpdateTime(activity.getUpdateTime());
            activityElement.setIsFollowed(userService.isFollow(activity.getUid(), uid));
            if(vflag < 1){
            	if(activity.getTyp() > 1){
            		activityElement.setContentType(0);
            	}else{
            		activityElement.setContentType(activity.getTyp());
            	}
            }else{
            	activityElement.setContentType(activity.getTyp());
            }
            activityElement.setContentUrl(activity.getLinkUrl());
            activityElement.setType(4);
            activityElement.setLinkUrl("");
            showActivitiesDto.getActivityData().add(activityElement);
        }
        log.info("getActivity end ...");
        return Response.success(showActivitiesDto);
    }

    //判断核心圈身份
    private int getInternalStatus(Map<String, Object> topic, long uid) {
        int internalStatus = 0;
        String coreCircle = (String)topic.get("core_circle");
        if(null != coreCircle){
            JSONArray array = JSON.parseArray(coreCircle);
            for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i) == uid) {
                    internalStatus = Specification.SnsCircle.CORE.index;
                    break;
                }
            }
        }

        return internalStatus;
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
     *
     * @param content
     */
    @Override
    public void joinActivity(String content, long uid) {
        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
        Matcher matcher = pattern.matcher(content);
        boolean result = matcher.matches();
        if (result) {
            String hashTitle = matcher.group(2);
            // 获取hash title
            ActivityWithBLOBs activityWithBLOBs = activityMybatisDao.getActivityByHashTitle(hashTitle);
            if (activityWithBLOBs == null) {
                return;
            }
            // 判断当前活动是否过期
            if (activityMybatisDao.isEnd(activityWithBLOBs.getId())) {
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
        if (activityWithBLOBs == null) {
            return null;
        }
        int internalStatus = activityWithBLOBs.getInternalStatus();
        if (internalStatus == Specification.ActivityInternalStatus.NOTICED.index) {
            activityH5Dto.setActivityContent(activityWithBLOBs.getActivityResult());
            activityH5Dto.setTitle(activityWithBLOBs.getActivityNoticeTitle());
            activityH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + activityWithBLOBs.getActivityNoticeCover());
        } else {
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
    public void createActivityReview(long id, long uid, String review, long atUid) {
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
    public Response luckAward(long uid, String ip, int activityName, String channel, String version) {

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
        if (luckStatus != null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate = luckStatus.getEndTime();//运维后台设置的活动结束时间
        } else {
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }

        //用户信息(在活动时间内的用户)
        User user = userService.getUserByUidAndTime(uid, startDate, endDate);

        Date lastDay = new Date();
        //如果是活动最后一天
        Boolean isTrue = isSameDate(lastDay, endDate);
        if (isTrue) {
            if (luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                    && luckStatus.getVersion().equals(version)
                    && user != null && nowDate.compareTo(startDate) > 0
                    && nowDate.compareTo(endDate) < 0) {
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
                        return awardCommonLastDay(uid, user, luckacts, luckCount2, luck, userProfile, ip, activityName);
                    } else {
                        //否则返回错误信息 今天次数已经用完
                        return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                    }
                }
                //有抽奖次数才能继续往下执行
                if (luckCount2.getNum() > 0) {
                    return awardCommonLastDay(uid, user, luckacts, luckCount2, luck, userProfile, ip, activityName);
                } else {
                    //否则返回错误信息 今天次数已经用完
                    return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
                }
            } else if (nowDate.compareTo(startDate) < 0) {
                return Response.success(ResponseStatus.AWARD_ISNOT_START.status, ResponseStatus.AWARD_ISNOT_START.message);
            } else if (nowDate.compareTo(endDate) > 0) {
                //返回活动已结束
                return Response.success(ResponseStatus.AWARD_IS_END.status, ResponseStatus.AWARD_IS_END.message);
            } else if (!luckStatus.getChannel().equals(channel) || user == null || !luckStatus.getVersion().equals(version)) {
                //不具备抽奖条件
                return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status, ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
            }
            //awardStatus=0的情况下返回未开始
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }

        //如果当日奖品已经发出去4个了就不能再发了 否则继续发放奖品
        //还有个逻辑就是  如果是活动最后一天了直接送奖品出去
        Date newDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(newDate);
        String startDate1 = start + " 00:00:00";
        String endDate1 = start + " 23:59:59";
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sim.parse(startDate1);
            d2 = sim.parse(endDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //判断活动是否在活动开始时间内 渠道是否是小米 是否是活动时间内注册的用户 在的话可以继续参加活动 否则直接返回活动过期或者别的提示
        if (luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                && luckStatus.getVersion().equals(version)
                && user != null && nowDate.compareTo(startDate) > 0
                && nowDate.compareTo(endDate) < 0) {

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

            List<LuckAct> acts = activityMybatisDao.getLuckActByToday(d1, d2);
            //如果是当天 并且当天发出的奖品不能超过4次 才能中奖
            if (isToday(date) && acts.size() < 4) {
                if (luckCount2.getNum() > 0) {
                    //处理抽奖通用方法
                    return awardCommon(uid, user, luckacts, luckCount2, luck, userProfile, ip, activityName, luckStatus);
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
                    return awardCommon(uid, user, luckacts, luckCount2, luck, userProfile, ip, activityName, luckStatus);
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
            if (luckCount2.getNum() == 0) {
                return Response.success(ResponseStatus.RUN_OUT_OF_LOTTERY.status, ResponseStatus.RUN_OUT_OF_LOTTERY.message);
            }
            log.info("用户：" + user.getUserName() + " 获得了 " + award2.id + "等奖");
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
        } else if (nowDate.compareTo(startDate) < 0) {
            //两个Date类型的变量可以通过compareTo方法来比较。此方法的描述是这样的：如果参数 Date 等于此 Date，则返回值 0；
            // 如果此 Date 在 Date 参数之前，则返回小于 0 的值；如果此 Date 在 Date 参数之后，则返回大于 0 的值。
            //返回活动还未开始
            return Response.success(ResponseStatus.AWARD_ISNOT_START.status, ResponseStatus.AWARD_ISNOT_START.message);
        } else if (nowDate.compareTo(endDate) > 0) {
            //返回活动已结束
            return Response.success(ResponseStatus.AWARD_IS_END.status, ResponseStatus.AWARD_IS_END.message);
        } else if (!luckStatus.getChannel().equals(channel) || user == null || !luckStatus.getVersion().equals(version)) {
            //不具备抽奖条件
            return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status, ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
        }
        //awardStatus=0的情况下返回未开始
        return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
    }

    @Override
    public Response getAwardCount(long uid) {
        AwardDto awardDto = new AwardDto();
        LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
        if (luckCount != null && isToday(luckCount.getCreatTime())) {
            //用户存在并且是当天直接查询抽奖次数返回
            awardDto.setAwardCount(luckCount.getNum());
            log.info("get award count successs");
            return Response.success(awardDto);
        } else {
            //否则用户不存在或者不是当天都设置为初始值3次
            awardDto.setAwardCount(3);
            log.info("if not user set user award count 3");
            return Response.success(awardDto);
        }
    }

    @Override
    public Response awardShare(long uid, int activityName) {
        LuckCount luckCount = activityMybatisDao.getLuckCountByUid(uid);
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //活动开始和结束时间
        Date startDate = null;
        Date endDate = null;
        Date nowDate = new Date();
        if (luckStatus != null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate = luckStatus.getEndTime();//运维后台设置的活动结束时间
        } else {
            log.info("award activity is not exists");
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
        if (luckCount != null && nowDate.compareTo(startDate) > 0 && nowDate.compareTo(endDate) < 0) {
            //如果luckCount不等于空的话，说明肯定满足了所有抽奖条件了这个用户，不然不会存在这个表里
            luckCount.setNum(luckCount.getNum() + 1);
            activityMybatisDao.updateLuckCount(luckCount);
            log.info("award share success num + 1");
            return Response.success(ResponseStatus.AWARD_SHARE_SUCCESS.status, ResponseStatus.AWARD_SHARE_SUCCESS.message);
        } else {
            log.info("do not meet the conditions");
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
    }

    @Override
    public Response checkIsAward(long uid, int activityName, String channel, String version, String token) {
        //根据前台传来的活动id获取活动信息
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        //活动开始和结束时间
        Date startDate = null;
        Date endDate = null;
        Date nowDate = new Date();
        if (luckStatus != null) {
            startDate = luckStatus.getStartTime();//运维后台设置的活动开始时间
            endDate = luckStatus.getEndTime();//运维后台设置的活动结束时间
        } else {
            return Response.success(ResponseStatus.AWARD_ISNOT_EXISTS.status, ResponseStatus.AWARD_ISNOT_EXISTS.message);
        }
        //用户信息(在活动时间内的用户)
        User user = userService.getUserByUidAndTime(uid, startDate, endDate);
        //判断活动是否在活动开始时间内 渠道是否是小米 是否是活动时间内注册的用户 在的话可以继续参加活动 否则直接返回活动过期或者别的提示
        if (luckStatus.getAwardStatus() == 1 && luckStatus.getChannel().equals(channel)
                && luckStatus.getVersion().equals(version)
                && user != null && nowDate.compareTo(startDate) > 0
                && nowDate.compareTo(endDate) < 0) {
            log.info("meet the conditions you can award");
            ActivityModelDto activityModelDto = new ActivityModelDto();
            //DES加密
            String secruityUid = EncryUtil.encrypt(Long.toString(uid));
            String secruityToken = EncryUtil.encrypt(token);
            System.out.println("加密后secruityUid：" + secruityUid + " secruityToken：" + secruityToken);

            activityModelDto.setActivityUrl("http://webapp.me-to-me.com/web/lottery/awardJoin?uid=" + secruityUid + "&&token=" + secruityToken);
            log.info("get awardurl success");
            return Response.success(ResponseStatus.APPEASE_AWARD_TERM.status, ResponseStatus.APPEASE_AWARD_TERM.message, activityModelDto);
        } else {
            return Response.success(ResponseStatus.APPEASE_NOT_AWARD_TERM.status, ResponseStatus.APPEASE_NOT_AWARD_TERM.message);
        }
    }

    @Override
    public Response getUserAwardInfo(long uid) {
        LuckAct luckAct = activityMybatisDao.getLuckActByAwardId2(uid);
        AwardDto awardDto = new AwardDto();
        if (luckAct != null) {
            int awardId = luckAct.getAwardId();
            LuckPrize luckPrize = activityMybatisDao.getPrizeByAwardId(awardId);
            awardDto.setId(awardId);
            awardDto.setAwardName(luckPrize.getAwardName());
            awardDto.setProof(luckAct.getProof());
            log.info("get user award info success");
            return Response.success(ResponseStatus.USER_AWARD_INFO.status, ResponseStatus.USER_AWARD_INFO.message, awardDto);
        } else {
            return Response.success(ResponseStatus.USER_AWARD_NOT_INFO.status, ResponseStatus.USER_AWARD_NOT_INFO.message);
        }
    }

    @Override
    public Response getAwardStatus(int activityName) {
        LuckStatus luckStatus = activityMybatisDao.getLuckStatusByName(activityName);
        AwardStatusDto awardStatusDto = new AwardStatusDto();
        if (luckStatus != null) {
            awardStatusDto.setActivityName(luckStatus.getActivityName());
            awardStatusDto.setChannel(luckStatus.getChannel());
            awardStatusDto.setVersion(luckStatus.getVersion());
        }
        return Response.success(awardStatusDto);
    }

    public Response awardCommon(long uid, User user, List<LuckAct> luckacts, LuckCount luckCount2, LuckAct luck, UserProfile userProfile, String ip, int activityName, LuckStatus luckStatus) {
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
            log.info("用户：" + user.getUserName() + " 获得了 " + award2.id + "等奖");
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
                AwardDto award = lottery(awards, sum);
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
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
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
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
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
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
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
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
                } else if (award.id == 5) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(award.id);
                    award.setAwardName(prize.getAwardName());
                } else {//没中奖的话award_id为空
                    luckAct.setAwardId(0);
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    //System.out.println("恭喜您，抽到了：" + award.id);
                    log.info("用户：" + user.getUserName() + " 获得了 " + award.id + "等奖");
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
            } else {
                //每次抽奖机会-1
                luckCount2.setNum(luckCount2.getNum() - 1);
                activityMybatisDao.updateLuckCount(luckCount2);
                //日志记录中奖过了，返回前台还是抽奖的信息，但是不会中奖
                log.info("用户：" + user.getUserName() + " 已经中奖过了，不能继续中奖");
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

    public Response awardCommonLastDay(long uid, User user, List<LuckAct> luckacts, LuckCount luckCount2, LuckAct luck,
                                       UserProfile userProfile, String ip, int activityName) {
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
            log.info("用户：" + user.getUserName() + " 获得了 " + award2.id + "等奖");
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
                if (prize5.size() > 0) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 5等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(5);
                    award.setAwardName(prize.getAwardName());
                    award.setId(5);
                } else if (prize4.size() > 0) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 4等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(4);
                    award.setAwardName(prize.getAwardName());
                    award.setId(4);
                } else if (prize3.size() > 0) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 3等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(3);
                    award.setAwardName(prize.getAwardName());
                    award.setId(3);
                } else if (prize2.size() > 0) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 2等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(2);
                    award.setAwardName(prize.getAwardName());
                    award.setId(2);
                } else if (prize1.size() > 0) {
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
                    log.info("用户：" + user.getUserName() + " 获得了 1等奖");
                    LuckPrize prize = activityMybatisDao.getPrizeByAwardId(1);
                    award.setAwardName(prize.getAwardName());
                    award.setId(1);
                } else {
                    luckAct.setAwardId(0);
                    activityMybatisDao.createLuckAct(luckAct);
                    //每次抽奖机会-1
                    luckCount2.setNum(luckCount2.getNum() - 1);
                    activityMybatisDao.updateLuckCount(luckCount2);
                    log.info("用户：" + user.getUserName() + " 获得了 6等奖");
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
            } else {
                //每次抽奖机会-1
                luckCount2.setNum(luckCount2.getNum() - 1);
                activityMybatisDao.updateLuckCount(luckCount2);
                //日志记录中奖过了，返回前台还是抽奖的信息，但是不会中奖
                log.info("用户：" + user.getUserName() + " 已经中奖过了，不能继续中奖");
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

    public static AwardDto lottery(List<AwardDto> awards, int sum) {
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
        float randomPro = (float) random1(proSection.get(proSection.size() - 1), proSection.get(proSection.size() - 2), sum);
        //判断取到的随机数在哪个奖品的概率区间中
        for (int i = 0, size = proSection.size(); i < size; i++) {
            if (randomPro >= proSection.get(i)
                    && randomPro < proSection.get(i + 1)) {
                System.out.println("下标:" + i);
                return awards.get(i);
            }
        }
        return null;
    }

    //不中奖的情况调用此方法
    public static AwardDto lotteryLastDay(List<AwardDto> awards) {
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
        float randomPro = (float) random.nextInt((int) totalPro);
        //判断取到的随机数在哪个奖品的概率区间中
        for (int i = 0, size = proSection.size(); i < size; i++) {
            if (randomPro >= proSection.get(i)
                    && randomPro < proSection.get(i + 1)) {
                log.info("award info randomPro: " + randomPro + " proSection.get(i): " + proSection.get(i) + " i: " + i + " 总数:" + proSection.get(proSection.size() - 1));
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
        int month1 = c1.get(Calendar.MONTH) + 1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2) {
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

    public void setStartEndTime(String startDate, String endDate) {
        Date newDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(newDate);
        startDate = start + " 00:00:00";
        endDate = start + " 23:59:59";
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
        if (null != list && list.size() > 0) {
            List<LuckPrize> pList = activityMybatisDao.getPrizeListByActivityName(activityName);
            Map<String, LuckPrize> pMap = new HashMap<String, LuckPrize>();
            if (null != pList && pList.size() > 0) {
                for (LuckPrize lp : pList) {
                    pMap.put(lp.getAwardId() + "", lp);
                }
            }
            LuckPrize p = null;
            for (LuckAct la : list) {
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
                if (la.getAwardId() > 0) {
                    p = pMap.get(la.getAwardId() + "");
                    if (null != p) {
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
        if (null != list && list.size() > 0) {
            for (LuckWinners w : list) {
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
    public Response addWinners(long uid, int activityName, String mobile, int awardId, String awardName) {
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
        QiActivityDto qiActivityDto = new QiActivityDto();
        Auser auser = activityMybatisDao.getAuserByUid(uid);
        if (null != auser) {
            qiActivityDto.setAuid(auser.getId());
            qiActivityDto.setIsBind(1);
            qiActivityDto.setUserStatus(auser.getStatus());
            qiActivityDto.setMobile(auser.getMobile());
            if (auser.getStatus() == 3) {
                Atopic atopicSingle = activityMybatisDao.getAtopicByAuidAndSingle(auser.getId());
                boolean hasSingle = false;
                if (null != atopicSingle) {
                    Map<String, Object> topicSingle = liveForActivityDao.getTopicById(atopicSingle.getTopicId());
                    if (null != topicSingle && topicSingle.size() > 0) {
                        QiActivityDto.TopicElement topicElement = qiActivityDto.createElement();
                        topicElement.setLiveImage(Constant.QINIU_DOMAIN_COMMON + "/" + (String) topicSingle.get("live_image"));
                        topicElement.setTitle((String) topicSingle.get("title"));
                        topicElement.setTopicId((Long) topicSingle.get("id"));
                        topicElement.setStage(Specification.ASevenDayType.A_DOUBLE_STAGE.index);
                        topicElement.setHot(atopicSingle.getHot());
                        qiActivityDto.getTopicList().add(topicElement);
                        hasSingle = true;
                    }
                }
                if (!hasSingle) {
                    QiActivityDto.TopicElement topicElement = qiActivityDto.createElement();
                    topicElement.setTopicId(null);
                    qiActivityDto.getTopicList().add(topicElement);
                }
                Atopic atopicDouble = activityMybatisDao.getAtopicByAuidDouble(auser.getId());
                if (null != atopicDouble) {
                    Map<String, Object> topicDouble = liveForActivityDao.getTopicById(atopicDouble.getTopicId());
                    if (null != topicDouble && topicDouble.size() > 0) {
                        QiActivityDto.TopicElement topicElement = qiActivityDto.createElement();
                        topicElement = qiActivityDto.createElement();
                        topicElement.setLiveImage(Constant.QINIU_DOMAIN_COMMON + "/" + (String) topicDouble.get("live_image"));
                        topicElement.setTitle((String) topicDouble.get("title"));
                        topicElement.setTopicId((Long) topicDouble.get("id"));
                        topicElement.setStage(Specification.ASevenDayType.A_THREE_STAGE.index);
                        topicElement.setHot(atopicDouble.getHot());
                        qiActivityDto.getTopicList().add(topicElement);
                    }
                }
            }
        } else {//没绑过
            qiActivityDto.setIsBind(0);
        }

        return Response.success(qiActivityDto);
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
        if (null == aa || null == aa.getStartTime() || null == aa.getEndTime()
                || aa.getStartTime().getTime() > now.getTime()
                || aa.getEndTime().getTime() < now.getTime()) {
            log.info("now is out of activity!");
            return Response.failure("不在活动期");
        }
        //1先判断当前用户是否为我们活动报名用户，并审核通过
        Auser auser = this.getAuserByUid(uid);
        if (null == auser) {
            log.info("uid[" + uid + "] is not activity user!");
            return Response.failure("你未报名");
        }
        if (auser.getStatus() != 3) {
            log.info("uid[" + uid + "] is not audit success!");
            return Response.failure("你未审核通过");
        }
        if (type == Specification.ActivityKingdomType.SINGLEKING.index) {//单人王国
            Atopic singleKingdom = this.getAtopicByUidAndType(uid, type);
            if (null != singleKingdom) {
                log.info("user[" + uid + "] already has single kingdom");
                return Response.failure("你已经有单人王国了");
            }
        } else if (type == Specification.ActivityKingdomType.DOUBLEKING.index) {//双人王国
            if (uid2 <= 0) {
                log.info("uid2 less 1");
                return Response.failure("双人王国小王名字必须传递");
            }
            List<Long> uids = new ArrayList<Long>();
            uids.add(uid);
            uids.add(uid2);
            List<Atopic> topics = activityMybatisDao.getAtopicsByUids(uids);
            Map<String, Atopic> tMap = new HashMap<String, Atopic>();
            if (null != topics && topics.size() > 0) {
                for (Atopic t : topics) {
                    tMap.put(t.getUid() + "_" + t.getType(), t);
                }
            }
            Atopic t = tMap.get(uid + "_" + Specification.ActivityKingdomType.SINGLEKING.index);
            if (null == t) {
                return Response.failure("你必须创建单人王国才能创建双人王国");
            }
            t = tMap.get(uid2 + "_" + Specification.ActivityKingdomType.SINGLEKING.index);
            if (null == t) {
                return Response.failure("对方必须创建单人王国才能创建双人王国");
            }
            t = tMap.get(uid + "_" + Specification.ActivityKingdomType.DOUBLEKING.index);
            if (null != t) {
                return Response.failure("你已经有双人王国了，不能再创建了");
            }
            t = tMap.get(uid2 + "_" + Specification.ActivityKingdomType.DOUBLEKING.index);
            if (null != t) {
                return Response.failure("对方已经有双人王国了，不能再创建了");
            }
            List<AdoubleTopicApply> applyList = activityMybatisDao.getAgreeAdoubleTopicApplyByUidAndTargetUid(uid, uid2);
            boolean isCan = false;
            if (null != applyList && applyList.size() > 0) {
                isCan = true;
            }
            if (!isCan) {
                //再看下有没有强配的，有强配也可以创建
                AforcedPairing fp = activityMybatisDao.getAforcedPairingForUser(uid);
                if (null != fp && fp.getStatus() == 2) {
                    if ((fp.getUid().longValue() == uid && fp.getTargetUid().longValue() == uid2)
                            || (fp.getUid().longValue() == uid2 && fp.getTargetUid().longValue() == uid)) {
                        isCan = true;//强配成功
                    }
                }
            }
            if (!isCan) {
                return Response.failure("你和对方的双人王国申请未通过，不能创建");
            }
        } else {
            log.info("invalid type");
            return Response.failure("无效的王国类型");
        }
        return Response.success();
    }

    /**
     * 春节王国校验
     */
    @Override
    public Response checkUserActivityKindom4Spring(long uid) {
        //先判断是否在春节活动期间
        Date now = new Date();
        Aactivity aa = activityMybatisDao.getAactivity(2);
        if (null == aa || null == aa.getStartTime() || null == aa.getEndTime()
                || aa.getStartTime().getTime() > now.getTime()
                || aa.getEndTime().getTime() < now.getTime()) {
            log.info("now is out of spring activity!");
            return Response.failure("不在春节活动期，不能创建春节王国");
        }
        //再判断是否在可创建阶段
        List<AactivityStage> stageList = activityMybatisDao.getAactivityStage(2);
        boolean inStage = false;
        if (null != stageList && stageList.size() > 0) {
            for (AactivityStage stage : stageList) {
                if (stage.getStage() == 1 || stage.getStage() == 2) {
                    if (now.compareTo(stage.getStartTime()) > 0 && now.compareTo(stage.getEndTime()) < 0) {
                        inStage = true;
                        break;
                    }
                }
            }
        }
        if (!inStage) {
            return Response.failure("当前阶段不能创建春节王国");
        }

        //最后判断，一个用户只能创建一个有效的春节王国
        AkingDom kingdom = activityMybatisDao.getAkingDomByUidAndAid(uid, 2);
        if (null != kingdom) {
            return Response.failure("一个用户只能创建一个春节王国");
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
        if (type == Specification.ActivityKingdomType.DOUBLEKING.index) {
            t.setUid2(uid2);
        } else {
            t.setUid2(0l);
        }
        t.setTopicId(topicId);
        t.setType(type);
        t.setRights(1);
        t.setCreateTime(now);
        t.setHot(0l);
        t.setStatus(0);
        activityMybatisDao.createAtopic(t);

        if (type == Specification.ActivityKingdomType.DOUBLEKING.index) {
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

            //删除所有我发出的，并且已经同意了的请求
            List<AdoubleTopicApply> list1 = activityMybatisDao.getAdoubleTopicApplysByUidAndTypeAndStatus(uid, 1, 2);
            if (null != list1 && list1.size() > 0) {
                for (AdoubleTopicApply apply : list1) {
                    apply.setStatus(4);
                    activityMybatisDao.updateAdoubleTopicApply(apply);
                }
            }
            List<AdoubleTopicApply> list2 = activityMybatisDao.getAdoubleTopicApplysByUidAndTypeAndStatus(uid2, 1, 2);
            if (null != list2 && list2.size() > 0) {
                for (AdoubleTopicApply apply : list2) {
                    apply.setStatus(4);
                    activityMybatisDao.updateAdoubleTopicApply(apply);
                }
            }

            //通知小王
            UserProfile up = userService.getUserProfileByUid(uid);
            UserToken ut = userService.getUserTokenByUid(uid2);
            String msg = Specification.LinkPushType.CREATE_DOUBLE_KINGDOM_PARTNER.message.replaceAll("#\\{1\\}#", up.getNickName());
            StringBuilder sb = new StringBuilder();
            sb.append(activityWebUrl).append(Specification.LinkPushType.CREATE_DOUBLE_KINGDOM_PARTNER.linkUrl).append("?uid=");
            sb.append(uid2).append("&token=").append(ut.getToken());
            applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), uid2));

            //并且通知所有向我申请过的并且在申请中的人
            List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByTargetUidAndType(uid, 1);
            if (null != list && list.size() > 0) {
                List<Long> uids = new ArrayList<Long>();
                for (AdoubleTopicApply apply : list) {
                    if (apply.getUid().longValue() != uid) {
                        uids.add(apply.getUid());
                    }
                }
                if (uids.size() > 0) {
                    List<UserToken> uList = userService.getUserTokenByUids(uids);
                    if (null != uList && uList.size() > 0) {
                        for (UserToken u : uList) {
                            msg = Specification.LinkPushType.CREATE_DOUBLE_KINGDOM_WOOER.message.replaceAll("#\\{1\\}#", up.getNickName());
                            sb = new StringBuilder();
                            sb.append(activityWebUrl).append(Specification.LinkPushType.CREATE_DOUBLE_KINGDOM_WOOER.linkUrl).append("?uid=");
                            sb.append(u.getUid()).append("&token=").append(u.getToken());
                            applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), u.getUid()));
                        }
                    }

                }
            }
        }
    }

    @Override
    public void createActivityKingdom4Spring(long topicId, long uid) {
        AkingDom kingdom = new AkingDom();
        kingdom.setActivityId(2l);
        kingdom.setConditions(0);
        kingdom.setCreateTime(new Date());
        kingdom.setHot(0l);
        kingdom.setStatus(0);
        kingdom.setTopicId(topicId);
        kingdom.setUid(uid);
        activityMybatisDao.saveAkingDom(kingdom);
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
            return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status, ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }

        Aactivity aactivity = activityMybatisDao.getAactivity(qiUserDto.getActivityId());
        if (null == aactivity || null == aactivity.getStartTime() || null == aactivity.getEndTime()) {
            return Response.success(ResponseStatus.QIACTIVITY_NOT_START.status, ResponseStatus.QIACTIVITY_NOT_START.message);
        }
        Date nowDate = new Date();
        if (nowDate.compareTo(aactivity.getStartTime()) < 0 || nowDate.compareTo(aactivity.getEndTime()) > 0) {
            return Response.success(ResponseStatus.QIACTIVITY_NOT_START.status, ResponseStatus.QIACTIVITY_NOT_START.message);
        }

        AactivityStage aactivityStage1 = activityMybatisDao.getAactivityStageByStage(qiUserDto.getActivityId(), 1);
        if (null == aactivityStage1 || aactivityStage1.getType() != 0) {
            return Response.success(ResponseStatus.NOT_FIRST_STAGE.status, ResponseStatus.NOT_FIRST_STAGE.message);
        }

        Auser activityUser = activityMybatisDao.getAuserByMobile(qiUserDto.getMobile());
        if (null != activityUser) {
            return Response.success(ResponseStatus.CAN_ONLY_SIGN_UP_ONCE.status, ResponseStatus.CAN_ONLY_SIGN_UP_ONCE.message);
        }

        Auser auser = new Auser();
        auser.setActivityId(qiUserDto.getActivityId());
        auser.setAge(qiUserDto.getAge());
        auser.setChannel(qiUserDto.getChannel());
        auser.setCreateTime(new Date());
        auser.setMobile(qiUserDto.getMobile());
        auser.setName(qiUserDto.getName());
        auser.setSex(qiUserDto.getSex());
        auser.setStatus(1);//默认审核状态

        //判断绑定逻辑
        //是否APP内登陆
        if (qiUserDto.getUid() > 0) {//APP内登陆
            Auser appUser = activityMybatisDao.getAuserByUid(qiUserDto.getUid());
            if (null == appUser) {//没报过名
                auser.setUid(qiUserDto.getUid());
            } else {
                UserProfile userProfile = userService.getUserProfileByMobile(qiUserDto.getMobile());
                if (null != userProfile) {
                    auser.setUid(userProfile.getUid());
                }
            }
        } else {//APP外
            UserProfile userProfile = userService.getUserProfileByMobile(qiUserDto.getMobile());
            if (null != userProfile) {
                auser.setUid(userProfile.getUid());
            }
        }

        if (null != auser.getUid() && auser.getUid() > 0) {//绑了用户，则需要更新原始性别
            userService.updateUserSex(auser.getUid(), auser.getSex());
        }

        activityMybatisDao.createAuser(auser);
        //发送短信 报名成功
        List<String> mobileList = new ArrayList<String>();
        mobileList.add(qiUserDto.getMobile());
        smsService.send7dayCommon("144255", mobileList, null);
        return Response.success(ResponseStatus.REGISTRATION_SUCCESS.status, ResponseStatus.REGISTRATION_SUCCESS.message);
    }

    @Override
    public Response bindGetActivity(long uid, String mobile, String verifyCode) {

        VerifyDto verifyDto = new VerifyDto();
        //验证为1
        verifyDto.setAction(Specification.VerifyAction.CHECK.index);
        verifyDto.setMobile(mobile);
        verifyDto.setVerifyCode(verifyCode);
        Response response = userService.verify(verifyDto);

        if (response.getCode() == ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status) {
            QiStatusDto qiStatusDto = new QiStatusDto();
            Auser auser = activityMybatisDao.getAuserByMobile(mobile);
            if (null == auser) {
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status, ResponseStatus.USER_NOT_EXISTS.message);
            }
            //用户存在 说明绑定过
            qiStatusDto.setIsBind(1);
            qiStatusDto.setStatus(auser.getStatus());
            qiStatusDto.setAuid(auser.getId());

            if (uid > 0) {//APP过来的
                if (auser.getUid().longValue() == 0) {//还没有绑定的需要绑定
                    Auser appUser = activityMybatisDao.getAuserByUid(uid);
                    if (null == appUser) {//没有绑过就绑上
                        auser.setUid(uid);
                        activityMybatisDao.updateAuser(auser);
                        userService.updateUserSex(auser.getUid(), auser.getSex());
                    }
                }
            }
            return Response.success(ResponseStatus.QI_QUERY_SUCCESS.status, ResponseStatus.QI_QUERY_SUCCESS.message, qiStatusDto);
        } else {
            //验证码不正确
            return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status, ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }
    }

    @Override
    public Response getActivityInfo(long activityId) {
        QiActivityInfoDto infoDto = new QiActivityInfoDto();
        infoDto.init();
        Aactivity aactivity = activityMybatisDao.getAactivity(activityId);
        if (null != aactivity) {
            infoDto.setName(aactivity.getName());
            Date now = new Date();
            if (now.compareTo(aactivity.getStartTime()) < 0 || now.compareTo(aactivity.getEndTime()) > 0) {
                infoDto.setIsInActivity(0);
            } else {
                infoDto.setIsInActivity(1);
                List<AactivityStage> stageList = activityMybatisDao.getAactivityStage(activityId);
                if (null != stageList && stageList.size() > 0) {
                    for (AactivityStage stage : stageList) {
                        if (stage.getType() == 0) {
                            if (stage.getStage() == 1) {
                                infoDto.setIsSignUpStage(1);
                            } else if (stage.getStage() == 2) {
                                infoDto.setIsSingleStage(1);
                            } else if (stage.getStage() == 3 || stage.getStage() == 4) {
                                infoDto.setIsDoubleStage(1);
                            }
                        }
                    }
                }
            }
        } else {
            infoDto.setIsInActivity(0);
        }

        return Response.success(infoDto);
    }

    @Override
    public Response oneKeyAudit() {
        List<Auser> auserList = activityMybatisDao.getAuserList();
        //发短信给每个用户 告知审核通过了
        if (auserList.size() > 0 && auserList != null) {
            log.info("total [" + auserList.size() + "] user");
            AactivityStage stage2 = activityMybatisDao.getAactivityStageByStage2(1, 2);
            List<String> msgList = new ArrayList<String>();
            if (null != stage2) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(stage2.getStartTime());
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                msgList.add(String.valueOf(month));
                msgList.add(String.valueOf(day));
            } else {
                msgList.add("");
                msgList.add("");
            }

            List<String> mobileList = Lists.newArrayList();
            mobileList.add("18916103465");//默认给一个内部的手机号
            for (Auser auser : auserList) {
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());

                if (mobileList.size() >= 150) {
                    smsService.send7dayCommon("145624", mobileList, msgList);
                    log.info("send [" + mobileList.size() + "] user!");
                    mobileList.clear();
                }
            }
            if (mobileList.size() > 0) {
                smsService.send7dayCommon("145624", mobileList, msgList);
                log.info("send [" + mobileList.size() + "] user!");
            }

            log.info("send 7dayApply message success");
            //修改每个用户未审核通过
            activityMybatisDao.updateAuser();
            log.info("update Auser success");
            return Response.success(ResponseStatus.AWARD_MESSAGE_SUCCESS.status, ResponseStatus.AWARD_MESSAGE_SUCCESS.message);
        }

        return Response.success(ResponseStatus.CANNOT_FIND_AUSER.status, ResponseStatus.CANNOT_FIND_AUSER.message);
    }

    /**
     * 本方法暂时使用，这个抽奖活动名字以后肯定是用一张表来存储的，暂时先这样，下次有活动的时候再修改
     *
     * @param i
     * @return
     */
    private String getNameFromInt2String(int i) {
        if (i == 1) {
            return "小米活动";
        }
        return "未知";
    }

    /**
     * 本方法暂时使用，这个抽奖活动奖品名字以后肯定是用一张表来存储的，暂时先这样，下次有活动的时候再修改
     *
     * @param i
     * @return
     */
    private String getAwardNameFromInt2String(int i) {
        if (i == 1) {
            return "一等奖";
        } else if (i == 2) {
            return "二等奖";
        } else if (i == 3) {
            return "三等奖";
        } else if (i == 4) {
            return "四等奖";
        } else if (i == 5) {
            return "五等奖";
        } else if (i == 6) {
            return "六等奖";
        } else if (i == 7) {
            return "七等奖";
        } else if (i == 8) {
            return "八等奖";
        } else if (i == 9) {
            return "九等奖";
        } else if (i == 10) {
            return "十等奖";
        }
        return "未知";
    }

    /**
     * 随机方法1
     * 主要是：随机0-10之间，如果随机在0-8之间，则随机0-80这个方法，
     * 如果随机8-10，则随机80-100
     *
     * @return
     */
    public static int random1(float big, float small, int sum) {
        int i = randoms(sum);
        System.out.println("randoms: " + i);
        if (i == 0) {
            return randomone(small);
        } else {
            return randomtwo(big, small);
        }

    }

    /**
     * 产生随机数0---10
     * 如果当天有300人抽奖 这个改成100完全没问题 如果当天有3000人抽奖 这个改成1000完全没问题 百分之一 千分之一
     */
    public static int randoms(int sum) {
        Random random = new Random();
        int i = random.nextInt(sum);//数据库luck_status表award_sum_chance字段控制概率
        return i;
    }

    /**
     * 99%中奖
     *
     * @return
     */
    public static int randomone(float small) {
        Random random = new Random();
        //需要判断最后一个数据是否为0,为0的话随机1 因为0不能用nextInt会有异常
        if (small == 0) {
            small = 1f;
        }
        int i = random.nextInt((int) small);
        System.out.println("中奖范围: " + i);
        return i;
    }

    /**
     * 肯定不中奖
     *
     * @return
     */
    public static int randomtwo(float big, float small) {
        Random random = new Random();
        if (small == 0) {
            small = 1f;
        }
        int i = random.nextInt((int) small);//list最后一个数据 //只能随机到1-XXX +1表示不能为0
        if (i == 0) {
            i = 1;
        }
        int j = (int) big - (int) small;//最大值-中奖最小值 这样永远是不中奖几率 前提不中奖数据必须>中奖数据
        return j;
    }

    @Override
    public Response getLuckActStatList(int activityName) {
        List<LuckPrize> pList = activityMybatisDao.getPrizeListByActivityName(activityName);
        Map<String, LuckPrize> pMap = new HashMap<String, LuckPrize>();
        if (null != pList && pList.size() > 0) {
            for (LuckPrize lp : pList) {
                pMap.put(lp.getAwardId() + "", lp);
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
        if (null != luckActList && luckActList.size() > 0) {
            e.setPrizeNum(luckActList.size());
            e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
        } else {
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
        if (null != luckActList && luckActList.size() > 0) {
            e.setPrizeNum(luckActList.size());
            e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
        } else {
            e.setPrizeNum(0);
            e.setPrizeNames("");
        }
        slasDTO.getResult().add(e);
        //历史到当前天的按天统计
        //获取所有中奖纪录
        luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, null, null);
        List<LuckActStat2DTO> list2 = activityMybatisDao.getLuckActStat2List(activityName);
        if (null != list2 && list2.size() > 0) {
            for (LuckActStat2DTO dto2 : list2) {
                e = new ShowLuckActStatDTO.LuckActStatElement();
                e.setDateStr(dto2.getDateStr());
                e.setEnterPV(dto2.getEnterPV());
                e.setEnterUV(dto2.getEnterUV());
                e.setPrizeNum(dto2.getPrizeNum());
                if (dto2.getPrizeNum() > 0) {
                    e.setPrizeNames(this.getPrizeNames(luckActList, pMap, dto2.getDateStr()));
                } else {
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
        if (null != luckActList && luckActList.size() > 0) {
            e.setPrizeNum(luckActList.size());
            e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
        } else {
            e.setPrizeNum(0);
            e.setPrizeNames("");
        }
        slasDTO.getResult().add(e);

        return Response.success(slasDTO);
    }

    private String getPrizeNames(List<LuckAct> luckActList, Map<String, LuckPrize> pMap, String dateStr) {
        List<LuckAct> list = new ArrayList<LuckAct>();
        String date = null;
        for (LuckAct la : luckActList) {
            date = DateUtil.date2string(la.getCreatTime(), "yyyy-MM-dd");
            if (date.equals(dateStr)) {
                list.add(la);
            }
        }

        return this.getPrizeNames(list, pMap);
    }

    private String getPrizeNames(List<LuckAct> luckActList, Map<String, LuckPrize> pMap) {
        //PrizeName,Num
        Map<String, Integer> map = new HashMap<String, Integer>();
        LuckPrize p = null;
        String pname = null;
        Integer pNum = null;
        for (LuckAct la : luckActList) {
            p = pMap.get(String.valueOf(la.getAwardId()));
            if (null != p) {
                pname = p.getAwardName();
            } else {
                pname = this.getAwardNameFromInt2String(la.getAwardId());
            }
            if (null != pname && pname.length() > 0) {
                pNum = map.get(pname);
                if (null != pNum) {
                    pNum = Integer.valueOf(pNum + 1);
                } else {
                    pNum = Integer.valueOf(1);
                }
                map.put(pname, pNum);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                sb.append(";").append(entry.getKey()).append("X").append(entry.getValue());
            } else {
                sb.append(";").append(entry.getKey());
            }
        }
        String result = sb.toString();
        if (result.length() > 0) {
            result = result.substring(1);
        }
        return result;
    }

    @Override
    public Response getAwardStatusList(int activityName) {
        Integer aname = null;
        if (activityName > 0) {
            aname = Integer.valueOf(activityName);
        }
        List<LuckStatus> list = activityMybatisDao.getLuckStatusListByName(aname);
        ShowLuckStatusDTO dto = new ShowLuckStatusDTO();
        if (null != list && list.size() > 0) {
            LuckStatusDTO e = null;
            for (LuckStatus ls : list) {
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
        if (null != list && list.size() > 0) {
            ShowLuckPrizeDTO.LuckPrizeElement e = null;
            for (LuckPrize lp : list) {
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
        if (null != pList && pList.size() > 0) {
            for (LuckPrize lp : pList) {
                pMap.put(lp.getAwardId() + "", lp);
            }
        }

        ShowLuckActStatDTO slasDTO = new ShowLuckActStatDTO();
        LuckActStatDTO stat = activityMybatisDao.getLuckActStat(activityName, startTime, endTime);
        List<LuckAct> luckActList = activityMybatisDao.getPrizeLuckActListByActivityNameAndStartTimeAndEndTime(activityName, startTime, endTime);
        ShowLuckActStatDTO.LuckActStatElement e = new ShowLuckActStatDTO.LuckActStatElement();
        e.setDateStr("时间段");
        e.setEnterUV(stat.getEnterUV());
        e.setEnterPV(stat.getEnterPV());
        if (null != luckActList && luckActList.size() > 0) {
            e.setPrizeNum(luckActList.size());
            e.setPrizeNames(this.getPrizeNames(luckActList, pMap));
        } else {
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
    public Response getAliveInfo(long uid, String topicName, String nickName, int pageNum, int pageSize) {
        //判断当前是男性还是女性，查询出相反性别
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        Map map = Maps.newHashMap();
        AtopicInfoDto atopicInfoDto = new AtopicInfoDto();
        map.put("titleName", topicName);
        map.put("nickName", nickName);
        if (pageNum != 0) {
            pageNum = pageNum * pageSize;
        }
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("uid", uid);

        //查询是否报名过
        Auser isActivityUser = activityMybatisDao.getAuserByUid(uid);
        if (isActivityUser == null) {
            //没报名过 显示全部
            // TODO: 2016/12/28
            int total = activityMybatisDao.getAliveList(map);
            List<BlurSearchDto> allList = activityMybatisDao.getTopicByAll(map);
            if (allList.size() > 0 && allList != null) {
                for (BlurSearchDto blurSearchDto : allList) {
                    //是否单身，是否有双人王国条件判断
                    Atopic isAlone = activityMybatisDao.getAtopicByUid2(blurSearchDto.getUid());
                    if (isAlone == null) {
                        List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUid3(uid, blurSearchDto.getUid());
                        if (list.size() == 0) {
                            atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                        }
                    }
                }
                atopicInfoDto.setTotal(total);
                atopicInfoDto.setStatus(0);
                log.info("get aliveInfo success");
                return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message, atopicInfoDto);
            }

        } else {
            //报名过 显示异性
            if (userProfile != null) {
                if (userProfile.getGender() == 0) {
                    //查询总记录数
                    map.put("gender", 1);
                    int total = activityMybatisDao.getAliveList(map);
                    List<BlurSearchDto> boyList = activityMybatisDao.getTopicByBoy(map);
                    if (boyList.size() > 0 && boyList != null) {
                        for (BlurSearchDto blurSearchDto : boyList) {
                            //是否单身，是否有双人王国条件判断
                            Atopic isAlone = activityMybatisDao.getAtopicByUid2(blurSearchDto.getUid());
                            if (isAlone == null) {
                                //有 不是单身
                                //blurSearchDto.setIsAlone(1);
                                //申请过的不再显示
                                List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUid3(uid, blurSearchDto.getUid());
                                if (list.size() == 0) {
                                    atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                                }
                            }
                        }
                        atopicInfoDto.setTotal(total);
                        atopicInfoDto.setStatus(1);
                        log.info("get aliveInfo success");
                        return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message, atopicInfoDto);
                    }

                } else {
                    map.put("gender", 0);
                    int total = activityMybatisDao.getAliveList(map);
                    //1男 //查询女
                    List<BlurSearchDto> girlList = activityMybatisDao.getTopicByGirl(map);
                    if (girlList.size() > 0 && girlList != null) {
                        for (BlurSearchDto blurSearchDto : girlList) {
                            //是否单身，是否有双人王国条件判断
                            Atopic isAlone = activityMybatisDao.getAtopicByUid2(blurSearchDto.getUid());
                            if (isAlone == null) {
                                //有 不是单身
                                // blurSearchDto.setIsAlone(1);
                                //申请过的不再显示
                                List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUid3(uid, blurSearchDto.getUid());
                                if (list.size() == 0) {
                                    atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                                }
                            }
                        }
                        atopicInfoDto.setTotal(total);
                        atopicInfoDto.setStatus(1);
                        log.info("get aliveInfo success");
                        return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message, atopicInfoDto);
                    }
                }
            }
        }
        return Response.success(ResponseStatus.SEARCH_ATOPIC_FAILURE.status, ResponseStatus.SEARCH_ATOPIC_FAILURE.message, atopicInfoDto);
    }

    @Override
    public Response getBridList(long uid, String topicName, String nickName, int pageNum, int pageSize, int type) {
        //判断当前是男性还是女性，查询出相反性别
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        Map map = Maps.newHashMap();
        AtopicInfoDto atopicInfoDto = new AtopicInfoDto();
        map.put("titleName", topicName);
        map.put("nickName", nickName);
        if (pageNum != 0) {
            pageNum = pageNum * pageSize;
        }
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        //区分智能排序 按照昵称首字母排序
        if (type != 0) {
            map.put("type", type);
        }
        if (userProfile != null) {
            if (userProfile.getGender() == 0) {
                //查询总记录数
                map.put("gender", 1);
                // TODO: 2016/12/15
                int total = activityMybatisDao.getBridListTotal(map);
                atopicInfoDto.setTotal(total);
                //0女 查询男
                List<BlurSearchDto> boyList = activityMybatisDao.getBridList(map);
                if (boyList.size() > 0 && boyList != null) {
                    for (BlurSearchDto blurSearchDto : boyList) {
                        //申请过的不再显示
                        List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUid4(uid, blurSearchDto.getUid());
                        if (list.size() == 0) {
                            atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                        }
                        blurSearchDto.setIsAlone(1);
                    }
                    log.info("get bridList success");
                    return Response.success(ResponseStatus.SEARCH_BRIDLIST_SUCCESS.status, ResponseStatus.SEARCH_BRIDLIST_SUCCESS.message, atopicInfoDto);
                }

            } else {
                map.put("gender", 0);
                int total = activityMybatisDao.getBridListTotal(map);
                atopicInfoDto.setTotal(total);
                //1男 //查询女
                List<BlurSearchDto> girlList = activityMybatisDao.getBridList(map);
                if (girlList.size() > 0 && girlList != null) {
                    for (BlurSearchDto blurSearchDto : girlList) {
                        //申请过的不再显示
                        List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUid4(uid, blurSearchDto.getUid());
                        if (list.size() == 0) {
                            atopicInfoDto.getBlurSearchList().add(blurSearchDto);
                        }
                        blurSearchDto.setIsAlone(1);
                    }
                    log.info("get bridList success");
                    return Response.success(ResponseStatus.SEARCH_BRIDLIST_SUCCESS.status, ResponseStatus.SEARCH_BRIDLIST_SUCCESS.message, atopicInfoDto);
                }
            }

        }
        return Response.success(ResponseStatus.SEARCH_BRIDLIST_FAILURE.status, ResponseStatus.SEARCH_BRIDLIST_FAILURE.message, atopicInfoDto);

    }

    @Override
    public Response createDoubleLive(long uid, long targetUid, long activityId) {
        AactivityStage aactivityStage3 = activityMybatisDao.getAactivityStageByStage(activityId, 3);
        if (aactivityStage3 != null) {
            //单人王国
            Atopic ownerTopicSingle = activityMybatisDao.getAtopicByUid1(uid);
            Atopic targetTopicSingle = activityMybatisDao.getAtopicByUid1(targetUid);
            //双人王国
            Atopic ownerTopicDouble = activityMybatisDao.getAtopicByUid2(uid);
            Atopic targetTopicDouble = activityMybatisDao.getAtopicByUid2(targetUid);
            //申请次数 长度<5 为5次
            List<AdoubleTopicApply> applyOwner = activityMybatisDao.getTopicApply(uid);
            //满足只建立了单人王国，都没建立双人王国
            if (ownerTopicSingle != null && targetTopicSingle != null && ownerTopicDouble == null && targetTopicDouble == null) {
                //如果有申请中，和同意了的记录，不能再发申请
                List<AdoubleTopicApply> applyList = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUidandNotIn(uid, targetUid);
                if (applyList.size() > 0) {
                    return Response.success(ResponseStatus.APPLICATION_EXISTS.status, ResponseStatus.APPLICATION_EXISTS.message);
                }
                //申请次数
                String num = cacheService.get(SEVENDAY_KEY);
                if (applyOwner.size() < Integer.parseInt(num)) {
                    //如果申请记录存在，返回不能重复申请
                    AdoubleTopicApply apply = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid2(uid, targetUid);
                    if (apply != null) {
                        return Response.failure(ResponseStatus.CAN_NOT_REPEAT_THE_APPLICATION.status, ResponseStatus.CAN_NOT_REPEAT_THE_APPLICATION.message);
                    }
                    //请求
                    AdoubleTopicApply applyReq = new AdoubleTopicApply();
                    applyReq.setUid(uid);
                    applyReq.setTargetUid(targetUid);
                    //配对类型为1
                    applyReq.setType(1);
                    activityMybatisDao.createAdoubleTopicApply(applyReq);

                    //推送，让对方知晓
                    UserProfile up = userService.getUserProfileByUid(uid);
                    UserToken ut = userService.getUserTokenByUid(targetUid);
                    String msg = Specification.LinkPushType.PAIR_APPLY.message.replaceAll("#\\{1\\}#", up.getNickName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(activityWebUrl).append(Specification.LinkPushType.PAIR_APPLY.linkUrl).append("?uid=");
                    sb.append(targetUid).append("&token=").append(ut.getToken());
                    applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), targetUid));

                    log.info("application success");
                    return Response.success(ResponseStatus.APPLICATION_SUCCESS.status, ResponseStatus.APPLICATION_SUCCESS.message);
                } else {
                    return Response.success(ResponseStatus.NUMBER_IS_BOUND.status, ResponseStatus.NUMBER_IS_BOUND.message);
                }
            } else {
                return Response.failure(ResponseStatus.APPLICATION_FAILURE.status, ResponseStatus.APPLICATION_FAILURE.message);
            }
        }
        return Response.success(ResponseStatus.NOT_THREE_STAGE.status, ResponseStatus.NOT_THREE_STAGE.message);
    }

    @Override
    public Response getApplyInfo(long uid, int type, int pageNum, int pageSize) {
        ApplyListDto applyListDto = new ApplyListDto();
        if (type == 0) {
            //我发出的 不包含删除
            List<AdoubleTopicApply> sendList = activityMybatisDao.getAdoubleTopicApplyByUid(uid);
            //我接收到的 包含申请和拒绝
            List<AdoubleTopicApply> receiveList = activityMybatisDao.getAdoubleTopicApplyByUidReceive(uid, pageNum, pageSize);
            //我接收到的总条数
            int total = activityMybatisDao.getReceiveList(uid);
            applyListDto.setTotal(total);
            //我同意的
            List<AdoubleTopicApply> agreeList = activityMybatisDao.getAdoubleTopicApplyByUidAgree(uid);
            //我发出去的<=5
            if (sendList.size() > 0 && sendList != null) {
                List<ApplyListDto.ApplyElement> lists = applyListDto.getSendList();
                for (AdoubleTopicApply apply : sendList) {
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getTargetUid());
                    Atopic atopic = activityMybatisDao.getAtopicByUid1(apply.getTargetUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile, applyElement);
                    if (atopic != null) {
                        Map<String, Object> topic = liveForActivityDao.getTopicById(atopic.getTopicId());
                        applyElement.setTitle((String) topic.get("title"));
                        applyElement.setTopicId(atopic.getTopicId());
                    }
                    applyElement.setId(apply.getId());
                    applyElement.setStatus(apply.getStatus());
                    lists.add(applyElement);
                }
                log.info("get sendList success");
            }
            //接收到可能很多需要分页
            if (receiveList.size() > 0 && receiveList != null) {
                List<ApplyListDto.ApplyElement> lists = applyListDto.getReceiveList();
                for (AdoubleTopicApply apply : receiveList) {
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getUid());
                    Atopic atopic = activityMybatisDao.getAtopicByUid1(apply.getUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile, applyElement);
                    if (atopic != null) {
                        Map<String, Object> topic = liveForActivityDao.getTopicById(atopic.getTopicId());
                        applyElement.setTitle((String) topic.get("title"));
                        applyElement.setTopicId(atopic.getTopicId());
                    }
                    applyElement.setId(apply.getId());
                    //查询像我发出的申请的人有没有同意别人的邀请 如果同意了返回2
                    //AdoubleTopicApply only = activityMybatisDao.getAdoubleTopicApplyByUid5(apply.getUid());
                    //查询是否创建了双人王国 是的话已结婚 返回2
                    Atopic only = activityMybatisDao.getAtopicByUid5(apply.getUid());
                    if (only != null) {
                        applyElement.setStatus(2);
                    } else {
                        applyElement.setStatus(apply.getStatus());
                    }
                    lists.add(applyElement);
                }
                log.info("get receiveList success");
            }
            //我同意的<=1
            if (agreeList.size() > 0 && agreeList != null) {
                List<ApplyListDto.ApplyElement> lists = applyListDto.getAgreeList();
                for (AdoubleTopicApply apply : agreeList) {
                    UserProfile userProfile = userService.getUserProfileByUid(apply.getUid());
                    Atopic atopic = activityMybatisDao.getAtopicByUid1(apply.getUid());
                    ApplyListDto.ApplyElement applyElement = applyListDto.createApplyElement();
                    BeanUtils.copyProperties(userProfile, applyElement);
                    if (atopic != null) {
                        Map<String, Object> topic = liveForActivityDao.getTopicById(atopic.getTopicId());
                        applyElement.setTitle((String) topic.get("title"));
                        applyElement.setTopicId(atopic.getTopicId());
                    }
                    applyElement.setId(apply.getId());
                    applyElement.setStatus(apply.getStatus());
                    lists.add(applyElement);
                }
                log.info("get agreeList success");
            }
            return Response.success(ResponseStatus.APPLY_LIST_SUCCESS.status, ResponseStatus.APPLY_LIST_SUCCESS.message, applyListDto);
        }
        return null;
    }

    @Override
    public Response applyDoubleLive(long uid, int applyId, int operaStatus) {
        // 查询自己同意的条数 只能一条 ，接收方查询是targetUid
        List<AdoubleTopicApply> lists = activityMybatisDao.getAdoubleTopicApplyByUid2(uid);
        // 2同意，3拒绝，4删除
        if (operaStatus == 2) {
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            if (lists.size() < 1) {
                // 同意时，需要判断对方是否已经创建了双人王国，如果已经创建了，则无法同意了。
                if (topicApply != null) {
                    if (topicApply.getStatus() == 1) {
                        // 查看对方是否有双人王国
                        Atopic atopic = activityMybatisDao.getAtopicByAuidDoubleByUid(topicApply.getUid());
                        if (atopic == null) {
                            topicApply.setStatus(operaStatus);
                            activityMybatisDao.updateAdoubleTopicApply(topicApply);

                            // 同意对方申请，要通知对方
                            UserProfile up = userService.getUserProfileByUid(topicApply.getTargetUid());
                            UserToken ut = userService.getUserTokenByUid(topicApply.getUid());
                            String msg = Specification.LinkPushType.PAIR_AGREE.message.replaceAll("#\\{1\\}#", up.getNickName());
                            StringBuilder sb = new StringBuilder();
                            sb.append(activityWebUrl).append(Specification.LinkPushType.PAIR_AGREE.linkUrl).append("?uid=");
                            sb.append(topicApply.getUid()).append("&token=").append(ut.getToken());
                            applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), topicApply.getUid()));

                            //同意对方申请，要短信通知对方
                            List<String> msgList = new ArrayList<String>();
                            msgList.add(up.getNickName());

                            Auser auser = activityMybatisDao.getAuserByUid(topicApply.getUid());
                            if (null != auser) {
                                List<String> mobileList = new ArrayList<String>();
                                mobileList.add(auser.getMobile());
                                smsService.send7dayCommon("142385", mobileList, msgList);
                            }

                            log.info("update agree success");
                        } else {
                            return Response.success(ResponseStatus.TARGET_CREATE_TOPIC.status, ResponseStatus.TARGET_CREATE_TOPIC.message);
                        }
                    } else {
                        return Response.success(ResponseStatus.APPLY_IS_CANCELED.status, ResponseStatus.APPLY_IS_CANCELED.message);
                    }
                }
            } else {
                return Response.success(ResponseStatus.ONLY_AGREE_ONE_PEOPLE.status, ResponseStatus.ONLY_AGREE_ONE_PEOPLE.message);
            }
        } else if (operaStatus == 3) {
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            if (topicApply != null) {
                if (topicApply.getStatus() == 1) {
                    topicApply.setStatus(operaStatus);
                    activityMybatisDao.updateAdoubleTopicApply(topicApply);

                    // 拒绝对方申请，要通知对方
                    UserProfile up = userService.getUserProfileByUid(topicApply.getTargetUid());
                    UserToken ut = userService.getUserTokenByUid(topicApply.getUid());
                    String msg = Specification.LinkPushType.PAIR_REFUSE.message.replaceAll("#\\{1\\}#", up.getNickName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(activityWebUrl).append(Specification.LinkPushType.PAIR_REFUSE.linkUrl).append("?uid=");
                    sb.append(topicApply.getUid()).append("&token=").append(ut.getToken());
                    applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), topicApply.getUid()));

                    log.info("update refuse success");
                } else {
                    return Response.success(ResponseStatus.APPLY_IS_CANCELED.status, ResponseStatus.APPLY_IS_CANCELED.message);
                }
            }
        } else if (operaStatus == 4) {
            // 删除需要符合条件的才能删除，首先必须是自己发出的申请，并且对方还没有同意的申请才能删除，
            // 或者对方同意了但是已经和别人创建 双人王国了也能删除。
            AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
            Atopic atopic = activityMybatisDao.getAtopicByAuidDoubleByUid(topicApply.getUid());
            if ((topicApply.getUid().longValue() == uid && topicApply.getStatus() != 2)
                    || (topicApply.getStatus() == 2 && atopic != null)
                    || topicApply.getStatus() == 3) {
                topicApply.setStatus(operaStatus);
                activityMybatisDao.updateAdoubleTopicApply(topicApply);
                log.info("update delete success");
            } else {
                return Response.success(ResponseStatus.CANT_DELETE.status, ResponseStatus.CANT_DELETE.message);
            }
        }
        log.info("update state success");
        return Response.success(ResponseStatus.UPDATE_STATE_SUCCESS.status, ResponseStatus.UPDATE_STATE_SUCCESS.message);
    }

    @Override
    public Response bridApply(long uid, long targetUid) {
        Atopic ownerTopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid, 2);
        Atopic targetTopic = activityMybatisDao.getAtopicByUidandTypeBrid(targetUid, 2);
        String bridKey = cacheService.get(BRID_KEY);
        AactivityStage aactivityStage4 = activityMybatisDao.getAactivityStageByStage(1, 4);
        if (aactivityStage4 != null && aactivityStage4.getType() == 0) {
            if (bridKey != null) {
                List<AdoubleTopicApply> lists = activityMybatisDao.getAdoubleTopicApplyByUidBrid(uid);
                // 申请人没有双人王国，接收人有双人王国，才能抢亲 只能5次
                if (ownerTopic == null && targetTopic != null && lists.size() < Integer.parseInt(bridKey)) {
                    AdoubleTopicApply apply = new AdoubleTopicApply();
                    apply.setType(2);// 2是抢亲
                    apply.setUid(uid);
                    apply.setTargetUid(targetUid);
                    activityMybatisDao.createAdoubleTopicApply(apply);

                    // 发起抢亲向对方发推送
                    UserProfile up = userService.getUserProfileByUid(uid);
                    UserToken ut = userService.getUserTokenByUid(targetUid);
                    String msg = Specification.LinkPushType.ROB_APPLY.message.replaceAll("#\\{1\\}#", up.getNickName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(activityWebUrl).append(Specification.LinkPushType.ROB_APPLY.linkUrl).append("?uid=");
                    sb.append(targetUid).append("&token=").append(ut.getToken());
                    applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), targetUid));

                    // 向对方的原配发消息
                    ut = userService.getUserTokenByUid(targetTopic.getUid2());
                    msg = Specification.LinkPushType.ROB_APPLY_PARTNER.message.replaceAll("#\\{1\\}#", up.getNickName());
                    sb = new StringBuilder();
                    sb.append(activityWebUrl).append(Specification.LinkPushType.ROB_APPLY_PARTNER.linkUrl).append("?uid=");
                    sb.append(targetTopic.getUid2()).append("&token=").append(ut.getToken());
                    applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), targetTopic.getUid2()));

                    log.info("brid success");
                    return Response.success(ResponseStatus.APPLY_BRID_SUCCESS.status, ResponseStatus.APPLY_BRID_SUCCESS.message);
                } else {
                    return Response.success(ResponseStatus.BRID_UPPER_LIMIT.status, ResponseStatus.BRID_UPPER_LIMIT.message);
                }
            }
        } else {
            return Response.success(ResponseStatus.NOT_FOUR_STAGE.status, ResponseStatus.NOT_FOUR_STAGE.message);
        }
        return Response.success(ResponseStatus.CANT_APPLY_BRID.status, ResponseStatus.CANT_APPLY_BRID.message);
    }

    @Override
    public Response bridSearch(long uid, int type, int pageNum, int pageSize) {
        //我抢别人的列表
        if (type == 1) {
            if (pageNum != 0) {
                pageNum = pageNum * pageSize;
            }
            List<AdoubleTopicApply> applyList = activityMybatisDao.getDoubleTipicByBridAndUid(uid, 2, pageNum, pageSize);
            BridListDto bridListDto = new BridListDto();
            List<BridListDto.ApplyElement> lists = bridListDto.getBridList();
            if (applyList.size() > 0 && applyList != null) {
                for (AdoubleTopicApply topicApply : applyList) {
                    BridListDto.ApplyElement applyElement = bridListDto.createApplyElement();
                    UserProfile userProfile = userService.getUserProfileByUid(topicApply.getTargetUid());
                    BeanUtils.copyProperties(userProfile, applyElement);
                    Atopic atopic = activityMybatisDao.getAtopicByUid2(topicApply.getTargetUid());
                    if (atopic != null) {
                        Map<String, Object> map = liveForActivityDao.getTopicById(atopic.getTopicId());
                        applyElement.setTitle((String) map.get("title"));
                        applyElement.setTopicId((Long) map.get("id"));
                    }
                    applyElement.setId(topicApply.getId());
                    applyElement.setStatus(topicApply.getStatus());
                    lists.add(applyElement);
                }
                int total = activityMybatisDao.getDoubleTipicByBridAndUidCount(uid, 2);
                bridListDto.setTotal(total);
                log.info("brid get list success");
                return Response.success(ResponseStatus.BRID_GET_LIST_SUCCESS.status, ResponseStatus.BRID_GET_LIST_SUCCESS.message, bridListDto);
            }
        } else if (type == 2) {//别人抢我的列表
            if (pageNum != 0) {
                pageNum = pageNum * pageSize;
            }
            List<AdoubleTopicApply> applyList = activityMybatisDao.getDoubleTipicByBridAndTargetUid(uid, 2, pageNum, pageSize);
            BridListDto bridListDto = new BridListDto();
            List<BridListDto.ApplyElement> lists = bridListDto.getBridList();
            if (applyList.size() > 0 && applyList != null) {
                for (AdoubleTopicApply topicApply : applyList) {
                    BridListDto.ApplyElement applyElement = bridListDto.createApplyElement();
                    UserProfile userProfile = userService.getUserProfileByUid(topicApply.getUid());
                    BeanUtils.copyProperties(userProfile, applyElement);
                    Atopic atopic = activityMybatisDao.getAtopicByUid2(topicApply.getUid());
                    if (atopic != null) {
                        Map<String, Object> map = liveForActivityDao.getTopicById(atopic.getTopicId());
                        applyElement.setTitle((String) map.get("title"));
                        applyElement.setTopicId((Long) map.get("id"));
                    }
                    applyElement.setId(topicApply.getId());
                    applyElement.setStatus(topicApply.getStatus());
                    lists.add(applyElement);
                }
                int total = activityMybatisDao.getDoubleTipicByBridAndTargetUidCount(uid, 2);
                bridListDto.setTotal(total);
                log.info("brid get list success");
                return Response.success(ResponseStatus.BRID_GET_LIST_SUCCESS.status, ResponseStatus.BRID_GET_LIST_SUCCESS.message, bridListDto);
            }

        }
        return Response.success(ResponseStatus.BRID_GET_LIST_FAILURE.status, ResponseStatus.BRID_GET_LIST_FAILURE.message);
    }


    @Override
    public Response doublueLiveState(long uid) {
        DoubleLiveDto doubleLiveDto = new DoubleLiveDto();
        List<DoubleLiveDto.DoubleLiveElement> ownerInfo = doubleLiveDto.getOwnerInfo();
        List<DoubleLiveDto.DoubleLiveElement> targetInfo = doubleLiveDto.getTargetInfo();
        Atopic atopic = activityMybatisDao.getAtopicByType(uid, 2);
        if (atopic != null) {
            UserProfile ownerProfile = userService.getUserProfileByUid(uid);//自己
            UserProfile targetProfile = userService.getUserProfileByUid(atopic.getUid2());//爱人
            List<AdoubleTopicApply> ownerRobbed = activityMybatisDao.getAdoubleTopicApplyByUidBrid2(uid);//被抢次数
            List<AdoubleTopicApply> targetRobbed = activityMybatisDao.getAdoubleTopicApplyByUidBrid2(atopic.getUid2());

            //自己
            DoubleLiveDto.DoubleLiveElement ownerElement = doubleLiveDto.createDoubleLiveElement();
            ownerElement.setAvatar(ownerProfile.getAvatar());
            ownerElement.setNickName(ownerProfile.getNickName());
            ownerElement.setUid(ownerProfile.getUid());
            ownerElement.setRobbed(ownerRobbed.size());
            log.info("get owner doublueLiveState success");

            //对方
            DoubleLiveDto.DoubleLiveElement targetElement = doubleLiveDto.createDoubleLiveElement();
            targetElement.setAvatar(targetProfile.getAvatar());
            targetElement.setNickName(targetProfile.getNickName());
            targetElement.setUid(targetProfile.getUid());
            targetElement.setRobbed(targetRobbed.size());
            log.info("get target doublueLiveState success");

            ownerInfo.add(ownerElement);
            targetInfo.add(targetElement);

            Map<String,Object> topic = liveForActivityDao.getTopicById(atopic.getTopicId());
            Date createDate = (Date)topic.get("create_time");
            Date nowDate = new Date();
            long loveDay = DateUtil.getDaysBetween2Date(createDate, nowDate)+1;
            doubleLiveDto.setLoveDay((int)loveDay);
            log.info("get doublueLiveState success");
            return Response.success(doubleLiveDto);
        }

        return Response.success(ResponseStatus.NOT_GET_DOUBLELIVE.status, ResponseStatus.NOT_GET_DOUBLELIVE.message);
    }

    @Override
    public Response divorce(long uid, long targetUid) {
        Atopic atopic = activityMybatisDao.getAtopicByUidAndtargetUid(uid, targetUid);
        if (atopic != null) {
//            atopic.setStatus(1);//离婚
            Map map = Maps.newHashMap();
            map.put("topicId", atopic.getTopicId());
            activityMybatisDao.updateAtopicStatus(map);
            List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid3(uid, targetUid);
            List<AdoubleTopicApply> list2 = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid3(targetUid, uid);
            if (list != null && list.size() > 0) {
                for (AdoubleTopicApply a : list) {
                    if (a.getStatus() != 4) {
                        a.setStatus(4);
                        activityMybatisDao.updateAdoubleTopicApply(a);
                    }
                }
            }
            if (list2 != null && list2.size() > 0) {
                for (AdoubleTopicApply a : list2) {
                    if (a.getStatus() != 4) {
                        a.setStatus(4);
                        activityMybatisDao.updateAdoubleTopicApply(a);
                    }
                }
            }

            //也许是强配的结婚，所以也要查询下有没有强配的记录，有的话也要一并清除
            AforcedPairing ap = activityMybatisDao.getAforcedPairingForUser(uid);
            if (null != ap) {
                if (ap.getUid().longValue() == uid && ap.getTargetUid().longValue() == targetUid) {
                    activityMybatisDao.deleteAforcedPairingById(ap.getId());
                } else if (ap.getUid().longValue() == targetUid && ap.getTargetUid().longValue() == uid) {
                    activityMybatisDao.deleteAforcedPairingById(ap.getId());
                }//其他的是和别人的，可以不做处理
            }

            //离婚了要通知对方
            UserProfile up = userService.getUserProfileByUid(uid);
            UserToken ut = userService.getUserTokenByUid(targetUid);
            String msg = Specification.LinkPushType.DOUBLE_KINGDOM_BREAK.message.replaceAll("#\\{1\\}#", up.getNickName());
            StringBuilder sb = new StringBuilder();
            sb.append(activityWebUrl).append(Specification.LinkPushType.DOUBLE_KINGDOM_BREAK.linkUrl).append("?uid=");
            sb.append(targetUid).append("&token=").append(ut.getToken());
            applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), targetUid));

            log.info("divorce success");
            return Response.success(ResponseStatus.DIVORCE_SUCCESS.status, ResponseStatus.DIVORCE_SUCCESS.message);
        }
        return Response.success(ResponseStatus.NOT_GET_DOUBLELIVE.status, ResponseStatus.NOT_GET_DOUBLELIVE.message);
    }

    @Override
    public Response genMiliList4Spring(long uid) {
        Show7DayMiliDTO respDTO = new Show7DayMiliDTO();

        Date now = new Date();

        //一次性获取所有活动米粒语料（不在后面每次获取）
        Map<String, List<AmiliData>> miliMap = new HashMap<String, List<AmiliData>>();
        List<AmiliData> allMiliDatas = activityMybatisDao.getAllAmiliData(2);
        if (null != allMiliDatas && allMiliDatas.size() > 0) {
            List<AmiliData> list = null;
            for (AmiliData data : allMiliDatas) {
                list = miliMap.get(data.getMkey());
                if (null == list) {
                    list = new ArrayList<AmiliData>();
                    miliMap.put(data.getMkey(), list);
                }
                list.add(data);
            }
        }

        if (miliMap.size() == 0) {
            return Response.success(respDTO);
        }

        //获取所有
        Map<String, AactivityStage> stageMap = new HashMap<String, AactivityStage>();
        List<AactivityStage> allStages = activityMybatisDao.getAactivityStage(2);//7天活动
        if (null != allStages && allStages.size() > 0) {
            for (AactivityStage s : allStages) {
                stageMap.put(String.valueOf(s.getStage()), s);
            }
        }
        AactivityStage stage1 = stageMap.get("1");//预热阶段
        AactivityStage stage2 = stageMap.get("2");//活动阶段
        AactivityStage stage3 = stageMap.get("3");//结束阶段

        List<Map<String, String>> params = null;

        //0 公共部分
        //0.1 进入模板语料
        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ENTER_COMMON.key, null);

        //查询下当前用户是否创建了春节王国
        AkingDom kingdom = activityMybatisDao.getAkingDomByUidAndAid(uid, 2);

        //1预热阶段
        if (null != stage1 && checkInStage(now, stage1)) {
            //判断是否预热最后一天
            if (DateUtil.isSameDay(now, stage1.getEndTime())) {//最后一天
                if (null == kingdom) {//未建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_SPRING_KINGDOM_PREHEAT_2.key, null);
                } else {//已建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_SPRING_KINGDOM_PREHEAT_2.key, null);
                }
            } else {
                if (null == kingdom) {//未建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_SPRING_KINGDOM_PREHEAT_1.key, null);
                } else {//已建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_SPRING_KINGDOM_PREHEAT_1.key, null);
                }
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);

        //2活动期间
        if (null != stage2 && checkInStage(now, stage2)) {
            //判断是否活动第一天
            if (DateUtil.isSameDay(now, stage2.getStartTime())) {//活动第一天
                if (null == kingdom) {//未建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_SPRING_KINGDOM_PERIOD_1.key, null);
                } else {
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_SPRING_KINGDOM_PERIOD_1.key, null);
                }
                //活动第一天，也就是除夕
                if (currentHour >= 19) {
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEARS_EVE_19.key, null);
                }
            } else {
                long days = DateUtil.getDaysBetween2Date(now, stage2.getStartTime());
                if (null == kingdom) {//未建立
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_SPRING_KINGDOM_PERIOD_2.key, null);
                } else {
                    //活动天数
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("dayCount", String.valueOf(days + 1));
                    params.add(map);
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_SPRING_KINGDOM_PERIOD_2.key, params);
                }
                if (currentHour >= 9) {
                    //日期
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(now);
                    cal2.add(Calendar.DATE, -1);
                    int month = cal2.get(Calendar.MONTH) + 1;
                    int day = cal2.get(Calendar.DAY_OF_MONTH);
                    String daykey = DateUtil.date2string(cal2.getTime(), "yyyyMMdd");
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("month", String.valueOf(month));
                    map.put("day", String.valueOf(day));
                    map.put("daykey", daykey);
                    params.add(map);
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_9.key, params);
                }
                if (currentHour >= 12) {
                    //12点的信息每天都不一样。。喵了个咪的。。
                    //活动开始第二天开始有此条信息。每天不一样
                    if (days == 1) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_1.key, null);
                    } else if (days == 2) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_2.key, null);
                    } else if (days == 3) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_3.key, null);
                    } else if (days == 4) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_4.key, null);
                    } else if (days == 5) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_5.key, null);
                    } else if (days == 6) {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NEW_YEAR_12_6.key, null);
                    }
                }
            }
        }

        //3结束阶段
        if (null != stage3 && checkInStage(now, stage3)) {
            //活动结束阶段的第一天也有米粒
            if (DateUtil.isSameDay(now, stage3.getStartTime())) {//结束阶段第一天
                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SPRING_ACTIVITY_END.key, null);

                if (currentHour >= 9) {
                    //日期
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(now);
                    cal2.add(Calendar.DATE, -1);
                    int month = cal2.get(Calendar.MONTH) + 1;
                    int day = cal2.get(Calendar.DAY_OF_MONTH);
                    String daykey = DateUtil.date2string(cal2.getTime(), "yyyyMMdd");
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("month", String.valueOf(month));
                    map.put("day", String.valueOf(day));
                    map.put("daykey", daykey);
                    params.add(map);
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SPRING_ACTIVITY_END_9.key, params);
                }
            }
        }

        //米粒排序
        if (respDTO.getResult().size() > 1) {
            Collections.sort(respDTO.getResult(), new Comparator<Show7DayMiliDTO.MiliElement>() {
                public int compare(Show7DayMiliDTO.MiliElement e1, Show7DayMiliDTO.MiliElement e2) {
                    if (e1.getOrder() == e2.getOrder()) {
                        return 0;
                    } else if (e1.getOrder() > e2.getOrder()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }

        return Response.success(respDTO);
    }

    private boolean checkInStage(Date date, AactivityStage stage) {
        if (null == date || null == stage || null == stage.getStartTime() || null == stage.getEndTime()) {
            return false;
        }

        if (date.compareTo(stage.getStartTime()) > 0 && date.compareTo(stage.getEndTime()) < 0) {
            return true;
        }

        return false;
    }

    /**
     * 乱七八糟的逻辑。。一个巨大的深坑。。后来人注意
     */
    @Override
    public Response genActivity7DayMiliList(Activity7DayMiliDTO dto) {
        Show7DayMiliDTO respDTO = new Show7DayMiliDTO();

        //一次性获取所有活动米粒语料（不在后面每次获取）
        Map<String, List<AmiliData>> miliMap = new HashMap<String, List<AmiliData>>();
        List<AmiliData> allMiliDatas = activityMybatisDao.getAllAmiliData(1);
        if (null != allMiliDatas && allMiliDatas.size() > 0) {
            List<AmiliData> list = null;
            for (AmiliData data : allMiliDatas) {
                list = miliMap.get(data.getMkey());
                if (null == list) {
                    list = new ArrayList<AmiliData>();
                    miliMap.put(data.getMkey(), list);
                }
                list.add(data);
            }
        }

        if (miliMap.size() == 0) {
            return Response.success(respDTO);
        }

        List<Map<String, String>> params = null;

        //0 公共部分
        //0.1 进入模板语料
        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ENTER_COMMON.key, null);
        //0.2 是否首次进入
        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FIRST_ENTER.key, null);
        //0.3 活动介绍和状态
        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_INFO.key, null);
        if (dto.getIsApp() == 0) {
            //0.4 下载链接等
            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.APP_DOWNLOAD.key, null);
        }
        //0.5 系统运营文章
        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SYSTEM_ARTICLE.key, null);

        Map<String, AactivityStage> stageMap = new HashMap<String, AactivityStage>();
        List<AactivityStage> allStages = activityMybatisDao.getAactivityStage(1);//7天活动
        if (null != allStages && allStages.size() > 0) {
            for (AactivityStage s : allStages) {
                stageMap.put(String.valueOf(s.getStage()), s);
            }
        }
        AactivityStage stage1 = stageMap.get("1");//报名阶段
        AactivityStage stage2 = stageMap.get("2");//单人展示阶段
        AactivityStage stage3 = stageMap.get("3");//配对阶段
        AactivityStage stage5 = stageMap.get("5");//强配阶段
        AactivityStage stage4 = stageMap.get("4");//抢亲阶段

        Auser activityUser = null;
        Atopic singleKingdom = null;
        Atopic doubleKingdom = null;
        UserProfile userProfile = null;
        String userName = "";
        if (dto.getAuid() > 0) {
            activityUser = activityMybatisDao.getAuser(dto.getAuid());
            if (null != activityUser && activityUser.getUid() > 0) {
                userProfile = userService.getUserProfileByUid(activityUser.getUid());
                if (null != userProfile) {
                    userName = userProfile.getNickName();
                } else {
                    userName = activityUser.getMobile();
                }
                singleKingdom = activityMybatisDao.getAtopicByAuidAndSingle(dto.getAuid());
                if (null != singleKingdom) {
                    doubleKingdom = activityMybatisDao.getAtopicByAuidDouble(dto.getAuid());
                }
            }
        }

        int taskType = 0;
        if (null != doubleKingdom) {
            taskType = 2;
        } else if (null != singleKingdom) {
            taskType = 1;
        }
        if (taskType > 0) {
            //0.6 任务
            AtaskWithBLOBs lastTask = activityMybatisDao.getLastAtaskByType(1, taskType);
            if (null != lastTask && !StringUtils.isEmpty(lastTask.getMiliContent())) {
                params = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("content", lastTask.getMiliContent());
                params.add(map);
                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_TASK.key, params);


//				long topicId = 0;
//				if(lastTask.getType() == 1){
//					topicId = singleKingdom.getTopicId();
//				}else{
//					topicId = doubleKingdom.getTopicId();
//				}
//				AtaskUser ataskUser = activityMybatisDao.getAtaskUserByTopicIdAndTaskId(topicId, lastTask.getId());
//
//				params = new ArrayList<Map<String, String>>();
//				Map<String, String> map = new HashMap<String, String>();
//				if(null != ataskUser){//接受过任务
//					map.put("status", "status-msg-btn fs12 status-received");
//					map.put("statusName", "已接收");
//					map.put("param", "?tid="+lastTask.getId()+"&status=1");
//				}else{
//					map.put("status", "status-msg-btn fs12");
//					map.put("statusName", "待接收");
//					map.put("param", "?tid="+lastTask.getId()+"&status=2");
//				}
//				params.add(map);
//				String content = this.replaceMiliData(lastTask.getMiliContent(), params);
//				params = new ArrayList<Map<String, String>>();
//				map = new HashMap<String, String>();
//				map.put("content", content);
//				params.add(map);
//				this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_TASK.key, params);
            }
        }

        Date now = new Date();

        //1 报名阶段
        boolean isCheckStage1 = false;
        if (null != stage1 && stage1.getType() == 0) {
            isCheckStage1 = true;
            if (null != activityUser) {
                if (activityUser.getStatus() == 1) {//审核中
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    if (activityUser.getUid() == 0) {
                        map.put("userName", activityUser.getMobile());
                    } else {
                        map.put("userName", userName);
                    }
                    params.add(map);
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_1.key, params);
                } else if (activityUser.getStatus() == 3) {
                    if (null == singleKingdom && activityUser.getUid() > 0) {
                        params = new ArrayList<Map<String, String>>();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("userName", userName);
                        params.add(map);
                        if (dto.getIsApp() == 1) {//APP内
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, params);
                        } else {
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, params);
                        }
                    }
                }
            } else {
                if (dto.getIsApp() == 1) {//APP内
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_0_APP.key, null);
                } else {
                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_0_BROWSER.key, null);
                }
            }
            if (null != stage2) {
                if (now.compareTo(stage2.getStartTime()) < 0) {
                    long dayNum = DateUtil.getDaysBetween2Date(now, stage2.getStartTime());
                    if (dayNum > 0) {
                        params = new ArrayList<Map<String, String>>();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("dayCount", String.valueOf(dayNum));
                        params.add(map);
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.ACTIVITY_COUNTDOWN.key, params);
                    }
                }
            }
        }

        if (null != stage1) {
            if (now.compareTo(stage1.getEndTime()) > 0) {
                if (null == activityUser) {
                    if (dto.getIsApp() == 0) {//APP外部
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_END_BROWSER.key, null);
                    } else {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_END_APP.key, null);
                    }
                }

            }
        }

        //2单人阶段
        boolean isRec = false;
        if (null != stage2 && stage2.getType() == 0) {
            if (!isCheckStage1) {
                isCheckStage1 = true;
                if (null != activityUser && activityUser.getStatus() == 3 && null == singleKingdom && activityUser.getUid() > 0) {
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userName", userName);
                    params.add(map);
                    if (dto.getIsApp() == 1) {//APP内
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, params);
                    } else {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, params);
                    }
                }
            }
            if (dto.getIsApp() == 1) {//APP内才有的消息
                if (null != singleKingdom) {//存在单人王国
                    Map<String, Object> singleTopic = liveForActivityDao.getTopicById(singleKingdom.getTopicId());
                    if (null != singleTopic && null == doubleKingdom) {
                        Long updateTime = (Long) singleTopic.get("long_time");
                        if ((now.getTime() - updateTime) / (60 * 60 * 1000l) >= 12) {
                            params = new ArrayList<Map<String, String>>();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("topicId", String.valueOf(singleKingdom.getTopicId()));
                            map.put("userName", userName);
                            map.put("topicTitle", (String) singleTopic.get("title"));
                            map.put("topicImage", Constant.QINIU_DOMAIN_COMMON + "/" + (String) singleTopic.get("live_image"));
                            map.put("lastUpdateTime", DateUtil.date2string(new Date(updateTime), "yyyy-MM-dd HH:mm:ss"));
                            params.add(map);
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.UPDATE_SINGLE_KINGDOM.key, params);
                        }
                    }
                    if (null == doubleKingdom) {
                        int searchSex = 0;
                        if (userProfile.getGender() == 0) {
                            searchSex = 1;
                        }
                        this.genRecUserByTime(respDTO, miliMap, now, dto.getAuid(), singleKingdom.getUid(), this.isForce(stage5), searchSex, 1, userName);//关注
                        isRec = true;
                    }
                }
            }
        }

        //3 配对阶段
        boolean isDoubleCheck = false;
        if (null != stage3 && stage3.getType() == 0) {
            if (!isCheckStage1) {
                isCheckStage1 = true;
                if (null != activityUser && activityUser.getStatus() == 3 && null == singleKingdom && activityUser.getUid() > 0) {
                    params = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userName", userName);
                    params.add(map);
                    if (dto.getIsApp() == 1) {//APP内
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, params);
                    } else {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, params);
                    }
                }
            }
            if (dto.getIsApp() == 1) {//APP内才有的消息
                if (null != singleKingdom) {//存在单人王国
                    if (null == doubleKingdom) {//没有双人王国
                        if (!isRec) {
                            int searchSex = 0;
                            if (userProfile.getGender() == 0) {
                                searchSex = 1;
                            }
                            this.genRecUserByTime(respDTO, miliMap, now, dto.getAuid(), singleKingdom.getUid(), this.isForce(stage5), searchSex, 2, userName);//配对
                        }

                        List<AdoubleTopicApply> applyList = activityMybatisDao.getOptApplyByUidAndType(singleKingdom.getUid(), 1, 5);
                        if (null != applyList && applyList.size() > 0) {
                            params = new ArrayList<Map<String, String>>();
                            for (AdoubleTopicApply a : applyList) {
                                long uid = a.getUid();
                                if (a.getUid().longValue() == singleKingdom.getUid().longValue()) {//我发出的
                                    uid = a.getTargetUid();
                                }
                                UserProfile up = userService.getUserProfileByUid(uid);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                map.put("uid", String.valueOf(uid));
                                map.put("nickName", up.getNickName());
                                map.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + up.getAvatar());
                                map.put("display", "inline-block");
                                params.add(map);
                            }

                            if (params.size() < 5) {
                                int cc = 5 - params.size();
                                for (int i = 0; i < cc; i++) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("display", "none");
                                    params.add(map);
                                }
                            }

                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_DOUBLE_APPLY.key, params);
                        } else {
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_DOUBLE_APPLY.key, null);
                        }
                    } else {//有双人王国
                        isDoubleCheck = true;
                        Map<String, Object> doubleTopic = liveForActivityDao.getTopicById(doubleKingdom.getTopicId());
                        if (null != doubleTopic) {
                            UserProfile up = userService.getUserProfileByUid(doubleKingdom.getUid2());
                            Long updateTime = (Long) doubleTopic.get("long_time");
                            if ((now.getTime() - updateTime) / (60 * 60 * 1000l) >= 12) {
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("topicId", String.valueOf(doubleKingdom.getTopicId()));
                                map.put("userName", userName);
                                map.put("topicTitle", (String) doubleTopic.get("title"));
                                map.put("topicImage", Constant.QINIU_DOMAIN_COMMON + "/" + (String) doubleTopic.get("live_image"));
                                map.put("lastUpdateTime", DateUtil.date2string(new Date(updateTime), "yyyy-MM-dd HH:mm:ss"));
                                map.put("otherName", up.getNickName());
                                params.add(map);
                                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.UPDATE_DOUBLE_KINGDOM.key, params);
                            }

                            long days = DateUtil.getDaysBetween2Date((Date) doubleTopic.get("create_time"), now);
                            if (days == 0) {
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("topicId", String.valueOf(doubleKingdom.getTopicId()));
                                map.put("otherName", up.getNickName());
                                map.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + userProfile.getAvatar());
                                map.put("otherAvatar", Constant.QINIU_DOMAIN_COMMON + "/" + up.getAvatar());
                                params.add(map);
                                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_DOUBLE_KINGDOM.key, params);
                            } else {
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("day", String.valueOf(days + 1));
                                map.put("topicId", String.valueOf(doubleKingdom.getTopicId()));
                                params.add(map);
                                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_DOUBLE_KINGDOM_2.key, params);
                            }
                        }
                    }
                }
            }
        }
//		else{
//			if(null != stage3 && stage3.getType() == 1){
//				//第三阶段已关闭
//				//判断有没有强配
//				if(null != activityUser && activityUser.getUid() > 0){
//					AforcedPairing fp = activityMybatisDao.getAforcedPairingForUser(activityUser.getUid());
//					if(null != fp && fp.getStatus() == 1){
//						params = new ArrayList<Map<String, String>>();
//						Map<String, String> map = new HashMap<String, String>();
//						map.put("userName", userName);
//						params.add(map);
//						this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FORCED_PAIRING_END.key, params);
//						fp.setStatus(4);//只能看一次
//						activityMybatisDao.updateAforcedPairing(fp);
//					}
//				}
//			}
//		}

        //强配
        if (dto.getIsApp() == 1) {//APP内才有的消息
            if (null != singleKingdom) {//存在单人王国
                if (null == doubleKingdom) {//没有双人王国
                    if (isForce(stage5)) {//强配阶段
                        AforcedPairing fp = activityMybatisDao.getAforcedPairingForUser(singleKingdom.getUid());
                        if (null == fp) {
                            //没有申请过，则提示可以强配
                            params = new ArrayList<Map<String, String>>();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("uid", String.valueOf(singleKingdom.getUid()));
                            map.put("userName", userName);
                            long hour = DateUtil.getHoursBetween2Date(now, stage3.getEndTime());
                            map.put("countDown", String.valueOf(hour));
                            params.add(map);
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FORCED_PAIRING.key, params);
                        } else {
                            if (fp.getStatus() == 1) {
                                //申请中，则提示申请中
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                params.add(map);
                                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FORCED_PAIRING_1.key, params);
                            } else if (fp.getStatus() == 2) {
                                //强配成功，展示可以创建双人王国
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                long targetUid = fp.getUid();
                                if (singleKingdom.getUid().longValue() == fp.getUid().longValue()) {
                                    targetUid = fp.getTargetUid();
                                }
                                UserProfile up = userService.getUserProfileByUid(targetUid);
                                map.put("otherName", up.getNickName());
                                map.put("otherAvatar", Constant.QINIU_DOMAIN_COMMON + "/" + up.getAvatar());
                                map.put("otherUid", String.valueOf(targetUid));
                                Atopic otherAtopic = activityMybatisDao.getAtopicByType(targetUid, 1);
                                if (null != otherAtopic) {
                                    Map<String, Object> otherSingleTopic = liveForActivityDao.getTopicById(otherAtopic.getTopicId());
                                    if (null != otherSingleTopic) {
                                        map.put("otherTopicId", String.valueOf(otherSingleTopic.get("id")));
                                        map.put("otherTopicName", String.valueOf(otherSingleTopic.get("title")));
                                        map.put("otherTopicImage", Constant.QINIU_DOMAIN_COMMON + "/" + String.valueOf(otherSingleTopic.get("live_image")));
                                        params.add(map);
                                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FORCED_PAIRING_2.key, params);
                                    }
                                }
                            } else {
                                //不需要强配的人，则不需要提示啥了
                            }
                        }
                    } else if (null != stage5 && stage5.getType() == 1) {
                        if (now.compareTo(stage5.getEndTime()) > 0 && null != activityUser) {
                            AforcedPairing fp = activityMybatisDao.getAforcedPairingForUser(activityUser.getUid());
                            if (null != fp && fp.getStatus() == 1) {
                                params = new ArrayList<Map<String, String>>();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                params.add(map);
                                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.FORCED_PAIRING_END.key, params);
                                fp.setStatus(4);//只能看一次
                                activityMybatisDao.updateAforcedPairing(fp);
                            }
                        }
                    }
                }
            }
        }


        //4抢亲阶段
        if (null != stage4 && stage4.getType() == 0) {
            if (!isCheckStage1) {
                isCheckStage1 = true;
                if (null != activityUser && activityUser.getStatus() == 3 && null == singleKingdom && activityUser.getUid() > 0) {
                    if (dto.getIsApp() == 1) {//APP内
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_APP.key, null);
                    } else {
                        this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.SIGNUP_STATUS_2_BROWSER.key, null);
                    }
                }
            }

            if (dto.getIsApp() == 1) {//APP内才有的消息
                if (null != singleKingdom) {//存在单人王国
                    if (null != doubleKingdom) {
                        Map<String, Object> doubleTopic = null;
                        if (!isDoubleCheck) {
                            isDoubleCheck = true;
                            doubleTopic = liveForActivityDao.getTopicById(doubleKingdom.getTopicId());
                            if (null != doubleTopic) {
                                Long updateTime = (Long) doubleTopic.get("long_time");
                                if ((now.getTime() - updateTime) / (60 * 60 * 1000l) >= 12) {
                                    params = new ArrayList<Map<String, String>>();
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("topicId", String.valueOf(doubleKingdom.getTopicId()));
                                    params.add(map);
                                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.UPDATE_DOUBLE_KINGDOM.key, params);
                                }
                            }
                        }

                        //看看有没有人抢我
                        List<AdoubleTopicApply> applyList = activityMybatisDao.getApply2MeByType(doubleKingdom.getUid(), 2, 3);
                        if (null != applyList && applyList.size() > 0) {
                            params = new ArrayList<Map<String, String>>();
                            for (AdoubleTopicApply a : applyList) {
                                long uid = a.getUid();
                                if (a.getUid().longValue() == singleKingdom.getUid().longValue()) {//我发出的
                                    uid = a.getTargetUid();
                                }
                                UserProfile up = userService.getUserProfileByUid(uid);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                map.put("uid", String.valueOf(uid));
                                map.put("nickName", up.getNickName());
                                map.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + up.getAvatar());
                                map.put("display", "inline-block");
                                params.add(map);
                            }
                            if (params.size() < 3) {
                                int cc = 3 - params.size();
                                for (int i = 0; i < cc; i++) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("display", "none");
                                    params.add(map);
                                }
                            }
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_ROB_BRIDE_2.key, params);
                        } else {
                            //没人抢
                            if (null == doubleTopic) {
                                doubleTopic = liveForActivityDao.getTopicById(doubleKingdom.getTopicId());
                            }

                            params = new ArrayList<Map<String, String>>();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("topicId", doubleTopic.get("id").toString());
                            params.add(map);
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.NO_ROB_BRIDE.key, params);
                        }
                    } else {//没有双人王国
                        //获取抢亲申请信息
                        List<AdoubleTopicApply> applyList = activityMybatisDao.getOptApplyByUidAndType(singleKingdom.getUid(), 2, 3);
                        if (null != applyList && applyList.size() > 0) {
                            params = new ArrayList<Map<String, String>>();
                            for (AdoubleTopicApply a : applyList) {
                                long uid = a.getUid();
                                if (a.getUid().longValue() == singleKingdom.getUid().longValue()) {//我发出的
                                    uid = a.getTargetUid();
                                }
                                UserProfile up = userService.getUserProfileByUid(uid);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("userName", userName);
                                map.put("uid", String.valueOf(uid));
                                map.put("nickName", up.getNickName());
                                map.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + up.getAvatar());
                                map.put("display", "inline-block");
                                params.add(map);
                            }
                            if (params.size() < 3) {
                                int cc = 3 - params.size();
                                for (int i = 0; i < cc; i++) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("display", "none");
                                    params.add(map);
                                }
                            }
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.HAS_ROB_BRIDE.key, params);
                        } else {
                            params = new ArrayList<Map<String, String>>();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("userName", userName);
                            params.add(map);
                            this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.CAN_ROB_BRIDE.key, params);
                        }
                    }
                }
            }
        }

        //米粒排序
        if (respDTO.getResult().size() > 1) {
            Collections.sort(respDTO.getResult(), new Comparator<Show7DayMiliDTO.MiliElement>() {
                public int compare(Show7DayMiliDTO.MiliElement e1, Show7DayMiliDTO.MiliElement e2) {
                    if (e1.getOrder() == e2.getOrder()) {
                        return 0;
                    } else if (e1.getOrder() > e2.getOrder()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }

        return Response.success(respDTO);
    }

    private boolean isForce(AactivityStage stage5) {
        if (null != stage5 && stage5.getType() == 0) {
            return true;
        }
        return false;
    }

//	private boolean isForce(AactivityStage stage3, Date now){
//		if(null != stage3 && stage3.getType() == 0){
//			if(DateUtil.isSameDay(stage3.getEndTime(), now)){//配对的最后一天为抢配阶段
//				return true;
//			}
//		}
//
//		return false;
//	}

    private void genRecUserByTime(Show7DayMiliDTO respDTO, Map<String, List<AmiliData>> miliMap, Date date, long auid, long uid, boolean isForce, int searchSex, int stageType, String userName) {
        boolean isOut = false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);//0-23
        String timeKey = null;
        if (hour < 8) {//8点前
            isOut = true;
            timeKey = DateUtil.date2string(DateUtil.addDay(date, -1), "yyyyMMdd") + "22";
        } else {
            String hourStr = null;
            if (hour % 2 > 0) {
                hourStr = String.valueOf(hour - 1);
            } else {
                hourStr = String.valueOf(hour);
            }
            if (hourStr.length() == 1) {
                hourStr = "0" + hourStr;
            }
            timeKey = DateUtil.date2string(date, "yyyyMMdd") + hourStr;
        }

        ArecommendUser recUser = activityMybatisDao.getArecommendUserByRecTimeKey(timeKey, auid);
        if (null != recUser) {
            //有记录了，说明已经看过了，不需要再展现了
            List<Long> uids = new ArrayList<Long>();
            String[] tmp = recUser.getRecUsers().split(",");
            if (null != tmp && tmp.length > 0) {
                for (String t : tmp) {
                    if (!StringUtils.isEmpty(t)) {
                        uids.add(Long.valueOf(t));
                        if (uids.size() >= 3) {
                            break;
                        }
                    }
                }
            }
            if (uids.size() > 0) {
                List<UserProfile> uList = userService.getUserProfilesByUids(uids);
                if (null != uList && uList.size() > 0) {
                    List<Map<String, String>> params = new ArrayList<Map<String, String>>();
                    Map<String, String> pMap = null;
                    for (UserProfile u : uList) {
                        pMap = new HashMap<String, String>();
                        pMap.put("count", String.valueOf(tmp.length));
                        pMap.put("userName", userName);
//						pMap.put("timeKey", timeKey.substring(0,4)+"-"+timeKey.substring(4,6)+"-"+timeKey.substring(6,8)+" "+timeKey.substring(8,10)+":00:00");
                        int m = Integer.valueOf(timeKey.substring(4, 6)).intValue();
                        int d = Integer.valueOf(timeKey.substring(6, 8)).intValue();
                        int h = Integer.valueOf(timeKey.substring(8, 10)).intValue();
                        pMap.put("timeKey", m + "月" + d + "日" + h + "点");
                        pMap.put("uid", String.valueOf(u.getUid()));
                        pMap.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + u.getAvatar());
                        pMap.put("v_lv", String.valueOf(u.getvLv()));
                        pMap.put("display", "inline-block");
                        params.add(pMap);
                    }
                    if (params.size() < 3) {
                        int cc = 3 - params.size();
                        for (int i = 0; i < cc; i++) {
                            pMap = new HashMap<String, String>();
                            pMap.put("display", "none");
                            params.add(pMap);
                        }
                    }

                    this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.RECOMMEND_USER_1.key, params);
                }
            }

            return;
        }

        log.info("sql start...");
        //这个sql可能是个坑，待优化
        List<Map<String, Object>> list = liveForActivityDao.getRecSingleUser(searchSex, uid, 6);
        log.info("sql end");
        if (null != list && list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> map : list) {
                sb.append(",").append(map.get("uid"));
            }
            String recUids = sb.toString().substring(1);
            recUser = new ArecommendUser();
            recUser.setAuid(auid);
            recUser.setCreateTime(date);
            recUser.setRecTimeKey(timeKey);
            recUser.setUid(uid);
            recUser.setType(stageType);
            recUser.setRecUsers(recUids);
            activityMybatisDao.saveArecommendUser(recUser);

            ArecommendUserDesc desc = null;
            for (Map<String, Object> map : list) {
                desc = new ArecommendUserDesc();
                desc.setAuid(auid);
                desc.setRecTimeKey(timeKey);
                desc.setType(stageType);
                desc.setUid(uid);
                desc.setRecUid((Long) map.get("uid"));
                activityMybatisDao.saveArecommendUserDesc(desc);
            }

            List<Map<String, String>> params = null;
            if (isOut) {
                params = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("userName", userName);
                params.add(map);
                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.RECOMMEND_USER_2.key, params);
            } else {
                params = new ArrayList<Map<String, String>>();
                Map<String, String> pMap = null;
                Map<String, Object> map = null;
                for (int i = 0; i < list.size() && i < 3; i++) {
                    map = list.get(i);
                    pMap = new HashMap<String, String>();
                    pMap.put("count", String.valueOf(list.size()));
                    pMap.put("userName", userName);
//					pMap.put("timeKey", timeKey.substring(0,4)+"-"+timeKey.substring(4,6)+"-"+timeKey.substring(6,8)+" "+timeKey.substring(8,10)+":00:00");
                    int m = Integer.valueOf(timeKey.substring(4, 6)).intValue();
                    int d = Integer.valueOf(timeKey.substring(6, 8)).intValue();
                    int h = Integer.valueOf(timeKey.substring(8, 10)).intValue();
                    pMap.put("timeKey", m + "月" + d + "日" + h + "点");
                    pMap.put("uid", map.get("uid").toString());
                    pMap.put("avatar", Constant.QINIU_DOMAIN_COMMON + "/" + (String) map.get("avatar"));
                    pMap.put("v_lv", map.get("v_lv").toString());
                    pMap.put("display", "inline-block");
                    params.add(pMap);
                }
                if (params.size() < 3) {
                    int cc = 3 - params.size();
                    for (int i = 0; i < cc; i++) {
                        pMap = new HashMap<String, String>();
                        pMap.put("display", "none");
                        params.add(pMap);
                    }
                }

                this.genMili(respDTO, miliMap, Specification.ActivityMiliDataKey.RECOMMEND_USER_1.key, params);
            }
        } else {
            log.info("no user recommend![" + auid + "]");
        }
    }


    private void genMili(Show7DayMiliDTO respDTO, Map<String, List<AmiliData>> miliMap, String key, List<Map<String, String>> params) {
        List<AmiliData> miliList = miliMap.get(key);
        if (null != miliList && miliList.size() > 0) {
            Show7DayMiliDTO.MiliElement e = null;
            for (AmiliData m : miliList) {
                e = new Show7DayMiliDTO.MiliElement();
                e.setContent(replaceMiliData(m.getContent(), params));
                e.setLinkUrl(m.getLinkUrl());
                e.setOrder(m.getOrderby());
                e.setType(m.getType());
                respDTO.getResult().add(e);
            }
        }
    }

    private String replaceMiliData(String content, List<Map<String, String>> params) {
        if (null == params || params.size() == 0) {
            return content;
        }

        boolean isOne = true;
        if (params.size() > 1) {
            isOne = false;
        }

        int i = 1;
        for (Map<String, String> map : params) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (isOne) {
                    content = content.replace("#{" + entry.getKey() + "}#", entry.getValue());
                    content = content.replace("#{" + entry.getKey() + "1}#", entry.getValue());
                } else {
                    content = content.replace("#{" + entry.getKey() + String.valueOf(i) + "}#", entry.getValue());
                }
            }
            i++;
        }

        return content;
    }

    @Override
    public Response recommendHistory(long auid, int page, int pageSize) {
        ShowRecommendHistoryDTO srhDTO = new ShowRecommendHistoryDTO();
        try {
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1) {
                pageSize = 10;
            }
            int start = (page - 1) * pageSize;
            srhDTO.setTotalCount(activityMybatisDao.countArecommendUserPageByAuid(auid));
            srhDTO.setTotalPage(srhDTO.getTotalCount() % pageSize == 0 ? srhDTO.getTotalCount() / pageSize : srhDTO.getTotalCount() / pageSize + 1);
            List<ArecommendUser> list = activityMybatisDao.getArecommendUserPageByAuid(auid, start, pageSize);
            if (null != list && list.size() > 0) {
                long currentUid = list.get(0).getUid();
                List<Long> uidList = new ArrayList<Long>();
                String[] uids = null;
                for (ArecommendUser au : list) {
                    if (!StringUtils.isEmpty(au.getRecTimeKey())
                            && !StringUtils.isEmpty(au.getRecUsers())) {
                        uids = au.getRecUsers().split(",");
                        if (null != uids && uids.length > 0) {
                            for (String u : uids) {
                                long uid = Long.valueOf(u);
                                uidList.add(uid);
                            }
                        }
                    }
                }

                Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
                Map<String, Map<String, Object>> singleTopicInfoMap = new HashMap<String, Map<String, Object>>();
                Map<String, Map<String, Object>> doubleTopicInfoMap = new HashMap<String, Map<String, Object>>();
                Map<String, AdoubleTopicApply> applyMap = new HashMap<String, AdoubleTopicApply>();
                if (uidList.size() > 0) {
                    List<UserProfile> userProfileList = userService.getUserProfilesByUids(uidList);
                    if (null != userProfileList && userProfileList.size() > 0) {
                        for (UserProfile up : userProfileList) {
                            userProfileMap.put(String.valueOf(up.getUid()), up);
                        }
                    }

                    List<Map<String, Object>> singleTopicInfoList = liveForActivityDao.getAtopicInfoByUids(uidList, 1);
                    if (null != singleTopicInfoList && singleTopicInfoList.size() > 0) {
                        for (Map<String, Object> st : singleTopicInfoList) {
                            singleTopicInfoMap.put(String.valueOf(st.get("uid")), st);
                        }
                    }

                    List<Map<String, Object>> doubleTopicInfoList = liveForActivityDao.getAtopicInfoByUids(uidList, 2);
                    if (null != doubleTopicInfoList && doubleTopicInfoList.size() > 0) {
                        for (Map<String, Object> st : doubleTopicInfoList) {
                            doubleTopicInfoMap.put(String.valueOf(st.get("uid")), st);
                        }
                    }

                    List<AdoubleTopicApply> applyList = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUids(currentUid, uidList, 1);
                    if (null != applyList && applyList.size() > 0) {
                        for (AdoubleTopicApply a : applyList) {
                            applyMap.put(String.valueOf(a.getTargetUid()), a);
                        }
                    }
                }

                ShowRecommendHistoryDTO.RecommendElement e = null;
                ShowRecommendHistoryDTO.RecommendUserItem item = null;
                UserProfile userProfile = null;
                Map<String, Object> singleTopicInfo = null;
                for (ArecommendUser au : list) {
                    if (!StringUtils.isEmpty(au.getRecTimeKey())
                            && !StringUtils.isEmpty(au.getRecUsers())) {
                        e = new ShowRecommendHistoryDTO.RecommendElement();
                        e.setRecommendTime(DateUtil.string2date(au.getRecTimeKey(), "yyyyMMddHH"));
                        uids = au.getRecUsers().split(",");
                        if (null != uids && uids.length > 0) {
                            for (String u : uids) {
                                long uid = Long.valueOf(u);
                                userProfile = userProfileMap.get(String.valueOf(uid));
                                if (null != userProfile) {
                                    item = new ShowRecommendHistoryDTO.RecommendUserItem();
                                    item.setUid(userProfile.getUid());
                                    item.setNickName(userProfile.getNickName());
                                    item.setAvatar(Constant.QINIU_DOMAIN_COMMON + "/" + userProfile.getAvatar());
                                    item.setSex(userProfile.getGender());
                                    item.setVlv(userProfile.getvLv());

                                    singleTopicInfo = singleTopicInfoMap.get(String.valueOf(uid));
                                    if (null != singleTopicInfo) {
                                        item.setTopicId((Long) singleTopicInfo.get("topic_id"));
                                        item.setTitle((String) singleTopicInfo.get("title"));
                                        item.setConverImage(Constant.QINIU_DOMAIN_COMMON + "/" + (String) singleTopicInfo.get("live_image"));
                                        item.setHot((Long) singleTopicInfo.get("hot"));

                                        if (au.getType() == 1) {
                                            item.setStatus(4);
                                        } else {
                                            //设置状态
                                            if (doubleTopicInfoMap.get(String.valueOf(uid)) != null) {
                                                item.setStatus(3);
                                            } else if (applyMap.get(String.valueOf(uid)) != null) {
                                                item.setStatus(2);
                                            } else {
                                                item.setStatus(1);
                                            }
                                        }
                                    } else {
                                        item.setStatus(0);//单人王国不存在
                                    }
                                    e.getUserList().add(item);
                                }
                            }
                        }
                        if (e.getUserList().size() > 0) {
                            srhDTO.getResult().add(e);
                        }
                    }
                }
            }

            return Response.success(srhDTO);
        } catch (Exception e) {
            log.error("查询失败", e);
            return Response.failure("查询失败");
        }
    }

    @Override
    public Response optForcedPairing(long uid, int action) {
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        if (null == userProfile) {
            log.info("user is not exist!");
            return Response.failure("用户不存在");
        }
        Auser auser = activityMybatisDao.getAuserByUid(uid);
        if (null == auser) {
            log.info("uid[" + uid + "] is not a activity user");
            return Response.failure("当前用户不是报名用户");
        }
        AforcedPairing fp = activityMybatisDao.getAforcedPairingForUser(uid);
        if (null != fp) {//已经处理过了
            log.info("repeat opt");
            return Response.success();
        }
        if (action == 2) {
            fp = new AforcedPairing();
            fp.setAuid(auser.getId());
            fp.setUid(uid);
            fp.setStatus(3);//取消适配
            fp.setSex(userProfile.getGender());
            fp.setTargetAuid(0l);
            fp.setTargetUid(0l);
            fp.setCreateTime(new Date());
            activityMybatisDao.saveAforcedPairing(fp);
        } else {
            //申请强配，如果数据库中有正在等待强配的异性，则直接配上
            boolean isSucc = false;

            int searchSex = 0;
            if (userProfile.getGender() == 0) {
                searchSex = 1;
            }

            int n = 1;
            while (n <= 100) {//最多循环100次。。防止死循环
                fp = activityMybatisDao.getOneAforcedPairingByStatusAndSex(1, searchSex);
                if (null == fp) {
                    //没有强配
                    break;
                }
                int updateRow = activityMybatisDao.updateAforcedPairing2Success(fp.getId(), uid, auser.getId());
                if (updateRow == 0) {//说明没有更新成功被别人更新去了。。所以得重新循环来
                    n++;
                    continue;
                } else {
                    //更新成功
                    isSucc = true;
                    break;
                }
            }

            if (!isSucc) {
                //没配到的则记录下来等待别人来配
                fp = new AforcedPairing();
                fp.setAuid(auser.getId());
                fp.setUid(uid);
                fp.setStatus(1);//强配中
                fp.setSex(userProfile.getGender());
                fp.setTargetAuid(0l);
                fp.setTargetUid(0l);
                fp.setCreateTime(new Date());
                activityMybatisDao.saveAforcedPairing(fp);
            }
        }
        return Response.success();
    }

    @Override
    public Response getTaskList(long uid, int page, int pageSize) {
        ShowTasksDTO stDTO = new ShowTasksDTO();
        Auser auser = activityMybatisDao.getAuserByUid(uid);
        if (null == auser) {
            log.info("user [" + uid + "] is not a activity user!");
            stDTO.setTotalCount(0);
            stDTO.setTotalPage(0);
            return Response.success(stDTO);
        }
        List<Long> topicIds = new ArrayList<Long>();
        int searchType = 0;
        Atopic doubleKingdom = activityMybatisDao.getAtopicByAuidDouble(auser.getId());
        if (null != doubleKingdom) {
            searchType = 2;
            topicIds.add(doubleKingdom.getTopicId());
        }
        Atopic singleKingdom = activityMybatisDao.getAtopicByAuidAndSingle(auser.getId());
        if (null != singleKingdom) {
            topicIds.add(singleKingdom.getTopicId());
            if (searchType == 0) {
                searchType = 1;
            }
        }

        if (searchType == 0) {
            stDTO.setTotalCount(0);
            stDTO.setTotalPage(0);
            return Response.success(stDTO);
        } else {
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1) {
                pageSize = 10;
            }
            int start = (page - 1) * pageSize;
            stDTO.setTotalCount(activityMybatisDao.countAtaskPageByType(1, searchType));
            stDTO.setTotalPage(stDTO.getTotalCount() % pageSize == 0 ? stDTO.getTotalCount() / pageSize : stDTO.getTotalCount() / pageSize + 1);
            List<AtaskWithBLOBs> list = activityMybatisDao.getAtaskPageByType(1, searchType, start, pageSize);
            if (null != list && list.size() > 0) {
                List<Long> taskIds = new ArrayList<Long>();
                for (Atask t : list) {
                    taskIds.add(t.getId());
                }
                List<AtaskUser> ataskUserList = activityMybatisDao.getAtaskUsersByTopicIdsAndTaskIds(topicIds, taskIds);
                Map<String, AtaskUser> map = new HashMap<String, AtaskUser>();
                if (null != ataskUserList && ataskUserList.size() > 0) {
                    for (AtaskUser atu : ataskUserList) {
                        map.put(String.valueOf(atu.getTaskId()), atu);
                    }
                }

                List<Map<String, String>> params = null;
                Map<String, String> pMap = null;
                ShowTasksDTO.TaskElement e = null;
                AtaskUser ataskUser = null;
                for (AtaskWithBLOBs t : list) {
                    e = new ShowTasksDTO.TaskElement();
                    e.setId(t.getId());
                    e.setTitle(t.getTitle());
                    e.setLinkUrl(t.getLinkUrl());
                    e.setType(t.getType());
                    params = new ArrayList<Map<String, String>>();
                    pMap = new HashMap<String, String>();
                    ataskUser = map.get(String.valueOf(t.getId()));
                    if (null != ataskUser) {
                        e.setStatus(1);//有说明已经接受过了（如果是双人王国，对方接受了，自己也就接受了）
                        pMap.put("status", "status-msg-btn fs12 status-received");
                        pMap.put("statusName", "已接收");
                        pMap.put("param", "?tid=" + t.getId() + "&status=1");
                    } else {
                        e.setStatus(2);//未接受
                        pMap.put("status", "status-msg-btn fs12");
                        pMap.put("statusName", "待接收");
                        pMap.put("param", "?tid=" + t.getId() + "&status=2");
                    }
                    params.add(pMap);
                    e.setContent(this.replaceMiliData(t.getContent(), params));
                    stDTO.getResult().add(e);
                }
            }
        }

        return Response.success(stDTO);
    }

    @Override
    public Response acceptTask(long tid, long uid) {
        AtaskWithBLOBs atask = activityMybatisDao.getAtaskById(tid);
        if (null == atask) {
            return Response.failure(ResponseStatus.ACCEPT_TASK_ERROR.status, "任务不存在");
        }

        Atopic atopic = null;
        if (atask.getType() == 1) {//单人王国
            atopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid, 1);
        } else {//双人王国
            atopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid, 2);
        }
        if (null == atopic) {
            return Response.failure(ResponseStatus.ACCEPT_TASK_ERROR.status, atask.getType() == 1 ? "没有对应的单人王国" : "没有对应的双人王国");
        }

        AtaskUser ataskUser = activityMybatisDao.getAtaskUserByTopicIdAndTaskId(atopic.getTopicId(), atask.getId());
        if (null != ataskUser) {
            //已经接收过了，则直接返回成功
            return Response.success();
        }

        Auser auser = activityMybatisDao.getAuserByUid(uid);
        if (null == auser) {
            return Response.failure(ResponseStatus.ACCEPT_TASK_ERROR.status, "当前用户不是报名用户");
        }

        ataskUser = new AtaskUser();
        ataskUser.setAuid(auser.getId());
        ataskUser.setCreateTime(new Date());
        ataskUser.setFragmentId(0l);
        ataskUser.setTaskId(atask.getId());
        ataskUser.setTopicId(atopic.getTopicId());
        ataskUser.setUid(uid);
        activityMybatisDao.saveAtaskUser(ataskUser);

        //接受完任务需要向对应王国里插一个图片，有接受人发起
        Map<String, String> param = new HashMap<String, String>();
        param.put("topic_id", String.valueOf(atopic.getTopicId()));
        param.put("uid", String.valueOf(uid));
        param.put("fragment_image", atask.getCardimage());
        param.put("fragment", "");
        param.put("type", "0");
        param.put("content_type", "1");
        param.put("at_uid", "0");
        param.put("status", "1");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"w\":").append(atask.getCardimageW()).append(",\"h\":").append(atask.getCardimageH()).append("}");
        param.put("extra", sb.toString());
        liveForActivityDao.insertTopicFragment(param);

        Calendar calendar = Calendar.getInstance();
        liveForActivityDao.updateTopicLongtime(atopic.getTopicId(), calendar.getTimeInMillis());
        
        //接任务王国赞+500
        liveForActivityDao.updateContentAddLike(atopic.getTopicId(), 500);

        return Response.success();
    }

    @Override
    public Response userTaskStatus(long tid, long uid) {
        Atask atask = activityMybatisDao.getAtaskById(tid);
        if (null == atask) {
            return Response.failure(ResponseStatus.USER_TASK_STATUS_QUERY_ERROR.status, "任务不存在");
        }

        UserTaskStatusDTO dto = new UserTaskStatusDTO();
        Atopic atopic = null;
        if (atask.getType() == 1) {//单人王国
            atopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid, 1);
        } else {//双人王国
            atopic = activityMybatisDao.getAtopicByUidandTypeBrid(uid, 2);
        }
        if (null == atopic) {
            dto.setStatus(2);//未接受
        } else {
            AtaskUser ataskUser = activityMybatisDao.getAtaskUserByTopicIdAndTaskId(atopic.getTopicId(), atask.getId());
            if (null != ataskUser) {
                //已经接收过了，则直接返回成功
                dto.setStatus(1);
            } else {
                dto.setStatus(2);//未接受
            }
        }

        return Response.success(dto);
    }

    public List<Long> getParingUser() {
        return liveForActivityDao.getPairingUser();
    }

    @Override
    public Response bindNotice() {
        //获取审核通过，但是没有绑定uid
        List<Auser> list = activityMybatisDao.getNoBindAuserList();
        if (null != list && list.size() > 0) {
            log.info("total [" + list.size() + "] user");
            AactivityStage stage2 = activityMybatisDao.getAactivityStageByStage2(1, 2);
            List<String> msgList = new ArrayList<String>();
            if (null != stage2) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(stage2.getStartTime());
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                msgList.add(String.valueOf(month));
                msgList.add(String.valueOf(day));
            } else {
                msgList.add("");
                msgList.add("");
            }

            List<String> mobileList = Lists.newArrayList();
            mobileList.add("18916103465");//默认给一个内部的手机号
            for (Auser auser : list) {
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());

                if (mobileList.size() >= 150) {
                    smsService.send7dayCommon("145621", mobileList, msgList);
                    log.info("send [" + mobileList.size() + "] user!");
                    mobileList.clear();
                }
            }
            if (mobileList.size() > 0) {
                smsService.send7dayCommon("145621", mobileList, msgList);
                log.info("send [" + mobileList.size() + "] user!");
            }
        }

        return Response.success();
    }
    
    @Override
    public List<String> getAllUserMobilesInApp(){
    	return liveForActivityDao.getAllUserMobilesInApp();
    }
    
    @Override
    public List<String> getAll7DayMobiles(){
    	List<Auser> list = activityMybatisDao.getAllAuditSuccessAuser();
    	if(null != list && list.size() > 0){
    		List<String> result = new ArrayList<String>();
    		for(Auser auser : list){
    			result.add(auser.getMobile());
    		}
    		return result;
    	}
    	return null;
    }

    @Override
    public Response noticeActivityStart() {
        List<Auser> list = activityMybatisDao.getAllAuditSuccessAuser();
        if (null != list && list.size() > 0) {
            log.info("total [" + list.size() + "] user");
            List<String> mobileList = Lists.newArrayList();
            mobileList.add("18916103465");//默认给一个内部的手机号
            for (Auser auser : list) {
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());

                if (mobileList.size() >= 150) {
                    smsService.send7dayCommon("145625", mobileList, null);
                    log.info("send [" + mobileList.size() + "] user!");
                    mobileList.clear();
                }
            }
            if (mobileList.size() > 0) {
                smsService.send7dayCommon("145625", mobileList, null);
                log.info("send [" + mobileList.size() + "] user!");
            }
        }

        return Response.success();
    }

    @Override
    public Response pairingNotice() {
        List<Auser> list = activityMybatisDao.getAllAuditSuccessAuser();
        if (null != list && list.size() > 0) {
            log.info("total [" + list.size() + "] user");
            List<String> mobileList = Lists.newArrayList();
            mobileList.add("18916103465");//默认给一个内部的手机号
            for (Auser auser : list) {
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());

                if (mobileList.size() >= 150) {
                    smsService.send7dayCommon("146662", mobileList, null);
                    log.info("send [" + mobileList.size() + "] user!");
                    mobileList.clear();
                }
            }
            if (mobileList.size() > 0) {
                smsService.send7dayCommon("146662", mobileList, null);
                log.info("send [" + mobileList.size() + "] user!");
            }
        }

        return Response.success();
    }

    @Override
    public Response operaBrid(long uid, int applyId, int operaStatus) {
        AdoubleTopicApply topicApply = activityMybatisDao.getAdoubleTopicApplyById(applyId);
        if (operaStatus == 2) {
            Atopic atopic = activityMybatisDao.getAtopicByAuidDoubleByUid(topicApply.getTargetUid());
            if (atopic != null) {
                //判断下发起方是否符合抢亲条件
                Atopic singleTopic = activityMybatisDao.getAtopicByType(topicApply.getUid(), 1);
                if (null != singleTopic) {
                    Atopic doubleTopic = activityMybatisDao.getAtopicByType(topicApply.getUid(), 2);
                    if (null == doubleTopic) {
                        topicApply.setStatus(operaStatus);
                        activityMybatisDao.updateAdoubleTopicApply(topicApply);
                        //强制离婚
                        Map map = Maps.newHashMap();
                        map.put("topicId", atopic.getTopicId());
                        activityMybatisDao.updateAtopicStatus(map);

                        List<AdoubleTopicApply> list = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid3(atopic.getUid(), atopic.getUid2());
                        List<AdoubleTopicApply> list2 = activityMybatisDao.getAdoubleTopicApplyByUidAndTargetUid3(atopic.getUid2(), atopic.getUid());
                        if (list != null && list.size() > 0) {
                            for (AdoubleTopicApply a : list) {
                                if (a.getStatus() != 4) {
                                    a.setStatus(4);
                                    activityMybatisDao.updateAdoubleTopicApply(a);
                                }
                            }
                        }
                        if (list2 != null && list2.size() > 0) {
                            for (AdoubleTopicApply a : list2) {
                                if (a.getStatus() != 4) {
                                    a.setStatus(4);
                                    activityMybatisDao.updateAdoubleTopicApply(a);
                                }
                            }
                        }

                        //也许是强配的结婚，所以也要查询下有没有强配的记录，有的话也要一并清除
                        AforcedPairing ap = activityMybatisDao.getAforcedPairingForUser(atopic.getUid());
                        if (null != ap) {
                            if (ap.getUid().longValue() == atopic.getUid().longValue() && ap.getTargetUid().longValue() == atopic.getUid2().longValue()) {
                                activityMybatisDao.deleteAforcedPairingById(ap.getId());
                            } else if (ap.getUid().longValue() == atopic.getUid2().longValue() && ap.getTargetUid().longValue() == atopic.getUid().longValue()) {
                                activityMybatisDao.deleteAforcedPairingById(ap.getId());
                            }//其他的是和别人的，可以不做处理
                        }

                        //同意抢亲需要通知前夫/前妻
                        UserProfile up = userService.getUserProfileByUid(atopic.getUid());
                        UserToken ut = userService.getUserTokenByUid(atopic.getUid2());
                        String msg = Specification.LinkPushType.ROB_AGREE.message.replaceAll("#\\{1\\}#", up.getNickName());
                        StringBuilder sb = new StringBuilder();
                        sb.append(activityWebUrl).append(Specification.LinkPushType.ROB_AGREE.linkUrl).append("?uid=");
                        sb.append(atopic.getUid2()).append("&token=").append(ut.getToken());
                        applicationEventBus.post(new LinkPushEvent(msg, sb.toString(), atopic.getUid2()));

                        return Response.success(ResponseStatus.BRID_IS_SUCCESS.status, ResponseStatus.BRID_IS_SUCCESS.message);
                    } else {
                        return Response.failure(500, "对方已经不符合抢请条件了");
                    }
                } else {
                    return Response.failure(500, "对方已经不符合抢请条件了");
                }
            } else {
                return Response.success(ResponseStatus.TARGET_NOT_CREATE_TOPIC.status, ResponseStatus.TARGET_NOT_CREATE_TOPIC.message);
            }
        } else if (operaStatus == 3) {
            //拒绝直接改变状态
            topicApply.setStatus(operaStatus);
            activityMybatisDao.updateAdoubleTopicApply(topicApply);
            return Response.success(ResponseStatus.BRID_IS_FAILURE.status, ResponseStatus.BRID_IS_FAILURE.message);
        } else if (operaStatus == 4) {
            topicApply.setStatus(operaStatus);
            activityMybatisDao.updateAdoubleTopicApply(topicApply);
            return Response.success(ResponseStatus.BRID_IS_DELETE.status, ResponseStatus.BRID_IS_DELETE.message);
        }
        return Response.failure("不支持的操作类型");
    }

    @Override
    public ShowActivity7DayUserStatDTO get7dayUserStat(String channel,
                                                       String code, String startTime, String endTime) {
        Map<String, Object> map = liveForActivityDao.get7dayUserStat(channel, code, startTime, endTime);
        ShowActivity7DayUserStatDTO dto = new ShowActivity7DayUserStatDTO();
        if (null != map) {
            dto.setTotalUser((Long) map.get("total"));
            dto.setManCount((Long) map.get("manCount"));
            dto.setWomanCount((Long) map.get("womanCount"));
            dto.setBindCount((Long) map.get("bindCount"));
        }
        return dto;
    }

    @Override
    public Response searchMiliDatas(String mkey, long activity, int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        ShowMiliDatasDTO dto = new ShowMiliDatasDTO();
        dto.setTotalCount(activityMybatisDao.countAmiliDataPage(mkey, activity));
        dto.setTotalPage(dto.getTotalCount() % pageSize == 0 ? (dto.getTotalCount() / pageSize) : (dto.getTotalCount() / pageSize + 1));

        int start = (page - 1) * pageSize;
        List<AmiliData> list = activityMybatisDao.getAmiliDataPage(mkey, activity, start, pageSize);
        if (null != list && list.size() > 0) {
            ShowMiliDatasDTO.MiliDataElement e = null;
            for (AmiliData a : list) {
                e = new ShowMiliDatasDTO.MiliDataElement();
                e.setId(a.getId());
                e.setMkey(a.getMkey());
                e.setContent(a.getContent());
                e.setOrderby(a.getOrderby());
                e.setStatus(a.getStatus());
                dto.getResult().add(e);
            }
        }

        return Response.success(dto);
    }

    @Override
    public AmiliData getAmiliDataById(long id) {
        return activityMybatisDao.getAmiliDataById(id);
    }

    @Override
    public void updateAmiliData(AmiliData data) {
        if (null != data.getId() && data.getId().longValue() > 0) {
            activityMybatisDao.updateAmiliData(data);
        }
    }

    @Override
    public void saveAmiliData(AmiliData data) {
        activityMybatisDao.saveAmiliData(data);
    }

    @Override
    public void deleteAkingDomByTopicId(long topicId) {
        liveForActivityDao.updateDeleteAkingdomByTopicId(topicId);
        //并且删除榜单表相关
        liveForActivityDao.deleteAkingdomListByTopicId(topicId);
    }

    @Override
    public List<AactivityStage> getAllStage(long activity) {
        return activityMybatisDao.getAllStage(activity);
    }

    public AactivityStage getAactivityStageById(long id) {
        return activityMybatisDao.getAactivityStageById(id);
    }

    public void updateAactivityStage(AactivityStage stage) {
        activityMybatisDao.updateAactivityStage(stage);
    }

    public Response getTaskPage(String title, long activityId, int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        int start = (page - 1) * pageSize;
        ShowActivity7DayTasksDTO dto = new ShowActivity7DayTasksDTO();
        dto.setTotalCount(activityMybatisDao.countAtaskPage(title, activityId));
        dto.setTotalPage(dto.getTotalCount() % pageSize == 0 ? dto.getTotalCount() / pageSize : (dto.getTotalCount() / pageSize + 1));

        List<AtaskWithBLOBs> list = activityMybatisDao.searchAtaskPage(title, activityId, start, pageSize);
        if (null != list && list.size() > 0) {
            ShowActivity7DayTasksDTO.TaskElement e = null;
            for (AtaskWithBLOBs t : list) {
                e = new ShowActivity7DayTasksDTO.TaskElement();
                e.setId(t.getId());
                e.setTitle(t.getTitle());
                e.setContent(t.getContent());
                e.setMiliContent(t.getMiliContent());
                e.setActivityId(t.getActivityId());
                e.setStatus(t.getStatus());
                e.setType(t.getType());
                e.setUpdateTime(t.getUpdateTime());
                dto.getResult().add(e);
            }
        }

        return Response.success(dto);
    }

    @Override
    public AtaskWithBLOBs getAtaskWithBLOBsById(long id) {
        return activityMybatisDao.getAtaskById(id);
    }

    @Override
    public void updateAtaskWithBLOBs(AtaskWithBLOBs task){
        activityMybatisDao.updateAtaskWithBLOBs(task);
    }
    
    @Override
    public List<TopicItem> getActivityTopicIds(long activityId){
    	return liveForActivityDao.getActivityTopicIds(activityId);
    }

    @Override
    public List<Long> get7dayTopicIdsByType(int type){
        return liveForActivityDao.get7DayTopicIdsByType(type);
    }


    @Override
    public List<Long> getSingleHotByDoubleTopicId(long doubleTopicId){
        return liveForActivityDao.getSingleHotsByDoubleTopicId(doubleTopicId);
    }
    
    @Override
    public void batchInsertKingdomList(List<KingdomHotDTO> list){
    	if(null == list || list.size() == 0){
    		return;
    	}
    	liveForActivityDao.batchInsertKingdomList(list);
    }
    
    @Override
    public void batchUpdateKingdomHot(List<KingdomHotDTO> list){
    	if(null == list || list.size() == 0){
    		return;
    	}
    	liveForActivityDao.batchUpdateKingdomHot(list);
    }
    
    @Override
    public void updateKingdomHotInitByTopicIds(List<Long> topicIds){
    	liveForActivityDao.updateKingdomHotInit(topicIds);
    }

    @Override
    public void updateTopicHot(long topicId, int hot){
        liveForActivityDao.updateTopicHot(topicId, hot);
    }
    
    @Override
    public void deleteKingdomListByDayKey(String dayKey){
    	liveForActivityDao.deleteKingdomListByDayKey(dayKey);
    }

    @Override
    public List<Long> getSpringKingdomUids(){
    	return liveForActivityDao.getAllKingdomUids();
    }

    @Override
    public TopicCountDTO getTopicCount(long topicId){
        Map<String, Object> map = liveForActivityDao.getTopicCount(topicId);
        TopicCountDTO dto = new TopicCountDTO();
        if (null != map && map.size() > 0) {
            dto.setLikeCount((Integer) map.get("like_count"));
            dto.setReadCount((Integer) map.get("read_count_dummy"));
            dto.setReviewCount(((Long) map.get("reviewCount")).intValue());
            dto.setUpdateCount(((Long) map.get("updateCount")).intValue());
        }
        return dto;
    }
    
    @Override
    public List<TopicCountDTO> getTopicCountsByTopicIdsAndTime(List<Long> topicIds, String startTime, String endTime){
    	List<Map<String, Object>> list = liveForActivityDao.getTopicCountsByTopicIds(topicIds, startTime, endTime);
    	if(null != list && list.size() > 0){
    		List<TopicCountDTO> result = new ArrayList<TopicCountDTO>();
    		TopicCountDTO dto = null;
    		for(Map<String, Object> map : list){
    			dto = new TopicCountDTO();
    			dto.setReviewCount(((Long)map.get("reviewCount")).intValue());
                dto.setUpdateCount(((Long)map.get("updateCount")).intValue());
                dto.setTopicId((Long)map.get("topic_id"));
                result.add(dto);
    		}
    		return result;
    	}
    	
    	return null;
    }

    @Override
    public Response send7DayKingdomMessage(int sex) {
        List<Auser> list = activityMybatisDao.getAllAuditSuccessAuserBySex(sex);
        String templateId = "145750";//男的
        if (sex == 0) {//女的
            templateId = "145751";
        }

        if (null != list && list.size() > 0) {
            AactivityStage stage2 = activityMybatisDao.getAactivityStageByStage2(1, 2);
            List<String> msgList = new ArrayList<String>();
            if (null != stage2) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(stage2.getStartTime());
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                msgList.add(String.valueOf(month));
                msgList.add(String.valueOf(day));
            } else {
                msgList.add("");
                msgList.add("");
            }


            log.info("total [" + list.size() + "] user");
            List<String> mobileList = Lists.newArrayList();
            mobileList.add("18916103465");//默认给一个内部的手机号
            for (Auser auser : list) {
                //通知所有审核中的用户
                mobileList.add(auser.getMobile());

                if (mobileList.size() >= 150) {
                    smsService.send7dayCommon(templateId, mobileList, msgList);
                    log.info("send [" + mobileList.size() + "] user!");
                    mobileList.clear();
                }
            }
            if (mobileList.size() > 0) {
                smsService.send7dayCommon(templateId, mobileList, msgList);
                log.info("send [" + mobileList.size() + "] user!");
            }
        }

        return Response.success();
    }

    public Response taskPublish(long taskId, int type) {
        AtaskWithBLOBs atask = activityMybatisDao.getAtaskById(taskId);
        if (null == atask) {
            return Response.failure("任务不存在");
        }

        if (type == 1) {//发布任务
            if (atask.getStatus() == 0) {
                return Response.success(200, "发布原本就是启用状态");
            }
            atask.setStatus(0);
            activityMybatisDao.updateAtaskWithBLOBs(atask);
            return Response.success(200, "发布任务成功");
        } else if (type == 2) {//任务推送
            List<Atopic> list = activityMybatisDao.getAtopicByType(atask.getType());
            if (null != list && list.size() > 0) {
                List<Long> uidList = new ArrayList<Long>();
                for (Atopic t : list) {
                    uidList.add(t.getUid());
                }
                applicationEventBus.post(new TaskPushEvent(Specification.LinkPushType.TASK_PUSH.message, activityWebUrl + Specification.LinkPushType.TASK_PUSH.linkUrl, uidList));
            }

            return Response.success(200, "任务推送中..请耐心等待");
        } else {
            return Response.failure("无效的type值");
        }
    }

    @Override
    public List<Long> get7dayKingdomUpdateUids() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, -12);
        long time = cal.getTimeInMillis();

        return liveForActivityDao.get7dayKingdomUpdateUids(time);
    }

    @Override
    public Response forcedPairing(int isTest, long testUid1, long testUid2) {
        if (isTest == 1) {
            log.info("forcedPairing test begin...uid1[" + testUid1 + "], uid2[" + testUid2 + "]");
            if (testUid1 <= 0 || testUid2 <= 0) {
                return Response.failure(500, "测试模式需要传入两个有效用户");
            }
            Atopic singleKingdom1 = activityMybatisDao.getAtopicByType(testUid1, 1);
            if (null != singleKingdom1) {
                Atopic doubleKingdom1 = activityMybatisDao.getAtopicByType(testUid1, 2);
                if (null != doubleKingdom1) {
                    return Response.failure(500, "[" + testUid1 + "]已经有双人王国了");
                }
            } else {
                return Response.failure(500, "[" + testUid1 + "]没有单人王国");
            }

            Atopic singleKingdom2 = activityMybatisDao.getAtopicByType(testUid2, 1);
            if (null != singleKingdom2) {
                Atopic doubleKingdom2 = activityMybatisDao.getAtopicByType(testUid2, 2);
                if (null != doubleKingdom2) {
                    return Response.failure(500, "[" + testUid2 + "]已经有双人王国了");
                }
            } else {
                return Response.failure(500, "[" + testUid2 + "]没有单人王国");
            }

            AforcedPairing fp1 = activityMybatisDao.getAforcedPairingForUser(testUid1);
            if (null != fp1) {
                return Response.failure(500, "[" + testUid1 + "]已经强配了");
            }
            AforcedPairing fp2 = activityMybatisDao.getAforcedPairingForUser(testUid2);
            if (null != fp2) {
                return Response.failure(500, "[" + testUid2 + "]已经强配了");
            }

            UserProfile up1 = userService.getUserProfileByUid(testUid1);
            UserProfile up2 = userService.getUserProfileByUid(testUid2);
            if (up1.getGender() == up2.getGender()) {
                return Response.failure(500, "[" + testUid1 + "]和[" + testUid2 + "]不是异性");
            }
            Auser auser1 = activityMybatisDao.getAuserByUid(testUid1);
            if (null == auser1) {
                return Response.failure(500, "[" + testUid1 + "]未报名");
            }
            Auser auser2 = activityMybatisDao.getAuserByUid(testUid2);
            if (null == auser2) {
                return Response.failure(500, "[" + testUid2 + "]未报名");
            }

            AforcedPairing fp = new AforcedPairing();
            fp.setAuid(auser1.getId());
            fp.setUid(auser1.getUid());
            fp.setStatus(2);
            fp.setSex(up1.getGender());
            fp.setTargetAuid(auser2.getId());
            fp.setTargetUid(auser2.getUid());
            fp.setCreateTime(new Date());
            activityMybatisDao.saveAforcedPairing(fp);

            //发短信,给双方都要发短信
            List<String> msgList = new ArrayList<String>();
            msgList.add(up1.getNickName());
            List<String> mobileList = new ArrayList<String>();
            mobileList.add(auser2.getMobile());
            smsService.send7dayCommon("142385", mobileList, msgList);

            msgList.clear();
            msgList.add(up2.getNickName());
            mobileList.clear();
            mobileList.add(auser1.getMobile());
            smsService.send7dayCommon("142385", mobileList, msgList);

            log.info("forcedPairing test end.");
        } else {
            log.info("forcedPairing begin...");
            List<Map<String, Object>> getAllSinglePersonList = liveForActivityDao.getSinglePerson();
            List<Map<String, Object>> manList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> womanList = new ArrayList<Map<String, Object>>();
            if (null != getAllSinglePersonList && getAllSinglePersonList.size() > 0) {
                for (Map<String, Object> m : getAllSinglePersonList) {
                    int sex = (Integer) m.get("gender");
                    if (sex == 0) {
                        womanList.add(m);
                    } else {
                        manList.add(m);
                    }
                }
            }
            log.info("man==" + manList.size() + ", woman==" + womanList.size());
            if (manList.size() > 0 || womanList.size() > 0) {
                int min = manList.size();
                if (womanList.size() < min) {
                    min = womanList.size();
                }
                Map<String, Object> man = null;
                Map<String, Object> woman = null;
                AforcedPairing fp = null;
                List<String> msgList = null;
                List<String> mobileList = null;
                for (int i = 0; i < min; i++) {
                    man = manList.get(i);
                    woman = womanList.get(i);

                    fp = new AforcedPairing();
                    fp.setAuid((Long) man.get("auid"));
                    fp.setUid((Long) man.get("uid"));
                    fp.setStatus(2);
                    fp.setSex(1);
                    fp.setTargetAuid((Long) woman.get("auid"));
                    fp.setTargetUid((Long) woman.get("uid"));
                    fp.setCreateTime(new Date());
                    activityMybatisDao.saveAforcedPairing(fp);

                    //发短信
                    msgList = new ArrayList<String>();
                    msgList.add((String) man.get("nick_name"));
                    mobileList = new ArrayList<String>();
                    mobileList.add((String) woman.get("mobile"));
                    smsService.send7dayCommon("142385", mobileList, msgList);

                    msgList.clear();
                    msgList.add((String) woman.get("nick_name"));
                    mobileList.clear();
                    mobileList.add((String) man.get("mobile"));
                    smsService.send7dayCommon("142385", mobileList, msgList);
                }

                //剩下的不处理

            }


            log.info("forcedPairing end...");
        }


        return Response.success();
    }

    @Override
    public ShowActivity7DayUsersDTO get7dayUsers(String channel, String code,
                                                 String startTime, String endTime, int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        int start = (page - 1) * pageSize;
        List<Map<String, Object>> list = liveForActivityDao.get7dayUsers(channel, code, startTime, endTime, start, pageSize);
        ShowActivity7DayUsersDTO dto = new ShowActivity7DayUsersDTO();
        if (null != list && list.size() > 0) {
            ShowActivity7DayUsersDTO.UserItemElement e = null;
            String cc = null;
            String[] tmp = null;
            for (Map<String, Object> map : list) {
                e = new ShowActivity7DayUsersDTO.UserItemElement();
                e.setMobile((String) map.get("mobile"));
                e.setName((String) map.get("name"));
                e.setSex((Integer) map.get("sex"));
                cc = (String) map.get("channel");
                if (!StringUtils.isEmpty(cc)) {
                    tmp = cc.split("=");
                    e.setChannel(tmp[0]);
                    if (tmp.length > 1) {
                        e.setCode(tmp[1]);
                    } else {
                        e.setCode("");
                    }
                }
                e.setUid((Long) map.get("uid"));
                e.setKingdomCount((Long) map.get("kingdomCount"));
                e.setCreateTime((Date) map.get("create_time"));
                dto.getResult().add(e);
            }
        }

        return dto;
    }
    
    @Override
    public List<AkingDom> getAkingDomsByConditions(int conditions, long activityId){
    	return activityMybatisDao.getAkingDomListByActivityIdAndConditions(activityId, conditions);
    }
    
    @Override
    public List<AkingDom> getAkingdomsTop(long activityId, int topNum){
    	return activityMybatisDao.getAkingDomListByTop(activityId, topNum);
    }

    @Override
    public Response getNewYearLiveInfo(long uid, long activityId) {
        BlurSearchDto dto = new BlurSearchDto();
        AkingDom akingDom = activityMybatisDao.getAkingDomByUidAndAid(uid, activityId);
        if (akingDom != null) {
            dto.setTopicId(akingDom.getTopicId());
            dto.setHot(akingDom.getHot());
            Map<String, Object> topic = liveForActivityDao.getTopicById(akingDom.getTopicId());
            dto.setTitle((String) topic.get("title"));
            dto.setLiveImage((String) topic.get("live_image"));
            log.info("get getNewYearLiveInfo success");
            return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message, dto);
        }
        return Response.success(ResponseStatus.SEARCH_ATOPIC_FAILURE.status, ResponseStatus.SEARCH_ATOPIC_FAILURE.message);
    }

    @Override
    public Response getAllNewYearLiveInfo(long uid, long activityId, int pageNum, int pageSize, String topicName, String nickName) {
        Map map = Maps.newHashMap();
        if (pageNum != 0) {
            pageNum = pageNum * pageSize;
        }
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("topicName", topicName);
        map.put("nickName", nickName);

        List<BlurSearchDto> newYearLiveList = activityMybatisDao.getAllNewYearLive(map);
        NewYearDto dto = new NewYearDto();
        if (newYearLiveList.size() > 0 && newYearLiveList != null) {
            for (BlurSearchDto newYearDto : newYearLiveList) {
                NewYearDto.NewYearElement newYearElement = dto.createNewYearElement();
                BeanUtils.copyProperties(newYearDto, newYearElement);
                newYearElement.setIsFollowed(userService.isFollow(newYearDto.getUid(), uid));//souceuid表里是被关注的人
                newYearElement.setIsFollowMe(userService.isFollow(uid, newYearDto.getUid()));
                newYearElement.setAvatar(Constant.QINIU_DOMAIN + "/" + newYearDto.getAvatar());
                dto.getNewYearList().add(newYearElement);
            }
            log.info("get getAllNewYearLiveInfo success");
            return Response.success(ResponseStatus.SEARCH_ATOPIC_SUCCESS.status, ResponseStatus.SEARCH_ATOPIC_SUCCESS.message, dto);
        }
        return Response.success(ResponseStatus.SEARCH_ATOPIC_FAILURE.status, ResponseStatus.SEARCH_ATOPIC_FAILURE.message);
    }

    @Override
    public Response getlightboxInfo() {
        Date nowDate = new Date();
        LightBoxDto dto = new LightBoxDto();
        AppLightboxSource appLightboxSource = activityMybatisDao.getAppLightboxSource(nowDate);
        if (appLightboxSource != null) {
            BeanUtils.copyProperties(appLightboxSource, dto);
            dto.setImage(Constant.QINIU_DOMAIN + "/" + appLightboxSource.getImage());
            log.info("get getlightboxInfo success");
            return Response.success(dto);
        }
        return Response.success(ResponseStatus.SEARCH_LIGHTBOX_NOT_EXISTS.status, ResponseStatus.SEARCH_LIGHTBOX_NOT_EXISTS.message);
    }

    @Override
    public Response getActualAndHistoryList(long uid, int type, String date, long activityId) {
    	ActualAndHistoryDto dto = new ActualAndHistoryDto();
    	ActualAndHistoryDto.ActualAndHistoryElement myElement = dto.createActualAndHistoryElement();
    	//查询用户资料
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        myElement.setAvatar(userProfile.getAvatar());
    	myElement.setNickName(userProfile.getNickName());
    	myElement.setUid(uid);
        if(type == 1){//实时榜单
        	//先查询自己的东东
        	AkingDom kingdom = activityMybatisDao.getAkingDomByUidAndAid(uid, activityId);
        	if(null != kingdom){
        		Map<String, Object> topics = liveForActivityDao.getTopicById(kingdom.getTopicId());
        		if(null != topics){
        			myElement.setTitle((String)topics.get("title"));
        		}
        		myElement.setTopicId(kingdom.getTopicId());
        		myElement.setHot(kingdom.getHot());
        		if(kingdom.getConditions() == 1){
	        		//查询实时排名
	        		int ranks = activityMybatisDao.getRanksAkingDom(kingdom.getHot());
	        		myElement.setRanks(ranks);
        		}
        	}
        	dto.getMyActualAndHistoryList().add(myElement);
        	log.info("get myActualAndHistoryList success");
        	//再查询TOP10
        	List<AkingDom> top10List = activityMybatisDao.getTop10AkingDom(activityId);
        	if(null != top10List && top10List.size() > 0){
        		if(top10List.size() >= 10){
        			AkingDom last = top10List.get(9);
        			top10List = activityMybatisDao.getAkingDomListByHots(activityId, last.getHot());
        		}
        		
        		List<Long> uids = new ArrayList<Long>();
        		List<Long> topicIds = new ArrayList<Long>();
        		for(AkingDom k : top10List){
        			uids.add(k.getUid());
        			topicIds.add(k.getTopicId());
        		}
        		Map<String, UserProfile> userProfileMap = this.genUserProfileMap(uids);
        		Map<String, Map<String, Object>> topicMap = this.genTopicMap(topicIds);
        		
        		long currentHot = Long.MAX_VALUE;
        		int ranks = 0;
        		int index = 0;
        		UserProfile u = null;
        		Map<String, Object> t = null;
        		ActualAndHistoryDto.ActualAndHistoryElement e = null;
        		for(AkingDom k : top10List){
        			index++;
        			e = dto.createActualAndHistoryElement();
        			e.setTopicId(k.getTopicId());
        			u = userProfileMap.get(String.valueOf(k.getUid()));
        			e.setUid(k.getUid());
        			e.setNickName(u.getNickName());
        			e.setAvatar(u.getAvatar());
        			t = topicMap.get(String.valueOf(k.getTopicId()));
        			if(null != t){
        				e.setTitle((String)t.get("title"));
        			}
        			e.setHot(k.getHot());
        			//计算排名
        			if(k.getHot() < currentHot){
        				currentHot = k.getHot();
        				ranks = index;
        			}
        			e.setRanks(ranks);
        			dto.getActualAndHistoryList().add(e);
        		}
        		log.info("get akingDomActualList success");
        		return Response.success(dto);
        	}
        }else if(type == 2){//历史榜单
        	//先查询自己的东东
        	AkingDomList kingdomList = activityMybatisDao.getAkingDomListByUidAndAidAndDaykey(uid, activityId, date);
        	if(null != kingdomList){
        		myElement.setTopicId(kingdomList.getTopicId());
        		myElement.setHot(kingdomList.getHot());
        		if(kingdomList.getConditions() == 1){
	        		//查询实时排名
        			int ranks = activityMybatisDao.getRanksAkingDomList(kingdomList.getHot(), date);
	        		myElement.setRanks(ranks);
        		}
        		Map<String, Object> topic = liveForActivityDao.getTopicById(kingdomList.getTopicId());
        		if(null != topic){
	        		myElement.setTitle((String)topic.get("title"));
        		}
        	}
        	dto.getMyActualAndHistoryList().add(myElement);
        	log.info("get myActualAndHistoryList success");
        	//再查询TOP10
        	List<AkingDomList> top10List = activityMybatisDao.getTop10AkingDomList(activityId, date);
        	if(null != top10List && top10List.size() > 0){
        		if(top10List.size() >= 10){
        			AkingDomList last = top10List.get(9);
        			top10List = activityMybatisDao.getAkingDomListsByHots(activityId, last.getHot(), date);
        		}
        		List<Long> uids = new ArrayList<Long>();
        		List<Long> topicIds = new ArrayList<Long>();
        		for(AkingDomList k : top10List){
        			uids.add(k.getUid());
        			topicIds.add(k.getTopicId());
        		}
        		Map<String, UserProfile> userProfileMap = this.genUserProfileMap(uids);
        		Map<String, Map<String, Object>> topicMap = this.genTopicMap(topicIds);
        		
        		long currentHot = Long.MAX_VALUE;
        		int ranks = 0;
        		int index = 0;
        		UserProfile u = null;
        		Map<String, Object> t = null;
        		ActualAndHistoryDto.ActualAndHistoryElement e = null;
        		for(AkingDomList k : top10List){
        			index++;
        			e = dto.createActualAndHistoryElement();
        			e.setTopicId(k.getTopicId());
        			u = userProfileMap.get(String.valueOf(k.getUid()));
        			e.setUid(k.getUid());
        			e.setNickName(u.getNickName());
        			e.setAvatar(u.getAvatar());
        			t = topicMap.get(String.valueOf(k.getTopicId()));
        			if(null != t){
        				e.setTitle((String)t.get("title"));
        			}
        			e.setHot(k.getHot());
        			//计算排名
        			if(k.getHot() < currentHot){
        				currentHot = k.getHot();
        				ranks = index;
        			}
        			e.setRanks(ranks);
        			dto.getActualAndHistoryList().add(e);
        		}
        		log.info("get akingDomActualList success");
        		return Response.success(dto);
        	}
        }
        
        return Response.success(ResponseStatus.SEARCH_LIST_NOT_EXISTS.status, ResponseStatus.SEARCH_LIST_NOT_EXISTS.message, dto);
    }

    @Override
    public ActivityWithBLOBs getActivityByCid(long topicId ,int type) {
        return activityMybatisDao.getActivityByCid(topicId ,type);
    }

    @Override
    public void updateActivity(ActivityWithBLOBs activity) {
        activityMybatisDao.updateActivity(activity);
    }

    private Map<String, UserProfile> genUserProfileMap(List<Long> uids){
    	Map<String, UserProfile> result = new HashMap<String, UserProfile>();
    	if(null == uids || uids.size() == 0){
    		return result;
    	}
    	List<UserProfile> list = userService.getUserProfilesByUids(uids);
    	if(null != list && list.size() > 0){
    		for(UserProfile u : list){
    			result.put(String.valueOf(u.getUid()), u);
    		}
    	}
    	return result;
    }
    
    private Map<String, Map<String, Object>> genTopicMap(List<Long> topicIds){
    	Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
    	if(null == topicIds || topicIds.size() == 0){
    		return result;
    	}
    	List<Map<String, Object>> list = liveForActivityDao.getTopicsByIds(topicIds);
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> t : list){
    			result.put(String.valueOf((Long)t.get("id")), t);
    		}
    	}
    	return result;
    }
    
    
    public Response getActualAndHistoryList2(long uid, int type, String date, long activityId) {
        ActualAndHistoryDto dto = new ActualAndHistoryDto();
        ActualAndHistoryDto.ActualAndHistoryElement Elements = dto.createActualAndHistoryElement();
        //查询用户资料
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        if (type == 1) {
            //实时榜单
            List<AkingDom> akingDomActualList = activityMybatisDao.getAkingDomByUidAndAidAndConditionsActual(uid, activityId, 1);
            if (akingDomActualList.size() > 0 && akingDomActualList != null) {
                if (akingDomActualList.get(0).getUid() == uid && akingDomActualList.get(0).getConditions() == 1) {
                    //自己符合条件才显示排名 热度
                    Map<String, Object> topics = liveForActivityDao.getTopicById(akingDomActualList.get(0).getTopicId());
                    BeanUtils.copyProperties(akingDomActualList.get(0), Elements);
                    int ranks = activityMybatisDao.getRanksAkingDom(akingDomActualList.get(0).getHot());
                    Elements.setRanks(ranks);
                    if (userProfile != null) {
                        Elements.setAvatar(userProfile.getAvatar());
                        Elements.setNickName(userProfile.getNickName());
                    }
                    if (topics != null) {
                        Elements.setTitle((String) topics.get("title"));
                    }
                    dto.getMyActualAndHistoryList().add(Elements);
                    akingDomActualList.remove(0);
                    log.info("get myActualAndHistoryList success");
                }else {
                    if (akingDomActualList.get(0).getUid() == uid && akingDomActualList.get(0).getConditions() == 0) {
                        Elements.setHot(akingDomActualList.get(0).getHot());
                        Map<String, Object> topics = liveForActivityDao.getTopicById(akingDomActualList.get(0).getTopicId());
                        if (topics != null) {
                            Elements.setTitle((String) topics.get("title"));
                            Elements.setTopicId(akingDomActualList.get(0).getTopicId());
                        }
                        akingDomActualList.remove(0);
                    }
                    //用户信息必须有,没有查询到信息，只返回用户资料
                    Elements.setAvatar(userProfile.getAvatar());
                    Elements.setNickName(userProfile.getNickName());
                    Elements.setUid(uid);
                    dto.getMyActualAndHistoryList().add(Elements);
                }
                for (AkingDom akingDom : akingDomActualList) {
                    UserProfile profile = userService.getUserProfileByUid(akingDom.getUid());
                    Map<String, Object> topic = liveForActivityDao.getTopicById(akingDom.getTopicId());
                    ActualAndHistoryDto.ActualAndHistoryElement actualAndHistoryElement = dto.createActualAndHistoryElement();
                    BeanUtils.copyProperties(akingDom, actualAndHistoryElement);
                    if (profile != null) {
                        actualAndHistoryElement.setAvatar(profile.getAvatar());
                        actualAndHistoryElement.setNickName(profile.getNickName());
                    }
                    if (topic != null) {
                        actualAndHistoryElement.setTitle((String) topic.get("title"));
                    }
                    dto.getActualAndHistoryList().add(actualAndHistoryElement);
                }
                log.info("get akingDomActualList success");
                return Response.success(dto);
            } else {
                //list为空的时候返回用户信息
                Elements.setAvatar(userProfile.getAvatar());
                Elements.setNickName(userProfile.getNickName());
                Elements.setUid(uid);
                dto.getMyActualAndHistoryList().add(Elements);
            }
        } else if (type == 2) {
            //历史榜单
            List<AkingDomList> akingDomHistoryList = activityMybatisDao.getAkingDomListByUidAndAidAndConditionsHistory(uid, activityId, 1, date);
            if (akingDomHistoryList.size() > 0 && akingDomHistoryList != null) {
                if (akingDomHistoryList.get(0).getUid() == uid  && akingDomHistoryList.get(0).getConditions() == 1) {
                    //自己符合条件才显示排名 热度
                    Map<String, Object> topics = liveForActivityDao.getTopicById(akingDomHistoryList.get(0).getTopicId());
                    BeanUtils.copyProperties(akingDomHistoryList.get(0), Elements);
                    int ranks = activityMybatisDao.getRanksAkingDomList(akingDomHistoryList.get(0).getHot(), date);
                    Elements.setRanks(ranks);
                    if (userProfile != null) {
                        Elements.setAvatar(userProfile.getAvatar());
                        Elements.setNickName(userProfile.getNickName());
                    }
                    if (topics != null) {
                        Elements.setTitle((String) topics.get("title"));
                    }
                    dto.getMyActualAndHistoryList().add(Elements);
                    akingDomHistoryList.remove(0);
                    log.info("get myActualAndHistoryList success");
                } else {
                    if (akingDomHistoryList.get(0).getUid() == uid  && akingDomHistoryList.get(0).getConditions() == 0) {
                        Elements.setHot(akingDomHistoryList.get(0).getHot());
                        Map<String, Object> topics = liveForActivityDao.getTopicById(akingDomHistoryList.get(0).getTopicId());
                        if (topics != null) {
                            Elements.setTitle((String) topics.get("title"));
                            Elements.setTopicId(akingDomHistoryList.get(0).getTopicId());
                        }
                        akingDomHistoryList.remove(0);
                    }
                    //用户信息必须有,没有查询到信息，只返回用户资料
                    Elements.setAvatar(userProfile.getAvatar());
                    Elements.setNickName(userProfile.getNickName());
                    Elements.setUid(uid);
                    dto.getMyActualAndHistoryList().add(Elements);
                }
                for (AkingDomList akingDomList : akingDomHistoryList) {
                    UserProfile profile = userService.getUserProfileByUid(akingDomList.getUid());
                    Map<String, Object> topic = liveForActivityDao.getTopicById(akingDomList.getTopicId());
                    ActualAndHistoryDto.ActualAndHistoryElement actualAndHistoryElement = dto.createActualAndHistoryElement();
                    BeanUtils.copyProperties(akingDomList, actualAndHistoryElement);
                    if (profile != null) {
                        actualAndHistoryElement.setAvatar(profile.getAvatar());
                        actualAndHistoryElement.setNickName(profile.getNickName());
                    }
                    if (topic != null) {
                        actualAndHistoryElement.setTitle((String) topic.get("title"));
                    }
                    dto.getActualAndHistoryList().add(actualAndHistoryElement);
                    log.info("get akingDomHistoryList success");
                }
                return Response.success(dto);
            }else {
                //list为空的时候返回用户信息
                Elements.setAvatar(userProfile.getAvatar());
                Elements.setNickName(userProfile.getNickName());
                Elements.setUid(uid);
                dto.getMyActualAndHistoryList().add(Elements);
            }
        }
        return Response.success(ResponseStatus.SEARCH_LIST_NOT_EXISTS.status, ResponseStatus.SEARCH_LIST_NOT_EXISTS.message, dto);
    }
    
    @Override
    public List<AppUiControl> getAppUiControlList(String searchTime){
    	Date sTime = null;
    	if(!StringUtils.isEmpty(searchTime)){
    		try{
    			sTime = DateUtil.string2date(searchTime, "yyyy-MM-dd HH:mm:ss");
    		}catch(Exception e){
    			log.error("日期转换失败", e);
    			sTime = null;
    		}
    	}
    	return activityMybatisDao.getAppUiControlListByTime(sTime);
    }
    
    @Override
    public AppUiControl getAppUiControlById(long id){
    	return activityMybatisDao.getAppUiControlById(id);
    }
    
    @Override
    public void updateAppUiControl(AppUiControl appui){
    	activityMybatisDao.updateAppUiControl(appui);
    }
    
    @Override
    public void createAppUiControl(AppUiControl appui){
    	activityMybatisDao.createAppUiControl(appui);
    }
    
    @Override
    public List<AppLightboxSource> getAppLightboxSourceList(String searchTime){
    	Date sTime = null;
    	if(!StringUtils.isEmpty(searchTime)){
    		try{
    			sTime = DateUtil.string2date(searchTime, "yyyy-MM-dd HH:mm:ss");
    		}catch(Exception e){
    			log.error("日期转换失败", e);
    			sTime = null;
    		}
    	}
    	return activityMybatisDao.getAppLightboxSourceListByTime(sTime);
    }
    
    @Override
    public AppLightboxSource getAppLightboxSourceById(long id){
    	return activityMybatisDao.getAppLightboxSourceById(id);
    }
    
    @Override
    public void updateAppLightboxSource(AppLightboxSource lightbox){
    	activityMybatisDao.updateAppLightboxSource(lightbox);
    }
    
    @Override
    public void createAppLightboxSource(AppLightboxSource lightbox){
    	activityMybatisDao.createAppLightboxSource(lightbox);
    }
    
    @Override
    public boolean isTopicRec(long topicId){
    	ActivityWithBLOBs banner = activityMybatisDao.getActivityByCid(topicId ,2);
    	if(null != banner){
    		return true;
    	}
    	
    	return false;
    }
    
    @Override
    public List<Tchannel> getAppChannel(String code){
    	return activityMybatisDao.getAppChannel(code);
    }
    
    @Override
    public Tchannel getTchannelById(long id){
    	return activityMybatisDao.getTchannelById(id);
    }
    
    @Override
    public Tchannel getTchannelByCode(String code){
    	return activityMybatisDao.getTchannelByCode(code);
    }
    
    @Override
    public void saveTchannel(Tchannel c){
    	activityMybatisDao.saveTchannel(c);
    }
    
    @Override
    public void updateTchannel(Tchannel c){
    	activityMybatisDao.updateTchannel(c);
    }
    
    @Override
    public void deleteTchannel(long id){
    	activityMybatisDao.deleteTchannel(id);
    }
    
    @Override
    public Response specailTopicBillboard(long type, long searchUid){
    	ShowActivityBillboardDTO result = new ShowActivityBillboardDTO();
    	
    	if(type == 1){//地区
    		List<AcommonList> list = activityMybatisDao.getAcommonListAreaBillboard(1, 3);
    		if(null != list && list.size() > 0){
    			AcommonList alist = null;
    			ShowActivityBillboardDTO.AreaElement e = null;
    			for(int i=0;i<list.size();i++){
    				alist = list.get(i);
    				e = new ShowActivityBillboardDTO.AreaElement();
    				e.setTopicId(alist.getTargetId());
    				e.setRank(i+1);
    				e.setScore(alist.getScore());
    				e.setName(alist.getAlias());
    				result.getAreaData().add(e);
    			}
    		}
    	}else if(type==2){//用户
    		//先查个人的
    		if(searchUid > 0){
    			UserProfile u = userService.getUserProfileByUid(searchUid);
    			if(null != u){
    				result.getMyUser().setUid(searchUid);
    				result.getMyUser().setNickName(u.getNickName());
    				result.getMyUser().setAvatar(Constant.QINIU_DOMAIN_COMMON + "/" + u.getAvatar());
    				result.getMyUser().setV_lv(u.getvLv());
    				result.getMyUser().setLevel(u.getLevel());
    				
    				AcommonList my = activityMybatisDao.getAcommonList(2, 3, searchUid);
    				if(null != my && my.getScore().longValue() > 0){
    					result.getMyUser().setScore(my.getScore());
    					result.getMyUser().setRank(liveForActivityDao.getAcommonListRank(2, 3, my.getScore(), DateUtil.date2string(my.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
    				}else{
    					result.getMyUser().setScore(0);
    					result.getMyUser().setRank(0);
    				}
    			}
    		}
    		
    		List<AcommonList> list = activityMybatisDao.getAcommonListBillboard(2, 3, 10);
    		if(null != list && list.size() > 0){
    			List<Long> uidList = new ArrayList<Long>();
    			for(AcommonList a : list){
    				uidList.add(a.getTargetId());
    			}
    			Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
    			List<UserProfile> uList = userService.getUserProfilesByUids(uidList);
    			if(null != uList && uList.size() > 0){
    				for(UserProfile u : uList){
    					userMap.put(u.getUid().toString(), u);
    				}
    			}
    			AcommonList alist = null;
    			ShowActivityBillboardDTO.UserElement e = null;
    			UserProfile user = null;
    			for(int i=0;i<list.size();i++){
    				alist = list.get(i);
    				user = userMap.get(alist.getTargetId().toString());
    				e = new ShowActivityBillboardDTO.UserElement();
    				if(null != user){
    					e.setAvatar(Constant.QINIU_DOMAIN_COMMON + "/" + user.getAvatar());
        				e.setNickName(user.getNickName());
        				e.setV_lv(user.getvLv());
        				e.setLevel(user.getLevel());
    				}
    				e.setRank(i+1);
    				e.setScore(alist.getScore());
    				e.setUid(alist.getTargetId());
    				result.getUserData().add(e);
    			}
    		}
    	}
    	
    	return Response.success(result);
    }
    
    @Override
    public Response areaHot(long topicId){
    	ShowAreaHotDTO result = new ShowAreaHotDTO();
    	result.setTopicId(topicId);
    	
    	AcommonList a = activityMybatisDao.getAcommonList(1, 3, topicId);
    	if(null != a){
    		result.setName(a.getAlias());
    		result.setScore(a.getScore());
    		if(a.getScore() > 0){
    			result.setRank(liveForActivityDao.getAcommonListRank(1, 3, a.getScore(), DateUtil.date2string(a.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
    		}
    	}
    	return Response.success(result);
    }
    
    @Override
    public Response areaSupport(long optUid, long topicId){
    	if(optUid <= 0){
    		return Response.success(500,"必须登录后才能支持");
    	}
    	//判断是否已经支持过了 origin code
//    	AcommonHotDetail detail = activityMybatisDao.getAcommonHotDetail(3, topicId, optUid);
        // this now modify this by kylin
        AcommonHotDetail detail = activityMybatisDao.getAcommonHotDetail(3, optUid);
    	if(null != detail){
    		return Response.success(500,"您已经参与过支持活动");
    	}
    	//判断该王国是否为活动王国
    	AcommonList aKingdom = activityMybatisDao.getAcommonList(1, 3, topicId);
    	if(null == aKingdom){
    		return Response.success(500,"非活动区域");
    	}
    	boolean isTop10 = false;
    	long score = 1;//默认是+1
    	//判断是否前10的用户，前10的用户都是加自己的荣誉值
    	AcommonList a = activityMybatisDao.getAcommonList(2, 3, optUid);
    	if(null != a && a.getScore() > 0){
    		int rank = liveForActivityDao.getAcommonListRank(2, 3, a.getScore(), DateUtil.date2string(a.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
    		if(rank <= 10){
    			score = a.getScore();
    			isTop10 = true;
    		}
    	}
    	liveForActivityDao.specialTopicAddHot(topicId, 1, 3, (int)score);
    	//记录历史
    	detail = new AcommonHotDetail();
    	detail.setActivityId(3l);
    	detail.setHot(score);
    	detail.setTopicId(topicId);
    	detail.setUid(optUid);
    	activityMybatisDao.saveAcommonHotDetail(detail);
    	//记录留言表
    	AcommonChat chat = new AcommonChat();
    	Date now = new Date();
    	chat.setActivityId(3l);
    	chat.setCreateTime(now);
    	chat.setLongTime(now.getTime());
    	chat.setMessage("支持了"+aKingdom.getAlias()+"，"+aKingdom.getAlias()+"热度值 +"+score);
    	chat.setTopicId(topicId);
    	if(isTop10){
    		chat.setType(2);//top10支持
    	}else{
    		chat.setType(1);//普通支持
    	}
    	chat.setUid(optUid);
    	activityMybatisDao.saveAcommonChat(chat);
    	
    	return Response.success(200, "操作成功");
    }
    
    @Override
    public Response chatQuery(long sinceId){
    	ShowAcommonChatQueryDTO result = new ShowAcommonChatQueryDTO();
        //第一页展示，则从当前时间点往前推5分钟开始加载
//    	if(sinceId<=0){
//    		Date d = new Date();
//    		sinceId = d.getTime() - 120*60*1000;
//    	}
        // 获取最新50条数据
    	List<AcommonChat> list = activityMybatisDao.getAcommonChats(3, 50);
    	Collections.sort(list, new Comparator<AcommonChat>() {
            @Override
            public int compare(AcommonChat o1, AcommonChat o2) {
                return o1.getLongTime()>o2.getLongTime()?1:-1;
            }
        });
    	if(null != list && list.size() > 0){
    		List<Long> uidList = new ArrayList<Long>();
    		for(AcommonChat chat : list){
    			uidList.add(chat.getUid());
    		}
    		Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
    		List<UserProfile> uList = userService.getUserProfilesByUids(uidList);
    		if(null != uList && uList.size() > 0){
    			for(UserProfile u : uList){
    				userMap.put(u.getUid().toString(), u);
    			}
    		}
    		
    		ShowAcommonChatQueryDTO.ChatElement e = null;
    		UserProfile user = null;
    		for(AcommonChat chat : list){
    			e = new ShowAcommonChatQueryDTO.ChatElement();
    			user = userMap.get(chat.getUid().toString());
    			if(null == user){
    				continue;
    			}
    			e.setAvatar(Constant.QINIU_DOMAIN_COMMON + "/" + user.getAvatar());
    			e.setMessage(chat.getMessage());
    			e.setNickName(user.getNickName());
    			e.setSinceId(chat.getLongTime());
    			e.setType(chat.getType());
    			result.getResult().add(e);
    		}
    	}
    	
    	return Response.success(result);
    }
    
    @Override
    public Response top10SupportChatQuery(){
    	List<AcommonChat> list = activityMybatisDao.getTop10Chats(3);
    	
    	ShowTop10SupportChatQuery result = new ShowTop10SupportChatQuery();
    	if(null != list && list.size() > 0){
    		ShowTop10SupportChatQuery.Top10ChatElement e = null;
    		AcommonList userList = null;
    		AcommonList areaList = null;
    		UserProfile user = null;
    		for(AcommonChat chat : list){
    			e = new ShowTop10SupportChatQuery.Top10ChatElement();
    			user = userService.getUserProfileByUid(chat.getUid());
    			if(null != user){
    				e.setNickName(user.getNickName());
    			}
    			userList = activityMybatisDao.getAcommonList(2, 3, chat.getUid());
    			if(null != userList){
    				e.setScore(userList.getScore());
    				if(userList.getScore() > 0){
    					e.setUserRank(liveForActivityDao.getAcommonListRank(2, 3, userList.getScore(), DateUtil.date2string(userList.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
    				}
    			}
    			areaList = activityMybatisDao.getAcommonList(1, 3, chat.getTopicId());
    			if(null != areaList){
    				e.setAreaName(areaList.getAlias());
    			}
    			result.getResult().add(e);
    		}
    	}
    	
    	return Response.success(result);
    }
    
    @Override
    public Response chat(long uid, String message){
    	if(uid <= 0 || StringUtils.isEmpty(message)){
    		return Response.success();
    	}
    	AcommonChat chat = new AcommonChat();
    	Date now = new Date();
    	chat.setActivityId(3l);
    	chat.setCreateTime(now);
    	chat.setLongTime(now.getTime());
    	chat.setMessage(message);
    	chat.setTopicId(0l);
    	chat.setType(0);
    	chat.setUid(uid);
    	activityMybatisDao.saveAcommonChat(chat);
    	
    	return Response.success();
    }
    
    @Override
    public void saveAcommonList(AcommonList alist){
    	activityMybatisDao.createAcommonList(alist);
    }
    
    public void updateAcommonList(AcommonList alist){
    	activityMybatisDao.updateAcommonList(alist);
    }
    
    @Override
    public void deleteAcommonListById(long id){
    	activityMybatisDao.deleteAcommonListById(id);
    }
    
    @Override
    public AcommonList getAcommonList(long targetId, long activityId, int type){
    	return activityMybatisDao.getAcommonList(type, activityId, targetId);
    }
    
    @Override
    public Response anchorList(long uid){
    	ShowAnchorListDTO result = new ShowAnchorListDTO();
    	result.setEnterStatus(0);
    	
    	List<Map<String, Object>> list = liveForActivityDao.getAnchorList();
    	
    	//查询报名状态
    	Map<String, String> statusMap = new HashMap<String, String>();
    	List<Map<String, Object>> enterList = liveForActivityDao.getUserAnchorEnterByUid(uid);
    	if(null != enterList && enterList.size() > 0){
    		result.setEnterStatus(1);
    		for(Map<String, Object> m : enterList){
    			statusMap.put(String.valueOf(m.get("anchor_id")), "1");
    		}
    	}
    	
    	if(null != list && list.size() > 0){
    		ShowAnchorListDTO.AnchorInfoElement e = null;
    		for(Map<String, Object> m : list){
    			e = new ShowAnchorListDTO.AnchorInfoElement();
    			e.setAid((Long)m.get("id"));
    			e.setAvatar(Constant.QINIU_DOMAIN_COMMON + "/" + (String)m.get("avatar"));
    			e.setNickName((String)m.get("name"));
    			e.setQq((String)m.get("qq"));
    			if(null != m.get("cc")){
    				e.setSignUpCount(((Long)m.get("cc")).intValue());
    			}else{
    				e.setSignUpCount(0);
    			}
    			e.setSummary((String)m.get("summary"));
    			if(uid != 100 && null != statusMap.get(String.valueOf(e.getAid()))){
    				e.setStatus(1);
    			}else{
    				e.setStatus(0);
    			}
    			result.getResultList().add(e);
    		}
    	}
    	
    	return Response.success(result);
    }
    
    @Override
    public Response enterAnchor(long uid, long aid){
    	if(uid==100){
    		return Response.failure(500, "报名失败");
    	}
    	
//    	Map<String, Object> ea = liveForActivityDao.getAnchorEnterByUidAndAid(uid, aid);
//    	if(null != ea){
//    		return Response.failure(500, "已经报过名了");
//    	}
    	
    	List<Map<String, Object>> list = liveForActivityDao.getAnchorEnterByUid(uid);
    	if(null != list && list.size() > 0){
    		return Response.failure(501, "不能重复报名");
    	}
    	
    	liveForActivityDao.insertAnchorEnter(uid, aid);
    	
    	return Response.success(200, "报名成功");
    }
}
