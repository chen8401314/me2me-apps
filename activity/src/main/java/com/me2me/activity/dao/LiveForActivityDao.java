package com.me2me.activity.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LiveForActivityDao {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public Map<String,Object> getTopicById(long id){
        String sql = "select * from topic where id="+id;
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        if(null != list && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
	
	public List<Map<String,Object>> getRecSingleUser(int searchSex, long myUid, int count){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.auid,t.topic_id,t.uid,p.avatar,p.v_lv");
		sb.append(" from a_topic t, user_profile p");
		sb.append(" where p.gender=").append(searchSex);
		sb.append(" and t.type=1 and t.uid=p.uid");
		sb.append(" and t.uid<>").append(myUid);
		sb.append(" and t.uid not in (select d.rec_uid from a_recommend_user_desc d where d.auid=t.auid)");
		sb.append(" and t.uid not in (select a.uid from a_topic a where a.uid=t.uid and a.type=2)");
		sb.append(" order by p.mobile limit ").append(count);
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		return list;
	}
	
	public Map<String,Object> getLastApply(long uid, int type){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from a_double_topic_apply");
		sb.append(" where ((uid=").append(uid).append(" and status in (2,3)) or (target_uid=");
		sb.append(uid).append(" and status in (1,4))) and type=").append(type);
		sb.append(" order by create_time desc limit 1");
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public Map<String, Object> getLastTargetDouble(long uid){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from a_topic t where t.uid in");
		sb.append(" (select target_uid from a_double_topic_apply d where d.uid=");
		sb.append(uid).append(" and d.status=1) and t.type=2 and t.status=0");
		sb.append(" order by create_time desc limit 1");
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Map<String, Object>> getAtopicInfoByUids(List<Long> uids, int type){
		if(null == uids || uids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select p.uid,t.topic_id,p.title,p.live_image,t.hot");
		sb.append(" from topic p,a_topic t");
		sb.append(" where p.id=t.topic_id and t.type=").append(type);
		sb.append(" and t.status=0 and t.uid in (");
		for(int i=0;i<uids.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(uids.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
}
