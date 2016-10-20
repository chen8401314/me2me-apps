package com.me2me.content.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LiveForContentJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public JSONArray getTopicCoreCircle(long topicId) {

        String sql = "select core_circle from topic where id=?";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,topicId);
        if(list.size()>0){
            return JSON.parseArray((String)list.get(0).get("core_circle"));
        }
        return  null;
    }
}
