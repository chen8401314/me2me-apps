package com.me2me.dao;

import com.me2me.live.mapper.TopicMapper;
import com.me2me.live.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
@Repository
public class LiveMybatisDao {

    @Autowired
    private TopicMapper topicMapper;

    public void createTopic(Topic topic){
        topicMapper.insertSelective(topic);
    }
}
