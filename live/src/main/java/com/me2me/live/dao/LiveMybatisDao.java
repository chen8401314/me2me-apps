package com.me2me.live.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.common.page.PageBean;
import com.me2me.common.web.Specification;
import com.me2me.live.dto.GetLiveDetailDto;
import com.me2me.live.dto.GetLiveUpdateDto;
import com.me2me.live.dto.SearchDropAroundTopicDto;
import com.me2me.live.dto.SearchTopicDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.mapper.BlockTopicMapper;
import com.me2me.live.mapper.DeleteLogMapper;
import com.me2me.live.mapper.GiftHistoryMapper;
import com.me2me.live.mapper.GiftInfoMapper;
import com.me2me.live.mapper.LiveDisplayBarrageMapper;
import com.me2me.live.mapper.LiveDisplayFragmentMapper;
import com.me2me.live.mapper.LiveDisplayProtocolMapper;
import com.me2me.live.mapper.LiveDisplayReviewMapper;
import com.me2me.live.mapper.LiveFavoriteDeleteMapper;
import com.me2me.live.mapper.LiveFavoriteMapper;
import com.me2me.live.mapper.LiveReadHistoryMapper;
import com.me2me.live.mapper.LotteryAppointMapper;
import com.me2me.live.mapper.LotteryContentMapper;
import com.me2me.live.mapper.LotteryInfoMapper;
import com.me2me.live.mapper.LotteryProhibitMapper;
import com.me2me.live.mapper.LotteryWinMapper;
import com.me2me.live.mapper.QuotationInfoMapper;
import com.me2me.live.mapper.RobotInfoMapper;
import com.me2me.live.mapper.RobotQuotationRecordMapper;
import com.me2me.live.mapper.SignRecordMapper;
import com.me2me.live.mapper.SignSaveRecordMapper;
import com.me2me.live.mapper.TeaseInfoMapper;
import com.me2me.live.mapper.TopicAggregationApplyMapper;
import com.me2me.live.mapper.TopicAggregationMapper;
import com.me2me.live.mapper.TopicBarrageMapper;
import com.me2me.live.mapper.TopicCategoryMapper;
import com.me2me.live.mapper.TopicDataMapper;
import com.me2me.live.mapper.TopicDroparoundMapper;
import com.me2me.live.mapper.TopicDroparoundTrailMapper;
import com.me2me.live.mapper.TopicFragmentMapper;
import com.me2me.live.mapper.TopicFragmentTemplateMapper;
import com.me2me.live.mapper.TopicGivenMapper;
import com.me2me.live.mapper.TopicListedMapper;
import com.me2me.live.mapper.TopicMapper;
import com.me2me.live.mapper.TopicNewsMapper;
import com.me2me.live.mapper.TopicPriceChangeLogMapper;
import com.me2me.live.mapper.TopicPriceHisMapper;
import com.me2me.live.mapper.TopicPriceSubsidyConfigMapper;
import com.me2me.live.mapper.TopicReadHisMapper;
import com.me2me.live.mapper.TopicTagDetailMapper;
import com.me2me.live.mapper.TopicTagMapper;
import com.me2me.live.mapper.TopicTransferRecordMapper;
import com.me2me.live.mapper.TopicUserConfigMapper;
import com.me2me.live.mapper.UserStealLogMapper;
import com.me2me.live.mapper.VoteInfoMapper;
import com.me2me.live.mapper.VoteOptionMapper;
import com.me2me.live.mapper.VoteRecordMapper;
import com.me2me.live.model.BlockTopic;
import com.me2me.live.model.BlockTopicExample;
import com.me2me.live.model.DeleteLog;
import com.me2me.live.model.GiftHistory;
import com.me2me.live.model.GiftHistoryExample;
import com.me2me.live.model.GiftInfo;
import com.me2me.live.model.GiftInfoExample;
import com.me2me.live.model.LiveDisplayBarrage;
import com.me2me.live.model.LiveDisplayFragment;
import com.me2me.live.model.LiveDisplayFragmentExample;
import com.me2me.live.model.LiveDisplayProtocol;
import com.me2me.live.model.LiveDisplayProtocolExample;
import com.me2me.live.model.LiveDisplayReview;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LiveFavoriteDelete;
import com.me2me.live.model.LiveFavoriteDeleteExample;
import com.me2me.live.model.LiveFavoriteExample;
import com.me2me.live.model.LiveReadHistory;
import com.me2me.live.model.LiveReadHistoryExample;
import com.me2me.live.model.LotteryAppoint;
import com.me2me.live.model.LotteryAppointExample;
import com.me2me.live.model.LotteryContent;
import com.me2me.live.model.LotteryContentExample;
import com.me2me.live.model.LotteryInfo;
import com.me2me.live.model.LotteryInfoExample;
import com.me2me.live.model.LotteryProhibit;
import com.me2me.live.model.LotteryProhibitExample;
import com.me2me.live.model.LotteryWin;
import com.me2me.live.model.LotteryWinExample;
import com.me2me.live.model.QuotationInfo;
import com.me2me.live.model.QuotationInfoExample;
import com.me2me.live.model.RobotInfo;
import com.me2me.live.model.RobotInfoExample;
import com.me2me.live.model.RobotQuotationRecord;
import com.me2me.live.model.SignRecord;
import com.me2me.live.model.SignSaveRecord;
import com.me2me.live.model.TeaseInfo;
import com.me2me.live.model.TeaseInfoExample;
import com.me2me.live.model.Topic;
import com.me2me.live.model.Topic2;
import com.me2me.live.model.TopicAggregation;
import com.me2me.live.model.TopicAggregationApply;
import com.me2me.live.model.TopicAggregationApplyExample;
import com.me2me.live.model.TopicAggregationExample;
import com.me2me.live.model.TopicBarrage;
import com.me2me.live.model.TopicBarrageExample;
import com.me2me.live.model.TopicCategory;
import com.me2me.live.model.TopicCategoryExample;
import com.me2me.live.model.TopicData;
import com.me2me.live.model.TopicDataExample;
import com.me2me.live.model.TopicDroparound;
import com.me2me.live.model.TopicDroparoundExample;
import com.me2me.live.model.TopicDroparoundTrail;
import com.me2me.live.model.TopicExample;
import com.me2me.live.model.TopicFragmentExample;
import com.me2me.live.model.TopicFragmentWithBLOBs;
import com.me2me.live.model.TopicPriceChangeLog;
import com.me2me.live.model.TopicReadHisExample;
import com.me2me.live.model.TopicFragmentExample.Criteria;
import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.model.TopicFragmentTemplateExample;
import com.me2me.live.model.TopicGiven;
import com.me2me.live.model.TopicGivenExample;
import com.me2me.live.model.TopicListed;
import com.me2me.live.model.TopicListedExample;
import com.me2me.live.model.TopicNews;
import com.me2me.live.model.TopicNewsExample;
import com.me2me.live.model.TopicPriceHis;
import com.me2me.live.model.TopicPriceHisExample;
import com.me2me.live.model.TopicPriceSubsidyConfig;
import com.me2me.live.model.TopicPriceSubsidyConfigExample;
import com.me2me.live.model.TopicReadHis;
import com.me2me.live.model.TopicTag;
import com.me2me.live.model.TopicTagDetail;
import com.me2me.live.model.TopicTagDetailExample;
import com.me2me.live.model.TopicTagExample;
import com.me2me.live.model.TopicTransferRecord;
import com.me2me.live.model.TopicTransferRecordExample;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.live.model.TopicUserConfigExample;
import com.me2me.live.model.UserStealLog;
import com.me2me.live.model.VoteInfo;
import com.me2me.live.model.VoteInfoExample;
import com.me2me.live.model.VoteOption;
import com.me2me.live.model.VoteOptionExample;
import com.me2me.live.model.VoteRecord;
import com.me2me.live.model.VoteRecordExample;
import com.me2me.sns.mapper.SnsCircleMapper;
import com.me2me.sns.model.SnsCircle;
import com.me2me.sns.model.SnsCircleExample;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
@Repository
public class LiveMybatisDao {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicFragmentMapper topicFragmentMapper;

    @Autowired
    private LiveFavoriteMapper liveFavoriteMapper;

    @Autowired
    private LiveReadHistoryMapper liveReadHistoryMapper;

    @Autowired
    private TopicBarrageMapper topicBarrageMapper;

    @Autowired
    private LiveFavoriteDeleteMapper liveFavoriteDeleteMapper;

    @Autowired
    private LiveDisplayBarrageMapper liveDisplayBarrageMapper;

    @Autowired
    private LiveDisplayFragmentMapper liveDisplayFragmentMapper;

    @Autowired
    private LiveDisplayReviewMapper liveDisplayReviewMapper;

    @Autowired
    private DeleteLogMapper deleteLogMapper;

    @Autowired
    private SnsCircleMapper snsCircleMapper;

    @Autowired
    private LiveDisplayProtocolMapper liveDisplayProtocolMapper;

    @Autowired
    private TopicUserConfigMapper topicUserConfigMapper;

    @Autowired
    private TopicAggregationApplyMapper topicAggregationApplyMapper;

    @Autowired
    private TopicAggregationMapper topicAggregationMapper;

    @Autowired
    private TopicDroparoundMapper topicDroparoundMapper;

    @Autowired
    private TopicDroparoundTrailMapper topicDroparoundTrailMapper;

    @Autowired
    private TopicFragmentTemplateMapper topicFragmentTemplateMapper;

    @Autowired
    private TopicTagMapper topicTagMapper;

    @Autowired
    private TopicTagDetailMapper topicTagDetailMapper;
    
    @Autowired
    private TeaseInfoMapper teaseInfoMapper;
    
    @Autowired
    private VoteInfoMapper voteInfoMapper;
    
    @Autowired
    private VoteOptionMapper voteOptionMapper;
    
    @Autowired
    private VoteRecordMapper voteRecordMapper;
    
    @Autowired
    private BlockTopicMapper  blockTopicMapper;
    
    @Autowired
    private TopicNewsMapper  topicNewsMapper;
    
    @Autowired
    private TopicTransferRecordMapper  topicTransferRecordMapper;
    
    @Autowired
    private UserStealLogMapper  stealLogMapper;
    
    @Autowired
    private TopicReadHisMapper topicReadHisMapper;
    
    @Autowired
    private TopicPriceHisMapper topicPriceHisMapper;
    
    @Autowired
    private TopicDataMapper topicDataMapper;
    
    @Autowired
    private TopicPriceSubsidyConfigMapper topicPriceSubsidyConfigMapper;
    
    @Autowired
    private TopicListedMapper topicListedMapper;
    
    @Autowired
    private TopicGivenMapper givenMapper;
    
    @Autowired
    private RobotInfoMapper robotInfoMapper;
    
    @Autowired
    private QuotationInfoMapper quotationInfoMapper;
    
    @Autowired
    private SignRecordMapper signRecordMapper;
    
    @Autowired
    private RobotQuotationRecordMapper robotQuotationRecordMapper;
    
    @Autowired
    private SignSaveRecordMapper signSaveRecordMapper;
    
    @Autowired
    private LotteryInfoMapper lotteryInfoMapper;
    
    @Autowired
    private LotteryContentMapper lotteryContentMapper;
    
    @Autowired
    private LotteryProhibitMapper lotteryProhibitMapper;
    
    @Autowired
    private LotteryWinMapper lotteryWinMapper;
    
    @Autowired
    private TopicPriceChangeLogMapper topicPriceChangeLogMapper;
    
    @Autowired
    private GiftInfoMapper giftInfoMapper;
    
    @Autowired
    private GiftHistoryMapper giftHistoryMapper;
    
    @Autowired
    private TopicCategoryMapper topicCategoryMapper;
    
    @Autowired
    private LotteryAppointMapper lotteryAppointMapper;
    
    public void createTopic(Topic topic) {
        topicMapper.insertSelective(topic);
    }

    public Topic getTopicById(long topicId) {
        return topicMapper.selectByPrimaryKey(topicId);
    }

    public List<Topic> getTopicsByIds(List<Long> ids){
    	TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        return topicMapper.selectByExample(example);
    }

    public int getUserTopicCount(long uid){
    	TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return topicMapper.countByExample(example);
    }
    
    public List<TopicFragmentWithBLOBs> getTopicFragment(long topicId, long sinceId, int pageSize) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdGreaterThan(sinceId);
//        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id asc limit "+ pageSize);
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }
  /**
   * 获取王国图库
   * @author zhangjiwei
   * @date May 5, 2017
   * @param topicId
   * @param sinceId 
   * @param goDown 是否向下翻
   * @param size 数量
   * @return
   */
    public List<TopicFragmentWithBLOBs> getTopicImgFragment(long topicId, long sinceId,boolean goDown,int size) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        if(goDown){
        	criteria.andIdGreaterThan(sinceId);
        }else{
        	criteria.andIdLessThan(sinceId);
        }
        criteria.andTypeEqualTo(0);
        criteria.andContentTypeEqualTo(1);
        criteria.andStatusEqualTo(1);
        example.setOrderByClause("id asc limit "+size);
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }
    /**
     * 获取指定ID的上一个或者下一个id的所在月份
     * @author zhangjiwei
     * @date May 17, 2017
     * @param topicId 王国ID。
     * @param sinceId 数据起始ID
     * @param goDown 向下还是向上。
     * @return
     */
	public String getNextMonthByImgFragment(long topicId, long sinceId, boolean goDown) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String month = null;

		TopicFragmentExample example = new TopicFragmentExample();
		Criteria ct =example.createCriteria().andTopicIdEqualTo(topicId).andTypeEqualTo(0).andContentTypeEqualTo(1).andStatusEqualTo(1);
		if (goDown) {
			ct.andIdGreaterThan(sinceId);
			example.setOrderByClause(" id asc limit 1");
		} else {
			ct.andIdLessThan(sinceId);
	    	example.setOrderByClause(" id desc limit 1");
		}
		List<TopicFragmentWithBLOBs> fgs = topicFragmentMapper.selectByExampleWithBLOBs(example);
		if (fgs.size() > 0) {
			Date date = fgs.get(0).getCreateTime();
			month = sdf.format(date);
		}

		return month;
	}
	/**
	 * 按月获取王国图库图片
	 * @author zhangjiwei
	 * @date May 17, 2017
	 * @param topicId
	 * @param month
	 * @return
	 */
	public List<TopicFragmentWithBLOBs> getImgFragmentByMonth(long topicId,String month){
		return topicFragmentMapper.getImgFragmentByMonth(topicId, month);
	}

    public List<TopicFragmentWithBLOBs> getTopicFragmentByMode(long topicId, long sinceId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdGreaterThan(sinceId);
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        criteria.andTypeNotEqualTo(Specification.LiveSpeakType.ANCHOR_AT.index);
        example.setOrderByClause("id asc limit 10 ");
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public List<TopicFragmentWithBLOBs> getTopicReviewByMode(long topicId, long sinceId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdGreaterThan(sinceId);
        criteria.andUidNotEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        TopicFragmentExample.Criteria criteria2 = example.createCriteria();
        criteria2.andTypeEqualTo(Specification.LiveSpeakType.ANCHOR_AT.index);
        example.or(criteria2);
        example.setOrderByClause("id asc limit 30 ");
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public List<TopicFragmentWithBLOBs> getPrevTopicFragment(long topicId, long sinceId) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id desc limit 10 ");
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public TopicFragmentWithBLOBs getLastTopicFragment(long topicId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id desc limit 1 ");
        List<TopicFragmentWithBLOBs> topicFragmentList = topicFragmentMapper.selectByExampleWithBLOBs(example);
        return (topicFragmentList != null && topicFragmentList.size() > 0) ? topicFragmentList.get(0) : null;
    }

    public TopicFragmentWithBLOBs getLastTopicFragmentByCoreCircle(long topicId, String coreCircle) {
        JSONArray array = JSON.parseArray(coreCircle);
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getLong(i));
        }
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidIn(list);
        /*criteria.andTypeNotEqualTo(Specification.LiveSpeakType.AT.index);
        criteria.andTypeNotEqualTo(Specification.LiveSpeakType.ANCHOR_AT.index);*/
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id desc limit 1");
        List<TopicFragmentWithBLOBs> topicFragmentList = topicFragmentMapper.selectByExampleWithBLOBs(example);
        return (topicFragmentList != null && topicFragmentList.size() > 0) ? topicFragmentList.get(0) : null;
    }

    public TopicFragmentWithBLOBs getLastTopicFragmentByUid(long topicId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo(Specification.LiveSpeakType.ANCHOR.index);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id desc limit 1 ");
        List<TopicFragmentWithBLOBs> topicFragmentList = topicFragmentMapper.selectByExampleWithBLOBs(example);
        return (topicFragmentList != null && topicFragmentList.size() > 0) ? topicFragmentList.get(0) : null;
    }

    public void createTopicFragment(TopicFragmentWithBLOBs topicFragment) {
        topicFragment.setStatus(Specification.TopicFragmentStatus.ENABLED.index);
        topicFragmentMapper.insertSelective(topicFragment);
        // 王国更新的时候去掉用户屏蔽的王国。
        this.removeBlockedKingodm(topicFragment.getTopicId());
    }

    public Topic getTopic(long uid, long topicId) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andIdEqualTo(topicId);
        List<Topic> list = topicMapper.selectByExample(example);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public void updateTopic(Topic topic) {
        topicMapper.updateByPrimaryKeySelective(topic);
    }

    public List<Topic> getMyLives(long uid, long sinceId, List<Long> topics) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andIdLessThan(sinceId);
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        example.setOrderByClause("id desc, status asc limit 10");
        return topicMapper.selectByExample(example);
    }

    public int countLives() {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andStatusNotEqualTo(Specification.LiveStatus.LIVING.index);
        return topicMapper.countByExample(example);
    }

    public List<Topic> getALLMyLivesByUpdateTime(long uid, long updateTime, List<Long> topics) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andLongTimeLessThan(updateTime);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andLongTimeLessThan(updateTime);
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        //最后更新时间降序排列
        example.setOrderByClause("long_time desc limit 10");
        return topicMapper.selectByExample(example);
    }

    public List<Topic> getMyLivesByUpdateTime(long uid, long updateTime, List<Long> topics) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andLongTimeLessThan(updateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        criteria.andLongTimeGreaterThan(calendar.getTimeInMillis());
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andLongTimeLessThan(updateTime);
            criteriaOr.andLongTimeGreaterThan(calendar.getTimeInMillis());
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        //最后更新时间降序排列
        example.setOrderByClause("long_time desc limit 10");
        return topicMapper.selectByExample(example);
    }

    public List<Topic2> getMyLivesByUpdateTimeNew(long uid, long updateTime) {
        Map map = Maps.newHashMap();
        map.put("uid" ,uid);
        map.put("updateTime" ,updateTime);
        return topicMapper.getMyLivesByUpdateTimeNew(map);
    }

    //获取所有直播红点
    public List<Topic> getMyLivesByUpdateTime2(long uid, long updateTime, List<Long> topics) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andLongTimeLessThan(updateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        criteria.andLongTimeGreaterThan(calendar.getTimeInMillis());
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andLongTimeLessThan(updateTime);
            criteriaOr.andLongTimeGreaterThan(calendar.getTimeInMillis());
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        //最后更新时间降序排列（必须获取所有的）
        example.setOrderByClause("long_time desc");
        return topicMapper.selectByExample(example);
    }

    public int getInactiveLiveCount(long uid, List<Long> topics) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        criteria.andLongTimeLessThan(calendar.getTimeInMillis());
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            criteriaOr.andLongTimeLessThan(calendar.getTimeInMillis());
            example.or(criteriaOr);
        }
        return topicMapper.countByExample(example);
    }

    public List<Long> getTopicId(long uid) {
        List<Long> result = Lists.newArrayList();
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<LiveFavorite> liveFavoriteList = liveFavoriteMapper.selectByExample(example);
        for (LiveFavorite liveFavorite : liveFavoriteList) {
            result.add(liveFavorite.getTopicId());
        }
        return result;
    }

    public List<Topic> getLives(long sinceId) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andIdLessThan(sinceId);
        criteria.andStatusEqualTo(Specification.LiveStatus.LIVING.index);
        example.setOrderByClause(" long_time desc limit 20 ");
        return topicMapper.selectByExample(example);
    }

    public List<Topic> getLivesByUpdateTime(long updateTime) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andLongTimeLessThan(updateTime);
        criteria.andStatusEqualTo(Specification.LiveStatus.LIVING.index);
        example.setOrderByClause(" long_time desc limit 10 ");
        return topicMapper.selectByExample(example);
    }

    public LiveFavorite getLiveFavorite(long uid, long topicId) {
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        List<LiveFavorite> liveFavoriteList = liveFavoriteMapper.selectByExample(example);
        return (liveFavoriteList != null && liveFavoriteList.size() > 0) ? liveFavoriteList.get(0) : null;
    }

    public List<LiveFavorite> getLiveFavoritePageByTopicIdAndExceptUids(long topicId, List<Long> exceptUids, int start, int pageSize){
    	LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        if(null != exceptUids && exceptUids.size() > 0){
        	criteria.andUidNotIn(exceptUids);
        }
        example.setOrderByClause(" id asc limit "+start+","+pageSize);
        return liveFavoriteMapper.selectByExample(example);
    }

    public int countLiveFavoriteByTopicIdAndExceptUids(long topicId, List<Long> exceptUids){
    	LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        if(null != exceptUids && exceptUids.size() > 0){
        	criteria.andUidNotIn(exceptUids);
        }
        return liveFavoriteMapper.countByExample(example);
    }

    public List<LiveFavorite> getLiveFavoritesByUidAndTopicIds(long uid, List<Long> topicIds){
    	if(null == topicIds || topicIds.size() == 0){
    		return null;
    	}
    	LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdIn(topicIds);
        return liveFavoriteMapper.selectByExample(example);
    }

    public List<LiveFavorite> getLiveFavoritesByUidsAndTopicId(List<Long> uids, long topicId){
    	LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(uids);
        criteria.andTopicIdEqualTo(topicId);
        return liveFavoriteMapper.selectByExample(example);
    }

    public void createLiveFavorite(LiveFavorite liveFavorite) {
        liveFavoriteMapper.insertSelective(liveFavorite);
    }

    public void deleteLiveFavorite(LiveFavorite liveFavorite) {
        liveFavoriteMapper.deleteByPrimaryKey(liveFavorite.getId());
    }

    public int countFragment(long topicId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidNotEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        return topicFragmentMapper.countByExample(example);
    }

    public int countFragmentByUid(long topicId, long uid) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        return topicFragmentMapper.countByExample(example);
    }

    public List<LiveFavorite> getFavoriteList(long topicId) {
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        example.setOrderByClause(" id asc limit 20");
        return liveFavoriteMapper.selectByExample(example);
    }

    public List<LiveFavorite> getFavoriteAll(long topicId) {
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        return liveFavoriteMapper.selectByExample(example);
    }


    public LiveReadHistory getLiveReadHistory(long topicId, long uid) {
        LiveReadHistoryExample example = new LiveReadHistoryExample();
        LiveReadHistoryExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        List<LiveReadHistory> liveReadHistories = liveReadHistoryMapper.selectByExample(example);
        return (liveReadHistories != null && liveReadHistories.size() > 0) ? liveReadHistories.get(0) : null;
    }

    public void createLiveReadHistory(long topicId, long uid) {
        LiveReadHistory liveReadHistory = new LiveReadHistory();
        liveReadHistory.setTopicId(topicId);
        liveReadHistory.setUid(uid);
        liveReadHistoryMapper.insertSelective(liveReadHistory);

    }

    public void createTopicBarrage(TopicBarrage topicBarrage) {
        topicBarrageMapper.insertSelective(topicBarrage);
    }

   /* public List<TopicBarrage> getBarrage(long topicId,long sinceId, long topId ,long bottomId ){
        TopicBarrage topicBarrage = new TopicBarrage();
        topicBarrage.setTopId(topicId);
        topicBarrage.setTopId(topId);
        topicBarrage.setBottomId(bottomId);
        topicBarrage.setId(sinceId);
        return topicBarrageMapper.selectByExampleWithBLOBsDistinct(topicBarrage);
    }*/

    public List<TopicBarrage> getBarrage(long topicId, long sinceId, long topId, long bottomId) {
        TopicBarrageExample example = new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andTopIdGreaterThanOrEqualTo(topId);
        criteria.andBottomIdEqualTo(bottomId);
        criteria.andIdGreaterThan(sinceId);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause(" id asc limit 20 ");
        return topicBarrageMapper.selectByExampleWithBLOBs(example);
    }

    public TopicBarrage getBarrage(long topicId, long topId, long bottomId, int type, long uid) {
        TopicBarrageExample example = new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andTopIdEqualTo(topId);
        criteria.andBottomIdEqualTo(bottomId);
        criteria.andTypeEqualTo(type);
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        List<TopicBarrage> topicBarrages = topicBarrageMapper.selectByExampleWithBLOBs(example);
        return com.me2me.common.utils.Lists.getSingle(topicBarrages);
    }

    public List<TopicBarrage> getBarrageListByTopicIds(List<Long> topicIds, long topId, long bottomId, int type, long uid){
    	TopicBarrageExample example = new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdIn(topicIds);
        criteria.andTopIdEqualTo(topId);
        criteria.andBottomIdEqualTo(bottomId);
        criteria.andTypeEqualTo(type);
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        return topicBarrageMapper.selectByExampleWithBLOBs(example);
    }

    public List<Topic> getInactiveLive(long uid, List<Long> topics, long updateTime) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        criteria.andLongTimeLessThan(updateTime);
        criteria.andUidEqualTo(uid);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if (topics != null && topics.size() > 0) {
            criteriaOr.andLongTimeLessThan(updateTime);
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        //最后更新时间降序排列
        example.setOrderByClause(" long_time desc limit 10");
        return topicMapper.selectByExample(example);
    }

    public List<Topic> getMyTopic(long uid) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        return topicMapper.selectByExample(example);
    }

    public void deleteLiveFavoriteByUid(long uid, long topicId) {
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        liveFavoriteMapper.deleteByExample(example);
    }

    public void createFavoriteDelete(long uid, long topicId) {
        LiveFavoriteDelete liveFavoriteDelete = new LiveFavoriteDelete();
        liveFavoriteDelete.setUid(uid);
        liveFavoriteDelete.setTopicId(topicId);
        liveFavoriteDeleteMapper.insertSelective(liveFavoriteDelete);
    }

    public LiveFavoriteDelete getFavoriteDelete(long uid, long topicId) {
        LiveFavoriteDeleteExample example = new LiveFavoriteDeleteExample();
        LiveFavoriteDeleteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        List<LiveFavoriteDelete> list = liveFavoriteDeleteMapper.selectByExample(example);
        return com.me2me.common.utils.Lists.getSingle(list);
    }

    public List<LiveFavoriteDelete> getFavoriteDeletesByTopicIds(long uid, List<Long> topicIds){
    	LiveFavoriteDeleteExample example = new LiveFavoriteDeleteExample();
        LiveFavoriteDeleteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdIn(topicIds);
        return liveFavoriteDeleteMapper.selectByExample(example);
    }

    public void deleteFavoriteDelete(long uid, long topicId) {
        LiveFavoriteDeleteExample example = new LiveFavoriteDeleteExample();
        LiveFavoriteDeleteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        liveFavoriteDeleteMapper.deleteByExample(example);
    }

    public void batchDeleteFavoriteDeletes(long uid, List<Long> topicIds){
    	LiveFavoriteDeleteExample example = new LiveFavoriteDeleteExample();
        LiveFavoriteDeleteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdIn(topicIds);
        liveFavoriteDeleteMapper.deleteByExample(example);
    }

    public void batchDeleteFavoriteDeletesByUids(List<Long> uids, long topicId){
    	LiveFavoriteDeleteExample example = new LiveFavoriteDeleteExample();
        LiveFavoriteDeleteExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(uids);
        criteria.andTopicIdEqualTo(topicId);
        liveFavoriteDeleteMapper.deleteByExample(example);
    }

    public List<Topic> getMyTopic4Follow(long uid) {
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return topicMapper.selectByExample(example);
    }

    public List<TopicFragmentWithBLOBs> getTopicFragment(long topicId) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        example.setOrderByClause("id asc ");
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public void createLiveDisplayFragment(SpeakDto speakDto) {
        LiveDisplayFragment displayFragment = new LiveDisplayFragment();
        displayFragment.setUid(speakDto.getUid());
        displayFragment.setFragment(speakDto.getFragment());
        displayFragment.setFragmentImage(speakDto.getFragmentImage());
        displayFragment.setTopicId(speakDto.getTopicId());
        displayFragment.setType(speakDto.getType());
        liveDisplayFragmentMapper.insertSelective(displayFragment);
    }

    public void updateLiveDisplayFragment(SpeakDto speakDto) {
        LiveDisplayFragmentExample example = new LiveDisplayFragmentExample();
        liveDisplayFragmentMapper.selectByExample(example);
        LiveDisplayFragment displayFragment = new LiveDisplayFragment();
        displayFragment.setUid(speakDto.getUid());
        displayFragment.setFragment(speakDto.getFragment());
        displayFragment.setFragmentImage(speakDto.getFragmentImage());
        displayFragment.setTopicId(speakDto.getTopicId());
        displayFragment.setType(speakDto.getType());
        liveDisplayFragmentMapper.insertSelective(displayFragment);
    }

    public void createLiveDisplayReview(SpeakDto speakDto) {
        LiveDisplayReview displayReview = new LiveDisplayReview();
        displayReview.setUid(speakDto.getUid());
        displayReview.setReview(speakDto.getFragment());
        displayReview.setTopicId(speakDto.getTopicId());
        displayReview.setType(speakDto.getType());
        liveDisplayReviewMapper.insertSelective(displayReview);
    }

    public void createLiveDisplayBarrage(SpeakDto speakDto) {
        LiveDisplayBarrage displayBarrage = new LiveDisplayBarrage();
        displayBarrage.setUid(speakDto.getUid());
        displayBarrage.setBarrage(speakDto.getFragment());
        displayBarrage.setTopicId(speakDto.getTopicId());
        liveDisplayBarrageMapper.insertSelective(displayBarrage);
    }

    public List<LiveDisplayFragment> getDisPlayFragmentByMode(long topicId, long sinceId, long uid) {
        LiveDisplayFragmentExample example = new LiveDisplayFragmentExample();
        LiveDisplayFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdGreaterThan(sinceId);
        criteria.andUidEqualTo(uid);
        example.setOrderByClause("id asc limit 10 ");
        return liveDisplayFragmentMapper.selectByExample(example);
    }

    public TopicBarrage getTopicBarrageByTopicId(long topicId, long uid) {
        TopicBarrageExample example = new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo(Specification.LiveSpeakType.LIKES.index);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);
        List<TopicBarrage> list = topicBarrageMapper.selectByExample(example);
        return com.me2me.common.utils.Lists.getSingle(list);
    }

    public List<SnsCircle> getCoreCircle(long uid) {
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andOwnerEqualTo(uid);
        criteria.andInternalStatusEqualTo(Specification.SnsCircle.CORE.index);
        return snsCircleMapper.selectByExample(example);
    }


    public int deleteLiveFragmentById(long fid) {
    	TopicFragmentWithBLOBs fragment = new TopicFragmentWithBLOBs();
        fragment.setId(fid);
        fragment.setStatus(Specification.TopicFragmentStatus.DISABLED.index);
        return topicFragmentMapper.updateByPrimaryKeySelective(fragment);
    }

    public void createDeleteLog(DeleteLog deleteLog) {
        deleteLogMapper.insert(deleteLog);
    }

    public LiveDisplayProtocol getLiveDisplayProtocol(int vLv) {
        LiveDisplayProtocolExample examle = new LiveDisplayProtocolExample();
        examle.or().andVLvEqualTo(Specification.VipLevel.noV.index);
        examle.or().andVLvEqualTo(vLv);
        examle.setOrderByClause(" vlv desc limit 1");

        List<LiveDisplayProtocol> list = liveDisplayProtocolMapper.selectByExample(examle);
        return (list==null||list.isEmpty())?null:list.get(0);
    }

    public int deleteLiveBarrageById(long bid) {
        TopicBarrage barrage = new TopicBarrage();
        barrage.setStatus(Specification.TopicFragmentStatus.DISABLED.index);
        barrage.setId(bid);

        return topicBarrageMapper.updateByPrimaryKeySelective(barrage);
    }

    public TopicBarrage getTopicBarrageByFId(long fid) {
        TopicBarrageExample example= new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria = example.createCriteria();
        criteria.andFidEqualTo(fid);
        criteria.andStatusEqualTo(Specification.TopicFragmentStatus.ENABLED.index);

        List<TopicBarrage> list = topicBarrageMapper.selectByExampleWithBLOBs(example);
        return list==null||list.isEmpty()?null:list.get(0);
    }

    public int updateTopFragmentById(SpeakDto speakDto) {
    	TopicFragmentWithBLOBs fragment = new TopicFragmentWithBLOBs();
        fragment.setId(speakDto.getFragmentId());
        fragment.setExtra(speakDto.getExtra());

        return topicFragmentMapper.updateByPrimaryKeySelective(fragment);
    }

    public int countFragmentByTopicId(long topicId) {
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
       return  topicFragmentMapper.countByExample(example);
    }
    
    public int countFragmentBeforeFid(long topicId, long fid){
    	TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdLessThanOrEqualTo(fid);
        return topicFragmentMapper.countByExample(example);
    }

    public List<TopicFragmentWithBLOBs> getTopicFragmentForPage(GetLiveDetailDto getLiveDetailDto) {
       /* TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(getLiveDetailDto.getTopicId());
        int pageNo = getLiveDetailDto.getPageNo();
        String order = "id asc limit "+((pageNo-1)*getLiveDetailDto.getOffset())+","+getLiveDetailDto.getOffset();
        example.setOrderByClause(order);
        return topicFragmentMapper.selectByExampleWithBLOBs(example);*/

        int startIndex = (getLiveDetailDto.getPageNo()-1)*getLiveDetailDto.getOffset();
        getLiveDetailDto.setStartIndex(startIndex);
        return topicFragmentMapper.getTopicFragmentForPage(getLiveDetailDto);
    }

    public Map<String,Long> countFragmentByTopicIdWithSince(GetLiveUpdateDto getLiveUpdateDto) {
        return topicFragmentMapper.countFragmentByTopicIdWithSince(getLiveUpdateDto);
    }

    public TopicFragmentWithBLOBs getTopicFragmentById(long id){
    	return topicFragmentMapper.selectByPrimaryKey(id);
    }

    public TopicUserConfig getTopicUserConfig(long uid ,long topicId){
        TopicUserConfigExample example = new TopicUserConfigExample();
        TopicUserConfigExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        List<TopicUserConfig> list = topicUserConfigMapper.selectByExample(example);
        return list != null && list.size() > 0?list.get(0):null;
    }

    public void updateTopicUserConfig(TopicUserConfig topicUserConfig) {
        topicUserConfigMapper.updateByPrimaryKeySelective(topicUserConfig);
    }

    public void insertTopicUserConfig(TopicUserConfig topicUserConfig) {
    	topicUserConfigMapper.insertSelective(topicUserConfig);
    }

    public void updateTopicAggregation(TopicAggregation topicAggregation) {
        topicAggregationMapper.updateByPrimaryKeySelective(topicAggregation);
    }

    public List<TopicAggregation> getTopicAggregationsByTopicId(long topicId){
    	TopicAggregationExample example = new TopicAggregationExample();
    	TopicAggregationExample.Criteria criteria = example.createCriteria();
    	criteria.andTopicIdEqualTo(topicId);
    	return topicAggregationMapper.selectByExample(example);
    }

    public List<TopicAggregation> getTopicAggregationsBySubTopicId(long subTopicId){
    	TopicAggregationExample example = new TopicAggregationExample();
    	TopicAggregationExample.Criteria criteria = example.createCriteria();
    	criteria.andSubTopicIdEqualTo(subTopicId);
    	return topicAggregationMapper.selectByExample(example);
    }

    public void updateTopicAggregationApply(TopicAggregationApply topicAggregationApply) {
        topicAggregationApplyMapper.updateByPrimaryKeySelective(topicAggregationApply);
    }

    public void createTopicAggApply(TopicAggregationApply topicAggregationApply) {
        topicAggregationApplyMapper.insertSelective(topicAggregationApply);
    }

    public void createTopicAgg(TopicAggregation topicAggregation) {
        topicAggregationMapper.insertSelective(topicAggregation);
    }

    public void deleteTopicAgg(long ceTopicId ,long acTopicId) {
        TopicAggregationExample example = new TopicAggregationExample();
        TopicAggregationExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(ceTopicId);
        criteria.andSubTopicIdEqualTo(acTopicId);
       topicAggregationMapper.deleteByExample(example);
    }

    public TopicAggregation getTopicAggregationByTopicIdAndSubId(long topicId ,long subId){
        TopicAggregationExample example = new TopicAggregationExample();
        TopicAggregationExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andSubTopicIdEqualTo(subId);
        List<TopicAggregation> list = topicAggregationMapper.selectByExample(example);
        return list.size() > 0 && list != null?list.get(0):null;
    }

    public List<TopicAggregation> getTopicAggregationByTopicIdAndIsTop(long topicId, int isTop){
        TopicAggregationExample example = new TopicAggregationExample();
        TopicAggregationExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIsTopEqualTo(isTop);

        return topicAggregationMapper.selectByExample(example);
    }

    public TopicAggregationApply getTopicAggregationApplyById(long applyId){
        TopicAggregationApplyExample example = new TopicAggregationApplyExample();
        TopicAggregationApplyExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(applyId);
        List<TopicAggregationApply> list = topicAggregationApplyMapper.selectByExample(example);
        return list.size() > 0 && list != null?list.get(0):null;
    }

    public List<TopicAggregationApply> getTopicAggregationApplyByTopicAndTargetAndResult(long ownerTopicId ,long targetTopicId ,int type, List<Integer> resultList){
        TopicAggregationApplyExample example = new TopicAggregationApplyExample();
        TopicAggregationApplyExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(ownerTopicId);
        criteria.andTargetTopicIdEqualTo(targetTopicId);
        criteria.andTypeEqualTo(type);
        if(null != resultList && resultList.size() > 0){
        	criteria.andResultIn(resultList);
        }
        return topicAggregationApplyMapper.selectByExample(example);
    }

    /**
     * 本方法慎用
     */
    public List<TopicAggregationApply> getTopicAggregationApplyBySourceIdsAndTargetIdsAndResults(List<Long> sourceIds, List<Long> targetIds, List<Integer> resultList){
    	TopicAggregationApplyExample example = new TopicAggregationApplyExample();
        TopicAggregationApplyExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdIn(sourceIds);
        criteria.andTargetTopicIdIn(targetIds);
        if(null != resultList && resultList.size() > 0){
        	criteria.andResultIn(resultList);
        }
        return topicAggregationApplyMapper.selectByExample(example);
    }
    /**
     * 随机获取一条运营指定的可串门王国。
     * @author zhangjiwei
     * @date Jul 27, 2017
     * @param map
     * @return
     */
    public TopicDroparound getRandomDropaRound(Map<String ,Object> map){
        TopicDroparound topicDroparound = topicDroparoundMapper.getRandomDropaRound(map);
        return topicDroparound;
    }

    public Topic getRandomDropaRoundAlgorithm(Map<String ,Object> map){
        Topic topic = topicMapper.getRandomDropaRoundAlgorithm(map);
        return topic;
    }
    public Topic getRandomTopicByTag(Map<String ,Object> map){
        Topic topic = topicMapper.getRandomTopicByTag(map);
        return topic;
    }
    public void createTopicDroparoundTrail(TopicDroparoundTrail trail){
        topicDroparoundTrailMapper.insertSelective(trail);
    }
	/**
	 * 获取串门轨迹语言模板列表。返回所有语言模板。
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param queryStr
	 * @return
	 */
	public List<TopicFragmentTemplate> getFragmentTplList(String queryStr) {
		TopicFragmentTemplateExample tf = new TopicFragmentTemplateExample();
		if(!StringUtils.isEmpty(queryStr)){
			tf.createCriteria().andContentLike("%"+queryStr+"%");
		}
		List<TopicFragmentTemplate> templates = topicFragmentTemplateMapper.selectByExample(tf);
		return templates;
	}
	/**
	 * 添加一个语言模板
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param obj
	 */
	public void addFragmentTpl(TopicFragmentTemplate obj) {
		topicFragmentTemplateMapper.insert(obj);
	}
	/**
	 * 根据ID取一个语言模板
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param id
	 * @return
	 */
	public TopicFragmentTemplate getFragmentTplById(Long id) {
		return topicFragmentTemplateMapper.selectByPrimaryKey(id);
	}
	/**
	 * 删除王国语言模板
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param msgId
	 */
	public void deleteFragmentTplById(Long msgId) {
		topicFragmentTemplateMapper.deleteByPrimaryKey(msgId);
	}
	/**
	 * 修改王国语言模板
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param obj
	 */
	public void updateFragmentTpl(TopicFragmentTemplate obj) {
		topicFragmentTemplateMapper.updateByPrimaryKey(obj);
	}
	/**
	 *  添加可串门的王国
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param tropicId
	 * @param sort
	 */
	public void addDropAroundKingdom(long tropicId, int sort) {
		TopicDroparound record =new TopicDroparound();
		record.setSort(sort);
		record.setTopicid(tropicId);
		topicDroparoundMapper.insert(record);
	}
	/**
	 * 删除可串门的王国
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param tropicId
	 */
	public void deleteDropAroundKingdom(long tropicId) {
		topicDroparoundMapper.deleteByPrimaryKey(tropicId);
	}
	/**
	 * 查询指定的王国ID是否在可串门的王国列表中。存在返回true,不存在返回false.
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param tropicId 指定的王国ID
	 */
	public boolean existsDropAroundKingdom(long tropicId){
		TopicDroparoundExample example = new TopicDroparoundExample();
		example.createCriteria().andTopicidEqualTo(tropicId);
		int count = topicDroparoundMapper.countByExample(example);
		return count==1;
	}
	/**
	 * 修改串门王国
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param td
	 */
	public void updateDropAroundKingdom(TopicDroparound td) {
		topicDroparoundMapper.updateByPrimaryKeySelective(td);
	}
	/**
	 * 查询王国分页
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param page
	 * @param searchKeyword
	 * @return
	 */
	public PageBean<Topic> getTopicPage(PageBean page, String searchKeyword) {
		TopicExample example = new TopicExample();
		if(!org.apache.commons.lang.StringUtils.isEmpty(searchKeyword)){
			example.createCriteria().andTitleLike("%"+searchKeyword+"%");
			if(searchKeyword.matches("^\\d+$")){
				TopicExample.Criteria ct2= example.createCriteria().andIdEqualTo(Long.parseLong(searchKeyword));
				example.or(ct2);
			}
		}
		int count =topicMapper.countByExample(example);
		int beginRow = (page.getCurrentPage()-1)*page.getPageSize();
		example.setOrderByClause(" id desc limit "+beginRow+","+page.getPageSize());
		List<Topic> topicList = topicMapper.selectByExample(example);
		page.setDataList(topicList);
		page.setTotalRecords(count);
		return page;
	}
	/**
	 * 取可串门王国分页
	 * @author zhangjiwei
	 * @date Mar 20, 2017
	 * @param page
	 * @return
	 */
	public PageBean<SearchDropAroundTopicDto> getDropAroundKingdomPage(PageBean page,String searchStr) {
		Map<String,Object> obj = new HashMap<>();
		obj.put("skip", (page.getCurrentPage()-1) * page.getPageSize());
		obj.put("limit", page.getPageSize());
		if(searchStr!=null){
			obj.put("searchStr", "%"+searchStr+"%");
		}
		List<SearchDropAroundTopicDto> topicList = topicDroparoundMapper.getDropAroundKingdomPage(obj);
		int count = topicDroparoundMapper.countDropAroundKingdomPage(obj);

		page.setDataList(topicList);
		page.setTotalRecords(count);
		return page;
	}

	public void truncateKingdomCountDay(){
		topicMapper.truncateKingdomCountDay();
	}

	public void statKingdomCountDay(){
		topicMapper.statKingdomCountDay();
	}

    public TopicFragmentTemplate getTopicFragmentTemplate(){
        List<TopicFragmentTemplate> list = topicFragmentTemplateMapper.selectByExample(null);
        if(null != list && list.size() > 1){
        	Collections.shuffle(list);
        }
        return list != null && list.size() > 0 ? list.get(0):null;
    }

    public TopicFragmentWithBLOBs getFragmentByAT(Map map){
        return topicFragmentMapper.getFragmentByAT(map);
    }

	public PageBean<SearchTopicDto> getTopicPage(PageBean page, Map<String, Object> params) {

		params.put("skip", (page.getCurrentPage()-1) * page.getPageSize());
		params.put("limit", page.getPageSize());
		long count = topicMapper.countTopicForPage(params);
		List<SearchTopicDto> topicList = topicMapper.getTopicPage(params);
		page.setDataList(topicList);
		page.setTotalRecords(count);
		return page;
	}

	public TopicTag getTopicTagByTag(String tag){
		TopicTagExample example = new TopicTagExample();
		TopicTagExample.Criteria criteria = example.createCriteria();
		criteria.andTagEqualTo(tag);
		List<TopicTag> list = topicTagMapper.selectByExample(example);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public TopicTag getRecTopicTagWithoutOwn(long topicId, List<Long> tagIdList, boolean isAdmin){
		TopicTagExample example = new TopicTagExample();
		TopicTagExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(0);//正常的
		criteria.andIsRecEqualTo(1);//推荐的
		if(null != tagIdList && tagIdList.size() > 0){
			criteria.andIdNotIn(tagIdList);
		}
		if(!isAdmin){//不是管理员推荐的标签中不能出现“官方”二字
			criteria.andTagNotLike("%官方%");
		}
		example.setOrderByClause(" id desc limit 1");
		List<TopicTag> list =  topicTagMapper.selectByExample(example);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	public void insertTopicTag(TopicTag tag){
		topicTagMapper.insertSelective(tag);
	}
	
	public void updateTopicTag(TopicTag tag){
		topicTagMapper.updateByPrimaryKeySelective(tag);
	}
	
	public TopicTag getTopicTagById(long id){
		return topicTagMapper.selectByPrimaryKey(id);
	}
	public List<TopicTag> getTopicTagByIds(List<Long> ids){
		TopicTagExample example = new TopicTagExample();
		example.createCriteria().andIdIn(ids);
		return topicTagMapper.selectByExample(example);
	}
	public void insertTopicTagDetail(TopicTagDetail tagDetail){
		topicTagDetailMapper.insertSelective(tagDetail);
	}
	
	public TopicTagDetail getTopicTagDetailById(long id){
		return topicTagDetailMapper.selectByPrimaryKey(id);
	}
	
	public void updateTopicTagDetail(TopicTagDetail tagDetail){
		topicTagDetailMapper.updateByPrimaryKeySelective(tagDetail);
	}

	public List<TopicTagDetail> getTopicTagDetailsByTopicId(long topicId){
        TopicTagDetailExample example = new TopicTagDetailExample();
        TopicTagDetailExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andStatusEqualTo(0);
        example.setOrderByClause(" create_time asc ");
        return topicTagDetailMapper.selectByExample(example);
    }
	/**
	 * 判断王国是否包含某一标签。
	 * @author zhangjiwei
	 * @date Jul 19, 2017
	 * @param topicId
	 * @param tag
	 * @return
	 */
	public boolean existsTopicTag(long topicId,String tag){
        TopicTagDetailExample example = new TopicTagDetailExample();
        TopicTagDetailExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andTagEqualTo(tag);
        criteria.andStatusEqualTo(0);
        return topicTagDetailMapper.countByExample(example)>0;
    }
	
	public List<TopicTagDetail> getTopicTagDetailsByTagId(long tagId){
		TopicTagDetailExample example = new TopicTagDetailExample();
        TopicTagDetailExample.Criteria criteria = example.createCriteria();
        criteria.andTagIdEqualTo(tagId);
        criteria.andStatusEqualTo(0);
        return topicTagDetailMapper.selectByExample(example);
	}
	
	public List<TopicTagDetail> getTopicTagDetailListByTopicIds(List<Long> topicIds){
		if(null == topicIds || topicIds.size() == 0){
			return null;
		}
		TopicTagDetailExample example = new TopicTagDetailExample();
        TopicTagDetailExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdIn(topicIds);
        criteria.andStatusEqualTo(0);
        example.setOrderByClause(" topic_id asc,id asc ");
        return topicTagDetailMapper.selectByExample(example);
	}
	
	public PageBean<TeaseInfo> getTeaseInfoPage(PageBean<TeaseInfo> page, Map<String, Object> conditions) {
		TeaseInfoExample example = new TeaseInfoExample();
		int count = teaseInfoMapper.countByExample(example);
		example.setOrderByClause("id desc limit "+((page.getCurrentPage()-1)*page.getPageSize())+","+page.getPageSize());
		example.createCriteria().andStatusEqualTo(1);
		List<TeaseInfo> packList=  teaseInfoMapper.selectByExample(example);
		page.setPageSize(count);
		page.setDataList(packList);
		return page;
	}
	public void updateTeaseInfoByKey(TeaseInfo teaseInfo) {
		teaseInfoMapper.updateByPrimaryKeySelective(teaseInfo);
	}
	public Integer addTeaseInfo(TeaseInfo teaseInfo) {
		return teaseInfoMapper.insertSelective(teaseInfo);
	}
	public TeaseInfo getTeaseInfoByKey(Long id) {
		return teaseInfoMapper.selectByPrimaryKey(id);
	}
	public List<TeaseInfo> teaseListQuery(){
		TeaseInfoExample example = new TeaseInfoExample();
		example.createCriteria().andStatusEqualTo(1);
		example.setOrderByClause("create_time asc");
        return teaseInfoMapper.selectByExample(example);
	}
	public Integer addVoteInfo(VoteInfo voteInfo) {
		return voteInfoMapper.insertSelective(voteInfo);
	}
	public Integer addVoteOption(VoteOption voteOption) {
		return voteOptionMapper.insertSelective(voteOption);
	}
	public Integer getVoteInfoCount(long uid){
		VoteInfoExample example = new VoteInfoExample();
		VoteInfoExample.Criteria criteria =  example.createCriteria();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startDate = sdf.format(new Date())+" 00:00:00";
		String endDate = sdf.format(new Date())+" 23:59:59";
		try {
			criteria.andCreateTimeBetween(sdf1.parse(startDate), sdf1.parse(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		criteria.andUidEqualTo(uid);
		return voteInfoMapper.countByExample(example);
	}
	public Integer addVoteRecord(VoteRecord voteRecord) {
		return voteRecordMapper.insertSelective(voteRecord);
	}
	
	public VoteInfo getVoteInfoByKey(Long id) {
		return voteInfoMapper.selectByPrimaryKey(id);
	}
	public int getVoteRecordCountByUidAndVoteId(long uid,long voteId){
		VoteRecordExample example = new VoteRecordExample();
		VoteRecordExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andVoteidEqualTo(voteId);
        return voteRecordMapper.countByExample(example);
	}
	public Integer updateVoteInfo(VoteInfo voteInfo) {
		return voteInfoMapper.updateByPrimaryKeySelective(voteInfo);
	}
	public Integer deleteFragmentByIdForPhysics(Long id) {
		return topicFragmentMapper.deleteByPrimaryKey(id);
	}
	
	public List<VoteOption> getVoteOptionList(Long voteId){
		VoteOptionExample example = new VoteOptionExample();
		VoteOptionExample.Criteria criteria = example.createCriteria();
		criteria.andVoteidEqualTo(voteId);
        return voteOptionMapper.selectByExample(example);
	}
	public int getVoteRecordCountByOptionId(long voteOptionId){
		VoteRecordExample example = new VoteRecordExample();
		VoteRecordExample.Criteria criteria = example.createCriteria();
		criteria.andOptionidEqualTo(voteOptionId);
        return voteRecordMapper.countByExample(example);
	}
	
	public List<VoteRecord> getMyVoteRecord(Long uid,Long voteId){
		VoteRecordExample example = new VoteRecordExample();
		VoteRecordExample.Criteria criteria = example.createCriteria();
		criteria.andVoteidEqualTo(voteId);
		criteria.andUidEqualTo(uid);
        return voteRecordMapper.selectByExample(example);
	}
	public int getVoteRecordCountByVoteId(long voteId){
		VoteRecordExample example = new VoteRecordExample();
		VoteRecordExample.Criteria criteria = example.createCriteria();
		criteria.andVoteidEqualTo(voteId);
        return voteRecordMapper.countByExample(example);
	}
	
	/**
	 * 屏蔽用户王国
	 * @author zhangjiwei
	 * @date May 9, 2017
	 * @param topicId
	 * @param uid
	 */
	public void blockUserKingdom(long topicId, long uid){
		BlockTopic bt = new BlockTopic();
		bt.setUid(uid);
		bt.setTopicId(topicId);
		blockTopicMapper.insert(bt);
	}
	/**
	 * 移除屏蔽的王国
	 * @author zhangjiwei
	 * @date May 9, 2017
	 * @param topicId
	 */
	public void removeBlockedKingodm(long topicId){
		BlockTopicExample example = new BlockTopicExample();
		example.createCriteria().andTopicIdEqualTo(topicId);
		blockTopicMapper.deleteByExample(example);
	}
	
	/**
	 * 获取用户情绪王国.
	 * @author chenxiang
	 * @date 2017-05-24
	 * @param uid 用户ID
	 */
	public Topic getEmotionTopic(long uid){
		TopicExample example = new TopicExample();
		TopicExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andSubTypeEqualTo(1);
	
		List<Topic> list = topicMapper.selectByExample(example);
		return list.size()>0?list.get(0):null;
	}
	
	/**
	 * 获取过去24小时跑马灯信息列表.
	 * @author chenxiang
	 * @date 2017-06-8
	 * @param date 当前时间
	 */
	public List<TopicNews> getTopicNewsList24h(Date  date){
		TopicNewsExample example = new TopicNewsExample();
		TopicNewsExample.Criteria criteria = example.createCriteria();
		criteria.andCreateTimeGreaterThan(date);
		List<TopicNews> list = topicNewsMapper.selectByExample(example);
		return list;
	}
	/**
	 * 王国转让历史查询
	 * @author chenxiang
	 * @date 2017-06-8
	 * @param date 当前时间
	 */
    public List<TopicTransferRecord> getKingdomTransferRecord(long topicId, long sinceId) {
    	TopicTransferRecordExample example = new TopicTransferRecordExample();
    	TopicTransferRecordExample.Criteria criteria = example.createCriteria();
    	if(sinceId>0){
        criteria.andIdLessThan(sinceId);
    	}
        criteria.andTopicIdEqualTo(topicId);
        example.setOrderByClause("id desc limit 10");
        return topicTransferRecordMapper.selectByExample(example);
    }

	/**
	 * 保存跑马灯信息记录
	 * @author chenxiang
	 * @date 2017-06-8
	 * @param 
	 */
	public Integer addTopicNews(TopicNews topicNews) {
		return topicNewsMapper.insertSelective(topicNews);
	}
	
	/**
	 * 保存王国转让信息
	 * @author chenxiang
	 * @date 2017-06-8
	 * @param 
	 */
	public Integer addTopicTransferRecord(TopicTransferRecord topicTransferRecord) {
		return topicTransferRecordMapper.insertSelective(topicTransferRecord);
	}
	
	public void saveTopicReadHis(TopicReadHis trh){
		topicReadHisMapper.insertSelective(trh);
	}

	public boolean isNewInTopic(long uid, long topicId){
		TopicReadHisExample example = new TopicReadHisExample();
		TopicReadHisExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andTopicIdEqualTo(topicId);
		int count = topicReadHisMapper.countByExample(example);
		if(count == 0){
			return true;
		}
		return false;
	}

	public void addStealLog(UserStealLog log) {
		stealLogMapper.insert(log);
	}
	/**
	 * 获取王国总数
	 * @author chenxiang
	 * @date 2017-06-15
	 * @param 
	 */
    public int getTopicCount() {
    	TopicExample example = new TopicExample();
    	TopicExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        return topicMapper.countByExample(example);
    }
	/**
	 * 获取比自己王国价值低的王国数
	 * @author chenxiang
	 * @date 2017-06-15
	 * @param 
	 */
    public int getLessPriceTopicCount(int price) {
    	TopicExample example = new TopicExample();
    	TopicExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        criteria.andPriceLessThan(price);
        return topicMapper.countByExample(example);
    }
    
	/**
	 * 查询王国最近10天价值
	 * @author chenxiang
	 * @date 2017-6-15
	 * @param day
	 * @return
	 */
	public List<TopicPriceHis> getLastTenDaysTopicPrice(long topicId) {
		TopicPriceHisExample example = new TopicPriceHisExample();
		TopicPriceHisExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        example.setOrderByClause("create_time desc limit 10");
		return topicPriceHisMapper.selectByExample(example);
	}
	

	/**
	 * 查询王国价值详情
	 * @author chenxiang
	 * @date 2017-6-15
	 * @param day
	 * @return
	 */
	public TopicData getTopicDataByTopicId(long topicId) {
		TopicDataExample example = new TopicDataExample();
		TopicDataExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
		List<TopicData>  list = topicDataMapper.selectByExample(example);
		return list.size()>0?list.get(0):null;
	}
	
	/**
	 * 获取王国价值总数
	 * @author chenxiang
	 * @date 2017-06-15
	 * @param 
	 */
    public int getTopicDataCount() {
    	TopicDataExample example = new TopicDataExample();
    	TopicDataExample.Criteria criteria = example.createCriteria();
        return topicDataMapper.countByExample(example);
    }
    
    public void saveTopicData(TopicData td){
    	topicDataMapper.insertSelective(td);
    }
    
    
	/**
	 * 获取增长比自己低的王国数
	 * @author chenxiang
	 * @date 2017-06-15
	 * @param 
	 */
    public int getLessPriceChangeTopicCount(int price) {
    	TopicDataExample example = new TopicDataExample();
    	TopicDataExample.Criteria criteria = example.createCriteria();
        criteria.andLastPriceIncrLessThan(price);
        return topicDataMapper.countByExample(example);
    }
	/**
	 * 获取所有补贴配置
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
    public List<TopicPriceSubsidyConfig> getTopicPriceSubsidyConfigList() {
    	TopicPriceSubsidyConfigExample example = new TopicPriceSubsidyConfigExample();
        return topicPriceSubsidyConfigMapper.selectByExample(example);
    }
    
	/**
	 * 获取补贴配置
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
    public TopicPriceSubsidyConfig getTopicPriceSubsidyConfigById(long id) {
    	TopicPriceSubsidyConfigExample example = new TopicPriceSubsidyConfigExample();
    	example.createCriteria().andIdEqualTo(id);
    	List<TopicPriceSubsidyConfig> list = topicPriceSubsidyConfigMapper.selectByExample(example);
    	return list.size()>0?list.get(0):null;
    }
	/**
	 * 添加补贴配置
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
	public void saveTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc){
		topicPriceSubsidyConfigMapper.insertSelective(tpsc);
	}
	/**
	 * 修改补贴配置
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
	public void editTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc){
		topicPriceSubsidyConfigMapper.updateByPrimaryKeySelective(tpsc);
	}
	/**
	 * 删除补贴配置
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
	public void delTopicPriceSubsidyConfig(long id){
		topicPriceSubsidyConfigMapper.deleteByPrimaryKey(id);
	}
	/**
	 * 修改上市王国信息
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
	public void updateTopicListed(TopicListed topicListed){
		topicListedMapper.updateByPrimaryKeySelective(topicListed);
	}
	/**
	 * 获取上市王国信息
	 * @author chenxiang
	 * @date 2017-06-22
	 * @param 
	 */
	public TopicListed getTopicListedById(long id){
		return topicListedMapper.selectByPrimaryKey(id);
	}
	/**
	 * 获取上市王国信息
	 * @author chenxiang
	 * @date 2017-07-3
	 * @param 
	 */
	public TopicListed getTopicListedByTopicId(long topicId){
		TopicListedExample example = new TopicListedExample();
    	example.createCriteria().andTopicIdEqualTo(topicId);
    	List<TopicListed> list = topicListedMapper.selectByExample(example);
		return list.size()>0?list.get(0):null;
	}
	/**
	 * 添加上市王国信息
	 * @author chenxiang
	 * @date 2017-07-3
	 * @param 
	 */
	public int addTopicListed(TopicListed topicListed){
		return topicListedMapper.insertSelective(topicListed);
	}
	/**
	 * 上市王国记录分页查询
	 * @author chenxiang
	 * @date 2017-7-8
	 * @param sinceId 
	 */
    public List<TopicListed> getTopicListedList(long sinceId) {
    	TopicListedExample example = new TopicListedExample();
    	TopicListedExample.Criteria criteria = example.createCriteria();
    	if(sinceId>0){
        criteria.andIdLessThan(sinceId);
    	}
    	List<Integer> list = new ArrayList<Integer>();
    	list.add(0);
    	list.add(1);
    	criteria.andStatusIn(list);
        example.setOrderByClause("id desc limit 10");
        return topicListedMapper.selectByExample(example);
    }
	/**
	 * 用户是否收购过王国
	 * @author chenxiang
	 * @date 2017-7-8
	 * @param sinceId 
	 */
    public boolean isBuyTopic(long uid) {
    	TopicListedExample example = new TopicListedExample();
    	TopicListedExample.Criteria criteria = example.createCriteria();
    	criteria.andBuyUidEqualTo(uid);
    	List<TopicListed> list = topicListedMapper.selectByExample(example);
        return list.size()>0;
    }
	/**
	 * 删除上市王国信息
	 * @author chenxiang
	 * @date 2017-07-3
	 * @param 
	 */
	public int delTopicListed(Long topicId){
		TopicListedExample example = new TopicListedExample();
    	TopicListedExample.Criteria criteria = example.createCriteria();
    	criteria.andTopicIdEqualTo(topicId);
		return topicListedMapper.deleteByExample(example);
	}
	
	/**
	 * 获取随机两条不重复的机器人
	 * @author chenxiang
	 * @date 2017-07-20
	 * @param 
	 */
	public List<RobotInfo> getRobotInfoRandom(List<Long> ids){
		RobotInfoExample example = new RobotInfoExample();
		RobotInfoExample.Criteria criteria = example.createCriteria();
		if(ids.size()>0){
			criteria.andUidNotIn(ids);
		}
		List<RobotInfo> list = robotInfoMapper.selectByExample(example);
		List<RobotInfo> result = new ArrayList<RobotInfo>();
		if(null != list && list.size() > 0){
			Random random = new Random();
			for(int i=0;i<2;i++){
				if(list.size() < 1){
					break;
				}
				int idx = random.nextInt(list.size());
				result.add(list.get(idx));
				list.remove(idx);
			}
		}
		return result;
	}
	/**
	 * 获取随机两条不重复的语录
	 * @author chenxiang
	 * @date 2017-07-20
	 * @param 
	 */
	public List<QuotationInfo> getQuotationInfoRandom(List<Long> ids){
		QuotationInfoExample example = new QuotationInfoExample();
		QuotationInfoExample.Criteria criteria = example.createCriteria();
		criteria.andTypeEqualTo(0);
		if(ids.size()>0){
			criteria.andIdNotIn(ids);
		}
		List<QuotationInfo> list = quotationInfoMapper.selectByExample(example);
		List<QuotationInfo> result = new ArrayList<QuotationInfo>();
		if(null != list && list.size() > 0){
			Random random = new Random();
			for(int i=0;i<2;i++){
				if(list.size() < 1){
					break;
				}
				int idx = random.nextInt(list.size());
				result.add(list.get(idx));
				list.remove(idx);
			}
		}
		return result;
	}
	
	public TopicGiven getGivenKingomdById(long givenKingdomId) {
		return givenMapper.selectByPrimaryKey((int)givenKingdomId);
	}
	public void deleteGivenKingdomById(long givenKingdomId){
		givenMapper.deleteByPrimaryKey((int)givenKingdomId);
	}
	/**
	 * 取用户的赠送王国。
	 * @author zhangjiwei
	 * @date Jul 20, 2017
	 * @param uid
	 * @return
	 */
	public List<TopicGiven> getMyGivenKingdoms(long uid) {
		TopicGivenExample example = new TopicGivenExample();
		example.createCriteria().andUidEqualTo((int)uid);
		return givenMapper.selectByExample(example);
	}
	/**
	 * 添加日签记录
	 * @author chenxiang
	 * @date 2017-07-20
	 * @param 
	 */
	public int addSignRecord(SignRecord signRecord){
		return signRecordMapper.insertSelective(signRecord);
	}
	/**
	 * 添加日签语录记录
	 * @author chenxiang
	 * @date 2017-07-20
	 * @param 
	 */
	public int addRobotQuotationRecord(RobotQuotationRecord robotQuotationRecord){
		return robotQuotationRecordMapper.insertSelective(robotQuotationRecord);
	}
	/**
	 * 添加日签保存分享记录
	 * @author chenxiang
	 * @date 2017-07-20
	 * @param 
	 */
	public int saveSignSaveRecord(SignSaveRecord SignSaveRecord){
		return signSaveRecordMapper.insertSelective(SignSaveRecord);
	}

	public RobotInfo getRobotInfo(){
	    return robotInfoMapper.getRandomRobotInfo();
    }


	public QuotationInfo getQuotationInfoByType(int type){
	    return  quotationInfoMapper.getQuotationInfo(type);
    }

    public List<QuotationInfo> getQuotationInfoByList(int limit){
        return  quotationInfoMapper.selectListQuotationInfo(limit);
    }

	public int getUnActivedKingdomCount(long uid) {
		TopicGivenExample example = new TopicGivenExample();
		example.createCriteria().andUidEqualTo((int)uid);
		return givenMapper.countByExample(example);
	}
	   
	public int saveQuotationInfo(QuotationInfo quotationInfo){
		return quotationInfoMapper.insertSelective(quotationInfo);
	}
	public int updateQuotationInfo(QuotationInfo quotationInfo){
		return quotationInfoMapper.updateByPrimaryKeySelective(quotationInfo);
	}
	public int delQuotationInfo(long id){
		return quotationInfoMapper.deleteByPrimaryKey(id);
	}
	public QuotationInfo getQuotationInfoById(long id){
		return quotationInfoMapper.selectByPrimaryKey(id);
	}
	
	public int countUserFragment(long topicId, long uid){
    	TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        return topicFragmentMapper.countByExample(example);
    }
	
	public Topic getUserSpecialTopicBySubType(long uid, int subType){
		TopicExample example = new TopicExample();
		TopicExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andSubTypeEqualTo(subType);
		List<Topic> list = topicMapper.selectByExample(example);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

    public int saveLotteryInfo(LotteryInfo lotteryInfo){
    	return lotteryInfoMapper.insertSelective(lotteryInfo);
    }
	public LotteryInfo getLotteryInfoById(long id){
		return lotteryInfoMapper.selectByPrimaryKey(id);
	}
	public int updateLotteryInfoById(LotteryInfo lotteryInfo){
		return lotteryInfoMapper.updateByPrimaryKeySelective(lotteryInfo);
	}
    public int countLotteryContent(long lotteryId, long uid){
    	LotteryContentExample example = new LotteryContentExample();
    	LotteryContentExample.Criteria criteria = example.createCriteria();
    	criteria.andLotteryIdEqualTo(lotteryId);
    	criteria.andUidEqualTo(uid);
        return lotteryContentMapper.countByExample(example);
    }
    public int countLotteryJoinUser(long lotteryId, List<Long> prohibitList){
    	LotteryContentExample example = new LotteryContentExample();
    	LotteryContentExample.Criteria criteria = example.createCriteria();
    	criteria.andLotteryIdEqualTo(lotteryId);
    	criteria.andUidNotIn(prohibitList);
        return lotteryContentMapper.countByExample(example);
    }
    public int lotteryIsWin(long lotteryId, long uid){
    	LotteryWinExample example = new LotteryWinExample();
    	LotteryWinExample.Criteria criteria = example.createCriteria();
    	criteria.andLotteryIdEqualTo(lotteryId);
    	criteria.andUidEqualTo(uid);
        return lotteryWinMapper.countByExample(example);
    }
    public int saveLotteryContent(LotteryContent lotteryContent){
    	return lotteryContentMapper.insertSelective(lotteryContent);
    }
    public int delLotteryContent(long id){
    	return lotteryContentMapper.deleteByPrimaryKey(id);
    }
    public LotteryContent getLotteryContentById(long id){
    	return lotteryContentMapper.selectByPrimaryKey(id);
    }
    public int saveLotteryProhibit(LotteryProhibit lotteryProhibit){
    	return lotteryProhibitMapper.insertSelective(lotteryProhibit);
    }
    public int countLotteryProhibit(long lotteryId, long uid){
    	LotteryProhibitExample example = new LotteryProhibitExample();
    	LotteryProhibitExample.Criteria criteria = example.createCriteria();
    	criteria.andLotteryIdEqualTo(lotteryId);
    	criteria.andUidEqualTo(uid);
        return lotteryProhibitMapper.countByExample(example);
    }

    public List<LotteryInfo> getLotteryListByTopicId(long topicId, long sinceId) {
    	LotteryInfoExample example = new LotteryInfoExample();
    	LotteryInfoExample.Criteria criteria = example.createCriteria();
    	if(sinceId>0){
        criteria.andIdLessThan(sinceId);
    	}
        criteria.andTopicIdEqualTo(topicId);
        criteria.andStatusNotEqualTo(-1);
        example.setOrderByClause("id desc limit 20");
        return lotteryInfoMapper.selectByExample(example);
    }
    public int delLotteryProhibit(long lotteryId, long uid) {
    	LotteryProhibitExample example = new LotteryProhibitExample();
    	LotteryProhibitExample.Criteria criteria = example.createCriteria();
    	criteria.andLotteryIdEqualTo(lotteryId);
    	criteria.andUidEqualTo(uid);
        return lotteryProhibitMapper.deleteByExample(example);
    }
    
    public int saveLotteryWin(LotteryWin lotteryWin){
    	return lotteryWinMapper.insertSelective(lotteryWin);
    }
    public int countLotteryByTopicId(long topicId){
    	LotteryInfoExample example = new LotteryInfoExample();
    	LotteryInfoExample.Criteria criteria = example.createCriteria();
    	criteria.andTopicIdEqualTo(topicId);
    	criteria.andStatusNotEqualTo(-1);
    	int count = lotteryInfoMapper.countByExample(example);
        return  count;
    }
    public void saveTopicPriceChangeLog(TopicPriceChangeLog log){
    	topicPriceChangeLogMapper.insertSelective(log);
    }
    
    public List<GiftInfo> getGiftInfoList() {
    	GiftInfoExample example = new GiftInfoExample();
    	GiftInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        example.setOrderByClause(" sort_number asc ");
        return giftInfoMapper.selectByExample(example);
    }

	public void deleteTopicTag(long topicId, String tag) {
		TopicTagDetailExample example = new TopicTagDetailExample();
		example.createCriteria().andTopicIdEqualTo(topicId).andTagEqualTo(tag);
		topicTagDetailMapper.deleteByExample(example);
	}
    public GiftInfo getGiftInfoById(long id){
    	return giftInfoMapper.selectByPrimaryKey(id);
    }
    public int saveGiftInfo(GiftInfo giftInfo){
    	return giftInfoMapper.insertSelective(giftInfo);
    }
    public int updateGiftInfo(GiftInfo giftInfo){
    	return giftInfoMapper.updateByPrimaryKeySelective(giftInfo);
    }
    public int saveGiftHistory(GiftHistory giftHistory){
    	return giftHistoryMapper.insertSelective(giftHistory);
    }
	/**
	 * 获取过去24小时送礼物列表
	 * @author chenxiang
	 * @date 2017-09-06
	 * @param date 24之前时间
	 */
	public List<GiftHistory> getGiftList24h(long topicId,Date date){
		GiftHistoryExample example = new GiftHistoryExample();
		GiftHistoryExample.Criteria criteria = example.createCriteria();
		criteria.andCreateTimeGreaterThan(date);
		criteria.andTopicIdEqualTo(topicId);
        example.setOrderByClause(" gift_price / gift_count desc ");
		List<GiftHistory> list = giftHistoryMapper.selectByExample(example);
		return list;
	}
	
	public TopicCategory getTopicCategoryById(int id){
		return topicCategoryMapper.selectByPrimaryKey(id);
	}
	
	public List<TopicCategory> getAllTopicCategory(){
		TopicCategoryExample example = new TopicCategoryExample();
		return topicCategoryMapper.selectByExample(example);
	}
	
	public List<LotteryAppoint> getAppointUser(long lotteryId){
		LotteryAppointExample example = new LotteryAppointExample();
		LotteryAppointExample.Criteria criteria = example.createCriteria();
		criteria.andLotteryIdEqualTo(lotteryId);
		example.setOrderByClause(" id asc ");
		return lotteryAppointMapper.selectByExample(example);
	}
}
