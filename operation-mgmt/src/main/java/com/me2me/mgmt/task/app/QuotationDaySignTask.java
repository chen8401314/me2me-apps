package com.me2me.mgmt.task.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.JPushUtils;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.user.service.UserService;

@Component
public class QuotationDaySignTask {

	private static final Logger logger = LoggerFactory.getLogger(QuotationDaySignTask.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private LocalJdbcDao localJdbcDao;
	
	
	@Scheduled(cron="0 0 9 * * ?")
	public void doTask(){
		logger.info("语录日签推送任务开始");
		long s = System.currentTimeMillis();
		try{
			String msg="";
			JsonObject jsonObject = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuilder userSql = new StringBuilder();
			userSql.append("select uid from user where disable_user=0");
			List<Map<String,Object>> userList = localJdbcDao.queryEvery(userSql.toString());
			String yesterDayStr =CommonUtils.getCalculationDayStr(-1, "yyyy-MM-dd");
			for (Map<String, Object> user : userList) {
				try{
				String uid = user.get("uid").toString();
				StringBuilder textSql = new StringBuilder();
				textSql.append("select t.id,t.create_time FROM topic_fragment t,topic tp WHERE t.status = 1  AND   t.topic_id = tp.id AND  ((t.type=0 AND t.content_type = 0) OR (t.TYPE=1 AND t.content_type = 0)) ");
				textSql.append(" AND t.fragment <> tp.summary ");
				textSql.append(" AND t.create_time <='").append(yesterDayStr).append(" 23:59:59'");
				textSql.append(" AND t.uid = ").append(uid);
				textSql.append("  ORDER BY t.create_time DESC LIMIT 1 ");
				List<Map<String,Object>> textList = localJdbcDao.queryEvery(textSql.toString());
				String textCreateTime = "";
				if(textList!=null &&textList.size()>0){
					textCreateTime = textList.get(0).get("create_time").toString();
				}
				
				StringBuilder imgSql = new StringBuilder();
				imgSql.append("select t.* FROM topic_fragment t WHERE  t.status = 1  AND  ");
				imgSql.append(" ((t.type=0 AND t.content_type = 1) OR (t.type=1 AND t.content_type = 1) OR (t.type=51 AND t.content_type = 51))  ");
				imgSql.append(" AND t.create_time <='").append(yesterDayStr).append(" 23:59:59'");
				imgSql.append(" AND fn_Json_getKeyValue(t.extra,1,'w')>300  AND (fn_Json_getKeyValue(t.extra,1,'imgType') IS  NULL  OR fn_Json_getKeyValue(t.extra,1,'imgType') <> 1)");
				imgSql.append(" AND t.uid = ").append(uid);
				imgSql.append("  ORDER BY t.create_time DESC LIMIT 1 ");
				List<Map<String,Object>> imgList = localJdbcDao.queryEvery(imgSql.toString());
				String imgCreateTime = "";
				if(imgList!=null && imgList.size()>0){
					imgCreateTime = imgList.get(0).get("create_time").toString();
				}
				//没有日签
				if(StringUtils.isEmpty(textCreateTime) && StringUtils.isEmpty(imgCreateTime)){
					continue;
				}
				String finalTime = "";
				if(StringUtils.isEmpty(textCreateTime)){
					finalTime = imgCreateTime;
				}
				if(StringUtils.isEmpty(imgCreateTime)){
					finalTime = textCreateTime;
				}
				
				if(!StringUtils.isEmpty(textCreateTime) && !StringUtils.isEmpty(imgCreateTime)){
					if(sdf.parse(textCreateTime).getTime()>sdf.parse(imgCreateTime).getTime()){
						finalTime = textCreateTime;
					}else{
						finalTime = imgCreateTime;
					}
				}
				int day = DateUtil.daysBetween(finalTime, sdf.format(new Date()));
				if(day==1 || day ==2 || day ==7 || day==15 ||day==30){
					if(day==1){
						msg = "你的今日语录已经生成，赶紧瞅一眼你的妙语连珠吧。";
					}else{
						msg="给低调的你推荐一位伶牙俐齿的小伙伴，看TA侃侃而谈的语录打动你了吗？";
					}
				jsonObject = new JsonObject();
	            jsonObject.addProperty("messageType", 0);
	            jsonObject.addProperty("type", -1);
	            userService.pushWithExtra(uid, msg, JPushUtils.packageExtra(jsonObject));
				}
				}catch(Exception e){
					continue;
				}
			}
			
		}catch(Exception e){
			logger.error("语录日签推送任务出错", e);
		}
		long e = System.currentTimeMillis();
		logger.info("语录日签推送任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	
}
