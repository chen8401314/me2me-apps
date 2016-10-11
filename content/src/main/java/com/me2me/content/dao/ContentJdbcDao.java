package com.me2me.content.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by pc188 on 2016/10/11.
 */
@Repository
@Slf4j
public class ContentJdbcDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void updateContentReviewCountByTid(long topicId) {
        String sql = "update content set review_count=review_count-1 where type=3 and forward_cid=?";
        jdbcTemplate.update(sql,topicId);
    }

}
