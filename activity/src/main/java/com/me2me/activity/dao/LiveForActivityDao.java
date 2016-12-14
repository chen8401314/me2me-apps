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
}
