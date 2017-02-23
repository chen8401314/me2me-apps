package com.me2me.user.dao;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 王一武
 * Date: 2016/11/02
 * Time :16:16
 */
@Repository
@Slf4j
public class LiveForUserJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long getKingFromTopic(long topicId) {
        String caseSql = " select uid from topic where id=" + topicId;
        Map<String,Object> map = jdbcTemplate.queryForMap(caseSql);
        if(map!=null){
           return (long)map.get("uid");
        }
        return 0L;
    }

    public List<Map<String,Object>> getTopicByUid(long uid){
        String sql = "select * from topic where uid="+uid;
        return jdbcTemplate.queryForList(sql);
    }

    public void favoriteTopic(long topicId,long uid){
        String sql = "insert into live_favorite (uid,topic_id) values ("+uid+","+topicId+")";
        jdbcTemplate.execute(sql);
    }


    public void addToSnsCircle(long owner,long uid){
        String sql = "insert into sns_circle (owner,uid,internal_status)values("+owner+","+uid+",0)";
        jdbcTemplate.execute(sql);
    }
    
    public List<Map<String,Object>> getTopicListByIds(List<Long> ids){
    	if(null == ids || ids.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select id,uid,core_circle,type from topic where id in (");
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
    
    public List<Map<String,Object>> getTopicAggregationApplyListByIds(List<Long> ids){
    	if(null == ids || ids.size() == 0){
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("select id,topic_id,target_topic_id,type,result,create_time,");
    	sb.append("update_time from topic_aggregation_apply where id in (");
    	for(int i=0;i<ids.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append(ids.get(i));
    	}
    	sb.append(")");
    	return jdbcTemplate.queryForList(sb.toString());
    }
}
