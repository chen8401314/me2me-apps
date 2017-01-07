package com.me2me.user.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Author: 马秀成
 * Date: 2017/1/7
 */
@Repository
public class ActivityJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String,Object> getAppUiControl(){
        String sql = "select * from app_ui_control where status = 0 and start_time<=now() and end_time>=now()";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        if(null != list && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

}
