package com.me2me.mgmt.task.index;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.common.enums.USER_OPRATE_TYPE;
import com.me2me.common.utils.DateUtil;
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
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private LiveService liveService;
	@Autowired
	private UserService userService;
	private Map<Long,TagInfo> tagCache;
	
	private Map<Long,List<TopicTagDetail>> kingdomTagCache;
	
	public class TagInfo{
		public Long id;
		public Long pid;
		public String tag;
		public Integer is_sys;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getPid() {
			return pid;
		}
		public void setPid(Long pid) {
			this.pid = pid;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public Integer getIs_sys() {
			return is_sys;
		}
		public void setIs_sys(Integer is_sys) {
			this.is_sys = is_sys;
		}
		@Override
		public String toString() {
			return "TagInfo [id=" + id + ", pid=" + pid + ", tag=" + tag + ", is_sys=" + is_sys + "]";
		}
		
	}
	@Scheduled(cron="0 30 2 * * ?")
	public void userIndexJob() {
		logger.info("开始分析用户喜好日志");
		countUserTagLikeDay();
		logger.info("分析用户喜好结束");
		
	}
	public void loadTagCache(){
		tagCache= new HashMap<>();
		List<Map<String,Object>> tagList = jdbcTemplate.queryForList("select id,pid,tag,is_sys from topic_tag where status=0");
		for(Map<String, Object> tag:tagList){
			Long key= Long.parseLong(tag.get("id").toString());
			TagInfo tagInfo= new TagInfo();
			try {
				BeanUtils.populate(tagInfo, tag);
				tagCache.put(key,tagInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		kingdomTagCache=new HashMap<>();
		
	}
	/**
	 * 递归获取父标签。如果自身没有pid则返回自身。
	 * @author zhangjiwei
	 * @date Aug 17, 2017
	 * @param tagId
	 * @return
	 */
	private String getRootParentTag(Long tagId){
		TagInfo info = tagCache.get(tagId);
		if(info!=null){
			if(info.pid==0 && info.is_sys==1){
				return info.tag;
			}
			if(info.pid!=0){
				return getRootParentTag(info.pid);
			}
		}
		return null;
	}
	private TagInfo getTagByName(String tag){
		if(tag!=null){
			for(TagInfo info:tagCache.values()){
				if(info.tag.equals(tag)){
					return info;
				}
			}
		}
		return null;
	}
	public void countUserTagLikeDay() {
		// 缓存系统标签表
		loadTagCache();
		
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
			logger.info("分析用户访问日志：{} ,data pos:{} size:{}",day,skip);
			if(dataList.isEmpty()){
				break;
			}
			
			// 干活了。
			for(Map<String,Object> data:dataList){
				//logger.info("日志=>{}",data);
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
					String extra = (String)data.get("extra");
					TagInfo detail = this.getTagByName(extra);
					if(detail!=null){
						String parentTag = getRootParentTag(detail.id);
						if(parentTag!=null){
							analyzer.addUserLog(parentTag, score);
						}
					}
				}else{		//王国情况
					Object strTopicId= data.get("topic_id");
					if(strTopicId!=null){
						Long topicId = Long.parseLong(strTopicId.toString());
						List<TopicTagDetail> tags = kingdomTagCache.get(topicId);
						if(tags==null){
							tags = this.liveService.getTopicTagDetailsByTopicId(topicId);		//   还是缓存吧~~
							kingdomTagCache.put(topicId, tags);
						}
						
						for(TopicTagDetail detail:tags){
							String parentTag = getRootParentTag(detail.getTagId());
							if(parentTag!=null){
								analyzer.addUserLog(parentTag, score);
							}
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
    	String sql = "select uid,action,topic_id,extra from user_visit_log where DATE_FORMAT(create_time,'%Y-%m-%d')=? limit ?,?";
		return jdbcTemplate.queryForList(sql,day,skip,limit);
    }
    
    public boolean existsUserTagLike(long uid,String tag){
    	return jdbcTemplate.queryForObject("select count(1) from user_tag_like where uid=? and tag=?",Integer.class,uid,tag)>0?true:false;
    }
    
	public void updateUserTagLike(long uid, String tag, int score) {
		if(existsUserTagLike(uid,tag)){
			jdbcTemplate.update("update user_tag_like set score=score*0.9+?,last_update_time=now() where uid=? and tag=?",score,uid,tag);
		}else{
			jdbcTemplate.update("insert into user_tag_like(uid,tag,score,last_update_time) values(?,?,?,now())",uid,tag,score);
		}
	}
}
