package com.me2me.search.dao;

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
}
