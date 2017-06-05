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

@Component("colorfulKingdomBillBoardTask")
public class ColorfulKingdomBillBoardTask {

	private static final Logger logger = LoggerFactory.getLogger(ColorfulKingdomBillBoardTask.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private CacheService cacheService;
	
	public void doTask(){
		logger.info("[最丰富多彩的王国]榜单任务开始...");
		long s = System.currentTimeMillis();
		
		try{
			this.execTask();
		}catch(Exception e){
			logger.error("[最丰富多彩的王国]榜单任务执行失败", e);
		}

		long e = System.currentTimeMillis();
		logger.info("[最丰富多彩的王国]榜单任务结束，共耗时"+(e-s)/1000+"秒");
	}
	
	private void execTask(){
		StringBuilder sb = new StringBuilder();
		sb.append("select m.topic_id,if(m.textCount>0,POW(m.textCount,0.8),1)");
		sb.append("*if(m.imageCount>0,POW(m.imageCount,1.1),1)*if(m.audioCount>0,POW(m.audioCount,1.5),1)");
		sb.append("*if(m.vedioCount>0,POW(m.vedioCount,1.5),1) as sinceId");
		sb.append(" from content c LEFT JOIN topic t ON c.forward_cid = t.id,(select f.topic_id,");
		sb.append("count(if(f.type=0 and f.content_type=0, TRUE, NULL)) as textCount,");
		sb.append("count(if(f.type=0 and f.content_type=1, TRUE, NULL)) as imageCount,");
		sb.append("count(if(f.type=12, TRUE, NULL)) as vedioCount,");
		sb.append("count(if(f.type=13, TRUE, NULL)) as audioCount");
		sb.append(" from topic_fragment f");
		sb.append(" where f.create_time>date_add(now(), interval -7 day)");
		sb.append(" and f.type in (0,12,13) and f.status=1");
		sb.append(" group by f.topic_id) m");
		sb.append(" where c.forward_cid=m.topic_id and c.type=3");
		sb.append(" and c.status=0 and c.read_count_dummy>150");
		sb.append(" AND t.sub_type <>1 ");
		sb.append(" order by sinceId desc limit 100");
		
		List<Map<String, Object>> searchList = contentService.queryEvery(sb.toString());
		if(null != searchList && searchList.size() > 0){
			logger.info("共有"+searchList.size()+"个王国存在增量数据");
			//存入数据库
			//1 查询当前列表key
			String nextCacheKey = null;
			String currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_COLOURFUL_KINGDOM);
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
			String listKey = Constant.BILLBOARD_KEY_COLOURFUL_KINGDOM + nextCacheKey;
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
			cacheService.set(Constant.BILLBOARD_KEY_COLOURFUL_KINGDOM, nextCacheKey);
		}else{
			logger.info("共有0个王国存在增量数据");
		}
	}
}
