package com.me2me.live.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.me2me.live.mapper.TopicCategoryMapper;
import com.me2me.live.mapper.TopicMapper;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicCategory;
import com.me2me.live.model.TopicCategoryExample;
import com.me2me.live.model.TopicExample;

@Repository
public class LiveExtDao {
	private static final Logger logger = LoggerFactory.getLogger(LiveExtDao.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TopicCategoryMapper categoryMapper;
	@Autowired
	private TopicMapper topicMapper;
	
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
	public List<Topic> getCategoryKingdom(int kcid,int page) {
		TopicExample example = new TopicExample();
		example.createCriteria().andCategoryIdEqualTo(kcid);
		example.setOrderByClause("update_time desc");
		List<Topic> topicList = topicMapper.selectByExample(example);
		return topicList;
	}
}
