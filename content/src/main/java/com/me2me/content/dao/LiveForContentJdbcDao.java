package com.me2me.content.dao;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    	sb.append("select t.id,p.uid,p.nick_name,p.avatar,p.v_lv");
    	sb.append(" from user_profile p LEFT JOIN topic t on t.uid=p.uid");
    	sb.append(" where t.id in (");
    	for(int i=0;i<topicIds.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(topicIds.get(i));
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
    	sb.append(" order by f.create_time desc limit ").append(start);
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
}
