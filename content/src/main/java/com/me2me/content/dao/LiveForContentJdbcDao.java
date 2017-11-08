package com.me2me.content.dao;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.me2me.common.utils.DateUtil;
import com.me2me.content.dto.BillBoardListDTO;

import java.util.ArrayList;
import java.util.Date;
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
    
    public void deleteTopicListed(long topicId){
    	String sql = "delete from topic_listed where topic_id=?";
    	jdbcTemplate.update(sql, topicId);
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
    	sb.append("select c.forward_cid as id,p.uid,p.nick_name,p.avatar,p.v_lv,p.level");
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
    public List<BillBoardListDTO> getActiveUserBillboard(long sinceId, int pageSize, List<Long> blacklistUids){
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select m.uid,m.tid from (select t.uid,min(t.id) as tid");
    	sb.append(" from topic t where t.status=0 group by t.uid) m");
    	sb.append(" where m.tid<").append(sinceId);
    	sb.append(" and m.uid not in (select u.uid from user_profile u where u.nick_name like '%米汤客服%')");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and m.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
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
    public List<BillBoardListDTO> getInteractionHottestKingdomBillboard(long sinceId, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,m.cc as sinceId from topic t,content c,(");
    	sb.append("select f.topic_id,count(1) as cc");
    	sb.append(" from topic_fragment f where f.type not in (0,12,13)");
    	sb.append(" and f.create_time>date_add(now(), interval -1 day)");
    	sb.append(" group by f.topic_id) m where t.id=c.forward_cid and c.type=3 and t.sub_type =0");
    	sb.append(" and t.id=m.topic_id and m.cc<").append(sinceId);
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
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
    public List<BillBoardListDTO> getLivesByUpdateTime(long sinceId, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,t.long_time from topic t where t.status=0 and t.sub_type =0 ");
    	sb.append(" and t.long_time<").append(sinceId);
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
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
    public List<BillBoardListDTO> getNewPeople(int sex, long sinceId, int pageSize, List<Long> blacklistUids){
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
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and u.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
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
    public List<BillBoardListDTO> fansBillboard(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.uid,m.fanscount from user_profile u, (");
    	sb.append("select f.target_uid, count(DISTINCT f.source_uid) as fanscount");
    	sb.append(" from user_follow f group by f.target_uid) m");
    	sb.append(" where u.uid=m.target_uid and u.nick_name not like '%米汤客服%'");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and u.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
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
    
    /**
     * 邀请榜单(人榜)
     * @param startTime
     * @param endTime
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> invitationBillboard(String startTime, String endTime, long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.uid,m.cc,p.nick_name from user_profile p,(");
    	sb.append("select u.referee_uid,count(1) as cc,max(u.create_time) as maxtime");
    	sb.append(" from user_profile u where u.referee_uid>0 and u.is_activate=1");
    	sb.append(" and u.create_time>='").append(startTime).append("' and u.create_time<'");
    	sb.append(endTime).append("' group by u.referee_uid) m where p.uid=m.referee_uid");
    	sb.append(" and p.nick_name not like '%米汤客服%'");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and p.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by m.cc DESC,m.maxtime asc limit ");
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
    
    /**
     * 个人米汤币排行榜
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> userCoinList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.uid from user_profile u");
    	sb.append(" where u.nick_name not like '%米汤客服%'");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and u.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by u.available_coin desc,u.id desc limit ");
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
    
    /**
     * 对外分享次数用户榜单(2017-08-07 00:00:00开始)
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> shareUserList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.uid,m.hcount from user_profile p,(");
    	sb.append("select h.uid,count(1) as hcount from content_share_history h");
    	sb.append(" where h.create_time>='2017-08-07 00:00:00' group by h.uid) m");
    	sb.append(" where p.uid=m.uid and p.nick_name not like '%米汤客服%'");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and p.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by m.hcount desc,p.uid limit ");
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
    
    /**
     * 外部阅读次数王国榜单(2017-08-07 00:00:00开始)
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> outReadKingdomList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,m.tcount from topic t,(");
    	sb.append("select h.topic_id,count(1) as tcount from topic_read_his h");
    	sb.append(" where h.create_time>='2017-08-07 00:00:00'");
    	sb.append(" and h.in_app=0 group by h.topic_id) m");
    	sb.append(" where t.id=m.topic_id");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by m.tcount desc,t.id limit ");
    	sb.append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 王国价值最高排行榜
     * @param start
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> kingdomPriceList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id from topic t where 1=1");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by t.price desc,t.id desc");
    	sb.append(" limit ").append(start).append(",").append(pageSize);

    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 正在抽奖的王国
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> kingdomLotteryList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.topic_id,max(id) as maxid from lottery_info t");
    	sb.append(" where t.status=0 and t.end_time>now()");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" group by t.topic_id order by maxid desc");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("topic_id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 优秀的新王国(王国榜单)
     * 1 王国成立时间5天(宽泛的5天,天数相减即可,不需要精确到小时)
	 * 2 这5天内更新天数超过4天
	 * 3 第5天(也就是今天)还在更新
	 * 4 除去语录更新
	 * 5 排序规则为: 王国价值从大到小
     * @param start
     * @param pageSize
     * @param blacklistUids
     * @return
     */
    public List<BillBoardListDTO> goodNewKingdomList(long start, int pageSize, List<Long> blacklistUids){
    	Date now = new Date();
    	String today = DateUtil.date2string(now, "yyyy-MM-dd");
    	String fiveDay = DateUtil.date2string(DateUtil.addDay(now, -4), "yyyy-MM-dd");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select f.topic_id,count(DISTINCT DATE_FORMAT(f.create_time,'%Y%m%d')) as cc,");
    	sb.append("max(f.create_time) as maxtime,max(t.price) as pp");
    	sb.append(" from topic t,topic_fragment f where t.id=f.topic_id");
    	sb.append(" and t.create_time>='").append(fiveDay).append(" 00:00:00'");
    	sb.append(" and t.create_time<='").append(fiveDay).append(" 23:59:59'");
    	sb.append(" and f.extra not like '%image_daycard%'");
    	sb.append(" and f.type in (0,3,11,12,13,15,52,55)");
    	sb.append(" and f.status=1");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" group by f.topic_id");
    	sb.append(" having maxtime>='").append(today).append(" 00:00:00' and cc>=4");
    	sb.append(" order by pp desc,f.topic_id ASC");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("topic_id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    
    /**
     * 王国价值上升最快排行榜
     * @param start
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> kingdomIncrPriceList(long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
//    	sb.append("select t.id from topic_data d,topic t");
//    	sb.append(",(select f.topic_id,COUNT(DISTINCT IF(f.type IN (0,3,11,12,13,15,52,55), DATE_FORMAT(f.create_time,'%Y%m%d'), NULL)) AS updateDayCount ");
//    	sb.append(" FROM topic_fragment f WHERE f.status=1 AND f.create_time>DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -5 DAY),'%Y-%m-%d 00:00:00')  GROUP BY f.topic_id) m ");
//    	sb.append(" where d.topic_id=t.id and d.topic_id=m.topic_id ");
//    	
//    	if(null != blacklistUids && blacklistUids.size() > 0){
//    		sb.append(" and t.uid not in (");
//    		for(int i=0;i<blacklistUids.size();i++){
//    			if(i>0){
//    				sb.append(",");
//    			}
//    			sb.append(blacklistUids.get(i).toString());
//    		}
//    		sb.append(")");
//    	}
//    	sb.append(" ORDER BY d.last_price_incr*m.updateDayCount DESC,d.topic_id ");
//    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	sb.append("select t.id from topic_data d,topic t where d.topic_id=t.id");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" order by d.last_price_incr desc,t.id");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 标签王国价值最高排行榜
     * @param tag		标签名
     * @param start
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> tagKingdomPriceList(String tag, long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder tagSql = new StringBuilder();
    	tagSql.append("select t1.id as pid,t2.id as sid from topic_tag t1 LEFT JOIN topic_tag t2");
    	tagSql.append(" on t1.id=t2.pid where t1.tag='").append(tag).append("'");
    	
    	List<Map<String, Object>> tagList = jdbcTemplate.queryForList(tagSql.toString());
    	List<Long> tagIds = new ArrayList<Long>();
    	if(null != tagList && tagList.size() > 0){
    		Map<String, Object> m = null;
    		for(int i=0;i<tagList.size();i++){
    			m = tagList.get(i);
    			if(i == 0){
    				tagIds.add((Long)m.get("pid"));
    			}
    			if(null != m.get("sid")){
    				tagIds.add((Long)m.get("sid"));
    			}
    		}
    	}
    	
    	if(tagIds.size() == 0){
    		return null;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,max(t.price) as pri from topic_tag_detail d,topic t");
    	sb.append(" where d.topic_id=t.id and d.status=0");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" and d.tag_id in (");
    	for(int i=0;i<tagIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(tagIds.get(i).toString());
    	}
    	sb.append(") group by t.id order by pri desc,t.id DESC");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    /**
     * 标签王国价值增长最快排行榜
     * @param tag		标签名
     * @param start
     * @param pageSize
     * @return
     */
    public List<BillBoardListDTO> tagKingdomIncrPriceList(String tag, long start, int pageSize, List<Long> blacklistUids){
    	StringBuilder tagSql = new StringBuilder();
    	tagSql.append("select t1.id as pid,t2.id as sid from topic_tag t1 LEFT JOIN topic_tag t2");
    	tagSql.append(" on t1.id=t2.pid where t1.tag='").append(tag).append("'");
    	
    	List<Map<String, Object>> tagList = jdbcTemplate.queryForList(tagSql.toString());
    	List<Long> tagIds = new ArrayList<Long>();
    	if(null != tagList && tagList.size() > 0){
    		Map<String, Object> m = null;
    		for(int i=0;i<tagList.size();i++){
    			m = tagList.get(i);
    			if(i == 0){
    				tagIds.add((Long)m.get("pid"));
    			}
    			if(null != m.get("sid")){
    				tagIds.add((Long)m.get("sid"));
    			}
    		}
    	}
    	
    	if(tagIds.size() == 0){
    		return null;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id,max(td.last_price_incr) as pri from topic_tag_detail d,topic t,topic_data td");
    	sb.append(" where d.topic_id=t.id and t.id=td.topic_id");
    	sb.append(" and d.status=0");
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and t.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
    	sb.append(" and d.tag_id in (");
    	for(int i=0;i<tagIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(tagIds.get(i).toString());
    	}
    	sb.append(") group by t.id order by pri desc,t.id desc");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	
    	List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
    	if(null != list && list.size() > 0){
    		BillBoardListDTO bbl = null;
    		Map<String, Object> m = null;
    		for(int i=0;i<list.size();i++){
    			m = list.get(i);
    			bbl = new BillBoardListDTO();
    			bbl.setTargetId((Long)m.get("id"));
    			bbl.setType(1);
    			bbl.setSinceId(start+i+1);
    			result.add(bbl);
    		}
    	}
    	return result;
    }
    
    public List<BillBoardListDTO> getNewRegisterUsers(long sinceId, int pageSize, List<Long> blacklistUids){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.uid,p.id from user_profile p");
    	sb.append(" where p.nick_name not like '%米汤客服%'");
    	sb.append(" and p.id<").append(sinceId);
    	if(null != blacklistUids && blacklistUids.size() > 0){
    		sb.append(" and p.uid not in (");
    		for(int i=0;i<blacklistUids.size();i++){
    			if(i>0){
    				sb.append(",");
    			}
    			sb.append(blacklistUids.get(i).toString());
    		}
    		sb.append(")");
    	}
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
    public List<Map<String, Object>> getTopicTagDetailListByTopicId(long topicId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select * from topic_tag_detail d where d.status=0");
    	sb.append(" and d.topic_id =").append(topicId);
    	sb.append(" order by topic_id asc,id asc");
    	return jdbcTemplate.queryForList(sb.toString());
    }
    
	public List<Map<String, Object>> getTopPricedKingdomList(int page, int pageSize, List<Long> blacklistUids) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic where status!=2");
		if(null != blacklistUids && blacklistUids.size() > 0){
			sb.append(" and uid not in (");
			for(int i=0;i<blacklistUids.size();i++){
				if(i>0){
					sb.append(",");
				}
				sb.append(blacklistUids.get(i).toString());
			}
			sb.append(")");
		}
		sb.append(" order by price desc limit ?,?");
		return jdbcTemplate.queryForList(sb.toString(),(page-1)*pageSize,pageSize);
	}
	/**
	 * 取上市王国
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getListingKingdoms(int page, int pageSize) {
		String sql = "SELECT t.* FROM topic t,topic_listed tl WHERE tl.topic_id  =t.id and tl.status in(0,1) ORDER BY tl.create_time desc limit ?,?";
		return jdbcTemplate.queryForList(sql,(page-1)*pageSize,pageSize);
	}
	
    /**
     * 转让王国修改UGC uid
     * @param topicId
     */
    public void updateContentUid(long newUid,long topicId){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update content set uid=").append(newUid);
    	sb.append(" where type = 3  ");
    	sb.append(" and forward_cid = ").append(topicId);
    	jdbcTemplate.execute(sb.toString());
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
    
    /**
     * 获取国王所有王国ids
     * @param uids
     * @return
     */
    public List<Long> getTopicIdsByUids(List<Long> uids){
    	if(null == uids || uids.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select t.id from topic t where t.uid in (");
    	for(int i=0;i<uids.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(uids.get(i).toString());
    	}
    	sb.append(")");
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	List<Long> result = new ArrayList<Long>();
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> t : list){
    			result.add((Long)t.get("id"));
    		}
    	}
    	return result;
    }
    
    /**
     * 获取王国中最后一次更新往前{limitMinute}分钟的王国外露数据
     * @param topicIds
     * @param limitMinute
     * @param privateTopicIds	私密王国ID
     * @return
     */
    public List<Map<String, Object>> getOutFragments(List<Long> topicIds, int limitMinute, List<Long> privateTopicIds){
    	if(null == topicIds || topicIds.size() == 0 || limitMinute<=0){
    		return null;
    	}else{
    		if(null != privateTopicIds && privateTopicIds.size() > 0){
    			for(int i=0;i<topicIds.size();i++){
    				if(privateTopicIds.contains(topicIds.get(i))){
    					topicIds.remove(i);
    					i--;
    				}
    			}
    			if(topicIds.size() == 0){
    				return null;
    			}
    		}
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("select f.* from topic_fragment f,topic t");
    	sb.append(" where f.topic_id=t.id and f.topic_id in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i).toString());
    	}
    	sb.append(") and f.status=1 and f.out_type=1");
    	sb.append(" and f.create_time>=date_add(t.out_time, interval -").append(limitMinute).append(" minute)");
    	sb.append(" order by f.topic_id,f.id desc");

    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sb.toString());
    	if(null != result && result.size() > 0){
    		Map<String, Object> m = null;
    		String fragmentImage = null;
    		String extra = null;
    		for(int i=0;i<result.size();i++){
    			m = result.get(i);
    			int type = (Integer)m.get("type");
    			int contentType = (Integer)m.get("content_type");
    			if(type == 0 && contentType == 1){
    				fragmentImage = (String)m.get("fragment_image");
    				if(StringUtils.isEmpty(fragmentImage)){
    					result.remove(i);
    					i--;
    					continue;
    				}
    				extra = (String)m.get("extra");
    				if(!StringUtils.isEmpty(extra) && extra.contains("image_daycard")){//外露日签图片特殊处理
    					if(fragmentImage.contains("-")){//有小横杠的是安卓的。。
    						m.put("fragment_image", fragmentImage+"-nrwl_rqcl");
    					}else{
    						m.put("fragment_image", fragmentImage+"-nrwl_rq_ios");
    					}
    				}
    			}
    		}
    	}
    	
    	return result;
    }
    
    public Map<String,Object> getTopicById(long id){
		String sql = "select * from topic where id = "+id;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size() > 0 && list != null){
			return list.get(0);
		}
		return null;
	}
    
    public Map<String,Object> getUserSpecialTopicBySubType(long uid, int subType){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select * from topic t where t.uid=").append(uid);
    	sb.append(" and t.sub_type=").append(subType);
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sb.toString());
		if(list.size() > 0 && list != null){
			return list.get(0);
		}
		return null;
    }

	public Map<String, Object> getTopicTagById(Long dataId) {
		try{
			return jdbcTemplate.queryForMap("select * from topic_tag where id=?",dataId);
		}catch(Exception e){
			log.error("未找到标签，ID:"+dataId);
		}
		return null;
	}
	/**
	 * 
	 * @author zhangjiwei
	 * @date Sep 4, 2017
	 * @param uid
	 * @param type 1王国，2标签
	 * @param like 0不喜欢，1喜欢
	 * @return
	 */
	public List<String> getUserNotLike(long uid,int type,int like) {
		List<Map<String,Object>> dataList = jdbcTemplate.queryForList("select data from user_dislike where uid=? and is_like=? and type=?",uid,like,type);
		List<String> dataList2=new ArrayList<>();
		for(Map<String,Object> data:dataList){
			dataList2.add((String) data.get("data"));
		}
		return dataList2;
	}
	
	public List<Map<String, Object>> getAllKingdomCategory(){
		String sql = "select * from topic_category";
		return jdbcTemplate.queryForList(sql);
	}
	
	/**
	 * 根据广告位ID查询所有有效广告信息
	 * @param bannerId
	 * @return
	 */
	public List<Map<String, Object>> getAllEffectiveAdInfoByBannerId(long bannerId){
		StringBuilder sb = new StringBuilder();
    	sb.append("select a.id,a.ad_title,a.ad_cover,a.ad_cover_width,a.ad_cover_height,a.ad_url,");
    	sb.append("a.topic_id,a.type,a.display_probability,t.type AS topic_type,t.core_circle ");
    	sb.append("  FROM ad_info a LEFT JOIN topic t ON a.topic_id = t.id WHERE a.status=0 and a.effective_time>NOW() AND  a.banner_id = ").append(bannerId);
    	sb.append(" ORDER BY a.effective_time DESC");
		return jdbcTemplate.queryForList(sb.toString());
	}
	/**
	 * 获取广告点击信息
	 * @param uid
	 * @param adList
	 * @return
	 */
	public List<Map<String, Object>> getAdRecordByUid(long uid, List<Map<String,Object>> adList) {
		StringBuilder sb = new StringBuilder();
		sb.append("select adid,COUNT(adid) as recordCount FROM ad_record  WHERE uid =  ").append(uid).append(" and adid in(");
		for (int i = 0; i < adList.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			Map<String,Object> ad = adList.get(i);
			sb.append(ad.get("id").toString());
		}
		sb.append(")");
		sb.append(" group by adid");
		return jdbcTemplate.queryForList(sb.toString());
	}
	
	/**
	 * 更新邀请表已领取
	 * @param hisId
	 */
	public void updateUserInvitationHisReceive(long hisId){
		String sql = "update user_invitation_his set status=1,receive_time=now() where id=?";
		jdbcTemplate.update(sql, hisId);
	}
	
	public List<Map<String, Object>> getKingdomRelevantInfo(long uid, List<Long> tidList){
		if(null == tidList || tidList.size() == 0){
			return null;
		}
		StringBuilder inIds = new StringBuilder();
		for(int i=0;i<tidList.size();i++){
			if(i>0){
				inIds.append(",");
			}
			inIds.append(tidList.get(i).toString());
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select o.topic_id,sum(topic_count) as topic_count,sum(review_count) as review_count,");
		sb.append("sum(favorite_count) as favorite_count,sum(non_core_count) as non_core_count,");
		sb.append("sum(core_count) as core_count from ((select f.topic_id,count(if(t.uid=f.uid,TRUE,NULL)) as topic_count,");
		sb.append("count(if(t.uid<>f.uid,TRUE,NULL)) as review_count,0 as favorite_count,0 as non_core_count,");
		sb.append("0 as core_count from topic t, topic_fragment f where t.id=f.topic_id and t.id in (");
		sb.append(inIds.toString()).append(") group by f.topic_id) union (");
		sb.append("select t.topic_id,0 as topic_count,0 as review_count,count(1) favorite_count,0 as non_core_count,");
		sb.append("0 as core_count from live_favorite t where t.uid=").append(uid);
		sb.append(" and t.topic_id in (").append(inIds.toString()).append(") group by t.topic_id) union (");
		sb.append("select f.topic_id,0 as topic_count,0 as review_count,0 as favorite_count,count(1) core_count,");
		sb.append("0 as non_core_count from live_favorite f,topic t where f.topic_id=t.id");
		sb.append(" and not FIND_IN_SET(f.uid, SUBSTR(t.core_circle FROM 2 FOR LENGTH(t.core_circle)-2))");
		sb.append(" and f.topic_id in (").append(inIds.toString());
		sb.append(") group by f.topic_id) union (");
		sb.append("select t.id as topic_id, 0 as topic_count,0 as review_count,0 as favorite_count,0 as non_core_count,");
		sb.append("LENGTH(t.core_circle)-LENGTH(replace(t.core_circle,',','')) as core_count");
		sb.append(" from topic t where t.id in (").append(inIds.toString());
		sb.append(") group by t.id)) o group by o.topic_id");
		return jdbcTemplate.queryForList(sb.toString());
	}
	public Map<String, Object> getTopiciTagByTag(String tag){
		String sql  ="select * from topic_tag where tag = ? and status =0";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,tag);
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	public List<Map<String, Object>> getAdBannerByTagId(long tagId){
		StringBuilder sb = new StringBuilder();
		sb.append("select DISTINCT a.banner_id,b.ad_banner_height,b.ad_banner_width,a.position FROM ad_tag a,ad_banner b LEFT JOIN (");
		sb.append("select i.banner_id,1 as has from ad_info i where i.status=0 and i.effective_time>now()");
		sb.append(" group by i.banner_id) m on b.id=m.banner_id WHERE a.banner_id=b.id AND b.type IN (0,2)");
		sb.append(" AND  tag_id=? and m.has is not null");
		return jdbcTemplate.queryForList(sb.toString(),tagId);
	}
    public List<Map<String, Object>> getTagTopicInfo(List<Long> tagIds){
    	if(tagIds.size()==0){
    		return new ArrayList<Map<String, Object>>();
    	}
    	StringBuilder tagIdsStr = new StringBuilder();
		for(int i=0;i<tagIds.size();i++){
			if(i>0){
				tagIdsStr.append(",");
			}
			tagIdsStr.append(tagIds.get(i).toString());
		}
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select o1.tag_id, SUM(o1.topic_count) kingdomCount,SUM(o1.price_sum) tagPrice,o2.uid_count tagPersons  ");
    	sb.append(" FROM  (SELECT t4.tag_id tag_id,COUNT(DISTINCT t1.id) topic_count,SUM(DISTINCT t1.price) price_sum ");
    	sb.append(" FROM topic t1,content t2,topic_tag_detail t3,(SELECT  id,id AS tag_id FROM ");
    	sb.append(" topic_tag WHERE id IN (");
        sb.append(tagIdsStr);
    	sb.append(")");
    	sb.append(" UNION SELECT id,pid AS tag_id  FROM topic_tag WHERE pid IN (");
        sb.append(tagIdsStr);
    	sb.append(" )) t4 ");
        sb.append("  WHERE t1.id = t2.forward_cid AND t4.id = t3.tag_id AND t2.type = 3 ");
    	sb.append("  AND t1.id = t3.topic_id  AND t1.status = 0 AND t3.status = 0 ");
    	sb.append("  GROUP BY t4.tag_id) o1,(SELECT x.tag_id,x.userCount+FLOOR(y.readcount / 20) uid_count ");
    	sb.append(" FROM (SELECT m2.tag_id,COUNT(DISTINCT t1.uid) AS userCount ");
    	sb.append(" FROM (SELECT t2.topic_id,t4.tag_id ");
    	sb.append("  FROM topic_tag_detail t2,(SELECT id,id AS tag_id FROM topic_tag WHERE id IN (");
        sb.append(tagIdsStr);
    	sb.append(") UNION SELECT id,pid AS tag_id FROM topic_tag WHERE pid IN (");
        sb.append(tagIdsStr);
    	sb.append(") ) t4 WHERE t2.status=0 ");
    	sb.append(" AND t2.tag_id=t4.id GROUP BY t4.tag_id,t2.topic_id ");
    	sb.append(" ) m2,topic_fragment t1 WHERE m2.topic_id=t1.topic_id ");
    	sb.append(" AND t1.status=1 GROUP BY m2.tag_id ");
    	sb.append(" ) X,(SELECT m.tag_id,SUM(t3.read_count) AS readcount ");
    	sb.append(" FROM (SELECT t2.topic_id,t4.tag_id ");
    	sb.append(" FROM topic_tag_detail t2,(SELECT id,id AS tag_id FROM topic_tag WHERE id IN (");
    	sb.append(tagIdsStr);
    	sb.append(") UNION SELECT id,pid AS tag_id FROM topic_tag WHERE pid IN (");
    	sb.append(tagIdsStr);
    	sb.append(") ) t4 WHERE t2.status=0 ");
    	sb.append(" AND t2.tag_id=t4.id GROUP BY t4.tag_id,t2.topic_id ");
    	sb.append(" ) m,content t3 WHERE m.topic_id=t3.forward_cid AND t3.type=3 ");
    	sb.append(" GROUP BY m.tag_id) Y WHERE x.tag_id=y.tag_id) o2");
    	sb.append(" WHERE o1.tag_id = o2.tag_id  GROUP BY o1.tag_id ");
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString());
    	return list;
    }
    /**
     * 查询子标签信息
     * @param pid
     * @return
     */
    public List<Map<String,Object>> getTopicTagByPid(long pid){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select * from topic_tag where pid  =? and status = 0 ");
    	return jdbcTemplate.queryForList(sb.toString(),pid);
    }
    
    /**
     * 查询标签下王国信息（按王国价值增长量倒序）
     * @param tagId
     * @param pageSize
     * @return
     */
    public List<Map<String,Object>> getTopicByTagId(long tagId,int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select distinct c.id,c.content,c.forward_cid as topic_id,c.conver_image,td.last_price_incr from topic_tag_detail tt,content c,topic_data td ");
    	sb.append("   where c.type = 3 and c.status = 0 and  tt.status = 0 and tt.topic_id  = c.forward_cid  ");
    	sb.append(" and c.forward_cid   = td.topic_id and tt.tag_id = ?  order by td.last_price_incr desc limit ? ");
    	return jdbcTemplate.queryForList(sb.toString(),tagId,pageSize);
    }
}
