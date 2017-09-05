package com.me2me.activity.service;

import com.me2me.activity.dto.*;
import com.me2me.activity.model.Aactivity;
import com.me2me.activity.model.AactivityStage;
import com.me2me.activity.model.AcommonList;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.model.AkingDom;
import com.me2me.activity.model.AmiliData;
import com.me2me.activity.model.AppLightboxSource;
import com.me2me.activity.model.AppUiControl;
import com.me2me.activity.model.AtaskWithBLOBs;
import com.me2me.activity.model.Atopic;
import com.me2me.activity.model.Auser;
import com.me2me.activity.model.Tchannel;
import com.me2me.common.web.Response;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
public interface ActivityService {


    /**
     * 创建活动
     * @return
     */
    Response createActivity(CreateActivityDto createActivityDto);

    //创建王国活动
    void createActivityLive(CreateActivityDto createActivityDto);

    Response showActivity(int page,int pageSize,String keyword);

    List<ActivityWithBLOBs> getActivityTop5();
    
    List<ActivityWithBLOBs> getHotActivity();

    Response getActivity(int sinceId,long uid,int vflag);

    ActivityWithBLOBs loadActivityById(long id);

    void modifyActivity(ActivityWithBLOBs activity);

    void createActivityNotice(CreateActivityNoticeDto createActivityNoticeDto);

    void joinActivity(String content,long uid);

    ActivityH5Dto getActivityH5(long id);

    ActivityDto getActivity(long id);

    void createActivityReview(long id,long uid,String review,long atUid);

    void createActivityTagsDetails(long id,long uid,long tid);

    void createActivityLikesDetails(long id,long uid);

    int getLikeCount(long id);

    int getReviewCount(long id);

    Response luckAward(long uid ,String ip ,int activityName ,String channel ,String version);

    Response getAwardCount(long uid);

    Response awardShare(long uid ,int activityName);

    Response checkIsAward(long uid ,int activityName ,String channel ,String version ,String token);

    Response getUserAwardInfo(long uid);

    Response getAwardStatus(int activityName);
    
    Response getWinners(int activityName);
    
    Response getWinnersCommitInfo(int activityName);

    Response addWinners(long uid ,int activityName ,String mobile ,int awardId ,String awardName);

    Response getActivityUser(long uid);
    
    Auser getAuserByUid(long uid);
    
    Aactivity getAactivityById(long id);
    
    Atopic getAtopicByUidAndType(long uid, int type);
    
    Response checkUserActivityKindom(long uid, int type, long uid2);
    
    Response checkUserActivityKindom4Spring(long uid);
    
    void createActivityKingdom(long topicId, long uid, int type, long uid2);
    
    void createActivityKingdom4Spring(long topicId, long uid);
    
    List<Atopic> getAtopicsByUidsAndType(List<Long> uids, int type);

    Response enterActivity(QiUserDto qiUserDto);

    Response bindGetActivity(long uid ,String mobile ,String verifyCode);

    Response getActivityInfo(long activityId);

    Response oneKeyAudit();
    
    List<String> getAllUserMobilesInApp();
    
    List<String> getAll7DayMobiles();

    /**
     * 获取抽奖活动统计方法
     * 返回列表字段：时间，参与人数，参与人次，中奖次数，中奖奖品
     * 时间有如下：过去1小时内，过去2小时内，历史按天统计，总计
     * @param activityName	抽奖活动
     * @return
     */
    Response getLuckActStatList(int activityName);
    
    Response getAwardStatusList(int activityName);
    
    Response getLuckStatusById(int id);
    
    Response updateLuckStatus(LuckStatusDTO dto);
    
    Response getLuckPrizeList(int activityName);
    
    Response getLuckActList(int activityName, Date startTime, Date endTime);

    Atopic getAtopicByTopicId(long topicId);

    void updateAtopicStatus(Map map);

    Response getAliveInfo(long uid ,String topicName ,String nickName ,int pageNum ,int pageSize);

    Response getBridList(long uid ,String topicName ,String nickName ,int pageNum ,int pageSize ,int type);

    Response createDoubleLive(long uid ,long targetUid ,long activityId);

    Response getApplyInfo(long uid ,int type ,int pageNum ,int pageSize);

    Response applyDoubleLive(long uid ,int applyId ,int operaStatus);

    Response bridApply(long uid ,long targetUid);

    Response bridSearch(long uid ,int type ,int pageNum ,int pageSize);

    Response doublueLiveState(long uid);

    Response divorce(long uid ,long targetUid);
    
    Response genActivity7DayMiliList(Activity7DayMiliDTO dto);
    
    Response genMiliList4Spring(long uid);

    Response recommendHistory(long auid, int page, int pageSize);
    
    Response optForcedPairing(long uid, int action);
    
    Response getTaskList(long uid, int page, int pageSize);
    
    Response acceptTask(long tid, long uid);
    
    Response userTaskStatus(long tid, long uid);
    
    List<Long> getParingUser();
    
    Response bindNotice();
    
    Response pairingNotice();
    
    Response searchMiliDatas(String mkey, long activity, int page, int pageSize);
    
    Response noticeActivityStart();
    
    Response operaBrid(long uid ,int applyId ,int operaStatus);
    
    AmiliData getAmiliDataById(long id);
    
    void updateAmiliData(AmiliData data);
    
    void saveAmiliData(AmiliData data);
    
    void deleteAkingDomByTopicId(long topicId);
    
    List<AactivityStage> getAllStage(long activity);
    
    AactivityStage getAactivityStageById(long id);
    
    void updateAactivityStage(AactivityStage stage);
    
    Response getTaskPage(String title, long activityId, int page, int pageSize);
    
    AtaskWithBLOBs getAtaskWithBLOBsById(long id);
    
    void updateAtaskWithBLOBs(AtaskWithBLOBs task);
    
    TopicCountDTO getTopicCount(long topicId);
    
    List<TopicCountDTO> getTopicCountsByTopicIdsAndTime(List<Long> topicIds, String startTime, String endTime);
    
    List<TopicItem> getActivityTopicIds(long activityId);
    
    List<Long> get7dayTopicIdsByType(int type);
    
    List<Long> getSingleHotByDoubleTopicId(long doubleTopicId);
    
    void updateTopicHot(long topicId, int hot);
    
    Response send7DayKingdomMessage(int sex);
    
    List<Long> get7dayKingdomUpdateUids();
    
    Response taskPublish(long taskId, int type);
    
    Response forcedPairing(int isTest, long testUid1, long testUid2);

    ShowActivity7DayUserStatDTO get7dayUserStat(String channel, String code, String startTime, String endTime);
    
    ShowActivity7DayUsersDTO get7dayUsers(String channel, String code, String startTime, String endTime, int page, int pageSize);

    void deleteKingdomListByDayKey(String dayKey);
    
    void batchInsertKingdomList(List<KingdomHotDTO> list);
    
    void batchUpdateKingdomHot(List<KingdomHotDTO> list);
    
    void updateKingdomHotInitByTopicIds(List<Long> topicIds);
    
    List<Long> getSpringKingdomUids();
    
    List<AkingDom> getAkingDomsByConditions(int conditions, long activityId);
    
    List<AkingDom> getAkingdomsTop(long activityId, int topNum);
    
    List<AppUiControl> getAppUiControlList(String searchTime);
    
    AppUiControl getAppUiControlById(long id);
    
    void updateAppUiControl(AppUiControl appui);
    
    void createAppUiControl(AppUiControl appui);
    
    List<AppLightboxSource> getAppLightboxSourceList(String searchTime);
    
    AppLightboxSource getAppLightboxSourceById(long id);
    
    void updateAppLightboxSource(AppLightboxSource lightbox);
    
    void createAppLightboxSource(AppLightboxSource lightbox);
    
    Response getNewYearLiveInfo(long uid ,long activityId);

    Response getAllNewYearLiveInfo(long uid ,long activityId ,int pageNum ,int pageSize ,String topicName ,String nickName);

    Response getlightboxInfo();

    Response getActualAndHistoryList(long uid ,int type ,String date ,long activityId);

    ActivityWithBLOBs getActivityByCid(long topicId ,int type);

    void updateActivity(ActivityWithBLOBs activity);

    boolean isTopicRec(long topicId);
    
    List<Tchannel> getAppChannel(String code);
    
    Tchannel getTchannelById(long id);
    
    Tchannel getTchannelByCode(String code);
    
    void saveTchannel(Tchannel c);
    
    void updateTchannel(Tchannel c);
    
    void deleteTchannel(long id);
    
    Response specailTopicBillboard(long type, long searchUid);
    
    Response areaHot(long topicId);
    
    Response areaSupport(long optUid, long topicId);
    
    Response chatQuery(long sinceId);
    
    Response top10SupportChatQuery();
    
    Response chat(long uid, String message);
    
    void saveAcommonList(AcommonList alist);
    
    void updateAcommonList(AcommonList alist);
    
    void deleteAcommonListById(long id);
    
    AcommonList getAcommonList(long targetId, long activityId, int type);
    
    /**
     * 主播列表信息接口
     * @return
     */
    Response anchorList(long uid);
    
    /**
     * 主播活动报名接口
     * @param uid
     * @param aid
     * @return
     */
    Response enterAnchor(long uid, long aid);
}
