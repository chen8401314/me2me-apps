package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.utils.DateUtil;
import com.me2me.mgmt.dao.LocalJdbcDao;

@Controller
@RequestMapping("/task")
public class TaskConsoleController {

	private static final Logger logger = LoggerFactory.getLogger(TaskConsoleController.class);
	
	@Autowired
	private LocalJdbcDao localJdbcDao;
	
	@RequestMapping(value = "/ugcCollect")
	@ResponseBody
	public String ugcCollect() {
		logger.info("开始进行UGC编入情绪王国整理");
		long s = System.currentTimeMillis();
		String returnMsg = "执行完成";
		try{
			StringBuilder countSql = new StringBuilder();
			countSql.append("select count(DISTINCT t.uid) as cc from content t where t.type=0");
			countSql.append(" and t.status=0 and t.rights=1 and t.ugc_status=0");
			List<Map<String, Object>> countList = localJdbcDao.queryEvery(countSql.toString());
			int total = 0;
			if(null != countList && countList.size() > 0){
				Map<String, Object> count = countList.get(0);
				total = ((Long)count.get("cc")).intValue();
			}
			logger.info("共["+total+"]个用户的UGC需要整理");
			
			StringBuilder ugcUserSqlBuilder = new StringBuilder();
			ugcUserSqlBuilder.append("select t.uid from content t where t.type=0 and t.status=0 and t.rights=1");
			ugcUserSqlBuilder.append(" and t.ugc_status=0 group by t.uid order by t.uid");
			String ugcUserSql = ugcUserSqlBuilder.toString();
			
			int start = 0;
			int batch = 200;
			int runCount = 0;
			List<Map<String, Object>> userList = null;
			while(start < total){
				userList = localJdbcDao.queryEvery(ugcUserSql + " limit " + start + "," + batch);
				if(null == userList || userList.size() == 0){
					break;
				}
				for(Map<String, Object> u : userList){
					long uid = (Long)u.get("uid");
					this.ugcCollectExce(uid);
				}
				runCount = runCount + userList.size();
				logger.info("本次处理了["+userList.size()+"]个用户，共["+total+"]个用户，还剩["+(total-runCount)+"]个用户");
				start = start + batch;
			}
		}catch(Exception e){
			logger.error("任务执行失败", e);
			returnMsg = "执行失败";
		}
		long e = System.currentTimeMillis();
		logger.info("UGC编入情绪王国整理完成，共耗时["+(e-s)/1000+"]秒");
		
		return returnMsg;
	}
	
	private void ugcCollectExce(long uid){
		String ugcListSql = "select t.id,t.title,t.conver_image,t.content from content t where t.type=0 and t.status=0 and t.rights=1  and t.ugc_status=0 and t.uid="+uid+" order by id";
		List<Map<String, Object>> contentList = localJdbcDao.queryEvery(ugcListSql);
		if(null != contentList && contentList.size() > 0){
			//先判断是否有情绪王国，如果没有则创建
			String userEmotionTopicSql = "select * from topic t where t.sub_type=1 and t.uid="+uid;
			List<Map<String, Object>> topicList = localJdbcDao.queryEvery(userEmotionTopicSql);
			long userEmotionTopicId = 0;
			if(null != topicList && topicList.size() > 0){//有
				Map<String, Object> userEmotionTopic = topicList.get(0);
				userEmotionTopicId = (Long)userEmotionTopic.get("id");
			}else{//没有，则创建
				
				
			}
			
			
		}
		
		
	}
	
	private long createUserEmotionTopic(long uid){
		String userProfileSql = "select u.uid,u.nick_name from user_profile u where u.uid="+uid;
		List<Map<String, Object>> userList = localJdbcDao.queryEvery(userProfileSql);
		String nickName = null;
		if(null != userList && userList.size() > 0){
			Map<String, Object> userProfile = userList.get(0);
			nickName = (String)userProfile.get("nick_name");
		}
		if(StringUtils.isEmpty(nickName)){
			logger.info("用户["+uid+"]不存在");
			return 0;
		}
		if (nickName.matches("用户\\d+.*")) {
			nickName = "我";
		}
		String title = nickName + "的生活记录";
		
		String topicCoverImage = null;
		String topicCoverSql = "select pic, RAND() rd from topic_preset_pic order by rd desc limit 1";
		List<Map<String, Object>> topicCoverList = localJdbcDao.queryEvery(topicCoverSql);
		if(null != topicCoverList && topicCoverList.size() > 0){
			Map<String, Object> topicCover = topicCoverList.get(0);
			topicCoverImage = (String)topicCover.get("pic");
		}
		if(StringUtils.isEmpty(topicCoverImage)){
			logger.info("没有王国封面图片");
			return 0;
		}
		
		String summary = "吃喝玩乐，记录我的日常。";
		long nowTime = System.currentTimeMillis();
		//创建topic表
		StringBuilder insertTopicSql = new StringBuilder();
		insertTopicSql.append("insert into topic(uid,live_image,title,status,create_time,update_time,long_time,qrcode,core_circle,type,ce_audit_type,ac_audit_type,ac_publish_type,rights,summary,sub_type,price,listing_time)");
		insertTopicSql.append(" values(").append(uid).append(",'").append(topicCoverImage).append("','").append(title).append("',0,now(),now(),").append(nowTime);
		insertTopicSql.append(",'','[").append(uid).append("]',0,0,1,0,1,'").append(summary).append("',1,0,null)");
		localJdbcDao.executeSql(insertTopicSql.toString());
		//创建content表
		long topicId = 0;
		String topicIdSql = "select t.id from topic t where t.sub_type=1 and t.uid="+uid;
		List<Map<String, Object>> topicIdSearchList = localJdbcDao.queryEvery(topicIdSql);
		if(null != topicIdSearchList && topicIdSearchList.size() > 0){
			Map<String, Object> topicIdSearch = topicIdSearchList.get(0);
			topicId = (Long)topicIdSearch.get("id");
		}
		if(topicId <= 0){
			logger.info("保存失败了？？？");
			return 0;
		}
		StringBuilder insertContentSql = new StringBuilder();
		
		
		
		return 0;
	}
}
