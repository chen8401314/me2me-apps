package com.me2me.mgmt.task.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.user.service.UserService;

@Component
public class PriceChangePushTask {

	private static final Logger logger = LoggerFactory.getLogger(PriceChangePushTask.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private LocalJdbcDao localJdbcDao;
	
	@Scheduled(cron="0 0 11 * * ?")
	public void doTask(){
		logger.info("王国价值升值/贬值推送任务开始");
		long s = System.currentTimeMillis();
		try{
			this.execute();
		}catch(Exception e){
			logger.error("王国价值升值/贬值推送任务出错", e);
		}
		long e = System.currentTimeMillis();
		logger.info("王国价值升值/贬值推送任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	private void execute(){
		Date now  = new Date();
		String dateStr = DateUtil.date2string(now, "yyyyMMdd");
		logger.info("当前需要推送的是["+dateStr+"]的待推送信息");
		
		StringBuilder needPushSearchSql = new StringBuilder();
		needPushSearchSql.append("select m.* from (select t.*,case when @preVal = t.uid then @curVal:=@curVal+1");
		needPushSearchSql.append(" when @preVal:=t.uid then @curVal:=1 end as rowno");
		needPushSearchSql.append(" from topic_price_push t,(select @preVal:=null,@curVal:=null) r");
		needPushSearchSql.append(" where t.date_code='").append(dateStr).append("'");
		needPushSearchSql.append(" and t.status=0");
		needPushSearchSql.append(" order by t.uid,t.kingdom_price desc,t.id) m");
		needPushSearchSql.append(" where m.rowno=1");
		
		List<Map<String, Object>> dataList = localJdbcDao.queryEvery(needPushSearchSql.toString());
		
		if(null != dataList && dataList.size() > 0){
			logger.info("共有["+dataList.size()+"]个需要推送的");
			List<Long> topicIds = new ArrayList<Long>();
			for(Map<String, Object> m : dataList){
				topicIds.add((Long)m.get("topic_id"));
			}
			StringBuilder topicSql = new StringBuilder();
			topicSql.append("select * from topic t where t.id in (");
			for(int i=0;i<topicIds.size();i++){
				if(i>0){
					topicSql.append(",");
				}
				topicSql.append(topicIds.get(i).toString());
			}
			topicSql.append(")");
			List<Map<String, Object>> topicList = localJdbcDao.queryEvery(topicSql.toString());
			Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
			if(null != topicList && topicList.size() > 0){
				for(Map<String, Object> t : topicList){
					topicMap.put(String.valueOf(t.get("id")), t);
				}
			}
			
			Map<String, Object> topic = null;
			JsonObject jsonObject = null;
			String msg = null;
			int total = 0;
			for(Map<String, Object> m : dataList){
				topic = topicMap.get(String.valueOf(m.get("topic_id")));
				if(null == topic){
					continue;
				}
				int type = (Integer)m.get("type");
				int changePrice = (Integer)m.get("change_price");
						
				if(type == 1){//升值
					msg = "恭喜！你的王国《"+(String)topic.get("title")+"》昨日升值了"+changePrice+"米汤币，今天也要保持啊！";
				}else{//贬值
					msg = "矮油可惜了！你的王国《"+(String)topic.get("title")+"》昨日贬值了"+changePrice+"米汤币，要加油啊。";
				}
				
				jsonObject = new JsonObject();
	            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
	            jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
	            jsonObject.addProperty("topicId", (Long)m.get("topic_id"));
	            jsonObject.addProperty("contentType", (Integer)topic.get("type"));
	            jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//这里是给国王的通知，所以直接显示核心圈即可
	            userService.pushWithExtra(String.valueOf(topic.get("uid")), msg, JPushUtils.packageExtra(jsonObject));
	            total++;
			}
			logger.info("实际推送了["+total+"]个");
			
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update topic_price_push set status=1 where date_code='").append(dateStr).append("'");
			localJdbcDao.executeSql(updateSql.toString());
		}
	}
}
