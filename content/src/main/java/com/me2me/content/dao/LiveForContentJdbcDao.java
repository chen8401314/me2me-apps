package com.me2me.content.dao;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.content.dto.BillBoardListDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :11:51
 */
@Repository
@Slf4j
public class LiveForContentJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 记录删除日志
     * @param type	类型
     * @param oid	操作对象ID
     * @param uid	删除人UID
     */
    public void insertDeleteLog(int type, long oid, long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("insert into delete_log(type,oid,uid,del_time) values(");
    	sb.append(type).append(",").append(oid).append(",");
    	sb.append(uid).append(",now())");
    	String sql = sb.toString();
    	jdbcTemplate.execute(sql);
    }
    
    public List<Map<String,Object>> getTopicListByIds(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic where id in (");
		for(int i=0;i<ids.size();i++){
			if(i > 0){
				sb.append(",");
			}
			sb.append(ids.get(i).longValue());
		}
		sb.append(")");
		String sql = sb.toString();
		return jdbcTemplate.queryForList(sql);
	}

	public Map<String,Object> getTopicListByCid(long cid){
		String sql = "select id,core_circle,type from topic where id = "+cid;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size() > 0 && list != null){
			return list.get(0);
		}
		return null;
	}

	public int getTopicAggregationCountByTopicId(long topicId){
		String sql = "select count(1) as count from topic_aggregation where topic_id = "+topicId;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return Integer.valueOf(list.get(0).get("count").toString());
	}
    
    public List<Map<String,Object>> getUserTopicPageByUid(long uid, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select id,uid,live_image,title,status,create_time,update_time,core_circle from topic ");
    	sb.append("where uid=").append(uid).append(" order by id desc limit ").append(start).append(",").append(pageSize);
    	return jdbcTemplate.queryForList(sb.toString());
    }
    
    public int countUserTopicPageByUid(long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as count from topic ");
    	sb.append("where uid=").append(uid);
    	return Integer.valueOf(this.jdbcTemplate.queryForList(sb.toString()).get(0).get("count").toString());
    }
    
    public List<Map<String,Object>> getUserTopicFragmentPageByUid(long uid, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select id,topic_id,uid,fragment_image,fragment,type,content_type,create_time,extra,status from topic_fragment ");
    	sb.append(" where uid=").append(uid).append(" order by topic_id desc, id desc limit ").append(start).append(",").append(pageSize);
    	return jdbcTemplate.queryForList(sb.toString());
    }
    
    public int countUserTopicFragmentPageByUid(long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as count from topic_fragment ");
    	sb.append(" where uid=").append(uid);
    	return Integer.valueOf(this.jdbcTemplate.queryForList(sb.toString()).get(0).get("count").toString());
    }
    
    public void deleteTopicFragmentById(long id){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update topic_fragment set status=0 where id=").append(id);
    	this.jdbcTemplate.execute(sb.toString());
    }
    
    public void deleteTopicBarrageByFie(long fid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update topic_barrage set status=0 where fid=").append(fid);
    	this.jdbcTemplate.execute(sb.toString());
    }
    
    public void addContentLikeByCid(long cid, long addNum){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update content set like_count=like_count+").append(addNum);
    	sb.append(" where id=").append(cid);
    	this.jdbcTemplate.execute(sb.toString());
    }
    
    public void deleteAggregationTopic(long topicId){
    	//删除聚合关系
    	StringBuilder sb = new StringBuilder();
    	sb.append("delete from topic_aggregation where topic_id=");
    	sb.append(topicId).append(" or sub_topic_id=");
    	sb.append(topicId);
    	jdbcTemplate.execute(sb.toString());
    	//聚合申请相关至失效
    	StringBuilder sb2 = new StringBuilder();
    	sb2.append("update topic_aggregation_apply set result=3");
    	sb2.append(" where (topic_id=").append(topicId);
    	sb2.append(" or target_topic_id=").append(topicId);
    	sb2.append(") and result in (0,1)");
    	jdbcTemplate.execute(sb2.toString());
    }
    
    /**
     * 删除banner上的王国
     * @param topicId
     */
    public void deleteBannerTopic(long topicId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("delete from activity where typ=2 and cid=").append(topicId);
    	jdbcTemplate.execute(sb.toString());
    }
    
    /**
     * 删除王国的标签
     * @param topicId
     */
    public void deleteTopicTagByTopicId(long topicId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update topic_tag_detail set status=1 where topic_id=").append(topicId);
    	jdbcTemplate.execute(sb.toString());
    }
    
    /**
     * 删除王国相关的榜单记录
     * @param topicId
     */
    public void deleteTopicBillboard(long topicId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("delete from billboard_relation where type=1 and target_id=").append(topicId);
    	jdbcTemplate.execute(sb.toString());
    }
    
    public Map<String,Object> getTopicUserConfig(long topicId, long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select id,uid,topic_id,push_type from topic_user_config ");
    	sb.append("where topic_id=").append(topicId);
    	sb.append(" and uid=").append(uid);
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
    	if(null != list && list.size() > 0){
    		return list.get(0);
    	}
    	return null;
    }
    
    public List<Map<String,Object>> getTopicUserProfileByTopicIds(List<Long> topicIds){
    	if(null == topicIds || topicIds.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select c.forward_cid as id,p.uid,p.nick_name,p.avatar,p.v_lv");
    	sb.append(" from user_profile p LEFT JOIN content c on c.uid=p.uid");
    	sb.append(" where c.forward_cid in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i));
    	}
    	sb.append(") and c.type=3");
    	
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
    
    public List<Map<String, Object>> getTopicMembersByTopicId(long topicId, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.* from live_favorite f,user_profile p");
    	sb.append(" where f.uid=p.uid and f.topic_id=").append(topicId);
    	sb.append(" order by f.create_time limit ").append(start);
    	sb.append(",").append(pageSize);
    	
    	return jdbcTemplate.queryForList(sb.toString());
    }
    
    public List<Map<String, Object>> getLastFragmentByTopicIds(List<Long> topicIds){
    	if(null == topicIds || topicIds.size() == 0){
    		return null;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select f2.* from topic_fragment f2,(select max(f.id) as fid");
    	sb.append(" from topic_fragment f where f.topic_id in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i));
    	}
    	sb.append(") group by f.topic_id) m where f2.id=m.fid");
    		
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
    
    public List<Map<String, Object>> queryBySql(String sql){
    	return jdbcTemplate.queryForList(sql);
    }
    
    public void executeSql(String sql){
    	jdbcTemplate.execute(sql);
    }
    
    /**
     * 最活跃的米汤新鲜人
     * @param sinceId
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> getActiveUserBillboard(long sinceId, int pageSize){
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select m.uid,m.tid from (select t.uid,min(t.id) as tid");
    	sb.append(" from topic t where t.status=0 group by t.uid) m");
    	sb.append(" where m.tid<").append(sinceId);
    	sb.append(" and m.uid not in (select u.uid from user_profile u where u.nick_name like '%米汤客服%')");
    	sb.append(" order by m.tid desc limit ").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		for(Map<String, Object> m : list){
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("uid"));
    			bbl.setType(2);
    			bbl.setSinceId((Long)m.get("tid"));
    			result.add(bbl);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 这里的互动最热闹
     * @param sinceId
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> getInteractionHottestKingdomBillboard(long sinceId, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,m.cc as sinceId from topic t,content c,(");
    	sb.append("select f.topic_id,count(1) as cc");
    	sb.append(" from topic_fragment f where f.type not in (0,12,13)");
    	sb.append(" and f.create_time>date_add(now(), interval -1 day)");
    	sb.append(" group by f.topic_id) m where t.id=c.forward_cid and c.type=3 and t.sub_type <> 1");
    	sb.append(" and t.id=m.topic_id and m.cc<").append(sinceId);
    	sb.append(" order by cc DESC limit ").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		for(Map<String, Object> m : list){
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId((Long)m.get("sinceId"));
    			result.add(bbl);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 最新更新的王国
     * @param sinceId
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> getLivesByUpdateTime(long sinceId, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,t.long_time from topic t where t.status=0 and t.sub_type <> 1 ");
    	sb.append(" and t.long_time<").append(sinceId);
    	sb.append(" order by t.long_time desc limit ").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		for(Map<String, Object> m : list){
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId((Long)m.get("long_time"));
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 获取有王国的新注册的用户
     * @param sex		-1全部，0女，1男
     * @param sinceId
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> getNewPeople(int sex, long sinceId, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.uid,u.id from user_profile u,(");
    	sb.append("select t.uid,count(1) as cc from topic t");
    	sb.append(" group by t.uid) m where u.uid=m.uid");
    	sb.append(" and u.nick_name not like '%米汤客服%'");
    	sb.append(" and u.id<").append(sinceId);
    	if(sex == 0){
    		sb.append(" and u.gender<>1");
    	}else if(sex == 1){
    		sb.append(" and u.gender=1");
    	}
    	sb.append(" order by u.id DESC limit ").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		for(Map<String, Object> m : list){
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("uid"));
    			bbl.setType(2);
    			bbl.setSinceId((Long)m.get("id"));
    			result.add(bbl);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 炙手可热的米汤红人
     * @param sinceId
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> fansBillboard(long start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.uid,m.fanscount from user_profile u, (");
    	sb.append("select f.target_uid, count(DISTINCT f.source_uid) as fanscount");
    	sb.append(" from user_follow f group by f.target_uid) m");
    	sb.append(" where u.uid=m.target_uid and u.nick_name not like '%米汤客服%'");
    	sb.append(" order by m.fanscount desc,uid desc limit ");
    	sb.append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("uid"));
    			bbl.setType(2);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    public List<BillBoardListDTO> getNewRegisterUsers(long sinceId, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.uid,p.id from user_profile p");
    	sb.append(" where p.nick_name not like '%米汤客服%'");
    	sb.append(" and p.id<").append(sinceId);
    	sb.append(" order by p.id desc limit ").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		List<Long> uidList = new ArrayList<Long>();
    		BillBoardListDTO bbl = null;
    		Long uid = null;
    		for(Map<String, Object> m : list){
    			uid = (Long)m.get("uid");
    			if(uidList.contains(uid)){
    				continue;
    			}
    			uidList.add(uid);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId(uid);
    			bbl.setType(2);
    			bbl.setSinceId((Long)m.get("id"));
    			result.add(bbl);
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

	public List<Map<String, Object>> getTopPricedKingdomList(int page, int pageSize) {
		String sql = "select * from topic where status!=2 order by price desc limit ?,?";
		return jdbcTemplate.queryForList(sql,(page-1)*pageSize,pageSize);
	}
	/**
	 * 取上市王国
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getListingKingodms(int minPrice,int page, int pageSize) {
		String sql = "select * from topic where price>=? and listing_time is not null and status!=2 order by listing_time desc limit ?,?";
		return jdbcTemplate.queryForList(sql,minPrice,(page-1)*pageSize,pageSize);
	}
}
