package com.me2me.mgmt.task.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.common.utils.DateUtil;
import com.me2me.content.service.ContentService;
import com.me2me.user.service.UserService;

@Component
public class KingdomPriceTask {

	private static final Logger logger = LoggerFactory.getLogger(KingdomPriceTask.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private ContentService contentService;
	
	private static List<String> weightKeyList = new ArrayList<String>(){
		private static final long serialVersionUID = -7635500651260154850L;

		{
			this.add("ALGORITHM_DILIGENTLY_WEIGHT");
			this.add("ALGORITHM_UPDATE_TEXTWORDCOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_TEXTCOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_VEDIOLENGHT_WEIGHT");
			this.add("ALGORITHM_UPDATE_VEDIOCOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_AUDIOLENGHT_WEIGHT");
			this.add("ALGORITHM_UPDATE_AUDIOCOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_IMAGECOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_VOTECOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_TEASECOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_DAYCOUNT_WEIGHT");
			this.add("ALGORITHM_UPDATE_FREQUENCY_WEIGHT");
			this.add("ALGORITHM_APPROVE_WEIGHT");
			this.add("ALGORITHM_REVIEW_TEXTCOUNT_INAPP_WEIGHT");
			this.add("ALGORITHM_REVIEW_TEXTCOUNT_OUTAPP_WEIGHT");
			this.add("ALGORITHM_REVIEW_TEXTWORDCOUNT_INAPP_WEIGHT");
			this.add("ALGORITHM_REVIEW_TEXTWORDCOUNT_OUTAPP_WEIGHT");
			this.add("ALGORITHM_READCOUNT_INAPP_WEIGHT");
			this.add("ALGORITHM_READCOUNT_OUTAPP_WEIGHT");
			this.add("ALGORITHM_SUBSCRIBECOUNT_WEIGHT");
			this.add("ALGORITHM_REVIEW_TEASECOUNT_WEIGHT");
			this.add("ALGORITHM_REVIEW_VOTECOUNT_WEIGHT");
			this.add("ALGORITHM_SHARECOUNT_WEIGHT");
			this.add("ALGORITHM_REVIEWDAYCOUNT_WEIGHT");
			this.add("ALGORITHM_READDAYCOUNT_WEIGHT");
			this.add("ALGORITHM_V_WEIGHT");
			this.add("ALGORITHM_ABILITYVALUE_WEIGHT");
			this.add("ALGORITHM_DECAY_BASE_WEIGHT");
			this.add("ALGORITHM_DECAY_BASEDAYCOUNT_WEIGHT");
		}
	};
	
	@Scheduled(cron="0 2 0 * * ?")
	public void doTask(){
		logger.info("王国价值任务开始");
		long s = System.currentTimeMillis();
		try{
			this.executeIncr();
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
	private void executeIncr(){
		//获取各种权重配置
		Map<String, String> weightConfigMap = userService.getAppConfigsByKeys(weightKeyList);
		//获取任务需要的当前的各种系数配置
		double diligentlyWeight = this.getDoubleConfig("ALGORITHM_DILIGENTLY_WEIGHT", weightConfigMap, 1);//用心度权重
		double updateTextWordCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTWORDCOUNT_WEIGHT", weightConfigMap, 1);//更新文字字数权重
		double updateTextCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEXTCOUNT_WEIGHT", weightConfigMap, 1);//更新文字条数权重
		double updateVedioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOLENGHT_WEIGHT", weightConfigMap, 1);//更新视频长度权重
		double updateVedioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VEDIOCOUNT_WEIGHT", weightConfigMap, 1);//更新视频条数权重
		double updateAudioLenghtWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOLENGHT_WEIGHT", weightConfigMap, 1);//更新语音长度权重
		double updateAudioCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_AUDIOCOUNT_WEIGHT", weightConfigMap, 1);//更新语音条数权重
		double updateImageCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_IMAGECOUNT_WEIGHT", weightConfigMap, 1);//更新图片权重
		double updateVoteCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_VOTECOUNT_WEIGHT", weightConfigMap, 1);//投票权重
		double updateTeaseCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_TEASECOUNT_WEIGHT", weightConfigMap, 1);//更新逗一逗权重
		double updateDayCountWeight = this.getDoubleConfig("ALGORITHM_UPDATE_DAYCOUNT_WEIGHT", weightConfigMap, 1);//更新天数权重
		double updateFrequencyWeight = this.getDoubleConfig("ALGORITHM_UPDATE_FREQUENCY_WEIGHT", weightConfigMap, 1);//频度权重
		
		double approveWeight = this.getDoubleConfig("ALGORITHM_APPROVE_WEIGHT", weightConfigMap, 1);//认可度权重
		double reviewTextCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_INAPP_WEIGHT", weightConfigMap, 1);//app内评论条数权重
		double reviewTextCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTCOUNT_OUTAPP_WEIGHT", weightConfigMap, 1);//app外评论条数权重
		double reviewTextWordCountInAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_INAPP_WEIGHT", weightConfigMap, 1);//app内评论字数条数权重
		double reviewTextWordCountOutAppWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEXTWORDCOUNT_OUTAPP_WEIGHT", weightConfigMap, 1);//app外评论字数条数权重
		double readCountInAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_INAPP_WEIGHT", weightConfigMap, 1);//app内阅读权重
		double readCountOutAppWeight = this.getDoubleConfig("ALGORITHM_READCOUNT_OUTAPP_WEIGHT", weightConfigMap, 1);//app外阅读权重
		double subscribeCountWeight = this.getDoubleConfig("ALGORITHM_SUBSCRIBECOUNT_WEIGHT", weightConfigMap, 1);//订阅数权重
		double reviewTeaseCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_TEASECOUNT_WEIGHT", weightConfigMap, 1);//评论逗一逗权重
		double reviewVoteCountWeight = this.getDoubleConfig("ALGORITHM_REVIEW_VOTECOUNT_WEIGHT", weightConfigMap, 1);//参与投票权重
		double shareCountWeight = this.getDoubleConfig("ALGORITHM_SHARECOUNT_WEIGHT", weightConfigMap, 1);//分享次数权重
		double reviewDayCountWeight = this.getDoubleConfig("ALGORITHM_REVIEWDAYCOUNT_WEIGHT", weightConfigMap, 1);//产生品论的天数权重
		double readDayCountWeight = this.getDoubleConfig("ALGORITHM_READDAYCOUNT_WEIGHT", weightConfigMap, 1);//产生阅读的天数权重
		
		double vWeight = this.getDoubleConfig("ALGORITHM_V_WEIGHT", weightConfigMap, 1);//大V系数权重
		double abilityValueWeight = this.getDoubleConfig("ALGORITHM_ABILITYVALUE_WEIGHT", weightConfigMap, 1);//能力值权重
		
		double decayBaseWeight = this.getDoubleConfig("ALGORITHM_DECAY_BASE_WEIGHT", weightConfigMap, 1);//衰减基数权重
		double decayBaseDayCountWeight = this.getDoubleConfig("ALGORITHM_DECAY_BASEDAYCOUNT_WEIGHT", weightConfigMap, 1);//衰减标准天数权重
		
		String yesterday = DateUtil.date2string(DateUtil.addDay(new Date(), -1), "yyyy-MM-dd");
		String startTime = startTime = yesterday + " 00:00:00";
		String endTime = yesterday + " 23:59:59";
		
		String topicSql = "select t.id,t. from topic t where t.create_time<='"+endTime+"' order by t.id ";
		
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
		while(true){
			topicList = contentService.queryEvery(topicSql+start+","+pageSize);
			if(null == topicList || topicList.size() == 0){
				break;
			}
			
			//处理fragment相关数据
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
			fragmentList = contentService.queryEvery(topicFragmentSql.toString());
			kingCountMap = new HashMap<String, KingdomCount>();
			if(null != fragmentList && fragmentList.size() > 0){
				for(Map<String, Object> f : fragmentList){
					long topicId = ((Long)f.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					if(null == kc){
						kc = new KingdomCount();
						kc.setTopicId(topicId);
						kingCountMap.put(String.valueOf(topicId), kc);
					}
					this.genFragmentKingdomCount(kc, f);
				}
			}
			//处理几个连续数据
			topicDayCountSql = new StringBuilder();
			topicDayCountSql.append("select f.topic_id,");
			topicDayCountSql.append("count(DISTINCT if(f.type in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as updateDayCount,");
			topicDayCountSql.append("count(DISTINCT if(f.type not in (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) as reviewDayCount,");
			topicDayCountSql.append("MAX(if(f.type in (0,3,11,12,13,15,52,55),f.create_time, NULL)) as lastUpdateTime");
			topicDayCountSql.append("from topic_fragment f where f.status=1");
			topicDayCountSql.append(" and f.create_time<='").append(endTime);
			topicDayCountSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDayCountSql.append(",");
				}
				topicDayCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDayCountSql.append(") group by f.topic_id");
			topicDayCountList = contentService.queryEvery(topicDayCountSql.toString());
			if(null != topicDayCountList && topicDayCountList.size() > 0){
				for(Map<String, Object> c : topicDayCountList){
					long topicId = ((Long)c.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					if(null == kc){
						kc = new KingdomCount();
						kc.setTopicId(topicId);
						kingCountMap.put(String.valueOf(topicId), kc);
					}
					kc.setUpdateDayCount(((Long)c.get("updateDayCount")).intValue());
					kc.setReviewDayCount(((Long)c.get("reviewDayCount")).intValue());
					kc.setLastUpdateTime((Date)c.get("lastUpdateTime"));
				}
			}
			
			//订阅数
			
			
			
			
			
			
			for(Map<String, Object> t : topicList){
				
			}
			
			
			
			totalCount = totalCount + topicList.size();
			logger.info("本次处理了["+topicList.size()+"]个王国，共处理了["+totalCount+"]个王国");
		}
		logger.info("王国价值处理完成");
		
		
		
		
		
		
		
		
		
	}
	
	//处理topic_fragment的相关数据
	private void genFragmentKingdomCount(KingdomCount kc, Map<String, Object> f){
		int type = ((Integer)f.get("type")).intValue();
		int contentType = ((Integer)f.get("content_type")).intValue();
		String fragment = (String)f.get("fragment");
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
	}
	
	public void executeFull(){
		logger.info("全量计算王国价值开始");
		//获取各种权重配置
		Map<String, String> weightConfigMap = userService.getAppConfigsByKeys(weightKeyList);
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
		
		Date yesterday = DateUtil.addDay(new Date(), -1);
		String endTime = DateUtil.date2string(yesterday, "yyyy-MM-dd") + " 23:59:59";
		
		String topicSql = "select t.id,t.create_time,t.uid from topic t where t.create_time<='"+endTime+"' order by t.id ";
		
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
		while(true){
			topicList = contentService.queryEvery(topicSql+start+","+pageSize);
			if(null == topicList || topicList.size() == 0){
				break;
			}
			start = start + pageSize;
			
			kingCountMap = new HashMap<String, KingdomCount>();
			
			uidList = new ArrayList<Long>();
			for(Map<String, Object> t : topicList){
				long topicId = ((Long)t.get("topic_id")).longValue();
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
			fragmentList = contentService.queryEvery(topicFragmentSql.toString());
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
			topicDayCountSql.append("from topic_fragment f where f.status=1");
			topicDayCountSql.append(" and f.create_time<='").append(endTime);
			topicDayCountSql.append("' and f.topic_id in (");
			for(int i=0;i<topicList.size();i++){
				if(i>0){
					topicDayCountSql.append(",");
				}
				topicDayCountSql.append(String.valueOf(topicList.get(i).get("id")));
			}
			topicDayCountSql.append(") group by f.topic_id");
			topicDayCountList = contentService.queryEvery(topicDayCountSql.toString());
			if(null != topicDayCountList && topicDayCountList.size() > 0){
				for(Map<String, Object> c : topicDayCountList){
					long topicId = ((Long)c.get("topic_id")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setUpdateDayCount(((Long)c.get("updateDayCount")).intValue());
					if(kc.getUpdateDayCount() == 0){
						kc.setUpdateDayCount(1);
					}
					kc.setReviewDayCount(((Long)c.get("reviewDayCount")).intValue());
					kc.setLastUpdateTime((Date)c.get("lastUpdateTime"));
					long dayCount = DateUtil.getDaysBetween2Date(kc.getCreateTime(), kc.getLastUpdateTime()) + 1;
					kc.setReadDayCount((int)dayCount);//老数据处理，产生阅读的天数，即为从创建到最后一次更新的天数
					kc.setUpdateFrequency((double)kc.getUpdateDayCount()/(double)dayCount);
					long noUpdateDayCount = DateUtil.getDaysBetween2Date(kc.getLastUpdateTime(), yesterday);
					kc.setNoUpdateDayCount((int)noUpdateDayCount);
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
			favouriteCountList = contentService.queryEvery(favouriteSql.toString());
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
			readCountList = contentService.queryEvery(readCountSql.toString());
			if(null != readCountList && readCountList.size() > 0){
				for(Map<String, Object> r : readCountList){
					long topicId = ((Long)r.get("forward_cid")).longValue();
					kc = kingCountMap.get(String.valueOf(topicId));
					kc.setReadCountInApp((Integer)r.get("read_count"));
					kc.setReadCountDummyInApp((Integer)r.get("read_count_dummy"));
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
			voteCountList = contentService.queryEvery(voteCountSql.toString());
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
			userProfileList = contentService.queryEvery(userProfileSql.toString());
			if(null != userProfileList && userProfileList.size() > 0){
				for(Map<String, Object> u : userProfileList){
					vlvMap.put(String.valueOf(u.get("uid")), (Integer)u.get("v_lv"));
				}
			}
			
			//开始计算
			for(Map.Entry<String, KingdomCount> entry : kingCountMap.entrySet()){
				kc = entry.getValue();
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
						+ kc.getReviewVoteCount()*reviewVoteCountWeight + kc.getShareCount()*shareCountWeight)*(kc.getReviewDayCount()*reviewDayCountWeight
						+ kc.getReadDayCount()*readDayCountWeight)/kc.getUpdateDayCount();

				kc.setApprove(new BigDecimal(y).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				kc.setDiligently(new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				
				if(kc.getNoUpdateDayCount()>20){
					double kv = Math.pow(Math.pow(x, 2)+Math.pow(y, 2),0.5);
					kc.setPrice((int)kv);
				}
				this.saveKingdomCount(kc);
			}
			
			
			
			totalCount = totalCount + topicList.size();
			logger.info("本次处理了["+topicList.size()+"]个王国，共处理了["+totalCount+"]个王国");
		}
		logger.info("王国价值处理完成");
	}
	
	private void saveKingdomCount(KingdomCount kc){
		StringBuilder topicPriceQuerySql = new StringBuilder();
		topicPriceQuerySql.append("select t.price from topic t where t.id=").append(kc.getTopicId());
		List<Map<String, Object>> topicPriceList = contentService.queryEvery(topicPriceQuerySql.toString());
		int oldPrice = 0;
		if(null != topicPriceList && topicPriceList.size() > 0){
			Map<String, Object> topicPrice = topicPriceList.get(0);
			oldPrice = (Integer)topicPrice.get("price");
		}
		
		StringBuilder topicDataSql = new StringBuilder();
		topicDataSql.append("select * from topic_data d where d.topic_id=").append(kc.getTopicId());
		List<Map<String, Object>> list = contentService.queryEvery(topicDataSql.toString());
		StringBuilder saveSql = new StringBuilder();
		if(null != list && list.size() > 0){//有的，则更新
			saveSql.append("update topic_data set steal_price=").append(kc.getStealPrice());
			saveSql.append(",last_price=").append(oldPrice);
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
			saveSql.append("insert into topic_data(topic_id,last_price,steal_price,update_time,diligently,approve,update_text_length,");
			saveSql.append("update_text_count,update_image_count,update_vedio_count,update_vedio_length,update_audio_count,");
			saveSql.append("update_audio_length,update_vote_count,update_tease_count,update_day_count,review_text_count,review_text_length)");
			saveSql.append(" values (").append(kc.getTopicId()).append(",").append(oldPrice).append(",").append(kc.getStealPrice()).append(",now(),");
			saveSql.append(kc.getDiligently()).append(",").append(kc.getApprove()).append(",").append(kc.getUpdateTextWordCount());
			saveSql.append(",").append(kc.getUpdateTextCount()).append(",").append(kc.getUpdateImageCount()).append(",");
			saveSql.append(kc.getUpdateVedioCount()).append(",").append(kc.getUpdateVedioLenght()).append(",").append(kc.getUpdateAudioCount());
			saveSql.append(",").append(kc.getUpdateAudioLenght()).append(",").append(kc.getUpdateVoteCount()).append(",");
			saveSql.append(kc.getUpdateTeaseCount()).append(",").append(kc.getUpdateDayCount()).append(",").append(kc.getReviewTextCountInApp()+kc.getReviewTextCountOutApp());
			saveSql.append(",").append(kc.getReviewTextWordCountInApp()+kc.getReviewTextWordCountOutApp()).append(")");
		}
		contentService.executeSql(saveSql.toString());
		
		StringBuilder saveHisSql = new StringBuilder();
		saveHisSql.append("insert into topic_price_his(topic_id,price,create_time)");
		saveHisSql.append(" values (").append(kc.getTopicId()).append(",").append(kc.getPrice());
		saveHisSql.append(",now())");
		contentService.executeSql(saveSql.toString());
		
		StringBuilder updatePriceSql = new StringBuilder();
		updatePriceSql.append("update topic set price=").append(kc.getPrice()).append(" where id=").append(kc.getTopicId());
		contentService.executeSql(saveSql.toString());
	}
}
