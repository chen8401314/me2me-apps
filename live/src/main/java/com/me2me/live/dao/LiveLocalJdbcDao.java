package com.me2me.live.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LiveFavoriteDelete;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicBarrage;

@Repository
public class LiveLocalJdbcDao {

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
			//查询我创建的个人王国+我是核心圈的个人王国+我订阅的个人王国
			sb.append("select t.* from (");
			sb.append("select m.*,m.long_time*10000 as longtime");
			sb.append(" from topic m where m.type=0 and m.uid=").append(currentUid);
			sb.append(" union ");
			sb.append("select m1.*,m1.long_time*100 as longtime");
			sb.append(" from topic m1 where m1.type=0 and m1.uid<>").append(currentUid);
			sb.append(" and FIND_IN_SET(").append(currentUid);
			sb.append(",SUBSTR(m1.core_circle FROM 2 FOR LENGTH(m1.core_circle)-2))");
			sb.append(" union ");
			sb.append("select m2.*,m2.long_time as longtime");
			sb.append(" from topic m2,live_favorite f");
			sb.append(" where m2.type=0 and m2.id=f.topic_id");
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
	
	public List<Map<String, Object>> getTopSubTopic(long topicId, int limit){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.* from topic t,topic_aggregation a ");
		sb.append("where t.id=a.sub_topic_id and a.topic_id=").append(topicId);
		sb.append(" and a.is_top=1 order by a.update_time desc limit ").append(limit);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getNoTopSubTopic(long topicId, int limit){
		StringBuilder sb = new StringBuilder();
		sb.append("select t1.* from topic t1,topic_aggregation a1 ");
		sb.append("where t1.id=a1.sub_topic_id and a1.topic_id=").append(topicId);
		sb.append(" and a1.is_top=0 order by t1.long_time desc limit ").append(limit);
		
		return jdbcTemplate.queryForList(sb.toString());
	}

}
