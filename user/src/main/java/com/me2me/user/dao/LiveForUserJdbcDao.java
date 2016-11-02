package com.me2me.user.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Specification;
import com.me2me.user.dto.UserAccountBindStatusDto;
import com.me2me.user.model.User;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 王一武
 * Date: 2016/11/02
 * Time :16:16
 */
@Repository
@Slf4j
public class LiveForUserJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long getKingFromTopic(long topicId) {
        String caseSql = " select uid from topic where id=" + topicId;
        Map<String,Object> map = jdbcTemplate.queryForMap(caseSql);
        if(map!=null){
           return (long)map.get("uid");
        }
        return 0L;
    }
}
