package com.me2me.activity.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.me2me.activity.dto.KingdomHotDTO;
import com.me2me.activity.dto.TopicItem;

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

	public Map<String,Object> getContentByForwordCid(long id){
		String sql = "select * from content where forward_cid="+id;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Map<String,Object>> getTopicsByIds(List<Long> ids){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic where id in (");
		for(int i=0;i<ids.size();i++){
			if(i>0){
				sb.append(",");
			}
			sb.append(ids.get(i));
		}
		sb.append(")");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public void insertTopicFragment(Map<String, String> param){
		if(null == param){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into topic_fragment(topic_id,uid,fragment_image,fragment,type,content_type,at_uid,status,extra)");
		sb.append(" values(").append(param.get("topic_id")).append(",").append(param.get("uid"));
		sb.append(",'").append(param.get("fragment_image")).append("','").append(param.get("fragment"));
		sb.append("',").append(param.get("type")).append(",").append(param.get("content_type"));
		sb.append(",").append(param.get("at_uid")).append(",").append(param.get("status")).append(",'").append(param.get("extra")).append("')");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	public void updateTopicLongtime(long topicId, long longTime){
		StringBuilder sb = new StringBuilder();
		sb.append("update topic set long_time=").append(longTime);
		sb.append(" where id=").append(topicId);
		jdbcTemplate.execute(sb.toString());
	}
	
	public List<Map<String,Object>> getRecSingleUser(int searchSex, long myUid, int count){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.auid,t.topic_id,t.uid,p.avatar,p.v_lv");
		sb.append(" from a_topic t, user_profile p");
		sb.append(" where p.gender=").append(searchSex);
		sb.append(" and t.type=1 and t.status=0 and t.uid=p.uid");
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
	
	public List<Long> getPairingUser(){
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
		sb.append(" order by t.create_time desc limit ").append(start).append(",").append(pageSize);
		
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	public Map<String, Object> getTopicCount(long topicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select m.read_count_dummy,m.like_count,n.updateCount,n.reviewCount from (");
		sb.append("select t.read_count_dummy,t.like_count,t.forward_cid from content t where t.forward_cid=").append(topicId);
		sb.append(" and t.type=3) m,(select count(if(f.type=0,TRUE,NULL)) as updateCount, count(if(f.type>0,TRUE,NULL)) as reviewCount, f.topic_id ");
		sb.append("from topic_fragment f where f.topic_id=").append(topicId).append(" and f.status=1) n ");
		sb.append("where m.forward_cid=n.topic_id");

		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Map<String, Object>> getTopicCountsByTopicIds(List<Long> topicIds, String startTime, String endTime){
		if(null == topicIds || topicIds.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select count(if(f.type in (0,12,13,15),TRUE,NULL)) as updateCount, ");
		sb.append("count(if(f.type not in (0,12,13,15),TRUE,NULL)) as reviewCount, f.topic_id ");
		sb.append("from topic_fragment f where f.topic_id in (");
		for(int i=0;i<topicIds.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(topicIds.get(i));
		}
		sb.append(") and f.status=1 ");
		if(null != startTime && !"".equals(startTime)){
			sb.append("and f.create_time>='").append(startTime).append("' ");
		}
		if(null != endTime && !"".equals(endTime)){
			sb.append("and f.create_time<='").append(endTime).append("' ");
		}
		sb.append("group by f.topic_id");
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		return list;
	}
	
	public List<Long> get7DayTopicIdsByType(int type){
		String sql = "select DISTINCT t.topic_id from a_topic t where t.status=0 and t.type="+type;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			List<Long> result = new ArrayList<Long>();
			for(Map<String,Object> map : list){
				result.add((Long)map.get("topic_id"));
			}
			return result;
		}
		return null;
	}
	
	public List<TopicItem> getActivityTopicIds(long activityId){
		String sql = "select k.topic_id,k.uid from a_kingdom k where k.status=0 and k.activity_id=" + activityId;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			List<TopicItem> result = new ArrayList<TopicItem>();
			TopicItem item = null;
			for(Map<String,Object> m : list){
				item = new TopicItem();
				item.setTopicId((Long)m.get("topic_id"));
				item.setUid((Long)m.get("uid"));
				result.add(item);
			}
			return result;
		}
		
		return null;
	}
	
	public List<Long> getSingleHotsByDoubleTopicId(long doubleTopicId){
		StringBuilder sb = new StringBuilder();
		sb.append("select a.hot from a_topic a where a.status=0 and a.type=1 ");
		sb.append("and a.uid in (select t.uid from a_topic t where t.status=0 ");
		sb.append("and t.topic_id=").append(doubleTopicId).append(")");
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			List<Long> result = new ArrayList<Long>();
			for(Map<String,Object> map : list){
				result.add((Long)map.get("hot"));
			}
			return result;
		}
		
		return null;
	}
	
	public void updateTopicHot(long topicId, int hot){
		String sql = "update a_topic set hot="+hot+" where topic_id="+topicId;
		jdbcTemplate.execute(sql);
	}
	
	public List<Long> get7dayKingdomUpdateUids(long time){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.uid from a_topic t,topic p where t.topic_id=p.id and t.status=0 ");
		sb.append("and p.long_time<").append(time);
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(null != list && list.size() > 0){
			List<Long> result = new ArrayList<Long>();
			for(Map<String,Object> map : list){
				result.add((Long)map.get("uid"));
			}
			return result;
		}
		return null;
	}
	
	public void updateDeleteAkingdomByTopicId(long topicId){
		String sql = "update a_kingdom set status=1 where topic_id="+topicId;
		jdbcTemplate.execute(sql);
	}
	
	public void deleteAkingdomListByTopicId(long topicId){
		String sql = "delete from a_kingdom_list where topic_id="+topicId;
		jdbcTemplate.execute(sql);
	}
	
	public void updateContentAddLike(long topicId, int likeCount){
		String sql = "update content set like_count=like_count+"+likeCount+" where forward_cid="+topicId+" and type=3";
		jdbcTemplate.execute(sql);
	}
	
	public void deleteKingdomListByDayKey(String dayKey){
		String sql = "delete from a_kingdom_list where day_key='"+dayKey+"'";
		jdbcTemplate.execute(sql);
	}
	
	public List<Long> getAllKingdomUids(){
		String sql = "select DISTINCT t.uid from a_kingdom t where t.status=0";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			List<Long> uids = new ArrayList<Long>();
			for(Map<String,Object> m : list){
				uids.add((Long)m.get("uid"));
			}
			return uids;
		}
		return null;
	}
	
	public List<Map<String, Object>> getSinglePerson(){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.auid,t.uid,u.gender,r.mobile,u.nick_name ");
		sb.append("from a_topic t,user_profile u,topic o,a_user r ");
		sb.append("where t.uid=r.uid and t.topic_id=o.id and t.uid=u.uid and t.status=0 and t.type=1 ");
		sb.append("and not EXISTS (select 1 from a_topic p where p.status=0 and p.type=2 and p.uid=t.uid) ");
		sb.append("and not EXISTS (select 1 from a_forced_pairing f where f.uid=t.uid or f.target_uid=t.uid) ");
		sb.append("order by o.long_time desc");

		List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		return list;
	}
	
	public void batchInsertKingdomList(List<KingdomHotDTO> list){
		if(null == list || list.size() == 0){
    		return;
    	}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into a_kingdom_list(day_key,uid,topic_id,activity_id,hot,conditions) values");
		KingdomHotDTO dto = null;
		for(int i=0;i<list.size();i++){
			dto = list.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append("('").append(dto.getDayKey()).append("',").append(dto.getUid()).append(",");
			sb.append(dto.getTopicId()).append(",").append(dto.getActivityId()).append(",");
			sb.append(dto.getHot()).append(",").append(dto.getConditions()).append(")");
		}
		jdbcTemplate.execute(sb.toString());
	}
	
	public void batchUpdateKingdomHot(List<KingdomHotDTO> list){
		if(null == list || list.size() == 0){
    		return;
    	}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		KingdomHotDTO dto = null;
		for(int i=0;i<list.size();i++){
			dto = list.get(i);
			sb = new StringBuilder();
			sb.append("update a_kingdom set hot=").append(dto.getHot());
			sb.append(",conditions=").append(dto.getConditions());
			sb.append(" where topic_id=").append(dto.getTopicId());
			sb.append(" and status=0");
			insertSqls[i] = sb.toString();
		}
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void updateKingdomHotInit(List<Long> topicIds){
		if(null == topicIds || topicIds.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update a_kingdom set hot=0,conditions=0 where topic_id in (");
		for(int i=0;i<topicIds.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(topicIds.get(i));
		}
		sb.append(")");
		jdbcTemplate.execute(sb.toString());
	}
	
	public List<String> getAllUserMobilesInApp(){
		String sql = "select DISTINCT t.mobile from user_profile t where t.third_part_bind like '%mobile%' order by t.mobile";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			List<String> result = new ArrayList<String>();
			String m = null;
			for(Map<String,Object> map : list){
				m = (String)map.get("mobile");
				if(!StringUtils.isEmpty(m)){
					result.add(m);
				}
			}
			return result;
		}
		return null;
	}
}
