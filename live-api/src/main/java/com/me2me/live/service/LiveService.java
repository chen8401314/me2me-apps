package com.me2me.live.service;

import com.me2me.common.web.Response;
import com.me2me.live.dto.*;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicUserConfig;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
public interface LiveService {

    /**
     * 创建直播
     * @return
     */
    Response createLive(CreateLiveDto createLiveDto);

    
    /**
     * 创建王国整合（创建+第一次发言）
     * @return
     */
    Response createKingdom(CreateKingdomDto createKingdomDto);
    
    /**
     * 获取直播时间线(轮询机制)
     * @return
     */
    Response getLiveTimeline(GetLiveTimeLineDto getLiveTimeLineDto);

    /**
     * 直播发言
     * @return
     */
    Response speak(SpeakDto speakDto);

    /**
     * 获取直播列表
     * @return
     */
    Response MyLives(long uid,long sinceId);

    /**
     * 获取所有未结束的直播列表
     * @param uid
     * @return
     */
    Response Lives(long uid,long sinceId);

    /**
     * 按时间倒序排列
     * @param uid
     * @param updateTime
     * @return
     */
    Response LivesByUpdateTime(long uid,long updateTime);

    /**
     * 关注/取消关注我参与的直播
     * @param uid
     * @param topicId
     * @return
     */
    Response setLive(long uid,long topicId,long topId,long bottomId);


    Response setLive2(long uid,long topicId,long topId,long bottomId,int action);

    Response setLiveFromSnsFollow(long uid, List<Long> topicIds, long topId, long bottomId, int action);
    
    /**
     * 结束自己当前直播
     * @param uid
     * @param topicId
     * @return
     */
    Response finishMyLive(long uid, long topicId);

    /**
     * 移除自己完结的直播
     * @param uid
     * @param topicId
     * @return
     */
    Response removeLive(long uid, long topicId);

    /**
     * 退出非自己的直播
     * @param uid
     * @param topicId
     * @return
     */
    Response signOutLive(long uid, long topicId);

    int countFragment(long topicId,long uid);

    Response getFavoriteList(long topicId);

    Response liveTimeline(GetLiveTimeLineDto getLiveTimeLineDto);

    Response liveCover(long topicId,long uid);

    Response barrage(LiveBarrageDto barrageDto);

    Response getLiveByCid(long cid,long uid);

    /**
     * 获取直播列表按时间线
     * @return
     */
    Response MyLivesByUpdateTime(long uid,long updateTime);

    Response myLivesAllByUpdateTime(long uid, long updateTime);
    
    Response getInactiveLive(long uid,long updateTime);

    Topic getTopicById(long topicId);

    List<Topic> getTopicList(long uid);

    List<Topic> getMyTopic4Follow(long uid);

    void createFavoriteDelete(long uid,long topicId);

    void deleteFavoriteDelete(long uid,long topicId);

    TopicFragment getLastTopicFragmentByUid(long topicId, long uid);

    Live4H5Dto getLive4H5(long id);

    Response getLiveTimeline2(GetLiveTimeLineDto2 getLiveTimeLineDto);

    Response cleanUpdate(long uid);

    Response genQRcode(long topicId);

    Response setLive3(long uid, long topicId);
    
    Response setLive3WithBatch(List<Long> uids, long topicId);

    Response deleteLiveFragment(long topicId, long fid, long uid);

    Response displayProtocol(int vLv);

    Response getRedDot(long uid,long updateTime);

    /**
     * 修改王国发言内容
     * @param speakDto
     * @return
     */
    Response editSpeak(SpeakDto speakDto);

    /**
     * 获取王国详情，可分页
     * @param liveDetailDto
     * @return
     */
    Response getLiveDetail(GetLiveDetailDto liveDetailDto);

    Response getLiveUpdate(GetLiveUpdateDto getLiveUpdateDto);

    Response testApi(TestApiDto request);

    Response kingdomSearch(long currentUid, KingdomSearchDTO searchDTO);

    Response settings(long uid ,long topicId);

    Response settingModify(SettingModifyDto dto);
    
    Response aggregationPublish(long uid, long topicId, long fid);

    Response aggregationOpt(AggregationOptDto dto);

    Response aggregationApplyOpt(AggregationOptDto dto);
    
    TopicUserConfig getTopicUserConfigByTopicIdAndUid(long topicId, long uid);
    
    Response subscribedTopicNew(long topicId, long uid, int action);
    
    List<LiveFavorite> getLiveFavoriteByTopicId(long topicId, List<Long> exceptUids, int start, int pageSize);
    
    int countLiveFavoriteByTopicId(long topicId, List<Long> exceptUids);
    
    Response fragmentForward(long uid, long fid, long sourceTopicId, long targetTopicId);

    Response recommend(long uid ,long topicId ,long action);

    Response dropAround(long uid ,long sourceTopicId);
}
