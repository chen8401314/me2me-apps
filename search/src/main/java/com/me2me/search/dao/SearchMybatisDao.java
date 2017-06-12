package com.me2me.search.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.me2me.search.mapper.*;
import com.me2me.search.model.*;

@Repository
public class SearchMybatisDao {

	@Autowired
	private SearchHotKeywordMapper searchHotKeywordMapper;
	@Autowired
	private SearchUserDislikeMapper searchUserDislikeMapper;
	
	public List<SearchHotKeyword> getRecWords(int pageSize){
		SearchHotKeywordExample example = new SearchHotKeywordExample();
//		SearchHotKeywordExample.Criteria criteria = example.createCriteria();
		example.setOrderByClause(" order_num asc limit " + pageSize);
		return searchHotKeywordMapper.selectByExample(example);
	}
	
	public List<SearchUserDislike> getSearchUserDislikesByUidsAndType(long uid, int type){
		SearchUserDislikeExample example = new SearchUserDislikeExample();
		SearchUserDislikeExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andTypeEqualTo(type);
		return searchUserDislikeMapper.selectByExample(example);
	}
	
	public SearchUserDislike getSearchUserDislikeByUidAndCidAndType(long uid, long cid, int type){
		SearchUserDislikeExample example = new SearchUserDislikeExample();
		SearchUserDislikeExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andCidEqualTo(cid);
		criteria.andTypeEqualTo(type);
		List<SearchUserDislike> list = searchUserDislikeMapper.selectByExample(example);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public void saveSearchUserDislike(SearchUserDislike sud){
		searchUserDislikeMapper.insertSelective(sud);
	}
}
