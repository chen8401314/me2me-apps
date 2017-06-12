package com.me2me.sns.dao;

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
public class LiveJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void updateTopic(long topicId, String coreCircle) {
        String sql = "update topic set core_circle = ? where id = ?";
        jdbcTemplate.update(sql,coreCircle,topicId);
    }
    
    public List<Map<String, Object>> getMyFansNotInTopicPage(long uid, long topicId, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select u.* from user_profile u left join user_follow f ");
    	sb.append("on u.uid = f.source_uid where f.target_uid=").append(uid);
    	sb.append(" and not EXISTS (select 1 from live_favorite l where l.topic_id=");
    	sb.append(topicId).append(" and l.uid=f.source_uid)");
    	sb.append(" order by convert(u.nick_name USING gbk) COLLATE gbk_chinese_ci");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	return jdbcTemplate.queryForList(sb.toString());
    }
}
