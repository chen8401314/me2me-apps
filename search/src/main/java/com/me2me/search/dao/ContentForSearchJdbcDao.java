package com.me2me.search.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContentForSearchJdbcDao {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> getUGCContentByIds(List<Long> cidList){
		if(null == cidList || cidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from content c where c.status=0 and c.rights=1 and c.id in (");
		for(int i=0;i<cidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(cidList.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getTopicContentByTopicIds(List<Long> tidList){
		if(null == tidList || tidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from content c where c.status=0 and c.type=3 and c.forward_cid in (");
		for(int i=0;i<tidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(tidList.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public List<Map<String, Object>> getTopicByIds(List<Long> tidList){
		if(null == tidList || tidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic t where t.status=0 and t.id in (");
		for(int i=0;i<tidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(tidList.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public Map<String, Object> getTopicById(long id){
		String sql = "select * from topic t where t.id=" + id;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Map<String,Object>> getLiveFavoritesByUidAndTopicIds(long uid, List<Long> topicIds){
    	if(null == topicIds || topicIds.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select * from live_favorite t where t.uid=");
    	sb.append(uid).append(" and t.topic_id in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i));
    	}
    	sb.append(")");
    	
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
	
	public List<Map<String, Object>> getTopicTagDetailListByTopicIds(List<Long> topicIds){
    	if(null == topicIds || topicIds.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select * from topic_tag_detail d where d.status=0");
    	sb.append(" and d.topic_id in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i).longValue());
    	}
    	sb.append(") order by topic_id asc,id asc");
    	
    	return jdbcTemplate.queryForList(sb.toString());
    }
	
	public String getTopicTagsByTopicId(long topicId){
		String sql = "select * from topic_tag_detail d where d.status = 0 and d.topic_id="+topicId;
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		StringBuilder sb = new StringBuilder();
		if(null != list && list.size() > 0){
			for(int i=0;i<list.size();i++){
				if(i>0){
					sb.append(";");
				}
				sb.append((String)list.get(i).get("tag"));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取用户设置情绪次数
	 * @param uid
	 * @param userEmotionId 如果>0则表示查询当前userEmotionId是第几次设置；如果<=0则表示查询所有次数
	 * @return
	 */
	public int countUserEmotions(long uid, long userEmotionId){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as cc from emotion_record t");
		sb.append(" where t.uid=").append(uid);
		if(userEmotionId > 0){
			sb.append(" and t.id<=").append(userEmotionId);
		}
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			Map<String, Object> c = list.get(0);
			return ((Long)c.get("cc")).intValue();
		}
		return 0;
	}
	
	/**
	 * 根据Id获取表情包表情
	 * @param eids
	 * @return
	 */
	public List<Map<String, Object>> getEmotionsByIds(List<Long> eids){
		if(null == eids || eids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from emotion_pack_detail d where d.id in (");
		for(int i=0;i<eids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(eids.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public Map<String, Object> getUserEmotionKingdom(long uid){
		return null;
	}
}
