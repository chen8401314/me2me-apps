package com.me2me.mgmt.task.billboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.content.model.BillBoardList;
import com.me2me.content.service.ContentService;

@Component("lonelyKingdomBillboardTask")
public class LonelyKingdomBillboardTask {

	private static final Logger logger = LoggerFactory.getLogger(LonelyKingdomBillboardTask.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	
	public void doTask(){
		logger.info("[求安慰的孤独王国]榜单任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("[求安慰的孤独王国]榜单任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("[求安慰的孤独王国]榜单任务结束，共耗时"+(e-s)/1000+"秒");
	}

	private void execTask(){
		StringBuilder sb = new StringBuilder();
		sb.append("select m.topic_id,POW(m.ucount,2)-if(m.rcount>0,POW(m.rcount,2),0) as sinceId");
		sb.append(" from content c  LEFT JOIN topic t ON c.forward_cid = t.id,(select f.topic_id,");
		sb.append("count(if(f.type in (0,11,12,13,15,52,55),TRUE,NULL)) as ucount,");
		sb.append("count(if(f.type in (0,11,12,13,15,52,55),NULL,TRUE)) as rcount");
		sb.append(" from topic_fragment f where f.status=1");
		sb.append(" and f.create_time>date_add(now(), interval -3 day)");
		sb.append(" group by f.topic_id) m");
		sb.append(" where c.forward_cid=m.topic_id and c.type=3");
		sb.append(" and c.read_count_dummy>150 and m.ucount>m.rcount");
		sb.append(" AND t.sub_type <>1   ");
		sb.append(" order by sinceId desc limit 100");

		List<Map<String, Object>> searchList = contentService.queryEvery(sb.toString());
		if(null != searchList && searchList.size() > 0){
			logger.info("共有"+searchList.size()+"个王国存在增量数据");
			//存入数据库
			//1 查询当前列表key
			String nextCacheKey = null;
			String currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_LONELY_KINGDOM);
			if(StringUtils.isNotBlank(currentCacheKey)){
				if(currentCacheKey.equals(Constant.BILLBOARD_KEY_TARGET2)){
					nextCacheKey = Constant.BILLBOARD_KEY_TARGET1;
				}else{
					nextCacheKey = Constant.BILLBOARD_KEY_TARGET2;
				}
			}else{
				nextCacheKey = Constant.BILLBOARD_KEY_TARGET2;
			}
			//2处理待插入对象
			List<BillBoardList> insertList = new ArrayList<BillBoardList>();
			BillBoardList bbl = null;
			String listKey = Constant.BILLBOARD_KEY_LONELY_KINGDOM + nextCacheKey;
			int s = 1;
			for(Map<String, Object> m : searchList){
				bbl = new BillBoardList();
				bbl.setListKey(listKey);
				bbl.setTargetId((Long)m.get("topic_id"));
				bbl.setType(1);
				bbl.setSinceId(s);
				insertList.add(bbl);
				s++;
			}
			//3存入数据库
			contentService.insertBillboardList(insertList, listKey);
			//4将新的列表提交到缓存
			cacheService.set(Constant.BILLBOARD_KEY_LONELY_KINGDOM, nextCacheKey);
		}else{
			logger.info("共有0个王国存在增量数据");
		}
	}
}
