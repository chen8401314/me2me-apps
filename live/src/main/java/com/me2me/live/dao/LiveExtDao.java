package com.me2me.live.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.live.mapper.TopicCategoryMapper;
import com.me2me.live.mapper.TopicMapper;
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
}
