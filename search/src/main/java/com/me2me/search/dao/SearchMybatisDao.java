package com.me2me.search.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.me2me.search.mapper.SearchHotKeywordMapper;
import com.me2me.search.model.SearchHotKeyword;
import com.me2me.search.model.SearchHotKeywordExample;

@Repository
public class SearchMybatisDao {

	@Autowired
	private SearchHotKeywordMapper searchHotKeywordMapper;
	
	public List<SearchHotKeyword> getRecWords(int pageSize){
		SearchHotKeywordExample example = new SearchHotKeywordExample();
//		SearchHotKeywordExample.Criteria criteria = example.createCriteria();
		example.setOrderByClause(" order_num asc limit " + pageSize);
		return searchHotKeywordMapper.selectByExample(example);
	}
}
