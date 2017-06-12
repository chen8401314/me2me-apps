package com.me2me.mgmt.task.app;

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
			this.execute();
		}catch(Exception e){
			logger.error("王国价值任务出错", e);
		}
		long e = System.currentTimeMillis();
		logger.info("王国价值任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	private void execute(){
		//查看执行方式(全量 or 增量)
		boolean isFull = false;
		String mode = userService.getAppConfigByKey("PRICE_TASK_MODE");
		if(StringUtils.isNotBlank(mode) && "1".equals(mode)){
			isFull = true;
		}
		logger.info("本次执行任务为["+(isFull?"全量":"增量")+"]方式");
		
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
		String startTime = null;
		if(isFull){//全量，则从2016-01-01 00:00:00开始
			startTime = "2016-01-01 00:00:00";
		}else{//增量
			startTime = yesterday + " 00:00:00";
		}
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
		
		private int diligently = 0;//用心度
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
		private int updateFrequency = 0;//频度
		
		private int approve = 0;//认可度权重
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
		
		private int abilityValue = 0;//能力值
		
		private int noUpdateDayCount = 0;//最后一次更新到当前的天数
		private int decay = 0;//衰减值
		
		
		//额外需要记录的参数
		private Date lastUpdateTime;
	}
}
