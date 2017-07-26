package com.me2me.live.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.me2me.core.KeysManager;
import com.me2me.live.event.*;
import com.me2me.live.model.*;
import com.me2me.user.dto.ModifyUserCoinDto;
import com.me2me.user.dto.PermissionDescriptionDto;
import com.me2me.user.dto.PermissionDescriptionDto.PermissionNodeDto;
import com.me2me.user.rule.CoinRule;
import com.me2me.user.rule.Rules;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.TopicCountDTO;
import com.me2me.activity.model.AcommonList;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.service.ActivityService;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.page.PageBean;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.TeaseInfoDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.EmotionPack;
import com.me2me.content.model.EmotionPackDetail;
import com.me2me.content.service.ContentService;
import com.me2me.core.QRCodeUtil;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.cache.TeaseAutoPlayStatusModel;
import com.me2me.live.cache.TopicNewsModel;
import com.me2me.live.dao.LiveLocalJdbcDao;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.AggregationOptDto;
import com.me2me.live.dto.CreateKingdomDto;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.CreateVoteDto;
import com.me2me.live.dto.CreateVoteResponeDto;
import com.me2me.live.dto.DaySignInfoDto;
import com.me2me.live.dto.DropAroundDto;
import com.me2me.live.dto.GetKingdomPriceDto;
import com.me2me.live.dto.GetLiveDetailDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.GetLiveTimeLineDto2;
import com.me2me.live.dto.GetLiveUpdateDto;
import com.me2me.live.dto.GivenKingdomDto;
import com.me2me.live.dto.KingdomImgDB;
import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.dto.ListedTopicListDto;
import com.me2me.live.dto.Live4H5Dto;
import com.me2me.live.dto.LiveBarrageDto;
import com.me2me.live.dto.LiveCoverDto;
import com.me2me.live.dto.LiveDetailDto;
import com.me2me.live.dto.LiveDisplayProtocolDto;
import com.me2me.live.dto.LiveParamsDto;
import com.me2me.live.dto.LiveQRCodeDto;
import com.me2me.live.dto.LiveTimeLineDto;
import com.me2me.live.dto.LiveTimeLineDto2;
import com.me2me.live.dto.LiveUpdateDto;
import com.me2me.live.dto.ResendVoteDto;
import com.me2me.live.dto.SearchDropAroundTopicDto;
import com.me2me.live.dto.SearchQuotationListDto;
import com.me2me.live.dto.SearchRobotListDto;
import com.me2me.live.dto.SearchTopicDto;
import com.me2me.live.dto.SearchTopicListedListDto;
import com.me2me.live.dto.SettingModifyDto;
import com.me2me.live.dto.SettingsDto;
import com.me2me.live.dto.ShowBarrageDto;
import com.me2me.live.dto.ShowFavoriteListDto;
import com.me2me.live.dto.ShowLiveDto;
import com.me2me.live.dto.ShowRecQueryDTO;
import com.me2me.live.dto.ShowTagKingdomsDTO;
import com.me2me.live.dto.ShowTopicListDto;
import com.me2me.live.dto.ShowTopicListDto.GivenKingdom;
import com.me2me.live.dto.ShowTopicSearchDTO;
import com.me2me.live.dto.ShowTopicTagsDTO;
import com.me2me.live.dto.ShowUserAtListDTO;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.dto.StealResultDto;
import com.me2me.live.dto.SubscribedTopicDTO;
import com.me2me.live.dto.TestApiDto;
import com.me2me.live.dto.TopicTransferRecordDto;
import com.me2me.live.dto.TopicVoteInfoDto;
import com.me2me.live.dto.UserAtListDTO;
import com.me2me.live.dto.UserKingdomInfoDTO;
import com.me2me.live.dto.VoteInfoDto;
import com.me2me.live.service.exceptions.KingdomStealException;
import com.me2me.search.dto.RecommendUser;
import com.me2me.search.dto.RecommendUserDto;
import com.me2me.search.service.SearchService;
import com.me2me.sms.dto.ImSendMessageDto;
import com.me2me.sms.service.JPushService;
import com.me2me.sms.service.SmsService;
import com.me2me.user.dto.EmotionInfoListDto;
import com.me2me.user.dto.RechargeToKingdomDto;
import com.me2me.user.model.EmotionInfo;
import com.me2me.user.model.EmotionRecord;
import com.me2me.user.model.SystemConfig;
import com.me2me.user.model.User;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserNo;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserNoticeUnread;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
@Service
@Slf4j
public class LiveServiceImpl implements LiveService {

    @Autowired
    private LiveMybatisDao liveMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private JPushService jPushService;

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Autowired
    private LiveLocalJdbcDao liveLocalJdbcDao;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SearchService searchService;
    
    @Autowired
    private SmsService smsService;





    @Value("#{app.live_web}")
    private String live_web;
    @Value("#{app.dubboRegistry}")
    private String zkAddr;
    /** 王国发言(评论等)最新ID */
    public static final String TOPIC_FRAGMENT_NEWEST_MAP_KEY = "TOPIC_FRAGMENT_NEWEST";

    /** 聚合王国内容下发次数 */
    private static final String TOPIC_AGGREGATION_PUBLISH_COUNT = "TOPIC_AGGREGATION_PUBLISH_COUNT";

    //置顶次数
    private static final String TOP_COUNT = "topCount";

    /** 普通用户每天可发起投票数 */
    private static final String NORMAL_CREATE_VOTE_COUNT = "NORMAL_CREATE_VOTE_COUNT";

    /** 大V用户每天可发起投票数 */
    private static final String V_CREATE_VOTE_COUNT = "V_CREATE_VOTE_COUNT";

    //获取王国上市标准
    private static final String LISTED_PRICE = "LISTED_PRICE";

    @SuppressWarnings("rawtypes")
    @Override
    public Response<SpeakDto> createLive(CreateLiveDto createLiveDto) {
        log.info("createLive start ...");
        if(StringUtils.isEmpty(createLiveDto.getLiveImage()) || StringUtils.isEmpty(createLiveDto.getTitle())){
            log.info("liveImage or title is empty");
            return Response.failure(ResponseStatus.KINGDOM_CREATE_FAILURE.status, ResponseStatus.KINGDOM_CREATE_FAILURE.message);
        }
        Topic topic = new Topic();
        topic.setTitle(createLiveDto.getTitle());
        topic.setLiveImage(createLiveDto.getLiveImage());
        topic.setUid(createLiveDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
        Calendar calendar = Calendar.getInstance();
        topic.setLongTime(calendar.getTimeInMillis());
        topic.setCreateTime(new Date());
        //初始化核心圈为用户的核心圈
        // List<SnsCircle> snsCircles = liveMybatisDao.getCoreCircle(createLiveDto.getUid());
        JSONArray array = new JSONArray();
        array.add(createLiveDto.getUid());

        topic.setCoreCircle(array.toString());

        liveMybatisDao.createTopic(topic);
        //创建直播之后添加到我的UGC
        ContentDto contentDto = new ContentDto();
        contentDto.setContent(createLiveDto.getTitle());
        contentDto.setFeeling(createLiveDto.getTitle());
        contentDto.setTitle(createLiveDto.getTitle());
        contentDto.setImageUrls(createLiveDto.getLiveImage());
        contentDto.setUid(createLiveDto.getUid());
        contentDto.setType(Specification.ArticleType.LIVE.index);
        contentDto.setForwardCid(topic.getId());

        contentDto.setRights(Specification.ContentRights.EVERY.index);
        contentService.publish(contentDto);

        applicationEventBus.post(new CacheLiveEvent(createLiveDto.getUid(), topic.getId()));

        SpeakDto speakDto = new SpeakDto();
        speakDto.setTopicId(topic.getId());
        UserProfile profile = userService.getUserProfileByUid(createLiveDto.getUid());
        speakDto.setV_lv(profile.getvLv());
        //检查有没有出错的数据，如果有则删除出错数据
        contentService.clearData();




        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status, ResponseStatus.USER_CREATE_LIVE_SUCCESS.message, speakDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        log.info("getLiveTimeline start ...");
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(getLiveTimeLineDto.getUid(), getLiveTimeLineDto.getTopicId() + "", "0");
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId(), getLiveTimeLineDto.getPageSize());
        log.info("get timeLine data");
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        log.info("buildLiveTimeLine success");

        //将当前用户针对于本王国的相关消息置为已读
        userService.clearUserNoticeUnreadByCid(getLiveTimeLineDto.getUid(), Specification.UserNoticeUnreadContentType.KINGDOM.index, getLiveTimeLineDto.getTopicId());


        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status, ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message, liveTimeLineDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response liveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        //判断进入直播是否是第一次
        LiveReadHistory liveReadHistory = liveMybatisDao.getLiveReadHistory(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getUid());
        List<TopicFragment> fragmentList = Lists.newArrayList();
        if (getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.FIRST.index) {
            if (liveReadHistory == null) {
                fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId(), 50);
                liveMybatisDao.createLiveReadHistory(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getUid());
            } else {
                fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), Integer.MAX_VALUE);
            }
        } else if (getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.NEXT.index) {
            fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId(), 50);
        } else if (getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.PREV.index) {
            fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        }
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status, ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message, liveTimeLineDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response liveCover(long topicId, long uid, int vflag, int source) {
        log.info("liveCover start ...");
        LiveCoverDto liveCoverDto = new LiveCoverDto();
        String KINGDOM_VIEW_KEY = "USER_VIEW_KINGDOM_"+uid+"_"+topicId;
        cacheService.set(KINGDOM_VIEW_KEY, "1");
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(topic==null){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
        }
        
        //特殊王国处理，对于给定的王国，如果非国王或核心圈则无法进入
        String specialTopicIds = userService.getAppConfigByKey("SPECIAL_KINGDOM_IDS");
        if(!StringUtils.isEmpty(specialTopicIds)){
        	String[] tmp = specialTopicIds.split(",");
        	String currentTopicId = String.valueOf(topicId);
        	for(String t : tmp){
        		if(currentTopicId.equals(t)){//符合条件，则进行判断
        			if(!this.isKing(uid, topic.getUid()) && !this.isInCore(uid, topic.getCoreCircle())){
        				return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,"此王国需要经过国王邀请才允许进入");
        			}
        			break;
        		}
        	}
        }
        
        liveCoverDto.setTitle(topic.getTitle());
        liveCoverDto.setCreateTime(topic.getCreateTime());
        liveCoverDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
        liveCoverDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        liveCoverDto.setNickName(userProfile.getNickName());
        liveCoverDto.setUid(topic.getUid());
        liveCoverDto.setLastUpdateTime(topic.getLongTime());
        liveCoverDto.setReviewCount(liveMybatisDao.countFragment(topic.getId(), topic.getUid()));
        liveCoverDto.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(), topic.getUid()));
        liveCoverDto.setV_lv(userProfile.getvLv());
        liveCoverDto.setLevel(userProfile.getLevel());

        LiveFavorite hasFavorite =  liveMybatisDao.getLiveFavorite(uid,topicId);
        liveCoverDto.setHasFavorite(hasFavorite==null?0:1);
        liveCoverDto.setFavorite(liveCoverDto.getHasFavorite());

        liveCoverDto.setInternalStatus(getInternalStatus(topic,uid));
        liveCoverDto.setLiveWebUrl(live_web+topicId);//返回直播URL地址
        //添加直播阅读数log.info("liveCover end ...");
        Content content = contentService.getContentByTopicId(topicId);
        content.setReadCount(content.getReadCount() + 1);

        if(activityService.isTopicRec(topicId)){
            liveCoverDto.setIsRec(1);
        }else{
            liveCoverDto.setIsRec(0);
        }

        //标签
        if(vflag > 0){
            String tags = "";
            List<Long> tagIdList = new ArrayList<Long>();
            List<TopicTagDetail> topicTagDetails = liveMybatisDao.getTopicTagDetailsByTopicId(topicId);
            if(topicTagDetails != null && topicTagDetails.size() > 0){
                StringBuilder builder = new StringBuilder();
                for (TopicTagDetail detail : topicTagDetails){
                    tagIdList.add(detail.getTagId());
                    String tag = detail.getTag();
                    if(tags.equals("")){
                        tags = builder.append(tag).toString();
                    }else {
                        builder.append(";"+tag);
                    }
                }
                liveCoverDto.setTags(builder.toString());
            }

           /* boolean isAdmin = userService.isAdmin(uid);
            if(liveCoverDto.getInternalStatus() == Specification.SnsCircle.CORE.index
                    || isAdmin){//核心圈的或管理员，需要返回推荐标签
                //第一步，先返回运营指定的推荐标签（1个）
                TopicTag recTag = liveMybatisDao.getRecTopicTagWithoutOwn(topicId, tagIdList, isAdmin);
                if(null != recTag){
                    liveCoverDto.setRecTags(recTag.getTag());
                }
            }*/
        }

        //这里需要判断是否需要返回足迹信息
        //条件：不是国王，不是核心圈，没有加入过王国，并且是第一次进入这个王国
        if(!this.isKing(uid, topic.getUid()) && !this.isInCore(uid, topic.getCoreCircle())
        		&& null == hasFavorite && liveMybatisDao.isNewInTopic(uid, topicId)){
        	TopicFragmentTemplate topicFragmentTemplate = liveMybatisDao.getTopicFragmentTemplate();
            if(topicFragmentTemplate != null && !StringUtils.isEmpty(topicFragmentTemplate.getContent())){
                String text = topicFragmentTemplate.getContent();
                String[] temp = text.split("##");
                if(null != temp && temp.length > 0){
                	liveCoverDto.setTrackContent(temp[0]);
                    if(temp.length > 1 && !StringUtils.isEmpty(temp[1])){
                    	liveCoverDto.setTrackImage(Constant.QINIU_DOMAIN + "/" + temp[1]);
                    }
                }
            }
        }
        
        //记录阅读历史
        TopicReadHis trh = new TopicReadHis();
        trh.setUid(uid);
        trh.setTopicId(topicId);
        trh.setReadCount(1);
        if(source == 0){//APP内
            trh.setInApp(1);
        }else{//APP外
            trh.setInApp(0);
        }
        trh.setCreateTime(new Date());

        if(content.getReadCount() == 1 || content.getReadCount() == 2){
            liveCoverDto.setReadCount(1);
            content.setReadCountDummy(1);
            contentService.updateContentById(content);
            trh.setReadCountDummy(1);
        }else {
            SystemConfig systemConfig = userService.getSystemConfig();
            int start = systemConfig.getReadCountStart();
            int end = systemConfig.getReadCountEnd();
            int readCountDummy = content.getReadCountDummy();
            Random random = new Random();
            //取1-6的随机数每次添加
            int value = random.nextInt(end) + start;
            int readDummy = readCountDummy + value;
            content.setReadCountDummy(readDummy);
            contentService.updateContentById(content);
            liveCoverDto.setReadCount(readDummy);
            trh.setReadCountDummy(value);
        }
        liveMybatisDao.saveTopicReadHis(trh);


        // 添加成员数量
        Long favoriteCount = Long.valueOf(0);
        List<Long> topicIdList = new ArrayList<Long>();
        topicIdList.add(Long.valueOf(topicId));
        Map<String, Long> memberMap = liveLocalJdbcDao.getTopicMembersCount(topicIdList);
        if(null != memberMap && memberMap.size() > 0){
            if(null != memberMap.get(String.valueOf(topicId))){
                favoriteCount = memberMap.get(String.valueOf(topicId));
            }
        }
        if(favoriteCount.longValue() > 0){
            liveCoverDto.setMembersCount(favoriteCount.intValue() + 1);
        }else{
            liveCoverDto.setMembersCount(1);
        }

        //聚合相关属性--begin--add by zcl 20170205
        int max = 10;
        String count = cacheService.get(TOPIC_AGGREGATION_PUBLISH_COUNT);
        if(!StringUtils.isEmpty(count)){
            max = Integer.valueOf(count);
        }
        liveCoverDto.setPublishLimit(max);
        liveCoverDto.setType(topic.getType());
        if(topic.getType() == Specification.KingdomType.NORMAL.index){//个人王国
            //被聚合次数
            int ceCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId2(topicId);
            liveCoverDto.setCeCount(ceCount);
        }if(topic.getType() == Specification.KingdomType.AGGREGATION.index){//聚合王国
            //子王国数
            int acCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId(topicId);
            liveCoverDto.setAcCount(acCount);
            //子王国top列表
            int needNum = 30;
            //置顶的按置顶时间倒序，非置顶的按更新时间倒叙
            List<Map<String, Object>> acTopList = liveLocalJdbcDao.getAcTopicListByCeTopicId(topicId, 0, needNum);
            if(null != acTopList && acTopList.size() > 0){
                List<Long> uidList = new ArrayList<Long>();
                Long id = null;
                for(Map<String, Object> t : acTopList){
                    id = (Long)t.get("uid");
                    if(!uidList.contains(id)){
                        uidList.add(id);
                    }
                }

                LiveCoverDto.TopicElement e = null;
                for(Map<String, Object> t : acTopList){
                    e = new LiveCoverDto.TopicElement();
                    e.setTopicId((Long)t.get("id"));
                    e.setTitle((String)t.get("title"));
                    e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)t.get("live_image"));
                    e.setInternalStatus(this.getUserInternalStatus((String)t.get("core_circle"), uid));
                    liveCoverDto.getAcTopList().add(e);
                }
            }
        }else{
            //暂不支持
        }
        //聚合相关属性--end--

        //跑马灯列表信息处理
        Date date = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.add(Calendar.DATE, -1);
        List<TopicNews> topicNewsList = liveMybatisDao.getTopicNewsList24h(cal1.getTime());
        for (int i = 0; i < topicNewsList.size(); i++) {
            TopicNews topicNews = topicNewsList.get(i);
            TopicNewsModel topicNewsModel = new TopicNewsModel(topicNews.getId(),uid,"0");
            String isTopicNews= cacheService.hGet(topicNewsModel.getKey(), topicNewsModel.getField());
            if (!StringUtils.isEmpty(isTopicNews)) {
                continue;
            } else {
                cacheService.hSet(topicNewsModel.getKey(), topicNewsModel.getField(), topicNewsModel.getValue());
            }
            LiveCoverDto.TopicNewsElement  topicNewsElement = new LiveCoverDto.TopicNewsElement();
            topicNewsElement.setId(topicNews.getId());
            topicNewsElement.setContent(topicNews.getContent());
            topicNewsElement.setType(topicNews.getType());
            topicNewsElement.setTopicId(topicNews.getTopicId());
            Topic newsTopic  = liveMybatisDao.getTopicById(topicNews.getTopicId());
            if(newsTopic==null){
                continue;
            }
            topicNewsElement.setContentType(newsTopic.getType());
            topicNewsElement.setInternalStatus(this.getUserInternalStatus(newsTopic.getCoreCircle(), uid));
            liveCoverDto.getNewsTopList().add(topicNewsElement);
        }
        liveCoverDto.setTopicPrice(topic.getPrice());
        liveCoverDto.setTopicRMB(exchangeKingdomPrice(topic.getPrice()));
        TopicData topicData = liveMybatisDao.getTopicDataByTopicId(topicId);
        int percentage=0;
        if(topicData==null){
            liveCoverDto.setTopicPriceChanged(0);
        }else{
            liveCoverDto.setTopicPriceChanged(topicData.getLastPriceIncr());
            percentage = new BigDecimal((liveMybatisDao.getLessPriceChangeTopicCount(topicData.getLastPriceIncr()) * 100.0 / liveMybatisDao.getTopicDataCount() ))
                    .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        liveCoverDto.setBeatTopicPercentage(percentage);
        liveCoverDto.setIsSteal(1);
        try {
            int coins= getAliveCoinsForSteal(uid,topicId);
        } catch (KingdomStealException e2) {
            if(e2.getErrorCode()==KingdomStealException.KINGDOM_STEALED){
                liveCoverDto.setIsSteal(2);
            }
        } catch(Exception e){
        	
        }

        return Response.success(ResponseStatus.GET_LIVE_COVER_SUCCESS.status, ResponseStatus.GET_LIVE_COVER_SUCCESS.message, liveCoverDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response barrage(LiveBarrageDto barrageDto) {
        log.info("barrage start ...");
        ShowBarrageDto showBarrageDto = new ShowBarrageDto();
        List<TopicBarrage> topicBarrages = liveMybatisDao.getBarrage(barrageDto.getTopicId(), barrageDto.getSinceId(), barrageDto.getTopId(), barrageDto.getBottomId());
        log.info("topicBarrages data success");
        for (TopicBarrage barrage : topicBarrages) {
            long uid = barrage.getUid();
            UserProfile userProfile = userService.getUserProfileByUid(uid);
            ShowBarrageDto.BarrageElement barrageElement = ShowBarrageDto.createElement();
            barrageElement.setUid(uid);
            barrageElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            barrageElement.setNickName(userProfile.getNickName());
            barrageElement.setLevel(userProfile.getLevel());
            if (barrageElement.getContentType() == Specification.LiveContent.TEXT.index) {
                barrageElement.setFragment(barrage.getFragment());
            } else if (barrageElement.getContentType() == Specification.LiveContent.IMAGE.index) {
                barrageElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + barrage.getFragmentImage());
            }
            barrageElement.setCreateTime(barrage.getCreateTime());
            barrageElement.setType(barrage.getType());
            barrageElement.setContentType(barrage.getContentType());
            barrageElement.setId(barrage.getId());
            Topic topic = liveMybatisDao.getTopicById(barrageDto.getTopicId());
            barrageElement.setInternalStatus(userService.getUserInternalStatus(uid, topic.getUid()));
            showBarrageDto.getBarrageElements().add(barrageElement);
        }
        log.info("barrage end ...");
        return Response.success(ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.status, ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.message, showBarrageDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response getLiveByCid(long cid, long uid,int vflag) {
        ShowLiveDto showLiveDto = new ShowLiveDto();
        Topic topic = liveMybatisDao.getTopicById(cid);
        if(topic==null){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
        }
        String KINGDOM_VIEW_KEY = "USER_VIEW_KINGDOM_"+uid+"_"+topic.getId();
        String visited =  cacheService.get(KINGDOM_VIEW_KEY);
        showLiveDto.setIsFirstView(visited==null?1:0);
        
        //特殊王国处理，对于给定的王国，如果非国王或核心圈则无法进入
        String specialTopicIds = userService.getAppConfigByKey("SPECIAL_KINGDOM_IDS");
        if(!StringUtils.isEmpty(specialTopicIds)){
        	String[] tmp = specialTopicIds.split(",");
        	String currentTopicId = String.valueOf(cid);
        	for(String t : tmp){
        		if(currentTopicId.equals(t)){//符合条件，则进行判断
        			if(!this.isKing(uid, topic.getUid()) && !this.isInCore(uid, topic.getCoreCircle())){
        				return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,"此王国需要经过国王邀请才允许进入");
        			}
        			break;
        		}
        	}
        }
        
        Content content = contentService.getContentByTopicId(cid);
        showLiveDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        showLiveDto.setUid(topic.getUid());
        UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
        showLiveDto.setV_lv(userProfile.getvLv());
        showLiveDto.setLevel(userProfile.getLevel());
        showLiveDto.setNickName(userProfile.getNickName());
        showLiveDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        showLiveDto.setCreateTime(topic.getCreateTime());
        showLiveDto.setUpdateTime(topic.getLongTime());
        Long favoriteCount = Long.valueOf(0);
        List<Long> topicIdList = new ArrayList<Long>();
        topicIdList.add(Long.valueOf(cid));
        Map<String, Long> memberMap = liveLocalJdbcDao.getTopicMembersCount(topicIdList);
        if(null != memberMap && memberMap.size() > 0){
            if(null != memberMap.get(String.valueOf(cid))){
                favoriteCount = memberMap.get(String.valueOf(cid));
            }
        }
        if(favoriteCount.longValue() > 0){
            showLiveDto.setFavoriteCount(favoriteCount.intValue() + 1);
        }else{
            showLiveDto.setFavoriteCount(1);
        }
        showLiveDto.setLikeCount(content.getLikeCount());
        showLiveDto.setPersonCount(content.getPersonCount());
        showLiveDto.setTopicId(topic.getId());
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topic.getId());
        if (liveFavorite != null) {
            showLiveDto.setFavorite(Specification.LiveFavorite.FAVORITE.index);
        } else {
            showLiveDto.setFavorite(Specification.LiveFavorite.NORMAL.index);
        }
        showLiveDto.setCid(content.getId());
        showLiveDto.setIsFollowed(userService.isFollow(topic.getUid(), uid));
        showLiveDto.setIsFollowMe(userService.isFollow(uid, topic.getUid()));
        showLiveDto.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid(), content.getUid()));
        showLiveDto.setTitle(topic.getTitle());
        showLiveDto.setStatus(topic.getStatus());
        showLiveDto.setIsLike(contentService.isLike(content.getId(), uid));
        showLiveDto.setInternalStatus(this.getInternalStatus(topic, uid));
        showLiveDto.setContentType(topic.getType());

        if(activityService.isTopicRec(cid)){
            showLiveDto.setIsRec(1);
        }else{
            showLiveDto.setIsRec(0);
        }

        if(vflag > 0){
            //标签
            List<Long> tagIdList = new ArrayList<Long>();
            String tags = "";
            List<TopicTagDetail> topicTagDetails = liveMybatisDao.getTopicTagDetailsByTopicId(cid);
            if(topicTagDetails != null && topicTagDetails.size() > 0){
                StringBuilder builder = new StringBuilder();
                for (TopicTagDetail detail : topicTagDetails){
                    tagIdList.add(detail.getTagId());
                    String tag = detail.getTag();
                    if(tags.equals("")){
                        tags = builder.append(tag).toString();
                    }else {
                        builder.append(";"+tag);
                    }
                }
                showLiveDto.setTags(builder.toString());
            }

            /*boolean isAdmin = userService.isAdmin(uid);
            if(showLiveDto.getInternalStatus() == Specification.SnsCircle.CORE.index
                    || isAdmin){//核心圈的或者管理员，需要返回推荐标签
                //第一步，先返回运营指定的推荐标签（1个）
                TopicTag recTag = liveMybatisDao.getRecTopicTagWithoutOwn(cid, tagIdList, isAdmin);
                if(null != recTag){
                    showLiveDto.setRecTags(recTag.getTag());
                }
            }*/
        }

        if(topic.getType() == Specification.KingdomType.NORMAL.index){//个人王国
            //被聚合次数
            int ceCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId2(cid);
            showLiveDto.setCeCount(ceCount);
        }if(topic.getType() == Specification.KingdomType.AGGREGATION.index){//聚合王国
            //子王国数
            int acCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId(cid);
            showLiveDto.setAcCount(acCount);
            //子王国top5列表
            int needNum = 30;
            //置顶的按置顶时间倒序，非置顶的按更新时间倒叙
            List<Map<String, Object>> acTopList = liveLocalJdbcDao.getAcTopicListByCeTopicId(cid, 0, needNum);

            if(null != acTopList && acTopList.size() > 0){
                List<Long> uidList = new ArrayList<Long>();
                Long id = null;
                for(Map<String, Object> t : acTopList){
                    id = (Long)t.get("uid");
                    if(!uidList.contains(id)){
                        uidList.add(id);
                    }
                }

                ShowLiveDto.TopicElement e = null;
                for(Map<String, Object> t : acTopList){
                    e = new ShowLiveDto.TopicElement();
                    e.setTopicId((Long)t.get("id"));
                    e.setTitle((String)t.get("title"));
                    e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)t.get("live_image"));
                    e.setInternalStatus(this.getUserInternalStatus((String)t.get("core_circle"), uid));
                    showLiveDto.getAcTopList().add(e);
                }
            }
        }else{
            //暂不支持
        }
        showLiveDto.setTopicPrice(topic.getPrice());
        showLiveDto.setTopicRMB(exchangeKingdomPrice(topic.getPrice()));
        TopicData topicData = liveMybatisDao.getTopicDataByTopicId(cid);
        int percentage = 0;
        if(topicData==null){
            showLiveDto.setTopicPriceChanged(0);
        }else{
            showLiveDto.setTopicPriceChanged(topicData.getLastPriceIncr());
            percentage = new BigDecimal((liveMybatisDao.getLessPriceChangeTopicCount(topicData.getLastPriceIncr()) * 100.0 / liveMybatisDao.getTopicDataCount() ))
                    .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        showLiveDto.setBeatTopicPercentage(percentage);
        showLiveDto.setIsSteal(1);
        try {
            int coins= getAliveCoinsForSteal(uid,cid);
        } catch (KingdomStealException e2) {
            if(e2.getErrorCode()==KingdomStealException.KINGDOM_STEALED){
                showLiveDto.setIsSteal(2);
            }
        } catch(Exception e){
        	
        }

        return Response.success(showLiveDto);
    }

    private void buildLiveTimeLine(GetLiveTimeLineDto getLiveTimeLineDto, LiveTimeLineDto liveTimeLineDto, List<TopicFragment> fragmentList) {
        List<Long> uidList = new ArrayList<Long>();
        for (TopicFragment topicFragment : fragmentList) {
            if(!uidList.contains(topicFragment.getUid())){
                uidList.add(topicFragment.getUid());
            }
            if (null != topicFragment.getAtUid() && topicFragment.getAtUid() != 0) {
                if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
                        || topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
                        || topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
                    if(!uidList.contains(topicFragment.getAtUid())){
                        uidList.add(topicFragment.getAtUid());
                    }
                }
            }
        }

        Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
        List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
        if(null != userList && userList.size() > 0){
            for(UserProfile u : userList){
                userMap.put(u.getUid().toString(), u);
            }
        }

        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(getLiveTimeLineDto.getUid(), uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }

        Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
        LiveTimeLineDto.LiveElement liveElement = null;
        UserProfile atUser = null;
        UserProfile userProfile = null;
        for (TopicFragment topicFragment : fragmentList) {
            long uid = topicFragment.getUid();

            liveElement = LiveTimeLineDto.createElement();
            int status = topicFragment.getStatus();
            liveElement.setStatus(status);
            liveElement.setId(topicFragment.getId());
            if(status==0){
                continue;
            }

            if(!CommonUtils.isNewVersion(getLiveTimeLineDto.getVersion(), "2.2.2")){
                if(topicFragment.getType() == 51 && topicFragment.getContentType()==16){//足迹
                    liveElement.setStatus(0);
                    continue;
                }else if(topicFragment.getType() == 1000){//系统灰条
                    liveElement.setStatus(0);
                    continue;
                }
            }
            if(!CommonUtils.isNewVersion(getLiveTimeLineDto.getVersion(), "2.2.4")){
                if(topicFragment.getType() == 51 || topicFragment.getType() == 52){
                    if(topicFragment.getContentType() == 17 || topicFragment.getContentType() == 18){//表情包
                        liveElement.setStatus(0);
                        continue;
                    }
                }
            }
            if(!CommonUtils.isNewVersion(getLiveTimeLineDto.getVersion(), "2.2.5")){
                if(topicFragment.getType() == 51 || topicFragment.getType() == 52){
                    if(topicFragment.getContentType() == 20){//逗一逗
                        liveElement.setStatus(0);
                        continue;
                    }
                }
                if(topicFragment.getType() == 52){
                    if(topicFragment.getContentType() == 19){//投票
                        liveElement.setStatus(0);
                        continue;
                    }
                }
            }
            //逗一逗自动播放状态
            int teaseStatus = 0;
            if((topicFragment.getType() == 51 || topicFragment.getType() == 52) && topicFragment.getContentType() == 20){
                JSONObject extraJson  = 	 JSONObject.parseObject(topicFragment.getExtra());
                if((getLiveTimeLineDto.getUid()+"").equals(extraJson.getString("uid"))){
                    TeaseAutoPlayStatusModel teaseAutoPlayStatusModel =  new TeaseAutoPlayStatusModel(topicFragment.getId(), "0");
                    String isTeaseStatus = cacheService.hGet(teaseAutoPlayStatusModel.getKey(), teaseAutoPlayStatusModel.getField());
                    if (!StringUtils.isEmpty(isTeaseStatus)) {
                        teaseStatus=0;
                    } else {
                        teaseStatus=1;
                        cacheService.hSet(teaseAutoPlayStatusModel.getKey(), teaseAutoPlayStatusModel.getField(), teaseAutoPlayStatusModel.getValue());
                    }
                }
            }
            liveElement.setTeaseStatus(teaseStatus);

            userProfile = userMap.get(String.valueOf(uid));
            liveElement.setUid(uid);
            if(null != userProfile){
                liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                liveElement.setNickName(userProfile.getNickName());
                liveElement.setV_lv(userProfile.getvLv());
                liveElement.setLevel(userProfile.getLevel());
            }
            liveElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if (!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            if(null != followMap.get(getLiveTimeLineDto.getUid()+"_"+topicFragment.getUid())){
                liveElement.setIsFollowed(1);
            }else{
                liveElement.setIsFollowed(0);
            }
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveElement.setSource(topicFragment.getSource());
            if(!StringUtils.isEmpty(topicFragment.getSource())) {
                liveElement.setExtra(topicFragment.getExtra());
            }

            liveElement.setInternalStatus(getInternalStatus(topic, uid));
            if (null != topicFragment.getAtUid() && topicFragment.getAtUid() != 0) {
                if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
                        || topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
                        || topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
                    atUser = userMap.get(topicFragment.getAtUid().toString());
                    if(null != atUser){
                        liveElement.setAtUid(atUser.getUid());
                        liveElement.setAtNickName(atUser.getNickName());
                    }
                }
            }
            liveElement.setScore(topicFragment.getScore());
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
    }

    //判断核心圈身份
    private int getInternalStatus(Topic topic, long uid) {
        String coreCircle = topic.getCoreCircle();
        JSONArray array = JSON.parseArray(coreCircle);
        int internalStatus = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == uid) {
                internalStatus = Specification.SnsCircle.CORE.index;
                break;
            }
        }
//        if (internalStatus == 0) {
//            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
//        }

        return internalStatus;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response speak(SpeakDto speakDto) {
        log.info("speak start ...");
        if (speakDto.getType() != Specification.LiveSpeakType.LIKES.index && speakDto.getType() != Specification.LiveSpeakType.SUBSCRIBED.index && speakDto.getType() != Specification.LiveSpeakType.SHARE.index && speakDto.getType() != Specification.LiveSpeakType.FOLLOW.index && speakDto.getType() != Specification.LiveSpeakType.INVITED.index) {
            //由于低版本前端在at的时候有bug，故关于at这里要做一个保护措施
            if(speakDto.getType() == Specification.LiveSpeakType.AT.index
                    || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
                    || speakDto.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
                if(!StringUtils.isEmpty(speakDto.getExtra())){
                    JSONObject obj = JSON.parseObject(speakDto.getExtra());
                    int start = 0;
                    int end = 0;
                    if(null != obj.get("atStart")){
                        start = obj.getIntValue("atStart");
                    }
                    if(null != obj.get("atEnd")){
                        end = obj.getIntValue("atEnd");
                    }
                    if(start > end){
                        obj.put("atStart", 0);
                        obj.put("atEnd", 0);
                        speakDto.setExtra(obj.toJSONString());
                    }
                }
                if(!StringUtils.isEmpty(speakDto.getFragment())){
                    JSONObject obj = JSON.parseObject(speakDto.getFragment());
                    int start = 0;
                    int end = 0;
                    if(null != obj.get("atStart")){
                        start = obj.getIntValue("atStart");
                    }
                    if(null != obj.get("atEnd")){
                        end = obj.getIntValue("atEnd");
                    }
                    if(start > end){
                        obj.put("atStart", 0);
                        obj.put("atEnd", 0);
                        speakDto.setFragment(obj.toJSONString());
                    }
                }
            }

            TopicFragment topicFragment = new TopicFragment();
            topicFragment.setFragmentImage(speakDto.getFragmentImage());
            topicFragment.setFragment(speakDto.getFragment());
            topicFragment.setUid(speakDto.getUid());
            topicFragment.setContentType(speakDto.getContentType());
            topicFragment.setType(speakDto.getType());
            topicFragment.setTopicId(speakDto.getTopicId());
            topicFragment.setBottomId(speakDto.getBottomId());
            topicFragment.setTopId(speakDto.getTopId());
            int score = getTopicFragmentScore(speakDto.getType(),speakDto.getContentType());
            topicFragment.setScore(score);
            speakDto.setScore(score);
            long atUid = speakDto.getAtUid();
            if(atUid==-1){
                JSONObject fragment = JSON.parseObject(speakDto.getFragment());
                if(fragment!=null){
                    JSONArray atArray = fragment.containsKey("atArray")?fragment.getJSONArray("atArray"):null;
                    if(atArray!=null&&atArray.size()>0) {
                        topicFragment.setAtUid(atArray.getLongValue(0));
                    }
                }
            }else{
                topicFragment.setAtUid(speakDto.getAtUid());
            }
            topicFragment.setSource(speakDto.getSource());
            topicFragment.setExtra(speakDto.getExtra());
            liveMybatisDao.createTopicFragment(topicFragment);

            //由于系统消息和足迹不参与王国更新排序计算，故这里不需要更新时间
            if(speakDto.getType() != Specification.LiveSpeakType.SYSTEM.index
                    && (speakDto.getType() != 51 || speakDto.getContentType() != 16)){
                Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
                Calendar calendar = Calendar.getInstance();
                topic.setUpdateTime(calendar.getTime());
                topic.setLongTime(calendar.getTimeInMillis());
                liveMybatisDao.updateTopic(topic);
                
                //国王/核心圈发言，需要更新content表的updateTime
                if(topic.getUid().longValue() == speakDto.getUid() 
                		|| speakDto.getType() == 0 || speakDto.getType() == 3
                		|| speakDto.getType() == 11 || speakDto.getType() == 12
                		|| speakDto.getType() == 13 || speakDto.getType() == 15
                		|| speakDto.getType() == 52 || speakDto.getType() == 55){
                	liveLocalJdbcDao.updateContentUpdateTime4Kingdom(speakDto.getTopicId(), calendar.getTime());
                	liveLocalJdbcDao.updateContentUpdateId4Kingdom(speakDto.getTopicId(),cacheService.incr("UPDATE_ID"));
                	
                	//判断是否第一次更新
                	if(userService.isUserFirst(speakDto.getUid(), Specification.UserFirstActionType.SPEAK_UPDATE.index)){
                		speakDto.setIsFirstUpdate(1);
                		userService.saveUserFistLog(speakDto.getUid(), Specification.UserFirstActionType.SPEAK_UPDATE.index);
                	}else{
                		speakDto.setIsFirstUpdate(0);
                	}
                }
                
                log.info("updateTopic updateTime");
            }

            long fid = topicFragment.getId();

            SpeakNewEvent speakNewEvent = new SpeakNewEvent();
            speakNewEvent.setTopicId(speakDto.getTopicId());
            speakNewEvent.setType(speakDto.getType());
            speakNewEvent.setContentType(speakDto.getContentType());
            speakNewEvent.setUid(speakDto.getUid());
            speakNewEvent.setFragmentId(fid);
            speakNewEvent.setAtUid(speakDto.getAtUid());
            speakNewEvent.setFragmentContent(speakDto.getFragment());
            speakNewEvent.setFragmentExtra(speakDto.getExtra());
            applicationEventBus.post(speakNewEvent);
            //--add update kingdom cache -- modify by zcl -- begin --
            //此处暂不考虑原子操作
            int total = liveMybatisDao.countFragmentByTopicId(speakDto.getTopicId());
            String value = fid + "," + total;
            cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + speakDto.getTopicId(), value);
            //--add update kingdom cache -- modify by zcl -- end --

//            this.activitySpecialTopicHandler(speakDto.getUid(), speakDto.getTopicId(), fid, speakDto.getType(), speakDto.getContentType(), 1);
        }
        //获取最后一次发言FragmentId
        TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(speakDto.getTopicId(), speakDto.getUid());
        if (topicFragment != null) {
            speakDto.setFragmentId(topicFragment.getId());
        }
        log.info("createTopicFragment success");
        //弹幕已经不需要了
//        TopicBarrage topicBarrage = new TopicBarrage();
//        topicBarrage.setFragmentImage(speakDto.getFragmentImage());
//        topicBarrage.setFragment(speakDto.getFragment());
//        topicBarrage.setBottomId(speakDto.getBottomId());
//        topicBarrage.setTopicId(speakDto.getTopicId());
//        topicBarrage.setTopId(speakDto.getTopId());
//        topicBarrage.setContentType(speakDto.getContentType());
//        topicBarrage.setType(speakDto.getType());
//        topicBarrage.setUid(speakDto.getUid());
//        topicBarrage.setFid(fid);

        //保存弹幕
//        TopicBarrage barrage = liveMybatisDao.getBarrage(speakDto.getTopicId(), speakDto.getTopId(), speakDto.getBottomId(), speakDto.getType(), speakDto.getUid());
//        if (barrage == null) {
//            if (speakDto.getType() != Specification.LiveSpeakType.ANCHOR.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_AT.index && speakDto.getType() != Specification.LiveSpeakType.VIDEO.index && speakDto.getType() != Specification.LiveSpeakType.SOUND.index) {
//                liveMybatisDao.createTopicBarrage(topicBarrage);
//            }
//        } else {
//            if (speakDto.getType() == Specification.LiveSpeakType.SUBSCRIBED.index || speakDto.getType() == Specification.LiveSpeakType.FANS.index || speakDto.getType() == Specification.LiveSpeakType.FORWARD.index || speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.SHARE.index || speakDto.getType() == Specification.LiveSpeakType.AT.index) {
//                liveMybatisDao.createTopicBarrage(topicBarrage);
//            }
//        }
//        if(barrage == null && speakDto.getType() != Specification.LiveSpeakType.ANCHOR.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_AT.index && speakDto.getType() != Specification.LiveSpeakType.VIDEO.index) {
//            liveMybatisDao.createTopicBarrage(topicBarrage);
//        }else if(barrage != null && (speakDto.getType() == Specification.LiveSpeakType.SUBSCRIBED.index || speakDto.getType() == Specification.LiveSpeakType.FANS.index || speakDto.getType() == Specification.LiveSpeakType.FORWARD.index || speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.LIKES.index || speakDto.getType() == Specification.LiveSpeakType.SHARE.index ||speakDto.getType() == Specification.LiveSpeakType.AT.index  )){
//            liveMybatisDao.createTopicBarrage(topicBarrage);
//        }
        log.info("createTopicBarrage success");
        //提醒
        int like = 0;
        if (speakDto.getType() == Specification.LiveSpeakType.LIKES.index) {
            LikeDto likeDto = new LikeDto();
            //点赞
            Content content = contentService.getContentByTopicId(speakDto.getTopicId());
            likeDto.setCid(content.getId());
            likeDto.setAction(0);
            likeDto.setUid(speakDto.getUid());
            likeDto.setType(Specification.LikesType.LIVE.index);
            contentService.like2(likeDto);
            CoinRule coinRule =  userService.getCoinRules().get(Rules.LIKES_UGC_KEY);
            coinRule.setExt(content.getId());
            ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(likeDto.getUid(), coinRule);
            speakDto.setUpgrade(modifyUserCoinDto.getUpgrade());
            speakDto.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
            like = 1 ;

        } else if (speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            //贴标
            Content content = contentService.getContentByTopicId(speakDto.getTopicId());
            WriteTagDto writeTagDto = new WriteTagDto();
            writeTagDto.setType(Specification.WriteTagType.CONTENT.index);
            writeTagDto.setUid(speakDto.getUid());
            writeTagDto.setCid(content.getId());
            writeTagDto.setTag(speakDto.getFragment());
            contentService.writeTag2(writeTagDto);
        }
//        Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
        //直播发言时候更新直播更新时间
//        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.VIDEO.index || speakDto.getType() == Specification.LiveSpeakType.SOUND.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index||speakDto.getType()==Specification.LiveSpeakType.AT_CORE_CIRCLE.index
//        		|| this.isInCore(speakDto.getUid(), topic.getCoreCircle())) {
//            Calendar calendar = Calendar.getInstance();
//            topic.setUpdateTime(calendar.getTime());
//            topic.setLongTime(calendar.getTimeInMillis());
//            liveMybatisDao.updateTopic(topic);
//            log.info("updateTopic updateTime");
//        }

        int isJion = 0 ;
        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index) {
//            List<LiveFavorite> list = liveMybatisDao.getFavoriteList(speakDto.getTopicId());
//            for(LiveFavorite liveFavorite : list) {
//                //主播发言提醒关注的人
//                //userService.push(liveFavorite.getUid(),topic.getUid(),Specification.PushMessageType.UPDATE.index,topic.getTitle());
//                log.info("update push");
//            }
            //更新或者是核心圈跟新加分
            CoinRule coinRule = userService.getCoinRules().get(Rules.SPEAK_KEY);
            isJion = 1;
            /*coinRule.setExt(speakDto.getUid());*/
            ModifyUserCoinDto muDto= userService.coinRule(speakDto.getUid(), userService.getCoinRules().get(Rules.SPEAK_KEY));
            speakDto.setUpgrade(muDto.getUpgrade());
            speakDto.setCurrentLevel(muDto.getCurrentLevel());


        } else if (speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            //粉丝贴标提醒
            //Topic live = liveMybatisDao.getTopicById(speakDto.getTopicId());
            //liveRemind(live.getUid(), speakDto.getUid() ,Specification.LiveSpeakType.FANS_WRITE_TAG.index ,speakDto.getTopicId(),speakDto.getFragment());
            //userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_TAG.index,topic.getTitle());
            //log.info("live tag push");
        } else if (speakDto.getType() == Specification.LiveSpeakType.FANS.index) {
            //粉丝发言提醒
            //Topic live = liveMybatisDao.getTopicById(speakDto.getTopicId());
            //liveRemind(live.getUid(), speakDto.getUid() ,Specification.LiveSpeakType.FANS.index ,speakDto.getTopicId(),speakDto.getFragment());
            //userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_REVIEW.index,topic.getTitle());
            //log.info("live review push");
        } else if (speakDto.getType() == Specification.LiveSpeakType.AT.index) {
            RemindAndJpushAtMessageEvent event = new RemindAndJpushAtMessageEvent();
            event.setSpeakDto(speakDto);
            applicationEventBus.post(event);
//            remindAndJpushAtMessage(speakDto);
        } else if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index) {
            RemindAndJpushAtMessageEvent event = new RemindAndJpushAtMessageEvent();
            event.setSpeakDto(speakDto);
            applicationEventBus.post(event);
//            remindAndJpushAtMessage(speakDto);
        } else if (speakDto.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index) { //2.1.2
            RemindAndJpushAtMessageEvent event = new RemindAndJpushAtMessageEvent();
            event.setSpeakDto(speakDto);
            applicationEventBus.post(event);
//            remindAndJpushAtMessage(speakDto);
        }
        log.info("speak end ...");
        //2.0.7
        //直播信息保存
        //saveLiveDisplayData(speakDto);
        //判断是否升级
        int share = 0 ;
        //判断不是足迹 和不是大表情
        if( speakDto.getContentType() != 16 && speakDto.getContentType() != 17  ){
            //判断是分享的Type
            if(speakDto.getType() == 52 || speakDto.getType() == 51 || speakDto.getType() == 72 ){
            CoinRule coinRuleShare = userService.getCoinRules().get(Rules.SHARE_KING_KEY);
            ModifyUserCoinDto muDto= userService.coinRule(speakDto.getUid(), coinRuleShare);
            speakDto.setUpgrade(muDto.getUpgrade());
            speakDto.setCurrentLevel(muDto.getCurrentLevel());
            share = 1 ;
        }}
        //如果不是 加入王国  喜欢王国  分享王国 进入 只加2分
        if(isJion != 1 && like !=1 && share != 1) {
       /* log.info("############################################################################");
        log.info("############################################################################");*/
        CoinRule coinRule = userService.getCoinRules().get(Rules.SPEAK_KEY);
       /* log.info("coinRule info : " + coinRule.getName());
        log.info("############################################################################");
        log.info("############################################################################");
*/
            ModifyUserCoinDto muDto = userService.coinRule(speakDto.getUid(), userService.getCoinRules().get(Rules.SPEAK_KEY));
            speakDto.setUpgrade(muDto.getUpgrade());
            speakDto.setCurrentLevel(muDto.getCurrentLevel());
        }
        // 核心圈发文本,自动打Tag
        if(speakDto.getType()==0 && speakDto.getContentType()==0){
        	//cacheService.set(key, value);
        	//判断本王国是否有未过试用期的TAG，如果有，就不打了；
        	try{
	        	String tag = searchService.recommendTags(speakDto.getFragment(), 1).getData().getTags().get(0);
	        	boolean exists =liveLocalJdbcDao.existsTrialTagInKingdom(speakDto.getTopicId(),tag);
	        	if(!exists){
	        		TopicTag ttag = liveMybatisDao.getTopicTagByTag(tag);
	        		TopicTagDetail detail = new TopicTagDetail();
	        		detail.setTopicId(speakDto.getTopicId());
	        		detail.setUid(-1L);
	        		if(ttag!=null){
	        			detail.setTagId(ttag.getId());
	        		}
	        		detail.setTag(tag);
	        		detail.setCreateTime(new Date());
	        		detail.setStatus(0);
	        		detail.setAutoTag(1);
	        		liveMybatisDao.insertTopicTagDetail(detail);
	        	}
        	}catch(Exception e){
        		log.error("自动打标签失败", e);
        	}
        }
        
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status, ResponseStatus.USER_SPEAK_SUCCESS.message, speakDto);
    }

    private void activitySpecialTopicHandler(long uid, long topicId, long fragmentId, int type, int contentType, int optAction){
        //附加逻辑，活动需求，特殊王国在发言时增加用户的荣誉值以及该王国的热度
        String specialSwitch = cacheService.get(CacheConstant.ACTIVITY_SPECIAL_TOPIC_HANDLE_KEY);
        if(!StringUtils.isEmpty(specialSwitch) && "on".equals(specialSwitch)){//开关存在，并且是打开的状态
            String keyType = null;
            //判断是否特殊王国
            Set<String> specialTopicList = cacheService.smembers(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY);
            //王国内的操作
            if(null != specialTopicList && specialTopicList.size() > 0
                    && specialTopicList.contains(String.valueOf(topicId))){
                if((type == Specification.LiveSpeakType.ANCHOR.index && contentType == Specification.LiveContent.TEXT.index)
                        || type == Specification.LiveSpeakType.FANS.index){//主播文本发言 || 粉丝回复
                    //文本
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_TEXT;
                }else if(type == Specification.LiveSpeakType.ANCHOR.index && contentType == Specification.LiveContent.IMAGE.index){
                    //图片
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_IMAGE;
                }else if(type == Specification.LiveSpeakType.VIDEO.index){
                    //视频
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_VIDEO;
                }else if(type == Specification.LiveSpeakType.SOUND.index){
                    //音频
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_AUDIO;
                }else if(type == Specification.LiveSpeakType.AT.index
                        || type == Specification.LiveSpeakType.ANCHOR_AT.index
                        || type == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){//普通@，主播@，@核心圈
                    //@
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_AT;
                }else if((type==51||type==52) && contentType == 72){//分享王国内链
                    //分享
                    keyType = CacheConstant.SPECIAL_TOPIC_TYPE_SHARE;
                }
            }else if(null != specialTopicList && specialTopicList.size() > 0){
                //王国外的操作，这里只有一个对外分享
                if((type==51||type==52) && contentType == 72){//首先这个得是分享
                    TopicFragment tf = liveMybatisDao.getTopicFragmentById(fragmentId);
                    if(null != tf && !StringUtils.isEmpty(tf.getExtra())){
                        JSONObject obj = JSON.parseObject(tf.getExtra());
                        if(null != obj.get("id")){
                            Long kingdomId = obj.getLong("id");
                            if(kingdomId.longValue() > 0 && specialTopicList.contains(kingdomId.toString())){
                                //对外分享的是活动王国
                                keyType = CacheConstant.SPECIAL_TOPIC_TYPE_OUT_SHARE;
                            }
                        }
                    }
                }
            }

            if(null == keyType){
                return;//不在我们定义的范围内
            }

            String key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + keyType;
            if(optAction == 1){//新增
                String limit = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_LIMIT);
                if(StringUtils.isEmpty(limit)){
                    return;//未设置
                }
                int limitCount = Integer.valueOf(limit);
                if(limitCount < 1){
                    return;
                }
                AcommonList alist = activityService.getAcommonList(topicId, 3, 1);
                if(null == alist){
                    return;
                }
                //总限额
                String totalLimitStr = cacheService.get(CacheConstant.SPECIAL_TOPIC_HOT_LIMIT_TOTAL);
                int totalLimit = 0;
                if(!StringUtils.isEmpty(totalLimitStr)){
                    totalLimit = Integer.valueOf(totalLimitStr);
                }
                if(alist.getScore().intValue() >= totalLimit){
                    return;
                }

                String personLimit = cacheService.get(CacheConstant.SPECIAL_TOPIC_USER_LIMIT_PRE + keyType + CacheConstant.KEY_SEPARATOR_UNDERLINE + uid);
                if(StringUtils.isEmpty(personLimit)){
                    personLimit = "1";
                }else{
                    int personLimitCount = Integer.valueOf(personLimit);
                    if(personLimitCount >= limitCount){
                        return;//已经到上限了
                    }
                    personLimitCount++;
                    personLimit = String.valueOf(personLimitCount);
                }
                cacheService.set(CacheConstant.SPECIAL_TOPIC_USER_LIMIT_PRE + keyType + CacheConstant.KEY_SEPARATOR_UNDERLINE + uid, personLimit);

                //每日限额
                boolean isOverDayLimit = false;
                String dayLimitStr = cacheService.get(CacheConstant.SPECIAL_TOPIC_HOT_LIMIT_DAY);
                int dayLimit = 0;
                if(!StringUtils.isEmpty(dayLimitStr)){
                    dayLimit = Integer.valueOf(dayLimitStr);
                }
                String topicDayHotStr = cacheService.hGet(CacheConstant.SPECIAL_TOPIC_HOT_DAY_PRE+DateUtil.date2string(new Date(), "yyyyMMdd"), String.valueOf(topicId));
                int topicDayHot = 0;
                if(!StringUtils.isEmpty(topicDayHotStr)){
                    topicDayHot = Integer.valueOf(topicDayHotStr);
                }
                if(topicDayHot>=dayLimit){
                    isOverDayLimit = true;
                }

                String personScore = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_ADD);
                String kingdomScore = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_ADD);
                int personScoreCount = 0;
                if(!StringUtils.isEmpty(personScore)){
                    personScoreCount = Integer.valueOf(personScore);
                }
                int kingdomScoreCount = 0;
                if(!StringUtils.isEmpty(kingdomScore)){
                    kingdomScoreCount = Integer.valueOf(kingdomScore);
                }
                boolean needDetail = false;
                if(personScoreCount > 0){
                    if(isOverDayLimit){
                        personScoreCount = personScoreCount/2;
                    }
                    needDetail = true;
                    liveLocalJdbcDao.specialTopicAddHot(uid, 2, 3, personScoreCount);
                }
                if(kingdomScoreCount > 0){
                    if(isOverDayLimit){
                        kingdomScoreCount = kingdomScoreCount/2;
                    }
                    needDetail = true;
                    liveLocalJdbcDao.specialTopicAddHot(topicId, 1, 3, kingdomScoreCount);
                    topicDayHot = topicDayHot + kingdomScoreCount;
                    cacheService.hSet(CacheConstant.SPECIAL_TOPIC_HOT_DAY_PRE+DateUtil.date2string(new Date(), "yyyyMMdd"), String.valueOf(topicId), String.valueOf(topicDayHot));
                }
                if(needDetail){
                    liveLocalJdbcDao.insertSpecialTopicHotDetail(3, fragmentId);
                }
            }else if(optAction == 2){//删除
                //首先查询是否需要减，只有产生过热度或荣誉的那些记录是需要减的
                Map<String, Object> detail = liveLocalJdbcDao.getSpecialTopicHotDetail(3, fragmentId);
                if(null == detail){
                    return;
                }

                String personScore = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_DEL);
                String kingdomScore = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_DEL);
                int personScoreCount = 0;
                if(!StringUtils.isEmpty(personScore)){
                    personScoreCount = Integer.valueOf(personScore);
                }
                int kingdomScoreCount = 0;
                if(!StringUtils.isEmpty(kingdomScore)){
                    kingdomScoreCount = Integer.valueOf(kingdomScore);
                }
                if(personScoreCount > 0){
                    liveLocalJdbcDao.specialTopicReduceHot(uid, 2, 3, personScoreCount);
                }
                if(kingdomScoreCount > 0){
                    liveLocalJdbcDao.specialTopicReduceHot(topicId, 1, 3, kingdomScoreCount);
                }
            }
        }
    }

    /**
     * 获取我关注的直播，和我的直播列表
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Response MyLives(long uid, long sinceId) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        List<Topic> topicList = liveMybatisDao.getMyLives(uid, sinceId, topics);
        log.info("getMyLives data success");
        builder(uid, showTopicListDto, topicList);
        
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status, ResponseStatus.GET_MY_LIVE_SUCCESS.message, showTopicListDto);
    }

    /**
     * 获取所有正在直播列表
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Response Lives(long uid, long sinceId) {
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLives(sinceId);
        builder(uid, showTopicListDto, topicList);
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status, ResponseStatus.GET_LIVES_SUCCESS.message, showTopicListDto);
    }

    /**
     * 获取所有正在直播列表
     *
     * @param uid
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Response LivesByUpdateTime(long uid, long updateTime) {
        log.info("getLivesByUpdateTime start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLivesByUpdateTime(updateTime);
        log.info("getLivesByUpdateTime data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getLivesByUpdateTime end ...");
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status, ResponseStatus.GET_LIVES_SUCCESS.message, showTopicListDto);
    }

    private void builder(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        if(null == topicList || topicList.size() == 0){
            return;
        }
        List<Long> uidList = new ArrayList<Long>();
        List<Long> tidList = new ArrayList<Long>();
        List<Long> ceTidList = new ArrayList<Long>();
        for(Topic topic : topicList){
            if(!uidList.contains(topic.getUid())){
                uidList.add(topic.getUid());
            }
            if(!tidList.contains(topic.getId())){
                tidList.add(topic.getId());
            }
            if(topic.getType() == 1000){
                if(ceTidList.contains(topic.getId())){
                    ceTidList.add(topic.getId());
                }
            }
        }

        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(tidList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
            List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }
        //一次性查询所有王国的最新一条核心圈更新
        Map<String, Map<String, Object>> lastFragmentMap = new HashMap<String, Map<String, Object>>();
        List<Map<String, Object>> lastFragmentList = liveLocalJdbcDao.getLastCoreCircleFragmentByTopicIds(tidList);
        if(null != lastFragmentList && lastFragmentList.size() > 0){
            for(Map<String, Object> m : lastFragmentList){
                lastFragmentMap.put(String.valueOf(m.get("topic_id")), m);
            }
        }
        List<Long> cidList = new ArrayList<Long>();
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(tidList);
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
                if(!cidList.contains(c.getId())){
                    cidList.add(c.getId());
                }
            }
        }
        //一次性查询用户是否点赞过
        Map<String, Long> contentLikeCountMap = liveLocalJdbcDao.getLikeCountByUidAndCids(uid, cidList);
        if(null == contentLikeCountMap){
            contentLikeCountMap = new HashMap<String, Long>();
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
            }
        }

        UserProfile userProfile = null;
        Map<String, Object> lastFragment = null;
        Content content = null;
        ShowTopicListDto.ShowTopicElement showTopicElement = null;
        for (Topic topic : topicList) {
            showTopicElement = ShowTopicListDto.createShowTopicElement();

            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            userProfile = profileMap.get(String.valueOf(topic.getUid()));
            showTopicElement.setV_lv(userProfile.getvLv());
            showTopicElement.setLevel(userProfile.getLevel());
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            showTopicElement.setUpdateTime(topic.getLongTime());
         /*   UserProfile userProfile1 = userService.getUserProfileByUid(topic.getUid());*/
            showTopicElement.setLevel(userProfile.getLevel());
            if(null != followMap.get(uid+"_"+topic.getUid().toString())){
                showTopicElement.setIsFollowed(1);
            }else{
                showTopicElement.setIsFollowed(0);
            }
            if(null != followMap.get(topic.getUid().toString()+"_"+uid)){
                showTopicElement.setIsFollowMe(1);
            }else{
                showTopicElement.setIsFollowMe(0);
            }
            if(null != topicCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setTopicCount(topicCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setTopicCount(0);
            }
            showTopicElement.setInternalStatus(this.getInternalStatus(topic, uid));
            showTopicElement.setContentType(topic.getType());
            if(topic.getType() == 1000){
                if(null != acCountMap.get(String.valueOf(topic.getId()))){
                    showTopicElement.setAcCount(acCountMap.get(String.valueOf(topic.getId())).intValue());
                }else{
                    showTopicElement.setAcCount(0);
                }
            }

            lastFragment = lastFragmentMap.get(String.valueOf(topic.getId()));
            if (null != lastFragment) {
                showTopicElement.setLastContentType((Integer)lastFragment.get("content_type"));
                showTopicElement.setLastFragment((String)lastFragment.get("fragment"));
                showTopicElement.setLastFragmentImage((String)lastFragment.get("fragment_image"));
                showTopicElement.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
            } else {
                showTopicElement.setLastContentType(-1);
            }
            if(null != reviewCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setReviewCount(reviewCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setReviewCount(0);
            }
            content = contentMap.get(String.valueOf(topic.getId()));
            if (content != null) {
                showTopicElement.setLikeCount(content.getLikeCount());
                showTopicElement.setPersonCount(content.getPersonCount());
                showTopicElement.setFavoriteCount(content.getFavoriteCount()+1);//把国王加入进去
                showTopicElement.setCid(content.getId());
                if(null != contentLikeCountMap.get(String.valueOf(content.getId()))
                        && contentLikeCountMap.get(String.valueOf(content.getId())).longValue() > 0){
                    showTopicElement.setIsLike(1);
                }else{
                    showTopicElement.setIsLike(0);
                }
                showTopicElement.setReadCount(content.getReadCountDummy());
            }

            //判断是否收藏了
            if (null != liveFavoriteMap.get(String.valueOf(topic.getId()))) {
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }

            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void builderWithCache(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        if(null == topicList || topicList.size() == 0){
            return;
        }
        List<Long> uidList = new ArrayList<Long>();
        List<Long> tidList = new ArrayList<Long>();
        List<Long> ceTidList = new ArrayList<Long>();
        for(Topic topic : topicList){
            if(!uidList.contains(topic.getUid())){
                uidList.add(topic.getUid());
            }
            if(!tidList.contains(topic.getId())){
                tidList.add(topic.getId());
            }
            if(topic.getType() == 1000){//聚合王国
                if(!ceTidList.contains(topic.getId())){
                    ceTidList.add(topic.getId());
                }
            }
        }
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
            List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(tidList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        //一次性查询所有王国的最新一条核心圈更新
        Map<String, Map<String, Object>> lastFragmentMap = new HashMap<String, Map<String, Object>>();
        List<Map<String, Object>> lastFragmentList = liveLocalJdbcDao.getLastCoreCircleFragmentByTopicIds(tidList);
        if(null != lastFragmentList && lastFragmentList.size() > 0){
            for(Map<String, Object> m : lastFragmentList){
                lastFragmentMap.put(String.valueOf(m.get("topic_id")), m);
            }
        }
        List<Long> cidList = new ArrayList<Long>();
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(tidList);
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
                if(!cidList.contains(c.getId())){
                    cidList.add(c.getId());
                }
            }
        }
        //一次性查询用户是否点赞过
        Map<String, Long> contentLikeCountMap = liveLocalJdbcDao.getLikeCountByUidAndCids(uid, cidList);
        if(null == contentLikeCountMap){
            contentLikeCountMap = new HashMap<String, Long>();
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
            }
        }

        UserProfile userProfile = null;
        Map<String, Object> lastFragment = null;
        Content content = null;
        for (Topic topic : topicList) {
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            userProfile = profileMap.get(String.valueOf(topic.getUid()));
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setUpdateTime(topic.getLongTime());
            if(null != followMap.get(uid+"_"+topic.getUid().toString())){
                showTopicElement.setIsFollowed(1);
            }else{
                showTopicElement.setIsFollowed(0);
            }
            if(null != followMap.get(topic.getUid().toString()+"_"+uid)){
                showTopicElement.setIsFollowMe(1);
            }else{
                showTopicElement.setIsFollowMe(0);
            }
            if(null != topicCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setTopicCount(topicCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setTopicCount(0);
            }
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            showTopicElement.setV_lv(userProfile.getvLv());
            showTopicElement.setInternalStatus(this.getInternalStatus(topic, uid));
            showTopicElement.setContentType(topic.getType());
            if(topic.getType() == 1000){
                if(null != acCountMap.get(String.valueOf(topic.getId()))){
                    showTopicElement.setAcCount(acCountMap.get(String.valueOf(topic.getId())).intValue());
                }else{
                    showTopicElement.setAcCount(0);
                }
            }
            processCache(uid,topic,showTopicElement);
            lastFragment = lastFragmentMap.get(String.valueOf(topic.getId()));
            if (null != lastFragment) {
                showTopicElement.setLastContentType((Integer)lastFragment.get("content_type"));
                showTopicElement.setLastFragment((String)lastFragment.get("fragment"));
                showTopicElement.setLastFragmentImage((String)lastFragment.get("fragment_image"));
                showTopicElement.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
            } else {
                showTopicElement.setLastContentType(-1);
            }
            if(null != reviewCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setReviewCount(reviewCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setReviewCount(0);
            }
            content = contentMap.get(String.valueOf(topic.getId()));
            if (content != null) {
                showTopicElement.setLikeCount(content.getLikeCount());
                showTopicElement.setPersonCount(content.getPersonCount());
                showTopicElement.setFavoriteCount(content.getFavoriteCount()+1);//把国王加入进去
                showTopicElement.setCid(content.getId());
                if(null != contentLikeCountMap.get(String.valueOf(content.getId()))
                        && contentLikeCountMap.get(String.valueOf(content.getId())).longValue() > 0){
                    showTopicElement.setIsLike(1);
                }else{
                    showTopicElement.setIsLike(0);
                }
                showTopicElement.setReadCount(content.getReadCountDummy());
            }
            //判断是否收藏了
            if (null != liveFavoriteMap.get(String.valueOf(topic.getId()))) {
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }


            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void builderWithCache2(long uid, ShowTopicListDto showTopicListDto, List<Topic2> topicList) {
        if(null == topicList || topicList.size() == 0){
            return;
        }
        List<Long> uidList = new ArrayList<Long>();
        List<Long> tidList = new ArrayList<Long>();
        List<Long> ceTidList = new ArrayList<Long>();
        for(Topic2 topic : topicList){
            if(!uidList.contains(topic.getUid())){
                uidList.add(topic.getUid());
            }
            if(!tidList.contains(topic.getId())){
                tidList.add(topic.getId());
            }
            if(topic.getType() == 1000){//聚合王国
                if(!ceTidList.contains(topic.getId())){
                    ceTidList.add(topic.getId());
                }
            }
        }
        //一次性查询所有王国的最新一条数据
        Map<String, Map<String, Object>> lastFragmentMap = new HashMap<String, Map<String, Object>>();
        List<Map<String, Object>> lastFragmentList = liveLocalJdbcDao.getLastFragmentByTopicIds(tidList);
        if(null != lastFragmentList && lastFragmentList.size() > 0){
            Long fuid = null;
            for(Map<String, Object> m : lastFragmentList){
                lastFragmentMap.put(String.valueOf(m.get("topic_id")), m);
                fuid = (Long)m.get("uid");
                if(!uidList.contains(fuid)){
                    uidList.add(fuid);
                }
            }
        }
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
            List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(tidList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }

        List<Long> cidList = new ArrayList<Long>();
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(tidList);
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
                if(!cidList.contains(c.getId())){
                    cidList.add(c.getId());
                }
            }
        }
        //一次性查询用户是否点赞过
        Map<String, Long> contentLikeCountMap = liveLocalJdbcDao.getLikeCountByUidAndCids(uid, cidList);
        if(null == contentLikeCountMap){
            contentLikeCountMap = new HashMap<String, Long>();
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
            }
        }
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveLocalJdbcDao.getTopicMembersCount(tidList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }

        UserProfile userProfile = null;
        UserProfile lastUserProfile = null;
        Map<String, Object> lastFragment = null;
        Content content = null;
        for (Topic2 topic : topicList) {
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            userProfile = profileMap.get(String.valueOf(topic.getUid()));
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            /*int lastLevel =(Integer) liveLocalJdbcDao.getlastUser(topic.getId()).get("level");
            showTopicElement.setLevel(lastLevel);*/
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setPrice(topic.getPrice()); //2.2.7 王国估值
            //取这个排序
            showTopicElement.setUpdateTime(topic.getLongTimes());
            if(null != followMap.get(uid+"_"+topic.getUid().toString())){
                showTopicElement.setIsFollowed(1);
            }else{
                showTopicElement.setIsFollowed(0);
            }
            if(null != followMap.get(topic.getUid().toString()+"_"+uid)){
                showTopicElement.setIsFollowMe(1);
            }else{
                showTopicElement.setIsFollowMe(0);
            }
            if(null != topicCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setTopicCount(topicCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setTopicCount(0);
            }
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            showTopicElement.setV_lv(userProfile.getvLv());
            showTopicElement.setLevel(userProfile.getLevel());
            showTopicElement.setInternalStatus(this.getInternalStatus(topic, uid));
            showTopicElement.setContentType(topic.getType());
            if(topic.getType() == 1000){
                if(null != acCountMap.get(String.valueOf(topic.getId()))){
                    showTopicElement.setAcCount(acCountMap.get(String.valueOf(topic.getId())).intValue());
                }else{
                    showTopicElement.setAcCount(0);
                }
            }

            lastFragment = lastFragmentMap.get(String.valueOf(topic.getId()));
            if (null != lastFragment) {
                showTopicElement.setLastContentType((Integer)lastFragment.get("content_type"));
                showTopicElement.setLastFragment((String)lastFragment.get("fragment"));
                showTopicElement.setLastFragmentImage((String)lastFragment.get("fragment_image"));
                //新增
                showTopicElement.setLastType((Integer) lastFragment.get("type"));
                showTopicElement.setLastStatus((Integer)lastFragment.get("status"));
                showTopicElement.setLastExtra((String)lastFragment.get("extra"));
                showTopicElement.setLastAtUid((Long)lastFragment.get("at_uid"));
                showTopicElement.setIsTop(topic.getIsTop());
                showTopicElement.setLastUid((Long)lastFragment.get("uid"));
                if(null != lastFragment.get("create_time")){
                    showTopicElement.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
                }
                lastUserProfile = profileMap.get(String.valueOf(lastFragment.get("uid")));
                if(null != lastUserProfile){
                    showTopicElement.setLastNickName(lastUserProfile.getNickName());
                    showTopicElement.setLastAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
                    showTopicElement.setLastV_lv(lastUserProfile.getvLv());
                    showTopicElement.setLevel(lastUserProfile.getLevel());
                }
            } else {
                showTopicElement.setLastContentType(-1);
            }
            processCache2(uid,topic,showTopicElement);
            if(null != reviewCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setReviewCount(reviewCountMap.get(String.valueOf(topic.getId())).intValue());
            }else{
                showTopicElement.setReviewCount(0);
            }
            content = contentMap.get(String.valueOf(topic.getId()));
            if (content != null) {
                showTopicElement.setLikeCount(content.getLikeCount());
                showTopicElement.setPersonCount(content.getPersonCount());
                showTopicElement.setCid(content.getId());
                if(null != contentLikeCountMap.get(String.valueOf(content.getId()))
                        && contentLikeCountMap.get(String.valueOf(content.getId())).longValue() > 0){
                    showTopicElement.setIsLike(1);
                }else{
                    showTopicElement.setIsLike(0);
                }
                showTopicElement.setReadCount(content.getReadCountDummy());
            }
            if(null == topicMemberCountMap.get(String.valueOf(topic.getId()))){
                showTopicElement.setFavoriteCount(1);//默认只有国王一个成员
            }else{
                showTopicElement.setFavoriteCount(topicMemberCountMap.get(String.valueOf(topic.getId())).intValue()+1);
            }
            //判断是否收藏了
            if (null != liveFavoriteMap.get(String.valueOf(topic.getId()))) {
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }


            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void afterProcess(long uid, Topic topic, ShowTopicListDto.ShowTopicElement showTopicElement, TopicFragment topicFragment) {
        if (topicFragment != null) {
            showTopicElement.setLastContentType(topicFragment.getContentType());
            showTopicElement.setLastFragment(topicFragment.getFragment());
            showTopicElement.setLastFragmentImage(topicFragment.getFragmentImage());
            showTopicElement.setLastUpdateTime(topicFragment.getCreateTime().getTime());
        } else {
            showTopicElement.setLastContentType(-1);
        }
        Content content = contentService.getContentByTopicId(topic.getId());
        if (content != null) {
            showTopicElement.setLikeCount(content.getLikeCount());
            showTopicElement.setPersonCount(content.getPersonCount());
            showTopicElement.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid(), content.getUid()));
            showTopicElement.setFavoriteCount(content.getFavoriteCount()+1);//把国王加入进去
            showTopicElement.setCid(content.getId());
            showTopicElement.setIsLike(contentService.isLike(content.getId(), uid));
            showTopicElement.setReadCount(content.getReadCountDummy());
        }
    }

    private void processCache(long uid, Topic topic, ShowTopicListDto.ShowTopicElement showTopicElement) {
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId() + "", "0");
        String isUpdate = cacheService.hGet(cacheModel.getKey(), topic.getId() + "");
        showTopicElement.setIsUpdate(0);
        if (!StringUtils.isEmpty(isUpdate)) {
            int update = Integer.parseInt(isUpdate);
            if(update > 0){
                showTopicElement.setIsUpdate(1);
            }
        }
    }

    private void processCache2(long uid, Topic topic, ShowTopicListDto.ShowTopicElement showTopicElement) {
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId() + "", "0");
        String isUpdate = cacheService.hGet(cacheModel.getKey(), topic.getId() + "");
        if(StringUtils.isEmpty(isUpdate) || Integer.parseInt(isUpdate) == 0){
            showTopicElement.setIsUpdate(0);
        }else {
            showTopicElement.setIsUpdate(1);
            Map map = Maps.newHashMap();
            map.put("fid" ,isUpdate);
            map.put("uid" ,uid);
            map.put("topicId", topic.getId());
            TopicFragment topicFragment = liveMybatisDao.getFragmentByAT(map);
            if(topicFragment != null){
                showTopicElement.setLastContentType(topicFragment.getContentType());
                showTopicElement.setLastFragment(topicFragment.getFragment());
                showTopicElement.setLastFragmentImage(topicFragment.getFragmentImage());
                //新增
                showTopicElement.setLastType(topicFragment.getType());
                showTopicElement.setLastStatus(topicFragment.getStatus());
                showTopicElement.setLastExtra(topicFragment.getExtra());
                showTopicElement.setLastAtUid(topicFragment.getAtUid());
                showTopicElement.setLastUid(topicFragment.getUid());
                showTopicElement.setLastUpdateTime(topicFragment.getCreateTime().getTime());
                UserProfile lastUserProfile = userService.getUserProfileByUid(topicFragment.getUid());
                if(null != lastUserProfile){
                    showTopicElement.setLastNickName(lastUserProfile.getNickName());
                    showTopicElement.setLastAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
                    showTopicElement.setLastV_lv(lastUserProfile.getvLv());
                }
            }
        }
    }

    /**
     * 关注，取消关注
     *
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive(long uid, long topicId, long topId, long bottomId) {
        log.info("setLive start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if (liveFavorite != null) {
            liveMybatisDao.deleteLiveFavorite(liveFavorite);
            if ((content.getFavoriteCount() - 1) < 0) {
                content.setFavoriteCount(0);
            } else {
                content.setFavoriteCount(content.getFavoriteCount() - 1);
            }
            contentService.updateContentById(content);
            log.info("setLive end ...");
            return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        } else {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
            //保存弹幕
            TopicBarrage barrage = liveMybatisDao.getBarrage(topicId, topId, bottomId, Specification.LiveSpeakType.SUBSCRIBED.index, uid);
            saveBarrage(uid, topicId, topId, bottomId, barrage);
            content.setFavoriteCount(content.getFavoriteCount() + 1);
            contentService.updateContentById(content);
            log.info("setLive end ...");
            return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
        }
    }

    private void saveBarrage(long uid, long topicId, long topId, long bottomId, TopicBarrage barrage) {
        if (barrage == null) {
            TopicBarrage topicBarrage = new TopicBarrage();
            topicBarrage.setBottomId(bottomId);
            topicBarrage.setTopicId(topicId);
            topicBarrage.setTopId(topId);
            topicBarrage.setContentType(0);
            topicBarrage.setType(Specification.LiveSpeakType.SUBSCRIBED.index);
            topicBarrage.setUid(uid);
            //保存弹幕
            liveMybatisDao.createTopicBarrage(topicBarrage);
        }
    }

    /**
     * 订阅取消订阅
     *
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive2(long uid, long topicId, long topId, long bottomId, int action) {
        log.info("setLive2 start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if (action == 0) {
            if (liveFavorite == null) {
                liveFavorite = new LiveFavorite();
                liveFavorite.setTopicId(topicId);
                liveFavorite.setUid(uid);
                liveMybatisDao.createLiveFavorite(liveFavorite);
                liveMybatisDao.deleteFavoriteDelete(uid, topicId);
                //保存弹幕
                TopicBarrage barrage = liveMybatisDao.getBarrage(topicId, topId, bottomId, Specification.LiveSpeakType.SUBSCRIBED.index, uid);
                if (barrage != null) {
                    content.setFavoriteCount(content.getFavoriteCount() + 1);
                    contentService.updateContentById(content);
                    return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
                } else {
                    saveBarrage(uid, topicId, topId, bottomId, barrage);
                    content.setFavoriteCount(content.getFavoriteCount() + 1);
                    contentService.updateContentById(content);
                    log.info("setLive2 end ...");
                }
            }
            return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
        } else if (action == 1) {
            if (liveFavorite != null) {
                liveMybatisDao.deleteLiveFavorite(liveFavorite);
            }
            if (liveMybatisDao.getFavoriteDelete(uid, topicId) == null) {
                liveMybatisDao.createFavoriteDelete(uid, topicId);
                if ((content.getFavoriteCount() - 1) < 0) {
                    content.setFavoriteCount(0);
                } else {
                    content.setFavoriteCount(content.getFavoriteCount() - 1);
                }
            }
            contentService.updateContentById(content);
            log.info("setLive end ...");
            return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        }
        return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status, ResponseStatus.ILLEGAL_REQUEST.message);
    }

    @Override
    public Response setLiveFromSnsFollow(long uid, List<Long> topicIds, long topId, long bottomId, int action){
        log.info("setLiveFromSnsFollow start ...");
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(uid, topicIds);
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(lf.getUid()+"_"+lf.getTopicId(), lf);
            }
        }
        List<Content> contentList = contentService.getContentsByTopicIds(topicIds);
        Map<String, Content> contentMap = new HashMap<String, Content>();
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
            }
        }
        Response resp = null;
        if (action == 0) {//订阅
            resp = this.setLiveFollow(uid, topicIds, topId, bottomId, liveFavoriteMap, contentMap);
        } else if (action == 1) {//取消订阅
            resp = this.cancelLiveFollow(uid, topicIds, liveFavoriteMap, contentMap);
        }else{
            resp = Response.failure(ResponseStatus.ILLEGAL_REQUEST.status, ResponseStatus.ILLEGAL_REQUEST.message);
        }
        log.info("setLiveFromSnsFollow end ...");
        return resp;
    }

    private Response setLiveFollow(long uid, List<Long> topicIds, long topId, long bottomId, Map<String, LiveFavorite> liveFavoriteMap, Map<String, Content> contentMap){
        Map<String, TopicBarrage> barrageMap = new HashMap<String, TopicBarrage>();
        List<TopicBarrage> barrageList = liveMybatisDao.getBarrageListByTopicIds(topicIds, topId, bottomId, Specification.LiveSpeakType.SUBSCRIBED.index, uid);
        if(null != barrageList && barrageList.size() > 0){
            for(TopicBarrage tb : barrageList){
                barrageMap.put(String.valueOf(tb.getTopicId()), tb);
            }
        }

        List<LiveFavorite> needInsertLiveFavoriteList = new ArrayList<LiveFavorite>();
        List<Long> needDeleteFavoriteDeleteTopicIdList = new ArrayList<Long>();
        List<TopicBarrage> needInsertTopicBarrageList = new ArrayList<TopicBarrage>();
        List<Long> needAddOneContentIdList = new ArrayList<Long>();

        LiveFavorite liveFavorite = null;
        Content content = null;
        TopicBarrage barrage = null;
        for(Long topicId : topicIds){
            liveFavorite = liveFavoriteMap.get(uid + "_" + topicId);
            if (liveFavorite == null) {
                liveFavorite = new LiveFavorite();
                liveFavorite.setTopicId(topicId);
                liveFavorite.setUid(uid);
                needInsertLiveFavoriteList.add(liveFavorite);
                needDeleteFavoriteDeleteTopicIdList.add(topicId);

                //保存弹幕
                barrage = barrageMap.get(String.valueOf(topicId));
                if(null == barrage){
                    barrage = new TopicBarrage();
                    barrage.setBottomId(bottomId);
                    barrage.setTopicId(topicId);
                    barrage.setTopId(topId);
                    barrage.setContentType(0);
                    barrage.setType(Specification.LiveSpeakType.SUBSCRIBED.index);
                    barrage.setUid(uid);
                    needInsertTopicBarrageList.add(barrage);
                }
                content = contentMap.get(String.valueOf(topicId));
                if(null != content){
                    needAddOneContentIdList.add(content.getId());
                }
            }
        }

        //开始批量处理数据库更新
        if(needInsertLiveFavoriteList.size() > 0){
            liveLocalJdbcDao.batchInsertLiveFavorite(needInsertLiveFavoriteList);
        }
        if(needDeleteFavoriteDeleteTopicIdList.size() > 0){
            liveMybatisDao.batchDeleteFavoriteDeletes(uid, needDeleteFavoriteDeleteTopicIdList);
        }
        if(needInsertTopicBarrageList.size() > 0){
            liveLocalJdbcDao.batchInsertTopicBarrage(needInsertTopicBarrageList);
        }
        if(needAddOneContentIdList.size() > 0){
            liveLocalJdbcDao.updateContentAddOneFavoriteCount(needAddOneContentIdList);
        }

        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    @Override
    public Response subscribedTopicNew(long topicId, long uid, int action){
        log.info("subscribedTopicNew start ...");
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(null == topic){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, ResponseStatus.LIVE_HAS_DELETED.message);
        }
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);

        Response resp = null;
        if (action == 0) {//订阅
            if(null == liveFavorite){
                liveFavorite = new LiveFavorite();
                liveFavorite.setTopicId(topicId);
                liveFavorite.setUid(uid);
                liveMybatisDao.createLiveFavorite(liveFavorite);
                //content表favorite_count+1
                liveLocalJdbcDao.contentAddFavoriteCount(topicId, 1);
            }

            //判断是否是特殊王国，如果是特殊王国，则加入王国的同时，加入核心圈--“赛出家乡美”活动需求
            Set<String> specialTopicList = cacheService.smembers(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY);
            if(null != specialTopicList && specialTopicList.size() > 0 && specialTopicList.contains(String.valueOf(topicId))){
                //特殊王国，则加入的同时加入核心圈，无需关心是否超出核心圈上限
                if(!this.isInCore(uid, topic.getCoreCircle())){
                    topic = liveMybatisDao.getTopicById(topicId);//再拉一次，减少并发问题
                    JSONArray array = JSON.parseArray(topic.getCoreCircle());
                    array.add(uid);
                    topic.setCoreCircle(array.toJSONString());
                    liveMybatisDao.updateTopic(topic);
                }
            }
            CoinRule coinRule =userService.getCoinRules().get(Rules.JOIN_KING_KEY);
            coinRule.setExt(topicId);
            ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(uid,coinRule);
            SubscribedTopicDTO result = new SubscribedTopicDTO();
            result.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
            result.setUpgrade(modifyUserCoinDto.getUpgrade());
            
            if(userService.isUserFirst(uid, Specification.UserFirstActionType.SUBSCRIBED_KINGDOM.index)){
            	result.setIsFirst(1);
            	userService.saveUserFistLog(uid, Specification.UserFirstActionType.SUBSCRIBED_KINGDOM.index);
            }else{
            	result.setIsFirst(0);
            }
            
            resp  = Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message, result);
        } else if (action == 1) {//取消订阅
            if(null != liveFavorite){
                liveMybatisDao.deleteLiveFavorite(liveFavorite);
                //content表favorite_count-1
                liveLocalJdbcDao.contentAddFavoriteCount(topicId, 0);
            }
            //如果是核心圈的，取消订阅的同时，需要退出核心圈(国王不能退出核心圈)
            if(null != topic.getCoreCircle() && !"".equals(topic.getCoreCircle())
            		&& uid != topic.getUid().longValue()){
                JSONArray array = JSON.parseArray(topic.getCoreCircle());
                boolean needUpdate = false;
                for (int i = 0; i < array.size(); i++) {
                    if (array.getLong(i).longValue() == uid) {
                        array.remove(i);
                        needUpdate = true;
                    }
                }
                if(needUpdate){
                    topic.setCoreCircle(array.toJSONString());
                    liveMybatisDao.updateTopic(topic);
                }
            }

            resp = Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        }else{
            resp = Response.failure(ResponseStatus.ILLEGAL_REQUEST.status, ResponseStatus.ILLEGAL_REQUEST.message);
        }
        log.info("subscribedTopicNew end ...");
        return resp;
    }

    private Response cancelLiveFollow(long uid, List<Long> topicIds, Map<String, LiveFavorite> liveFavoriteMap, Map<String, Content> contentMap){
        Map<String, LiveFavoriteDelete> favoriteDeleteMap = new HashMap<String, LiveFavoriteDelete>();
        List<LiveFavoriteDelete> favoriteDeleteList = liveMybatisDao.getFavoriteDeletesByTopicIds(uid, topicIds);
        if(null != favoriteDeleteList && favoriteDeleteList.size() > 0){
            for(LiveFavoriteDelete lfd : favoriteDeleteList){
                favoriteDeleteMap.put(String.valueOf(lfd.getTopicId()), lfd);
            }
        }

        List<Long> needDeleteLiveFavoriteIdList = new ArrayList<Long>();
        List<LiveFavoriteDelete> needInsertLiveFavoriteDeleteList = new ArrayList<LiveFavoriteDelete>();
        List<Long> needDecrOneContentIdList = new ArrayList<Long>();

        LiveFavorite liveFavorite = null;
        LiveFavoriteDelete liveFavoriteDelete = null;
        Content content = null;
        for(Long topicId : topicIds){
            liveFavorite = liveFavoriteMap.get(uid + "_" + topicId);
            if (liveFavorite != null) {
                needDeleteLiveFavoriteIdList.add(liveFavorite.getId());
            }

            liveFavoriteDelete = favoriteDeleteMap.get(String.valueOf(topicId));
            if(null == liveFavoriteDelete){
                liveFavoriteDelete = new LiveFavoriteDelete();
                liveFavoriteDelete.setUid(uid);
                liveFavoriteDelete.setTopicId(topicId);
                needInsertLiveFavoriteDeleteList.add(liveFavoriteDelete);
                content = contentMap.get(String.valueOf(topicId));
                if(null != content){
                    needDecrOneContentIdList.add(content.getId());
                }
            }
        }

        //开始批量处理
        if(needDeleteLiveFavoriteIdList.size() > 0){
            liveLocalJdbcDao.deleteLiveFavoriteByIds(needDeleteLiveFavoriteIdList);
        }
        if(needInsertLiveFavoriteDeleteList.size() > 0){
            liveLocalJdbcDao.batchInsertLiveFavoriteDelete(needInsertLiveFavoriteDeleteList);
        }
        if(needDecrOneContentIdList.size() > 0){
            liveLocalJdbcDao.updateContentDecrOneFavoriteCount(needDecrOneContentIdList);
        }

        return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
    }

    /**
     * 订阅取消订阅
     *
     * @param uid
     * @param topicId
     * @return
     */
    public Response setLive3(long uid, long topicId) {
        log.info("setLive3 start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if (liveFavorite == null) {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
            liveMybatisDao.deleteFavoriteDelete(uid, topicId);
            content.setFavoriteCount(content.getFavoriteCount() + 1);
            contentService.updateContentById(content);
            log.info("setLive3 end ...");
        }
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    public Response setLive3WithBatch(List<Long> uids, long topicId){
        log.info("setLive3WithBatch start...");
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidsAndTopicId(uids, topicId);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getUid()), lf);
            }
        }

        List<LiveFavorite> needInsertLiveFavoriteList = new ArrayList<LiveFavorite>();
        List<Long> needDeleteFavoriteDeleteUidList = new ArrayList<Long>();

        LiveFavorite liveFavorite = null;
        int needContentAddOne = 0;
        for(Long uid : uids){
            liveFavorite = liveFavoriteMap.get(String.valueOf(uid));
            if(null == liveFavorite){
                liveFavorite = new LiveFavorite();
                liveFavorite.setTopicId(topicId);
                liveFavorite.setUid(uid);
                needInsertLiveFavoriteList.add(liveFavorite);
                needDeleteFavoriteDeleteUidList.add(uid);
                needContentAddOne++;
            }
        }

        if(needInsertLiveFavoriteList.size() > 0){
            liveLocalJdbcDao.batchInsertLiveFavorite(needInsertLiveFavoriteList);
        }
        if(needDeleteFavoriteDeleteUidList.size() > 0){
            liveMybatisDao.batchDeleteFavoriteDeletesByUids(uids, topicId);
        }
        if(needContentAddOne > 0){
            liveLocalJdbcDao.updateContentAddFavoriteCountByForwardCid(needContentAddOne, topicId);
        }

        log.info("setLive3WithBatch end!");
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    /**
     * 删除王国跟贴内容
     *
     * 王国删帖规则--20161114
     * 1）管理员和国王（非小王），可以删除王国里的任何发言、评论等。
     * 2）其他人只能删除自己发的评论。
     * 3）其中有个特例，小王的发言以及@核心圈是以卡片形式展现的，小王是无法删除卡片的。
     *
     * @param topicId
     * @param fid
     * @param uid
     * @return
     */
    @Override
    public Response deleteLiveFragment(long topicId, long fid, long uid) {
        log.info("delete topic fragment start ...");
        TopicFragment tf = liveMybatisDao.getTopicFragmentById(fid);
        if(null == tf || tf.getStatus().intValue() == Specification.TopicFragmentStatus.DISABLED.index){
            return Response.success(ResponseStatus.TOPIC_FRAGMENT_DELETE_SUCCESS.status, ResponseStatus.TOPIC_FRAGMENT_DELETE_SUCCESS.message);
        }
        try {
            //判断当前用户是否有删除本条内容的权限
            boolean canDel = false;
            //本人可删除自己的内容。
            if(tf.getUid() == uid){
                canDel=true;
            }
            //判断是否是管理员，管理员啥都能删
            if(userService.isAdmin(uid)){
                canDel = true;
            }
            //再验证是否是国王，国王也啥都能删
            Topic topic = liveMybatisDao.getTopicById(topicId);
            if(!canDel){
                if(topic.getUid() == uid){
                    canDel = true;
                }
            }
            // 核心圈可删除非核心圈内容。
            if(!canDel){
                boolean meInCore= this.isInCore(uid, topic.getCoreCircle());
                boolean fragmentUserInCore = this.isInCore(tf.getUid(), topic.getCoreCircle());
                if(meInCore && !fragmentUserInCore){
                    canDel=true;
                }
                //再判断是否是自己发的内容，自己的内容有可能是可以删
        		/*if(tf.getUid() == uid){
        			//再判断是否是卡片（核心圈发言、核心圈@），卡片不能删
        			if(tf.getType() != Specification.LiveSpeakType.ANCHOR.index
        					&& tf.getType() != Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
        				canDel = true;
        			}
        		}*/
            }

            if(!canDel){
                return Response.failure(ResponseStatus.TOPIC_FRAGMENT_CAN_NOT_DELETE.status, ResponseStatus.TOPIC_FRAGMENT_CAN_NOT_DELETE.message);
            }

            //从topicFragment中删除
            int updateRows = liveMybatisDao.deleteLiveFragmentById(fid);
            if (updateRows == 1) {
                DeleteLog deleteLog = new DeleteLog();
                deleteLog.setDelTime(new Date());
                deleteLog.setType(Specification.DeleteObjectType.TOPIC_FRAGMENT.index);
                deleteLog.setOid(fid);
                deleteLog.setUid(uid);

                liveMybatisDao.createDeleteLog(deleteLog);

                //删除特殊王国产生的热度等
                this.activitySpecialTopicHandler(tf.getUid(), tf.getTopicId(), tf.getId(), tf.getType(), tf.getContentType(), 2);
            }

            //从topicBarrage中删除
            TopicBarrage barrage = liveMybatisDao.getTopicBarrageByFId(fid);
            if (barrage!=null) {
                liveMybatisDao.deleteLiveBarrageById(barrage.getId());
                DeleteLog deleteLog = new DeleteLog();
                deleteLog.setDelTime(new Date());
                deleteLog.setType(Specification.DeleteObjectType.TOPIC_BARRAGE.index);
                deleteLog.setOid(barrage.getId());
                deleteLog.setUid(uid);

                liveMybatisDao.createDeleteLog(deleteLog);
            }
            log.info("delete topic fragment end ...");
            return Response.success(ResponseStatus.TOPIC_FRAGMENT_DELETE_SUCCESS.status, ResponseStatus.TOPIC_FRAGMENT_DELETE_SUCCESS.message);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.failure(ResponseStatus.TOPIC_FRAGMENT_DELETE_FAILURE.status, ResponseStatus.TOPIC_FRAGMENT_DELETE_FAILURE.message);
        }
    }

    /**
     * 获取王国显示协议
     *
     * @param vLv
     * @return
     */
    @Override
    public Response displayProtocol(int vLv) {
        log.info("get live display protocol start ...");
        LiveDisplayProtocol protocol = liveMybatisDao.getLiveDisplayProtocol(vLv);
        LiveDisplayProtocolDto liveDisplayProtocolDto = (LiveDisplayProtocolDto) CommonUtils.copyDto(protocol, new LiveDisplayProtocolDto());
        log.info("get live display protocol end ...");
        return Response.success(liveDisplayProtocolDto);
    }

    /**
     * 结束自己的直播
     *
     * @param topicId
     * @return
     */
    @Override
    public Response finishMyLive(long uid, long topicId) {
        log.info("finishMyLive start ...");
        Topic topic = liveMybatisDao.getTopic(uid, topicId);
        if (topic != null) {
            if (topic.getStatus() == Specification.LiveStatus.LIVING.index) {
                topic.setStatus(Specification.LiveStatus.OVER.index);
                liveMybatisDao.updateTopic(topic);
                log.info("finishMyLive end ...");
                return Response.success(ResponseStatus.USER_FINISH_LIVE_SUCCESS.status, ResponseStatus.USER_FINISH_LIVE_SUCCESS.message);
            } else {
                return Response.success(ResponseStatus.USER_LIVE_IS_OVER.status, ResponseStatus.USER_LIVE_IS_OVER.message);
            }
        } else {
            return Response.success(ResponseStatus.FINISH_LIVE_NO_POWER.status, ResponseStatus.FINISH_LIVE_NO_POWER.message);
        }
    }

    /**
     * 置顶/取消置顶
     *
     * @param topicId
     * @return
     */
    public Response top(long topicId) {
        return null;
    }

    /**
     * 删除直播
     *
     * @param topicId
     * @return
     */
    public Response delete(long topicId) {
        return null;
    }

    @Override
    public Response removeLive(long uid, long topicId) {
        log.info("removeLive start ...");
        //判断是否是自己的直播
        Topic topic = liveMybatisDao.getTopic(uid, topicId);
        if (topic == null) {
            return Response.failure(ResponseStatus.LIVE_REMOVE_IS_NOT_YOURS.status, ResponseStatus.LIVE_REMOVE_IS_NOT_YOURS.message);
        }
        //判断是否完结
        if (topic.getStatus() == Specification.LiveStatus.LIVING.index) {
            return Response.failure(ResponseStatus.LIVE_REMOVE_IS_NOT_OVER.status, ResponseStatus.LIVE_REMOVE_IS_NOT_OVER.message);
        }
        //移除
        topic.setStatus(Specification.LiveStatus.REMOVE.index);
        liveMybatisDao.updateTopic(topic);
        log.info("removeLive end ...");
        return Response.success(ResponseStatus.LIVE_REMOVE_SUCCESS.status, ResponseStatus.LIVE_REMOVE_SUCCESS.message);
    }

    @Override
    public Response signOutLive(long uid, long topicId) {
        log.info("signOutLive start ...");
        //判断是否是自己的直播
        Topic topic = liveMybatisDao.getTopic(uid, topicId);
        if (topic != null) {
            return Response.failure(ResponseStatus.LIVE_OWNER_CAN_NOT_SIGN_OUT.status, ResponseStatus.LIVE_OWNER_CAN_NOT_SIGN_OUT.message);
        } else {
            topic = liveMybatisDao.getTopicById(topicId);
            if (topic == null) {
                return Response.failure(ResponseStatus.LIVE_IS_NOT_EXIST.status, ResponseStatus.LIVE_IS_NOT_EXIST.message);
            }
        }
        //移除我的关注列表/退出
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        if (liveFavorite == null) {
            Response.failure(ResponseStatus.LIVE_IS_NOT_SIGN_IN.status, ResponseStatus.LIVE_IS_NOT_SIGN_IN.message);
        }
        liveMybatisDao.deleteLiveFavorite(liveFavorite);
        log.info("deleteLiveFavorite success");
        Content content = contentService.getContentByTopicId(topicId);
        if ((content.getFavoriteCount() - 1) < 0) {
            content.setFavoriteCount(0);
        } else {
            content.setFavoriteCount(content.getFavoriteCount() - 1);
        }
        contentService.updateContentById(content);
        log.info("signOutLive end ...");
        return Response.success(ResponseStatus.LIVE_SIGN_OUT_SUCCESS.status, ResponseStatus.LIVE_SIGN_OUT_SUCCESS.message);
    }


    @Override
    public int countFragment(long topicId, long uid) {
        return liveMybatisDao.countFragment(topicId, uid);
    }

    @Override
    public Response getFavoriteList(long topicId) {
        log.info("getFavoriteList start ...");
        ShowFavoriteListDto showFavoriteListFto = new ShowFavoriteListDto();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getFavoriteList(topicId);
        for (LiveFavorite liveFavorite : liveFavoriteList) {
            ShowFavoriteListDto.FavoriteUser favoriteUser = ShowFavoriteListDto.createElement();
            UserProfile userProfile = userService.getUserProfileByUid(liveFavorite.getUid());
            if(null == userProfile){
                continue;
            }
            favoriteUser.setV_lv(userProfile.getvLv());
            favoriteUser.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            favoriteUser.setUid(userProfile.getUid());
            favoriteUser.setNickName(userProfile.getNickName());
            favoriteUser.setLevel(userProfile.getLevel());
            showFavoriteListFto.getUserElements().add(favoriteUser);
        }
        log.info("getFavoriteList end ...");
        return Response.success(showFavoriteListFto);
    }

    @Override
    public Response myLivesAllByUpdateTime(long uid, long updateTime){
        log.info("myLivesAllByUpdateTime start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if (updateTime == 0) {
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getALLMyLivesByUpdateTime(uid, updateTime, topics);
        log.info("getMyLives data success");
        builderWithCache(uid, showTopicListDto, topicList);
        log.info("getMyLivesss start ...");

        //获取所有更新中直播主笔的信息
        List<Topic> list = liveMybatisDao.getLives(Long.MAX_VALUE);
        int num = 0;
        for (Topic topic : list) {
            if(num > 8){
                break;
            }
            num++;
            ShowTopicListDto.UpdateLives updateLives = ShowTopicListDto.createUpdateLivesElement();
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            updateLives.setV_lv(userProfile.getvLv());
            updateLives.setLevel(userProfile.getLevel());
            updateLives.setUid(userProfile.getUid());
            updateLives.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicListDto.getUpdateLives().add(updateLives);
        }
        showTopicListDto.setLiveCount(liveMybatisDao.countLives());
        MyLivesStatusModel myLivesStatusModel = new MyLivesStatusModel(uid, "0");
        String isUpdate = cacheService.hGet(myLivesStatusModel.getKey(), myLivesStatusModel.getField());
        if (!StringUtils.isEmpty(isUpdate)) {
            showTopicListDto.setIsUpdate(Integer.parseInt(isUpdate));
        } else {
            showTopicListDto.setIsUpdate(0);
        }
        log.info("myLivesAllByUpdateTime end ...");
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status, ResponseStatus.GET_MY_LIVE_SUCCESS.message, showTopicListDto);
    }

    /**
     * 获取我关注的直播，和我的直播列表
     *
     * @param uid
     * @return
     */
    @Override
    public Response MyLivesByUpdateTime(long uid, long updateTime) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if (updateTime == 0) {
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getMyLivesByUpdateTime(uid, updateTime, topics);
        log.info("getMyLives data success");
        builderWithCache(uid, showTopicListDto, topicList);
        log.info("getMyLives start ...");
        int inactiveLiveCount = liveMybatisDao.getInactiveLiveCount(uid, topics);
        showTopicListDto.setInactiveLiveCount(inactiveLiveCount);
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        List<Topic> live = liveMybatisDao.getInactiveLive(uid, topics, calendar.getTimeInMillis());
        if (live.size() > 0) {
            showTopicListDto.setLiveTitle(live.get(0).getTitle());
        }
        //获取所有更新中直播主笔的信息
        List<Topic> list = liveMybatisDao.getLives(Long.MAX_VALUE);
        for (Topic topic : list) {
            ShowTopicListDto.UpdateLives updateLives = ShowTopicListDto.createUpdateLivesElement();
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            updateLives.setV_lv(userProfile.getvLv());
            updateLives.setLevel(userProfile.getLevel());
            updateLives.setUid(userProfile.getUid());
            updateLives.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicListDto.getUpdateLives().add(updateLives);
        }
        showTopicListDto.setLiveCount(liveMybatisDao.countLives());
        MyLivesStatusModel myLivesStatusModel = new MyLivesStatusModel(uid, "0");
        String isUpdate = cacheService.hGet(myLivesStatusModel.getKey(), myLivesStatusModel.getField());
        if (!StringUtils.isEmpty(isUpdate)) {
            showTopicListDto.setIsUpdate(Integer.parseInt(isUpdate));
        } else {
            showTopicListDto.setIsUpdate(0);
        }
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status, ResponseStatus.GET_MY_LIVE_SUCCESS.message, showTopicListDto);
    }

    @Override
    public Response getMyTopic(long uid, long updateTime) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        //查询5个用户关注
        List<Content> attentionList = contentService.getAttention(Integer.MAX_VALUE ,uid, 1);
        if (attentionList.size() > 0 && attentionList != null) {
            int size = 0;
            for (Content content : attentionList) {
                size++;
                ShowTopicListDto.AttentionElement attentionElement = ShowTopicListDto.createAttentionElement();
                UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
                attentionElement.setAvatar(Constant.QINIU_DOMAIN+"/"+userProfile.getAvatar());
                attentionElement.setUid(content.getUid());
                attentionElement.setV_lv(userProfile.getvLv());
                attentionElement.setLevel(userProfile.getLevel());
                showTopicListDto.getAttentionData().add(attentionElement);
                if(size == 5){
                    break;
                }
            }
        }
        if (updateTime == 0) {
            updateTime = Long.MAX_VALUE;
        }
        List<Topic2> topicList = liveMybatisDao.getMyLivesByUpdateTimeNew(uid ,updateTime);
        log.info("getMyLives data success");
        builderWithCache2(uid, showTopicListDto, topicList);

        MyLivesStatusModel myLivesStatusModel = new MyLivesStatusModel(uid, "0");
        String isUpdate = cacheService.hGet(myLivesStatusModel.getKey(), myLivesStatusModel.getField());
        if (!StringUtils.isEmpty(isUpdate)) {
            showTopicListDto.setIsUpdate(Integer.parseInt(isUpdate));
        } else {
            showTopicListDto.setIsUpdate(0);
        }
     // 赠送王国
        List<TopicGiven> givenKingdomList = liveMybatisDao.getMyGivenKingdoms(uid);
        UserProfile myProfile= this.userService.getUserProfileByUid(uid);
        for(TopicGiven given:givenKingdomList){
        	GivenKingdom gk = new GivenKingdom();
        	gk.setGivenKingdomId(given.getId());
        	gk.setTitle(given.getTitle());
        	gk.setSummary(given.getSummary());
        	gk.setCoverImage(Constant.QINIU_DOMAIN + "/" +given.getCover());
        	gk.setCreateTime(given.getCreateTime());
        	gk.setUid(given.getUid());
        	gk.setTags(given.getTags());
        	gk.setAvatar(myProfile.getAvatar());
        	gk.setNickName(myProfile.getNickName());
        	gk.setV_lv(myProfile.getvLv());
        	gk.setLevel(myProfile.getLevel());
        	
        	showTopicListDto.getGivenKingdoms().add(gk);
        }
        
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status, ResponseStatus.GET_MY_LIVE_SUCCESS.message, showTopicListDto);
    }

    @Override
    public Response getInactiveLive(long uid, long updateTime) {
        log.info("getInactiveLive start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        if (updateTime == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -3);
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getInactiveLive(uid, topics, updateTime);
        log.info("getInactiveLive data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getInactiveLive end ...");
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status, ResponseStatus.GET_MY_LIVE_SUCCESS.message, showTopicListDto);
    }

    public Topic getTopicById(long topicId) {
        return liveMybatisDao.getTopicById(topicId);
    }

    public List<Topic> getTopicList(long uid) {
        return liveMybatisDao.getMyTopic(uid);
    }

    @Override
    public List<Topic> getMyTopic4Follow(long uid) {
        return liveMybatisDao.getMyTopic4Follow(uid);
    }

    @Override
    public void deleteFavoriteDelete(long uid, long topicId) {
        liveMybatisDao.deleteFavoriteDelete(uid, topicId);
    }

    @Override
    public TopicFragment getLastTopicFragmentByUid(long topicId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        criteria.andTypeEqualTo(Specification.LiveSpeakType.ANCHOR.index);
        example.setOrderByClause(" order by id desc limit 1");
        return liveMybatisDao.getLastTopicFragmentByUid(topicId, uid);
    }

    @Override
    public Live4H5Dto getLive4H5(long id) {
        List<TopicFragment> list = liveMybatisDao.getTopicFragment(id);
        Topic topic = liveMybatisDao.getTopicById(id);
        Live4H5Dto live4H5Dto = new Live4H5Dto();
        live4H5Dto.getLive().setTitle(topic.getTitle());
        live4H5Dto.getLive().setCreateTime(topic.getCreateTime());
        live4H5Dto.getLive().setCover(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        UserProfile user = userService.getUserProfileByUid(topic.getUid());
        live4H5Dto.getLive().setNickName(user.getNickName());
        live4H5Dto.getLive().setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
        for (TopicFragment topicFragment : list) {
            Live4H5Dto.Fragment fragment = Live4H5Dto.createFragment();
            fragment.setType(topicFragment.getType());
            fragment.setCreateTime(topicFragment.getCreateTime());
            fragment.setContentType(topicFragment.getContentType());
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            fragment.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            fragment.setNickName(userProfile.getNickName());
            fragment.setFragment(topicFragment.getFragment());
            fragment.setFragmentImage(topicFragment.getFragmentImage());
            live4H5Dto.getFragments().add(fragment);
        }
        return live4H5Dto;
    }

    @Override
    public Response getLiveTimeline2(GetLiveTimeLineDto2 getLiveTimeLineDto) {
        if (getLiveTimeLineDto.getFirst() == Specification.LiveFist.YES.index) {
            if (getLiveTimeLineDto.getMode() == Specification.LiveMode.COMMON.index) {

            }

        }
        LiveTimeLineDto2 liveTimeLineDto = new LiveTimeLineDto2();
        log.info("getLiveTimeline2 start ...");
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(getLiveTimeLineDto.getUid(), getLiveTimeLineDto.getTopicId() + "", "0");
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
        Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
        List<LiveDisplayFragment> fragmentList = liveMybatisDao.getDisPlayFragmentByMode(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId(), topic.getUid());
        log.info("get getLiveTimeline2 data");
       // buildTimeLine2(getLiveTimeLineDto, liveTimeLineDto, topic, fragmentList);
        log.info("buildLiveTimeLine2 success");
        List<TopicFragment> reviewList = liveMybatisDao.getTopicReviewByMode(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId(), topic.getUid());
        for (TopicFragment topicFragment : reviewList) {
            LiveTimeLineDto2.LastElement lastElement = LiveTimeLineDto2.createLastElement();
            UserProfile userProfile = userService.getUserProfileByUid(topicFragment.getUid());
            lastElement.setUid(userProfile.getUid());
            lastElement.setNickName(userProfile.getNickName());
            lastElement.setLevel(userProfile.getLevel());
            lastElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if (!StringUtils.isEmpty(fragmentImage)) {
                lastElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            lastElement.setCreateTime(topicFragment.getCreateTime());
            lastElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(), getLiveTimeLineDto.getUid());
            lastElement.setIsFollowed(isFollow);
            lastElement.setContentType(topicFragment.getContentType());
            lastElement.setFragmentId(topicFragment.getId());
            lastElement.setInternalStatus(userService.getUserInternalStatus(userProfile.getUid(), topic.getUid()));
            liveTimeLineDto.getLastElements().add(lastElement);
        }

        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status, ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message, liveTimeLineDto);
    }

    private void buildTimeLine2(GetLiveTimeLineDto2 getLiveTimeLineDto, LiveTimeLineDto2 liveTimeLineDto, Topic topic, List<TopicFragment> fragmentList) {
        long uid = topic.getUid();
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        for (TopicFragment topicFragment : fragmentList) {
            LiveTimeLineDto2.LiveElement liveElement = LiveTimeLineDto2.createElement();
            liveElement.setUid(uid);
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if (!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(), getLiveTimeLineDto.getUid());
            liveElement.setIsFollowed(isFollow);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveElement.setInternalStatus(userService.getUserInternalStatus(uid, topic.getUid()));
            liveElement.setReviewCount(0);
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
    }

    @Override
    public void createFavoriteDelete(long uid, long topicId) {
        liveMybatisDao.createFavoriteDelete(uid, topicId);
    }


    public Response cleanUpdate(long uid) {
        MyLivesStatusModel myLivesStatusModel = new MyLivesStatusModel(uid, "0");
        cacheService.hSet(myLivesStatusModel.getKey(), myLivesStatusModel.getField(), "0");
        return Response.success();
    }

    @Override
    public Response genQRcode(long TopicId) {
        LiveQRCodeDto liveQRCodeDto = new LiveQRCodeDto();
        try {
            Topic topic = getTopicById(TopicId);
            liveQRCodeDto.setSummary(topic.getSummary());
            if (StringUtils.isEmpty(topic.getQrcode())) {
                byte[] image = QRCodeUtil.encode(live_web + TopicId);
                String key = UUID.randomUUID().toString();
                fileTransferService.upload(image, key);
                liveQRCodeDto.setLiveQrCodeUrl(Constant.QINIU_DOMAIN + "/" + key);
                topic.setQrcode(key);
                liveMybatisDao.updateTopic(topic);
            } else {
                liveQRCodeDto.setLiveQrCodeUrl(Constant.QINIU_DOMAIN + "/" + topic.getQrcode());
            }
        } catch (Exception e) {
        	log.error("获取二维码失败",e);
            return Response.failure(ResponseStatus.QRCODE_FAILURE.status, ResponseStatus.QRCODE_FAILURE.message);
        }
        return Response.success(ResponseStatus.QRCODE_SUCCESS.status, ResponseStatus.QRCODE_SUCCESS.message, liveQRCodeDto);
    }

    @Override
    public Response getRedDot(long uid, long updateTime) {
        log.info("getRedDot start ...");
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if (updateTime == 0) {
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getMyLivesByUpdateTime2(uid, updateTime, topics);
        List reds = Lists.newArrayList();
        for (Topic topic : topicList) {
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId() + "", "0");
            String isUpdate = cacheService.hGet(cacheModel.getKey(), topic.getId() + "");
            reds.add(isUpdate);
        }
        if (reds.size()>0 && reds.contains("1")) {
            log.info("getRedDot end ...");
            return Response.success(ResponseStatus.GET_REDDOT_SUCCESS.status ,ResponseStatus.GET_REDDOT_SUCCESS.message);
        }else{
            log.info("getRedDot end ...");
            return Response.success(ResponseStatus.GET_REDDOT_FAILURE.status ,ResponseStatus.GET_REDDOT_FAILURE.message);
        }
    }

    @Override
    public Response editSpeak(SpeakDto speakDto) {
        log.info("edit speak start...");
        liveMybatisDao.updateTopFragmentById(speakDto);
        log.info("edit speak end...");
        return Response.success(ResponseStatus.EDIT_TOPIC_FRAGMENT_SUCCESS.status,ResponseStatus.EDIT_TOPIC_FRAGMENT_SUCCESS.message);
    }

    @Override
    public Response getLiveDetail(GetLiveDetailDto getLiveDetailDto) {
        log.info("get live detail start ... request:"+JSON.toJSONString(getLiveDetailDto));
        log.info("get total records...");
        Topic topic = liveMybatisDao.getTopicById(getLiveDetailDto.getTopicId());
        if(null == topic){
        	return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, ResponseStatus.LIVE_HAS_DELETED.message);
        }

        //消除红点
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(getLiveDetailDto.getUid(), getLiveDetailDto.getTopicId() + "", "0");
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());

        int totalRecords = liveMybatisDao.countFragmentByTopicId(getLiveDetailDto.getTopicId());

        LiveDetailDto liveDetailDto = new LiveDetailDto();
        liveDetailDto.setTotalRecords(totalRecords);
        int offset = getLiveDetailDto.getOffset();
        int totalPages =totalRecords%offset==0?totalRecords/offset:totalRecords/offset+1;
        liveDetailDto.setTotalPages(totalPages);
        log.info("get page records...");

        liveDetailDto.getPageInfo().setStart(getLiveDetailDto.getPageNo());

        int ss = 0;//预防机制。。防止程序出错死循环
        while(true){
        	ss++;
        	if(ss > 100){//预计不会查询超过100页数据的，预防死循环
        		break;
        	}
        	List<TopicFragment> list = liveMybatisDao.getTopicFragmentForPage(getLiveDetailDto);
        	if(null == list || list.size() == 0){//理论上就是到底了
        		if(getLiveDetailDto.getDirection() == Specification.LiveDetailDirection.DOWN.index){
	        		if(ss == 1){//第一次循环就没拉到数据，那么说明就没有数据了。。这里要补全上下页
	        			liveDetailDto.getPageInfo().setEnd(getLiveDetailDto.getPageNo());
	        			LiveDetailDto.PageDetail pd = new LiveDetailDto.PageDetail();
	        	        pd.setPage(getLiveDetailDto.getPageNo());
	        	        pd.setRecords(0);
	        	        pd.setIsFull(2);
	        	        liveDetailDto.getPageInfo().getDetail().add(pd);
	        		}
	        		break;
        		}
        	}
        	int flag = buildLiveDetail(getLiveDetailDto,liveDetailDto,list, topic);
        	if(liveDetailDto.getLiveElements().size() >= offset){
        		break;
        	}
        	if(flag == 1){
        		break;
        	}
        	//还没满，则继续查询上一页或下一页
        	if(getLiveDetailDto.getDirection() == Specification.LiveDetailDirection.DOWN.index){
        		getLiveDetailDto.setPageNo(getLiveDetailDto.getPageNo() + 1);
        	}else{
        		getLiveDetailDto.setPageNo(getLiveDetailDto.getPageNo() - 1);
        		if(getLiveDetailDto.getPageNo() < 1){
            		break;//向上拉到顶了
            	}
        	}
        }

        //将当前用户针对于本王国的相关消息置为已读
        userService.clearUserNoticeUnreadByCid(getLiveDetailDto.getUid(), Specification.UserNoticeUnreadContentType.KINGDOM.index, getLiveDetailDto.getTopicId());

        log.info("get live detail end ...");
        return  Response.success(ResponseStatus.GET_LIVE_DETAIL_SUCCESS.status, ResponseStatus.GET_LIVE_DETAIL_SUCCESS.message, liveDetailDto);
    }

    @Override
    public Response getLiveUpdate(GetLiveUpdateDto getLiveUpdateDto) {
        log.info("get live update start ... request:"+JSON.toJSONString(getLiveUpdateDto));
        log.info("get total records...");
        int totalRecords;
        int updateRecords;
        long lastFragmentId = 0;

        if(getLiveUpdateDto.getSinceId()>0){
        	//newestId,totalCount
        	String value = cacheService.hGet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + getLiveUpdateDto.getTopicId());
        	long newestFragmentId = 0;
        	int cacheTotalCount = 0;
        	if(null != value && !"".equals(value)){
        		String[] tmp = value.split(",");
        		if(tmp.length == 2){
        			newestFragmentId = Long.valueOf(tmp[0]);
        			cacheTotalCount = Integer.valueOf(tmp[1]);
        		}
        	}
        	if(newestFragmentId == 0 || newestFragmentId > getLiveUpdateDto.getSinceId()){//没有缓存，或缓存里的数据比传递过来的新，则重新拉取
        		Map<String,Long> result  = liveMybatisDao.countFragmentByTopicIdWithSince(getLiveUpdateDto);
                totalRecords = result.get("total_records").intValue();
                updateRecords = result.get("update_records").intValue();
                lastFragmentId = result.get("lastFragmentId").longValue();
        	}else{
        		totalRecords = cacheTotalCount;
        		updateRecords = 0;//没有更新，则更新数为0
        		lastFragmentId = newestFragmentId;
        	}
        }else {
        	Map<String,Long> result  = liveMybatisDao.countFragmentByTopicIdWithSince(getLiveUpdateDto);
            totalRecords = result.get("total_records").intValue();
            updateRecords = result.get("update_records").intValue();
            lastFragmentId = result.get("lastFragmentId").longValue();
        }
        LiveUpdateDto liveUpdateDto = new LiveUpdateDto();
        liveUpdateDto.setLastFragmentId(lastFragmentId);
        liveUpdateDto.setTotalRecords(totalRecords);
        int offset = getLiveUpdateDto.getOffset();
        int totalPages =totalRecords%offset==0?totalRecords/offset:totalRecords/offset+1;
        liveUpdateDto.setTotalPages(totalPages);
        liveUpdateDto.setUpdateRecords(updateRecords);

        int nums = totalRecords-updateRecords;
        int startPageNo = nums/offset+1;
        liveUpdateDto.setStartPageNo(startPageNo);

        log.info("get live update start ...");
        return Response.success(ResponseStatus.GET_LIVE_UPDATE_SUCCESS.status, ResponseStatus.GET_LIVE_UPDATE_SUCCESS.message, liveUpdateDto);
    }

    //返回 1：到最后了  2：没到最后
    private int buildLiveDetail(GetLiveDetailDto getLiveDetailDto, LiveDetailDto liveDetailDto, List<TopicFragment> fragmentList, Topic topic) {
        log.info("build live detail start ...");
        liveDetailDto.setPageNo(getLiveDetailDto.getPageNo());
        liveDetailDto.getPageInfo().setEnd(getLiveDetailDto.getPageNo());

        List<Long> uidList = new ArrayList<Long>();
        for (TopicFragment topicFragment : fragmentList) {
    		if(!uidList.contains(topicFragment.getUid())){
    			uidList.add(topicFragment.getUid());
    		}
    		if (null != topicFragment.getAtUid() && topicFragment.getAtUid() != 0) {
            	if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
            		if(!uidList.contains(topicFragment.getAtUid())){
            			uidList.add(topicFragment.getAtUid());
            		}
            	}
    		}
    	}

    	Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
    	List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
    	if(null != userList && userList.size() > 0){
    		for(UserProfile u : userList){
    			userMap.put(u.getUid().toString(), u);
    		}
    	}

    	//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(getLiveDetailDto.getUid(), uidList);
        if(null != userFollowList && userFollowList.size() > 0){
        	for(UserFollow uf : userFollowList){
        		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
        	}
        }


        int count = 0;
        UserProfile userProfile = null;
        UserProfile atUser = null;
        for (TopicFragment topicFragment : fragmentList) {
            long uid = topicFragment.getUid();

            LiveDetailDto.LiveElement liveElement = LiveDetailDto.createElement();
            int status = topicFragment.getStatus();
            liveElement.setStatus(status);
            liveElement.setId(topicFragment.getId());
            if(status==0){
            	//删除的不要了
                //liveDetailDto.getLiveElements().add(liveElement);
                continue;
            }

            //系统灰条过滤处理（预防低版本）
            if(getLiveDetailDto.getVersionFlag() < 1){//低于V2.2.2版本
            	//系统灰条和足迹不展示
            	if(topicFragment.getType() == 51&&topicFragment.getContentType()==16){//足迹
            		liveElement.setStatus(0);
            		continue;
            	}else if(topicFragment.getType() == 1000){//系统灰条
            		liveElement.setStatus(0);
            		continue;
            	}
            }
            //表情包过滤处理（预防低版本）
            if(getLiveDetailDto.getVersionFlag() < 2){//低于V2.2.4版本
            	if(topicFragment.getType() == 51 || topicFragment.getType() == 52){
            		if(topicFragment.getContentType() == 17 || topicFragment.getContentType() == 18){//表情包
            			liveElement.setStatus(0);
                		continue;
            		}
            	}
            }
            //逗一逗和投票（预防低版本）
            if(getLiveDetailDto.getVersionFlag() < 3){//低于V2.2.5版本
            	if(topicFragment.getType() == 51 || topicFragment.getType() == 52){
            		if(topicFragment.getContentType() == 20){//逗一逗
            			liveElement.setStatus(0);
                		continue;
            		}
            	}
            	if(topicFragment.getType() == 52){
            		if(topicFragment.getContentType() == 19){//投票
            			liveElement.setStatus(0);
                		continue;
            		}
            	}
            }
            //逗一逗自动播放状态
            int teaseStatus = 0;
            if((topicFragment.getType() == 51 || topicFragment.getType() == 52) && topicFragment.getContentType() == 20){
               JSONObject extraJson  = 	 JSONObject.parseObject(topicFragment.getExtra());
               if((getLiveDetailDto.getUid()+"").equals(extraJson.getString("uid"))){
            	TeaseAutoPlayStatusModel teaseAutoPlayStatusModel =  new TeaseAutoPlayStatusModel(topicFragment.getId(), "0");
            	String isTeaseStatus = cacheService.hGet(teaseAutoPlayStatusModel.getKey(), teaseAutoPlayStatusModel.getField());
            	  if (!StringUtils.isEmpty(isTeaseStatus)) {
            		  teaseStatus=0;
                  } else {
                	  teaseStatus=1;
                	  cacheService.hSet(teaseAutoPlayStatusModel.getKey(), teaseAutoPlayStatusModel.getField(), teaseAutoPlayStatusModel.getValue());
                  }
               }
            }
            liveElement.setTeaseStatus(teaseStatus);

            userProfile = userMap.get(String.valueOf(uid));
            liveElement.setUid(uid);
            if(null != userProfile){
            	liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                liveElement.setNickName(userProfile.getNickName());
                liveElement.setV_lv(userProfile.getvLv());
                liveElement.setLevel(userProfile.getLevel());
            }
            liveElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if (!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            if(null != followMap.get(getLiveDetailDto.getUid()+"_"+topicFragment.getUid())){
            	liveElement.setIsFollowed(1);
            }else{
            	liveElement.setIsFollowed(0);
            }
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveElement.setSource(topicFragment.getSource());
            liveElement.setExtra(topicFragment.getExtra());

            liveElement.setInternalStatus(getInternalStatus(topic, uid));
            if (null != topicFragment.getAtUid() && topicFragment.getAtUid() > 0){
            	if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
	                atUser = userMap.get(topicFragment.getAtUid().toString());
	                if(null != atUser){
	                	liveElement.setAtUid(atUser.getUid());
		                liveElement.setAtNickName(atUser.getNickName());
	                }
            	}
            }
            if(getLiveDetailDto.getDirection() == Specification.LiveDetailDirection.DOWN.index){
            	liveDetailDto.getLiveElements().add(liveElement);
            }else{
            	liveDetailDto.getLiveElements().add(count, liveElement);
            }
            liveElement.setScore(topicFragment.getScore());
            count++;
        }
        LiveDetailDto.PageDetail pd = new LiveDetailDto.PageDetail();
        pd.setPage(getLiveDetailDto.getPageNo());
        pd.setRecords(count);
        pd.setIsFull(fragmentList.size() >= getLiveDetailDto.getOffset()?1:2);
        liveDetailDto.getPageInfo().getDetail().add(pd);
        log.info("build live detail end ...");

        //判断是否到底或到顶
        int result = 2;
        if(getLiveDetailDto.getDirection() == Specification.LiveDetailDirection.DOWN.index){//向下拉，那么返回的数据不满50条说明到底了
        	if(fragmentList.size() < getLiveDetailDto.getOffset()){
        		result = 1;
        	}
        }else{//向上拉，那么page到第一页时说明就到顶了
        	if(getLiveDetailDto.getPageNo() <= 1){
        		result = 1;
        	}
        }

        return result;
    }

    @Override
    public Response testApi(TestApiDto request) {
        TopicFragment fragment = new TopicFragment();
        try {
            BeanUtils.copyProperties(fragment,request);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        liveMybatisDao.createTopicFragment(fragment);

        Topic topic = liveMybatisDao.getTopicById(fragment.getTopicId());
        Calendar calendar = Calendar.getInstance();
        topic.setUpdateTime(calendar.getTime());
        topic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.updateTopic(topic);

        //--add update kingdom cache -- modify by zcl -- begin --
        //此处暂不考虑原子操作
        int total = liveMybatisDao.countFragmentByTopicId(fragment.getTopicId());
        String value = fragment.getId() + "," + total;
        cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + fragment.getTopicId(), value);
        //--add update kingdom cache -- modify by zcl -- end --

        return Response.success();
    }

	@Override
	public Response<SpeakDto> createKingdom(CreateKingdomDto createKingdomDto) {
		log.info("createKingdom start...");
		if(StringUtils.isEmpty(createKingdomDto.getLiveImage()) || StringUtils.isEmpty(createKingdomDto.getTitle())){
        	log.info("liveImage or title is empty");
        	return Response.failure(ResponseStatus.KINGDOM_CREATE_FAILURE.status, ResponseStatus.KINGDOM_CREATE_FAILURE.message);
        }

		boolean isDouble = false;
		int type = 0;
		long uid2 = 0;
		String cExtraJson = createKingdomDto.getCExtra();
		JSONObject cExtraObj = null;
		//判断特殊王国条件
		if(createKingdomDto.getKType() != Specification.KingdomType.NORMAL.index
				&& createKingdomDto.getKType() != Specification.KingdomType.AGGREGATION.index){
			log.info("special kingdom check start...");
			//目前特殊王国就7天活动，故目前只要判断7天活动王国规则即可
			if(StringUtils.isEmpty(cExtraJson)){
				log.info("cExtra is null");
				return Response.failure(ResponseStatus.KINGDOM_CREATE_FAILURE.status, ResponseStatus.KINGDOM_CREATE_FAILURE.message);
			}
			cExtraObj = JSON.parseObject(cExtraJson);
			type = cExtraObj.getIntValue("type");
			if(null != cExtraObj.get("uid2")){
				uid2 = cExtraObj.getLongValue("uid2");
			}

			Response resp = null;
			if(type == Specification.ActivityKingdomType.SPRINGKING.index){
				//春节王国
				resp = activityService.checkUserActivityKindom4Spring(createKingdomDto.getUid());
			}else{
				resp = activityService.checkUserActivityKindom(createKingdomDto.getUid(), type, uid2);
			}

			if(null == resp){
				return Response.failure(ResponseStatus.KINGDOM_CREATE_FAILURE.status, ResponseStatus.KINGDOM_CREATE_FAILURE.message);
			}else if(resp.getCode() == 500){
				return Response.failure(ResponseStatus.KINGDOM_CREATE_FAILURE.status, (String)resp.getData());
			}
			if(type == Specification.ActivityKingdomType.DOUBLEKING.index){
				isDouble = true;
			}
			log.info("special kingdom check end...");
		}

		Date now = new Date();
		log.info("create cover..");
		Topic topic = new Topic();
		topic.setTitle(createKingdomDto.getTitle());
        topic.setLiveImage(createKingdomDto.getLiveImage());
        topic.setUid(createKingdomDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
        topic.setLongTime(now.getTime());
        topic.setCreateTime(now);
        topic.setUpdateTime(now);
        JSONArray array = new JSONArray();
        array.add(createKingdomDto.getUid());
        if(isDouble){
        	array.add(uid2);
        }
        topic.setCoreCircle(array.toString());
        //聚合版本新加属性
        int kingdomType = Specification.KingdomType.NORMAL.index;
        if(createKingdomDto.getKType() == Specification.KingdomType.AGGREGATION.index){
        	kingdomType = Specification.KingdomType.AGGREGATION.index;
        	// 判断聚合王国是否上限
            PermissionDescriptionDto permissionDescriptionDto= userService.getUserPermission(createKingdomDto.getUid());
            List<PermissionNodeDto> perList = permissionDescriptionDto.getNodes();
            int limitCount = 0;
            for(PermissionNodeDto p : perList){
                if (p.getCode() == 7){
                    if (p.getNum() == null){
                        limitCount = 0;
                        break;
                    }else {
                    limitCount = p.getNum();
                    break;
                    }
                }
            }
            List<Map<String,Object>> list = liveLocalJdbcDao.getConvergeTopic(createKingdomDto.getUid());
            int hasCount = list.size();

            if(hasCount >= limitCount){
                return Response.failure(500,"你当前的等级已经达到了创建聚合王国的上限。");
            }
        }
        topic.setType(kingdomType);
        topic.setRights(Specification.KingdomRights.PUBLIC_KINGDOM.index);//目前默认公开的，等以后有需求的再说
        topic.setSummary(createKingdomDto.getFragment());//目前，第一次发言即王国简介
        topic.setCeAuditType(0);//聚合王国属性，是否需要国王审核才能加入此聚合王国，默认0是
        topic.setAcAuditType(1);//个人王国属性，是否需要国王审核才能收录此王国，默认1否
        topic.setAcPublishType(0);//个人王国属性，是否接受聚合王国下发的消息，默认0是
        topic.setSubType(createKingdomDto.getSubType());
        //初始化王国价值，默认估值:米汤币为15,随机增减0-8
        int price = 15;
        Random random = new Random();
        int incr = random.nextInt(9);
        int flag = random.nextInt(2);
        if(flag == 0){
        	price = price - incr;
        }else{
        	price = price + incr;
        }
        
        //查询新建王国可以被偷的配置值
        int newStealPrice = 0;
        String newKingdomStealPrice = userService.getAppConfigByKey("NEW_KINGDOM_STEAL_PRICE");
        if(!StringUtils.isEmpty(newKingdomStealPrice)){
        	newStealPrice = Integer.valueOf(newKingdomStealPrice).intValue();
        }
        
        topic.setPrice(price + newStealPrice);
        liveMybatisDao.createTopic(topic);

        //创建直播之后添加到我的UGC
        ContentDto contentDto = new ContentDto();
        contentDto.setContent(createKingdomDto.getTitle());
        contentDto.setFeeling(createKingdomDto.getTitle());
        contentDto.setTitle(createKingdomDto.getTitle());
        contentDto.setImageUrls(createKingdomDto.getLiveImage());
        contentDto.setUid(createKingdomDto.getUid());
        contentDto.setType(Specification.ArticleType.LIVE.index);
        contentDto.setForwardCid(topic.getId());
        contentDto.setRights(Specification.ContentRights.EVERY.index);
        contentService.publish(contentDto);
        
        applicationEventBus.post(new CacheLiveEvent(createKingdomDto.getUid(), topic.getId()));

        //创建王国之后创建相应的价值数据表（主要是可以创建后即可有一定的被偷值）
        TopicData td = new TopicData();
        td.setApprove(0d);
        td.setDiligently(0d);
        td.setLastPrice(0);
        td.setLastPriceIncr(0);
        td.setReviewTextCount(0);
        td.setReviewTextLength(0);
        td.setStealPrice(newStealPrice);
        td.setTopicId(topic.getId());
        td.setUpdateAudioCount(0);
        td.setUpdateAudioLength(0);
        td.setUpdateDayCount(0);
        td.setUpdateImageCount(0);
        td.setUpdateTeaseCount(0);
        td.setUpdateTextCount(0);
        td.setUpdateTextLength(0);
        td.setUpdateTime(now);
        td.setUpdateVedioCount(0);
        td.setUpdateVedioLength(0);
        td.setUpdateVoteCount(0);
        liveMybatisDao.saveTopicData(td);
        
        
        SpeakDto speakDto2 = new SpeakDto();
        speakDto2.setTopicId(topic.getId());
        UserProfile profile = userService.getUserProfileByUid(createKingdomDto.getUid());
        speakDto2.setV_lv(profile.getvLv());
        //检查有没有出错的数据，如果有则删除出错数据
        contentService.clearData();

        log.info("first speak...");
        long lastFragmentId = 0;
        long total = 0;
        if(createKingdomDto.getContentType() == 0){
        	TopicFragment topicFragment = new TopicFragment();
        	topicFragment.setFragment(createKingdomDto.getFragment());
        	topicFragment.setUid(createKingdomDto.getUid());
        	topicFragment.setType(0);//第一次发言肯定是主播发言
        	topicFragment.setContentType(0);
        	topicFragment.setTopicId(topic.getId());
            topicFragment.setBottomId(0l);
            topicFragment.setTopId(0l);
            topicFragment.setSource(createKingdomDto.getSource());
            topicFragment.setExtra(createKingdomDto.getExtra());
            topicFragment.setCreateTime(now);
            liveMybatisDao.createTopicFragment(topicFragment);
            lastFragmentId = topicFragment.getId();
            total++;
        }else{//图片
        	String[] imgs = createKingdomDto.getFragment().split(";");
        	Map<String, String> map = new HashMap<String, String>();
        	String extra = createKingdomDto.getExtra();
        	if(!StringUtils.isEmpty(extra)){
        		JSONArray obj = JSON.parseArray(extra);
        		if(!obj.isEmpty()){
        			for(int i=0;i<obj.size();i++){
        				map.put(String.valueOf(i), obj.getJSONObject(i).toJSONString());
        			}
        		}
        	}

        	if(null != imgs && imgs.length > 0){
        		TopicFragment topicFragment = null;
        		String e = null;
        		for(int i=0;i<imgs.length;i++){
        			topicFragment = new TopicFragment();
                	topicFragment.setFragmentImage(imgs[i]);
                	topicFragment.setUid(createKingdomDto.getUid());
                	topicFragment.setType(0);//第一次发言肯定是主播发言
                	topicFragment.setContentType(1);
                	topicFragment.setTopicId(topic.getId());
                    topicFragment.setBottomId(0l);
                    topicFragment.setTopId(0l);
                    topicFragment.setSource(createKingdomDto.getSource());
                    topicFragment.setCreateTime(now);
                    e = map.get(String.valueOf(i));
                    if(null == e){
                    	e = "";
                    }
                    topicFragment.setExtra(e);
                    liveMybatisDao.createTopicFragment(topicFragment);
                    lastFragmentId = topicFragment.getId();
                    total++;
        		}
        	}
        }

        //特殊王国需要做一点特殊处理
        if(createKingdomDto.getKType() != Specification.KingdomType.NORMAL.index
        		&& createKingdomDto.getKType() != Specification.KingdomType.AGGREGATION.index){
        	if(type == Specification.ActivityKingdomType.SPRINGKING.index){
        		activityService.createActivityKingdom4Spring(topic.getId(), createKingdomDto.getUid());
        	}else{
        		activityService.createActivityKingdom(topic.getId(), createKingdomDto.getUid(), type, uid2);
        	}
        }

        //--add update kingdom cache -- modify by zcl -- begin --
		String value = lastFragmentId + "," + total;
        cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + topic.getId(), value);
        //--add update kingdom cache -- modify by zcl -- end --
        // 找到机器TAG
        
        Set<String> autoTagSet = new HashSet<>();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(createKingdomDto.getAutoTags())){
        	for(String t:createKingdomDto.getAutoTags().split(";")){
        		autoTagSet.add(t.trim());
        	}
        }
        //add kingdom tags -- begin --
        if(!StringUtils.isEmpty(createKingdomDto.getTags())){
        	String[] tags = createKingdomDto.getTags().split(";");
        	if(null != tags && tags.length > 0){
        		TopicTag topicTag = null;
        		TopicTagDetail tagDetail = null;
        		for(String tag : tags){
        			tag = tag.trim();
        			if(!StringUtils.isEmpty(tag)){
        				topicTag = liveMybatisDao.getTopicTagByTag(tag);
        				if(null == topicTag){
        					topicTag = new TopicTag();
        					topicTag.setTag(tag);
        					liveMybatisDao.insertTopicTag(topicTag);
        				}
        			}
        			tagDetail = new TopicTagDetail();
        			tagDetail.setTag(tag);
        			tagDetail.setTagId(topicTag.getId());
        			tagDetail.setTopicId(topic.getId());
        			tagDetail.setUid(createKingdomDto.getUid());
        			if(autoTagSet.contains(tag)){
        				tagDetail.setAutoTag(1);
        			}else{
        				tagDetail.setAutoTag(0);
        			}
        			liveMybatisDao.insertTopicTagDetail(tagDetail);
        		}
        	}
        }
        //add kingdom tags -- end --

        // 机器人自动回复开始 -- start --
        Set<String> set = cacheService.keys(KeysManager.SEVEN_DAY_REGISTER_PREFIX+"*");
        Iterator<String> it = set.iterator();
        Set<String> newRegisterFor7Days = Sets.newConcurrentHashSet();
        while (it.hasNext()){
            String key = it.next();
            newRegisterFor7Days.add(key.split(":")[1]);
        }

        if(newRegisterFor7Days.contains(topic.getUid()+"")) {
            applicationEventBus.post(new AutoReplyEvent(topic.getUid(), topic.getId(), topic.getCreateTime()));
        }
        // 机器人自动回复结束 -- end --
        log.info("createKingdom end");
        CoinRule coinRule = userService.getCoinRules().get(Rules.CREATE_KING_KEY);
        coinRule.setExt(createKingdomDto.getUid());
        ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(createKingdomDto.getUid(), userService.getCoinRules().get(Rules.CREATE_KING_KEY));
        speakDto2.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
        speakDto2.setUpgrade(modifyUserCoinDto.getUpgrade());
        Response response = Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status, ResponseStatus.USER_CREATE_LIVE_SUCCESS.message, speakDto2);
        return response;

	}

	@Override
	public Response kingdomSearch(long currentUid, KingdomSearchDTO searchDTO){
		List<Map<String,Object>> topicList = new ArrayList<Map<String,Object>>();
		boolean first = false;
		if(searchDTO.getUpdateTime() <= 0){//第一次
			searchDTO.setUpdateTime(Long.MAX_VALUE);
			first = true;
		}
		Map<String, String> topMap = new HashMap<String, String>();//母查子第一次需要
		Map<String, String> publishMap = new HashMap<String, String>();//子查母需要
		if(searchDTO.getSearchScene() == 0){
			//母查子的第一次需要先将置顶的全部查询出来
			if(searchDTO.getTopicId() > 0 && searchDTO.getTopicType() == 2){//母查子
				if(first){//第一次
					List<Map<String,Object>> topList = liveLocalJdbcDao.searchTopics(searchDTO, 1);
					if(null != topList && topList.size() > 0){
						for(Map<String,Object> t : topList){
							topMap.put(String.valueOf(t.get("id")), "1");
						}
						topicList.addAll(topList);
					}
					List<Map<String,Object>> noList = liveLocalJdbcDao.searchTopics(searchDTO, 0);
					if(null != noList && noList.size() > 0){
						topicList.addAll(noList);
					}
				}else{
					topicList = liveLocalJdbcDao.searchTopics(searchDTO, 0);
				}
			}else{
				topicList = liveLocalJdbcDao.searchTopics(searchDTO, -1);
			}
			if(searchDTO.getTopicId() > 0 && searchDTO.getTopicType()==1){//子查母需要知道子对于母是否开启了内容下发
				List<TopicAggregation> list = liveMybatisDao.getTopicAggregationsBySubTopicId(searchDTO.getTopicId());
				if(null != list && list.size() > 0){
					for(TopicAggregation ta : list){
						if(ta.getIsPublish() == 0){
							publishMap.put(String.valueOf(ta.getTopicId()), "1");
						}
					}
				}
			}
		}else{
			if(searchDTO.getSearchScene() == 7){
				topicList = liveLocalJdbcDao.getRecTopicByTag(searchDTO.getTopicId(), searchDTO.getUpdateTime(), 20, 0);//查询推荐的个人王国
			}else{
				topicList = liveLocalJdbcDao.getKingdomListBySearchScene(currentUid, searchDTO);
			}
		}

		ShowTopicSearchDTO showTopicSearchDTO = new ShowTopicSearchDTO();
		//先将场景需要的一些信息不全，再处理检索列表
    	if(searchDTO.getSearchScene() > 0){
    		if(searchDTO.getSearchScene() == 2 && searchDTO.getTopicId() > 0){//聚合王国被动场景
    			//需要被检索王国的子王国数
    			int acCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId(searchDTO.getTopicId());
    			showTopicSearchDTO.setAcCount(acCount);
    		}else if(searchDTO.getSearchScene() == 4 && searchDTO.getTopicId() > 0){//个人王国被动场景
    			//需要被检索王国的母王国数
    			int ceCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId2(searchDTO.getTopicId());
    			showTopicSearchDTO.setCeCount(ceCount);
    		}else if(searchDTO.getSearchScene() == 7 && searchDTO.getTopicId() > 0){//聚合王国新主动场景
    			//需要被检索王国的子王国数
    			int acCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId(searchDTO.getTopicId());
    			showTopicSearchDTO.setAcCount(acCount);
    		}
    	}
		if(null != topicList && topicList.size() > 0){
			this.builderTopicSearch(currentUid, showTopicSearchDTO, topicList, topMap, publishMap);
		}

		if(searchDTO.getVersionFlag() < 2){
			if(showTopicSearchDTO.getResultList().size() > 0){
				for(ShowTopicSearchDTO.TopicElement e : showTopicSearchDTO.getResultList()){
					e.setTags(null);
				}
			}
		}

		return Response.success(showTopicSearchDTO);
	}


	@Override
    public Response settings(long uid, long topicId, int vflag) {
        SettingsDto dto = new SettingsDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(topic != null) {
            dto.setTopicId(topicId);
            dto.setCoverImage(Constant.QINIU_DOMAIN+"/"+topic.getLiveImage());
            dto.setTitle(topic.getTitle());
            Content content = contentService.getContentByTopicId(topic.getId());
            if(content != null) {
                dto.setReadCount(content.getReadCountDummy());
            }
            Long favoriteCount = Long.valueOf(0);
            List<Long> topicIdList = new ArrayList<Long>();
            topicIdList.add(Long.valueOf(topicId));
            Map<String, Long> memberMap = liveLocalJdbcDao.getTopicMembersCount(topicIdList);
            if(null != memberMap && memberMap.size() > 0){
            	if(null != memberMap.get(String.valueOf(topicId))){
            		favoriteCount = memberMap.get(String.valueOf(topicId));
            	}
            }
            if(favoriteCount.longValue() > 0){
            	dto.setFavoriteCount(favoriteCount.intValue() + 1);
            }else{
            	dto.setFavoriteCount(1);
            }
            TopicCountDTO topicCountDTO = activityService.getTopicCount(topicId);
            dto.setTopicCount(topicCountDTO.getUpdateCount());
            dto.setCreateTime(topic.getCreateTime().getTime());
            dto.setSummary(topic.getSummary());
            if(topic.getType() == 1000){
                //查子王国
                int acCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId(topicId);
                dto.setAcCount(acCount);
            }else {
                //查母王国
                int ceCount = liveLocalJdbcDao.getTopicAggregationCountByTopicId2(topicId);
                dto.setCeCount(ceCount);
            }
            TopicUserConfig topicUserConfig = liveMybatisDao.getTopicUserConfig(uid ,topicId);
            if(topicUserConfig != null){
                dto.setPushType(topicUserConfig.getPushType());
            }
            dto.setAcPublishType(topic.getAcPublishType());
            dto.setCeAuditType(topic.getCeAuditType());
            dto.setAcAuditType(topic.getAcAuditType());
            //标签
            if(vflag > 0){
	            String tags = "";
	            List<TopicTagDetail> topicTagDetails = liveMybatisDao.getTopicTagDetailsByTopicId(topicId);
	            if(topicTagDetails != null && topicTagDetails.size() > 0){
	                StringBuilder builder = new StringBuilder();
	                for (TopicTagDetail detail : topicTagDetails){
	                    String tag = detail.getTag();
	                    if(tags.equals("")){
	                        tags = builder.append(tag).toString();
	                    }else {
	                        builder.append(";"+tag);
	                    }
	                }
	                dto.setTags(builder.toString());
	            }
            }
            log.info("get settings success");
        }
        return Response.success(dto);
    }

	@Override
	public Response settingModify(SettingModifyDto dto) {
        //每个人都能操作
        if (dto.getAction() == Specification.SettingModify.PUSH.index) {
            int pushType = Integer.valueOf(dto.getParams()).intValue();
            TopicUserConfig topicUserConfig = liveMybatisDao.getTopicUserConfig(dto.getUid(), dto.getTopicId());
            if (topicUserConfig != null) {
                topicUserConfig.setPushType(pushType);
                liveMybatisDao.updateTopicUserConfig(topicUserConfig);
                log.info("update pushType success");
            } else {
                topicUserConfig = new TopicUserConfig();
                topicUserConfig.setUid(dto.getUid());
                topicUserConfig.setTopicId(dto.getTopicId());
                topicUserConfig.setPushType(pushType);
                liveMybatisDao.insertTopicUserConfig(topicUserConfig);
                log.info("update pushType success");
            }
            return Response.success();
        }
		Topic topic = liveMybatisDao.getTopicById(dto.getTopicId());
		if (null != topic && topic.getUid() == dto.getUid()) {
			// 国王操作
			if (dto.getAction() == Specification.SettingModify.COVER.index) {
				topic.setLiveImage(dto.getParams());
				liveMybatisDao.updateTopic(topic);
				liveLocalJdbcDao.updateTopicContentCover(topic.getId(), dto.getParams());
				log.info("update cover success");
                LiveParamsDto paramsDto = new LiveParamsDto();
                paramsDto.setCoverImage(Constant.QINIU_DOMAIN+"/"+dto.getParams());
				return Response.success(paramsDto);
			} else if (dto.getAction() == Specification.SettingModify.SUMMARY.index) {
				topic.setSummary(dto.getParams());
				liveMybatisDao.updateTopic(topic);
				log.info("update Summary success");

				// 更新成功需要在当前王国中插入一条国王发言
				if (!StringUtils.isEmpty(dto.getParams())) {
					TopicFragment topicFragment = new TopicFragment();
					topicFragment.setFragment("王国简介修改:" + dto.getParams());
					topicFragment.setUid(dto.getUid());
					topicFragment.setType(0);// 第一次发言肯定是主播发言
					topicFragment.setContentType(0);// 文本
					topicFragment.setTopicId(topic.getId());
					topicFragment.setBottomId(0l);
					topicFragment.setTopId(0l);
					topicFragment.setSource(0);
					// topicFragment.setExtra();
					topicFragment.setCreateTime(new Date());
					liveMybatisDao.createTopicFragment(topicFragment);
					long lastFragmentId = topicFragment.getId();

					//王国修改简介，肯定是国王操作，这里需要更新更新时间
					Calendar calendar = Calendar.getInstance();
					topic.setUpdateTime(calendar.getTime());
					topic.setLongTime(calendar.getTimeInMillis());
					liveMybatisDao.updateTopic(topic);
					
					liveLocalJdbcDao.updateContentUpdateTime4Kingdom(topic.getId(), calendar.getTime());
					liveLocalJdbcDao.updateContentUpdateId4Kingdom(topic.getId(),cacheService.incr("UPDATE_ID"));

					// 更新缓存
					int total = liveMybatisDao.countFragmentByTopicId(topic.getId());
					String value = lastFragmentId + "," + total;
					cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + topic.getId(), value);

					SpeakNewEvent speakNewEvent = new SpeakNewEvent();
                    speakNewEvent.setTopicId(topicFragment.getTopicId());
                	speakNewEvent.setType(topicFragment.getType());
                	speakNewEvent.setContentType(topicFragment.getContentType());
                	speakNewEvent.setUid(topicFragment.getUid());
                	speakNewEvent.setFragmentId(lastFragmentId);
                	speakNewEvent.setFragmentContent(topicFragment.getFragment());
                	speakNewEvent.setFragmentExtra(topicFragment.getExtra());
                    applicationEventBus.post(speakNewEvent);
				}

				return Response.success();
			} else if (dto.getAction() == Specification.SettingModify.TAGS.index) {
				log.info("暂时不考虑标签");
			}  else if (dto.getAction() == Specification.SettingModify.AGVERIFY.index) {
				topic.setCeAuditType(Integer.valueOf(dto.getParams()));
				liveMybatisDao.updateTopic(topic);
				log.info("update CeAuditType success");
				return Response.success();
			} else if (dto.getAction() == Specification.SettingModify.VERIFY.index) {
				topic.setAcAuditType(Integer.valueOf(dto.getParams()));
				liveMybatisDao.updateTopic(topic);
				log.info("update AcAuditType success");
				return Response.success();
			} else if (dto.getAction() == Specification.SettingModify.ISSUED_MESSAGE.index) {
				// 下发消息
				topic.setAcPublishType(Integer.valueOf(dto.getParams()));
				liveMybatisDao.updateTopic(topic);
				log.info("update AcPublishType success");
				return Response.success();
			}else if(dto.getAction() == Specification.SettingModify.LIVE_NAME.index){
                topic.setTitle(dto.getParams());
                liveMybatisDao.updateTopic(topic);
                liveLocalJdbcDao.updateTopicContentTitle(topic.getId(), dto.getParams());
                log.info("update live success");
                return Response.success();
            }
		} else {
			return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
		}

		return Response.failure(ResponseStatus.ACTION_NOT_SUPPORT.status, ResponseStatus.ACTION_NOT_SUPPORT.message);
	}

    private void builderTopicSearch(long uid, ShowTopicSearchDTO showTopicSearchDTO, List<Map<String,Object>> topicList,
    		Map<String, String> topMap, Map<String, String> publishMap) {
    	if(null == topicList || topicList.size() == 0){
    		return;
    	}
    	if(null == topMap){
    		topMap = new HashMap<String, String>();
    	}
    	if(null == publishMap){
    		publishMap = new HashMap<String, String>();
    	}

		List<Long> uidList = new ArrayList<Long>();
		List<Long> tidList = new ArrayList<Long>();
		List<Long> ceTidList = new ArrayList<Long>();
    	for(Map<String,Object> topic : topicList){
    		Long u = (Long)topic.get("uid");
    		Long id = (Long)topic.get("id");
    		if(!uidList.contains(u)){
    			uidList.add(u);
    		}
    		if(!tidList.contains(id)){
    			tidList.add(id);
    		}
    		if(((Integer)topic.get("type")).intValue() == Specification.KingdomType.AGGREGATION.index){//聚合王国
    			if(!ceTidList.contains(id)){
    				ceTidList.add(id);
    			}
    		}
    	}
    	//一次性查询用户属性
    	Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
        	for(UserProfile up : profileList){
        		profileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
        	for(UserFollow uf : userFollowList){
        		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
        	}
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(tidList);
        if(null != tcList && tcList.size() > 0){
        	for(Map<String, Object> m : tcList){
        		topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
        		reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
        	}
        }
        List<Long> cidList = new ArrayList<Long>();
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(tidList);
        if(null != contentList && contentList.size() > 0){
        	for(Content c : contentList){
        		contentMap.put(String.valueOf(c.getForwardCid()), c);
        		if(!cidList.contains(c.getId())){
        			cidList.add(c.getId());
        		}
        	}
        }
        //一次性查询用户是否点赞过
        Map<String, Long> contentLikeCountMap = liveLocalJdbcDao.getLikeCountByUidAndCids(uid, cidList);
        if(null == contentLikeCountMap){
        	contentLikeCountMap = new HashMap<String, Long>();
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
        	for(LiveFavorite lf : liveFavoriteList){
        		liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
        	}
        }
        //一次性查询所有王国的最新一条核心圈更新
        Map<String, Map<String, Object>> lastFragmentMap = new HashMap<String, Map<String, Object>>();
        List<Map<String, Object>> lastFragmentList = liveLocalJdbcDao.getLastCoreCircleFragmentByTopicIds(tidList);
        if(null != lastFragmentList && lastFragmentList.size() > 0){
        	for(Map<String, Object> m : lastFragmentList){
        		lastFragmentMap.put(String.valueOf(m.get("topic_id")), m);
        	}
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
        	List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
        	if(null != acCountList && acCountList.size() > 0){
        		for(Map<String,Object> a : acCountList){
        			acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
        		}
        	}
        }
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveLocalJdbcDao.getTopicMembersCount(tidList);
        if(null == topicMemberCountMap){
        	topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
        List<TopicTagDetail> topicTagList = liveMybatisDao.getTopicTagDetailListByTopicIds(tidList);
        if(null != topicTagList && topicTagList.size() > 0){
        	long tid = 0;
        	String tags = null;
        	for(TopicTagDetail ttd : topicTagList){
        		if(ttd.getTopicId().longValue() != tid){
        			//先插入上一次
        			if(tid > 0 && !StringUtils.isEmpty(tags)){
        				topicTagMap.put(String.valueOf(tid), tags);
        			}
        			//再初始化新的
        			tid = ttd.getTopicId().longValue();
        			tags = null;
        		}
        		if(tags != null){
        			tags = tags + ";" + ttd.getTag();
        		}else{
        			tags = ttd.getTag();
        		}
        	}
        	if(tid > 0 && !StringUtils.isEmpty(tags)){
        		topicTagMap.put(String.valueOf(tid), tags);
        	}
        }

        UserProfile userProfile = null;
        ShowTopicSearchDTO.TopicElement e = null;
        MySubscribeCacheModel cacheModel = null;
        Map<String, Object> lastFragment = null;
        Content content = null;
        Long topicUid = null;
        Long topicId = null;
        for (Map<String,Object> topic : topicList) {
        	e = new ShowTopicSearchDTO.TopicElement();
        	topicUid = (Long)topic.get("uid");
        	topicId = (Long)topic.get("id");
        	userProfile = profileMap.get(String.valueOf(topicUid));
            e.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            e.setNickName(userProfile.getNickName());
            e.setV_lv(userProfile.getvLv());
            e.setLevel(userProfile.getLevel());
            e.setUid(topicUid);
            e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
            e.setTitle((String)topic.get("title"));
            e.setCreateTime((Date)topic.get("create_time"));
            e.setTopicId(topicId);
            e.setStatus((Integer)topic.get("status"));
            e.setUpdateTime((Long)topic.get("long_time"));
            e.setPrice((Integer)topic.get("price"));		// 2.2.7王国价值
            if(null != followMap.get(uid+"_"+topicUid)){
            	e.setIsFollowed(1);
            }else{
            	e.setIsFollowed(0);
            }
            if(null != followMap.get(topicUid+"_"+uid)){
            	e.setIsFollowMe(1);
            }else{
            	e.setIsFollowMe(0);
            }
            e.setLastUpdateTime((Long)topic.get("long_time"));
            if(null != topicCountMap.get(String.valueOf(topicId))){
            	e.setTopicCount(topicCountMap.get(String.valueOf(topicId)).intValue());
            }else{
            	e.setTopicCount(0);
            }
            e.setInternalStatus(this.getUserInternalStatus((String)topic.get("core_circle"), uid));

            cacheModel = new MySubscribeCacheModel(uid, String.valueOf(topicId), "0");
            String isUpdate = cacheService.hGet(cacheModel.getKey(), String.valueOf(topicId));
            if (!StringUtils.isEmpty(isUpdate) && Integer.valueOf(isUpdate) > 0) {
                e.setIsUpdate(1);
            }

            lastFragment = lastFragmentMap.get(String.valueOf(topicId));
            if (null != lastFragment) {
                e.setLastContentType((Integer)lastFragment.get("content_type"));
                e.setLastFragment((String)lastFragment.get("fragment"));
                e.setLastFragmentImage((String)lastFragment.get("fragment_image"));
                e.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
            } else {
                e.setLastContentType(-1);
            }
            if(null != reviewCountMap.get(String.valueOf(topicId))){
            	e.setReviewCount(reviewCountMap.get(String.valueOf(topicId)).intValue());
            }else{
            	e.setReviewCount(0);
            }
            content = contentMap.get(String.valueOf(topicId));
            if (content != null) {
                e.setLikeCount(content.getLikeCount());
                e.setPersonCount(content.getPersonCount());
                e.setCid(content.getId());
                if(null != contentLikeCountMap.get(String.valueOf(content.getId()))
                		&& contentLikeCountMap.get(String.valueOf(content.getId())).longValue() > 0){
                	e.setIsLike(1);
                }else{
                	e.setIsLike(0);
                }
                e.setReadCount(content.getReadCountDummy());
                e.setType(content.getType());
            }
            if(null != topicMemberCountMap.get(String.valueOf(topicId))){
            	e.setFavoriteCount(topicMemberCountMap.get(String.valueOf(topicId)).intValue()+1);
            }else{
            	e.setFavoriteCount(1);
            }

            //判断是否收藏了
            if (null != liveFavoriteMap.get(String.valueOf(topicId))) {
                e.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                e.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }

            if(null != topMap.get(String.valueOf(topicId))){
            	e.setIsTop(1);
            }else{
                e.setIsTop(0);
            }
            if(null != publishMap.get(String.valueOf(topicId))){
                e.setIsPublish(1);
            }else{
                e.setIsPublish(0);
            }

            e.setContentType((Integer)topic.get("type"));

            if(e.getContentType() == Specification.KingdomType.AGGREGATION.index){//聚合王国
                if(null != acCountMap.get(String.valueOf(topicId))){
                    e.setAcCount(acCountMap.get(String.valueOf(topicId)).intValue());
                }else{
                    e.setAcCount(0);
                }
            }

            e.setPageUpdateTime((Long)topic.get("longtime"));

            if(null != topicTagMap.get(topicId.toString())){
                e.setTags(topicTagMap.get(topicId.toString()));
            }else{
                e.setTags("");
            }

            showTopicSearchDTO.getResultList().add(e);
        }
    }

    private int getUserInternalStatus(String coreCircle, long uid) {
        JSONArray array = JSON.parseArray(coreCircle);
        int internalStatus = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == uid) {
                internalStatus = Specification.SnsCircle.CORE.index;
                break;
            }
        }
//        if (internalStatus == 0 && null != internalStatusMap.get(uid+"_"+topicUid)) {
//            internalStatus = internalStatusMap.get(uid+"_"+topicUid).intValue();
//        }

        return internalStatus;
    }

    @Override
    public Response aggregationPublish(long uid, long topicId, long fid){
        Topic topic = liveMybatisDao.getTopicById(topicId);
        //必须是国王才能进行下发操作
        if(null == topic || topic.getUid().longValue() != uid){
            return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
        }
        if(topic.getType().intValue() != Specification.KingdomType.AGGREGATION.index){
            return Response.failure(ResponseStatus.KINGDOM_IS_NOT_AGGREGATION.status, ResponseStatus.KINGDOM_IS_NOT_AGGREGATION.message);
        }

        int max = 10;
        String count = cacheService.get(TOPIC_AGGREGATION_PUBLISH_COUNT);
        if(!StringUtils.isEmpty(count)){
            max = Integer.valueOf(count);
        }

        String dayStr = DateUtil.date2string(new Date(), "yyyyMMdd");
        String key = topicId+"_"+dayStr;
        String result = cacheService.get(key);
        int currentCount = 0;
        if(!StringUtils.isEmpty(result)){
            currentCount = Integer.valueOf(result);
        }
        currentCount++;
        if(currentCount > max){//超过了
            return Response.failure(ResponseStatus.AGGREGATION_PUBLISH_OVER_LIMIT.status, ResponseStatus.AGGREGATION_PUBLISH_OVER_LIMIT.message.replace("#{count}#", String.valueOf(max)));
        }

        TopicFragment tf = liveMybatisDao.getTopicFragmentById(fid);
        if(null == tf || tf.getTopicId().longValue() != topic.getId().longValue()
                || tf.getStatus() != Specification.TopicFragmentStatus.ENABLED.index){
            return Response.failure(ResponseStatus.FRAGMENT_IS_NOT_EXIST.status, ResponseStatus.FRAGMENT_IS_NOT_EXIST.message);
        }

        //异步处理内容下发
        AggregationPublishEvent event = new AggregationPublishEvent();
        event.setUid(uid);
        event.setTopicId(topicId);
        event.setFid(fid);
        event.setLiveWebUrl(live_web);
        applicationEventBus.post(event);

        //记录下发次数
        cacheService.setex(key,String.valueOf(currentCount),60*60*24);

        return Response.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response aggregationOpt(AggregationOptDto dto) {
        if (dto.getAcTopicId() == dto.getCeTopicId()) {// 自己对自己暂不支持操作
            return Response.failure(ResponseStatus.ACTION_NOT_SUPPORT.status, ResponseStatus.ACTION_NOT_SUPPORT.message);
        }

        Date now = new Date();
        TopicAggregation topicAggregation = liveMybatisDao.getTopicAggregationByTopicIdAndSubId(dto.getCeTopicId(), dto.getAcTopicId());

        if (dto.getType() == Specification.KingdomLanuchType.PERSONAL_LANUCH.index) {// 个人王国发起
            Topic topicOwner = liveMybatisDao.getTopicById(dto.getAcTopicId());
            Topic topic = liveMybatisDao.getTopicById(dto.getCeTopicId());
            if (topic == null || topicOwner == null) {
                return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, ResponseStatus.LIVE_HAS_DELETED.message);
            }

            if (dto.getAction() == Specification.AggregationOptType.APPLY.index) {// 收录申请
                if(!this.isInCore(dto.getUid(), topicOwner.getCoreCircle())){
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_CORECIRCLE.status, ResponseStatus.YOU_ARE_NOT_CORECIRCLE.message);
                }

                //是否重复收录
                if(topicAggregation != null){
                    return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已申请");
                }

                if(this.isKing(dto.getUid(), topic.getUid())){//我是聚合王国国王
                    //直接成功
                    TopicAggregation agg = new TopicAggregation();
                    agg.setTopicId(dto.getCeTopicId());
                    agg.setSubTopicId(dto.getAcTopicId());
                    liveMybatisDao.createTopicAgg(agg);
                    this.aggregateSuccessAfter(topic, topicOwner);

                    //如果我不是个人王国国王，则需要通知个人王国国王
//                    if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                    	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "收录了你的个人王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //发推送
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                        	userService.noticeMessagePush(topicOwner.getUid(), "有聚合王国收录了你的个人王国", 2);
//                        }
//                    }

                    return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                }else if(this.isInCore(dto.getUid(), topic.getCoreCircle())){//我是聚合王国的核心圈
                    //直接成功
                    TopicAggregation agg = new TopicAggregation();
                    agg.setTopicId(dto.getCeTopicId());
                    agg.setSubTopicId(dto.getAcTopicId());
                    liveMybatisDao.createTopicAgg(agg);
                    this.aggregateSuccessAfter(topic, topicOwner);

//                    //先向聚合王国国王发消息
//                    this.aggregationRemind(dto.getUid(), topic.getUid(), "加入了你的聚合王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                    	userService.noticeMessagePush(topic.getUid(), "有个人王国加入了你的聚合王国", 2);
//                    }
//
//                    //如果我不是个人王国国王，则需要通知个人王国国王
//                    if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                    	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "收录了你的个人王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //发推送
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                        	userService.noticeMessagePush(topicOwner.getUid(), "有聚合王国收录了你的个人王国", 2);
//                        }
//                    }

                    return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                }else{//我是圈外身份
                    if (topic.getCeAuditType() == 0) {//需要审核
                        //查询是否申请过
                        List<Integer> resultList = new ArrayList<Integer>();
                        resultList.add(0);
                        resultList.add(1);
                        List<TopicAggregationApply> list = liveMybatisDao.getTopicAggregationApplyByTopicAndTargetAndResult(dto.getAcTopicId(), dto.getCeTopicId(), 2, resultList);
                        if(null != list && list.size() > 0){
                            //重复操作
                            return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已申请");
                        }
                        //需要申请同意收录
                        TopicAggregationApply apply = new TopicAggregationApply();
                        apply.setResult(0);
                        apply.setTopicId(dto.getAcTopicId());
                        apply.setTargetTopicId(dto.getCeTopicId());
                        apply.setCreateTime(now);
                        apply.setUpdateTime(now);
                        apply.setType(2);
                        apply.setOperator(dto.getUid());
                        liveMybatisDao.createTopicAggApply(apply);

                        //向聚合王国的核心圈下发申请消息和推送
                        CoreAggregationRemindEvent event = new CoreAggregationRemindEvent();
                        event.setApplyId(apply.getId());
                        event.setReview("申请加入你的聚合王国");
                        event.setSourceTopic(topicOwner);
                        event.setSourceUid(dto.getUid());
                        event.setTargetTopic(topic);
                        event.setMessage("有个人王国申请加入你的聚合王国");
                        this.applicationEventBus.post(event);

                        return Response.success(200, "已发送申请");
                    }else{//不需要审核
                        TopicAggregation agg = new TopicAggregation();
                        agg.setTopicId(dto.getCeTopicId());
                        agg.setSubTopicId(dto.getAcTopicId());
                        liveMybatisDao.createTopicAgg(agg);
                        this.aggregateSuccessAfter(topic, topicOwner);

//                        //发送消息
//                        this.aggregationRemind(dto.getUid(), topic.getUid(), "加入了你的聚合王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //发推送
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                        	userService.noticeMessagePush(topic.getUid(), "有个人王国加入了你的聚合王国", 2);
//                        }
//
//                        //判断个人王国的国王是不是当前操作人，如果是，则不需要消息
//                        if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                        	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "收录了你的个人王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                            //发推送
//                            //本消息是由王国发起的，所以需要判断王国的配置
//                            if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                            	userService.noticeMessagePush(topicOwner.getUid(), "有聚合王国收录了你的个人王国", 2);
//                            }
//                        }

                        return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                    }
                }
            } else if (dto.getAction() == Specification.AggregationOptType.DISMISS.index) {// 解散聚合
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }

                liveMybatisDao.deleteTopicAgg(dto.getCeTopicId(), dto.getAcTopicId());

                List<Long> ids = new ArrayList<Long>();
                ids.add(dto.getCeTopicId());
                ids.add(dto.getAcTopicId());
                List<Integer> resultList = new ArrayList<Integer>();
                resultList.add(1);
                //解除如果以前申请成功过，需要将原先申请的记录置为失效
                List<TopicAggregationApply> list =  liveMybatisDao.getTopicAggregationApplyBySourceIdsAndTargetIdsAndResults(ids, ids, resultList);
                if(null != list && list.size() > 0){
                    for(TopicAggregationApply a : list){
                        a.setResult(3);
                        liveMybatisDao.updateTopicAggregationApply(a);
                    }
                }

                if(topicOwner.getUid().longValue() != topic.getUid().longValue() ){//如果是踢自己的王国，不需要发消息和推送
                    this.aggregationRemind(topicOwner.getUid(), topic.getUid(), "退出了你的聚合王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);

                    //发推送
                    //本消息是由王国发起的，所以需要判断王国的配置
//                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                    	userService.noticeMessagePush(topic.getUid(), "有个人王国退出了你的聚合王国", 2);
//                    }
                }

                return Response.success(200, "操作成功");
            } else if (dto.getAction() == Specification.AggregationOptType.ISSUED.index) {// 接受下发设置(个人王国设置)
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }
                if (topicAggregation != null) {
                    // 0接受推送 1不接受推送
                    topicAggregation.setIsPublish(0);
                    liveMybatisDao.updateTopicAggregation(topicAggregation);
                }
                log.info("issued success");
                return Response.success(200, "操作成功");
            } else if (dto.getAction() == Specification.AggregationOptType.CANCEL_ISSUED.index) {// 取消接受下发设置(个人王国设置)
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }
                if (topicAggregation != null) {
                    // 0接受推送 1不接受推送
                    topicAggregation.setIsPublish(1);
                    liveMybatisDao.updateTopicAggregation(topicAggregation);
                }
                log.info("cancel issued success");
                return Response.success(200, "操作成功");
            }
        } else if (dto.getType() == Specification.KingdomLanuchType.AGGREGATION_LANUCH.index) {// 聚合王国王国发起
            Topic topicOwner = liveMybatisDao.getTopicById(dto.getCeTopicId());
            Topic topic = liveMybatisDao.getTopicById(dto.getAcTopicId());
            if (topic == null || topicOwner == null) {
                return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, ResponseStatus.LIVE_HAS_DELETED.message);
            }

            if (dto.getAction() == Specification.AggregationOptType.APPLY.index) {// 收录申请
                if(!this.isInCore(dto.getUid(), topicOwner.getCoreCircle())){
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_CORECIRCLE.status, ResponseStatus.YOU_ARE_NOT_CORECIRCLE.message);
                }

                //是否重复收录
                if(topicAggregation != null){
                    return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已申请");
                }

                if(this.isKing(dto.getUid(), topic.getUid().longValue())){//我是个人王国的国王
                    //直接成功
                    TopicAggregation agg = new TopicAggregation();
                    agg.setTopicId(dto.getCeTopicId());
                    agg.setSubTopicId(dto.getAcTopicId());
                    liveMybatisDao.createTopicAgg(agg);
                    this.aggregateSuccessAfter(topicOwner, topic);

//                    //如果我不是聚合王国的，则需要向聚合王国国王发消息
//                    if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                    	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "加入了你的聚合王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                        	userService.noticeMessagePush(topicOwner.getUid(), "有个人王国加入了你的聚合王国", 2);
//                        }
//                    }

                    return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                }else if(this.isInCore(dto.getUid(), topic.getCoreCircle())){//我是个人王国的核心圈
                    //直接成功
                    TopicAggregation agg = new TopicAggregation();
                    agg.setTopicId(dto.getCeTopicId());
                    agg.setSubTopicId(dto.getAcTopicId());
                    liveMybatisDao.createTopicAgg(agg);
                    this.aggregateSuccessAfter(topicOwner, topic);

//                    //向双方国王发送消息
//                    //先向个人王国发送消息和推送
//                    this.aggregationRemind(dto.getUid(), topic.getUid(), "收录了你的个人王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                    //本消息是由王国发起的，所以需要判断王国的配置
//                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                    	userService.noticeMessagePush(topic.getUid(), "有聚合王国收录了你的个人王国", 2);
//                    }
//                    //再想聚合王国发送消息和推送（如果这个聚合王国是自己的，则不需要消息了）
//                    if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                    	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "加入了你的聚合王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                        	userService.noticeMessagePush(topicOwner.getUid(), "有个人王国加入了你的聚合王国", 2);
//                        }
//                    }

                    return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                }else{//我是圈外身份
                    if (topic.getAcAuditType() == 0) {//需要审核
                        //查询是否申请过
                        List<Integer> resultList = new ArrayList<Integer>();
                        resultList.add(0);
                        resultList.add(1);
                        List<TopicAggregationApply> list = liveMybatisDao.getTopicAggregationApplyByTopicAndTargetAndResult(dto.getCeTopicId() ,dto.getAcTopicId(), 1, resultList);
                        if(null != list && list.size() > 0){//有过申请，并且是初始化的或已同意的
                            return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已申请");
                        }
                        //需要申请同意收录
                        TopicAggregationApply apply = new TopicAggregationApply();
                        apply.setResult(0);
                        apply.setTopicId(dto.getCeTopicId());
                        apply.setTargetTopicId(dto.getAcTopicId());
                        apply.setCreateTime(now);
                        apply.setUpdateTime(now);
                        apply.setType(1);
                        apply.setOperator(dto.getUid());
                        liveMybatisDao.createTopicAggApply(apply);

                        //向个人王国的核心圈发送申请消息和推送
                        CoreAggregationRemindEvent event = new CoreAggregationRemindEvent();
                        event.setApplyId(apply.getId());
                        event.setReview("申请收录你的个人王国");
                        event.setSourceTopic(topicOwner);
                        event.setSourceUid(dto.getUid());
                        event.setTargetTopic(topic);
                        event.setMessage("有聚合王国申请收录你的个人王国");
                        this.applicationEventBus.post(event);

                        return Response.success(200, "已发送申请");
                    }else{//不需要审核
                        TopicAggregation agg = new TopicAggregation();
                        agg.setTopicId(dto.getCeTopicId());
                        agg.setSubTopicId(dto.getAcTopicId());
                        liveMybatisDao.createTopicAgg(agg);
                        this.aggregateSuccessAfter(topicOwner, topic);

//                        //向双方国王发送消息
//                        //先向个人王国发送消息和推送
//                        this.aggregationRemind(dto.getUid(), topic.getUid(), "收录了你的个人王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                        //本消息是由王国发起的，所以需要判断王国的配置
//                        if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                        	userService.noticeMessagePush(topic.getUid(), "有聚合王国收录了你的个人王国", 2);
//                        }
//                        //再想聚合王国发送消息和推送（如果这个聚合王国是自己的，则不需要消息了）
//                        if(!this.isKing(dto.getUid(), topicOwner.getUid().longValue())){
//                        	this.aggregationRemind(dto.getUid(), topicOwner.getUid(), "加入了你的聚合王国", 0, topicOwner, topic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                            //本消息是由王国发起的，所以需要判断王国的配置
//                            if(this.checkTopicPush(topicOwner.getId(), topicOwner.getUid())){
//                            	userService.noticeMessagePush(topicOwner.getUid(), "有个人王国加入了你的聚合王国", 2);
//                            }
//                        }
                        return Response.success(ResponseStatus.AGGREGATION_APPLY_SUCCESS.status,ResponseStatus.AGGREGATION_APPLY_SUCCESS.message);
                    }
                }
            } else if (dto.getAction() == Specification.AggregationOptType.DISMISS.index) {// 解散聚合
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }

                liveMybatisDao.deleteTopicAgg(dto.getCeTopicId(), dto.getAcTopicId());

                if(topicOwner.getUid().longValue() != topic.getUid().longValue() ){//如果是踢自己的王国，不需要发消息和推送
                    this.aggregationRemind(topicOwner.getUid(), topic.getUid(), "踢走了你的个人王国", 0, topic, topicOwner, Specification.UserNoticeType.AGGREGATION_NOTICE.index);

                    List<Long> ids = new ArrayList<Long>();
                    ids.add(dto.getCeTopicId());
                    ids.add(dto.getAcTopicId());
                    List<Integer> resultList = new ArrayList<Integer>();
                    resultList.add(1);
                    //解除如果以前申请成功过，需要将原先申请的记录置为失效
                    List<TopicAggregationApply> list =  liveMybatisDao.getTopicAggregationApplyBySourceIdsAndTargetIdsAndResults(ids, ids, resultList);
                    if(null != list && list.size() > 0){
                        for(TopicAggregationApply a : list){
                            a.setResult(3);
                            liveMybatisDao.updateTopicAggregationApply(a);
                        }
                    }

                    //发推送
                    //本消息是由王国发起的，所以需要判断王国的配置
//                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                    	userService.noticeMessagePush(topic.getUid(), "有聚合王国踢走了你的个人王国", 2);
//                    }
                }

                return Response.success(200, "操作成功");
            } else if (dto.getAction() == Specification.AggregationOptType.TOP.index) {// 置顶操作(聚合王国设置)
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }
                if (topicAggregation != null) {
                    List<TopicAggregation> list = liveMybatisDao.getTopicAggregationByTopicIdAndIsTop(dto.getCeTopicId(), 1);
                    if (list.size() < Integer.valueOf(cacheService.get(TOP_COUNT))) {
                        // 1置顶 0不置顶
                        topicAggregation.setIsTop(1);
                        // 设置时间为了下次查询会显示在第一个
                        topicAggregation.setUpdateTime(now);
                        liveMybatisDao.updateTopicAggregation(topicAggregation);
                    } else {
                        log.info("top over limit");
                        return Response.failure(ResponseStatus.TOP_COUNT_OVER_LIMIT.status, ResponseStatus.TOP_COUNT_OVER_LIMIT.message);
                    }
                }
                log.info("top success");
                return Response.success(200, "操作成功");
            } else if (dto.getAction() == Specification.AggregationOptType.CANCEL_TOP.index) {// 取消置顶操作(聚合王国设置)
                if (!this.isKing(dto.getUid(), topicOwner.getUid().longValue())) {
                    return Response.failure(ResponseStatus.YOU_ARE_NOT_KING.status, ResponseStatus.YOU_ARE_NOT_KING.message);
                }
                if (topicAggregation != null) {
                    topicAggregation.setIsTop(0);
                    liveMybatisDao.updateTopicAggregation(topicAggregation);
                }
                log.info("cancel top success");
                return Response.success(200, "操作成功");
            }
        }

        return Response.failure(ResponseStatus.ACTION_NOT_SUPPORT.status, ResponseStatus.ACTION_NOT_SUPPORT.message);
    }

    private boolean checkTopicPush(long topicId, long uid){
        TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
        if(null != tuc && tuc.getPushType().intValue() == 1){
            return false;
        }
        return true;
    }

    @Override
    public Response aggregationApplyOpt(AggregationOptDto dto) {
        TopicAggregationApply topicAggregationApply = liveMybatisDao.getTopicAggregationApplyById(dto.getApplyId());
        if(topicAggregationApply != null) {
            Topic topic = liveMybatisDao.getTopicById(topicAggregationApply.getTopicId());
            Topic targetTopic = liveMybatisDao.getTopicById(topicAggregationApply.getTargetTopicId());
            if(topic ==null || targetTopic == null){
                //失效
                topicAggregationApply.setResult(3);
                liveMybatisDao.updateTopicAggregationApply(topicAggregationApply);
                log.info("update topicAggreationApply result : 3");
                return Response.failure(ResponseStatus.DATA_DOES_NOT_EXIST.status, ResponseStatus.DATA_DOES_NOT_EXIST.message);
            }
            //1同意 2拒绝
            if (topicAggregationApply.getResult() == 0) {
                //0初始的情况才能操作
                String review = null;
                String message = null;
                if (dto.getAction() == 1) {
                    topicAggregationApply.setResult(1);
                    topicAggregationApply.setOperator2(dto.getUid());
                    liveMybatisDao.updateTopicAggregationApply(topicAggregationApply);
                    log.info("update topic_agg_apply success");
                    TopicAggregation aggregation = new TopicAggregation();
                    if(topicAggregationApply.getType() == 1){//母拉子
                        aggregation.setTopicId(topicAggregationApply.getTopicId());
                        aggregation.setSubTopicId(topicAggregationApply.getTargetTopicId());
                        liveMybatisDao.createTopicAgg(aggregation);
                        this.aggregateSuccessAfter(topic, targetTopic);
                        message = "个人王国同意了你的收录申请";
                    }else{//子求母
                        aggregation.setTopicId(topicAggregationApply.getTargetTopicId());
                        aggregation.setSubTopicId(topicAggregationApply.getTopicId());
                        liveMybatisDao.createTopicAgg(aggregation);
                        this.aggregateSuccessAfter(targetTopic, topic);
                        message = "聚合王国同意了你的收录申请";
                    }
                    review = "同意你的收录申请";
                    log.info("create topic_agg success");
                } else if (dto.getAction() == 2) {
                    topicAggregationApply.setResult(2);
                    topicAggregationApply.setOperator2(dto.getUid());
                    liveMybatisDao.updateTopicAggregationApply(topicAggregationApply);
                    review = "拒绝你的收录申请";
                    if(topicAggregationApply.getType() == 1){//母拉子
                        message = "个人王国拒绝了你的收录申请";
                    }else{//子求母
                        message = "聚合王国拒绝了你的收录申请";
                    }
                    log.info("update topic_agg_apply success");
                }else{
                    return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "无效操作");
                }

                //先向对方操作人发消息，这个消息肯定要发的
                this.aggregationRemind(dto.getUid(), topicAggregationApply.getOperator().longValue(), review, 0, topic, targetTopic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
                //本消息是由王国发起的，所以需要判断王国的配置
//                if(this.checkTopicPush(topic.getId(), topicAggregationApply.getOperator().longValue())){
//                	userService.noticeMessagePush(topicAggregationApply.getOperator().longValue(), message, 2);
//                }

//                //然后是同意的，则需要向对方国王发消息，当然如果这个国王就是操作人则不需要发了，已经发过了
//                if (dto.getAction() == 1 && topicAggregationApply.getOperator().longValue() != topic.getUid().longValue()) {
//                	this.aggregationRemind(dto.getUid(), topic.getUid(), review, 0, topic, targetTopic, Specification.UserNoticeType.AGGREGATION_NOTICE.index);
//                    //本消息是由王国发起的，所以需要判断王国的配置
//                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
//                    	userService.noticeMessagePush(topic.getUid(), message, 2);
//                    }
//                }

                return Response.success(200, "操作成功");
            }else if (topicAggregationApply.getResult() == 1) {
                return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已经同意了哦");
            }else if (topicAggregationApply.getResult() == 2) {
                return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "已经拒绝了哦");
            }else if (topicAggregationApply.getResult() == 3) {
                return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, "申请已失效");
            }else {
                return Response.failure(ResponseStatus.REPEATED_TREATMENT.status, ResponseStatus.REPEATED_TREATMENT.message);
            }
        }

        return Response.failure(ResponseStatus.DATA_DOES_NOT_EXIST.status, ResponseStatus.DATA_DOES_NOT_EXIST.message);
    }

    private void aggregationRemind(long sourceUid, long targetUid, String review, long cid, Topic textTopic, Topic coverTopic, int type) {
        if (targetUid == sourceUid) {
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        UserProfile customerProfile = userService.getUserProfileByUid(targetUid);
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setToUid(customerProfile.getUid());

        userNotice.setCid(cid);
        userNotice.setReview(review);
        userNotice.setNoticeType(type);

        userNotice.setSummary("");
        userNotice.setLikeCount(0);
        userNotice.setTag("");
        userNotice.setReadStatus(0);

        userNotice.setCoverImage(coverTopic.getLiveImage());

        JSONObject obj = new JSONObject();
        obj.put("textImage", textTopic.getLiveImage());
        obj.put("textTitle", textTopic.getTitle());
        obj.put("textType", textTopic.getType());
        obj.put("textTopicId", textTopic.getId());
        obj.put("coverImage", coverTopic.getLiveImage());
        obj.put("coverTitle", coverTopic.getTitle());
        obj.put("coverType", coverTopic.getType());
        obj.put("coverTopicId", coverTopic.getId());
        userNotice.setExtra(obj.toJSONString());

        long unid = userService.createUserNoticeAndReturnId(userNotice);

        Date now = new Date();
        //V2.2.5版本开始使用新的红点体系
        UserNoticeUnread unu = new UserNoticeUnread();
        unu.setUid(targetUid);
        unu.setCreateTime(now);
        unu.setNoticeId(unid);
        unu.setNoticeType(type);
        unu.setContentType(Specification.UserNoticeUnreadContentType.KINGDOM.index);
        unu.setCid(cid);
        unu.setLevel(Specification.UserNoticeLevel.LEVEL_2.index);
        userService.createUserNoticeUnread(unu);

        //添加系统消息红点
        cacheService.set("my:notice:level2:"+targetUid, "1");

        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        userTips.setType(type);
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
        }
        userService.noticeCountPush(targetUid);
    }

    /**
     * 收录成功后需要做的事
     * 再母王国和子王国中插入各自王国的内链
     */
    private void aggregateSuccessAfter(Topic ceTopic, Topic acTopic){
        //在双方的王国里插入相关系统提示信息
        Content ceContent = contentService.getContentByTopicId(ceTopic.getId());
        Content acContent = contentService.getContentByTopicId(acTopic.getId());
        UserProfile ceUser = userService.getUserProfileByUid(ceTopic.getUid());
        UserProfile acUser = userService.getUserProfileByUid(acTopic.getUid());
        //1在母王国里插入
        String ceFragmentContent = "王国"+acTopic.getTitle()+"已加入了本聚合王国";
        TopicFragment ceFragment = new TopicFragment();
        ceFragment.setTopicId(ceTopic.getId());
        ceFragment.setUid(ceTopic.getUid());
        ceFragment.setFragment(ceFragmentContent);
        ceFragment.setType(Specification.LiveSpeakType.SYSTEM.index);
        ceFragment.setContentType(72);//王国内链
        //组装extra
        JSONObject obj = new JSONObject();
        obj.put("type", "system");
        obj.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
        obj.put("content", ceFragmentContent);
        obj.put("linkType", 72);//王国内链
        obj.put("linkColor", "#8B572A");
        obj.put("linkStart", 2);//从0算起
        obj.put("linkEnd", acTopic.getTitle().length()+2);
        //组装链接
        JSONObject linkObj = new JSONObject();
        linkObj.put("type", "link");
        linkObj.put("id", acTopic.getId());
        linkObj.put("cid", acContent.getId());
        linkObj.put("uid", acTopic.getUid());
        linkObj.put("title", acTopic.getTitle());
        linkObj.put("subType", acTopic.getType());
        linkObj.put("avatar", Constant.QINIU_DOMAIN + "/" + acUser.getAvatar());
        linkObj.put("createTime", acTopic.getCreateTime().getTime());
        linkObj.put("url", this.live_web + acTopic.getId());
        linkObj.put("v_lv", acUser.getvLv());
        linkObj.put("name", acUser.getNickName());
        linkObj.put("cover", Constant.QINIU_DOMAIN + "/" + acTopic.getLiveImage());
        linkObj.put("action", 0);
        obj.put("link", linkObj);
        ceFragment.setExtra(obj.toJSONString());
        liveMybatisDao.createTopicFragment(ceFragment);

        //系统信息不参与排序计算，故不需要更新王国更新时间
//    	Calendar calendar = Calendar.getInstance();
//    	ceTopic.setUpdateTime(calendar.getTime());
//    	ceTopic.setLongTime(calendar.getTimeInMillis());
//        liveMybatisDao.updateTopic(ceTopic);

        //更新缓存
        long ceLastFragmentId = ceFragment.getId();
        int ceTotal = liveMybatisDao.countFragmentByTopicId(ceTopic.getId());
        String ceValue = ceLastFragmentId + "," + ceTotal;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + ceTopic.getId(), ceValue);

        SpeakNewEvent speakNewEvent = new SpeakNewEvent();
        speakNewEvent.setTopicId(ceFragment.getTopicId());
        speakNewEvent.setType(ceFragment.getType());
        speakNewEvent.setContentType(ceFragment.getContentType());
        speakNewEvent.setUid(ceFragment.getUid());
        speakNewEvent.setFragmentId(ceLastFragmentId);
        speakNewEvent.setFragmentContent(ceFragment.getFragment());
        speakNewEvent.setFragmentExtra(ceFragment.getExtra());
        applicationEventBus.post(speakNewEvent);

        //2在子王国里插入
        String acFragmentContent = "本王国已加入聚合王国"+ceTopic.getTitle();
        TopicFragment acFragment = new TopicFragment();
        acFragment.setTopicId(acTopic.getId());
        acFragment.setUid(acTopic.getUid());
        acFragment.setFragment(acFragmentContent);
        acFragment.setType(Specification.LiveSpeakType.SYSTEM.index);
        acFragment.setContentType(72);//王国内链
        //组装extra
        JSONObject obj2 = new JSONObject();
        obj2.put("type", "system");
        obj2.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
        obj2.put("content", acFragmentContent);
        obj2.put("linkType", 72);//王国内链
        obj2.put("linkColor", "#8B572A");
        obj2.put("linkStart", 10);//从0算起
        obj2.put("linkEnd", acFragmentContent.length());
        //组装链接
        JSONObject linkObj2 = new JSONObject();
        linkObj2.put("type", "link");
        linkObj2.put("id", ceTopic.getId());
        linkObj2.put("cid", ceContent.getId());
        linkObj2.put("uid", ceTopic.getUid());
        linkObj2.put("title", ceTopic.getTitle());
        linkObj2.put("subType", ceTopic.getType());
        linkObj2.put("avatar", Constant.QINIU_DOMAIN + "/" + ceUser.getAvatar());
        linkObj2.put("createTime", ceTopic.getCreateTime().getTime());
        linkObj2.put("url", this.live_web + ceTopic.getId());
        linkObj2.put("v_lv", ceUser.getvLv());
        linkObj2.put("name", ceUser.getNickName());
        linkObj2.put("cover", Constant.QINIU_DOMAIN + "/" + ceTopic.getLiveImage());
        linkObj2.put("action", 0);
        obj2.put("link", linkObj2);
        acFragment.setExtra(obj2.toJSONString());
        liveMybatisDao.createTopicFragment(acFragment);

        //系统信息不参与排序计算，故不需要更新王国更新时间
//    	calendar = Calendar.getInstance();
//    	acTopic.setUpdateTime(calendar.getTime());
//    	acTopic.setLongTime(calendar.getTimeInMillis());
//        liveMybatisDao.updateTopic(acTopic);

        //更新缓存
        long acLastFragmentId = acFragment.getId();
        int acTotal = liveMybatisDao.countFragmentByTopicId(acTopic.getId());
        String acValue = acLastFragmentId + "," + acTotal;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + acTopic.getId(), acValue);

        speakNewEvent = new SpeakNewEvent();
        speakNewEvent.setTopicId(acFragment.getTopicId());
        speakNewEvent.setType(acFragment.getType());
        speakNewEvent.setContentType(acFragment.getContentType());
        speakNewEvent.setUid(acFragment.getUid());
        speakNewEvent.setFragmentId(acLastFragmentId);
        speakNewEvent.setFragmentContent(acFragment.getFragment());
        speakNewEvent.setFragmentExtra(acFragment.getExtra());
        applicationEventBus.post(speakNewEvent);
    }

    @Override
    public TopicUserConfig getTopicUserConfigByTopicIdAndUid(long topicId, long uid){
        return liveMybatisDao.getTopicUserConfig(uid, topicId);
    }

    @Override
    public List<LiveFavorite> getLiveFavoriteByTopicId(long topicId, List<Long> exceptUids, int start, int pageSize){
        return liveMybatisDao.getLiveFavoritePageByTopicIdAndExceptUids(topicId, exceptUids, start, pageSize);
    }

    @Override
    public int countLiveFavoriteByTopicId(long topicId, List<Long> exceptUids){
        return liveMybatisDao.countLiveFavoriteByTopicIdAndExceptUids(topicId, exceptUids);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response fragmentForward(long uid, long fid, long sourceTopicId, long targetTopicId){
        Topic sourceTopic = liveMybatisDao.getTopicById(sourceTopicId);
        if(null == sourceTopic){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, "发生未知错误转发失败，再试一次吧。");
        }
        Topic targetTopic = liveMybatisDao.getTopicById(targetTopicId);
        if(null == targetTopic){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, "发生未知错误转发失败，再试一次吧。");
        }
        TopicFragment tf = liveMybatisDao.getTopicFragmentById(fid);
        if(null == tf || tf.getTopicId().longValue() != sourceTopicId
                || tf.getStatus() != Specification.TopicFragmentStatus.ENABLED.index){
            return Response.failure(ResponseStatus.FRAGMENT_IS_NOT_EXIST.status, "发生未知错误转发失败，再试一次吧。");
        }

        boolean isCoreUser = false;
        if(uid == targetTopic.getUid().longValue() || this.isInCore(uid, targetTopic.getCoreCircle())){
            isCoreUser = true;
        }

        TopicFragment newtf = new TopicFragment();
        newtf.setUid(uid);//记录操作人的UID
        newtf.setFragmentImage(tf.getFragmentImage());
        newtf.setFragment(tf.getFragment());
        newtf.setTopicId(targetTopicId);
        //判断身份
        if(isCoreUser){
            newtf.setType(55);
        }else{
            newtf.setType(56);
        }
        newtf.setContentType(this.genContentType(tf.getType(), tf.getContentType()));//转换成新的contentType

        String extra = tf.getExtra();
        //extra转换，添加from属性
        if(null == extra || "".equals(extra)){
            extra = "{}";
        }
        JSONObject obj = JSON.parseObject(extra);
        String only = UUID.randomUUID().toString()+"-"+new Random().nextInt();
        obj.put("only", only);
        obj.put("action", Integer.valueOf(3));//转发
        UserProfile up = userService.getUserProfileByUid(sourceTopic.getUid());
        Content topicContent = contentService.getContentByTopicId(sourceTopicId);
        JSONObject fromObj = new JSONObject();
        fromObj.put("uid", sourceTopic.getUid());
        fromObj.put("avatar", Constant.QINIU_DOMAIN+"/"+up.getAvatar());
        fromObj.put("id", Long.valueOf(sourceTopicId));
        fromObj.put("cid", topicContent.getId());
        fromObj.put("title", sourceTopic.getTitle());
        fromObj.put("cover", Constant.QINIU_DOMAIN+"/"+sourceTopic.getLiveImage());
        fromObj.put("url", live_web+sourceTopicId);
        obj.put("from", fromObj);
        newtf.setExtra(obj.toJSONString());
        liveMybatisDao.createTopicFragment(newtf);

        Calendar calendar = Calendar.getInstance();
        targetTopic.setUpdateTime(calendar.getTime());
        targetTopic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.updateTopic(targetTopic);

        if(isCoreUser){//如果是核心圈，则更新content表的updateTime
        	liveLocalJdbcDao.updateContentUpdateTime4Kingdom(targetTopicId, calendar.getTime());
        	liveLocalJdbcDao.updateContentUpdateId4Kingdom(targetTopicId,cacheService.incr("UPDATE_ID"));
        }
        
        //更新缓存
        long lastFragmentId = newtf.getId();
        int total = liveMybatisDao.countFragmentByTopicId(targetTopicId);
        String value = lastFragmentId + "," + total;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + targetTopicId, value);

        SpeakNewEvent speakNewEvent = new SpeakNewEvent();
        speakNewEvent.setTopicId(targetTopicId);
        speakNewEvent.setType(newtf.getType());
        speakNewEvent.setContentType(newtf.getContentType());
        speakNewEvent.setUid(uid);
        speakNewEvent.setFragmentId(lastFragmentId);
        speakNewEvent.setFragmentContent(newtf.getFragment());
        speakNewEvent.setFragmentExtra(newtf.getExtra());
        applicationEventBus.post(speakNewEvent);

        return Response.success(200, "转发成功");
    }

    @Override
    public Response recommend(long uid, long topicId, long action) {
        Topic topic = liveMybatisDao.getTopicById(topicId);
        CreateActivityDto createActivityDto = new CreateActivityDto();
        if(userService.isAdmin(uid)) {
            ActivityWithBLOBs activity = activityService.getActivityByCid(topicId, 2);
            if (action == 0) {
                if (activity != null) {
                    activity.setStatus(1);
                    activityService.updateActivity(activity);
                    log.info("update activity status : 1");
                    if (topic != null) {
                        setCreateActivityDto(createActivityDto, topic);
                        activityService.createActivityLive(createActivityDto);
                        log.info("create activity success");
                    }
                } else {
                    if (topic != null) {
                        setCreateActivityDto(createActivityDto, topic);
                        activityService.createActivityLive(createActivityDto);
                        log.info("create activity success");
                    }
                }
            }else if(action == 1){
                //取消
                if (activity != null) {
                    activity.setStatus(1);
                    activityService.updateActivity(activity);
                    log.info("update activity status : 1");
                }
            }
            return Response.success(200, "操作成功");
        }

        return Response.failure(ResponseStatus.YOU_ARE_NOT_ADMIN.status ,ResponseStatus.YOU_ARE_NOT_ADMIN.message);
    }

    @Override
    public Response dropAround(long uid, long sourceTopicId) {
        int dr =0;
        String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String number = cacheService.get("droparound:"+uid+"@"+now);
        if(!StringUtils.isEmpty(number)){
            //有的话取
            dr = Integer.parseInt(number);
        }
        //每次进来+1 控制每人每天五次
        cacheService.set("droparound:"+uid+"@"+now ,String.valueOf(dr+1));
        cacheService.expire("droparound:"+uid+"@"+now ,3600*24);//24小时过期

        //控制每个用户避免进入重复的
        Set<String> s = cacheService.smembers("list:user@"+uid );
        String set = org.apache.commons.lang3.StringUtils.join(s.toArray(), ",");
        //王国避免自己串自己
        if(sourceTopicId > 0 && null != s && s.size() > 0 && !s.contains(String.valueOf(sourceTopicId))){
            set = set + "," + sourceTopicId;
        }

        DropAroundDto dto = new DropAroundDto();

        if(sourceTopicId == 0){
            //注册页进来
            setDropaRoundDto(dto ,uid ,set,sourceTopicId);
        }else {
            if(dr <= 5){
                setDropaRoundDto(dto ,uid ,set,sourceTopicId);
            }else {
                //算法取王国
                setDropaRoundDtoAlgorithm(dto ,uid ,set,sourceTopicId);
            };
        }
        //设置轨迹
        TopicDroparoundTrail trail = new TopicDroparoundTrail();
        trail.setCreateTime(new Date());
        trail.setSourceTopicId(sourceTopicId);
        trail.setUid(uid);
        trail.setTargetTopicId(dto.getTopicId());
        liveMybatisDao.createTopicDroparoundTrail(trail);

        return Response.success(dto);
    }

    @Override
    public Response myTopicOpt(long uid, int action, long topicId) {
        TopicUserConfig config = liveMybatisDao.getTopicUserConfig(uid ,topicId);
        if(config != null){
            config.setIsTop(action);
            liveMybatisDao.updateTopicUserConfig(config);
            log.info("update topic_user_config success");
        }else {
            TopicUserConfig topicUserConfig = new TopicUserConfig();
            topicUserConfig.setUid(uid);
            topicUserConfig.setIsTop(action);
            topicUserConfig.setTopicId(topicId);
            liveMybatisDao.insertTopicUserConfig(topicUserConfig);
            log.info("insert topic_user_config success");
        }
        return Response.success(200 ,"操作成功");
    }

    public void setDropaRoundDto(DropAroundDto dto ,long uid ,String set,long sourceTopicId){
        Map<String ,Object> map = Maps.newHashMap();
        map.put("uid",String.valueOf(uid));
        map.put("set",set);
        map.put("minPrice", Integer.parseInt(userService.getAppConfigByKey(Constant.DROPAROUND_PRICE_MIN_KEY)));
        //随机获取一条王国
        TopicDroparound droparound = liveMybatisDao.getRandomDropaRound(map);
        if(droparound == null){
            //没有数据了 算法取
            setDropaRoundDtoAlgorithm(dto ,uid ,set,sourceTopicId);
        }else {
            Topic topic = liveMybatisDao.getTopicById(droparound.getTopicid());
            Content content = contentService.getContentByTopicId(droparound.getTopicid());
            if (topic != null) {
                int status = this.getInternalStatus(topic, uid);
                dto.setInternalStatus(status);
                dto.setTopicType(topic.getType());
            }
            if (content != null) {
                dto.setCid(content.getId());
            }
            dto.setTopicId(droparound.getTopicid());
            cacheService.sadd("list:user@" + uid, String.valueOf(droparound.getTopicid()));
            TopicFragmentTemplate topicFragmentTemplate = liveMybatisDao.getTopicFragmentTemplate();
            if (topicFragmentTemplate != null && !StringUtils.isEmpty(topicFragmentTemplate)) {
                String text = topicFragmentTemplate.getContent();
                String[] temp = text.split("##");
                if(null != temp && temp.length > 0){
                    dto.setTrackContent(temp[0]);
                    if(temp.length > 1 && !StringUtils.isEmpty(temp[1])){
                        dto.setTrackImage(Constant.QINIU_DOMAIN+"/"+temp[1]);
                    }
                }
            }
        }
        log.info("setDropaRoundDto is ok");
    }

    //算法取
    public void setDropaRoundDtoAlgorithm(DropAroundDto dto ,long uid ,String set,long sourceTopicId){
        Map<String ,Object> map = Maps.newHashMap();
        map.put("uid",String.valueOf(uid));
        map.put("set",set);
        map.put("sourceTopicId", sourceTopicId);
        map.put("minPrice", Integer.parseInt(userService.getAppConfigByKey(Constant.DROPAROUND_PRICE_MIN_KEY)));
        Topic topicInfo = liveMybatisDao.getRandomTopicByTag(map);
        if(topicInfo == null){
            //topic王国取完了
            //cacheService.del("list:user@" + uid);
            topicInfo = liveMybatisDao.getRandomDropaRoundAlgorithm(map);
        }
        //随机获取一条王国,
        if(topicInfo == null){
            //topic王国取完了
            cacheService.del("list:user@" + uid);
            topicInfo = liveMybatisDao.getRandomDropaRoundAlgorithm(map);
        }
       
        //Topic topic = liveMybatisDao.getTopicById(topicInfo.getId());		// 不明逻辑。
        Content content = contentService.getContentByTopicId(topicInfo.getId());
        if(topicInfo != null){
            int status = this.getInternalStatus(topicInfo ,uid);
            dto.setInternalStatus(status);
            dto.setTopicType(topicInfo.getType());
        }if(content != null){
            dto.setCid(content.getId());
        }
        dto.setTopicId(topicInfo.getId());
        cacheService.sadd("list:user@"+uid ,String.valueOf(topicInfo.getId()));
        TopicFragmentTemplate topicFragmentTemplate = liveMybatisDao.getTopicFragmentTemplate();
        if(topicFragmentTemplate != null && !StringUtils.isEmpty(topicFragmentTemplate.getContent())){
            String text = topicFragmentTemplate.getContent();
            String[] temp = text.split("##");
            if(null != temp && temp.length > 0){
                dto.setTrackContent(temp[0]);
                if(temp.length > 1 && !StringUtils.isEmpty(temp[1])){
                    dto.setTrackImage(Constant.QINIU_DOMAIN+"/"+temp[1]);
                }
            }
        }
        log.info("setDropaRoundDtoAlgorithm is ok");
    }

    private static final String DEFAULT_KINGDOM_ACTIVITY_CONTENT = "<p style=\"text-align:center;\"><span style=\"font-family:宋体;\"><span style=\"font-size:16px;\">米汤新版本已登场！</span></span></p><p style=\"text-align:center;\"><span style=\"font-family:宋体;\"><span style=\"font-size:16px;\">您目前的米汤版本太低，不升级的话是无法看到帅气新界面的哦。</span></span></p><p style=\"text-align: center;\"><span style=\"font-family:宋体;\"><span style=\"font-size:16px;\"><strong>请及时下载更新至最新版本。</strong></span></span></p>";

    public CreateActivityDto setCreateActivityDto(CreateActivityDto createActivityDto ,Topic topic){
        createActivityDto.setUid(topic.getUid());
        createActivityDto.setIssue("");
        //为了让低版本能看到兼容内容，故这里需将特定的兼容内容放置进来
        createActivityDto.setContent(DEFAULT_KINGDOM_ACTIVITY_CONTENT);
        createActivityDto.setCover(topic.getLiveImage());
        createActivityDto.setTitle(topic.getTitle());
        createActivityDto.setHashTitle("#" + topic.getTitle() + "#");
        try {
            createActivityDto.setStartTime(new Date());
            createActivityDto.setEndTime(DateUtil.string2date("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            log.error("time error");
        };
        createActivityDto.setCid(topic.getId());
        createActivityDto.setType(2);
        return createActivityDto;
    }
    /**
     * 是否在核心圈里面
     * @author zhangjiwei
     * @date May 10, 2017
     * @param uid
     * @param coreCircle
     * @return
     */
    private boolean isInCore(long uid, String coreCircle){
        boolean result = false;
        if(null != coreCircle && !"".equals(coreCircle)){
            JSONArray array = JSON.parseArray(coreCircle);
            for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i).longValue() == uid) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isKing(long uid, long topicUid){
        boolean result = false;
        if(uid == topicUid){
            result = true;
        }
        return result;
    }

    private int genContentType(int oldType, int oldContentType){
        if(oldType == Specification.LiveSpeakType.ANCHOR.index){//主播发言

        }else if(oldType == Specification.LiveSpeakType.FANS.index){//粉丝回复

        }else if(oldType == Specification.LiveSpeakType.FORWARD.index){//转发

        }else if(oldType == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index){//主播贴标
            return 2;
        }else if(oldType == Specification.LiveSpeakType.FANS_WRITE_TAG.index){//粉丝贴标
            return 2;
        }else if(oldType == Specification.LiveSpeakType.LIKES.index){//点赞

        }else if(oldType == Specification.LiveSpeakType.SUBSCRIBED.index){//订阅

        }else if(oldType == Specification.LiveSpeakType.SHARE.index){//分享

        }else if(oldType == Specification.LiveSpeakType.FOLLOW.index){//关注

        }else if(oldType == Specification.LiveSpeakType.INVITED.index){//邀请

        }else if(oldType == Specification.LiveSpeakType.AT.index){//有人@
            return 10;
        }else if(oldType == Specification.LiveSpeakType.ANCHOR_AT.index){//主播@
            return 11;
        }else if(oldType == Specification.LiveSpeakType.VIDEO.index){//视频
            return 62;
        }else if(oldType == Specification.LiveSpeakType.SOUND.index){//语音
            return 63;
        }else if(oldType == Specification.LiveSpeakType.ANCHOR_RED_BAGS.index){//国王收红包

        }else if(oldType == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){//@核心圈
            return 15;
        }else if(oldType == Specification.LiveSpeakType.SYSTEM.index){//系统

        }
        if(oldContentType == 1){
            return 51;
        }
        return oldContentType;
    }

    @Override
    public void statKingdomCountDay(){
        //先删除掉库的记录
        liveMybatisDao.truncateKingdomCountDay();
        //再加上统计记录
        liveMybatisDao.statKingdomCountDay();
    }

    @Override
    public List<TopicFragmentTemplate> getFragmentTplList(String queryStr) {
        List<TopicFragmentTemplate> tpls = liveMybatisDao.getFragmentTplList(queryStr);
        return tpls;
    }

    @Override
    public void addFragmentTpl(TopicFragmentTemplate obj) {
        liveMybatisDao.addFragmentTpl(obj);
    }

    @Override
    public TopicFragmentTemplate getFragmentTplById(Long id) {
        TopicFragmentTemplate tt = liveMybatisDao.getFragmentTplById(id);
        return tt;
    }

    @Override
    public void deleteFragmentTpl(Long msgId) {
        liveMybatisDao.deleteFragmentTplById(msgId);
    }

    @Override
    public void updateFragmentTpl(TopicFragmentTemplate obj) {
        liveMybatisDao.updateFragmentTpl(obj);

    }

    @Override
    public void copyKingdomToDropAroundKingdom(int tropicId,int sort) {
        if(!liveMybatisDao.existsDropAroundKingdom(tropicId)){
            liveMybatisDao.addDropAroundKingdom(tropicId,sort);
        }else{
            log.info("topicId{} exists.",tropicId);
        }
    }

    @Override
    public void delDropAroundKingdom(int tropicId) {
        liveMybatisDao.deleteDropAroundKingdom(tropicId);
    }

    @Override
    public void updateDropAroundKingdom(TopicDroparound td) {
        liveMybatisDao.updateDropAroundKingdom(td);
    }

    @Override
    public PageBean<SearchDropAroundTopicDto> getTopicPage(PageBean page, String searchKeyword) {
        //获取所有更新中直播主笔的信息
        PageBean<Topic> page2 = liveMybatisDao.getTopicPage(page, searchKeyword);
        List<Topic> list = page2.getDataList();
        List<SearchDropAroundTopicDto> showElementList= buildShowTopicList(list);
        page.setDataList(showElementList);
        page.setTotalRecords(page2.getTotalRecords());
        return page;

    }

    private List<SearchDropAroundTopicDto> buildShowTopicList(List<Topic> list){
        List<Long> uidList = new ArrayList<Long>();
        for(Topic topic :list){
            if(!uidList.contains(topic.getUid())){
                uidList.add(topic.getUid());
            }
        }
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        // 合并
        List<SearchDropAroundTopicDto> showElementList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            SearchDropAroundTopicDto ele = new SearchDropAroundTopicDto();
            Topic topic = list.get(i);
            UserProfile profile = profileMap.get(topic.getUid()+"");
            if(profile!=null){
                ele.setNickName(profile.getNickName());
                ele.setUid(profile.getId().intValue());
                ele.setvLv(profile.getvLv());
            }
            ele.setTitle(topic.getTitle());
            ele.setTopicId(topic.getId().intValue());
            showElementList.add(ele);
        }
        return showElementList;
    }

    @Override
    public PageBean<SearchDropAroundTopicDto> getDropAroundKingdomPage(PageBean page,String queryStr) {
        return liveMybatisDao.getDropAroundKingdomPage(page,queryStr);
    }

    @Override
    public PageBean<SearchTopicDto> getTopicPage(PageBean page, Map<String, Object> params) {
        if(params.containsKey("favoriteCount_min")){		// 成员数筛选，小朱说=favoriteCount-1
            params.put("favoriteCount_min",Integer.parseInt((String) params.get("favoriteCount_min"))-1);
        }
        if(params.containsKey("favoriteCount_max")){
            params.put("favoriteCount_max", Integer.parseInt((String) params.get("favoriteCount_max"))-1);
        }
        return liveMybatisDao.getTopicPage(page,params);
    }

    @Override
    public Response topicTags(long uid, long topicId){
        ShowTopicTagsDTO resultDTO = new ShowTopicTagsDTO();
        //获取王国本身的王国
        resultDTO.setTopicTags("");
        if(topicId > 0){
            List<TopicTagDetail> tagList = liveMybatisDao.getTopicTagDetailsByTopicId(topicId);
            if(null != tagList && tagList.size() > 0){
                StringBuilder topicTags = new StringBuilder();
                TopicTagDetail ttd = null;
                for(int i=0;i<tagList.size();i++){
                    ttd = tagList.get(i);
                    if(i>0){
                        topicTags.append(";");
                    }
                    topicTags.append(ttd.getTag());
                }
                resultDTO.setTopicTags(topicTags.toString());
            }
        }
        //获取我常用的标签
        resultDTO.setMyUsedTags("");
        List<Map<String, Object>> myTags = liveLocalJdbcDao.getMyTopicTags(uid, 20);
        if(null != myTags && myTags.size() > 0){
            StringBuilder myTopicTags = new StringBuilder();
            Map<String, Object> t = null;
            for(int i=0;i<myTags.size();i++){
                t = myTags.get(i);
                if(i>0){
                    myTopicTags.append(";");
                }
                myTopicTags.append((String)t.get("tag"));
            }
            resultDTO.setMyUsedTags(myTopicTags.toString());
        }
        //获取推荐的标签
        //2.2.3版本暂时先只返回运营推荐的标签
        boolean isAdmin = userService.isAdmin(uid);
        List<Map<String, Object>> recTags = liveLocalJdbcDao.getRecTopicTags(isAdmin, 10);
        if(null != recTags && recTags.size() > 0){
            ShowTopicTagsDTO.TagElement e = null;
            for(Map<String, Object> m : recTags){
                e = new ShowTopicTagsDTO.TagElement();
                e.setTag((String)m.get("tag"));
                e.setTopicCount((Long)m.get("kcount"));
                resultDTO.getRecTags().add(e);
            }
        }

        return Response.success(resultDTO);
    }

    @Override
    public Response topicTagsModify(long uid, long topicId, String tags){
        //判断操作权限，这个操作只有国王、核心圈，以及管理员可以操作
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(null == topic){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
        }
        boolean isAdmin = userService.isAdmin(uid);
        if(!this.isKing(uid, topic.getUid()) && !this.isInCore(uid, topic.getCoreCircle())
                && !isAdmin){
            return Response.failure(ResponseStatus.YOU_DO_NOT_HAVE_PERMISSION.status,ResponseStatus.YOU_DO_NOT_HAVE_PERMISSION.message);
        }

        List<String> newTagList = new ArrayList<String>();
        if(!StringUtils.isEmpty(tags)){
            String[] tmp = tags.split(";");
            for(String s : tmp){
                s = s.trim();
                if(!StringUtils.isEmpty(s)){
                    if(!newTagList.contains(s)){
                        newTagList.add(s);
                    }
                }
            }
        }

        List<TopicTagDetail> tagList = liveMybatisDao.getTopicTagDetailsByTopicId(topicId);

        //先将需要删除的删除掉
        if(null != tagList && tagList.size() > 0){
            for(TopicTagDetail ttd : tagList){
                if(newTagList.contains(ttd.getTag())){//已经有了，则不用重新保存一遍
                    newTagList.remove(ttd.getTag());
                }else{//没有的，则需要删除掉
                    ttd.setStatus(1);
                    liveMybatisDao.updateTopicTagDetail(ttd);
                }
            }
        }

        //剩下的需要保存到库里面
        if(newTagList.size() > 0){
            TopicTagDetail tagDetail = null;
            TopicTag topicTag = null;
            for(String tag : newTagList){
                //非管理员不能打带“官方”二字的标签
                if(!isAdmin){
                    if(tag.contains("官方")){
                        continue;
                    }
                }
                topicTag = liveMybatisDao.getTopicTagByTag(tag);
                if(null == topicTag){
                    topicTag = new TopicTag();
                    topicTag.setTag(tag);
                    liveMybatisDao.insertTopicTag(topicTag);
                }

                tagDetail = new TopicTagDetail();
                tagDetail.setTag(tag);
                tagDetail.setTagId(topicTag.getId());
                tagDetail.setTopicId(topicId);
                tagDetail.setUid(uid);
                liveMybatisDao.insertTopicTagDetail(tagDetail);
            }
        }

        return Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message);
    }

    @Override
    public Response topicTagCheck(long uid, String tag){
        if(null != tag && !"".equals(tag.trim())){
            tag = tag.trim();
            //如果是非管理员，则标签中不能出现tag
            if(!userService.isAdmin(uid)){
                if(tag.contains("官方")){
                    return Response.failure(ResponseStatus.TAG_HAS_BEEN_FORBIDDEN.status,ResponseStatus.TAG_HAS_BEEN_FORBIDDEN.message);
                }
            }

            TopicTag topicTag = liveMybatisDao.getTopicTagByTag(tag);
            if(null != topicTag && topicTag.getStatus() == 1){
                return Response.failure(ResponseStatus.TAG_HAS_BEEN_FORBIDDEN.status,ResponseStatus.TAG_HAS_BEEN_FORBIDDEN.message);
            }
        }

        return Response.success(ResponseStatus.OPERATION_SUCCESS.status, "正常");
    }

    @Override
    public Response tagKingdoms(String tag, long sinceId, long currentUid){
        ShowTagKingdomsDTO resultDTO = new ShowTagKingdomsDTO();
        resultDTO.setIsForbidden(0);
        //这里需要查询拥有该标签的王国，以及关联推荐的王国，目前就将拥有该标签的王国查询出来
        if(null == tag || "".equals(tag.trim())){
            return Response.success(resultDTO);
        }
        boolean isFirst = false;
        if(sinceId == -1){
            isFirst = true;
            sinceId = Long.MAX_VALUE;
        }
        //第一次需要判断该标签是否正常
        if(isFirst){
            TopicTag topicTag = liveMybatisDao.getTopicTagByTag(tag.trim());
            if(null != topicTag && topicTag.getStatus() == 1){
                resultDTO.setIsForbidden(1);
                return Response.success(resultDTO);
            }
        }

        List<Long> blacklistUids = liveLocalJdbcDao.getBlacklist(currentUid);
        
        List<Map<String, Object>> topicList = liveLocalJdbcDao.getTagKingdomListByTag(tag.trim(), sinceId, 10, blacklistUids);
        if(null == topicList || topicList.size() == 0){
            return Response.success(resultDTO);
        }

        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> ceTopicIdList = new ArrayList<Long>();
        List<Long> uidList = new ArrayList<Long>();
        for(Map<String, Object> m : topicList){
            topicIdList.add((Long)m.get("id"));
            uidList.add((Long)m.get("uid"));
            if(((Integer)m.get("type")).intValue() == 1000){
                ceTopicIdList.add((Long)m.get("id"));
            }
        }

        //一次性查询用户属性
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(topicIdList);
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
            }
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
            }
        }
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveLocalJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTopicIdList.size() > 0){
            List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTopicIdList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
        List<TopicTagDetail> topicTagList = liveMybatisDao.getTopicTagDetailListByTopicIds(topicIdList);
        if(null != topicTagList && topicTagList.size() > 0){
            long topicId = 0;
            String tags = null;
            for(TopicTagDetail ttd : topicTagList){
                if(ttd.getTopicId().longValue() != topicId){
                    //先插入上一次
                    if(topicId > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(topicId), tags);
                    }
                    //再初始化新的
                    topicId = ttd.getTopicId().longValue();
                    tags = null;
                }
                if(tags != null){
                    tags = tags + ";" + ttd.getTag();
                }else{
                    tags = ttd.getTag();
                }
            }
            if(topicId > 0 && !StringUtils.isEmpty(tags)){
                topicTagMap.put(String.valueOf(topicId), tags);
            }
        }

        ShowTagKingdomsDTO.KingdomElement e = null;
        UserProfile userProfile = null;
        Content content = null;
        Long kingUid = null;
        Long topicId = null;
        for(Map<String, Object> topic : topicList){
            kingUid = (Long)topic.get("uid");
            topicId = (Long)topic.get("id");
            e = new ShowTagKingdomsDTO.KingdomElement();
            userProfile = profileMap.get(kingUid.toString());
            if(null == userProfile){
                continue;
            }
            content = contentMap.get(topicId.toString());
            if(null == content){
                continue;
            }
            e.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            e.setNickName(userProfile.getNickName());
            e.setUid(kingUid);
            e.setV_lv(userProfile.getvLv());
            e.setLevel(userProfile.getLevel());
            if(null != followMap.get(currentUid+"_"+kingUid.longValue())){
                e.setIsFollowed(1);
            }else{
                e.setIsFollowed(0);
            }
            if(null != followMap.get(kingUid.longValue()+"_"+currentUid)){
                e.setIsFollowMe(1);
            }else{
                e.setIsFollowMe(0);
            }
            e.setSinceId((Long)topic.get("long_time"));
            e.setTopicId(topicId);
            e.setTitle((String)topic.get("title"));
            e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
            e.setCreateTime(((Date)topic.get("create_time")).getTime());
            e.setLastUpdateTime((Long)topic.get("long_time"));
            e.setUpdateTime((Long)topic.get("long_time"));
            e.setType(3);//默认王国3
            e.setContentType((Integer)topic.get("type"));
            e.setInternalStatus(this.getUserInternalStatus((String)topic.get("core_circle"), currentUid));
            if(null != topicCountMap.get(topicId.toString())){
                e.setTopicCount(topicCountMap.get(topicId.toString()).longValue());
            }else{
                e.setTopicCount(0);
            }
            if(null != reviewCountMap.get(topicId.toString())){
                e.setReviewCount(reviewCountMap.get(topicId.toString()).longValue());
            }else{
                e.setReviewCount(0);
            }
            e.setCid(content.getId());
            e.setLikeCount(content.getLikeCount());
            e.setReadCount(content.getReadCountDummy());
            if(null != liveFavoriteMap.get(topicId.toString())){
                e.setFavorite(1);
            }else{
                e.setFavorite(0);
            }
            if(null != topicMemberCountMap.get(topicId.toString())){
                e.setFavoriteCount(topicMemberCountMap.get(topicId.toString()).intValue()+1);
            }else{
                e.setFavoriteCount(1);
            }
            if(e.getContentType() == 1000){
                if(null != acCountMap.get(topicId.toString())){
                    e.setAcCount(acCountMap.get(topicId.toString()).longValue());
                }else{
                    e.setAcCount(0);
                }
            }
            if(null != topicTagMap.get(topicId.toString())){
                e.setTags(topicTagMap.get(topicId.toString()));
            }else{
                e.setTags("");
            }
            resultDTO.getKingdomList().add(e);
        }

        return Response.success(resultDTO);
    }

    @Override
    public Response recQuery(long topicId, long sinceId, long currentUid){
        ShowRecQueryDTO resultDTO = new ShowRecQueryDTO();

        if(sinceId < 0){
            sinceId = Long.MAX_VALUE;
        }
        List<Map<String, Object>> topicList = null;
        if(topicId > 0){
            topicList = liveLocalJdbcDao.getRecTopicByTag(topicId, sinceId, 20, -1);
        }
        if(null == topicList || topicList.size() == 0){
            return Response.success(resultDTO);
        }

        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> ceTopicIdList = new ArrayList<Long>();
        List<Long> uidList = new ArrayList<Long>();
        for(Map<String, Object> m : topicList){
            topicIdList.add((Long)m.get("id"));
            uidList.add((Long)m.get("uid"));
            if(((Integer)m.get("type")).intValue() == 1000){
                ceTopicIdList.add((Long)m.get("id"));
            }
        }

        //一次性查询用户属性
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveLocalJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        //一次性查询所有topic对应的content
        Map<String, Content> contentMap = new HashMap<String, Content>();
        List<Content> contentList = contentService.getContentsByTopicIds(topicIdList);
        if(null != contentList && contentList.size() > 0){
            for(Content c : contentList){
                contentMap.put(String.valueOf(c.getForwardCid()), c);
            }
        }
        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, LiveFavorite> liveFavoriteMap = new HashMap<String, LiveFavorite>();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(LiveFavorite lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.getTopicId()), lf);
            }
        }
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveLocalJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTopicIdList.size() > 0){
            List<Map<String,Object>> acCountList = liveLocalJdbcDao.getTopicAggregationAcCountByTopicIds(ceTopicIdList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
        List<TopicTagDetail> topicTagList = liveMybatisDao.getTopicTagDetailListByTopicIds(topicIdList);
        if(null != topicTagList && topicTagList.size() > 0){
            long tid = 0;
            String tags = null;
            for(TopicTagDetail ttd : topicTagList){
                if(ttd.getTopicId().longValue() != tid){
                    //先插入上一次
                    if(tid > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(tid), tags);
                    }
                    //再初始化新的
                    tid = ttd.getTopicId().longValue();
                    tags = null;
                }
                if(tags != null){
                    tags = tags + ";" + ttd.getTag();
                }else{
                    tags = ttd.getTag();
                }
            }
            if(tid > 0 && !StringUtils.isEmpty(tags)){
                topicTagMap.put(String.valueOf(tid), tags);
            }
        }

        ShowRecQueryDTO.KingdomElement e = null;
        UserProfile userProfile = null;
        Content content = null;
        Long kingUid = null;
        Long tid = null;
        for(Map<String, Object> topic : topicList){
            kingUid = (Long)topic.get("uid");
            tid = (Long)topic.get("id");
            e = new ShowRecQueryDTO.KingdomElement();
            userProfile = profileMap.get(kingUid.toString());
            if(null == userProfile){
                continue;
            }
            content = contentMap.get(tid.toString());
            if(null == content){
                continue;
            }
            e.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            e.setNickName(userProfile.getNickName());
            e.setUid(kingUid);
            e.setV_lv(userProfile.getvLv());
            e.setLevel(userProfile.getLevel());
            if(null != followMap.get(currentUid+"_"+kingUid.longValue())){
                e.setIsFollowed(1);
            }else{
                e.setIsFollowed(0);
            }
            if(null != followMap.get(kingUid.longValue()+"_"+currentUid)){
                e.setIsFollowMe(1);
            }else{
                e.setIsFollowMe(0);
            }
            e.setSinceId((Long)topic.get("long_time"));
            e.setTopicId(tid);
            e.setTitle((String)topic.get("title"));
            e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
            e.setCreateTime(((Date)topic.get("create_time")).getTime());
            e.setLastUpdateTime((Long)topic.get("long_time"));
            e.setUpdateTime((Long)topic.get("long_time"));
            e.setType(3);//默认王国3
            e.setContentType((Integer)topic.get("type"));
            e.setInternalStatus(this.getUserInternalStatus((String)topic.get("core_circle"), currentUid));
            if(null != topicCountMap.get(tid.toString())){
                e.setTopicCount(topicCountMap.get(tid.toString()).longValue());
            }else{
                e.setTopicCount(0);
            }
            if(null != reviewCountMap.get(tid.toString())){
                e.setReviewCount(reviewCountMap.get(tid.toString()).longValue());
            }else{
                e.setReviewCount(0);
            }
            e.setCid(content.getId());
            e.setLikeCount(content.getLikeCount());
            e.setReadCount(content.getReadCountDummy());
            if(null != liveFavoriteMap.get(tid.toString())){
                e.setFavorite(1);
            }else{
                e.setFavorite(0);
            }
            if(null != topicMemberCountMap.get(tid.toString())){
                e.setFavoriteCount(topicMemberCountMap.get(tid.toString()).intValue()+1);
            }else{
                e.setFavoriteCount(1);
            }
            if(e.getContentType() == 1000){
                if(null != acCountMap.get(tid.toString())){
                    e.setAcCount(acCountMap.get(tid.toString()).longValue());
                }else{
                    e.setAcCount(0);
                }
            }
            if(null != topicTagMap.get(tid.toString())){
                e.setTags(topicTagMap.get(tid.toString()));
            }else{
                e.setTags("");
            }
            resultDTO.getResult().add(e);
        }

        return Response.success(resultDTO);
    }

    @Override
    public TopicTag getTopicTagById(long id){
        return liveMybatisDao.getTopicTagById(id);
    }

    @Override
    public TopicTag getTopicTagByTag(String tag){
        return liveMybatisDao.getTopicTagByTag(tag);
    }

    @Override
    public Long createTopicTag(TopicTag tag){
        liveMybatisDao.insertTopicTag(tag);
        
        return tag.getId();
    }

    @Override
    public void updateTopicTag(TopicTag tag){
        liveMybatisDao.updateTopicTag(tag);
    }

    @Override
    public void delTagTopic(long tagTopicId){
        TopicTagDetail ttd = liveMybatisDao.getTopicTagDetailById(tagTopicId);
        if(null != ttd){
            ttd.setStatus(1);
            liveMybatisDao.updateTopicTagDetail(ttd);
        }
    }

    @Override
    public void addTagTopics(long tagId, List<Long> topicIdList){
        if(null == topicIdList || topicIdList.size() == 0 || tagId == 0){
            return;
        }
        TopicTag tag = liveMybatisDao.getTopicTagById(tagId);
        if(null == tag){
            return;
        }

        List<TopicTagDetail> oldList = liveMybatisDao.getTopicTagDetailsByTagId(tagId);
        List<Long> oldTopicIdList = new ArrayList<Long>();
        if(null != oldList && oldList.size() > 0){
            for(TopicTagDetail ttd : oldList){
                oldTopicIdList.add(ttd.getTopicId());
            }
        }
        TopicTagDetail ttd = null;
        for(Long topicId : topicIdList){
            if(oldTopicIdList.contains(topicId)){
                continue;//已经有了，就不要重复添加了
            }
            ttd = new TopicTagDetail();
            ttd.setStatus(0);
            ttd.setTag(tag.getTag());
            ttd.setTagId(tagId);
            ttd.setTopicId(topicId);
            ttd.setUid(Long.valueOf(0));
            liveMybatisDao.insertTopicTagDetail(ttd);
        }
    }
    @Override
    public PageBean<TeaseInfo> getTeaseInfoPage(PageBean<TeaseInfo> page, Map<String, Object> conditions) {
        return liveMybatisDao.getTeaseInfoPage(page, conditions);
    }
    @Override
    public void updateTeaseInfoByKey(TeaseInfo teaseInfo) {
        liveMybatisDao.updateTeaseInfoByKey(teaseInfo);
    }
    @Override
    public Integer addTeaseInfo(TeaseInfo teaseInfo) {
        return liveMybatisDao.addTeaseInfo(teaseInfo);
    }
    @Override
    public TeaseInfo getTeaseInfoByKey(Long id) {
        return liveMybatisDao.getTeaseInfoByKey(id);
    }

    @Override
    public Response teaseListQuery() {
        TeaseInfoDto dto = new TeaseInfoDto();
        List<TeaseInfo> teaseInfoList = liveMybatisDao.teaseListQuery();
        TeaseInfoDto.TeaseElement	data = null;
        for(TeaseInfo teaseInfo:teaseInfoList){
            data= new TeaseInfoDto.TeaseElement();
            data.setId(teaseInfo.getId());
            data.setName(teaseInfo.getName());
            data.setImage(Constant.QINIU_DOMAIN + "/" + teaseInfo.getImage());
            data.setAudio(Constant.QINIU_DOMAIN + "/" + teaseInfo.getAudio());
            data.setExtra(teaseInfo.getExtra());
            dto.getTeaseData().add(data);
        }
        return Response.success(dto);
    }
    @Override
    public Response createVote(CreateVoteDto dto) {
        UserProfile userProfile =  userService.getUserProfileByUid(dto.getUid());
        if(userProfile==null){
            return Response.failure(500,"用户不存在");
        }
        int max = 1;
        if(userProfile.getvLv()==1){
            max = 5;
            String count = cacheService.get(V_CREATE_VOTE_COUNT);
            if(!StringUtils.isEmpty(count)){
                max = Integer.valueOf(count);
            }
        }else{
            max = 1;
            String count = cacheService.get(NORMAL_CREATE_VOTE_COUNT);
            if(!StringUtils.isEmpty(count)){
                max = Integer.valueOf(count);
            }
        }
        int voteCount = liveMybatisDao.getVoteInfoCount(dto.getUid());
        if(voteCount>=max){
            return Response.failure(500,"今天发起投票次数已经超过限制！");
        }
        VoteInfo voteInfo = new VoteInfo();
        voteInfo.setTopicid(dto.getTopicId());
        voteInfo.setTitle(dto.getTitle());
        voteInfo.setType(dto.getType());
        voteInfo.setUid(dto.getUid());
        voteInfo.setStatus(1);
        liveMybatisDao.addVoteInfo(voteInfo);
        String[] options = dto.getOption().split(";");
        for (int i = 0; i < options.length; i++) {
            VoteOption vo = new VoteOption();
            vo.setVoteid(voteInfo.getId());
            vo.setOptionname(options[i]);
            liveMybatisDao.addVoteOption(vo);
        }
        JSONObject json = new JSONObject();
        json.put("type", "vote");
        json.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
        json.put("title", dto.getTitle());
        json.put("id", voteInfo.getId());
        SpeakDto speakDto = new SpeakDto();
        speakDto.setType(52);
        speakDto.setContentType(19);
        speakDto.setUid(dto.getUid());
        speakDto.setTopicId(dto.getTopicId());
        speakDto.setSource(dto.getSource());
        speakDto.setExtra(json.toJSONString());
        speak(speakDto);
        CreateVoteResponeDto cvd = new CreateVoteResponeDto();
        cvd.setVoteId(voteInfo.getId());
        cvd.setFragmentId(speakDto.getFragmentId());
        cvd.setScore(speakDto.getScore());
        return Response.success(ResponseStatus.CREATE_VOTE_SUCCESS.status, ResponseStatus.CREATE_VOTE_SUCCESS.message,cvd);
    }
    @Override
    public Response vote(long uid,long voteId,String optionId) {
        int count = liveMybatisDao.getVoteRecordCountByUidAndVoteId(uid,voteId);
        if(count>0){
            return Response.failure(500,"您已经投过票了！");
        }
        VoteInfo voteInfo  =liveMybatisDao.getVoteInfoByKey(voteId);
        if(voteInfo==null){
            return Response.failure(500,"没有找到该投票！");
        }
        String[] optionIdStr = optionId.split(";");
        if(voteInfo.getType()==0 &&optionIdStr.length>1){
            return Response.failure(500,"该投票只能单选！");
        }
        for (int i = 0; i < optionIdStr.length; i++) {
            VoteRecord vr = new VoteRecord();
            vr.setUid(uid);
            vr.setOptionid(Long.valueOf(optionIdStr[i]));
            vr.setVoteid(voteId);
            liveMybatisDao.addVoteRecord(vr);
        }
        return Response.success(ResponseStatus.VOTE_SUCCESS.status, ResponseStatus.VOTE_SUCCESS.message);
    }
    @Override
    public Response endVote(long voteId,long uid) {
        VoteInfo voteInfo  =liveMybatisDao.getVoteInfoByKey(voteId);
        if(voteInfo==null){
            return Response.failure(500,"没有找到该投票！");
        }
        Topic topic = liveMybatisDao.getTopicById(voteInfo.getTopicid());
        if(topic==null){
            return Response.failure(500,"没有找到该投票王国！");
        }
        if(topic.getUid().longValue() != uid && voteInfo.getUid().longValue() != uid ){
            return Response.failure(500,"您没有结束投票权限！");
        }
        VoteInfo updateVoteInfo = new VoteInfo();
        updateVoteInfo.setId(voteId);
        updateVoteInfo.setStatus(Specification.VoteStatus.END.index);
        liveMybatisDao.updateVoteInfo(updateVoteInfo);
        return Response.success(ResponseStatus.END_VOTE_SUCCESS.status, ResponseStatus.END_VOTE_SUCCESS.message);
    }

    @Override
    public Response resendVote(long fragmentId,long uid) {
        TopicFragment tf  =liveMybatisDao.getTopicFragmentById(fragmentId);
        Topic tp  =liveMybatisDao.getTopicById(tf.getTopicId());
        int internalStatus = getInternalStatus(tp,uid);
        if(tp.getUid().longValue() != uid && internalStatus!=Specification.SnsCircle.CORE.index){
            return Response.failure(500,"您没有重新发送投票权限！");
        }
        tf.setId(null);
        tf.setCreateTime(new Date());
        liveMybatisDao.createTopicFragment(tf);
        liveMybatisDao.deleteFragmentByIdForPhysics(fragmentId);
        ResendVoteDto rv = new ResendVoteDto();
        rv.setFragmentId(tf.getId());
        return Response.success(ResponseStatus.RESEND_VOTE_SUCCESS.status, ResponseStatus.RESEND_VOTE_SUCCESS.message,rv);
    }

    @Override
    public Response getTopicVoteInfo(long voteId) {
        VoteInfo voteInfo =liveMybatisDao.getVoteInfoByKey(voteId);
        if(voteInfo==null){
            return Response.failure(500,"没有找到该投票！");
        }
        TopicVoteInfoDto dto = new TopicVoteInfoDto();
        dto.setVoteId(voteInfo.getId());
        dto.setTitle(voteInfo.getTitle());
        dto.setType(voteInfo.getType());
        dto.setStatus(voteInfo.getStatus());
        List<VoteOption> optionList = liveMybatisDao.getVoteOptionList(voteId);
        for (int i = 0; i < optionList.size(); i++) {
            TopicVoteInfoDto.OptionElement data= TopicVoteInfoDto.createElement();
            VoteOption vo = optionList.get(i);
            data.setId(vo.getId());
            data.setOption(vo.getOptionname());
            int count = liveMybatisDao.getVoteRecordCountByOptionId(vo.getId());
            data.setCount(count);
            dto.getOptions().add(data);
        }
        return Response.success(dto);
    }

    @Override
    public Response getVoteInfo(long voteId,long uid) {
        VoteInfo voteInfo =liveMybatisDao.getVoteInfoByKey(voteId);
        if(voteInfo==null){
            return Response.failure(500,"没有找到该投票！");
        }
        UserProfile user =userService.getUserProfileByUid(voteInfo.getUid());
        VoteInfoDto dto = new VoteInfoDto();
        dto.setUid(user.getUid());
        dto.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
        dto.setNickName(user.getNickName());
        dto.setV_lv(user.getvLv());
        dto.setLevel(user.getLevel());
        StringBuffer myVote = new StringBuffer();
        List<VoteRecord> vrList =liveMybatisDao.getMyVoteRecord(uid,voteId);
        for (int i = 0; i < vrList.size(); i++) {
            if(i!=0){
                myVote.append(";");
            }
            VoteRecord vr = vrList.get(i);
            myVote.append(String.valueOf(vr.getOptionid()));
        }
        dto.setMyVote(myVote.toString());
        dto.setVoteId(voteId);
        dto.setTitle(voteInfo.getTitle());
        dto.setType(voteInfo.getType());
        dto.setStatus(voteInfo.getStatus());
        Topic topic = liveMybatisDao.getTopicById(voteInfo.getTopicid());
        if(topic==null){
            return Response.failure(500,"没有找到该投票王国！");
        }
        if(voteInfo.getUid().longValue() == uid ){
            dto.setCanEnd(1);
        }else{
            dto.setCanEnd(0);
        }
        int recordCount = liveMybatisDao.getVoteRecordCountByVoteId(voteId);
        dto.setRecordCount(recordCount);
        dto.setCreateTime(voteInfo.getCreateTime());
        List<VoteOption> optionList = liveMybatisDao.getVoteOptionList(voteId);
        for (int i = 0; i < optionList.size(); i++) {
            VoteInfoDto.OptionElement data= VoteInfoDto.createOptionElement();
            VoteOption vo = optionList.get(i);
            data.setId(vo.getId());
            data.setOption(vo.getOptionname());
            int count = liveMybatisDao.getVoteRecordCountByOptionId(vo.getId());
            data.setCount(count);
            dto.getOptions().add(data);
        }
        List<Map<String, Object>> usersList = liveLocalJdbcDao.getVoteUserProfileByVoteId(voteId);
        for (int i = 0; i < usersList.size(); i++) {
            VoteInfoDto.UserElement data= VoteInfoDto.createUserElement();
            Map<String, Object> map = usersList.get(i);
            data.setUid(Long.valueOf(map.get("uid").toString()));
            data.setAvatar(Constant.QINIU_DOMAIN + "/" + map.get("avatar").toString());
            data.setNickName(map.get("nick_name").toString());
            data.setV_lv(Integer.valueOf(map.get("v_lv").toString()));
            dto.getUsers().add(data);
        }
        return Response.success(dto);
    }
    // 按月加载图库，for ios.
    private Response kingdomImgDB2Month(long topicId, int direction, long sinceId){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Topic topic = this.getTopicById(topicId);
        // 取当前月数据。
        String month = null;
        String coverMonth = sdf.format(topic.getCreateTime());
        if(sinceId==-1 && direction ==2){		// 点击封面进来，加载本王国第一张图片所在月份。liveMybatisDao.getNextMonthByImgFragment(topicId,sinceId,true);
            month= coverMonth;
        }else{
            TopicFragment curTF = liveMybatisDao.getTopicFragmentById(sinceId);//topicFragmentMapper.selectByPrimaryKey(sinceId);

            if(direction==2){
                month = sdf.format(curTF.getCreateTime());
            }else if(direction==0){		// 向下
                month= liveMybatisDao.getNextMonthByImgFragment(topicId,sinceId,true);
            }else if(direction==1){		// 向上
                month= liveMybatisDao.getNextMonthByImgFragment(topicId,sinceId,false);
            }
        }
        KingdomImgDB imgDb = new KingdomImgDB();
        List<KingdomImgDB.ImgData> imgDataList = new ArrayList<>();

        if (month != null) {
            imgDb.setTopMonth(month);
            if(month.equals(coverMonth)){	// 点封面进来时，默认加上封面。
                KingdomImgDB.ImgData imgData = new KingdomImgDB.ImgData();
                String fragmentImage = Constant.QINIU_DOMAIN + "/"  + topic.getLiveImage();
                imgData.setFragmentImage(fragmentImage);
                imgData.setFragmentId(-1);
                imgData.setCreateTime(topic.getCreateTime().getTime());
                imgDataList.add(imgData);
            }

            List<TopicFragment> fragmentList = liveMybatisDao.getImgFragmentByMonth(topicId, month);
            for (TopicFragment fg : fragmentList) {
                KingdomImgDB.ImgData imgData = new KingdomImgDB.ImgData();
                String fragmentImage = Constant.QINIU_DOMAIN + "/" +  fg.getFragmentImage();
                imgData.setFragmentImage(fragmentImage);
                imgData.setFragmentId(fg.getId());
                imgData.setContentType(fg.getContentType());
                imgData.setCreateTime(fg.getCreateTime().getTime());
                imgData.setExtra(fg.getExtra());
                imgData.setFragment(fg.getFragment());
                imgData.setType(fg.getType());
                imgDataList.add(imgData);
            }

        }else{	// 加入王国封面，当向上加载时没有更多数据，显示封面。		不再重新加载封面。
            if(direction==1){
                imgDb.setTopMonth(coverMonth);

                KingdomImgDB.ImgData imgData = new KingdomImgDB.ImgData();
                String fragmentImage = Constant.QINIU_DOMAIN + "/"  + topic.getLiveImage();
                imgData.setFragmentImage(fragmentImage);
                imgData.setFragmentId(-1);
                imgData.setCreateTime(topic.getCreateTime().getTime());
                imgDataList.add(imgData);

            }
        }

        imgDb.setImgData(imgDataList);
        imgDb.setTopMonthDataSize(imgDataList.size());
        return Response.success(imgDb);
    }
    @Override
    public Response kingdomImgDB(long topicId, int direction, long fragmentId,int type) {

        if(type==1){
            return kingdomImgDB2Month( topicId,  direction, fragmentId);
        }

        final int pageSize= 200;
        KingdomImgDB imgDb = new KingdomImgDB();
        List<TopicFragment> fragmentList=new ArrayList<>();
        if(direction==0){		//向下
            fragmentList = liveMybatisDao.getTopicImgFragment(topicId, fragmentId, true, pageSize);
        }else if(direction ==1){		// 向上
            fragmentList = liveMybatisDao.getTopicImgFragment(topicId, fragmentId, false, pageSize);
            // 计算最后一条所在月份的总数量
            if(fragmentList.size()>0){
                String month =DateUtil.date2string(fragmentList.get(0).getCreateTime(),"yyyy-MM");
                imgDb.setTopMonth(month);
                long monthDataSize = liveLocalJdbcDao.countTopicImgByMonth(topicId, month);
                imgDb.setTopMonthDataSize(monthDataSize);
            }
        }else{		// 中间
            List<TopicFragment> fgUp = liveMybatisDao.getTopicImgFragment(topicId, fragmentId, false, pageSize/2);		// 上100
            List<TopicFragment> fgDown = liveMybatisDao.getTopicImgFragment(topicId, fragmentId, true, pageSize/2);		// 上100
            if(fgUp.size()>0){
                String month =DateUtil.date2string(fgUp.get(0).getCreateTime(),"yyyy-MM");
                imgDb.setTopMonth(month);
                long monthDataSize = liveLocalJdbcDao.countTopicImgByMonth(topicId, month);
                imgDb.setTopMonthDataSize(monthDataSize);
            }
            fragmentList.addAll(fgUp);
            fragmentList.add(liveMybatisDao.getTopicFragmentById(fragmentId));
            fragmentList.addAll(fgDown);
        }

        List<KingdomImgDB.ImgData> imgDataList = new ArrayList<>();
        for(TopicFragment fg:fragmentList){
            KingdomImgDB.ImgData imgData= new KingdomImgDB.ImgData();
            String fragmentImage = Constant.QINIU_DOMAIN + "/" +fg.getFragmentImage();
            imgData.setFragmentImage(fragmentImage);
            imgData.setFragmentId(fg.getId());
            imgData.setContentType(fg.getContentType());
            imgData.setCreateTime(fg.getCreateTime().getTime());
            imgData.setExtra(fg.getExtra());
            imgData.setFragment(fg.getFragment());
            imgData.setType(fg.getType());
            imgDataList.add(imgData);
        }
        imgDb.setImgData(imgDataList);
        return Response.success(imgDb);
    }

    @Override
    public Response blockUserKingdom(long topicId,long uid) {
        liveMybatisDao.blockUserKingdom(topicId,uid);
        return Response.success();
    }

    @Override
    public List<Topic> getTopicListByIds(List<Long> ids){
        if(null == ids && ids.size() == 0){
            return null;
        }
        return liveMybatisDao.getTopicsByIds(ids);
    }

    @Override
    public Response userAtList(UserAtListDTO atListDTO){
        Topic topic = liveMybatisDao.getTopicById(atListDTO.getTopicId());
        if(null == topic){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status, ResponseStatus.LIVE_HAS_DELETED.message);
        }
        List<Long> coreUidList = new ArrayList<Long>();
        coreUidList.add(topic.getUid());//国王肯定是核心圈
        if(!StringUtils.isEmpty(topic.getCoreCircle())){
            JSONArray array = JSON.parseArray(topic.getCoreCircle());
            Long uid = null;
            for (int i = 0; i < array.size(); i++) {
                uid = array.getLong(i);
                if(!coreUidList.contains(uid)){
                    coreUidList.add(uid);
                }
            }
        }

        ShowUserAtListDTO result = new ShowUserAtListDTO();
        List<Map<String, Object>> uList = null;
        if(StringUtils.isEmpty(atListDTO.getKeyword())){//王国内检索
            int page = atListDTO.getPage();
            if(page < 1){
                page = 1;
            }
            int pageSize = 20;
            int start = (page-1)*pageSize;

            int totalCount = liveLocalJdbcDao.countUserAtListInTopic(atListDTO.getTopicId(), coreUidList, atListDTO.getUid());
            int totalPage = totalCount%pageSize==0?(totalCount/pageSize):(totalCount/pageSize)+1;
            result.setTotalPage(totalPage);

            uList = liveLocalJdbcDao.getUserAtListInTopic(atListDTO.getTopicId(), start, pageSize, coreUidList, atListDTO.getUid());
        }else{//全站检索
            uList = searchService.topicAtUserList(atListDTO.getKeyword(), atListDTO.getUid());
            if(null != uList && uList.size() > 0){
                result.setTotalPage(1);
            }else{
                result.setTotalPage(0);
            }
        }

        if(null != uList && uList.size() > 0){
            ShowUserAtListDTO.UserElement e = null;
            Long uid = null;
            for(Map<String, Object> u : uList){
                e = new ShowUserAtListDTO.UserElement();
                uid = (Long)u.get("uid");
                e.setUid(uid);
                e.setNickName((String)u.get("nick_name"));
                e.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)u.get("avatar"));
                e.setV_lv((Integer)u.get("v_lv"));
                e.setLevel((Integer) u.get("level"));
                if(coreUidList.contains(uid)){
                    e.setInternalStatus(2);
                }else{
                    e.setInternalStatus(0);
                }
                result.getUserData().add(e);
            }
        }

        return Response.success(result);
    }

    @Override
    public Response submitEmotion(long uid, int source, long emotionId, int happyValue, int freeValue) {
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        if (userProfile == null) {
            return Response.failure(500, "没有该用户信息！");
        }
        EmotionInfo emotionInfo = userService.getEmotionInfoByKey(emotionId);
        if (emotionInfo == null) {
            return Response.failure(500, "没有该情绪信息！");
        }
        EmotionPackDetail emotionPackDetail = contentService
                .getEmotionPackDetailByKey(Integer.valueOf(emotionInfo.getEmotionpackid() + ""));
        if (emotionPackDetail == null) {
            return Response.failure(500, "没有该情绪大表情信息！");
        }
        EmotionPack emotionPack = contentService
                .getEmotionPackByKey(Integer.valueOf(emotionPackDetail.getPackId() + ""));
        if (emotionPack == null) {
            return Response.failure(500, "没有该情绪大表情包信息！");
        }
        Topic topic = liveMybatisDao.getEmotionTopic(uid);
        if (topic == null) {
        	topic =createEmotionTopic(uid);
        }
         if(topic!=null){
             SpeakDto speakDto = new SpeakDto();
             speakDto.setType(52);
             speakDto.setContentType(18);
             speakDto.setUid(userProfile.getUid());
             speakDto.setTopicId(topic.getId());
             speakDto.setSource(source);

             JSONObject extra = new JSONObject();
             extra.put("type", "emoji");
             extra.put("only", UUID.randomUUID().toString() + "-" + new Random().nextInt());
             JSONObject fromObj = new JSONObject();
             fromObj.put("uid", uid);
             fromObj.put("avatar", Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
             fromObj.put("id", topic.getId());
             Content topicContet = contentService.getContentByTopicId(topic.getId());
             fromObj.put("cid", topicContet == null ? "" : topicContet.getId());
             fromObj.put("title", topic.getTitle());
             fromObj.put("cover", Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
             fromObj.put("url", live_web + topicContet.getId());
             extra.put("from", fromObj);
             extra.put("title", emotionPackDetail.getTitle());
             extra.put("content", emotionPackDetail.getExtra());
             extra.put("emojiType", 1);
             extra.put("image", Constant.QINIU_DOMAIN + "/" + emotionPackDetail.getImage());
             extra.put("w", emotionPackDetail.getW());
             extra.put("h", emotionPackDetail.getH());
             extra.put("thumb", emotionPackDetail.getThumb());
             extra.put("thumb_w", emotionPackDetail.getThumbW());
             extra.put("thumb_h", emotionPackDetail.getThumbH());
             extra.put("packageId", emotionPack.getId());
             extra.put("packageName", emotionPack.getName());
             extra.put("packageCover", emotionPack.getCover());

             speakDto.setExtra(extra.toJSONString());
             speak(speakDto);
         } 
        EmotionRecord emotionRecord = new EmotionRecord();
        emotionRecord.setUid(uid);
        emotionRecord.setEmotionid(emotionId);
        emotionRecord.setFreevalue(freeValue);
        emotionRecord.setHappyvalue(happyValue);
        userService.addEmotionRecord(emotionRecord);
        return Response.success();
    }

    @Override
    public Response startNewEmotionInfo(long uid, int source, String image, int w, int h) {
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        if (userProfile == null) {
            return Response.failure(500, "没有该用户信息！");
        }
        Topic topic = liveMybatisDao.getEmotionTopic(uid);
            if (topic == null) {
            	topic =createEmotionTopic(uid);
            }
            if (topic != null) {
            SpeakDto speakDto = new SpeakDto();
            speakDto.setType(0);
            speakDto.setContentType(1);
            speakDto.setUid(userProfile.getUid());
            speakDto.setTopicId(topic.getId());
            speakDto.setSource(source);
            speakDto.setFragmentImage(image);

            JSONObject extra = new JSONObject();
            extra.put("type", "image");
            extra.put("only", UUID.randomUUID().toString() + "-" + new Random().nextInt());
            JSONObject fromObj = new JSONObject();
            fromObj.put("uid", uid);
            fromObj.put("avatar", Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            fromObj.put("id", topic.getId());
            Content topicContet = contentService.getContentByTopicId(topic.getId());
            fromObj.put("cid", topicContet == null ? "" : topicContet.getId());
            fromObj.put("title", topic.getTitle());
            fromObj.put("cover", Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            fromObj.put("url", live_web + topicContet.getId());
            extra.put("from", fromObj);
            extra.put("w", w);
            extra.put("h", h);
            extra.put("length", 0);
            extra.put("orientation", 1);
            extra.put("format", "");
            extra.put("rLen", 0);
            extra.put("rFmt", "");
            speakDto.setExtra(extra.toJSONString());
            speak(speakDto);

        }
        return Response.success();
    }
    @Override
    public Response emotionInfoList(){
        List<EmotionInfo> datas = userService.getEmotionInfoList();
        EmotionInfoListDto dto = new EmotionInfoListDto();
        for (int i = 0; i < datas.size(); i++) {
            EmotionInfoListDto.EmotionInfoElement ee = EmotionInfoListDto.createEmotionInfoElement();
            Map<String,Object> map = new HashMap<String,Object>();
            EmotionInfo emotionInfo  =datas.get(i);
            ee.setId(emotionInfo.getId());
            ee.setEmotionName(emotionInfo.getEmotionname());
            ee.setHappyMin(emotionInfo.getHappymin());
            ee.setHappyMax(emotionInfo.getHappymax());
            ee.setFreeMin(emotionInfo.getFreemin());
            ee.setFreeMax(emotionInfo.getFreemax());
            if(emotionInfo.getTopicid()!=null){
                ee.setTopicId(emotionInfo.getTopicid());
                Topic topic =  liveMybatisDao.getTopicById(emotionInfo.getTopicid());
                if(topic!=null){
                    ee.setTopicTitle(topic.getTitle());
                }
            }
            EmotionPackDetail epd = contentService.getEmotionPackDetailByKey(Integer.valueOf(emotionInfo.getEmotionpackid()+""));
            EmotionInfoListDto.EmotionPack ep = EmotionInfoListDto.createEmotionPack();
            ep.setId(epd.getId());
            ep.setTitle(epd.getTitle());
            ep.setContent(epd.getExtra());
            ep.setImage(epd.getImage());
            ep.setThumb(epd.getThumb());
            ep.setW(epd.getW());
            ep.setH(epd.getH());
            ep.setThumb_w(epd.getThumbW());
            ep.setThumb_h(epd.getThumbH());
            ep.setExtra(epd.getExtra());
            ep.setEmojiType(1);
            ee.setEmotionPack(ep);
            dto.getEmotionInfoData().add(ee);
        }
        return Response.success(dto);
    }

    private int getTopicFragmentScore(int type, int contentType){
        int result = 0;

        String key = "TOPICFRAGMENTSCORE_"+type+"_"+contentType;

        String res = userService.getAppConfigByKey(key);
        if(!StringUtils.isEmpty(res)){
            try{
                result = Integer.valueOf(res);
            }catch(Exception e){
                log.error("数字转换失败", e);
            }
        }

        return result;
    }

    @Override
    public String changeTopicKing(long topicId, long newUid){
        if(newUid <= 0){
            return "未传递新UID";
        }
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(null == topic){
            return "王国不存在";
        }
        UserProfile oldUser = userService.getUserProfileByUid(topic.getUid());
        if(null == oldUser){
            return "老国王不存在";
        }
        UserProfile newUser = userService.getUserProfileByUid(newUid);
        if(null == newUser){
            return "新国王不存在";
        }
        if(topic.getUid() == newUid){
            return "王国不能自己给自己";
        }

        log.info("王国["+topic.getTitle()+"]国王变更，老国王："+oldUser.getNickName()+"，新国王：" + newUser.getUid());

        Date now = new Date();
        //1 变更国王
        Topic updateTopic = new Topic();
        updateTopic.setId(topic.getId());
        //1.1 将王国UID变更
        updateTopic.setUid(newUid);
        //1.2 由于原国王本身就在核心圈里，所以不需要处理了，但是需要将新国王放入核心圈里（如果已经在了就不需要了）
        JSONArray array = JSON.parseArray(topic.getCoreCircle());
        boolean needAdd = true;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == newUid) {
                needAdd = false;
                break;
            }
        }
        if(needAdd){
            array.add(newUid);
            updateTopic.setCoreCircle(array.toString());
        }
        liveMybatisDao.updateTopic(updateTopic);
        //1.3 如果新国王原先加入过这个王国的，则去除加入关系
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(newUid, topicId);
        if(null != liveFavorite){
            liveMybatisDao.deleteLiveFavorite(liveFavorite);
        }
        //1.4 将老国王加入到这个王国（因为已经是核心圈了）
        LiveFavorite oldLiveFavorite = liveMybatisDao.getLiveFavorite(oldUser.getUid(), topicId);
        if(null == oldLiveFavorite){
            oldLiveFavorite = new LiveFavorite();
            oldLiveFavorite.setUid(oldUser.getUid());
            oldLiveFavorite.setTopicId(topicId);
            oldLiveFavorite.setCreateTime(now);
            liveMybatisDao.createLiveFavorite(oldLiveFavorite);
        }
        //变更UGC
        contentService.updateContentUid(newUid, topicId);
        //2 记录转让历史
        TopicTransferRecord ttr = new TopicTransferRecord();
        ttr.setCreateTime(now);
        ttr.setNewUid(newUid);
        ttr.setOldUid(oldUser.getUid());
        ttr.setPrice(topic.getPrice());
        ttr.setTopicId(topicId);
        liveMybatisDao.addTopicTransferRecord(ttr);

        //3 在该王国的详情中插入转让卡片
        TopicFragment fragment = new TopicFragment();
        fragment.setTopicId(topicId);
        fragment.setUid(newUid);
        fragment.setType(52);
        fragment.setContentType(21);//王国内链
        fragment.setCreateTime(now);
        //组装extra
        JSONObject extra = new JSONObject();
        extra.put("type", "kingdomOTD");
        extra.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
        extra.put("price", topic.getPrice());
        extra.put("uid", newUid);
        extra.put("avatar", Constant.QINIU_DOMAIN + "/" +newUser.getAvatar());
        extra.put("name", newUser.getNickName());
        extra.put("oldUid", oldUser.getUid());
        extra.put("oldAvatar", Constant.QINIU_DOMAIN + "/" +oldUser.getAvatar());
        extra.put("oldName", oldUser.getNickName());
        fragment.setExtra(extra.toJSONString());
        fragment.setScore(0);//这个是没有分值的
        liveMybatisDao.createTopicFragment(fragment);

        //4 记录跑马灯记录
        //XXX的《王国名》以XXX元成功转让，欢迎新国王XXX闪亮登场。
        String exchangeRateConfig = userService.getAppConfigByKey("EXCHANGE_RATE");
        int exchangeRate = 100;
        if(!StringUtils.isEmpty(exchangeRateConfig)){
        	exchangeRate = Integer.valueOf(exchangeRateConfig).intValue();
        }
        double rmb = (double)topic.getPrice()/(double)exchangeRate;
        
        rmb = new BigDecimal(rmb).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        
        TopicNews topicNews = new TopicNews();
        topicNews.setTopicId(topicId);
        topicNews.setType(Specification.TopicNewsType.BUSINESS.index);
        topicNews.setContent(oldUser.getNickName()+"的《"+topic.getTitle()+"》以"+rmb+"元成功转让，欢迎新国王"+newUser.getNickName()+"闪亮登场");
        topicNews.setCreateTime(now);
        liveMybatisDao.addTopicNews(topicNews);

        return "0";
    }

    @Override
    public Response getKingdomTransferRecord(long topicId,long sinceId){
        List<TopicTransferRecord> datas = liveMybatisDao.getKingdomTransferRecord(topicId, sinceId);
        TopicTransferRecordDto dto = new TopicTransferRecordDto();
        for (int i = 0; i < datas.size(); i++) {
            TopicTransferRecordDto.TopicTransferRecordElement ee =new TopicTransferRecordDto.TopicTransferRecordElement();
            TopicTransferRecord topicTransferRecord  =datas.get(i);
            ee.setSinceId(topicTransferRecord.getId());
            ee.setTransferPrice(topicTransferRecord.getPrice());
            UserProfile oldUser = userService.getUserProfileByUid(topicTransferRecord.getOldUid());
            ee.setOldNickName(oldUser.getNickName());
            ee.setOldAvatar(Constant.QINIU_DOMAIN + "/" +oldUser.getAvatar());
            UserProfile newUser = userService.getUserProfileByUid(topicTransferRecord.getNewUid());
            ee.setNewNickName(newUser.getNickName());
            ee.setNewAvatar(Constant.QINIU_DOMAIN + "/" +newUser.getAvatar());
            ee.setCreateTime(topicTransferRecord.getCreateTime().getTime());
            dto.getTopicTransferRecordList().add(ee);
        }
        return Response.success(dto);
    }
    /**
     * 获取用户在指定的王国下可偷的金币数量。如果可偷，返回具体可偷取的数量；否则抛出异常，提示不可偷取原因
     * @author zhangjiwei
     * @date Jun 12, 2017
     * @param uid
     * @param topicId
     * @return
     * @throws Exception
     */
    private int getAliveCoinsForSteal(long uid,long topicId) throws Exception{
        //判断用户能不能偷取王国
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(topic.getUid()==uid){
            throw new KingdomStealException("不能偷取自己的王国");
        }
        // 本王国今日余额
        int topicRemainCoins= this.liveLocalJdbcDao.getTopicRemainPrice(topicId);

        if(topicRemainCoins<=0){
            throw new KingdomStealException("该王国没有可偷的金币了");
        }
        // 用户今天已偷
        String day = DateUtil.date2string(new Date(), "yyyy-MM-dd");
        // 用户今日可偷
        List<Map<String,Object>> userStealLog = liveLocalJdbcDao.getUserStealLogByDay(uid,day);
        int stealedCoins=0;
        boolean stealed = false;
        for(Map<String,Object> log:userStealLog){
            int logCoin = (int)log.get("stealed_coins");
            stealedCoins+=logCoin;
            if(topicId==(long)log.get("topic_id")){
                stealed=true;
            }
        }
        if(stealed){
            throw new KingdomStealException(KingdomStealException.KINGDOM_STEALED,"不能重复偷取此王国");
        }
        // 每天可偷数量
        int userDayLimit = 0;//Integer.parseInt(userService.getAppConfigByKey(Constant.USER_STEAL_COIN_DAY_LIMIT_KEY));
        int userOnceLimit = 0;//Integer.parseInt(userService.getAppConfigByKey(Constant.USER_STEAL_COIN_ONCE_LIMIT_KEY));
        int userTopicLimit = 0;//Integer.parseInt(userService.getAppConfigByKey(Constant.USER_STEAL_TOPIC_DAY_LIMIT_KEY));
        PermissionDescriptionDto permisson=  userService.getUserPermission(uid);
        for(PermissionNodeDto per: permisson.getNodes()){
        	if(per.getCode()==8){// 可偷王国数量
        		userTopicLimit=per.getNum();
        	}
        	if(per.getCode()==9){// 单次偷取上限
        		userOnceLimit=per.getNum();
        	}
        	if(per.getCode()==10){// 个人每日获取金币上限
        		userDayLimit=per.getNum();
        	}
        }
        
        	
        if(userStealLog.size()>=userTopicLimit){
            throw new KingdomStealException("你当前的等级每天只能偷取"+userTopicLimit+"个王国哦");
        }
        stealedCoins=liveLocalJdbcDao.getUserConinsByDay(uid, day);		// 此处重新计算用户当日获取到的总金币数，包括操作所得和偷取所得。
        //int userTodayRemain=userDayLimit-stealedCoins;

        //if(userTodayRemain<=0){
        //    throw new KingdomStealException("当天可获取的米汤币已达上限");
        //}

       // int canStealCount = Math.min(userTodayRemain, topicRemainCoins);
        int canStealCount =topicRemainCoins;
        canStealCount=Math.min(canStealCount, userOnceLimit);
        // 判断王国剩余价值。
        //随机数
        int coins = RandomUtils.nextInt(1, canStealCount+1);
        return coins;
    }
    /**
     * 此方法必须同步执行。此实现使用zookeeper实现分布式锁。
     */
    @Override
    public Response stealKingdomCoin(long uid,long topicId) {
		String addr= zkAddr.replace("zookeeper://", "");
		final int NEW_USER_ZHONGJIANG_COUNT=3;		// 新用户3次必中。
		DistributedLock lock = null;
		try {

			lock = new DistributedLock(addr, "steal-topic-"+topicId);
			lock.lock();
			int coins=0;
			int isBigRedPack =0;
			try{
				coins = getAliveCoinsForSteal(uid, topicId);
				if(coins>0){ // 大红包逻辑
					List<Map<String,Object>> list = liveLocalJdbcDao.getUserStealLogByCountAsc(uid, NEW_USER_ZHONGJIANG_COUNT);
					if(list.size()<NEW_USER_ZHONGJIANG_COUNT){	// 新手3次必中
						boolean has = false;
						for(Map<String,Object> data:list){
							if(data.get("is_big_red_pack")!=null && data.get("is_big_red_pack").equals(1)){
								has=true;
								break;
							}
						}
						if(!has){	// 3次以内没中，随机开奖。
							int r = RandomUtils.nextInt(list.size(), NEW_USER_ZHONGJIANG_COUNT);
							if(r==list.size()){
								isBigRedPack=1;
							}
						}
					}else{
						int ratio = (int)(Integer.parseInt(userService.getAppConfigByKey(Constant.BIG_RED_PACK_RATIO_KEY)));
						
						Set<Integer> jpSet = new HashSet<>();
						while(jpSet.size()<ratio){		// // 设定ratio个随机奖牌
							int r = RandomUtils.nextInt(1, 101);
							if(!jpSet.contains(r)){
								jpSet.add(r);
							}
						}
						
						int r = RandomUtils.nextInt(1, 101);// 翻牌子
						if(jpSet.contains(r)){		// 恭喜你，中奖了。
							isBigRedPack=1;
						}
					}
					if(isBigRedPack==1){ //中奖了
						Topic topic = liveMybatisDao.getTopicById(topicId);
						UserProfile profile= userService.getUserProfileByUid(uid);
						coins= RandomUtils.nextInt(Integer.parseInt(userService.getAppConfigByKey(Constant.BIG_RED_PACK_MIN_KEY)),
								Integer.parseInt(userService.getAppConfigByKey(Constant.BIG_RED_PACK_MAX_KEY))+1);
						TopicNews news = new TopicNews();
						news.setTopicId(topicId);
						news.setContent(profile.getNickName()+"在《"+topic.getTitle()+"》获得一个大红包,价值"+coins+"米汤币");
						news.setType(2);
						news.setCreateTime(new Date());
						liveMybatisDao.addTopicNews(news);
					}
				}
			}catch(Exception e){
				return Response.failure(500,e.getMessage());
			}
			if(isBigRedPack==0){
				// 	修改王国可被偷数
				this.liveLocalJdbcDao.stealTopicPrice(coins, topicId);
			}
			// 记录偷取日志
			UserStealLog log = new UserStealLog();
			log.setCreateTime(new Date());
			log.setIsBigRedPack(isBigRedPack);
			log.setStealedCoins(coins);
			log.setTopicId(topicId);
			log.setUid(uid);
			liveMybatisDao.addStealLog(log);
			// 修改用户金币数
			ModifyUserCoinDto modifyDetail=userService.modifyUserCoin(uid,coins);

			StealResultDto dto= new StealResultDto();
			dto.setIsBigRedPack(isBigRedPack);
			dto.setStealedCoins(coins);
			dto.setUpgrade(modifyDetail.getUpgrade());
			dto.setCurrentLevel(modifyDetail.getCurrentLevel());
			return Response.success(dto);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.failure(500,e.getMessage());
		} finally {
			if(lock != null)
				lock.unlock();
		}
    }

    @Override
    public Response rechargeToKingdom(RechargeToKingdomDto rechargeToKingdomDto) {

        UserProfile  userProfile = userService.getUserProfileByUid(rechargeToKingdomDto.getUid());
        if(userProfile.getAvailableCoin() ==0){
            return  Response.failure(500,"没有可转移米汤币");
        }
        rechargeToKingdomDto.setAmount(userProfile.getAvailableCoin());

        Topic topic = getTopicById(rechargeToKingdomDto.getTopicId());

        if(topic == null || userProfile == null){
            return  Response.failure(500,"王国或用户无效");
        }
        // 判断当前的王国是否是自己的王国.
       /* if(rechargeToKingdomDto.getUid() != topic.getUid()){
            return  Response.failure("王国无效");
        }*/
        if(rechargeToKingdomDto.getAmount() == userProfile.getAvailableCoin()){
            liveLocalJdbcDao.rechargeToKingDom(rechargeToKingdomDto.getTopicId(),rechargeToKingdomDto.getAmount());
            liveLocalJdbcDao.zeroMyCoins(rechargeToKingdomDto.getUid());
            // 更新完成后判断王国的数值是否达到上市标准.如果达标调用跑马灯接口
            //取出达到上市条件的米汤币数量
           /* String listedPrice = userService.getAppConfigByKey(LISTED_PRICE);
            int i = Integer.parseInt( listedPrice, 10);
            if((topic.getPrice()+rechargeToKingdomDto.getAmount())>= i ){

                // XXX的《王国名》挂牌上市了，快来围观抢购吧。
                String content = userProfile.getNickName()+"的《"+topic.getTitle()+"》挂牌上市了，快来围观抢购吧。";
                liveLocalJdbcDao.writeTopicNews(topic.getId(),content);
                liveLocalJdbcDao.writeTopicTime(topic.getId());
            }*/

            return Response.success();
        }else {
            return Response.failure(500,"充值米汤币与实际不符");
        }
    }


    @Override
    public Response getKingdomPrice(long topicId) {
        Topic topic = getTopicById(topicId);
        if (topic == null) {
            return Response.failure(500,"找不到王国");
        }
        GetKingdomPriceDto dto = new GetKingdomPriceDto();
        dto.setTitle(topic.getTitle());
        dto.setTopicPrice(topic.getPrice());
        dto.setTopicRMB(exchangeKingdomPrice(topic.getPrice()));
        // 王国转让客服联系ID
        String sellUidStr = userService.getAppConfigByKey("SELL_UID");
        if (StringUtils.isEmpty(sellUidStr)) {
            return Response.failure(500,"王国转让客服联系ID配置错误！");
        }
        dto.setSellUid(Long.parseLong(sellUidStr));
        TopicListed topicListed = liveMybatisDao.getTopicListedByTopicId(topicId);
        if(topicListed!=null){
        	 dto.setIsSell(2);
        	 dto.setDistanceListed(0);
        }else{
        // 米汤上市界限
        String listedPriceStr = userService.getAppConfigByKey(Constant.LISTING_PRICE_KEY);
        if (StringUtils.isEmpty(listedPriceStr)) {
            return Response.failure(500,"米汤上市界限配置错误！");
        }
        int listedPrice = Integer.parseInt(listedPriceStr);
        if (topic.getPrice() >= listedPrice) {
            dto.setIsSell(1);
            dto.setDistanceListed(0);
        } else {
            dto.setIsSell(0);
            dto.setDistanceListed(listedPrice - topic.getPrice());
        }
        }
        List<TopicPriceHis> topicPriceChangedList = liveMybatisDao.getLastTenDaysTopicPrice(topicId);
        for (int i = topicPriceChangedList.size(); i > 0; i--) {
            TopicPriceHis topicPriceHis = topicPriceChangedList.get(i - 1);
            GetKingdomPriceDto.TopicPriceChangeElement topicPriceChangeElement = GetKingdomPriceDto
                    .createTopicPriceChangeElement();
            topicPriceChangeElement.setTopicPrice(topicPriceHis.getPrice());
            dto.getTopicPriceChangedList().add(topicPriceChangeElement);
        }
        int topicCount = liveMybatisDao.getTopicCount();
        int lessPriceTopicCount = liveMybatisDao.getLessPriceTopicCount(topic.getPrice());
        int percentage = new BigDecimal((lessPriceTopicCount * 100.0 / topicCount))
                .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        dto.setBeatTopicPercentage(percentage);
        TopicData topicData = liveMybatisDao.getTopicDataByTopicId(topicId);
        if (topicData == null) {
            dto.setTopicPriceChanged(0);
            dto.setUpdateTextCount(0);
            dto.setUpdateImageCount(0);
            dto.setUpdateVoiceCount(0);
            dto.setUpdateVideoCount(0);
            dto.setVoteCount(0);
            dto.setTeaseCount(0);
            dto.setUpdateDaysCount(0);
            dto.setReplyTextCount(0);
            dto.setTopicUserCount(0);
            dto.setReadCount(0);
            dto.setShareCount(0);
            dto.setOuterReadCount(0);
            dto.setCareDegree(0);
            dto.setApprovalDegree(0);
        } else {
            dto.setTopicPriceChanged(topicData.getLastPriceIncr());
            dto.setUpdateTextCount(topicData.getUpdateTextLength());
            dto.setUpdateImageCount(topicData.getUpdateImageCount());
            dto.setUpdateVoiceCount(topicData.getUpdateAudioLength());
            dto.setUpdateVideoCount(topicData.getUpdateVedioLength());
            dto.setVoteCount(topicData.getUpdateVoteCount());
            dto.setTeaseCount(topicData.getUpdateTeaseCount());
            dto.setUpdateDaysCount(topicData.getUpdateDayCount());
            dto.setReplyTextCount(topicData.getReviewTextLength());
            dto.setTopicUserCount(contentService.getTopicMembersCount(topicId));
            dto.setReadCount(liveLocalJdbcDao.getReadCount(topicId));
            dto.setOuterReadCount(liveLocalJdbcDao.getReadCountOuter(topicId));
            dto.setShareCount(contentService.getTopicShareCount(topicId));
            dto.setCareDegree(topicData.getDiligently());
            dto.setApprovalDegree(topicData.getApprove());
        }
        return Response.success(dto);
    }

    /**
     * 米汤币兑换人名币
     * @param price
     * @return
     */
    public double exchangeKingdomPrice(int price) {
        String  result =  userService.getAppConfigByKey("EXCHANGE_RATE");
        if(StringUtils.isEmpty(result)){
            return 0;
        }
        BigDecimal exchangeRate = new BigDecimal(result);
        return new BigDecimal(price).divide(exchangeRate, 2, RoundingMode.HALF_UP).doubleValue();
    }
    @Override
    public List<TopicPriceSubsidyConfig> getTopicPriceSubsidyConfigList() {
    	return liveMybatisDao.getTopicPriceSubsidyConfigList();
    }
    @Override
    public TopicPriceSubsidyConfig getTopicPriceSubsidyConfigById(long id)  {
    	return liveMybatisDao.getTopicPriceSubsidyConfigById(id);
    }
    @Override
	public void saveTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc){
		liveMybatisDao.saveTopicPriceSubsidyConfig(tpsc);
	}
    @Override
	public void editTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc){
		liveMybatisDao.editTopicPriceSubsidyConfig(tpsc);
	}
    @Override
	public void delTopicPriceSubsidyConfig(long id){
		liveMybatisDao.delTopicPriceSubsidyConfig(id);
	}

    @Override
    public Response searchTopicListedPage(int status,String title,int page, int pageSize){
     	int totalRecord = liveLocalJdbcDao.countTopicListedByStatus(status,title);
    	int totalPage = (totalRecord + pageSize - 1) / pageSize;
    	if(page>totalPage){
    		page=totalPage;
    	}
    	if(page<1){
    		page=1;
    	}
    	int start = (page-1)*pageSize;
    	List<Map<String, Object>> list = liveLocalJdbcDao.getTopicListedListByStatus(status,title,start, pageSize);
    	SearchTopicListedListDto dto = new SearchTopicListedListDto();
        dto.setTotalRecord(totalRecord);
        dto.setTotalPage(totalPage);
        for(Map<String, Object> map : list){
        	SearchTopicListedListDto.TopicListedElement e = dto.createTopicListedElement();
        	 e.setId((Long)map.get("id"));
        	 e.setTitle((String)map.get("title"));
        	 int statusD = (Integer)map.get("status");
        	 e.setStatus(statusD);
        	 if(statusD==1){
        		 e.setPrice((Double)map.get("frozenPrice"));
        	 }else if(statusD==0){
        		 e.setPrice(exchangeKingdomPrice((Integer)map.get("price")));
        	 }else if(statusD==2){
        		 e.setPrice((Double)map.get("frozenPrice")); 
        		 long buyUid = Long.parseLong(map.get("buy_uid").toString());
        		 if(buyUid!=0l){
        			 e.setMeNumber(map.get("me_number").toString());
        		 }
        	 }
             e.setNickName((String)map.get("nick_name"));
             e.setCreateTime((Date)map.get("create_time"));
            dto.getResult().add(e);
        }
        return Response.success(dto);
    }
    @Override
    public void updateTopicListedStatus(TopicListed topicListed){
    	TopicListed oldTopicListed = liveMybatisDao.getTopicListedById(topicListed.getId());
    	Topic topic  = liveMybatisDao.getTopicById(oldTopicListed.getTopicId());
    	if(oldTopicListed.getStatus()==0){
    		if(topic!=null){
    			topicListed.setPersonCount(liveLocalJdbcDao.getTopicMembersCount(topic.getId()));
        		topicListed.setReadCount(liveLocalJdbcDao.getReadCount(topic.getId()));
        		topicListed.setReviewCount(liveLocalJdbcDao.getTopicReviewCount(topic.getId()));
        		topicListed.setPrice(topic.getPrice());
        		topicListed.setPriceRmb(exchangeKingdomPrice(topic.getPrice()));
    		}
    	}else if(oldTopicListed.getStatus()==1){
    		if(topicListed.getStatus()==0){
    			topicListed.setPersonCount(0);
        		topicListed.setReadCount(0);
        		topicListed.setReviewCount(0);
        		topicListed.setPrice(0);
        		topicListed.setPriceRmb(0.0);
        		topicListed.setBuyUid(0l);
    		}else if(topicListed.getStatus()==2){
    			if(topic!=null){
    				StringBuffer message=new StringBuffer();
    				message.append("您上市的王国《").append(topic.getTitle()).append("》对方已付款，请您在一个工作日之内提供支付宝帐号，我们会尽快替您完成交易。");
                	try {
						ImSendMessageDto dto=	smsService.sendSysMessage(topic.getUid().toString(), message.toString());
					} catch (Exception e) {
						log.error(e.getMessage());
					}
    			}
    		}
    	}
		liveMybatisDao.updateTopicListed(topicListed);
	}
    @Override
	public String handleTransaction(long id,long meNumber){
    	UserNo userNo  =userService.getUserNoByMeNumber(meNumber);
    	if(userNo==null){
    		return "找不到该用户";
    	}
    	TopicListed topicListed = liveMybatisDao.getTopicListedById(id);
    	if(topicListed==null){
    		return "topicListed为空";
    	}
		return changeTopicKing(topicListed, userNo.getUid());
	}
    @Override
    public String changeTopicKing(TopicListed topicListed, long newUid){
        if(newUid <= 0){
            return "未传递新UID";
        }
        long topicId = topicListed.getTopicId();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(null == topic){
            return "王国不存在";
        }
        UserProfile oldUser = userService.getUserProfileByUid(topic.getUid());
        if(null == oldUser){
            return "老国王不存在";
        }
        UserProfile newUser = userService.getUserProfileByUid(newUid);
        if(null == newUser){
            return "新国王不存在";
        }
        if(topic.getUid() == newUid){
            return "王国不能自己给自己";
        }

        log.info("王国["+topic.getTitle()+"]国王变更，老国王："+oldUser.getNickName()+"，新国王：" + newUser.getUid());

        Date now = new Date();
        //1 变更国王
        Topic updateTopic = new Topic();
        updateTopic.setId(topic.getId());
        //1.1 将王国UID变更
        updateTopic.setUid(newUid);
        //1.2 由于原国王本身就在核心圈里，所以不需要处理了，但是需要将新国王放入核心圈里（如果已经在了就不需要了）
        JSONArray array = JSON.parseArray(topic.getCoreCircle());
        boolean needAdd = true;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == newUid) {
                needAdd = false;
                break;
            }
        }
        if(needAdd){
            array.add(newUid);
            updateTopic.setCoreCircle(array.toString());
        }
        liveMybatisDao.updateTopic(updateTopic);
        //1.3 如果新国王原先加入过这个王国的，则去除加入关系
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(newUid, topicId);
        if(null != liveFavorite){
            liveMybatisDao.deleteLiveFavorite(liveFavorite);
        }
        //1.4 将老国王加入到这个王国（因为已经是核心圈了）
        LiveFavorite oldLiveFavorite = liveMybatisDao.getLiveFavorite(oldUser.getUid(), topicId);
        if(null == oldLiveFavorite){
            oldLiveFavorite = new LiveFavorite();
            oldLiveFavorite.setUid(oldUser.getUid());
            oldLiveFavorite.setTopicId(topicId);
            oldLiveFavorite.setCreateTime(now);
            liveMybatisDao.createLiveFavorite(oldLiveFavorite);
        }
        //变更UGC
        contentService.updateContentUid(newUid, topicId);
        //2 记录转让历史
        TopicTransferRecord ttr = new TopicTransferRecord();
        ttr.setCreateTime(now);
        ttr.setNewUid(newUid);
        ttr.setOldUid(oldUser.getUid());
        ttr.setPrice(topicListed.getPrice());
        ttr.setPriceRmb(topicListed.getPriceRmb());
        ttr.setTopicId(topicId);
        liveMybatisDao.addTopicTransferRecord(ttr);

        //3 在该王国的详情中插入转让卡片
        TopicFragment fragment = new TopicFragment();
        fragment.setTopicId(topicId);
        fragment.setUid(newUid);
        fragment.setType(52);
        fragment.setContentType(21);//王国内链
        fragment.setCreateTime(now);
        //组装extra
        JSONObject extra = new JSONObject();
        extra.put("type", "kingdomOTD");
        extra.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
        extra.put("price", topic.getPrice());
        extra.put("uid", newUid);
        extra.put("avatar", Constant.QINIU_DOMAIN + "/" +newUser.getAvatar());
        extra.put("name", newUser.getNickName());
        extra.put("oldUid", oldUser.getUid());
        extra.put("oldAvatar", Constant.QINIU_DOMAIN + "/" +oldUser.getAvatar());
        extra.put("oldName", oldUser.getNickName());
        fragment.setExtra(extra.toJSONString());
        fragment.setScore(0);//这个是没有分值的
        liveMybatisDao.createTopicFragment(fragment);

        //4 记录跑马灯记录
        //XXX的《王国名》以XXX元成功转让，欢迎新国王XXX闪亮登场。
        String exchangeRateConfig = userService.getAppConfigByKey("EXCHANGE_RATE");
        int exchangeRate = 100;
        if(!StringUtils.isEmpty(exchangeRateConfig)){
        	exchangeRate = Integer.valueOf(exchangeRateConfig).intValue();
        }
        double rmb = (double)topic.getPrice()/(double)exchangeRate;
        
        rmb = new BigDecimal(rmb).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        
        TopicNews topicNews = new TopicNews();
        topicNews.setTopicId(topicId);
        topicNews.setType(Specification.TopicNewsType.BUSINESS.index);
        topicNews.setContent(oldUser.getNickName()+"的《"+topic.getTitle()+"》以"+rmb+"元成功转让，欢迎新国王"+newUser.getNickName()+"闪亮登场");
        topicNews.setCreateTime(now);
        liveMybatisDao.addTopicNews(topicNews);
        //5 上市王国信息删除
    	liveMybatisDao.delTopicListed(topicListed.getTopicId());
    	try {
        	StringBuffer oldUserMessage=new StringBuffer();
        	oldUserMessage.append("您上市的王国《").append(topic.getTitle()).append("》已成交，快去确认收款吧。");
        	ImSendMessageDto dto1=	smsService.sendSysMessage(topic.getUid().toString(), oldUserMessage.toString());
        	StringBuffer newUserMessage=new StringBuffer();
        	newUserMessage.append("您收购的王国《").append(topic.getTitle()).append("》已经成交，您已经成为新国王了，快去您的王国看看吧！");
        	ImSendMessageDto dto2=	smsService.sendSysMessage(newUid+"", newUserMessage.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
        return "0";
    }
    @Override
	public Response listTopic(long topicId,long uid){
    	Topic topic = liveMybatisDao.getTopicById(topicId);
    	if(topic==null){
    		return Response.failure(500, "找不到该王国！") ;
    	}
    	UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
    	if(userProfile==null){
    		return Response.failure(500, "找不到国王！") ;
    	}
    	if(uid!=topic.getUid()){
    		return Response.failure(500, "您不是国王！") ;
    	}
    	  // 米汤上市界限
        String listedPriceStr = userService.getAppConfigByKey(Constant.LISTING_PRICE_KEY);
        if (StringUtils.isEmpty(listedPriceStr)) {
            return Response.failure(500,"米汤上市界限配置错误！");
        }
        int listedPrice = Integer.parseInt(listedPriceStr);
        if (topic.getPrice() <listedPrice) {
        	 return Response.failure(500,"未达到米汤上市最低值！");
        }
    	TopicListed topicListed = liveMybatisDao.getTopicListedByTopicId(topicId);
    	if(topicListed==null){
    		topicListed = new TopicListed();
    		topicListed.setTopicId(topicId);
    		liveMybatisDao.addTopicListed(topicListed);
    	}else {
    		if(topicListed.getStatus()==0 ||topicListed.getStatus()==1 ||topicListed.getStatus()==2  ){
    			 return Response.failure(500,"该王国已上市！");
    		}else{
            topicListed.setPersonCount(0);
            topicListed.setReviewCount(0);
        	topicListed.setReadCount(0);
        	topicListed.setPrice(0);
        	topicListed.setPriceRmb(0.0);
        	topicListed.setBuyUid(0l);
        	topicListed.setStatus(0);
        	liveMybatisDao.updateTopicListed(topicListed);
    		}
    	}
    	//添加跑马灯
    	TopicNews topicNews = new TopicNews();
    	topicNews.setTopicId(topicId);
    	topicNews.setType(1);
    	StringBuffer sb = new StringBuffer();
    	sb.append(userProfile.getNickName());
    	sb.append("的《");
    	sb.append(topic.getTitle());
    	sb.append("》挂牌上市了，快来围观抢购吧");
    	topicNews.setContent(sb.toString());
    	liveMybatisDao.addTopicNews(topicNews);
    	return Response.success();
	}
    @Override
    public Response listedTopicList(long sinceId){
        List<TopicListed> datas = liveMybatisDao.getTopicListedList(sinceId);
        ListedTopicListDto dto = new ListedTopicListDto();
        for (int i = 0; i < datas.size(); i++) {
        	ListedTopicListDto.TopicListedElement ee =new ListedTopicListDto.TopicListedElement();
        	TopicListed topicListed  =datas.get(i);
            ee.setSinceId(topicListed.getId());
            ee.setTopicId(topicListed.getTopicId());
            Topic topic = liveMybatisDao.getTopicById(topicListed.getTopicId());
            if(topic!=null){
            	ee.setUid(topic.getUid());
            	ee.setCoverImage(Constant.QINIU_DOMAIN + "/" +topic.getLiveImage());
            	ee.setTitle(topic.getTitle());
            	UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            	if(userProfile!=null){
            		ee.setNickName(userProfile.getNickName());
            	}
            	ee.setContentType(topic.getType());
            	if(topicListed.getStatus()==1){
            		ee.setPersonCount(topicListed.getPersonCount());
                	ee.setReadCount(topicListed.getReadCount());
                	ee.setReviewCount(topicListed.getReviewCount());
                	ee.setPrice(topicListed.getPrice());
                	ee.setPriceRMB(topicListed.getPriceRmb());
                	ee.setBuyUid(topicListed.getBuyUid());
            	}else{
            	ee.setPersonCount(liveLocalJdbcDao.getTopicMembersCount(topic.getId()));
            	ee.setReadCount(liveLocalJdbcDao.getReadCount(topic.getId()));
            	ee.setReviewCount(liveLocalJdbcDao.getTopicReviewCount(topic.getId()));
            	ee.setPrice(topic.getPrice());
            	ee.setPriceRMB(exchangeKingdomPrice(topic.getPrice()));
            	}
            }
            ee.setStatus(topicListed.getStatus());
         	dto.getTopicList().add(ee);
        }
        if(isRestTopicListed()){
        	dto.setIsClosed(1);
        }else{
        	dto.setIsClosed(0);
        }
        String customerServiceUid = userService.getAppConfigByKey("SELL_UID");
        if(!StringUtils.isEmpty(customerServiceUid)){
        	 dto.setCustomerServiceUid(Long.parseLong(customerServiceUid));
        }
        return Response.success(dto);
    }
    //判断是否休市
    public boolean isRestTopicListed(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	    	Date date = new Date();
	    	String listedStartTime = userService.getAppConfigByKey("LISTED_START_TIME");
	    	if(StringUtils.isEmpty(listedStartTime)){
	    		listedStartTime="09:00:00";
	    	}
	    	String listedEndTime = userService.getAppConfigByKey("LISTED_END_TIME");
	    	if(StringUtils.isEmpty(listedEndTime)){
	    		listedEndTime="17:00:00";
	    	}
	    	SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd "+listedStartTime);
	    	SimpleDateFormat sdfEnd= new SimpleDateFormat("yyyy-MM-dd "+listedEndTime);
	    	String  strDateBegin = sdfStart.format(date);
		    String  strDateEnd = sdfEnd.format(date);
		    if((date.compareTo(sdf.parse(strDateBegin))==1 || date.compareTo(sdf.parse(strDateBegin))==0)
		    		& (date.compareTo(sdf.parse(strDateEnd))==-1 || date.compareTo(sdf.parse(strDateEnd))==0)
		    		){
		    	 Calendar cal = Calendar.getInstance();
			        cal.setTime(date);
			        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			        if(w==6||w==7){
			        	 return true;
			        }else{
			       String restStrDateBegin =cacheService.get("REST_LISTED_START_DATE");
			       String restStrDateEnd =cacheService.get("REST_LISTED_END_DATE");
			       if(StringUtils.isEmpty(restStrDateBegin) || StringUtils.isEmpty(restStrDateEnd)){
			    	   return false;
			       }else{
			    	    restStrDateBegin +=" 00:00:00";
			        	 restStrDateEnd +=" 23:59:59";
					    if((date.compareTo(sdf.parse(restStrDateBegin))==1 || date.compareTo(sdf.parse(restStrDateBegin))==0)
					    		& (date.compareTo(sdf.parse(restStrDateEnd))==-1 || date.compareTo(sdf.parse(restStrDateEnd))==0)
					    		){
					    	return true;
					    }  else{
					    	return false;
					    }
			       }
			        }
		    }else{
		    	return true;
		    }
		} catch (ParseException e) {
			log.error(e.getMessage());
			return true;
		}
    }

	@Override
	public Response takeoverTopic(long topicId, long uid) {
		Topic topic = liveMybatisDao.getTopicById(topicId);
		if (topic == null) {
			return Response.failure(500, "找不到该王国！");
		}
		TopicListed topicListed = liveMybatisDao.getTopicListedByTopicId(topicId);
		if (topicListed == null) {
			return Response.failure(500, "该王国还未挂牌上市，不可收购！");
		}
		if (liveMybatisDao.isBuyTopic(uid)) {
			return Response.failure(500, "不能重复收购！");
		}
		if (uid == topic.getUid()) {
			return Response.failure(500, "不能收购自己的王国！");
		}
		if (topicListed.getStatus() != 0) {
			return Response.failure(500, "该王国正在被其他人收购！");
		}
		if (isRestTopicListed()) {
			return Response.failure(500, "休市期间不能收购！");
		}

		topicListed.setPersonCount(liveLocalJdbcDao.getTopicMembersCount(topic.getId()));
		topicListed.setReadCount(liveLocalJdbcDao.getReadCount(topic.getId()));
		topicListed.setReviewCount(liveLocalJdbcDao.getTopicReviewCount(topic.getId()));
		topicListed.setPrice(topic.getPrice());
		topicListed.setPriceRmb(exchangeKingdomPrice(topic.getPrice()));
		topicListed.setStatus(1);
		topicListed.setBuyUid(uid);
		topicListed.setBuyTime(new Date());
		liveMybatisDao.updateTopicListed(topicListed);
		StringBuffer message = new StringBuffer();
		message.append("您上市的王国《").append(topic.getTitle()).append("》正在被人收购中。");
		try {
			ImSendMessageDto dto = smsService.sendSysMessage(topic.getUid().toString(), message.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return Response.success(200,"您已发送收购申请");
	}

	@Override
	public void updateExpiredTrialTag() {
		int expireDely = Integer.parseInt(userService.getAppConfigByKey("MACHINE_LABLE_TRIAL_TIME_DAY"));
		liveLocalJdbcDao.updateExpiredTrialTag(expireDely);
	}
	@Override
	public TopicListed getTopicListedByTopicId(long topicId){
		return liveMybatisDao.getTopicListedByTopicId(topicId);
	}
	/**
	 * 向王国中添加一个标签，如果标签已经存在，则跳过。
	 * @author zhangjiwei
	 * @date Jul 19, 2017
	 */
	private void addTopicTag(long topicId,long uid,String tag){
		TopicTag topicTag = liveMybatisDao.getTopicTagByTag(tag);
		if(null == topicTag){
			topicTag = new TopicTag();
			topicTag.setTag(tag);
			liveMybatisDao.insertTopicTag(topicTag);
		}
		if(!this.liveMybatisDao.existsTopicTag(topicId, tag)){
			TopicTagDetail tagDetail = new TopicTagDetail();
			tagDetail.setTag(tag);
			tagDetail.setTagId(topicTag.getId());
			tagDetail.setTopicId(topicId);
			tagDetail.setUid(uid);
			liveMybatisDao.insertTopicTagDetail(tagDetail);
		}
	}
	@Override
	public Response givenKingdomOpration(long uid, long givenKingdomId,String action) {
		String activeKey = "GIVEN_ACTIVE_"+uid;
		GivenKingdomDto resp = new GivenKingdomDto();
		String cacheState = cacheService.get(activeKey);
		if("ACTIVE".equals(action)){
			TopicGiven given= liveMybatisDao.getGivenKingomdById(givenKingdomId);
			if(given.getSubType()!=null && given.getSubType()==1){	//情绪王国,如果存在就更新为激活指定的信息。
				Topic topic = this.liveMybatisDao.getEmotionTopic(uid);
				if(topic!=null){
					topic.setTitle(given.getTitle());
					topic.setLiveImage(given.getCover());
					topic.setSummary(given.getSummary());
					this.liveMybatisDao.updateTopic(topic);
	        		this.addTopicTag(topic.getId(),topic.getUid(),given.getTags());
					resp.setTopicId(topic.getId());
				}else{
					 CreateKingdomDto createKingdomDto  = new CreateKingdomDto();
					 createKingdomDto.setUid(uid);
					 createKingdomDto.setTitle(given.getTitle());
					 createKingdomDto.setLiveImage(given.getCover());
					 createKingdomDto.setContentType(0);
					 createKingdomDto.setFragment(given.getSummary());
					 createKingdomDto.setSource(0);
					 createKingdomDto.setExtra("");
					 createKingdomDto.setKType(0);
					 createKingdomDto.setCExtra("");
					 createKingdomDto.setKConfig("");
					 createKingdomDto.setTags(given.getTags());
					 createKingdomDto.setSubType(1);
					 topic = createSpecialTopic(createKingdomDto);
					 resp.setTopicId(topic.getId());
				}
			}else{		// 普通王国
				CreateKingdomDto createKingdomDto  = new CreateKingdomDto();
				 createKingdomDto.setUid(uid);
				 createKingdomDto.setTitle(given.getTitle());
				 createKingdomDto.setLiveImage(given.getCover());
				 createKingdomDto.setContentType(0);
				 createKingdomDto.setFragment(given.getSummary());
				 createKingdomDto.setSource(0);
				 createKingdomDto.setExtra("");
				 createKingdomDto.setKType(0);
				 createKingdomDto.setCExtra("");
				 createKingdomDto.setKConfig("");
				 createKingdomDto.setTags(given.getTags());
				 createKingdomDto.setSubType(0);
				 Topic topic = createSpecialTopic(createKingdomDto);
				 resp.setTopicId(topic.getId());
			}
			liveMybatisDao.deleteGivenKingdomById(givenKingdomId);
			int count =liveMybatisDao.getUnActivedKingdomCount(uid);
			resp.setActived(cacheState==null?0:1);
			cacheService.set(activeKey, "1");
		}else if("DEL".equals(action)){
			liveMybatisDao.deleteGivenKingdomById(givenKingdomId);
		}
		return Response.success(resp);
	}

	@Override
	public Response getDaySignInfo(long uid) {
		DaySignInfoDto dto = new DaySignInfoDto();
		User user = userService.getUserByUid(uid);
		if (user == null) {
			return Response.failure(500, "找不到该用户！");
		}
		UserProfile userProfile = userService.getUserProfileByUid(uid);
		if (userProfile == null) {
			return Response.failure(500, "找不到该用户！");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = sdf.format(new Date());
		String yesterDay = CommonUtils.getCalculationDayStr(-1, "yyyy-MM-dd");
		List<Map<String, Object>> signRecordList = liveLocalJdbcDao.getSignRecordList(todayStr, uid);
		int signRecordCountTemp = 0;
		if (signRecordList.size() >0) {
			dto.setIsSave(1);
		} else {
			dto.setIsSave(0);
			signRecordCountTemp=1;
		}
		int signPostion = 3;
		String signPostionStr = userService.getAppConfigByKey("SIGN_POSITION");
		if (!StringUtils.isEmpty(signPostionStr)) {
			signPostion = Integer.parseInt(signPostionStr);
		}
		dto.setPosition(signPostion);
		int signRecordCount = liveLocalJdbcDao.getSignRecordCount(uid);
		dto.setSerialNumber(signRecordCount + signRecordCountTemp);
		dto.setSignDate(todayStr);
		// 判断是否是新注册第一天用户
		if (todayStr.equals(sdf.format(user.getCreateTime()))) {
			dto.setStatus(1);
			dto.setNickName(userProfile.getNickName());
			dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			dto.setTopicId(0);
			dto.setUid(0);
			dto.setFragment("这是我在米汤的第一天");
			dto.setImage("");
			dto.setTopicTitle("");
		} else {
			dto.setIsFirstDay(0);
			Map<String, Object> maxFragment = liveLocalJdbcDao.getMaxFragment(yesterDay, uid, 60, 90);
			if (maxFragment == null) {
				maxFragment = liveLocalJdbcDao.getMaxFragment(yesterDay, uid, 0, 0);
			}
			// 判断是否有文字发言
			if (maxFragment == null) {
				Map<String, Object> imageData =liveLocalJdbcDao.getFragmentImage(yesterDay, uid, 0);
				if (imageData != null) {
					Topic topic = liveMybatisDao.getTopicById(Long.parseLong(imageData.get("topic_id").toString()));
					dto.setStatus(2);
					dto.setNickName(userProfile.getNickName());
					dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
					dto.setUid(uid);
					dto.setTopicId(topic.getId());
					dto.setFragment("今天懒癌发作，什么都没说~");
					dto.setImage(Constant.QINIU_DOMAIN + "/" + imageData.get("fragment_image").toString());
					dto.setTopicTitle(topic.getTitle());
				} else {
					// 其他人标签逻辑

					Response response = searchService.recommendUser(uid, 1, 10);
					RecommendUserDto ruDto = (RecommendUserDto) response.getData();
					List<RecommendUser> recUserData = ruDto.getRecUserData();
					if (recUserData.size() > 0) {
						int[] s = CommonUtils.randomArray(0, recUserData.size() - 1, recUserData.size());
						for (int i = 0; i < s.length; i++) {
							RecommendUser recommendUser = recUserData.get(s[i]);
							if (recommendUser != null) {
								long ruid = recommendUser.getUid();
								UserProfile ruserProfile = userService.getUserProfileByUid(ruid);
								Map<String, Object> rmaxFragment = liveLocalJdbcDao.getMaxFragment(yesterDay, ruid, 60,
										90);
								if (rmaxFragment == null) {
									rmaxFragment = liveLocalJdbcDao.getMaxFragment(yesterDay, ruid, 0, 0);
								}
								if (rmaxFragment == null) {
									continue;
								} else {
									Map<String, Object> rtopicData = liveLocalJdbcDao.getFragmentImage(yesterDay, ruid,
											Long.parseLong(rmaxFragment.get("topic_id").toString()));
									Topic topic = liveMybatisDao.getTopicById(Long.parseLong(rmaxFragment.get("topic_id").toString()));
									dto.setNickName(ruserProfile.getNickName());
									dto.setAvatar(Constant.QINIU_DOMAIN + "/" + ruserProfile.getAvatar());
									dto.setUid(ruid);
									dto.setTopicId(Long.parseLong(rmaxFragment.get("topic_id").toString()));
									dto.setFragment(rmaxFragment.get("fragment").toString());
									dto.setTopicTitle(topic.getTitle());
									if (rtopicData == null) {
										dto.setImage("");
									} else {
										dto.setImage(
												Constant.QINIU_DOMAIN + "/" + rtopicData.get("fragment_image").toString());
									}
									dto.setStatus(3);
									break;
								}

							}
						}
						if (dto.getStatus() != 3) {
							return Response.failure(500, "没有日签数据！");
						}
					}
				}
			} else {
				Map<String, Object> imageData =liveLocalJdbcDao.getFragmentImage(yesterDay, uid,
						Long.parseLong(maxFragment.get("topic_id").toString())); 
				Topic topic = liveMybatisDao.getTopicById(Long.parseLong(maxFragment.get("topic_id").toString()));
				dto.setNickName(userProfile.getNickName());
				dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
				dto.setUid(uid);
				dto.setTopicId(Long.parseLong(maxFragment.get("topic_id").toString()));
				dto.setFragment(maxFragment.get("fragment").toString());
				dto.setTopicTitle(topic.getTitle());
				if (imageData == null) {
					dto.setStatus(1);
					dto.setImage("");
				} else {
					dto.setStatus(0);
					dto.setImage(Constant.QINIU_DOMAIN + "/" + imageData.get("fragment_image").toString());
				}
			}
		}
		List<Map<String, Object>> robotQuotationRecordList = liveLocalJdbcDao.getRobotQuotationRecordList(todayStr, uid);
		if (robotQuotationRecordList.size() >0) {
			for (int i = 0; i < robotQuotationRecordList.size(); i++) {
				Map<String, Object> robotQuotationRecord = robotQuotationRecordList.get(i);
				DaySignInfoDto.Quotation quotation = new DaySignInfoDto.Quotation();
				quotation.setUid(Long.parseLong(robotQuotationRecord.get("robot_uid").toString()));
				UserProfile robotProfile = userService.getUserProfileByUid(Long.parseLong(robotQuotationRecord.get("robot_uid").toString()));
				quotation.setNickName(robotProfile.getNickName());
				quotation.setAvatar(Constant.QINIU_DOMAIN + "/" + robotProfile.getAvatar());
				Long quotationId = Long.parseLong(robotQuotationRecord.get("quotation_id").toString());
				QuotationInfo quotationInfo  = liveMybatisDao.getQuotationInfoById(quotationId);
				quotation.setQuotationId(quotationId);
				quotation.setQuotation(quotationInfo.getQuotation());
				dto.getQuotations().add(quotation);
			}
		} else {
			String preWeekDay = CommonUtils.getCalculationDayStr(-7, "yyyy-MM-dd");
			List<Map<String, Object>> hisList = liveLocalJdbcDao.getHisRobotQuotationRecord(preWeekDay, uid);
			List<Long> hisRobot = new ArrayList<Long>();
			List<Long> hisQuotation = new ArrayList<Long>();
			for (int i = 0; i < hisList.size(); i++) {
				Map<String, Object> map = hisList.get(i);
				hisRobot.add(Long.parseLong(map.get("robot_uid").toString()));
				hisQuotation.add(Long.parseLong(map.get("quotation_id").toString()));
			}
			List<RobotInfo> listRobot = liveMybatisDao.getRobotInfoRandom(hisRobot);
			List<QuotationInfo> listQuotation = liveMybatisDao.getQuotationInfoRandom(hisQuotation);
			for (int i = 0; i < listRobot.size(); i++) {
				if (listQuotation.size() > i) {
					RobotInfo robotInfo = listRobot.get(i);
					DaySignInfoDto.Quotation quotation = new DaySignInfoDto.Quotation();
					quotation.setUid(robotInfo.getUid());
					UserProfile robotProfile = userService.getUserProfileByUid(robotInfo.getUid());
					quotation.setNickName(robotProfile.getNickName());
					quotation.setAvatar(Constant.QINIU_DOMAIN + "/" + robotProfile.getAvatar());
					QuotationInfo quotationInfo = listQuotation.get(i);
					quotation.setQuotationId(quotationInfo.getId());
					quotation.setQuotation(quotationInfo.getQuotation());
					dto.getQuotations().add(quotation);
				}
			}
		}
		
		
		return Response.success(dto);
	}

	@Override
	public Response saveDaySignInfo(long uid, String image,String extra,String uids,int source,String quotationIds) {
	if(StringUtils.isEmpty(uids) || StringUtils.isEmpty(quotationIds) ){
		//不做处理；
	}else{
		String[] uidArr = uids.split(",");
		 String[] quotationIdArr = quotationIds.split(",");
		 if(uidArr.length!=quotationIdArr.length){
			 return Response.failure(500, "机器人和语录数量不匹配！");
		 }
		 if(uidArr.length>0){
			 for (int i = 0; i < uidArr.length; i++) {
				String robotUid = uidArr[i];
				String quotationId = quotationIdArr[i];
				RobotQuotationRecord robotQuotationRecord = new RobotQuotationRecord();
				robotQuotationRecord.setUid(uid);
				robotQuotationRecord.setRobotUid(Long.parseLong(robotUid));
				robotQuotationRecord.setQuotationId(Long.parseLong(quotationId));
				liveMybatisDao.addRobotQuotationRecord(robotQuotationRecord);
			}
		 }
	}
	 SignRecord signRecord = new SignRecord();
	 signRecord.setUid(uid);
	 signRecord.setImage(image);
	 liveMybatisDao.addSignRecord(signRecord);
	 
	 Topic topic = liveMybatisDao.getEmotionTopic(uid);
	 if(topic==null){
	  topic = createEmotionTopic(uid);
	 }
	 if(topic!=null && topic.getId()!=null){
     SpeakDto speakDto = new SpeakDto();
     speakDto.setType(0);
     speakDto.setContentType(1);
     speakDto.setUid(uid);
     speakDto.setTopicId(topic.getId());
     speakDto.setSource(source);
     speakDto.setExtra(extra);
     speakDto.setFragmentImage(image);
     speak(speakDto);
	 }
		return Response.success();
	}
	/**
	 * 创建特殊王国
	 * @param createKingdomDto
	 * @return
	 */
	public Topic createSpecialTopic(CreateKingdomDto createKingdomDto){
		if(StringUtils.isEmpty(createKingdomDto.getLiveImage()) || StringUtils.isEmpty(createKingdomDto.getTitle())){
        	return null;
        }

		boolean isDouble = false;
		int type = 0;
		long uid2 = 0;
		String cExtraJson = createKingdomDto.getCExtra();
		JSONObject cExtraObj = null;

		Date now = new Date();
		Topic topic = new Topic();
		topic.setTitle(createKingdomDto.getTitle());
        topic.setLiveImage(createKingdomDto.getLiveImage());
        topic.setUid(createKingdomDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
        topic.setLongTime(now.getTime());
        topic.setCreateTime(now);
        topic.setUpdateTime(now);
        JSONArray array = new JSONArray();
        array.add(createKingdomDto.getUid());
        if(isDouble){
        	array.add(uid2);
        }
        topic.setCoreCircle(array.toString());
        //聚合版本新加属性
        int kingdomType = Specification.KingdomType.NORMAL.index;
        topic.setType(kingdomType);
        topic.setRights(Specification.KingdomRights.PUBLIC_KINGDOM.index);//目前默认公开的，等以后有需求的再说
        topic.setSummary(createKingdomDto.getFragment());//目前，第一次发言即王国简介
        topic.setCeAuditType(0);//聚合王国属性，是否需要国王审核才能加入此聚合王国，默认0是
        topic.setAcAuditType(1);//个人王国属性，是否需要国王审核才能收录此王国，默认1否
        topic.setAcPublishType(0);//个人王国属性，是否接受聚合王国下发的消息，默认0是
        topic.setSubType(createKingdomDto.getSubType());
        //初始化王国价值，默认估值:米汤币为15,随机增减0-8
        int price = 15;
        Random random = new Random();
        int incr = random.nextInt(9);
        int flag = random.nextInt(2);
        if(flag == 0){
        	price = price - incr;
        }else{
        	price = price + incr;
        }
        
        //查询新建王国可以被偷的配置值
        int newStealPrice = 0;
        String newKingdomStealPrice = userService.getAppConfigByKey("NEW_KINGDOM_STEAL_PRICE");
        if(!StringUtils.isEmpty(newKingdomStealPrice)){
        	newStealPrice = Integer.valueOf(newKingdomStealPrice).intValue();
        }
        
        topic.setPrice(price + newStealPrice);
        liveMybatisDao.createTopic(topic);

        //创建直播之后添加到我的UGC
        ContentDto contentDto = new ContentDto();
        contentDto.setContent(createKingdomDto.getTitle());
        contentDto.setFeeling(createKingdomDto.getTitle());
        contentDto.setTitle(createKingdomDto.getTitle());
        contentDto.setImageUrls(createKingdomDto.getLiveImage());
        contentDto.setUid(createKingdomDto.getUid());
        contentDto.setType(Specification.ArticleType.LIVE.index);
        contentDto.setForwardCid(topic.getId());
        contentDto.setRights(Specification.ContentRights.EVERY.index);
        contentService.publish(contentDto);
        
        applicationEventBus.post(new CacheLiveEvent(createKingdomDto.getUid(), topic.getId()));

        //创建王国之后创建相应的价值数据表（主要是可以创建后即可有一定的被偷值）
        TopicData td = new TopicData();
        td.setApprove(0d);
        td.setDiligently(0d);
        td.setLastPrice(0);
        td.setLastPriceIncr(0);
        td.setReviewTextCount(0);
        td.setReviewTextLength(0);
        td.setStealPrice(newStealPrice);
        td.setTopicId(topic.getId());
        td.setUpdateAudioCount(0);
        td.setUpdateAudioLength(0);
        td.setUpdateDayCount(0);
        td.setUpdateImageCount(0);
        td.setUpdateTeaseCount(0);
        td.setUpdateTextCount(0);
        td.setUpdateTextLength(0);
        td.setUpdateTime(now);
        td.setUpdateVedioCount(0);
        td.setUpdateVedioLength(0);
        td.setUpdateVoteCount(0);
        liveMybatisDao.saveTopicData(td);
        
        
        SpeakDto speakDto2 = new SpeakDto();
        speakDto2.setTopicId(topic.getId());
        UserProfile profile = userService.getUserProfileByUid(createKingdomDto.getUid());
        speakDto2.setV_lv(profile.getvLv());
        //检查有没有出错的数据，如果有则删除出错数据
        contentService.clearData();

        log.info("first speak...");
        long lastFragmentId = 0;
        long total = 0;
        if(createKingdomDto.getContentType() == 0){
        	TopicFragment topicFragment = new TopicFragment();
        	topicFragment.setFragment(createKingdomDto.getFragment());
        	topicFragment.setUid(createKingdomDto.getUid());
        	topicFragment.setType(0);//第一次发言肯定是主播发言
        	topicFragment.setContentType(0);
        	topicFragment.setTopicId(topic.getId());
            topicFragment.setBottomId(0l);
            topicFragment.setTopId(0l);
            topicFragment.setSource(createKingdomDto.getSource());
            topicFragment.setExtra(createKingdomDto.getExtra());
            topicFragment.setCreateTime(now);
            liveMybatisDao.createTopicFragment(topicFragment);
            lastFragmentId = topicFragment.getId();
            total++;
        }else{//图片
        	String[] imgs = createKingdomDto.getFragment().split(";");
        	Map<String, String> map = new HashMap<String, String>();
        	String extra = createKingdomDto.getExtra();
        	if(!StringUtils.isEmpty(extra)){
        		JSONArray obj = JSON.parseArray(extra);
        		if(!obj.isEmpty()){
        			for(int i=0;i<obj.size();i++){
        				map.put(String.valueOf(i), obj.getJSONObject(i).toJSONString());
        			}
        		}
        	}

        	if(null != imgs && imgs.length > 0){
        		TopicFragment topicFragment = null;
        		String e = null;
        		for(int i=0;i<imgs.length;i++){
        			topicFragment = new TopicFragment();
                	topicFragment.setFragmentImage(imgs[i]);
                	topicFragment.setUid(createKingdomDto.getUid());
                	topicFragment.setType(0);//第一次发言肯定是主播发言
                	topicFragment.setContentType(1);
                	topicFragment.setTopicId(topic.getId());
                    topicFragment.setBottomId(0l);
                    topicFragment.setTopId(0l);
                    topicFragment.setSource(createKingdomDto.getSource());
                    topicFragment.setCreateTime(now);
                    e = map.get(String.valueOf(i));
                    if(null == e){
                    	e = "";
                    }
                    topicFragment.setExtra(e);
                    liveMybatisDao.createTopicFragment(topicFragment);
                    lastFragmentId = topicFragment.getId();
                    total++;
        		}
        	}
        }

        //特殊王国需要做一点特殊处理
        if(createKingdomDto.getKType() != Specification.KingdomType.NORMAL.index
        		&& createKingdomDto.getKType() != Specification.KingdomType.AGGREGATION.index){
        	if(type == Specification.ActivityKingdomType.SPRINGKING.index){
        		activityService.createActivityKingdom4Spring(topic.getId(), createKingdomDto.getUid());
        	}else{
        		activityService.createActivityKingdom(topic.getId(), createKingdomDto.getUid(), type, uid2);
        	}
        }

        //--add update kingdom cache -- modify by zcl -- begin --
		String value = lastFragmentId + "," + total;
        cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + topic.getId(), value);
        //--add update kingdom cache -- modify by zcl -- end --
        // 找到机器TAG
        
        Set<String> autoTagSet = new HashSet<>();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(createKingdomDto.getAutoTags())){
        	for(String t:createKingdomDto.getAutoTags().split(";")){
        		autoTagSet.add(t.trim());
        	}
        }
        //add kingdom tags -- begin --
        if(!StringUtils.isEmpty(createKingdomDto.getTags())){
        	String[] tags = createKingdomDto.getTags().split(";");
        	if(null != tags && tags.length > 0){
        		TopicTag topicTag = null;
        		TopicTagDetail tagDetail = null;
        		for(String tag : tags){
        			tag = tag.trim();
        			if(!StringUtils.isEmpty(tag)){
        				topicTag = liveMybatisDao.getTopicTagByTag(tag);
        				if(null == topicTag){
        					topicTag = new TopicTag();
        					topicTag.setTag(tag);
        					liveMybatisDao.insertTopicTag(topicTag);
        				}
        			}
        			tagDetail = new TopicTagDetail();
        			tagDetail.setTag(tag);
        			tagDetail.setTagId(topicTag.getId());
        			tagDetail.setTopicId(topic.getId());
        			tagDetail.setUid(createKingdomDto.getUid());
        			if(autoTagSet.contains(tag)){
        				tagDetail.setAutoTag(1);
        			}else{
        				tagDetail.setAutoTag(0);
        			}
        			liveMybatisDao.insertTopicTagDetail(tagDetail);
        		}
        	}
        }
        return topic;
	}
	/**
	 * 创建情绪王国
	 * @param uid
	 * @return
	 */
	public Topic createEmotionTopic(long uid){
		 UserProfile userProfile = userService.getUserProfileByUid(uid);
		 CreateKingdomDto createKingdomDto  = new CreateKingdomDto();
		 createKingdomDto.setUid(uid);
		 createKingdomDto.setTitle(userProfile.getNickName()+"的生活记录");
		 List<String> randomCover = userService. getRandomKingdomCover(1);
		 if(randomCover.size()>0){
			 createKingdomDto.setLiveImage(randomCover.get(0));
		 }
		 createKingdomDto.setContentType(0);
		 createKingdomDto.setFragment("吃喝玩乐，记录我的日常。");
		 createKingdomDto.setSource(0);
		 JSONObject extra = new JSONObject();
         extra.put("type", "textNormal");
         extra.put("only", UUID.randomUUID().toString() + "-" + new Random().nextInt());
         extra.put("hAlign", "center");
		 createKingdomDto.setExtra(extra.toJSONString());
		 createKingdomDto.setKType(0);
		 createKingdomDto.setCExtra("");
		 createKingdomDto.setKConfig("");
		 createKingdomDto.setTags("非典型性话痨");
		 createKingdomDto.setSubType(1);
		 Topic topic = createSpecialTopic(createKingdomDto);
		 return topic;
	}
	@Override
	public Response saveSignSaveRecord(long uid,int type){
		SignSaveRecord signSaveRecord = new SignSaveRecord();
		signSaveRecord.setUid(uid);
		signSaveRecord.setType(type);
		liveMybatisDao.saveSignSaveRecord(signSaveRecord);
		return Response.success();
	}

    @Override
    public RobotInfo selectRobotInfo() {
        return liveMybatisDao.getRobotInfo();
    }

    @Override
    public QuotationInfo selectQuotation() {
        return liveMybatisDao.getQuotationInfo();
    }

    @Override
    public List<QuotationInfo> selectQuotationByList(int limit) {
        return liveMybatisDao.getQuotationInfoByList(limit);
    }

    @Override
    public Response userKingdomInfo(long uid){
    	UserKingdomInfoDTO result = new UserKingdomInfoDTO();
    	result.setValidKingdomCount(liveMybatisDao.getUserTopicCount(uid));
    	result.setTotalKingdomCount(liveLocalJdbcDao.getUserTotalKingdomCount(uid));
    	
    	return Response.success(result);
    }
    
    @Override
    public Response searchRobotListPage(String nickName,int page, int pageSize){
     	int totalRecord = liveLocalJdbcDao.countRobotListByNickName(nickName);
    	int totalPage = (totalRecord + pageSize - 1) / pageSize;
    	if(page>totalPage){
    		page=totalPage;
    	}
    	if(page<1){
    		page=1;
    	}
    	int start = (page-1)*pageSize;
    	List<Map<String, Object>> list = liveLocalJdbcDao.getRobotListByNickName(nickName,start, pageSize);
    	SearchRobotListDto dto = new SearchRobotListDto();
        dto.setTotalRecord(totalRecord);
        dto.setTotalPage(totalPage);
        for(Map<String, Object> map : list){
        	SearchRobotListDto.RobotElement e = dto.createTopicListedElement();
        	 e.setId((Long)map.get("id"));
        	 e.setUid((Long)map.get("uid"));
        	 e.setNickName( map.get("nick_name").toString());
        	 e.setAvatar(Constant.QINIU_DOMAIN + "/" +  map.get("avatar").toString());
            dto.getResult().add(e);
        }
        return Response.success(dto);
    }
    @Override
    public Response searchQuotationListPage(String quotation,int page, int pageSize){
     	int totalRecord = liveLocalJdbcDao.countQuotationListByQuotation(quotation);
    	int totalPage = (totalRecord + pageSize - 1) / pageSize;
    	if(page>totalPage){
    		page=totalPage;
    	}
    	if(page<1){
    		page=1;
    	}
    	int start = (page-1)*pageSize;
    	List<Map<String, Object>> list = liveLocalJdbcDao.getQuotationListByQuotation(quotation,start, pageSize);
    	SearchQuotationListDto dto = new SearchQuotationListDto();
        dto.setTotalRecord(totalRecord);
        dto.setTotalPage(totalPage);
        for(Map<String, Object> map : list){
        	SearchQuotationListDto.QuotationElement e = dto.createQuotationElement();
        	 e.setId((Long)map.get("id"));
        	 e.setType((Integer)map.get("type"));
        	 e.setQuotation( map.get("quotation").toString());
        	 e.setCreateTime((Date)map.get("create_time"));
            dto.getResult().add(e);
        }
        return Response.success(dto);
    }
    
    @Override
	public int saveQuotationInfo(QuotationInfo quotationInfo){
		return liveMybatisDao.saveQuotationInfo(quotationInfo);
	}
    @Override
	public int updateQuotationInfo(QuotationInfo quotationInfo){
		return liveMybatisDao.updateQuotationInfo(quotationInfo);
	}
    
    @Override
	public int delQuotationInfo(long id){
    	return liveMybatisDao.delQuotationInfo(id);
	}
    @Override
	public QuotationInfo getQuotationInfoById(long id){
    	return liveMybatisDao.getQuotationInfoById(id);
	}
    
}
