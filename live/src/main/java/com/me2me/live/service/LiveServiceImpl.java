package com.me2me.live.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.*;
import com.me2me.live.model.*;
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

    @Autowired
    private ContentService contentService;

    @Override
    public Response createLive(CreateLiveDto createLiveDto) {
        Topic topic = new Topic();
        topic.setTitle(createLiveDto.getTitle());
        topic.setLiveImage(createLiveDto.getLiveImage());
        topic.setUid(createLiveDto.getUid());
        topic.setStatus(Specification.LiveStatus.LIVING.index);
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
        return Response.success(ResponseStatus.USER_CREATE_LIVE_SUCCESS.status,ResponseStatus.USER_CREATE_LIVE_SUCCESS.message);
    }

    @Override
    public Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto) {
        LiveTimeLineDto liveTimeLineDto = new LiveTimeLineDto();
        List<TopicFragment> fragmentList = liveMybatisDao.getTopicFragment(getLiveTimeLineDto.getTopicId(),getLiveTimeLineDto.getSinceId());
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
        //直播贴标签，粉丝贴标签，点赞处理
        //主播贴标
        // TODO: 2016/4/25 贴标签和点赞时候保存数量
        if(speakDto.getType() == Specification.LiveSpeakType.ANCHORWRITETAG.index){
            WriteTagDto writeTagDto = new WriteTagDto();
            writeTagDto.setTag(speakDto.getFragment());
          //  writeTagDto.setCid();
            //保存标签
           // contentService.writeTag(writeTagDto);
            //更新标签数量

        //粉丝贴标
        }else if(speakDto.getType() == Specification.LiveSpeakType.FANSWRITETAG.index){
            //保存标签
            WriteTagDto writeTagDto = new WriteTagDto();
            //保存标签
           // contentService.writeTag(writeTagDto);
        //点赞
        }else if(speakDto.getType() == Specification.LiveSpeakType.LIKES.index){
            //更新点赞的数量

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
                showTopicElement.setReviewCount(liveMybatisDao.countFragment(content.getForwardCid()));
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
    public Response setLive(long uid, long topicId) {
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
                return Response.success(ResponseStatus.USER_FINISH_LIVE_SUCCESS.status, ResponseStatus.GET_USER_TAGS_SUCCESS.message);
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
        return Response.success(ResponseStatus.LIVE_SIGN_OUT_SUCCESS.status,ResponseStatus.LIVE_SIGN_OUT_SUCCESS.message);
    }


    @Override
    public int countFragment(long topicId){
        return liveMybatisDao.countFragment(topicId);
    }


}
