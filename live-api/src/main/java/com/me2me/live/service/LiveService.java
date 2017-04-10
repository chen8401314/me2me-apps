package com.me2me.live.service;

import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.live.dto.*;
import com.me2me.live.dto.ShowTopicListDto.ShowTopicElement;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicDroparound;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.model.TopicTag;
import com.me2me.live.model.TopicUserConfig;

import java.util.List;
import java.util.Map;

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

    Response getMyTopic(long uid,long updateTime);

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
    
    Response myTopicOpt(long uid ,int action ,long topicId);

    TopicTag getTopicTagById(long id);
    
    TopicTag getTopicTagByTag(String tag);
    
    void createTopicTag(TopicTag tag);
    
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
	Response topicTagCheck(String tag);
	
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
}
