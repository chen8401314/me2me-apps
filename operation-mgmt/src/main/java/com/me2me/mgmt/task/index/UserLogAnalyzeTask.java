package com.me2me.mgmt.task.index;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.common.enums.USER_OPRATE_TYPE;
import com.me2me.common.utils.DateUtil;
import com.me2me.content.service.ContentService;
import com.me2me.live.model.TopicTagDetail;
import com.me2me.live.service.LiveService;
import com.me2me.user.service.UserService;
/**
 * 用户行为习惯分析
 * @author zhangjiwei
 * @date Apr 7, 2017
 */
@Component
public class UserLogAnalyzeTask{
	private static final Logger logger = LoggerFactory.getLogger(UserLogAnalyzeTask.class);
	
	@Autowired
	private ContentService contentService;
	@Autowired
	private LiveService liveService;
	@Autowired
	private UserService userService;
	
	@Scheduled(cron="0 30 2 * * ?")
	public void userIndexJob() {
		logger.info("开始分析用户喜好日志");
		countUserTagLikeDay();
		logger.info("分析用户喜好结束");
		
	}
	
	public void countUserTagLikeDay() {
		
		// 拿用户访问日志
		int pageSize=1000;
		Calendar cd = Calendar.getInstance();
		cd.setTime(new Date());
		cd.add(Calendar.DAY_OF_MONTH,-1);
		Date yesterday= cd.getTime();
		String day = DateUtil.date2string(yesterday, "yyyy-MM-dd");
		int skip=0;
		// load settings;
		Map<String,Integer> scoreMap = new HashMap<String, Integer>(){{
			for(USER_OPRATE_TYPE scoreKey:USER_OPRATE_TYPE.values()){
				String key = scoreKey.toString();
				Integer value = userService.getIntegerAppConfigByKey(key+"_SCORE");
				put(key, value==null?0:value);
			}
		}};
		Map<Long,TagScoreAnalyzer> userCounterMap=  new HashMap<>();
		while(true){
			List<Map<String,Object>> dataList =this.getUserVisitLogByDay(day,skip, pageSize);
			if(dataList.isEmpty()){
				break;
			}
			
			// 干活了。
			for(Map<String,Object> data:dataList){
				long uid =Long.parseLong(data.get("uid").toString());

				String action = data.get("action").toString();
				Integer score=scoreMap.get(action);				// 计分
				if(score==null){score=0;}
				
				TagScoreAnalyzer analyzer =userCounterMap.get(uid);
				if(analyzer==null){
					analyzer=new TagScoreAnalyzer();
					userCounterMap.put(uid, analyzer);
				}
				
				
				if(USER_OPRATE_TYPE.HIT_TAG.toString().equals(action)){	//标签点击情况特殊处理
					String extra = data.get("extra").toString();
					analyzer.addUserLog(extra, score);
				}else{		//王国情况
					Object strTopicId= data.get("topic_id");
					if(strTopicId!=null){
						long topicId = Long.parseLong(strTopicId.toString());
						List<TopicTagDetail> tags = this.liveService.getTopicTagDetailsByTopicId(topicId);		// 其实我想缓存，但是把所有王国的标签都查出来，估计会爆内存。
						for(TopicTagDetail detail:tags){
							analyzer.addUserLog(detail.getTag(), score);
						}
					}
				}
				
			}

			skip+=pageSize;
			try {
				Thread.sleep(1000);//防跑死数据库,其实没屌用
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}
		// 开始合并
		for(Map.Entry<Long, TagScoreAnalyzer> entry: userCounterMap.entrySet()){
			// 修改用户数据
			long uid = entry.getKey();
			Map<String,Integer> tagCountMap = entry.getValue().tagCountMap;
			for(String tag: tagCountMap.keySet()){
				int score = tagCountMap.get(tag);
				this.updateUserTagLike(uid,tag,score);
			}
		}
	}
    /**
     * 查用户访问日志
     * @author zhangjiwei
     * @date Aug 9, 2017
     * @param day
     * @param skip
     * @param limit
     * @return
     */
    public List<Map<String,Object>> getUserVisitLogByDay(String day,int skip,int limit){
    	String sql = "select * from user_visit_log where DATE_FORMAT(create_time,'%Y-%m-%d')=? limit ?,?";
		return contentService.queryForList(sql,day,skip,limit);
    }
    
    public boolean existsUserTagLike(long uid,String tag){
    	return contentService.queryForObject("select count(1) from user_tag_like where uid=? and tag=?",Integer.class,uid,tag)>0?true:false;
    }
    
	public void updateUserTagLike(long uid, String tag, int score) {
		if(existsUserTagLike(uid,tag)){
			contentService.update("update user_tag_like set score=score*0.9+? where uid=? and tag=? and last_update_time=now()",score,uid,tag);
		}else{
			contentService.update("insert into user_tag_like(uid,tag,score,last_update_time) values(?,?,?,now())",uid,tag,score);
		}
	}
}
