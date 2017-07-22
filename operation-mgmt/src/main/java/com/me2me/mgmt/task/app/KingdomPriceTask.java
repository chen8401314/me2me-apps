package com.me2me.mgmt.task.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicListed;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.user.service.UserService;

@Component
public class KingdomPriceTask {

	private static final Logger logger = LoggerFactory.getLogger(KingdomPriceTask.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private LocalJdbcDao localJdbcDao;
	@Autowired
	private LiveService liveService;
	
	private static List<String> weightKeyList = new ArrayList<String>();
	
	@PostConstruct
	public void init(){
		weightKeyList.add("ALGORITHM_DILIGENTLY_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_TEXTWORDCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_TEXTCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_VEDIOLENGHT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_VEDIOCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_AUDIOLENGHT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_AUDIOCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_IMAGECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_VOTECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_TEASECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_UPDATE_FREQUENCY_WEIGHT");
		weightKeyList.add("ALGORITHM_APPROVE_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_TEXTCOUNT_INAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_TEXTCOUNT_OUTAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_TEXTWORDCOUNT_INAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_TEXTWORDCOUNT_OUTAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_READCOUNT_INAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_READCOUNT_OUTAPP_WEIGHT");
		weightKeyList.add("ALGORITHM_SUBSCRIBECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_TEASECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEW_VOTECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_SHARECOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_REVIEWDAYCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_READDAYCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_V_WEIGHT");
		weightKeyList.add("ALGORITHM_ABILITYVALUE_WEIGHT");
		weightKeyList.add("ALGORITHM_DECAY_BASE_WEIGHT");
		weightKeyList.add("ALGORITHM_DECAY_BASEDAYCOUNT_WEIGHT");
		weightKeyList.add("ALGORITHM_STEAL_WEIGHT_R0");
		weightKeyList.add("ALGORITHM_STEAL_WEIGHT_R1");
		weightKeyList.add("LISTED_PRICE");
		weightKeyList.add("ALGORITHM_PUSH_PRICE_LIMIT");
		weightKeyList.add("ALGORITHM_PUSH_PRICE_INCR_LIMIT");
		weightKeyList.add("ALGORITHM_PUSH_PRICE_REDUCE_LIMIT");
	}
	
	@Scheduled(cron="0 2 0 * * ?")
	public void doTask(){
		logger.info("王国价值任务开始");
		long s = System.currentTimeMillis();
		try{
			this.executeIncr(null);
		}catch(Exception e){
			logger.error("王国价值任务出错", e);
		}
		long e = System.currentTimeMillis();
		logger.info("王国价值任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	/**
	 * 增量方式计算
	 * 每天只计算昨天的增量
	 */
	public void executeIncr(String dateStr) throws Exception{
		logger.info("增量计算王国价值开始");
		//获取各种权重配置
		Map<String, String> weightConfigMap = userService.getAppConfigsByKeys(weightKeyList);
		//获取任务需要的当前的各种系数配置
		double diligentlyWeight = this.getDoubleConfig("ALGORITHM_DILIGENTLY_WEIGHT", weightConfigMap, 1);//用心度权重
		double updateTextWordCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTWORDCOUNT_WEIGHT", weightConfigMap, 1);//更新文字字数权重
		double updateTextCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTCOUNT_WEIGHT", weightConfigMap, 0);//更新文字条数权重
		double updateVedioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOLENGHT_WEIGHT", weightConfigMap, 0);//更新视频长度权重
		double updateVedioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOCOUNT_WEIGHT", weightConfigMap, 40);//更新视频条数权重
		double updateAudioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOLENGHT_WEIGHT", weightConfigMap, 0);//更新语音长度权重
		double updateAudioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOCOUNT_WEIGHT", weightConfigMap, 30);//更新语音条数权重
		double updateImageCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_IMAGECOUNT_WEIGHT", weightConfigMap, 20);//更新图片权重
		double updateVoteCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VOTECOUNT_WEIGHT", weightConfigMap, 1);//投票权重
		double updateTeaseCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEASECOUNT_WEIGHT", weightConfigMap, 10);//更新逗一逗权重
		double updateFrequencyWeight = this.getDoubleConfig("ALGORITHM_UPDATE_FREQUENCY_WEIGHT", weightConfigMap, 1);//频度权重
		
		double approveWeight = this.getDoubleConfig("ALGORITHM_APPROVE_WEIGHT", weightConfigMap, 1);//认可度权重
		double reviewTextCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_INAPP_WEIGHT", weightConfigMap, 0);//app内评论条数权重
		double reviewTextCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_OUTAPP_WEIGHT", weightConfigMap, 0);//app外评论条数权重
		double reviewTextWordCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_INAPP_WEIGHT", weightConfigMap, 25);//app内评论字数条数权重
		double reviewTextWordCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_OUTAPP_WEIGHT", weightConfigMap, 25);//app外评论字数条数权重
		double readCountInAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_INAPP_WEIGHT", weightConfigMap, 15);//app内阅读权重
		double readCountOutAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_OUTAPP_WEIGHT", weightConfigMap, 15);//app外阅读权重
		double subscribeCountWeight = this.getDoubleConfig("ALGORITHM_SUBSCRIBECOUNT_WEIGHT", weightConfigMap, 0);//订阅数权重
		double reviewTeaseCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEASECOUNT_WEIGHT", weightConfigMap, 20);//评论逗一逗权重
		double reviewVoteCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_VOTECOUNT_WEIGHT", weightConfigMap, 1);//参与投票权重
		double shareCountWeight = this.getDoubleConfig("ALGORITHM_SHARECOUNT_WEIGHT", weightConfigMap, 1);//分享次数权重
		double reviewDayCountWeight = this.getDoubleConfig("ALGORITHM_REVIEWDAYCOUNT_WEIGHT", weightConfigMap, 0);//产生品论的天数权重
		double readDayCountWeight = this.getDoubleConfig("ALGORITHM_READDAYCOUNT_WEIGHT", weightConfigMap, 1);//产生阅读的天数权重
		
		double vWeight = this.getDoubleConfig("ALGORITHM_V_WEIGHT", weightConfigMap, 0.2);//大V系数权重
		
		double decayBaseWeight = this.getDoubleConfig("ALGORITHM_DECAY_BASE_WEIGHT", weightConfigMap, 1);//衰减基数权重
		double decayBaseDayCountWeight = this.getDoubleConfig("ALGORITHM_DECAY_BASEDAYCOUNT_WEIGHT", weightConfigMap, 1);//衰减标准天数权重
		double stealWeightR0 = this.getDoubleConfig("ALGORITHM_STEAL_WEIGHT_R0", weightConfigMap, 0.05);//被偷配置R0
		double stealWeightR1 = this.getDoubleConfig("ALGORITHM_STEAL_WEIGHT_R1", weightConfigMap, 0.3);//被偷配置R1
		double coreReadCountWeight = this.getDoubleConfig("ALGORITHM_CORE_READ_COUNT_WEIGHT", weightConfigMap, 0);//核心圈阅读量所需占比
		
		int listedPrice = this.getIntegerConfig("LISTED_PRICE", weightConfigMap, 20000);
		
		int pushPriceLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_LIMIT", weightConfigMap, 200);//推送王国价值阈值
		int pushPriceIncrLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_INCR_LIMIT", weightConfigMap, 50);//推送王国价值增长阈值
		int pushPriceReduceLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_INCR_LIMIT", weightConfigMap, 50);//推送王国价值减少阈值
		
		//获取补助配置
		StringBuilder subsidyConfigSql = new StringBuilder();
		subsidyConfigSql.append("select * from topic_price_subsidy_config t order by t.m1 asc");
		List<Map<String, Object>> subsidyConfigList = localJdbcDao.queryEvery(subsidyConfigSql.toString());
		
		String nowDateStr = null;
		Date now = new Date();
		Date yesterday = null;
		if(StringUtils.isNotBlank(dateStr)){
			Date c = DateUtil.string2date(dateStr, "yyyy-MM-dd");
			yesterday = DateUtil.addDay(c, -1);
			nowDateStr = DateUtil.date2string(c, "yyyyMMdd");
		}else{
			yesterday = DateUtil.addDay(now, -1);
			nowDateStr = DateUtil.date2string(now, "yyyyMMdd");
		}
		
		String startTime = DateUtil.date2string(yesterday, "yyyy-MM-dd") + " 00:00:00";
		String endTime = DateUtil.date2string(yesterday, "yyyy-MM-dd") + " 23:59:59";
		
		String topicSql = "select t.id,t.create_time,t.uid from topic t where t.create_time<='"+endTime+"' order by t.id limit ";
		
//		String topicSql = "select t.id,t.create_time,t.uid from topic t,(select f.topic_id,";
//		topicSql = topicSql + "MAX(if(f.type in (0,3,11,12,13,15,52,55),f.create_time, NULL)) as lastUpdateTime";
//		topicSql = topicSql + " from topic_fragment f where f.status=1 group by f.topic_id) m";
//		topicSql = topicSql + " where t.id=m.topic_id and m.lastUpdateTime>'2017-05-15 00:00:00'";
//		topicSql = topicSql + " and t.create_time<'" + endTime + "' order by t.id limit ";
		
		logger.info("开始处理王国价值");
		int start = 0;
		int pageSize = 500;
		int totalCount = 0;
		List<Map<String, Object>> topicList = null;
		StringBuilder topicFragmentSql = null;
		List<Map<String, Object>> fragmentList = null;
		Map<String, KingdomCount> kingCountMap = null;
		KingdomCount kc = null;
		StringBuilder topicDayCountSql = null;
		List<Map<String, Object>> topicDayCountList = null;
		StringBuilder favouriteSql = null;
		List<Map<String, Object>> favouriteCountList = null;
		StringBuilder readCountSql = null;
		List<Map<String, Object>> readCountList = null;
		StringBuilder voteCountSql = null;
		List<Map<String, Object>> voteCountList = null;
		List<Long> uidList = null;
		StringBuilder userProfileSql = null;
		List<Map<String, Object>> userProfileList = null;
		Map<String, Integer> vlvMap = null;
		StringBuilder shareSql = null;
		List<Map<String, Object>> shareList = null;
		StringBuilder topicDataSql = null;
		List<Map<String, Object>> topicDataList = null;
		Map<String, Map<String, Object>> topicDataMap = null;
		Map<String, Object> topicData = null;
		Map<String, Object> subsidyConfig = null;
		while(true){
			topicList = localJdbcDao.queryEvery(topicSql+start+","+pageSize);
			if(null == topicList || topicList.size() == 0){
				break;
			}
			start = start + pageSize;
			
			kingCountMap = new HashMap<String, KingdomCount>();
			
			uidList = new ArrayList<Long>();
			for(Map<String, Object> t : topicList){
				long topicId = ((Long)t.get("id")).longValue();
				kc = new KingdomCount();
				kc.setTopicId(topicId);
				kc.setCreateTime((Date)t.get("create_time"));
				kingCountMap.put(String.valueOf(topicId), kc);
				Long uid = (Long)t.get("uid");
				kc.setUid(uid);
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			
			//处理fragment相关数据(增量)
			topicFragmentSql = new StringBuilder();
			topicFragmentSql.append("select * from topic_fragment f");
			topicFragmentSql.append(" where f.status=1 and f.create_time>='").append(startTime);
			topicFragmentSql.append("' and f.create_time<='").append(endTime).append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicFragmentSql.append(",");
				}
				topicFragmentSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicFragmentSql.append(")");
			fragmentList = localJdbcDao.queryEvery(topicFragmentSql.toString());
			if(null != fragmentList && fragmentList.size() > 0){
				for(Map<String, Object> f : fragmentList){
					long topicId = ((Long)f.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					this.genFragmentKingdomCount(kc, f);
				}
			}
			//处理几个连续数据
			topicDayCountSql = new StringBuilder();
			topicDayCountSql.append("select f.topic_id,");
			topicDayCountSql.append("count(DISTINCT if(f.type in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as updateDayCount,");
			topicDayCountSql.append("count(DISTINCT if(f.type not in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as reviewDayCount,");
			topicDayCountSql.append("MAX(if(f.type in (0,3,11,12,13,15,52,55),f.create_time, NULL)) as lastUpdateTime");
			topicDayCountSql.append(" from topic_fragment f where f.status=1");
			topicDayCountSql.append(" and f.create_time<='").append(endTime);
			topicDayCountSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDayCountSql.append(",");
				}
				topicDayCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDayCountSql.append(") group by f.topic_id");
			topicDayCountList = localJdbcDao.queryEvery(topicDayCountSql.toString());
			if(null != topicDayCountList && topicDayCountList.size() > 0){
				for(Map<String, Object> c : topicDayCountList){
					long topicId = ((Long)c.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setUpdateDayCount(((Long)c.get("updateDayCount")).intValue());
					if(kc.getUpdateDayCount() == 0){
						kc.setUpdateDayCount(1);
					}
					kc.setReviewDayCount(((Long)c.get("reviewDayCount")).intValue());
					if(null != c.get("lastUpdateTime") && StringUtils.isNotBlank((String)c.get("lastUpdateTime"))){
						kc.setLastUpdateTime(DateUtil.string2date((String)c.get("lastUpdateTime"), "yyyy-MM-dd HH:mm:ss"));
					}else{
						kc.setLastUpdateTime(kc.getCreateTime());
					}
					long dayCount = DateUtil.getDaysBetween2Date(kc.getCreateTime(), kc.getLastUpdateTime()) + 1;
					kc.setReadDayCount((int)dayCount);//老数据处理，产生阅读的天数，即为从创建到最后一次更新的天数
					kc.setUpdateFrequency((double)kc.getUpdateDayCount()/(double)dayCount);
					long noUpdateDayCount = DateUtil.getDaysBetween2Date(kc.getLastUpdateTime(), yesterday);
					kc.setNoUpdateDayCount((int)noUpdateDayCount);
				}
			}
			
			//订阅数(增量)
			favouriteSql = new StringBuilder();
			favouriteSql.append("select f.topic_id, count(DISTINCT f.uid) as fcount");
			favouriteSql.append(" from live_favorite f where f.create_time>='").append(startTime);
			favouriteSql.append("' and f.create_time<='").append(endTime);
			favouriteSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					favouriteSql.append(",");
				}
				favouriteSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			favouriteSql.append(") group by f.topic_id");
			favouriteCountList = localJdbcDao.queryEvery(favouriteSql.toString());
			if(null != favouriteCountList && favouriteCountList.size() > 0){
				for(Map<String, Object> f : favouriteCountList){
					long topicId = ((Long)f.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setSubscribeCount(((Long)f.get("fcount")).intValue());
				}
			}
			
//			readCountSql = new StringBuilder();
//			readCountSql.append("select c.forward_cid,c.read_count,c.read_count_dummy");
//			readCountSql.append(" from content c where c.type=3 and c.forward_cid in (");
//			for(int i=0;i<topicList.size();i++){
//				if(i>0){
//					readCountSql.append(",");
//				}
//				readCountSql.append(String.valueOf(topicList.get(i).get("id")));
//			}
//			readCountSql.append(")");
//			readCountList = localJdbcDao.queryEvery(readCountSql.toString());
//			if(null != readCountList && readCountList.size() > 0){
//				for(Map<String, Object> r : readCountList){
//					long topicId = ((Long)r.get("forward_cid")).longValue();
//					kc = kingCountMap.get(String.valueOf(topicId));
//					int readCount = (Integer)r.get("read_count");
//					int readCountDummy = (Integer)r.get("read_count_dummy");
//					int totalDayNum = (int)DateUtil.getDaysBetween2Date(kc.getCreateTime(), now);
//					kc.setReadCountInApp(readCount/totalDayNum);
//					kc.setReadCountDummyInApp(readCountDummy/totalDayNum);
//				}
//			}
			
			//阅读数(增量)
			readCountSql = new StringBuilder();
			readCountSql.append("select t.topic_id,SUM(if(t.in_app=1,t.read_count,NULL)) as readInApp,");
			readCountSql.append("SUM(if(t.in_app=1 and not FIND_IN_SET(t.uid,SUBSTR(p.core_circle FROM 2 FOR LENGTH(p.core_circle)-2)),t.read_count,NULL)) as readInAppNotCore,");
			readCountSql.append("SUM(if(t.in_app=1,t.read_count_dummy,NULL)) as readDummyInApp,");
			readCountSql.append("SUM(if(t.in_app=1 and not FIND_IN_SET(t.uid,SUBSTR(p.core_circle FROM 2 FOR LENGTH(p.core_circle)-2)),t.read_count_dummy,NULL)) as readDummyInAppNotCore,");
			readCountSql.append("SUM(if(t.in_app=0,t.read_count,NULL)) as readOutApp,");
			readCountSql.append("SUM(if(t.in_app=0 and not FIND_IN_SET(t.uid,SUBSTR(p.core_circle FROM 2 FOR LENGTH(p.core_circle)-2)),t.read_count,NULL)) as readOutAppNotCore,");
			readCountSql.append("SUM(if(t.in_app=0,t.read_count_dummy,NULL)) as readDummyOutApp,");
			readCountSql.append("SUM(if(t.in_app=0 and not FIND_IN_SET(t.uid,SUBSTR(p.core_circle FROM 2 FOR LENGTH(p.core_circle)-2)),t.read_count_dummy,NULL)) as readDummyOutAppNotCore");
			readCountSql.append(" from topic_read_his t,topic p where t.topic_id=p.id and p.uid!=t.uid");
			readCountSql.append(" and t.create_time>='").append(startTime);
			readCountSql.append("' and t.create_time<='").append(endTime);
			readCountSql.append("' and t.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					readCountSql.append(",");
				}
				readCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			readCountSql.append(") group by t.topic_id");
			readCountList = localJdbcDao.queryEvery(readCountSql.toString());
			if(null != readCountList && readCountList.size() > 0){
				for(Map<String, Object> r : readCountList){
					long topicId = ((Long)r.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					if(null != r.get("readInApp")){
						int total = ((BigDecimal)r.get("readInApp")).intValue();
						int notCore = 0;
						if(null != r.get("readInAppNotCore")){
							notCore = ((BigDecimal)r.get("readInAppNotCore")).intValue();
						}
						kc.setReadCountInApp(notCore + (int)(total*coreReadCountWeight));
					}
					if(null != r.get("readDummyInApp")){
						int total = ((BigDecimal)r.get("readDummyInApp")).intValue();
						int notCore = 0;
						if(null != r.get("readDummyInAppNotCore")){
							notCore = ((BigDecimal)r.get("readDummyInAppNotCore")).intValue();
						}
						kc.setReadCountDummyInApp(notCore + (int)(total*coreReadCountWeight));
					}
					if(null != r.get("readOutApp")){
						int total = ((BigDecimal)r.get("readOutApp")).intValue();
						int notCore = 0;
						if(null != r.get("readOutAppNotCore")){
							notCore = ((BigDecimal)r.get("readOutAppNotCore")).intValue();
						}
						kc.setReadCountOutApp(notCore + (int)(total*coreReadCountWeight));
					}
					if(null != r.get("readDummyOutApp")){
						int total = ((BigDecimal)r.get("readDummyOutApp")).intValue();
						int notCore = 0;
						if(null != r.get("readDummyOutAppNotCore")){
							notCore = ((BigDecimal)r.get("readDummyOutAppNotCore")).intValue();
						}
						kc.setReadCountDummyOutApp(notCore + (int)(total*coreReadCountWeight));
					}
				}
			}
			
			//分享次数(增量)
			shareSql = new StringBuilder();
			shareSql.append("select t.cid,count(1) as cc from content_share_history t");
			shareSql.append(" where t.create_time>='").append(startTime);
			shareSql.append("' and t.create_time<='").append(endTime);
			shareSql.append("' and t.type=1 and t.cid in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					shareSql.append(",");
				}
				shareSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			shareSql.append(") group by t.cid");
			shareList = localJdbcDao.queryEvery(shareSql.toString());
			if(null != shareList && shareList.size() > 0){
				for(Map<String, Object> s : shareList){
					long topicId = ((Long)s.get("cid")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setShareCount(((Long)s.get("cc")).intValue());
				}
			}
			
			//参与投票次数(增量)
			voteCountSql = new StringBuilder();
			voteCountSql.append("select i.topicId,count(1) as vcount");
			voteCountSql.append(" from vote_info i,vote_record r");
			voteCountSql.append(" where i.id=r.voteId and i.topicId in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					voteCountSql.append(",");
				}
				voteCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			voteCountSql.append(") and r.create_time<='").append(endTime);
			voteCountSql.append("' and r.create_time>='").append(startTime);
			voteCountSql.append("' group by i.topicId");
			voteCountList = localJdbcDao.queryEvery(voteCountSql.toString());
			if(null != voteCountList && voteCountList.size() > 0){
				for(Map<String, Object> v : voteCountList){
					long topicId = ((Long)v.get("topicId")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setReviewVoteCount(((Long)v.get("vcount")).intValue());
				}
			}
			
			//处理大V
			vlvMap = new HashMap<String, Integer>();
			userProfileSql = new StringBuilder();
			userProfileSql.append("select u.uid,u.v_lv from user_profile u where uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					userProfileSql.append(",");
				}
				userProfileSql.append(uidList.get(i));
			}
			userProfileSql.append(")");
			userProfileList = localJdbcDao.queryEvery(userProfileSql.toString());
			if(null != userProfileList && userProfileList.size() > 0){
				for(Map<String, Object> u : userProfileList){
					vlvMap.put(String.valueOf(u.get("uid")), (Integer)u.get("v_lv"));
				}
			}
			
			//获取所有的topic data
			topicDataSql = new StringBuilder();
			topicDataSql.append("select t.*,p.price from topic_data t,topic p where t.topic_id=p.id and t.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDataSql.append(",");
				}
				topicDataSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDataSql.append(")");
			topicDataList = localJdbcDao.queryEvery(topicDataSql.toString());
			topicDataMap = new HashMap<String, Map<String, Object>>();
			if(null != topicDataList && topicDataList.size() > 0){
				for(Map<String, Object> t : topicDataList){
					long topicId = ((Long)t.get("topic_id")).longValue();
					topicDataMap.put(String.valueOf(topicId), t);
				}
			}
			
			
			//开始计算
			for(Map.Entry<String, KingdomCount> entry : kingCountMap.entrySet()){
				kc = entry.getValue();
				//几个时间处理下
				if(null == kc.getLastUpdateTime()){
					kc.setLastUpdateTime(kc.getCreateTime());
					kc.setReadDayCount(1);
					kc.setUpdateDayCount(1);
					kc.setUpdateFrequency(1);
					long noUpdateDayCount = DateUtil.getDaysBetween2Date(kc.getLastUpdateTime(), yesterday);
					kc.setNoUpdateDayCount((int)noUpdateDayCount);
				}
				if(null != vlvMap.get(String.valueOf(kc.getUid())) && vlvMap.get(String.valueOf(kc.getUid())).intValue() == 1){
					kc.setVlv(true);
				}
				double _x = (kc.getUpdateTextWordCount()*updateTextWordCountWeight + kc.getUpdateTextCount()*updateTextCountWeight
						+ kc.getUpdateVedioCount()*updateVedioCountWeight + kc.getUpdateVedioLenght()*updateVedioLenghtWeight
						+ kc.getUpdateAudioCount()*updateAudioCountWeight + kc.getUpdateAudioLenght()*updateAudioLenghtWeight
						+ kc.getUpdateImageCount()*updateImageCountWeight + kc.getUpdateVoteCount()*updateVoteCountWeight
						+ kc.getUpdateTeaseCount()*updateTeaseCountWeight)*kc.getUpdateFrequency()*updateFrequencyWeight;
				if(kc.isVlv()){
					_x = _x * (1 + vWeight);
				}
				
				double _y = (kc.getReviewTextCountInApp()*reviewTextCountInAppWeight + kc.getReviewTextCountOutApp()*reviewTextCountOutAppWeight
						+ kc.getReviewTextWordCountInApp()*reviewTextWordCountInAppWeight + kc.getReviewTextWordCountOutApp()*reviewTextWordCountOutAppWeight
						+ kc.getReadCountInApp()*readCountInAppWeight + kc.getReadCountOutApp()*readCountOutAppWeight
						+ kc.getSubscribeCount()*subscribeCountWeight + kc.getReviewTeaseCount()*reviewTeaseCountWeight
						+ kc.getReviewVoteCount()*reviewVoteCountWeight + kc.getShareCount()*shareCountWeight)*(kc.getReviewDayCount()*reviewDayCountWeight
						+ kc.getReadDayCount()*readDayCountWeight)/kc.getUpdateDayCount();

				topicData = topicDataMap.get(String.valueOf(kc.getTopicId()));
				int xx = (int)(_x*1000);
				int yy = (int)(_y*1000);
				boolean isCreateTime = DateUtil.isSameDay(kc.getCreateTime(), yesterday);
				if(null == topicData || isCreateTime){//当天新增的王国
					kc.setApprove(new BigDecimal((double)yy/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					kc.setDiligently(new BigDecimal((double)xx/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					
					double kv = Math.pow(Math.pow(_x, 2)+Math.pow(_y, 2),0.5);
					
					//特别补助
					subsidyConfig = this.getSubsidyConfig((int)kv, subsidyConfigList);
					if(null != subsidyConfig && subsidyConfig.size() == 3){
						double k1 = (Double)subsidyConfig.get("k1");
						double k2 = (Double)subsidyConfig.get("k2");
						double k3 = (Double)subsidyConfig.get("k3");
						int m2 = (Integer)subsidyConfig.get("m2");
						int subsidy = (int)(_x*(k1+Math.pow(_y/_x, k2)) + k3);
						kv = kv + subsidy;
						if(kv > m2){
							kv = m2;
						}
					}
					
					if(kv > 0){
						int totalDecay = (int)(kv*stealWeightR0);
						int delPrice = 0;
						if(totalDecay > 0){
							delPrice = (int)(totalDecay * stealWeightR1);
						}
						int stealPrice = totalDecay-delPrice;
						if(stealPrice < 10){
							stealPrice = 10;
						}
						kc.setStealPrice(stealPrice);
						kv = kv - delPrice;
					}else{
						kc.setStealPrice(0);
					}
					kc.setPrice((int)kv);
					
					if(null == topicData){
						this.saveKingdomCount(kc, true, listedPrice, yesterday, pushPriceLimit, pushPriceIncrLimit, pushPriceReduceLimit, nowDateStr);
					}else{
						this.saveKingdomCount(kc, false, listedPrice, yesterday, pushPriceLimit, pushPriceIncrLimit, pushPriceReduceLimit, nowDateStr);
					}
				}else{//历史有的，则需要做增量计算
					int oldStealPrice = (Integer)topicData.get("steal_price");
					int kv0 = ((Integer)topicData.get("price")).intValue();
					double x0 = ((Double)topicData.get("diligently")).doubleValue();
					double y0 = ((Double)topicData.get("approve")).doubleValue();
					int _kv = 0;
					if(kv0 == 0){
						_kv = (int)Math.pow(Math.pow(_x, 2)+Math.pow(_y, 2),0.5);
					}else{
						_kv = (int)((Math.pow(Math.min(1, _x/x0), diligentlyWeight) + Math.pow(Math.min(1, _y/y0), approveWeight))*kv0/2);
					}
					
					int d = 0;
					if(kc.getNoUpdateDayCount() > 0){
						d = (int)((kv0/decayBaseDayCountWeight)*Math.pow(decayBaseWeight, kc.getNoUpdateDayCount()));
						if(d < 2){
							d = 2;
						}
					}
					
					int kv = kv0 + _kv;
					if(_kv > 0){
						subsidyConfig = this.getSubsidyConfig(kv, subsidyConfigList);
						if(null != subsidyConfig && subsidyConfig.size() == 3){
							double k1 = (Double)subsidyConfig.get("k1");
							double k2 = (Double)subsidyConfig.get("k2");
							double k3 = (Double)subsidyConfig.get("k3");
							int m2 = (Integer)subsidyConfig.get("m2");
							int subsidy = (int)(_x*(k1+Math.pow(_y/_x, k2)) + k3);
							kv = kv + subsidy;
							if(kv > m2){
								kv = m2;
							}
						}
					}
					
					if(kv > 0){
						int totalDecay = (int)(_kv*stealWeightR0 + d);
						int delPrice = 0;
						if(totalDecay > 0){
							delPrice = (int)(totalDecay * stealWeightR1);
						}
						int stealPrice = totalDecay-delPrice;
						if(stealPrice < 10){
							stealPrice = 10;
						}
						kc.setStealPrice(stealPrice+oldStealPrice);
						kv = kv - delPrice;
					}else{
						kc.setStealPrice(0);
					}
					
					//有增加，但是王国价值没变，则数字不足+1
					if(_kv > 0 && kv == kv0){
						kv = kv + 1;
					}
					
					kc.setPrice(kv);
					kc.setDiligently(x0+new BigDecimal((double)xx/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					kc.setApprove(y0+new BigDecimal((double)yy/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					kc.setUpdateTextWordCount(((Integer)topicData.get("update_text_length")).intValue() + kc.getUpdateTextWordCount());
					kc.setUpdateTextCount(((Integer)topicData.get("update_text_count")).intValue() + kc.getUpdateTextCount());
					kc.setUpdateImageCount(((Integer)topicData.get("update_image_count")).intValue() + kc.getUpdateImageCount());
					kc.setUpdateVedioCount(((Integer)topicData.get("update_vedio_count")).intValue() + kc.getUpdateVedioCount());
					kc.setUpdateVedioLenght(((Integer)topicData.get("update_vedio_length")).intValue() + kc.getUpdateVedioLenght());
					kc.setUpdateAudioCount(((Integer)topicData.get("update_audio_count")).intValue() + kc.getUpdateAudioCount());
					kc.setUpdateAudioLenght(((Integer)topicData.get("update_audio_length")).intValue() + kc.getUpdateAudioLenght());
					kc.setUpdateVoteCount(((Integer)topicData.get("update_vote_count")).intValue() + kc.getUpdateVoteCount());
					kc.setUpdateTeaseCount(((Integer)topicData.get("update_tease_count")).intValue() + kc.getUpdateTeaseCount());
					kc.setReviewTextCountInApp(((Integer)topicData.get("review_text_count")).intValue() + kc.getReviewTextCountInApp());//这里因为保存的两个相加值，故这里只在一个参数上加上原有值
					kc.setReviewTextWordCountInApp(((Integer)topicData.get("review_text_length")).intValue() + kc.getReviewTextWordCountInApp());//这里因为保存的两个相加值，故这里只在一个参数上加上原有值
					
					this.saveKingdomCount(kc, false, listedPrice, yesterday, pushPriceLimit, pushPriceIncrLimit, pushPriceReduceLimit, nowDateStr);
				}
			}
			
			
			
			totalCount = totalCount + topicList.size();
			logger.info("本次处理了["+topicList.size()+"]个王国，共处理了["+totalCount+"]个王国");
		}
		logger.info("王国价值处理完成");
	}
	
	private Map<String, Object> getSubsidyConfig(int kv, List<Map<String, Object>> subsidyConfigList){
		if(null == subsidyConfigList || subsidyConfigList.size() == 0){
			return null;
		}
		Map<String, Object> result = null;
		for(Map<String, Object> s : subsidyConfigList){
			int m1 = ((Integer)s.get("m1")).intValue();
			int m2 = ((Integer)s.get("m2")).intValue();
			if(kv>=m1 && kv<=m2){
				result = new HashMap<String, Object>();
				result.put("k1", (Double)s.get("k1"));
				result.put("k2", (Double)s.get("k2"));
				result.put("k3", (Double)s.get("k3"));
				result.put("m2", (Integer)s.get("m2"));
				break;
			}
		}
		return result;
	}
	
	
	//处理topic_fragment的相关数据
	private void genFragmentKingdomCount(KingdomCount kc, Map<String, Object> f){
		int type = ((Integer)f.get("type")).intValue();
		int contentType = ((Integer)f.get("content_type")).intValue();
		String fragment = (String)f.get("fragment");
		if(null == fragment){
			fragment = "";
		}
		String extra = (String)f.get("extra");
		int source = ((Integer)f.get("source")).intValue();
		if(type == 0 && contentType == 0){//主播发言
			kc.setUpdateTextCount(kc.getUpdateTextCount() + 1);
			if(StringUtils.isNotBlank(fragment)){
				kc.setUpdateTextWordCount(kc.getUpdateTextWordCount() + fragment.length());
			}
		}else if(type == 52 && contentType == 17){//主播中表情
			kc.setUpdateTextCount(kc.getUpdateTextCount() + 1);
			kc.setUpdateTextWordCount(kc.getUpdateTextWordCount() + 5);
		}else if(type == 52 && contentType == 18){//主播大表情
			kc.setUpdateTextCount(kc.getUpdateTextCount() + 1);
			kc.setUpdateTextWordCount(kc.getUpdateTextWordCount() + 10);
		}else if((type == 11 && contentType == 11) || (type == 15 && contentType == 15)){//主播@ or 核心圈@
			kc.setUpdateTextCount(kc.getUpdateTextCount() + 1);
			String atString = fragment.trim();
			if(fragment.startsWith("{")){
				JSONObject obj = JSON.parseObject(fragment);
				atString = obj.getString("text");
			}
			kc.setUpdateTextWordCount(kc.getUpdateTextWordCount() + atString.length());
		}else if(type == 12 && contentType == 12){//主播视频
			kc.setUpdateVedioCount(kc.getUpdateVedioCount() + 1);
			if(StringUtils.isNotBlank(extra)){
				JSONObject obj = JSON.parseObject(extra);
				if(null != obj.get("duration")){
					int d = obj.getIntValue("duration");
					kc.setUpdateVedioLenght(kc.getUpdateVedioLenght() + d);
				}else{
					kc.setUpdateVedioLenght(kc.getUpdateVedioLenght() + 10);
				}
			}else{
				kc.setUpdateVedioLenght(kc.getUpdateVedioLenght() + 10);
			}
		}else if(type == 13 && contentType == 13){//主播音频
			kc.setUpdateAudioCount(kc.getUpdateAudioCount() + 1);
			if(StringUtils.isNotBlank(extra)){
				JSONObject obj = JSON.parseObject(extra);
				if(null != obj.get("duration")){
					int d = obj.getIntValue("duration");
					kc.setUpdateAudioLenght(kc.getUpdateAudioLenght() + d);
				}else{
					kc.setUpdateAudioLenght(kc.getUpdateAudioLenght() + 10);
				}
			}else{
				kc.setUpdateAudioLenght(kc.getUpdateAudioLenght() + 10);
			}
		}else if(type == 0 && contentType == 1){//主播图片
			kc.setUpdateImageCount(kc.getUpdateImageCount() + 1);
		}else if(type == 52 && contentType == 19){//主播投票
			kc.setUpdateVoteCount(kc.getUpdateVoteCount() + 1);
		}else if(type == 52 && contentType == 20){//主播逗一逗
			kc.setUpdateTeaseCount(kc.getUpdateTeaseCount() + 1);
		}else if(type == 1 && contentType == 0){//评论回复
			if(source == 3){//H5上微信登录回复
				kc.setReviewTextCountOutApp(kc.getReviewTextCountOutApp() + 1);
				kc.setReviewTextWordCountOutApp(kc.getReviewTextWordCountOutApp() + fragment.length());
			}else{//APP内回复
				kc.setReviewTextCountInApp(kc.getReviewTextCountInApp() + 1);
				kc.setReviewTextWordCountInApp(kc.getReviewTextWordCountInApp() + fragment.length());
			}
		}else if(type == 10 && contentType == 10){//评论@
			//这个只有APP内有
			kc.setReviewTextCountInApp(kc.getReviewTextCountInApp() + 1);
			String atString = fragment.trim();
			if(fragment.startsWith("{")){
				JSONObject obj = JSON.parseObject(fragment);
				atString = obj.getString("text");
			}
			kc.setReviewTextWordCountInApp(kc.getReviewTextWordCountInApp() + atString.length());
		}else if(type == 51 && contentType == 17){//评论中表情
			//这个只有APP内有
			kc.setReviewTextCountInApp(kc.getReviewTextCountInApp() + 1);
			kc.setReviewTextWordCountInApp(kc.getReviewTextWordCountInApp() + 5);
		}else if(type == 51 && contentType == 18){//评论大表情
			//这个只有APP内有
			kc.setReviewTextCountInApp(kc.getReviewTextCountInApp() + 1);
			kc.setReviewTextWordCountInApp(kc.getReviewTextWordCountInApp() + 10);
		}else if(type == 51 && contentType == 20){//评论逗一逗
			kc.setReviewTeaseCount(kc.getReviewTeaseCount() + 1);
		}
	}
	
	private double getDoubleConfig(String key, Map<String, String> configMap, double defaultValue){
		if(null == configMap || configMap.size() == 0 ||
				StringUtils.isBlank(key) || StringUtils.isBlank(configMap.get(key))){
			return defaultValue;
		}
		double result = defaultValue;
		try{
			result = Double.valueOf(configMap.get(key)).doubleValue();
		}catch(Exception e){
			logger.error("配置项["+key+"]有问题", e);
		}
		logger.info("配置["+key+"]==["+result+"]");
		return result;
	}
	
	private int getIntegerConfig(String key, Map<String, String> configMap, int defaultValue){
		if(null == configMap || configMap.size() == 0 ||
				StringUtils.isBlank(key) || StringUtils.isBlank(configMap.get(key))){
			return defaultValue;
		}
		int result = defaultValue;
		try{
			result = Integer.valueOf(configMap.get(key)).intValue();
		}catch(Exception e){
			logger.error("配置项["+key+"]有问题", e);
		}
		logger.info("配置["+key+"]==["+result+"]");
		return result;
	}
	
	@Data
	private class KingdomCount{
		private long topicId;
		private int price = 0;
		private int stealPrice = 0;
		
		private double diligently = 0;//用心度
		private int updateTextWordCount = 0;//更新文字字数
		private int updateTextCount = 0;//更新文字条数
		private int updateVedioLenght = 0;//更新视频长度
		private int updateVedioCount = 0;//更新视频条数
		private int updateAudioLenght = 0;//更新语音长度
		private int updateAudioCount = 0;//更新语音条数
		private int updateImageCount = 0;//更新图片
		private int updateVoteCount = 0;//投票
		private int updateTeaseCount = 0;//更新逗一逗
		private int updateDayCount = 0;//更新天数
		private double updateFrequency = 0;//频度
		
		private double approve = 0;//认可度权重
		private int reviewTextCountInApp = 0;//app内评论条数
		private int reviewTextCountOutApp = 0;//app外评论条数
		private int reviewTextWordCountInApp = 0;//app内评论字数条数
		private int reviewTextWordCountOutApp = 0;//app外评论字数条数
		private int readCountInApp = 0;//app内阅读
		private int readCountOutApp = 0;//app外阅读
		private int subscribeCount = 0;//订阅数
		private int reviewTeaseCount = 0;//评论逗一逗
		private int reviewVoteCount = 0;//参与投票
		private int shareCount = 0;//分享次数
		private int reviewDayCount = 0;//产生品论的天数
		private int readDayCount = 0;//产生阅读的天数
		
		private boolean isVlv = false;
		
		private int noUpdateDayCount = 0;//最后一次更新到当前的天数
		private int decay = 0;//衰减值
		
		
		//额外需要记录的参数
		private Date createTime;
		private Date lastUpdateTime;
		private int readCountDummyInApp = 0;//APP内虚拟阅读数
		private int readCountDummyOutApp = 0;//APP外虚拟阅读数
		private long uid;//国王UID
		private long updateCount = 0;
	}
	
	public void executeFull(String dateStr) throws Exception{
		logger.info("全量计算王国价值开始");
		//获取各种权重配置
		Map<String, String> weightConfigMap = userService.getAppConfigsByKeys(weightKeyList);
		logger.info("共["+weightKeyList.size()+"]个配置");
		logger.info("共["+weightConfigMap.size()+"]个配置，有参数值=="+weightConfigMap);
		//获取任务需要的当前的各种系数配置
		double updateTextWordCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTWORDCOUNT_WEIGHT", weightConfigMap, 1);//更新文字字数权重
		double updateTextCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTCOUNT_WEIGHT", weightConfigMap, 0);//更新文字条数权重
		double updateVedioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOLENGHT_WEIGHT", weightConfigMap, 0);//更新视频长度权重
		double updateVedioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOCOUNT_WEIGHT", weightConfigMap, 40);//更新视频条数权重
		double updateAudioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOLENGHT_WEIGHT", weightConfigMap, 0);//更新语音长度权重
		double updateAudioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOCOUNT_WEIGHT", weightConfigMap, 30);//更新语音条数权重
		double updateImageCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_IMAGECOUNT_WEIGHT", weightConfigMap, 20);//更新图片权重
		double updateVoteCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VOTECOUNT_WEIGHT", weightConfigMap, 1);//投票权重
		double updateTeaseCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEASECOUNT_WEIGHT", weightConfigMap, 10);//更新逗一逗权重
		double updateFrequencyWeight = this.getDoubleConfig("ALGORITHM_UPDATE_FREQUENCY_WEIGHT", weightConfigMap, 1);//频度权重
		
		double reviewTextCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_INAPP_WEIGHT", weightConfigMap, 0);//app内评论条数权重
		double reviewTextCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_OUTAPP_WEIGHT", weightConfigMap, 0);//app外评论条数权重
		double reviewTextWordCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_INAPP_WEIGHT", weightConfigMap, 25);//app内评论字数条数权重
		double reviewTextWordCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_OUTAPP_WEIGHT", weightConfigMap, 25);//app外评论字数条数权重
		double readCountInAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_INAPP_WEIGHT", weightConfigMap, 15);//app内阅读权重
		double readCountOutAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_OUTAPP_WEIGHT", weightConfigMap, 15);//app外阅读权重
		double subscribeCountWeight = this.getDoubleConfig("ALGORITHM_SUBSCRIBECOUNT_WEIGHT", weightConfigMap, 0);//订阅数权重
		double reviewTeaseCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEASECOUNT_WEIGHT", weightConfigMap, 20);//评论逗一逗权重
		double reviewVoteCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_VOTECOUNT_WEIGHT", weightConfigMap, 1);//参与投票权重
		double shareCountWeight = this.getDoubleConfig("ALGORITHM_SHARECOUNT_WEIGHT", weightConfigMap, 1);//分享次数权重
		double reviewDayCountWeight = this.getDoubleConfig("ALGORITHM_REVIEWDAYCOUNT_WEIGHT", weightConfigMap, 0);//产生品论的天数权重
		double readDayCountWeight = this.getDoubleConfig("ALGORITHM_READDAYCOUNT_WEIGHT", weightConfigMap, 1);//产生阅读的天数权重
		
		double vWeight = this.getDoubleConfig("ALGORITHM_V_WEIGHT", weightConfigMap, 0.2);//大V系数权重
		
		int listedPrice = this.getIntegerConfig("LISTED_PRICE", weightConfigMap, 20000);
		
		int pushPriceLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_LIMIT", weightConfigMap, 200);//推送王国价值阈值
		int pushPriceIncrLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_INCR_LIMIT", weightConfigMap, 50);//推送王国价值增长阈值
		int pushPriceReduceLimit = this.getIntegerConfig("ALGORITHM_PUSH_PRICE_INCR_LIMIT", weightConfigMap, 50);//推送王国价值减少阈值
		
		String nowDateStr = null;
		Date now = new Date();
		Date yesterday = null;
		if(StringUtils.isNotBlank(dateStr)){
			Date c = DateUtil.string2date(dateStr, "yyyy-MM-dd");
			yesterday = DateUtil.addDay(c, -1);
			nowDateStr = DateUtil.date2string(c, "yyyyMMdd");
		}else{
			yesterday = DateUtil.addDay(now, -1);
			nowDateStr = DateUtil.date2string(now, "yyyyMMdd");
		}
		
		String endTime = DateUtil.date2string(yesterday, "yyyy-MM-dd") + " 23:59:59";
		
		String topicSql = "select t.id,t.create_time,t.uid from topic t where t.create_time<='"+endTime+"' order by t.id limit ";
		
//		String topicSql = "select t.id,t.create_time,t.uid from topic t,(select f.topic_id,";
//		topicSql = topicSql + "MAX(if(f.type in (0,3,11,12,13,15,52,55),f.create_time, NULL)) as lastUpdateTime";
//		topicSql = topicSql + " from topic_fragment f where f.status=1 group by f.topic_id) m";
//		topicSql = topicSql + " where t.id=m.topic_id and m.lastUpdateTime>'2017-05-15 00:00:00'";
//		topicSql = topicSql + " and t.create_time<'" + endTime + "' order by t.id limit ";
		
		logger.info("开始处理王国价值");
		int start = 0;
		int pageSize = 500;
		int totalCount = 0;
		List<Map<String, Object>> topicList = null;
		StringBuilder topicFragmentSql = null;
		List<Map<String, Object>> fragmentList = null;
		Map<String, KingdomCount> kingCountMap = null;
		KingdomCount kc = null;
		StringBuilder topicDayCountSql = null;
		List<Map<String, Object>> topicDayCountList = null;
		StringBuilder favouriteSql = null;
		List<Map<String, Object>> favouriteCountList = null;
		StringBuilder readCountSql = null;
		List<Map<String, Object>> readCountList = null;
		StringBuilder voteCountSql = null;
		List<Map<String, Object>> voteCountList = null;
		List<Long> uidList = null;
		StringBuilder userProfileSql = null;
		List<Map<String, Object>> userProfileList = null;
		Map<String, Integer> vlvMap = null;
		StringBuilder topicDataSql = null;
		List<Map<String, Object>> topicDataList = null;
		Map<String, Map<String, Object>> topicDataMap = null;
		Map<String, Object> topicData = null;
		while(true){
			topicList = localJdbcDao.queryEvery(topicSql+start+","+pageSize);
			if(null == topicList || topicList.size() == 0){
				break;
			}
			start = start + pageSize;
			
			kingCountMap = new HashMap<String, KingdomCount>();
			
			uidList = new ArrayList<Long>();
			for(Map<String, Object> t : topicList){
				long topicId = ((Long)t.get("id")).longValue();
				kc = new KingdomCount();
				kc.setTopicId(topicId);
				kc.setCreateTime((Date)t.get("create_time"));
				kingCountMap.put(String.valueOf(topicId), kc);
				Long uid = (Long)t.get("uid");
				kc.setUid(uid);
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			
			//处理fragment相关数据
			topicFragmentSql = new StringBuilder();
			topicFragmentSql.append("select * from topic_fragment f");
			topicFragmentSql.append(" where f.status=1 and f.create_time<='").append(endTime).append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicFragmentSql.append(",");
				}
				topicFragmentSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicFragmentSql.append(")");
			fragmentList = localJdbcDao.queryEvery(topicFragmentSql.toString());
			if(null != fragmentList && fragmentList.size() > 0){
				for(Map<String, Object> f : fragmentList){
					long topicId = ((Long)f.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					this.genFragmentKingdomCount(kc, f);
				}
			}
			//处理几个连续数据
			topicDayCountSql = new StringBuilder();
			topicDayCountSql.append("select f.topic_id,");
			topicDayCountSql.append("count(DISTINCT if(f.type in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as updateDayCount,");
			topicDayCountSql.append("count(if(f.type in (0,3,11,12,13,15,52,55), TRUE, NULL)) as updateCount,");
			topicDayCountSql.append("count(DISTINCT if(f.type not in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as reviewDayCount,");
			topicDayCountSql.append("MAX(if(f.type in (0,3,11,12,13,15,52,55),f.create_time, NULL)) as lastUpdateTime");
			topicDayCountSql.append(" from topic_fragment f where f.status=1");
			topicDayCountSql.append(" and f.create_time<='").append(endTime);
			topicDayCountSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDayCountSql.append(",");
				}
				topicDayCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDayCountSql.append(") group by f.topic_id");
			topicDayCountList = localJdbcDao.queryEvery(topicDayCountSql.toString());
			if(null != topicDayCountList && topicDayCountList.size() > 0){
				for(Map<String, Object> c : topicDayCountList){
					long topicId = ((Long)c.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setUpdateDayCount(((Long)c.get("updateDayCount")).intValue());
					if(kc.getUpdateDayCount() == 0){
						kc.setUpdateDayCount(1);
					}
					kc.setReviewDayCount(((Long)c.get("reviewDayCount")).intValue());
					if(null != c.get("lastUpdateTime") && StringUtils.isNotBlank((String)c.get("lastUpdateTime"))){
						kc.setLastUpdateTime(DateUtil.string2date((String)c.get("lastUpdateTime"), "yyyy-MM-dd HH:mm:ss"));
					}else{
						kc.setLastUpdateTime(kc.getCreateTime());
					}
					long dayCount = DateUtil.getDaysBetween2Date(kc.getCreateTime(), kc.getLastUpdateTime()) + 1;
					kc.setReadDayCount((int)dayCount);//老数据处理，产生阅读的天数，即为从创建到最后一次更新的天数
					kc.setUpdateFrequency((double)kc.getUpdateDayCount()/(double)dayCount);
					long noUpdateDayCount = DateUtil.getDaysBetween2Date(kc.getLastUpdateTime(), yesterday);
					kc.setNoUpdateDayCount((int)noUpdateDayCount);
					if(null != c.get("updateCount")){
						kc.setUpdateCount((Long)c.get("updateCount"));
					}
				}
			}
			
			//订阅数
			favouriteSql = new StringBuilder();
			favouriteSql.append("select f.topic_id, count(DISTINCT f.uid) as fcount");
			favouriteSql.append(" from live_favorite f where f.create_time<='").append(endTime);
			favouriteSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					favouriteSql.append(",");
				}
				favouriteSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			favouriteSql.append(") group by f.topic_id");
			favouriteCountList = localJdbcDao.queryEvery(favouriteSql.toString());
			if(null != favouriteCountList && favouriteCountList.size() > 0){
				for(Map<String, Object> f : favouriteCountList){
					long topicId = ((Long)f.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setSubscribeCount(((Long)f.get("fcount")).intValue());
				}
			}
			
			//阅读数
			readCountSql = new StringBuilder();
			readCountSql.append("select c.forward_cid,c.read_count,c.read_count_dummy");
			readCountSql.append(" from content c where c.type=3 and c.forward_cid in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					readCountSql.append(",");
				}
				readCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			readCountSql.append(")");
			readCountList = localJdbcDao.queryEvery(readCountSql.toString());
			if(null != readCountList && readCountList.size() > 0){
				for(Map<String, Object> r : readCountList){
					long topicId = ((Long)r.get("forward_cid")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					int readCount = (Integer)r.get("read_count");
					int readCountDummy = (Integer)r.get("read_count_dummy");
					int dayNum = (int)DateUtil.getDaysBetween2Date(kc.getCreateTime(), yesterday)+1;
					int totalDayNum = (int)DateUtil.getDaysBetween2Date(kc.getCreateTime(), now);
					int readCountInApp = readCount*dayNum/totalDayNum - (int)kc.getUpdateCount()*4/5;
					kc.setReadCountInApp(readCountInApp);
					kc.setReadCountDummyInApp(readCountDummy*dayNum/totalDayNum);
				}
			}
			
			//分享次数，全量的，默认为0
			
			//参与投票次数
			voteCountSql = new StringBuilder();
			voteCountSql.append("select i.topicId,count(1) as vcount");
			voteCountSql.append(" from vote_info i,vote_record r");
			voteCountSql.append(" where i.id=r.voteId and i.topicId in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					voteCountSql.append(",");
				}
				voteCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			voteCountSql.append(") and r.create_time<='").append(endTime);
			voteCountSql.append("' group by i.topicId");
			voteCountList = localJdbcDao.queryEvery(voteCountSql.toString());
			if(null != voteCountList && voteCountList.size() > 0){
				for(Map<String, Object> v : voteCountList){
					long topicId = ((Long)v.get("topicId")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setReviewVoteCount(((Long)v.get("vcount")).intValue());
				}
			}
			
			//处理大V
			vlvMap = new HashMap<String, Integer>();
			userProfileSql = new StringBuilder();
			userProfileSql.append("select u.uid,u.v_lv from user_profile u where uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					userProfileSql.append(",");
				}
				userProfileSql.append(uidList.get(i));
			}
			userProfileSql.append(")");
			userProfileList = localJdbcDao.queryEvery(userProfileSql.toString());
			if(null != userProfileList && userProfileList.size() > 0){
				for(Map<String, Object> u : userProfileList){
					vlvMap.put(String.valueOf(u.get("uid")), (Integer)u.get("v_lv"));
				}
			}
			
			//获取所有的topic data
			topicDataSql = new StringBuilder();
			topicDataSql.append("select * from topic_data t where t.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDataSql.append(",");
				}
				topicDataSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDataSql.append(")");
			topicDataList = localJdbcDao.queryEvery(topicDataSql.toString());
			topicDataMap = new HashMap<String, Map<String, Object>>();
			if(null != topicDataList && topicDataList.size() > 0){
				for(Map<String, Object> t : topicDataList){
					long topicId = ((Long)t.get("topic_id")).longValue();
					topicDataMap.put(String.valueOf(topicId), t);
				}
			}
			
			//开始计算
			for(Map.Entry<String, KingdomCount> entry : kingCountMap.entrySet()){
				kc = entry.getValue();
				//几个时间处理下
				if(null == kc.getLastUpdateTime()){
					kc.setLastUpdateTime(kc.getCreateTime());
					kc.setReadDayCount(1);
					kc.setUpdateDayCount(1);
					kc.setUpdateFrequency(1);
					long noUpdateDayCount = DateUtil.getDaysBetween2Date(kc.getLastUpdateTime(), yesterday);
					kc.setNoUpdateDayCount((int)noUpdateDayCount);
				}
				
				if(null != vlvMap.get(String.valueOf(kc.getUid())) && vlvMap.get(String.valueOf(kc.getUid())).intValue() == 1){
					kc.setVlv(true);
				}
				double x = (kc.getUpdateTextWordCount()*updateTextWordCountWeight + kc.getUpdateTextCount()*updateTextCountWeight
						+ kc.getUpdateVedioCount()*updateVedioCountWeight + kc.getUpdateVedioLenght()*updateVedioLenghtWeight
						+ kc.getUpdateAudioCount()*updateAudioCountWeight + kc.getUpdateAudioLenght()*updateAudioLenghtWeight
						+ kc.getUpdateImageCount()*updateImageCountWeight + kc.getUpdateVoteCount()*updateVoteCountWeight
						+ kc.getUpdateTeaseCount()*updateTeaseCountWeight)*kc.getUpdateFrequency()*updateFrequencyWeight;
				if(kc.isVlv()){
					x = x * (1 + vWeight);
				}
				
				double y = (kc.getReviewTextCountInApp()*reviewTextCountInAppWeight + kc.getReviewTextCountOutApp()*reviewTextCountOutAppWeight
						+ kc.getReviewTextWordCountInApp()*reviewTextWordCountInAppWeight + kc.getReviewTextWordCountOutApp()*reviewTextWordCountOutAppWeight
						+ kc.getReadCountInApp()*readCountInAppWeight + kc.getReadCountOutApp()*readCountOutAppWeight
						+ kc.getSubscribeCount()*subscribeCountWeight + kc.getReviewTeaseCount()*reviewTeaseCountWeight
						+ kc.getReviewVoteCount()*reviewVoteCountWeight + kc.getShareCount()*shareCountWeight);

				int xx = (int)(x*1000);
				int yy = (int)(y*1000);
				
				kc.setApprove(new BigDecimal((double)yy/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				kc.setDiligently(new BigDecimal((double)xx/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				
//				if(kc.getNoUpdateDayCount()<=20){老毛说这里不需要限制了，老的也有价值计算
					double kv = Math.pow(Math.pow(x, 2)+Math.pow(y, 2),0.5);
					kc.setPrice((int)kv);
//				}
				
				topicData = topicDataMap.get(String.valueOf(kc.getTopicId()));
				if(null == topicData){//当天新增的王国
					this.saveKingdomCount(kc, true, listedPrice, yesterday, pushPriceLimit, pushPriceIncrLimit, pushPriceReduceLimit, nowDateStr);
				}else{
					this.saveKingdomCount(kc, false, listedPrice, yesterday, pushPriceLimit, pushPriceIncrLimit, pushPriceReduceLimit, nowDateStr);
				}
			}
			
			totalCount = totalCount + topicList.size();
			logger.info("本次处理了["+topicList.size()+"]个王国，共处理了["+totalCount+"]个王国");
		}
		logger.info("王国价值处理完成");
	}
	
	private void saveKingdomCount(KingdomCount kc, boolean isNew, int listedPrice, Date recordTime, int pushPriceLimit, int pushPriceIncrLimit, int pushPriceReduceLimit, String nowDateStr){
		StringBuilder topicPriceQuerySql = new StringBuilder();
		topicPriceQuerySql.append("select t.title,t.price,p.nick_name,t.uid from topic t,user_profile p where t.uid=p.uid and t.id=").append(kc.getTopicId());
		List<Map<String, Object>> topicPriceList = localJdbcDao.queryEvery(topicPriceQuerySql.toString());
		int oldPrice = 0;
		String title = "";
		String kingName = "";
		long uid = 0;
		if(null != topicPriceList && topicPriceList.size() > 0){
			Map<String, Object> topicPrice = topicPriceList.get(0);
			oldPrice = (Integer)topicPrice.get("price");
			title = (String)topicPrice.get("title");
			kingName = (String)topicPrice.get("nick_name");
			uid = (Long)topicPrice.get("uid");
		}
		
		if(kc.getPrice()<0){
			kc.setPrice(0);
		}
		if(kc.getStealPrice() < 0){
			kc.setStealPrice(0);
		}
		
		if(kc.getStealPrice() > kc.getPrice()){
			kc.setStealPrice(kc.getPrice());
		}
		
		StringBuilder saveSql = new StringBuilder();
		if(!isNew){//有的，则更新
			saveSql.append("update topic_data set steal_price=").append(kc.getStealPrice());
			saveSql.append(",last_price=").append(oldPrice);
			saveSql.append(",last_price_incr=").append(kc.getPrice()-oldPrice);
			saveSql.append(",diligently=").append(kc.getDiligently());
			saveSql.append(",approve=").append(kc.getApprove());
			saveSql.append(",update_text_length=").append(kc.getUpdateTextWordCount());
			saveSql.append(",update_text_count=").append(kc.getUpdateTextCount());
			saveSql.append(",update_image_count=").append(kc.getUpdateImageCount());
			saveSql.append(",update_vedio_count=").append(kc.getUpdateVedioCount());
			saveSql.append(",update_vedio_length=").append(kc.getUpdateVedioLenght());
			saveSql.append(",update_audio_count=").append(kc.getUpdateAudioCount());
			saveSql.append(",update_audio_length=").append(kc.getUpdateAudioLenght());
			saveSql.append(",update_vote_count=").append(kc.getUpdateVoteCount());
			saveSql.append(",update_tease_count=").append(kc.getUpdateTeaseCount());
			saveSql.append(",update_day_count=").append(kc.getUpdateDayCount());
			saveSql.append(",review_text_count=").append(kc.getReviewTextCountInApp()+kc.getReviewTextCountOutApp());
			saveSql.append(",review_text_length=").append(kc.getReviewTextWordCountInApp()+kc.getReviewTextWordCountOutApp());
			saveSql.append(" where topic_id=").append(kc.getTopicId());
		}else{//没有，则新增
			saveSql.append("insert into topic_data(topic_id,last_price,last_price_incr,steal_price,update_time,diligently,approve,update_text_length,");
			saveSql.append("update_text_count,update_image_count,update_vedio_count,update_vedio_length,update_audio_count,");
			saveSql.append("update_audio_length,update_vote_count,update_tease_count,update_day_count,review_text_count,review_text_length)");
			saveSql.append(" values (").append(kc.getTopicId()).append(",").append(oldPrice).append(",");
			saveSql.append(kc.getPrice()-oldPrice).append(",").append(kc.getStealPrice()).append(",now(),");
			saveSql.append(kc.getDiligently()).append(",").append(kc.getApprove()).append(",").append(kc.getUpdateTextWordCount());
			saveSql.append(",").append(kc.getUpdateTextCount()).append(",").append(kc.getUpdateImageCount()).append(",");
			saveSql.append(kc.getUpdateVedioCount()).append(",").append(kc.getUpdateVedioLenght()).append(",").append(kc.getUpdateAudioCount());
			saveSql.append(",").append(kc.getUpdateAudioLenght()).append(",").append(kc.getUpdateVoteCount()).append(",");
			saveSql.append(kc.getUpdateTeaseCount()).append(",").append(kc.getUpdateDayCount()).append(",").append(kc.getReviewTextCountInApp()+kc.getReviewTextCountOutApp());
			saveSql.append(",").append(kc.getReviewTextWordCountInApp()+kc.getReviewTextWordCountOutApp()).append(")");
		}
		localJdbcDao.executeSql(saveSql.toString());
		
		StringBuilder saveHisSql = new StringBuilder();
		saveHisSql.append("insert into topic_price_his(topic_id,price,create_time)");
		saveHisSql.append(" values (").append(kc.getTopicId()).append(",").append(kc.getPrice());
		saveHisSql.append(",'").append(DateUtil.date2string(recordTime, "yyyy-MM-dd HH:mm:ss"));
		saveHisSql.append("')");
		localJdbcDao.executeSql(saveHisSql.toString());
		
		StringBuilder updatePriceSql = new StringBuilder();
		updatePriceSql.append("update topic set price=").append(kc.getPrice()).append(",update_time=update_time where id=").append(kc.getTopicId());
		localJdbcDao.executeSql(updatePriceSql.toString());
		
		/* 2.2.8版本进行用户手动上市了，所以这里无需再自动上市以及跑马灯处理了
		String updatelistedTimeSql = null;
		if(oldPrice >= listedPrice){
			if(kc.getPrice() >= listedPrice){
				//上一次已经上市了，这次就不用上市了
			}else{
				//上一次上市，但是这次没上市，需要将时间清空
				updatelistedTimeSql = "update topic set listing_time=null,update_time=update_time where id="+kc.getTopicId();
				localJdbcDao.executeSql(updatelistedTimeSql);
			}
		}else{
			if(kc.getPrice() >= listedPrice){
				//上一次没上市，这次上市了
				updatelistedTimeSql = "update topic set listing_time=now(),update_time=update_time where id="+kc.getTopicId();
				localJdbcDao.executeSql(updatelistedTimeSql);
				//并且添加跑马灯
				kingName = kingName.replaceAll("'", "''");
				title = title.replaceAll("'", "''");
				StringBuilder insertTopicNewsSql = new StringBuilder();
				insertTopicNewsSql.append("insert into topic_news(topic_id,content,type,create_time)");
				insertTopicNewsSql.append(" values (").append(kc.getTopicId()).append(",'").append(kingName).append("的《").append(title);
				insertTopicNewsSql.append("》挂牌上市了，快来围观抢购吧。',1,now())");
				localJdbcDao.executeSql(insertTopicNewsSql.toString());
			}else{
				//上一次没上市，这次还没上市，不用处理啥
			}
		}
		*/
		//王国价值低于上市估值自动下架
		if(kc.getPrice() < listedPrice){
			TopicListed topicListed = liveService.getTopicListedByTopicId(kc.getTopicId());
			//已经上市
			if(topicListed!=null){
				//已锁定和正在交易的不会下架
				if(topicListed.getStatus()==0){
					String delTopicListed = "delete from topic_listed where topic_id="+kc.getTopicId();
					localJdbcDao.executeSql(delTopicListed);
					Topic topic = liveService.getTopicById(kc.getTopicId());
					if(topic!=null){
						JsonObject jsonObject = new JsonObject();
		                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
		                jsonObject.addProperty("topicId",topic.getId());
		                jsonObject.addProperty("contentType",topic.getType());
		                jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//核心圈
		                userService.pushWithExtra(topic.getUid().toString(), "由于你的王国『"+topic.getTitle()+"』已经低于上市估值，现已被米汤王国下架。", JPushUtils.packageExtra(jsonObject));
					}
				}
			}
		}else{
			//不用处理
		}
		
		//推送逻辑
		int a = kc.getPrice()-oldPrice;
		//先判断当前王国是否用户的第一个王国，并且是当前创建的
		boolean needPush = false;
		if(kc.getPrice() > 0 && DateUtil.isSameDay(kc.getCreateTime(), recordTime)){//是否昨天创建的
			StringBuilder firstKingdomSql = new StringBuilder();
			firstKingdomSql.append("select * from topic t where t.uid=").append(uid);
			firstKingdomSql.append(" order by t.id limit 1");
			List<Map<String, Object>> firstKingdomList = localJdbcDao.queryEvery(firstKingdomSql.toString());
			Map<String, Object> firstKingdom = null;
			if(null != firstKingdomList && firstKingdomList.size() > 0){
				firstKingdom = firstKingdomList.get(0);
			}
			if(null != firstKingdom && ((Long)firstKingdom.get("id")).longValue() == kc.getTopicId()){
				//这个是必须得推送的
				needPush = true;
			}
		}
		//再判断是否需要根据阈值进行推送
		if(!needPush){
			if(kc.getPrice() > 0 && kc.getPrice() >= pushPriceLimit){//王国值阈值
				if(a>0 && a>=pushPriceIncrLimit){//增
					needPush = true;
				}else if(a<0 && Math.abs(a) >= pushPriceReduceLimit){
					needPush = true;
				}
			}
		}
		
		if(needPush){//需要推送，则记录待推送记录表
			StringBuilder insertPricePushSql = new StringBuilder();
			insertPricePushSql.append("insert into topic_price_push(date_code,uid,topic_id,type,change_price,kingdom_price,status)");
			insertPricePushSql.append(" values('").append(nowDateStr).append("',").append(uid).append(",").append(kc.getTopicId());
			insertPricePushSql.append(",").append(a>=0?1:2).append(",").append(Math.abs(a)).append(",").append(kc.getPrice());
			insertPricePushSql.append(",0)");
			localJdbcDao.executeSql(insertPricePushSql.toString());
			
			//需要再王国里插入一段系统消息
			String msg = null;
			if(a>0){//升值
				msg = "昨日升值了"+String.valueOf(Math.abs(a))+"米汤币，棒棒哒！再接再厉吧。";
			}else{
				msg = "昨日贬值了"+String.valueOf(Math.abs(a))+"米汤币，略可惜，继续加油吧。";
			}
			
			JSONObject extra = new JSONObject();
			extra.put("type", "system");
			extra.put("only", UUID.randomUUID().toString()+"-"+new Random().nextInt());
			extra.put("content", msg);
			extra.put("linkType", 0);//纯文本
			
			StringBuilder insertSystemMsgSql = new StringBuilder();
			insertSystemMsgSql.append("insert into topic_fragment(topic_id,uid,fragment_image,fragment,type,content_type,top_id,");
			insertSystemMsgSql.append("bottom_id,create_time,at_uid,source,extra,score,status) values(");
			insertSystemMsgSql.append(kc.getTopicId()).append(",").append(uid).append(",'','").append(msg).append("',");
			insertSystemMsgSql.append(Specification.LiveSpeakType.SYSTEM.index).append(",0,0,0,now(),0,0,'");
			insertSystemMsgSql.append(extra.toJSONString()).append("',0,1)");
			localJdbcDao.executeSql(insertSystemMsgSql.toString());
		}
	}
}
