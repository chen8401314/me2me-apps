package com.me2me.search.dao;

import java.util.ArrayList;
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
	
	public Map<String, Object> getTopicByUidAndTitle(long uid, String title){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic t where t.uid=").append(uid);
		sb.append(" and t.title='").append(title).append("'");
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public Map<String, Object> getTopicContentByTopicId(long topicId){
		String sql = "select * from content t where t.forward_cid=" + topicId + " and t.type=3";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public boolean isFavoriteTopic(long uid, long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from live_favorite f where f.uid=").append(uid);
		sb.append(" and f.topic_id=").append(topicId);

		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return true;
		}
		return false;
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
	
	/**
	 * 获取王国成员数（不包含国王）
	 * @param topicId
	 * @return
	 */
	public int getSingleTopicMemberCount(long topicId){
		int result = 0;
		//获取非核心圈人数
		StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as cc from live_favorite f,topic t");
    	sb.append(" where f.topic_id=t.id ");
    	sb.append(" and not FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
    	sb.append(" and f.topic_id=").append(topicId);
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	if(null != list && list.size() > 0){
    		Map<String, Object> cl = list.get(0);
    		result = result + ((Long)cl.get("cc")).intValue();
    	}
		//获取核心圈人数（不包含国王）
    	StringBuilder sb2 = new StringBuilder();
    	sb2.append("select t.id, LENGTH(t.core_circle)-LENGTH(replace(t.core_circle,',','')) as coreCount");
    	sb2.append(" from topic t where t.id=").append(topicId);
    	list = jdbcTemplate.queryForList(sb2.toString());
    	if(null != list && list.size() > 0){
    		Map<String, Object> cl2 = list.get(0);
    		result = result + ((Long)cl2.get("coreCount")).intValue();
    	}
    	return result;
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
	
	public int getSingleTopicAggregationAcCount(long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as cc");
		sb.append(" from topic_aggregation t");
		sb.append(" where t.topic_id=").append(topicId);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			Map<String, Object> l = list.get(0);
			return ((Long)l.get("cc")).intValue();
		}
		return 0;
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
	
	public List<String> getSingleTopicTag(long topicId){
		StringBuilder sb = new StringBuilder();
    	sb.append("select * from topic_tag_detail d where d.status=0");
    	sb.append(" and d.topic_id=").append(topicId);
    	sb.append(" order by id asc");
    	
    	List<String> result = new ArrayList<String>();
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> map : list){
    			result.add((String)map.get("tag"));
    		}
    	}
    	
    	return result;
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
	
	/**
	 * 获取用户情绪王国
	 * @param uid
	 * @return
	 */
	public Map<String, Object> getUserEmotionKingdom(long uid){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic t where t.uid=").append(uid);
		sb.append(" and t.sub_type=1");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Long> getInitUserByType(String type){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.uid from user_rec_init t where t.type='").append(type).append("'");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		List<Long> result = new ArrayList<Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.add((Long)m.get("uid"));
			}
		}
		return result;
	}
	
	public List<Map<String, Object>> getUserProfileInfoByUids(List<Long> uids){
		if(null == uids || uids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from user_profile u where u.uid in (");
		for(int i=0;i<uids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uids.get(i).toString());
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public Map<String, List<Long>> getUserHobbyIdsByUids(List<Long> uids){
		Map<String, List<Long>> result = new HashMap<String, List<Long>>();
		if(null == uids || uids.size() == 0){
			return result;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from user_hobby h where h.uid in (");
		for(int i=0;i<uids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uids.get(i));
		}
		sb.append(") order by h.uid");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			String uid = null;
			List<Long> hobbyIds = null;
			for(Map<String, Object> m : list){
				uid = String.valueOf(m.get("uid"));
				hobbyIds = result.get(uid);
				if(null == hobbyIds){
					hobbyIds = new ArrayList<Long>();
					result.put(uid, hobbyIds);
				}
				hobbyIds.add((Long)m.get("hobby"));
			}
		}
		
		return result;
	}
	
	/**
	 * 获取{sourceUid}所建立王国拥有相同标签的其他国王
	 * @param sourceUid
	 * @param uidList
	 * @return
	 */
	public List<Long> getSameTagTopicKing(long sourceUid, List<Long> uidList){
		if(null == uidList || uidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select DISTINCT t.uid from topic_tag_detail d,topic t");
		sb.append(" where d.topic_id=t.id and d.status=0 and d.tag_id in (");
		sb.append("select DISTINCT d2.tag_id from topic_tag_detail d2,topic t2");
		sb.append(" where d2.topic_id=t2.id and d2.status=0 and t2.uid=").append(sourceUid);
		sb.append(") and t.uid in (");
		for(int i=0;i<uidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uidList.get(i).toString());
		}
		sb.append(")");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		List<Long> result = new ArrayList<Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.add((Long)m.get("uid"));
			}
		}
		return result;
	}
	
	public Map<String, Long> getUserLastEmotionId(List<Long> uidList){
		if(null == uidList || uidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from emotion_record r2 where r2.id in (");
		sb.append("select max(r.id) from emotion_record r where r.uid in (");
		for(int i=0;i<uidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uidList.get(i).toString());
		}
		sb.append(") group by r.uid)");

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		Map<String, Long> result = new HashMap<String, Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.put(String.valueOf(m.get("uid")), (Long)m.get("emotionId"));
			}
		}
		return result;
	}
	
	public Map<String, Long> getSameFollowCountByUids(long sourceUid, List<Long> uidList){
		if(null == uidList || uidList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select f2.source_uid,count(1) as cc from user_follow f2");
		sb.append(" where f2.source_uid in (");
		for(int i=0;i<uidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uidList.get(i).toString());
		}
		sb.append(") and f2.target_uid in (select f.target_uid from user_follow f");
		sb.append(" where f.source_uid=").append(sourceUid);
		sb.append(") group by f2.source_uid");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		Map<String, Long> result = new HashMap<String, Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.put(String.valueOf(m.get("source_uid")), (Long)m.get("cc"));
			}
		}
		return result;
	}
	
	public Map<String, Long> getSameJoinTopicCountByUids(long sourceUid, List<Long> uidList){
		if(null == uidList || uidList.size() == 0){
			return null;
		}
		//先查王国
		String sql1 = "select f.topic_id from live_favorite f where f.uid=?";
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql1, sourceUid);
		if(null == list1 || list1.size() == 0){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select f2.uid,count(1) as cc from live_favorite f2");
		sb.append(" where f2.uid in (");
		for(int i=0;i<uidList.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(uidList.get(i).toString());
		}
		sb.append(") and f2.topic_id in (");
		Map<String, Object> mm = null;
		for(int i=0;i<list1.size();i++){
			mm = list1.get(i);
			if(i>0){
				sb.append(",");
			}
			sb.append(((Long)mm.get("topic_id")).toString());
		}
		sb.append(") group by f2.uid");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
		Map<String, Long> result = new HashMap<String, Long>();
		if(null != list && list.size() > 0){
			for(Map<String, Object> m : list){
				result.put(String.valueOf(m.get("uid")), (Long)m.get("cc"));
			}
		}
		return result;
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
}
