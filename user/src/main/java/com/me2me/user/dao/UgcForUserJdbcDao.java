package com.me2me.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 王一武
 * Date: 2016/11/02
 * Time :16:46
 */
@Repository
@Slf4j
public class UgcForUserJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long getAuthorFromUgc(long cid) {
        String caseSql = " select uid from content where id=" + cid;
        Map<String,Object> map = jdbcTemplate.queryForMap(caseSql);
        if(map!=null){
           return (long)map.get("uid");
        }
        return 0L;
    }
}
