package com.me2me.live.service;

import java.util.List;
import java.util.Map;

import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.live.dto.AggregationOptDto;
import com.me2me.live.dto.CreateKingdomDto;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.CreateVoteDto;
import com.me2me.live.dto.GetLiveDetailDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.GetLiveTimeLineDto2;
import com.me2me.live.dto.GetLiveUpdateDto;
import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.dto.Live4H5Dto;
import com.me2me.live.dto.LiveBarrageDto;
import com.me2me.live.dto.SearchDropAroundTopicDto;
import com.me2me.live.dto.SearchTopicDto;
import com.me2me.live.dto.SettingModifyDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.dto.StealResultDto;
import com.me2me.live.dto.TestApiDto;
import com.me2me.live.dto.UserAtListDTO;
import com.me2me.live.model.GiftInfo;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LotteryInfo;
import com.me2me.live.model.QuotationInfo;
import com.me2me.live.model.RobotInfo;
import com.me2me.live.model.TeaseInfo;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicDroparound;
import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.model.TopicFragmentWithBLOBs;
import com.me2me.live.model.TopicListed;
import com.me2me.live.model.TopicPriceSubsidyConfig;
import com.me2me.live.model.TopicTag;
import com.me2me.live.model.TopicTagDetail;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.user.dto.RechargeToKingdomDto;

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

    Response liveCover(long topicId,long uid,int vflag, int source,Long fromUid);

    Response barrage(LiveBarrageDto barrageDto);

    Response getLiveByCid(long cid,long uid,int vflag);

    /**
     * 获取直播列表按时间线
     * @return
     */
    Response MyLivesByUpdateTime(long uid,long updateTime);

    Response getMyTopic(long uid,long updateTime);

    Response myLivesAllByUpdateTime(long uid, long updateTime);
    
    Response getInactiveLive(long uid,long updateTime);

    Topic getTopicById(long topicId);

    List<Topic> getTopicList(long uid);

    List<Topic> getMyTopic4Follow(long uid);

    void createFavoriteDelete(long uid,long topicId);

    void deleteFavoriteDelete(long uid,long topicId);

    TopicFragmentWithBLOBs getLastTopicFragmentByUid(long topicId, long uid);

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
    
    /**
     * 获取王国详情，可分页
     * 返回也是按分页返回
     * @param liveDetailDto
     * @return
     */
    Response getLiveDetailPage(GetLiveDetailDto liveDetailDto);

    /**
     * 查询王国详情页分页的相关状态信息
     * @param topicId
     * @param pageNo
     * @param offset
     * @param uid
     * @return
     */
    Response detailPageStatus(long topicId, int pageNo, int offset);
    
    /**
     * 根据fragmentId，获取该fid所在的分页页数
     * @param topicId
     * @param fid
     * @param offset
     * @return
     */
    Response detailFidPage(long topicId, long fid, int offset);
    
    Response getLiveUpdate(GetLiveUpdateDto getLiveUpdateDto);

    Response testApi(TestApiDto request);

    Response kingdomSearch(long currentUid, KingdomSearchDTO searchDTO);

    Response settings(long uid ,long topicId, int vflag);

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
    
    Response myTopicOpt(long uid ,int action ,long topicId);

    TopicTag getTopicTagById(long id);
    
    TopicTag getTopicTagByTag(String tag);
    
    Long createTopicTag(TopicTag tag);
    
    void updateTopicTag(TopicTag tag);
    
    void delTagTopic(long tagTopicId);
    
    void addTagTopics(long tagId, List<Long> topicIdList);
    
    /**
     * 王国信息统计
     */
    void statKingdomCountDay();
	
	/**
	 * 获取所有足迹提示
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @return
	 */
    List<TopicFragmentTemplate> getFragmentTplList(String queryStr);
	/**
	 * 添加足迹提示消息 
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @return
	 */
	void addFragmentTpl(TopicFragmentTemplate obj);
	/**
	 * 获取一个足迹消息 
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param id
	 * @return
	 */
	TopicFragmentTemplate getFragmentTplById(Long id);
	/**
	 * 删除足迹提示消息 
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @param msgId
	 * @return
	 */
	void deleteFragmentTpl(Long msgId);
	/**
	 * 修改足迹提示消息 。
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @return
	 */
	void updateFragmentTpl(TopicFragmentTemplate obj);
	/**
	 * 拷贝王国到可串门的王国列表。
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @param tropicId
	 * @return
	 */
	
	void copyKingdomToDropAroundKingdom(int tropicId,int sort);
	/**
	 * 删除一个可串门的王国
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @param tropicId
	 * @return
	 */
	void delDropAroundKingdom(int tropicId);

	/**
	 * 修改一个可串门的王国
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param td
	 */
	void updateDropAroundKingdom(TopicDroparound td);
	/**
	 * 获取可被串门的王国列表
	 * @author zhangjiwei
	 * @date Mar 17, 2017
	 * @return
	 */
	public PageBean<SearchDropAroundTopicDto> getDropAroundKingdomPage(PageBean page,String queryStr);


	/**
	 * 搜索所有王国，返回分页
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param page
	 * @param searchKeyword
	 * @return
	 */
	PageBean<SearchDropAroundTopicDto> getTopicPage(PageBean page,String searchKeyword);
	
	/**
	 * 
	 * 搜索王国，带统计字段。
	 * @author zhangjiwei
	 * @date Mar 24, 2017
	 * @param page
	 * @param params
	 * @return
	 */
	PageBean<SearchTopicDto> getTopicPage(PageBean page,Map<String,Object> params);
	
	/**
	 * 王国标签查询
	 * @param uid
	 * @param topicId
	 * @return
	 */
	Response topicTags(long uid, long topicId);
	
	/**
	 * 王国标签修改
	 * @param uid
	 * @param topicId
	 * @param tags
	 * @return
	 */
	Response topicTagsModify(long uid, long topicId, String tags);
	
	/**
	 * 王国标签校验
	 * @param tag
	 * @return
	 */
	Response topicTagCheck(long uid, String tag);
	
	/**
	 * 标签王国查询
	 * @param tag
	 * @param sinceId
	 * @param currentUid
	 * @return
	 */
	Response tagKingdoms(String tag, long sinceId, long currentUid);
	
	/**
	 * 王国关联推荐查询接口
	 * @param topicId
	 * @param sinceId
	 * @param currentUid
	 * @return
	 */
	Response recQuery(long topicId, long sinceId, long currentUid);
	
	/**
	 * 投票创建接口
	 * @param dto
	 * @return
	 */
	public Response createVote(CreateVoteDto dto);
	
	
	/**
	 * 逗一逗后台分页查询
	 * @param page
	 * @param conditions
	 * @return
	 */
	public PageBean<TeaseInfo> getTeaseInfoPage(PageBean<TeaseInfo> page,Map<String,Object> conditions);
	
	/**
	 * 修改逗一逗信息
	 * @param teaseInfo
	 */
	public void updateTeaseInfoByKey(TeaseInfo teaseInfo);
	
	/**
	 * 添加逗一逗信息
	 * @param teaseInfo
	 * @return
	 */
	public Integer addTeaseInfo(TeaseInfo teaseInfo);
	
	/**
	 * 获取逗一逗信息
	 * @param id
	 * @return
	 */
	public TeaseInfo getTeaseInfoByKey(Long id);
	
	/**
	 * 逗一逗全量查询接口
	 * @return
	 */
	Response teaseListQuery();
	 
	/**
	 * 投票
	 * @param uid
	 * @param voteId
	 * @param optionId
	 * @return
	 */
	public Response vote(long uid,long voteId,String optionId);
	 
	/**
	 * 结束投票
	 * @param voteId
	 * @param uid
	 * @return
	 */
	public Response endVote(long voteId,long uid); 
	
	/**
	 * 重新发送投票
	 * @param fragmentId
	 * @param uid
	 * @return
	 */
	public Response resendVote(long fragmentId,long uid);
	
	/**
	 * 王国详情列表获取投票详情信息
	 * @param voteId
	 * @return
	 */
	public Response getTopicVoteInfo(long voteId);
	
	/**
	 * 获取投票详情
	 * @param voteId
	 * @param uid
	 * @return
	 */
	public Response getVoteInfo(long voteId,long uid); 
	/**
	 * 王国图库查询
	 * @author zhangjiwei
	 * @date May 5, 2017
	 * @param topicId
	 * @param direction
	 * @param fragmentId
	 * @param type 0老模式，1新模式
	 * @return
	 */
	Response kingdomImgDB(long topicId,int direction,long fragmentId,int type);
	/**
	 * 移除王国
	 * @author zhangjiwei
	 * @date May 5, 2017
	 * @param topicId
	 * @param uid 
	 * @return
	 */
	Response blockUserKingdom(long topicId, long uid);
	
	/**
	 * 批量获取王国
	 * @param ids
	 * @return
	 */
	List<Topic> getTopicListByIds(List<Long> ids);
	
	/**
	 * 王国@用户列表获取
	 * @param atListDTO
	 * @return
	 */
	Response userAtList(UserAtListDTO atListDTO);
	
	
	/**
	 * 确认情绪信息
	 * @param uid
	 * @param source
	 * @param emotionId
	 * @param happyValue
	 * @param freeValue
	 * @return
	 */
	Response submitEmotion(long uid, int source,long emotionId,int happyValue,int freeValue) ;
	
	
	 /**
	  * 开启新的一周情绪
	 * @param uid
	 * @param source
	 * @param image
	 * @param w
	 * @param h
	 * @return
	 */
	Response startNewEmotionInfo(long uid, int source,String image,int w,int h);
	
	
	/**
	 * 获取所有情绪信息列表
	 * @return
	 */
	public Response emotionInfoList();
	
	/**
	 * 王国国王变更
	 * @param topicId
	 * @param newUid
	 * @return 0表示变更成功，其他变更失败
	 */
	String changeTopicKing(long topicId, long newUid);

	/**
	 * 王国转让历史查询
	 * @return
	 */
	public Response getKingdomTransferRecord(long topicId,long sinceId);

	/**
	 * 偷金币 
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param topicId
	 * @return
	 */
	public Response<StealResultDto> stealKingdomCoin(long uid,long topicId);


	Response rechargeToKingdom(RechargeToKingdomDto rechargeToKingdomDto);

	Response getKingdomPrice(long topicId) ;
	 
	public List<TopicPriceSubsidyConfig> getTopicPriceSubsidyConfigList();
	
    public TopicPriceSubsidyConfig getTopicPriceSubsidyConfigById(long id);
    
	public void saveTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc);
	
	public void editTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc);
	
	public void delTopicPriceSubsidyConfig(long id);
	
    public Response searchTopicListedPage(int status,String title,int page, int pageSize);
    
    public void updateTopicListedStatus(TopicListed topicListed);
    
	public String handleTransaction(long id,long meNumber);
	
	public String changeTopicKing(TopicListed topicListed, long newUid);
	
	public Response listTopic(long topicId,long uid);
	
    public Response listedTopicList(long sinceId);
    
	public Response takeoverTopic(long topicId,long uid);

	/**
	 * 检查试用期标签，如果过了试用期，更新auto_tag字段为0；
	 * @author zhangjiwei
	 * @date Jul 5, 2017
	 */
	void updateExpiredTrialTag();
	
	public TopicListed getTopicListedByTopicId(long topicId);
	
	public Response getDaySignInfo(long uid) ;
	
	/**
	 * 赠送王国操作，可删除和激活。
	 * @author zhangjiwei
	 * @date Jul 19, 2017
	 * @param uid
	 * @param givenKingdomId
	 */
	Response givenKingdomOpration(long uid, long givenKingdomId,String action);
	
	 Response saveDaySignInfo(long uid, String image,String extra,String uids,int source,String quotationIds);

     Response saveSignSaveRecord(long uid,int type);

     RobotInfo selectRobotInfo();

     QuotationInfo selectQuotationByType(int type);

	List<QuotationInfo> selectQuotationByList(int limit);
     
     /**
      * 获取用户王国信息
      * @param uid
      * @return
      */
     Response userKingdomInfo(long uid);
     
     Response searchRobotListPage(String nickName,int type,int page, int pageSize);
     
     Response searchQuotationListPage(String quotation,int type,int page, int pageSize);
     
 	 int saveQuotationInfo(QuotationInfo quotationInfo);
 	 
	 int updateQuotationInfo(QuotationInfo quotationInfo);
	 
	 int delQuotationInfo(long id);
	 
	 QuotationInfo getQuotationInfoById(long id);
	 
	 /**
	  * 查询用户特殊王国信息
	  * @param uid
	  * @param searchType
	  * @return
	  */
	 Response specialKingdomInfo(long uid, int searchType);
	 
	/**
	 * 3.0.2版本的新UGC发布接口 将UGC发布到指定王国里去
	 * @param contentDto
	 * @return
	 */
	Response publishUGC(ContentDto contentDto);

	 Response createLottery(LotteryInfo lotteryInfo,int source);
	 
	 Response editLottery(LotteryInfo lotteryInfo);
	 
	 Response getLottery(long lotteryId,long uid,int outApp);
	 
	 Response getJoinLotteryUsers(long lotteryId,long sinceId);
	 
	 Response joinLottery(long lotteryId,String content,long uid,int source);
	 
	 Response delLotteryContent(long contentId,long uid);
	 
	 Response prohibitLottery(long lotteryId,long uid,long joinUid);
	 
	 Response getLotteryList(long topicId,long sinceId,long uid);
	 
	 Response runLottery(long lotteryId,long uid,int source);
	 
	 Response delLottery(long lotteryId,long uid);
	 /**
	  * 用户点击推送消息。
	  * @author zhangjiwei
	  * @date Aug 9, 2017
	  * @param uid
	  * @param topicId
	  * @return
	  */
	Response hitPushMessage(long uid,long topicId);

	/**
	 * 获取王国标签。
	 * @author zhangjiwei
	 * @date Aug 10, 2017
	 * @param topicId
	 * @return
	 */
	List<TopicTagDetail> getTopicTagDetailsByTopicId(long topicId);
	/**
	 * 添加用户下载记录
	 * @author zhangjiwei
	 * @date Aug 11, 2017
	 * @param uid 
	 * @param fromUid 推销员
	 * @return
	 */
	Response addAppDownloadLog(long uid,long fromUid);
	
    public List<GiftInfo> getGiftInfoList();
    /**
     * 用指定是否喜欢王国或标签。
     * @author zhangjiwei
     * @date Sep 1, 2017
     * @param uid
     * @param data 王国ID或者标签
     * @param isLike 1喜欢，0不喜欢
     * @param type 1王国，2标签
     * @return
     */
    Response userLike(long uid,String data,int isLike,int type);
    /**
     * 举报标签。
     * @author zhangjiwei
     * @date Sep 1, 2017
     * @param uid
     * @param topicId
     * @param tag
     * @return
     */
    Response badTag(long uid,long topicId,String tag);
    
    public GiftInfo getGiftInfoById(long id);
    
    public int saveGiftInfo(GiftInfo giftInfo);
    
    public int updateGiftInfo(GiftInfo giftInfo);
    	
    Response getAllGiftInfoList();
    
    Response sendGift(long uid,long topicId,long giftId,int giftCount,String onlyCode,int source);
    
    Response harvestKingdomList(long uid, int page);
    
    Response harvestKingdomCoin(long uid, long topicId);
}
