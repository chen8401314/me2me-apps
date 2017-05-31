package com.me2me.mgmt.task.app;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.common.utils.DateUtil;
import com.me2me.content.service.ContentService;

@Component
public class TestStatTask {

	private static final Logger logger = LoggerFactory.getLogger(TestStatTask.class);
	
	@Autowired
	private ContentService contentService;
	
//	@Scheduled(cron="0 40 16 * * ?")
	public void doTask(){
		logger.info("数据导出任务开始...");
		long start = System.currentTimeMillis();
		try{
			this.exec();
		}catch(Exception e){
			logger.error("数据导出任务失败", e);
		}
		long end = System.currentTimeMillis();
		logger.info("数据导出任务结束，共耗时["+(end-start)/1000+"]秒");
	}
	
	private void exec() throws Exception{
		logger.info("查询有多少个王国符合要求需要统计...");
		//先查出来具体哪些个王国需要导出来
		//15天（ >=15）有更新（在左边显示的数据）的王国
		StringBuilder searchTopicSql = new StringBuilder();
		searchTopicSql.append("select m.topic_id, count(DISTINCT m.datestr) as cnum, max(m.datestr) as maxdate");
		searchTopicSql.append(" from (select f.topic_id,DATE_FORMAT(f.create_time,'%Y%m%d') as datestr");
		searchTopicSql.append(" from topic_fragment f,topic t where f.topic_id=t.id");
		searchTopicSql.append(" and (f.type in (0,3,11,12,13,15,52,55) or");
		searchTopicSql.append(" (f.type=54 and FIND_IN_SET(f.uid,SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))))");
		searchTopicSql.append(" and f.status=1) m group by m.topic_id having cnum>14");
		List<Map<String, Object>> topicList = contentService.queryEvery(searchTopicSql.toString());
		logger.info("查询有多少个王国符合要求需要统计完成，共["+((null!=topicList&&topicList.size()>0)?topicList.size():0)+"]个王国");
		
		List<Long> topicIdList = new ArrayList<Long>();
		if(null != topicList && topicList.size() > 0){
			Long topicId = null;
			for(Map<String, Object> m : topicList){
				topicId = (Long)m.get("topic_id");
				if(!topicIdList.contains(topicId)){
					topicIdList.add(topicId);
				}
			}
		}
		
		if(topicIdList.size() == 0){
			logger.info("没有需要统计的王国，直接退出统计");
			return;
		}
		
		logger.info("查询王国基本信息");
		Map<String, Map<String, Object>> topicInfoMap = new HashMap<String, Map<String, Object>>();
		StringBuilder topicInfoSql = new StringBuilder();
		topicInfoSql.append("select * from topic t where t.id in (");
		for(int i=0;i<topicIdList.size();i++){
			if(i>0){
				topicInfoSql.append(",");
			}
			topicInfoSql.append(topicIdList.get(i));
		}
		topicInfoSql.append(")");
		List<Map<String, Object>> topicInfoList = contentService.queryEvery(topicInfoSql.toString());
		logger.info("查询王国基本信息完成，共有["+((null!=topicInfoList&&topicInfoList.size()>0)?topicInfoList.size():0)+"]个王国");
		
		if(null == topicInfoList || topicInfoList.size() == 0){
			logger.info("王国信息都消失了。。直接退出统计");
			return;
		}
		
		logger.info("查询王国Content属性");
		StringBuilder topicContentSql = new StringBuilder();
		topicContentSql.append("select * from content c where c.type=3 and c.forward_cid in (");
		for(int i=0;i<topicIdList.size();i++){
			if(i>0){
				topicContentSql.append(",");
			}
			topicContentSql.append(topicIdList.get(i));
		}
		topicContentSql.append(")");
		List<Map<String, Object>> topicContentList = contentService.queryEvery(topicContentSql.toString());
		Map<String, Map<String, Object>> topicContentMap = new HashMap<String, Map<String, Object>>();
		for(Map<String, Object> m : topicContentList){
			topicContentMap.put(String.valueOf(m.get("forward_cid")), m);
		}
		logger.info("查询王国Content属性完成");
		
		List<Long> kingUidList = new ArrayList<Long>();
		Long kingUid = null;
		for(Map<String, Object> m : topicInfoList){
			topicInfoMap.put(String.valueOf(m.get("id")), m);
			
			kingUid = (Long)m.get("uid");
			if(!kingUidList.contains(kingUid)){
				kingUidList.add(kingUid);
			}
		}
		
		logger.info("查询国王属性");
		Map<String, Map<String, Object>> kingMap = new HashMap<String, Map<String, Object>>();
		StringBuilder kingSql = new StringBuilder();
		kingSql.append("select * from user_profile u where u.uid in (");
		for(int i=0;i<kingUidList.size();i++){
			if(i>0){
				kingSql.append(",");
			}
			kingSql.append(kingUidList.get(i));
		}
		kingSql.append(")");
		List<Map<String, Object>> kingList = contentService.queryEvery(kingSql.toString());
		for(Map<String, Object> m : kingList){
			kingMap.put(String.valueOf(m.get("uid")), m);
		}
		logger.info("查询国王属性完成");
		
		logger.info("查询王国成员数");
		Map<String, Long> topicMemberMap = this.getTopicMembersCount(topicIdList);
		if(null == topicMemberMap){
			topicMemberMap = new HashMap<String, Long>();
		}
		logger.info("查询王国成员数完成");
		
		//开始轮询统计
		logger.info("开始统计");
		List<DataItem> result = new ArrayList<DataItem>();
		
		DataItem item = null;
		Map<String, Object> topicInfo = null;
		Map<String, Object> king = null;
		Map<String, Object> topicContent = null;
		Long topicMember = null;
		List<String> coreTextList = null;
		List<String> guestTextList = null;
		List<String> vedioList = null;
		List<String> audioList = null;
		Map<String, Object> coreCount = null;
		Map<String, Object> guestCount = null;
		JSONObject obj = null;
		String lastDayStr = null;
		int c = 0;
		for(Map<String, Object> m : topicList){
			item = new DataItem();
			//基本属性
			topicInfo = topicInfoMap.get(String.valueOf(m.get("topic_id")));
			if(null == topicInfo){
				continue;
			}
			item.setTopicName((String)topicInfo.get("title"));
			item.setTopicId((Long)topicInfo.get("id"));
			king = kingMap.get(String.valueOf(topicInfo.get("uid")));
			if(null == king){
				continue;
			}
			item.setKingName((String)king.get("nick_name"));
			item.setKingUid((Long)king.get("uid"));
			item.setKingVlv((Integer)king.get("v_lv"));
			item.setDateStr(DateUtil.date2string((Date)topicInfo.get("create_time"), "yyyy-MM-dd HH:mm:ss"));
			item.setDayCount((int)DateUtil.getDaysBetween2Date((Date)topicInfo.get("create_time"), DateUtil.string2date("20170531", "yyyyMMdd")));
			topicContent = topicContentMap.get(String.valueOf(m.get("topic_id")));
			if(null == topicContent){
				continue;
			}
			item.setReadCount((Integer)topicContent.get("read_count_dummy"));
			item.setRealReadCount((Integer)topicContent.get("read_count"));
			topicMember = topicMemberMap.get(String.valueOf(m.get("topic_id")));
			if(null != topicMember && topicMember.intValue() > 0){
				item.setFavouriteCount(topicMember.intValue());
			}else{
				item.setFavouriteCount(0);
			}
			//国王+核心圈
			coreCount = this.getCoreCount((Long)m.get("topic_id"));
			if(null == coreCount){
				item.setCoreTextCount(0);
				item.setCoreVedioCount(0);
				item.setCoreAudioCount(0);
				item.setCoreImgCount(0);
				item.setCoreBigEmojiCount(0);
				item.setCoreMiddleEmojiCount(0);
				item.setCoreTeaseCount(0);
			}else{
				item.setCoreTextCount(((Long)coreCount.get("textCount")).intValue());
				item.setCoreVedioCount(((Long)coreCount.get("vedioCount")).intValue());
				item.setCoreAudioCount(((Long)coreCount.get("audioCount")).intValue());
				item.setCoreImgCount(((Long)coreCount.get("imgCount")).intValue());
				item.setCoreBigEmojiCount(((Long)coreCount.get("bigEmojiCount")).intValue());
				item.setCoreMiddleEmojiCount(((Long)coreCount.get("middleEmojiCount")).intValue());
				item.setCoreTeaseCount(((Long)coreCount.get("teaseCount")).intValue());
			}
			//文字字数
			coreTextList = this.getTextFragmentByTopicId((Long)m.get("topic_id"), true);
			int coreTextWordCount = 0;
			if(null != coreTextList && coreTextList.size() > 0){
				for(String t : coreTextList){
					coreTextWordCount = coreTextWordCount + t.length();
				}
				
			}
			item.setCoreTextWordCount(coreTextWordCount);
			//视频时长
			vedioList = this.getExtraByTypeAndTopicId(12, (Long)m.get("topic_id"));
			int vedioDuration = 0;
			if(null != vedioList && vedioList.size() > 0){
				for(String v : vedioList){
					obj = JSON.parseObject(v);
					if(null != obj.get("duration")){
						int d = obj.getIntValue("duration");
						vedioDuration = vedioDuration + d;
					}
				}
			}
			item.setCoreVedioDuration(vedioDuration);
			//音频时长
			audioList = this.getExtraByTypeAndTopicId(13, (Long)m.get("topic_id"));
			int audioDuration = 0;
			if(null != audioList && audioList.size() > 0){
				for(String a : audioList){
					obj = JSON.parseObject(a);
					if(null != obj.get("duration")){
						int d = obj.getIntValue("duration");
						audioDuration = audioDuration + d;
					}
				}
			}
			item.setCoreAudioDuration(audioDuration);
			item.setCoreTotalDayCount(((Long)m.get("cnum")).intValue());
			lastDayStr = (String)m.get("maxdate");
			item.setCoreLastDayCount((int)DateUtil.getDaysBetween2Date(DateUtil.string2date(lastDayStr, "yyyyMMdd"), DateUtil.string2date("20170531", "yyyyMMdd")));
			
			//非国王核心圈
			guestCount = this.getGuestCount((Long)m.get("topic_id"));
			if(null != guestCount){
				item.setReviewCount(((Long)guestCount.get("textCount")).intValue());
				item.setFootCount(((Long)guestCount.get("footCount")).intValue());
				item.setTeaseCount(((Long)guestCount.get("teaseCount")).intValue());
				item.setWeixinReviewCount(((Long)guestCount.get("weixinCount")).intValue());
			}else{
				item.setReviewCount(0);
				item.setFootCount(0);
				item.setTeaseCount(0);
				item.setWeixinReviewCount(0);
			}
			item.setTotalReviewDayCount(this.getGuestDayCount((Long)m.get("topic_id")));
			guestTextList = this.getTextFragmentByTopicId((Long)m.get("topic_id"), false);
			int guestTextWordCount = 0;
			if(null != guestTextList && guestTextList.size() > 0){
				for(String t : guestTextList){
					guestTextWordCount = guestTextWordCount + t.length();
				}
			}
			item.setReviewWordCount(guestTextWordCount);
			result.add(item);
			c++;
			if(c%10 == 0){
				logger.info("统计 process " + c);
			}
		}
		logger.info("统计 process " + c);
		logger.info("统计完成，共统计出["+result.size()+"]个内容");
		
		logger.info("开始写文件");
//		String filePath = "/root/tmp/data.csv";
		String filePath = "d:\\test\\data.csv";
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "GBK"));
			bw.append("王国名,王国ID,用户名,用户ID,是否大v,建立时间,建立天数,实际阅读量,虚拟阅读量,订阅数,更新文字(字数),更新文字(条数),更新视频(条数),更新视频(时长),更新语音(条数),更新语音(时长),更新图片(条数),更新大表情,更新中号表情,更新逗一逗,总更新天数,最后一次更新到5.31号间隔的天数,评论(条数),评论(字数),评论足迹(条数),逗一逗,评论天数,APP外回复数\n");
			for(DataItem d : result){
				bw.append(genString(d.getTopicName())).append(",").append(genString(String.valueOf(d.getTopicId()))).append(",");
				bw.append(genString(d.getKingName())).append(",").append(genString(String.valueOf(d.getKingUid()))).append(",");
				bw.append(genString(String.valueOf(d.getKingVlv()))).append(",").append(genString(d.getDateStr())).append(",");
				bw.append(genString(String.valueOf(d.getDayCount()))).append(",").append(genString(String.valueOf(d.getRealReadCount()))).append(",");
				bw.append(genString(String.valueOf(d.getReadCount()))).append(",").append(genString(String.valueOf(d.getFavouriteCount()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreTextWordCount()))).append(",").append(genString(String.valueOf(d.getCoreTextCount()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreVedioCount()))).append(",").append(genString(String.valueOf(d.getCoreVedioDuration()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreAudioCount()))).append(",").append(genString(String.valueOf(d.getCoreAudioDuration()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreImgCount()))).append(",").append(genString(String.valueOf(d.getCoreBigEmojiCount()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreMiddleEmojiCount()))).append(",").append(genString(String.valueOf(d.getTeaseCount()))).append(",");
				bw.append(genString(String.valueOf(d.getCoreTotalDayCount()))).append(",").append(genString(String.valueOf(d.getCoreLastDayCount()))).append(",");
				bw.append(genString(String.valueOf(d.getReviewCount()))).append(",").append(genString(String.valueOf(d.getReviewWordCount()))).append(",");
				bw.append(genString(String.valueOf(d.getFootCount()))).append(",").append(genString(String.valueOf(d.getTeaseCount()))).append(",");
				bw.append(genString(String.valueOf(d.getTotalReviewDayCount()))).append(",").append(genString(String.valueOf(d.getWeixinReviewCount())));
				bw.append("\n");
			}
			bw.flush();
		}catch(Exception e){
			logger.error("写文件失败", e);
		}finally{
			if(null != bw){
				bw.close();
			}
		}
		logger.info("写文件结束");
	}
	
	@Data
	public class DataItem{
		private String topicName;
		private long topicId;
		private String kingName;
		private long kingUid;
		private int kingVlv;
		private String dateStr;
		private int dayCount;
		private long realReadCount;
		private long readCount;
		private int favouriteCount;
		
		//国王+核心圈
		private int coreTextWordCount;//字数
		private int coreTextCount;//条数
		private int coreVedioCount;//条数
		private int coreVedioDuration;//时长
		private int coreAudioCount;
		private int coreAudioDuration;
		private int coreImgCount;
		private int coreBigEmojiCount;
		private int coreMiddleEmojiCount;
		private int coreTeaseCount;
		private int coreTotalDayCount;
		private int coreLastDayCount;
		
		//非国王核心圈
		private int reviewCount;
		private int reviewWordCount;
		private int footCount;
		private int TeaseCount;
		private int totalReviewDayCount;
		private int weixinReviewCount;
	}
	
	private String genString(String str){
		if(null == str){
			return "\"\"";
		}else{
			return "\""+str+"\"";
		}
	}
	
	private Map<String, Object> getGuestCount(long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(if(f.type in (1,10), TRUE, NULL)) as textCount,");
		sb.append("count(if(f.type=51 and f.content_type=16, TRUE, NULL)) as footCount,");
		sb.append("count(if(f.type=51 and f.content_type=20, TRUE, NULL)) as teaseCount,");
		sb.append("count(if(f.type=1 and f.source=3, TRUE, NULL)) as weixinCount");
		sb.append(" from topic_fragment f where f.status=1 and f.topic_id=").append(topicId);
			
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	private int getGuestDayCount(long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(DISTINCT m.datestr) as reviewDayCount");
		sb.append(" from (select DATE_FORMAT(f.create_time,'%Y%m%d') as datestr");
		sb.append(" from topic_fragment f,topic t where f.topic_id=t.id");
		sb.append(" and f.status=1 and f.topic_id=").append(topicId);
		sb.append(" and f.type not in (0,3,11,12,13,15,52,55)");
		sb.append(" and (f.type!=54 or not FIND_IN_SET(f.uid,SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2)))) m");
		
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			Map<String, Object> c = list.get(0);
			return ((Long)c.get("reviewDayCount")).intValue();
		}
		return 0;
	}
	
	private Map<String, Object> getCoreCount(long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(if((f.type=0 and f.content_type=0) or f.type in (11, 15), TRUE, NULL)) as textCount,");
		sb.append("count(if(f.type=12, TRUE, NULL)) as vedioCount,");
		sb.append("count(if(f.type=13, TRUE, NULL)) as audioCount,");
		sb.append("count(if(f.type=0 and f.content_type=1, TRUE, NULL)) as imgCount,");
		sb.append("count(if(f.type=52 and f.content_type=18, TRUE, NULL)) as bigEmojiCount,");
		sb.append("count(if(f.type=52 and f.content_type=17, TRUE, NULL)) as middleEmojiCount,");
		sb.append("count(if(f.type=52 and f.content_type=20, TRUE, NULL)) as teaseCount");
		sb.append(" from topic_fragment f where f.status=1 and f.topic_id=").append(topicId);
		
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	private List<String> getExtraByTypeAndTopicId(int type, long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic_fragment f where f.status=1");
		sb.append(" and f.type=").append(type).append(" and f.topic_id=").append(topicId);
		
		List<String> result = new ArrayList<String>();
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				if(!StringUtils.isEmpty((String)m.get("extra"))){
					result.add((String)m.get("extra"));
				}
			}
		}
		return result;
	}
	
	private List<String> getTextFragmentByTopicId(long topicId, boolean isCore){
		StringBuilder sb = new StringBuilder();
		
		if(isCore){
			sb.append("select * from topic_fragment f where f.status=1");
			sb.append(" and ((f.type=0 and f.content_type=0) or f.type in (11, 15))");
			sb.append(" and f.topic_id=").append(topicId);
		}else{
			sb.append("select * from topic_fragment f where f.status=1");
			sb.append(" and f.type in (1,10) and f.topic_id=").append(topicId);
		}
		
	    List<String> result = new ArrayList<String>();
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			String fragment = null;
			for(Map<String, Object> m : list){
				int type = (Integer)m.get("type");
				fragment = (String)m.get("fragment");
				if(!StringUtils.isEmpty(fragment)){
					if(type == 0 || type == 1){
						result.add(fragment.trim());
					} else if(type == 11 || type == 15 || type == 10){
						if(fragment.startsWith("{")){
							JSONObject obj = JSON.parseObject(fragment);
							result.add(obj.getString("text"));
						}else{
							result.add(fragment.trim());
						}
					}
				}
				
			}
		}
		return result;
	}
	
	private Map<String, Long> getTopicMembersCount(List<Long> topicIdList){
		Map<String, Long> result = new HashMap<String, Long>();
    	if(null == topicIdList || topicIdList.size() == 0){
    		return result;
    	}
    	//查询非核心圈成员
    	StringBuilder sb = new StringBuilder();
    	sb.append("select f.topic_id,count(1) cc from live_favorite f,topic t");
    	sb.append(" where f.topic_id=t.id ");
    	sb.append(" and not FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
    	sb.append(" and f.topic_id in (");
    	for(int i=0;i<topicIdList.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIdList.get(i));
    	}
    	sb.append(") group by f.topic_id");
    	List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> m : list){
    			result.put(String.valueOf(m.get("topic_id")), (Long)m.get("cc"));
    		}
    	}
    	//查询核心圈成员
    	StringBuilder sb2 = new StringBuilder();
    	sb2.append("select t.id, LENGTH(t.core_circle)-LENGTH(replace(t.core_circle,',','')) as coreCount");
    	sb2.append(" from topic t where t.id in (");
    	for(int i=0;i<topicIdList.size();i++){
    		if(i>0){
    			sb2.append(",");
    		}
    		sb2.append(topicIdList.get(i));
    	}
    	sb2.append(")");
    	List<Map<String, Object>> list2 = contentService.queryEvery(sb2.toString());
    	if(null != list2 && list2.size() > 0){
    		Long count = null;
    		Long coreCount = null;
    		for(Map<String, Object> m : list2){
    			coreCount = (Long)m.get("coreCount");
    			if(coreCount > 0){
    				count = result.get(String.valueOf(m.get("id")));
    				if(null == count){
    					count = coreCount;
    				}else{
    					count = count.longValue() + coreCount.longValue();
    				}
    				result.put(String.valueOf(m.get("id")), count);
    			}
    		}
    	}
    	
    	return result;
    }
}
