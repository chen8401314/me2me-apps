package com.me2me.mgmt.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LocalJdbcDao {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> queryEvery(String sql){
		sql = sql.trim();
		if(null == sql || "".equals(sql)
				|| !sql.startsWith("select")){
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(null != list && list.size() > 0){
			Map<String, Object> map = null;
			for(Map<String, Object> m : list){
				map = new HashMap<String, Object>();
				for(Map.Entry<String, Object> entry : m.entrySet()){
					map.put(entry.getKey(), entry.getValue());
				}
				result.add(map);
			}
		}
		return result;
	}
	
	public void executeSql(String sql){
		jdbcTemplate.update(sql);
    }
}
