package com.me2me.live.service;

import com.me2me.common.web.Response;
import com.me2me.live.dto.BarrageDto;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.SpeakDto;

import java.util.Date;

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
    Response getMyLives(long uid,long sinceId);

    /**
     * 获取所有未结束的直播列表
     * @param uid
     * @return
     */
    Response getLives(long uid,long sinceId);

    /**
     * 按时间倒序排列
     * @param uid
     * @param updateTime
     * @return
     */
    Response getLivesByUpdateTime(long uid,long updateTime);

    /**
     * 关注/取消关注我参与的直播
     * @param uid
     * @param topicId
     * @return
     */
    Response setLive(long uid,long topicId);

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

    Response liveCover(long topicId);

    Response barrage(BarrageDto barrageDto);
}
