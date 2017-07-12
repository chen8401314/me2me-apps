package com.me2me.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public class BaseJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String,Object>> query(String sql, Object ... params){
        return this.jdbcTemplate.queryForList(sql,params);
    }

    public void execute(String sql){
        this.jdbcTemplate.execute(sql);
    }

    public void update(String sql,Object ... params){
        this.jdbcTemplate.update(sql,params);
    }

    public int count(String sql, Object ... params){
        return Integer.valueOf(this.jdbcTemplate.queryForList(sql,params).get(0).get("count").toString());
    }



}
