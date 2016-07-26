package com.me2me.live.service;

import com.google.common.collect.Lists;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.model.*;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

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
        contentDto.setContentType(Specification.ContentType.TEXT.index);
        contentDto.setRights(Specification.ContentRights.EVERY.index);
        contentService.publish(contentDto);
        List<UserFollow> list = userService.getFans(createLiveDto.getUid());
        log.info("user fans data ");
        for(UserFollow userFollow : list) {
            //主播发言提醒关注的人
            //userService.push(userFollow.getSourceUid(),createLiveDto.getUid(),Specification.PushMessageType.LIVE.index,createLiveDto.getTitle());
            //主播的粉丝强制订阅
            setLive3(userFollow.getSourceUid(),topic.getId());
        }
        log.info("createLive end ...");
        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status,ResponseStatus.USER_CREATE_LIVE_SUCCESS.message);
    }

    @Override
    public Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        log.info("getLiveTimeline start ...");
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(getLiveTimeLineDto.getUid(), getLiveTimeLineDto.getTopicId() + "", "0");
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(),cacheModel.getValue());
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getSinceId());
        log.info("get timeLine data");
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        log.info("buildLiveTimeLine success");
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status,ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message,liveTimeLineDto);
    }

    @Override
    public Response liveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        //判断进入直播是否是第一次
        LiveReadHistory liveReadHistory = liveMybatisDao.getLiveReadHistory(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getUid());
        List<TopicFragment> fragmentList = Lists.newArrayList();
        if(getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.FIRST.index) {
            if (liveReadHistory == null) {
                fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
                liveMybatisDao.createLiveReadHistory(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getUid());
            } else {
                fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), Integer.MAX_VALUE);
            }
        }else if(getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.NEXT.index){
            fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        }else if(getLiveTimeLineDto.getDirection() == Specification.LiveTimeLineDirection.PREV.index){
            fragmentList = liveMybatisDao.getPrevTopicFragment(getLiveTimeLineDto.getTopicId(), getLiveTimeLineDto.getSinceId());
        }
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status,ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message,liveTimeLineDto);
    }

    @Override
    public Response liveCover(long topicId) {
        log.info("liveCover start ...");
        LiveCoverDto liveCoverDto = new LiveCoverDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        liveCoverDto.setTitle(topic.getTitle());
        liveCoverDto.setCreateTime(topic.getCreateTime());
        liveCoverDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
        liveCoverDto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        liveCoverDto.setNickName(userProfile.getNickName());
        liveCoverDto.setUid(topic.getUid());
        liveCoverDto.setLastUpdateTime(topic.getLongTime());
        liveCoverDto.setReviewCount(liveMybatisDao.countFragment(topic.getId(),topic.getUid()));
        liveCoverDto.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(),topic.getUid()));
        log.info("liveCover end ...");
        return Response.success(ResponseStatus.GET_LIVE_COVER_SUCCESS.status,ResponseStatus.GET_LIVE_COVER_SUCCESS.message,liveCoverDto);
    }

    @Override
    public Response barrage(LiveBarrageDto barrageDto) {
        log.info("barrage start ...");
        ShowBarrageDto showBarrageDto = new ShowBarrageDto();
        List<TopicBarrage> topicBarrages = liveMybatisDao.getBarrage(barrageDto.getTopicId(),barrageDto.getSinceId(),barrageDto.getTopId(),barrageDto.getBottomId());
        log.info("topicBarrages data success");
        for(TopicBarrage barrage :topicBarrages){
            long uid = barrage.getUid();
            UserProfile userProfile = userService.getUserProfileByUid(uid);
            ShowBarrageDto.BarrageElement barrageElement = ShowBarrageDto.createElement();
            barrageElement.setUid(uid);
            barrageElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            barrageElement.setNickName(userProfile.getNickName());
            if(barrageElement.getContentType() == Specification.LiveContent.TEXT.index) {
                barrageElement.setFragment(barrage.getFragment());
            }else if(barrageElement.getContentType() == Specification.LiveContent.IMAGE.index){
                barrageElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + barrage.getFragmentImage());
            }
            barrageElement.setCreateTime(barrage.getCreateTime());
            barrageElement.setType(barrage.getType());
            barrageElement.setContentType(barrage.getContentType());
            barrageElement.setId(barrage.getId());
            Topic topic = liveMybatisDao.getTopicById(barrageDto.getTopicId());
            barrageElement.setInternalStatus(userService.getUserInternalStatus(uid,topic.getUid()));
            showBarrageDto.getBarrageElements().add(barrageElement);
        }
        log.info("barrage end ...");
        return Response.success(ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.status,ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.message,showBarrageDto);
    }

    @Override
    public Response getLiveByCid(long cid,long uid) {
        ShowLiveDto showLiveDto = new ShowLiveDto();
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        Topic topic = liveMybatisDao.getTopicById(cid);
        Content content =contentService.getContentByTopicId(cid);
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
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topic.getId());
        if(liveFavorite != null){
            showLiveDto.setFavorite(Specification.LiveFavorite.FAVORITE.index);
        }else{
            showLiveDto.setFavorite(Specification.LiveFavorite.NORMAL.index);
        }
        showLiveDto.setCid(content.getId());
        showLiveDto.setIsFollowed(userService.isFollow(topic.getUid(),uid));
        showLiveDto.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
        showLiveDto.setTitle(topic.getTitle());
        showLiveDto.setStatus(topic.getStatus());
        showLiveDto.setIsLike(contentService.isLike(content.getId(),uid));
        return Response.success(showLiveDto);
    }

    private void buildLiveTimeLine(GetLiveTimeLineDto getLiveTimeLineDto, LiveTimeLineDto liveTimeLineDto, List<TopicFragment> fragmentList) {
        for(TopicFragment topicFragment : fragmentList){
            long uid = topicFragment.getUid();
            UserProfile userProfile = userService.getUserProfileByUid(uid);
            LiveTimeLineDto.LiveElement liveElement = LiveTimeLineDto.createElement();
            liveElement.setUid(uid);
            liveElement.setId(topicFragment.getId());
            liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if(!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(),getLiveTimeLineDto.getUid());
            liveElement.setIsFollowed(isFollow);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
            liveElement.setInternalStatus(userService.getUserInternalStatus(uid,topic.getUid()));
            if(topicFragment.getAtUid() != 0){
                UserProfile atUser = userService.getUserProfileByUid(topicFragment.getAtUid());
                liveElement.setAtUid(atUser.getUid());
                liveElement.setAtNickName(atUser.getNickName());
            }
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
    }

    @Override
    public Response speak(SpeakDto speakDto) {
        log.info("speak start ...");
        //如果是主播发言更新cache
        if(speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index ||speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index ){
            List<LiveFavorite> liveFavorites = liveMybatisDao.getFavoriteList(speakDto.getTopicId());
            for(LiveFavorite liveFavorite : liveFavorites) {
                MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId() + "", "1");
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }
        }
        if(speakDto.getType() != Specification.LiveSpeakType.LIKES.index &&speakDto.getType() != Specification.LiveSpeakType.SUBSCRIBED.index && speakDto.getType() != Specification.LiveSpeakType.SHARE.index  && speakDto.getType() != Specification.LiveSpeakType.FOLLOW.index && speakDto.getType() != Specification.LiveSpeakType.INVITED.index) {
            TopicFragment topicFragment = new TopicFragment();
            topicFragment.setFragmentImage(speakDto.getFragmentImage());
            topicFragment.setFragment(speakDto.getFragment());
            topicFragment.setUid(speakDto.getUid());
            topicFragment.setContentType(speakDto.getContentType());
            topicFragment.setType(speakDto.getType());
            topicFragment.setTopicId(speakDto.getTopicId());
            topicFragment.setBottomId(speakDto.getBottomId());
            topicFragment.setTopId(speakDto.getTopId());
            topicFragment.setAtUid(speakDto.getAtUid());
            liveMybatisDao.createTopicFragment(topicFragment);
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
        //保存弹幕
        TopicBarrage barrage = liveMybatisDao.getBarrage(speakDto.getTopicId(),speakDto.getTopId(),speakDto.getBottomId(),speakDto.getType(),speakDto.getUid());
        if(barrage == null){
           if(speakDto.getType() != Specification.LiveSpeakType.ANCHOR.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index && speakDto.getType() != Specification.LiveSpeakType.ANCHOR_AT.index && speakDto.getType() != Specification.LiveSpeakType.VIDEO.index) {
               liveMybatisDao.createTopicBarrage(topicBarrage);
           }
        }else{
            if(speakDto.getType() == Specification.LiveSpeakType.SUBSCRIBED.index || speakDto.getType() == Specification.LiveSpeakType.FANS.index || speakDto.getType() == Specification.LiveSpeakType.FORWARD.index || speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index || speakDto.getType() == Specification.LiveSpeakType.LIKES.index || speakDto.getType() == Specification.LiveSpeakType.SHARE.index ||speakDto.getType() == Specification.LiveSpeakType.AT.index ){
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
        if(speakDto.getType() == Specification.LiveSpeakType.LIKES.index) {
            LikeDto likeDto = new LikeDto();
            //点赞
            Content content =contentService.getContentByTopicId(speakDto.getTopicId());
            likeDto.setCid(content.getId());
            likeDto.setAction(0);
            likeDto.setUid(speakDto.getUid());
            likeDto.setType(Specification.LikesType.LIVE.index);
            contentService.like2(likeDto);
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index){
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
        if(speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index) {
            Calendar calendar = Calendar.getInstance();
            topic.setUpdateTime(calendar.getTime());
            topic.setLongTime(calendar.getTimeInMillis());
            liveMybatisDao.updateTopic(topic);
            log.info("updateTopic updateTime");
        }
        if(speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index){
            List<LiveFavorite> list = liveMybatisDao.getFavoriteList(speakDto.getTopicId());
            for(LiveFavorite liveFavorite : list) {
                //主播发言提醒关注的人
                //userService.push(liveFavorite.getUid(),topic.getUid(),Specification.PushMessageType.UPDATE.index,topic.getTitle());
                log.info("update push");
            }
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index){
            //粉丝贴标提醒
            //Topic live = liveMybatisDao.getTopicById(speakDto.getTopicId());
            //liveRemind(live.getUid(), speakDto.getUid() ,Specification.LiveSpeakType.FANS_WRITE_TAG.index ,speakDto.getTopicId(),speakDto.getFragment());
            //userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_TAG.index,topic.getTitle());
            log.info("live tag push");
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANS.index){
            //粉丝发言提醒
            //Topic live = liveMybatisDao.getTopicById(speakDto.getTopicId());
            //liveRemind(live.getUid(), speakDto.getUid() ,Specification.LiveSpeakType.FANS.index ,speakDto.getTopicId(),speakDto.getFragment());
            //userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_REVIEW.index,topic.getTitle());
            log.info("live review push");
        }else if(speakDto.getType() == Specification.LiveSpeakType.AT.index){
            //Topic live = liveMybatisDao.getTopicById(speakDto.getTopicId());
            liveRemind(topic.getUid(), speakDto.getUid() ,Specification.LiveSpeakType.FANS.index ,speakDto.getTopicId(),speakDto.getFragment());
            userService.push(speakDto.getAtUid(),speakDto.getUid(),Specification.PushMessageType.AT.index,topic.getTitle());
        }else if(speakDto.getType() == Specification.LiveSpeakType.ANCHOR_AT.index){
            liveRemind(speakDto.getUid() ,topic.getUid(),Specification.LiveSpeakType.FANS.index ,speakDto.getTopicId(),speakDto.getFragment());
            userService.push(speakDto.getAtUid(),speakDto.getUid(),Specification.PushMessageType.AT.index,topic.getTitle());
        }
        log.info("speak end ...");
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status,ResponseStatus.USER_SPEAK_SUCCESS.message);
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
        Topic topic =liveMybatisDao.getTopicById(cid);
        userNotice.setCoverImage(topic.getLiveImage());
        if(fragment.length() > 50) {
            userNotice.setSummary(fragment.substring(0,50));
        }else{
            userNotice.setSummary(fragment);
        }

        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        if(type == Specification.LiveSpeakType.FANS_WRITE_TAG.index){
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_TAG.index);
        }else if(type == Specification.LiveSpeakType.FANS.index){
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        userNotice.setReadStatus(0);
        userService.createUserNotice(userNotice);
        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        if(type == Specification.LiveSpeakType.FANS_WRITE_TAG.index){
            userTips.setType(Specification.UserNoticeType.LIVE_TAG.index);
        }else if(type == Specification.LiveSpeakType.FANS.index){
            userTips.setType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        UserTips tips  =  userService.getUserTips(userTips);
        if(tips == null){
            userTips.setCount(1);
            userService.createUserTips(userTips);
        }else{
            tips.setCount(tips.getCount()+1);
              userService.modifyUserTips(tips);
        }
    }

    /**
     * 获取我关注的直播，和我的直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getMyLives(long uid ,long sinceId) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        List<Topic> topicList = liveMybatisDao.getMyLives(uid ,sinceId ,topics);
        log.info("getMyLives data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getMyLives end ...");
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status,ResponseStatus.GET_MY_LIVE_SUCCESS.message,showTopicListDto);
    }

    /**
     * 获取所有正在直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getLives(long uid,long sinceId) {
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLives(sinceId);
        builder(uid, showTopicListDto, topicList);
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status,ResponseStatus.GET_LIVES_SUCCESS.message,showTopicListDto);
    }

    /**
     * 获取所有正在直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getLivesByUpdateTime(long uid,long updateTime) {
        log.info("getLivesByUpdateTime start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLivesByUpdateTime(updateTime);
        log.info("getLivesByUpdateTime data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getLivesByUpdateTime end ...");
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status,ResponseStatus.GET_LIVES_SUCCESS.message,showTopicListDto);
    }

    private void builder(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        for(Topic topic : topicList){
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN  + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar() );
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setUpdateTime(topic.getLongTime());
            showTopicElement.setIsFollowed(userService.isFollow(topic.getUid(),uid));
            showTopicElement.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(),topic.getUid()));
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(topic.getId(),topic.getUid());
            afterProcess(uid, topic, showTopicElement, topicFragment);
            //判断是否收藏了
            LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topic.getId());
            if(liveFavorite != null){
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            }else{
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }
            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void builderWithCache(long uid, ShowTopicListDto showTopicListDto, List<Topic> topicList) {
        for(Topic topic : topicList){
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN  + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            showTopicElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar() );
            showTopicElement.setNickName(userProfile.getNickName());
            showTopicElement.setCreateTime(topic.getCreateTime());
            showTopicElement.setTopicId(topic.getId());
            showTopicElement.setStatus(topic.getStatus());
            showTopicElement.setUpdateTime(topic.getLongTime());
            showTopicElement.setIsFollowed(userService.isFollow(topic.getUid(),uid));
            showTopicElement.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(),topic.getUid()));
            showTopicElement.setLastUpdateTime(topic.getLongTime());
            processCache(uid,topic,showTopicElement);
            TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(topic.getId(),topic.getUid());
            afterProcess(uid, topic, showTopicElement, topicFragment);
            //判断是否收藏了
            LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topic.getId());
            if(liveFavorite != null){
                showTopicElement.setFavorite(Specification.LiveFavorite.FAVORITE.index);
            }else{
                showTopicElement.setFavorite(Specification.LiveFavorite.NORMAL.index);
            }
            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
    }

    private void afterProcess(long uid, Topic topic, ShowTopicListDto.ShowTopicElement showTopicElement, TopicFragment topicFragment) {
        if(topicFragment != null) {
            showTopicElement.setLastContentType(topicFragment.getContentType());
            showTopicElement.setLastFragment(topicFragment.getFragment());
            showTopicElement.setLastFragmentImage(topicFragment.getFragmentImage());
        }else{
            showTopicElement.setLastContentType(-1);
        }
        Content content = contentService.getContentByTopicId(topic.getId());
        if(content != null) {
            showTopicElement.setLikeCount(content.getLikeCount());
            showTopicElement.setPersonCount(content.getPersonCount());
            showTopicElement.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
            showTopicElement.setFavoriteCount(content.getFavoriteCount());
            showTopicElement.setCid(content.getId());
            showTopicElement.setIsLike(contentService.isLike(content.getId(),uid));
        }
    }

    private void processCache(long uid, Topic topic, ShowTopicListDto.ShowTopicElement showTopicElement) {
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId()+"","0");
        String isUpdate = cacheService.hGet(cacheModel.getKey(),topic.getId()+"");
        showTopicElement.setIsUpdate(Integer.parseInt(isUpdate));
    }

    /**
     * 关注，取消关注
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive(long uid, long topicId,long topId,long bottomId) {
        log.info("setLive start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if(liveFavorite != null){
            liveMybatisDao.deleteLiveFavorite(liveFavorite);
            if((content.getFavoriteCount() - 1) < 0){
                content.setFavoriteCount(0);
            }else{
                content.setFavoriteCount(content.getFavoriteCount() - 1);
            }
            contentService.updateContentById(content);
            log.info("setLive end ...");
            return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        }else {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
            //保存弹幕
            TopicBarrage barrage = liveMybatisDao.getBarrage(topicId,topId,bottomId,Specification.LiveSpeakType.SUBSCRIBED.index,uid);
            saveBarrage(uid, topicId, topId, bottomId, barrage);
            content.setFavoriteCount(content.getFavoriteCount()+1);
            contentService.updateContentById(content);
            log.info("setLive end ...");
            return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
        }
    }

    private void saveBarrage(long uid, long topicId, long topId, long bottomId, TopicBarrage barrage) {
        if(barrage == null) {
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
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive2(long uid, long topicId,long topId,long bottomId,int action) {
        log.info("setLive2 start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if (action == 0) {
            if (liveFavorite == null) {
                liveFavorite = new LiveFavorite();
                liveFavorite.setTopicId(topicId);
                liveFavorite.setUid(uid);
                liveMybatisDao.createLiveFavorite(liveFavorite);
                liveMybatisDao.deleteFavoriteDelete(uid,topicId);
                //保存弹幕
                TopicBarrage barrage = liveMybatisDao.getBarrage(topicId, topId, bottomId, Specification.LiveSpeakType.SUBSCRIBED.index, uid);
                if(barrage != null){
                    content.setFavoriteCount(content.getFavoriteCount() + 1);
                    contentService.updateContentById(content);
                    return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
                }else {
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
            if(liveMybatisDao.getFavoriteDelete(uid, topicId) == null) {
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
        return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
    }

    /**
     * 订阅取消订阅
     * @param uid
     * @param topicId
     * @return
     */
    private Response setLive3(long uid, long topicId) {
        log.info("setLive3 start ...");
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid, topicId);
        Content content = contentService.getContentByTopicId(topicId);
        if (liveFavorite == null) {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
            liveMybatisDao.deleteFavoriteDelete(uid,topicId);
            content.setFavoriteCount(content.getFavoriteCount() + 1);
            contentService.updateContentById(content);
            log.info("setLive3 end ...");
        }
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    /**
     * 结束自己的直播
     * @param topicId
     * @return
     */
    @Override
    public Response finishMyLive(long uid,long topicId) {
        log.info("finishMyLive start ...");
        Topic topic = liveMybatisDao.getTopic(uid,topicId);
        if(topic != null) {
            if(topic.getStatus() == Specification.LiveStatus.LIVING.index) {
                topic.setStatus(Specification.LiveStatus.OVER.index);
                liveMybatisDao.updateTopic(topic);
                log.info("finishMyLive end ...");
                return Response.success(ResponseStatus.USER_FINISH_LIVE_SUCCESS.status, ResponseStatus.USER_FINISH_LIVE_SUCCESS.message);
            }else{
                return Response.success(ResponseStatus.USER_LIVE_IS_OVER.status, ResponseStatus.USER_LIVE_IS_OVER.message);
            }
        }else{
            return Response.success(ResponseStatus.FINISH_LIVE_NO_POWER.status,ResponseStatus.FINISH_LIVE_NO_POWER.message);
        }
    }

    /**
     * 置顶/取消置顶
     * @param topicId
     * @return
     */
    public Response top(long topicId){
        return null;
    }

    /**
     * 删除直播
     * @param topicId
     * @return
     */
    public Response delete(long topicId){
        return null;
    }

    @Override
    public Response removeLive(long uid, long topicId){
        log.info("removeLive start ...");
        //判断是否是自己的直播
        Topic topic = liveMybatisDao.getTopic(uid,topicId);
        if(topic == null){
            return Response.failure(ResponseStatus.LIVE_REMOVE_IS_NOT_YOURS.status,ResponseStatus.LIVE_REMOVE_IS_NOT_YOURS.message);
        }
        //判断是否完结
        if(topic.getStatus() == Specification.LiveStatus.LIVING.index){
            return Response.failure(ResponseStatus.LIVE_REMOVE_IS_NOT_OVER.status,ResponseStatus.LIVE_REMOVE_IS_NOT_OVER.message);
        }
        //移除
        topic.setStatus(Specification.LiveStatus.REMOVE.index);
        liveMybatisDao.updateTopic(topic);
        log.info("removeLive end ...");
        return Response.success(ResponseStatus.LIVE_REMOVE_SUCCESS.status,ResponseStatus.LIVE_REMOVE_SUCCESS.message);
    }

    @Override
    public Response signOutLive(long uid, long topicId){
        log.info("signOutLive start ...");
        //判断是否是自己的直播
        Topic topic = liveMybatisDao.getTopic(uid,topicId);
        if(topic != null){
            return Response.failure(ResponseStatus.LIVE_OWNER_CAN_NOT_SIGN_OUT.status,ResponseStatus.LIVE_OWNER_CAN_NOT_SIGN_OUT.message);
        }else{
            topic =liveMybatisDao.getTopicById(topicId);
            if(topic == null){
               return Response.failure(ResponseStatus.LIVE_IS_NOT_EXIST.status ,ResponseStatus.LIVE_IS_NOT_EXIST.message);
            }
        }
        //移除我的关注列表/退出
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topicId);
        if(liveFavorite == null){
            Response.failure(ResponseStatus.LIVE_IS_NOT_SIGN_IN.status ,ResponseStatus.LIVE_IS_NOT_SIGN_IN.message);
        }
        liveMybatisDao.deleteLiveFavorite(liveFavorite);
        log.info("deleteLiveFavorite success");
        Content content = contentService.getContentByTopicId(topicId);
        if((content.getFavoriteCount() - 1) < 0){
            content.setFavoriteCount(0);
        }else{
            content.setFavoriteCount(content.getFavoriteCount() - 1);
        }
        contentService.updateContentById(content);
        log.info("signOutLive end ...");
        return Response.success(ResponseStatus.LIVE_SIGN_OUT_SUCCESS.status,ResponseStatus.LIVE_SIGN_OUT_SUCCESS.message);
    }


    @Override
    public int countFragment(long topicId,long uid){
        return liveMybatisDao.countFragment(topicId,uid);
    }

    @Override
    public Response getFavoriteList(long topicId) {
        log.info("getFavoriteList start ...");
        ShowFavoriteListDto showFavoriteListFto = new ShowFavoriteListDto();
        List<LiveFavorite> liveFavoriteList = liveMybatisDao.getFavoriteList(topicId);
        for (LiveFavorite liveFavorite : liveFavoriteList){
            ShowFavoriteListDto.FavoriteUser favoriteUser = ShowFavoriteListDto.createElement();
            UserProfile userProfile = userService.getUserProfileByUid(liveFavorite.getUid());
            favoriteUser.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            favoriteUser.setUid(userProfile.getUid());
            favoriteUser.setNickName(userProfile.getNickName());
            showFavoriteListFto.getUserElements().add(favoriteUser);
        }
        log.info("getFavoriteList end ...");
        return  Response.success(showFavoriteListFto);
    }

    /**
     * 获取我关注的直播，和我的直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getMyLivesByUpdateTime(long uid ,long updateTime) {
        log.info("getMyLives start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        Calendar calendar = Calendar.getInstance();
        if(updateTime == 0){
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getMyLivesByUpdateTime(uid ,updateTime ,topics);
        //初始化cache
        for(Topic topic : topicList) {
            MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topic.getId()+"","0");
            String isUpdate = cacheService.hGet(cacheModel.getKey(),topic.getId()+"");
            if(StringUtils.isEmpty(isUpdate)) {
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            }
        }
        log.info("getMyLives data success");
        builderWithCache(uid, showTopicListDto, topicList);
        log.info("getMyLives start ...");
        int inactiveLiveCount = liveMybatisDao.getInactiveLiveCount(uid,topics);
        showTopicListDto.setInactiveLiveCount(inactiveLiveCount);
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        List<Topic> live = liveMybatisDao.getInactiveLive(uid ,topics,calendar.getTimeInMillis());
        if(live.size() > 0) {
            showTopicListDto.setLiveTitle(live.get(0).getTitle());
        }
        //获取所有更新中直播主笔的信息
        List<Topic> list =  liveMybatisDao.getLives(Long.MAX_VALUE);
        for(Topic topic : list){
            ShowTopicListDto.UpdateLives updateLives = ShowTopicListDto.createUpdateLivesElement();
            UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
            updateLives.setUid(userProfile.getUid());
            updateLives.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            showTopicListDto.getUpdateLives().add(updateLives);
        }
        showTopicListDto.setLiveCount(liveMybatisDao.countLives());
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status,ResponseStatus.GET_MY_LIVE_SUCCESS.message,showTopicListDto);
    }

    @Override
    public Response getInactiveLive(long uid ,long updateTime) {
        log.info("getInactiveLive start ...");
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        if(updateTime == 0){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,-3);
            updateTime = calendar.getTimeInMillis();
        }
        List<Topic> topicList = liveMybatisDao.getInactiveLive(uid ,topics,updateTime);
        log.info("getInactiveLive data success");
        builder(uid, showTopicListDto, topicList);
        log.info("getInactiveLive end ...");
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status,ResponseStatus.GET_MY_LIVE_SUCCESS.message,showTopicListDto);
    }

    public Topic getTopicById(long topicId){
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
    public void deleteFavoriteDelete(long uid,long topicId){
        liveMybatisDao.deleteFavoriteDelete(uid, topicId);
    }

    @Override
    public TopicFragment getLastTopicFragmentByUid(long topicId,long uid) {
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
        for(TopicFragment topicFragment : list){
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
        LiveTimeLineDto2 liveTimeLineDto = new LiveTimeLineDto2();
        log.info("getLiveTimeline2 start ...");
        MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(getLiveTimeLineDto.getUid(), getLiveTimeLineDto.getTopicId() + "", "0");
        cacheService.hSet(cacheModel.getKey(), cacheModel.getField(),cacheModel.getValue());
        Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragmentByMode(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getSinceId(),topic.getUid());
        log.info("get getLiveTimeline2 data");
        buildTimeLine2(getLiveTimeLineDto, liveTimeLineDto, topic, fragmentList);
        log.info("buildLiveTimeLine2 success");
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status,ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message,liveTimeLineDto);
    }

    private void buildTimeLine2(GetLiveTimeLineDto2 getLiveTimeLineDto, LiveTimeLineDto2 liveTimeLineDto, Topic topic, List<TopicFragment> fragmentList) {
        long uid = topic.getUid();
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        for(TopicFragment topicFragment : fragmentList){
            LiveTimeLineDto2.LiveElement liveElement = LiveTimeLineDto2.createElement();
            liveElement.setUid(uid);
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            String fragmentImage = topicFragment.getFragmentImage();
            if(!StringUtils.isEmpty(fragmentImage)) {
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(),getLiveTimeLineDto.getUid());
            liveElement.setIsFollowed(isFollow);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveElement.setInternalStatus(userService.getUserInternalStatus(uid,topic.getUid()));


            liveElement.setReviewCount(0);
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
    }

    @Override
    public void createFavoriteDelete(long uid,long topicId){
        liveMybatisDao.createFavoriteDelete(uid, topicId);
    }

}
