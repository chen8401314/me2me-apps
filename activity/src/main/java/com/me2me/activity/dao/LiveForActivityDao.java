package com.me2me.activity.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
		sb.append(" and not exists(select 1 from a_topic a where a.type=2 and a.uid=t.uid)");
		sb.append(" and not exists(select 1 from a_recommend_user_desc d where d.uid=");
		sb.append(myUid).append(" and d.rec_uid=t.uid)");
		//不连续的mysql随机，如果用random()方法，效率很低。。这里先这么搞着吧。。
		Random r = new Random();
		int rd = r.nextInt(4);
		String p = null;
		if(rd == 0){
			p = "p.mobile";
		}else if(rd == 1){
			p = "p.avatar";
		}else if(rd == 2){
			p = "t.uid";
		}else{
			p = "t.create_time";
		}
		String d = "";
		int dd = r.nextInt(2);
		if(dd == 0){
			d = " desc";
		}else{
			d = " asc";
		}
		sb.append(" order by ").append(p).append(d).append(" limit ").append(count);
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
	
	public Map<String, Object> getUserTokenInfo(long uid){
		String sql = "select u.nick_name,t.token from user_profile u,user_token t where u.uid=t.uid and t.uid="+uid;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Long> getForcedPairingUser(){
		String sql = "select t.uid from a_topic t where not EXISTS (select 1 from a_topic p where p.type=2 and p.status=0 and p.uid=t.uid)";
		sql = sql + " and t.type=1 and t.status=0";
		List<Long> result = new ArrayList<Long>();
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			for(Map<String,Object> m : list){
				result.add((Long)m.get("uid"));
			}
		}
		
		return result;
	}
	
	public Map<String, Object> get7dayUserStat(String channel, String code, String startTime, String endTime){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as total,count(if(t.sex=0,TRUE,NULL)) as womanCount,count(if(t.sex=1,TRUE,NULL)) as manCount,");
		sb.append("count(if(t.uid>0,TRUE,NULL)) as bindCount from a_user t where 1=1");
		if(!StringUtils.isEmpty(channel)){
			if(!StringUtils.isEmpty(code)){
				sb.append(" and t.channel='").append(channel).append("=").append(code).append("'");
			}else{
				sb.append(" and t.channel like '").append(channel).append("=%'");
			}
		}
		if(!StringUtils.isEmpty(startTime)){
			sb.append(" and t.create_time>='").append(startTime).append("'");
		}
		if(!StringUtils.isEmpty(endTime)){
			sb.append(" and t.create_time<='").append(endTime).append("'");
		}
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Map<String, Object>> get7dayUsers(String channel, String code, String startTime, String endTime, int start, int pageSize){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.*,(select count(1) from topic p where p.uid=t.uid) as kingdomCount");
		sb.append(" from a_user t where 1=1");
		if(!StringUtils.isEmpty(channel)){
			if(!StringUtils.isEmpty(code)){
				sb.append(" and t.channel='").append(channel).append("=").append(code).append("'");
			}else{
				sb.append(" and t.channel like '").append(channel).append("=%'");
			}
		}
		if(!StringUtils.isEmpty(startTime)){
			sb.append(" and t.create_time>='").append(startTime).append("'");
		}
		if(!StringUtils.isEmpty(endTime)){
			sb.append(" and t.create_time<='").append(endTime).append("'");
		}
		sb.append(" order by t.channel limit ").append(start).append(",").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
}
