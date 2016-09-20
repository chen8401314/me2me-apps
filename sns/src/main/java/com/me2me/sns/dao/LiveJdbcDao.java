package com.me2me.sns.dao;

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
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :11:51
 */
@Repository
@Slf4j
public class LiveJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void updateTopic(long topicId, String coreCircle) {
        String sql = "update topic set core_circle = ? where id = ?";
        jdbcTemplate.update(sql,coreCircle,topicId);
    }
}
