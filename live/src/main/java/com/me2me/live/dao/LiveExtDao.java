package com.me2me.live.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.live.mapper.TopicCategoryMapper;
import com.me2me.live.model.TopicCategory;
import com.me2me.live.model.TopicCategoryExample;

@Repository
public class LiveExtDao {
	private static final Logger logger = LoggerFactory.getLogger(LiveExtDao.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TopicCategoryMapper categoryMapper;
	
	public List<TopicCategory> getAllCategory() {
		TopicCategoryExample example = new TopicCategoryExample();
		example.setOrderByClause("order_num asc");
		List<TopicCategory> catList = categoryMapper.selectByExample(example);
		return catList;
	}
	public TopicCategory getCategoryById(int kcid){
		TopicCategory cat = categoryMapper.selectByPrimaryKey(kcid);
		return cat;
		
	}
	public List<Map<String,Object>> getCategoryKingdom(int kcid,int page,int pageSize) {
		String sql = "select * from topic where category_id=? order by update_time desc limit ?,?";
		return jdbcTemplate.queryForList(sql, kcid,(page-1)*pageSize,pageSize);
	}
	/**
	 * 查询最近n分钟升值最快的王国。
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @param categoryId
	 * @param limitMinute
	 * @return
	 */
	public Map<String,Object> getCategoryCoverKingdom(int categoryId,int limitMinute){
		String sql ="select t.title,t.live_image,t.id,t.uid,p.nick_name,p.avatar"+
		" from topic t,user_profile p,("+
		" select max(t1.out_time) as maxtime"+
		" from topic t1"+
		" where t1.category_id=?"+
		" ) m"+
		" where t.category_id=?"+
		" and p.uid=t.uid"+
		" and t.out_time>=date_add(m.maxtime,interval -? minute)" +
		" order by t.price desc limit 1;";
		try{
			return jdbcTemplate.queryForMap(sql,categoryId,categoryId,limitMinute);
		}catch(Exception e){
			return null;
		}
	}
	
	public List<Map<String, Object>> getKingdomImageMonth(long topicId, int type){
		StringBuilder sb = new StringBuilder();
		sb.append("select DATE_FORMAT(t.create_time,'%Y-%m') as mm,count(1) as cc,");
		sb.append("max(t.fid) as maxfid,min(t.fid) as minfid ");
		sb.append("from topic_image t ");
		sb.append("where t.topic_id=? ");
		if(type > 0){
			sb.append("and t.type=").append(type);
		}
		sb.append(" group by mm order by mm");
		return jdbcTemplate.queryForList(sb.toString(), topicId);
	}
	
	public List<Map<String, Object>> getKingdomImageList(long topicId, String month, int type){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.fid,t.image,t.extra from topic_image t ");
		sb.append("where t.topic_id=? and DATE_FORMAT(t.create_time,'%Y-%m')=? ");
		if(type > 0){
			sb.append(" and t.type=").append(type);
		}
		sb.append(" order by t.fid,t.id");
		return jdbcTemplate.queryForList(sb.toString(), topicId, month);
	}
	
	public List<Map<String, Object>> getTopicCardImageInfo(long topicId, long uid, long fid){
		StringBuilder sb = new StringBuilder();
		sb.append("select f.fragment,f.fragment_image,f.type,f.content_type,f.extra");
		sb.append(" from topic_fragment f where f.status=1 and f.topic_id=? and f.id>=?");
		sb.append(" and f.uid=? and ((f.type in (0,52) and f.content_type in (0,1))");
		sb.append(" or f.content_type in (12,23,25)) order by f.id limit 3");
		return jdbcTemplate.queryForList(sb.toString(), topicId,fid,uid);
	}
	
	public Map<String, Object> getTopicFragmentInfo(long topicId, long uid, long fid){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as cc,max(f.create_time) as maxtime");
		sb.append(" from topic_fragment f where f.status=1");
		sb.append(" and f.topic_id=? and f.id<=? and f.uid=?");
		sb.append(" and f.type in (0,3,11,12,13,52,54,55,57,58)");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sb.toString(), topicId,fid,uid);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public void updateTopicImageLikeCount(long imageId, int type){
		StringBuilder sb = new StringBuilder();
		sb.append("update topic_image ");
		if(type==0){//+1
			sb.append("set like_count=like_count+1 where id=?");
		}else{//-1
			sb.append("set like_count=like_count-1 where id=? and like_count>0");
		}
		jdbcTemplate.update(sb.toString(), imageId);
	}
}
