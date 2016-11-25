package com.me2me.live.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LiveFavoriteDelete;
import com.me2me.live.model.TopicBarrage;

@Repository
public class LiveLocalJdbcDao {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void batchInsertLiveFavorite(List<LiveFavorite> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		LiveFavorite lf = null;
		for(int i=0;i<list.size();i++){
			lf = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into live_favorite(topic_id,uid) values(");
			sb.append(lf.getTopicId()).append(",").append(lf.getUid());
			sb.append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void batchInsertTopicBarrage(List<TopicBarrage> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		TopicBarrage tb = null;
		for(int i=0;i<list.size();i++){
			tb = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into topic_barrage(topic_id,uid,type,content_type,top_id,bottom_id) values(");
			sb.append(tb.getTopicId()).append(",").append(tb.getUid()).append(",").append(tb.getType());
			sb.append(",").append(tb.getContentType()).append(",").append(tb.getTopId());
			sb.append(",").append(tb.getBottomId()).append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void updateContentAddOneFavoriteCount(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update content set favorite_count=favorite_count+1 where id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	public void updateContentAddFavoriteCountByForwardCid(int count, long forwardCid){
		if(count < 1){
			return;
		}
		String sql = "update content set favorite_count=favorite_count+"+count+" where forward_cid="+forwardCid;
		jdbcTemplate.execute(sql);
	}
	
	public void deleteLiveFavoriteByIds(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("delete from live_favorite where id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
	
	public void batchInsertLiveFavoriteDelete(List<LiveFavoriteDelete> list){
		if(null == list || list.size() == 0){
			return;
		}
		String[] insertSqls = new String[list.size()];
		StringBuilder sb = null;
		LiveFavoriteDelete lfd = null;
		for(int i=0;i<list.size();i++){
			lfd = list.get(i);
			sb = new StringBuilder();
			sb.append("insert into live_favorite_delete(topic_id,uid) values(");
			sb.append(lfd.getTopicId()).append(",").append(lfd.getUid());
			sb.append(")");
			insertSqls[i] = sb.toString();
		}
		
		jdbcTemplate.batchUpdate(insertSqls);
	}
	
	public void updateContentDecrOneFavoriteCount(List<Long> ids){
		if(null == ids || ids.size() == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update content set favorite_count=favorite_count-1 where favorite_count>0 and id in (");
		Long id = null;
		for(int i=0;i<ids.size();i++){
			id = ids.get(i);
			if(i > 0){
				sb.append(",");
			}
			sb.append(id);
		}
		sb.append(")");
		
		jdbcTemplate.execute(sb.toString());
	}
}
