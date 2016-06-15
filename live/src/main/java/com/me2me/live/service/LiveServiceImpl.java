package com.me2me.live.service;

import com.google.common.collect.Lists;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.model.*;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
@Service
public class LiveServiceImpl implements LiveService {

    @Autowired
    private LiveMybatisDao liveMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Override
    public Response createLive(CreateLiveDto createLiveDto) {
        Topic topic = new Topic();
        topic.setTitle(createLiveDto.getTitle());
        topic.setLiveImage(createLiveDto.getLiveImage());
        topic.setUid(createLiveDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
        Calendar calendar = Calendar.getInstance();
        topic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.createTopic(topic);
        List<UserFollow> list = userService.getFans(createLiveDto.getUid());
        for(UserFollow userFollow : list) {
            //主播发言提醒关注的人
            userService.push(userFollow.getSourceUid(),createLiveDto.getUid(),Specification.PushMessageType.LIVE.index,createLiveDto.getTitle());
        }
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
        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status,ResponseStatus.USER_CREATE_LIVE_SUCCESS.message);
    }

    @Override
    public Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getSinceId());
        buildLiveTimeLine(getLiveTimeLineDto, liveTimeLineDto, fragmentList);
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
        LiveCoverDto liveCoverDto = new LiveCoverDto();
        Topic topic = liveMybatisDao.getTopicById(topicId);
        liveCoverDto.setTitle(topic.getTitle());
        liveCoverDto.setCreateTime(topic.getCreateTime());
        liveCoverDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.getLiveImage());
        UserProfile userProfile = userService.getUserProfileByUid(topic.getUid());
        liveCoverDto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        liveCoverDto.setNickName(userProfile.getNickName());
        liveCoverDto.setUid(topic.getUid());
        liveCoverDto.setLastUpdateTime(topic.getUpdateTime());
        liveCoverDto.setReviewCount(liveMybatisDao.countFragment(topic.getId(),topic.getUid()));
        liveCoverDto.setTopicCount(liveMybatisDao.countFragmentByUid(topic.getId(),topic.getUid()));
        return Response.success(ResponseStatus.GET_LIVE_COVER_SUCCESS.status,ResponseStatus.GET_LIVE_COVER_SUCCESS.message,liveCoverDto);
    }

    @Override
    public Response barrage(LiveBarrageDto barrageDto) {
        ShowBarrageDto showBarrageDto = new ShowBarrageDto();
        List<TopicBarrage> topicBarrages = liveMybatisDao.getBarrage(barrageDto.getTopicId(),barrageDto.getSinceId(),barrageDto.getTopId(),barrageDto.getBottomId());
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
            showBarrageDto.getBarrageElements().add(barrageElement);
        }
        return Response.success(ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.status,ResponseStatus.GET_LIVE_BARRAGE_SUCCESS.message,showBarrageDto);
    }

    private void buildLiveTimeLine(GetLiveTimeLineDto getLiveTimeLineDto, LiveTimeLineDto liveTimeLineDto, List<TopicFragment> fragmentList) {
        for(TopicFragment topicFragment : fragmentList){
            long uid = topicFragment.getUid();
            UserProfile userProfile = userService.getUserProfileByUid(uid);
            LiveTimeLineDto.LiveElement liveElement = LiveTimeLineDto.createElement();
            liveElement.setUid(uid);
            liveElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            liveElement.setNickName(userProfile.getNickName());
            if(topicFragment.getContentType() == Specification.LiveContent.TEXT.index) {
                liveElement.setFragment(topicFragment.getFragment());
            }else if(topicFragment.getContentType() == Specification.LiveContent.IMAGE.index){
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + topicFragment.getFragmentImage());
            }
            liveElement.setCreateTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            int isFollow = userService.isFollow(topicFragment.getUid(),getLiveTimeLineDto.getUid());
            liveElement.setIsFollowed(isFollow);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
    }

    @Override
    public Response speak(SpeakDto speakDto) {
        TopicFragment topicFragment = new TopicFragment();
        TopicBarrage topicBarrage = new TopicBarrage();
        topicFragment.setFragmentImage(speakDto.getFragmentImage());
        topicFragment.setFragment(speakDto.getFragment());
        topicFragment.setUid(speakDto.getUid());
        topicFragment.setContentType(speakDto.getContentType());
        topicFragment.setType(speakDto.getType());
        topicFragment.setTopicId(speakDto.getTopicId());
        topicBarrage.setBottomId(speakDto.getBottomId());
        topicBarrage.setTopId(speakDto.getTopId());
        liveMybatisDao.createTopicFragment(topicFragment);
        topicBarrage.setFragmentImage(speakDto.getFragmentImage());
        topicBarrage.setFragment(speakDto.getFragment());
        topicBarrage.setBottomId(speakDto.getBottomId());
        topicBarrage.setTopicId(speakDto.getTopicId());
        topicBarrage.setTopId(speakDto.getTopId());
        topicBarrage.setContentType(speakDto.getContentType());
        topicBarrage.setType(speakDto.getType());
        topicBarrage.setUid(speakDto.getUid());
        //保存弹幕
        liveMybatisDao.createTopicBarrage(topicBarrage);
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
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANSWRITETAG.index){
            //贴标
            Content content =contentService.getContentByTopicId(speakDto.getTopicId());
            WriteTagDto writeTagDto = new WriteTagDto();
            writeTagDto.setType(Specification.WriteTagType.CONTENT.index);
            writeTagDto.setUid(speakDto.getUid());
            writeTagDto.setCid(content.getId());
            writeTagDto.setTag(speakDto.getFragment());
            contentService.writeTag(writeTagDto);
        }
        Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
        //直播发言时候更新直播更新时间
        Calendar calendar = Calendar.getInstance();
        topic.setUpdateTime(calendar.getTime());
        topic.setLongTime(calendar.getTimeInMillis());
        liveMybatisDao.updateTopic(topic);
        if(speakDto.getType() == Specification.LiveSpeakType.ANCHOR.index || speakDto.getType() == Specification.LiveSpeakType.ANCHORWRITETAG.index){
            List<LiveFavorite> list = liveMybatisDao.getFavoriteList(speakDto.getTopicId());
            for(LiveFavorite liveFavorite : list) {
                //主播发言提醒关注的人
                userService.push(liveFavorite.getUid(),topic.getUid(),Specification.PushMessageType.UPDATE.index,topic.getTitle());
            }
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANSWRITETAG.index){
            //粉丝贴标提醒
            userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_TAG.index,topic.getTitle());
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANS.index){
            //粉丝发言提醒
            userService.push(topic.getUid(),speakDto.getUid(),Specification.PushMessageType.LIVE_REVIEW.index,topic.getTitle());
        }
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status,ResponseStatus.USER_SPEAK_SUCCESS.message);
    }

    /**
     * 获取我关注的直播，和我的直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getMyLives(long uid ,long sinceId) {
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Long> topics = liveMybatisDao.getTopicId(uid);
        List<Topic> topicList = liveMybatisDao.getMyLives(uid ,sinceId ,topics);
        builder(uid, showTopicListDto, topicList);
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
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLivesByUpdateTime(updateTime);
        builder(uid, showTopicListDto, topicList);
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
            TopicFragment topicFragment = liveMybatisDao.getLastTopicFragment(topic.getId(),topic.getUid());
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
                //showTopicElement.setReviewCount(content.getReviewCount());
                showTopicElement.setFavoriteCount(content.getFavoriteCount());
                showTopicElement.setCid(content.getId());
                showTopicElement.setIsLike(contentService.isLike(content.getId(),uid));
            }
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

    /**
     * 关注，取消关注
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive(long uid, long topicId,long topId,long bottomId) {
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
            return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        }else {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
            //保存弹幕
            TopicBarrage topicBarrage = new TopicBarrage();
            topicBarrage.setBottomId(bottomId);
            topicBarrage.setTopicId(topicId);
            topicBarrage.setTopId(bottomId);
            topicBarrage.setType(Specification.LiveSpeakType.SUBSCRIBED.index);
            topicBarrage.setUid(uid);
            //保存弹幕
            liveMybatisDao.createTopicBarrage(topicBarrage);
            content.setFavoriteCount(content.getFavoriteCount()+1);
            contentService.updateContentById(content);
            return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
        }
    }

    /**
     * 结束自己的直播
     * @param topicId
     * @return
     */
    @Override
    public Response finishMyLive(long uid,long topicId) {
        Topic topic = liveMybatisDao.getTopic(uid,topicId);
        if(topic != null) {
            if(topic.getStatus() == Specification.LiveStatus.LIVING.index) {
                topic.setStatus(Specification.LiveStatus.OVER.index);
                liveMybatisDao.updateTopic(topic);
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
        return Response.success(ResponseStatus.LIVE_REMOVE_SUCCESS.status,ResponseStatus.LIVE_REMOVE_SUCCESS.message);
    }

    @Override
    public Response signOutLive(long uid, long topicId){
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
        Content content = contentService.getContentByTopicId(topicId);
        if((content.getFavoriteCount() - 1) < 0){
            content.setFavoriteCount(0);
        }else{
            content.setFavoriteCount(content.getFavoriteCount() - 1);
        }
        contentService.updateContentById(content);
        return Response.success(ResponseStatus.LIVE_SIGN_OUT_SUCCESS.status,ResponseStatus.LIVE_SIGN_OUT_SUCCESS.message);
    }


    @Override
    public int countFragment(long topicId,long uid){
        return liveMybatisDao.countFragment(topicId,uid);
    }

    @Override
    public Response getFavoriteList(long topicId) {
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
        return  Response.success(showFavoriteListFto);
    }


}
