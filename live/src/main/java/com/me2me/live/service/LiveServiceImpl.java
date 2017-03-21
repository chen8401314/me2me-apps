package com.me2me.live.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.TopicCountDTO;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.service.ActivityService;
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
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.core.QRCodeUtil;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveLocalJdbcDao;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.event.AggregationPublishEvent;
import com.me2me.live.event.CacheLiveEvent;
import com.me2me.live.event.CoreAggregationRemindEvent;
import com.me2me.live.event.RemindAndJpushAtMessageEvent;
import com.me2me.live.event.SpeakEvent;
import com.me2me.live.event.TopicNoticeEvent;
import com.me2me.live.mapper.TopicFragmentTemplateMapper;
import com.me2me.live.model.*;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.*;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    

    @Value("#{app.live_web}")
    private String live_web;

    /** 王国发言(评论等)最新ID */
    public static final String TOPIC_FRAGMENT_NEWEST_MAP_KEY = "TOPIC_FRAGMENT_NEWEST";
    
    /** 聚合王国内容下发次数 */
    private static final String TOPIC_AGGREGATION_PUBLISH_COUNT = "TOPIC_AGGREGATION_PUBLISH_COUNT";
    
    //置顶次数
    private static final String TOP_COUNT = "topCount";

    @SuppressWarnings("rawtypes")
	@Override
    public Response createLive(CreateLiveDto createLiveDto) {
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
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        log.info("get timeLine data");
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        log.info("buildLiveTimeLine success");
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
                fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
                liveMybatisDao.createLiveReadHistory(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getUid());
            } else {
                fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), Integer.MAX_VALUE);
            }
        } else if (getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.NEXT.index) {
            fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        } else if (getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.PREV.index) {
            fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        }
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status, ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message, liveTimeLineDto);
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Response liveCover(long topicId, long uid) {
        log.info("liveCover start ...");
        LiveCoverDto liveCoverDto = new LiveCoverDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(topic==null){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
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
        
        if(content.getReadCount() == 1 || content.getReadCount() == 2){
            liveCoverDto.setReadCount(1);
            content.setReadCountDummy(1);
            contentService.updateContentById(content);
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
        }

        // 添加成员数量
        List<LiveFavorite> list = liveMybatisDao.getFavoriteAll(topicId);
        if (list != null && list.size() > 0) {
            liveCoverDto.setMembersCount(list.size());
        } else {
            liveCoverDto.setMembersCount(0);
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
    public Response getLiveByCid(long cid, long uid) {
        ShowLiveDto showLiveDto = new ShowLiveDto();
        Topic topic = liveMybatisDao.getTopicById(cid);
        if(topic==null){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
        }
        Content content = contentService.getContentByTopicId(cid);
        showLiveDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        showLiveDto.setUid(topic.getUid());
        UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
        showLiveDto.setV_lv(userProfile.getvLv());
        showLiveDto.setNickName(userProfile.getNickName());
        showLiveDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        showLiveDto.setCreateTime(topic.getCreateTime());
        showLiveDto.setUpdateTime(topic.getLongTime());
        showLiveDto.setFavoriteCount(content.getFavoriteCount()+1);
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
        
        return Response.success(showLiveDto);
    }

    private void buildLiveTimeLine(GetLiveTimeLineDto getLiveTimeLineDto, LiveTimeLineDto liveTimeLineDto, List<TopicFragment> fragmentList) {
        Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
        for (TopicFragment topicFragment : fragmentList) {
            long uid = topicFragment.getUid();

            LiveTimeLineDto.LiveElement liveElement = LiveTimeLineDto.createElement();
            int status = topicFragment.getStatus();
            liveElement.setStatus(status);
            liveElement.setId(topicFragment.getId());
            if(status==0){
                //2.1.3以后的版本返回已删除的记录
                if(CommonUtils.afterVersion(getLiveTimeLineDto.getVersion(),"2.1.3")){
                    liveTimeLineDto.getLiveElements().add(liveElement);
                }
                continue;
            }

            UserProfile userProfile = userService.getUserProfileByUid(uid);
            liveElement.setUid(uid);
            liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            liveElement.setV_lv(userProfile.getvLv());
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
            liveElement.setSource(topicFragment.getSource());
            if(!StringUtils.isEmpty(topicFragment.getSource())) {
                liveElement.setExtra(topicFragment.getExtra());
            }

            liveElement.setInternalStatus(getInternalStatus(topic, uid));
            if (null != topicFragment.getAtUid() && topicFragment.getAtUid() != 0) {
            	if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
	                UserProfile atUser = userService.getUserProfileByUid(topicFragment.getAtUid());
	                liveElement.setAtUid(atUser.getUid());
	                liveElement.setAtNickName(atUser.getNickName());
            	}
            }
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
        //如果是主播发言更新cache
        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index||speakDto.getType()==Specification.LiveSpeakType.AT_CORE_CIRCLE.index||speakDto.getType()==Specification.LiveSpeakType.ANCHOR_AT.index||speakDto.getType()==Specification.LiveSpeakType.FANS.index||speakDto.getType()==Specification.LiveSpeakType.AT.index) {
            //只更新大王发言,如果是主播(大王)发言更新cache
//            Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
//            //小王发言更新cache（topic.getUid() == speakDto.getUid()为该王国国王 通知所有人）
//            if(topic.getUid() == speakDto.getUid()) {
            SpeakEvent speakEvent = new SpeakEvent();
            speakEvent.setTopicId(speakDto.getTopicId());
            speakEvent.setType(speakDto.getType());
            speakEvent.setUid(speakDto.getUid());
            String atUids = "";
            if(speakDto.getAtUid()==-1){
                JSONObject fragment = JSON.parseObject(speakDto.getFragment());
                if(fragment!=null){
                    JSONArray array = fragment.containsKey("atArray")?fragment.getJSONArray("atArray"):null;
                    atUids = array.toJSONString().replaceAll("[\\[|\\]]",",");
                }
            }else{
               atUids =CommonUtils.wrapString(speakDto.getAtUid(),",");
            }
            speakEvent.setAtUids(atUids);

            applicationEventBus.post(speakEvent);
//            }
            //粉丝有留言提醒主播
        } else {
        	//这部分貌似不需要了。。有问题再改回来。。20170206
//            if (speakDto.getType() != Specification.LiveSpeakType.INVITED.index && speakDto.getType() != Specification.LiveSpeakType.SHARE.index && speakDto.getType() != Specification.LiveSpeakType.SUBSCRIBED.index && speakDto.getType() != Specification.LiveSpeakType.FORWARD.index && speakDto.getType() != Specification.LiveSpeakType.FOLLOW.index && speakDto.getType() != Specification.LiveSpeakType.LIKES.index) {
//                Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
//                MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(topic.getUid(), topic.getId() + "", "1");
//                log.info("speak by other start update hset cache key{} field {} value {}", cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
//                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
//                //直播回复的推送
//                if (speakDto.getType() ==
//                        Specification.LiveSpeakType.FANS.index) {
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("messageType", Specification.PushMessageType.LIVE_REVIEW.index);
//                    String alias = String.valueOf(topic.getUid());
//                    UserProfile userProfile = userService.getUserProfileByUid(speakDto.getUid());
//                    jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "评论了你", JPushUtils.packageExtra(jsonObject));
//
//                }
//            }
        }
        long fid = 0;
        if (speakDto.getType() != Specification.LiveSpeakType.LIKES.index && speakDto.getType() != Specification.LiveSpeakType.SUBSCRIBED.index && speakDto.getType() != Specification.LiveSpeakType.SHARE.index && speakDto.getType() != Specification.LiveSpeakType.FOLLOW.index && speakDto.getType() != Specification.LiveSpeakType.INVITED.index) {
            TopicFragment topicFragment = new TopicFragment();
            topicFragment.setFragmentImage(speakDto.getFragmentImage());
            topicFragment.setFragment(speakDto.getFragment());
            topicFragment.setUid(speakDto.getUid());
            topicFragment.setContentType(speakDto.getContentType());
            topicFragment.setType(speakDto.getType());
            topicFragment.setTopicId(speakDto.getTopicId());
            topicFragment.setBottomId(speakDto.getBottomId());
            topicFragment.setTopId(speakDto.getTopId());
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

            Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
            Calendar calendar = Calendar.getInstance();
            topic.setUpdateTime(calendar.getTime());
            topic.setLongTime(calendar.getTimeInMillis());
            liveMybatisDao.updateTopic(topic);
            log.info("updateTopic updateTime");
            
            fid = topicFragment.getId();
            
            //--add update kingdom cache -- modify by zcl -- begin --
            //此处暂不考虑原子操作
            int total = liveMybatisDao.countFragmentByTopicId(speakDto.getTopicId());
            String value = fid + "," + total;
            cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + speakDto.getTopicId(), value);
            //--add update kingdom cache -- modify by zcl -- end --
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
        if (speakDto.getType() == Specification.LiveSpeakType.LIKES.index) {
            LikeDto likeDto = new LikeDto();
            //点赞
            Content content = contentService.getContentByTopicId(speakDto.getTopicId());
            likeDto.setCid(content.getId());
            likeDto.setAction(0);
            likeDto.setUid(speakDto.getUid());
            likeDto.setType(Specification.LikesType.LIVE.index);
            contentService.like2(likeDto);
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
        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index) {
//            List<LiveFavorite> list = liveMybatisDao.getFavoriteList(speakDto.getTopicId());
//            for(LiveFavorite liveFavorite : list) {
//                //主播发言提醒关注的人
//                //userService.push(liveFavorite.getUid(),topic.getUid(),Specification.PushMessageType.UPDATE.index,topic.getTitle());
//                log.info("update push");
//            }
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
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status, ResponseStatus.USER_SPEAK_SUCCESS.message, speakDto);
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
        log.info("getMyLives end ...");
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
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setLastUpdateTime(topic.getLongTime());
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
        List<Map<String, Object>> lastFragmentList = liveLocalJdbcDao.getLastCoreCircleFragmentByTopicIds2(tidList);
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
        for (Topic2 topic : topicList) {
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
                //新增
                showTopicElement.setLastType((Integer) lastFragment.get("type"));
                showTopicElement.setLastStatus((Integer)lastFragment.get("status"));
                showTopicElement.setLastExtra((String)lastFragment.get("extra"));
                showTopicElement.setIsTop(topic.getIsTop());
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
        if (!StringUtils.isEmpty(isUpdate)) {
            showTopicElement.setIsUpdate(Integer.parseInt(isUpdate));
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
    		resp = Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    	} else if (action == 1) {//取消订阅
    		if(null != liveFavorite){
    			liveMybatisDao.deleteLiveFavorite(liveFavorite);
    			//content表favorite_count-1
    			liveLocalJdbcDao.contentAddFavoriteCount(topicId, 0);
    		}
    		//如果是核心圈的，取消订阅的同时，需要退出核心圈
    		if(null != topic.getCoreCircle() && !"".equals(topic.getCoreCircle())){
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
        try {
        	//判断当前用户是否有删除本条内容的权限
        	boolean canDel = false;
        	//判断是否是管理员，管理员啥都能删
        	if(userService.isAdmin(uid)){
        		canDel = true;
        	}
        	if(!canDel){
        		//再验证是否是国王，国王也啥都能删
        		Topic topic = liveMybatisDao.getTopicById(topicId);
        		if(topic.getUid() == uid){
        			canDel = true;
        		}
        	}
        	if(!canDel){
        		//再判断是否是自己发的内容，自己的内容有可能是可以删
        		TopicFragment tf = liveMybatisDao.getTopicFragmentById(fid);
        		if(tf.getUid() == uid){
        			//再判断是否是卡片（核心圈发言、核心圈@），卡片不能删
        			if(tf.getType() != Specification.LiveSpeakType.ANCHOR.index
        					&& tf.getType() != Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
        				canDel = true;
        			}
        		}
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
                ShowTopicListDto.AttentionElement attentionElement = showTopicListDto.createAttentionElement();
                UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
                attentionElement.setAvatar(userProfile.getAvatar());
                attentionElement.setUid(content.getUid());
                attentionElement.setV_lv(userProfile.getvLv());
                showTopicListDto.getAttentionData().add(attentionElement);
                if(size == 5){
                    break;
                }
            }
        }
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if (updateTime == 0) {
            updateTime = Long.MAX_VALUE;
        }
        List<Topic2> topicList = liveMybatisDao.getMyLivesByUpdateTimeNew(uid ,updateTime);
        log.info("getMyLives data success");
        builderWithCache2(uid, showTopicListDto, topicList);
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
        int count = 0;
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

            UserProfile userProfile = userService.getUserProfileByUid(uid);
            liveElement.setUid(uid);
            liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            liveElement.setV_lv(userProfile.getvLv());
            String fragmentImage = topicFragment.getFragmentImage();
            if (!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(), getLiveDetailDto.getUid());
            liveElement.setIsFollowed(isFollow);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveElement.setSource(topicFragment.getSource());
            liveElement.setExtra(topicFragment.getExtra());

            liveElement.setInternalStatus(getInternalStatus(topic, uid));
            if (null != topicFragment.getAtUid() && topicFragment.getAtUid() > 0){
            	if(topicFragment.getType() == Specification.LiveSpeakType.AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.ANCHOR_AT.index
            			|| topicFragment.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){
	                UserProfile atUser = userService.getUserProfileByUid(topicFragment.getAtUid());
	                liveElement.setAtUid(atUser.getUid());
	                liveElement.setAtNickName(atUser.getNickName());
            	}
            }
            if(getLiveDetailDto.getDirection() == Specification.LiveDetailDirection.DOWN.index){
            	liveDetailDto.getLiveElements().add(liveElement);
            }else{
            	liveDetailDto.getLiveElements().add(count, liveElement);
            }
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
	public Response createKingdom(CreateKingdomDto createKingdomDto) {
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
        }
        topic.setType(kingdomType);
        topic.setRights(Specification.KingdomRights.PUBLIC_KINGDOM.index);//目前默认公开的，等以后有需求的再说
        topic.setSummary(createKingdomDto.getFragment());//目前，第一次发言即王国简介
        topic.setCeAuditType(0);//聚合王国属性，是否需要国王审核才能加入此聚合王国，默认0是
        topic.setAcAuditType(1);//个人王国属性，是否需要国王审核才能收录此王国，默认1否
        topic.setAcPublishType(0);//个人王国属性，是否接受聚合王国下发的消息，默认0是
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
        
        log.info("createKingdom end");
        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status, ResponseStatus.USER_CREATE_LIVE_SUCCESS.message, speakDto2);
	}

	@Override
	public Response kingdomSearch(long currentUid, KingdomSearchDTO searchDTO){
		List<Map<String,Object>> topicList = new ArrayList<Map<String,Object>>();
		boolean first = false;
		if(searchDTO.getUpdateTime() == 0){//第一次
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
			topicList = liveLocalJdbcDao.getKingdomListBySearchScene(currentUid, searchDTO);
		}
		
		ShowTopicSearchDTO showTopicSearchDTO = new ShowTopicSearchDTO();
		if(null != topicList && topicList.size() > 0){
			this.builderTopicSearch(currentUid, showTopicSearchDTO, topicList, topMap, publishMap);
		}
		return Response.success(showTopicSearchDTO);
	}

    @Override
    public Response settings(long uid, long topicId) {
        SettingsDto dto = new SettingsDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        if(topic != null) {
            dto.setTopicId(topicId);
            dto.setCoverImage(Constant.QINIU_DOMAIN+"/"+topic.getLiveImage());
            dto.setTitle(topic.getTitle());
            Content content = contentService.getContentByTopicId(topic.getId());
            if(content != null) {
                dto.setReadCount(content.getReadCountDummy());
                dto.setFavoriteCount(content.getFavoriteCount()+1);
            }
            TopicCountDTO topicCountDTO = activityService.getTopicCount(topicId);
            dto.setTopicCount(topicCountDTO.getUpdateCount());
            dto.setCreateTime(topic.getLongTime());
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
				Content content = contentService.getContentByTopicId(topic.getId());
				if(null != content){
					content.setConverImage(dto.getParams());
					contentService.updateContentById(content);
				}
				log.info("update cover success");
				return Response.success();
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

					// 更新缓存
					int total = liveMybatisDao.countFragmentByTopicId(topic.getId());
					String value = lastFragmentId + "," + total;
					cacheService.hSet(TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + topic.getId(), value);
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
            e.setUid(topicUid);
            e.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
            e.setTitle((String)topic.get("title"));
            e.setCreateTime((Date)topic.get("create_time"));
            e.setTopicId(topicId);
            e.setStatus((Integer)topic.get("status"));
            e.setUpdateTime((Long)topic.get("long_time"));
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
            if (!StringUtils.isEmpty(isUpdate)) {
                e.setIsUpdate(Integer.parseInt(isUpdate));
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
                e.setFavoriteCount(content.getFavoriteCount()+1);
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
                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
                    	userService.noticeMessagePush(topic.getUid(), "有个人王国退出了你的聚合王国", 2);
                    }
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
                    if(this.checkTopicPush(topic.getId(), topic.getUid())){
                    	userService.noticeMessagePush(topic.getUid(), "有聚合王国踢走了你的个人王国", 2);
                    }
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
                if(this.checkTopicPush(topic.getId(), topicAggregationApply.getOperator().longValue())){
                	userService.noticeMessagePush(topicAggregationApply.getOperator().longValue(), message, 2);
                }
                
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
        
        userService.createUserNotice(userNotice);

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
    	obj.put("linkColor", 2);//从0算起
    	obj.put("linkColor", acTopic.getTitle().length()+2);
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
    	
    	Calendar calendar = Calendar.getInstance();
    	ceTopic.setUpdateTime(calendar.getTime());
    	ceTopic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.updateTopic(ceTopic);
        
        //更新缓存
        long ceLastFragmentId = ceFragment.getId();
        int ceTotal = liveMybatisDao.countFragmentByTopicId(ceTopic.getId());
        String ceValue = ceLastFragmentId + "," + ceTotal;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + ceTopic.getId(), ceValue);
    	
        
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
    	obj2.put("linkColor", 2);//从0算起
    	obj2.put("linkColor", acFragmentContent.length());
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
    	
    	calendar = Calendar.getInstance();
    	acTopic.setUpdateTime(calendar.getTime());
    	acTopic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.updateTopic(acTopic);
        
        //更新缓存
        long acLastFragmentId = acFragment.getId();
        int acTotal = liveMybatisDao.countFragmentByTopicId(acTopic.getId());
        String acValue = acLastFragmentId + "," + acTotal;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + acTopic.getId(), acValue);
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
		
//		if(isCoreUser){//如果是核心圈的发言，则需要更新评论缓存
			Calendar calendar = Calendar.getInstance();
			targetTopic.setUpdateTime(calendar.getTime());
			targetTopic.setLongTime(calendar.getTimeInMillis());
            liveMybatisDao.updateTopic(targetTopic);
//		}
		
		//更新缓存
		long lastFragmentId = newtf.getId();
        int total = liveMybatisDao.countFragmentByTopicId(targetTopicId);
        String value = lastFragmentId + "," + total;
        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + targetTopicId, value);
    	
        //推送&&红点等设置
        TopicNoticeEvent event = new TopicNoticeEvent(uid, targetTopicId);
        this.applicationEventBus.post(event);
        
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
        String number = cacheService.hGet("droparound" ,uid+"@"+now);
        if(!StringUtils.isEmpty(number)){
            //有的话取
            dr = Integer.parseInt(number);
        }
        //每次进来+1 控制每人每天五次
        cacheService.hSet("droparound" ,uid+"@"+now ,String.valueOf(dr+1));
        log.info("key:droparound filed:{}@{} value: {}" ,uid , now ,dr+1);
        cacheService.expire(uid+"@"+now ,3600*24);//24小时过期

        //控制每个用户避免进入重复的
        Set<String> s = cacheService.smembers("list:user@"+uid );
        String set = org.apache.commons.lang3.StringUtils.join(s.toArray(), ",");
        DropAroundDto dto = new DropAroundDto();

        if(sourceTopicId == 0){
            //注册页进来
            setDropaRoundDto(dto ,uid ,set);
        }else {
            if(dr <= 5){
                setDropaRoundDto(dto ,uid ,set);
            }else {
                //算法取王国
                setDropaRoundDtoAlgorithm(dto ,uid ,set);
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

    public void setDropaRoundDto(DropAroundDto dto ,long uid ,String set){
        Map<String ,String> map = Maps.newHashMap();
        map.put("uid",String.valueOf(uid));
        map.put("set",set);
        //随机获取一条王国
        TopicDroparound droparound = liveMybatisDao.getRandomDropaRound(map);
        if(droparound == null){
            //没有数据了 算法取
            setDropaRoundDtoAlgorithm(dto ,uid ,set);
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
            if (topicFragmentTemplate != null) {
                dto.setTrackContent(topicFragmentTemplate.getContent());
            }
        }
            log.info("setDropaRoundDto is ok");
    }

    //算法取
    public void setDropaRoundDtoAlgorithm(DropAroundDto dto ,long uid ,String set){
        Map<String ,String> map = Maps.newHashMap();
        System.out.println(set);
        map.put("uid",String.valueOf(uid));
        map.put("set",set);
        //随机获取一条王国
        Topic topicInfo;
        topicInfo = liveMybatisDao.getRandomDropaRoundAlgorithm(map);
        if(topicInfo == null){
            //topic王国取完了
            cacheService.del("list:user@" + uid);
            topicInfo = liveMybatisDao.getRandomDropaRoundAlgorithm(map);
        }
        Topic topic = liveMybatisDao.getTopicById(topicInfo.getId());
        Content content = contentService.getContentByTopicId(topicInfo.getId());
        if(topic != null){
            int status = this.getInternalStatus(topic ,uid);
            dto.setInternalStatus(status);
            dto.setTopicType(topic.getType());
        }if(content != null){
            dto.setCid(content.getId());
        }
        dto.setTopicId(topicInfo.getId());
        cacheService.sadd("list:user@"+uid ,String.valueOf(topicInfo.getId()));
        TopicFragmentTemplate topicFragmentTemplate = liveMybatisDao.getTopicFragmentTemplate();
        if(topicFragmentTemplate != null){
            dto.setTrackContent(topicFragmentTemplate.getContent());
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
	List<SearchDropAroundTopicDto> buildShowTopicList(List<Topic> list){
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
}
