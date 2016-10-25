package com.me2me.live.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.JPushUtils;
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
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.event.CacheLiveEvent;
import com.me2me.live.event.SpeakEvent;
import com.me2me.live.model.*;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.*;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    @Value("#{app.live_web}")
    private String live_web;


    @Override
    public Response createLive(CreateLiveDto createLiveDto) {
        log.info("createLive start ...");
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

    @Override
    public Response liveCover(long topicId, long uid) {
        log.info("liveCover start ...");
        LiveCoverDto liveCoverDto = new LiveCoverDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
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

        liveCoverDto.setInternalStatus(getInternalStatus(topic,uid));
        liveCoverDto.setLiveWebUrl(Constant.Live_WEB_URL+topicId);//返回直播URL地址
        //添加直播阅读数log.info("liveCover end ...");
        Content content = contentService.getContentByTopicId(topicId);
        content.setReadCount(content.getReadCount() + 1);
        contentService.updateContentById(content);
        // 添加成员数量
        liveCoverDto.setReadCount((int) Math.rint(content.getReadCount() * 5.3));
        List<LiveFavorite> list = liveMybatisDao.getFavoriteAll(topicId);
        if (list != null && list.size() > 0) {
            liveCoverDto.setMembersCount(list.size());
        } else {
            liveCoverDto.setMembersCount(0);
        }
        return Response.success(ResponseStatus.GET_LIVE_COVER_SUCCESS.status, ResponseStatus.GET_LIVE_COVER_SUCCESS.message, liveCoverDto);
    }

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

    @Override
    public Response getLiveByCid(long cid, long uid) {
        ShowLiveDto showLiveDto = new ShowLiveDto();
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        showLiveDto.setV_lv(userProfile.getvLv());
        Topic topic = liveMybatisDao.getTopicById(cid);
        if(topic==null){
            return Response.failure(ResponseStatus.LIVE_HAS_DELETED.status,ResponseStatus.LIVE_HAS_DELETED.message);
        }
        Content content = contentService.getContentByTopicId(cid);
        showLiveDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        showLiveDto.setUid(topic.getUid());
        showLiveDto.setNickName(userProfile.getNickName());
        showLiveDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        showLiveDto.setCreateTime(topic.getCreateTime());
        showLiveDto.setUpdateTime(topic.getLongTime());
        showLiveDto.setFavoriteCount(content.getFavoriteCount());
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
        showLiveDto.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid(), content.getUid()));
        showLiveDto.setTitle(topic.getTitle());
        showLiveDto.setStatus(topic.getStatus());
        showLiveDto.setIsLike(contentService.isLike(content.getId(), uid));
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
            liveElement.setExtra(topicFragment.getExtra());

            liveElement.setInternalStatus(getInternalStatus(topic, uid));
            if (topicFragment.getAtUid() != 0) {
                UserProfile atUser = userService.getUserProfileByUid(topicFragment.getAtUid());
                liveElement.setAtUid(atUser.getUid());
                liveElement.setAtNickName(atUser.getNickName());
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
        if (internalStatus == 0) {
            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
        }

        return internalStatus;
    }

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
            applicationEventBus.post(speakEvent);
//            }
            //粉丝有留言提醒主播
        } else {
            if (speakDto.getType() != Specification.LiveSpeakType.INVITED.index && speakDto.getType() != Specification.LiveSpeakType.SHARE.index && speakDto.getType() != Specification.LiveSpeakType.SUBSCRIBED.index && speakDto.getType() != Specification.LiveSpeakType.FORWARD.index && speakDto.getType() != Specification.LiveSpeakType.FOLLOW.index && speakDto.getType() != Specification.LiveSpeakType.LIKES.index) {
                Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
                MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(topic.getUid(), topic.getId() + "", "1");
                log.info("speak by other start update hset cache key{} field {} value {}", cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
                //直播回复的推送
                if (speakDto.getType() ==
                        Specification.LiveSpeakType.FANS.index) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("messageType", Specification.PushMessageType.LIVE_REVIEW.index);
                    String alias = String.valueOf(topic.getUid());
                    UserProfile userProfile = userService.getUserProfileByUid(speakDto.getUid());
                    jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "评论了你", JPushUtils.packageExtra(jsonObject));

                }
            }
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

            fid = topicFragment.getId();
        }
        //获取最后一次发言FragmentId
        TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(speakDto.getTopicId(), speakDto.getUid());
        if (topicFragment != null) {
            speakDto.setFragmentId(topicFragment.getId());
        }
        log.info("createTopicFragment success");
        TopicBarrage topicBarrage = new TopicBarrage();
        topicBarrage.setFragmentImage(speakDto.getFragmentImage());
        topicBarrage.setFragment(speakDto.getFragment());
        topicBarrage.setBottomId(speakDto.getBottomId());
        topicBarrage.setTopicId(speakDto.getTopicId());
        topicBarrage.setTopId(speakDto.getTopId());
        topicBarrage.setContentType(speakDto.getContentType());
        topicBarrage.setType(speakDto.getType());
        topicBarrage.setUid(speakDto.getUid());
        topicBarrage.setFid(fid);

        //保存弹幕
        TopicBarrage barrage = liveMybatisDao.getBarrage(speakDto.getTopicId(), speakDto.getTopId(), speakDto.getBottomId(), speakDto.getType(), speakDto.getUid());
        if (barrage == null) {
            if (speakDto.getType() != Specification.LiveSpeakType.ANCHOR.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_AT.index && speakDto.getType() != Specification.LiveSpeakType.VIDEO.index && speakDto.getType() != Specification.LiveSpeakType.SOUND.index) {
                liveMybatisDao.createTopicBarrage(topicBarrage);
            }
        } else {
            if (speakDto.getType() == Specification.LiveSpeakType.SUBSCRIBED.index || speakDto.getType() == Specification.LiveSpeakType.FANS.index || speakDto.getType() == Specification.LiveSpeakType.FORWARD.index || speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.SHARE.index || speakDto.getType() == Specification.LiveSpeakType.AT.index) {
                liveMybatisDao.createTopicBarrage(topicBarrage);
            }
        }
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
        Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
        //直播发言时候更新直播更新时间
        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.VIDEO.index || speakDto.getType() == Specification.LiveSpeakType.SOUND.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index||speakDto.getType()==Specification.LiveSpeakType.AT_CORE_CIRCLE.index) {
            Calendar calendar = Calendar.getInstance();
            topic.setUpdateTime(calendar.getTime());
            topic.setLongTime(calendar.getTimeInMillis());
            liveMybatisDao.updateTopic(topic);
            log.info("updateTopic updateTime");
        }
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
            remindAndJpushAtMessage(speakDto, Specification.LiveSpeakType.AT.index);
        } else if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index) {
            remindAndJpushAtMessage(speakDto, Specification.LiveSpeakType.AT.index);
        } else if (speakDto.getType() == Specification.LiveSpeakType.AT_CORE_CIRCLE.index) { //2.1.2
            remindAndJpushAtMessage(speakDto, Specification.LiveSpeakType.AT_CORE_CIRCLE.index);
        }
        log.info("speak end ...");
        //2.0.7
        //直播信息保存
        //saveLiveDisplayData(speakDto);
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status, ResponseStatus.USER_SPEAK_SUCCESS.message, speakDto);
    }

    private void remindAndJpushAtMessage(SpeakDto speakDto, int messageType) {
        JSONArray atArray = null;
        if(speakDto.getAtUid()==-1){  //atUid==-1时为多人@
            JSONObject fragment = JSON.parseObject(speakDto.getFragment());
            if(fragment==null)
                return;
            atArray = fragment.containsKey("atArray")?fragment.getJSONArray("atArray"):null;
            if(atArray==null)
                return;
        }else{
            atArray = new JSONArray();
            atArray.add(speakDto.getAtUid());
        }

        for(int i=0;i<atArray.size();i++){
            long atUid = atArray.getLongValue(i);
            liveRemind(atUid, speakDto.getUid(), Specification.LiveSpeakType.FANS.index, speakDto.getTopicId(), speakDto.getFragment());

            UserProfile userProfile = userService.getUserProfileByUid(speakDto.getUid());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", messageType);
            jsonObject.addProperty("topicId",speakDto.getTopicId());
            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            String alias = String.valueOf(atUid);
            jPushService.payloadByIdExtra(alias, userProfile.getNickName() + "@了你!", JPushUtils.packageExtra(jsonObject));
        }

    }

    private void saveLiveDisplayData(SpeakDto speakDto) {
        //直播文字，图片，视频，音频，感受--主要信息表
        if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.VIDEO.index || speakDto.getType() == Specification.LiveSpeakType.SOUND.index) {
            liveMybatisDao.createLiveDisplayFragment(speakDto);
            return;
            //评论数据表：主播@,圈内人@，圈内人发言，圈内人感受
        } else if (speakDto.getMode() == Specification.LiveMode.COMMON.index) {
            if (speakDto.getType() != Specification.LiveSpeakType.ANCHOR.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index && speakDto.getType() != Specification.LiveSpeakType.VIDEO.index && speakDto.getType() != Specification.LiveSpeakType.SOUND.index) {
                liveMybatisDao.createLiveDisplayReview(speakDto);
                return;
            }
            //弹幕数据：圈外人，游客 ：发言，感受，订阅，点赞，分享，要求
        } else if (speakDto.getMode() == Specification.LiveMode.SENIOR.index) {
            //高级模式：主播@显示评论区
            if (speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index) {
                liveMybatisDao.createLiveDisplayReview(speakDto);
                return;
                //如果是非主播@则判断是否是圈内人，圈内人显示在评论区，非圈内人显示在弹幕
            } else if (speakDto.getType() == Specification.LiveSpeakType.AT.index || speakDto.getType() == Specification.LiveSpeakType.FANS.index || speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
                //判断时候是圈内人,如果是圈内人是评论，则是弹幕
                Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
                int isFollow = userService.isFollow(speakDto.getUid(), topic.getUid());
                int isFollow2 = userService.isFollow(topic.getUid(), speakDto.getUid());
                LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(speakDto.getUid(), speakDto.getTopicId());
                if (isFollow == 1 && isFollow2 == 1 && liveFavorite != null) {
                    liveMybatisDao.createLiveDisplayReview(speakDto);
                    return;
                } else {
                    liveMybatisDao.createLiveDisplayBarrage(speakDto);
                    return;
                }
            } else {
                liveMybatisDao.createLiveDisplayBarrage(speakDto);
                return;
            }
        }
    }



    private void liveRemind(long targetUid, long sourceUid ,int type ,long cid,String fragment ){
        if(targetUid == sourceUid){
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        UserProfile customerProfile = userService.getUserProfileByUid(targetUid);
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(cid);
        Topic topic = liveMybatisDao.getTopicById(cid);
        userNotice.setCoverImage(topic.getLiveImage());
        if (fragment.length() > 50) {
            userNotice.setSummary(fragment.substring(0, 50));
        } else {
            userNotice.setSummary(fragment);
        }

        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        if (type == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_TAG.index);
        } else if (type == Specification.LiveSpeakType.FANS.index) {
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        userNotice.setReadStatus(0);
        userService.createUserNotice(userNotice);
        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        if (type == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            userTips.setType(Specification.UserNoticeType.LIVE_TAG.index);
        } else if (type == Specification.LiveSpeakType.FANS.index) {
            userTips.setType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId",cid);
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }

        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId",cid);
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }
        }
    }

    /**
     * 获取我关注的直播，和我的直播列表
     *
     * @param uid
     * @return
     */
    @Override
    public Response getMyLives(long uid, long sinceId) {
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
    @Override
    public Response getLives(long uid, long sinceId) {
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
    @Override
    public Response getLivesByUpdateTime(long uid, long updateTime) {
        log.info("getLivesByUpdateTime start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLivesByUpdateTime(updateTime);
//        for(Topic topic : topicList) {
//            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId()+"","0");
//            String isUpdate = cacheService.hGet(cacheModel.getKey(),topic.getId()+"");
//            if(StringUtils.isEmpty(isUpdate)) {
//                log.info("cache key {} , cache topic id {},cache value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
//                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
//            }
//        }
        log.info("getLivesByUpdateTime data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getLivesByUpdateTime end ...");
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status, ResponseStatus.GET_LIVES_SUCCESS.message, showTopicListDto);
    }

    private void builder(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        for (Topic topic : topicList) {
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            UserProfile profile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setV_lv(profile.getvLv());
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            showTopicElement.setUpdateTime(topic.getLongTime());
            showTopicElement.setIsFollowed(userService.isFollow(topic.getUid(), uid));
            showTopicElement.setIsFollowMe(userService.isFollow(uid, topic.getUid()));
            showTopicElement.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(), topic.getUid()));

            TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(topic.getId(), topic.getUid());
            afterProcess(uid, topic, showTopicElement, topicFragment);
            //判断是否收藏了
            LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topic.getId());
            if (liveFavorite != null) {
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }
            Content content = contentService.getContentByTopicId(topic.getId());
            showTopicElement.setReadCount((int) Math.rint(content.getReadCount() * 5.3));
            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void builderWithCache(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        for (Topic topic : topicList) {
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setUpdateTime(topic.getLongTime());
            showTopicElement.setIsFollowed(userService.isFollow(topic.getUid(), uid));
            showTopicElement.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(), topic.getUid()));
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            UserProfile profile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setV_lv(profile.getvLv());
            processCache(uid,topic,showTopicElement);
            TopicFragment topicFragment = liveMybatisDao.getLastTopicFragmentByCoreCircle(topic.getId(),topic.getCoreCircle());
            afterProcess(uid, topic, showTopicElement, topicFragment);
            //判断是否收藏了
            LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topic.getId());
            if (liveFavorite != null) {
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            } else {
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }
            //直播阅读数
            Content content = contentService.getContentByTopicId(topic.getId());
            showTopicElement.setReadCount((int) Math.rint(content.getReadCount() * 5.3));
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
            showTopicElement.setFavoriteCount(content.getFavoriteCount());
            showTopicElement.setCid(content.getId());
            showTopicElement.setIsLike(contentService.isLike(content.getId(), uid));
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

    /**
     * 删除王国跟贴内容
     *
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
            //验证是否是王国国王
            Topic topic = liveMybatisDao.getTopicById(topicId);
            UserProfile profile = userService.getUserProfileByUid(uid);
            if(topic.getUid()!=uid&&profile.getIsPromoter()!=1){
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
        LiveDisplayProtocolDto liveDisplayProtocolDto = (LiveDisplayProtocolDto) CommonUtils.fromPojoToDto(protocol, new LiveDisplayProtocolDto());
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
            favoriteUser.setV_lv(userProfile.getvLv());
            favoriteUser.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            favoriteUser.setUid(userProfile.getUid());
            favoriteUser.setNickName(userProfile.getNickName());
            showFavoriteListFto.getUserElements().add(favoriteUser);
        }
        log.info("getFavoriteList end ...");
        return Response.success(showFavoriteListFto);
    }

    /**
     * 获取我关注的直播，和我的直播列表
     *
     * @param uid
     * @return
     */
    @Override
    public Response getMyLivesByUpdateTime(long uid, long updateTime) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if (updateTime == 0) {
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getMyLivesByUpdateTime(uid, updateTime, topics);
//        //初始化cache
//        for(Topic topic : topicList) {
//            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId()+"","0");
//            String isUpdate = cacheService.hGet(cacheModel.getKey(),topic.getId()+"");
//            if(StringUtils.isEmpty(isUpdate)) {
//                log.info("cache key {} , cache topic id {},cache value {}",cacheModel.getKey(),cacheModel.getField(),cacheModel.getValue());
//                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
//            }
//        }
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
        int totalRecords = liveMybatisDao.countFragmentByTopicId(getLiveDetailDto.getTopicId());

        LiveDetailDto liveDetailDto = new LiveDetailDto();
        liveDetailDto.setTotalRecords(totalRecords);
        int offset = getLiveDetailDto.getOffset();
        int totalPages =totalRecords%offset==0?totalRecords/offset:totalRecords/offset+1;
        liveDetailDto.setTotalPages(totalPages);
        liveDetailDto.setPageNo(getLiveDetailDto.getPageNo());
        log.info("get page records...");
        List<TopicFragment> list = liveMybatisDao.getTopicFragmentForPage(getLiveDetailDto);

        buildLiveDetail(getLiveDetailDto,liveDetailDto,list);
        log.info("get live detail end ...");
        return  Response.success(ResponseStatus.GET_LIVE_DETAIL_SUCCESS.status, ResponseStatus.GET_LIVE_DETAIL_SUCCESS.message, liveDetailDto);
    }

    private void buildLiveDetail(GetLiveDetailDto getLiveDetailDto, LiveDetailDto liveDetailDto, List<TopicFragment> fragmentList) {
        log.info("build live detail start ...");
        Topic topic = liveMybatisDao.getTopicById(getLiveDetailDto.getTopicId());
        for (TopicFragment topicFragment : fragmentList) {
            long uid = topicFragment.getUid();

            LiveDetailDto.LiveElement liveElement = LiveDetailDto.createElement();
            int status = topicFragment.getStatus();
            liveElement.setStatus(status);
            liveElement.setId(topicFragment.getId());
            if(status==0){
                liveDetailDto.getLiveElements().add(liveElement);
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
            if (topicFragment.getAtUid() != 0) {
                UserProfile atUser = userService.getUserProfileByUid(topicFragment.getAtUid());
                liveElement.setAtUid(atUser.getUid());
                liveElement.setAtNickName(atUser.getNickName());
            }
            liveDetailDto.getLiveElements().add(liveElement);
        }
        log.info("build live detail end ...");
    }
}
