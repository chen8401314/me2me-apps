package com.me2me.live.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicExample;
import com.me2me.live.model.TopicFragment;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Response createLive(CreateLiveDto createLiveDto) {
        Topic topic = new Topic();
        topic.setTitle(createLiveDto.getTitle());
        topic.setLiveImage(createLiveDto.getLiveImage());
        topic.setUid(createLiveDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
        liveMybatisDao.createTopic(topic);
        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status,ResponseStatus.USER_CREATE_LIVE_SUCCESS.message);
    }

    @Override
    public Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        Topic topic = liveMybatisDao.getTopicById(getLiveTimeLineDto.getTopicId());
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getSinceId());
        for(TopicFragment topicFragment : fragmentList){
            long uid = topicFragment.getUid();
            UserProfile userProfile = userService.getUserProfileByUid(uid);
            LiveTimeLineDto.LiveElement liveElement = LiveTimeLineDto.createElement();
            liveElement.setUid(uid);
            liveElement.setNickName(userProfile.getNickName());
            if(topicFragment.getContentType() == Specification.LiveContent.TEXT.index) {
                liveElement.setFragment(topicFragment.getFragment());
            }else if(topicFragment.getContentType() == Specification.LiveContent.IMAGE.index){
                liveElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + topicFragment.getFragmentImage());
            }
            liveElement.setPublishTime(topicFragment.getCreateTime());
            liveElement.setType(topicFragment.getType());
            //// TODO: 2016/4/12  follow 逻辑
            liveElement.setIsFollow(0);
            liveElement.setContentType(topicFragment.getContentType());
            liveElement.setFragmentId(topicFragment.getId());
            liveTimeLineDto.getLiveElements().add(liveElement);
        }
        return Response.success(ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.status,ResponseStatus.GET_LIVE_TIME_LINE_SUCCESS.message,liveTimeLineDto);
    }

    @Override
    public Response speak(SpeakDto speakDto) {
        TopicFragment topicFragment = new TopicFragment();
        topicFragment.setFragmentImage(speakDto.getFragmentImage());
        topicFragment.setFragment(speakDto.getFragment());
        topicFragment.setUid(speakDto.getUid());
        topicFragment.setContentType(speakDto.getContentType());
        topicFragment.setType(speakDto.getType());
        topicFragment.setTopicId(speakDto.getTopicId());
        liveMybatisDao.createTopicFragment(topicFragment);
        return Response.success(ResponseStatus.USER_SPEAK_SUCCESS.status,ResponseStatus.USER_SPEAK_SUCCESS.message);
    }

    /**
     * 获取我关注的直播，和我的直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getMyLives(long uid) {
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getMyLives(uid);
        for(Topic topic : topicList){
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN  + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            //// TODO: 2016/4/13
            showTopicElement.setAvatar("");
            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
        return Response.success(ResponseStatus.GET_MY_LIVE_SUCCESS.status,ResponseStatus.GET_MY_LIVE_SUCCESS.message,showTopicListDto);
    }

    /**
     * 获取所有正在直播列表
     * @param uid
     * @return
     */
    @Override
    public Response getLives(long uid) {
        ShowTopicListDto showTopicListDto = new ShowTopicListDto();
        List<Topic> topicList = liveMybatisDao.getLives();
        for(Topic topic : topicList){
            ShowTopicListDto.ShowTopicElement showTopicElement = ShowTopicListDto.createShowTopicElement();
            showTopicElement.setUid(topic.getUid());
            showTopicElement.setCoverImage(Constant.QINIU_DOMAIN  + "/" + topic.getLiveImage());
            showTopicElement.setTitle(topic.getTitle());
            //// TODO: 2016/4/13
            showTopicElement.setAvatar("");
            showTopicListDto.getShowTopicElements().add(showTopicElement);
        }
        return Response.success(ResponseStatus.GET_LIVES_SUCCESS.status,ResponseStatus.GET_LIVES_SUCCESS.message,showTopicListDto);
    }

    /**
     * 关注，取消关注
     * @param uid
     * @param topicId
     * @return
     */
    @Override
    public Response setLive(long uid, long topicId) {
        LiveFavorite liveFavorite = liveMybatisDao.getLiveFavorite(uid,topicId);
        if(liveFavorite != null){
            liveMybatisDao.deleteLiveFavorite(liveFavorite);
            return Response.success(ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.CANCEL_LIVE_FAVORITE_SUCCESS.message);
        }else {
            liveFavorite = new LiveFavorite();
            liveFavorite.setTopicId(topicId);
            liveFavorite.setUid(uid);
            liveMybatisDao.createLiveFavorite(liveFavorite);
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
                return Response.success(ResponseStatus.USER_FINISH_LIVE_SUCCESS.status, ResponseStatus.GET_USER_TAGS_SUCCESS.message);
            }else{
                return Response.success(ResponseStatus.USER_LIVE_IS_OVER.status, ResponseStatus.USER_LIVE_IS_OVER.message);
            }
        }else{
            return Response.success(ResponseStatus.FINISH_LIVE_NO_POWER.status,ResponseStatus.FINISH_LIVE_NO_POWER.message);
        }

    }
}
