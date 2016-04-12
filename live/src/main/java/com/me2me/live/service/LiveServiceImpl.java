package com.me2me.live.service;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.LiveTimeLineDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicFragment;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.model.UserProfile;
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
    private UserMybatisDao userMybatisDao;

    @Override
    public Response createLive(CreateLiveDto createLiveDto) {
        Topic topic = new Topic();
        topic.setTitle(createLiveDto.getTitle());
        topic.setLiveImage(createLiveDto.getLiveImage());
        topic.setUid(createLiveDto.getUid());
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
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
            LiveTimeLineDto.LiveElement liveElement = LiveTimeLineDto.createElement();
            liveElement.setUid(uid);
            liveElement.setNickName(userProfile.getNickName());
            liveElement.setFragment(topicFragment.getFragment());
            liveElement.setFragment(topicFragment.getFragment());
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
}
