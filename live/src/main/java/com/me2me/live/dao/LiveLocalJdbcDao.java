package com.me2me.live.dao;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.common.Constant;
import com.me2me.common.utils.DateUtil;
import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LiveFavoriteDelete;
import com.me2me.live.model.TopicBarrage;

@Repository
public class LiveLocalJdbcDao {
	private static final Logger logger = LoggerFactory.getLogger(LiveLocalJdbcDao.class);
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void batchInsertLiveFavorite(List<LiveFavorite> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		LiveFavorite lf = null;
		for(int i=0;i<list.size();i++){
			lf = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into live_favorite(topic_id,uid) values(");
			sb.append(lf.getTopicId()).append(",").append(lf.getUid());
			sb.append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void batchInsertTopicBarrage(List<TopicBarrage> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		TopicBarrage tb = null;
		for(int i=0;i<list.size();i++){
			tb = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into topic_barrage(topic_id,uid,type,content_type,top_id,bottom_id) values(");
			sb.append(tb.getTopicId()).append(",").append(tb.getUid()).append(",").append(tb.getType());
			sb.append(",").append(tb.getContentType()).append(",").append(tb.getTopId());
			sb.append(",").append(tb.getBottomId()).append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void updateContentAddOneFavoriteCount(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update content set favorite_count=favorite_count+1 where id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	public void contentAddFavoriteCount(long topicId, int type){
		StringBuilder sb = new StringBuilder();
		sb.append("update content set favorite_count=favorite_count");
		if(type>0){
			sb.append("+1");
		}else{
			sb.append("-1");
		}
		sb.append(" where forward_cid=").append(topicId);
		sb.append(" and type=3");
		if(type<=0){
			sb.append(" and favorite_count>0");
		}
		jdbcTemplate.execute(sb.toString());
	}
	
	public void updateContentAddFavoriteCountByForwardCid(int count, long forwardCid){
		if(count < 1){
			return;
		}
		String sql = "update content set favorite_count=favorite_count+"+count+" where forward_cid="+forwardCid+" and type=3";
		jdbcTemplate.execute(sql);
	}
	
	public void deleteLiveFavoriteByIds(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("delete from live_favorite where id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	public void batchInsertLiveFavoriteDelete(List<LiveFavoriteDelete> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		LiveFavoriteDelete lfd = null;
		for(int i=0;i<list.size();i++){
			lfd = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into live_favorite_delete(topic_id,uid) values(");
			sb.append(lfd.getTopicId()).append(",").append(lfd.getUid());
			sb.append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void updateContentDecrOneFavoriteCount(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update content set favorite_count=favorite_count-1 where favorite_count>0 and id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	/**
	 * 
	 * @param searchDTO
	 * @param topType 查询是否置顶 -1全部 0不置顶的 1置顶的
	 * @return
	 */
	public List<Map<String,Object>> searchTopics(KingdomSearchDTO searchDTO, int topType){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.* from (");
		if(searchDTO.getSearchUid() > 0){
			sb.append("select m.*,m.long_time*10000 as longtime");
			sb.append(" from topic m where m.uid=").append(searchDTO.getSearchUid());
			if(searchDTO.getAllowCore() > 0){
				sb.append(" union ");
				sb.append("select m1.*,m1.long_time*100 as longtime");
				sb.append(" from topic m1 where m1.uid<>").append(searchDTO.getSearchUid());
				sb.append(" and FIND_IN_SET(").append(searchDTO.getSearchUid());
				sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			}
			if(searchDTO.getAllowCore() > 1){
				sb.append(" union ");
				sb.append("select m2.*,m2.long_time as longtime");
				sb.append(" from topic m2,live_favorite f");
				sb.append(" where m2.id=f.topic_id");
				sb.append(" and m2.uid<>").append(searchDTO.getSearchUid());
				sb.append(" and not FIND_IN_SET(").append(searchDTO.getSearchUid());
				sb.append(",SUBSTR(m2.core_circle FROM 2 FOR LENGTH(m2.core_circle)-2))");
				sb.append(" and f.uid=").append(searchDTO.getSearchUid());
			}
		}else{
			sb.append("select m.*,m.long_time*10000 as longtime");
			sb.append(" from topic m");
		}
		sb.append(") t");

		if(searchDTO.getTopicType() > 0 && searchDTO.getTopicId() > 0){
			sb.append(",topic_aggregation a");
			if(searchDTO.getTopicType() == 1){//个人王国查聚合的母王国
				sb.append(" where t.id=a.topic_id");
				sb.append(" and a.sub_topic_id=").append(searchDTO.getTopicId());
			}else if(searchDTO.getTopicType() == 2){//聚合王国查询所聚合的子王国
				sb.append(" where t.id=a.sub_topic_id");
				sb.append(" and a.topic_id=").append(searchDTO.getTopicId());
				if(topType >= 0){
					sb.append(" and a.is_top=").append(topType);
				}
			}else{
				sb.append(" where t.id=a.sub_topic_id and a.id=0");//默认查不到数据
			}
		}else{
			sb.append(" where 1=1");
		}
		
		sb.append(" and t.longtime<").append(searchDTO.getUpdateTime());
		if(searchDTO.getSearchRights() > 0){
			sb.append(" and t.rights=").append(searchDTO.getSearchRights());
		}
		if(StringUtils.isNotBlank(searchDTO.getKeyword())){
			sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
		}
		if(searchDTO.getSearchType() > 0){
			if(searchDTO.getSearchType() == 1){
				sb.append(" and t.type=0");
			}else if(searchDTO.getSearchType() == 2){
				sb.append(" and t.type=1000");
			}else{
				sb.append(" and t.type=").append(searchDTO.getSearchType());
			}
		}
		if(searchDTO.getExceptTopicId() > 0){
			sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
		}
		if(searchDTO.getTopicId() > 0 && searchDTO.getTopicType() == 2 && topType == 1){//母查置顶的子,则查出所有的置顶项并按置顶时间倒序
			sb.append(" order by a.update_time desc");
		}else{
			sb.append(" order by t.longtime desc limit 10");
		}
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String,Object>> getKingdomListBySearchScene(long currentUid, KingdomSearchDTO searchDTO){
		StringBuilder sb = new StringBuilder();
		
		if(searchDTO.getSearchScene() == 1){//聚合王国主动场景（收录列表）
			//查询我创建的个人王国+我是核心圈的个人王国+我订阅的个人王国+其他的王国
			sb.append("select t.* from (");
			//我是国王
			sb.append("select m.*,m.long_time*1000 as longtime");
			sb.append(" from topic m where m.type=0 and m.uid=").append(currentUid);
			//我是核心圈
			sb.append(" union ");
			sb.append("select m1.*,m1.long_time*100 as longtime");
			sb.append(" from topic m1 where m1.type=0 and m1.status=0 and m1.uid<>").append(currentUid);
			sb.append(" and FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			//我加入的
			sb.append(" union ");
			sb.append("select m2.*,m2.long_time*10 as longtime");
			sb.append(" from topic m2,live_favorite f");
			sb.append(" where m2.type=0 and m2.status=0 and m2.id=f.topic_id");
			sb.append(" and m2.uid<>").append(currentUid);
			sb.append(" and not FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m2.core_circle FROM 2 FOR LENGTH(m2.core_circle)-2))");
			sb.append(" and f.uid=").append(currentUid);
//			sb.append(" union ");
//			sb.append(" select m3.*,m3.long_time as longtime");
//			sb.append(" from topic m3 where m3.type=0 and m3.status=0 and m3.uid<>").append(currentUid);
//			sb.append(" and not FIND_IN_SET(").append(currentUid);
//			sb.append(",SUBSTR(m3.core_circle FROM 2 FOR LENGTH(m3.core_circle)-2))");
//			sb.append(" and not EXISTS (select 1 from live_favorite f3 where f3.uid=");
//			sb.append(currentUid).append(" and f3.topic_id=m3.id)");
			sb.append(") t");
			sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
			if(searchDTO.getExceptTopicId() > 0){
				sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
			}
			if(StringUtils.isNotBlank(searchDTO.getKeyword())){
				sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
			}
			sb.append(" order by t.longtime desc limit 10");
		}else if(searchDTO.getSearchScene() == 2){//聚合王国被动场景（加入列表）
			if(searchDTO.getVersionFlag() == 0){//查询我创建的个人王国
				sb.append("select t.*,t.long_time as longtime from topic t ");
				sb.append(" where t.type=0 and t.uid=").append(currentUid);
				sb.append(" and t.long_time<").append(searchDTO.getUpdateTime());
				if(searchDTO.getExceptTopicId() > 0){
					sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
				}
				if(StringUtils.isNotBlank(searchDTO.getKeyword())){
					sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
				}
				sb.append(" order by t.long_time desc limit 10");
			}else{//V2.2.1版本开始查询我创建的个人王国+我是核心圈的个人王国
				sb.append("select t.* from (");
				sb.append("select m.*,m.long_time*100 as longtime");
				sb.append(" from topic m where m.type=0 and m.uid=").append(currentUid);
				sb.append(" union ");
				sb.append("select m1.*,m1.long_time as longtime");
				sb.append(" from topic m1 where m1.type=0 and m1.uid<>").append(currentUid);
				sb.append(" and FIND_IN_SET(").append(currentUid);
				sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
				sb.append(") t");
				sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
				if(searchDTO.getExceptTopicId() > 0){
					sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
				}
				if(StringUtils.isNotBlank(searchDTO.getKeyword())){
					sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
				}
				sb.append(" order by t.longtime desc limit 10");
			}
		}else if(searchDTO.getSearchScene() == 4){//个人王国被动场景
			if(searchDTO.getVersionFlag() == 0){//查询我创建的聚合王国
				sb.append("select t.*,t.long_time as longtime from topic t ");
				sb.append(" where t.type=1000 and t.uid=").append(currentUid);
				sb.append(" and t.long_time<").append(searchDTO.getUpdateTime());
				if(searchDTO.getExceptTopicId() > 0){
					sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
				}
				if(StringUtils.isNotBlank(searchDTO.getKeyword())){
					sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
				}
				sb.append(" order by t.long_time desc limit 10");
			}else{//V2.2.1版本开始
				//查询我创建的聚合王国+我是核心圈的聚合王国
				sb.append("select t.* from (");
				sb.append("select m.*,m.long_time*100 as longtime");
				sb.append(" from topic m where m.type=1000 and m.uid=").append(currentUid);
				sb.append(" union ");
				sb.append("select m1.*,m1.long_time as longtime");
				sb.append(" from topic m1 where m1.type=1000 and m1.uid<>").append(currentUid);
				sb.append(" and FIND_IN_SET(").append(currentUid);
				sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
				sb.append(") t");
				sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
				if(searchDTO.getExceptTopicId() > 0){
					sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
				}
				if(StringUtils.isNotBlank(searchDTO.getKeyword())){
					sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
				}
				sb.append(" order by t.longtime desc limit 10");
			}
		}else if(searchDTO.getSearchScene() == 5){//分享场景
			//查询我创建的+我是核心圈的+我订阅的
			sb.append("select t.* from (");
			sb.append("select m.*,m.long_time*10000 as longtime");
			sb.append(" from topic m where m.uid=").append(currentUid);
			sb.append(" union ");
			sb.append("select m1.*,m1.long_time*100 as longtime");
			sb.append(" from topic m1 where m1.uid<>").append(currentUid);
			sb.append(" and FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			sb.append(" union ");
			sb.append("select m2.*,m2.long_time as longtime");
			sb.append(" from topic m2,live_favorite f");
			sb.append(" where m2.id=f.topic_id");
			sb.append(" and m2.uid<>").append(currentUid);
			sb.append(" and not FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m2.core_circle FROM 2 FOR LENGTH(m2.core_circle)-2))");
			sb.append(" and f.uid=").append(currentUid);
			sb.append(") t");
			sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
			if(searchDTO.getExceptTopicId() > 0){
				sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
			}
			if(StringUtils.isNotBlank(searchDTO.getKeyword())){
				sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
			}
			if(searchDTO.getSearchType() > 0){
				if(searchDTO.getSearchType() == 1){
					sb.append(" and t.type=0");
				}else if(searchDTO.getSearchType() == 2){
					sb.append(" and t.type=1000");
				}else{
					sb.append(" and t.type=").append(searchDTO.getSearchType());
				}
			}
			sb.append(" order by t.longtime desc limit 10");
		}else if(searchDTO.getSearchScene() == 6){//转发场景
			//查询我创建的+我是核心圈的+我订阅的
			sb.append("select t.* from (");
			sb.append("select m.*,m.long_time*10000 as longtime");
			sb.append(" from topic m where m.uid=").append(currentUid);
			sb.append(" union ");
			sb.append("select m1.*,m1.long_time*100 as longtime");
			sb.append(" from topic m1 where m1.uid<>").append(currentUid);
			sb.append(" and FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			sb.append(" union ");
			sb.append("select m2.*,m2.long_time as longtime");
			sb.append(" from topic m2,live_favorite f");
			sb.append(" where m2.id=f.topic_id");
			sb.append(" and m2.uid<>").append(currentUid);
			sb.append(" and not FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m2.core_circle FROM 2 FOR LENGTH(m2.core_circle)-2))");
			sb.append(" and f.uid=").append(currentUid);
			sb.append(") t");
			sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
			if(searchDTO.getExceptTopicId() > 0){
				sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
			}
			if(StringUtils.isNotBlank(searchDTO.getKeyword())){
				sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
			}
			if(searchDTO.getSearchType() > 0){
				if(searchDTO.getSearchType() == 1){
					sb.append(" and t.type=0");
				}else if(searchDTO.getSearchType() == 2){
					sb.append(" and t.type=1000");
				}else{
					sb.append(" and t.type=").append(searchDTO.getSearchType());
				}
			}
			sb.append(" order by t.longtime desc limit 10");
		}else if(searchDTO.getSearchScene() == 8){//转发进来的场景
			//查询我创建的，我是核心圈的，我订阅的
			sb.append("select t.* from (");
			//我创建的
			sb.append("select m.*,m.long_time*1000 as longtime");
			sb.append(" from topic m where m.uid=").append(currentUid);
			//我是核心圈的
			sb.append(" union ");
			sb.append("select m1.*,m1.long_time*100 as longtime");
			sb.append(" from topic m1 where m1.uid<>").append(currentUid);
			sb.append(" and FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			//我加入的
			sb.append(" union ");
			sb.append("select m2.*,m2.long_time*10 as longtime");
			sb.append(" from topic m2,live_favorite f");
			sb.append(" where m2.id=f.topic_id");
			sb.append(" and m2.uid<>").append(currentUid);
			sb.append(" and not FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m2.core_circle FROM 2 FOR LENGTH(m2.core_circle)-2))");
			sb.append(" and f.uid=").append(currentUid);
			//其他的
			sb.append(" union ");
			sb.append(" select m3.*,m3.long_time as longtime");
			sb.append(" from topic m3 where m3.uid<>").append(currentUid);
			sb.append(" and not FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m3.core_circle FROM 2 FOR LENGTH(m3.core_circle)-2))");
			sb.append(" and not EXISTS (select 1 from live_favorite f3 where f3.uid=");
			sb.append(currentUid).append(" and f3.topic_id=m3.id)");
			sb.append(") t");
			sb.append(" where t.longtime<").append(searchDTO.getUpdateTime());
			if(searchDTO.getExceptTopicId() > 0){
				sb.append(" and t.id<>").append(searchDTO.getExceptTopicId());
			}
			if(StringUtils.isNotBlank(searchDTO.getKeyword())){
				sb.append(" and t.title like '%").append(searchDTO.getKeyword()).append("%'");
			}
			sb.append(" order by t.longtime desc limit 20");
		}else{
			return null;
		}
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getTopicUpdateCount(List<Long> tids){
		if(null == tids || tids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select f.topic_id, count(if(t.uid=f.uid,TRUE,NULL)) as topicCount,");
		sb.append(" count(if(t.uid<>f.uid,TRUE,NULL)) as reviewCount");
		sb.append(" from topic t,topic_fragment f");
		sb.append(" where t.id=f.topic_id and t.id in (");
		for(int i=0;i<tids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(tids.get(i));
		}
		sb.append(") group by f.topic_id");
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public Map<String, Integer> getUserInternalStatus(long uid, List<Long> owners){
		if(null == owners || owners.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select t.uid,t.owner,t.internal_status from sns_circle t");
		sb.append(" where t.uid=").append(uid).append(" and t.owner in (");
		for(int i=0;i<owners.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(owners.get(i));
		}
		sb.append(")");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			Map<String, Integer> result = new HashMap<String, Integer>();
			String key = null;
			for(Map<String, Object> m : list){
				key = ((Long)m.get("uid")).toString()+"_"+((Long)m.get("owner")).toString();
				result.put(key, (Integer)m.get("internal_status"));
			}
			return result;
		}
		return null;
	}
	
	public List<Map<String,Object>> getTopicAggregationAcCountByTopicIds(List<Long> topicIds){
		if(null == topicIds || topicIds.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select t.topic_id,count(1) as cc");
		sb.append(" from topic_aggregation t");
		sb.append(" where t.topic_id in (");
		for(int i=0;i<topicIds.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(topicIds.get(i).longValue());
		}
		sb.append(") group by t.topic_id");
		
		return jdbcTemplate.queryForList(sb.toString());
	}

	public int getTopicAggregationCountByTopicId(long topicId){
		String sql = "select count(1) as count from topic_aggregation where topic_id = "+topicId;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return Integer.valueOf(list.get(0).get("count").toString());
	}

	/**
	 *  查母王国
	 * @param topicId
	 * @return
	 */
	public int getTopicAggregationCountByTopicId2(long topicId){
		String sql = "select count(1) as count from topic_aggregation where sub_topic_id = "+topicId;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return Integer.valueOf(list.get(0).get("count").toString());
	}
	
	public Map<String, Long> getLikeCountByUidAndCids(long uid, List<Long> cids){
		if(null == cids || cids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select t.cid as cid,count(1) as cc from content_likes_details t");
		sb.append(" where t.uid=").append(uid).append(" and t.cid in (");
		for(int i=0;i<cids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(cids.get(i));
		}
		sb.append(") group by t.cid");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		Map<String, Long> result = new HashMap<String, Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.put(String.valueOf(m.get("cid")), (Long)m.get("cc"));
			}
		}
		return result;
	}
	
	public List<Map<String, Object>> getLastCoreCircleFragmentByTopicIds(List<Long> topicIds){
		if(null == topicIds || topicIds.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select p.* from topic_fragment p, (");
		sb.append("select max(f.id) as fid from topic_fragment f, topic t ");
		sb.append("where f.topic_id=t.id and t.id in (");
		for(int i=0;i<topicIds.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(topicIds.get(i));
		}
		sb.append(") and FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
		sb.append(" group by t.id) m where p.id=m.fid");
		
		return jdbcTemplate.queryForList(sb.toString());
	}

	public List<Map<String, Object>> getLastFragmentByTopicIds(List<Long> topicIds){
		if(null == topicIds || topicIds.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select p.* from topic_fragment p, (");
		sb.append("select f.topic_id, max(f.id) as fid from topic_fragment f ");
		sb.append("where f.status=1 and f.type!=1000 and (f.type!=51 or f.content_type!=16) and f.topic_id in (");
		for(int i=0;i<topicIds.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(topicIds.get(i));
		}
		sb.append(") group by f.topic_id) m where p.id=m.fid");

		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getAcTopicListByCeTopicId(long ceTopicId, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select m.* from (select t.*,unix_timestamp(a.update_time)*100000 as ltime");
    	sb.append(" from topic_aggregation a,topic t where a.sub_topic_id=t.id");
    	sb.append(" and a.topic_id=").append(ceTopicId).append(" and a.is_top=1");
    	sb.append(" UNION ");
    	sb.append("select t.*,t.long_time as ltime from topic_aggregation a,topic t");
    	sb.append(" where a.sub_topic_id=t.id and a.topic_id=").append(ceTopicId);
    	sb.append(" and a.is_top=0 ) m ");
    	sb.append(" order by m.ltime desc limit ").append(start).append(",").append(pageSize);

    	return jdbcTemplate.queryForList(sb.toString());
    }

	public Map<String, Long> getTopicMembersCount(List<Long> topicIdList){
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
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
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
    	List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sb2.toString());
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
	
	public List<Map<String, Object>> getMyTopicTags(long uid, int pageSize){
		StringBuilder sb = new StringBuilder();
		sb.append("select d.tag, max(d.create_time) as maxtime");
		sb.append(" from topic_tag_detail d,topic_tag t");
		sb.append(" where d.tag_id = t.id and t.status=0");
		sb.append(" and d.uid=").append(uid).append(" group by d.tag");
		sb.append(" order by maxtime desc limit ").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getRecTopicTags(boolean isAdmin, int pageSize){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.tag, count(d.topic_id) as kcount");
		sb.append(" from topic_tag t LEFT JOIN topic_tag_detail d");
		sb.append(" on t.id=d.tag_id and d.status=0");
		sb.append(" where t.is_rec=1 and t.status=0");
		if(!isAdmin){//不是管理员，则推荐标签中不能出现“官方”二字
			sb.append(" and t.tag not like '%官方%'");
		}
		sb.append(" group by t.tag order by kcount desc limit ").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getTagKingdomListByTag(String tag, long sinceId, int pageSize, List<Long> blacklistUids){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.* from topic_tag_detail d,topic t");
		sb.append(" where d.tag='").append(tag).append("' and d.status=0");
		if(null != blacklistUids && blacklistUids.size() > 0){
			sb.append(" and t.uid not in (");
			for(int i=0;i<blacklistUids.size();i++){
				if(i>0){
					sb.append(",");
				}
				sb.append(blacklistUids.get(i).toString());
			}
			sb.append(")");
		}
		sb.append(" and d.topic_id=t.id and t.long_time<").append(sinceId);
		sb.append(" order by t.long_time DESC limit ").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public void updateTopicContentCover(long topicId, String cover){
		StringBuilder sb = new StringBuilder();
		sb.append("update content set conver_image='").append(cover);
		sb.append("' where forward_cid=").append(topicId);
		sb.append(" and type=3");
		jdbcTemplate.execute(sb.toString());
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("update content set conver_image='").append(Constant.QINIU_DOMAIN).append("/").append(cover);
		sb2.append("' where forward_cid=").append(topicId);
		sb2.append(" and type=6");
		jdbcTemplate.execute(sb2.toString());
		
	}
	
	public void updateTopicContentTitle(long topicId, String title){
		StringBuilder sb = new StringBuilder();
		sb.append("update content set title='").append(title);
		sb.append("',content='").append(title);
		sb.append("' where forward_cid=").append(topicId);
		sb.append(" and type=3");
		jdbcTemplate.execute(sb.toString());
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("update content set forward_title='").append(title);
		sb2.append("' where forward_cid=").append(topicId);
		sb2.append(" and type=6");
		jdbcTemplate.execute(sb2.toString());
	}
	
	/**
	 * 根据王国标签进行推荐
	 * @param topicId	王国ID
	 * @param sinceId	分页参数
	 * @param pageSize	每页个数
	 * @param topicType	待查询的王国类型，当<0时表示查询所有类型的王国
	 * @return
	 */
	public List<Map<String, Object>> getRecTopicByTag22(long topicId, long sinceId, int pageSize, int topicType){
		if(topicId <= 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select t.*,t.long_time as longtime from topic t,(select DISTINCT d2.topic_id as tid");
		sb.append(" from topic_tag_detail d2 where d2.status=0 and d2.topic_id!=").append(topicId);
		sb.append(" and d2.tag_id in (select d.tag_id from topic_tag_detail d");
		sb.append(" where d.topic_id=").append(topicId).append(" and d.status=0)) m");
		sb.append(" where t.id=m.tid and t.long_time<").append(sinceId);
		if(topicType >= 0){
			sb.append(" and t.type=").append(topicType);
		}
		sb.append(" order by t.long_time DESC limit ").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	/**
	 * 这个方法是，返回以自身标签为主，附加运营推荐标签
	 * @param topicId
	 * @param sinceId
	 * @param pageSize
	 * @param topicType
	 * @return
	 */
	public List<Map<String, Object>> getRecTopicByTag(long topicId, long sinceId, int pageSize, int topicType){
		if(topicId <= 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select t.*,t.long_time*n.pp as longtime from topic t,(");
		sb.append("select m.topic_id,max(pp) as pp from (");
		sb.append("select DISTINCT d1.topic_id, 100 as pp");
		sb.append(" from topic_tag_detail d1 where d1.status=0");
		sb.append(" and d1.topic_id!=").append(topicId);
		sb.append(" and d1.tag_id in (select d11.tag_id from topic_tag_detail d11 where d11.status=0 and d11.topic_id=");
		sb.append(topicId).append(")");
		sb.append(" union all ");
		sb.append("select DISTINCT d2.topic_id, 1 as pp from topic_tag_detail d2");
		sb.append(" where d2.status=0 and d2.tag_id in (select t22.id from topic_tag t22 where t22.is_rec=1 and t22.status=0)");
		sb.append(") m group by m.topic_id) n");
		sb.append(" where t.id=n.topic_id and t.status=0");
		if(topicType >= 0){
			sb.append(" and t.type=").append(topicType);
		}
		sb.append(" and t.long_time*n.pp<").append(sinceId);
		sb.append(" order by longtime desc limit ").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}

	/**
	 * 特殊活动 王国/用户 增加 热度/荣誉
	 * @param targetId
	 * @param type			1王国，2用户
	 * @param activityId
	 * @param score
	 */
	public void specialTopicAddHot(long targetId, int type, long activityId, int score){
		if(type == 2){
			String sql = "select 1 from a_common_list where target_id="+targetId;
			sql = sql + " and type=2 and activity_id="+activityId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			if(null == list || list.size() == 0){
				String insert = "insert into a_common_list(target_id,alias,type,score,activity_id) values (";
				insert = insert + targetId + ",'',2," + score + ","+activityId+")";
				jdbcTemplate.execute(insert);
				return;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("update a_common_list set score = score+").append(score);
		sb.append(" where target_id=").append(targetId);
		sb.append(" and type=").append(type);
		sb.append(" and activity_id=").append(activityId);
		jdbcTemplate.execute(sb.toString());
	}

	/**
	 * 特殊活动 王国/用户 减少 热度/荣誉
	 * @param targetId
	 * @param type			1王国，2用户
	 * @param activityId
	 * @param score
	 */
	public void specialTopicReduceHot(long targetId, int type, long activityId, int score){
		StringBuilder sb = new StringBuilder();
		sb.append("update a_common_list set score=case when score>=").append(score);
		sb.append(" then score-").append(score);
		sb.append(" else 0 end where target_id=").append(targetId);
		sb.append(" and type=").append(type);
		sb.append(" and activity_id=").append(activityId);
		jdbcTemplate.execute(sb.toString());
	}
	
	public void insertSpecialTopicHotDetail(long acitivityId, long fragmentId){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into a_common_topic_hot_detail(target_id,activity_id)");
		sb.append(" values (").append(fragmentId).append(",").append(acitivityId);
		sb.append(")");
		jdbcTemplate.execute(sb.toString());
	}
	
	public Map<String, Object> getSpecialTopicHotDetail(long activityId, long targetId){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from a_common_topic_hot_detail t");
		sb.append(" where t.target_id=").append(targetId);
		sb.append(" and t.activity_id=").append(activityId);
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询投票用户用户信息
	 * @param voteId
	 * @return
	 */
	public List<Map<String, Object>> getVoteUserProfileByVoteId(long voteId){
		StringBuilder sb = new StringBuilder();
		sb.append("select * FROM user_profile u,");
		sb.append("(select uid,MAX(id) maxId FROM vote_record ");
		sb.append(" WHERE voteId=");
		sb.append(String.valueOf(voteId));
		sb.append("  GROUP BY uid ORDER BY maxId DESC LIMIT 0,50 ) v WHERE u.uid = v.uid ");
		return jdbcTemplate.queryForList(sb.toString());
	}
	/**
	 * 统计某月份的王国图片数量
	 * @author zhangjiwei
	 * @date May 5, 2017
	 * @param topicId
	 * @param month
	 * @return
	 */
	public long countTopicImgByMonth(long topicId, String month) {
		String sql = "select count(1) from topic_fragment where topic_id=? and type=0 and content_type=1 and status =1 and DATE_FORMAT(create_time,'%Y%m')=?";
		long count = jdbcTemplate.queryForObject(sql,new Object[]{topicId,month}, Long.class);
		return count;
	}
	
	public List<Map<String, Object>> getUserAtListInTopic(long topicId, int start, int pageSize, List<Long> coreUidList, long searchUid){
		StringBuilder sb = new StringBuilder();
		sb.append("select uu.* from user_profile uu,(select f.uid as uid,min(f.id) as sinceId");
		sb.append(" from topic_fragment f where f.topic_id=").append(topicId);
		sb.append(" group by f.uid");
		sb.append(" UNION ");
		sb.append("select DISTINCT l.uid as uid, 9223372036854775807 as sinceId");
		sb.append(" from live_favorite l where l.topic_id=").append(topicId);
		sb.append(" and not EXISTS (select 1 from topic_fragment f2 where f2.topic_id=").append(topicId);
		sb.append(" and f2.uid=l.uid)");
		sb.append(" UNION ");
		sb.append(" select u.uid as uid, 9223372036854775807 as sinceId");
		sb.append(" from user_profile u where u.uid in (");
		for(int i=0;i<coreUidList.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(coreUidList.get(i));
		}
		sb.append(") and not EXISTS(select 1 from topic_fragment f3 where f3.topic_id=").append(topicId);
		sb.append(" and f3.uid=u.uid) and not EXISTS(select 1 from live_favorite l3 where l3.topic_id=");
		sb.append(topicId).append(" and l3.uid=u.uid)) m");
		sb.append(" where uu.uid=m.uid");
		if(searchUid > 0){
			sb.append(" and uu.uid!=").append(searchUid);
		}
		sb.append(" order by m.sinceId,m.uid limit ").append(start).append(",").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public int countUserAtListInTopic(long topicId, List<Long> coreUidList, long searchUid){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as cc from user_profile uu,(select f.uid as uid,min(f.id) as sinceId");
		sb.append(" from topic_fragment f where f.topic_id=").append(topicId);
		sb.append(" group by f.uid");
		sb.append(" UNION ");
		sb.append("select DISTINCT l.uid as uid, 9223372036854775807 as sinceId");
		sb.append(" from live_favorite l where l.topic_id=").append(topicId);
		sb.append(" and not EXISTS (select 1 from topic_fragment f2 where f2.topic_id=").append(topicId);
		sb.append(" and f2.uid=l.uid)");
		sb.append(" UNION ");
		sb.append(" select u.uid as uid, 9223372036854775807 as sinceId");
		sb.append(" from user_profile u where u.uid in (");
		for(int i=0;i<coreUidList.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(coreUidList.get(i));
		}
		sb.append(") and not EXISTS(select 1 from topic_fragment f3 where f3.topic_id=").append(topicId);
		sb.append(" and f3.uid=u.uid) and not EXISTS(select 1 from live_favorite l3 where l3.topic_id=");
		sb.append(topicId).append(" and l3.uid=u.uid)) m");
		sb.append(" where uu.uid=m.uid");
		if(searchUid > 0){
			sb.append(" and uu.uid!=").append(searchUid);
		}
		
		List<Map<String, Object>> countList = jdbcTemplate.queryForList(sb.toString());
		if(null != countList && countList.size() > 0){
			Map<String, Object> count = countList.get(0);
			return ((Long)count.get("cc")).intValue();
		}
		return 0;
	}
	/**
	 * 统计用户某天偷取的王国金币数。
	 * @author zhangjiwei
	 * @date Jun 12, 2017
	 * @param day
	 * @return
	 */
	public List<Map<String,Object>> getUserStealLogByDay(long uid,String day) {
		String sql = "select * from user_steal_log where uid=? and DATE_FORMAT(create_time,'%Y-%m-%d')=?";
		return jdbcTemplate.queryForList(sql,new Object[]{uid,day});
	}
	/**
	 * 判断用户是否有拿大红包的资格
	 * @author zhangjiwei
	 * @date Jun 12, 2017
	 * @param day
	 * @return
	 */
	public List<Map<String,Object>> getUserStealLogByCountAsc(long uid,int count) {
		String sql = "select * from user_steal_log where uid=? order by id asc limit ?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,new Object[]{uid,count});
		return list;
	}
	/**
	 * 获取用户当天获得金币总数量，包含操作得币和偷取得币，大红包不计入。
	 * @author zhangjiwei
	 * @date Jun 21, 2017
	 * @param uid
	 * @param day
	 * @return
	 */
	public int getUserCoinsByDay(long uid,String day) {
		String sql="select sum(coins) from("+
			" select sum(stealed_coins) coins from user_steal_log  where uid=? and ( is_big_red_pack=0 or is_big_red_pack is null) and DATE_FORMAT(create_time,'%Y-%m-%d')=?"+
			" UNION ALL"+
			" select sum(coin) coins from rule_log  where uid=? and DATE_FORMAT(create_time,'%Y-%m-%d')=?"+
			") c";
		Integer result =jdbcTemplate.queryForObject(sql,new Object[]{uid,day,uid,day},Integer.class);
		if(result==null){
			result =0;
		}
		return result;
	}
	/**
	 * 充值到某个王国
	 * @param topicId
	 * @param amount
	 */
    public void rechargeToKingDom(long topicId, int amount) {
		String sql = "update topic set price = price+"+amount+" where id = ? ";
		jdbcTemplate.update(sql,topicId);
    }

	public void zeroMyCoins(long uid) {
		String sql = "update user_profile set available_coin = 0 where uid = ?";
		jdbcTemplate.update(sql,uid);
	}
	public void addMyCoins(long uid,int price) {
		String sql = "update user_profile set available_coin = available_coin+ ? where uid = ?";
		jdbcTemplate.update(sql,new Object[]{price,uid});
	}
	public int getReadCount(long topicId) {
		String sql = "select sum(read_count_dummy) as readCount from topic_read_his where topic_id=? group by topic_id";
		List<Map<String, Object>> list= jdbcTemplate.queryForList(sql, new Object[]{topicId});
		return list.size()>0?Integer.parseInt(list.get(0).get("readCount").toString()):0;
	}

	public int getReadCountOuter(long topicId) {
		String sql = "select sum(read_count_dummy) as readCount from topic_read_his where topic_id=? and in_app = 0 group by topic_id";
		List<Map<String, Object>> list= jdbcTemplate.queryForList(sql, new Object[]{topicId});
		return list.size()>0?Integer.parseInt(list.get(0).get("readCount").toString()):0;
	}
	/**
	 * 获取王国的剩余价值,如果查询失败，返回0
	 * @author zhangjiwei
	 * @date Jun 15, 2017
	 * @param topicId 王国ID
	 * @return
	 */
	public int getTopicRemainPrice(long topicId) {
		int remain=0;
		try{
			remain=jdbcTemplate.queryForObject("select steal_price from topic_data where topic_id=?",new Object[]{ topicId},Integer.class);
		}catch(Exception e){
			logger.error("未获取到王国["+topicId+"]剩余价值，可能每日统计未完成");
		}
		return remain;
	}
	/**
	 * 执行偷取王国价值
	 * @author zhangjiwei
	 * @date Jun 15, 2017
	 * @param stealedCoins 偷取的金币数量。
	 * @param topicId 王国ID
	 * @return
	 */
	public void stealTopicPrice(int stealedCoins,long topicId) {
		jdbcTemplate.update("update topic_data set steal_price=steal_price-? where topic_id=?",new Object[]{stealedCoins, topicId});
		jdbcTemplate.update("update topic set price=price-? where id=?",new Object[]{stealedCoins, topicId});
	}

	public int balanceTopicStealPriceHarvest(long topicId){
		String sql = "select d.harvest_price-t.price as balancePrice from topic t,topic_data d where t.id=d.topic_id and t.id=?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,topicId);
		if(null != list && list.size() > 0){
			Map<String,Object> c = list.get(0);
			int balancePrice = ((Long)c.get("balancePrice")).intValue();
			if(balancePrice > 0){
				jdbcTemplate.update("update topic_data set harvest_price=harvest_price-? where id=?",new Object[]{balancePrice, topicId});
			}
			return balancePrice;
		}
		return 0;
	}

	public void writeTopicNews(long topicID , String content){
		String sql = "insert into topic_news (topic_id , content , `type`) values(?,?,1)";
		jdbcTemplate.update(sql,topicID,content);
	}

	public void writeTopicTime(long topicID){
		String sql = "update topic set listing_time = now() where id = ?";
		jdbcTemplate.update(sql,topicID);
	}

	public List<Map<String,Object>> getConvergeTopic(long uid) {
		String sql = "SELECT id FROM topic WHERE uid = ? and type = 1000";
		return jdbcTemplate.queryForList(sql,uid);
	}

	public Map<String,Object> getlastUser(long topicId) {
		String sql = "select u.* from topic_fragment f,user_profile u where f.uid=u.uid and f.topic_id=? order by f.id desc limit 1";
		return jdbcTemplate.queryForMap(sql,topicId);
	}
    public int countTopicListedByStatus(int status,String title){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as count ");
    	sb.append("from topic_listed tl LEFT JOIN topic t ON tl.topic_id = t.id  ");
    	sb.append("where 1=1  ");
    	if(status==-1){
          sb.append(" and (tl.status=0 or tl.status=1)  ");
    	}else{
    		sb.append(" and tl.status= ");	
    		sb.append(status);
    	}
    	if(!StringUtils.isEmpty(title)){
    		sb.append(" and t.title like '%").append(title).append("%'");	
    	}
    	String sql = sb.toString();
    	Integer count=0;
		try{
			count=jdbcTemplate.queryForObject(sql, Integer.class);
		}catch(Exception e){
			count=0;
		}
    	return count;
    }
    public List<Map<String,Object>> getTopicListedListByStatus(int status,String title,int start,int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT tl.id,t.title,t.price,tl.price_RMB AS frozenPrice,u.nick_name,tl.create_time,tl.status,un.me_number,tl.buy_uid,tl.buy_time");
    	sb.append(" FROM topic_listed tl LEFT JOIN topic t ON tl.topic_id = t.id LEFT JOIN user_profile u ON u.uid = t.uid ");
    	sb.append(" left join user_no un on un.uid = tl.buy_uid ");
    	sb.append("where 1=1  ");
    	if(status==-1){
          sb.append(" and (tl.status=0 or tl.status=1)  ");
    	}else{
    		sb.append(" and tl.status= ");	
    		sb.append(status);
    	}
    	if(!StringUtils.isEmpty(title)){
    		sb.append(" and t.title like '%").append(title).append("%'");	
    	}
    	sb.append(" order by tl.create_time desc limit ").append(start).append(",").append(pageSize);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    
	/**
	 * 获取王国评论数
	 * @param topidId
	 * @return
	 */
	public int getTopicReviewCount(long topidId){
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" count(if(t.uid<>f.uid,TRUE,NULL)) as reviewCount");
		sb.append(" from topic t,topic_fragment f");
		sb.append(" where t.id=f.topic_id and t.id =");
		sb.append(topidId);
		sb.append(" group by f.topic_id");
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(list.size()==0){
			return 0;
		}else{
			Map<String,Object> map  = list.get(0);
			return Integer.parseInt(map.get("reviewCount").toString());
		}
	}
	/**
	 * 获取王国成员数
	 * @param topidId
	 * @return
	 */
	public int getTopicMembersCount(long topidId){
		Map<String, Long> result = new HashMap<String, Long>();
    	//查询非核心圈成员
    	StringBuilder sb = new StringBuilder();
    	sb.append("select f.topic_id,count(1) cc from live_favorite f,topic t");
    	sb.append(" where f.topic_id=t.id ");
    	sb.append(" and not FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
    	sb.append(" and f.topic_id = ");
    	sb.append(topidId);
    	sb.append(" group by f.topic_id");
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	int fNum =0 ;
    	if(null != list && list.size() > 0){
    		Map<String, Object> m  = list.get(0);
    		fNum = Integer.parseInt(m.get("cc").toString());
    	}
    	//查询核心圈成员
    	StringBuilder sb2 = new StringBuilder();
    	sb2.append("select t.id, LENGTH(t.core_circle)-LENGTH(replace(t.core_circle,',','')) as coreCount");
    	sb2.append(" from topic t where t.id =");
    	sb2.append(topidId);
    	List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sb2.toString());
    	int num = 0;
    	if(null != list2 && list2.size() > 0){
    		Map<String, Object> m  = list2.get(0);
    		num = Integer.parseInt(m.get("coreCount").toString());
           }
    	return fNum+num+1;
}

	public boolean existsTrialTagInKingdom(Long topicId, String tag) {
		String sql ="select count(1) from topic_tag_detail where topic_id=? and status=0 and (tag=? or auto_tag=1)";
		return jdbcTemplate.queryForObject(sql, new Object[]{topicId, tag}, Integer.class)>0;
	}

	public void updateExpiredTrialTag(int delayDay) {
		String sql ="update topic_tag_detail set auto_tag=0 where datediff(now(),create_time)>=? and auto_tag=1 and status=0";
		jdbcTemplate.update(sql,delayDay);
	}
	
	public void updateContentUpdateTime4Kingdom(long topicId, Date time){
		String sql = "update content set update_time=? where forward_cid=? and type=3";
		jdbcTemplate.update(sql,time,topicId);
	}

	public void updateContentUpdateId4Kingdom(long topicId, long updateId){
		String sql = "update content set update_id=? where forward_cid=? and type=3";
		jdbcTemplate.update(sql,updateId,topicId);
	}
	
	/**
     * 获取黑名单UID列表
     * @param uid
     * @return
     */
    public List<Long> getBlacklist(long uid){
    	String sql = "select * from user_black_list t where t.uid=" + uid;
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    	List<Long> result = new ArrayList<Long>();
    	if(null != list && list.size() > 0){
    		Long targetUid = null;
    		for(Map<String, Object> b : list){
    			targetUid = (Long)b.get("target_uid");
    			if(!result.contains(targetUid)){
    				result.add(targetUid);
    			}
    		}
    	}
    	return result;
    }
    
    public Map<String,Object> getMaxFragment(String strDate,long uid,int startNum,int endNum,String specialTopicIds){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT t.*,tp.title FROM topic_fragment t,topic tp WHERE t.status = 1  AND  t.topic_id = tp.id AND t.content_type = 0 ");
    	sb.append(" AND t.fragment <> tp.summary ");
    	sb.append(" AND t.create_time >='").append(strDate).append(" 00:00:00' AND t.create_time <='").append(strDate).append(" 23:59:59'");
    	sb.append(" AND t.uid = ").append(uid);
    	if(startNum!=0){
          sb.append(" AND LENGTH(t.fragment) >=").append(startNum);
    	}
    	if(endNum!=0){
            sb.append("  AND LENGTH(t.fragment)<=").append(endNum);
      	}
    	if(!StringUtils.isEmpty(specialTopicIds)){
    		 sb.append("  AND tp.id not in(").append(specialTopicIds).append(") ");
    	}
    	sb.append("  ORDER BY LENGTH(t.fragment) DESC LIMIT 1 ");
    	String sql = sb.toString();
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list.size()>0?list.get(0):null;
    }
    public Map<String,Object> getMaxFragmentByType(String strDate,long uid,int startNum,int endNum,int type,String specialTopicIds){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT t.*,tp.title FROM topic_fragment t,topic tp WHERE t.status = 1  AND  t.topic_id = tp.id AND t.content_type = 0  ");
    	sb.append( " and t.type =").append(type);
    	sb.append(" AND t.fragment <> tp.summary ");
    	sb.append(" AND t.create_time >='").append(strDate).append(" 00:00:00' AND t.create_time <='").append(strDate).append(" 23:59:59'");
    	sb.append(" AND t.uid = ").append(uid);
    	if(startNum!=0){
          sb.append(" AND LENGTH(t.fragment) >=").append(startNum);
    	}
    	if(endNum!=0){
            sb.append("  AND LENGTH(t.fragment)<=").append(endNum);
      	}
    	if(!StringUtils.isEmpty(specialTopicIds)){
   		 sb.append("  AND tp.id not in(").append(specialTopicIds).append(") ");
      	}
    	sb.append("  ORDER BY LENGTH(t.fragment) DESC LIMIT 1 ");
    	String sql = sb.toString();
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list.size()>0?list.get(0):null;
    }
    public Map<String,Object> getFragmentImage(String strDate,long uid,long topicId,String specialTopicIds){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT t.* FROM topic_fragment t WHERE t.status = 1  and  ((t.type=0 AND t.content_type = 1) OR (t.type=1 AND t.content_type = 1)  OR (t.type=51 AND t.content_type = 51)) ");
    	sb.append(" AND t.create_time >='").append(strDate).append(" 00:00:00' AND t.create_time <='").append(strDate).append(" 23:59:59'");
    	sb.append(" AND t.uid = ").append(uid);
    	if(topicId!=0){
            sb.append("  AND t.topic_id=").append(topicId);
      	}
    	sb.append(" and fn_Json_getKeyValue(t.extra,1,'w')>300 ");
    	sb.append(" AND (fn_Json_getKeyValue(t.extra,1,'type') IS  NULL  OR fn_Json_getKeyValue(t.extra,1,'type')  <> 'image_daycard') ");
    	if(!StringUtils.isEmpty(specialTopicIds)){
   		 sb.append("  AND t.topic_id not in(").append(specialTopicIds).append(") ");
       	}
    	sb.append("  ORDER BY fn_Json_getKeyValue(t.extra,1,'length')  DESC LIMIT 1 ");
    	String sql = sb.toString();
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list.size()>0?list.get(0):null;
    }
    public List<Map<String,Object>> getRobotQuotationRecordList(String strDate,long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT * FROM robot_quotation_record where ");
    	sb.append("  create_time >='").append(strDate).append(" 00:00:00' AND create_time <='").append(strDate).append(" 23:59:59'");
    	sb.append(" AND uid = ").append(uid);
    	String sql = sb.toString();
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
    }
    public List<Map<String,Object>> getSignRecordList(String strDate,long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT * FROM sign_record where ");
    	sb.append("  create_time >='").append(strDate).append(" 00:00:00' AND create_time <='").append(strDate).append(" 23:59:59'");
    	sb.append(" AND uid = ").append(uid);
    	String sql = sb.toString();
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
    }
    public int getSignRecordCount(long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT count(*) as num FROM sign_record where ");
    	sb.append("  uid = ").append(uid);
    	String sql = sb.toString();
    	Map<String,Object> map = jdbcTemplate.queryForMap(sql);
		return Integer.parseInt(map.get("num").toString());
    }
    public List<Map<String,Object>> getHisRobotQuotationRecord(String strDate,long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT * FROM robot_quotation_record where ");
    	sb.append("  create_time >='").append(strDate).append(" 00:00:00' ");
    	sb.append(" AND uid = ").append(uid);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    
    public int getUserTotalKingdomCount(long uid){
    	String sql = "select count(1) as cc from content c where c.uid=? and c.type=3";
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, uid);
    	int result = 0;
    	if(null != list && list.size() > 0){
    		Map<String,Object> count = list.get(0);
    		if(null != count.get("cc")){
    			result = ((Long)count.get("cc")).intValue();
    		}
    	}
    	return result;
    }
    public int countRobotListByNickName(String nickName,int type){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(*) FROM robot_info r,user_profile u WHERE r.uid = u.uid");
    	if(!StringUtils.isEmpty(nickName)){
    		sb.append(" and u.nick_name like '%").append(nickName).append("%'");
    	}
    	if(type!=-1){
    		sb.append(" and r.type=").append(type);
    	}
    	String sql = sb.toString();
    	Integer count=0;
		try{
			count=jdbcTemplate.queryForObject(sql, Integer.class);
		}catch(Exception e){
			count=0;
		}
    	return count;
    }
    public List<Map<String,Object>> getRobotListByNickName(String nickName,int type,int start,int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select r.id,r.uid,u.nick_name,u.avatar,r.type FROM robot_info r,user_profile u WHERE r.uid = u.uid ");
    	if(!StringUtils.isEmpty(nickName)){
    		sb.append(" and u.nick_name like '%").append(nickName).append("%'");
    	}
    	if(type!=-1){
    		sb.append(" and r.type=").append(type);
    	}
    	sb.append(" order by r.create_time desc limit ").append(start).append(",").append(pageSize);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    public int countQuotationListByQuotation(String quotation,int type){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(*) FROM quotation_info WHERE 1=1");
    	if(!StringUtils.isEmpty(quotation)){
    		sb.append(" and quotation like '%").append(quotation).append("%'");
    	}
    	if(type!=-1){
    		sb.append(" and type=").append(type);
    	}
    	String sql = sb.toString();
    	Integer count=0;
		try{
			count=jdbcTemplate.queryForObject(sql, Integer.class);
		}catch(Exception e){
			count=0;
		}
    	return count;
    }
    public List<Map<String,Object>> getQuotationListByQuotation(String quotation,int type,int start,int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select * FROM quotation_info WHERE 1=1");
    	if(!StringUtils.isEmpty(quotation)){
    		sb.append(" and quotation like '%").append(quotation).append("%'");
    	}
    	if(type!=-1){
    		sb.append(" and type=").append(type);
    	}
    	sb.append(" order by id desc limit ").append(start).append(",").append(pageSize);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    public int countLotteryJoinUser(long lotteryId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select COUNT(DISTINCT uid) AS COUNT FROM lottery_content WHERE lottery_id  = ");
    	sb.append(lotteryId);
    	String sql = sb.toString();
    	Integer count=0;
		try{
			count=jdbcTemplate.queryForObject(sql, Integer.class);
		}catch(Exception e){
			count=0;
		}
    	return count;
    }
    public List<Map<String,Object>> getLotteryWinUserList(long lotteryId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.uid,u.avatar,u.avatar_frame,u.nick_name,u.v_lv,u.level FROM user_profile u ,lottery_win l WHERE l.uid = u.uid AND l.lottery_id = ");
    	sb.append(lotteryId);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    
    public List<Map<String,Object>> getJoinLotteryUsers(long lotteryId,long sinceId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select l.id AS sinceId,u.uid,u.avatar,u.avatar_frame,u.nick_name,u.v_lv,u.level,l.content,p.id AS prohibit,l.create_time ");
    	sb.append(" FROM (lottery_content l,user_profile u) LEFT JOIN lottery_prohibit p ON p.lottery_id=l.lottery_id AND u.uid = p.uid  ");
    	sb.append("  WHERE l.uid = u.uid AND l.lottery_id =  ");
    	sb.append(lotteryId);
    	if(sinceId!=-1){
    		sb.append(" and l.id<").append(sinceId);
    	}
    	sb.append(" ORDER BY l.id DESC limit 20");
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String,Object>> getRandomLotteryUser(long lotteryId,int winNumber){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select DISTINCT c.uid FROM lottery_content c WHERE c.lottery_id = ");
    	sb.append(lotteryId);
    	sb.append(" AND c.uid NOT IN  (SELECT uid FROM lottery_prohibit WHERE lottery_id = ");
    	sb.append(lotteryId);
    	sb.append(") ORDER BY RAND() LIMIT ");
    	sb.append(winNumber);
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String,Object>> getDistinctLotteryUser(long lotteryId){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select DISTINCT c.uid FROM lottery_content c WHERE c.lottery_id = ");
    	sb.append(lotteryId);
    	sb.append(" AND c.uid NOT IN  (SELECT uid FROM lottery_prohibit WHERE lottery_id = ");
    	sb.append(lotteryId);
    	sb.append(") ");
    	String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
    }
    
    public List<Map<String, Object>> get24HourNewUserIdByUids(List<Long> uidList){
    	if(null == uidList || uidList.size() == 0){
    		return null;
    	}
    	String time = DateUtil.date2string(DateUtil.addDay(new Date(), -1), "yyyy-MM-dd HH:mm:ss");
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.uid from user_profile t where t.create_time>'");
    	sb.append(time).append("' and t.uid in (");
    	for(int i=0;i<uidList.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(uidList.get(i).toString());
    	}
    	sb.append(")");
    	return jdbcTemplate.queryForList(sb.toString());
    }
    
    public List<Long> getOver2WinUidByUids(List<Long> uidList){
    	List<Long> result = new ArrayList<Long>();
    	if(null == uidList || uidList.size() == 0){
    		return result;
    	}
    	String time = DateUtil.date2string(new Date(), "yyyy-MM-dd");
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.uid from lottery_win t where t.create_time>='").append(time).append(" 00:00:00'");
    	sb.append(" and t.uid in (");
    	for(int i=0;i<uidList.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(uidList.get(i).toString());
    	}
    	sb.append(") group by t.uid having count(1)>1");
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> u : list){
    			result.add((Long)u.get("uid"));
    		}
    	}
    	return result;
    }

	public void addAppDownloadLog(long uid, long fromUid) {
		String sql ="insert into app_download_log(uid,fromUid,create_time) values(?,?,now())";
		jdbcTemplate.update(sql,uid,fromUid);
		
	}

	public void updateContentReadCount(long cid, int addReadCount, int addReadCountDummy){
		String sql = "update content set read_count=read_count+"+addReadCount+",read_count_dummy=read_count_dummy+"+addReadCountDummy+" where id="+cid;
		jdbcTemplate.execute(sql);
	}
	
	public List<Map<String, Object>> getLastOutFragment(List<Long> topicIds, int limitMinute, List<Long> privateTopicIds){
		if(null == topicIds || topicIds.size() == 0 || limitMinute<=0){
    		return null;
    	}else{
    		if(null != privateTopicIds && privateTopicIds.size() > 0){
    			for(int i=0;i<topicIds.size();i++){
    				if(privateTopicIds.contains(topicIds.get(i))){
    					topicIds.remove(i);
    					i--;
    				}
    			}
    			if(topicIds.size() == 0){
    				return null;
    			}
    		}
    	}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select f2.* from topic_fragment f2,(select f.topic_id,max(f.id) maxid");
		sb.append(" from topic_fragment f,topic t where f.topic_id=t.id and f.topic_id in (");
		for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i).toString());
    	}
		sb.append(") and f.status=1 and f.out_type=1");
		sb.append(" and f.create_time>=date_add(t.out_time, interval -").append(limitMinute);
		sb.append(" minute) group by f.topic_id) m where f2.id=m.maxid");
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public void updateTopicUpdateTime(long topicId, Date updateTime, long longTime){
		String sql = "update topic set update_time=?,long_time=? where id=? and long_time<?";
		jdbcTemplate.update(sql, updateTime,longTime,topicId,longTime);
	}
	
	public void updateTopicOutTime(long topicId, Date outTime){
		String sql = "update topic set out_time=? where id=? and out_time<?";
		jdbcTemplate.update(sql, outTime,topicId,outTime);
	}
	
	public void updateUserLikeTagScore(long uid, String tag, int score) {
		jdbcTemplate.update("update user_tag_like set score=score+? where uid=? and tag=?",score,uid,tag);
		
	}
	public void updateUserLikeTagScoreTo0(long uid, String tag) {
		jdbcTemplate.update("update user_tag_like set score=0 where uid=? and tag=?",uid,tag);		
	}

	public boolean isBadTag(long topicId, String tag) {
		int count =jdbcTemplate.queryForObject("select count(1) from topic_bad_tag where topic_id=? and tag=?",new Object[]{topicId,tag},Integer.class);
		return count>0;
	}
	
	public void batchInsertUserLikeTags(Long uid, Set<Long> userHobbyTags) {
		if(null == userHobbyTags || userHobbyTags.size() == 0){
			return;
		}
		StringBuilder delSql = new StringBuilder();
		delSql.append("delete from user_dislike where uid=? and is_like=1 and type=2 and data in (");
		int i=0;
		for(Long tag:userHobbyTags){
			if(i>0){
				delSql.append(",");
			}
			delSql.append(tag.toString());
			i++;
		}
		delSql.append(")");
		jdbcTemplate.update(delSql.toString(),uid);
		String sql ="insert into user_dislike (uid,data,is_like,type,create_time) values(?,?,1,2,now())";
		List<Object[]> batchArgs = new ArrayList<>();
		for(Long tag:userHobbyTags){
			batchArgs.add(new Object[]{uid,tag});
		}
		jdbcTemplate.batchUpdate(sql, batchArgs );
	}
	
	public List<Map<String, Object>> getHarvestKingdomListByUid(long uid, int start, int pageSize){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.*,d.harvest_price from topic t,topic_data d");
		sb.append(" where t.id=d.topic_id and t.uid=? and t.id not in (select l.topic_id from topic_listed l)");
		sb.append(" order by d.harvest_price desc,t.id");
		sb.append(" limit ?,?");
		
		return jdbcTemplate.queryForList(sb.toString(), uid,start,pageSize);
	}
	
	public int countMyKingdom(long uid){
		String sql = "select count(1) from topic t where t.uid=?";
		return jdbcTemplate.queryForObject(sql,new Object[]{uid},Integer.class);
	}
	
	public void decrTopicPrice(long topicId, int decrPrice){
		String sql = "update topic set price=(CASE WHEN price>? then price-? ELSE 0 END) where id=?";
		jdbcTemplate.update(sql, decrPrice,decrPrice,topicId);
	}
	
	public void balanceTopicPriceAndStealPrice(long topicId){
		String sql = "update topic_data,topic set steal_price=(CASE WHEN topic_data.steal_price>topic.price then topic.price ELSE topic_data.steal_price END) where topic.id=topic_data.topic_id and topic_data.topic_id=?";
		jdbcTemplate.update(sql, topicId);
	}
	
	public void decrTopicHarvestPrice(long topicId, int decrPrice){
		String sql = "update topic_data set harvest_price=(CASE WHEN harvest_price>? then harvest_price-? ELSE 0 END) where topic_id=?";
		jdbcTemplate.update(sql, decrPrice,decrPrice,topicId);
	}

	public List<String> getUserLikeTags(long uid) {
        List<String> retList = new ArrayList<>();
        String sql ="select tag from topic_tag where id in(select data from user_dislike where uid=? and is_like=1 and type=2)";
        List<Map<String,Object>> ret =jdbcTemplate.queryForList(sql,uid);
        for(Map<String,Object> item:ret){
        	retList.add((String)item.get("tag"));
        }
		return retList;
	}
	
	public void removeUserDislikeTags(long uid, List<Long> tagIds){
		if(null == tagIds || tagIds.size() == 0){
			return;
		}
		StringBuilder delSql = new StringBuilder();
		delSql.append("delete from user_dislike where uid=").append(uid);
		delSql.append(" and is_like=0 and type=2 and data in (");
		for(int i=0;i<tagIds.size();i++){
			if(i>0){
				delSql.append(",");
			}
			delSql.append(tagIds.get(i).toString());
		}
		delSql.append(")");
		jdbcTemplate.update(delSql.toString());
	}
}
