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
	
	public void updateContentAddFavoriteCountByForwardCid(int count, long forwardCid){
		if(count < 1){
			return;
		}
		String sql = "update content set favorite_count=favorite_count+"+count+" where forward_cid="+forwardCid;
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
	public List<Topic> searchTopics(KingdomSearchDTO searchDTO, int topType){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.* from topic t");
		if(searchDTO.getTopicId() > 0){
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
				sb.append(" where t.id=a.sub_topic_id and a.id=0");
			}
		}
		
		sb.append(" and t.long_time<").append(searchDTO.getUpdateTime());
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
		if(searchDTO.getSearchUid() > 0){
			if(searchDTO.getAllowCore() > 0){
				sb.append(" and (t.uid=").append(searchDTO.getSearchUid());
				sb.append(" or FIND_IN_SET(").append(searchDTO.getSearchUid());
				sb.append(",SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2)))");
			}else{
				sb.append(" and t.uid=").append(searchDTO.getSearchUid());
			}
		}
		if(searchDTO.getTopicId() > 0 && searchDTO.getTopicType() == 2 && topType == 1){//母查置顶的子,则查出所有的置顶项并按置顶时间倒序
			sb.append(" order by a.update_time desc");
		}else{
			sb.append(" order by t.long_time desc limit 10");
		}
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			List<Topic> result = new ArrayList<Topic>();
			Topic topic = null;
			for(Map<String,Object> t : list){
				topic = new Topic();
				topic.setId((Long)t.get("id"));
				topic.setUid((Long)t.get("uid"));
				topic.setLiveImage((String)t.get("live_image"));
				topic.setTitle((String)t.get("title"));
				topic.setStatus((Integer)t.get("status"));
				topic.setCreateTime((Date)t.get("create_time"));
				topic.setUpdateTime((Date)t.get("update_time"));
				topic.setLongTime((Long)t.get("long_time"));
				topic.setQrcode((String)t.get("qrcode"));
				topic.setCoreCircle((String)t.get("core_circle"));
				topic.setType((Integer)t.get("type"));
				topic.setCeAuditType((Integer)t.get("ce_audit_type"));
				topic.setAcAuditType((Integer)t.get("ac_audit_type"));
				topic.setAcPublishType((Integer)t.get("ac_publish_type"));
				topic.setRights((Integer)t.get("rights"));
				topic.setSummary((String)t.get("summary"));
				result.add(topic);
			}
			return result;
		}
		return null;
	}
	
	public List<Map<String, Object>> getTopicUpdateCount(List<Long> tids){
		if(null == tids || tids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select f.topic_id, count(1) as total,count(if(t.uid=f.uid,TRUE,NULL)) as topicCount,");
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
}
